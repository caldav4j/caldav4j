package org.osaf.caldav4j.methods;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.UrlUtils;

public class HttpDelTicketMethod extends BaseDavRequest {

	private String ticket = null;
	
	/**
	 * @param path Path to the Resource
	 * @param ticket Ticket ID.
	 */
	public HttpDelTicketMethod(String path, String ticket) {
		super(URI.create(UrlUtils.removeDoubleSlashes(path)));
		this.ticket = ticket;
		setPath(path);
	}	
	
	/**
	 * @see org.apache.commons.httpclient.HttpMethodBase#setPath(String)
	 */
	public void setPath(String path){
		super.setURI(URI.create(UrlUtils.removeDoubleSlashes(path)));
	}
	
	@Override
	/**
	 * @see HttpMethod#getName()
	 */
	public String getMethod() {
		return CalDAVConstants.METHOD_DELTICKET;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	
	
    //TODO !A! modify so that this is called
    /** For httpclient 3.1 this was a protected method that was called automatically before the request 
     *  was executed. With httpclient 4 this method needs to be called explicitly.  */
	/**
	 * Adds the Ticket Request Header
	 */
	public void addRequestHeaders() {
		addHeader(CalDAVConstants.TICKET_HEADER, ticket);
	}
	
    @Override
    public boolean succeeded(HttpResponse response) {
 	   int statusCode = response.getStatusLine().getStatusCode();
 	   return statusCode == CaldavStatus.SC_OK;
    }
	
}
