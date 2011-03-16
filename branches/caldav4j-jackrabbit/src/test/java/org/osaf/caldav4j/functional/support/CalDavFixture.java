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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.osaf.caldav4j.credential.CaldavCredential;
import org.osaf.caldav4j.dialect.CalDavDialect;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.DeleteMethod;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.support.HttpClientTestUtils;
import org.osaf.caldav4j.support.HttpClientTestUtils.HttpMethodCallback;
import org.osaf.caldav4j.util.UrlUtils;

/**
 * Provides fixture support for CalDAV functional tests.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id: CalDavFixture.java 305 2011-02-23 10:59:11Z robipolli@gmail.com $
 */
public class CalDavFixture
{
	// fields -----------------------------------------------------------------
	
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
		MkCalendarMethod method = methodFactory.createMkCalendarMethod(relativePath);

		executeMethod(HttpStatus.SC_CREATED, method, true);
	}
	public void makeCollection(String relativePath) throws IOException
	{
		MkColMethod method = new MkColMethod(UrlUtils.removeDoubleSlashes(relativePath));

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
		DeleteMethod method = new DeleteMethod(relativePath);

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
}
