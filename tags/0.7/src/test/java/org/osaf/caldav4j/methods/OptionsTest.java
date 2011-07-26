package org.osaf.caldav4j.methods;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;

@Ignore
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
	@Test
	public void testOptions() {
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        
        for (String s : new String[] {INBOX, OUTBOX} ) {
        	
	
	        OptionsMethod options = new OptionsMethod();
	        options.setPath(caldavCredential.home + s);
	        try {
				http.executeMethod(hostConfig,options);
				int statusCode = options.getStatusCode();
				if (statusCode == 200) {
					log.info(options.getResponseHeader("Allow"));
					for (Header h : options.getResponseHeaders("DAV")) {
						if (h != null) {
							 if (h.getValue().contains("calendar-access")) { 
								 log.info(h);
							 } else if (h.getValue().contains("calendar-schedule")) {
								 log.info(h);
							 } else {
								 assertTrue(false);
							 }
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail(e.getMessage());
			} 
        }
        
	}
	
	
}
