package org.osaf.caldav4j.methods;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.functional.support.CaldavFixtureHarness;

public class CalDAVReportMethodTest extends BaseTestCase {

	private static final Log log = LogFactory.getLog(CalDAVReportMethodTest.class);
	// private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();



	@Before
	public void setUp() throws Exception {
		super.setUp();
		CaldavFixtureHarness.provisionSimpleEvents(fixture);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		fixture.tearDown();
	}

	@Test
	public void testCheckTrailingSlash() throws Exception{
		CalDAVReportMethod method = fixture.getMethodFactory().createCalDAVReportMethod();
		assertNotNull(method);

		method.setPath(fixture.getCollectionPath());
		assertNotNull(method.getPath());
		assertTrue(method.getPath().endsWith("/"));

	}


}