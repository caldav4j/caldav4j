package com.github.caldav4j.cache;

import com.github.caldav4j.exceptions.CacheException;

import java.io.Serializable;

import com.github.caldav4j.CalDAVResource;

/**
 * Implementations allow for caching of CalDAVResources based on path, and also for
 * paths based on UID.
 * 
 * @author bobbyrullo
 *
 */
public interface CalDAVResourceCache<T extends Serializable> {

	/**
	 * Returns the cached CalDAVResource for the given href, or null
	 * if none has been cached yet.
	 *
	 * @param href the href to use to lookup the CalDAVResource
	 * @return CalDAVResource referred by the href, null if not found.
	 * @throws CacheException If error is encountered.
	 */
	public CalDAVResource<T> getResource(String href) throws CacheException;

	/**
	 * Adds the given resource to the cache, retrievable its href.
	 * <p>
	 * If there is an event in the enclosed calendar, adds the href to the cache
	 * retrievable by the UID of the event.
	 *
	 * @param calDAVResource the resource to cache
	 * @throws CacheException If error is encountered.
	 */
	public void putResource(CalDAVResource<T> calDAVResource) throws CacheException;

	/**
	 * Returns the href for which the resource with the event with the given
	 * UID is stored.
	 *
	 * @param uid the uid for the resource whose href you want
	 * @return The href defined by the Event with that UID.
	 * @throws CacheException If error is encountered.
	 */
	public String getHrefForEventUID(String uid) throws CacheException;

	/**
	 * Removes a resource from the cache
	 *
	 * @param href the href to use to lookup the CalDAVResource
	 * @throws CacheException If error is encountered.
	 */
	public void removeResource(String href) throws CacheException;

}
