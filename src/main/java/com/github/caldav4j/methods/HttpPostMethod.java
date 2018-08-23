package com.github.caldav4j.methods;

import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.UnsupportedCharsetException;

import net.fortuna.ical4j.model.Calendar;
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
	 *
	 */
	public HttpPostMethod(URI uri, CalendarRequest calendarRequest, CalendarOutputter calendarOutputter) {
		super(uri);
		addRequestHeaders(calendarRequest);
		generateRequestBody(calendarRequest, calendarOutputter);
	}

	public HttpPostMethod(String uri, CalendarRequest calendarRequest, CalendarOutputter calendarOutputter) {
		this(URI.create(uri), calendarRequest, calendarOutputter);
	}

	public String getMethod() {
    	return CalDAVConstants.METHOD_POST;
    }

	/**
	 * Generates the calendar request body, and sets the entity.
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
}
