package org.osaf.caldav4j.methods;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.security.AclProperty;

/**  When Jackrabbit moved to HttpClient 4 they did not provide a refactored version of AclMethod 
 *   Reason is not clear. Therefore this straightforward translation of AclMethod has been added 
 *   back in. 
 *   //TODO !A! check whether this is really needed or whether this could be the wrong approach some reason... 
 * */

public class HttpAclMethod extends BaseDavRequest {

    public HttpAclMethod(String uri, AclProperty aclProperty) throws IOException {
        super(URI.create(uri));
        super.setEntity(XmlEntity.create(aclProperty));
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
