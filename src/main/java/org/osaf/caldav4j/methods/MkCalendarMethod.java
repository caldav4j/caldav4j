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
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.jackrabbit.webdav.client.methods.DavMethodBase;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.model.request.CalendarDescription;
import org.osaf.caldav4j.model.request.DisplayName;
import org.osaf.caldav4j.model.request.MkCalendar;
import org.osaf.caldav4j.model.request.Prop;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.UrlUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class MkCalendarMethod extends DavMethodBase {
    
	
	/**
	 * Standard calendar properties
	 */
	
	protected String CALENDAR_DESCRIPTION = "calendar-description";

	protected MkCalendar mkCalendar = null;
    // --------------------------------------------------------- Public Methods

    public MkCalendarMethod(String uri, String DisplayName, String description, String DescriptionLang)
            throws IOException {
        super(UrlUtils.removeDoubleSlashes(uri));
        Prop p = new Prop();
        if(DisplayName != null)
            p.add(new DisplayName(DisplayName));

        if(description != null)
            p.add(new CalendarDescription(description, DescriptionLang));
        mkCalendar = new MkCalendar(p);
        processRequest();
	}

	public MkCalendarMethod(String uri, String DisplayName, String description) throws IOException {
        this(uri, DisplayName, description, null);
    }

    public MkCalendarMethod(String uri) {
        super(uri);
    }

    public MkCalendarMethod(String uri, MkCalendar m) throws IOException {
        super(UrlUtils.removeDoubleSlashes(uri));
        if(m != null)
            this.mkCalendar = m;
        processRequest();
    }

	private void processRequest() throws IOException {

        try {
            Document d = DomUtil.createDocument();
            d.appendChild(mkCalendar.toXml(d));
            setRequestBody(d);
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException
    {
        addRequestHeader(CalDAVConstants.HEADER_CONTENT_TYPE, CalDAVConstants.CONTENT_TYPE_TEXT_XML);
        super.addRequestHeaders(state, conn);
    }

    /**
     * @see org.apache.commons.httpclient.HttpMethodBase#setPath(String)
     */
	public void setPath(String path) {
    	super.setPath(UrlUtils.removeDoubleSlashes(path));
    }

    // --------------------------------------------------- WebdavMethod Methods

    /**
     * @see HttpMethod#getName()
     */
    public String getName() {
        return CalDAVConstants.METHOD_MKCALENDAR;
    }

    @Override
    protected boolean isSuccess(int statusCode) {
        return statusCode == CaldavStatus.SC_CREATED;
    }
}
