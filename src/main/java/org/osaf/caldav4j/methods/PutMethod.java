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

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements the HTTP PUT method, along with Calendar handling specific code.
 *
 * @see org.apache.commons.httpclient.methods.PutMethod
 */
public class PutMethod extends org.apache.commons.httpclient.methods.PutMethod{
	private static final Logger log = LoggerFactory.getLogger(PutMethod.class);

	private Calendar calendar = null;
	private String procID = CalDAVConstants.PROC_ID_DEFAULT;
	private CalendarOutputter calendarOutputter = null;
	private Set<String> etags = new HashSet<String>();
	private boolean ifMatch = false;
	private boolean ifNoneMatch = false;
	private boolean allEtags = false;
	private Charset charset = null;

	/**
	 * Set the character set for the reponse.
	 * @param charset Charset object representing the charset.
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	/**
	 * Retrieve the current charset value.
	 * @return Charset or null if none present.
	 */
	public Charset getCharset() {
		if (charset == null) {
			charset = Charset.forName("UTF-8");
		}
		return charset;
	}

	/**
	 * Default Constructor
	 */
	public PutMethod (){
		super();
	}

	/**
	 * @see HttpMethodBase#getName()
	 */
	public String getName() {
		return CalDAVConstants.METHOD_PUT;
	}

	/**
	 * The set of eTags that will be used in "if-none-match" or "if-match" if the
	 * ifMatch or ifNoneMatch properties are set
	 * @return set of eTags
	 */
	public Set getEtags() {
		return etags;
	}

	/**
	 * Add eTags to be set for "if-none-match" or "if-match", based on the
	 * enabled property.
	 * @param etags Set of strings containing eTags
	 */
	public void setEtags(Set<String> etags) {
		this.etags.addAll(etags);
	}

	/**
	 * Add's the etag provided to the "if-none-match" or "if-match" header.
	 *
	 * Note - You MUST provide a quoted string!
	 * @param etag eTag to add
	 */
	public void addEtag(String etag){
		etags.add(etag);
	}


	/**
	 * Remove eTag from the current list.
	 * @param etag eTag to remove
	 */
	public void removeEtag(String etag){
		etags.remove(etag);
	}

	/**
	 * If true the "if-match" conditional header is used with the etags set in the
	 * etags property.
	 * @return value of property
	 */
	public boolean isIfMatch() {
		return ifMatch;
	}

	/**
	 * If true is provided, then enable the "if-match" property along with the associated eTags.
	 * @param ifMatch True to enable.
	 */
	public void setIfMatch(boolean ifMatch) {
		this.ifMatch = ifMatch;
	}

	/**
	 * If true the "if-none-match" conditional header is used with the etags set in the
	 * etags property.
	 * @return value of property
	 */
	public boolean isIfNoneMatch() {
		return ifNoneMatch;
	}

	/**
	 * If true is provided, then set the "if--none-match" property along with the associated eTags.
	 * @param ifNoneMatch True to enable.
	 */
	public void setIfNoneMatch(boolean ifNoneMatch) {
		this.ifNoneMatch = ifNoneMatch;
	}

	/**
	 * If enabled "if-none-match" or "if-match" will have a value of "*" i.e. for all eTags.
	 * @return Value of Property
	 */
	public boolean isAllEtags() {
		return allEtags;
	}

	/**
	 * Set "if-none-match" or "if-match" with the value "*" i.e. for all eTags if enabled.
	 * @param allEtags True to enable
	 */
	public void setAllEtags(boolean allEtags) {
		this.allEtags = allEtags;
	}

	/**
	 * Set the calendar to put.
	 * @param calendar Calendar Resource to be used.
	 */
	public void setRequestBody(Calendar calendar){
		this.calendar = calendar;
	}

	/**
	 * Convenience method to set the request body based on the Vevent and timezone
	 * @param vevent Event to set
	 * @param vtimeZone Timezone to use
	 */
	public void setRequestBody(VEvent vevent, VTimeZone vtimeZone){
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId("-//Open Source Applications Foundation//NONSGML Scooby Server//EN"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		calendar.getComponents().add(vevent);
		if (vtimeZone != null){
			calendar.getComponents().add(vtimeZone);
		}
		this.calendar = calendar;
	}

	/**
	 * Convenience method to set the request body based on the Vevent and timezone
	 * @param vevent Event to set
	 */
	public void setRequestBody(VEvent vevent){
		setRequestBody(vevent, null);
	}

	/**
	 * The ProcID to use when creating a new VCALENDAR component
	 * @return the value of current ProcID
	 */
	public String getProcID() {
		return procID;
	}

	/**
	 * Sets the ProcID to use when creating a new VCALENDAR component
	 * @param procID ProcID to set
	 */
	public void setProcID(String procID) {
		this.procID = procID;
	}

	/**
	 *
	 * @return CalendarOutputter currently being used
	 */
	public CalendarOutputter getCalendarOutputter() {
		return calendarOutputter;
	}

	/**
	 * Set the CalendarOutputter to be used for generating the request body based
	 * on the calendar.
	 * @param calendarOutputter
	 */
	public void setCalendarOutputter(CalendarOutputter calendarOutputter) {
		this.calendarOutputter = calendarOutputter;
	}

	/**
	 * Overridden to add the calendar as the Request Body.
	 * @see org.apache.commons.httpclient.methods.PutMethod#generateRequestBody()
	 */
	protected byte[] generateRequestBody()  {
		if (calendar != null){
			StringWriter writer = new StringWriter();
			try{
				calendarOutputter.output(calendar, writer);

				RequestEntity requestEntity = new StringRequestEntity(writer.toString(),
						CalDAVConstants.CONTENT_TYPE_CALENDAR,
						getCharset().toString());
				setRequestEntity(requestEntity);
			} catch (UnsupportedEncodingException e) {
				log.error("Unsupported encoding in event" + writer.toString());
				throw new RuntimeException("Problem generating calendar. ", e);
			} catch (Exception e){
				log.error("Problem generating calendar: ", e);
				throw new RuntimeException("Problem generating calendar. ", e);
			}
		}
		return super.generateRequestBody();
	}

	/**
	 * @see org.apache.commons.httpclient.methods.PutMethod#setRequestEntity(RequestEntity)
	 * @param string The requestEntity to set.
	 */
	public void setRequestEntity(String string) {
		RequestEntity requestEntity;
		try {
			requestEntity = new StringRequestEntity(string,
					CalDAVConstants.CONTENT_TYPE_CALENDAR,
					getCharset().toString());
			super.setRequestEntity(requestEntity);

		} catch (UnsupportedEncodingException e) {
			log.error("Unsupported encoding in event" + string);
			throw new RuntimeException("Problem generating calendar. ", e);
		}
	}

	/**
	 * Overridden function to add headers for ETags.
	 * @see org.apache.commons.httpclient.methods.PutMethod#addRequestHeaders(HttpState, HttpConnection)
	 */
	protected void addRequestHeaders(HttpState state, HttpConnection conn)
			throws IOException, HttpException {
		if (ifMatch || ifNoneMatch){
			String name = ifMatch ? CalDAVConstants.HEADER_IF_MATCH : CalDAVConstants.HEADER_IF_NONE_MATCH;
			String value = null;
			if (allEtags){
				value = "*";
			} else {
				StringBuffer buf = new StringBuffer();
				int x = 0;
				for (Iterator i = etags.iterator(); i.hasNext();){
					if (x > 0){
						buf.append(", ");
					}
					String etag = (String)i.next();
					buf.append(etag);
					x++;
				}
				value = buf.toString();
			}
			setRequestHeader(name, value);
		}

		super.addRequestHeaders(state, conn);
	}

	/**
	 * @see org.apache.commons.httpclient.HttpMethodBase#setPath(String)
	 */
	public void setPath(String path) {
		super.setPath(UrlUtils.removeDoubleSlashes(path));
	}

	/**
	 * @see org.apache.commons.httpclient.methods.PutMethod#hasRequestContent()
	 */
	protected boolean hasRequestContent() {
		if (calendar != null) {
			return true;
		} else {
			return super.hasRequestContent();
		}
	}
}
