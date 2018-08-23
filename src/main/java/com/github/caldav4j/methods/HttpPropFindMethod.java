package com.github.caldav4j.methods;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import com.github.caldav4j.exceptions.CalDAV4JException;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.util.CaldavStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpPropFindMethod extends HttpPropfind {
	
	private static final Logger log = LoggerFactory.getLogger(HttpPropFindMethod.class);


	public HttpPropFindMethod(URI uri, int propfindType, DavPropertyNameSet names, int depth) throws IOException {
		super(uri, propfindType, names, depth);
	}

	public HttpPropFindMethod(URI uri, DavPropertyNameSet names, int depth) throws IOException {
		super(uri, names, depth);
	}

	public HttpPropFindMethod(URI uri, int propfindType, int depth) throws IOException {
		super(uri, propfindType, depth);
	}

	/**
     * Constructor, which takes in the Properties
     *
     * @param uri Path of the principal
     * @param names Properties to make the Propfind, call for.
     * @param depth Depth of the Propfind Method.
     * @throws IOException
     */
    public HttpPropFindMethod(String uri, DavPropertyNameSet names, int depth) throws IOException {
        super(uri, names, depth);
    }
    
    /**
     * @param uri Path of the principal
     * @param propfindType Type of Propfind Call. Specified, in DavConstants or CalDavConstants
     * @param propNameSet Properties to make the Propfind, call for.
     * @param depth Depth of the Propfind Method.
     * @throws IOException
     */
    public HttpPropFindMethod(String uri, int propfindType, DavPropertyNameSet propNameSet, int depth) throws IOException {
        super(uri, propfindType, propNameSet, depth);
    }

	public HttpPropFindMethod(String uri, int propfindType, int depth) throws IOException {
		super(uri, propfindType, depth);
	}
    
    /**
     * return the AclProperty relative to a given url
     * @author rpolli, ankushm
     * @param urlPath
     * @return AclProperty xml response or null if missing
     */
    public AclProperty getAcl(HttpResponse httpResponse,String urlPath) {
        DavProperty p = getDavProperty(httpResponse, urlPath, CalDAVConstants.DNAME_ACL);
        if(p != null) {
            try {
                return AclProperty.createFromXml(p.toXml(DomUtil.createDocument()));
            } catch (DavException e) {
                log.warn("Unable to create AclProperty");
            } catch (ParserConfigurationException e) {
                log.warn("Unable to create AclProperty");
            }
        }

        return null;
    }

    public List<AclProperty.Ace> getAces(HttpResponse httpResponse, String urlPath) throws CalDAV4JException {
        if(succeeded(httpResponse)) {
            AclProperty acls = getAcl(httpResponse,urlPath);
            return acls.getValue();
        }
        throw new CalDAV4JException("Error getting ACLs. PROPFIND status is: " + httpResponse.getStatusLine().getStatusCode());
    }

    public String getCalendarDescription(HttpResponse httpResponse, String urlPath) {
        DavProperty p =  getDavProperty(httpResponse, urlPath, CalDAVConstants.DNAME_CALENDAR_DESCRIPTION);
        if (p!= null && p.getValue() != null) {
            return p.getValue().toString();
        } else {
            return "";
        }
    }
    public String getDisplayName(HttpResponse httpResponse, String urlPath) {
        DavProperty p= getDavProperty(httpResponse, urlPath, DavPropertyName.DISPLAYNAME);
        if (p != null && p.getValue() != null) {
            return p.getValue().toString();
        } else {
            return "";
        }
    }


    /**
     *
     * @param urlPath Location of the CalendarResource
     * @param property DavPropertyName of the property whose value is to be returned.
     * @return DavProperty
     */
    public DavProperty getDavProperty(HttpResponse httpResponse, String urlPath, DavPropertyName property) {
        try {
            MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(httpResponse).getResponses();
            if(responses != null && succeeded(httpResponse)) {
                for (MultiStatusResponse r : responses) {
                    if(r.getHref().equals(urlPath)){
                        DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                        return props.get(property);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Unable to get MultiStatusResponse. Status: " + httpResponse.getStatusLine().getStatusCode());
        }

        log.warn("Can't find object at: " + urlPath);
        return null;
    }

    /**
     * Returns all the set of properties and their value, for all the hrefs
     * @param property
     * @return
     */
    public Collection<DavProperty> getDavProperties(HttpResponse httpResponse, DavPropertyName property) {
        Collection<DavProperty> set = new ArrayList<DavProperty>();

        try {
            MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(httpResponse).getResponses();
            if(responses != null && succeeded(httpResponse)) {
                for (MultiStatusResponse r : responses) {
                    DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                    if(!props.isEmpty()) set.add(props.get(property));
                }
            }
        } catch (Exception e) {
            log.warn("Unable to get MultiStatusResponse. Status: " + httpResponse.getStatusLine().getStatusCode());
        }

        return set;
    }

    /**
     * Returns the MultiStatusResponse to the corresponding uri.
     * @param uri
     * @return
     */
    public MultiStatusResponse getResponseBodyAsMultiStatusResponse(HttpResponse httpResponse, String uri) throws IOException, DavException {
        MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(httpResponse).getResponses();
        for(MultiStatusResponse response: responses)
            if(response.getHref().equals(uri))
                return response;
        log.warn("No Response found for uri: " + uri);
        return null;
    }    
    
    
    
    
	
	
}
