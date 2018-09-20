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
import com.github.caldav4j.util.CalDAVStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an HTTP PROPFIND request. Some of the options can be found in {@link CalDAVConstants}
 *
 * @see <a href="http://webdav.org/specs/rfc4918.html#rfc.section.9.1">RFC 4918, Section 9.1</a>
 */
public class HttpPropFindMethod extends HttpPropfind {

	private static final Logger log = LoggerFactory.getLogger(HttpPropFindMethod.class);

	/**
	 * @param uri          Path of the principal
	 * @param propfindType Type of Propfind Call. Specified, in CalDavConstants.
	 *                     Specifically, {@code CalDavConstants.PROPFIND_BY_PROPERTY},
	 *                     {@code CalDavConstants.PROPFIND_ALL_PROP},
	 *                     {@code CalDavConstants.PROPFIND_PROPERTY_NAMES},
	 *                     {@code CalDavConstants.PROPFIND_ALL_PROP_INCLUDE}
	 *                     More info, in <a href="http://webdav.org/specs/rfc4918.html#rfc.section.9.1">RFC 4918 Section 9.1</a>
	 * @param names  Properties to make the Propfind, call for.
	 * @param depth        Depth of the Propfind Method.
	 * @throws IOException on error
	 */
	public HttpPropFindMethod(URI uri, int propfindType, DavPropertyNameSet names, int depth) throws IOException {
		super(uri, propfindType, names, depth);
	}

	/**
	 * Convenience constructor with Propfind Type as {@code PROPFIND_BY_PROPERTY}
	 * @param uri          Path of the principal
	 * @param names  Properties to make the Propfind, call for.
	 * @param depth        Depth of the Propfind Method.
	 * @throws IOException on error
	 */
	public HttpPropFindMethod(URI uri, DavPropertyNameSet names, int depth) throws IOException {
		super(uri, names, depth);
	}

	/**
	 * Convenience constructor with empty property names
	 * @param uri          Path of the principal
	 * @param depth        Depth of the Propfind Method.
	 * @throws IOException on error
	 */
	public HttpPropFindMethod(URI uri, int propfindType, int depth) throws IOException {
		super(uri, propfindType, depth);
	}

	/**
	 * Constructor, which takes in the Properties
	 *
	 * @param uri   Path of the principal
	 * @param names Properties to make the Propfind, call for.
	 * @param depth Depth of the Propfind Method.
	 * @throws IOException on error
	 */
	public HttpPropFindMethod(String uri, DavPropertyNameSet names, int depth) throws IOException {
		super(uri, names, depth);
	}

	/**
	 * @param uri          Path of the principal
	 * @param propfindType Type of Propfind Call. Specified, in DavConstants or CalDavConstants
	 * @param propNameSet  Properties to make the Propfind, call for.
	 * @param depth        Depth of the Propfind Method.
	 * @throws IOException on error
	 */
	public HttpPropFindMethod(String uri, int propfindType, DavPropertyNameSet propNameSet, int depth) throws IOException {
		super(uri, propfindType, propNameSet, depth);
	}

	public HttpPropFindMethod(String uri, int propfindType, int depth) throws IOException {
		super(uri, propfindType, depth);
	}

	/**
	 * return the AclProperty relative to a given url
	 *
	 * @param urlPath
	 * @return AclProperty xml response or null if missing
	 * @author rpolli, ankushm
	 */
	public AclProperty getAcl(HttpResponse httpResponse, String urlPath) {
		DavProperty p = getDavProperty(httpResponse, urlPath, CalDAVConstants.DNAME_ACL);
		if (p != null) {
			try {
				return AclProperty.createFromXml(p.toXml(DomUtil.createDocument()));
			} catch (DavException | ParserConfigurationException e) {
				log.warn("Unable to create AclProperty");
			}
		}

		return null;
	}

	public List<AclProperty.Ace> getAces(HttpResponse httpResponse, String urlPath) throws CalDAV4JException {
		if (succeeded(httpResponse)) {
			AclProperty acls = getAcl(httpResponse, urlPath);
			return acls.getValue();
		}
		throw new CalDAV4JException("Error getting ACLs. PROPFIND status is: " + httpResponse.getStatusLine().getStatusCode());
	}

	public String getCalendarDescription(HttpResponse httpResponse, String urlPath) {
		DavProperty p = getDavProperty(httpResponse, urlPath, CalDAVConstants.DNAME_CALENDAR_DESCRIPTION);
		if (p != null && p.getValue() != null) {
			return p.getValue().toString();
		} else {
			return "";
		}
	}

	public String getDisplayName(HttpResponse httpResponse, String urlPath) {
		DavProperty p = getDavProperty(httpResponse, urlPath, DavPropertyName.DISPLAYNAME);
		if (p != null && p.getValue() != null) {
			return p.getValue().toString();
		} else {
			return "";
		}
	}


	/**
	 * @param urlPath  Location of the CalendarResource
	 * @param property DavPropertyName of the property whose value is to be returned.
	 * @return DavProperty
	 */
	public DavProperty getDavProperty(HttpResponse httpResponse, String urlPath, DavPropertyName property) {
		try {
			MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(httpResponse).getResponses();
			if (responses != null && succeeded(httpResponse)) {
				for (MultiStatusResponse r : responses) {
					if (r.getHref().equals(urlPath)) {
						DavPropertySet props = r.getProperties(CalDAVStatus.SC_OK);
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
	 * @param property Property to retrieve
	 * @return Returns all the set of properties and their value, for all the hrefs
	 */
	public Collection<DavProperty> getDavProperties(HttpResponse httpResponse, DavPropertyName property) {
		Collection<DavProperty> set = new ArrayList<>();

		try {
			MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(httpResponse).getResponses();
			if (responses != null && succeeded(httpResponse)) {
				for (MultiStatusResponse r : responses) {
					DavPropertySet props = r.getProperties(CalDAVStatus.SC_OK);
					if (!props.isEmpty()) set.add(props.get(property));
				}
			}
		} catch (Exception e) {
			log.warn("Unable to get MultiStatusResponse. Status: " + httpResponse.getStatusLine().getStatusCode());
		}

		return set;
	}

	/**
	 * @param uri URI to retrieve the Response for
	 * @return Returns the MultiStatusResponse to the corresponding uri.
	 */
	public MultiStatusResponse getResponseBodyAsMultiStatusResponse(HttpResponse httpResponse, String uri) throws DavException {
		MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(httpResponse).getResponses();
		for (MultiStatusResponse response : responses)
			if (response.getHref().equals(uri))
				return response;
		log.warn("No Response found for uri: " + uri);
		return null;
	}


}
