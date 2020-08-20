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

package com.github.caldav4j.util;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UrlUtilsTest {

    @Test
    public void stripDoubleSlashes() {

        String uris[] = {"http://a/b/c/", "http://a//b/c//", "http:///a/b/c///", "http://a///b/c/"};
        String EXPECTED = "http://a/b/c/";
        for (String s : uris) {
            String s1 = UrlUtils.removeDoubleSlashes(s);
            assertEquals(EXPECTED, s1);
            assertEquals(EXPECTED, UrlUtils.removeDoubleSlashes(s1));
        }
    }

    @Test
    public void stripDoesntRegress() {

        String uris[] = {"/a/b/c/", "/a//b/c//", "/a///b/c///", "/a///b////c///"};
        String EXPECTED = "/a/b/c/";
        for (String s : uris) {
            String s1 = UrlUtils.removeDoubleSlashes(s);
            assertEquals(EXPECTED, s1);
            assertEquals(EXPECTED, UrlUtils.removeDoubleSlashes(s1));
        }
    }

    @Test
    public void testIsBlank() throws Exception {
        String s = null;
        assertTrue(UrlUtils.isBlank(s));
        s = "";
        assertTrue(UrlUtils.isBlank(s));
        s = "    \t\r\n";
        assertTrue(UrlUtils.isBlank(s));
    }
}
