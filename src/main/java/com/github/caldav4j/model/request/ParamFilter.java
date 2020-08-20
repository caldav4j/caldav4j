/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Bobby Rullo, Roberto Polli
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

/**
 * Limits the search to specific parameter values. It specifies a query targeted at a specific
 * calendar property parameter (e.g., PARTSTAT) in the scope of the calendar property on which it is
 * defined.
 *
 * <pre>
 * &lt;!ELEMENT param-filter (is-not-defined | text-match?) &gt;
 * &lt;!ATTLIST param-filter name CDATA #REQUIRED&gt;
 * </pre>
 *
 * @author bobbyrullo
 */
public class ParamFilter extends OutputsDOMBase {

    public static final String ELEMENT_NAME = "param-filter";
    public static final String ELEM_IS_DEFINED = "is-defined";
    public static final String ATTR_NAME = "name";

    private boolean isDefined = false;
    private TextMatch textMatch = null;
    private String name = null;

    public ParamFilter() {}

    public ParamFilter(String name, boolean isDefined, TextMatch textMatch) {
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
        ArrayList<XmlSerializable> children = new ArrayList<>();
        if (isDefined) {
            children.add(new PropProperty(ELEM_IS_DEFINED, CalDAVConstants.NAMESPACE_CALDAV));
        } else if (textMatch != null) {
            children.add(textMatch);
        }

        return children;
    }

    protected String getTextContent() {
        return null;
    }

    protected Map<String, String> getAttributes() {
        Map<String, String> m = new HashMap<>();
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
     *
     *
     * <pre>
     * &lt;!ELEMENT param-filter (is-defined | text-match) &gt;
     * &lt;!ATTLIST param-filter name CDATA #REQUIRED&gt;
     * </pre>
     */
    public void validate() throws DOMValidationException {
        if (name == null) {
            throwValidationException("Name is a required property");
        }

        if (isDefined && textMatch != null) {
            throwValidationException("isDefined and textMatch are mutually exclusive");
        }

        if (textMatch != null) {
            textMatch.validate();
        }
    }
}
