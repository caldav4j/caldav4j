package com.github.caldav4j.scheduling.methods;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import com.github.caldav4j.methods.HttpPostMethod;
import com.github.caldav4j.model.request.CalendarRequest;

import java.net.URI;

/** Make sure to call addRequestHeaders() before the method is executed. */
public class HttpSchedulePostMethod extends HttpPostMethod {

	public HttpSchedulePostMethod(URI uri, CalendarRequest calendarRequest, CalendarOutputter calendarOutputter) {
		super(uri, calendarRequest, calendarOutputter);
	}

	public HttpSchedulePostMethod(String uri, CalendarRequest calendarRequest, CalendarOutputter calendarOutputter) {
		super(uri, calendarRequest, calendarOutputter);
	}

	// we have to set the Attendees and Organize headers taken from Calendar
	protected void addRequestHeaders(CalendarRequest calendarRequest) {

		boolean addOrganizerToAttendees = false;

		Calendar calendar = calendarRequest.getCalendar();
		// get ATTENDEES and ORGANIZER from ical and add 
		// Originator and Recipient to Header
		if ( calendar != null) {
			ComponentList cList = calendar.getComponents(); 
			if (Method.REPLY.equals(calendar.getProperty(Property.METHOD))) {
				addOrganizerToAttendees = true;
			}
			for (Object obj : cList) {
				if (! (obj  instanceof VTimeZone)) {
					CalendarComponent event = (CalendarComponent) obj;
					Organizer organizer = (Organizer) event.getProperty(Property.ORGANIZER); 

					if ((organizer != null) && (organizer.getValue() != null) &&
							(organizer.getValue().startsWith("mailto:"))
					) {
						
						super.addHeader("Originator", organizer.getValue());
						if (addOrganizerToAttendees) {
							super.addHeader("Recipient", organizer.getValue());    							
						}

						for (Object oAttendee: event.getProperties(Property.ATTENDEE)) {
							Attendee a = (Attendee) oAttendee;
							if (a.getValue().startsWith("mailto:")) {
								super.addHeader("Recipient", a.getValue());    							
							}
						}   
					}
				} 
			}    					
		}

		super.addRequestHeaders(calendarRequest);
	}
	
}
