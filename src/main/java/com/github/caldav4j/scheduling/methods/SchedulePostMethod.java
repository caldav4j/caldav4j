package com.github.caldav4j.scheduling.methods;

import com.github.caldav4j.methods.HttpPostMethod;
import com.github.caldav4j.model.request.CalendarRequest;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.immutable.ImmutableMethod;

/**
 * Implements the Schedule Post method as defined in <a
 * href="https://tools.ietf.org/html/rfc6638">RFC 6638</a>
 *
 * <p>It does so by extendind {@link HttpPostMethod}
 */
public class SchedulePostMethod extends HttpPostMethod {

    public SchedulePostMethod(
            URI uri, CalendarRequest calendarRequest, CalendarOutputter calendarOutputter) {
        super(uri, calendarRequest, calendarOutputter);
    }

    public SchedulePostMethod(
            String uri, CalendarRequest calendarRequest, CalendarOutputter calendarOutputter) {
        super(uri, calendarRequest, calendarOutputter);
    }

    // we have to set the Attendees and Organize headers taken from Calendar

    /**
     * We have to set the Attendees and Organize headers taken from Calendar.
     *
     * @see HttpPostMethod#addRequestHeaders(CalendarRequest)
     */
    protected void addRequestHeaders(CalendarRequest calendarRequest) {

        boolean addOrganizerToAttendees = false;
        boolean hasAttendees = false;

        // get ATTENDEES and ORGANIZER from ical and add
        // Originator and Recipient to Header
        Calendar calendar = calendarRequest.getCalendar();
        if (calendar != null) {
            List<CalendarComponent> cList = calendar.getComponents();
            if (ImmutableMethod.REPLY.equals(calendar.getProperty(Property.METHOD).orElse(null))) {
                addOrganizerToAttendees = true;
            }
            for (CalendarComponent event : cList) {
                if (!(event instanceof VTimeZone)) {
                    Optional<Property> organizer = event.getProperty(Property.ORGANIZER);

                    if ((organizer.isPresent())
                            && (organizer.get().getValue() != null)
                            && (organizer.get().getValue().startsWith("mailto:"))) {

                        addHeader("Originator", organizer.get().getValue());
                        if (addOrganizerToAttendees) {
                            addHeader("Recipient", organizer.get().getValue());
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
