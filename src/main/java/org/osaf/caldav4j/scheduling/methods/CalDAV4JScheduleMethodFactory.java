package org.osaf.caldav4j.scheduling.methods;

import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.model.request.CalendarRequest;

import java.net.URI;

/**
 * Factory class for Scheduling related methods, for now only Schedule POST exists.
 */
public class CalDAV4JScheduleMethodFactory extends CalDAV4JMethodFactory {

	public SchedulePostMethod createSchedulePostMethod(URI uri, CalendarRequest calendarRequest) {
		return new SchedulePostMethod(uri, calendarRequest, getCalendarOutputterInstance());
	}

	public SchedulePostMethod createSchedulePostMethod(String uri, CalendarRequest calendarRequest) {
		return new SchedulePostMethod(uri, calendarRequest, getCalendarOutputterInstance());
	}
}
