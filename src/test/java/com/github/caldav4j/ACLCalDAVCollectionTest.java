/*
 * Copyright © 2018 Ankush Mishra, Roberto Polli
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
import com.github.caldav4j.functional.support.CaldavFixtureHarness;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.util.XMLUtils;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ACLCalDAVCollectionTest extends BaseTestCase {


	protected static final Log log = LogFactory
	.getLog(ACLCalDAVCollectionTest.class);

	@Before
	public void setUp() throws Exception {
		super.setUp();
		CaldavFixtureHarness.provisionGoogleEvents(fixture);
	}

	@After
	public void tearDown() throws Exception {
		fixture.tearDown();
	}


	@Test
	public void getFolderAces() throws CalDAV4JException {
		List<AclProperty.Ace> aces = collection.getAces(fixture.getHttpClient(), null);
		assertNotNull(aces);
		for(AclProperty.Ace ace : aces)
			log.info(XMLUtils.prettyPrint(ace));
	}

	@Test
	public void getResourceAces() throws Exception {		
		List<AclProperty.Ace> aces = collection.getAces(fixture.getHttpClient(), ICS_GOOGLE_DAILY_NY_5PM);
		assertNotNull(aces);		
		log.info(aces);	
	}

	@Test // TODO
	@Ignore
	public void setFolderAces() {

	}

	@Test //t TODO
	@Ignore
	public void updateFolderAces() {

	}
}
