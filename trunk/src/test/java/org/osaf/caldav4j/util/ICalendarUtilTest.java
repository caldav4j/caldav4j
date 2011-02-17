package org.osaf.caldav4j.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.methods.PutGetTest;

public class ICalendarUtilTest extends BaseTestCase{
	private static final Log log = LogFactory.getLog(PutGetTest.class);

	// list of different components
	private static List<String> calendarList = new ArrayList<String>();

	@Before
	public void setUp() throws Exception {
		log.trace("setUp");
		super.setUp();
		calendarList.add("icalendar/DAY-VTODO-123123.ics");
		calendarList.add(ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH);

	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}


	//
	// methods
	//


	/**
	 * update a master event or a recurring one
	 * 
	 * user story: google does it this way:
	 *  - pick an event on calendar by the 3-ple (UID, DTSTART, DTEND)
	 *  - if the event is recurring one then asks you whether:
	 *    - change the master
	 *    - change the recurrence identified by the 3-ple
	 *    - change the events subsequent
	 */
	@Test
	@Ignore
	public void testUpdateMasterComponent () throws Exception {
		// TODO writeme
	}

	/** 
	 * as testSetCalendarUid, but should throw exception, 
	 *  as .ics is not a valid caldav one
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testSetIcsUid() throws Exception {
		// test variable
		Component component = null; 

		// create a mixed VEVENT VTODO Calendar    	
		Calendar mixedCalendar = this.parseICS(calendarList.get(0));
		Calendar tmp = this.parseICS(calendarList.get(1));

		mixedCalendar.getComponents().add(ICalendarUtils.getFirstComponent(tmp));
		log.debug(mixedCalendar.toString());
		assertNotNull(mixedCalendar);

		// check that getFirstComponent rejects those kind of calendar
		try {
			component = ICalendarUtils.getFirstComponent(mixedCalendar);			
		} catch (Exception e) {	
			log.debug(e.getMessage());
		}
		assertNull(component);

	}

	@Test
	public void testParseStrangeICs() throws Exception {
		//	CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, false);
		//CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
		//CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true);
		//CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);

		Calendar strangeIcs =  this.parseICS("icalendar/Recurrent_with_timezone.ics");
		log.info(strangeIcs);
	}

	/**
	 * retrieve a component from a calendar with timezone
	 * @throws ParserException 
	 * @throws IOException 
	 */
	@Test
	public void testGetComponentFromCalendarWithTimezone() 
	throws Exception{
		// get a calendar from file  
		// or create one
		for (String resource : this.calendarList) {

			log.info("testing with: " + resource);
			Calendar cal = this.parseICS(resource);

			// get first component
			Component firstComponent = ICalendarUtils.getFirstComponent(cal);

			// print it | test if matches	
			log.debug(cal);	       
			log.debug(firstComponent.toString());
		}
	}

	/**
	 * retrieve a component from a calendar without timezone
	 */
	@Test
	@Ignore
	public void testGetComponentFromCalendar(){
		// get a calendar from file  
		// or create one

		// get first component
		// print it | test if matches
	}

	/**
	 * append an uid to a Component from an one-component calendar
	 */
	@Test
	@Ignore
	public void testSetCalendarUid() throws Exception {
		// get a calendar from file  
		// or create one
		String resource = "icalendar/Google_NOUID_Daily_NY_5pm.ics";
		String newUid = "newUid";

		log.info("testing with: " + resource);
		Calendar cal = this.parseICS(resource);

		// get first component
		Component firstComponent = ICalendarUtils.getFirstComponent(cal);

		// check that UID is not present
		String uid = ICalendarUtils.getUIDValue(cal);
		log.trace("uid=" + uid);
		if ("".equals(uid)) {
			uid = null;
		}
		assertNull(uid);

		// create one, add then retrieve from calendar
		ICalendarUtils.addOrReplaceProperty(firstComponent, new Uid(newUid));
		uid = ICalendarUtils.getUIDValue(cal);
		log.trace("uid=" + uid);
		if ("".equals(uid)) {
			uid = null;
		}

		// print it | test if matches
		assertEquals(newUid, uid);		
		log.debug(cal.toString());		
	}

	/**
	 * replace an uid to a Component from an one-component calendar
	 */
	public void _testReplaceCalendarUid() throws Exception{
		// get a calendar from file  
		// or create one
		String resource = "icalendar/Google_Daily_NY_5pm.ics";
		String newUid = "newUid";

		log.info("testing with: " + resource);
		Calendar cal = this.parseICS(resource);

		// check that UID is present
		String uid = ICalendarUtils.getUIDValue(cal);
		log.trace("uid=" + uid);
		if ("".equals(uid)) {
			uid = null;
		}
		assertNotNull(uid);

		// create one, replace, then retrieve from calendar
		ICalendarUtils.setUIDValue(cal, newUid);
		uid = ICalendarUtils.getUIDValue(cal);
		log.trace("uid=" + uid);
		if ("".equals(uid)) {
			uid = null;
		}

		// print it | test if matches
		assertEquals(newUid, uid);		
		log.debug(cal.toString());		

	}


	//
	// private
	//
	private Calendar parseICS(String resource) 
	throws Exception {
		InputStream stream = this.getClass().getClassLoader()
		.getResourceAsStream(resource);

		CalendarBuilder cb = new CalendarBuilder();
		Calendar cal = cb.build(stream);

		// a bit of more testing
		// XXX
		Component firstComponent =ICalendarUtils.getFirstComponent(cal); 
		if (firstComponent instanceof VToDo) {
			VToDo new_name = (VToDo) firstComponent;
			log.info("resource type of is: VTODO");
		} else if (firstComponent instanceof VEvent) {
			VEvent new_name = (VEvent) firstComponent;
			log.info("resource is: VEVENT");
		}
		return cal;
	}

}
