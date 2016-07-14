package org.osaf.caldav4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.functional.support.CaldavFixtureHarness;
import org.osaf.caldav4j.util.XMLUtils;

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
