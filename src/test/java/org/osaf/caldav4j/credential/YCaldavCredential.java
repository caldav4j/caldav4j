package org.osaf.caldav4j.credential;


public class YCaldavCredential extends CaldavCredential {
	   public YCaldavCredential() {
		     this.host = "caldav.calendar.yahoo.com";
		     this.port = 443;
		     this.protocol = "https";
		     this.user = "yahooid";
		     this.home = "/dav/"+this.user+"/Calendar/";
		     this.password = "password";
		     this.collection      = "collection/";
	    }

}

