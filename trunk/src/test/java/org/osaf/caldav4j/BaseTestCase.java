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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.osaf.caldav4j.credential.CaldavCredential;
import org.osaf.caldav4j.dialect.CalDavDialect;
import org.osaf.caldav4j.functional.support.CalDavFixture;
import org.osaf.caldav4j.functional.support.CaldavFixtureHarness;
import org.osaf.caldav4j.methods.HttpClient;

public abstract class BaseTestCase   implements TestConstants {
    protected static final Log log = LogFactory.getLog(BaseTestCase.class);

    protected CaldavCredential caldavCredential = new CaldavCredential();

	protected CalDavDialect caldavDialect;

	protected CalDavFixture fixture;

	protected CalDAVCollection collection;
	
	protected CalDAVCollection uncachedCollection;

	@Before
	public void setUp() throws Exception {
		fixture = new CalDavFixture();
		fixture.setUp(caldavCredential, caldavDialect);
		
		collection = CaldavFixtureHarness.createCollectionFromFixture(fixture);

    }
    
    @After
    public void tearDown() throws Exception {
    	
    }
    


    // constructor
    public BaseTestCase() {
	}
	public BaseTestCase(CaldavCredential credential, CalDavDialect dialect) {
		this.caldavCredential = credential;
		this.caldavDialect = dialect;	
	}
	public HttpClient createHttpClient(){
        HttpClient http = new HttpClient();

        Credentials credentials = new UsernamePasswordCredentials(caldavCredential.user, 
        		caldavCredential.password);
        http.getState().setCredentials(
        		new AuthScope(this.getCalDAVServerHost(), this.getCalDAVServerPort()),
        		credentials);
        http.getParams().setAuthenticationPreemptive(true);
        return http;
    }
	public static HttpClient createHttpClient(CaldavCredential caldavCredential){
        HttpClient http = new HttpClient();

        Credentials credentials = new UsernamePasswordCredentials(caldavCredential.user, 
        		caldavCredential.password);
        http.getState().setCredentials(
        		new AuthScope(caldavCredential.host, caldavCredential.port),
        		credentials);
        http.getParams().setAuthenticationPreemptive(true);
        return http;
    }
    public static HttpClient createHttpClientWithNoCredentials(){

        HttpClient http = new HttpClient();
        http.getParams().setAuthenticationPreemptive(true);
        return http;
    }
    public HostConfiguration createHostConfiguration(){
        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(getCalDAVServerHost(), getCalDAVServerPort(), getCalDavSeverProtocol());
        return hostConfig;
    }
    public static HostConfiguration createHostConfiguration(CaldavCredential caldavCredential){
        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(caldavCredential.host,caldavCredential.port, caldavCredential.protocol);
        return hostConfig;
    }
    


    // TODO testme
    public static Calendar getCalendarResource(String resourceName) {
        Calendar cal;

        InputStream stream = BaseTestCase.class.getClassLoader()
                .getResourceAsStream(resourceName);
        CalendarBuilder cb = new CalendarBuilder();
        
        try {
            cal = cb.build(stream);
            Assert.assertNotNull("Missing entry " + resourceName, cal);
        } catch (Exception e) {        	
            throw new RuntimeException("Problems opening file:" + resourceName + "\n" + e);
        }
        
        return cal;
    }    
    
    

		// getter+setter
	    public String getCalDAVServerHost() {
	        return caldavCredential.host;
	    }
	    
	    public int getCalDAVServerPort(){
	        return caldavCredential.port;
	    }
	    
	    public String getCalDavSeverProtocol(){
	        return caldavCredential.protocol;
	    }
	    
	    public String getCalDavSeverWebDAVRoot(){
	        return caldavCredential.home;
	    }
	    
	    public String getCalDavSeverUsername(){
	        return caldavCredential.user;
	    }
	    
	    public String getCalDavSeverPassword(){
	        return caldavCredential.password;
	    }
	        
}
