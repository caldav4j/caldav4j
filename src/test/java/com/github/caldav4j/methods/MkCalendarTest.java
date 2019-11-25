/*
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

package com.github.caldav4j.methods;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.jackrabbit.webdav.client.methods.HttpMkcol;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.functional.support.CalDavFixture;
import com.github.caldav4j.util.CalDAVStatus;

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
		fixture.executeMethod(CalDAVStatus.SC_CREATED, mk, true, null, true);

		mk.setURI(URI.create(collectionPath + "root1/sub/"));
		fixture.executeMethod(CalDAVStatus.SC_CREATED, mk, false, null, true);
	}

	@Test
	public void testCreateRemoveCalendarCollection() throws Exception{
		String collectionPath = caldavCredential.home + caldavCredential.collection;

		// Create collection.
		HttpMkCalendarMethod mk = new HttpMkCalendarMethod(collectionPath,
				"My Display Name", "This is my Default Calendar", "en");

		fixture.executeMethod(CalDAVStatus.SC_CREATED, mk, true, null, true);
		
		// Get collection to make sure it's there.
		HttpGetMethod get = fixture.getMethodFactory().createGetMethod(collectionPath);
		fixture.executeMethod(CalDAVStatus.SC_OK, get, false, null, true);

		// Delete collection.
		HttpDeleteMethod delete = new HttpDeleteMethod(collectionPath);
		fixture.executeMethod(CalDAVStatus.SC_NO_CONTENT, delete, false, null, true);

		// Make sure the collection goes away.
		fixture.executeMethod(CalDAVStatus.SC_NOT_FOUND, get, false, null, true);
	}
}