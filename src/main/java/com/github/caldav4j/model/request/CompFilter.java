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
import java.util.*;
import net.fortuna.ical4j.model.Date;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

/**
 * Specifies search criteria on calendar components. The CALDAV:comp-filter XML element specifies a
 * query targeted at the calendar object or event. The scope of this element is the calendar object
 * when used as a child of the {@link CalendarQuery} or {@link CalendarMultiget} XML element. The
 * scope of the element is the enclosing calendar component when used as a child of another
 * CALDAV:comp-filter XML element.
 *
 * <pre>
 * &lt;!ELEMENT comp-filter (is-defined | time-range)?
 *                        comp-filter* prop-filter*&gt;
 * &lt;!ATTLIST comp-filter name CDATA #REQUIRED&gt;
 * </pre>
 *
 * @author bobbyrullo
 */
public class CompFilter extends OutputsDOMBase {

    public static final String ELEMENT_NAME = "comp-filter";
    public static final String ELEM_IS_DEFINED = "is-defined";
    public static final String ATTR_NAME = "name";

    private boolean isDefined = false;
    private TimeRange timeRange = null;
    private List<CompFilter> compFilters = new ArrayList<>();
    private List<PropFilter> propFilters = new ArrayList<>();
    private String name = null;

    /**
     * @param name a calendar object or calendar component type (e.g., VEVENT)
     */
    public CompFilter(String name) {
        this.name = name;
    }

    public CompFilter() {}

    /**
     * Create a CompFilter based on the parameters
     *
     * @param caldavNamespaceQualifier Caldav NameSpace qualifier
     * @param name a calendar object or calendar component type (e.g., VEVENT)
     * @param isDefined if true, the calendar object or calendar component type specified by the
     *     "name" attribute does not exist in the current scope
     * @param start Start of the time range of the recurrence
     * @param end end of the time range of recurrence
     * @param compFilters Comp Filters nested under this filter
     * @param propFilters Prop filters under this filter.
     */
    public CompFilter(
            String caldavNamespaceQualifier,
            String name,
            boolean isDefined,
            Date start,
            Date end,
            List<CompFilter> compFilters,
            List<PropFilter> propFilters) {
        this(name, isDefined, start, end, compFilters, propFilters);
    }

    /**
     * Create a CompFilter based on the parameters
     *
     * @param name a calendar object or calendar component type (e.g., VEVENT)
     * @param isDefined if true, the calendar object or calendar component type specified by the
     *     "name" attribute does not exist in the current scope
     * @param start Start of the time range of the recurrence
     * @param end end of the time range of recurrence
     * @param compFilters Comp Filters nested under this filter
     * @param propFilters Prop filters under this filter.
     */
    public CompFilter(
            String name,
            boolean isDefined,
            Date start,
            Date end,
            List<CompFilter> compFilters,
            List<PropFilter> propFilters) {
        this.isDefined = isDefined;
        this.name = name;

        if (start != null || end != null) { // XXX test the || instead of && (open interval)
            this.timeRange = new TimeRange(start, end);
        }

        if (propFilters != null) {
            this.propFilters.addAll(propFilters);
        }

        if (compFilters != null) {
            this.compFilters.addAll(compFilters);
        }
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
        } else if (timeRange != null) {
            children.add(timeRange);
        }

        if (compFilters != null) {
            children.addAll(compFilters);
        }

        if (propFilters != null) {
            children.addAll(propFilters);
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

    public List<CompFilter> getCompFilters() {
        return compFilters;
    }

    public void setCompFilters(List<CompFilter> compFilters) {
        this.compFilters = compFilters;
    }

    public void addCompFilter(CompFilter compFilter) {
        compFilters.add(compFilter);
    }

    public boolean isDefined() {
        return isDefined;
    }

    public void setDefined(boolean isDefined) {
        this.isDefined = isDefined;
    }

    public List<PropFilter> getPropFilters() {
        return propFilters;
    }

    public void setPropFilters(List<PropFilter> propFilters) {
        this.propFilters = propFilters;
    }

    public void addPropFilter(PropFilter propFilter) {
        propFilters.add(propFilter);
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     *
     * <pre>
     *   &lt;!ELEMENT comp-filter (is-defined | time-range)?
     *                        comp-filter* prop-filter*&gt;
     *   &lt;!ATTLIST comp-filter name CDATA #REQUIRED&gt;
     * </pre>
     */
    public void validate() throws DOMValidationException {
        if (name == null) {
            throwValidationException("Name is a required property.");
        }

        if (isDefined && timeRange != null) {
            throwValidationException("TimeRange and isDefined are mutually exclusive");
        }

        if (timeRange != null) {
            timeRange.validate();
        }

        if (compFilters != null) {
            validate(compFilters);
        }

        if (propFilters != null) {
            validate(propFilters);
        }
    }
}
