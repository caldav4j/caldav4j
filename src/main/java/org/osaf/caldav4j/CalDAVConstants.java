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

package org.osaf.caldav4j;

import org.apache.webdav.lib.util.QName;
import org.osaf.caldav4j.model.request.PropProperty;

public interface CalDAVConstants {
    
    public static final String TICKET_HEADER = "Ticket";

    public static final String METHOD_MKCALENDAR = "MKCALENDAR";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_REPORT = "REPORT";
    public static final String METHOD_MKTICKET = "MKTICKET";
    public static final String METHOD_DELTICKET = "DELTICKET";
    
    public static final String NS_CALDAV = "urn:ietf:params:xml:ns:caldav";
    public static final String NS_DAV = "DAV:";
    public static final String NS_XYTHOS = "http://www.xythos.com/namespaces/StorageServer";
    
    public static final String PROC_ID_DEFAULT =  "-//OSAF//NONSGML CalDAV4j Client//EN";
    
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String HEADER_IF_MATCH = "If-Match";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    
    public static final String CALENDAR_CONTENT_TYPE = "text/calendar";
    public static final String TEXT_XML_CONTENT_TYPE = "text/xml";
    
    public static final String PROP_GETETAG = "getetag";
    
    public static final QName QNAME_GETETAG 
        = new QName(NS_DAV, PROP_GETETAG);
    
    public static final String NS_QUAL_TICKET = "ticket";
    public static final String NS_QUAL_DAV = "D";
    
    public static final String ELEM_TICKETDISCOVERY = "ticketdiscovery";
    public static final String ELEM_TICKETINFO = "ticketinfo";
    public static final String ELEM_TIMEOUT = "timeout";
    public static final String ELEM_VISITS = "visits";
    public static final String ELEM_PRIVILIGE = "privilege";
    public static final String ELEM_READ = "read";
    public static final String ELEM_WRITE = "write";
    public static final String ELEM_ID = "id";
    public static final String ELEM_OWNER = "owner";
    public static final String ELEM_HREF = "href";
    public static final String ELEM_ALLPROP = "allprop";
   
    public static final Integer INFINITY = -1;
    public static final String  INFINITY_STRING = "infinity";
    
    public static final String TIMEOUT_UNITS_SECONDS = "Second-";

    public static final String URL_APPENDER = "?ticket=";

	public static final PropProperty PROP_ETAG = new PropProperty(
			CalDAVConstants.NS_DAV, "D", CalDAVConstants.PROP_GETETAG);
	
}
