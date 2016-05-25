package org.osaf.caldav4j.methods;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.cache.EhCacheResourceCache;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.functional.support.CaldavFixtureHarness;
import org.osaf.caldav4j.model.request.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by emanon on 5/18/16.
 */
public class NewCalDAVReportTest extends BaseTestCase{

    private static final Log log = LogFactory.getLog(NewCalDAVReportTest.class);
    private EhCacheResourceCache myCache = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        CaldavFixtureHarness.provisionGoogleEvents(fixture);

        myCache = CaldavFixtureHarness.createSimpleCache();
        collection.setCache(myCache);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        fixture.tearDown();
    }

    private String ElementoString(Element node) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(node);
        transformer.transform(source, result);

        String xmlString = result.getWriter().toString();
        log.info(xmlString);
        return xmlString;
    }

    public void printXml(XmlSerializable xml) throws ParserConfigurationException, TransformerException {
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

        CompFilter filter = new CompFilter("C", "VCALENDAR");
        filter.getCompFilters().add(new CompFilter("C", "VEVENT"));
        filter.getCompFilters().get(0).setTimeRange(
                new TimeRange(new DateTime(), new DateTime()));
        CalendarQuery reportInfo = new CalendarQuery();
        reportInfo.setCompFilter(filter);
        reportInfo.setCalendarDataProp(calendarData);
        //reportInfo.setAllProp(true);
        reportInfo.addProperty(DavPropertyName.GETETAG);
        printXml(reportInfo);
    }

    @Test
    public void queryPartialCalendar() throws CalDAV4JException, IOException, TransformerException, ParserConfigurationException, DavException, ParseException {
        String collectionPath = fixture.getCollectionPath();
        Calendar calendar = null;

        HttpClient http = fixture.getHttpClient();
        HostConfiguration hostConfig = http.getHostConfiguration();

        CalendarQuery calendarQuery = new CalendarQuery();
        CalendarData calendarData = new CalendarData(CalendarData.EXPAND, new DateTime("20060101T000000Z"), new DateTime("20060105T230000Z"), null);
        CompFilter vcalendar = new CompFilter("C", "VCALENDAR");
        vcalendar.addCompFilter(new CompFilter("", "VEVENT"));
        vcalendar.getCompFilters().get(0).setTimeRange(new TimeRange(new DateTime("20060101T000000Z"), new DateTime("20060105T230000Z")));

        calendarQuery.setCalendarDataProp(calendarData);
        calendarQuery.setCompFilter(vcalendar);
        printXml(calendarQuery);
        NewCalDAVReportMethod calDAVReportMethod = new NewCalDAVReportMethod(collectionPath, calendarQuery);

        http.executeMethod(hostConfig, calDAVReportMethod);
        log.info(calDAVReportMethod.getStatusLine());

        Collection<DavProperty> calendars = calDAVReportMethod.getDavProperty(DavPropertyName.create(CalDAVConstants.CALDAV_CALENDAR_DATA, CalDAVConstants.NAMESPACE_CALDAV));

        for(DavProperty property: calendars)
            printXml(property);
        assertEquals(2, calendars.size());
    }


}
