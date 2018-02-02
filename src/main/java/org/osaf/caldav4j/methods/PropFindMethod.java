/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osaf.caldav4j.methods;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Implements the PROPFIND method as specified in the RFC 4918, Section 9.1
 * <p>
 * Supported types:
 * <ul>
 *   <li>{@link CalDAVConstants#PROPFIND_ALL_PROP}: all custom properties,
 *   plus the live properties defined in RFC2518/RFC4918
 *   <li>{@link CalDAVConstants#PROPFIND_ALL_PROP_INCLUDE}: same as
 *   {@link CalDAVConstants#PROPFIND_ALL_PROP} plus the properties specified
 *   in <code>propNameSet</code>
 *   <li>{@link CalDAVConstants#PROPFIND_BY_PROPERTY}: just the properties
 *   specified in <code>propNameSet</code>
 *   <li>{@link CalDAVConstants#PROPFIND_PROPERTY_NAMES}: just the property names
 * </ul>
 * <p>
 * This is based on the Jackrabbit's WebDAV implementation of PROPFIND, which has
 * been extended for use with Calendar specific methods.
 * @see org.apache.jackrabbit.webdav.client.methods.PropFindMethod
 * @author Ankush Mishra
 */
public class PropFindMethod extends org.apache.jackrabbit.webdav.client.methods.PropFindMethod {
    private static final Logger log = LoggerFactory.getLogger(PropFindMethod.class);


	/**
	 * Default Constuctor
	 *
	 * @param uri URI to the calendar resource
	 * @throws IOException on error.
	 */
	public PropFindMethod(String uri) throws IOException {
		super(uri);
	}

	/**
	 * Constructor, which takes in the Properties Set, and the depth.
	 *
     * @param path Path of the principal
     * @param propNameSet Properties to make the Propfind, call for.
     * @param depth Depth of the Propfind Method.
     * @throws IOException on error.
     */
    public PropFindMethod(String path, DavPropertyNameSet propNameSet, int depth) throws IOException {
        super(path, propNameSet, depth);
    }

	/**
	 * Constructor which takes in the properties with the type of propfind.
	 * @param uri Path of the principal
	 * @param propfindType Type of Propfind Call. Specified, in DavConstants or CalDavConstants
	 * @param propNameSet Properties to make the Propfind, call for.
	 * @param depth Depth of the Propfind Method.
	 * @throws IOException on error.
	 */
	public PropFindMethod(String uri, int propfindType, DavPropertyNameSet propNameSet,
	                      int depth) throws IOException {
		super(uri, propfindType, propNameSet, depth);
	}

	/**
	 * Return the AclProperty relative to a given url
	 * @author rpolli, Ankush Mishra
	 * @param urlPath Location of the CalendarResource
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

	/**
	 * Return the ACL Ace returned from the PROPFIND call.
	 * @param urlPath URL of the ACL
	 * @return List of {@link AclProperty.Ace}
	 * @throws CalDAV4JException on error retrieving them.
	 */
	public List<AclProperty.Ace> getAces(String urlPath) throws CalDAV4JException {
		if(succeeded()) {
			AclProperty acls = getAcl(urlPath);
			return acls.getValue();
		}
		throw new CalDAV4JException("Error getting ACLs. PROPFIND status is: " + getStatusCode());
	}

	/**
	 * Convenience method to return the Calendar Description from the
	 * @param urlPath Location of the CalendarResource
	 * @return Calendar Description as String
	 */
	public String getCalendarDescription(String urlPath) {
		DavProperty p =  getDavProperty(urlPath, CalDAVConstants.DNAME_CALENDAR_DESCRIPTION);
		if (p!= null && p.getValue() != null) {
			return p.getValue().toString();
		} else {
			return "";
		}
	}

	/**
	 * Convenience method to return the Calendar Display Name.
	 * @param urlPath Location of the CalendarResource
	 * @return Display Name as string
	 */
	public String getDisplayName(String urlPath) {
		DavProperty p= getDavProperty(urlPath, DavPropertyName.DISPLAYNAME);
		if (p != null && p.getValue() != null) {
			return p.getValue().toString();
		} else {
			return "";
		}
	}


	/**
	 * Returns the DavProperty associated with Property Name.
	 * @param urlPath Location of the CalendarResource
     * @param property DavPropertyName of the property whose value is to be returned.
     * @return DavProperty
     *
     *
     */
    public DavProperty getDavProperty(String urlPath, DavPropertyName property) {
        try {
            MultiStatusResponse[] responses = getResponseBodyAsMultiStatus().getResponses();
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
     * @param property DavPropertyName of the property whose value is to be returned.
     * @return Collection of DavProperties associated with PropertyName.
     */
    public Collection<DavProperty> getDavProperties(DavPropertyName property) {
        Collection<DavProperty> set = new ArrayList<DavProperty>();

        try {
            MultiStatusResponse[] responses = getResponseBodyAsMultiStatus().getResponses();
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
     * Returns the MultiStatusResponse to the corresponding uri.
     * @param uri Location of the CalendarResource
     * @return MultiStatus Response retrieved from the HTTP Response.
     * @throws IOException on error parsing xml.
     * @throws DavException on error HTTP status error
     */
    public MultiStatusResponse getResponseBodyAsMultiStatusResponse(String uri) throws IOException, DavException {
        MultiStatusResponse[] responses = getResponseBodyAsMultiStatus().getResponses();
        for(MultiStatusResponse response: responses)
            if(response.getHref().equals(uri))
                return response;
        log.warn("No Response found for uri: " + uri);
        return null;
    }
}
