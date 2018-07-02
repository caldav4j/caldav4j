package org.osaf.caldav4j.methods;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.model.request.CalendarDescription;
import org.osaf.caldav4j.model.request.DisplayName;
import org.osaf.caldav4j.model.request.MkCalendar;
import org.osaf.caldav4j.model.request.Prop;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.UrlUtils;
import org.w3c.dom.Document;


public class HttpMkCalendarMethod extends BaseDavRequest {


	/**
	 * Standard calendar properties
	 */
	
	protected String CALENDAR_DESCRIPTION = "calendar-description";

	protected MkCalendar mkCalendar = null;
	
    // --------------------------------------------------------- Public Methods    
    public HttpMkCalendarMethod(String uri,  String DisplayName, String description, String DescriptionLang)
            throws IOException {
		super(URI.create(UrlUtils.removeDoubleSlashes(uri)));
        Prop p = new Prop();
        if(DisplayName != null)
            p.add(new DisplayName(DisplayName));

        if(description != null)
            p.add(new CalendarDescription(description, DescriptionLang));
        mkCalendar = new MkCalendar(p);
        processRequest();
	}
    
	public HttpMkCalendarMethod(String uri, String DisplayName, String description) throws IOException {
        this(uri, DisplayName, description, null);
    }

    public HttpMkCalendarMethod(String uri) {  
        super(URI.create(uri));
    }

    public HttpMkCalendarMethod(String uri, MkCalendar m) throws IOException {
        super(URI.create(UrlUtils.removeDoubleSlashes(uri)));
        if(m != null)
            this.mkCalendar = m;
        processRequest();
    }
    
    
	private void processRequest() throws IOException { 

        try {
            Document d = DomUtil.createDocument();
            d.appendChild(mkCalendar.toXml(d));
            super.setEntity(XmlEntity.create(d));
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    
	 //TODO !A! this needs to be called explicitly before executing the request	
    public void addRequestHeaders() throws IOException
    {
    	addHeader(CalDAVConstants.HEADER_CONTENT_TYPE, CalDAVConstants.CONTENT_TYPE_TEXT_XML);
    }
    
    public void setPath(String path) {
    	super.setURI(URI.create(UrlUtils.removeDoubleSlashes(path)));    	
    }
    
    // --------------------------------------------------- WebdavMethod Methods    

    @Override
    public String getMethod() {
        return CalDAVConstants.METHOD_MKCALENDAR;
    }
    
    //!A! Done
    @Override
    public boolean succeeded(HttpResponse response) {
 	   int statusCode = response.getStatusLine().getStatusCode();
 	   return statusCode == CaldavStatus.SC_CREATED;
    }


}
