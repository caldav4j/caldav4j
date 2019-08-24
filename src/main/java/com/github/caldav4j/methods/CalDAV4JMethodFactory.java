/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Bobby Rullo, Mark Hobson, Roberto Polli
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

import net.fortuna.ical4j.model.Calendar;

import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.security.AclProperty;
import com.github.caldav4j.model.request.CalDAVReportRequest;
import com.github.caldav4j.model.request.CalendarRequest;
import com.github.caldav4j.model.request.MkCalendar;

import java.io.IOException;
import java.net.URI;

/**
 * Method factory for creating instances of CalDAV related Methods.
 * This automatically handles, any basic configuration required.
 */
public class CalDAV4JMethodFactory extends DAVMethodFactory<Calendar> {

	private static final String HEADER_ACCEPT = "Accept";
	
	private boolean validatingOutputter = false;

	/**
	 * @return True or False based on the Calendar Validating Outputter setting.
	 */
	public boolean isCalendarValidatingOutputter() {
		return validatingOutputter;
	}

	/**
	 * Set whether the CalendarBuilder is Validating or not.
	 * @param validatingOutputter Value to set.
	 */
	public void setCalendarValidatingOutputter(boolean validatingOutputter) {
		this.validatingOutputter = validatingOutputter;
	}

	@Override
	protected ResourceParser<Calendar> buildResourceParser() {
		return new CalendarResourceParser(validatingOutputter);
	}
	
	@Override
	public HttpGetMethod<Calendar> createGetMethod(URI uri) {
		HttpGetMethod<Calendar> request = super.createGetMethod(uri);
        request.addHeader(HEADER_ACCEPT, "text/calendar; text/html; text/xml;"); // required for bedework
		return request;
	}
}
