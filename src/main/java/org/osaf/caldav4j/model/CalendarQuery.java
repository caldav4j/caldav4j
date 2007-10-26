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

package org.osaf.caldav4j.model;

import java.util.Collection;

import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.xml.OutputsDOMBase;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * <!ELEMENT calendar-query (DAV:allprop | DAV:propname | DAV:prop)? filter>
 * 
 * @author bobbyrullo
 * 
 */
public class CalendarQuery extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "calendar-query";
    
    private String namespaceQualifier = null;

    public CalendarQuery(String namespaceQualifier) {
        this.namespaceQualifier = namespaceQualifier;
    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected String getNamespaceQualifier() {
        return namespaceQualifier;
    }

    protected String getNamespaceURI() {
        return CalDAVConstants.NS_CALDAV;
    }

    protected Collection getChildren() {
        // TODO Auto-generated method stub
        return null;
    }

    protected String getTextContent() {
        return null;
    }


}
