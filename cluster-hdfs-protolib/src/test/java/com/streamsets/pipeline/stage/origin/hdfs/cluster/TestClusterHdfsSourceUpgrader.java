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
package com.streamsets.pipeline.stage.origin.hdfs.cluster;

import com.streamsets.pipeline.api.Config;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.config.DataFormat;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestClusterHdfsSourceUpgrader {

  @Test
  public void testUpgradeV3toV4() throws StageException {
    List<Config> configs = new ArrayList<>();
    configs.add(new Config("dataFormat", DataFormat.TEXT));
    configs.add(new Config("hdfsUri", "MY_URI"));
    configs.add(new Config("hdfsDirLocations", null));
    configs.add(new Config("recursive", true));
    configs.add(new Config("produceSingleRecordPerMessage", false));
    configs.add(new Config("hdfsKerberos", true));
    configs.add(new Config("hdfsConfDir", "MY_DIR"));
    configs.add(new Config("hdfsUser", "MY_USER"));
    configs.add(new Config("hdfsConfigs", null));
    configs.add(new Config("textMaxLineLen", 1024));

    ClusterHdfsSourceUpgrader clusterHdfsSourceUpgrader = new ClusterHdfsSourceUpgrader();
    clusterHdfsSourceUpgrader.upgrade("a", "b", "c", 3, 4, configs);

    Assert.assertEquals(10, configs.size());

    HashMap<String, Object> configValues = new HashMap<>();
    for (Config c : configs) {
      configValues.put(c.getName(), c.getValue());
    }

    Assert.assertTrue(configValues.containsKey("conf.dataFormat"));
    Assert.assertEquals(DataFormat.TEXT, configValues.get("conf.dataFormat"));

    Assert.assertTrue(configValues.containsKey("conf.hdfsUri"));
    Assert.assertEquals("MY_URI", configValues.get("conf.hdfsUri"));

    Assert.assertTrue(configValues.containsKey("conf.hdfsDirLocations"));
    Assert.assertEquals(null, configValues.get("conf.hdfsDirLocations"));

    Assert.assertTrue(configValues.containsKey("conf.recursive"));
    Assert.assertEquals(true, configValues.get("conf.recursive"));

    Assert.assertTrue(configValues.containsKey("conf.produceSingleRecordPerMessage"));
    Assert.assertEquals(false, configValues.get("conf.produceSingleRecordPerMessage"));

    Assert.assertTrue(configValues.containsKey("conf.hdfsKerberos"));
    Assert.assertEquals(true, configValues.get("conf.hdfsKerberos"));

    Assert.assertTrue(configValues.containsKey("conf.hdfsConfDir"));
    Assert.assertEquals("MY_DIR", configValues.get("conf.hdfsConfDir"));

    Assert.assertTrue(configValues.containsKey("conf.hdfsUser"));
    Assert.assertEquals("MY_USER", configValues.get("conf.hdfsUser"));

    Assert.assertTrue(configValues.containsKey("conf.hdfsConfigs"));
    Assert.assertEquals(null, configValues.get("conf.hdfsConfigs"));

    Assert.assertTrue(configValues.containsKey("conf.dataFormatConfig.textMaxLineLen"));
    Assert.assertEquals(1024, configValues.get("conf.dataFormatConfig.textMaxLineLen"));
  }
}