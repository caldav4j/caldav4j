package com.github.caldav4j.model.request;

import com.github.caldav4j.CalDAVConstants;

/**
 * This property defines the human-readable name of the calendar.
 */
public class DisplayName extends PropProperty {
	protected static String DISPLAY_NAME = "displayname" ;

	public DisplayName(){
		this(null);
	}

	/**
	 * @param displayName Display name of the calendar
	 */
	public DisplayName(String displayName) {
		super(DISPLAY_NAME, displayName, CalDAVConstants.NAMESPACE_WEBDAV);
	}
}

