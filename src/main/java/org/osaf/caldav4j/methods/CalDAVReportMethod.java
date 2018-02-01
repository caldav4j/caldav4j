/*
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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.httpclient.*;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethodBase;
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * CalDAV Report Method, which extends DavMethodBase. Implements Section 7 of RFC4791
 * @author <a href="mailto:ankushmishra9@gmail.com">Ankush Mishra</a>
 */
public class CalDAVReportMethod extends DavMethodBase {

    private static final Logger log = LoggerFactory.getLogger(CalDAVReportMethod.class);

    private boolean isCalendarResponse = false;
    private boolean isDeep = false;
    private Calendar calendarResponse = null;
    private CalDAVReportRequest reportRequest = null;
    private CalendarBuilder calendarBuilder = null;

    /**
     * Default Constructor.
     * @param uri URI to the calendar resource.
     */
    public CalDAVReportMethod(String uri) {
        super(uri);
    }

    /**
     * Depth is set to 1, by default.
     * @param uri URI to the calendar resource.
     * @param reportRequest Report for the Request to handle.
     * @throws IOException
     */
    public CalDAVReportMethod(String uri, CalDAVReportRequest reportRequest) throws IOException {
        this(uri, reportRequest, CalDAVConstants.DEPTH_1);
    }

    /**
     *
     * @param uri URI to the calendar resource.
     * @param reportRequest Report for the Request to handle.
     * @param depth Depth of the Report Request
     * @throws IOException
     */
    public CalDAVReportMethod(String uri, CalDAVReportRequest reportRequest, int depth) throws IOException {
        super(uri);
        this.reportRequest = reportRequest;
        processReportRequest(reportRequest);
        setDepth(depth);
    }

    /**
     * Sets the depth and the request body as the Report specified.
     * @param reportRequest Report for Request body
     * @throws IOException
     */
    private void processReportRequest(CalDAVReportRequest reportRequest) throws IOException {
        setRequestBody(reportRequest);
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

        setRequestHeader(new DepthHeader(depth));
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

    /**
     * @see HttpMethod#getName()
     */
    @Override
    public String getName() {
        return DavMethods.METHOD_REPORT;
    }

    /**
     *
     * @param statusCode
     * @return true if status code is {@link DavServletResponse#SC_OK 200 (OK)}
     * or {@link DavServletResponse#SC_MULTI_STATUS 207 (Multi Status)}. If the
     * report request included a depth other than {@link CalDAVConstants#DEPTH_0 0}
     * a multi status response is required.
     */
    @Override
    protected boolean isSuccess(int statusCode) {
        if (isDeep) {
            return statusCode == CaldavStatus.SC_MULTI_STATUS;
        } else {
            return statusCode == CaldavStatus.SC_OK || statusCode == CaldavStatus.SC_MULTI_STATUS;
        }
    }

    /**
     * Overriding to check if the Response contains Calendar or not.
     * @param state
     * @param conn
     */
    @Override
    protected void processResponseHeaders(HttpState state, HttpConnection conn) {
        super.processResponseHeaders(state, conn);
        Header header = getResponseHeader(CalDAVConstants.HEADER_CONTENT_TYPE);

        //Note: Sometimes this does not happen. To take that into account.
        if(header != null) {
            HeaderElement[] elements = header.getElements();
            for (HeaderElement element : elements) {
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
     * Overridden to build the Calendar from the stream provided.
     * If the calendarBuilder was not specified, then this will not work correctly.
     * @param httpState
     * @param httpConnection
     */
    @Override
    protected void processResponseBody(HttpState httpState, HttpConnection httpConnection) {
        if (getStatusCode() == CaldavStatus.SC_OK && isCalendarResponse){
            try {
                InputStream stream = getResponseBodyAsStream();
                calendarResponse = calendarBuilder.build(stream);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error while parsing Calendar response: " + e);
            }
        }
        else
            super.processResponseBody(httpState, httpConnection);
    }

    /**
     * Return the Property associated with a path.
     * @param urlPath Location of the CalendarResource
     * @param property DavPropertyName of the property whose value is to be returned.
     * @return DavProperty
     *
     *
     */
    public DavProperty getDavProperty(String urlPath, DavPropertyName property) {
        try {
            MultiStatusResponse[] responses = getResponseBodyAsMultiStatus().getResponses();
            if(responses != null && succeeded()) {
                for (MultiStatusResponse r : responses) {
                    if(r.getHref().equals(urlPath)){
                        DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                        return props.get(property);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Unable to get MultiStatusResponse. Status: " + getStatusCode());
        }

        log.warn("Can't find object at: " + urlPath);
        return null;
    }

    /**
     * Returns all the set of properties and their value, for all the hrefs
     * @param property Property Name to return.
     * @return Collection of Properties.
     */
    public Collection<DavProperty> getDavProperties(DavPropertyName property) {
        Collection<DavProperty> set = new ArrayList<DavProperty>();

        try {
            MultiStatusResponse[] responses = getResponseBodyAsMultiStatus().getResponses();
            if(responses != null && succeeded()) {
                for (MultiStatusResponse r : responses) {
                    DavPropertySet props = r.getProperties(CaldavStatus.SC_OK);
                    if(!props.isEmpty()) set.add(props.get(property));
                }
            }
        } catch (Exception e) {
            log.warn("Unable to get MultiStatusResponse. Status: " + getStatusCode());
        }

        return set;
    }

    /**
     * Returns the MultiStatusResponse to the corresponding uri.
     * @param uri URI to the calendar resource.
     * @return
     */
    public MultiStatusResponse getResponseBodyAsMultiStatusResponse(String uri) throws IOException, DavException {
        MultiStatusResponse[] responses = getResponseBodyAsMultiStatus().getResponses();
        for(MultiStatusResponse response: responses)
            if(response.getHref().equals(uri))
                return response;
        log.warn("No Response found for uri: " + uri);
        return null;
    }
}
