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

import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.xml.OutputsDOMBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Defines a report for querying calendar object resources.
 *
 * <!ELEMENT calendar-query (DAV:allprop | DAV:propname | DAV:prop)?
 *                            filter>
 * <!ELEMENT filter comp-filter>
 * 
 * <!ELEMENT comp-filter (is-defined | time-range)?
 *                         comp-filter* prop-filter*>
 *
 * <!ATTLIST comp-filter name CDATA #REQUIRED>
 *
 * @author bobbyrullo
 * @see <a href=http://tools.ietf.org/html/rfc4791#section-9.5>RFC 4791 Section 9.5</a>
 */
public class CalendarQuery extends OutputsDOMBase implements CalDAVReportRequest{

	public static final String ELEMENT_NAME = "calendar-query";
	public static final String ELEM_ALLPROP = "allprop";
	public static final String ELEM_PROPNAME = "propname";
	public static final String ELEM_FILTER = "filter";

	private boolean allProp = false;
	private boolean propName = false;
	private Prop properties = new Prop();
	private CompFilter compFilter = null;
	private CalendarData calendarDataProp = null;

	public CalendarQuery() {

	}

	public CalendarQuery(Prop properties, CompFilter compFilter, CalendarData calendarData,
	                     boolean allProp, boolean propName) {
		this(compFilter, calendarData, allProp, propName);
		if (properties != null)
			this.properties.addChildren(properties);
	}

	public CalendarQuery(DavPropertyNameSet properties, CompFilter compFilter, CalendarData calendarData,
	                     boolean allProp, boolean propName) {
		this(compFilter, calendarData, allProp, propName);
		if (properties != null)
			this.properties.addChildren(properties);
	}

	public CalendarQuery(Collection<? extends XmlSerializable> properties, CompFilter compFilter, CalendarData calendarData,
	                     boolean allProp, boolean propName) {
		this(compFilter, calendarData, allProp, propName);
		if (properties != null)
			this.properties.addChildren(properties);
	}

	public CalendarQuery(CompFilter compFilter, CalendarData calendarData, boolean allProp, boolean propName) {
		this.compFilter = compFilter;
		this.calendarDataProp = calendarData;
		this.allProp = allProp;
		this.propName = propName;
	}


	protected String getElementName() {
		return ELEMENT_NAME;
	}

	protected Namespace getNamespace() {
		return CalDAVConstants.NAMESPACE_CALDAV;
	}

	protected Collection<XmlSerializable> getChildren() {
		ArrayList<XmlSerializable> children = new ArrayList<XmlSerializable>();

		if (allProp) {
			children.add(new PropProperty(ELEM_ALLPROP, CalDAVConstants.NAMESPACE_WEBDAV));
		} else if (propName) {
			children.add(new PropProperty(ELEM_PROPNAME, CalDAVConstants.NAMESPACE_WEBDAV));
		} else if ((properties != null && !properties.isEmpty())
				|| calendarDataProp != null) {
			if (calendarDataProp != null) {
				properties.add(calendarDataProp);
			}
			children.add(properties);
		}

		if (compFilter != null) {
			PropProperty filter = new PropProperty(ELEM_FILTER, CalDAVConstants.NAMESPACE_CALDAV);
			filter.addChild(compFilter);
			children.add(filter);
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

	public Prop getProperties() {
		return properties;
	}

	public void setProperties(Collection<PropProperty> properties) {
		this.properties.addChildren(properties);
	}

	public void setProperties(DavPropertyNameSet properties) {
		this.properties.addChildren(properties);
	}

	public void addProperty(XmlSerializable propProperty) {
		properties.add(propProperty);
	}

	public void addProperty(String propertyName, Namespace namespace) {
		PropProperty propProperty = new PropProperty(propertyName, namespace);
		properties.add(propProperty);
	}

	protected Map<String, String> getAttributes() {
		return null;
	}

	public CompFilter getCompFilter() {
		return compFilter;
	}

	public void setCompFilter(CompFilter compFilter) {
		this.compFilter = compFilter;
	}

	public CalendarData getCalendarDataProp() {
		return calendarDataProp;
	}

	public void setCalendarDataProp(CalendarData calendarDataProp) {
		this.calendarDataProp = calendarDataProp;
	}

	/**
	 * Validates that the object validates against the following dtd:
	 * <p>
	 * <!ELEMENT calendar-query (DAV:allprop | DAV:propname | DAV:prop)? filter>
	 */
	public void validate() throws DOMValidationException {
		if (calendarDataProp != null) {
			calendarDataProp.validate();
		}
		if (compFilter == null) {
			throwValidationException("CompFilter cannot be null.");
		}

		compFilter.validate();
	}

}
