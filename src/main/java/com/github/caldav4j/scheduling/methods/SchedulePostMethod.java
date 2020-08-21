package com.github.caldav4j.scheduling.methods;

import com.github.caldav4j.methods.HttpPostMethod;
import com.github.caldav4j.methods.ResourceParser;
import com.github.caldav4j.model.request.CalendarRequest;
import com.github.caldav4j.model.request.ResourceRequest;
import java.net.URI;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;

/**
 * Implements the Schedule Post method as defined in <a
 * href="https://tools.ietf.org/html/rfc6638">RFC 6638</a>
 *
 * <p>It does so by extendind {@link HttpPostMethod}
 */
public class SchedulePostMethod extends HttpPostMethod<Calendar> {

    public SchedulePostMethod(
            URI uri, CalendarRequest calendarRequest, ResourceParser<Calendar> calendarOutputter) {
        super(uri, calendarRequest, calendarOutputter);
    }

    public SchedulePostMethod(
            String uri,
            CalendarRequest calendarRequest,
            ResourceParser<Calendar> calendarOutputter) {
        super(uri, calendarRequest, calendarOutputter);
    }

    // we have to set the Attendees and Organize headers taken from Calendar

    /**
     * We have to set the Attendees and Organize headers taken from Calendar.
     *
     * @see HttpPostMethod#addRequestHeaders(ResourceRequest)
     * @param calendarRequest calendar request information
     */
    protected void addRequestHeaders(CalendarRequest calendarRequest) {

        boolean addOrganizerToAttendees = false;
        boolean hasAttendees = false;

        // get ATTENDEES and ORGANIZER from ical and add
        // Originator and Recipient to Header
        Calendar calendar = calendarRequest.getCalendar();
        if (calendar != null) {
            ComponentList<CalendarComponent> cList = calendar.getComponents();
            if (Method.REPLY.equals(calendar.getProperty(Property.METHOD))) {
                addOrganizerToAttendees = true;
            }
            for (CalendarComponent event : cList) {
                if (!(event instanceof VTimeZone)) {
                    Organizer organizer = event.getProperty(Property.ORGANIZER);

                    if ((organizer != null)
                            && (organizer.getValue() != null)
                            && (organizer.getValue().startsWith("mailto:"))) {

                        addHeader("Originator", organizer.getValue());
                        if (addOrganizerToAttendees) {
                            addHeader("Recipient", organizer.getValue());
                        }

                        for (Object oAttendee : event.getProperties(Property.ATTENDEE)) {
                            Attendee a = (Attendee) oAttendee;
                            if (a.getValue().startsWith("mailto:")) {
                                addHeader("Recipient", a.getValue());
                            }
                        }
                    }
                }
            }
        }

        super.addRequestHeaders(calendarRequest);
    }
}
