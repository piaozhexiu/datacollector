/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.pipeline.util;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.el.ELEvalException;
import com.streamsets.pipeline.api.impl.Utils;
import com.streamsets.pipeline.config.ConfigConfiguration;
import com.streamsets.pipeline.config.ConfigDefinition;
import com.streamsets.pipeline.config.PipelineConfiguration;
import com.streamsets.pipeline.config.StageDefinition;
import com.streamsets.pipeline.el.ELEvaluator;
import com.streamsets.pipeline.el.ELVariables;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElUtil {

  private static final String KEY = "key";
  private static final String VALUE = "value";
  private static final String EL_PREFIX = "${";
  private static final String CONSTANTS = "constants";

  public static Object evaluate(Object value, Field field, StageDefinition stageDefinition,
                                 ConfigDefinition configDefinition,
                                 Map<String, Object> constants) throws ELEvalException {
    if(configDefinition.getEvaluation() == ConfigDef.Evaluation.IMPLICIT) {
      if(isElString(value)) {
        //its an EL expression, try to evaluate it.
        ELEvaluator elEvaluator = createElEval(field.getName(), constants, getElDefs(stageDefinition,
          configDefinition));
        Type genericType = field.getGenericType();
        Class<?> klass;
        if(genericType instanceof ParameterizedType) {
          //As of today the only supported parameterized types are
          //1. List<String>
          //2. Map<String, String>
          //3. List<Map<String, String>>
          //In all cases we want to return String.class
          klass = String.class;
        } else {
          klass = (Class<?>) genericType;
        }
        return elEvaluator.evaluate(new ELVariables(constants), (String)value, klass);
      }
    }
    return value;
  }

  public static boolean isElString(Object value) {
    if(value instanceof String && ((String) value).startsWith(EL_PREFIX)) {
      return true;
    }
    return false;
  }

  public static Class<?>[] getElDefs(StageDefinition stageDef, ConfigDefinition configDefinition) {
    ClassLoader cl = stageDef.getStageClassLoader();
    List<String> elDefs = configDefinition.getElDefs();
    if(elDefs != null && elDefs.size() > 0) {
      Class<?>[] elDefClasses = new Class[elDefs.size()];
      for(int i = 0; i < elDefs.size(); i++) {
        try {
          elDefClasses[i] = cl.loadClass(elDefs.get(i));
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(
            Utils.format("Could not load EL Definition from '{}', {}", stageDef.getStageClassLoader(), e.getMessage()),
            e);
        }
      }
      return elDefClasses;
    }
    return null;
  }

  public static ELEvaluator createElEval(String name, Map<String, Object> constants, Class<?>... elDefs) {
    return new ELEvaluator(name, constants, elDefs);
  }

  public static Map<String, Object> getConstants(PipelineConfiguration pipelineConf) {
    Map<String, Object> constants = new HashMap<>();
    if(pipelineConf != null && pipelineConf.getConfiguration() != null) {
      for (ConfigConfiguration configConfiguration : pipelineConf.getConfiguration()) {
        if (configConfiguration.getName().equals(CONSTANTS) && configConfiguration.getValue() != null) {
          List<Map<String, String>> consts = (List<Map<String, String>>) configConfiguration.getValue();
          for (Map<String, String> constant : consts) {
            constants.put(constant.get(KEY), constant.get(VALUE));
          }
          return constants;
        }
      }
    }
    return constants;
  }

}