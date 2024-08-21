/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Bobby Rullo, Mark Hobson, Roberto Polli
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

package com.github.caldav4j.methods;

import com.github.caldav4j.model.request.CalDAVReportRequest;
import com.github.caldav4j.model.request.CalendarRequest;
import com.github.caldav4j.model.request.MkCalendar;
import java.io.IOException;
import java.net.URI;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.security.AclProperty;

/**
 * Method factory for creating instances of CalDAV related Methods. This automatically handles, any
 * basic configuration required.
 */
public class CalDAV4JMethodFactory {

    private boolean validatingOutputter = false;

    private ThreadLocal<CalendarBuilder> calendarBuilderThreadLocal = new ThreadLocal<>();
    private CalendarOutputter calendarOutputter = null;

    /** Empty Constructor */
    public CalDAV4JMethodFactory() {}

    /**
     * Creates a {@link HttpPutMethod} instance.
     *
     * @param calendarRequest Object representing the method execution options.
     * @param uri URI to the Calendar resource to create.
     * @return the instance
     */
    public HttpPutMethod createPutMethod(URI uri, CalendarRequest calendarRequest) {
        return new HttpPutMethod(uri, calendarRequest, getCalendarOutputterInstance());
    }

    /**
     * Creates a {@link HttpPutMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @param calendarRequest Object representing the method execution options.
     * @return the instance
     */
    public HttpPutMethod createPutMethod(String uri, CalendarRequest calendarRequest) {
        return new HttpPutMethod(uri, calendarRequest, getCalendarOutputterInstance());
    }

    /**
     * Creates a {@link HttpPostMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @param calendarRequest Object representing the method execution options.
     * @return the instance
     */
    public HttpPostMethod createPostMethod(URI uri, CalendarRequest calendarRequest) {
        return new HttpPostMethod(uri, calendarRequest, getCalendarOutputterInstance());
    }

    /**
     * Creates a {@link HttpPostMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @param calendarRequest Object representing the method execution options.
     * @return the instance
     */
    public HttpPostMethod createPostMethod(String uri, CalendarRequest calendarRequest) {
        return new HttpPostMethod(uri, calendarRequest, getCalendarOutputterInstance());
    }

    /**
     * Creates a {@link HttpMkCalendarMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @return the instance
     */
    public HttpMkCalendarMethod createMkCalendarMethod(URI uri) {
        return new HttpMkCalendarMethod(uri);
    }

    /**
     * Creates a {@link HttpMkCalendarMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @param displayName Display Name of Calendar
     * @param description Description of Calendar.
     * @param descriptionLanguage Language of the Description. Optional.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod createMkCalendarMethod(
            URI uri, String displayName, String description, String descriptionLanguage)
            throws IOException {
        return new HttpMkCalendarMethod(uri, displayName, description, descriptionLanguage);
    }

    /**
     * Creates a {@link HttpMkCalendarMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @param mkCalendar {@link MkCalendar} instance which contains all the details for creating a
     *     calendar.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod createMkCalendarMethod(URI uri, MkCalendar mkCalendar)
            throws IOException {
        return new HttpMkCalendarMethod(uri, mkCalendar);
    }

    /**
     * Creates a {@link HttpMkCalendarMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @param displayName Display Name of Calendar
     * @param description Description of Calendar.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod createMkCalendarMethod(
            URI uri, String displayName, String description) throws IOException {
        return new HttpMkCalendarMethod(uri, displayName, description);
    }

    /**
     * Creates a {@link HttpMkCalendarMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @return the instance
     */
    public HttpMkCalendarMethod createMkCalendarMethod(String uri) {
        return new HttpMkCalendarMethod(uri);
    }

    /**
     * Creates a {@link HttpMkCalendarMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @param displayName Display Name of Calendar
     * @param description Description of Calendar.
     * @param descriptionLanguage Language of the Description. Optional.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod createMkCalendarMethod(
            String uri, String displayName, String description, String descriptionLanguage)
            throws IOException {
        return new HttpMkCalendarMethod(uri, displayName, description, descriptionLanguage);
    }

    /**
     * Creates a {@link HttpMkCalendarMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @param mkCalendar {@link MkCalendar} instance which contains all the details for creating a
     *     calendar.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod createMkCalendarMethod(String uri, MkCalendar mkCalendar)
            throws IOException {
        return new HttpMkCalendarMethod(uri, mkCalendar);
    }

    /**
     * Creates a {@link HttpMkCalendarMethod} instance.
     *
     * @param uri URI to the Calendar resource to create.
     * @param displayName Display Name of Calendar
     * @param description Description of Calendar.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod createMkCalendarMethod(
            String uri, String displayName, String description) throws IOException {
        return new HttpMkCalendarMethod(uri, displayName, description);
    }

    /**
     * Creates a {@link HttpPropFindMethod} instance.
     *
     * @param uri URI to the Calendar resource.
     * @param names Properties to make the Propfind request for.
     * @param depth Depth of the Request
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpPropFindMethod createPropFindMethod(URI uri, DavPropertyNameSet names, int depth)
            throws IOException {
        return new HttpPropFindMethod(uri, names, depth);
    }

    /**
     * Creates a {@link HttpPropFindMethod} instance.
     *
     * @param uri URI to the Calendar resource.
     * @param propfindtype Type of Propfind Call.
     * @param names Properties to make the Propfind request for.
     * @param depth Depth of the Request
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpPropFindMethod createPropFindMethod(
            URI uri, int propfindtype, DavPropertyNameSet names, int depth) throws IOException {
        return new HttpPropFindMethod(uri, propfindtype, names, depth);
    }

    /**
     * Creates a {@link HttpPropFindMethod} instance.
     *
     * @param uri URI to the Calendar resource.
     * @param names Properties to make the Propfind request for.
     * @param depth Depth of the Request
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpPropFindMethod createPropFindMethod(String uri, DavPropertyNameSet names, int depth)
            throws IOException {
        return new HttpPropFindMethod(uri, names, depth);
    }

    /**
     * Creates a {@link HttpPropFindMethod} instance.
     *
     * @param uri URI to the Calendar resource.
     * @param propfindtype Type of Propfind Call.
     * @param names Properties to make the Propfind request for.
     * @param depth Depth of the Request
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpPropFindMethod createPropFindMethod(
            String uri, int propfindtype, DavPropertyNameSet names, int depth) throws IOException {
        return new HttpPropFindMethod(uri, propfindtype, names, depth);
    }

    /**
     * Creates a {@link HttpCalDAVReportMethod} instance.
     *
     * @param uri URI to the Calendar resource.
     * @param request The Report to make a request for.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpCalDAVReportMethod createCalDAVReportMethod(URI uri, CalDAVReportRequest request)
            throws IOException {
        HttpCalDAVReportMethod m = new HttpCalDAVReportMethod(uri, request);
        m.setCalendarBuilder(getCalendarBuilderInstance());
        return m;
    }

    /**
     * Creates a {@link HttpCalDAVReportMethod} instance.
     *
     * @param uri URI to the Calendar resource.
     * @param request The Report to make a request for.
     * @param depth Depth of the request, the values possible are: {@code CalDAVConstants.DEPTH_0},
     *     {@code CalDAVConstants.DEPTH_1}, {@code CalDAVConstants.DEPTH_INFINITY}
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpCalDAVReportMethod createCalDAVReportMethod(
            URI uri, CalDAVReportRequest request, int depth) throws IOException {
        HttpCalDAVReportMethod m = new HttpCalDAVReportMethod(uri, request, depth);
        m.setCalendarBuilder(getCalendarBuilderInstance());
        return m;
    }

    /**
     * Creates a {@link HttpCalDAVReportMethod} instance.
     *
     * @param uri URI to the Calendar resource.
     * @param request The Report to make a request for.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpCalDAVReportMethod createCalDAVReportMethod(String uri, CalDAVReportRequest request)
            throws IOException {
        return createCalDAVReportMethod(URI.create(uri), request);
    }

    /**
     * Creates a {@link HttpCalDAVReportMethod} instance.
     *
     * @param uri URI to the Calendar resource.
     * @param request The Report to make a request for.
     * @param depth Depth of the request, the values possible are: * {@code
     *     CalDAVConstants.DEPTH_0}, {@code CalDAVConstants.DEPTH_1}, * {@code
     *     CalDAVConstants.DEPTH_INFINITY}
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpCalDAVReportMethod createCalDAVReportMethod(
            String uri, CalDAVReportRequest request, int depth) throws IOException {
        return createCalDAVReportMethod(URI.create(uri), request, depth);
    }

    /**
     * Creates a {@link HttpGetMethod} instance.
     *
     * @param uri URI to the resource.
     * @return the instance
     */
    public HttpGetMethod createGetMethod(URI uri) {
        return new HttpGetMethod(uri, getCalendarBuilderInstance());
    }

    /**
     * Creates a {@link HttpGetMethod} instance.
     *
     * @param uri URI to the resource.
     * @return the instance
     */
    public HttpGetMethod createGetMethod(String uri) {
        return new HttpGetMethod(uri, new CalendarBuilder());
    }

    /**
     * Creates a {@link HttpAclMethod} instance.
     *
     * @param uri URI to the resource.
     * @param property The AclProperty to make the request with.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpAclMethod createAclMethod(URI uri, AclProperty property) throws IOException {
        return new HttpAclMethod(uri, property);
    }

    /**
     * Creates a {@link HttpAclMethod} instance.
     *
     * @param uri URI to the resource.
     * @param property The AclProperty to make the request with.
     * @return the instance
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpAclMethod createAclMethod(String uri, AclProperty property) throws IOException {
        return new HttpAclMethod(uri, property);
    }

    /**
     * Creates a {@link HttpDeleteMethod} instance.
     *
     * @param uri URI to the resource.
     * @return the instance
     */
    public HttpDeleteMethod createDeleteMethod(String uri) {
        return new HttpDeleteMethod(uri);
    }

    /**
     * Creates a {@link HttpDeleteMethod} instance.
     *
     * @param uri URI to the resource.
     * @return the instance
     */
    public HttpDeleteMethod createDeleteMethod(URI uri) {
        return new HttpDeleteMethod(uri);
    }

    /**
     * Creates a {@link HttpDeleteMethod} instance.
     *
     * @param uri URI to the resource.
     * @param etag Etag to check the resource against. Note: should be a quoted string
     * @return the instance
     */
    public HttpDeleteMethod createDeleteMethod(String uri, String etag) {
        return new HttpDeleteMethod(uri, etag);
    }

    /**
     * Creates a {@link HttpDeleteMethod} instance.
     *
     * @param uri URI to the resource.
     * @param etag Etag to check the resource against. Note: should be a quoted string
     * @return the instance
     */
    public HttpDeleteMethod createDeleteMethod(URI uri, String etag) {
        return new HttpDeleteMethod(uri, etag);
    }

    /**
     * @return True or False based on the Calendar Validating Outputter setting.
     */
    public boolean isCalendarValidatingOutputter() {
        return validatingOutputter;
    }

    /**
     * Set whether the CalendarBuilder is Validating or not.
     *
     * @param validatingOutputter Value to set.
     */
    public void setCalendarValidatingOutputter(boolean validatingOutputter) {
        this.validatingOutputter = validatingOutputter;
    }

    /**
     * Return the CalendarOuputter instance.
     *
     * @return CalendarOutputter
     */
    protected synchronized CalendarOutputter getCalendarOutputterInstance() {
        if (calendarOutputter == null) {
            calendarOutputter = new CalendarOutputter(validatingOutputter);
        }
        return calendarOutputter;
    }

    /**
     * Return the CalendarBuilder instance.
     *
     * @return CalendarBuilder
     */
    private CalendarBuilder getCalendarBuilderInstance() {
        CalendarBuilder builder = calendarBuilderThreadLocal.get();
        if (builder == null) {
            builder = new CalendarBuilder();
            calendarBuilderThreadLocal.set(builder);
        }
        return builder;
    }
}
