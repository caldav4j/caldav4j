package com.github.caldav4j.methods;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.exceptions.CalDAV4JProtocolException;
import com.github.caldav4j.util.UrlUtils;
import net.fortuna.ical4j.data.ParserException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

/**
 * Extended GET method for Calendar Parsing.
 */
public class HttpGetMethod<T extends Serializable> extends HttpGet {
	
	private static final String ERR_CONTENT_TYPE = "Expected content-type text/calendar. Was: ";

	private static final Logger log = LoggerFactory.getLogger(HttpGetMethod.class);

	private ResourceParser<T> parser = null;

	/**
	 * @param uri Location of the CalendarResource
	 * @param calendarBuilder Builder Instance for constructing the response object.
	 */
    HttpGetMethod (URI uri, ResourceParser<T> parser){
        super(uri);
        this.parser = parser;
    }

	/**
	 * @param uri Location of the CalendarResource
	 * @param calendarBuilder Builder Instance for constructing the response object.
	 */
	HttpGetMethod (String uri, ResourceParser<T> parser){
		this(URI.create(uri), parser);
	}

	/**
	 * @param response Response object from the request
	 * @return Returns the Calendar Object if parsed.
	 * @throws ParserException on parsing calendar error.
	 * @throws CalDAV4JException on error in retrieving and parsing the response.
	 */
    public T getResponseBodyAsCalendar(HttpResponse response)  throws
            ParserException, CalDAV4JException {
    	T ret = null;
    	BufferedInputStream stream = null;
        try {
		    Header header = getFirstHeader(CalDAVConstants.HEADER_CONTENT_TYPE);
		    String contentType = (header != null) ? header.getValue() : null;
		    
	    	if ((UrlUtils.isBlank(contentType) || contentType.startsWith(parser.getResponseContentType()))) {
		    	if (response.getEntity() != null && response.getEntity().getContent() != null) {	    		
		    		stream = new BufferedInputStream(response.getEntity().getContent());
		    		ret =  parser.read(stream);
		    		return ret;		        
		    	}

		    	throw new CalDAV4JException("Error: No content stream at "+ getURI());
	    	} else {
		        log.error(ERR_CONTENT_TYPE + contentType);
		        throw new CalDAV4JProtocolException(ERR_CONTENT_TYPE + contentType );
	    		
	    	}
        } catch (IOException e) {
        	if (stream != null && log.isWarnEnabled()) { //the server sends the response
        			log.warn("Server response is " + UrlUtils.parseISToString(stream));
        	}
        	throw new CalDAV4JException("Error retrieving and parsing server response at " + getURI(), e);
        }	       
    }

	/**
	 * @return Returns the associated ResourceParser Instance
	 */
	public ResourceParser<T> getResourceParser() {
		return parser;
	}

	/**
	 * Set the ResourceParser Instance
	 * @param parser Instance to set
	 */
	public void setResourceParser(ResourceParser<T> parser) {
		this.parser = parser;
	}

}
