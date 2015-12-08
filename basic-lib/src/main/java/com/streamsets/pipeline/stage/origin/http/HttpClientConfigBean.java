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
package com.streamsets.pipeline.stage.origin.http;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ConfigDefBean;
import com.streamsets.pipeline.api.ValueChooserModel;
import com.streamsets.pipeline.config.DataFormat;
import com.streamsets.pipeline.stage.origin.lib.DataParserFormatConfig;

public class HttpClientConfigBean {

  @ConfigDefBean(groups = "HTTP")
  public DataParserFormatConfig dataFormatConfig;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      label = "Data Format",
      defaultValue = "JSON",
      description = "Format of data in the files",
      displayPosition = 0,
      group = "#0"
  )
  @ValueChooserModel(DataFormatChooserValues.class)
  public DataFormat dataFormat;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "Resource URL",
      defaultValue = "https://stream.twitter.com/1.1/statuses/sample.json",
      description = "Specify the streaming HTTP resource URL",
      displayPosition = 10,
      group = "#0"
  )
  public String resourceUrl;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      label = "HTTP Method",
      defaultValue = "GET",
      description = "HTTP method to send",
      displayPosition = 20,
      group = "#0"
  )
  @ValueChooserModel(HttpMethodChooserValues.class)
  public HttpMethod httpMethod;

  @ConfigDef(
      required = false,
      type = ConfigDef.Type.TEXT,
      label = "Request Data",
      description = "Data that should be included as a part of the request",
      displayPosition = 25,
      lines = 2,
      dependsOn = "httpMethod",
      triggeredByValue = {"POST", "PUT", "DELETE"},
      group = "#0"
  )
  public String requestData;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      label = "Request Timeout",
      defaultValue = "1000",
      description = "HTTP request timeout in milliseconds.",
      displayPosition = 30,
      group = "#0"
  )
  public long requestTimeoutMillis;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      label = "Mode",
      defaultValue = "STREAMING",
      displayPosition = 40,
      group = "#0"
  )
  @ValueChooserModel(HttpClientModeChooserValues.class)
  public HttpClientMode httpMode;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      label = "Polling Interval (ms)",
      defaultValue = "5000",
      displayPosition = 45,
      group = "#0",
      dependsOn = "httpMode",
      triggeredByValue = "POLLING"
  )
  public long pollingInterval;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.BOOLEAN,
      label = "Use OAuth",
      defaultValue = "false",
      description = "Enables OAuth tab for connections requiring authentication.",
      displayPosition = 50,
      group = "#0"
  )
  public boolean isOAuthEnabled;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      label = "Batch Size (recs)",
      defaultValue = "100",
      description = "Maximum number of response entities to queue (e.g. JSON objects).",
      displayPosition = 60,
      group = "#0"
  )
  public int batchSize;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      label = "Batch Wait Time (ms)",
      defaultValue = "5000",
      description = "Maximum amount of time to wait to fill a batch before sending it",
      displayPosition = 70,
      group = "#0"
  )
  public long maxBatchWaitTime;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "Consumer Key",
      description = "OAuth Consumer Key",
      displayPosition = 10,
      group = "#1",
      dependsOn = "isOAuthEnabled",
      triggeredByValue = {"true"}
  )
  public String consumerKey;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "Consumer Secret",
      description = "OAuth Consumer Secret",
      displayPosition = 20,
      group = "#1",
      dependsOn = "isOAuthEnabled",
      triggeredByValue = {"true"}
  )
  public String consumerSecret;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "Token",
      description = "OAuth Consumer Token",
      displayPosition = 30,
      group = "#1",
      dependsOn = "isOAuthEnabled",
      triggeredByValue = {"true"}
  )
  public String token;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "Token Secret",
      description = "OAuth Token Secret",
      displayPosition = 40,
      group = "#1",
      dependsOn = "isOAuthEnabled",
      triggeredByValue = "true"
  )
  public String tokenSecret;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "Entity Delimiter",
      defaultValue = "\\r\\n",
      description = "Records may be delimited by a user-defined string. Common values are \\r\\n and \\n",
      displayPosition = 20,
      group = "JSON",
      dependsOn = "jsonMode",
      triggeredByValue = "MULTIPLE_OBJECTS"
  )
  public String jsonEntityDelimiter;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "Entity Delimiter",
      defaultValue = "\\r\\n",
      description = "Records may be delimited by a user-defined string. Common values are \\r\\n and \\n",
      displayPosition = 20,
      group = "XML",
      dependsOn = "xmlMode",
      triggeredByValue = "MULTIPLE_OBJECTS"
  )
  public String xmlEntityDelimiter;
}
