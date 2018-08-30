/*
 * Copyright 2005 Open Source Applications Foundation
 * 
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

package com.github.caldav4j;

import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

/**
 * Contains useful constants which are used, and can also be used by clients.
 */
public interface CalDAVConstants {

	public static final String METHOD_MKCALENDAR = "MKCALENDAR";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_REPORT = "REPORT";

	public static final String NS_CALDAV = "urn:ietf:params:xml:ns:caldav";
	public static final String NS_DAV = "DAV:";
	public static final String NS_QUAL_DAV = "D";
	public static final String NS_QUAL_CALDAV = "C";

	public static final String PROC_ID_DEFAULT =  "-//NONSGML CalDAV4j Client//EN";

	public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
	public static final String HEADER_IF_MATCH = "If-Match";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_ETAG = "ETag";

	public static final String CONTENT_TYPE_CALENDAR = "text/calendar";
	public static final String CONTENT_TYPE_TEXT_XML = "text/xml";


	public static final String DAV_ACL ="acl";
	public static final String DAV_PROP ="prop";
	public static final String DAV_PROPFIND ="propfind";
	public static final String DAV_DISPLAYNAME = "displayname" ;
	public static final String DAV_PRINCIPAL_OWNER = "owner";
	public static final String DAV_PRINCIPAL_AUTHENTICATED = "authenticated";

	public static final String CALDAV_PRIVILEGE_READ_FREE_BUSY =  "read-free-busy";
	public static final String CALDAV_PRIVILEGE_SCHEDULE =  "schedule";
	public static final String CALDAV_CALENDAR_DESCRIPTION = "calendar-description" ;
	public static final String CALDAV_CALENDAR_QUERY = "calendar-query";
	public static final String CALDAV_CALENDAR_DATA = "calendar-data";

	public static final String ATTR_START = "start";
	public static final String ATTR_END = "end";

	public static final String ELEM_TIMEOUT = "timeout";
	public static final String ELEM_VISITS = "visits";
	public static final String ELEM_PRIVILIGE = "privilege";
	public static final String ELEM_READ = "read";
	public static final String ELEM_WRITE = "write";
	public static final String ELEM_ID = "id";
	public static final String ELEM_OWNER = "owner";
	public static final String ELEM_HREF = "href";
	public static final String ELEM_ALLPROP = "allprop";
	public static final String ELEM_EXPAND_RECURRENCE_SET = "expand";
	public static final String ELEM_LIMIT_RECURRENCE_SET = "limit-recurrence-set";
	public static final String ELEM_PROPNAME = "propname";
	public static final String ELEM_FILTER = "filter";
	public static final String ELEM_GETETAG = "getetag";


	public static final String COLLATION_ASCII = "i;ascii-casemap";
	public static final String COLLATION_OCTET = "i;octet";

	public static final Integer INFINITY = -1;
	public static final String  INFINITY_STRING = "infinity";

	public static final String TIMEOUT_UNITS_SECONDS = "Seconds-";

	public static final String URL_APPENDER = "?ticket=";


	//Jackrabbit Namespaces
	public static final Namespace NAMESPACE_CALDAV = Namespace.getNamespace(NS_QUAL_CALDAV, NS_CALDAV);
	public static final Namespace NAMESPACE_WEBDAV = Namespace.getNamespace(NS_QUAL_DAV, NS_DAV);


	//Jackrabbit Constants

	// Depth
	//Definitions taken from: https://stackoverflow.com/questions/31284615/meaning-of-depth-header-in-webdav-propfind-method

	public static final int DEPTH_INFINITY = Integer.MAX_VALUE; // as 1 + properties of all files in sub-directories of the directory (recursively)
	public static final int DEPTH_0 = 0; //Retrieve properties of the directory
	public static final int DEPTH_1 = 1; //as 0 + properties of all files in the directory

	//DavPropertyNames
	public static final DavPropertyName DNAME_CALENDAR_DESCRIPTION = DavPropertyName.create(CALDAV_CALENDAR_DESCRIPTION, NAMESPACE_CALDAV);
	public static final DavPropertyName DNAME_CALENDAR_DATA = DavPropertyName.create(CALDAV_CALENDAR_DATA, NAMESPACE_CALDAV);
	public static final DavPropertyName DNAME_ACL = DavPropertyName.create(CalDAVConstants.DAV_ACL, NAMESPACE_WEBDAV);
	public static final DavPropertyName DNAME_GETETAG = DavPropertyName.create(ELEM_GETETAG, NAMESPACE_WEBDAV);
	public static final DavPropertyName DNAME_ALLPROP = DavPropertyName.create(ELEM_ALLPROP, NAMESPACE_WEBDAV);
	public static final DavPropertyName DNAME_DISPLAYNAME = DavPropertyName.create(DAV_DISPLAYNAME, NAMESPACE_CALDAV);
	//-------------------------------------------------< PropFind Constants >---
	public static final int PROPFIND_BY_PROPERTY = 0;
	public static final int PROPFIND_ALL_PROP = 1;
	public static final int PROPFIND_PROPERTY_NAMES = 2;
	public static final int PROPFIND_ALL_PROP_INCLUDE = 3; // RFC 4918, Section 9.1
}
