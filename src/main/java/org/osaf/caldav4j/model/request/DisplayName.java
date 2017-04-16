package org.osaf.caldav4j.model.request;

import org.osaf.caldav4j.CalDAVConstants;

public class DisplayName extends PropProperty {
	protected static String DISPLAY_NAME = "displayname" ;

	public DisplayName(){
		this(null);
	}
	public DisplayName(String displayName) {
		super(DISPLAY_NAME, displayName, CalDAVConstants.NAMESPACE_WEBDAV);
	}
}

