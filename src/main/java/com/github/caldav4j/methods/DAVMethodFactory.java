package com.github.caldav4j.methods;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.security.AclProperty;

import com.github.caldav4j.model.request.CalDAVReportRequest;
import com.github.caldav4j.model.request.MkCalendar;
import com.github.caldav4j.model.request.ResourceRequest;

public abstract class DAVMethodFactory<T extends Serializable> {

	protected ResourceParser<T> parser;
	
	protected abstract ResourceParser<T> buildResourceParser();

	final protected ResourceParser<T> getResourceParser() {
		if(parser == null) {
			parser = buildResourceParser();
		}
		return parser;
	}
	
	/**
	 * Creates a {@link HttpPutMethod} instance.
	 * @param calendarRequest Object representing the method execution options.
	 * @param uri URI to the Calendar resource to create. 
	 * @return the instance
	 */
	public HttpPutMethod<T> createPutMethod(URI uri, ResourceRequest<T> calendarRequest){
		return new HttpPutMethod<>(uri, calendarRequest, getResourceParser());
	}

	/**
	 * Creates a {@link HttpPutMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param calendarRequest Object representing the method execution options.   
	 * @return the instance
	 */
	public HttpPutMethod<T> createPutMethod(String uri, ResourceRequest<T> calendarRequest){
		return new HttpPutMethod<>(uri, calendarRequest, getResourceParser());
	}

	/**
	 * Creates a {@link HttpPostMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param calendarRequest Object representing the method execution options.
	 * @return the instance
	 */
	public HttpPostMethod<T> createPostMethod(URI uri, ResourceRequest<T> calendarRequest){
		return new HttpPostMethod<>(uri, calendarRequest, getResourceParser());
	}

	/**
	 * Creates a {@link HttpPostMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param calendarRequest Object representing the method execution options.
	 * @return the instance
	 */
	public HttpPostMethod<T> createPostMethod(String uri, ResourceRequest<T> calendarRequest){
		return new HttpPostMethod<>(uri, calendarRequest, getResourceParser());
	}

	/**
	 * Creates a {@link HttpMkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @return the instance
	 */
	public HttpMkCalendarMethod createMkCalendarMethod(URI uri) {
		return new HttpMkCalendarMethod(uri);
	}

	/**
	 * Creates a {@link HttpMkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param displayName Display Name of Calendar
	 * @param description Description of Calendar.
	 * @param descriptionLanguage Language of the Description. Optional.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpMkCalendarMethod createMkCalendarMethod(URI uri, String displayName, String description, String descriptionLanguage) throws IOException {
		return new HttpMkCalendarMethod(uri, displayName, description, descriptionLanguage);
	}

	/**
	 * Creates a {@link HttpMkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param mkCalendar {@link MkCalendar} instance which
	 * contains all the details for creating a calendar.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpMkCalendarMethod createMkCalendarMethod(URI uri, MkCalendar mkCalendar) throws IOException {
		return new HttpMkCalendarMethod(uri, mkCalendar);
	}


	 /** Creates a {@link HttpMkCalendarMethod} instance.
	  * @param uri URI to the Calendar resource to create.
	  * @param displayName Display Name of Calendar
	  * @param description Description of Calendar.
	  * @return the instance
	  * @throws IOException if error occurred during parsing of parameters
	  */
	public HttpMkCalendarMethod createMkCalendarMethod(URI uri, String displayName, String description) throws IOException {
		return new HttpMkCalendarMethod(uri, displayName, description);
	}

	/**
	 * Creates a {@link HttpMkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @return the instance
	 */
	public HttpMkCalendarMethod createMkCalendarMethod(String uri) {
		return new HttpMkCalendarMethod(uri);
	}

	/**
	 * Creates a {@link HttpMkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param displayName Display Name of Calendar
	 * @param description Description of Calendar.
	 * @param descriptionLanguage Language of the Description. Optional.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpMkCalendarMethod createMkCalendarMethod(String uri, String displayName, String description, String descriptionLanguage) throws IOException {
		return new HttpMkCalendarMethod(uri, displayName, description, descriptionLanguage);
	}

	/**
	 * Creates a {@link HttpMkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param mkCalendar {@link MkCalendar} instance which
	 * contains all the details for creating a calendar.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpMkCalendarMethod createMkCalendarMethod(String uri, MkCalendar mkCalendar) throws IOException {
		return new HttpMkCalendarMethod(uri, mkCalendar);
	}

	/** Creates a {@link HttpMkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param displayName Display Name of Calendar
	 * @param description Description of Calendar.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpMkCalendarMethod createMkCalendarMethod(String uri, String displayName, String description) throws IOException {
		return new HttpMkCalendarMethod(uri, displayName, description);
	}

	/**
	 * Creates a {@link HttpPropFindMethod} instance.
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
	 * @param uri URI to the Calendar resource.
	 * @param propfindtype Type of Propfind Call.
	 * @param names Properties to make the Propfind request for.
	 * @param depth Depth of the Request
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpPropFindMethod createPropFindMethod(URI uri, int propfindtype, DavPropertyNameSet names, int depth) throws IOException {
		return new HttpPropFindMethod(uri, propfindtype, names, depth);
	}

	/**
	 * Creates a {@link HttpPropFindMethod} instance.
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
	 * @param uri URI to the Calendar resource.
	 * @param propfindtype Type of Propfind Call.
	 * @param names Properties to make the Propfind request for.
	 * @param depth Depth of the Request
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpPropFindMethod createPropFindMethod(String uri, int propfindtype, DavPropertyNameSet names, int depth) throws IOException {
		return new HttpPropFindMethod(uri, propfindtype, names, depth);
	}

	/**
	 * Creates a {@link HttpCalDAVReportMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param request The Report to make a request for.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpCalDAVReportMethod<T> createCalDAVReportMethod(URI uri, CalDAVReportRequest request) throws IOException {
		HttpCalDAVReportMethod<T> m = new HttpCalDAVReportMethod<>(uri, request);
		m.setCalendarBuilder(getResourceParser());
		return m;
	}

	/**
	 * Creates a {@link HttpCalDAVReportMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param request The Report to make a request for.
	 * @param depth Depth of the request, the values possible are:
	 *              {@code CalDAVConstants.DEPTH_0}, {@code CalDAVConstants.DEPTH_1},
	 *              {@code CalDAVConstants.DEPTH_INFINITY}
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpCalDAVReportMethod<T> createCalDAVReportMethod(URI uri, CalDAVReportRequest request, int depth) throws IOException {
		HttpCalDAVReportMethod<T> m = new HttpCalDAVReportMethod<>(uri, request, depth);
		m.setCalendarBuilder(getResourceParser());
		return m;
	}

	/**
	 * Creates a {@link HttpCalDAVReportMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param request The Report to make a request for.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpCalDAVReportMethod<T> createCalDAVReportMethod(String uri, CalDAVReportRequest request) throws IOException {
		return createCalDAVReportMethod(URI.create(uri), request);
	}

	/**
	 * Creates a {@link HttpCalDAVReportMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param request The Report to make a request for.
	 * @param depth Depth of the request, the values possible are:
	 * 	 *              {@code CalDAVConstants.DEPTH_0}, {@code CalDAVConstants.DEPTH_1},
	 * 	 *              {@code CalDAVConstants.DEPTH_INFINITY}
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public HttpCalDAVReportMethod<T> createCalDAVReportMethod(String uri, CalDAVReportRequest request, int depth) throws IOException {
		return createCalDAVReportMethod(URI.create(uri), request, depth);
	}

	/**
	 * Creates a {@link HttpGetMethod} instance.
	 * @param uri URI to the resource.
	 * @return the instance
	 */
	public HttpGetMethod<T> createGetMethod(URI uri) {
		return new HttpGetMethod<>(uri, getResourceParser());
	}

	/**
	 * Creates a {@link HttpGetMethod} instance.
	 * @param uri URI to the resource.
	 * @return the instance
	 */
	public HttpGetMethod<T> createGetMethod(String uri) {
		return new HttpGetMethod<>(uri, getResourceParser());
	}

	/**
	 * Creates a {@link HttpAclMethod} instance.
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
	 * @param uri URI to the resource.
	 * @return the instance
	 */
	public HttpDeleteMethod createDeleteMethod(String uri) {
		return new HttpDeleteMethod(uri);
	}

	/**
	 * Creates a {@link HttpDeleteMethod} instance.
	 * @param uri URI to the resource.
	 * @return the instance
	 */
	public HttpDeleteMethod createDeleteMethod(URI uri) {
		return new HttpDeleteMethod(uri);
	}

	/**
	 * Creates a {@link HttpDeleteMethod} instance.
	 * @param uri URI to the resource.
	 * @param etag Etag to check the resource against.
	 *             Note: should be a quoted string
	 * @return the instance
	 */
	public HttpDeleteMethod createDeleteMethod(String uri, String etag) {
		return new HttpDeleteMethod(uri, etag);
	}

	/**
	 * Creates a {@link HttpDeleteMethod} instance.
	 * @param uri URI to the resource.
	 * @param etag Etag to check the resource against.
	 *             Note: should be a quoted string
	 * @return the instance
	 */
	public HttpDeleteMethod createDeleteMethod(URI uri, String etag) {
		return new HttpDeleteMethod(uri, etag);
	}

}
