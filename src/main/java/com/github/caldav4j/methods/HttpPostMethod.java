package com.github.caldav4j.methods;

import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.UnsupportedCharsetException;

import com.github.caldav4j.util.CalDAVStatus;
import net.fortuna.ical4j.model.Calendar;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.model.request.CalendarRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fortuna.ical4j.data.CalendarOutputter;

/**
 * Extended HttpPost class to allow easy addition of the Calendar.
 *
 * @see HttpPost
 */
public class HttpPostMethod extends HttpPost {

    private static final Logger log = LoggerFactory.getLogger(HttpPostMethod.class);

	/**
	 * @param uri URI to the given resource
	 * @param calendarRequest Object marshalling all the request options
	 * @param calendarOutputter Outputter object to generate the calendar.
	 *                          Only required if {@link CalendarRequest}
	 *                          contains a calendar
	 */
	public HttpPostMethod(URI uri, CalendarRequest calendarRequest, CalendarOutputter calendarOutputter) {
		super(uri);
		addRequestHeaders(calendarRequest);
		generateRequestBody(calendarRequest, calendarOutputter);
	}

	/**
	 * @param uri URI to the given resource
	 * @param calendarRequest Object marshalling all the request options
	 * @param calendarOutputter Outputter object to generate the calendar.
	 *                          Only required if {@link CalendarRequest}
	 *                          contains a calendar
	 */
	public HttpPostMethod(String uri, CalendarRequest calendarRequest, CalendarOutputter calendarOutputter) {
		this(URI.create(uri), calendarRequest, calendarOutputter);
	}

	/**
	 * Generates the calendar request body, and sets the entity.
	 * @param calendarRequest Object representing the marshalled properties
	 *                        of the request
	 * @param calendarOutputter Outputter object to generate the calendar string output
	 */
	protected void generateRequestBody(CalendarRequest calendarRequest, CalendarOutputter calendarOutputter)  {
	    Calendar calendar = calendarRequest.getCalendar();
        if ( calendar != null){
            StringWriter writer = new StringWriter();
            try{
                calendarOutputter.output(calendar, writer);
                
                ContentType ct = ContentType.create(CalDAVConstants.CONTENT_TYPE_CALENDAR, calendarRequest.getCharset());

                setEntity(new StringEntity(writer.toString(), ct));
            } catch (UnsupportedCharsetException e) {
            	 log.error("Unsupported encoding in event" + writer.toString());
            	 throw new RuntimeException("Problem generating calendar. ", e);
            } catch (Exception e){
                log.error("Problem generating calendar: ", e);
                throw new RuntimeException("Problem generating calendar. ", e);
            }
        }
    }

	/**
	 * Adds the respective Request headers based on the provided flags.
	 * @param calendarRequest Object representing the marshalled properties of the request
	 */
    protected void addRequestHeaders(CalendarRequest calendarRequest) {
		boolean ifMatch = calendarRequest.isIfMatch(), ifNoneMatch = calendarRequest.isIfNoneMatch();
        if (ifMatch || ifNoneMatch){
            String name = ifMatch ? CalDAVConstants.HEADER_IF_MATCH : CalDAVConstants.HEADER_IF_NONE_MATCH;
            String value = null;
            if (calendarRequest.isAllEtags()){
                value = "*";
            } else {
                StringBuilder buf = new StringBuilder();
                for (String etag : calendarRequest.getEtags()){
                    buf.append(etag);
                }
                value = buf.toString();
            }
            addHeader(name, value);
        }
    }

	/**
	 * Check the provided {@link HttpResponse} for successful execution.
	 * This treats all 2xx status codes.
	 * @param response Response to check
	 */
	public boolean succeeded(HttpResponse response) {
		int status = response.getStatusLine().getStatusCode();
		return  status == CalDAVStatus.SC_CREATED ||
				status == CalDAVStatus.SC_NO_CONTENT;
	}
}
