/*
 * Copyright © 2018 Ankush Mishra, Roberto Polli
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
package com.github.caldav4j.model.request;

import com.github.caldav4j.CalDAVConstants;

/**
 * This property defines the human-readable name of the calendar.
 *
 * @author rpolli, Ankush Mishra
 */
public class DisplayName extends PropProperty<String> {
    public static final String DISPLAY_NAME = "displayname";

    public DisplayName() {
        this(null);
    }

    /**
     * @param displayName Display name of the calendar
     */
    public DisplayName(String displayName) {
        super(DISPLAY_NAME, displayName, CalDAVConstants.NAMESPACE_WEBDAV);
    }
}
