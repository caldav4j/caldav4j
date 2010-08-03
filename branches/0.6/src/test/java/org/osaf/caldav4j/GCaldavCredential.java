package org.osaf.caldav4j;

public class GCaldavCredential extends CaldavCredential {

	    public GCaldavCredential() {
	    	CALDAV_SERVER_HOST = "www.google.com";
	    	CALDAV_SERVER_PORT = 443;
	    	CALDAV_SERVER_PROTOCOL = "https";
	    	CALDAV_SERVER_WEBDAV_ROOT = "/calendar/dav/test@gmail.com/";
	    	CALDAV_SERVER_USERNAME = "test@gmail.com";
	    	CALDAV_SERVER_PASSWORD = "password";
	    	COLLECTION      = "events/";

	    }
}

