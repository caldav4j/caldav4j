package org.osaf.caldav4j;

import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.util.ICalendarUtils;

public class CalDAVCalendarCollectionTest extends BaseTestCase {
    private static final Log log = LogFactory.getLog(CalDAVCalendarCollectionTest.class);
    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
    
    
    public static final String COLLECTION      = "collection";
    public static final String COLLECTION_PATH = CALDAV_SERVER_WEBDAV_ROOT
            + COLLECTION;
    
    protected void setUp() throws Exception {
        super.setUp();
        mkdir(COLLECTION_PATH);
        put(ICS_DAILY_NY_5PM, COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
        put(ICS_ALL_DAY_JAN1, COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
        put(ICS_NORMAL_PACIFIC_1PM, COLLECTION_PATH + "/" + ICS_NORMAL_PACIFIC_1PM);
        put(ICS_SINGLE_EVENT, COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);
        put(ICS_FLOATING_JAN2_7PM, COLLECTION_PATH + "/" + ICS_FLOATING_JAN2_7PM);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        del(COLLECTION_PATH);
    }
    
    public void xtestGetCalendar() throws Exception{
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        Calendar calendar = null;
        try {
            calendar = calendarCollection.getCalendar(ICS_DAILY_NY_5PM_UID);
        } catch (CalDAV4JException ce){
            assertNull(ce);
        }
        
        assertNotNull(calendar);
        VEvent vevent = ICalendarUtils.getFirstEvent(calendar);
        assertNotNull(vevent);
        String summary = ICalendarUtils.getSummaryValue(vevent);
        assertEquals(ICS_DAILY_NY_5PM_SUMMARY, summary);
        
        CalDAV4JException calDAV4JException = null;
        try {
            calendar = calendarCollection.getCalendar("NON_EXISTENT_RESOURCE");
        } catch (CalDAV4JException ce){
            calDAV4JException = ce;
        }
        
        assertNotNull(calDAV4JException);
    }
    
    public void xtestGetCalendarByPath() throws Exception{
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        Calendar calendar = null;
        try {
            calendar = calendarCollection.getCalendarByPath(ICS_DAILY_NY_5PM);
        } catch (CalDAV4JException ce){
            assertNull(ce);
        }
        
        assertNotNull(calendar);
        VEvent vevent = ICalendarUtils.getFirstEvent(calendar);
        assertNotNull(vevent);
        String summary = ICalendarUtils.getSummaryValue(vevent);
        assertEquals(ICS_DAILY_NY_5PM_SUMMARY, summary);
        
        CalDAV4JException calDAV4JException = null;
        try {
            calendar = calendarCollection.getCalendarByPath("NON_EXISTENT_RESOURCE");
        } catch (CalDAV4JException ce){
            calDAV4JException = ce;
        }
        
        assertNotNull(calDAV4JException);
    }
    
    public void xtestGetEventResources1() throws Exception{
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        Date beginDate = ICalendarUtils.createDateTime(2006, 0, 1, null, true);
        Date endDate = ICalendarUtils.createDateTime(2006, 0, 9, null, true);
        List<Calendar> l = calendarCollection.getEventResources(beginDate, endDate);
        
        //4 calendars - one for each resource (not including expanded recurrences)
        assertEquals(4, l.size());
        
        for (Calendar calendar : l){
            ComponentList vevents = calendar.getComponents().getComponents(
                    Component.VEVENT);
            VEvent ve = (VEvent) vevents.get(0);
            String uid = ICalendarUtils.getUIDValue(ve);
            int correctNumberOfEvents = -1;
            if (ICS_DAILY_NY_5PM_UID.equals(uid)){
                //one for each day
                correctNumberOfEvents = 8;
            } else if (ICS_ALL_DAY_JAN1_UID.equals(uid)){
                correctNumberOfEvents = 1;
            } else if (ICS_NORMAL_PACIFIC_1PM_UID.equals(uid)){
                correctNumberOfEvents = 1;
            } else if (ICS_FLOATING_JAN2_7PM_UID.equals(uid)){
                correctNumberOfEvents = 1;
            } else {
                fail(uid + " is not the uid of any event that should have been returned");
            }
            
            assertEquals(correctNumberOfEvents, vevents.size());
        }
       
    }

    public void testGetEventResourcesFloatingIssues() throws Exception{
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();

        //make sure our 7pm event gets returned
        Date beginDate = ICalendarUtils.createDateTime(2006, 0, 2, 19, 0, 0, 0,  null, true);
        Date endDate =   ICalendarUtils.createDateTime(2006, 0, 2, 20, 1, 0, 0, null, true);
        List<Calendar> l = calendarCollection.getEventResources(beginDate, endDate);
        assertTrue(hasEventWithUID(l, ICS_FLOATING_JAN2_7PM_UID));

        beginDate = ICalendarUtils.createDateTime(2006, 0, 2, 20, 1, 0, 0,  null, true);
        endDate =   ICalendarUtils.createDateTime(2006, 0, 2, 20, 2, 0, 0, null, true);
        l = calendarCollection.getEventResources(beginDate, endDate);
        assertFalse(hasEventWithUID(l, ICS_FLOATING_JAN2_7PM_UID));

        
    }
    private boolean hasEventWithUID(List<Calendar> cals, String uid){
        for (Calendar cal : cals){
            ComponentList vEvents = cal.getComponents().getComponents(Component.VEVENT);
            VEvent ve = (VEvent) vEvents.get(0);
            String curUid = ICalendarUtils.getUIDValue(ve);
            if (curUid != null && uid.equals(curUid)){
                return true;
            }
        }
        
        return false;
    }
    
    private CalDAVCalendarCollection createCalDAVCalendarCollection(){
        CalDAVCalendarCollection calendarCollection = new CalDAVCalendarCollection(
                COLLECTION_PATH, createHttpClient(), createHostConfiguration(),
                methodFactory);
        return calendarCollection;
    }
}