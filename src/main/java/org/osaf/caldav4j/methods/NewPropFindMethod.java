package org.osaf.caldav4j.methods;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
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
public class NewPropFindMethod extends PropFindMethod {
    private static final Log log = LogFactory.getLog(PropFindMethod.class);
    private MultiStatusResponse[] responses = null;


    public NewPropFindMethod(String uri) throws IOException {
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
    public NewPropFindMethod(String path, DavPropertyNameSet propNameSet, int depth) throws IOException {
        super(path, propNameSet, depth);
    }

    /**
     * @param uri Path of the principal
     * @param propfindType Type of Propfind Call. Specified, in DavConstants or CalDavConstants
     * @param propNameSet Properties to make the Propfind, call for.
     * @param depth Depth of the Propfind Method.
     * @throws IOException
     */
    public NewPropFindMethod(String uri, int propfindType, DavPropertyNameSet propNameSet,
                             int depth) throws IOException {
        super(uri, propfindType, propNameSet, depth);
    }


    /**
     * return the AclProperty relative to a given url
     * @author rpolli
     * @param urlPath
     * @return AclProperty xml response or null if missing
     */
    public AclProperty getAcl(String urlPath) throws ParserConfigurationException, DavException {
        DavProperty p = getDavProperty(urlPath, DavPropertyName.create(CalDAVConstants.DAV_ACL, CalDAVConstants.NAMESPACE_WEBDAV));
        return AclProperty.createFromXml(p.toXml(DomUtil.createDocument()));
    }

    public List<AclProperty.Ace> getAces(String urlPath) throws ParserConfigurationException, DavException, CalDAV4JException {
        if(succeeded()) {
            AclProperty acls = getAcl(urlPath);
            return acls.getValue();
        }
        throw new CalDAV4JException("Error getting ACLs. PROPFIND status is: " + getStatusCode());
    }

    public String getCalendarDescription(String urlPath) {
        DavProperty p =  getDavProperty(urlPath, DavPropertyName.create(CalDAVConstants.CALDAV_CALENDAR_DESCRIPTION, CalDAVConstants.NAMESPACE_CALDAV));
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
        if(responses != null && succeeded()) {
            for (MultiStatusResponse r : responses) {
                if(r.getHref().equals(urlPath)){
                    DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                    return props.get(property);
                }
            }
        }

        log.warn("Can't find object at: " + urlPath);
        return null;
    }

    @Override
    protected void processMultiStatusBody(MultiStatus multiStatus, HttpState httpState, HttpConnection httpConnection) {
        responses = multiStatus.getResponses();
    }
}
