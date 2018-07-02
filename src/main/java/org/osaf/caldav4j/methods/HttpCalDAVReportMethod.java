package org.osaf.caldav4j.methods;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.model.request.CalDAVReportRequest;
import org.osaf.caldav4j.util.CaldavStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;

/**
 * HttpCalDAVReport Method, which extends BaseDavRequest. Implements Section 7 of RFC4791
 * @author <a href="mailto:ankushmishra9@gmail.com">Ankush Mishra</a> (author of orignal apache http 3 version)
 */

public class HttpCalDAVReportMethod extends BaseDavRequest {

	private static final Logger log = LoggerFactory.getLogger(HttpCalDAVReportMethod.class);
	
    private boolean isCalendarResponse = false;
    private boolean isDeep = false;
    private Calendar calendarResponse = null;
    private CalDAVReportRequest reportRequest = null;
    private CalendarBuilder calendarBuilder = null;
	
    /**
     * Default Constructor.
     * @param uri URI to the calendar resource.
     */
    public HttpCalDAVReportMethod(String uri) {
        super(URI.create(uri));
    }
    
    
    /**
     * Depth is set to 1, by default.
     * @param uri URI to the calendar resource.
     * @param reportRequest Report for the Request to handle.
     * @throws IOException
     */
    public HttpCalDAVReportMethod(String uri, CalDAVReportRequest reportRequest) throws IOException {
        this(uri, reportRequest, CalDAVConstants.DEPTH_1);
    }
    
    /**
    *
    * @param uri URI to the calendar resource.
    * @param reportRequest Report for the Request to handle.
    * @param depth Depth of the Report Request
    * @throws IOException
    */
   public HttpCalDAVReportMethod(String uri, CalDAVReportRequest reportRequest, int depth) throws IOException {
       super(URI.create(uri));
       this.reportRequest = reportRequest;
       processReportRequest(reportRequest);
       setDepth(depth);
   }
   
   @Override
   public String getMethod() {
       return DavMethods.METHOD_REPORT;
   }   
    
   /**
    * Sets the depth and the request body as the Report specified.
    * @param reportRequest Report for Request body
    * @throws IOException
    */
   private void processReportRequest(CalDAVReportRequest reportRequest) throws IOException {
	   super.setEntity(XmlEntity.create(reportRequest));	   
   }
   
   /**
    * Set the current request as the Report specified.
    * @param reportRequest Report to set.
    * @throws IOException
    */
   public void setReportRequest(CalDAVReportRequest reportRequest) throws IOException {
       this.reportRequest = reportRequest;
       processReportRequest(reportRequest);
   }

   /**
    * Change the depth of the Request.
    * @param depth
    */
   public void setDepth(int depth){
       isDeep = depth > CalDAVConstants.DEPTH_0;
       DepthHeader dh = new DepthHeader(depth);
       setHeader(dh.getHeaderName(),dh.getHeaderValue());
   }

   /**
    * Set the Calendar Builder used to create the calendar.
    * @param calendarBuilder
    */
   public void setCalendarBuilder(CalendarBuilder calendarBuilder) {
       this.calendarBuilder = calendarBuilder;
   }

   /**
    * Retrieve the current Calendar Builder.
    * @return
    */
   public CalendarBuilder getCalendarBuilder() {
       return this.calendarBuilder;
   }


   @Override
   public boolean succeeded(HttpResponse response) {
	   int statusCode = response.getStatusLine().getStatusCode();
       if (isDeep) {
           return statusCode == CaldavStatus.SC_MULTI_STATUS;
       } else {
           return statusCode == CaldavStatus.SC_OK || statusCode == CaldavStatus.SC_MULTI_STATUS;
       }
   }   

   
   /**
    * Check if the Response contains Calendar or not.
    * @param response
    */
   protected void processResponseHeaders(HttpResponse response) {
       Header header = response.getFirstHeader(CalDAVConstants.HEADER_CONTENT_TYPE);

       //Note: Sometimes this does not happen. To take that into account.
       if(header != null) {
           for (HeaderElement element : header.getElements()) {
               if (element.getName().equals(CalDAVConstants.CONTENT_TYPE_CALENDAR)) {
                   isCalendarResponse = true;
                   log.info("Response Content-Type: text/calendar");
               }
           }
       }
   }

   /**
    * @return If the Response was a calendar, then we return the {@link Calendar} instance,
    * else null is returned.
    */
   public Calendar getResponseBodyAsCalendar(){
       return this.calendarResponse;
   }

   /**
    * Builds the Calendar from the stream provided.
    * If the calendarBuilder was not specified, then this will not work correctly.
    * @param response
    */
   protected void processResponseBody(HttpResponse response) {
	   int statusCode = response.getStatusLine().getStatusCode();
       if (statusCode == CaldavStatus.SC_OK && isCalendarResponse){
           try {
               InputStream stream = response.getEntity().getContent(); //TODO !A! 
               calendarResponse = calendarBuilder.build(stream);
           } catch (Exception e) {
               e.printStackTrace();                                    //TODO !A! Do we really want this in here?                                    
               log.error("Error while parsing Calendar response: " + e);
           }
       }
       else 
           EntityUtils.consumeQuietly(response.getEntity()); //TODO !A! HttpClient 3 version called super.processResponseBody(). Not applicable here.
   }

   /**
    * Return the Property associated with a path.
    * @param urlPath Location of the CalendarResource
    * @param property DavPropertyName of the property whose value is to be returned.
    * @return DavProperty
    *
    *
    */
   public DavProperty getDavProperty(HttpResponse response, String urlPath, DavPropertyName property) {
       try {
           MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(response).getResponses();
           if(responses != null && succeeded(response)) {
               for (MultiStatusResponse r : responses) {
                   if(r.getHref().equals(urlPath)){
                       DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                       return props.get(property);
                   }
               }
           }
       } catch (Exception e) {
           log.warn("Unable to get MultiStatusResponse. Status: " + response.getStatusLine().getStatusCode());
       }

       log.warn("Can't find object at: {}", urlPath);
       return null;
   }

   /**
    * Returns all the set of properties and their value, for all the hrefs
    * @param property Property Name to return.
    * @return Collection of Properties.
    */
   public Collection<DavProperty> getDavProperties(HttpResponse response, DavPropertyName property) {
       Collection<DavProperty> set = new ArrayList<DavProperty>();

       try {
           MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(response).getResponses();
           if(responses != null && succeeded(response)) {
               for (MultiStatusResponse r : responses) {
                   DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                   if(!props.isEmpty()) set.add(props.get(property));
               }
           }
       } catch (Exception e) {
           log.warn("Unable to get MultiStatusResponse. Status: " + response.getStatusLine().getStatusCode());
       }

       return set;
   }

   /**
    * Returns the MultiStatusResponse to the corresponding uri.
    * @param uri URI to the calendar resource.
    * @return
    */
   public MultiStatusResponse getResponseBodyAsMultiStatusResponse(HttpResponse httpResponse, String uri) throws IOException, DavException {
       MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(httpResponse).getResponses();
       for(MultiStatusResponse response: responses)
           if(response.getHref().equals(uri))
               return response;
       log.warn("No Response found for uri: {}", uri);
       return null;
   }

    
}
