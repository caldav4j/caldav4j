/*
 * Copyright © 2018 Ankush Mishra
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

package com.github.caldav4j.model.request;

import com.github.caldav4j.methods.HttpPostMethod;
import com.github.caldav4j.methods.HttpPutMethod;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import com.github.caldav4j.CalDAVConstants;

/**
 * This class is a wrapper for the all the properties of the POST and PUT Method
 * requests.
 * @see HttpPostMethod
 * @see HttpPutMethod
 * @author Ankush Mishra
 */
public class CalendarRequest extends ResourceRequest<Calendar> {

	public CalendarRequest() {
		
	}
	
	public CalendarRequest(Calendar calendar) {
		super(calendar);
	}

	public CalendarRequest(Calendar calendar, boolean ifMatch, boolean ifNoneMatch, boolean allEtags) {
		super(calendar, ifMatch, ifNoneMatch, allEtags);
	}

	public Calendar getCalendar() {
		return getResource();
	}

	public void setCalendar(Calendar calendar) {
		setResource(calendar);
	}

	public void setCalendar(VEvent vevent, VTimeZone vtimeZone, String prodId) {
		if(prodId == null)
			prodId = CalDAVConstants.PROC_ID_DEFAULT;

		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId(prodId));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		calendar.getComponents().add(vevent);
		if (vtimeZone != null){
			calendar.getComponents().add(vtimeZone);
		}
		setResource(calendar);
	}

	public void setCalendar(VEvent event, String prodId) {
		setCalendar(event, null, prodId);
	}

	public void setCalendar(VEvent event) {
		setCalendar(event, null, null);
	}

}
