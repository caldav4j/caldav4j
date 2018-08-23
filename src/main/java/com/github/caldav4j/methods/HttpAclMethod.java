package com.github.caldav4j.methods;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.security.AclProperty;

public class HttpAclMethod extends BaseDavRequest {

    public HttpAclMethod(URI uri, AclProperty aclProperty) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(aclProperty));
    }

    public HttpAclMethod(String uri, AclProperty aclProperty) throws IOException {
        this(URI.create(uri), aclProperty);
    }
    
    @Override
    public String getMethod() {
        return DavMethods.METHOD_ACL;
    }
    
    
    @Override
    public boolean succeeded(HttpResponse response) {
    	return response.getStatusLine().getStatusCode() == DavServletResponse.SC_OK;
    }
    

}
