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
package com.streamsets.pipeline.lib.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * The lifecycle of a file context provider is:
 *
 * repeat as needed:
 *
 *    setOffsets()
 *    loop while disFullLoop()
 *      next()
 *    optionally call startNewLoop() and do a new loop
 *    getOffsets()
 *
 *  close()
 *
 */
public interface FileContextProvider extends Closeable {

  void setOffsets(Map<String, String> offsets) throws IOException;

  FileContext next();

  boolean didFullLoop();

  void startNewLoop();

  Map<String, String> getOffsets() throws IOException;

  void purge();

}
