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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.transform.Transformer;

/**
 * Calendar transformer that removes specified properties.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id: RemovePropertyTransformer.java 294 2011-02-22 11:50:25Z markhobson $
 */
class RemovePropertyTransformer extends Transformer
{
	// fields -----------------------------------------------------------------
	
	private final List<String> propertyNames;

	// constructors -----------------------------------------------------------
	
	public RemovePropertyTransformer(String... propertyNames)
	{
		this.propertyNames = Arrays.asList(propertyNames);
	}

	// Transformer methods ----------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Calendar transform(Calendar calendar)
	{
		Calendar newCalendar = deepCopy(calendar);
		
		apply(newCalendar.getProperties());
		apply(newCalendar.getComponents());
		
		return newCalendar;
	}
	
	// private methods --------------------------------------------------------
	
	private static Calendar deepCopy(Calendar calendar)
	{
		try
		{
			return new Calendar(calendar);
		}
		catch (ParseException exception)
		{
			throw new IllegalStateException("Cannot copy calendar", exception);
		}
		catch (IOException exception)
		{
			throw new IllegalStateException("Cannot copy calendar", exception);
		}
		catch (URISyntaxException exception)
		{
			throw new IllegalStateException("Cannot copy calendar", exception);
		}
	}
	
	private void apply(PropertyList properties)
	{
		for (String propertyName : propertyNames)
		{
			PropertyList namedProperties = properties.getProperties(propertyName);
		
			properties.removeAll(namedProperties);
		}
	}
	
	private void apply(ComponentList components)
	{
		for (Object component : components)
		{
			apply((Component) component);
		}
	}
	
	private void apply(Component component)
	{
		apply(component.getProperties());
	}
}
