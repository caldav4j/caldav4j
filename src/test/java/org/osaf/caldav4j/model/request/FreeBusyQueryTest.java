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
package org.osaf.caldav4j.model.request;

import static org.junit.Assert.assertEquals;
import static org.osaf.caldav4j.CalDAVConstants.NS_QUAL_CALDAV;

import java.text.ParseException;

import net.fortuna.ical4j.model.DateTime;

import org.junit.Test;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.util.XMLUtils;
import org.osaf.caldav4j.xml.OutputsDOM;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Tests {@code FreeBusyQuery}.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id: FreeBusyQueryTest.java 294 2011-02-22 11:50:25Z markhobson $
 * @see FreeBusyQuery
 */
public class FreeBusyQueryTest
{
	// tests ------------------------------------------------------------------
	
	@Test
	public void createNewDocument() throws ParseException, DOMValidationException
	{
		FreeBusyQuery query = createFreeBusyQuery("20000101T000000Z", "20000201T000000Z");

		String expected = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n"
			+ "<C:free-busy-query xmlns:C=\"urn:ietf:params:xml:ns:caldav\">"
				+ "<C:time-range end=\"20000201T000000Z\" start=\"20000101T000000Z\"/>"
			+ "</C:free-busy-query>";
		
		assertCreateNewDocument(expected, query);
	}
	
	@Test(expected = DOMValidationException.class)
	public void validateWithNoTimeRange() throws DOMValidationException
	{
		FreeBusyQuery query = createFreeBusyQuery();
		
		query.validate();
	}
	
	@Test(expected = DOMValidationException.class)
	public void validateWithInvalidTimeRange() throws DOMValidationException
	{
		FreeBusyQuery query = createFreeBusyQuery();
		query.setTimeRange(new TimeRange(NS_QUAL_CALDAV, null, null));
		
		query.validate();
	}
	
	public void validateWithTimeRange() throws ParseException, DOMValidationException
	{
		FreeBusyQuery query = createFreeBusyQuery("20000101T000000Z", "20000201T000000Z");
		
		query.validate();
	}
	
	// private methods --------------------------------------------------------
	
	private static FreeBusyQuery createFreeBusyQuery()
	{
		return new FreeBusyQuery(NS_QUAL_CALDAV);
	}
	
	private static FreeBusyQuery createFreeBusyQuery(String timeRangeStart, String timeRangeEnd) throws ParseException
	{
		FreeBusyQuery query = createFreeBusyQuery();
		
		TimeRange timeRange = new TimeRange(NS_QUAL_CALDAV, new DateTime(timeRangeStart), new DateTime(timeRangeEnd));
		query.setTimeRange(timeRange);
		
		return query;
	}
	
	private static void assertCreateNewDocument(String expected, OutputsDOM output) throws DOMValidationException
	{
		DOMImplementation domImplementation = XMLUtils.getDOMImplementation();
		Document document = output.createNewDocument(domImplementation);
		
		String actual = XMLUtils.toXML(document);
		
		assertEquals("createNewDocument", expected, actual);
	}
}
