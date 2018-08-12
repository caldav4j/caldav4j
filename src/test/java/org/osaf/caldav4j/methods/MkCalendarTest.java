package org.osaf.caldav4j.methods;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.jackrabbit.webdav.client.methods.HttpMkcol;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.functional.support.CalDavFixture;
import org.osaf.caldav4j.util.CaldavStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author rpolli
 *
 * TODO This whole class can be fixturized and shortened. The only reason not to do so
 * 				is to keep things simple when testing bare methods.
 */
//@Ignore
public class MkCalendarTest extends BaseTestCase {

	private static final Logger log = LoggerFactory.getLogger(MkCalendarTest.class);

	private List<String> addedItems = new ArrayList<String>();
	@Override
    @Before
	//skip collection creation while initializing
	public void setUp() throws Exception {
		fixture = new CalDavFixture();
		fixture.setUp(caldavCredential, caldavDialect, true);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		log.debug("Removing base collection created during this test.");    	
		for (String p : addedItems) {
			fixture.delete(p, false);			
		}
	}

	/**
	 * this should return something like
	 * @see <a href='http://tools.ietf.org/html/rfc4791#section-5.3.1.2'>RFC4791 Section 5.3.1.2</a>
	 */
	@Test
	public void testPrintMkCalendar() throws IOException {
		HttpMkCalendarMethod mk = new HttpMkCalendarMethod(caldavCredential.home + caldavCredential.collection,
				"My display Name", "this is my default calendar", "en");

		mk.getEntity().writeTo(System.out);

	}

	@Test
	@Ignore
	public void testCreateSubCollection() throws Exception {
		String collectionPath = fixture.getCollectionPath();
		addedItems.add("root1/");

		HttpMkcol mk = new HttpMkcol(collectionPath + "root1/");
		fixture.executeMethod(CaldavStatus.SC_CREATED, mk, true, null, true);

		mk.setURI(URI.create(collectionPath + "root1/sub/"));
		fixture.executeMethod(CaldavStatus.SC_CREATED, mk, false, null, true );
	}

	@Test
	public void testCreateRemoveCalendarCollection() throws Exception{
		String collectionPath = caldavCredential.home + caldavCredential.collection;

		HttpMkCalendarMethod mk = new HttpMkCalendarMethod(collectionPath,
				"My Display Name", "This is my Default Calendar",
				"en");

		HttpClient http = createHttpClient();
		HttpHost hostConfig = createHostConfiguration();
		HttpResponse response = http.execute(hostConfig, mk);

		int statusCode = response.getStatusLine().getStatusCode();
		// whatever successful status code the caldav server returns,
		//   the base collection is created, and should be removed.
		//   TODO CaldavFixture may handle it automagically
		if ((statusCode < 300) && (statusCode >=200)) {
			addedItems.add("");			
		}
		// if resource already exists, remove it on teardown

		switch (statusCode) {
		case CaldavStatus.SC_METHOD_NOT_ALLOWED:
		case CaldavStatus.SC_FORBIDDEN:
			addedItems.add("");			
			break;
		default:
			break;
		}

		/// Test if the caldav server return the right status code
		assertEquals("Status code for mk:", CaldavStatus.SC_CREATED, statusCode);

		//now let's try and get it, make sure it's there
		HttpGetMethod get = fixture.getMethodFactory().createGetMethod(collectionPath);

		statusCode = http.execute(hostConfig, get).getStatusLine().getStatusCode();
		assertEquals("Status code for get:", CaldavStatus.SC_OK, statusCode);

		HttpDeleteMethod delete = new HttpDeleteMethod(collectionPath);

		statusCode = http.execute(hostConfig, delete).getStatusLine().getStatusCode();
		assertEquals("Status code for delete:", CaldavStatus.SC_NO_CONTENT, statusCode);
		addedItems.remove("");

		//Now make sure that it goes away
		get = fixture.getMethodFactory().createGetMethod(collectionPath);
		statusCode = http.execute(hostConfig, get).getStatusLine().getStatusCode();
		assertEquals("Status code for get:", CaldavStatus.SC_NOT_FOUND, statusCode);
	}
}