/*
 * Copyright © 2018 Ankush Mishra
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

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.cache.EhCacheResourceCache;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.functional.support.CaldavFixtureHarness;
import com.github.caldav4j.model.request.*;
import com.github.caldav4j.model.response.CalendarDataProperty;
import com.github.caldav4j.util.ICalendarUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ankush Mishra
 */
public class CalDAVReportTest extends BaseTestCase {

    private static final Logger log = LoggerFactory.getLogger(CalDAVReportTest.class);
    private EhCacheResourceCache myCache = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        CaldavFixtureHarness.provisionGoogleEvents(fixture);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        fixture.tearDown();
    }

    private String ElementoString(Element node) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(node);
        transformer.transform(source, result);

        String xmlString = result.getWriter().toString();
        log.info(xmlString);
        return xmlString;
    }

    public void printXml(XmlSerializable xml)
            throws ParserConfigurationException, TransformerException {
        Document document = DomUtil.createDocument();
        ElementoString(xml.toXml(document));
    }

    @Test
    public void printCalendarQuery() throws TransformerException, ParserConfigurationException {
        CalendarData calendarData = new CalendarData();
        Comp vcalendar = new Comp("VCALENDAR");
        vcalendar.addProp(new CalDAVProp("VERSION"));
        vcalendar.addComp(new Comp("VEVENT"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("SUMMARY"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("UID"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("DTSTART"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("DTEND"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("DURATION"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("RRULE"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("RDATE"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("EXRULE"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("EXDATE"));
        vcalendar.getComps().get(0).addProp(new CalDAVProp("RECURRENCE-ID"));
        vcalendar.addComp(new Comp("VTIMEZONE"));
        calendarData.setComp(vcalendar);

        CompFilter filter = new CompFilter("VCALENDAR");
        filter.getCompFilters().add(new CompFilter("VEVENT"));
        filter.getCompFilters().get(0).setTimeRange(new TimeRange(new DateTime(), new DateTime()));
        CalendarQuery reportInfo = new CalendarQuery();
        reportInfo.setCompFilter(filter);
        reportInfo.setCalendarDataProp(calendarData);
        // reportInfo.setAllProp(true);
        reportInfo.addProperty(DavPropertyName.GETETAG);
        printXml(reportInfo);
    }

    @Test
    public void queryPartialCalendar()
            throws IOException,
                    TransformerException,
                    ParserConfigurationException,
                    ParseException,
                    DavException {
        String collectionPath = fixture.getCollectionPath();
        Calendar calendar = null;

        HttpClient http = fixture.getHttpClient();
        HttpHost hostConfig = fixture.getHostConfig();

        CalendarQuery calendarQuery = new CalendarQuery();
        CalendarData calendarData =
                new CalendarData(
                        CalendarData.EXPAND,
                        new DateTime("20060103T000000Z"),
                        new DateTime("20060105T230000Z"),
                        null); // new Comp("VCALENDAR"));
        CompFilter vcalendar = new CompFilter("VCALENDAR");
        vcalendar.addCompFilter(new CompFilter("VEVENT"));
        vcalendar
                .getCompFilters()
                .get(0)
                .setTimeRange(
                        new TimeRange(
                                new DateTime("20060103T000000Z"),
                                new DateTime("20060105T230000Z")));

        calendarQuery.setCalendarDataProp(calendarData);
        calendarQuery.setCompFilter(vcalendar);
        printXml(calendarQuery);
        HttpCalDAVReportMethod calDAVReportMethod =
                new HttpCalDAVReportMethod(collectionPath, calendarQuery);

        HttpResponse response = http.execute(hostConfig, calDAVReportMethod);
        log.info(response.getStatusLine().toString());

        Collection<DavProperty> calendars =
                calDAVReportMethod.getDavProperties(response, CalDAVConstants.DNAME_CALENDAR_DATA);

        ComponentList<VEvent> templist = new ComponentList<>();

        for (DavProperty property : calendars) {
            templist.addAll(
                    CalendarDataProperty.getCalendarfromProperty(property)
                            .getComponents(Component.VEVENT));
        }

        assertEquals(3, templist.getAll().size());
    }

    /**
     * Tests the FreeBusyQuery, on the server and checks the starts and end dates for verification
     */
    @Test
    public void testFreeBusyQuery() throws ParseException, IOException, CalDAV4JException {
        HttpClient client = fixture.getHttpClient();

        FreeBusyQuery query =
                new FreeBusyQuery(
                        new TimeRange(
                                new DateTime("20060104T140000Z"),
                                new DateTime("20060105T220000Z")));

        HttpCalDAVReportMethod method =
                fixture.getMethodFactory()
                        .createCalDAVReportMethod(
                                fixture.getCollectionPath(), query, CalDAVConstants.DEPTH_1);
        HttpResponse response = client.execute(fixture.getHostConfig(), method);
        Calendar calendar = method.getResponseBodyAsCalendar(response);
        VFreeBusy freeBusy = (VFreeBusy) ICalendarUtils.getFirstComponent(calendar);

        /// Check returned Calendar
        assertEquals(
                "Start Dates are not equal",
                freeBusy.getDateTimeStart().orElse(null),
                new DtStart<OffsetDateTime>("20060104T140000Z"));
        assertEquals(
                "End Dates are not equal",
                freeBusy.getDateTimeEnd().orElse(null),
                new DtEnd<OffsetDateTime>("20060105T220000Z"));
    }
}
