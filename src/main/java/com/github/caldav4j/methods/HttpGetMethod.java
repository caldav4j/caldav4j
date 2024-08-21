package com.github.caldav4j.methods;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.exceptions.CalDAV4JProtocolException;
import com.github.caldav4j.util.UrlUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Extended GET method for Calendar Parsing. */
public class HttpGetMethod extends HttpGet {

    private static final String HEADER_ACCEPT = "Accept";

    private static final String ERR_CONTENT_TYPE = "Expected content-type text/calendar. Was: ";

    private static final Logger log = LoggerFactory.getLogger(HttpGetMethod.class);

    private CalendarBuilder calendarBuilder = null;

    /**
     * @param uri Location of the CalendarResource
     * @param calendarBuilder Builder Instance for constructing the response object.
     */
    public HttpGetMethod(URI uri, CalendarBuilder calendarBuilder) {
        super(uri);
        this.calendarBuilder = calendarBuilder;
        this.addHeader(
                HEADER_ACCEPT, "text/calendar; text/html; text/xml;"); // required for bedework
    }

    /**
     * @param uri Location of the CalendarResource
     * @param calendarBuilder Builder Instance for constructing the response object.
     */
    public HttpGetMethod(String uri, CalendarBuilder calendarBuilder) {
        this(URI.create(uri), calendarBuilder);
    }

    /**
     * @param response Response object from the request
     * @return Returns the Calendar Object if parsed.
     * @throws ParserException on parsing calendar error.
     * @throws CalDAV4JException on error in retrieving and parsing the response.
     */
    public Calendar getResponseBodyAsCalendar(HttpResponse response)
            throws ParserException, CalDAV4JException {
        Calendar ret = null;
        BufferedInputStream stream = null;
        try {
            Header header = getFirstHeader(CalDAVConstants.HEADER_CONTENT_TYPE);
            String contentType = (header != null) ? header.getValue() : null;

            if ((UrlUtils.isBlank(contentType)
                    || contentType.startsWith(CalDAVConstants.CONTENT_TYPE_CALENDAR))) {
                if (response.getEntity() != null && response.getEntity().getContent() != null) {
                    stream = new BufferedInputStream(response.getEntity().getContent());
                    ret = calendarBuilder.build(stream);
                    return ret;
                }

                throw new CalDAV4JException("Error: No content stream at " + getURI());
            } else {
                log.error(ERR_CONTENT_TYPE + contentType);
                throw new CalDAV4JProtocolException(ERR_CONTENT_TYPE + contentType);
            }
        } catch (IOException e) {
            if (stream != null && log.isWarnEnabled()) { // the server sends the response
                log.warn("Server response is " + UrlUtils.parseISToString(stream));
            }
            throw new CalDAV4JException(
                    "Error retrieving and parsing server response at " + getURI(), e);
        }
    }

    /**
     * @return Returns the associated CalendarBuilder Instance
     */
    public CalendarBuilder getCalendarBuilder() {
        return calendarBuilder;
    }

    /**
     * Set the CalendarBuilder Instance
     *
     * @param calendarBuilder Instance to set
     */
    public void setCalendarBuilder(CalendarBuilder calendarBuilder) {
        this.calendarBuilder = calendarBuilder;
    }
}
