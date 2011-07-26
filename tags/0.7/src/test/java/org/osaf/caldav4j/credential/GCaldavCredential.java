package org.osaf.caldav4j.credential;


public class GCaldavCredential extends CaldavCredential {
	   public GCaldavCredential() {
		     this.host = "www.google.com";
		     this.port = 443;
		     this.protocol = "https";
		     this.user = "caldav4j@gmail.com";
		     this.home = "/calendar/dav/"+this.user+"/";
		     this.password = "caldav4j";
		     this.collection      = "events/";
	    }

}

