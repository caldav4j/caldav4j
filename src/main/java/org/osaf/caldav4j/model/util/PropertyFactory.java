package org.osaf.caldav4j.model.util;

import org.apache.commons.lang.ArrayUtils;
import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.model.request.PropProperty;

public  class PropertyFactory implements CalDAVConstants {

	public static final String ACL =CalDAVConstants.DAV_ACL;
	public static final String PROPFIND = CalDAVConstants.DAV_PROPFIND;
	public static final String DISPLAYNAME = CalDAVConstants.DAV_DISPLAYNAME;
	public static final String PROP = CalDAVConstants.DAV_PROP;
	public static final String OWNER = CalDAVConstants.DAV_PRINCIPAL_OWNER;
	
	
	private static  String[] davProperties = new String[] {ACL, PROPFIND, DISPLAYNAME, PROP, OWNER};
	private static  String[] caldavProperties = new String[] {};
	
	public static  PropProperty createProperty(String property) throws CalDAV4JException {
		
		if (ArrayUtils.contains((Object[])davProperties, property)) {
			return new PropProperty(NS_DAV, NS_QUAL_DAV, property);
		} else if (ArrayUtils.contains((Object[])davProperties, property)) {
			return new PropProperty(NS_DAV, NS_QUAL_DAV, property);
		} 
		
		throw new CalDAV4JException("Unsupported property: "+ property); 
		
	}
	
}
