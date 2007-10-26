package org.osaf.caldav4j;

import java.util.List;

import net.fortuna.ical4j.model.Calendar;
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
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        del(COLLECTION_PATH);
    }
    
    public void testGetCalendar() throws Exception{
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
    
    public void testGetCalendarByPath() throws Exception{
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
    
    public void testGetEventResources() throws Exception{
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        Date beginDate = ICalendarUtils.createDate(2300, 0, 1, null, true);
        Date endDate = ICalendarUtils.createDate(2300, 0, 9, null, true);
        List l = calendarCollection.getEventResources(beginDate, endDate);
        
    }

    private CalDAVCalendarCollection createCalDAVCalendarCollection(){
        CalDAVCalendarCollection calendarCollection = new CalDAVCalendarCollection(
                COLLECTION_PATH, createHttpClient(), createHostConfiguration(),
                methodFactory);
        return calendarCollection;
    }
}