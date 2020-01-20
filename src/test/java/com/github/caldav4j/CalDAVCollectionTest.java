/*
 * Copyright © 2018 Ankush Mishra, Roberto Polli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.caldav4j;

import com.github.caldav4j.cache.EhCacheResourceCache;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.exceptions.ResourceNotFoundException;
import com.github.caldav4j.functional.support.CaldavFixtureHarness;
import com.github.caldav4j.model.request.CalendarData;
import com.github.caldav4j.model.request.CalendarQuery;
import com.github.caldav4j.model.request.PropProperty;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.GenerateQuery;
import com.github.caldav4j.util.ICalendarUtils;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import org.apache.http.HttpHost;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TODO re-implement test using deprecated methods using current methods
 */
public class CalDAVCollectionTest extends BaseTestCase {
	public CalDAVCollectionTest() {
		super();
	}

	protected static final Logger log = LoggerFactory.getLogger(CalDAVCollectionTest.class);

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

			assertEquals(CalDAVStatus.SC_OK, actual);
		} catch (CalDAV4JException e) {
			e.printStackTrace();
			assertNull(e);
		}
		HttpHost hostConfig = HttpHost.create("http://UNEXISTENT");
		collection.setHttpHost(hostConfig);
		try {
			int actual = collection.testConnection(fixture.getHttpClient());
			assertFalse("Hey! We shouldn't be able to connect now",
					actual == CalDAVStatus.SC_OK);
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
	public void queryCalendar() throws CalDAV4JException, IOException {
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
	public void queryPartialCalendar() throws CalDAV4JException, IOException {
		GenerateQuery gq=new GenerateQuery();

		//query by UID in a given timerange
		gq.setFilter("VEVENT : UID=="+ICS_GOOGLE_DAILY_NY_5PM_UID );
		gq.setRecurrenceSet("20060101T170000Z","20060103T230000Z", CalendarData.EXPAND);
		CalendarQuery query = gq.generate();
		List<Calendar> calendars = collection.queryCalendars(fixture.getHttpClient(), query);
		assertNotNull(calendars);
		assertEquals("bad number of responses: ",3,calendars.size());
		for (Calendar c : calendars) {
			assertEquals(ICalendarUtils.getUIDValue(c), ICS_GOOGLE_DAILY_NY_5PM_UID);
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
		Date endDate = ICalendarUtils.createDateTime(2006, 0, 9, null, true);
		Thread.sleep(1000);
		List<Calendar> l = collection.getEventResources(fixture.getHttpClient(),
				beginDate, endDate);

		for (Calendar calendar : l) {
			ComponentList<CalendarComponent> vevents = calendar.getComponents().getComponents(
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

		log.info("Delete event with uid " + newUid);
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

		final String baseUri = fixture.getCollectionPath();

		List <String> calendarUris =  new ArrayList<String>();
		calendarUris.add( baseUri +"/"+ ICS_ALL_DAY_JAN1_UID +".ics");
		calendarUris.add( baseUri +"/"+ CALDAV_SERVER_BAD_USERNAME);

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

	/**
	 * Tests add to check if same UID is added again, the UID is modified.
	 * @throws Exception
	 */
	@Test
	public void testAdd() throws Exception {
		InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream(ICS_GOOGLE_ALL_DAY_JAN1_PATH);
		Calendar c = (new CalendarBuilder()).build(stream);

		String uid = collection.add(fixture.getHttpClient(), c);
		assertNotEquals("These should not be equal", uid, ICS_ALL_DAY_JAN1_UID);
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





	/**
	 * Asserts over the {@code resource} retrieved from the request made to the server.
	 * @param resource Resource to assert.
	 * @param expectedUid Expected UID value found in the VEVENT component.
	 */
	private void checkCalendarResource(CalDAVResource resource, String expectedUid) {
		assertNotNull(resource.getCalendar());
		assertNotNull(resource.getCalendar().getComponents());
		CalendarComponent vevent = resource.getCalendar().getComponent(Component.VEVENT);
		assertNotNull(vevent);
		assertEquals(expectedUid, vevent.getProperty(Property.UID).getValue());
		assertNotNull(resource.getCalendar().getComponent(Component.VTIMEZONE));
	}

	/**
	 * Validates the resource is retrieved from the cache when it's Etag matches the one sent as parameter.
	 * @throws CalDAV4JException
	 */
	@Test
	public void testGetResourceUsingCacheWhenETagMatches() throws CalDAV4JException {
		String path = collection.getAbsolutePath(ICS_GOOGLE_DAILY_NY_5PM_UID + ".ics");
		String eTag = collection.getETag(fixture.getHttpClient(), path);
		// Add UID to cache.
		CalDAVResource calendar = collection.getCalDAVResourceByUID(fixture.getHttpClient(), Component.VEVENT, ICS_GOOGLE_DAILY_NY_5PM_UID);
		assertNotNull(calendar);

		CalDAVResource resource = collection.getCalDAVResource(fixture.getHttpClient(), path, eTag, null);

		assertNotNull(resource);
		checkCalendarResource(resource, ICS_GOOGLE_DAILY_NY_5PM_UID);
	}

	/**
	 * Retrieves resource by making another request to the server when response sent is null.
	 * @throws CalDAV4JException
	 */
	@Test
	public void testGetResourceFromRequestWhenResponseIsNull() throws CalDAV4JException {
		String path = collection.getAbsolutePath(ICS_GOOGLE_DAILY_NY_5PM_UID + ".ics");

		CalDAVResource resource = collection.getCalDAVResource(fixture.getHttpClient(), path, null, null);

		assertNotNull(resource);
		checkCalendarResource(resource, ICS_GOOGLE_DAILY_NY_5PM_UID);
	}

	/**
	 * Retrieves resource by making another request to the server when response sent doesn't have a calendar component.
	 * @throws CalDAV4JException
	 */
	@Test
	public void testGetResourceFromRequestWhenResponseWithoutCalendarComponent() throws CalDAV4JException {
		String path = collection.getAbsolutePath(ICS_GOOGLE_DAILY_NY_5PM_UID + ".ics");
		MultiStatusResponse response = new MultiStatusResponse("HREF", CalDAVStatus.SC_NO_CONTENT);

		CalDAVResource resource = collection.getCalDAVResource(fixture.getHttpClient(), path, null, response);

		assertNotNull(resource);
		checkCalendarResource(resource, ICS_GOOGLE_DAILY_NY_5PM_UID);
	}

	/**
	 * Retrieves resource directly from the response sent as it contains a valid calendar component.
	 * Loads a different resource into the mock response to ensure the mock is used instead of the server request.
	 * @throws CalDAV4JException Possible exception when requesting the resource from the CalDAV server.
	 */
	@Test
	public void testGetResourceUsingExistentResponseWhenAvailable() throws CalDAV4JException, IOException, ParserException {
		String path = collection.getAbsolutePath(ICS_GOOGLE_DAILY_NY_5PM_PATH + ".ics");
		MultiStatusResponse mockResponse = new MultiStatusResponse("HREF", "");
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(ICS_GOOGLE_ALL_DAY_JAN1_PATH);
		PropProperty<Calendar> property = new PropProperty<>(CalDAVConstants.CALDAV_CALENDAR_DATA,
				(new CalendarBuilder()).build(stream), CalDAVConstants.NAMESPACE_CALDAV);
		mockResponse.add(property, CalDAVStatus.SC_OK);

		CalDAVResource resource = collection.getCalDAVResource(fixture.getHttpClient(), path, null, mockResponse);

		assertNotNull(resource);
		checkCalendarResource(resource, ICS_GOOGLE_ALL_DAY_JAN1_UID);
	}
}
