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

/**
 * This is the root class for all CalDAV4J specific exceptions.
 *
 * @author bobbyrullo
 */
public class CalDAV4JException extends Exception {

    private static final long serialVersionUID = 1L;

    public CalDAV4JException(String message) {
        super(message);
    }

    public CalDAV4JException(String message, Throwable cause) {
        super(message, cause);
    }
}
