package org.osaf.caldav4j.methods;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;

public class CalDAVReportMethodTest extends BaseTestCase {

	private static final Log log = LogFactory.getLog(CalDAVReportMethodTest.class);
	// private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();



	@Before
	public void setUp() throws Exception {
		super.setUp();
		mkcalendar(COLLECTION_PATH);
		put(ICS_DAILY_NY_5PM_PATH, COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
		put(ICS_ALL_DAY_JAN1_PATH, COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
		put(ICS_NORMAL_PACIFIC_1PM_PATH, COLLECTION_PATH + "/" + ICS_NORMAL_PACIFIC_1PM);
		put(ICS_SINGLE_EVENT_PATH, COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		del(COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
		del(COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
		del(COLLECTION_PATH + "/" + ICS_NORMAL_PACIFIC_1PM);
		del(COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);
		del(COLLECTION_PATH);
	}

	@Test
	public void testCheckTrailingSlash() throws Exception{
		CalDAVReportMethod method = methodFactory.createCalDAVReportMethod(COLLECTION_PATH);
		assertNotNull(method);


		assertNotNull(method.getPath());
		assertTrue(method.getPath().endsWith("/"));

	}


}