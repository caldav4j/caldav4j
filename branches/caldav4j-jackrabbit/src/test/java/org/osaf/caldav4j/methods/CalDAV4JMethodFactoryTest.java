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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Tests {@code CalDAV4JMethodFactory}.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id: CalDAV4JMethodFactoryTest.java 294 2011-02-22 11:50:25Z markhobson $
 * @see CalDAV4JMethodFactory
 */
public class CalDAV4JMethodFactoryTest
{
	// tests ------------------------------------------------------------------
	
	// TODO: test other factory methods
	
	@Test
	public void createCalendarCalDAVReportMethod()
	{
		CalDAV4JMethodFactory factory = new CalDAV4JMethodFactory();
		CalendarCalDAVReportMethod method = factory.createCalendarCalDAVReportMethod();
		
		assertNotNull("Method", method);
		assertNotNull("Calendar builder", method.getCalendarBuilder());
	}
}
