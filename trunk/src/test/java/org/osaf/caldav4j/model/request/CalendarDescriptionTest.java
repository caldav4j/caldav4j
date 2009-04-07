package org.osaf.caldav4j.model.request;

import org.osaf.caldav4j.BaseTestCase;

public class CalendarDescriptionTest extends BaseTestCase {

	public void testPrintCalendarDescription() {
		CalendarDescription d = new CalendarDescription("My Description");
		System.out.println(prettyPrint(d)	);
		
		d = new CalendarDescription("My Description", "it");
		System.out.println(prettyPrint(d)	);

	}
}
