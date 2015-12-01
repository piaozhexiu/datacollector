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
package com.streamsets.pipeline.config;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.streamsets.pipeline.api.GenerateResourceBundle;
import com.streamsets.pipeline.api.Label;
import com.streamsets.pipeline.api.impl.Utils;
import com.streamsets.pipeline.lib.csv.CsvFormat;

@GenerateResourceBundle
public enum CsvMode implements Label {
  CSV("Default CSV (ignores empty lines)"),
  RFC4180("RFC4180 CSV"),
  EXCEL("MS Excel CSV"),
  MYSQL("MySQL CSV"),
  TDF("Tab Separated Values"),
  CUSTOM("Custom")
  ;

  private final String label;

  CsvMode(String label) {
    this.label = label;
  }

  // Please refer to https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.html
  // regarding the following CSV formats.
  public static CsvFormat getCsvFormat(CsvMode csvMode) {
    CsvFormat csvFormat = new CsvFormat();
    if (csvMode == CsvMode.CSV) {
      // Default CsvFormat
    } else if (csvMode == CsvMode.RFC4180) {
      csvFormat.setIgnoreEmptyLines(false);
      csvFormat.setCsvSchema(CsvSchema.emptySchema().withLineSeparator("\r\n"));
    } else if (csvMode == CsvMode.EXCEL) {
      csvFormat.setIgnoreEmptyLines(false);
      csvFormat.setAllowMissingColumnNames(true);
      csvFormat.setCsvSchema(CsvSchema.emptySchema().withLineSeparator("\r\n"));
    } else if (csvMode == CsvMode.MYSQL) {
      csvFormat.setIgnoreEmptyLines(false);
      csvFormat.setCsvSchema(CsvSchema.emptySchema().withColumnSeparator('\t').withEscapeChar('\\').withoutQuoteChar());
    } else if (csvMode == CsvMode.TDF) {
      csvFormat.setIgnoreSurroundingSpaces(true);
      csvFormat.setCsvSchema(CsvSchema.emptySchema().withColumnSeparator('\t').withLineSeparator("\r\n"));
    } else {
      throw new IllegalArgumentException(Utils.format("Unknown CsvMode: {}", csvMode));
    }
    return csvFormat;
  }

  @Override
  public String getLabel() {
    return label;
  }
}
