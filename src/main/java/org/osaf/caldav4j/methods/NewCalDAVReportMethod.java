package org.osaf.caldav4j.methods;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.client.methods.ReportMethod;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.CaldavStatus;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by emanon on 5/16/16.
 */
public class NewCalDAVReportMethod extends ReportMethod {

    private static final Log log = LogFactory.getLog(NewCalDAVReportMethod.class);

    private boolean isCalendarResponse = false;
    private Calendar calendarResponse = null;

    public NewCalDAVReportMethod(String uri, ReportInfo reportInfo) throws IOException {
        super(uri, reportInfo);
    }

    @Override
    protected void processResponseHeaders(HttpState state, HttpConnection conn) {
        super.processResponseHeaders(state, conn);
        HeaderElement[] elements = getResponseHeader("content-type").getElements();
        for (HeaderElement element : elements) {
            if (element.getName().equals(CalDAVConstants.CONTENT_TYPE_CALENDAR)) {
                isCalendarResponse = true;
                log.info("Response Content-Type: text/calendar");
            } else if (element.getName().equals(CalDAVConstants.CONTENT_TYPE_TEXT_XML)) {
                log.info("Response Content-Type: text/xml");
            }
        }
    }

    public Calendar getResponseBodyasCalendar(){
        return this.calendarResponse;
    }

    @Override
    protected void processResponseBody(HttpState httpState, HttpConnection httpConnection) {
        if (getStatusCode() == CaldavStatus.SC_OK && isCalendarResponse){
            try {
                InputStream stream = getResponseBodyAsStream();
                CalendarBuilder calendarBuilder = new CalendarBuilder();
                calendarResponse = calendarBuilder.build(stream);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Error while parsing Calendar response: " + e);
            } catch (ParserException e) {
                e.printStackTrace();
                log.error("Error while parsing Calendar response: " + e);
            }
        }
        else
            super.processResponseBody(httpState, httpConnection);
    }
}