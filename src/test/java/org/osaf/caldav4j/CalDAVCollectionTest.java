/**
 * TODO re-implement test using deprecated methods using current methods
 */
package org.osaf.caldav4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.sf.ehcache.CacheManager;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.cache.EhCacheResourceCache;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.exceptions.ResourceNotFoundException;
import org.osaf.caldav4j.functional.support.CaldavFixtureHarness;
import org.osaf.caldav4j.model.request.CalendarData;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.GenerateQuery;
import org.osaf.caldav4j.util.ICalendarUtils;

@Ignore
public class CalDAVCollectionTest extends BaseTestCase {
	public CalDAVCollectionTest() {
		super();
	}

	protected static final Log log = LogFactory
	.getLog(CalDAVCollectionTest.class);





	// cache should be visible to be used in assertions
	private EhCacheResourceCache myCache = null;

	public static final Integer TEST_TIMEOUT = 3600;
	public static final boolean TEST_READ = true;
	public static final boolean TEST_WRITE = true;
	public static final Integer TEST_VISITS = CalDAVConstants.INFINITY;

	public static final String  TEST_TIMEOUT_UNITS = "Second";

	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		CaldavFixtureHarness.provisionGoogleEvents(fixture);
		
		myCache = CaldavFixtureHarness.createSimpleCache();
		collection.setCache(myCache);
		
		uncachedCollection = CaldavFixtureHarness.createCollectionFromFixture(fixture);
	}



	@After
	public void tearDown() throws Exception {
		CaldavFixtureHarness.removeSimpleCache();		
		fixture.tearDown();
	}

	@Test
	public void testTestConnection()
	{

		try {
			// test with the right collection is ok
			int actual = collection.testConnection(fixture.getHttpClient());

			assertEquals(CaldavStatus.SC_OK, actual);
		} catch (CalDAV4JException e) {
			e.printStackTrace();
			assertNull(e);
		}
		HostConfiguration hostConfig = collection.getHostConfiguration();
		hostConfig.setHost("UNEXISTENT");
		try {
			int actual = collection.testConnection(fixture.getHttpClient());
			assertFalse("Hey! We shouldn't be able to connect now", 
					actual==CaldavStatus.SC_OK);
		} catch (CalDAV4JException e) {
			// do nothing, it should except
			assertNotNull("Server shouldn't connect now", e);
		}



	}

	//
	// new tests for CalDAVCollection
	//
	@Test
	@Ignore
	public void testAddDeleteComponent() {
		// add a VEVENT with resource=uid.ics
		// check ETAGS in response

		// remove the VEVENT by UID
		// add a VEVENT with resource!=uid.ics
		// remove the VEVENT by UID

	}


	// get a Calendar by uid, then by summary, then by recurrence-id
	@Test
	public void queryCalendar() throws CalDAV4JException {
		Calendar calendar = null;
		GenerateQuery gq=new GenerateQuery();

		// query by uid
		calendar = collection.queryCalendar(fixture.getHttpClient(), Component.VEVENT, ICS_GOOGLE_DAILY_NY_5PM_UID, null);
		assertNotNull(calendar);

		//check if is cache
		assertNotNull(collection.getCache().getHrefForEventUID(ICS_GOOGLE_DAILY_NY_5PM_UID));		

		//query by SUMMARY
		calendar = null;
		gq.setFilter("VEVENT : SUMMARY=="+ICS_GOOGLE_NORMAL_PACIFIC_1PM_SUMMARY );
		List<Calendar>calendars = collection.queryCalendars(fixture.getHttpClient(), gq.generate());		
		assertNotNull(calendars);
		assertEquals("non unique result",calendars.size(), 1);
		calendar = calendars.get(0);
		assertEquals(ICalendarUtils.getUIDValue(calendar), ICS_GOOGLE_NORMAL_PACIFIC_1PM_UID);
		//check if is in cache

	}

	// TODO: this is work in progress; see issue 48
	@Ignore
	@Test
	public void queryPartialCalendar() throws CalDAV4JException {
		Calendar calendar = null;
		GenerateQuery gq=new GenerateQuery();

		//query by UID in a given timerange
		calendar = null;
		gq.setFilter("VEVENT : UID=="+ICS_GOOGLE_DAILY_NY_5PM_UID );
		gq.setRecurrenceSet("20060101T170000Z","20060103T230000Z", CalendarData.EXPAND);

		List<Calendar>calendars = collection.queryCalendars(fixture.getHttpClient(), gq.generate());		
		assertNotNull(calendars);
		assertEquals("bad number of responses: ",3,calendars.size());
		for (Calendar c : calendars) {
			assertEquals(ICalendarUtils.getUIDValue(calendar), ICS_GOOGLE_DAILY_NY_5PM_UID);
			assertNotNull(ICalendarUtils.getPropertyValue(c.getComponent(Component.VEVENT), Property.RECURRENCE_ID));
		}
		//check if is in cache

	}

	@Test
	public void testGetCalendarByPath() throws Exception {
		Calendar calendar = null;
		try {
			calendar = uncachedCollection.getCalendar(fixture.getHttpClient(),
					ICS_GOOGLE_DAILY_NY_5PM_UID + ".ics");
		} catch (CalDAV4JException ce) {
			assertNull(ce);
		}

		assertNotNull(calendar);
		VEvent vevent = ICalendarUtils.getFirstEvent(calendar);
		assertNotNull(vevent);
		String summary = ICalendarUtils.getSummaryValue(vevent);
		assertEquals(ICS_DAILY_NY_5PM_SUMMARY, summary);

		CalDAV4JException calDAV4JException = null;
		try {
			calendar = uncachedCollection.getCalendar(fixture.getHttpClient(),
			"NON_EXISTENT_RESOURCE");
		} catch (CalDAV4JException ce) {
			calDAV4JException = ce;
		}

		assertNotNull(calDAV4JException);
	}

	/**
	 * uses getCalendar(fixture.getHttpClient(), query)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetEventResources() throws Exception {
		Date beginDate = ICalendarUtils.createDateTime(2006, 0, 1, null, true);
		Date endDate = ICalendarUtils.createDateTime(2006, 11, 9, null, true);
		List<Calendar> l = collection.getEventResources(fixture.getHttpClient(),
				beginDate, endDate);

		for (Calendar calendar : l) {
			ComponentList vevents = calendar.getComponents().getComponents(
					Component.VEVENT);
			VEvent ve = (VEvent) vevents.get(0);
			String uid = ICalendarUtils.getUIDValue(ve);
			int correctNumberOfEvents = -1;
			if (ICS_DAILY_NY_5PM_UID.equals(uid)) {
				// one for each day
				correctNumberOfEvents = 1;
			} else if (ICS_ALL_DAY_JAN1_UID.equals(uid)) {
				correctNumberOfEvents = 1;
			} else if (ICS_NORMAL_PACIFIC_1PM_UID.equals(uid)) {
				correctNumberOfEvents = 1;
			} else if (ICS_FLOATING_JAN2_7PM_UID.equals(uid)) {
				correctNumberOfEvents = 0;
			} else {
				fail(uid
						+ " is not the uid of any event that should have been returned");
			}

			assertEquals(correctNumberOfEvents, vevents.size());
		}

		// 3 calendars - one for each resource (not including expanded
		// recurrences)
		assertEquals(3, l.size());

	}

	// TODO wait on floating test until we can pass timezones
	/**
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testGetEventResourcesFloatingIssues() throws Exception {

		// make sure our 7pm event gets returned
		Date beginDate = ICalendarUtils.createDateTime(2006, 0, 2, 19, 0, 0, 0,
				null, true);
		Date endDate = ICalendarUtils.createDateTime(2006, 0, 2, 20, 1, 0, 0,
				null, true);
		List<Calendar> l = uncachedCollection.getEventResources(fixture.getHttpClient(),
				beginDate, endDate);
		assertTrue(hasEventWithUID(l, ICS_FLOATING_JAN2_7PM_UID));

		beginDate = ICalendarUtils.createDateTime(2006, 0, 2, 20, 1, 0, 0,
				null, true);
		endDate = ICalendarUtils.createDateTime(2006, 0, 2, 20, 2, 0, 0, null,
				true);
		l = uncachedCollection
		.getEventResources(fixture.getHttpClient(), beginDate, endDate);
		assertFalse(hasEventWithUID(l, ICS_FLOATING_JAN2_7PM_UID));
	}

	/**
	 * add and remove a vevent using
	 *  - add(), queryCalendar(component, uid), delete(component, uid)
	 * @throws Exception
	 */
	@Test
	public void testAddNewRemove() throws Exception {
		String newUid = "NEW_UID";
		String newEvent = "NEW_EVENT";
		VEvent ve = newEvent(newUid, newEvent);


		collection.add(fixture.getHttpClient(), ve, null);
		Calendar calendar = collection.queryCalendar(fixture.getHttpClient(), Component.VEVENT, newUid, null);
		assertNotNull(calendar);

		log.info("Delete event with uid" + newUid);
		collection.delete(fixture.getHttpClient(), Component.VEVENT, newUid);

		log.info("Check if event is still on server");
		calendar = null;
		try {
			calendar = collection.queryCalendar(fixture.getHttpClient(), Component.VEVENT, newUid, null);
		} catch (ResourceNotFoundException e) {}

		assertNull(calendar);
	}



	/**
	 * @param newUid
	 * @param newEvent
	 * @return
	 */
	public VEvent newEvent(String newUid, String newEvent) {
		VEvent ve = new VEvent();

		DtStart dtStart = new DtStart(new DateTime());
		Summary summary = new Summary(newEvent);
		Uid uid = new Uid(newUid);

		ve.getProperties().add(dtStart);
		ve.getProperties().add(summary);
		ve.getProperties().add(uid);
		return ve;
	}
	/**
	 * @throws Exception
	 */
	@Test
	public void testGetWithoutCacheThenWithCache()  throws IOException {
		String newUid = "NEW_UID";
		String newEvent = "NEW_EVENT";
		VEvent ve = newEvent(newUid, newEvent);

		CalDAV4JException e = null;
		try {
			uncachedCollection.add(fixture.getHttpClient(), ve, null);

			uncachedCollection.setCache(myCache);

			// set only etag for given resource
			CalendarQuery query = new GenerateQuery("VEVENT", "VEVENT : UID=="+newUid).generate();
			query.setCalendarDataProp(null);
			List<CalDAVResource> res = uncachedCollection.getCalDAVResources(fixture.getHttpClient(), query);
			assertTrue(res.size()>0);
			CalDAVResource r = res.get(0);
			assertNotNull(r);

			Calendar calendar  = uncachedCollection.getCalendar(fixture.getHttpClient(), newUid+".ics");
			assertNotNull(calendar);
		} catch (CalDAV4JException e1) {
			e=e1;
		} finally {
			fixture.delete(newUid+".ics");
		}
		assertNull(e);
	}
	/**
	 * @throws Exception
	 */
	@Test
	public void testUpdateEvent() throws Exception {

		Calendar calendar = collection.getCalendarForEventUID(
				fixture.getHttpClient(), ICS_NORMAL_PACIFIC_1PM_UID);

		VEvent ve = ICalendarUtils.getFirstEvent(calendar);

		// sanity!
		assertNotNull(calendar);
		assertEquals(ICS_NORMAL_PACIFIC_1PM_SUMMARY, ICalendarUtils
				.getSummaryValue(ve));

		ICalendarUtils.addOrReplaceProperty(ve, new Summary("NEW"));

		collection.updateMasterEvent(fixture.getHttpClient(), ve, null);

		calendar = collection.getCalendarForEventUID(fixture.getHttpClient(),
				ICS_NORMAL_PACIFIC_1PM_UID);

		ve = ICalendarUtils.getFirstEvent(calendar);
		assertEquals("NEW", ICalendarUtils.getSummaryValue(ve));

	}

	/**
	 * do a calendar-multiget with a valid event and an invalid one
	 */
	@Test
	public void testMultigetCalendar() throws Exception {

		final String baseUri = caldavCredential.protocol +"://" 
		+ caldavCredential.host+":" +caldavCredential.port 
		+caldavCredential.home+"/" + caldavCredential.collection;

		List <String> calendarUris =  new ArrayList<String>();
		calendarUris.add( baseUri +"/"+BaseTestCase.ICS_ALL_DAY_JAN1_UID+".ics");
		calendarUris.add( baseUri +"/"+BaseTestCase.CALDAV_SERVER_BAD_USERNAME);

		List<Calendar> calendarList = uncachedCollection.multigetCalendarUris(fixture.getHttpClient(),
				calendarUris );

		// sanity
		assertNotNull(calendarList);
		assertEquals( ICS_ALL_DAY_JAN1_UID, ICalendarUtils.getUIDValue(calendarList.get(0).getComponent(CalendarComponent.VEVENT)) );

	}


	@Test
	public void testReportCalendarWithTimezone() throws Exception {

		GenerateQuery gq = new GenerateQuery();
		gq.setComponent("VEVENT :");
		CalendarQuery query = gq.generate();

		Calendar calendar = null;
		try {
			calendar = collection.getCalendarForEventUID(fixture.getHttpClient(),
					ICS_DAILY_NY_5PM_UID);
		} catch (CalDAV4JException ce) {
			assertNull(ce);
		}

		assertNotNull(calendar);
		VEvent vevent = ICalendarUtils.getFirstEvent(calendar);
		assertNotNull(vevent);
		String summary = ICalendarUtils.getSummaryValue(vevent);
		assertEquals(ICS_DAILY_NY_5PM_SUMMARY, summary);

		CalDAV4JException calDAV4JException = null;
		try {
			calendar = collection.getCalendarForEventUID(fixture.getHttpClient(),
			"NON_EXISTENT_RESOURCE");
		} catch (CalDAV4JException ce) {
			calDAV4JException = ce;
		}

		assertNotNull(calDAV4JException);
	}


	@Test
	public void getHref() {
		String ret = collection.getHref("PATH");
		log.info(ret);
	}
	//
	// private
	//
	private boolean hasEventWithUID(List<Calendar> cals, String uid) {
		for (Calendar cal : cals) {
			ComponentList vEvents = cal.getComponents().getComponents(
					Component.VEVENT);
			if (vEvents.size() == 0) {
				return false;
			}
			VEvent ve = (VEvent) vEvents.get(0);
			String curUid = ICalendarUtils.getUIDValue(ve);
			if (curUid != null && uid.equals(curUid)) {
				return true;
			}
		}

		return false;
	}





}
