/*
 * Copyright 2003-2013 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.http;

import java.io.IOException;

public class NullPartSplitter implements MultipartSplitter {
    public long getTotalBytesRead() {
        return 0;
    }

    public boolean hasNext() throws IOException {
        return false;
    }

    public void next() throws IOException {
        throw new IOException("Empty result");
    }

    public int read() throws IOException {
        return -1;
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {
        return -1;
    }

    public void close() {
        // do nothing
    }

    public void setBufferSize(int size) {
        // do nothing
    }

    public int getBufferSize() {
        return 0;
    }
}
