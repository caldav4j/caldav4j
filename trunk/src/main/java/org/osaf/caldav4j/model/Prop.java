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
import java.util.List;

import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.xml.OutputsDOMBase;

public class Prop extends OutputsDOMBase{
    public static final String ELEMENT_NAME = "prop";

    private String namespaceQualifier;
    private List children = null;
    
    public Prop(String namespaceQualifier){
        this.namespaceQualifier = namespaceQualifier;
    }
    
    public Prop(String namespaceQualifier, List properties){
        this.namespaceQualifier = namespaceQualifier;
        this.children = properties;
    }
    
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected String getNamespaceQualifier() {
        return namespaceQualifier;
    }

    protected String getNamespaceURI() {
        return CalDAVConstants.NS_DAV;
    }

    protected Collection getChildren() {
        return children;
    }

    protected String getTextContent() {
        return null;
    }

}
