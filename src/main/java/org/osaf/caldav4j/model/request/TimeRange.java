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
import net.fortuna.ical4j.model.DateTime;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.xml.OutputsDOM;
import org.osaf.caldav4j.xml.OutputsDOMBase;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  <!ELEMENT time-range EMPTY>
 *
 *  <!ATTLIST time-range start CDATA
 *                       end CDATA>
 * @author bobbyrullo
 * 
 */
public class TimeRange extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "time-range";
    public static final String ATTR_START = "start";
    public static final String ATTR_END = "end";

    private Date start = null;
    private Date end = null;
    
    public TimeRange(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    protected Collection<OutputsDOM> getChildren() {
        return null;
    }

    protected String getTextContent() {
        return null;
    }
    
    protected Map<String, String> getAttributes() {
        Map<String, String> m =  new LinkedHashMap<String, String>();
        if (start != null) {
        	m.put(ATTR_START, start.toString());
        }

        if (end != null) {
            m.put(ATTR_END, end.toString());
        }
        return m;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setTimeRange(String start, String end) throws ParseException {
        this.start = new DateTime(start);
        this.end = new DateTime(end);
    }


    /**
     * <!ELEMENT time-range EMPTY>
     * 
     * <!ATTLIST time-range start CDATA end CDATA>
     * @see http://tools.ietf.org/html/rfc4791#section-9.9
     * Time ranges open at one end can be specified by
      including only one attribute; however, at least one attribute MUST
      always be present in the CALDAV:time-range element. 
     */
    public void validate() throws DOMValidationException{
        if (start == null && end == null){
            throwValidationException("You must have a start or an end date");
        }
    }


}
