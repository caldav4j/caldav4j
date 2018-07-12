package org.osaf.caldav4j.methods;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

/** @{link {@link #generateRequestBody()})} must be called before executing this method!*/
public class HttpPostMethod extends HttpPost{

    private static final Logger log = LoggerFactory.getLogger(PostMethod.class);
    
    protected Calendar calendar = null; 
    private String procID = CalDAVConstants.PROC_ID_DEFAULT;
    private CalendarOutputter calendarOutputter = null;
    private Set<String> etags = new HashSet<String>();
    private boolean ifMatch = false;
    private boolean ifNoneMatch = false;
    private boolean allEtags = false;

    public HttpPostMethod() {
    	super(); 
    }
    
    public String getMethod() {
    	return CalDAVConstants.METHOD_POST;
    }
    
    /**
     * The set of eTags that will be used in "if-none-match" or "if-match" if the
     * ifMatch or ifNoneMatch properties are set
     * @return
     */
    public Set getEtags() {
        return etags;
    }

    public void setEtags(Set<String> etags) {
        this.etags = etags;
    }
    
    /**
     * Add's the etag provided to the "if-none-match" or "if-match" header.
     * 
     * Note - You MUST provide a quoted string!
     * @param etag
     */
    public void addEtag(String etag){
        etags.add(etag);
    }
    
    
    public void removeEtag(String etag){
        etags.remove(etag);
    }

    /**
     * If true the "if-match" conditional header is used with the etags set in the 
     * etags property.
     * @return
     */
    public boolean isIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(boolean ifMatch) {
        this.ifMatch = ifMatch;
    }

    /**
     * If true the "if-none-match" conditional header is used with the etags set in the 
     * etags property.
     * @return
     */
    public boolean isIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(boolean ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public boolean isAllEtags() {
        return allEtags;
    }

    public void setAllEtags(boolean allEtags) {
        this.allEtags = allEtags;
    }
    
    public void setRequestBody(Calendar calendar){
        this.calendar = calendar;
    }
    
    public void setRequestBody(VEvent vevent, VTimeZone vtimeZone){
        calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Open Source Applications Foundation//NONSGML Scooby Server//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getComponents().add(vevent);
        if (vtimeZone != null){
            calendar.getComponents().add(vtimeZone);
        }
    }
    
    public void setRequestBody(VEvent vevent){
        setRequestBody(vevent, null);
    }
    
    /**
     * The ProcID to use when creating a new VCALENDAR component
     * @return
     */
    public String getProcID() {
        return procID;
    }
    
    /**
     * Sets the ProcID to use when creating a new VCALENDAR component
     * @param procID
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
    
   
    /** In httpclient 3.1 this was a protected method which was called automatically. 
     * In httpclient 4 this method must be called explicitly to set the request entity. */
    public void generateRequestBody()  {  
        if (calendar != null){
            StringWriter writer = new StringWriter();
            try{
                calendarOutputter.output(calendar, writer);
                
                ContentType ct = ContentType.create(CalDAVConstants.CONTENT_TYPE_CALENDAR,Charset.defaultCharset().toString());
                setEntity(new StringEntity(writer.toString(),ct));
            } catch (UnsupportedCharsetException e) {
            	 log.error("Unsupported encoding in event" + writer.toString());
            	 throw new RuntimeException("Problem generating calendar. ", e);
            } catch (Exception e){
                log.error("Problem generating calendar: ", e);
                throw new RuntimeException("Problem generating calendar. ", e);
            }
        }
    }
    
    public void addRequestHeaders() {
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
            addHeader(name, value);
        }
        addHeader(CalDAVConstants.HEADER_CONTENT_TYPE, CalDAVConstants.CONTENT_TYPE_CALENDAR);
    }

    /**
     * @see org.apache.commons.httpclient.HttpMethodBase#setPath(String)
     */
    public void setPath(String path) {
    	super.setURI(URI.create(UrlUtils.removeDoubleSlashes(path)));
    }

    
    
	
}
