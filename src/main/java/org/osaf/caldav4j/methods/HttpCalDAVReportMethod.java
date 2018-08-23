package org.osaf.caldav4j.methods;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * HttpCalDAVReport Method, which extends BaseDavRequest. Implements Section
 * 7 of RFC4791
 *
 * @author <a href="mailto:ankushmishra9@gmail.com">Ankush Mishra</a> (author of orignal apache http 3 version)
 */

public class HttpCalDAVReportMethod extends BaseDavRequest {

	private static final Logger log = LoggerFactory.getLogger(HttpCalDAVReportMethod.class);
    private CalendarBuilder calendarBuilder = null;
    
    /**
    *
    * @param uri URI to the calendar resource.
    * @param reportRequest Report for the Request to handle.
    * @param depth Depth of the Report Request
    * @throws IOException
    */
   public HttpCalDAVReportMethod(URI uri, CalDAVReportRequest reportRequest, int depth) throws IOException {
       super(uri);
       processReportRequest(reportRequest);
       setDepth(depth);
   }

	/**
	 * Depth is set to 0, by default. According to the RFC Specs.
	 * @param uri URI to the calendar resource.
	 * @param reportRequest Report for the Request to handle.
	 * @throws IOException
	 */
	public HttpCalDAVReportMethod(URI uri, CalDAVReportRequest reportRequest) throws IOException {
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
        this(URI.create(uri), reportRequest, depth);
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
	   setEntity(XmlEntity.create(reportRequest));
   }

   /**
    * Change the depth of the Request.
    * @param depth
    */
   public void setDepth(int depth){
       DepthHeader dh = new DepthHeader(depth);
       setHeader(dh.getHeaderName(),dh.getHeaderValue());
   }

   @Override
   public boolean succeeded(HttpResponse response) {
	   int statusCode = response.getStatusLine().getStatusCode();
       return statusCode == CaldavStatus.SC_OK || statusCode == CaldavStatus.SC_MULTI_STATUS;
   }

	public CalendarBuilder getCalendarBuilder() {
		return calendarBuilder;
	}

	public void setCalendarBuilder(CalendarBuilder calendarBuilder) {
		this.calendarBuilder = calendarBuilder;
	}

	/**
    * Set the CalendarBuilder instance, before invoking this.
	 * Will be auto assigned if invoked from the {@link CalDAV4JMethodFactory}
    * @return If the Response was a calendar, then we return the {@link Calendar} instance,
    * else null is returned.
    */
   public Calendar getResponseBodyAsCalendar(HttpResponse response) throws IOException {
	   Calendar calendarResponse = null;
	   if(this.succeeded(response)) {
		   InputStream in = response.getEntity().getContent();
		   try {
			   calendarResponse = calendarBuilder.build(in);
		   } catch (ParserException e) {
			   throw new IOException("Error parsing calendar from response", e);
		   } finally {
			   in.close();
		   }
	   }
       return calendarResponse;
   }

   /**
    * Return the Property associated with a path.
    * @param urlPath Location of the CalendarResource
    * @param property DavPropertyName of the property whose value is to be returned.
    * @return DavProperty
    *
    *
    */
   public DavProperty getDavProperty(HttpResponse response, String urlPath, DavPropertyName property) throws DavException {
	   MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(response).getResponses();
	   if (responses != null && succeeded(response)) {
		   for (MultiStatusResponse r : responses) {
			   if (r.getHref().equals(urlPath)) {
				   DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
				   return props.get(property);
			   }
		   }
	   }
	   return null;
   }

   /**
    * Returns all the set of properties and their value, for all the hrefs
    * @param property Property Name to return.
    * @return Collection of Properties.
    */
   public Collection<DavProperty> getDavProperties(HttpResponse response, DavPropertyName property) throws DavException {
	   Collection<DavProperty> set = new ArrayList<DavProperty>();
	   MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(response).getResponses();
	   if (responses != null && succeeded(response)) {
		   for (MultiStatusResponse r : responses) {
			   DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
			   if (!props.isEmpty()) set.add(props.get(property));
		   }
	   }
	   return set;
   }

   /**
    * Returns the MultiStatusResponse to the corresponding uri.
    * @param uri URI to the calendar resource.
    * @return
    */
   public MultiStatusResponse getResponseBodyAsMultiStatusResponse(HttpResponse httpResponse, String uri) throws DavException {
	   MultiStatusResponse[] responses = getResponseBodyAsMultiStatus(httpResponse).getResponses();
	   for (MultiStatusResponse response : responses)
		   if (response.getHref().equals(uri))
			   return response;
	   return null;
   }
}
