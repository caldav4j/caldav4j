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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.model.request.CalDAVReportRequest;
import org.osaf.caldav4j.model.request.MkCalendar;
import org.osaf.caldav4j.model.request.TicketRequest;

import java.io.IOException;

/**
 * Method factory for creating instances of CalDAV related Methods.
 * This automatically handles, any basic configuration required.
 */
public class CalDAV4JMethodFactory {

	protected String prodID = CalDAVConstants.PROC_ID_DEFAULT;
	private boolean validatingOutputter = false;

	private ThreadLocal<CalendarBuilder> calendarBuilderThreadLocal = new ThreadLocal<CalendarBuilder>();
	private CalendarOutputter calendarOutputter = null;

	/**
	 * Empty Constructor
	 */
	public CalDAV4JMethodFactory(){

	}

	/**
	 * @return Returns the value of the current PRODID, which will be used during ICAL Generation.
	 */
	public String getProdID() {
		return prodID;
	}

	/**
	 * Sets the PRODID value to the one specified.
	 * @param prodID new value of PRODID
	 */
	public void setProdID(String prodID) {
		this.prodID = prodID;
	}

	/**
	 * Creates a {@link PutMethod} instance.
	 * @return the instance
	 */
	public PutMethod createPutMethod(){
		PutMethod putMethod = new PutMethod();
		putMethod.setProcID(prodID);
		putMethod.setCalendarOutputter(getCalendarOutputterInstance());
		return putMethod;
	}

	/**
	 * Creates a {@link PostMethod} instance.
	 * @return the instance
	 */
	public PostMethod createPostMethod(){
		PostMethod postMethod = new PostMethod();
		postMethod.setProcID(prodID);
		postMethod.setCalendarOutputter(getCalendarOutputterInstance());
		return postMethod;
	}

	/**
	 * Creates a {@link MkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @return the instance
	 */
	public MkCalendarMethod createMkCalendarMethod(String uri) {
		return new MkCalendarMethod(uri);
	}

	/**
	 * Creates a {@link MkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param DisplayName Display Name of Calendar
	 * @param Description Description of Calendar.
	 * @param DescriptionLanguage Language of the Description. Optional.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public MkCalendarMethod createMkCalendarMethod(String uri, String DisplayName, String Description,
												   String DescriptionLanguage) throws IOException {
		MkCalendarMethod mkCalendarMethod = new MkCalendarMethod(uri, DisplayName, Description, DescriptionLanguage);
		return mkCalendarMethod;
	}

	/**
	 * Creates a {@link MkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param mkCalendar {@link MkCalendar} instance which
	 * contains all the details for creating a calendar.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public MkCalendarMethod createMkCalendarMethod(String uri, MkCalendar mkCalendar) throws IOException {
		return new MkCalendarMethod(uri, mkCalendar);
	}

	/**
	 * Creates a {@link MkTicketMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param tr {@link TicketRequest} instance regarding the request.
	 * @return the instance
	 */
	public MkTicketMethod createMkTicketMethod(String uri, TicketRequest tr){
		MkTicketMethod mkTicketMethod = new MkTicketMethod(uri, tr);
		return mkTicketMethod;
	}

	/**
	 * Creates a {@link DelTicketMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param ticket ID of the Ticket to be Deleted.
	 * @return the instance
	 */
	public DelTicketMethod createDelTicketMethod(String uri, String ticket){
		DelTicketMethod delTicketMethod = new DelTicketMethod(uri, ticket);
		return delTicketMethod;
	}

	/**
	 * Creates a {@link PropFindMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public PropFindMethod createPropFindMethod(String uri) throws IOException {
		PropFindMethod propFindMethod = new PropFindMethod(uri);
		return propFindMethod;
	}

	/**
	 * Creates a {@link PropFindMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param propertyNames Properties to make the Propfind request for.
	 * @param depth Depth of the Request
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public PropFindMethod createPropFindMethod(String uri, DavPropertyNameSet propertyNames, int depth)
			throws IOException {
		PropFindMethod propFindMethod = new PropFindMethod(uri, propertyNames, depth);
		return propFindMethod;
	}

	/**
	 * Creates a {@link PropFindMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param propfindtype Type of Propfind Call.
	 * @param propertyNames Properties to make the Propfind request for.
	 * @param depth Depth of the Request
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public PropFindMethod createPropFindMethod(String uri, int propfindtype, DavPropertyNameSet propertyNames,
											   int depth) throws IOException {
		PropFindMethod propFindMethod = new PropFindMethod(uri, propfindtype, propertyNames, depth);
		return propFindMethod;
	}

	/**
	 * Creates a {@link GetMethod} instance.
	 * @return the instance
	 */
	public GetMethod createGetMethod(){
		GetMethod getMethod = new GetMethod();
		getMethod.setCalendarBuilder(getCalendarBuilderInstance());
		return getMethod;
	}

	/**
	 * Creates a {@link CalDAVReportMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @return the instance
	 */
	public CalDAVReportMethod createCalDAVReportMethod(String uri) {
		CalDAVReportMethod reportMethod = new CalDAVReportMethod(uri);
		reportMethod.setCalendarBuilder(getCalendarBuilderInstance());
		return reportMethod;
	}

	/**
	 * Creates a {@link CalDAVReportMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param request The Report to make a request for.
	 * @return the instance
	 * @throws IOException if error occurred during parsing of parameters
	 */
	public CalDAVReportMethod createCalDAVReportMethod(String uri, CalDAVReportRequest request) throws IOException {
		CalDAVReportMethod reportMethod = new CalDAVReportMethod(uri, request);
		reportMethod.setCalendarBuilder(getCalendarBuilderInstance());
		return reportMethod;
	}

	/**
	 * @return True or False based on the Calendar Validating Outputter setting.
	 */
	public boolean isCalendarValidatingOutputter() {
		return validatingOutputter;
	}

	/**
	 * Set whether the CalendarBuilder is Validating or not.
	 * @param validatingOutputter Value to set.
	 */
	public void setCalendarValidatingOutputter(boolean validatingOutputter) {
		this.validatingOutputter = validatingOutputter;
	}

	/**
	 * Return the CalendarOuputter instance.
	 * @return CalendarOutputter
	 */
	protected synchronized CalendarOutputter getCalendarOutputterInstance(){
		if (calendarOutputter == null){
			calendarOutputter = new CalendarOutputter(validatingOutputter);
		}
		return calendarOutputter;
	}

	/**
	 * Return the CalendarBuilder instance.
	 * @return CalendarBuilder
	 */
	private CalendarBuilder getCalendarBuilderInstance(){
		CalendarBuilder builder = calendarBuilderThreadLocal.get();
		if (builder == null){
			builder = new CalendarBuilder();
			calendarBuilderThreadLocal.set(builder);
		}
		return builder;
	}
	
	//- - - - - - - - - - - - - Http4Client - - - - - - - - - - - - - - - - - - - -

	/**
	 * Creates a {@link PutMethod} instance.
	 * @return the instance
	 */
	public HttpPutMethod createHttpPutMethod(){
		HttpPutMethod putMethod = new HttpPutMethod();
		putMethod.setProcID(prodID);
		putMethod.setCalendarOutputter(getCalendarOutputterInstance());
		return putMethod;
	}
	
	/**
	 * Creates a {@link HttpMkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @return the instance
	 */
	public HttpMkCalendarMethod createHttpMkCalendarMethod(String uri) {
		return new HttpMkCalendarMethod(uri); 
	}

	/**
	 * Creates a {@link HttpMkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param displayName Display Name of Calendar
	 * @param description Description of Calendar.
	 * @param descriptionLanguage Language of the Description. Optional.
	 * @return the instance
	 */
	public HttpMkCalendarMethod createHttpMkCalendarMethod(String uri, String displayName, String description,
			   String descriptionLanguage) throws IOException {
		return new HttpMkCalendarMethod(uri, displayName, description, descriptionLanguage);
	}

	/**
	 * Creates a {@link MkCalendarMethod} instance.
	 * @param uri URI to the Calendar resource to create.
	 * @param mkCalendar {@link MkCalendar} instance which
	 * contains all the details for creating a calendar.
	 * @return the instance
	 */
	public HttpMkCalendarMethod createHttpMkCalendarMethod(String uri, MkCalendar mkCalendar) throws IOException {
		return new HttpMkCalendarMethod(uri, mkCalendar); 
	}
	
	/**
	 * Creates a {@link MkTicketMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param tr {@link TicketRequest} instance regarding the request.
	 * @return the instance
	 */
	public HttpMkTicketMethod createHttpMkTicketMethod(String uri, TicketRequest tr){
		HttpMkTicketMethod mkTicketMethod = new HttpMkTicketMethod(uri, tr);
		mkTicketMethod.addRequestHeaders();
		mkTicketMethod.generateRequestBody();		
		return mkTicketMethod;
	}
	
	/**
	 * Creates a {@link DelTicketMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param ticket ID of the Ticket to be Deleted.
	 * @return the instance
	 */
	public HttpDelTicketMethod createHttpDelTicketMethod(String uri, String ticket){
		return new HttpDelTicketMethod(uri, ticket);
	}
	
	/**
	 * Creates a {@link HttpPropFindMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param propertyNames Properties to make the Propfind request for.
	 * @param depth Depth of the Request
	 * @return the instance
	 */
	public HttpPropFindMethod createHttpPropFindMethod(String uri, DavPropertyNameSet propertyNames, int depth)
			throws IOException {
		return new HttpPropFindMethod(uri, propertyNames, depth);
	}
	
	/**
	 * Creates a {@link PropFindMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param propfindtype Type of Propfind Call.
	 * @param propertyNames Properties to make the Propfind request for.
	 * @param depth Depth of the Request
	 * @return the instance
	 */
	public HttpPropFindMethod createHttpPropFindMethod(String uri, int propfindtype, DavPropertyNameSet propertyNames,
											   int depth) throws IOException {
		return new HttpPropFindMethod(uri, propfindtype, propertyNames, depth);
	}
	
	/**
	 * Creates a {@link HttpGetMethod} instance.
	 * @return the instance
	 */
	public HttpGetMethod createHttpGetMethod() {
		HttpGetMethod getMethod = new HttpGetMethod();
		getMethod.setCalendarBuilder(getCalendarBuilderInstance());
		return getMethod;
	}
	
	/**
	 * Creates a {@link CalDAVReportMethod} instance.
	 * @param uri URI to the Calendar resource.
	 * @param request The Report to make a request for.
	 * @return the instance
	 */
	public HttpCalDAVReportMethod createHttpCalDAVReportMethod(String uri, CalDAVReportRequest request) throws IOException {
		HttpCalDAVReportMethod reportMethod = new HttpCalDAVReportMethod(uri, request);
		reportMethod.setCalendarBuilder(getCalendarBuilderInstance());
		return reportMethod;
	}	
	
	
}
