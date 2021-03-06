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
package com.streamsets.pipeline.stage.destination.elasticsearch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.OnRecordError;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.Stage;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.Target;
import com.streamsets.pipeline.sdk.RecordCreator;
import com.streamsets.pipeline.sdk.TargetRunner;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.plugins.PluginsService;
import org.elasticsearch.search.SearchHit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TestElasticSearchTarget {
  private static String esName = UUID.randomUUID().toString();
  private static Node esServer;
  private static int esPort;

  private static int getRandomPort() throws Exception {
    ServerSocket ss = new ServerSocket(0);
    int port = ss.getLocalPort();
    ss.close();
    return port;
  }

  @BeforeClass
  public static void setUp() throws Exception {
    File esDir = new File("target", UUID.randomUUID().toString());
    esPort = getRandomPort();
    Assert.assertTrue(esDir.mkdirs());
    ImmutableSettings.Builder settings = ImmutableSettings.builder();
    settings.put("cluster.name", esName);
    settings.put("http.enabled", false);
    settings.put("transport.tcp.port", esPort);
    settings.put("path.conf", esDir.getAbsolutePath());
    settings.put("path.data", esDir.getAbsolutePath());
    settings.put("path.logs", esDir.getAbsolutePath());
    settings.put("plugins." + PluginsService.LOAD_PLUGIN_FROM_CLASSPATH, false);
    esServer = NodeBuilder.nodeBuilder().settings(settings.build()).build();
    esServer.start();
  }


  @AfterClass
  public static void cleanUp() {
    if (esServer != null) {
      esServer.stop();
    }
  }

  // this is needed in embedded mode.
  private static void prepareElasticSearchServerForQueries() {
    esServer.client().admin().indices().prepareRefresh().execute().actionGet();
  }

  @Test
  public void testValidations() throws Exception {
    Target target = new ElasticSearchTarget("", Collections.EMPTY_LIST, Collections.EMPTY_MAP,
      "${record:value('/index')x}", "${record:valxue('/type')}", "", "UTF-8");
    TargetRunner runner = new TargetRunner.Builder(ElasticSearchDTarget.class, target).build();
    List<Stage.ConfigIssue> issues = runner.runValidateConfigs();
    Assert.assertEquals(4, issues.size());
    Assert.assertTrue(issues.get(0).toString().contains(Errors.ELASTICSEARCH_00.name()));
    Assert.assertTrue(issues.get(1).toString().contains(Errors.ELASTICSEARCH_03.name()));
    Assert.assertTrue(issues.get(2).toString().contains(Errors.ELASTICSEARCH_06.name()));
    Assert.assertTrue(issues.get(3).toString().contains(Errors.ELASTICSEARCH_07.name()));

    target = new ElasticSearchTarget("x", ImmutableList.of("x"), Collections.EMPTY_MAP, "x", "x", "", "UTF-8");
    runner = new TargetRunner.Builder(ElasticSearchDTarget.class, target).build();
    issues = runner.runValidateConfigs();
    Assert.assertEquals(1, issues.size());
    Assert.assertTrue(issues.get(0).toString().contains(Errors.ELASTICSEARCH_09.name()));

    target = new ElasticSearchTarget("x", ImmutableList.of("localhost:0"), Collections.EMPTY_MAP, "x", "x", "", "UTF-8");
    runner = new TargetRunner.Builder(ElasticSearchDTarget.class, target).build();
    issues = runner.runValidateConfigs();
    Assert.assertEquals(1, issues.size());
    Assert.assertTrue(issues.get(0).toString().contains(Errors.ELASTICSEARCH_08.name()));
  }

  private Target createTarget() {
    return new ElasticSearchTarget(esName, ImmutableList.of("127.0.0.1:" + esPort), Collections.EMPTY_MAP,
                                   "${record:value('/index')}", "${record:value('/type')}", "", "UTF-8");
  }

  @Test
  public void testWriteRecords() throws Exception {
    Target target = createTarget();
    TargetRunner runner = new TargetRunner.Builder(ElasticSearchDTarget.class, target).build();
    try {
      runner.runInit();
      List<Record> records = new ArrayList<>();
      Record record1 = RecordCreator.create();
      record1.set(Field.create(ImmutableMap.of("a", Field.create("Hello"),
                                               "index", Field.create("i"), "type", Field.create("t"))));
      Record record2 = RecordCreator.create();
      record2.set(Field.create(ImmutableMap.of("a", Field.create("Bye"),
                                               "index", Field.create("i"), "type", Field.create("t"))));
      records.add(record1);
      records.add(record2);
      runner.runWrite(records);
      Assert.assertTrue(runner.getErrorRecords().isEmpty());
      Assert.assertTrue(runner.getErrors().isEmpty());


      prepareElasticSearchServerForQueries();

      Set<Map> expected = new HashSet<>();
      expected.add(ImmutableMap.of("a", "Hello", "index", "i", "type", "t"));
      expected.add(ImmutableMap.of("a", "Bye", "index", "i", "type", "t"));

      SearchResponse response = esServer.client().prepareSearch("i").setTypes("t")
                                        .setSearchType(SearchType.DEFAULT).execute().actionGet();
      SearchHit[] hits = response.getHits().getHits();
      Assert.assertEquals(2, hits.length);
      Set<Map> got = new HashSet<>();
      got.add(hits[0].getSource());
      got.add(hits[1].getSource());

      Assert.assertEquals(expected, got);

    } finally {
      runner.runDestroy();
    }
  }

  @Test
  public void testWriteRecordsOnErrorDiscard() throws Exception {
    Target target = createTarget();
    TargetRunner runner = new TargetRunner.Builder(ElasticSearchDTarget.class, target).setOnRecordError(OnRecordError.DISCARD).build();
    try {
      runner.runInit();
      List<Record> records = new ArrayList<>();
      Record record1 = RecordCreator.create();
      record1.set(Field.create(ImmutableMap.of("a", Field.create("Hello"),
                                               "index", Field.create("II"), "type", Field.create("t"))));
      Record record2 = RecordCreator.create();
      record2.set(Field.create(ImmutableMap.of("a", Field.create("Bye"),
                                               "index", Field.create("ii"), "type", Field.create("t"))));
      records.add(record1);
      records.add(record2);
      runner.runWrite(records);
      Assert.assertTrue(runner.getErrorRecords().isEmpty());
      Assert.assertTrue(runner.getErrors().isEmpty());


      prepareElasticSearchServerForQueries();

      Set<Map> expected = new HashSet<>();
      expected.add(ImmutableMap.of("a", "Bye", "index", "ii", "type", "t"));

      SearchResponse response = esServer.client().prepareSearch("ii").setTypes("t")
                                        .setSearchType(SearchType.DEFAULT).execute().actionGet();
      SearchHit[] hits = response.getHits().getHits();
      Assert.assertEquals(1, hits.length);
      Set<Map> got = new HashSet<>();
      got.add(hits[0].getSource());

      Assert.assertEquals(expected, got);

    } finally {
      runner.runDestroy();
    }
  }

  @Test
  public void testWriteRecordsOnErrorToError() throws Exception {
    Target target = createTarget();
    TargetRunner runner = new TargetRunner.Builder(ElasticSearchDTarget.class, target).setOnRecordError(OnRecordError.TO_ERROR).build();
    try {
      runner.runInit();
      List<Record> records = new ArrayList<>();
      Record record1 = RecordCreator.create();
      record1.set(Field.create(ImmutableMap.of("a", Field.create("Hello"),
                                               "index", Field.create("III"), "type", Field.create("t"))));
      Record record2 = RecordCreator.create();
      record2.set(Field.create(ImmutableMap.of("a", Field.create("Bye"),
                                               "index", Field.create("iii"), "type", Field.create("t"))));
      records.add(record1);
      records.add(record2);
      runner.runWrite(records);
      Assert.assertEquals(1, runner.getErrorRecords().size());
      Assert.assertEquals("Hello", runner.getErrorRecords().get(0).get("/a").getValueAsString());
      Assert.assertTrue(runner.getErrors().isEmpty());


      prepareElasticSearchServerForQueries();

      Set<Map> expected = new HashSet<>();
      expected.add(ImmutableMap.of("a", "Bye", "index", "iii", "type", "t"));

      SearchResponse response = esServer.client().prepareSearch("iii").setTypes("t")
                                        .setSearchType(SearchType.DEFAULT).execute().actionGet();
      SearchHit[] hits = response.getHits().getHits();
      Assert.assertEquals(1, hits.length);
      Set<Map> got = new HashSet<>();
      got.add(hits[0].getSource());

      Assert.assertEquals(expected, got);

    } finally {
      runner.runDestroy();
    }
  }

  @Test(expected = StageException.class)
  public void testWriteRecordsOnErrorStopPipeline() throws Exception {
    Target target = createTarget();
    TargetRunner runner = new TargetRunner.Builder(ElasticSearchDTarget.class, target).setOnRecordError(OnRecordError.STOP_PIPELINE).build();
    try {
      runner.runInit();
      List<Record> records = new ArrayList<>();
      Record record1 = RecordCreator.create();
      record1.set(Field.create(ImmutableMap.of("a", Field.create("Hello"),
                                               "index", Field.create("IIII"), "type", Field.create("t"))));
      Record record2 = RecordCreator.create();
      record2.set(Field.create(ImmutableMap.of("a", Field.create("Bye"),
                                               "index", Field.create("iiii"), "type", Field.create("t"))));
      records.add(record1);
      records.add(record2);
      runner.runWrite(records);
    } finally {
      runner.runDestroy();
    }
  }

}
