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

package org.osaf.caldav4j.methods;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.jackrabbit.webdav.client.methods.DavMethodBase;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.model.request.*;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.UrlUtils;
import org.osaf.caldav4j.util.XMLUtils;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MkCalendarMethod extends DavMethodBase {
    
	
	/**
	 * Standard calendar properties
	 */
	
	protected String CALENDAR_DESCRIPTION = "calendar-description";
    /**
     * Map of the properties to set.
     */
    protected List<PropProperty> propertiesToSet = new ArrayList<PropProperty>();

    // --------------------------------------------------------- Public Methods

    public MkCalendarMethod(String uri) {
		// Add Headers Content-Type: text/xml
    	super(UrlUtils.removeDoubleSlashes(uri));

	}

    public void addRequestHeaders(HttpState state, HttpConnection conn)
            throws IOException, HttpException
    {
        //first add headers generate RequestEntity or
        //addContentLengthRequestHeader() will mess up things > result "400 Bad Request"
        //can not override generateRequestBody(), because called to often

        addRequestHeader(CalDAVConstants.HEADER_CONTENT_TYPE, CalDAVConstants.CONTENT_TYPE_TEXT_XML);
        //setRequestEntity(new ByteArrayRequestEntity(generateRequestBody()));
        super.addRequestHeaders(state, conn);
    }

    public void addDisplayName(String s) {
    	propertiesToSet.add(new DisplayName(s));
    }
    public void addDescription(String description, String lang) {
    	propertiesToSet.add(new CalendarDescription(description, lang));
    }
    public void addDescription(String description) {
    	propertiesToSet.add(new CalendarDescription(description));
    }
    /**
     * 
     */
    public void addPropertyToSet(String name, Namespace namespace,
            String value) {
        checkNotUsed();
        PropProperty propertyToSet = new PropProperty<String>(name, value, namespace);
        propertiesToSet.add(propertyToSet);
    }

    // remove double slashes
	public void setPath(String path) {
    	super.setPath(UrlUtils.removeDoubleSlashes(path));
    }
    
    // --------------------------------------------------- WebdavMethod Methods

    public String getName() {
        return CalDAVConstants.METHOD_MKCALENDAR;
    }
    
    /**
     *
     */
    protected byte[] generateRequestBody() {
        if (propertiesToSet.size() == 0 ){
            return null;
        }
        
        Prop prop = new Prop(propertiesToSet);
        MkCalendar mkCalendar = new MkCalendar("C",CalDAVConstants.NS_QUAL_DAV, prop);
        Document d = null;
        try {
            d = mkCalendar.createNewDocument(XMLUtils
                    .getDOMImplementation());
        } catch (DOMValidationException domve) {
            throw new RuntimeException(domve);
        }
        return XMLUtils.toPrettyXML(d).getBytes();
    }

    @Override
    protected boolean isSuccess(int statusCode) {
        return statusCode == CaldavStatus.SC_CREATED;
    }
}
