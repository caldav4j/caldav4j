/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Bobby Rullo, Roberto Polli
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
package com.github.caldav4j;

import com.github.caldav4j.credential.CaldavCredential;
import com.github.caldav4j.dialect.CalDavDialect;
import com.github.caldav4j.functional.support.CalDavFixture;
import com.github.caldav4j.functional.support.CaldavFixtureHarness;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import com.github.caldav4j.dialect.ChandlerCalDavDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public abstract class BaseTestCase   implements TestConstants {
	protected static final Logger log = LoggerFactory.getLogger(BaseTestCase.class);

protected CaldavCredential caldavCredential = new CaldavCredential(System.getProperty("caldav4jUri", null));

	protected CalDavDialect caldavDialect = new ChandlerCalDavDialect();

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
		return createHttpClient(this.caldavCredential);
    }
	public static HttpClient createHttpClient(CaldavCredential caldavCredential){
		// HttpClient 4 requires a Cred providers, to be added during creation of client
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
				AuthScope.ANY,
				new UsernamePasswordCredentials(caldavCredential.user, caldavCredential.password));

		return HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider).build();
    }
    public static HttpClient createHttpClientWithNoCredentials(){
        return HttpClients.createDefault();
    }

    public HttpHost createHostConfiguration(){
        return createHostConfiguration(this.caldavCredential);
    }
    public static HttpHost createHostConfiguration(CaldavCredential caldavCredential){
        return new HttpHost(caldavCredential.host,caldavCredential.port, caldavCredential.protocol);
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
