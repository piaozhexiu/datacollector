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
package com.streamsets.pipeline.lib.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * This class encapsulates extra formatting features that Apache Commons CSVParser supports
 * while Jackson CsvParser doesn't.
 */
public class CsvFormat {
  private CsvSchema csvSchema;
  private boolean ignoreEmptyLines;
  private boolean ignoreSurroundingSpaces;
  private boolean allowMissingColumnNames;

  // Default CSV format (equivalent to Apache Commons CSVFormat.DEFAULT)
  public CsvFormat() {
    this(CsvSchema.emptySchema().withLineSeparator("\r\n"), true, false, false);
  }

  public CsvFormat(CsvSchema csvSchema) {
    this(csvSchema, true, false, false);
  }

  public CsvFormat(
      CsvSchema csvSchema,
      boolean ignoreEmptyLines,
      boolean ignoreSurroundingSpaces,
      boolean allowMissingColumnNames) {
    this.csvSchema = csvSchema;
    this.ignoreEmptyLines = ignoreEmptyLines;
    this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
    this.allowMissingColumnNames = allowMissingColumnNames;
  }

  public CsvSchema getCsvSchema() {
    return csvSchema;
  }

  public void setCsvSchema(CsvSchema csvSchema) {
    this.csvSchema = csvSchema;
  }

  public boolean ignoreEmptyLines() {
    return ignoreEmptyLines;
  }

  public void setIgnoreEmptyLines(boolean ignoreEmptyLines) {
    this.ignoreEmptyLines = ignoreEmptyLines;
  }

  public boolean ignoreSurroundingSpaces() {
    return ignoreSurroundingSpaces;
  }

  public void setIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces) {
    this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
  }

  // It is not clear what allowMissingColumnNames is for in Apache Commons CSVFormat.
  // For now, this attribute is ignored. CsvMode.EXCEL sets it true.
  public boolean allowMissingColumnNames() {
    return allowMissingColumnNames;
  }

  public void setAllowMissingColumnNames(boolean allowMissingColumnNames) {
    this.allowMissingColumnNames = allowMissingColumnNames;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof CsvFormat) {
      CsvFormat other = (CsvFormat) o;
      return this.csvSchema.toString().equals(other.csvSchema.toString())
          && this.ignoreEmptyLines == other.ignoreEmptyLines
          && this.ignoreSurroundingSpaces == other.ignoreSurroundingSpaces
          && this.allowMissingColumnNames == other.allowMissingColumnNames;
    }
    return false;
  }
}
