package org.osaf.caldav4j;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Ace;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.functional.support.CaldavFixtureHarness;

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
		Ace[] aces = collection.getAces(fixture.getHttpClient(), null);
		assertNotNull(aces);		
		log.info(aces);			
	}

	@Test
	public void getResourceAces() throws Exception {		
		Ace[] aces = collection.getAces(fixture.getHttpClient(), ICS_GOOGLE_DAILY_NY_5PM);
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
