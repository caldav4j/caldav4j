/*
 * Copyright 2011 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Mark Hobson, Roberto Polli
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

package com.github.caldav4j.functional;

import static com.github.caldav4j.support.CalendarAssert.assertEqualsIgnoring;
import static com.github.caldav4j.support.HttpMethodCallbacks.calendarReportCallback;

import com.github.caldav4j.credential.CaldavCredential;
import com.github.caldav4j.dialect.CalDavDialect;
import com.github.caldav4j.dialect.ChandlerCalDavDialect;
import com.github.caldav4j.functional.support.CalDavFixture;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.model.request.FreeBusyQuery;
import com.github.caldav4j.model.request.TimeRange;
import com.github.caldav4j.support.CalendarBuilder;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.ICalendarUtils;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Period;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Uid;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Functional test for {@code FreeBusyQuery}.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see FreeBusyQuery
 */
@Ignore
public class FreeBusyQueryFunctionalTest {
    // fields -----------------------------------------------------------------

    private final CaldavCredential credential;

    private final CalDavDialect dialect;

    private CalDavFixture fixture;

    private CalendarBuilder builder;

    // constructors -----------------------------------------------------------

    private FreeBusyQueryFunctionalTest() {
        // TODO: run test for all server implementations
        this(new CaldavCredential(), new ChandlerCalDavDialect());
    }

    public FreeBusyQueryFunctionalTest(CaldavCredential credential, CalDavDialect dialect) {
        this.credential = credential;
        this.dialect = dialect;
    }

    // public methods ---------------------------------------------------------

    @Before
    public void setUp() throws IOException {
        fixture = new CalDavFixture();
        fixture.setUp(credential, dialect);

        builder = new CalendarBuilder(dialect);
    }

    @After
    public void tearDown() throws IOException {
        fixture.tearDown();
    }

    // tests ------------------------------------------------------------------

    @Test
    public void freeBusyQueryReportWithEmptyCalendar() throws Exception {
        HttpCalDAVReportMethod method =
                createFreeBusyQueryMethod("", "20000101T000000Z", "20000201T000000Z");

        Calendar expected = createFreeBusyCalendar("20000101T000000Z", "20000201T000000Z", null);

        Calendar actual =
                fixture.executeMethod(CalDAVStatus.SC_OK, method, false, calendarReportCallback());

        assertEqualsIgnoringUidAndDtStampAndAttendee(expected, actual);
    }

    @Test
    public void freeBusyQueryReportWithEvent() throws Exception {
        fixture.putEvent("a.ics", createEvent("a", "20000107T000000Z", "P1D", "a"));

        HttpCalDAVReportMethod method =
                createFreeBusyQueryMethod("", "20000101T000000Z", "20000201T000000Z");

        Calendar expected =
                createFreeBusyCalendar(
                        "20000101T000000Z",
                        "20000201T000000Z",
                        "20000107T000000Z/20000108T000000Z");

        Calendar actual =
                fixture.executeMethod(CalDAVStatus.SC_OK, method, false, calendarReportCallback());

        assertEqualsIgnoringUidAndDtStampAndAttendee(expected, actual);
    }

    // private methods --------------------------------------------------------

    private static VEvent createEvent(String uid, String start, String duration, String summary)
            throws ParseException {

        VEvent e = new VEvent(LocalDateTime.parse(start), Period.parse(duration), summary);
        ICalendarUtils.addOrReplaceProperty(e, new Uid(uid));
        return e;
    }

    private HttpCalDAVReportMethod createFreeBusyQueryMethod(
            String relativePath, String rangeStart, String rangeEnd)
            throws ParseException, IOException {
        FreeBusyQuery query = new FreeBusyQuery();
        query.setTimeRange(new TimeRange(new DateTime(rangeStart), new DateTime(rangeEnd)));

        return new HttpCalDAVReportMethod(relativePath, query);
    }

    private Calendar createFreeBusyCalendar(String start, String end, String busyPeriods)
            throws ParseException {
        Calendar calendar = builder.createCalendar();

        VFreeBusy vFreeBusy = new VFreeBusy(LocalDateTime.parse(start), LocalDateTime.parse(end));

        if (busyPeriods != null) {
            FreeBusy freeBusy = new FreeBusy(busyPeriods);

            // TODO: perhaps move this decision to the dialect?
            freeBusy.getParameters().add(FbType.BUSY);

            vFreeBusy.add(freeBusy);
        }

        calendar.add(vFreeBusy);

        return calendar;
    }

    private static void assertEqualsIgnoringUidAndDtStamp(Calendar expected, Calendar actual) {
        assertEqualsIgnoring(expected, actual, Property.UID, Property.DTSTAMP);
    }

    private static void assertEqualsIgnoringUidAndDtStampAndAttendee(
            Calendar expected, Calendar actual) {
        assertEqualsIgnoring(expected, actual, Property.UID, Property.DTSTAMP, Property.ATTENDEE);
    }
}
