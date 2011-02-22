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
package org.osaf.caldav4j.support;

import static org.junit.Assert.assertEquals;
import net.fortuna.ical4j.model.Calendar;

/**
 * Provides calendar-specific assertions for use by tests.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public final class CalendarAssert
{
	// constructors -----------------------------------------------------------
	
	private CalendarAssert()
	{
		throw new AssertionError();
	}
	
	// public methods ---------------------------------------------------------
	
	public static void assertEqualsIgnoring(Calendar expected, Calendar actual, String... ignoredPropertyNames)
	{
		Calendar reducedExpected = removeProperties(expected, ignoredPropertyNames);
		Calendar reducedActual = removeProperties(actual, ignoredPropertyNames);
		
		assertEquals(reducedExpected, reducedActual);
	}
	
	// private methods --------------------------------------------------------
	
	private static Calendar removeProperties(Calendar calendar, String... ignoredPropertyNames)
	{
		return new RemovePropertyTransformer(ignoredPropertyNames).transform(calendar);
	}
}
