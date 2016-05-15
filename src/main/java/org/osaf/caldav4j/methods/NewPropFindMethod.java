package org.osaf.caldav4j.methods;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.CaldavStatus;

import java.io.IOException;


/**
 *
 */
public class NewPropFindMethod extends PropFindMethod {
    private static final Log log = LogFactory.getLog(PropFindMethod.class);
    private MultiStatusResponse[] responses = null;


    public NewPropFindMethod(String uri) throws IOException {
        super(uri);
    }

    public NewPropFindMethod(String path, DavPropertyNameSet propNameSet, int depth) throws IOException {
        super(path, propNameSet, depth);
    }

    public NewPropFindMethod(String uri, int propfindType, DavPropertyNameSet propNameSet,
                             int depth) throws IOException {
        super(uri, propfindType, propNameSet, depth);
    }


//    /**
//     * return the AclProperty relative to a given url
//     * @author rpolli
//     * @param urlPath
//     * @return AclProperty xml response or null if missing
//     */
//    public AclProperty getAcl(String urlPath) {
//        return (AclProperty) getDavProperty(urlPath, DavPropertyName.create("acl", CalDAVConstants.WEBDAV_NS));
//    }
//
//    public Ace[] getAces(String urlPath) throws CalDAV4JException {
//        int status = -1;
//        AclProperty acls = getDavProperty(urlPath, DavPropertyName.create("acl", CalDAVConstants.WEBDAV_NS));
//        if (acls != null) {
//            status = acls.getStatusCode();
//            switch (status) {
//                case CaldavStatus.SC_OK:
//                    return acls.getAces();
//                default:
//                    break;
//            }
//        }
//        throw new CalDAV4JException("Error getting ACLs. PROPFIND status is: " + status);
//    }

    public String getCalendarDescription(String urlPath) {
        DavProperty p =  getDavProperty(urlPath, DavPropertyName.create(CalDAVConstants.CALDAV_CALENDAR_DESCRIPTION, CalDAVConstants.CALDAV_NS));
        if (p!= null) {
            return p.getValue().toString();
        } else {
            return "";
        }
    }
    public String getDisplayName(String urlPath) {
        DavProperty<?> p= getDavProperty(urlPath, DavPropertyName.DISPLAYNAME);
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
    public DavProperty<?> getDavProperty(String urlPath, DavPropertyName property) {
        if(responses != null && succeeded()) {
            for (MultiStatusResponse r : responses) {
                if(r.getHref().equals(urlPath)){
                    DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                    DavProperty<?> t = props.get(property);
                    return t;
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
