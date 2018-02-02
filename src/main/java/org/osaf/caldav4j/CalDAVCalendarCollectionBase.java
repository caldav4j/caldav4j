/*
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

package org.osaf.caldav4j;

import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.osaf.caldav4j.cache.CalDAVResourceCache;
import org.osaf.caldav4j.cache.EhCacheResourceCache;
import org.osaf.caldav4j.cache.NoOpResourceCache;
import org.osaf.caldav4j.exceptions.BadStatusException;
import org.osaf.caldav4j.exceptions.CacheException;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.OptionsMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.UrlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract Base class providing basic functionality for the CalDAVCollection
 *
 * @author rpolli
 */
//TODO tutti gli attributi, tutti i metodi non specializzati
public abstract class CalDAVCalendarCollectionBase {

	CalDAV4JMethodFactory methodFactory = null;
	private String calendarCollectionRoot = null;
	HostConfiguration hostConfiguration = null;
	String prodId = null;
	Random random = new Random();
	protected CalDAVResourceCache cache = NoOpResourceCache.SINGLETON;

	//Configuration Methods

	public HostConfiguration getHostConfiguration() {
		return hostConfiguration;
	}

	public void setHostConfiguration(HostConfiguration hostConfiguration) {
		this.hostConfiguration = hostConfiguration;
	}

	public CalDAV4JMethodFactory getMethodFactory() {
		return methodFactory;
	}

	public void setMethodFactory(CalDAV4JMethodFactory methodFactory) {
		this.methodFactory = methodFactory;
	}

	/**
	 * Remove double slashes
	 * @return CalendarCollectionRoot
	 */
	public String getCalendarCollectionRoot() {
		return UrlUtils.removeDoubleSlashes(calendarCollectionRoot);
	}

	/**
	 * Set base path, appending trailing "/" and  removing unneeded "/"
	 * @param path Calendar Collection Root
	 */
	public void setCalendarCollectionRoot(String path) {
		this.calendarCollectionRoot = UrlUtils.removeDoubleSlashes(path.concat("/"));
	}

	public CalDAVResourceCache getCache() {
		return cache;
	}

	public void setCache(CalDAVResourceCache cache) {
		this.cache = cache;
	}

	/**
	 * Check if a cache is set
	 * @return true if cache is not NoOpResourceCache
	 */
	public boolean isCacheEnabled() {
		boolean p =  (this.cache instanceof NoOpResourceCache);
		return !p;
	}


	/**
	 * @param href Absolute Path
	 * @return Returns the path relative to the calendars path given an href
	 */
	protected String getRelativePath(String href){
		int start = href.indexOf(calendarCollectionRoot);
		return href.substring(start + calendarCollectionRoot.length() + 1);
	}

	/**
	 * Create a PUT method setting If-None-Match: *
	 * this tag causes PUT fails if a given event exist  
	 * @param resourceName Resource under Collection Root
	 * @param calendar Calendar for the Put Method
	 * @return a PutMethod for creating events
	 */
	PutMethod createPutMethodForNewResource(String resourceName,
	                                        Calendar calendar) {
		PutMethod putMethod = methodFactory.createPutMethod();
		putMethod.setPath(calendarCollectionRoot + "/"
				+ resourceName);
		putMethod.setAllEtags(true);
		putMethod.setIfNoneMatch(true);
		putMethod.setRequestBody(calendar);
		return putMethod;
	}

	/**
	 * TODO check hostConfiguration.getUri()
	 * @param path
	 * @return the URI of the path resource
	 *
	 * XXX maybe it will be faster to write down the whole url including port...
	 */
	String getHref(String path){
		int port = hostConfiguration.getPort();
		String scheme = hostConfiguration.getProtocol().getScheme();
		String portString = "";
		if ( (port != 80 && "http".equals(scheme)) ||
				(port != 443 && "https".equals(scheme))
				) {
			portString = ":" + port;
		}

		return UrlUtils.removeDoubleSlashes(
				String.format("%s://%s%s/%s", scheme,
						hostConfiguration.getHost(),
						portString, path )
		);
	}


	/**
	 * Create cache resources: UID_TO_HREF, HREF_TO_RESOURCE
	 * XXX create test method
	 * @throws CalDAV4JException on error creating Cache
	 */
	public void enableSimpleCache() throws CalDAV4JException {
		EhCacheResourceCache cache = null;
		if (!isCacheEnabled()) {
			try {
				cache = EhCacheResourceCache.createSimpleCache();
			} catch (CacheException e) {
				// avoid error if cache doesn't exist
				e.printStackTrace();
			}
			this.setCache(cache);
		}
	}

	/**
	 * Set cache to NoOpResourceCache
	 *
	 */
	//TODO test it
	public void disableSimpleCache() {
		EhCacheResourceCache.removeSimpleCache();
		this.setCache(NoOpResourceCache.SINGLETON);
	}

	private boolean tolerantParsing = false;


	/**
	 * @return Whether Tolerant Parsing for Calendars is enabled or not.
	 */
	public boolean isTolerantParsing() {
		return tolerantParsing;
	}

	/**
	 * @param tolerantParsing Value used to enable or disable tolerant parsing of Calendars
	 */
	public void setTolerantParsing(boolean tolerantParsing) {
		this.tolerantParsing = tolerantParsing;
	}

	/**
	 * Retrieve OPTIONS for calendarCollectionRoot
	 * @param httpClient Client making the request
	 * @return List of Options Headers returns
	 * @throws CalDAV4JException on error
	 */
	public List<Header> getOptions(HttpClient httpClient)
			throws CalDAV4JException {
		List<Header> hList = new ArrayList<Header>();

		OptionsMethod optMethod = new OptionsMethod(this.calendarCollectionRoot);
		optMethod.setRequestHeader(new Header("Host",
				hostConfiguration.getHost()));

		try {
			httpClient.executeMethod(this.hostConfiguration, optMethod);
		} catch (Exception e) {
			throw new CalDAV4JException("Trouble executing OPTIONS", e);
		}


		int status = optMethod.getStatusCode();
		switch (status) {
			case CaldavStatus.SC_OK:
				break;
			default:
				throw new BadStatusException(status, optMethod.getName(), getCalendarCollectionRoot());
		}

		for (Header h: optMethod.getResponseHeaders()) {
			hList.add(h);
		}

		return hList;
	}

	/**
	 * Check whether server allows the given action
	 *
	 * @param httpClient Client making the request
	 * @param action Action to check for
	 * @param hList Allow Headers list.
	 * @return true if aloowed, false otherwise
	 */
	/*
	 XXX we should implement it as allowed props should be an attribute of this class,
	 set each time the base-path is changed
	 */
	public boolean allows(HttpClient httpClient, String action, List<Header> hList) {
		for (Header h : hList) {
			if ("Allow".equals(h.getName()) && (h.getValue() != null) && h.getValue().contains(action)) {
				return true;
			}
		}
		return false;
	}
}