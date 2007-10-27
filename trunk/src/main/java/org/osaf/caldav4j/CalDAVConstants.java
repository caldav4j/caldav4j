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

public interface CalDAVConstants {

    public static final String METHOD_MKCALENDAR = "MKCALENDAR";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_REPORT = "REPORT";
    
    public static final String NS_CALDAV = "urn:ietf:params:xml:ns:caldav";
    public static final String NS_DAV = "DAV:";
    
    public static final String PROC_ID_DEFAULT =  "-//OSAF//NONSGML CalDAV4j Client//EN";
    
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String HEADER_IF_MATCH = "If-Match";
    
    public static final String PROP_GETETAG = "getetag";
    
    public static final QName QNAME_GETETAG 
        = new QName(NS_DAV, PROP_GETETAG);

}
