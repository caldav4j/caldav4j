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

package com.github.caldav4j;

import com.github.caldav4j.cache.CalDAVResourceCache;
import com.github.caldav4j.cache.EhCacheResourceCache;
import com.github.caldav4j.cache.NoOpResourceCache;
import com.github.caldav4j.exceptions.BadStatusException;
import com.github.caldav4j.exceptions.CacheException;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.exceptions.ResourceNotFoundException;
import com.github.caldav4j.methods.DAVMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.methods.HttpGetMethod;
import com.github.caldav4j.model.request.CalDAVReportRequest;
import com.github.caldav4j.model.request.CalendarMultiget;
import com.github.caldav4j.model.request.CalendarQuery;
import com.github.caldav4j.model.response.CalendarDataProperty;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.GenerateQuery;
import com.github.caldav4j.util.MethodUtil;
import com.github.caldav4j.util.UrlUtils;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.utils.URIUtils;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Base class providing basic functionality for the CalDAVCollection
 *
 * @author rpolli
 */
public abstract class CalDAVCalendarCollectionBase<T extends Serializable> {

	private static final Logger log = LoggerFactory.getLogger(CalDAVCollection.class); 

	protected DAVMethodFactory<T> methodFactory = null;
	protected String calendarCollectionRoot = null;
	protected HttpHost httpHost = null;
	protected String prodId = null;
	protected CalDAVResourceCache cache = NoOpResourceCache.getCacheInstance();
	private final ResponseToResource<T> mapper;

	public CalDAVCalendarCollectionBase(ResponseToResource<T> mapper) {
		this.mapper = mapper;
	}
	
	//Configuration Methods

	public HttpHost getDefaultHttpHost(URI path) {
		if(httpHost == null)
			return URIUtils.extractHost(path);

		return httpHost;
	}

	public HttpHost getHttpHost() {
		return httpHost;
	}

	public void setHttpHost(HttpHost httpHost) {
		this.httpHost = httpHost;
	}

	public DAVMethodFactory<T> getMethodFactory() {
		return methodFactory;
	}

	public void setMethodFactory(DAVMethodFactory<T> methodFactory) {
		this.methodFactory = methodFactory;
	}

	/**
	 *
	 * @return CalendarCollectionRoot
	 */
	public String getCalendarCollectionRoot() {
		return calendarCollectionRoot;
	}

	/**
	 * Sets base path. Also set the host, based on if the path was Absolute
	 * @param path Calendar Collection Root
	 */
	public void setCalendarCollectionRoot(String path) {
		URI temp = URI.create(path);
		if(temp.isAbsolute())
			setHttpHost(URIUtils.extractHost(temp));

		this.calendarCollectionRoot = UrlUtils.removeDoubleSlashes(UrlUtils.ensureTrailingSlash(temp.getPath()));
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

	public String getHref(String path) {
		HttpHost httpHost = getDefaultHttpHost(URI.create(calendarCollectionRoot));
		int port = httpHost.getPort();
		String scheme = httpHost.getSchemeName();
		String portString = "";
		if ( (port != 80 && "http".equals(scheme)) ||
				(port != 443 && "https".equals(scheme))
		) {
			portString = ":" + port;
		}

		return UrlUtils.removeDoubleSlashes(
				String.format("%s://%s%s/%s", scheme,
						httpHost.getHostName(),
						portString, path )
		);
	}

	/**
	 * Create cache resources: UID_TO_HREF, HREF_TO_RESOURCE
	 */
	// XXX create test method
	public void enableSimpleCache() {
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
	public void disableSimpleCache() {
		EhCacheResourceCache.removeSimpleCache();
		this.setCache(NoOpResourceCache.getCacheInstance());
	}

	/**
	 * Return a list of caldav resources.
	 * All other methods should use this one
	 * 
	 * The use of caching changes the behavior of this method.
	 * if cache is not enable, returns a list of CalDAVResource parsed from the response
	 * if cache is enabled, foreach   HREF returned by server:
	 *   -  retrieve the resource using getCaldavReource(client, string), this method checks cache
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param query Query to get the CalDAV resources for
	 * @return List of CalDAVResource's
	 * @throws CalDAV4JException on error
	 */
	protected List<CalDAVResource<T>> getCalDAVResources(HttpClient httpClient, CalDAVReportRequest query)
            throws CalDAV4JException {
		boolean usingCache = isCacheEnabled();
		if (usingCache && CalendarQuery.class.isAssignableFrom(query.getClass())) {
			((CalendarQuery)query).setCalendarDataProp(null);
			log.debug("Using cache, so I am removing calendar data");
		}
		log.trace("Executing query: "  + GenerateQuery.printQuery(query));

		HttpCalDAVReportMethod<T> reportMethod = null;

        List<CalDAVResource<T>> list = new ArrayList<>();
		try {
            reportMethod = methodFactory.createCalDAVReportMethod(getCalendarCollectionRoot(),
                    query, CalDAVConstants.DEPTH_1);
			HttpResponse httpResponse = httpClient.execute(getDefaultHttpHost(reportMethod.getURI()), reportMethod);

            log.trace("Parsing response.. " );

            MultiStatusResponse[] responses = reportMethod.getResponseBodyAsMultiStatus(httpResponse).getResponses();
            for(MultiStatusResponse response: responses){
                String etag = CalendarDataProperty.getEtagfromResponse(response);

                if (usingCache) {
                    CalDAVResource<T> resource = getCalDAVResource(httpClient,
                            UrlUtils.stripHost(response.getHref()), etag);
                    list.add(resource);
                    cache.putResource(resource);
                } else {
                    if (response != null) {
                        list.add(mapper.toResource(response));
                    }
                }
            }

		} catch (ConnectException connEx) {
			throw new CalDAV4JException("Can't connecto to "+
					getDefaultHttpHost(reportMethod.getURI()), connEx.getCause());
		} catch (Exception he) {
			throw new CalDAV4JException("Problem executing method", he);
		} finally {
            if(reportMethod != null)
                reportMethod.reset();
        }

		return list;
	}

	/**
	 * GET the resource at the given path. Will check the cache first, and compare that to the
	 * latest etag obtained using a HEAD request.
	 * 
	 * if calendar resource in cache is void, retrieve directly from server (avoid get etag only)
	 * @param httpClient the httpClient which will make the request
	 * @param path to resource
	 * @return CalDAVResource
	 * @throws CalDAV4JException on error
	 */
	//FIXME testme
	protected CalDAVResource<T> getCalDAVResource(HttpClient httpClient,
			String path) throws CalDAV4JException {
		CalDAVResource<T> calDAVResource = cache.getResource(getHref(path));
		if (calDAVResource == null || calDAVResource.getPayload() == null) {
			return getCalDAVResourceFromServer(httpClient, path);
		} else {
			String currentEtag = getETag(httpClient, path);
			return getCalDAVResource(httpClient, path, currentEtag);
		}
	}

	/**
	 * Gets the resource for the given href. Will check the cache first, and if a cached
	 * version exists that has the etag provided it will be returned. Otherwise, it goes
	 * to the server for the resource.
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param path Path to Resource
	 * @param currentEtag Current Etag of the resource
	 * @return Corresponding CalDAVResource
	 * @throws CalDAV4JException on error
	 */
	protected CalDAVResource<T> getCalDAVResource(HttpClient httpClient,
			String path, String currentEtag) throws CalDAV4JException {

		//first try getting from the cache
		CalDAVResource<T> calDAVResource = cache.getResource(getHref(path));

		//ok, so we got the resource...but has it been changed recently?
		if (calDAVResource != null 
				&& calDAVResource.getPayload() != null) { // FIXME calDAVResource's calendar should not be null!
			String cachedEtag = calDAVResource.getResourceMetadata().getETag();
			if (cachedEtag.equals(currentEtag)){
				return calDAVResource;
			}
		}

		//either the etag was old, or it wasn't in the cache so let's get it
		//from the server       
		return getCalDAVResourceFromServer(httpClient, path);

	}

	/**
	 * Gets a CalDAVResource (not a mere timezone) from the server - in other words DOES NOT check the cache.
	 * Adds the new resource to the cache, replacing any pre-existing version.
	 * On Google Caldav Server, this method skips VTIMEZONE resources as they are used as tombstones 
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param path path to resource
	 * @return CalDAVResource
	 * @throws CalDAV4JException on error
	 */
	protected CalDAVResource<T> getCalDAVResourceFromServer(HttpClient httpClient,
			String path) throws CalDAV4JException {
		CalDAVResource<T> calDAVResource = null;
		HttpGetMethod<T> getMethod = getMethodFactory().createGetMethod(path);
		try {
			HttpResponse response = httpClient.execute(getDefaultHttpHost(getMethod.getURI()), getMethod);

            if (response.getStatusLine().getStatusCode() != CalDAVStatus.SC_OK){
                MethodUtil.StatusToExceptions(getMethod, response);
                throw new BadStatusException(getMethod, response);
            }

            String href = getHref(path);
			String etag = response.getFirstHeader(CalDAVConstants.HEADER_ETAG).getValue();
			
			T calendar = getMethod.getResponseBodyAsCalendar(response);

            calDAVResource = new CalDAVResource<T>();
            calDAVResource.setPayload(calendar);
            calDAVResource.getResourceMetadata().setETag(etag);
            calDAVResource.getResourceMetadata().setHref(href);

            cache.putResource(calDAVResource);
		} catch (BadStatusException e){
            throw e;
        }
        catch (Exception e){
			throw new CalDAV4JException("Problem executing get method",e);
		} finally {
            getMethod.reset();
        }

		return calDAVResource;			
	}

	/**
	 * Retrieve etags using HEAD /path/to/resource.ics
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param path Path to the Calendar
	 * @return ETag for calendar
	 * @throws CalDAV4JException on error
	 */
	protected String getETag(HttpClient httpClient, String path) throws CalDAV4JException{
		HttpHead headMethod = new HttpHead(path);

		try {
			HttpResponse response = httpClient.execute(getDefaultHttpHost(headMethod.getURI()), headMethod);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == CalDAVStatus.SC_NOT_FOUND) {
				throw new ResourceNotFoundException(
						ResourceNotFoundException.IdentifierType.PATH, path);
			}

			if (statusCode != CalDAVStatus.SC_OK){
				throw new BadStatusException(headMethod, response);
			}
		} catch (IOException e) {
			throw new CalDAV4JException("Problem executing HEAD method on: " + getDefaultHttpHost(headMethod.getURI()), e);
		}

		Header h = headMethod.getFirstHeader(CalDAVConstants.HEADER_ETAG);
		String etag = null;
		if (h != null) {
			etag = h.getValue();
		} else {
			etag = getETagbyMultiget(httpClient, path);
		}
		return etag;
	}

	/**
	 * Retrieves the Etag of the resource pointed by <code>path</code> by using a Multiget Query.
	 *
	 * @param httpClient Client which makes the request.
	 * @param path Path to the Calendar Resource
	 * @return ETag Value of the Resource
	 * @throws CalDAV4JException on error
     */
	protected String getETagbyMultiget(HttpClient httpClient, String path) throws CalDAV4JException {
		String etag = null;
		DavPropertyNameSet props = new DavPropertyNameSet();
		props.add(DavPropertyName.GETETAG);
		CalendarMultiget query = new CalendarMultiget(props, null, false, false);
		query.addHref(path);

		MultiStatus multiStatus = getMultiStatusforQuery(httpClient, query);
		for(MultiStatusResponse response : multiStatus.getResponses()){
			if(response.getStatus()[0].getStatusCode() == CalDAVStatus.SC_OK){
				etag = CalendarDataProperty.getEtagfromResponse(response);
			}
		}

		return etag;
	}

    /**
     * Get Responses for a specific ReportMethod Query
     * @param httpClient Client which makes the request.
     * @param query Query for the Report Method to execute.
     * @return MultiStatus Response for the Query
     * @throws CalDAV4JException on error
     */
	public MultiStatus getMultiStatusforQuery(HttpClient httpClient, CalDAVReportRequest query) throws CalDAV4JException {

		HttpCalDAVReportMethod<T> reportMethod = null;
		try {
            reportMethod = methodFactory.createCalDAVReportMethod(getCalendarCollectionRoot(), query, CalDAVConstants.DEPTH_1);
			HttpResponse response = httpClient.execute(getDefaultHttpHost(reportMethod.getURI()), reportMethod);

            if(reportMethod.succeeded(response))
                return reportMethod.getResponseBodyAsMultiStatus(response);

		} catch (Exception he) {
			throw new CalDAV4JException("Problem executing method", he);
		} finally {
            if(reportMethod != null)
                reportMethod.reset();
        }

		return null;
	}

}