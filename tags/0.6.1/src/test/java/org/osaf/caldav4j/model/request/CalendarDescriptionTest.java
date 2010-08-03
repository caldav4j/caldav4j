package org.osaf.caldav4j.model.request;

import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.util.XMLUtils;

public class CalendarDescriptionTest extends BaseTestCase {

	public CalendarDescriptionTest(String method) {
		super(method);
		// TODO Auto-generated constructor stub
	}

	public void testPrintCalendarDescription() {
		CalendarDescription d = new CalendarDescription();
		System.out.println(XMLUtils.prettyPrint(d)	);
		
		d = new CalendarDescription("My Description");
		System.out.println(XMLUtils.prettyPrint(d)	);
		
		d = new CalendarDescription("My Description", "it");
		System.out.println(XMLUtils.prettyPrint(d)	);

	}
	
	public void testPrintDisplayName() {
		DisplayName d = new DisplayName();
		System.out.println(XMLUtils.prettyPrint(d)	);
		
		d = new DisplayName("My Description");
		System.out.println(XMLUtils.prettyPrint(d)	);		

	}
}
