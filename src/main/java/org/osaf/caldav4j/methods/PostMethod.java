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
import org.apache.commons.httpclient.*;
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

public class PostMethod extends org.apache.commons.httpclient.methods.PostMethod{
    private static final Logger log = LoggerFactory.getLogger(PostMethod.class);
   
    protected Calendar calendar = null; 
    private String procID = CalDAVConstants.PROC_ID_DEFAULT;
    private CalendarOutputter calendarOutputter = null;
    private Set<String> etags = new HashSet<String>();
    private boolean ifMatch = false;
    private boolean ifNoneMatch = false;
    private boolean allEtags = false;
    
    public PostMethod (){
        super();
    }

    /**
     * @see HttpMethod#getName()
     */
    public String getName() {
        return CalDAVConstants.METHOD_POST;
    }
    
    /**
     * @return The set of eTags that will be used in "if-none-match" or "if-match" if the
     * ifMatch or ifNoneMatch properties are set
     */
    public Set getEtags() {
        return etags;
    }

	/**
	 * @param etags The set of eTags that will be used in "if-none-match" or "if-match"
	 */
	public void setEtags(Set<String> etags) {
        this.etags = etags;
    }
    
    /**
     * Add's the etag provided to the "if-none-match" or "if-match" header.
     * 
     * Note - You MUST provide a quoted string!
     * @param etag ETag to add
     */
    public void addEtag(String etag){
        etags.add(etag);
    }

	/**
	 * Remove the ETag from set.
	 * @param etag ETag to remove.
	 */
	public void removeEtag(String etag){
        etags.remove(etag);
    }

    /**
     * @return Returns true if the "if-match" conditional header is set. The matched ETags are set through
     * {@link #setEtags(Set)} or {@link #setAllEtags(boolean)}
     */
    public boolean isIfMatch() {
        return ifMatch;
    }

	/**
	 * Set the "if-match" conditional header.
	 * @param ifMatch True to set, else False to disable.
	 */
	public void setIfMatch(boolean ifMatch) {
        this.ifMatch = ifMatch;
    }

    /**
     * @return Returns true if the "if-none-match" conditional header is set. The ETags are set through
     * {@link #setEtags(Set)} or {@link #setAllEtags(boolean)}
     */
    public boolean isIfNoneMatch() {
        return ifNoneMatch;
    }

	/**
	 * Set the "if-none-match" conditional header.
	 * @param ifNoneMatch True to set, else False to disable.
	 */
	public void setIfNoneMatch(boolean ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

	/**
	 * @return True if enabled, else false.
	 */
	public boolean isAllEtags() {
        return allEtags;
    }

	/**
	 * Set the value of ETags as "*" i.e. match or do not match all ETags. Used along with
	 * {@link #setIfMatch(boolean)} or {@link #setIfNoneMatch(boolean)}
	 * @param allEtags True to enable, else disable
	 */
	public void setAllEtags(boolean allEtags) {
        this.allEtags = allEtags;
    }

	/**
	 * @param calendar Calendar to Set as request.
	 */
	public void setRequestBody(Calendar calendar){
        this.calendar = calendar;
    }

	/**
	 * Convenience method for creating a calendar from VEvent and VTimezone
	 * @param vevent Event
	 * @param vtimeZone Timezone
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
	 * Convenience method for creating a request calendar from VEvent without a timezone.
	 * @param vevent Event
	 */
	public void setRequestBody(VEvent vevent){
        setRequestBody(vevent, null);
    }
    
    /**
     * @return The ProcID to use when creating a new VCALENDAR component
     */
    public String getProcID() {
        return procID;
    }
    
    /**
     * @param procID The ProcID to use when creating a new VCALENDAR component
     */
    public void setProcID(String procID) {
        this.procID = procID;
    }

    public CalendarOutputter getCalendarOutputter() {
        return calendarOutputter;
    }

    public void setCalendarOutputter(CalendarOutputter calendarOutputter) {
        this.calendarOutputter = calendarOutputter;
    }

    protected byte[] generateRequestBody()  {
        if (calendar != null){
            StringWriter writer = new StringWriter();
            try{
                calendarOutputter.output(calendar, writer);
                
                RequestEntity requestEntity = new StringRequestEntity(writer.toString(),
						CalDAVConstants.CONTENT_TYPE_CALENDAR, 
						Charset.defaultCharset().toString());
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
                for (Iterator<String> i = etags.iterator();i.hasNext();){
                    if (x > 0){
                        buf.append(", ");
                    }
                    String etag = (String)i.next();
                    buf.append(etag);
                    x++;
                }
                value = buf.toString();
            }
            addRequestHeader(name, value);
        }
        addRequestHeader(CalDAVConstants.HEADER_CONTENT_TYPE, CalDAVConstants.CONTENT_TYPE_CALENDAR);
        super.addRequestHeaders(state, conn);
    }

    /**
     * @see org.apache.commons.httpclient.HttpMethodBase#setPath(String)
     */
    public void setPath(String path) {
    	super.setPath(UrlUtils.removeDoubleSlashes(path));
    }
    
    protected boolean hasRequestContent() {
        if (calendar != null) {
            return true;
        } else {
            return super.hasRequestContent();
        }
    }    
}
