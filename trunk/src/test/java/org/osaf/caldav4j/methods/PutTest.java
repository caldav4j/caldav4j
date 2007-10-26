package org.osaf.caldav4j.methods;

import java.io.InputStream;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.BaseTestCase;

public class PutTest extends BaseTestCase {
    private static final Log log = LogFactory.getLog(PutTest.class);
    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
     
    
    public static final String COLLECTION      = "collection";
    public static final String COLLECTION_PATH = CALDAV_SERVER_WEBDAV_ROOT
            + COLLECTION;
    
    protected void setUp() throws Exception {
        super.setUp();
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath(COLLECTION_PATH);
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        http.executeMethod(hostConfig, mk);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        DeleteMethod delete = new DeleteMethod();
        delete.setPath(COLLECTION_PATH);
        http.executeMethod(hostConfig, delete);
    }

    public void testAddRemoveCalendarResource() throws Exception{
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();

        Calendar cal = getCalendarResource("Daily_NY_5pm.ics");
        PutMethod put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        put.setPath(COLLECTION_PATH + "/" + "Daily_NY_5pm.ics");
        http.executeMethod(hostConfig, put);
        int statusCode = put.getStatusCode();
        assertEquals("Status code for put:", WebdavStatus.SC_CREATED, statusCode);

        //ok, so we created it...let's make sure it's there!
        GetMethod get = new GetMethod();
        get.setPath(COLLECTION_PATH + "/" + "Daily_NY_5pm.ics");
        http.executeMethod(hostConfig, get);
        statusCode = get.getStatusCode();
        assertEquals("Status code for get: ", WebdavStatus.SC_OK, statusCode);
        
        //let's make sure that a subsequent put with "if-none-match: *" fails
        put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        put.setPath(COLLECTION_PATH + "/" + "Daily_NY_5pm.ics");
        http.executeMethod(hostConfig, put);
        statusCode = put.getStatusCode();
        assertEquals("Status code for put:",
                WebdavStatus.SC_PRECONDITION_FAILED, statusCode);
   }
    
    private Calendar getCalendarResource(String resourceName) {
        Calendar cal;

        InputStream stream = this.getClass().getClassLoader()
                .getResourceAsStream(resourceName);
        CalendarBuilder cb = new CalendarBuilder();
        
        try {
            cal = cb.build(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return cal;
    }
}