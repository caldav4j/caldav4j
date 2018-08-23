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

package com.github.caldav4j.model.request;

import net.fortuna.ical4j.model.Date;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.DOMValidationException;
import com.github.caldav4j.xml.OutputsDOMBase;

import java.util.*;

/**
 * The CALDAV:prop-filter XML element specifies a query targeted at a specific calendar
 * property (e.g., CATEGORIES) scope of the enclosing calendar component.
 *
 * <pre>
 *  &lt;!ELEMENT prop-filter (is-defined | time-range | text-match)?
 *                          param-filter*&gt;
 *  &lt;!ATTLIST prop-filter name CDATA #REQUIRED&gt;
 * </pre>
 * @author bobbyrullo
 * 
 */
public class PropFilter extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "prop-filter";
    public static final String ELEM_IS_DEFINED = "is-defined";
    public static final String ELEM_IS_NOT_DEFINED = "is-not-defined";
    public static final String ATTR_NAME = "name";

    private String name = null;
    private Boolean isDefined = null;

    private TimeRange timeRange = null;
    private TextMatch textMatch = null;
    private List<ParamFilter> paramFilters = new ArrayList<ParamFilter>();
    
    public PropFilter() {

    }

	/**
	 * Create a ParamFilter based on the parameters
	 * @param name               a calendar property name (e.g., ATTENDEE)
	 * @param isDefined          if true check for existence of property
	 * @param timeRangeStart     Start date of time range
	 * @param timeRangeEnd       End date of time range.
	 * @param textmatchCaseless  Caseless matching of properties
	 * @param negateCondition    Negate
	 * @param textMatchCollation Collation, could be "i;ascii-casemap" or "i;octet"
	 * @param textMatchString    String to match
	 * @param paramFilters       Param Filters if any.
	 */
	public PropFilter(String name,
            Boolean isDefined, Date timeRangeStart, Date timeRangeEnd, 
            Boolean textmatchCaseless, boolean negateCondition,
            String textMatchCollation, String textMatchString, List<ParamFilter> paramFilters){

        this.name = name;

        
        if (isDefined != null) {
            this.isDefined = isDefined;
        } else if (timeRangeStart != null || timeRangeEnd != null){
            this.timeRange = new TimeRange(timeRangeStart, timeRangeEnd);
        } else if (textMatchString != null){
            this.textMatch = new TextMatch(textmatchCaseless, negateCondition, textMatchCollation,
            		textMatchString);
        }
        if (paramFilters != null){
            this.paramFilters = paramFilters;
        }
    }
    
    /**
     * Create a ParamFilter based on the parameters
     * @deprecated The Full constructor should be used
     * @param name a calendar property name (e.g., ATTENDEE)
     * @param isDefined if true check for existence of property
     * @param timeRangeStart Start date of time range
     * @param timeRangeEnd End date of time range.
     * @param textmatchCaseless Caseless matching of properties
     * @param textMatchString String to match
     * @param paramFilters Param Filters if any.
     */
    public PropFilter(String name,
            boolean isDefined, Date timeRangeStart, Date timeRangeEnd, 
            Boolean textmatchCaseless, String textMatchString, List<ParamFilter> paramFilters){

        this.name = name;
        this.isDefined = isDefined;
        if (timeRangeStart != null && timeRangeEnd != null){
            this.timeRange = new TimeRange(timeRangeStart, timeRangeEnd);
        } else if (textMatchString != null){
            this.textMatch = new TextMatch(textmatchCaseless, false, null,
            		textMatchString);
        }
        if (paramFilters != null){
            this.paramFilters = paramFilters;
        }    	

    }
     

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    protected Collection<XmlSerializable> getChildren() {
        ArrayList<XmlSerializable> children = new ArrayList<XmlSerializable>();
        
        if (isDefined != null) {
            children.add(new PropProperty(isDefined ? ELEM_IS_DEFINED :  ELEM_IS_NOT_DEFINED, CalDAVConstants.NAMESPACE_CALDAV));
        } else if (timeRange != null){
            children.add(timeRange);
        } else if (textMatch != null){
            children.add(textMatch);
        }
        
        if (paramFilters != null && paramFilters.size() > 0){
            children.addAll(paramFilters);
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

    public Boolean isDefined() {
        return isDefined;
    }

    public void setIsDefined(Boolean isDefined) {
        this.isDefined = isDefined;
    }

    public List<ParamFilter> getParamFilters() {
        return paramFilters;
    }

    public void setParamFilters(List<ParamFilter> paramFilters) {
        this.paramFilters = paramFilters;
    }
    
    public void addParamFilter(ParamFilter paramFilter){
        paramFilters.add(paramFilter);
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }
    
    public void setTimeRange(Date start, Date end){
        this.timeRange = new TimeRange(start, end);
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

    public TextMatch getTextMatch() {
        return textMatch;
    }

    public void setTextMatch(TextMatch textMatch) {
        this.textMatch = textMatch;
    }
    
    /**
     * <pre>
     *  &lt;!ELEMENT prop-filter (is-defined | time-range | text-match)?
     *                          param-filter*&gt;
     *  &lt;!ATTLIST prop-filter name CDATA #REQUIRED&gt;
     *  </pre>
     * @author bobbyrullo
     */
    public void validate() throws DOMValidationException{
        if (name == null){
            throwValidationException("Name is a required property");
        }
        
        if ( (isDefined != null) && (timeRange != null || textMatch != null)){
            throwValidationException("isDefined, timeRange and textMatch are mutually exclusive");
        }
        
        if (timeRange != null) {
            if (textMatch != null){
                throwValidationException("isDefined, timeRange and textMatch are mutually exclusive");
            } 
            
            timeRange.validate();
        } else if (textMatch != null){
            textMatch.validate();
        }

        if (paramFilters != null){
            validate(paramFilters);
        }

    }
}