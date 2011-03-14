/**
 * TODO re-implement test using deprecated methods using current methods
 */
package org.osaf.caldav4j;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.Privilege;
import org.apache.webdav.lib.methods.AclMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osaf.caldav4j.cache.EhCacheResourceCache;

public class CalDAVCollectionAceTest extends BaseTestCase {
	public CalDAVCollectionAceTest() {
		super();
	}

	protected static final Log log = LogFactory
	.getLog(CalDAVCollectionAceTest.class);





	// cache
	private static final String HREF_TO_RESOURCE_CACHE = "hrefToResourceCache";
	private static final String UID_TO_HREF_CACHE = "uidToHrefCache";
	private EhCacheResourceCache myCache = null;

	public static final Integer TEST_TIMEOUT = 3600;
	public static final boolean TEST_READ = true;
	public static final boolean TEST_WRITE = true;
	public static final Integer TEST_VISITS = CalDAVConstants.INFINITY;

	public static final String  TEST_TIMEOUT_UNITS = "Second";
	
	@Before
	public void setUp() throws Exception {
		super.setUp();

		try {
			mkcalendar(COLLECTION_PATH); 
		} catch (Exception e) {
			log.info("MKCALENDAR unsupported?", e);
		}
		caldavPut(ICS_GOOGLE_DAILY_NY_5PM_PATH);
		caldavPut(ICS_GOOGLE_ALL_DAY_JAN1_PATH);
		caldavPut(ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH);
		caldavPut(ICS_GOOGLE_SINGLE_EVENT_PATH);

		//initialize cache
		CacheManager cacheManager = CacheManager.create();
		myCache = new EhCacheResourceCache();
		Cache uidToHrefCache = new Cache(UID_TO_HREF_CACHE, 1000, false, false,
				600, 300, false, 0);
		Cache hrefToResourceCache = new Cache(HREF_TO_RESOURCE_CACHE, 1000,
				false, false, 600, 300, false, 0);
		myCache.setHrefToResourceCache(hrefToResourceCache);
		myCache.setUidToHrefCache(uidToHrefCache);
		cacheManager.addCache(uidToHrefCache);
		cacheManager.addCache(hrefToResourceCache);
	}

	@After
	public void tearDown() throws Exception {
		CacheManager cacheManager = CacheManager.create();
		cacheManager.removeCache(UID_TO_HREF_CACHE);
		cacheManager.removeCache(HREF_TO_RESOURCE_CACHE);
		cacheManager.shutdown();

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



	/**
	 * make a OPTIONS  requesto to caldav server
	 * @throws Exception
	 */
	@Test
	public void testGetOptions() throws Exception {
		CalDAVCollection calendarCollection = createCalDAVCollection();

		List<Header> headerList = calendarCollection.getOptions(httpClient);

		for (Header h : headerList) {
			log.info(h.getName() + ":" + h.getValue());
		}

		Privilege privilege = Privilege.WRITE;


		Ace ace = new Ace("principal");
		ace.addPrivilege(privilege);
		AclMethod aclMethod = new AclMethod("path_to_resource");
		aclMethod.addAce(ace);

		if (calendarCollection.allows(httpClient, "MKCOL", headerList)) {
			log.info("MKCOL exists");
		}
		if (calendarCollection.allows(httpClient, "REPORT", headerList)) {
			log.info("REPORT exists");
		}
		if (calendarCollection.allows(httpClient, "NOOP", headerList)) {
			log.info("NOOP exists");
		}
	}


	//
	// private
	//


	protected CalDAVCollection createCalDAVCollectionWithCache() {


		CalDAVCollection calendarCollection = createCalDAVCollection();
		calendarCollection.setCache(myCache);
		return calendarCollection;
	}



}
