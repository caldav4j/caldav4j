package org.osaf.caldav4j.cache;

import static org.osaf.caldav4j.util.UrlUtils.stripHost;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;

import org.osaf.caldav4j.CalDAVResource;
import org.osaf.caldav4j.util.ICalendarUtils;

/**
 * 
 * @author bobbyrullo
 */
public class EhCacheResourceCache implements CalDAVResourceCache {
    private Cache uidToHrefCache = null;
    private Cache hrefToResourceCache = null;
        
    public EhCacheResourceCache(){
        
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

    public synchronized String getHrefForEventUID(String uid) throws org.osaf.caldav4j.CacheException {
        Element e = null;
        try {
            e = uidToHrefCache.get(uid);
        } catch (CacheException ce){
            throw new org.osaf.caldav4j.CacheException("Problem with the uidToHrefCache",ce);
        }
        
        return e == null ? null : (String) e.getValue();
        
    }

    public synchronized CalDAVResource getResource(String href) throws org.osaf.caldav4j.CacheException {
        Element e = null;
        try {
            e = hrefToResourceCache.get(href);
            if (e == null){
                e = hrefToResourceCache.get(stripHost(href));
            }
        } catch (CacheException ce) {
            throw new org.osaf.caldav4j.CacheException(
                    "Problem with the hrefToResourceCache", ce);
        }

        return e == null ? null : (CalDAVResource) e.getValue();
    }

    /**
     * put a CalDAVResource in the cache, indexing by uid
     * 
     * XXX works only with VEVENT
     */
    public synchronized void putResource(CalDAVResource calDAVResource)
            throws org.osaf.caldav4j.CacheException {
        String href = calDAVResource.getResourceMetadata().getHref();
        Element resourceElement = new Element(href, calDAVResource);
        hrefToResourceCache.put(resourceElement);

        String uid = getEventUID(calDAVResource);
        if (uid != null) {
            Element hrefElement = new Element(uid, href);
            uidToHrefCache.put(hrefElement);
        }
    }

    public synchronized void removeResource(String href) throws org.osaf.caldav4j.CacheException {
        CalDAVResource resource = getResource(href);
        if (resource != null){
            hrefToResourceCache.remove(href);
        }
        String uid = getEventUID(resource);
        if (uid != null){
            uidToHrefCache.remove(uid);
        }
    }
    
    private synchronized String getEventUID(CalDAVResource calDAVResource){
        Calendar calendar = calDAVResource.getCalendar();
        VEvent vevent = ICalendarUtils.getFirstEvent(calendar);
        if (vevent != null){
            String uid = ICalendarUtils.getUIDValue(vevent);
            return uid;
        }
        return null;
    }
}
