/*
 * Copyright 2005 Open Source Applications Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osaf.caldav4j;

import java.io.InputStream;

import junit.framework.TestCase;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.apache.webdav.lib.util.WebdavStatus;
import org.junit.Before;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.util.XMLUtils;
import org.osaf.caldav4j.xml.OutputsDOMBase;
import org.w3c.dom.Document;

public abstract class BaseTestCase  extends TestCase {
    protected static final Log log = LogFactory.getLog(BaseTestCase.class);
    protected HttpClient http;
    protected HttpClient httpClient;

    protected HostConfiguration hostConfig;
    protected CaldavCredential caldavCredential = new CaldavCredential();
    

    @Before
    protected void setUp() throws Exception {
    	super.setUp();
        COLLECTION_PATH = caldavCredential.CALDAV_SERVER_WEBDAV_ROOT
        + caldavCredential.COLLECTION;
        hostConfig = createHostConfiguration();
        http  = createHttpClient();
        httpClient = createHttpClient();
        methodFactory = new CalDAV4JMethodFactory();
    	
    }
    

    public static final String CALDAV_SERVER_BAD_USERNAME = "IDONTEXIST";
    public  String COLLECTION_PATH;

    public static final String ICS_DAILY_NY_5PM = "Daily_NY_5pm.ics";
    public static final String ICS_DAILY_NY_5PM_UID = "DE916949-731D-4DAE-BA93-48A38B2B2030";
    public static final String ICS_DAILY_NY_5PM_SUMMARY = "Daily_NY_5pm";

    public static final String ICS_ALL_DAY_JAN1 = "All_Day_NY_JAN1.ics";
    public static final String ICS_ALL_DAY_JAN1_UID = "C68DADAD-37CE-44F7-8A37-52E1D02E29CA";

    public static final String ICS_NORMAL_PACIFIC_1PM = "Normal_Pacific_1pm.ics";
    public static final String ICS_NORMAL_PACIFIC_1PM_UID = "0F94FE7B-8E01-4B27-835E-CD1431FD6475";
    public static final String ICS_NORMAL_PACIFIC_1PM_SUMMARY = "Normal_Pacific_1pm";

    public static final String ICS_FLOATING_JAN2_7PM = "Floating_Jan_2_7pm.ics";
    public static final String ICS_FLOATING_JAN2_7PM_SUMMARY = "Floating_Jan_2_7pm";
    public static final String ICS_FLOATING_JAN2_7PM_UID = "0870D1E0-B17E-4875-85C5-2ABB02E27609";
    
    // google caldav server requires event file name == UID
    public static final String ICS_GOOGLE_DAILY_NY_5PM_UID = "DE916949-731D-4DAE-BA93-48A38B2B2030";
    public static final String ICS_GOOGLE_DAILY_NY_5PM = ICS_GOOGLE_DAILY_NY_5PM_UID + ".ics";
    public static final String ICS_GOOGLE_DAILY_NY_5PM_SUMMARY = "Daily_NY_5pm";
    public static final String ICS_GOOGLE_DAILY_NY_5PM_PATH = "Google_Daily_NY_5pm"+".ics";
    
    public static final String ICS_GOOGLE_ALL_DAY_JAN1_UID = "C68DADAD-37CE-44F7-8A37-52E1D02E29CA";
    public static final String ICS_GOOGLE_ALL_DAY_JAN1 = ICS_GOOGLE_ALL_DAY_JAN1_UID + ".ics";
    public static final String ICS_GOOGLE_ALL_DAY_JAN1_PATH = "Google_All_Day_NY_JAN1.ics";
    
    public static final String ICS_GOOGLE_NORMAL_PACIFIC_1PM_UID = "0F94FE7B-8E01-4B27-835E-CD1431FD6475";
    public static final String ICS_GOOGLE_NORMAL_PACIFIC_1PM = ICS_GOOGLE_NORMAL_PACIFIC_1PM_UID + ".ics";
    public static final String ICS_GOOGLE_NORMAL_PACIFIC_1PM_SUMMARY = "Normal_Pacific_1pm";
    public static final String ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH = "Google_Normal_Pacific_1pm.ics";
    
    public static final String ICS_GOOGLE_FLOATING_JAN2_7PM_UID = "0870D1E0-B17E-4875-85C5-2ABB02E27609";
    public static final String ICS_GOOGLE_FLOATING_JAN2_7PM_SUMMARY = "Floating_Jan_2_7pm";
    public static final String ICS_GOOGLE_FLOATING_JAN2_7PM = ICS_GOOGLE_FLOATING_JAN2_7PM_UID + ".ics";
    public static final String ICS_GOOGLE_FLOATING_JAN2_7PM_PATH = "Google_Floating_Jan_2_7pm.ics";

    public static final String ICS_GOOGLE_SINGLE_EVENT_PATH = "Google_singleEvent.ics";
    public static final String ICS_GOOGLE_SINGLE_EVENT_UID = "66be2585-327b-4cc1-93a7-d0e6de648183";
    public static final String ICS_GOOGLE_SINGLE_EVENT = "66be2585-327b-4cc1-93a7-d0e6de648183" + ".ics";
    public static final String ICS_SINGLE_EVENT= "singleEvent.ics";

    public static final String CALENDAR_DESCRIPTION = "My sample calendar description";
    
    protected CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
    
    public String getCalDAVServerHost() {
        return caldavCredential.CALDAV_SERVER_HOST;
    }
    
    public int getCalDAVServerPort(){
        return caldavCredential.CALDAV_SERVER_PORT;
    }
    
    public String getCalDavSeverProtocol(){
        return caldavCredential.CALDAV_SERVER_PROTOCOL;
    }
    
    public String getCalDavSeverWebDAVRoot(){
        return caldavCredential.CALDAV_SERVER_WEBDAV_ROOT;
    }
    
    public String getCalDavSeverUsername(){
        return caldavCredential.CALDAV_SERVER_USERNAME;
    }
    
    public String getCalDavSeverPassword(){
        return caldavCredential.CALDAV_SERVER_PASSWORD;
    }
        

    public BaseTestCase(String method) {
    	super(method);
	}
    public BaseTestCase() {
	}

	public HttpClient createHttpClient(){
        HttpClient http = new HttpClient();

        Credentials credentials = new UsernamePasswordCredentials(caldavCredential.CALDAV_SERVER_USERNAME, 
        		caldavCredential.CALDAV_SERVER_PASSWORD);
        http.getState().setCredentials(
        		new AuthScope(this.getCalDAVServerHost(), this.getCalDAVServerPort()),
        		credentials);
        http.getParams().setAuthenticationPreemptive(true);
        return http;
    }
    
    public HttpClient createHttpClientWithNoCredentials(){

        HttpClient http = new HttpClient();
        http.getParams().setAuthenticationPreemptive(true);
        return http;
    }
    
    public HostConfiguration createHostConfiguration(){
        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(getCalDAVServerHost(), getCalDAVServerPort(), getCalDavSeverProtocol());
        return hostConfig;
    }
    
    protected Calendar getCalendarResource(String resourceName) {
        Calendar cal;

        InputStream stream = this.getClass().getClassLoader()
                .getResourceAsStream(resourceName);
        CalendarBuilder cb = new CalendarBuilder();
        
        try {
            cal = cb.build(stream);
        } catch (Exception e) {        	
            throw new RuntimeException("Problems opening file:" + resourceName + "\n" + e);
        }
        
        return cal;
    }    
    
    /***
     * FIXME this put updates automatically the timestamp of the event 
     * @param resourceFileName
     * @param path
     */
    protected void put(String resourceFileName, String path) {    	
        PutMethod put = methodFactory.createPutMethod();
        InputStream stream = this.getClass().getClassLoader()
        .getResourceAsStream(resourceFileName);
        String event = parseISToString(stream);
        event = event.replaceAll("DTSTAMP:.*", "DTSTAMP:" + new DateTime(true).toString());
        log.debug(new DateTime(true).toString());
        //log.trace(event);        
        
        put.setRequestEntity(new StringRequestEntity(event));
        put.setPath(path);
    	log.debug("\nPUT " + put.getPath());
        try {
            http.executeMethod(hostConfig, put);
            
            int statusCode =  put.getStatusCode();
            
            switch (statusCode) {
			case WebdavStatus.SC_CREATED:
				
				break;
			case WebdavStatus.SC_NO_CONTENT:
				break;
			case WebdavStatus.SC_PRECONDITION_FAILED:
				log.error("item exists?");
				break;
			case WebdavStatus.SC_CONFLICT:
				log.error("conflict: item still on server" + put.getResponseBodyAsString());
				break;
			default:
                System.out.println(put.getResponseBodyAsString());
				throw new Exception("trouble executing PUT of " +resourceFileName + "\nresponse:" + put.getResponseBodyAsString());

			}
        } catch (Exception e){
        	log.info("Error while put():" + e.getMessage());
            throw new RuntimeException(e);
        }

    }
    
    protected void del(String path){
        DeleteMethod delete = new DeleteMethod();
        delete.setPath(path.replaceAll("/+", "/"));
        try {
        	http.executeMethod(hostConfig, delete);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    protected void mkcalendar(String path){
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath(path);
        mk.addDescription(CALENDAR_DESCRIPTION, "en");
        try {
        	http.executeMethod(hostConfig, mk);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    public String parseISToString(java.io.InputStream is){
        java.io.DataInputStream din = new java.io.DataInputStream(is);
        StringBuffer sb = new StringBuffer();
        try{
          String line = null;
          while((line=din.readLine()) != null){
            sb.append(line+"\n");
          }
        }catch(Exception ex){
          ex.getMessage();
        }finally{
          try{
            is.close();
          }catch(Exception ex){}
        }
        return sb.toString();
      }


    public String prettyPrint(OutputsDOMBase xml) {
	    try {
	    	Document doc = xml.createNewDocument(XMLUtils
	                .getDOMImplementation());
			return XMLUtils.toPrettyXML(doc);
	    	
	    } catch (DOMValidationException domve) {
	        throw new RuntimeException(domve);
	    } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
		
	}
}
