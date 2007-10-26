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
import java.util.List;
import java.util.Map;

import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.xml.OutputsDOMBase;
import org.osaf.caldav4j.xml.SimpleDOMOutputtingObject;
import org.osaf.caldav4j.xml.XMLUtils;

/**
 * <!ELEMENT filter comp-filter>
 *    <!ELEMENT comp-filter (is-defined | time-range)?
 *                         comp-filter* prop-filter*>
 *
 *  <!ATTLIST comp-filter name CDATA #REQUIRED>
 *  @author bobbyrullo
 * 
 */
public class CalendarQuery extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "calendar-query";
    public static final String ELEM_ALLPROP = "allprop";    
    public static final String ELEM_PROPNAME = "propname";
    
    private String caldavNamespaceQualifier = null;
    private String webdavNamespaceQualifier = null;
    private boolean allProp = false;
    private boolean propName = false;
    private List properties = new ArrayList();
    
    public CalendarQuery(String caldavNamespaceQualifier, String webdavNamespaceQualifer) {
        this.caldavNamespaceQualifier = caldavNamespaceQualifier;
        this.webdavNamespaceQualifier = webdavNamespaceQualifer;
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
        if (allProp){
            children.add(new SimpleDOMOutputtingObject(CalDAVConstants.NS_DAV,
                    webdavNamespaceQualifier, ELEM_ALLPROP));
        } else if (propName){
            children.add(new SimpleDOMOutputtingObject(CalDAVConstants.NS_DAV,
                    webdavNamespaceQualifier, ELEM_PROPNAME));
        } else if (properties != null && properties.size() > 0){
            Prop prop = new Prop(webdavNamespaceQualifier, properties);
            children.add(prop);
        }
        
        return children;
    }

    protected String getTextContent() {
        return null;
    }

    public boolean isAllProp() {
        return allProp;
    }

    public void setAllProp(boolean allProp) {
        this.allProp = allProp;
    }

    public boolean isPropName() {
        return propName;
    }

    public void setPropName(boolean propName) {
        this.propName = propName;
    }

    public List getProperties() {
        return properties;
    }

    public void setProperties(List properties) {
        this.properties = properties;
    }
    
    public void addProperty(PropProperty propProperty){
        properties.add(propProperty);
    }
    
    public void addProperty(String namespaceURI, String namespaceQualifier,
            String propertyName) {
        PropProperty propProperty = new PropProperty(namespaceURI,
                namespaceQualifier, propertyName);
        properties.add(propProperty);
    }
    protected Map getAttributes() {
        return null;
    }

}
