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
import org.osaf.caldav4j.util.UrlUtils;
import org.osaf.caldav4j.xml.OutputsDOMBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *  @see http://tools.ietf.org/html/rfc4791#section-9.5
 *          <!ELEMENT calendar-multiget ((DAV:allprop |
                                         DAV:propname |
                                         DAV:prop)?, DAV:href+)>
 * [differently from calendar-query...] it takes a list of DAV:href elements,
 * instead of a CALDAV:filter element, to determine which calendar
 * object resources to return.

 *  @author rpolli
 * 
 */
public class CalendarMultiget extends OutputsDOMBase implements CalDAVReportRequest{
    
    public static final String ELEMENT_NAME = "calendar-multiget";
    public static final String ELEM_ALLPROP = "allprop";    
    public static final String ELEM_PROPNAME = "propname";
    public static final String ELEM_FILTER = "filter";
    public static final String ELEM_HREF = CalDAVConstants.ELEM_HREF;
    
    private boolean allProp = false;
    private boolean propName = false;
    private CalendarData calendarDataProp = null;
    private List<String> hrefs = null;
    private Prop properties = new Prop();

    public CalendarMultiget() {

    }

    public CalendarMultiget(DavPropertyNameSet properties,
                            CalendarData calendarData, boolean allProp, boolean propName){
        this(calendarData, allProp, propName);
        this.properties.addChildren(properties);
    }

    public CalendarMultiget(Prop properties,
                            CalendarData calendarData, boolean allProp, boolean propName){
        this(calendarData, allProp, propName);
        this.properties.addChildren(properties);
    }

    public CalendarMultiget(CalendarData calendarData, boolean allProp, boolean propName){
        this.calendarDataProp = calendarData;
        this.allProp = allProp;
        this.propName = propName;
    }

    public CalendarMultiget(Collection<? extends XmlSerializable> properties, CalendarData calendarData,
                            boolean allProp, boolean propName) {
        this(calendarData, allProp, propName);
        this.properties.addChildren(properties);
    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    protected Collection<XmlSerializable> getChildren() {
        ArrayList<XmlSerializable> children = new ArrayList<XmlSerializable>();

        if (allProp){
            children.add(new PropProperty(CalDAVConstants.ELEM_ALLPROP, CalDAVConstants.NAMESPACE_WEBDAV));
        } else if (propName){
            children.add(new PropProperty(ELEM_PROPNAME, CalDAVConstants.NAMESPACE_WEBDAV));
        } else if ((properties != null && !properties.isEmpty())
                || calendarDataProp != null) {

            if (calendarDataProp != null){
              properties.addChild(calendarDataProp);
            }
            children.add(properties);
        }
        
        // remove double "//" from paths
        if ( hrefs != null ) { 
	        for (String uri : hrefs) {
	        	DavHref href = 
	        		new DavHref(UrlUtils.removeDoubleSlashes(uri));
	        	children.add(href);
			}
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

    public void addProperty(XmlSerializable propProperty){
        properties.add(propProperty);
    }

    public void addProperty(String propertyName, Namespace namespace) {
        PropProperty propProperty = new PropProperty(propertyName, namespace);
        properties.add(propProperty);
    }

    protected Map<String, String> getAttributes() {
        return null;
    }

    public CalendarData getCalendarDataProp() {
        return calendarDataProp;
    }

    public void setCalendarDataProp(CalendarData calendarDataProp) {
        this.calendarDataProp = calendarDataProp;
    }

    public void setHrefs(List<String> l) {
    	hrefs = l;
    }
    
    public List<String> getHrefs() {
    	return hrefs;
    }

    public void addHref(String name) {
        if(getHrefs() == null) hrefs = new ArrayList<String>();
        this.hrefs.add(name);
    }

    /**
     * Validates that the object validates against the following dtd:
     * 
     * <!ELEMENT calendar-query (DAV:allprop | DAV:propname | DAV:prop)? filter>
     */
    public void validate() throws DOMValidationException{
        if (calendarDataProp != null){
            calendarDataProp.validate();
        }
        if (hrefs == null){
            throwValidationException("Dav:Href cannot be null.");
        }
        
    }

}
