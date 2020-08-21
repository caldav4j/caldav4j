/*
 * Copyright 2006 Open Source Applications Foundation
 * Copyright © 2018 Bobby Rullo, Roberto Polli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.caldav4j.exceptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Issues with Status Code invoke this exception.
 *
 * @author bobbyrullo
 */
public class BadStatusException extends CalDAV4JException {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Bad status %d invoking method %s %s";

    public BadStatusException(String message) {
        super(message);
    }

    public BadStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadStatusException(int status, String method, String path) {
        super(String.format(MESSAGE, status, method, path));
    }

    public <T extends HttpRequestBase> BadStatusException(T method, HttpResponse response) {
        super(
                String.format(
                        MESSAGE,
                        response.getStatusLine().getStatusCode(),
                        method.getMethod(),
                        method.getURI()));
    }
}
