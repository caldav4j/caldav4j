package org.osaf.caldav4j;

import java.util.Enumeration;
import java.util.Vector;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.Value;

import org.apache.commons.httpclient.HostConfiguration;
import org.osaf.caldav4j.methods.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.methods.DepthSupport;
import org.apache.webdav.lib.methods.PropFindMethod;
import org.apache.webdav.lib.methods.XMLResponseMethodBase.Response;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;

public class FunTest extends BaseTestCase {
    private static final Log log = LogFactory
            .getLog(FunTest.class);

    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

    public static final String COLLECTION_PATH = CALDAV_SERVER_WEBDAV_ROOT
            + COLLECTION;

    protected void setUp() throws Exception {
        super.setUp();
        try {
        	mkdir(COLLECTION_PATH);
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
        	log.warn("MKCAL not supported?");
		}
        put(ICS_DAILY_NY_5PM, COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
        put(ICS_ALL_DAY_JAN1, COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
        put(ICS_NORMAL_PACIFIC_1PM, COLLECTION_PATH + "/"
                + ICS_NORMAL_PACIFIC_1PM);
        put(ICS_SINGLE_EVENT, COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);
        put(ICS_FLOATING_JAN2_7PM, COLLECTION_PATH + "/"
                + ICS_FLOATING_JAN2_7PM);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        del(COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
        del(COLLECTION_PATH + "/" + ICS_ALL_DAY_JAN1);
        del(COLLECTION_PATH + "/" + ICS_NORMAL_PACIFIC_1PM);
        del(COLLECTION_PATH + "/" + ICS_SINGLE_EVENT);
        del(COLLECTION_PATH + "/" + ICS_FLOATING_JAN2_7PM);
        del(COLLECTION_PATH);
    }

    public void testFun() throws Exception{
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();

        PropFindMethod propFindMethod = new PropFindMethod();
        PropertyName propName = new PropertyName(CalDAVConstants.NS_DAV, "resourcetype");
        propFindMethod.setDepth(DepthSupport.DEPTH_INFINITY);
        propFindMethod.setPath(CALDAV_SERVER_WEBDAV_ROOT);
        propFindMethod.setType(PropFindMethod.BY_NAME);
        Vector v = new Vector();
        v.add(propName);
        propFindMethod.setPropertyNames(v.elements());
        http.executeMethod(hostConfig, propFindMethod);
        Enumeration e = propFindMethod.getResponses();
        while (e.hasMoreElements()){
            Response response = (Response) e.nextElement();
            Enumeration eProp = response.getProperties();
            while (eProp.hasMoreElements()){
                Property property = (Property) eProp.nextElement();
                String nodeName = property.getElement().getNodeName();
                String localName = property.getElement().getLocalName();
                String tagName = property.getElement().getTagName();
                String namespaceURI = property.getElement().getNamespaceURI();
                System.err.println("nodename" + nodeName);
            }
            
        }
    }
    
    private CalDAVCalendarCollection createCalDAVCalendarCollection() {
        CalDAVCalendarCollection calendarCollection = new CalDAVCalendarCollection(
                COLLECTION_PATH,  createHostConfiguration(),
                methodFactory, CalDAVConstants.PROC_ID_DEFAULT);
        return calendarCollection;
    }
    
    public static void main (String args[]){
        try {
            Recur recur  = new Recur("FREQ=HOURLY");
            DateTime startDate = new DateTime("20060101T010000Z");
            DateTime endDate =   new DateTime("20060105T050000Z");
            DateTime baseDate =  new DateTime("20050101T033300");
            DateList dateList 
                = recur.getDates(baseDate, startDate, endDate, Value.DATE_TIME);
            for (int x = 0; x < dateList.size(); x++){
                DateTime d = (DateTime) dateList.get(x);
                System.out.println(d);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    


}
