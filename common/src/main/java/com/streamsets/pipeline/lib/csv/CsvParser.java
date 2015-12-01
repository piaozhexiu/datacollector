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

import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.streamsets.pipeline.api.impl.Utils;
import com.streamsets.pipeline.lib.io.CountingReader;
import com.streamsets.pipeline.lib.io.ObjectLengthException;
import com.streamsets.pipeline.lib.util.ExceptionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class CsvParser implements Closeable, AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(CsvParser.class);

  private final com.fasterxml.jackson.dataformat.csv.CsvParser parser;
  private final CsvFormat csvFormat;
  private final CountingReader reader;
  private final int maxObjectLen;
  private final String[] headers;
  private Iterator<String[]> iterator;
  private long currentPos;
  private boolean closed;

  public CsvParser(Reader reader, CsvFormat format, int maxObjectLen) throws IOException {
    this(new CountingReader(reader), format, maxObjectLen, 0);
  }

  public CsvParser(CountingReader reader, CsvFormat format, int maxObjectLen, long initialPosition) throws IOException {
    Utils.checkNotNull(reader, "reader");
    Utils.checkNotNull(reader.getPos() == 0,
        "reader must be in position zero, the CsvParser will fast-forward to the initialPosition");
    Utils.checkNotNull(format, "format");
    Utils.checkArgument(initialPosition >= 0, "initialPosition must be greater or equal than zero");
    this.reader = reader;
    this.currentPos = initialPosition;
    this.maxObjectLen = maxObjectLen;
    this.csvFormat = format;

    // The header is explicitly read as 1st row, so withSkipFirstDataRow(false).withoutHeader()
    // must be called if WITH_HEADER or IGNORE_HEADER is chosen.
    CsvSchema csvSchema = csvFormat.getCsvSchema();
    if (initialPosition == 0) {
      if (csvSchema.skipsFirstDataRow()) {
        csvSchema = csvSchema.withSkipFirstDataRow(false).withoutHeader();
        parser = new CsvFactory().createParser(reader);
        parser.setCodec(new CsvMapper());
        parser.setSchema(csvSchema);
        headers = read();
      } else {
        parser = new CsvFactory().createParser(reader);
        parser.setCodec(new CsvMapper());
        parser.setSchema(csvSchema);
        headers = null;
      }
    } else {
      if (csvSchema.skipsFirstDataRow()) {
        csvSchema = csvSchema.withSkipFirstDataRow(false).withoutHeader();
        parser = new CsvFactory().createParser(reader);
        parser.setCodec(new CsvMapper());
        parser.setSchema(csvSchema);
        headers = read();
        while (getReaderPosition() < initialPosition && read() != null) {
        }
        if (getReaderPosition() != initialPosition) {
          throw new IOException(Utils.format("Could not position reader at position '{}', got '{}' instead",
              initialPosition, getReaderPosition()));
        }
      } else {
        IOUtils.skipFully(reader, initialPosition);
        parser = new CsvFactory().createParser(reader);
        parser.setCodec(new CsvMapper());
        parser.setSchema(csvSchema);
        headers = null;
      }
    }
  }

  protected Reader getReader() {
    return reader;
  }

  public String[] getHeaders() throws IOException {
    return headers;
  }

  public long getReaderPosition() {
    return currentPos;
  }

  public String[] read() throws IOException {
    if (closed) {
      throw new IOException("Parser has been closed");
    }
    if (iterator == null) {
      iterator = parser.readValuesAs(String[].class);
    }
    while (iterator.hasNext()) {
      String[] record = iterator.next();
      long prevPos = currentPos;
      currentPos = parser.nextToken() != null
          ? parser.getCurrentLocation().getCharOffset() + 1
          : reader.getPos();
      if (maxObjectLen > -1) {
        if (currentPos - prevPos > maxObjectLen) {
          ExceptionUtils.throwUndeclared(new ObjectLengthException(
              Utils.format("CSV Object at offset '{}' exceeds max length '{}'", prevPos, maxObjectLen),
              prevPos));
        }
      }
      if (csvFormat.ignoreEmptyLines()) {
        LOG.debug("ignoreEmptyLines is enabled");
        if (record.length == 1 && record[0].isEmpty()) {
          continue;
        }
      }
      // TODO: Trimming spaces will be supported in Jackson CSV 2.7.
      // As of writing, 2.7 is not released yet, so we do it by ourselves.
      if (csvFormat.ignoreSurroundingSpaces()) {
        LOG.debug("ignoreSurroundingSpaces is enabled");
        for (int i = 0; i < record.length; i++) {
          record[i] = record[i].trim();
        }
      }
      return record;
    }
    return null;
  }

  @Override
  public void close() {
    try {
      closed = true;
      parser.close();
    } catch (IOException ex) {
      //NOP
    }
  }
}
