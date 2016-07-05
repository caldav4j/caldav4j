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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <!ELEMENT prop EMPTY>
 * 
 * <!ATTLIST prop name CDATA #REQUIRED
 *                novalue (yes|no) "no">
 *  ex. <C:PROP name="DESCRIPTION" />               
 * @author bobbyrullo
 * 
 */
public class CalDAVProp extends OutputsDOMBase {

    public static final String ELEMENT_NAME = "prop";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_NOVALUE = "novalue";
    public static final String ATTR_VAL_YES = "yes";
    public static final String ATTR_VAL_NO = "no";

    private boolean attrNoValueEnabled = true; //XXX used to disable the view on ATTR_NOVALUE

    private String name = null;
    private boolean novalue = false;



    public CalDAVProp(String name, boolean novalue, boolean attrNoValueEnabled) {
        this.name = name;
        this.novalue = novalue;
        this.attrNoValueEnabled = attrNoValueEnabled;
    }

    public CalDAVProp(String name, boolean novalue) {
        this(name, novalue, true);
    }

    public CalDAVProp(String name) {
        this(name, false, false);
    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    protected Map<String, String> getAttributes() {
        Map<String, String> m =  new HashMap<String, String>();
        m.put(ATTR_NAME, name);

        if (attrNoValueEnabled) {
            m.put(ATTR_NOVALUE, novalue ? ATTR_VAL_YES : ATTR_VAL_NO);
        }

        return m;
    }


    protected String getTextContent() {
        return null;
    }

    public Collection<XmlSerializable> getChildren() {
        return null;
    }
    
    /**
     * <!ELEMENT prop EMPTY>
     * 
     * <!ATTLIST prop name CDATA #REQUIRED
     *                novalue (yes|no) "no">
     */
    public void validate() throws DOMValidationException {
        if (name == null){
            throwValidationException("name is a required property");
        }
    }

}
