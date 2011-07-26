/*
 * Copyright 2011 Open Source Applications Foundation
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
package org.osaf.caldav4j.functional.support;

import static org.osaf.caldav4j.support.HttpMethodCallbacks.nullCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.methods.MkcolMethod;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.TestConstants;
import org.osaf.caldav4j.credential.CaldavCredential;
import org.osaf.caldav4j.dialect.CalDavDialect;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.DeleteMethod;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.support.HttpClientTestUtils;
import org.osaf.caldav4j.support.HttpClientTestUtils.HttpMethodCallback;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.UrlUtils;

/**
 * Provides fixture support for CalDAV functional tests.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class CalDavFixture
{
	// fields -----------------------------------------------------------------
    protected static final Log log = LogFactory.getLog(CalDavFixture.class);
	
	private HttpClient httpClient;
	
	private CalDAV4JMethodFactory methodFactory;
	
	private String collectionPath;
	
	private List<String> deleteOnTearDownPaths;
	
	private CalDavDialect dialect;
	
	// public methods ---------------------------------------------------------
	
	public void setUp(CaldavCredential credential, CalDavDialect dialect) throws IOException
	{
		httpClient = new HttpClient();
		configure(httpClient, credential);
		
		methodFactory = new CalDAV4JMethodFactory();
		collectionPath = UrlUtils.removeDoubleSlashes(credential.home + credential.collection);
		deleteOnTearDownPaths = new ArrayList<String>();
		this.dialect = dialect;

		// eventually make collection
		if (getDialect() != null && getDialect().isCreateCollection()) {
			makeCalendar("");
		}
	}
	
	public void tearDown() throws IOException
	{
		// clean up in reverse order
		
		Collections.reverse(deleteOnTearDownPaths);
		
		for (String path : deleteOnTearDownPaths)
		{
			delete(path);
		}
	}
	
	public void makeCalendar(String relativePath) throws IOException
	{
		/*
		GoogleCalDavDialect gdialect = new GoogleCalDavDialect();
		if (dialect.equals(gdialect.getProdId())) {
			log.warn("Google Caldav Server doesn't support MKCALENDAR");
			return;
		}
		*/
		MkCalendarMethod method = methodFactory.createMkCalendarMethod();
		method.setPath(relativePath);

		executeMethod(HttpStatus.SC_CREATED, method, true);
	}
	public void makeCollection(String relativePath) throws IOException
	{
		MkcolMethod method = new MkcolMethod(UrlUtils.removeDoubleSlashes(relativePath));

		executeMethod(HttpStatus.SC_CREATED, method, true);
	}
	public void putEvent(String relativePath, VEvent event) throws IOException
	{
		PutMethod method = methodFactory.createPutMethod();
		method.setPath(relativePath);
		method.setRequestBody(event);
		
		executeMethod(HttpStatus.SC_CREATED, method, true);
	}
	
	public void delete(String relativePath) throws IOException
	{
		DeleteMethod method = new DeleteMethod();
		method.setPath(relativePath);

		executeMethod(HttpStatus.SC_NO_CONTENT, method, false);
	}
	
	public void executeMethod(int expectedStatus, HttpMethod method, boolean deleteOnTearDown) throws IOException
	{
		executeMethod(expectedStatus, method, deleteOnTearDown, nullCallback());
	}
	
	public <R, M extends HttpMethod, E extends Exception> R executeMethod(int expectedStatus, M method,
		boolean deleteOnTearDown, HttpMethodCallback<R, M, E> methodCallback) throws IOException, E
	{
		String relativePath = method.getPath();
		
		// prefix path with collection path
		method.setPath(collectionPath + relativePath);
		
		R response = HttpClientTestUtils.executeMethod(expectedStatus, httpClient, method, methodCallback);
		
		if (deleteOnTearDown)
		{
			deleteOnTearDownPaths.add(relativePath);
		}
		
		return response;
	}
	
	// private methods --------------------------------------------------------
	
	private static void configure(HttpClient httpClient, CaldavCredential credential)
	{
		httpClient.getHostConfiguration().setHost(credential.host, credential.port, credential.protocol);
		
		Credentials httpCredentials = new UsernamePasswordCredentials(credential.user, credential.password);
		httpClient.getState().setCredentials(AuthScope.ANY, httpCredentials);
		
		httpClient.getParams().setAuthenticationPreemptive(true);
	}

	public void setDialect(CalDavDialect dialect) {
		this.dialect = dialect;
	}

	public CalDavDialect getDialect() {
		return dialect;
	}

	protected void mkcalendar(String path){
	    MkCalendarMethod mk = new MkCalendarMethod();
	    mk.setPath(path);
	    mk.addDescription(TestConstants.CALENDAR_DESCRIPTION, "en");
	    try {
	    	executeMethod(HttpStatus.SC_CREATED,  mk, true);
	    } catch (Exception e){
	        throw new RuntimeException(e);
	    }
	}

	protected void mkcol(String path) {
		MkcolMethod mk = new MkcolMethod(path);
	    try {
	    	executeMethod(HttpStatus.SC_CREATED,  mk, true);
	    } catch (Exception e){
	        throw new RuntimeException(e);
	    }
	
	}

	/***
	 * FIXME this put updates automatically the timestamp of the event 
	 * @param resourceFileName
	 * @param path
	 */
	public void put(String resourceFileName, String path) {    	
	    PutMethod put = methodFactory.createPutMethod();
	    InputStream stream = this.getClass().getClassLoader()
	    .getResourceAsStream(resourceFileName);
	    String event = UrlUtils.parseISToString(stream);
	    event = event.replaceAll("DTSTAMP:.*", "DTSTAMP:" + new DateTime(true).toString());
	    log.debug(new DateTime(true).toString());
	    //log.trace(event);        
	    
	    put.setRequestEntity(event);
	    put.setPath(path);
		log.debug("\nPUT " + put.getPath());
	    try {
	        executeMethod(CaldavStatus.SC_CREATED, put, true);
	        
	        int statusCode =  put.getStatusCode();
	        
	        switch (statusCode) {
			case CaldavStatus.SC_CREATED:
			case CaldavStatus.SC_NO_CONTENT:
				break;
			case CaldavStatus.SC_PRECONDITION_FAILED:
				log.error("item exists?");
				break;
			case CaldavStatus.SC_CONFLICT:
				log.error("conflict: item still on server");
			default:
	            log.error(put.getResponseBodyAsString());
				throw new Exception("trouble executing PUT of " +resourceFileName + "\nresponse:" + put.getResponseBodyAsString());
	
			}
	    } catch (Exception e){
	    	log.info("Error while put():" + e.getMessage());
	        throw new RuntimeException(e);
	    }
	
	}

	/**
	  * remove an event on a caldav store using UID.ics
	 * @throws IOException 
	  */
	 public void caldavDel(String s) throws IOException {
		 Calendar cal = BaseTestCase.getCalendarResource(s);
		 String delPath = collectionPath + "/" +cal.getComponent("VEVENT").getProperty("UID").getValue() + ".ics";
		 log.debug("DEL " + delPath);
		 delete(delPath);
	
	 }

	/**
	 * put an event on a caldav store using UID.ics
	 */
	 public void caldavPut(String s) {    	 
		 Calendar cal = BaseTestCase.getCalendarResource(s);
	
		 String resPath = //collectionPath + "/" +
		 	cal.getComponent("VEVENT").getProperty("UID").getValue() + ".ics";
		 
		 put (s, resPath);
	 }

	public CalDAV4JMethodFactory getMethodFactory() {
		return methodFactory;
	}

	public void setMethodFactory(CalDAV4JMethodFactory methodFactory) {
		this.methodFactory = methodFactory;
	}

	public String getCollectionPath() {
		return collectionPath;
	}

	public void setCollectionPath(String collectionPath) {
		this.collectionPath = collectionPath;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
}
