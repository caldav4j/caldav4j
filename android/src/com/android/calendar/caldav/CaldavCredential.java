//package org.osaf.caldav4j.credential;
//package com.sec.caldav;
package com.android.calendar.caldav;

public class CaldavCredential {
	// for davical server caldav://localhost/davical/caldav.php/caldavtest/home/
	/*
    public  String host = "10.0.2.2";
    public  int port = 80;
    public  String protocol = "http";
    public  String user = "caldavtest";
    public  String home = "/davical/caldav.php/" + user + "/";
    public  String password = "CalDavTest";
    public  String collection      = "home";
    */
	//chandler hub
	public  String host = "hub.chandlerproject.org";
	public  int port = 443;
    public  String protocol = "https";
    public  String user = "caldav4j";
    public  String home = "/dav/collection/";
    public  String password = "CalDAV4J";
    public  String collection      = "38f609b0-0c6a-11e0-9c9f-830012bc35cf";
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

