/*
 * Copyright 2006 Open Source Applications Foundation
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
package com.github.caldav4j;

import com.github.caldav4j.model.response.CalendarDataProperty;
import net.fortuna.ical4j.model.Calendar;
import org.apache.jackrabbit.webdav.MultiStatusResponse;

import java.io.Serializable;

/**
 * A serializable class representing the Calendar along with the associated metadata. Used for
 * storing into the cache.
 */
public class CalDAVResource implements Serializable{
	private static final long serialVersionUID = -2607152240683030192L;
	private ResourceMetadata resourceMetadata = null;
	private Calendar calendar = null;

	/**
	 * Construct a Resource based on the Response.
	 *
	 * @param response Response to contruct from.
	 */
	public CalDAVResource(MultiStatusResponse response) {
		this.calendar = CalendarDataProperty.getCalendarfromResponse(response);
		this.resourceMetadata = new ResourceMetadata();
		this.resourceMetadata.setETag(CalendarDataProperty.getEtagfromResponse(response));
		this.resourceMetadata.setHref(response.getHref());

	}

	/**
	 * Construct Resource based on the parameters.
	 * @param calendar Calendar
	 * @param etag ETag of the Calendar Resource
	 * @param href Href of the Calendar Resource
	 */
	public CalDAVResource(Calendar calendar, String etag, String href){
		this.calendar = calendar;
		ResourceMetadata rm = new ResourceMetadata();
		rm.setETag(etag);
		rm.setHref(href);
		this.resourceMetadata = rm;
	}

	/**
	 * Default constructor
	 */
	public CalDAVResource(){
		resourceMetadata = new ResourceMetadata();
	}

	public void setCalendar(Calendar calendar){
		this.calendar = calendar;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public ResourceMetadata getResourceMetadata() {
		return resourceMetadata;
	}
}