package org.osaf.caldav4j.cache;

import org.osaf.caldav4j.CalDAVResource;

/**
 * Cache which does nothing. But is very fast.
 * 
 * @author bobbyrullo
 */
public class NoOpResourceCache implements CalDAVResourceCache {

	/**
	 * Singleton class.
	 */
	private static NoOpResourceCache SINGLETON = null;

	public static NoOpResourceCache getCacheInstance() {
		if(SINGLETON == null)
			SINGLETON = new NoOpResourceCache();

		return SINGLETON;
	}

	public static void destroyCacheInstance() {
		SINGLETON = null;
	}

	private NoOpResourceCache() {

	}

	/**
	 * @see CalDAVResourceCache#getHrefForEventUID(String)
	 */
	public String getHrefForEventUID(String uid) {
		return null;
	}

	/**
	 * @see CalDAVResourceCache#getResource(String)
	 */
	public CalDAVResource getResource(String href) {
		return null;
	}

	/**
	 * @see CalDAVResourceCache#putResource(CalDAVResource)
	 */
	public void putResource(CalDAVResource calDAVResource) {
	}

	/**
	 * @see CalDAVResourceCache#removeResource(String)
	 */
	public void removeResource(String href) {
	}

}
