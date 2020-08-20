package com.github.caldav4j.methods;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.security.AclProperty;

/**
 * Backported AclMethod from Jackrabbit-Webdav
 *
 * @author alexander233
 */
public class HttpAclMethod extends BaseDavRequest {

    /**
     * @param uri URI to the calendar resource
     * @param aclProperty AclProperty for the resource
     * @throws IOException on error
     */
    public HttpAclMethod(URI uri, AclProperty aclProperty) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(aclProperty));
    }

    /**
     * @param uri URI to the calendar resource
     * @param aclProperty AclProperty for the resource
     * @throws IOException on error
     */
    public HttpAclMethod(String uri, AclProperty aclProperty) throws IOException {
        this(URI.create(uri), aclProperty);
    }

    /** {@inheritDoc} */
    @Override
    public String getMethod() {
        return DavMethods.METHOD_ACL;
    }

    /** {@inheritDoc} */
    @Override
    public boolean succeeded(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == DavServletResponse.SC_OK;
    }
}
