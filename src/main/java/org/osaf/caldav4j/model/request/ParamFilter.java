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

package org.osaf.caldav4j.model.request;

import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.xml.OutputsDOMBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <!ELEMENT param-filter (is-defined | text-match) >
 *
 * <!ATTLIST param-filter name CDATA #REQUIRED>
 *  
 * @author bobbyrullo
 * 
 */
public class ParamFilter extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "param-filter";
    public static final String ELEM_IS_DEFINED = "is-defined";
    public static final String ATTR_NAME = "name";

    private boolean isDefined = false;
    private TextMatch textMatch = null;
    private String name = null;

    public ParamFilter(String caldavNamespaceQualifier) {

    }

    public ParamFilter(){

    }

    public ParamFilter(String name, boolean isDefined, TextMatch textMatch){
        this.name = name;
        this.isDefined = isDefined;
        this.textMatch = textMatch;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    protected Collection<XmlSerializable> getChildren() {
        ArrayList<XmlSerializable> children = new ArrayList<XmlSerializable>();
        if (isDefined){
            children.add(new PropProperty(ELEM_IS_DEFINED, CalDAVConstants.NAMESPACE_CALDAV));
        } else if (textMatch != null){
            children.add(textMatch);
        }
        
        return children;
    }
    
    protected String getTextContent() {
        return null;
    }
    
    protected Map<String, String> getAttributes() {
        Map<String, String> m = new HashMap<String, String>();
        m.put(ATTR_NAME, name);
        return m;
    }


    public boolean isDefined() {
        return isDefined;
    }

    public void setDefined(boolean isDefined) {
        this.isDefined = isDefined;
    }
    
    public TextMatch getTextMatch() {
        return textMatch;
    }

    public void setTextMatch(TextMatch textMatch) {
        this.textMatch = textMatch;
    }
    
    /**
     * <!ELEMENT param-filter (is-defined | text-match) >
     * 
     * <!ATTLIST param-filter name CDATA #REQUIRED>
     * 
     */
    public void validate() throws DOMValidationException{
       if (name == null){
           throwValidationException("Name is a required property");
       }
        
       if (isDefined && textMatch != null){
           throwValidationException("isDefined and textMatch are mutually exclusive");
       }
       
       if (textMatch != null){
           textMatch.validate();
       }
    }
    
}
