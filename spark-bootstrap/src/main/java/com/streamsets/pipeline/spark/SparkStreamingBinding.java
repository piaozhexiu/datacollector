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
package com.streamsets.pipeline.spark;

import com.streamsets.pipeline.ClusterBinding;
import com.streamsets.pipeline.Utils;
import kafka.serializer.DefaultDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

public class SparkStreamingBinding implements ClusterBinding {
  private static final String MAX_WAIT_TIME = "maxWaitTime";
  private static final String METADATA_BROKER_LIST = "metadataBrokerList";
  private static final String TOPIC = "topic";
  private static final String AUTO_OFFSET_RESET = "auto.offset.reset";
  private static final Logger LOG = LoggerFactory.getLogger(SparkStreamingBinding.class);

  private JavaStreamingContext ssc;
  private final Properties properties;

  public SparkStreamingBinding(Properties properties) {
    this.properties = Utils.checkNotNull(properties, "Properties");
  }

  @Override
  public void init() throws Exception {
    SparkConf conf = new SparkConf().setAppName("StreamSets Data Collector - Streaming Mode");
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
    long duration;
    String durationAsString = getProperty(MAX_WAIT_TIME);
    try {
      duration = Long.parseLong(durationAsString);
    } catch (NumberFormatException ex) {
      String msg = "Invalid " + MAX_WAIT_TIME  + " '" + durationAsString + "' : " + ex;
      throw new IllegalArgumentException(msg, ex);
    }
    ssc = new JavaStreamingContext(conf, new Duration(duration));
    // mesos tries to stop the context internally, so don't do it here - deadlock bug in spark
    if (System.getProperty("SDC_MESOS_BASE_DIR") == null) {
      final Thread shutdownHookThread = new Thread("Spark.shutdownHook") {
        @Override
        public void run() {
          LOG.debug("Gracefully stopping Spark Streaming Application");
          ssc.stop(true, true);
          LOG.info("Application stopped");
        }
      };
      Runtime.getRuntime().addShutdownHook(shutdownHookThread);
    }
    LOG.info("Making calls through spark context ");
    JavaPairInputDStream<byte[], byte[]> dStream = createDirectStreamForKafka();
    dStream.foreachRDD(new SparkDriverFunction());
    ssc.start();
  }

  private String getProperty(String name) {
    Utils.checkArgumentNotNull(properties.getProperty(name),
      "Property " + name +" cannot be null");
    return properties.getProperty(name).trim();
  }

  private JavaPairInputDStream<byte[], byte[]> createDirectStreamForKafka() {
    HashMap<String, String> props = new HashMap<String, String>();
    // Check for null values
    // require only the broker list for direct stream API (low level consumer API)
    String metaDataBrokerList = getProperty(METADATA_BROKER_LIST);
    String topic = getProperty(TOPIC);
    props.put("metadata.broker.list", metaDataBrokerList);
    String autoOffsetValue = properties.getProperty(AUTO_OFFSET_RESET, "").trim();
    if (!autoOffsetValue.isEmpty()) {
      props.put(AUTO_OFFSET_RESET, autoOffsetValue);
    }
    String[] topicList = topic.split(",");
    LOG.info("Meta data broker list " + metaDataBrokerList);
    LOG.info("topic list " + topic);
    LOG.info("Auto offset is set to " + autoOffsetValue);
    JavaPairInputDStream<byte[], byte[]> dStream =
      KafkaUtils.createDirectStream(ssc, byte[].class, byte[].class, DefaultDecoder.class, DefaultDecoder.class, props,
        new HashSet<String>(Arrays.asList(topicList)));
    return dStream;
  }

  @Override
  public void awaitTermination() {
    ssc.awaitTermination();
  }

  @Override
  public void close() {
    if (ssc != null) {
      ssc.close();
    }
  }
}
