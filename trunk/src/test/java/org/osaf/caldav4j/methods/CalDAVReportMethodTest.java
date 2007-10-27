package org.osaf.caldav4j.methods;

import java.util.Enumeration;

import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.ResponseEntity;
import org.apache.webdav.lib.methods.XMLResponseMethodBase;
import org.apache.webdav.lib.properties.LockDiscoveryProperty;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.model.request.CompFilter;
import org.osaf.caldav4j.model.response.CalDAVResponse;

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
        assertTrue(true);
    }


}