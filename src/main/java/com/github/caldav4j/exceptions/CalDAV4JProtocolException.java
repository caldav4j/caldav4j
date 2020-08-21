/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Bobby Rullo, Roberto Polli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.caldav4j.exceptions;

/**
 * This is the base class for low level CalDAV4J Exceptions
 *
 * @author bobbyrullo
 */
public class CalDAV4JProtocolException extends CalDAV4JException {

    private static final long serialVersionUID = 8830949627634284982L;

    public CalDAV4JProtocolException(String message) {
        super(message);
    }

    public CalDAV4JProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
