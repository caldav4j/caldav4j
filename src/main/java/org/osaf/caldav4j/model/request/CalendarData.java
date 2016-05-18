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

import net.fortuna.ical4j.model.Date;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.xml.OutputsDOMBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *  <!ELEMENT calendar-data ((comp?, (expand-recurrence-set |
 *                                    limit-recurrence-set)?) |
 *                            #PCDATA)?>
 *
 *  <!ATTLIST calendar-data content-type CDATA "text/calendar">
 *
 *  <!ATTLIST calendar-data version CDATA "2.0">
 *  
 *  <!ELEMENT expand EMPTY>
 *  
 *  <!ATTLIST expand start CDATA #REQUIRED
 *                                  end CDATA #REQUIRED>
 *                                  
 *  <!ELEMENT limit-recurrence-set EMPTY>
 *  
 *  <!ATTLIST limit-recurrence-set start CDATA #REQUIRED
 *                                 end CDATA #REQUIRED>
 *                                 
 *    NOTE: The CALDAV:prop and CALDAV:allprop elements used here have the
 *  same name as elements defined in WebDAV.  However, the elements used
 *  here have the "urn:ietf:params:xml:ns:caldav" namespace, as opposed
 *  to the "DAV:" namespace used for elements defined in WebDAV.
 *
 *  <!ELEMENT comp ((allcomp, (allprop | prop*)) |
 *                  (comp*, (allprop | prop*)))>
 *
 * <!ATTLIST comp name CDATA #REQUIRED>
 * @author bobbyrullo
 * 
 */
public class CalendarData extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "calendar-data";
    public static final String ELEM_EXPAND_RECURRENCE_SET = "expand";
    public static final String ELEM_LIMIT_RECURRENCE_SET = "limit-recurrence-set";
    public static final String ATTR_START = "start";
    public static final String ATTR_END = "end";
    
    public static final Integer EXPAND = 0;
    public static final Integer LIMIT = 1;

    private Date recurrenceSetStart = null;
    private Date recurrenceSetEnd   = null;
    private Integer expandOrLimitRecurrenceSet;
    private Comp comp = null;
    
    public CalendarData(Integer expandOrLimitRecurrenceSet, Date recurrenceSetStart,
            Date recurrenceSetEnd, Comp comp) {

        this.expandOrLimitRecurrenceSet = expandOrLimitRecurrenceSet;
        this.recurrenceSetStart = recurrenceSetStart;
        this.recurrenceSetEnd = recurrenceSetEnd;
        this.comp = comp;
    }

    public CalendarData() {
    }

    /*
    Compatibility Constructors.
     */

    public CalendarData(String caldavnamespaceQualifier){
        this();
    }

    public CalendarData(String caldavnamespaceQualifier, Integer expandOrLimitRecurrenceSet, Date recurrenceSetStart,
                        Date recurrenceSetEnd, Comp comp){
        this(expandOrLimitRecurrenceSet, recurrenceSetStart, recurrenceSetEnd, comp);
    }

    public Comp getComp() {
        return comp;
    }

    public void setComp(Comp comp) {
        this.comp = comp;
    }

    public Integer getExpandOrLimitRecurrenceSet() {
        return expandOrLimitRecurrenceSet;
    }

    public void setExpandOrLimitRecurrenceSet(Integer expandOrLimitRecurrenceSet) {
        this.expandOrLimitRecurrenceSet = expandOrLimitRecurrenceSet;
    }

    public Date getRecurrenceSetEnd() {
        return recurrenceSetEnd;
    }

    public void setRecurrenceSetEnd(Date recurrenceSetEnd) {
        this.recurrenceSetEnd = recurrenceSetEnd;
    }

    public Date getRecurrenceSetStart() {
        return recurrenceSetStart;
    }

    public void setRecurrenceSetStart(Date recurrenceSetStart) {
        this.recurrenceSetStart = recurrenceSetStart;
    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    protected Collection<? extends XmlSerializable> getChildren() {
        ArrayList<XmlSerializable> children = new ArrayList<XmlSerializable>();
        if (comp != null) {
            children.add(comp);
        }

        if (expandOrLimitRecurrenceSet != null) {
            String elemName = EXPAND.equals(expandOrLimitRecurrenceSet) ? ELEM_EXPAND_RECURRENCE_SET
                    : ELEM_LIMIT_RECURRENCE_SET;
            PropProperty expandOrLimitElement = new PropProperty(elemName, CalDAVConstants.NAMESPACE_CALDAV);
            expandOrLimitElement.addAttribute(ATTR_START, recurrenceSetStart.toString());
            expandOrLimitElement.addAttribute(ATTR_END, recurrenceSetEnd.toString());
            children.add(expandOrLimitElement);
        }
        return children;
    }
    
    protected String getTextContent() {
        return null;
    }
    
    protected Map<String, String> getAttributes() {
        return null;
    }

    /**
     * <!ELEMENT calendar-data ((comp?, (expand-recurrence-set |
     * limit-recurrence-set)?) | #PCDATA)?>
     * 
     * <!ATTLIST calendar-data content-type CDATA "text/calendar">
     * 
     * <!ATTLIST calendar-data version CDATA "2.0">
     * 
     * <!ELEMENT expand-recurrence-set EMPTY>
     * 
     * <!ATTLIST expand-recurrence-set start CDATA #REQUIRED end CDATA
     * #REQUIRED>
     * 
     * <!ELEMENT limit-recurrence-set EMPTY>
     * 
     * <!ATTLIST limit-recurrence-set start CDATA #REQUIRED end CDATA #REQUIRED>
     */
    public void validate() throws DOMValidationException {
        if (expandOrLimitRecurrenceSet != null
                && (recurrenceSetStart == null || recurrenceSetEnd == null)) {
            throwValidationException("If you specify expand-recurrence-set or " +
                    "limit-recurrence-set you must specify a start and end date");
        }
        
        if (comp != null){
            comp.validate();
        }
        
    }       

    
}
