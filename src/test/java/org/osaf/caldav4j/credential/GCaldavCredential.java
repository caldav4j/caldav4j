package org.osaf.caldav4j.credential;


public class GCaldavCredential extends CaldavCredential {
	   public GCaldavCredential() {
		     this.host = "www.google.com";
		     this.port = 443;
		     this.protocol = "https";
		     this.user = "rpolli@example.com";
		     this.home = "/calendar/dav/"+this.user+"/";
		     this.password = "password";
		     this.collection      = "events/";
	    }

}

