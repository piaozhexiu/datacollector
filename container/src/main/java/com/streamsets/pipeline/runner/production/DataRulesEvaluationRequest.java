/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.pipeline.runner.production;

import com.streamsets.pipeline.api.Record;

import java.util.List;
import java.util.Map;

public class DataRulesEvaluationRequest {

  private final Map<String, List<Record>> snapshot;
  public DataRulesEvaluationRequest(Map<String, List<Record>> snapshot) {
    this.snapshot = snapshot;
  }

  public Map<String, List<Record>> getSnapshot() {
    return snapshot;
  }

}