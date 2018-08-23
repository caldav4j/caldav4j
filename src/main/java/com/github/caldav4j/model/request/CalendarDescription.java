package com.github.caldav4j.model.request;

import com.github.caldav4j.CalDAVConstants;


/**
 * Defines the Calendar Description, along with an optional language parameter.
 * <p>
 * Example:
 * <pre>
 *     &lt;C:calendar-description xml:lang="fr-CA" xmlns:C="urn:ietf:params:xml:ns:caldav"&gt;
 *          Roberto's Calendar
 *      &lt;/C:calendar-description&gt;
 * </pre>
 *
 * @author robipolli@gmail.com
 * @see <a href=http://tools.ietf.org/html/rfc4791#section-5.2.1>RFC 4791 Section 5.2.1</a>
 */
public class CalendarDescription extends PropProperty {

	public static final String CALENDAR_DESCRIPTION = "calendar-description";

	public CalendarDescription(){
        this(null);
    }

	/**
	 * @param value Calendar Description
	 */
	public CalendarDescription(String value) {
		this(value, null);

	}

	/**
	 * @param value Calendar Description
	 * @param lang Language
	 */
	public CalendarDescription(String value, String lang) {
		super(CalDAVConstants.CALDAV_CALENDAR_DESCRIPTION, value, CalDAVConstants.NAMESPACE_CALDAV);
		if (lang != null) {
			this.addAttribute("xml:lang", lang);
		}
	}
}
