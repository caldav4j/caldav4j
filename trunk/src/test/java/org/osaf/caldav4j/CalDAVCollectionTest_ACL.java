package org.osaf.caldav4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Ace;

public class CalDAVCollectionTest_ACL extends BaseTestCase {
	public CalDAVCollectionTest_ACL(String method) {
		super(method);
	}

	protected static final Log log = LogFactory
	.getLog(CalDAVCollectionTest_ACL.class);
	protected void setUp() throws Exception {
		super.setUp();

		try {
			mkcalendar(COLLECTION_PATH); 
		} catch (Exception e) {
			e.printStackTrace();
			log.info("MKCOL unsupported?", e);
		}
		caldavPut(ICS_GOOGLE_DAILY_NY_5PM_PATH);
		caldavPut(ICS_GOOGLE_ALL_DAY_JAN1_PATH);
		caldavPut(ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH);
		caldavPut(ICS_GOOGLE_SINGLE_EVENT_PATH);

	}

	protected void tearDown() throws Exception {
		super.tearDown();

		caldavDel(ICS_GOOGLE_DAILY_NY_5PM_PATH);
		caldavDel(ICS_GOOGLE_ALL_DAY_JAN1_PATH);
		caldavDel(ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH);
		caldavDel(ICS_GOOGLE_SINGLE_EVENT_PATH);
		try {
			del(COLLECTION_PATH);
		} catch (Exception e) {
			log.info("DELETE Collection unsupported", e);
		}
	}



	public void testFolder() throws CalDAV4JException {
		CalDAVCollection calendarCollection = createCalDAVCollection();
		Ace[] aces = calendarCollection.getAces(httpClient, null);
		assertNotNull(aces);		
		log.info(aces);			
	}
	
	public void testResource() throws Exception {		
		CalDAVCollection calendarCollection = createCalDAVCollection();		
		Ace[] aces = calendarCollection.getAces(httpClient, ICS_GOOGLE_DAILY_NY_5PM_PATH);
		assertNotNull(aces);		
		log.info(aces);	
	}
	
}
