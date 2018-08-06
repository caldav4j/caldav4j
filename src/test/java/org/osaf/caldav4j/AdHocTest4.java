package org.osaf.caldav4j;

//** !A! Created to run very simple tests on the basic configuration. Avoids the setup method of BaseTestCase  */
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.osaf.caldav4j.credential.CaldavCredential;
import org.osaf.caldav4j.dialect.CalDavDialect;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.GenerateQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

public class AdHocTest4 { 

	static final Logger log = LoggerFactory.getLogger(AdHocTest4.class);
	
	private static final String yahooCollectionString = "/dav/%user%/Calendar/<CalendarName>/"; 
	
    //protected CaldavCredential caldavCredential = new CaldavCredential(System.getProperty("caldav4jUri", null));
	protected CaldavCredential caldavCredential;	

	protected CalDavDialect caldavDialect = new ChandlerCalDavDialect();

	/** This method loads a credential from System.properties; it allows testers to define their own 
	 *  class org.osaf.caldav4j.CredentialLoader which can then load the credentials from a properties 
	 *  file or from somewhere else. Loading from a properties file may not be seen as a good approach by some developers. 
	 *  Therefore the CredentialLoader is only loaded via reflection and not included in the git repository. */
	protected void loadCredential(String key, String collectionString) {
		String uri = System.getProperty(key);
		if (uri == null) {
			try {
				Class<? extends Object> clazz = Class.forName("org.osaf.caldav4j.CredentialLoader");
				Method method = clazz.getMethod("getCredentialUri", String.class);
				uri = (String) method.invoke(null, key);
			} catch (ClassNotFoundException|NoSuchMethodException|InvocationTargetException|IllegalAccessException ex) {
				//This is ok and expected. There is nothing to do. We don't bother about IllegalAccessException
			}
		}
		
		caldavCredential = ( uri != null ? new CaldavCredential(uri) : new CaldavCredential() );
		caldavCredential.setCollectionString(collectionString);
	}
	
	
	@Test
	public void testDisplayConnectionData() {
		loadCredential("caldav.yahoo.1",yahooCollectionString);
		log.trace("credential user {} pw {}", caldavCredential.user, caldavCredential.password);
		log.trace("Credential host: {} port:  {} protocol: {}",caldavCredential.host, caldavCredential.port, caldavCredential.protocol);
		log.trace("Credential collection: {}",caldavCredential.collection);
	}
	
	@Test
	public void testAddEvent4() {
		loadCredential("caldav.yahoo.1",yahooCollectionString);
		HttpClient httpClient = getHttpClient4();
		HttpHost httpHost = new org.apache.http.HttpHost(caldavCredential.host,caldavCredential.port,caldavCredential.protocol); 		
		CalDAVCollection collection = new CalDAVCollection(caldavCredential.collection
				,httpHost
				,new CalDAV4JMethodFactory()
				,CalDAVConstants.PROC_ID_DEFAULT
		);
		
		LocalDate localDate = LocalDate.now().plusDays(10);
		java.util.Date date = java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		
		VEvent event = new VEvent(new Date(date),"cal test"); //all day event
		
		log.debug("event date {}",event.getStartDate());
		
		UidGenerator ug = null;
		try {
			ug = new UidGenerator("3443");
		} catch (SocketException e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		event.getProperties().add(ug.generateUid());
		event.getProperties().add(new Location("Test space"));
		
		
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId(CalDAVConstants.PROC_ID_DEFAULT) );
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
//		calendar.getProperties().add(new Location("Test booth"));
		
		calendar.getComponents().add(event);		
		
		try {		
			collection.add(httpClient, calendar);		
		} catch (CalDAV4JException e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testLoadEvents4() {
		loadCredential("caldav.yahoo.1",yahooCollectionString);
		HttpClient httpClient = getHttpClient4();
		HttpHost httpHost = new org.apache.http.HttpHost(caldavCredential.host,caldavCredential.port,caldavCredential.protocol); 		
		CalDAVCollection collection = new CalDAVCollection(caldavCredential.collection
				,httpHost
				,new CalDAV4JMethodFactory()
				,CalDAVConstants.PROC_ID_DEFAULT
		);
		
		//prepare the collection.
		GenerateQuery gq = new GenerateQuery();
		try {
			gq.setFilter("VEVENT [20180312T000000Z;20180315T000000Z] : STATUS!=CANCELLED");
			
			CalendarQuery calendarQuery = gq.generate();
			List<Calendar> calendars = null; 
			
			int count = 0;
			while(count<1){
				try {
					calendars = collection.queryCalendars(httpClient, calendarQuery);
					break; 
				} catch (Exception e) {
					log.error("Calendar events not retrieved: "+ e.getMessage());
					count++;
				}
			}
			
			assertFalse(calendars.isEmpty());
			assertEquals(1,calendars.size());
			
		} catch (CalDAV4JException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testDeleteManuallyCreatedEvent() {
		loadCredential("caldav.yahoo.1",yahooCollectionString);
		HttpClient httpClient = getHttpClient4();
		HttpHost httpHost = new org.apache.http.HttpHost(caldavCredential.host,caldavCredential.port,caldavCredential.protocol); 		
		CalDAVCollection collection = new CalDAVCollection(caldavCredential.collection
				,httpHost
				,new CalDAV4JMethodFactory()
				,CalDAVConstants.PROC_ID_DEFAULT
		);

		List<Calendar> calendars = null; 		

		//Fetch all events that have been added today
		GenerateQuery gq = new GenerateQuery();
		try {
			LocalDate today = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

			String filter = "VEVENT ["+formatter.format(today.minusDays(1)) + "T000000Z;" 
					+formatter.format(today.plusDays(1))+"T000000Z] : STATUS!=CANCELLED";
			
			log.trace(filter);
			
			gq.setFilter(filter);
			
			CalendarQuery calendarQuery = gq.generate();
			
			int count = 0;
			while(count<2){
				try {
					calendars = collection.queryCalendars(httpClient, calendarQuery);
					break; 
				} catch (Exception e) {
					log.error("Calendar events not retrieved: "+ e.getMessage());
					count++;
				}
			}

			assertNotNull(calendars);
			assertFalse("No events found. Please manually add an event labeled 'deleteme' today",calendars.isEmpty());
		} catch (CalDAV4JException e) {
			fail(e.getMessage());
		}
		
		boolean success = false;
		for (Calendar calendar : calendars) {
			ComponentList<VEvent> componentList = calendar.getComponents().getComponents(Component.VEVENT);
			for (VEvent it: componentList) {
				Summary summary = it.getSummary();
				if (summary != null && "deleteme".equalsIgnoreCase(summary.getValue())) {
					success = true;
					log.trace("found an event");
					try {
						collection.delete(httpClient, Component.VEVENT, it.getUid().getValue());
					} catch (CalDAV4JException e) {
						e.printStackTrace();
						fail(e.getMessage());
					}
					
				}
				break; 
			}
		}
		
		assertTrue("No events found. Please manually add an event labeled 'deleteme' today",success);
		
	}
	
	@Test
	public void testLoadEvents3() {
		loadCredential("caldav.yahoo.1",yahooCollectionString);
		org.osaf.caldav4j.methods.HttpClient httpClient = getHttpClient31();
		CalDAVCollection collection = new CalDAVCollection(caldavCredential.collection
				,httpClient.getHostConfiguration()
				,new CalDAV4JMethodFactory()
				,CalDAVConstants.PROC_ID_DEFAULT
		);
		
		//prepare the collection.
		GenerateQuery gq = new GenerateQuery();
		try {
			gq.setFilter("VEVENT [20180301T000000Z;20180315T000000Z] : STATUS!=CANCELLED");
			
			List<Calendar> calendars = collection.queryCalendars(httpClient,gq.generate());
			
			assertFalse(calendars.isEmpty());
			assertEquals(1,calendars.size());
			
		} catch (CalDAV4JException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test //Works on 2018-03-15
	public void testSimpleGetWithHostConfig4() {
		loadCredential("caldav.yahoo.1",yahooCollectionString);		
		HttpClient httpClient = getHttpClient4();
		HttpHost httpHost = new org.apache.http.HttpHost(caldavCredential.host,caldavCredential.port,caldavCredential.protocol);
		log.trace(httpHost.toURI());
		HttpGet get = new HttpGet("/dav/tact.cal@yahoo.com/Calendar/Tactonom%20Calendar/d97887a7-78ae-4fb3-9266-8b02b8204453.ics");
		try {
			HttpResponse response = httpClient.execute(httpHost,get);
			assertEquals(CaldavStatus.SC_OK, response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test //Works on 2018-03-15
	public void testSimpleGet4() {
		loadCredential("caldav.yahoo.1",yahooCollectionString);		
		HttpClient httpClient = getHttpClient4();
		HttpGet get = new HttpGet("https://calendar.yahoo.com/dav/tact.cal@yahoo.com/Calendar/Tactonom%20Calendar/d97887a7-78ae-4fb3-9266-8b02b8204453.ics");		
		try {
			HttpResponse response = httpClient.execute(get);
			assertEquals(CaldavStatus.SC_OK, response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	/** Instantiate the old http client. */
	protected org.osaf.caldav4j.methods.HttpClient getHttpClient31() { 
		org.osaf.caldav4j.methods.HttpClient httpClient = new org.osaf.caldav4j.methods.HttpClient();
		
		httpClient.getHostConfiguration().setHost(caldavCredential.host, caldavCredential.port, caldavCredential.protocol); 
		//httpClient.getParams().setAuthenticationPreemptive(true);
		
		org.apache.commons.httpclient.Credentials credentials = new org.apache.commons.httpclient.UsernamePasswordCredentials(caldavCredential.user, caldavCredential.password);
		httpClient.getState().setCredentials(new org.apache.commons.httpclient.auth.AuthScope(caldavCredential.host, 443, org.apache.commons.httpclient.auth.AuthScope.ANY_REALM), credentials);
		
		return httpClient; 
	}
	
	/** Use this method to instantiate the new HttpClient4 */
	protected HttpClient getHttpClient4() {
		CredentialsProvider credProv =  new BasicCredentialsProvider();
		credProv.setCredentials(
				new AuthScope(caldavCredential.host, 443), 
				//new AuthScope(caldavCredential.host, 443, AuthScope.ANY_REALM),
			    new UsernamePasswordCredentials(caldavCredential.user, caldavCredential.password)
	    );
		HttpClient client = HttpClientBuilder.create()
				     .setDefaultCredentialsProvider(credProv)
				     //TODO !A! check whether latest HttpClient still has this problem with nonstandard cookies
				     //see: https://stackoverflow.com/questions/36473478/fixing-httpclient-warning-invalid-expires-attribute-using-fluent-api
				     .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())			     
				     .build();
		return client;
	}
	

}
