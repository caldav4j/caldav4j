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

/**
 * Implements the MKCALENDAR Method as defined in RFC 4791
 */
public class MkCalendarMethod extends DavMethodBase {


	/**
	 * Standard calendar properties
	 */

	protected String CALENDAR_DESCRIPTION = "calendar-description";

	protected MkCalendar mkCalendar = null;
	// --------------------------------------------------------- Public Methods

	/**
	 * Convenience method to create a calendar with the defined properties.
	 *
	 * @param uri             Location to the CalendarResource
	 * @param DisplayName     Display Name of Calendar
	 * @param description     Description of Calendar
	 * @param DescriptionLang Language of Description.
	 * @throws IOException
	 */
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

	/**
	 * Convenience method to create a calendar with the defined properties.
	 * @param uri Location to the CalendarResource
	 * @param DisplayName Display Name of Calendar
	 * @param description Description of Calendar
	 * @throws IOException
	 */
	public MkCalendarMethod(String uri, String DisplayName, String description) throws IOException {
		this(uri, DisplayName, description, null);
	}

	/**
	 * Default Constructor, makes a calendar at URI.
	 * @param uri
	 * @throws IOException
	 */
	public MkCalendarMethod(String uri) {
		super(uri);
	}

	/**
	 * Create a calendar with the MkCalendar object and properties.
	 * @param uri Location to the CalendarResource
	 * @param m MkCalendar Object
	 * @throws IOException
	 */
	public MkCalendarMethod(String uri, MkCalendar m) throws IOException {
		super(UrlUtils.removeDoubleSlashes(uri));
		if(m != null)
			this.mkCalendar = m;
		processRequest();
	}

	/**
	 * Process the MkCalendar object and set it as request body.
	 * @throws IOException
	 */
	private void processRequest() throws IOException {

		try {
			Document d = DomUtil.createDocument();
			d.appendChild(mkCalendar.toXml(d));
			setRequestBody(d);
		} catch (ParserConfigurationException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Overridden to add the mkcalendar header content type.
	 * @see DavMethodBase#addRequestHeaders(HttpState, HttpConnection)
	 */
	public void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException {
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

	/**
	 * @see DavMethodBase#isSuccess(int)
	 */
	@Override
	protected boolean isSuccess(int statusCode) {
		return statusCode == CaldavStatus.SC_CREATED;
	}
}
