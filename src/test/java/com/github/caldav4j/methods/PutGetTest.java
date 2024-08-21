/*
 * Copyright © 2018 Ankush Mishra, Bobby Rullo, Roberto Polli
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
import static org.junit.Assert.assertTrue;

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.model.request.CalendarRequest;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.ICalendarUtils;
import com.github.caldav4j.util.MethodUtil;
import com.github.caldav4j.util.UrlUtils;
import java.nio.charset.Charset;
import java.util.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Ignore // to be run under functional
public class PutGetTest extends BaseTestCase {

    private static final Logger log = LoggerFactory.getLogger(PutGetTest.class);
    private ResourceBundle messages;

    private List<String> addedEventsFile = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        try {
            for (String s : addedEventsFile) {
                fixture.delete(s);
            }
        } finally {
            fixture.tearDown();
        }
    }

    @Test
    public void testResourceBundle() {
        // load an ICS and substitute summary with non-latin chars
        Locale mylocale = new Locale("ru", "RU");
        ResourceBundle messages = PropertyResourceBundle.getBundle("messages", mylocale);
        String myLocalSummary = messages.getString("summary");
        log.info("default charset: " + Charset.defaultCharset());
        assertTrue(true);
    }

    @Test
    public void testAddRemoveCalendarResource() throws Exception {
        HttpClient http = createHttpClient();
        HttpHost hostConfig = createHostConfiguration();
        String eventPath =
                UrlUtils.removeDoubleSlashes(
                        String.format(
                                "%s/%s.ics",
                                fixture.getCollectionPath(), BaseTestCase.ICS_DAILY_NY_5PM_UID));

        Calendar cal = getCalendarResource(BaseTestCase.ICS_DAILY_NY_5PM_PATH);

        CalendarRequest cr = new CalendarRequest(cal, false, true, true);
        HttpPutMethod put = fixture.getMethodFactory().createPutMethod(eventPath, cr);
        HttpResponse response = http.execute(hostConfig, put);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals("Status code for put:", CalDAVStatus.SC_CREATED, statusCode);
        addedEventsFile.add(BaseTestCase.ICS_DAILY_NY_5PM_UID + ".ics");
        // ok, so we created it...let's make sure it's there!
        HttpGetMethod get = fixture.getMethodFactory().createGetMethod(eventPath);

        response = http.execute(hostConfig, get);
        statusCode = response.getStatusLine().getStatusCode();
        MethodUtil.StatusToExceptions(get, response);
        assertEquals("Status code for get: ", CalDAVStatus.SC_OK, statusCode);

        // now let's make sure we can get the resource body as a calendar
        Calendar calendar = get.getResponseBodyAsCalendar(response);
        VEvent event = ICalendarUtils.getFirstEvent(calendar);
        String uid = ICalendarUtils.getUIDValue(event);
        assertEquals(ICS_DAILY_NY_5PM_UID, uid);

        // let's make sure that a subsequent put with "if-none-match: *" fails

        put =
                fixture.getMethodFactory()
                        .createPutMethod(eventPath, new CalendarRequest(cal, false, true, true));

        response = http.execute(hostConfig, put);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals("Status code for put:", CalDAVStatus.SC_PRECONDITION_FAILED, statusCode);
    }

    /** TODO test PUT with non-latin characters */
    @Test
    public void testPutNonLatin() throws Exception {

        HttpClient http = createHttpClient();
        HttpHost hostConfig = createHostConfiguration();

        // load an ICS and substitute summary with non-latin chars
        Locale mylocale = new Locale("ru", "RU");
        messages = PropertyResourceBundle.getBundle("messages", mylocale);
        String myLocalSummary = messages.getString("summary");
        log.info("default charset: " + Charset.defaultCharset());

        Calendar cal = getCalendarResource(BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM_PATH);
        Optional<CalendarComponent> calendarComponent = cal.getComponent(Component.VEVENT);
        assertTrue(calendarComponent.isPresent());
        ICalendarUtils.addOrReplaceProperty(calendarComponent.get(), new Summary(myLocalSummary));
        assertEquals(
                myLocalSummary,
                ICalendarUtils.getPropertyValue(calendarComponent.get(), Property.SUMMARY));

        // create a PUT request with the given ICS

        String eventPath = BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM_UID + ".ics";

        HttpPutMethod put =
                fixture.getMethodFactory()
                        .createPutMethod(
                                fixture.getCollectionPath() + "/" + eventPath,
                                new CalendarRequest(cal, false, true, true));

        HttpResponse response = http.execute(hostConfig, put);
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals("Status code for put:", CalDAVStatus.SC_CREATED, statusCode);
        addedEventsFile.add(eventPath);

        // ok, so we created it...let's make sure it's there!
        HttpGetMethod get =
                fixture.getMethodFactory()
                        .createGetMethod(fixture.getCollectionPath() + "/" + eventPath);

        response = http.execute(hostConfig, get);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals("Status code for get: ", CalDAVStatus.SC_OK, statusCode);

        // now let's make sure we can get the resource body as a calendar
        Calendar calendar = get.getResponseBodyAsCalendar(response);
        VEvent event = ICalendarUtils.getFirstEvent(calendar);
        String uid = ICalendarUtils.getUIDValue(event);
        String summary = ICalendarUtils.getPropertyValue(event, Property.SUMMARY);
        assertEquals(ICS_DAILY_NY_5PM_UID, uid);
        assertEquals(myLocalSummary, summary);

        // let's make sure that a subsequent put with "if-none-match: *" fails
        put =
                fixture.getMethodFactory()
                        .createPutMethod(
                                fixture.getCollectionPath() + "/" + eventPath,
                                new CalendarRequest(cal, false, true, true));

        response = http.execute(hostConfig, put);
        statusCode = response.getStatusLine().getStatusCode();
        assertEquals("Status code for put:", CalDAVStatus.SC_PRECONDITION_FAILED, statusCode);

        // test for exceptions
        // moreover: try a GET to see if event is changed
    }
}
