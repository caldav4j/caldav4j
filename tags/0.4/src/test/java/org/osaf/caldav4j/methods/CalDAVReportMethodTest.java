package org.osaf.caldav4j.methods;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.BaseTestCase;

public class CalDAVReportMethodTest extends BaseTestCase {
    private static final Log log = LogFactory.getLog(CalDAVReportMethodTest.class);
    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
    
    
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
        del(COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
        del(COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
        del(COLLECTION_PATH + "/" + ICS_NORMAL_PACIFIC_1PM);
        del(COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);
        del(COLLECTION_PATH);
    }
    
    public void testReport() throws Exception{
        assertTrue(true);
    }


}