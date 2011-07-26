package org.osaf.caldav4j.model.request;

import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.util.XMLUtils;

public class CalendarDescriptionTest extends BaseTestCase {

	public CalendarDescriptionTest() {
		super();
	}

	@Test
	public void testPrintCalendarDescription() {
		CalendarDescription d = new CalendarDescription();
		log.info(XMLUtils.prettyPrint(d)	);
		
		d = new CalendarDescription("My Description");
		log.info(XMLUtils.prettyPrint(d)	);
		
		d = new CalendarDescription("My Description", "it");
		log.info(XMLUtils.prettyPrint(d)	);

	}
	
	@Test
	public void testPrintDisplayName() {
		DisplayName d = new DisplayName();
		log.info(XMLUtils.prettyPrint(d)	);
		
		d = new DisplayName("My Description");
		log.info(XMLUtils.prettyPrint(d)	);		

	}
}
