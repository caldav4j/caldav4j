package org.osaf.caldav4j;

/** 
 * TODO tutti gli attributi, tutti i metodi non specializzati
 * 
 * @author rpolli
 * */
import java.util.Random;

import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.cache.CalDAVResourceCache;
import org.osaf.caldav4j.cache.NoOpResourceCache;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.model.request.PropProperty;

public abstract class CalDAVCalendarCollectionBase {

	public static final PropProperty PROP_ETAG = new PropProperty(
			CalDAVConstants.NS_DAV, "D", CalDAVConstants.PROP_GETETAG);	
	 CalDAV4JMethodFactory methodFactory = null;
	 String calendarCollectionRoot = null;
	 HostConfiguration hostConfiguration = null;
	 String prodId = null;
	 Random random = new Random();
	 CalDAVResourceCache cache = NoOpResourceCache.SINGLETON;

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
	this.methodFactory = methodFactory; }

	/**
	 * remove double slashes
	 * @return
	 */
	public String getCalendarCollectionRoot() {
	    return calendarCollectionRoot.replaceAll("/+", "/");
	}

	/**
	 * set base path removing multiple "/"
	 * @param path
	 */
	public void setCalendarCollectionRoot(String path) {
	    this.calendarCollectionRoot = path.replaceAll("/+", "/");
	}

	public CalDAVResourceCache getCache() {
	    return cache;
	}

	public void setCache(CalDAVResourceCache cache) {
	    this.cache = cache;
	}

	/**
	 * Returns the path relative to the calendars path given an href
	 * 
	 * @param href
	 * @return
	 */
	protected String getRelativePath(String href){
	    int start = href.indexOf(calendarCollectionRoot);
	    return href.substring(start + calendarCollectionRoot.length() + 1);
	}

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

	 String getHref(String path){
	    String href = hostConfiguration.getProtocol().getScheme() + "://"
	    + hostConfiguration.getHost()
	    + (hostConfiguration.getPort() != 80 ? ":" + hostConfiguration.getPort() : "")
	    + ""
	    + path;
	    return href;
	}

	void put(HttpClient httpClient, Calendar calendar, String path,
	        String etag)
	        throws CalDAV4JException {
	    PutMethod putMethod = methodFactory.createPutMethod();
	    putMethod.addEtag(etag);
	    putMethod.setPath(path);
	    putMethod.setIfMatch(true);
	    putMethod.setRequestBody(calendar);
	    try {
	        httpClient.executeMethod(hostConfiguration, putMethod);
	        int statusCode = putMethod.getStatusCode();
	        if (statusCode!= WebdavStatus.SC_NO_CONTENT
	                && statusCode != WebdavStatus.SC_CREATED) {
	            if (statusCode == WebdavStatus.SC_PRECONDITION_FAILED){
	                throw new ResourceOutOfDateException(
	                        "Etag was not matched: "
	                                + etag);
	            }
	        }
	    } catch (Exception e){
	        throw new CalDAV4JException("Problem executing put method",e);
	    }
	
	    Header h = putMethod.getResponseHeader("ETag");
	
	    if (h != null) {
	        String newEtag = h.getValue();
	        cache.putResource(new CalDAVResource(calendar, newEtag, getHref(path)));
	    }
	    
	
	}

}
