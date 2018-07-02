package org.osaf.caldav4j.methods;

import java.net.URI;

import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.UrlUtils;

public class HttpDelete  extends org.apache.jackrabbit.webdav.client.methods.HttpDelete {
	
    /**
     * Default Constructor to delete at location specified by uri.
     * @param uri The location to the resource to be deleted.
     */
	public HttpDelete(String uri) {
		super(UrlUtils.removeDoubleSlashes(uri));
	}

    /**
     * Constructor which takes in ETag along with URI.
     * This is for deleting a resource only with the specific ETag
     * @param uri Location to the resource on the server
     * @param etag ETag of the resource.
     */
    public HttpDelete(String uri, String etag){
        super(UrlUtils.removeDoubleSlashes(uri));
        setETag(etag);
    }

    
	/**
	 * @see org.apache.http.client.methods.HttpRequestBase#setURI(java.net.URI)
	 */
	public void setPath(String path) {
    	super.setURI(URI.create(UrlUtils.removeDoubleSlashes(path)));
    }

    /**
     * Sets the Request ETag for the DELETE method.
     * @param etag
     */
    public void setETag(String etag){
        if(etag != null && !etag.equals(""))
            super.setHeader(CalDAVConstants.HEADER_IF_MATCH, etag);
    }
	
	
}
