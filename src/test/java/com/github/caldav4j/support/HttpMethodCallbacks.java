/*
 * Copyright 2011 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Mark Hobson
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

package com.github.caldav4j.support;

import net.fortuna.ical4j.model.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.support.HttpClientTestUtils.HttpMethodCallback;

/**
 * Factory for producing {@code HttpMethodCallback} implementations.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public final class HttpMethodCallbacks
{
	// constants --------------------------------------------------------------
	
	private static final HttpMethodCallback<HttpResponse, HttpRequestBase, RuntimeException> NULL_CALLBACK =
			(method, response) -> response;

	private static final HttpMethodCallback<Calendar, HttpCalDAVReportMethod, Exception> CALENDAR_REPORT_CALLBACK =
			HttpCalDAVReportMethod<Calendar>::getResponseBodyAsCalendar;
		
	// constructors -----------------------------------------------------------
	
	private HttpMethodCallbacks()
	{
		throw new AssertionError();
	}
	
	// public methods ---------------------------------------------------------
	
	public static HttpMethodCallback<HttpResponse, HttpRequestBase, RuntimeException> nullCallback()
	{
		return NULL_CALLBACK;
	}
	
	public static HttpMethodCallback<Calendar, HttpCalDAVReportMethod, Exception> calendarReportCallback()
	{
		return CALENDAR_REPORT_CALLBACK;
	}
}
