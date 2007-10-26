package org.osaf.caldav4j.methods;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.model.CalendarQuery;
import org.osaf.caldav4j.model.CompFilter;

public class CalDAVReportMethodTest extends BaseTestCase {
    private static final Log log = LogFactory.getLog(CalDAVReportMethodTest.class);
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
    
    public void testReport() throws Exception{
        CalendarQuery calendarQuery = new CalendarQuery("C", "D");
        calendarQuery.setAllProp(true);
        CompFilter compfilter = new CompFilter("C");
        compfilter.setName("VCALENDAR");
        calendarQuery.setCompFilter(compfilter);
        CalDAVReportMethod reportMethod = new CalDAVReportMethod(
                COLLECTION_PATH, calendarQuery);
        
        createHttpClient().executeMethod(createHostConfiguration(), reportMethod);
    }


}