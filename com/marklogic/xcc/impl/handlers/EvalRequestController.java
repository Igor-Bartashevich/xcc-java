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
package com.marklogic.xcc.impl.handlers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.marklogic.http.HttpChannel;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.RequestOptions;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.impl.SessionImpl;
import com.marklogic.xcc.spi.ServerConnection;

public class EvalRequestController extends AbstractRequestController {
    private static final Map<Integer, ResponseHandler> handlers = new HashMap<Integer, ResponseHandler>(8);

    static {
        addDefaultHandler(handlers, new UnrecognizedCodeHandler());
        addHandler(handlers, HttpURLConnection.HTTP_UNAVAILABLE, new ServiceUnavailableHandler());
        addHandler(handlers, HttpURLConnection.HTTP_INTERNAL_ERROR, new ServerExceptionHandler());
        addHandler(handlers, HttpURLConnection.HTTP_UNAUTHORIZED, new UnauthorizedHandler());
        addHandler(handlers, HttpURLConnection.HTTP_NOT_FOUND, new NotFoundCodeHandler());
        addHandler(handlers, HttpURLConnection.HTTP_BAD_REQUEST, new NotFoundCodeHandler());
        addHandler(handlers, HttpURLConnection.HTTP_OK, new GoodQueryResponseHandler());
    }

    // --------------------------------------------------------

    protected final String body;
    private final String path;

    // --------------------------------------------------------

    public EvalRequestController(String path, String body) {
        super(handlers);

        this.path = path;
        this.body = body;
    }

    // --------------------------------------------------------
    // Invoked by superclass template method

    @Override
    public ResultSequence serverDialog(ServerConnection connection, Request request, RequestOptions options,
            Logger logger) throws IOException, RequestException {
        SessionImpl session = (SessionImpl)request.getSession();

        HttpChannel http = buildChannel(connection, path, session, options, logger);

        issueRequest(http, body, logger);

        int code = http.getResponseCode();

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("response: " + code + " (" + http.getResponseMessage() + ")");
        }

        session.setServerVersion(http.getServerVersion());
        session.readCookieValues(http);
        
        setConnectionTimeout(connection, http);

        return (ResultSequence)findHandler(code).handleResponse(http, code, request, connection, logger);
    }

    // --------------------------------------------------------

    @Override
    protected long interTryDelay(long delay, int currentTry) {
        if ((currentTry == 0) || (delay <= 0)) {
            return 0;
        }

        return delay;
    }

    // --------------------------------------------------------

    private HttpChannel buildChannel(ServerConnection connection, String path, SessionImpl session,
            RequestOptions options, Logger logger) {
        String method = "POST";

        HttpChannel http = new HttpChannel(connection.channel(), method, path, body.length(), options
                .getTimeoutMillis(), logger);

        http.setRequestContentType("application/x-www-form-urlencoded");
        http.setCloseOutputIfNoContentLength(true);

        addCommonHeaders(http, session, method, path, options, logger);

        return (http);
    }

    // -----------------------------------------------------

    private void issueRequest(HttpChannel http, String encodedQuery, Logger logger) throws IOException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("encoded query: " + encodedQuery);
        }

        logger.fine("writing query to HttpChannel");
        http.writeString(encodedQuery);
    }
}
