package org.osaf.caldav4j.credential;

public class BedeworkCaldavCredential extends CaldavCredential {
	public BedeworkCaldavCredential(){
		this.host = "bedework.example.com";
		this.port = 443;
		this.protocol = "https";
		this.user = "vbede";
		this.home = "/ucaldav/user/"+this.user+"/";
		this.password = "password";
		this.collection      = "collection/";
	}
}