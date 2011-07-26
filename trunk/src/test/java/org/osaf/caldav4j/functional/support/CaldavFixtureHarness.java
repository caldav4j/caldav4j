package org.osaf.caldav4j.functional.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.TestConstants;

public class CaldavFixtureHarness implements TestConstants {

	protected static final Log log = LogFactory.getLog(CaldavFixtureHarness.class);
	/**
	 * @param fixture 
	 * 
	 */
	public static void provisionGoogleEvents(CalDavFixture fixture) {
		provisionEvents(fixture,  new String[] {
				ICS_GOOGLE_DAILY_NY_5PM_PATH,
				ICS_GOOGLE_ALL_DAY_JAN1_PATH,
				ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH,
				ICS_GOOGLE_SINGLE_EVENT_PATH
		});
	}

	public static void provisionSimpleEvents(CalDavFixture fixture) {
		provisionEvents(fixture, new String[] {	ICS_DAILY_NY_5PM_PATH,
				ICS_ALL_DAY_JAN1_PATH,
				ICS_NORMAL_PACIFIC_1PM_PATH,
				ICS_SINGLE_EVENT_PATH,ICS_FLOATING_JAN2_7PM_PATH }        	
		);
	}
	
	
	private static void provisionEvents(CalDavFixture fixture, String[] events) {
		try {
			fixture.makeCalendar(""); 
		} catch (Exception e) {
			log.info("MKCALENDAR unsupported?", e);
		}

		for (String eventPath :events) {
			fixture.caldavPut(eventPath);
		}
	}

}
