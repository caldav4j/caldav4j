package org.osaf.caldav4j.google;


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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.PortableInterceptor.NON_EXISTENT;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.CalDAVCalendarCollection;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.ResourceNotFoundException;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.util.GenerateQuery;
import org.osaf.caldav4j.util.ICalendarUtils;

public class GCalDAVCalendarCollectionTest extends BaseTestCase {
    private static final Log log = LogFactory
            .getLog(GCalDAVCalendarCollectionTest.class);

    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

    private HttpClient httpClient = createHttpClient();

    public static final String COLLECTION_PATH = CALDAV_SERVER_WEBDAV_ROOT
            + COLLECTION;
    
    public static final Integer TEST_TIMEOUT = 3600;
    public static final boolean TEST_READ = true;
    public static final boolean TEST_WRITE = true;
    public static final Integer TEST_VISITS = CalDAVConstants.INFINITY;
    
    public static final String  TEST_TIMEOUT_UNITS = "Second";
       
    /**
     * put events on calendar
     */
    protected void setUp() throws Exception {
        super.setUp();

//        caldavPut(ICS_GOOGLE_DAILY_NY_5PM_PATH);
//        caldavPut(ICS_GOOGLE_ALL_DAY_JAN1_PATH);
//        caldavPut(ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH);
//        caldavPut(ICS_GOOGLE_FLOATING_JAN2_7PM_PATH);
    }

    /**
     * remove events from calendar
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        
//        caldavDel(ICS_GOOGLE_DAILY_NY_5PM_PATH);
//        caldavDel(ICS_ALL_DAY_JAN1);
//        caldavDel(ICS_NORMAL_PACIFIC_1PM);
//        caldavDel(ICS_SINGLE_EVENT);
    }

    /**
     * put an event on a caldav store using UID.ics
     */
    protected void caldavPut(String s) {
        Calendar cal = getCalendarResource(s);
        put (s,  COLLECTION_PATH + "/" +cal.getComponent("VEVENT").getProperty("UID").getValue() + ".ics");

    }
    
    /**
     * remove an event on a caldav store using UID.ics
     */
    protected void caldavDel(String s) {
        Calendar cal = getCalendarResource(s);
        String delPath = COLLECTION_PATH + "/" +cal.getComponent("VEVENT").getProperty("UID").getValue() + ".ics";
        del(delPath);

    }
    
    /**
     * test: get calendar by UID using REPORT, then checks if SUMMARY matches 
     * @throws Exception
     */
    public void ko_testGetCalendar() throws Exception {
        CalDAVCalendarCollection collection = createCalDAVCalendarCollection();
        Calendar calendar = null;
        try {
        	GenerateQuery gq = new GenerateQuery(null,
        			Component.VEVENT + "UID=="+ICS_DAILY_NY_5PM_UID );

        	System.out.println(gq.prettyPrint());
            calendar = collection.getComponentByQuery(httpClient, 
				            		Component.VEVENT,
				            		gq.generateQuery())
				            			.get(0);
                    
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
        	GenerateQuery gq = new GenerateQuery(null,
        			Component.VEVENT + "UID==NON_EXISTENT_RESOURCE");
            calendar = collection.getComponentByQuery(httpClient, 
            		Component.VEVENT,
            		gq.generateQuery())
            			.get(0);
        } catch (CalDAV4JException ce) {
            calDAV4JException = ce;
        }

        assertNotNull(calDAV4JException);
    }

    
    /**
     * retrieve calendar by /path/to/resource
     * @throws Exception
     */
    public void ok_testGetCalendarByPath() throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        
        System.out.println("GET "+ calendarCollection.getCalendarCollectionRoot()+ICS_DAILY_NY_5PM_UID+".ics");
        Calendar calendar = null;
        try {
            calendar = calendarCollection.getCalendarByPath(httpClient,
                    ICS_DAILY_NY_5PM_UID+".ics");
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

    /**
     * get VEVENT by date TODO
     * @throws Exception
     */
    public void ok_testGetEventResources() throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        Date beginDate = ICalendarUtils.createDateTime(2008, 0, 1, null, true);
        Date endDate = ICalendarUtils.createDateTime(2008, 8, 1, null, true);
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
    public void _donttestGetEventResourcesFloatingIssues() throws Exception {
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

        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        calendarCollection.addEvent(httpClient, ve, null);

        Calendar calendar = calendarCollection.getCalendarByPath(httpClient,
                newUid+".ics");
        assertNotNull(calendar);

        calendarCollection.deleteEvent(httpClient, newUid);
        calendar = null;
        try {
            calendar = calendarCollection.getCalendarByPath(httpClient,
                    newUid+".ics");
        } catch (ResourceNotFoundException e) {

        }
        assertNull(calendar);
    }

    /**
     * @throws Exception
     */
    public void _testUpdateEvent() throws Exception {
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
     * do a calendar-multiget with a valid event and an invalid one
     */
    public void _testMultigetCalendar() throws Exception {
    	CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
    	
    	final String baseUri = BaseTestCase.CALDAV_SERVER_PROTOCOL +"://" 
    			+ BaseTestCase.CALDAV_SERVER_HOST+":" +BaseTestCase.CALDAV_SERVER_PORT 
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
     * @throws Exception
     */
    public void _testTicket() throws Exception {

        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();

        // Create the Ticket
        String ticketID = calendarCollection.createTicket(httpClient,
                BaseTestCase.ICS_DAILY_NY_5PM,
                CalDAVConstants.INFINITY, TEST_TIMEOUT,
                TEST_READ, TEST_WRITE);

        assertNotNull(ticketID);

        // Make sure ticket is there
        
        List<String> ticketIDs = calendarCollection.getTicketsIDs(httpClient,
                BaseTestCase.ICS_DAILY_NY_5PM);

        assertEquals("Number of IDs Returned from getTickets:", ticketIDs
                .size(), 1);

        // Setup a HttpClient with no username or password
        HttpClient badHttpClient = createHttpClientWithNoCredentials();

        // Set the ticket
        badHttpClient.setTicket(ticketID);

        // Attempt to get the Calendar
        Calendar calendar = calendarCollection.getCalendarByPath(badHttpClient,
                ICS_DAILY_NY_5PM);

        assertNotNull(calendar);

        // Attempt to delete the Tickets
        calendarCollection.deleteTicket(httpClient, BaseTestCase.ICS_DAILY_NY_5PM, ticketID);

        // Make sure ticket is gone
        
        CalDAV4JException calDAV4JException = null;
        try {
            calendar = calendarCollection.getCalendarByPath(badHttpClient,
                    ICS_DAILY_NY_5PM);
        } catch (CalDAV4JException ce) {
            calDAV4JException = ce;
        }

        assertNotNull(calDAV4JException);

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
        return calendarCollection;
    }

}
