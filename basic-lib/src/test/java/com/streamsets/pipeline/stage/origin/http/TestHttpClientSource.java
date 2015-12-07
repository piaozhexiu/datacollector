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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.streamsets.pipeline.api.OnRecordError;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.sdk.SourceRunner;
import com.streamsets.pipeline.sdk.StageRunner;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;


public class TestHttpClientSource extends JerseyTest {

  @Path("/stream")
  @Produces("application/json")
  public static class StreamResource {
    @GET
    public Response getStream() {
      return Response.ok(
          "{\"name\": \"adam\"}\r\n" +
          "{\"name\": \"joe\"}\r\n" +
          "{\"name\": \"sally\"}"
      ).build();
    }

    @POST
    public Response postStream(String name) {
      Map<String, String> map = ImmutableMap.of("adam", "adam", "joe", "joe", "sally", "sally");
      String queriedName = map.get(name);
      return Response.ok(
          "{\"name\": \"" + queriedName + "\"}\r\n"
      ).build();
    }
  }

  @Path("/nlstream")
  @Produces("application/json")
  public static class NewlineStreamResource {
    @GET
    public Response getStream() {
      return Response.ok(
          "{\"name\": \"adam\"}\n" +
          "{\"name\": \"joe\"}\n" +
          "{\"name\": \"sally\"}"
      ).build();
    }
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(
        Sets.newHashSet(
            StreamResource.class,
            NewlineStreamResource.class
        )
    );
  }

  @Override
  protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
    return new GrizzlyWebTestContainerFactory();
  }

  @Override
  protected DeploymentContext configureDeployment() {
    return ServletDeploymentContext.forServlet(
        new ServletContainer(
            new ResourceConfig(
                Sets.newHashSet(
                    StreamResource.class,
                    NewlineStreamResource.class
                )
            )
        )
    ).build();
  }

  @Test
  public void testStreamingHttp() throws Exception {
    HttpClientConfigBean config = new HttpClientConfigBean();
    config.httpMode = HttpClientMode.STREAMING;
    config.resourceUrl = "http://localhost:9998/stream";
    config.requestTimeoutMillis = 1000;
//    config.entityDelimiter = "\r\n";
    config.batchSize = 100;
    config.maxBatchWaitTime = 1000;
    config.pollingInterval = 1000;
    config.httpMethod = HttpMethod.GET;
    HttpClientSource origin = new HttpClientSource(config);

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
        .addOutputLane("lane")
        .build();
    runner.runInit();

    try {
      StageRunner.Output output = runner.runProduce(null, 1000);
      Map<String, List<Record>> recordMap = output.getRecords();
      List<Record> parsedRecords = recordMap.get("lane");

      Assert.assertEquals(3, parsedRecords.size());

      String[] names = { "adam", "joe", "sally" };

      for (int i = 0; i < parsedRecords.size(); i++) {
        Assert.assertTrue(checkPersonRecord(parsedRecords.get(i), names[i]));
      }
    } finally {
      runner.runDestroy();
    }

  }

  @Test
  public void testStreamingPost() throws Exception {
    HttpClientConfigBean config = new HttpClientConfigBean();
    config.httpMode = HttpClientMode.STREAMING;
    config.resourceUrl = "http://localhost:9998/stream";
    config.requestTimeoutMillis = 1000;
//    config.entityDelimiter = "\r\n";
    config.batchSize = 100;
    config.maxBatchWaitTime = 1000;
    config.pollingInterval = 1000;
    config.httpMethod = HttpMethod.POST;
    config.requestData = "adam";
    HttpClientSource origin = new HttpClientSource(config);

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
        .addOutputLane("lane")
        .build();
    runner.runInit();

    try {
      StageRunner.Output output = runner.runProduce(null, 1000);
      Map<String, List<Record>> recordMap = output.getRecords();
      List<Record> parsedRecords = recordMap.get("lane");

      Assert.assertEquals(1, parsedRecords.size());

      String[] names = { "adam" };

      for (int i = 0; i < parsedRecords.size(); i++) {
        Assert.assertTrue(checkPersonRecord(parsedRecords.get(i), names[i]));
      }
    } finally {
      runner.runDestroy();
    }

  }

  @Test
  public void testStreamingHttpWithNewlineOnly() throws Exception {
    HttpClientConfigBean config = new HttpClientConfigBean();
    config.httpMode = HttpClientMode.STREAMING;
    config.resourceUrl = "http://localhost:9998/nlstream";
    config.requestTimeoutMillis = 1000;
//    config.enntityDelimiter = "\n";
    config.batchSize = 100;
    config.maxBatchWaitTime = 1000;
    config.pollingInterval = 1000;
    config.httpMethod = HttpMethod.GET;
    HttpClientSource origin = new HttpClientSource(config);

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
        .addOutputLane("lane")
        .build();
    runner.runInit();

    try {
      StageRunner.Output output = runner.runProduce(null, 1000);
      Map<String, List<Record>> recordMap = output.getRecords();
      List<Record> parsedRecords = recordMap.get("lane");

      Assert.assertEquals(3, parsedRecords.size());

      String[] names = { "adam", "joe", "sally" };

      for (int i = 0; i < parsedRecords.size(); i++) {
        Assert.assertTrue(checkPersonRecord(parsedRecords.get(i), names[i]));
      }
    } finally {
      runner.runDestroy();
    }

  }

  @Test
  public void testNoAuthorizeHttpOnSendToError() throws Exception {
    HttpClientSource origin = getTwitterHttpClientSource();
    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
      .addOutputLane("lane")
      .setOnRecordError(OnRecordError.TO_ERROR)
      .build();
    runner.runInit();

    try {
      runner.runProduce(null, 1000);
      List<String> errors = runner.getErrors();
      Assert.assertEquals(1, errors.size());
    } finally {
      runner.runDestroy();
    }
  }

  @Test
  public void testNoAuthorizeHttpOnStopPipeline() throws Exception {
    HttpClientSource origin = getTwitterHttpClientSource();
    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
      .addOutputLane("lane")
      .setOnRecordError(OnRecordError.STOP_PIPELINE)
      .build();
    runner.runInit();

    boolean exceptionThrown = false;

    try {
      runner.runProduce(null, 1000);
      List<String> errors = runner.getErrors();
      Assert.assertEquals(1, errors.size());
    } catch (StageException ex){
      exceptionThrown = true;
      Assert.assertEquals(ex.getErrorCode(), Errors.HTTP_01);
    }
    finally {
      runner.runDestroy();
    }

    Assert.assertTrue(exceptionThrown);
  }

  @Test
  public void testNoAuthorizeHttpOnDiscard() throws Exception {
    HttpClientSource origin = getTwitterHttpClientSource();

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
      .addOutputLane("lane")
      .setOnRecordError(OnRecordError.DISCARD)
      .build();
    runner.runInit();

    try {
      runner.runProduce(null, 1000);
      List<String> errors = runner.getErrors();
      Assert.assertEquals(0, errors.size());
    } finally {
      runner.runDestroy();
    }
  }

  private boolean checkPersonRecord(Record record, String name) {
    return record.has("/name") &&
        record.get("/name").getValueAsString().equals(name);
  }

  private HttpClientSource getTwitterHttpClientSource() {
    HttpClientConfigBean config = new HttpClientConfigBean();
    config.httpMode = HttpClientMode.STREAMING;
    config.resourceUrl = "https://stream.twitter.com/1.1/statuses/sample.json";
    config.requestTimeoutMillis = 1000;
//    config.entityDelimiter = "\r\n";
    config.batchSize = 100;
    config.maxBatchWaitTime = 1000;
    config.pollingInterval = 1000;
    config.httpMethod = HttpMethod.GET;

    return new HttpClientSource(config);
  }
}
