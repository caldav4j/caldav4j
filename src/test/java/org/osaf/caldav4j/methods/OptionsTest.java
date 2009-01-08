package org.osaf.caldav4j.methods;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.CalDAVCalendarCollection;

public class OptionsTest extends BaseTestCase {

	public static final String OUTBOX = "/Outbox";
	public static final String INBOX = "/Inbox";
    // private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

	/**
	   >> Request <<

	   OPTIONS /lisa/calendar/outbox/ HTTP/1.1
	   Host: cal.example.com

	   >> Response <<

	   HTTP/1.1 204 No Content
	   Date: Thu, 31 Mar 2005 09:00:00 GMT
	   Allow: OPTIONS, GET, HEAD, POST, DELETE, TRACE,
	   Allow: PROPFIND, PROPPATCH, LOCK, UNLOCK, REPORT, ACL
	   DAV: 1, 2, 3, access-control
	   DAV: calendar-access, calendar-auto-schedule
	   */
	public void testOptions() {
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        
        for (String s : new String[] {INBOX, OUTBOX} ) {
        	
	
	        OptionsMethod options = new OptionsMethod();
	        options.setPath(CALDAV_SERVER_WEBDAV_ROOT + s);
	        try {
				http.executeMethod(hostConfig,options);
				int statusCode = options.getStatusCode();
				if (statusCode == 200) {
					System.out.println(options.getResponseHeader("Allow"));
					for (Header h : options.getResponseHeaders("DAV")) {
						if (h != null) {
							 if (h.getValue().contains("calendar-access")) { 
								 System.out.println(h);
							 } else if (h.getValue().contains("calendar-schedule")) {
								 System.out.println(h);
							 } else {
								 assertTrue(false);
							 }
						}
					}
				}
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
	}
	
	
}
