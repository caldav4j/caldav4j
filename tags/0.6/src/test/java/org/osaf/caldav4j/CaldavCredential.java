package org.osaf.caldav4j;

public class CaldavCredential {
	// sample class for storing credentials 
    //public static final String CALDAV_SERVER_HOST = "10.0.8.205";
    public  String CALDAV_SERVER_HOST = "velvet";
    public  int CALDAV_SERVER_PORT = 28080;
    public  String CALDAV_SERVER_PROTOCOL = "http";
    public  String CALDAV_SERVER_WEBDAV_ROOT = "/ucaldav/user/test@prova.it/";
    public  String CALDAV_SERVER_USERNAME = "test@prova.it";
    public  String CALDAV_SERVER_PASSWORD = "password";
    public  String COLLECTION      = "collection";

    public CaldavCredential() {
    	
    }
    
    public CaldavCredential(String proto, String server, int  port, String base, String collection, String user, String pass) {
    	CALDAV_SERVER_HOST = server;
    	CALDAV_SERVER_PORT = port;
    	CALDAV_SERVER_PROTOCOL = proto;
    	CALDAV_SERVER_WEBDAV_ROOT = base;
    	COLLECTION = collection;
    	CALDAV_SERVER_USERNAME = user;
    	CALDAV_SERVER_PASSWORD = pass;
    }
    
}

