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
import com.github.caldav4j.exceptions.CacheException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpPutMethod;
import com.github.caldav4j.model.request.CalendarRequest;
import com.github.caldav4j.util.UrlUtils;
import java.net.URI;
import net.fortuna.ical4j.model.Calendar;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;

/**
 * Abstract Base class providing basic functionality for the CalDAVCollection
 *
 * @author rpolli
 */
public abstract class CalDAVCalendarCollectionBase {

    protected CalDAV4JMethodFactory methodFactory = null;
    protected String calendarCollectionRoot = null;
    protected HttpHost httpHost = null;
    protected String prodId = null;
    protected CalDAVResourceCache cache = NoOpResourceCache.getCacheInstance();
    private boolean tolerantParsing = false;

    // Configuration Methods

    public HttpHost getDefaultHttpHost(URI path) {
        if (httpHost == null) return URIUtils.extractHost(path);

        return httpHost;
    }

    public HttpHost getHttpHost() {
        return httpHost;
    }

    public void setHttpHost(HttpHost httpHost) {
        this.httpHost = httpHost;
    }

    public CalDAV4JMethodFactory getMethodFactory() {
        return methodFactory;
    }

    public void setMethodFactory(CalDAV4JMethodFactory methodFactory) {
        this.methodFactory = methodFactory;
    }

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
     * @return CalendarCollectionRoot
     */
    public String getCalendarCollectionRoot() {
        return calendarCollectionRoot;
    }

    /**
     * Sets base path. Also set the host, based on if the path was Absolute
     *
     * @param path Calendar Collection Root
     */
    public void setCalendarCollectionRoot(String path) {
        URI temp = URI.create(path);
        if (temp.isAbsolute()) setHttpHost(URIUtils.extractHost(temp));

        this.calendarCollectionRoot =
                UrlUtils.removeDoubleSlashes(UrlUtils.ensureTrailingSlash(temp.getPath()));
    }

    public CalDAVResourceCache getCache() {
        return cache;
    }

    public void setCache(CalDAVResourceCache cache) {
        this.cache = cache;
    }

    /**
     * Check if a cache is set
     *
     * @return true if cache is not NoOpResourceCache
     */
    public boolean isCacheEnabled() {
        boolean p = (this.cache instanceof NoOpResourceCache);
        return !p;
    }

    public String getHref(String path) {
        HttpHost httpHost = getDefaultHttpHost(URI.create(calendarCollectionRoot));
        int port = httpHost.getPort();
        String scheme = httpHost.getSchemeName();
        String portString = "";
        if ((port != 80 && "http".equals(scheme)) || (port != 443 && "https".equals(scheme))) {
            portString = ":" + port;
        }

        return UrlUtils.removeDoubleSlashes(
                String.format("%s://%s%s/%s", scheme, httpHost.getHostName(), portString, path));
    }

    /**
     * Create a PUT method setting If-None-Match: * this tag causes PUT fails if a given event exist
     *
     * @param resourceName Resource under Collection Root
     * @param calendar Calendar for the Put Method
     * @return a PutMethod for creating events
     */
    HttpPutMethod createPutMethodForNewResource(String resourceName, Calendar calendar) {
        CalendarRequest cr = new CalendarRequest();
        cr.setAllEtags(true);
        cr.setIfNoneMatch(true);
        cr.setCalendar(calendar);
        return methodFactory.createPutMethod(calendarCollectionRoot + resourceName, cr);
    }

    /** Create cache resources: UID_TO_HREF, HREF_TO_RESOURCE */
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

    /** Set cache to NoOpResourceCache */
    public void disableSimpleCache() {
        EhCacheResourceCache.removeSimpleCache();
        this.setCache(NoOpResourceCache.getCacheInstance());
    }
}
