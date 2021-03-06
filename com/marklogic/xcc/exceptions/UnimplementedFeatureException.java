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
package com.marklogic.xcc.exceptions;

/**
 * This exception indicates either that some planned feature is not yet implemented in the current
 * version of XCC, or that the server to which it is connecting does not support some the operation.
 */
public class UnimplementedFeatureException extends RuntimeException {
    private static final long serialVersionUID = -5553034179449649216L;

    public UnimplementedFeatureException(String message) {
        super(message);
    }
}
