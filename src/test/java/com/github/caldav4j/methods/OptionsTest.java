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

package com.github.caldav4j.methods;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.util.CalDAVStatus;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.jackrabbit.webdav.client.methods.HttpOptions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore // run thru functional OptionITCase
public class OptionsTest extends BaseTestCase {

    public static final String OUTBOX = "/Outbox/";
    public static final String INBOX = "/Inbox/";

    // private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

    @Before
    @Override
    // do not need the initialization in the base class
    public void setUp() throws Exception {}

    /**
     * >> Request <<
     *
     * <p>OPTIONS /lisa/calendar/outbox/ HTTP/1.1 Host: cal.example.com
     *
     * <p>>> Response <<
     *
     * <p>HTTP/1.1 204 No Content Date: Thu, 31 Mar 2005 09:00:00 GMT Allow: OPTIONS, GET, HEAD,
     * POST, DELETE, TRACE, Allow: PROPFIND, PROPPATCH, LOCK, UNLOCK, REPORT, ACL DAV: 1, 2, 3,
     * access-control DAV: calendar-access, calendar-auto-schedule
     */
    @Test
    public void testOptions() {
        HttpClient http = createHttpClient();
        HttpHost hostConfig = createHostConfiguration();

        for (String s : new String[] {INBOX, OUTBOX}) {

            HttpOptions options = new HttpOptions(caldavCredential.home + s);

            try {
                HttpResponse response = http.execute(hostConfig, options);
                if (response.getStatusLine().getStatusCode() == CalDAVStatus.SC_OK) {
                    log.info(response.getFirstHeader("Allow").toString());
                    for (Header h : response.getHeaders("DAV")) {
                        if (h != null) {
                            if (h.getValue().contains("calendar-access")) {
                                log.info(h.toString());
                            } else if (h.getValue().contains("calendar-schedule")
                                    || h.getValue().contains("calendar-auto-schedule")) {
                                log.info(h.toString());
                            } else {
                                assertTrue(false);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }
}
