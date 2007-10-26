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
import java.util.Map;

import net.fortuna.ical4j.model.Date;

import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.xml.OutputsDOMBase;

/**
 *  <!ELEMENT calendar-data ((comp?, (expand-recurrence-set |
 *                                    limit-recurrence-set)?) |
 *                            #PCDATA)?>
 *
 *  <!ATTLIST calendar-data content-type CDATA "text/calendar">
 *
 *  <!ATTLIST calendar-data version CDATA "2.0">
 * @author bobbyrullo
 * 
 */
public class CalendarData extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "calendar-data";
    public static final String ELEM_EXPAND_RECURRENCE_SET = "expand-recurrence-set";
    public static final String ELEM_LIMIT_RECURRENCE_SET = "limit-recurrence-set";
    
    public static final Integer EXPAND = new Integer(0);
    public static final Integer LIMIT = new Integer(1);
    
    private String caldavNamespaceQualifier = null;
    private Date recurrenceSetStart = null;
    private Date recurrenceSetEnd   = null;
    private Integer expandOrLimitRecurrenceSet;
    private Comp comp = null;
    
    public CalendarData(String caldavNamespaceQualifier) {
        this.caldavNamespaceQualifier = caldavNamespaceQualifier;
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
        
        return children;
    }
    
    protected String getTextContent() {
        return null;
    }
    
    protected Map getAttributes() {
        return null;
    }



    
}
