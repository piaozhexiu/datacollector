/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.pipeline.stage.origin.logtail;

import com.streamsets.pipeline.api.BatchMaker;
import com.streamsets.pipeline.api.ChooserMode;
import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ConfigGroups;
import com.streamsets.pipeline.api.GenerateResourceBundle;
import com.streamsets.pipeline.api.OffsetCommitter;
import com.streamsets.pipeline.api.RawSource;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageDef;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.ValueChooser;
import com.streamsets.pipeline.api.base.BaseSource;
import com.streamsets.pipeline.api.base.FileRawSourcePreviewer;
import com.streamsets.pipeline.config.DataFormat;
import com.streamsets.pipeline.lib.util.JsonLineToRecord;
import com.streamsets.pipeline.lib.util.LineToRecord;
import com.streamsets.pipeline.lib.util.ToRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@GenerateResourceBundle
@StageDef(
    version="1.0.0",
    label="File Tail",
    description = "Reads log or JSON data as it is written to a file",
    icon="fileTail.png"
)
@RawSource(rawSourcePreviewer = FileRawSourcePreviewer.class)
@ConfigGroups(com.streamsets.pipeline.stage.origin.logtail.ConfigGroups.class)
public class FileTailSource extends BaseSource implements OffsetCommitter {
  private static final int SLEEP_TIME_WAITING_FOR_BATCH_SIZE_MS = 100;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      label = "Data Format",
      description = "The data format in the files",
      displayPosition = 10,
      group = "FILE"
  )
  @ValueChooser(type = ChooserMode.PROVIDED, chooserValues = DataFormatChooserValues.class)
  public DataFormat dataFormat;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "File Path",
      description = "Full file path of the file to tail",
      displayPosition = 20,
      group = "FILE"
  )
  public String fileName;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.INTEGER,
      defaultValue = "10",
      label = "Maximum Lines per Batch",
      description = "The maximum number of file lines that will be sent in a single batch",
      displayPosition = 30,
      group = "FILE"
  )
  public int batchSize;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.INTEGER,
      defaultValue = "5",
      label = "Batch Wait Time (secs)",
      description = " Maximum amount of time to wait to fill a batch before sending it",
      displayPosition = 40,
      group = "FILE"
  )
  public int maxWaitTimeSecs;

  private BlockingQueue<String> logLinesQueue;
  private long maxWaitTimeMillis;
  private LogTail logTail;
  private ToRecord lineToRecord;

  private String fileOffset;
  private long recordCount;

  @Override
  protected List<ConfigIssue> validateConfigs() {
    List<ConfigIssue> issues = super.validateConfigs();
    File logFile = new File(fileName);
    if (!logFile.exists()) {
      try {
        // waiting for a second in case the log is in the middle of a file rotation and the file does not exist
        // at this very moment.
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        //NOP
      }
      if (!logFile.exists()) {
        issues.add(getContext().createConfigIssue(Errors.TAIL_00, logFile));
      }
    }
    if (logFile.exists() && !logFile.canRead()) {
      issues.add(getContext().createConfigIssue(Errors.TAIL_01, logFile));
    }
    if (logFile.exists() && !logFile.isFile()) {
      issues.add(getContext().createConfigIssue(Errors.TAIL_03, logFile));
    }
    switch (dataFormat) {
      case TEXT:
      case JSON:
        break;
      default:
        issues.add(getContext().createConfigIssue(Errors.TAIL_02, "dataFormat", dataFormat));
    }
    return issues;
  }

  @Override
  protected void init() throws StageException {
    super.init();
    File logFile = new File(fileName);
    maxWaitTimeMillis = maxWaitTimeSecs * 1000;
    logLinesQueue = new ArrayBlockingQueue<>(2 * batchSize);
    logTail = new LogTail(logFile, true, getInfo(), logLinesQueue);
    logTail.start();
    switch (dataFormat) {
      case TEXT:
        lineToRecord = new LineToRecord(false);
        break;
      case JSON:
        lineToRecord = new JsonLineToRecord();
        break;
      default:
        throw new StageException(Errors.TAIL_02, "dataFormat", dataFormat);
    }
    fileOffset = String.format("%s::%d", fileName, System.currentTimeMillis());
    recordCount = 0;
  }

  @Override
  public void destroy() {
    logTail.stop();
    super.destroy();
  }

  String getFileOffset() {
    return fileOffset;
  }

  long getRecordCount() {
    return recordCount;
  }

  @Override
  public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
    long start = System.currentTimeMillis();
    int fetch = Math.min(batchSize, maxBatchSize);
    List<String> lines = new ArrayList<>(fetch);
    while (((System.currentTimeMillis() - start) < maxWaitTimeMillis) && (logLinesQueue.size() < fetch)) {
      try {
        Thread.sleep(SLEEP_TIME_WAITING_FOR_BATCH_SIZE_MS);
      } catch (InterruptedException ex) {
        break;
      }
    }
    logLinesQueue.drainTo(lines, fetch);
    for (int i = 0; i < lines.size(); i++) {
      Record record = lineToRecord.createRecord(getContext(), getFileOffset(), getRecordCount(), lines.get(i), false);
      batchMaker.addRecord(record);
      recordCount++;
    }
    return getFileOffset() + "::" + getRecordCount();
  }

  @Override
  public void commit(String offset) throws StageException {
    //NOP
  }

}