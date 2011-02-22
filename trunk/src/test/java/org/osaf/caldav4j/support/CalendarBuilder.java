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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Version;

import org.osaf.caldav4j.dialect.CalDavDialect;

/**
 * Builder for constructing calendar models using a dialect.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class CalendarBuilder
{
	// fields -----------------------------------------------------------------
	
	private final CalDavDialect dialect;
	
	// constructors -----------------------------------------------------------
	
	public CalendarBuilder(CalDavDialect dialect)
	{
		this.dialect = dialect;
	}
	
	// public methods ---------------------------------------------------------
	
	public Calendar createCalendar()
	{
		Calendar calendar = new Calendar();

		// ProdId
		addIfNotNull(calendar.getProperties(), dialect.getProdId());

		// Version
		calendar.getProperties().add(Version.VERSION_2_0);
		
		// CalScale
		addIfNotNull(calendar.getProperties(), dialect.getDefaultCalScale());
		
		return calendar;
	}
	
	// private methods --------------------------------------------------------
	
	private static void addIfNotNull(PropertyList properties, Property property)
	{
		if (property != null)
		{
			properties.add(property);
		}
	}
}
