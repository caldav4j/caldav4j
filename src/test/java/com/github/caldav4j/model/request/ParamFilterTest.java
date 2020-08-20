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

import static org.junit.Assert.*;

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.exceptions.DOMValidationException;
import com.github.caldav4j.util.XMLUtils;
import org.junit.Test;

public class ParamFilterTest extends BaseTestCase {

    /** Don't need setUp here */
    @Override
    public void setUp() throws Exception {}
    ;

    @Test
    public void testSimpleConstructor() {
        ParamFilter p = new ParamFilter();
        try {
            // This won't work because name will be null
            p.validate();
            fail("should fail if no name specified");
        } catch (DOMValidationException e) {
            assertNotNull("Ok, doesn't accept null name", e);
        }
    }

    @Test
    public void testName() {
        ParamFilter p = new ParamFilter();
        p.setName("newname");
        assertEquals(p.getName(), "newname");
        try {
            p.validate();
            log.info(XMLUtils.prettyPrint(p));
        } catch (DOMValidationException e) {
            fail("Should have a valid ParamFilter");
        }

        p.setDefined(false);
    }

    @Test
    public void testDefined() {
        ParamFilter p = new ParamFilter();
        p.setName("testDefined");
        assertEquals(p.getName(), "testDefined");
        p.setDefined(true);

        try {
            p.validate();
            log.info(XMLUtils.prettyPrint(p));
        } catch (DOMValidationException e) {
            fail("Should have a valid ParamFilter");
        }
    }

    @Test
    public void testTextMatch() {
        ParamFilter p = new ParamFilter();
        p.setName("testDefined");
        assertEquals(p.getName(), "testDefined");
        p.setTextMatch(new TextMatch(null, null, null, "testTextMatch"));
        try {
            p.validate();
            log.info(XMLUtils.prettyPrint(p));
        } catch (DOMValidationException e) {
            fail("Should have a valid ParamFilter");
        }
    }
}
