package org.osaf.caldav4j;

/**
 * 
 * @author bobbyrullo
 */
public class EhCacheResourceCache implements CalDAVResourceCache {
 
    public EhCacheResourceCache(){
        
    }
    
    public String getHrefForEventUID(String uid) {
        return null;
    }

    public CalDAVResource getResource(String href) {
        return null;
    }

    public void putResource(CalDAVResource calDAVResource) {
    }

    public void removeResource(String href) {
    }

}
