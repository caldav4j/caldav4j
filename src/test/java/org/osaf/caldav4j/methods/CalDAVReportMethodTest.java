package org.osaf.caldav4j.methods;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.functional.support.CaldavFixtureHarness;
import org.osaf.caldav4j.model.request.CalDAVReportRequest;
import org.osaf.caldav4j.support.HttpClientTestUtils;
import org.osaf.caldav4j.util.CaldavStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.osaf.caldav4j.support.HttpClientTestUtils.executeMethod;
import static org.osaf.caldav4j.support.HttpMethodCallbacks.calendarReportCallback;

@Ignore
public class CalDAVReportMethodTest extends BaseTestCase {

	private static final Logger log = LoggerFactory.getLogger(CalDAVReportMethodTest.class);
	// private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();



	@Before
	public void setUp() throws Exception {
		super.setUp();
		CaldavFixtureHarness.provisionSimpleEvents(fixture);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		fixture.tearDown();
	}

	@Test
	public void testCheckTrailingSlash() throws Exception{
		CalDAVReportMethod method = fixture.getMethodFactory().createCalDAVReportMethod(fixture.getCollectionPath());
		assertNotNull(method);

		assertNotNull(method.getPath());
		assertTrue(method.getPath().endsWith("/"));

	}

    //Below are the merged tests from CalendarCalDAVReportMethod
	@Test
	public void executeCalendarReportMethod() throws Exception
	{
		CalDAVReportMethod method = createMethod("/path", new FakeCalDAVReportRequest());

		String expectedRequest = "REPORT /path HTTP/1.1\r\n"
				+ "User-Agent: Jakarta Commons-HttpClient/3.1\r\n"
				+ "Host: localhost\r\n"
				+ "Content-Length: 36\r\n"
				+ "Depth: 1\r\n"
				+ "Content-Type: text/xml\r\n"
				+ "\r\n"
				+ "<?xml version=\"1.0\"?>\n"
				+ "<fake-query/>\n";

		String response = "HTTP/1.1 200 OK\r\n"
				+ "Content-Type: text/calendar"
				+ "Content-Length: 189\r\n"
				+ "\r\n"
				+ "BEGIN:VCALENDAR\n"
				+ "PRODID:fake\n"
				+ "VERSION:2.0\n"
				+ "END:VCALENDAR\n";

		HttpClientTestUtils.setFakeSocketImpl(expectedRequest, response);
		Calendar actual = executeMethod(CaldavStatus.SC_OK, method, calendarReportCallback());

		assertEquals("Calendar", createCalendar(), actual);
	}

	// private methods --------------------------------------------------------

	private static CalDAVReportMethod createMethod(String path, CalDAVReportRequest reportRequest) throws IOException {
		CalDAVReportMethod method = new CalDAVReportMethod(path, reportRequest);

		method.setCalendarBuilder(new CalendarBuilder());

		return method;
	}

	private static Calendar createCalendar()
	{
		Calendar calendar = new Calendar();

		calendar.getProperties().add(new ProdId("fake"));
		calendar.getProperties().add(Version.VERSION_2_0);

		return calendar;
	}


}