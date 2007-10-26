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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.xml.OutputsDOMBase;

/**
 *   <!ELEMENT filter comp-filter>
 *   
 *   <!ELEMENT comp-filter (is-defined | time-range)?
 *                        comp-filter* prop-filter*>
 *                        
 *   <!ATTLIST comp-filter name CDATA #REQUIRED> 
 *  
 *   <!ELEMENT prop-filter (is-defined | time-range | text-match)?
 *                          param-filter*>
 *
 *   <!ATTLIST prop-filter name CDATA #REQUIRED>
 *
 *   <!ELEMENT param-filter (is-defined | text-match) >
 *
 *   <!ATTLIST param-filter name CDATA #REQUIRED>
 *
 *   <!ELEMENT is-defined EMPTY>
 * 
 * @author bobbyrullo
 * 
 */
public class Filter extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "filter";
    
    private String caldavNamespaceQualifier = null;
    
    public Filter(String caldavNamespaceQualifier) {
        this.caldavNamespaceQualifier = caldavNamespaceQualifier;
    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected String getNamespaceQualifier() {
        return caldavNamespaceQualifier;
    }

    protected String getNamespaceURI() {
        return CalDAVConstants.NS_CALDAV;
    }

    protected Collection getChildren() {
        ArrayList children = new ArrayList();
        
        return children;
    }

    protected String getTextContent() {
        return null;
    }
    protected Map getAttributes() {
        return null;
    }


}
