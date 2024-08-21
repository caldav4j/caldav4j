/*
 * Copyright © 2018 Ankush Mishra, Bobby Rullo, Mark Hobson, Roberto Polli
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

import static org.junit.Assert.assertEquals;

import com.github.caldav4j.model.request.CalDAVReportRequest;
import com.github.caldav4j.support.HttpClientTestUtils;
import com.github.caldav4j.support.HttpMethodCallbacks;
import com.github.caldav4j.util.CalDAVStatus;
import java.io.IOException;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the HttpClient, by overriding the socket and verifying the output there. Ignored, because
 * this test will set the Socket for the whole JVM.
 */
@Ignore
public class HttpClientTest {

    private static final Logger log = LoggerFactory.getLogger(HttpClientTest.class);

    // Below are the merged tests from CalendarCalDAVReportMethod
    @Test
    public void executeCalendarReportMethod() throws Exception {
        HttpCalDAVReportMethod method = createMethod("/path", new FakeCalDAVReportRequest());

        method.setHeader("User-Agent", "Apache-HttpClient/CalDAV4j");
        String expectedRequest =
                "REPORT /path HTTP/1.1\r\n"
                        + "Depth: 1\r\n"
                        + "User-Agent: Apache-HttpClient/CalDAV4j\r\n"
                        + "Content-Length: 67\r\n"
                        + "Content-Type: application/xml; charset=UTF-8\r\n"
                        + "Host: localhost:80\r\n"
                        + "Connection: Keep-Alive\r\n"
                        + "Accept-Encoding: gzip,deflate\r\n"
                        + "\r\n"
                        + "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><fake-query/>";

        String response =
                "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/calendar"
                        + "Content-Length: 189\r\n"
                        + "\r\n"
                        + "BEGIN:VCALENDAR\n"
                        + "PRODID:fake\n"
                        + "VERSION:2.0\n"
                        + "END:VCALENDAR\n";

        HttpClientTestUtils.setFakeSocketImplFactory();
        HttpClientTestUtils.setFakeSocketImpl(expectedRequest, response);
        Calendar actual =
                HttpClientTestUtils.executeMethod(
                        CalDAVStatus.SC_OK, method, HttpMethodCallbacks.calendarReportCallback());

        assertEquals("Calendar", createCalendar(), actual);
    }

    // private methods --------------------------------------------------------

    private static HttpCalDAVReportMethod createMethod(
            String path, CalDAVReportRequest reportRequest) throws IOException {
        HttpCalDAVReportMethod method = new HttpCalDAVReportMethod(path, reportRequest);

        method.setCalendarBuilder(new CalendarBuilder());

        return method;
    }

    private static Calendar createCalendar() {
        Calendar calendar = new Calendar();

        calendar.add(new ProdId("fake"));
        calendar.add(ImmutableVersion.VERSION_2_0);

        return calendar;
    }
}
