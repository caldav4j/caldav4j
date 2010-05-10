package org.osaf.caldav4j.credential;

public class CaldavCredential {
	// sample class for storing credentials 
    //public static final String CALDAV_SERVER_HOST = "10.0.8.205";
    public  String host = "localhost";
    public  int port = 8080;
    public  String protocol = "http";
    public  String user = "vbede";
    public  String home = "/ucaldav/user/"+user+"/";
    public  String password = "bedework";
    public  String collection      = "collection/";

    public CaldavCredential() {
    	
    }
    
    public CaldavCredential(String proto, String server, int  port, String base, String collection, String user, String pass) {
    	this.host = server;
    	this.port = port;
    	this.protocol = proto;
    	this.home = base;
    	this.collection = collection;
    	this.user = user;
    	this.password = pass;
    }
    
}

