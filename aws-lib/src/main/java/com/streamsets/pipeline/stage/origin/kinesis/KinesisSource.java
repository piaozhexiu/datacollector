/**
 * Copyright 2015 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.origin.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.clientlibrary.types.ExtendedSequenceNumber;
import com.amazonaws.services.kinesis.clientlibrary.types.UserRecord;
import com.google.common.base.Splitter;
import com.streamsets.pipeline.api.BatchMaker;
import com.streamsets.pipeline.api.ErrorCode;
import com.streamsets.pipeline.api.OffsetCommitter;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.BaseSource;
import com.streamsets.pipeline.api.impl.Utils;
import com.streamsets.pipeline.lib.parser.DataParser;
import com.streamsets.pipeline.lib.parser.DataParserException;
import com.streamsets.pipeline.lib.parser.DataParserFactory;
import com.streamsets.pipeline.stage.lib.kinesis.Errors;
import com.streamsets.pipeline.stage.lib.kinesis.Groups;
import com.streamsets.pipeline.stage.lib.kinesis.KinesisUtil;
import com.streamsets.pipeline.stage.lib.kinesis.RecordsAndCheckpointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

import static com.streamsets.pipeline.stage.lib.kinesis.KinesisUtil.ONE_MB;

public class KinesisSource extends BaseSource implements OffsetCommitter {
  private static final Logger LOG = LoggerFactory.getLogger(KinesisSource.class);
  private static final Splitter.MapSplitter offsetSplitter = Splitter.on("::").limit(2).withKeyValueSeparator("=");
  private final KinesisConsumerConfigBean conf;

  private ExecutorService executorService;
  private boolean isStarted = false;
  private Worker worker;
  private LinkedTransferQueue<RecordsAndCheckpointer> batchQueue;
  private LinkedTransferQueue<IRecordProcessorCheckpointer> commitQueue;
  private IRecordProcessorCheckpointer checkpointer;
  private DataParserFactory parserFactory;

  public KinesisSource(KinesisConsumerConfigBean conf) {
    this.conf = conf;
  }

  @Override
  protected List<ConfigIssue> init() {
    List<ConfigIssue> issues = super.init();

    KinesisUtil.checkStreamExists(conf.region, conf.streamName, issues, getContext());

    if (issues.isEmpty()) {
      batchQueue = new LinkedTransferQueue<>();
      commitQueue = new LinkedTransferQueue<>();

      conf.dataFormatConfig.init(
          getContext(),
          conf.dataFormat,
          Groups.KINESIS.name(),
          ONE_MB,
          issues
      );

      parserFactory = conf.dataFormatConfig.getParserFactory();

      executorService = Executors.newFixedThreadPool(1);

      IRecordProcessorFactory recordProcessorFactory =
          new StreamSetsRecordProcessorFactory(batchQueue, commitQueue);

      // Create the KCL worker with the StreamSets record processor factory
      worker = createKinesisWorker(recordProcessorFactory);
    }
    return issues;
  }

  private Worker createKinesisWorker(IRecordProcessorFactory recordProcessorFactory) {
    KinesisClientLibConfiguration kclConfig =
        new KinesisClientLibConfiguration(
            conf.applicationName,
            conf.streamName,
            KinesisUtil.getCredentialsProvider(conf),
            getWorkerId()
        );

    kclConfig
        .withRegionName(conf.region.getName())
        .withMaxRecords(conf.maxBatchSize)
        .withIdleTimeBetweenReadsInMillis(conf.idleTimeBetweenReads)
        .withInitialPositionInStream(conf.initialPositionInStream);

    return new Worker.Builder()
        .recordProcessorFactory(recordProcessorFactory)
        .config(kclConfig)
        .build();
  }

  private String getWorkerId() {
    String hostname = "unknownHostname";
    try {
      hostname = InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException ignored) {
      // ignored
    }
    return hostname + ":" + UUID.randomUUID();
  }

  @Override
  public void destroy() {
    if (worker != null) {
      LOG.info("Shutting down worker for application {}", worker.getApplicationName());
      worker.shutdown();
    }
    if (executorService != null) {
      try {
        executorService.shutdown();
        if (!executorService.awaitTermination(conf.maxWaitTime, TimeUnit.MILLISECONDS)) {
          executorService.shutdownNow();
        }
      } catch (InterruptedException e) {
        LOG.warn("Interrupted while terminating executor service.", e);
      }
      if (!batchQueue.isEmpty()) {
        LOG.error("Queue still had {} batches at shutdown.", batchQueue.size());
      } else {
        LOG.info("Queue was empty at shutdown. No data lost.");
      }
    }
    super.destroy();
  }

  @Override
  public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
    if (!isStarted) {
      executorService.execute(worker);
      isStarted = true;
      LOG.info("Launched KCL Worker for application: {}", worker.getApplicationName());
    }

    int recordCounter = 0;
    long startTime = System.currentTimeMillis();
    long waitTime = conf.maxWaitTime;

    if (getContext().isPreview()) {
      waitTime = conf.previewWaitTime;
    }

    while ((startTime + waitTime) > System.currentTimeMillis() && recordCounter < maxBatchSize) {
      try {
        long timeRemaining = (startTime + waitTime) - System.currentTimeMillis();
        final RecordsAndCheckpointer recordsAndCheckpointer =
            batchQueue.poll(timeRemaining, TimeUnit.MILLISECONDS);
        if (null != recordsAndCheckpointer) {
          final List<com.amazonaws.services.kinesis.model.Record> batch = recordsAndCheckpointer.getRecords();
          checkpointer = recordsAndCheckpointer.getCheckpointer();

          if (batch.isEmpty()) {
            // Signaled that this is the end of a shard.
            lastSourceOffset = ExtendedSequenceNumber.SHARD_END.toString();
          }
          for (com.amazonaws.services.kinesis.model.Record record : batch) {
            batchMaker.addRecord(processKinesisRecord(record));
            lastSourceOffset = "sequenceNumber=" + record.getSequenceNumber() + "::" +
                "subSequenceNumber=" + ((UserRecord) record).getSubSequenceNumber();
            ++recordCounter;
          }
        }
      } catch (IOException | DataParserException e) {
        handleErrorRecord(e, Errors.KINESIS_03, lastSourceOffset);
      } catch (InterruptedException ignored) {
        // pipeline shutdown request.
      }
    }
    return lastSourceOffset;
  }

  private Record processKinesisRecord(com.amazonaws.services.kinesis.model.Record kRecord)
      throws DataParserException, IOException {
    final String recordId = createKinesisRecordId(kRecord);
    DataParser parser = parserFactory.getParser(recordId, kRecord.getData().array());
    return parser.parse();
  }

  private String createKinesisRecordId(com.amazonaws.services.kinesis.model.Record record) {
    return conf.streamName + "::" + record.getPartitionKey() + "::" + record.getSequenceNumber() + "::" +
        ((UserRecord) record).getSubSequenceNumber();
  }

  @Override
  public void commit(String offset) throws StageException {
    final boolean isPreview = getContext().isPreview();
    if (null != checkpointer && !isPreview && !offset.isEmpty()) {
      try {
        LOG.debug("Checkpointing batch at offset {}", offset);
        if (offset.equals(ExtendedSequenceNumber.SHARD_END.toString())) {
          KinesisUtil.checkpoint(checkpointer);
        } else {
          Map<String, String> offsets = offsetSplitter.split(offset);
          KinesisUtil.checkpoint(
              checkpointer, offsets.get("sequenceNumber"), Long.parseLong(offsets.get("subSequenceNumber"))
          );
        }
        // Unblock worker thread.
        commitQueue.put(checkpointer);
      } catch (NumberFormatException e) {
        // Couldn't parse the provided subsequence, invalid offset string.
        LOG.error("Couldn't parse the offset string: {}", offset);
        throw new StageException(Errors.KINESIS_04, offset);
      }
    } else if(isPreview) {
      LOG.debug("Not checkpointing because this origin is in preview mode.");
    }
  }

  private void handleErrorRecord(
      Throwable e,
      ErrorCode errorCode,
      Object... context
  ) throws StageException {
    switch (getContext().getOnErrorRecord()) {
      case DISCARD:
        break;
      case TO_ERROR:
        getContext().reportError(errorCode, context);
        break;
      case STOP_PIPELINE:
        throw new StageException(errorCode, context);
      default:
        throw new IllegalStateException(
            Utils.format("Unknown OnError value '{}'", getContext().getOnErrorRecord(), e)
        );
    }
  }
}
