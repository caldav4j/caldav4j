package org.osaf.caldav4j.model.request;

import org.osaf.caldav4j.CalDAVConstants;


public class CalendarDescription extends PropProperty {

	public CalendarDescription(String value) {
		super(CalDAVConstants.NS_CALDAV, "C", "calendar-description");
		this.setTextContent(value);
	}
	public CalendarDescription(String value, String lang) {
		super(CalDAVConstants.NS_CALDAV, "C", "calendar-description");
		this.setTextContent(value);
		this.addAttribute("xml:lang", lang);
	}
}
