package org.osaf.caldav4j.cache;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.sf.ehcache.*;
import org.osaf.caldav4j.CalDAVResource;
import org.osaf.caldav4j.util.ICalendarUtils;
import org.osaf.caldav4j.util.UrlUtils;

import static org.osaf.caldav4j.util.UrlUtils.stripHost;

/**
 * A simple implementation of the resource cache based on EhCache
 * @author bobbyrullo
 */
public class EhCacheResourceCache implements CalDAVResourceCache {
	private Cache uidToHrefCache = null;
	private Cache hrefToResourceCache = null;

	// cache names
	private static final String HREF_TO_RESOURCE_CACHE = "hrefToResourceCache";
	private static final String UID_TO_HREF_CACHE = "uidToHrefCache";

	private static EhCacheResourceCache cache = null;

	public static EhCacheResourceCache getCacheInstance() throws org.osaf.caldav4j.exceptions.CacheException {
		if(cache == null)
			cache = createSimpleCache();

		return cache;
	}

	public static void destroyCacheInstance() {
		removeSimpleCache();
	}

	private EhCacheResourceCache() {

	}


	/**
	 * Create a Simple/Dummy cache
	 *
	 * @return a simple EhCacheResourceCache
	 * @throws org.osaf.caldav4j.exceptions.CacheException on error
	 */
	private static EhCacheResourceCache createSimpleCache()
			throws org.osaf.caldav4j.exceptions.CacheException {

		CacheManager cacheManager = CacheManager.create();
		EhCacheResourceCache myCache = new EhCacheResourceCache();
		Cache uidToHrefCache = new Cache(UID_TO_HREF_CACHE, 1000, null, false,
				System.getProperty("java.io.tmpdir"), false, 600, 300, false, 0, null);
		Cache hrefToResourceCache = new Cache(HREF_TO_RESOURCE_CACHE, 1000,
				null, false, System.getProperty("java.io.tmpdir"), false, 600, 300, false, 0, null);
		myCache.setHrefToResourceCache(hrefToResourceCache);
		myCache.setUidToHrefCache(uidToHrefCache);
		try {
			cacheManager.addCache(uidToHrefCache);
			cacheManager.addCache(hrefToResourceCache);
		} catch (ObjectExistsException e) {
			throw new org.osaf.caldav4j.exceptions.CacheException("Cache exists", e);
		}
		return myCache;
	}

	/**
	 * Remove simple cache from the manager
	 */
	private static void removeSimpleCache() {
		CacheManager cacheManager = CacheManager.create();
		cacheManager.removeCache(UID_TO_HREF_CACHE);
		cacheManager.removeCache(HREF_TO_RESOURCE_CACHE);
		cacheManager.shutdown();
	}

	public Cache getHrefToResourceCache() {
		return hrefToResourceCache;
	}

	public void setHrefToResourceCache(Cache pathToResourceCache) {
		this.hrefToResourceCache = pathToResourceCache;
	}

	public Cache getUidToHrefCache() {
		return uidToHrefCache;
	}

	public void setUidToHrefCache(Cache uidToPathCache) {
		this.uidToHrefCache = uidToPathCache;
	}

	/**
	 * @see CalDAVResourceCache#getHrefForEventUID(String)
	 */
	public synchronized String getHrefForEventUID(String uid) throws org.osaf.caldav4j.exceptions.CacheException {
		Element e = null;
		try {
			e = uidToHrefCache.get(uid);
		} catch (CacheException ce) {
			throw new org.osaf.caldav4j.exceptions.CacheException("Problem with the uidToHrefCache", ce);
		}

		return e == null ? null : (String) e.getObjectValue();

	}

	/**
	 * @see CalDAVResourceCache#getResource(String)
	 */
	public synchronized CalDAVResource getResource(String href) throws org.osaf.caldav4j.exceptions.CacheException {
		Element e = null;
		try {
			href = UrlUtils.removeDoubleSlashes(href);
			e = hrefToResourceCache.get(href);
			if (e == null) {
				e = hrefToResourceCache.get(stripHost(href));
			}
		} catch (CacheException ce) {
			throw new org.osaf.caldav4j.exceptions.CacheException(
					"Problem with the hrefToResourceCache", ce);
		}

		return e == null ? null : (CalDAVResource) e.getObjectValue();
	}

	/**
	 * put a CalDAVResource in the cache, indexing by uid
	 * <p>
	 * XXX works only with VEVENT
	 *
	 * @see CalDAVResourceCache#putResource(CalDAVResource)
	 */
	public synchronized void putResource(CalDAVResource calDAVResource)
			throws org.osaf.caldav4j.exceptions.CacheException {
		String href = calDAVResource.getResourceMetadata().getHref();
		Element resourceElement = new Element(href, calDAVResource);
		hrefToResourceCache.put(resourceElement);

		String uid = getEventUID(calDAVResource);
		if (uid != null) {
			Element hrefElement = new Element(uid, href);
			uidToHrefCache.put(hrefElement);
		}
	}

	/**
	 * @see CalDAVResourceCache#removeResource(String)
	 */
	public synchronized void removeResource(String href) throws org.osaf.caldav4j.exceptions.CacheException {
		CalDAVResource resource = getResource(href);
		if (resource != null) {
			hrefToResourceCache.remove(href);
			String uid = getEventUID(resource);
			if (uid != null) {
				uidToHrefCache.remove(uid);
			}
		}
	}

	/**
	 * Thread safe retrieval of event ID.
	 */
	private synchronized String getEventUID(CalDAVResource calDAVResource) {
		Calendar calendar = calDAVResource.getCalendar();
		VEvent vevent = ICalendarUtils.getFirstEvent(calendar);
		if (vevent != null) {
			String uid = ICalendarUtils.getUIDValue(vevent);
			return uid;
		}
		return null;
	}
}
