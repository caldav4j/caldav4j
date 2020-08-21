package com.github.caldav4j.methods;

import com.github.caldav4j.CalDAVConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.CompatibilityHints;

public class CalendarResourceParser implements ResourceParser<Calendar> {

    private ThreadLocal<CalendarBuilder> calendarBuilderThreadLocal = new ThreadLocal<>();

    private CalendarOutputter calendarOutputter = null;

    private boolean tolerantParsing = false;

    public CalendarResourceParser() {
        this(false);
    }

    public CalendarResourceParser(boolean validatingOutputter) {
        calendarOutputter = new CalendarOutputter(validatingOutputter);

        if (isTolerantParsing()) {
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, false);
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false);
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true);
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, false);
        }
    }

    @Override
    public Calendar read(InputStream in) throws IOException {
        try {
            return getCalendarBuilderInstance().build(in);
        } catch (ParserException e) {
            throw new IOException("Cannot read the Calendar", e);
        }
    }

    @Override
    public void write(Calendar resource, Writer out) throws IOException {
        calendarOutputter.output(resource, out);
    }

    @Override
    public String getResponseContentType() {
        return CalDAVConstants.CONTENT_TYPE_CALENDAR;
    }

    /**
     * Return the CalendarBuilder instance.
     *
     * @return CalendarBuilder
     */
    private CalendarBuilder getCalendarBuilderInstance() {
        CalendarBuilder builder = calendarBuilderThreadLocal.get();
        if (builder == null) {
            builder = new CalendarBuilder();
            calendarBuilderThreadLocal.set(builder);
        }
        return builder;
    }

    /** @return Whether Tolerant Parsing for Calendars is enabled or not. */
    public boolean isTolerantParsing() {
        return tolerantParsing;
    }

    /** @param tolerantParsing Value used to enable or disable tolerant parsing of Calendars */
    public void setTolerantParsing(boolean tolerantParsing) {
        this.tolerantParsing = tolerantParsing;
    }
}
