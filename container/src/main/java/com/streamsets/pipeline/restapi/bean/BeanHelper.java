/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.pipeline.restapi.bean;

import com.streamsets.pipeline.record.HeaderImpl;
import com.streamsets.pipeline.record.RecordImpl;
import com.streamsets.pipeline.runner.production.SourceOffset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanHelper {

  public static PipelineStateJson wrapPipelineState(com.streamsets.pipeline.prodmanager.PipelineState pipelineState) {
    if(pipelineState == null) {
      return null;
    }
    return new PipelineStateJson(pipelineState);
  }

  public static SnapshotStatusJson wrapSnapshotStatus(com.streamsets.pipeline.snapshotstore.SnapshotStatus snapshotStatus) {
    if(snapshotStatus == null) {
      return null;
    }
    return new SnapshotStatusJson(snapshotStatus);
  }

  public static List<PipelineStateJson> wrapPipelineStates(
    List<com.streamsets.pipeline.prodmanager.PipelineState> pipelineStates) {
    if(pipelineStates == null) {
      return null;
    }
    List<PipelineStateJson> states = new ArrayList<>(pipelineStates.size());
    for(com.streamsets.pipeline.prodmanager.PipelineState p : pipelineStates) {
      states.add(BeanHelper.wrapPipelineState(p));
    }
    return states;
  }

  public static List<com.streamsets.pipeline.prodmanager.PipelineState> unwrapPipelineStates(
    List<PipelineStateJson> pipelineStateJsons) {
    if(pipelineStateJsons == null) {
      return null;
    }
    List<com.streamsets.pipeline.prodmanager.PipelineState> states = new ArrayList<>(pipelineStateJsons.size());
    for(PipelineStateJson p : pipelineStateJsons) {
      states.add(p.getPipelineState());
    }
    return states;
  }

  public static List<PipelineInfoJson> wrapPipelineInfo(List<com.streamsets.pipeline.store.PipelineInfo> pipelines) {
    if(pipelines == null) {
      return null;
    }
    List<PipelineInfoJson> pipelineInfoJson = new ArrayList<>(pipelines.size());
    for(com.streamsets.pipeline.store.PipelineInfo p : pipelines) {
      pipelineInfoJson.add(new PipelineInfoJson(p));
    }
    return pipelineInfoJson;
  }

  public static ConfigConfigurationJson wrapConfigConfiguration(
    com.streamsets.pipeline.config.ConfigConfiguration configConfiguration) {
    if(configConfiguration == null) {
      return null;
    }
    return new ConfigConfigurationJson(configConfiguration);
  }

  public static List<ConfigConfigurationJson> wrapConfigConfiguration(
    List<com.streamsets.pipeline.config.ConfigConfiguration> configConfiguration) {
    if(configConfiguration == null) {
      return null;
    }
    List<ConfigConfigurationJson> unwrappedConfig = new ArrayList<>(configConfiguration.size());
    for(com.streamsets.pipeline.config.ConfigConfiguration c : configConfiguration) {
      unwrappedConfig.add(new ConfigConfigurationJson(c));
    }
    return unwrappedConfig;
  }

  public static List<com.streamsets.pipeline.config.ConfigConfiguration> unwrapConfigConfiguration(
    List<ConfigConfigurationJson> configConfigurationJson) {
    if(configConfigurationJson == null) {
      return null;
    }
    List<com.streamsets.pipeline.config.ConfigConfiguration> unwrappedConfig = new ArrayList<>(configConfigurationJson.size());
    for(ConfigConfigurationJson c : configConfigurationJson) {
      unwrappedConfig.add(c.getConfigConfiguration());
    }
    return unwrappedConfig;
  }

  public static List<com.streamsets.pipeline.config.StageConfiguration> unwrapStageConfigurations(
    List<StageConfigurationJson> stageConfigurationJson) {
    if(stageConfigurationJson == null) {
      return null;
    }
    List<com.streamsets.pipeline.config.StageConfiguration> configs = new ArrayList<>(stageConfigurationJson.size());
    for(StageConfigurationJson s : stageConfigurationJson) {
      configs.add(s.getStageConfiguration());
    }
    return configs;
  }

  public static com.streamsets.pipeline.config.StageConfiguration unwrapStageConfiguration(
    StageConfigurationJson stageConfigurationJson) {
    if(stageConfigurationJson == null) {
      return null;
    }
    return stageConfigurationJson.getStageConfiguration();
  }

  public static List<StageConfigurationJson> wrapStageConfigurations(
    List<com.streamsets.pipeline.config.StageConfiguration> stageConfiguration) {
    if(stageConfiguration == null) {
      return null;
    }
    List<StageConfigurationJson> configs = new ArrayList<>(stageConfiguration.size());
    for(com.streamsets.pipeline.config.StageConfiguration s : stageConfiguration) {
      configs.add(new StageConfigurationJson(s));
    }
    return configs;
  }

  public static StageConfigurationJson wrapStageConfiguration(
    com.streamsets.pipeline.config.StageConfiguration stageConfiguration) {
    if(stageConfiguration == null) {
      return null;
    }
    return new StageConfigurationJson(stageConfiguration);
  }

  public static List<MetricsRuleDefinitionJson> wrapMetricRuleDefinitions(
    List<com.streamsets.pipeline.config.MetricsRuleDefinition> metricsRuleDefinitions) {
    if(metricsRuleDefinitions == null) {
      return null;
    }
    List<MetricsRuleDefinitionJson> metricsRuleDefinitionJsonList = new ArrayList<>(metricsRuleDefinitions.size());
    for(com.streamsets.pipeline.config.MetricsRuleDefinition m : metricsRuleDefinitions) {
      metricsRuleDefinitionJsonList.add(new MetricsRuleDefinitionJson(m));
    }
    return metricsRuleDefinitionJsonList;
  }

  public static List<com.streamsets.pipeline.config.MetricsRuleDefinition> unwrapMetricRuleDefinitions(
    List<MetricsRuleDefinitionJson> metricsRuleDefinitionJsons) {
    if(metricsRuleDefinitionJsons == null) {
      return null;
    }
    List<com.streamsets.pipeline.config.MetricsRuleDefinition> metricsRuleDefinitionList = new ArrayList<>(metricsRuleDefinitionJsons.size());
    for(MetricsRuleDefinitionJson m : metricsRuleDefinitionJsons) {
      metricsRuleDefinitionList.add(m.getMetricsRuleDefinition());
    }
    return metricsRuleDefinitionList;
  }

  public static List<DataRuleDefinitionJson> wrapDataRuleDefinitions(
    List<com.streamsets.pipeline.config.DataRuleDefinition> dataRuleDefinitions) {
    if(dataRuleDefinitions == null) {
      return null;
    }
    List<DataRuleDefinitionJson> dataRuleDefinitionJsonList = new ArrayList<>(dataRuleDefinitions.size());
    for(com.streamsets.pipeline.config.DataRuleDefinition d : dataRuleDefinitions) {
      dataRuleDefinitionJsonList.add(new DataRuleDefinitionJson(d));
    }
    return dataRuleDefinitionJsonList;
  }

  public static List<com.streamsets.pipeline.config.DataRuleDefinition> unwrapDataRuleDefinitions(
    List<DataRuleDefinitionJson> dataRuleDefinitionJsons) {
    if(dataRuleDefinitionJsons == null) {
      return null;
    }
    List<com.streamsets.pipeline.config.DataRuleDefinition> dataRuleDefinitionList = new ArrayList<>(dataRuleDefinitionJsons.size());
    for(DataRuleDefinitionJson m : dataRuleDefinitionJsons) {
      dataRuleDefinitionList.add(m.getDataRuleDefinition());
    }
    return dataRuleDefinitionList;
  }

  public static PipelineConfigurationJson wrapPipelineConfiguration(
    com.streamsets.pipeline.config.PipelineConfiguration pipelineConfiguration) {
    if(pipelineConfiguration == null) {
      return null;
    }
    return new PipelineConfigurationJson(pipelineConfiguration);
  }

  public static com.streamsets.pipeline.config.PipelineConfiguration unwrapPipelineConfiguration(
    PipelineConfigurationJson pipelineConfigurationJson) {
    if(pipelineConfigurationJson == null) {
      return null;
    }
    return pipelineConfigurationJson.getPipelineConfiguration();
  }

  public static PipelineInfoJson wrapPipelineInfo(com.streamsets.pipeline.store.PipelineInfo pipelineInfo) {
    if(pipelineInfo == null) {
      return null;
    }
    return new PipelineInfoJson(pipelineInfo);
  }

  public static List<PipelineRevInfoJson> wrapPipelineRevInfo(
    List<com.streamsets.pipeline.store.PipelineRevInfo> pipelineRevInfos) {
    if(pipelineRevInfos == null) {
      return null;
    }
    List<PipelineRevInfoJson> pipelineRevInfoJsonList = new ArrayList<>(pipelineRevInfos.size());
    for(com.streamsets.pipeline.store.PipelineRevInfo p : pipelineRevInfos) {
      pipelineRevInfoJsonList.add(new PipelineRevInfoJson(p));
    }
    return pipelineRevInfoJsonList;
  }

  public static RuleDefinitionsJson wrapRuleDefinitions(com.streamsets.pipeline.config.RuleDefinitions ruleDefinitions) {
    if(ruleDefinitions == null) {
      return null;
    }
    return new RuleDefinitionsJson(ruleDefinitions);
  }

  public static com.streamsets.pipeline.config.RuleDefinitions unwrapRuleDefinitions(RuleDefinitionsJson ruleDefinitionsJson) {
    if(ruleDefinitionsJson == null) {
      return null;
    }
    return ruleDefinitionsJson.getRuleDefinitions();
  }

  public static List<ConfigDefinitionJson> wrapConfigDefinitions(
    List<com.streamsets.pipeline.config.ConfigDefinition> configDefinitions) {
    if(configDefinitions == null) {
      return null;
    }
    List<ConfigDefinitionJson> configDefinitionlist = new ArrayList<>(configDefinitions.size());
    for(com.streamsets.pipeline.config.ConfigDefinition c : configDefinitions) {
      configDefinitionlist.add(new ConfigDefinitionJson(c));
    }
    return configDefinitionlist;
  }

  public static List<com.streamsets.pipeline.config.ConfigDefinition> unwrapConfigDefinitions(
    List<ConfigDefinitionJson> configDefinitionJsons) {
    if(configDefinitionJsons == null) {
      return null;
    }
    List<com.streamsets.pipeline.config.ConfigDefinition> configDefinitionlist = new ArrayList<>(configDefinitionJsons.size());
    for(ConfigDefinitionJson c : configDefinitionJsons) {
      configDefinitionlist.add(c.getConfigDefinition());
    }
    return configDefinitionlist;
  }

  public static ModelDefinitionJson wrapModelDefinition(com.streamsets.pipeline.config.ModelDefinition modelDefinition) {
    if(modelDefinition == null) {
      return null;
    }
    return new ModelDefinitionJson(modelDefinition);
  }

  public static com.streamsets.pipeline.config.ModelDefinition unwrapModelDefinition(ModelDefinitionJson modelDefinitionJson) {
    if(modelDefinitionJson == null) {
      return null;
    }
    return modelDefinitionJson.getModelDefinition();
  }

  public static com.streamsets.pipeline.config.RawSourceDefinition unwrapRawSourceDefinition(
    RawSourceDefinitionJson rawSourceDefinitionJson) {
    if(rawSourceDefinitionJson == null) {
      return null;
    }
    return rawSourceDefinitionJson.getRawSourceDefinition();
  }

  public static RawSourceDefinitionJson wrapRawSourceDefinition(
    com.streamsets.pipeline.config.RawSourceDefinition rawSourceDefinition) {
    if(rawSourceDefinition == null) {
      return null;
    }
    return new RawSourceDefinitionJson(rawSourceDefinition);
  }

  public static ConfigGroupDefinitionJson wrapConfigGroupDefinition(
    com.streamsets.pipeline.config.ConfigGroupDefinition configGroupDefinition) {
    if(configGroupDefinition == null) {
      return null;
    }
    return new ConfigGroupDefinitionJson(configGroupDefinition);
  }

  public static com.streamsets.pipeline.config.ConfigGroupDefinition unwrapConfigGroupDefinition(
    ConfigGroupDefinitionJson configGroupDefinitionJson) {
    if(configGroupDefinitionJson == null) {
      return null;
    }
    return configGroupDefinitionJson.getConfigGroupDefinition();
  }

  public static List<StageDefinitionJson> wrapStageDefinitions(
    List<com.streamsets.pipeline.config.StageDefinition> stageDefinitions) {
    if(stageDefinitions == null) {
      return null;
    }
    List<StageDefinitionJson> stageDefinitionJsonList = new ArrayList<>(stageDefinitions.size());
    for(com.streamsets.pipeline.config.StageDefinition s : stageDefinitions) {
      stageDefinitionJsonList.add(new StageDefinitionJson(s));
    }
    return stageDefinitionJsonList;
  }

  public static com.streamsets.pipeline.config.StageDefinition unwrapStageDefinition(StageDefinitionJson stageDefinitionJson) {
    if(stageDefinitionJson == null) {
      return null;
    }
    return stageDefinitionJson.getStageDefinition();
  }

  public static List<com.streamsets.pipeline.config.StageDefinition> unwrapStageDefinitions(
    List<StageDefinitionJson> stageDefinitionJsons) {
    if(stageDefinitionJsons == null) {
      return null;
    }
    List<com.streamsets.pipeline.config.StageDefinition> stageDefinitionList = new ArrayList<>(stageDefinitionJsons.size());
    for(StageDefinitionJson s : stageDefinitionJsons) {
      stageDefinitionList.add(s.getStageDefinition());
    }
    return stageDefinitionList;
  }

  public static List<RuleIssueJson> wrapRuleIssues(List<com.streamsets.pipeline.validation.RuleIssue> ruleIssues) {
    if(ruleIssues == null) {
      return null;
    }
    List<RuleIssueJson> ruleIssueJsonList = new ArrayList<>(ruleIssues.size());
    for(com.streamsets.pipeline.validation.RuleIssue r : ruleIssues) {
      ruleIssueJsonList.add(new RuleIssueJson(r));
    }
    return ruleIssueJsonList;
  }

  public static List<IssueJson> wrapIssues(List<com.streamsets.pipeline.validation.Issue> issues) {
    if(issues == null) {
      return null;
    }
    List<IssueJson> issueJsonList = new ArrayList<>(issues.size());
    for(com.streamsets.pipeline.validation.Issue r : issues) {
      issueJsonList.add(new IssueJson(r));
    }
    return issueJsonList;
  }

  public static List<com.streamsets.pipeline.validation.Issue> unwrapIssues(List<IssueJson> issueJsons) {
    if(issueJsons == null) {
      return null;
    }
    List<com.streamsets.pipeline.validation.Issue> issueList = new ArrayList<>(issueJsons.size());
    for(IssueJson r : issueJsons) {
      issueList.add(r.getIssue());
    }
    return issueList;
  }

  public static Map<String, List<StageIssueJson>> wrapStageIssuesMap(
    Map<String, List<com.streamsets.pipeline.validation.StageIssue>> stageIssuesMapList) {
    if(stageIssuesMapList == null) {
      return null;
    }
    Map<String, List<StageIssueJson>> stageIssuesMap = new HashMap<>();
    for(Map.Entry<String, List<com.streamsets.pipeline.validation.StageIssue>> e : stageIssuesMapList.entrySet()) {
      stageIssuesMap.put(e.getKey(), wrapStageIssues(e.getValue()));
    }
    return stageIssuesMap;
  }

  public static Map<String, List<com.streamsets.pipeline.validation.StageIssue>> unwrapStageIssuesMap(
    Map<String, List<StageIssueJson>> stageIssues) {
    if(stageIssues == null) {
      return null;
    }
    Map<String, List<com.streamsets.pipeline.validation.StageIssue>> stageIssuesMap = new HashMap<>();
    for(Map.Entry<String, List<StageIssueJson>> e : stageIssues.entrySet()) {
      stageIssuesMap.put(e.getKey(), unwrapStageIssues(e.getValue()));
    }
    return stageIssuesMap;
  }

  public static List<StageIssueJson> wrapStageIssues(List<com.streamsets.pipeline.validation.StageIssue> issues) {
    if(issues == null) {
      return null;
    }
    List<StageIssueJson> issueList = new ArrayList<>(issues.size());
    for(com.streamsets.pipeline.validation.StageIssue r : issues) {
      issueList.add(new StageIssueJson(r));
    }
    return issueList;
  }

  public static List<com.streamsets.pipeline.validation.StageIssue> unwrapStageIssues(List<StageIssueJson> issues) {
    if(issues == null) {
      return null;
    }
    List<com.streamsets.pipeline.validation.StageIssue> issueList = new ArrayList<>(issues.size());
    for(StageIssueJson r : issues) {
      issueList.add(r.getStageIssue());
    }
    return issueList;
  }

  public static IssuesJson wrapIssues(com.streamsets.pipeline.validation.Issues issues) {
    if(issues == null) {
      return null;
    }
    return new IssuesJson(issues);
  }

  public static com.streamsets.pipeline.api.Field unwrapField(FieldJson fieldJson) {
    if(fieldJson == null) {
      return null;
    }
    return fieldJson.getField();
  }

  public static FieldJson wrapField(com.streamsets.pipeline.api.Field field) {
    if(field == null) {
      return null;
    }
    return new FieldJson(field);
  }

  public static HeaderImpl unwrapHeader(HeaderJson headerJson) {
    if(headerJson == null) {
      return null;
    }
    return headerJson.getHeader();
  }

  public static HeaderJson wrapHeader(HeaderImpl header) {
    if(header == null) {
      return null;
    }
    return new HeaderJson(header);
  }

  public static RecordJson wrapRecord(com.streamsets.pipeline.api.Record sourceRecord) {
    if(sourceRecord == null) {
      return null;
    }
    return new RecordJson((RecordImpl)sourceRecord);
  }

  public static com.streamsets.pipeline.api.Record unwrapRecord(RecordJson sourceRecordJson) {
    if(sourceRecordJson == null) {
      return null;
    }
    return sourceRecordJson.getRecord();
  }

  public static List<RecordJson> wrapRecords(List<com.streamsets.pipeline.api.Record> records) {
    if(records == null) {
      return null;
    }
    List<RecordJson> recordJsonList = new ArrayList<>(records.size());
    for(com.streamsets.pipeline.api.Record r : records) {
      recordJsonList.add(new RecordJson((RecordImpl)r));
    }
    return recordJsonList;
  }

  public static List<com.streamsets.pipeline.api.Record> unwrapRecords(List<RecordJson> recordJsons) {
    if(recordJsons == null) {
      return null;
    }
    List<com.streamsets.pipeline.api.Record> recordList = new ArrayList<>(recordJsons.size());
    for(RecordJson r : recordJsons) {
      recordList.add(r.getRecord());
    }
    return recordList;
  }

  public static Map<String, List<RecordJson>> wrapRecordsMap(
    Map<String, List<com.streamsets.pipeline.api.Record>> recordsMap) {
    if(recordsMap == null) {
      return null;
    }
    Map<String, List<RecordJson>> records = new HashMap<>();
    for(Map.Entry<String, List<com.streamsets.pipeline.api.Record>> e : recordsMap.entrySet()) {
      records.put(e.getKey(), BeanHelper.wrapRecords(e.getValue()));
    }
    return records;
  }

  public static Map<String, List<com.streamsets.pipeline.api.Record>> unwrapRecordsMap(
    Map<String, List<RecordJson>> recordsMap) {
    if(recordsMap == null) {
      return null;
    }
    Map<String, List<com.streamsets.pipeline.api.Record>> records = new HashMap<>();
    for(Map.Entry<String, List<RecordJson>> e : recordsMap.entrySet()) {
      records.put(e.getKey(), BeanHelper.unwrapRecords(e.getValue()));
    }
    return records;
  }

  public static List<ErrorMessageJson> wrapErrorMessages(
    List<com.streamsets.pipeline.api.impl.ErrorMessage> errorMessages) {
    if(errorMessages == null) {
      return null;
    }
    List<ErrorMessageJson> errorMessageJsonList = new ArrayList<>(errorMessages.size());
    for(com.streamsets.pipeline.api.impl.ErrorMessage e : errorMessages) {
      errorMessageJsonList.add(new ErrorMessageJson(e));
    }
    return errorMessageJsonList;
  }

  public static List<com.streamsets.pipeline.api.impl.ErrorMessage> unwrapErrorMessages(
    List<ErrorMessageJson> errorMessageJsons) {
    if(errorMessageJsons == null) {
      return null;
    }
    List<com.streamsets.pipeline.api.impl.ErrorMessage> errorMessageList = new ArrayList<>(errorMessageJsons.size());
    for(ErrorMessageJson e : errorMessageJsons) {
      errorMessageList.add(e.getErrorMessage());
    }
    return errorMessageList;
  }

  public static List<com.streamsets.pipeline.runner.StageOutput> unwrapStageOutput(List<StageOutputJson> stageOutputJsons) {
    if(stageOutputJsons == null) {
      return null;
    }
    List<com.streamsets.pipeline.runner.StageOutput> stageOutputList = new ArrayList<>(stageOutputJsons.size());
    for(StageOutputJson s : stageOutputJsons) {
      stageOutputList.add(s.getStageOutput());
    }
    return stageOutputList;
  }

  public static List<StageOutputJson> wrapStageOutput(List<com.streamsets.pipeline.runner.StageOutput> stageOutputs) {
    if(stageOutputs == null) {
      return null;
    }
    List<StageOutputJson> stageOutputList = new ArrayList<>(stageOutputs.size());
    for(com.streamsets.pipeline.runner.StageOutput s : stageOutputs) {
      stageOutputList.add(new StageOutputJson(s));
    }
    return stageOutputList;
  }

  public static List<List<StageOutputJson>> wrapStageOutputLists(
    List<List<com.streamsets.pipeline.runner.StageOutput>> stageOutputs) {
    if(stageOutputs == null) {
      return null;
    }
    List<List<StageOutputJson>> result = new ArrayList<>();
    for(List<com.streamsets.pipeline.runner.StageOutput> stageOutputList : stageOutputs) {
      List<StageOutputJson> stageOutputJson = new ArrayList<>();
      for(com.streamsets.pipeline.runner.StageOutput s : stageOutputList) {
        stageOutputJson.add(new StageOutputJson(s));
      }
      result.add(stageOutputJson);
    }
    return result;
  }



  public static PreviewPipelineOutputJson wrapPreviewPipelineOutput(
    com.streamsets.pipeline.runner.preview.PreviewPipelineOutput previewPipelineOutput) {
    if(previewPipelineOutput == null) {
      return null;
    }
    return new PreviewPipelineOutputJson(previewPipelineOutput);
  }

  public static PipelineDefinitionJson wrapPipelineDefinition(
    com.streamsets.pipeline.config.PipelineDefinition pipelineDefinition) {
    if(pipelineDefinition == null) {
      return null;
    }
    return new PipelineDefinitionJson(pipelineDefinition);
  }

  public static SourceOffset unwrapSourceOffset(SourceOffsetJson sourceOffsetJson) {
    if(sourceOffsetJson == null) {
      return null;
    }
    return sourceOffsetJson.getSourceOffset();
  }

  public static SourceOffsetJson wrapSourceOffset(SourceOffset sourceOffset) {
    if(sourceOffset == null) {
      return null;
    }
    return new SourceOffsetJson(sourceOffset);
  }

  /*****************************************************/
  /***************** Enum Helper ***********************/
  /*****************************************************/

  public static StateJson wrapState(com.streamsets.pipeline.prodmanager.State state) {
    if(state == null) {
      return null;
    }
    switch(state) {
      case STOPPED:
        return StateJson.STOPPED;
      case STOPPING:
        return StateJson.STOPPING;
      case RUNNING:
        return StateJson.RUNNING;
      case ERROR:
        return StateJson.ERROR;
      case FINISHED:
        return StateJson.FINISHED;
      case NODE_PROCESS_SHUTDOWN:
        return StateJson.NODE_PROCESS_SHUTDOWN;
      default:
        throw new IllegalArgumentException("Unrecognized state");
    }
  }

  public static com.streamsets.pipeline.prodmanager.State unwrapState(StateJson stateJson) {
    if(stateJson == null) {
      return null;
    }
    switch(stateJson) {
      case STOPPED:
        return com.streamsets.pipeline.prodmanager.State.STOPPED;
      case STOPPING:
        return com.streamsets.pipeline.prodmanager.State.STOPPING;
      case RUNNING:
        return com.streamsets.pipeline.prodmanager.State.RUNNING;
      case ERROR:
        return com.streamsets.pipeline.prodmanager.State.ERROR;
      case FINISHED:
        return com.streamsets.pipeline.prodmanager.State.FINISHED;
      case NODE_PROCESS_SHUTDOWN:
        return com.streamsets.pipeline.prodmanager.State.NODE_PROCESS_SHUTDOWN;
      default:
        throw new IllegalArgumentException("Unrecognized state");
    }
  }

  public static MetricElementJson wrapMetricElement(com.streamsets.pipeline.config.MetricElement metricElement) {
    if(metricElement == null) {
      return null;
    }
    switch(metricElement) {
      //Related to Counters
      case COUNTER_COUNT:
        return MetricElementJson.COUNTER_COUNT;

      //Related to Histogram
      case HISTOGRAM_COUNT:
        return MetricElementJson.HISTOGRAM_COUNT;
      case HISTOGRAM_MAX:
        return MetricElementJson.HISTOGRAM_MAX;
      case HISTOGRAM_MIN:
        return MetricElementJson.HISTOGRAM_MIN;
      case HISTOGRAM_MEAN:
        return MetricElementJson.HISTOGRAM_MEAN;
      case HISTOGRAM_MEDIAN:
        return MetricElementJson.HISTOGRAM_MEDIAN;
      case HISTOGRAM_P75:
        return MetricElementJson.HISTOGRAM_P75;
      case HISTOGRAM_P95:
        return MetricElementJson.HISTOGRAM_P95;
      case HISTOGRAM_P98:
        return MetricElementJson.HISTOGRAM_P98;
      case HISTOGRAM_P99:
        return MetricElementJson.HISTOGRAM_P99;
      case HISTOGRAM_P999:
        return MetricElementJson.HISTOGRAM_P999;
      case HISTOGRAM_STD_DEV:
        return MetricElementJson.HISTOGRAM_STD_DEV;

      //Meters
      case METER_COUNT:
        return MetricElementJson.METER_COUNT;
      case METER_M1_RATE:
        return MetricElementJson.METER_M1_RATE;
      case METER_M5_RATE:
        return MetricElementJson.METER_M5_RATE;
      case METER_M15_RATE:
        return MetricElementJson.METER_M15_RATE;
      case METER_M30_RATE:
        return MetricElementJson.METER_M30_RATE;
      case METER_H1_RATE:
        return MetricElementJson.METER_H1_RATE;
      case METER_H6_RATE:
        return MetricElementJson.METER_H6_RATE;
      case METER_H12_RATE:
        return MetricElementJson.METER_H12_RATE;
      case METER_H24_RATE:
        return MetricElementJson.METER_H24_RATE;
      case METER_MEAN_RATE:
        return MetricElementJson.METER_MEAN_RATE;

      //Timer
      case TIMER_COUNT:
        return MetricElementJson.TIMER_COUNT;
      case TIMER_MAX:
        return MetricElementJson.TIMER_MAX;
      case TIMER_MIN:
        return MetricElementJson.TIMER_MIN;
      case TIMER_MEAN:
        return MetricElementJson.TIMER_MEAN;
      case TIMER_P50:
        return MetricElementJson.TIMER_P50;
      case TIMER_P75:
        return MetricElementJson.TIMER_P75;
      case TIMER_P95:
        return MetricElementJson.TIMER_P95;
      case TIMER_P98:
        return MetricElementJson.TIMER_P98;
      case TIMER_P99:
        return MetricElementJson.TIMER_P99;
      case TIMER_P999:
        return MetricElementJson.TIMER_P999;
      case TIMER_STD_DEV:
        return MetricElementJson.TIMER_STD_DEV;
      case TIMER_M1_RATE:
        return MetricElementJson.TIMER_M1_RATE;
      case TIMER_M5_RATE:
        return MetricElementJson.TIMER_M5_RATE;
      case TIMER_M15_RATE:
        return MetricElementJson.TIMER_M15_RATE;
      case TIMER_MEAN_RATE:
        return MetricElementJson.TIMER_MEAN_RATE;
      default:
        throw new IllegalArgumentException("Unrecognized metric element");
    }
  }

  public static com.streamsets.pipeline.config.MetricElement unwrapMetricElement(MetricElementJson metricElementJson) {
    if(metricElementJson == null) {
      return null;
    }
    switch(metricElementJson) {
      //Related to Counters
      case COUNTER_COUNT:
        return com.streamsets.pipeline.config.MetricElement.COUNTER_COUNT;

      //Related to Histogram
      case HISTOGRAM_COUNT:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_COUNT;
      case HISTOGRAM_MAX:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_MAX;
      case HISTOGRAM_MIN:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_MIN;
      case HISTOGRAM_MEAN:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_MEAN;
      case HISTOGRAM_MEDIAN:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_MEDIAN;
      case HISTOGRAM_P75:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_P75;
      case HISTOGRAM_P95:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_P95;
      case HISTOGRAM_P98:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_P98;
      case HISTOGRAM_P99:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_P99;
      case HISTOGRAM_P999:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_P999;
      case HISTOGRAM_STD_DEV:
        return com.streamsets.pipeline.config.MetricElement.HISTOGRAM_STD_DEV;

      //Meters
      case METER_COUNT:
        return com.streamsets.pipeline.config.MetricElement.METER_COUNT;
      case METER_M1_RATE:
        return com.streamsets.pipeline.config.MetricElement.METER_M1_RATE;
      case METER_M5_RATE:
        return com.streamsets.pipeline.config.MetricElement.METER_M5_RATE;
      case METER_M15_RATE:
        return com.streamsets.pipeline.config.MetricElement.METER_M15_RATE;
      case METER_M30_RATE:
        return com.streamsets.pipeline.config.MetricElement.METER_M30_RATE;
      case METER_H1_RATE:
        return com.streamsets.pipeline.config.MetricElement.METER_H1_RATE;
      case METER_H6_RATE:
        return com.streamsets.pipeline.config.MetricElement.METER_H6_RATE;
      case METER_H12_RATE:
        return com.streamsets.pipeline.config.MetricElement.METER_H12_RATE;
      case METER_H24_RATE:
        return com.streamsets.pipeline.config.MetricElement.METER_H24_RATE;
      case METER_MEAN_RATE:
        return com.streamsets.pipeline.config.MetricElement.METER_MEAN_RATE;

      //Timer
      case TIMER_COUNT:
        return com.streamsets.pipeline.config.MetricElement.TIMER_COUNT;
      case TIMER_MAX:
        return com.streamsets.pipeline.config.MetricElement.TIMER_MAX;
      case TIMER_MIN:
        return com.streamsets.pipeline.config.MetricElement.TIMER_MIN;
      case TIMER_MEAN:
        return com.streamsets.pipeline.config.MetricElement.TIMER_MEAN;
      case TIMER_P50:
        return com.streamsets.pipeline.config.MetricElement.TIMER_P50;
      case TIMER_P75:
        return com.streamsets.pipeline.config.MetricElement.TIMER_P75;
      case TIMER_P95:
        return com.streamsets.pipeline.config.MetricElement.TIMER_P95;
      case TIMER_P98:
        return com.streamsets.pipeline.config.MetricElement.TIMER_P98;
      case TIMER_P99:
        return com.streamsets.pipeline.config.MetricElement.TIMER_P99;
      case TIMER_P999:
        return com.streamsets.pipeline.config.MetricElement.TIMER_P999;
      case TIMER_STD_DEV:
        return com.streamsets.pipeline.config.MetricElement.TIMER_STD_DEV;
      case TIMER_M1_RATE:
        return com.streamsets.pipeline.config.MetricElement.TIMER_M1_RATE;
      case TIMER_M5_RATE:
        return com.streamsets.pipeline.config.MetricElement.TIMER_M5_RATE;
      case TIMER_M15_RATE:
        return com.streamsets.pipeline.config.MetricElement.TIMER_M15_RATE;
      case TIMER_MEAN_RATE:
        return com.streamsets.pipeline.config.MetricElement.TIMER_MEAN_RATE;
      default:
        throw new IllegalArgumentException("Unrecognized metric element");
    }
  }

  public static MetricTypeJson wrapMetricType(com.streamsets.pipeline.config.MetricType metricType) {
    if(metricType == null) {
      return null;
    }
    switch(metricType) {
      case HISTOGRAM:
        return MetricTypeJson.HISTOGRAM;
      case TIMER:
        return MetricTypeJson.TIMER;
      case COUNTER:
        return MetricTypeJson.COUNTER;
      case METER:
        return MetricTypeJson.METER;
      default:
        throw new IllegalArgumentException("Unrecognized metric type");
    }
  }

  public static com.streamsets.pipeline.config.MetricType unwrapMetricType(MetricTypeJson metricType) {
    if(metricType == null) {
      return null;
    }
    switch(metricType) {
      case HISTOGRAM:
        return com.streamsets.pipeline.config.MetricType.HISTOGRAM;
      case TIMER:
        return com.streamsets.pipeline.config.MetricType.TIMER;
      case COUNTER:
        return com.streamsets.pipeline.config.MetricType.COUNTER;
      case METER:
        return com.streamsets.pipeline.config.MetricType.METER;
      default:
        throw new IllegalArgumentException("Unrecognized metric type");
    }
  }

  public static ThresholdTypeJson wrapThresholdType(com.streamsets.pipeline.config.ThresholdType thresholdType) {
    if(thresholdType == null) {
      return null;
    }
    switch (thresholdType) {
      case COUNT:
        return ThresholdTypeJson.COUNT;
      case PERCENTAGE:
        return ThresholdTypeJson.PERCENTAGE;
      default:
        throw new IllegalArgumentException("Unrecognized metric type");
    }
  }

  public static com.streamsets.pipeline.config.ThresholdType unwrapThresholdType(ThresholdTypeJson thresholdTypeJson) {
    if(thresholdTypeJson == null) {
      return null;
    }
    switch (thresholdTypeJson) {
      case COUNT:
        return com.streamsets.pipeline.config.ThresholdType.COUNT;
      case PERCENTAGE:
        return com.streamsets.pipeline.config.ThresholdType.PERCENTAGE;
      default:
        throw new IllegalArgumentException("Unrecognized metric type");
    }
  }

  public static ModelTypeJson wrapModelType(com.streamsets.pipeline.config.ModelType modelType) {
    if(modelType == null) {
      return null;
    }
    switch (modelType) {
      case COMPLEX_FIELD:
        return ModelTypeJson.COMPLEX_FIELD;
      case FIELD_SELECTOR_MULTI_VALUED:
        return ModelTypeJson.FIELD_SELECTOR_MULTI_VALUED;
      case FIELD_SELECTOR_SINGLE_VALUED:
        return ModelTypeJson.FIELD_SELECTOR_SINGLE_VALUED;
      case FIELD_VALUE_CHOOSER:
        return ModelTypeJson.FIELD_VALUE_CHOOSER;
      case LANE_PREDICATE_MAPPING:
        return ModelTypeJson.LANE_PREDICATE_MAPPING;
      case VALUE_CHOOSER:
        return ModelTypeJson.VALUE_CHOOSER;
      default:
        throw new IllegalArgumentException("Unrecognized model type");
    }
  }

  public static com.streamsets.pipeline.config.ModelType unwrapModelType(ModelTypeJson modelTypeJson) {
    if(modelTypeJson == null) {
      return null;
    }
    switch (modelTypeJson) {
      case COMPLEX_FIELD:
        return com.streamsets.pipeline.config.ModelType.COMPLEX_FIELD;
      case FIELD_SELECTOR_MULTI_VALUED:
        return com.streamsets.pipeline.config.ModelType.FIELD_SELECTOR_MULTI_VALUED;
      case FIELD_SELECTOR_SINGLE_VALUED:
        return com.streamsets.pipeline.config.ModelType.FIELD_SELECTOR_SINGLE_VALUED;
      case FIELD_VALUE_CHOOSER:
        return com.streamsets.pipeline.config.ModelType.FIELD_VALUE_CHOOSER;
      case LANE_PREDICATE_MAPPING:
        return com.streamsets.pipeline.config.ModelType.LANE_PREDICATE_MAPPING;
      case VALUE_CHOOSER:
        return com.streamsets.pipeline.config.ModelType.VALUE_CHOOSER;
      default:
        throw new IllegalArgumentException("Unrecognized model type");
    }
  }

  public static ChooserModeJson wrapChooserMode(com.streamsets.pipeline.api.ChooserMode chooserMode) {
    if(chooserMode == null) {
      return null;
    }
    switch (chooserMode) {
      case PROVIDED:
        return ChooserModeJson.PROVIDED;
      case SUGGESTED:
        return ChooserModeJson.SUGGESTED;
      default:
        throw new IllegalArgumentException("Unrecognized model type");
    }
  }

  public static com.streamsets.pipeline.api.ChooserMode unwrapChooserMode(ChooserModeJson chooserModeJson) {
    if(chooserModeJson == null) {
      return null;
    }
    switch (chooserModeJson) {
      case PROVIDED:
        return com.streamsets.pipeline.api.ChooserMode.PROVIDED;
      case SUGGESTED:
        return com.streamsets.pipeline.api.ChooserMode.SUGGESTED;
      default:
        throw new IllegalArgumentException("Unrecognized model type");
    }
  }

  public static StageTypeJson wrapStageType(com.streamsets.pipeline.config.StageType stageType) {
    if(stageType == null) {
      return null;
    }
    switch (stageType) {
      case TARGET:
        return StageTypeJson.TARGET;
      case SOURCE:
        return StageTypeJson.SOURCE;
      case PROCESSOR:
        return StageTypeJson.PROCESSOR;
      default:
        throw new IllegalArgumentException("Unrecognized model type");
    }
  }

  public static com.streamsets.pipeline.config.StageType unwrapStageType(StageTypeJson stageTypeJson) {
    if(stageTypeJson == null) {
      return null;
    }
    switch (stageTypeJson) {
      case TARGET:
        return com.streamsets.pipeline.config.StageType.TARGET;
      case SOURCE:
        return com.streamsets.pipeline.config.StageType.SOURCE;
      case PROCESSOR:
        return com.streamsets.pipeline.config.StageType.PROCESSOR;
      default:
        throw new IllegalArgumentException("Unrecognized model type");
    }
  }

}