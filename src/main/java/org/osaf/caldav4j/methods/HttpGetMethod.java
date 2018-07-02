package org.osaf.caldav4j.methods;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.exceptions.CalDAV4JProtocolException;
import org.osaf.caldav4j.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

public class HttpGetMethod  extends HttpGet {

	
	private static final String HEADER_ACCEPT = "Accept";
	
	private static final String ERR_CONTENT_TYPE = "Expected content-type text/calendar. Was: ";

	private static final Logger log = LoggerFactory.getLogger(GetMethod.class);
    
    private CalendarBuilder calendarBuilder = null;

    protected HttpGetMethod (){
        super();
        this.addHeader(HEADER_ACCEPT, 
        		"text/calendar; text/html; text/xml;"); // required for bedework
    }

    public CalendarBuilder getCalendarBuilder() {
        return calendarBuilder;
    }

    public void setCalendarBuilder(CalendarBuilder calendarBuilder) {
        this.calendarBuilder = calendarBuilder;
    }

    //TODO !A! switch to CloseAbleHttpResponse? Use EntityUtils.consumeQuietly()?
    public Calendar getResponseBodyAsCalendar(HttpResponse response)  throws
            ParserException, CalDAV4JException {
    	Calendar ret = null;
    	BufferedInputStream stream = null;
        try {
		    Header header = getFirstHeader(CalDAVConstants.HEADER_CONTENT_TYPE);
		    String contentType = (header != null) ? header.getValue() : null;
		    
	    	if ((UrlUtils.isBlank(contentType) || contentType.startsWith(CalDAVConstants.CONTENT_TYPE_CALENDAR))) {
		    	if (response.getEntity() != null && response.getEntity().getContent() != null) {	    		
		    		stream = new BufferedInputStream(response.getEntity().getContent());
		    		ret =  calendarBuilder.build(stream);
		    		return ret;		        
		    	} else throw new CalDAV4JException("Error: No content stream at "+ getURI());
	    	} else {
		        log.error(ERR_CONTENT_TYPE + contentType);
		        throw new CalDAV4JProtocolException(ERR_CONTENT_TYPE + contentType );
	    		
	    	}
        } catch (IOException e) {
        	if (stream != null && log.isWarnEnabled()) { //the server sends the response
        			log.warn("Server response is " + UrlUtils.parseISToString(stream)); //TODO !A! Not sure whether stream can be read again
        	}
        	throw new CalDAV4JException("Error retrieving and parsing server response at " + getURI(), e);
        }	       
    }

    public void setPath(String path) {
    	super.setURI(URI.create(UrlUtils.removeDoubleSlashes(path)));    	
    }
	
	
}
