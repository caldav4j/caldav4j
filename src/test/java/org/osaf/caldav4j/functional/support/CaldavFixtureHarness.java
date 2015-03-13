package org.osaf.caldav4j.functional.support;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.CalDAVCollection;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.TestConstants;
import org.osaf.caldav4j.cache.EhCacheResourceCache;

public class CaldavFixtureHarness implements TestConstants {

	protected static final Log log = LogFactory.getLog(CaldavFixtureHarness.class);
	
	
	public static CalDAVCollection createCollectionFromFixture(CalDavFixture fixture) {
		HostConfiguration conf = (HostConfiguration) fixture.getHttpClient().getHostConfiguration().clone();
		 return new CalDAVCollection(
					fixture.getCollectionPath(),
					conf,
					fixture.getMethodFactory(),
					CalDAVConstants.PROC_ID_DEFAULT
		);
	}
	/**
	 * @param fixture 
	 * 
	 */
	public static void provisionGoogleEvents(CalDavFixture fixture) {
		provisionEvents(fixture,  new String[] {
				ICS_GOOGLE_DAILY_NY_5PM_PATH,
				ICS_GOOGLE_ALL_DAY_JAN1_PATH,
				ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH,
				ICS_GOOGLE_SINGLE_EVENT_PATH
		});
	}

	public static void provisionSimpleEvents(CalDavFixture fixture) {
		provisionEvents(fixture, new String[] {	ICS_DAILY_NY_5PM_PATH,
				ICS_ALL_DAY_JAN1_PATH,
				ICS_NORMAL_PACIFIC_1PM_PATH,
				ICS_SINGLE_EVENT_PATH,ICS_FLOATING_JAN2_7PM_PATH }        	
		);
	}
	
	
	private static void provisionEvents(CalDavFixture fixture, String[] events) {
		for (String eventPath :events) {
			fixture.caldavPut(eventPath);
		}
	}
	/**
	 * 
	 */
	public static EhCacheResourceCache createSimpleCache() {
		//initialize cache
		CacheManager cacheManager = CacheManager.create();
		EhCacheResourceCache myCache = new EhCacheResourceCache();
		Cache uidToHrefCache = new Cache(UID_TO_HREF_CACHE, 1000, false, false,
				600, 300, false, 0);
		Cache hrefToResourceCache = new Cache(HREF_TO_RESOURCE_CACHE, 1000,
				false, false, 600, 300, false, 0);
		myCache.setHrefToResourceCache(hrefToResourceCache);
		myCache.setUidToHrefCache(uidToHrefCache);
		cacheManager.addCache(uidToHrefCache);
		cacheManager.addCache(hrefToResourceCache);
		
		return myCache;
	}
	/**
	 * 
	 */
	public static void removeSimpleCache() {
		CacheManager cacheManager = CacheManager.create();
		cacheManager.removeCache(UID_TO_HREF_CACHE);
		cacheManager.removeCache(HREF_TO_RESOURCE_CACHE);
		cacheManager.shutdown();
	}

}
