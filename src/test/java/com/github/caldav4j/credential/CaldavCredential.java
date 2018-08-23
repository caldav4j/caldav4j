package com.github.caldav4j.credential;

import java.net.URI;

public class CaldavCredential {
	static String CALDAV_YAHOO = "https://caldav.calendar.yahoo.com/dav/%s/Calendar/";
	static String CALDAV_GOOGLE = "https://www.google.com/calendar/dav/%s/";
	static String CALDAV_BEDEWORK = "http://revolver:8080/ucaldav/user/%s/";
	static String CALDAV_CHANDLER = "https://hub.chandlerproject.org/dav/%s/";

	// sample class for storing credentials 
    //public static final String CALDAV_SERVER_HOST = "10.0.8.205";
	public String host = "";
	public  int port = 443;
    public  String protocol = "https";
    public  String user = "caldav4j";
    public  String home = "/dav/"+user+"/";
    public  String password = "CalDAV4J";
    public  String collection      = "collection_changeme/";
	private String proxyHost = null;
	private int	proxyPort = 0;

    public CaldavCredential() {
    	
    }
    
    public CaldavCredential(String uri) {
    	try {
			URI server = new URI(uri);
			protocol = server.getScheme().replaceAll("://", "");
			host = server.getHost();
			//port = server.getPort() != -1 ? server.getPort() : (server.getScheme().endsWith("s") ? 443: 80);
			port = server.getPort(); //!A! For 3.1 ports 80 and 443 were assigned automatically. But fixing the https port to 443 led to error 502 - Connection refused.			
			home = server.getPath().replace("\\w+/$", "");
			String userInfo = server.getUserInfo();
			if (userInfo != null) {
				user = userInfo.split(":")[0];
				password = userInfo.split(":")[1];
			}
		} catch (Exception e) {
			// noop
		}
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

    public CaldavCredential(String proto, String server, int  port, String base, String collection, String user, String pass, String proxyHost, int proxyPort) {
    	this.host = server;
    	this.port = port;
    	this.protocol = proto;
    	this.home = base;
    	this.collection = collection;
    	this.user = user;
    	this.password = pass;
    	
    	// constructor supporting http proxy
    	this.proxyHost = proxyHost;
    	this.proxyPort = proxyPort;
    }

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	
	/** Sets the collection string, replacing <code>"%user%"</code> with the current user name.
	 *  In the following string 
	 *  <code>dav/%user%/Calendar/myCalendar</code>
	 *  the <code>%user%</code> part will be replaced with the current name (if any). 
	 *   */
	public void setCollectionString(String value) {
		this.collection = value.replace("%user%", this.user); 
	}
	
    
}

