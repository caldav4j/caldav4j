package org.osaf.caldav4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.cache.CalDAVResourceCache;
import org.osaf.caldav4j.cache.EhCacheResourceCache;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.exceptions.ResourceNotFoundException;
import org.osaf.caldav4j.util.ICalendarUtils;

/**
 * Tests CalDAVCalendarCollection with caching on. Mostly the same tests, but
 * with an EhCahche instead of a NoOp Cache
 * 
 * @author bobbyrullo
 * 
 */
public class CalDAVCalendarCollectionWithCacheTest extends BaseTestCase {

	private static final String HREF_TO_RESOURCE_CACHE = "hrefToResourceCache";

    private static final String UID_TO_HREF_CACHE = "uidToHrefCache";

    private static final Log log = LogFactory
            .getLog(CalDAVCalendarCollectionWithCacheTest.class);

//    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
//
//    private HttpClient httpClient = createHttpClient();



    private CalDAVResourceCache cache = null;

    private class TestingCacheEventListener implements CacheEventListener {
        public Element lastUidToHrefElementPut = null;

        public Element lastHreftoResourceElementPut = null;

        public Element lastUidToHrefElementRemoved = null;

        public Element lastHreftoResourceElementRemoved = null;

        public Element lastUidToHrefElementUpdated = null;

        public Element lastHreftoResourceElementUpdated = null;

        public void dispose() {
        }

        public void notifyElementExpired(Cache cache, Element element) {
        }

        public void notifyElementPut(Cache cache, Element element)
                throws CacheException {
            if (cache.getName().equals(UID_TO_HREF_CACHE)) {
                lastUidToHrefElementPut = element;
            } else if (cache.getName().equals(HREF_TO_RESOURCE_CACHE)) {
                lastHreftoResourceElementPut = element;
            }
        }

        public void notifyElementRemoved(Cache cache, Element element)
                throws CacheException {
            if (cache.getName().equals(UID_TO_HREF_CACHE)) {
                lastUidToHrefElementRemoved = element;
            } else if (cache.getName().equals(HREF_TO_RESOURCE_CACHE)) {
                lastHreftoResourceElementRemoved = element;
            }
        }

        public void notifyElementUpdated(Cache cache, Element element)
                throws CacheException {
            if (cache.getName().equals(UID_TO_HREF_CACHE)) {
                lastUidToHrefElementUpdated = element;
            } else if (cache.getName().equals(HREF_TO_RESOURCE_CACHE)) {
                lastHreftoResourceElementUpdated = element;
            }
        }

    };

    public TestingCacheEventListener listener = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mkcalendar(COLLECTION_PATH);
        put(ICS_DAILY_NY_5PM_PATH, COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
        put(ICS_ALL_DAY_JAN1_PATH, COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
        put(ICS_NORMAL_PACIFIC_1PM_PATH, COLLECTION_PATH + "/"
                + ICS_NORMAL_PACIFIC_1PM);
        put(ICS_SINGLE_EVENT_PATH, COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);

        cache = new EhCacheResourceCache();
        listener  = new TestingCacheEventListener();
        
        CacheManager cacheManager = CacheManager.create();
        Cache uidToHrefCache = new Cache(UID_TO_HREF_CACHE, 1000, false, false,
                600, 300, false, 0);
        uidToHrefCache.getCacheEventNotificationService().registerListener(listener);
        cacheManager.addCache(uidToHrefCache);
        Cache hrefToResourceCache = new Cache(HREF_TO_RESOURCE_CACHE, 1000,
                false, false, 600, 300, false, 0);
        hrefToResourceCache.getCacheEventNotificationService().registerListener(listener);
        cacheManager.addCache(hrefToResourceCache);
        
        ((EhCacheResourceCache) cache)
                .setHrefToResourceCache(hrefToResourceCache);
        ((EhCacheResourceCache) cache).setUidToHrefCache(uidToHrefCache);
    }

    @After
    public void tearDown() throws Exception {
        CacheManager cacheManager = CacheManager.create();
        cacheManager.removeCache(UID_TO_HREF_CACHE);
        cacheManager.removeCache(HREF_TO_RESOURCE_CACHE);
        
        del(COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
        del(COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
        del(COLLECTION_PATH + "/" + ICS_NORMAL_PACIFIC_1PM);
        del(COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);
        del(COLLECTION_PATH);
    }

    @Test
    public void testGetCalendar() throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
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

    @Test
    public void testGetCalendarByPath() throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        Calendar calendar = null;
        try {
            calendar = calendarCollection.getCalendarByPath(httpClient,
                    ICS_DAILY_NY_5PM);
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
            calendar = calendarCollection.getCalendarByPath(httpClient,
                    "NON_EXISTENT_RESOURCE");
        } catch (CalDAV4JException ce) {
            calDAV4JException = ce;
        }

        assertNotNull(calDAV4JException);
    }

    @Test
    public void testGetEventResources() throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        Date beginDate = ICalendarUtils.createDateTime(2006, 0, 1, null, true);
        Date endDate = ICalendarUtils.createDateTime(2006, 0, 9, null, true);
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
    @Test
    @Ignore
    public void testGetEventResourcesFloatingIssues() throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();

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

    @Test
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

        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        calendarCollection.addEvent(httpClient, ve, null);

        Calendar calendar = calendarCollection.getCalendarForEventUID(
                httpClient, newUid);
        assertNotNull(calendar);

        calendarCollection.deleteEvent(httpClient, newUid);
        calendar = null;
        try {
            calendar = calendarCollection.getCalendarForEventUID(httpClient,
                    newUid);
        } catch (ResourceNotFoundException e) {

        }
        assertNull(calendar);
    }

    @Test
    public void testUpdateEvent() throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();

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
     * We want to make sure that the cache actually is being used!
     * 
     */
    @Test
    public void testCacheGetsHit() throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();

        // basically ensure that nothing has been put in the cache yet
        assertNull(listener.lastHreftoResourceElementPut);
        assertNull(listener.lastUidToHrefElementPut);

        Calendar calendar = calendarCollection.getCalendarForEventUID(
                httpClient, ICS_NORMAL_PACIFIC_1PM_UID);

        // NOW something should be in the cache - let's make sure
        assertNotNull("Something didn't get put in the cache!",
                listener.lastHreftoResourceElementPut);
        assertNotNull("Something didn't get put in the cache!",
                listener.lastUidToHrefElementPut);

        // did the right thing get put in the cache? Check the uid.
        assertEquals(listener.lastUidToHrefElementPut.getKey(),
                ICS_NORMAL_PACIFIC_1PM_UID);
        String href = (String) listener.lastUidToHrefElementPut.getValue();
        assertEquals(listener.lastHreftoResourceElementPut.getKey(), href);

        long lastUID2HrefAccessTime = listener.lastUidToHrefElementPut
                .getLastAccessTime();
        long lastHref2ResourceAccessTime = listener.lastHreftoResourceElementPut
                .getLastAccessTime();

        // ok let's get the same resource again, and make sure the cache really
        // was hit
        calendar = calendarCollection.getCalendarForEventUID(httpClient,
                ICS_NORMAL_PACIFIC_1PM_UID);

        // lets see if the cache time changed:
        assertTrue(listener.lastUidToHrefElementPut.getLastAccessTime() > lastUID2HrefAccessTime);
        assertTrue(listener.lastHreftoResourceElementPut.getLastAccessTime() > lastHref2ResourceAccessTime);

        // update the values for the next test
        Element uidToHrefElement = listener.lastUidToHrefElementPut;
        Element hrefToResourceElement = listener.lastHreftoResourceElementPut;
        lastUID2HrefAccessTime = listener.lastUidToHrefElementPut
                .getLastAccessTime();
        lastHref2ResourceAccessTime = listener.lastHreftoResourceElementPut
                .getLastAccessTime();

        // let's get some random thing from the colleciton
        calendar = calendarCollection.getCalendarForEventUID(httpClient,
                ICS_ALL_DAY_JAN1_UID);

        // this time, the access times should be the same since we accessed a
        // different resource
        assertEquals(uidToHrefElement.getLastAccessTime(), lastUID2HrefAccessTime);
        assertEquals(hrefToResourceElement.getLastAccessTime(), lastHref2ResourceAccessTime);

    }

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

    private CalDAVCalendarCollection createCalDAVCalendarCollection() {
        CalDAVCalendarCollection calendarCollection = new CalDAVCalendarCollection(
                COLLECTION_PATH, createHostConfiguration(), methodFactory,
                CalDAVConstants.PROC_ID_DEFAULT);
        calendarCollection.setCache(cache);
        return calendarCollection;
    }
}
