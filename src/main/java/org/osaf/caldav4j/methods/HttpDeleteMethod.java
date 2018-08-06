package org.osaf.caldav4j.methods;

import java.net.URI;

import org.osaf.caldav4j.CalDAVConstants;

public class HttpDeleteMethod extends org.apache.jackrabbit.webdav.client.methods.HttpDelete {

    /**
     * Default Constructor to delete at location specified by uri.
     * @param uri The location to the resource to be deleted.
     */
	public HttpDeleteMethod(URI uri) {
		this(uri, null);
	}

    /**
     * Constructor which takes in ETag along with URI.
     * This is for deleting a resource only with the specific ETag
     * @param uri Location to the resource on the server
     * @param etag ETag of the resource.
     */
    public HttpDeleteMethod(URI uri, String etag){
        super(uri);
        setETag(etag);
    }

	public HttpDeleteMethod(String uri) {
		this(URI.create(uri));
	}

	public HttpDeleteMethod(String uri, String etag) {
		this(URI.create(uri), etag);
	}

	/**
     * Sets the Request ETag for the DELETE method.
     * @param etag
     */
    public void setETag(String etag){
        if(etag != null && !etag.equals(""))
            setHeader(CalDAVConstants.HEADER_IF_MATCH, etag);
    }
	
	
}
