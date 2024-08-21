package com.github.caldav4j.util;

import java.time.temporal.Temporal;
import java.util.Comparator;
import java.util.Objects;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;

/** This class provides the ability to compare two calendar by the first event date. */
// maybe the better place for this class is the package net.fortuna.ical4j.model.Calendar;
public class CalendarComparator implements Comparator<Calendar> {

    public int compare(Calendar o1, Calendar o2) {
        VEvent e1 = ICalendarUtils.getFirstEvent(o1);
        VEvent e2 = ICalendarUtils.getFirstEvent(o2);

        DtStart<Temporal> e1Start = e1.getDateTimeStart().orElse(null);
        DtStart<Temporal> e2Start = e2.getDateTimeStart().orElse(null);

        return Objects.requireNonNull(e1Start).compareTo(e2Start);
    }
}
