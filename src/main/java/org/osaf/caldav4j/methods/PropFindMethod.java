package org.osaf.caldav4j.methods;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.util.CaldavStatus;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;


/**
 *
 */
public class PropFindMethod extends org.apache.jackrabbit.webdav.client.methods.PropFindMethod {
    private static final Log log = LogFactory.getLog(PropFindMethod.class);


    public PropFindMethod(String uri) throws IOException {
        super(uri);
    }

    /**
     * Constructor, which takes in the Properties
     *
     * @param path Path of the principal
     * @param propNameSet Properties to make the Propfind, call for.
     * @param depth Depth of the Propfind Method.
     * @throws IOException
     */
    public PropFindMethod(String path, DavPropertyNameSet propNameSet, int depth) throws IOException {
        super(path, propNameSet, depth);
    }

    /**
     * @param uri Path of the principal
     * @param propfindType Type of Propfind Call. Specified, in DavConstants or CalDavConstants
     * @param propNameSet Properties to make the Propfind, call for.
     * @param depth Depth of the Propfind Method.
     * @throws IOException
     */
    public PropFindMethod(String uri, int propfindType, DavPropertyNameSet propNameSet,
                          int depth) throws IOException {
        super(uri, propfindType, propNameSet, depth);
    }

    /**
     * return the AclProperty relative to a given url
     * @author rpolli
     * @param urlPath
     * @return AclProperty xml response or null if missing
     */
    public AclProperty getAcl(String urlPath) {
        DavProperty p = getDavProperty(urlPath, CalDAVConstants.DNAME_ACL);
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

    public List<AclProperty.Ace> getAces(String urlPath) throws CalDAV4JException {
        if(succeeded()) {
            AclProperty acls = getAcl(urlPath);
            return acls.getValue();
        }
        throw new CalDAV4JException("Error getting ACLs. PROPFIND status is: " + getStatusCode());
    }

    public String getCalendarDescription(String urlPath) {
        DavProperty p =  getDavProperty(urlPath, CalDAVConstants.DNAME_CALENDAR_DESCRIPTION);
        if (p!= null) {
            return p.getValue().toString();
        } else {
            return "";
        }
    }
    public String getDisplayName(String urlPath) {
        DavProperty p= getDavProperty(urlPath, DavPropertyName.DISPLAYNAME);
        if (p != null) {
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
     *
     *
     */
    public DavProperty getDavProperty(String urlPath, DavPropertyName property) {
        try {
            MultiStatusResponse[] responses = getResponseBodyAsMultiStatusResponse();
            if(responses != null && succeeded()) {
                for (MultiStatusResponse r : responses) {
                    if(r.getHref().equals(urlPath)){
                        DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                        return props.get(property);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Unable to get MultiStatusResponse. Status: " + getStatusCode());
        }

        log.warn("Can't find object at: " + urlPath);
        return null;
    }

    /**
     * Returns all the set of properties and their value, for all the hrefs
     * @param property
     * @return
     */
    public DavPropertySet getDavProperties(DavPropertyName property) {
        DavPropertySet set = new DavPropertySet(); //TODO: Use Collection instead of Set?

        try {
            MultiStatusResponse[] responses = getResponseBodyAsMultiStatusResponse();
            if(responses != null && succeeded()) {
                for (MultiStatusResponse r : responses) {
                    DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                    if(!props.isEmpty()) set.add(props.get(property));
                }
            }
        } catch (Exception e) {
            log.warn("Unable to get MultiStatusResponse. Status: " + getStatusCode());
        }

        return set;
    }

    /**
     * Returns the responses as an array of MultiStatusResponses
     * @return MultiStatusResponse[]
     */
    public MultiStatusResponse[] getResponseBodyAsMultiStatusResponse() throws DavException, IOException {
        return getResponseBodyAsMultiStatus().getResponses();
    }

    /**
     * Returns the MultiStatusResponse to the corresponding uri.
     * Note: Can be only used once.
     * @param uri
     * @return
     */
    public MultiStatusResponse getResponseBodyAsMultiStatusResponse(String uri) throws IOException, DavException {
        MultiStatusResponse[] responses = getResponseBodyAsMultiStatusResponse();
        for(MultiStatusResponse response: responses)
            if(response.getHref().equals(uri))
                return response;
        log.warn("No Response found for uri: " + uri);
        return null;
    }
}
