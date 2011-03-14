/*
 * Copyright 2011 Open Source Applications Foundation
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
package org.osaf.caldav4j.methods;

import static org.junit.Assert.assertEquals;
import static org.osaf.caldav4j.support.HttpClientTestUtils.executeMethod;
import static org.osaf.caldav4j.support.HttpMethodCallbacks.calendarReportCallback;

import java.io.IOException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.model.request.CalDAVReportRequest;
import org.osaf.caldav4j.support.HttpClientTestUtils;
import org.osaf.caldav4j.util.CaldavStatus;

/**
 * Tests {@code CalendarCalDAVReportMethod}.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see CalendarCalDAVReportMethod
 */
public class CalendarCalDAVReportMethodTest extends CaldavStatus
{
	// public methods ---------------------------------------------------------
	
	@Before
	public void setUp() throws IOException
	{
		HttpClientTestUtils.setFakeSocketImplFactory();
	}
	
	@After
	public void tearDown() throws IOException
	{
		HttpClientTestUtils.unsetFakeSocketImplFactory();
	}

	// tests ------------------------------------------------------------------
	
	@Test
	public void execute() throws Exception
	{
		CalendarCalDAVReportMethod method = createMethod("/path", new FakeCalDAVReportRequest());
		
		String expectedRequest = "REPORT /path HTTP/1.1\r\n"
			+ "User-Agent: Jakarta Commons-HttpClient/3.0\r\n"
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
		Calendar actual = executeMethod(SC_OK, method, calendarReportCallback());
		
		assertEquals("Calendar", createCalendar(), actual);
	}
	
	// private methods --------------------------------------------------------
	
	private static CalendarCalDAVReportMethod createMethod(String path, CalDAVReportRequest reportRequest)
	{
		CalendarCalDAVReportMethod method = new CalendarCalDAVReportMethod();
		
		method.setCalendarBuilder(new CalendarBuilder());
		method.setPath(path);
		method.setReportRequest(reportRequest);
		
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
