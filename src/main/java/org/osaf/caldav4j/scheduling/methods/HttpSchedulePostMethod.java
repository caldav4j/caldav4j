package org.osaf.caldav4j.scheduling.methods;

import java.io.IOException;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.http.client.methods.HttpPost;
import org.osaf.caldav4j.methods.HttpPostMethod;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;

/** Make sure to call addRequestHeaders() before the method is executed. */
public class HttpSchedulePostMethod extends HttpPostMethod {

	@Override
	// we have to set the Attendees and Organize headers taken from Calendar
	public void addRequestHeaders() {

		boolean addOrganizerToAttendees = false;
		boolean hasAttendees = false;
		
		// get ATTENDEES and ORGANIZER from ical and add 
		// Originator and Recipient to Header
		if ( this.calendar != null) {
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

		super.addRequestHeaders();
	}
	
}
