package com.github.caldav4j.scheduling.methods;

import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.model.request.CalendarRequest;
import java.net.URI;

/** Factory class for Scheduling related methods, for now only Schedule POST exists. */
public class CalDAV4JScheduleMethodFactory extends CalDAV4JMethodFactory {

    public SchedulePostMethod createSchedulePostMethod(URI uri, CalendarRequest calendarRequest) {
        return new SchedulePostMethod(uri, calendarRequest, getCalendarOutputterInstance());
    }

    public SchedulePostMethod createSchedulePostMethod(
            String uri, CalendarRequest calendarRequest) {
        return new SchedulePostMethod(uri, calendarRequest, getCalendarOutputterInstance());
    }
}
