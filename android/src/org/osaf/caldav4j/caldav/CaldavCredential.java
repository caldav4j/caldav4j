//package org.osaf.caldav4j.credential;
//package com.sec.caldav;
package org.osaf.caldav4j.caldav;

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
    public  String collection      = "466b7d10-3919-11e0-a476-96ce68d180f3";
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

