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
package com.streamsets.pipeline.stage.origin.kafka;

public class ClusterKafkaSourceFactory extends KafkaSourceFactory {
  private static final String CLUSTER_MODE_CLASS = "com.streamsets.pipeline.stage.origin.kafka.cluster.ClusterKafkaSource";
  public ClusterKafkaSourceFactory(KafkaSourceConfigBean conf) {
    super(conf);
  }

  public BaseKafkaSource create() {
    try {
      Class clusterModeClazz = Class.forName(CLUSTER_MODE_CLASS);
      return (BaseKafkaSource) clusterModeClazz.getConstructor(KafkaSourceConfigBean.class)
        .newInstance(new Object[]{conf});
    } catch (Exception e) {
      throw new IllegalStateException("Exception while invoking kafka instance in cluster mode: " + e, e);
    }
  }
}
