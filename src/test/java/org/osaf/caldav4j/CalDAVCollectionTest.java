/**
 * TODO re-implement test using deprecated methods using current methods
 */
package org.osaf.caldav4j;

import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.Privilege;
import org.apache.webdav.lib.methods.AclMethod;
import org.osaf.caldav4j.cache.EhCacheResourceCache;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.GenerateQuery;
import org.osaf.caldav4j.util.ICalendarUtils;

public class CalDAVCollectionTest extends BaseTestCase {
	public CalDAVCollectionTest(String method) {
		super(method);
	}

	protected static final Log log = LogFactory
	.getLog(CalDAVCollectionTest.class);

	protected CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

	protected HttpClient httpClient = createHttpClient();




	// cache
	private static final String HREF_TO_RESOURCE_CACHE = "hrefToResourceCache";
	private static final String UID_TO_HREF_CACHE = "uidToHrefCache";
	private EhCacheResourceCache myCache = null;

	public static final Integer TEST_TIMEOUT = 3600;
	public static final boolean TEST_READ = true;
	public static final boolean TEST_WRITE = true;
	public static final Integer TEST_VISITS = CalDAVConstants.INFINITY;

	public static final String  TEST_TIMEOUT_UNITS = "Second";
	protected void setUp() throws Exception {
		super.setUp();

		try {
			mkcalendar(COLLECTION_PATH); 
		} catch (Exception e) {
			e.printStackTrace();
			log.info("MKCOL unsupported?", e);
		}
		caldavPut(ICS_GOOGLE_DAILY_NY_5PM_PATH);
		caldavPut(ICS_GOOGLE_ALL_DAY_JAN1_PATH);
		caldavPut(ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH);
		caldavPut(ICS_GOOGLE_SINGLE_EVENT_PATH);

		//initialize cache
		CacheManager cacheManager = CacheManager.create();
		myCache = new EhCacheResourceCache();
		Cache uidToHrefCache = new Cache(UID_TO_HREF_CACHE, 1000, false, false,
				600, 300, false, 0);
		Cache hrefToResourceCache = new Cache(HREF_TO_RESOURCE_CACHE, 1000,
				false, false, 600, 300, false, 0);
		myCache.setHrefToResourceCache(hrefToResourceCache);
		myCache.setUidToHrefCache(uidToHrefCache);
		cacheManager.addCache(uidToHrefCache);
		cacheManager.addCache(hrefToResourceCache);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		CacheManager cacheManager = CacheManager.create();
		cacheManager.removeCache(UID_TO_HREF_CACHE);
		cacheManager.removeCache(HREF_TO_RESOURCE_CACHE);
		cacheManager.shutdown();

		caldavDel(ICS_GOOGLE_DAILY_NY_5PM_PATH);
		caldavDel(ICS_GOOGLE_ALL_DAY_JAN1_PATH);
		caldavDel(ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH);
		caldavDel(ICS_GOOGLE_SINGLE_EVENT_PATH);
		try {
			del(COLLECTION_PATH);
		} catch (Exception e) {
			log.info("DELETE Collection unsupported", e);
		}
	}

	public void testTestConnection()
	{
		CalDAVCollection calendarCollection = createCalDAVCollectionWithCache();

		try {
			// test with the right collection is ok
			int actual = calendarCollection.testConnection(httpClient);

			assertEquals(CaldavStatus.SC_OK, actual);
		} catch (CalDAV4JException e) {
			e.printStackTrace();
			assertNull(e);
		}
		HostConfiguration hostConfig = calendarCollection.getHostConfiguration();
		hostConfig.setHost("UNEXISTENT");
		try {
			int actual = calendarCollection.testConnection(httpClient);
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
	public void _testAddDeleteComponent() {
		// add a VEVENT with resource=uid.ics
		// check ETAGS in response

		// remove the VEVENT by UID
		// add a VEVENT with resource!=uid.ics
		// remove the VEVENT by UID

	}


	// get a Calendar creating a query
	public void _testGetCalendarByQuery() {

	}



	//
	// old CalDAVCalendarCollectionTests
	//
	public void testGetCalendar() throws Exception {
		CalDAVCollection calendarCollection = createCalDAVCollectionWithCache();        
		Calendar calendar = null;
		try {
			calendar = calendarCollection.getCalendarForEventUID(httpClient,
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
			calendar = calendarCollection.getCalendarForEventUID(httpClient,
					"NON_EXISTENT_RESOURCE");
		} catch (CalDAV4JException ce) {
			calDAV4JException = ce;
		}

		assertNotNull(calDAV4JException);
	}

	public void testGetCalendarByPath() throws Exception {
		CalDAVCollection calendarCollection = createCalDAVCollection();
		Calendar calendar = null;
		try {
			calendar = calendarCollection.getCalendar(httpClient,
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
			calendar = calendarCollection.getCalendar(httpClient,
					"NON_EXISTENT_RESOURCE");
		} catch (CalDAV4JException ce) {
			calDAV4JException = ce;
		}

		assertNotNull(calDAV4JException);
	}

	/**
	 * uses getCalendar(httpclient, query)
	 * 
	 * @throws Exception
	 */
	public void testGetEventResources() throws Exception {
		CalDAVCollection calendarCollection = createCalDAVCollectionWithCache();
		Date beginDate = ICalendarUtils.createDateTime(2006, 0, 1, null, true);
		Date endDate = ICalendarUtils.createDateTime(2006, 11, 9, null, true);
		List<Calendar> l = calendarCollection.getEventResources(httpClient,
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
	public void _testGetEventResourcesFloatingIssues() throws Exception {
		CalDAVCollection calendarCollection = createCalDAVCollection();

		// make sure our 7pm event gets returned
		Date beginDate = ICalendarUtils.createDateTime(2006, 0, 2, 19, 0, 0, 0,
				null, true);
		Date endDate = ICalendarUtils.createDateTime(2006, 0, 2, 20, 1, 0, 0,
				null, true);
		List<Calendar> l = calendarCollection.getEventResources(httpClient,
				beginDate, endDate);
		assertTrue(hasEventWithUID(l, ICS_FLOATING_JAN2_7PM_UID));

		beginDate = ICalendarUtils.createDateTime(2006, 0, 2, 20, 1, 0, 0,
				null, true);
		endDate = ICalendarUtils.createDateTime(2006, 0, 2, 20, 2, 0, 0, null,
				true);
		l = calendarCollection
		.getEventResources(httpClient, beginDate, endDate);
		assertFalse(hasEventWithUID(l, ICS_FLOATING_JAN2_7PM_UID));
	}

	/**
	 * @throws Exception
	 */
	public void testAddNewRemove() throws Exception {
		String newUid = "NEW_UID";
		String newEvent = "NEW_EVENT";
		VEvent ve = new VEvent();

		DtStart dtStart = new DtStart(new DateTime());
		Summary summary = new Summary(newEvent);
		Uid uid = new Uid(newUid);

		ve.getProperties().add(dtStart);
		ve.getProperties().add(summary);
		ve.getProperties().add(uid);

		CalDAVCollection calendarCollection = createCalDAVCollectionWithCache();        

		calendarCollection.add(httpClient, ve, null);

		Calendar calendar = calendarCollection.getCalendarForEventUID(
				httpClient, newUid);
		assertNotNull(calendar);

		log.info("Delete event with uid" + newUid);
		calendarCollection.deleteEvent(httpClient, newUid);

		log.info("Check if event is still on server");
		calendar = null;
		try {
			calendar = calendarCollection.getCalendarForEventUID(httpClient,
					newUid);
		} catch (ResourceNotFoundException e) {

		}
		assertNull(calendar);
	}
	/**
	 * @throws Exception
	 */
	public void testGetWithoutCacheThenWithCache()  {
		String newUid = "NEW_UID";
		String newEvent = "NEW_EVENT";
		VEvent ve = new VEvent();

		DtStart dtStart = new DtStart(new DateTime());
		Summary summary = new Summary(newEvent);
		Uid uid = new Uid(newUid);

		ve.getProperties().add(dtStart);
		ve.getProperties().add(summary);
		ve.getProperties().add(uid);

		CalDAVCollection calendarCollection = createCalDAVCollection();        

		try {
			calendarCollection.add(httpClient, ve, null);

			calendarCollection.enableSimpleCache();

			// set only etag for given resource
			CalendarQuery query = new GenerateQuery("VEVENT", "VEVENT : UID=="+newUid).generateQuery();
			query.setCalendarDataProp(null);
			List<CalDAVResource> res = calendarCollection.getCalDAVResources(httpClient, query);
			CalDAVResource r = res.get(0);
			assertNotNull(r);

			Calendar calendar  = calendarCollection.getCalendar(httpClient, newUid);
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			del("/calendar/dav/mog5nihf51ibm157h59gc82ldg%40group.calendar.google.com/events/"+newUid);
		}
	}
	/**
	 * @throws Exception
	 */
	public void testUpdateEvent() throws Exception {
		CalDAVCollection calendarCollection = createCalDAVCollectionWithCache();

		Calendar calendar = calendarCollection.getCalendarForEventUID(
				httpClient, ICS_NORMAL_PACIFIC_1PM_UID);

		VEvent ve = ICalendarUtils.getFirstEvent(calendar);

		// sanity!
		assertNotNull(calendar);
		assertEquals(ICS_NORMAL_PACIFIC_1PM_SUMMARY, ICalendarUtils
				.getSummaryValue(ve));

		ICalendarUtils.addOrReplaceProperty(ve, new Summary("NEW"));

		calendarCollection.updateMasterEvent(httpClient, ve, null);

		calendar = calendarCollection.getCalendarForEventUID(httpClient,
				ICS_NORMAL_PACIFIC_1PM_UID);

		ve = ICalendarUtils.getFirstEvent(calendar);
		assertEquals("NEW", ICalendarUtils.getSummaryValue(ve));

	}

	/**
	 * do a calendar-multiget with a valid event and an invalid one
	 */
	public void testMultigetCalendar() throws Exception {
		CalDAVCollection calendarCollection = createCalDAVCollection();

		final String baseUri = caldavCredential.CALDAV_SERVER_PROTOCOL +"://" 
		+ caldavCredential.CALDAV_SERVER_HOST+":" +caldavCredential.CALDAV_SERVER_PORT 
		+COLLECTION_PATH;

		List <String> calendarUris =  new ArrayList<String>();
		calendarUris.add( baseUri +"/"+BaseTestCase.ICS_ALL_DAY_JAN1_UID+".ics");
		calendarUris.add( baseUri +"/"+BaseTestCase.CALDAV_SERVER_BAD_USERNAME);

		List<Calendar> calendarList = calendarCollection.multigetCalendarUris(httpClient,
				calendarUris );

		// sanity
		assertNotNull(calendarList);
		assertEquals( ICS_ALL_DAY_JAN1_UID, ICalendarUtils.getUIDValue(calendarList.get(0).getComponent(CalendarComponent.VEVENT)) );

	}


	/**
	 * make a OPTIONS  requesto to caldav server
	 * @throws Exception
	 */
	public void testGetOptions() throws Exception {
		CalDAVCollection calendarCollection = createCalDAVCollection();

		List<Header> headerList = calendarCollection.getOptions(httpClient);

		for (Header h : headerList) {
			log.info(h.getName() + ":" + h.getValue());
		}

		Privilege privilege = Privilege.WRITE;


		Ace ace = new Ace("principal");
		ace.addPrivilege(privilege);
		AclMethod aclMethod = new AclMethod("path_to_resource");
		aclMethod.addAce(ace);

		if (calendarCollection.allows(httpClient, "MKCOL", headerList)) {
			log.info("MKCOL exists");
		}
		if (calendarCollection.allows(httpClient, "REPORT", headerList)) {
			log.info("REPORT exists");
		}
		if (calendarCollection.allows(httpClient, "NOOP", headerList)) {
			log.info("NOOP exists");
		}
	}


	public void testReportCalendarWithTimezone() throws Exception {
		CalDAVCollection calendarCollection = createCalDAVCollectionWithCache(); 

		GenerateQuery gq = new GenerateQuery();
		gq.setComponent("VEVENT :");
		CalendarQuery query = gq.generateQuery();

		Calendar calendar = null;
		try {
			calendar = calendarCollection.getCalendarForEventUID(httpClient,
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
			calendar = calendarCollection.getCalendarForEventUID(httpClient,
					"NON_EXISTENT_RESOURCE");
		} catch (CalDAV4JException ce) {
			calDAV4JException = ce;
		}

		assertNotNull(calDAV4JException);
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

	protected CalDAVCollection createCalDAVCollection() {
		CalDAVCollection calendarCollection = new CalDAVCollection(
				COLLECTION_PATH, createHostConfiguration(), methodFactory,
				CalDAVConstants.PROC_ID_DEFAULT);
		return calendarCollection;
	}

	protected CalDAVCollection createCalDAVCollectionWithCache() {


		CalDAVCollection calendarCollection = createCalDAVCollection();
		calendarCollection.setCache(myCache);
		return calendarCollection;
	}

	/**
	 * put an event on a caldav store using UID.ics
	 */
	 protected void caldavPut(String s) {    	 
		 Calendar cal = getCalendarResource(s);

		 String resPath = COLLECTION_PATH + "/" +cal.getComponent("VEVENT").getProperty("UID").getValue() + ".ics";
		 put (s, resPath );

	 }

	 /**
	  * remove an event on a caldav store using UID.ics
	  */
	 protected void caldavDel(String s) {
		 Calendar cal = getCalendarResource(s);
		 String delPath = COLLECTION_PATH + "/" +cal.getComponent("VEVENT").getProperty("UID").getValue() + ".ics";
		 log.debug("DEL " + delPath);
		 del(delPath);

	 }

}
