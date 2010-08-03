package org.osaf.caldav4j.methods;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.ICalendarUtils;

public class PutGetTest extends BaseTestCase {
    public PutGetTest(String method) {
		super(method);
		// TODO Auto-generated constructor stub
	}

	private static final Log log = LogFactory.getLog(PutGetTest.class);
    private ResourceBundle messages;
    


    
    protected void setUp() throws Exception {
        super.setUp();
        mkcalendar(COLLECTION_PATH);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        del(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM);
        del(COLLECTION_PATH);
    }

    public void testAddRemoveCalendarResource() throws Exception{
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();

        Calendar cal = getCalendarResource(BaseTestCase.ICS_DAILY_NY_5PM);
        PutMethod put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        put.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM_SUMMARY+".ics");
        http.executeMethod(hostConfig, put);
        int statusCode = put.getStatusCode();
        assertEquals("Status code for put:", CaldavStatus.SC_CREATED, statusCode);

        //ok, so we created it...let's make sure it's there!
        GetMethod get = methodFactory.createGetMethod();
        get.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM_UID+".ics");
        http.executeMethod(hostConfig, get);
        statusCode = get.getStatusCode();
        assertEquals("Status code for get: ", CaldavStatus.SC_OK, statusCode);
        
        //now let's make sure we can get the resource body as a calendar
        Calendar calendar = get.getResponseBodyAsCalendar();
        VEvent event = ICalendarUtils.getFirstEvent(calendar);
        String uid = ICalendarUtils.getUIDValue(event);
        assertEquals(ICS_DAILY_NY_5PM_UID, uid);
        
        //let's make sure that a subsequent put with "if-none-match: *" fails
        put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        put.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM_UID+".ics");
        http.executeMethod(hostConfig, put);
        statusCode = put.getStatusCode();
        assertEquals("Status code for put:",
                CaldavStatus.SC_PRECONDITION_FAILED, statusCode);
   }
    
    /**
     * TODO test PUT with non-latin characters
     */
    public void testPutNonLatin()
    throws Exception {
    	
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
    	
    	// load an ICS and substitute summary with non-latin chars
    	Locale mylocale = new Locale("ru", "RU");
    	messages = PropertyResourceBundle.getBundle("messages",mylocale);
    	String myLocalSummary = messages.getString("summary"); 
    	log.info("default charser: "+ Charset.defaultCharset());

        Calendar cal = getCalendarResource(BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM_PATH);
        Component calendarComponent =  cal.getComponent(Component.VEVENT);
        ICalendarUtils.addOrReplaceProperty(calendarComponent, 
        		new Summary(myLocalSummary));
        assertEquals(myLocalSummary, 
        		ICalendarUtils.getPropertyValue(calendarComponent, Property.SUMMARY));

    	// create a PUT request with the given ICS
        PutMethod put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        put.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM);
        http.executeMethod(hostConfig, put);
        int statusCode = put.getStatusCode();
        assertEquals("Status code for put:", CaldavStatus.SC_CREATED, statusCode);

        //ok, so we created it...let's make sure it's there!
        GetMethod get = methodFactory.createGetMethod();
        get.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM);
        http.executeMethod(hostConfig, get);
        statusCode = get.getStatusCode();
        assertEquals("Status code for get: ", CaldavStatus.SC_OK, statusCode);
        
        //now let's make sure we can get the resource body as a calendar
        Calendar calendar = get.getResponseBodyAsCalendar();
        VEvent event = ICalendarUtils.getFirstEvent(calendar);
        String uid = ICalendarUtils.getUIDValue(event);
        String summary = ICalendarUtils.getPropertyValue(event, Property.SUMMARY);
        assertEquals(ICS_DAILY_NY_5PM_UID, uid);
        assertEquals(myLocalSummary, summary);

        
        //let's make sure that a subsequent put with "if-none-match: *" fails
        put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        put.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_GOOGLE_DAILY_NY_5PM);
        http.executeMethod(hostConfig, put);
        statusCode = put.getStatusCode();
        assertEquals("Status code for put:",
                CaldavStatus.SC_PRECONDITION_FAILED, statusCode);


        
    	// test for exceptions
    	// moreover: try a GET to see if event is changed
    }

}