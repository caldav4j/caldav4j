/*
 * Copyright © 2018 Roberto Polli
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

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.util.XMLUtils;
import org.junit.Test;

public class CalendarDescriptionTest extends BaseTestCase {

    public CalendarDescriptionTest() {
        super();
    }

    /** Don't need BaseTestCase.setUp() here */
    @Override
    public void setUp() throws Exception {}

    @Test
    public void testPrintCalendarDescription() {
        CalendarDescription d = new CalendarDescription();
        log.info(XMLUtils.prettyPrint(d));

        d = new CalendarDescription("My Description");
        log.info(XMLUtils.prettyPrint(d));

        d = new CalendarDescription("My Description", "it");
        log.info(XMLUtils.prettyPrint(d));
    }

    @Test
    public void testPrintDisplayName() {
        DisplayName d = new DisplayName();
        log.info(XMLUtils.prettyPrint(d));

        d = new DisplayName("My Description");
        log.info(XMLUtils.prettyPrint(d));
    }
}
