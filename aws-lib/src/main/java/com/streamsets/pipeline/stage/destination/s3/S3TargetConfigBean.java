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
package com.streamsets.pipeline.stage.destination.s3;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ConfigDefBean;
import com.streamsets.pipeline.api.Stage;
import com.streamsets.pipeline.api.ValueChooserModel;
import com.streamsets.pipeline.config.DataFormat;
import com.streamsets.pipeline.lib.generator.DataGeneratorFactory;
import com.streamsets.pipeline.stage.destination.lib.DataGeneratorFormatConfig;
import com.streamsets.pipeline.stage.origin.s3.S3AdvancedConfig;
import com.streamsets.pipeline.stage.origin.s3.S3Config;

import java.util.List;

public class S3TargetConfigBean {

  @ConfigDefBean(groups = {"S3"})
  public S3Config s3Config;

  @ConfigDef(
    required = true,
    type = ConfigDef.Type.STRING,
    defaultValue = "sdc",
    description = "Prefix for file names that will be uploaded on Amazon S3",
    label = "File Name Prefix",
    displayPosition = 190,
    group = "S3"
  )
  public String fileNamePrefix;

  @ConfigDef(
    required = true,
    type = ConfigDef.Type.MODEL,
    label = "Data Format",
    displayPosition = 200,
    group = "S3"
  )
  @ValueChooserModel(DataFormatChooserValues.class)
  public DataFormat dataFormat;

  @ConfigDef(
    required = true,
    type = ConfigDef.Type.BOOLEAN,
    defaultValue = "false",
    label = "Compress with gzip",
    displayPosition = 210,
    group = "S3"
  )
  public boolean compress;

  @ConfigDefBean(groups = {"S3"})
  public DataGeneratorFormatConfig dataGeneratorFormatConfig;

  @ConfigDefBean(groups = {"ADVANCED"})
  public S3AdvancedConfig advancedConfig;

  public List<Stage.ConfigIssue> init(Stage.Context context, List<Stage.ConfigIssue> issues) {
    s3Config.init(context, issues, advancedConfig);

    if(s3Config.bucket == null || s3Config.bucket.isEmpty()) {
      issues.add(context.createConfigIssue(Groups.S3.name(), "bucket", Errors.S3_01));
    } else if (!s3Config.getS3Client().doesBucketExist(s3Config.bucket)) {
      issues.add(context.createConfigIssue(Groups.S3.name(), "bucket", Errors.S3_02, s3Config.bucket));
    }

    dataGeneratorFormatConfig.init(context, dataFormat, Groups.S3.name(), "dataFormat", issues);

    if(issues.size() == 0) {
      generatorFactory = dataGeneratorFormatConfig.getDataGeneratorFactory();
    }
    return issues;
  }

  public void destroy() {
    s3Config.destroy();
  }

  private DataGeneratorFactory generatorFactory;

  public DataGeneratorFactory getGeneratorFactory() {
    return generatorFactory;
  }
}
