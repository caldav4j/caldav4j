/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Bobby Rullo
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

package com.github.caldav4j.model.request;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.DOMValidationException;
import com.github.caldav4j.xml.OutputsDOMBase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

/**
 *
 *
 * <pre>
 * &lt;!ELEMENT prop EMPTY&gt;
 *
 * &lt;!ATTLIST prop name CDATA #REQUIRED
 *                novalue (yes|no) "no"&gt;
 * </pre>
 *
 * ex. &lt;C:PROP name="DESCRIPTION" /&gt;
 *
 * <p>Based on RFC 4791, Defines which properties to return in the response.
 *
 * @author bobbyrullo
 * @see <a href=http://tools.ietf.org/html/rfc4791#section-9.6.4>RFC 4791 Section 9.6.4</a>
 */
public class CalDAVProp extends OutputsDOMBase {

    public static final String ELEMENT_NAME = "prop";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_NOVALUE = "novalue";
    public static final String ATTR_VAL_YES = "yes";
    public static final String ATTR_VAL_NO = "no";

    private boolean attrNoValueEnabled = true; // XXX used to disable the view on ATTR_NOVALUE

    private String name = null;
    private boolean novalue = false;

    /**
     * @param name specifies the name of the calendar property to return
     * @param novalue can be used to request that the actual value of the property not be returned
     * @param attrNoValueEnabled specifies if value of novalue to be used
     */
    public CalDAVProp(String name, boolean novalue, boolean attrNoValueEnabled) {
        this.name = name;
        this.novalue = novalue;
        this.attrNoValueEnabled = attrNoValueEnabled;
    }

    /**
     * @param name specifies the name of the calendar property to return
     * @param novalue can be used to request that the actual value of the property not be returned
     */
    public CalDAVProp(String name, boolean novalue) {
        this(name, novalue, true);
    }

    /**
     * @param name specifies the name of the calendar property to return
     */
    public CalDAVProp(String name) {
        this(name, false, false);
    }

    /**
     * @see OutputsDOMBase#getElementName()
     */
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    /**
     * @see OutputsDOMBase#getNamespace()
     */
    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    /**
     * @see OutputsDOMBase#getAttributes()
     */
    protected Map<String, String> getAttributes() {
        Map<String, String> m = new HashMap<>();
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
     * &lt;!ELEMENT prop EMPTY&gt;
     *
     * <p>&lt;!ATTLIST prop name CDATA #REQUIRED novalue (yes|no) "no"&gt;
     *
     * @see OutputsDOMBase#validate()
     */
    public void validate() throws DOMValidationException {
        if (name == null) {
            throwValidationException("name is a required property");
        }
    }
}
