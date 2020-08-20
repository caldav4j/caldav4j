/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Roberto Polli
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
 * 
 * 
 */

package com.github.caldav4j;

import com.github.caldav4j.exceptions.BadStatusException;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.exceptions.ResourceNotFoundException;
import com.github.caldav4j.exceptions.ResourceOutOfDateException;
import com.github.caldav4j.methods.*;
import com.github.caldav4j.model.request.*;
import com.github.caldav4j.model.response.CalendarDataProperty;
import com.github.caldav4j.util.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class provides a high level API to a calendar collection on a CalDAV server.
 * 
 * @author robipolli@gmail.com
 * 
 * implements methods for
 * - create
 * - retrieve
 * - update
 * - delete
 * 
 * calendars are retrieved in two ways
 *  - by path (with get methods)
 *  - by custom query (with search methods)
 * no customized queries should be public in this class
 */
public class CalDAVCollection extends CalDAVCalendarCollectionBase<Calendar> {
    
	private static final Logger log = LoggerFactory.getLogger(CalDAVCollection.class); 
	
	protected CalDAVCollection() {
		super(new ResponseToCalendar());
	}
	
	/**
	 * Creates a new CalDAVCalendar collection with the calendar collection root.
	 * This is a convenience constructor which sets the host, based on the URI
	 * provided, through {@link #getDefaultHttpHost(URI)}. It also sets
	 * the method factory, through {@link #setMethodFactory(DAVMethodFactory)}
	 *
	 * @param uri The path to the collection
	 */
	public CalDAVCollection(String uri) {
	    this();
		setCalendarCollectionRoot(uri);
		setMethodFactory(new CalDAV4JMethodFactory());
		this.prodId = CalDAVConstants.PROC_ID_DEFAULT;
	}

	/**
	 * Creates a new CalDAVCalendar collection with the specified parameters
	 *
	 * @param path The path to the collection
	 * @param httpHost Host information for the CalDAV Server
	 * @param methodFactory methodFactory to obtail HTTP methods from
	 * @param prodId String identifying who creates the iCalendar objects
	 */
	public CalDAVCollection(String path,
	                        HttpHost httpHost,
	                        CalDAV4JMethodFactory methodFactory, String prodId) {
        this();
		setCalendarCollectionRoot(path);
		this.httpHost = httpHost;
		this.methodFactory = methodFactory;
		this.prodId = prodId;
	}


	//Configuration Methods

	/**
	 * Returns the icalendar object which contains the event with the specified
	 * UID.
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param uid The uniqueID of the event to find
	 * @return the Calendar object containing the event with this UID
	 * @throws CalDAV4JException if there was a problem, or if the resource could
	 *         not be found.
	 *  @deprecated use a less-specialized query
	 */
	public Calendar getCalendarForEventUID(HttpClient httpClient, String uid)
            throws CalDAV4JException {
		// implement it using a simplequery: here we don't need meta-data/tags

		return getCalDAVResourceForEventUID(httpClient, uid).getCalendar();
	}

	/**
	 * Gets an icalendar object by GET
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param icsRelativePath the path, relative to the collection path
	 * @return the Calendar object at the specified path
	 * @throws CalDAV4JException on error
	 */
	public Calendar getCalendar(HttpClient httpClient, String icsRelativePath) 
	throws CalDAV4JException{
		return getCalDAVResource(httpClient, getAbsolutePath(icsRelativePath)).getCalendar();
	}
	
	/**
	 * Retrieve a single calendar by UID / COMPONENT using REPORT
	 * @param httpClient the httpClient which will make the request
	 * @param component Component to query
	 * @param uid UID of the Calendar
	 * @param recurrenceId If not null, then provides the recurrence ID
	 * @return  The Calendar with the given UID. null if not found
	 * @throws CalDAV4JException on error
	 */
	public Calendar queryCalendar(HttpClient httpClient, String component, String uid, String recurrenceId) throws CalDAV4JException {
		String filter =  String.format("%s : UID==%s", component, uid);
		if (recurrenceId != null) {
			filter  = String.format("%s, RECURRENCE-ID==%s", filter, recurrenceId);
		}		
		GenerateQuery gq = new GenerateQuery(component, filter);
		
		List<Calendar> cals = queryCalendars(httpClient, gq.generate());
		switch (cals.size()) {
			case 1:
				return cals.get(0);
			case 0:
				return null;
			default:
				throw new CalDAV4JException("More than one calendar returned for uid "+uid);
		}
	}

	/**
	 * Returns all Calendars which contain events which have instances who fall within 
	 * the two dates. Note that recurring events are NOT expanded. 
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param beginDate the beginning of the date range. Must be a UTC date
	 * @param endDate the end of the date range. Must be a UTC date.
	 * @return a List of Calendars
	 * @throws CalDAV4JException if there was a problem
	 * 
	 * @deprecated should be implemented by query 
	 */
	public List<Calendar> getEventResources(HttpClient httpClient,
			Date beginDate, Date endDate)
			throws CalDAV4JException {

		GenerateQuery gq = new GenerateQuery();
		gq.setFilter("VEVENT");
		gq.setTimeRange(beginDate, endDate);		
		return queryCalendars(httpClient,  gq.generate());
	}

	
	/**
	 * Delete every component with the given UID. As UID is unique in the
	 * collection  it should remove only one Calendar resource
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param component Component to remove
	 * @param uid UID to delete
	 * @throws CalDAV4JException on error
	 * 
	 * TODO this method should be refined with recurrenceid
	 */
	public void delete(HttpClient httpClient, String component, String uid)
	throws CalDAV4JException {


		CalDAVResource<Calendar> resource = getCalDAVResourceByUID(httpClient, component, uid);
		Calendar calendar = resource.getCalendar();
		ComponentList<CalendarComponent> eventList = calendar.getComponents().getComponents(component);

		// get a list of components to remove
		List<Component> componentsToRemove = new ArrayList<>();
		boolean hasOtherEvents = false;
		for (CalendarComponent event : eventList){
			String curUID = ICalendarUtils.getUIDValue(event);
			if (!uid.equals(curUID)){
				hasOtherEvents = true;
			} else {
				componentsToRemove.add(event);
			}
		}

		//
		// remove from calendar the components with the given UID
		// and PUT the calendar
		//
		if (hasOtherEvents){
			if (componentsToRemove.size() == 0){
				throw new ResourceNotFoundException(
						ResourceNotFoundException.IdentifierType.UID, uid);
			}

			for (Component removeMe : componentsToRemove){
				calendar.getComponents().remove(removeMe);
			}
			put(httpClient, calendar, UrlUtils.stripHost(resource.getResourceMetadata().getHref()),
					resource.getResourceMetadata().getETag());
			return;
		} else {
			delete(httpClient, UrlUtils.stripHost(resource.getResourceMetadata().getHref()));
		}
	}


	/**
	 * Creates a calendar at the specified path 
	 * @param httpClient the httpClient which will make the request
	 * @throws CalDAV4JException on error
	 */
	public void createCalendar(HttpClient httpClient) throws CalDAV4JException {

		HttpMkCalendarMethod mkCalendarMethod = null;
		try {
			mkCalendarMethod = methodFactory.createMkCalendarMethod(getCalendarCollectionRoot());
			HttpResponse response = httpClient.execute(getDefaultHttpHost(mkCalendarMethod.getURI()), mkCalendarMethod);
			if (!mkCalendarMethod.succeeded(response)){
				MethodUtil.StatusToExceptions(mkCalendarMethod, response);
			}
		} catch (Exception e) {
			throw new CalDAV4JException("Trouble executing MKCalendar", e);
		} finally {
			if(mkCalendarMethod != null)
            	mkCalendarMethod.reset();
        }
    }

    /**
     * @param httpClient the httpClient which will make the request
     * @param calendar iCal body to place on the server
     * @param path Path to the new/old resource
     * @param etag ETag if updation of calendar has to take place.
     * @throws CalDAV4JException on error
     */
    private void put(HttpClient httpClient, Calendar calendar, String path,
             String etag)
            throws CalDAV4JException {
	    CalendarRequest cr = new CalendarRequest();
	    cr.addEtag(etag);
	    cr.setIfMatch(true);
	    cr.setCalendar(calendar);
        HttpPutMethod putMethod = methodFactory.createPutMethod(path, cr);

        try {
            HttpResponse response = httpClient.execute(getDefaultHttpHost(putMethod.getURI()), putMethod);
            int statusCode = response.getStatusLine().getStatusCode();

            switch(statusCode) {
                case CalDAVStatus.SC_NO_CONTENT:
                case CalDAVStatus.SC_CREATED:
                    break;
                case CalDAVStatus.SC_PRECONDITION_FAILED:
                    throw new ResourceOutOfDateException("Etag was not matched: "+ etag);
                default:
                    throw new BadStatusException(statusCode, putMethod.getMethod(), path);
            }

	        if(isCacheEnabled()) {
		        Header h = putMethod.getFirstHeader("ETag");
		        String newEtag = null;
		        if (h != null) {
			        newEtag = h.getValue();
		        } else {
			        newEtag = getETagbyMultiget(httpClient, path);
		        }
		        cache.putResource(new CalDAVResource<Calendar>(calendar, newEtag, putMethod.getURI().toString()));
	        }

        } catch (ResourceOutOfDateException | BadStatusException e){
            throw e;
        } catch (Exception e){
            throw new CalDAV4JException("Problem executing put method",e);
        } finally {
            putMethod.reset();
        }
    }

	/**
	 * Adds a new Calendar with the given Component and VTimeZone to the collection.
	 *
	 * Tries to use the event UID followed by ".ics" as the name of the
	 * resource, otherwise will use the UID followed by a random number and
	 * ".ics" if the "UID.ics" already exists.
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param vevent The VEvent to put in the Calendar
	 * 
	 * @param timezone The VTimeZone of the VEvent if it references one, 
	 *                 otherwise null
	 * @throws CalDAV4JException on error
	 * @return Returns the final UID to the new resource.
	 */
	public String add(HttpClient httpClient, CalendarComponent vevent, VTimeZone timezone)
	throws CalDAV4JException {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId(prodId));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		if (timezone != null){
			calendar.getComponents().add(timezone);
		}
		calendar.getComponents().add(vevent);

		return add(httpClient, calendar);
	}

	/**
	 * Same as {@link #add(HttpClient, Calendar, boolean)}, with
	 * {@code attemptRetry} as true
	 * @param httpClient the httpClient which will make the request
	 * @param c Calendar to Add
	 * @return UID of added resource.
	 * @throws CalDAV4JException on error
	 * @see #add(HttpClient, Calendar, boolean)
	 */
	public String add(HttpClient httpClient, Calendar c)
			throws CalDAV4JException {
		return add(httpClient, c, true);
	}
	/**
	 * Adds a calendar object to caldav collection using "UID.ics" as file name.
	 * <br><br>
	 * If {@code attemptRetry} is {@code true}, we attempts to retry again when
	 * "UID.ics" already exists on server. It does so, by adding a random number
	 * to the UID. The retry attempt only occurs when a server returns the HTTP
	 * status 412 PRECONDITION_FAILED.
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param c Calendar to Add
	 * @param attemptRetry Sets if the request should be retried in case of error.
	 * @throws CalDAV4JException on error
	 * @return Returns the final UID to the new resource.
	 */
	public String add(HttpClient httpClient, Calendar c, boolean attemptRetry)
			throws CalDAV4JException {

		Random random = new Random();
		//
		// retry 3 times while caldav server returns PRECONDITION_FAILED
		//
		boolean didIt = false;

		Uid uid = null;
		for (int x = 0; x < 3 && !didIt; x++) {

			// Sets the UID if null.
			uid = ICalendarUtils.setUID(c);

			// change UID at second attempt
			if (x > 0) {
				uid.setValue(uid.getValue() + "-" +random.nextInt());
			}

			HttpPutMethod<Calendar> putMethod = createPutMethodForNewResource(uid.getValue() + ".ics", c);
			HttpResponse response = null;
			try {
				response = httpClient.execute(getDefaultHttpHost(putMethod.getURI()), putMethod);

				if(isCacheEnabled() && putMethod.succeeded(response)) {
					String etag = UrlUtils.getHeaderPrettyValue(response, CalDAVConstants.HEADER_ETAG);
					if (etag == null) {
						etag = getETagbyMultiget(httpClient, putMethod.getURI().toString());
					}
					CalDAVResource<Calendar> calDAVResource = new CalDAVResource<>(c, etag, putMethod.getURI().toString());
					cache.putResource(calDAVResource);
				}

			} catch (Exception e) {
				throw new CalDAV4JException("Trouble executing PUT", e);
			}

			int statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode) {
				//Succeeded
				case CalDAVStatus.SC_CREATED:
				case CalDAVStatus.SC_NO_CONTENT:
					didIt = true;
					break;
				//Another calendar with the same UID exists. Thus, retry.
				case CalDAVStatus.SC_PRECONDITION_FAILED:
					if(attemptRetry)
						continue;
				default:
					MethodUtil.StatusToExceptions(putMethod, response);
			}

		} //for
		return uid.getValue();
	}

	/**
	 * Create a PUT method setting If-None-Match: *
	 * this tag causes PUT fails if a given event exist  
	 * @param resourceName Resource under Collection Root
	 * @param calendar Calendar for the Put Method
	 * @return a PutMethod for creating events
	 */
	HttpPutMethod createPutMethodForNewResource(String resourceName,
	                                            Calendar calendar) {
		CalendarRequest cr = new CalendarRequest();
		cr.setAllEtags(true);
		cr.setIfNoneMatch(true);
		cr.setCalendar(calendar);
		return methodFactory.createPutMethod(calendarCollectionRoot + resourceName, cr);
	}

	/**
	 * Updates the resource containing the VEvent with the same UID as the given 
	 * VEvent with the given VEvent
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param vevent the vevent to update
	 * @param timezone The VTimeZone of the VEvent if it references one, 
	 *                 otherwise null
	 * @throws CalDAV4JException on error
	 */
	// TODO: Deal with SEQUENCE
	public void updateMasterEvent(HttpClient httpClient, VEvent vevent, VTimeZone timezone)
            throws CalDAV4JException {
		String uid = ICalendarUtils.getUIDValue(vevent);
		CalDAVResource<Calendar> resource = getCalDAVResourceByUID(httpClient, Component.VEVENT, uid);
		Calendar calendar = resource.getPayload();

		//let's find the master event first!
		VEvent originalVEvent = ICalendarUtils.getMasterEvent(calendar, uid);

		calendar.getComponents().remove(originalVEvent);
		calendar.getComponents().add(vevent);

		if(timezone != null) {
			VTimeZone originalVTimeZone = ICalendarUtils.getTimezone(calendar);
			if (originalVTimeZone != null)
				calendar.getComponents().remove(originalVTimeZone);
			calendar.getComponents().add(timezone);
		}

		put(httpClient, calendar,
				UrlUtils.stripHost(resource.getResourceMetadata().getHref()),
				resource.getResourceMetadata().getETag());
	}

	/**
	 * Get a CalDAVResource by UID
	 * it tries
	 *  - first by a REPORT
	 *  - then by GET /path
	 * @param httpClient the httpClient which will make the request
	 * @param uid UID of the Event
	 * @return CalDAVResource representing the Event
	 * @throws CalDAV4JException on error
	 * @deprecated This query is too specialized @see{getCalDAVResourceByUID()}
	 */
	private CalDAVResource<Calendar> getCalDAVResourceForEventUID(
			HttpClient httpClient, String uid) throws CalDAV4JException {

		return getCalDAVResourceByUID(httpClient, Component.VEVENT, uid);
	}

	/**
	 *
	 * It tries:
	 *  - first by a REPORT
	 *  - then by GET /path checking that UID=filename
	 *  TODO another strategy can be to
	 *   - first by GET /path and check UID
	 *   - else try by report
	 *   as the first case is the most common, I avoid overload the server with REPORT
	 * @param httpClient the httpClient which will make the request
	 * @param component Calendar Component
	 * @param uid UID of retrieved component
	 * @return a Caldav resource containing the component type with the given uid
	 * @throws CalDAV4JException on error
	 * @throws ResourceNotFoundException When resource is not found
	 */
	protected CalDAVResource<Calendar> getCalDAVResourceByUID(
			HttpClient httpClient, String component, String uid)
            throws CalDAV4JException, ResourceNotFoundException {

		//first check the cache!
		String href = cache.getHrefForEventUID(uid);
		CalDAVResource<Calendar> resource = null;

		if (href != null) {
			resource = getCalDAVResource(httpClient, UrlUtils.stripHost(href));

			if (resource != null) {
				return resource;
			}
		} else {
			try {		
				resource = getCalDAVResource(httpClient, getAbsolutePath(uid+".ics") );
				if (uid.equals(ICalendarUtils.getUIDValue(ICalendarUtils.getFirstComponent(resource, component)))) {
					return resource;
				}
			} catch (Exception e){
				// resource not found: continue...
				resource = null;
			}			
		}

		// then check by calendar query
		GenerateQuery gq;		
		gq = new GenerateQuery(null, component + " : UID=="+uid );


		List<CalDAVResource<Calendar>> cr = getCalDAVResources(httpClient, gq.generate());
		try {
			resource = cr.get(0);
			if (uid.equals(ICalendarUtils.getUIDValue(ICalendarUtils.getFirstComponent(resource, component)))) {
				cache.putResource(resource);
				return resource;
			} else {
				throw new Exception();
			}
		} catch (Exception e) {			
			throw new ResourceNotFoundException(
					ResourceNotFoundException.IdentifierType.UID, uid);
		}

	}

	/**
	 * Deletes a resource at a given path. Also removes it from cache.
	 *
	 * @param httpClient HTTPClient making the request
	 * @param path       Path to resource
	 * @throws CalDAV4JException on error
	 */
	public void delete(HttpClient httpClient, String path)
	throws CalDAV4JException {
		HttpDeleteMethod deleteMethod = new HttpDeleteMethod(path);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpHost, deleteMethod);
		} catch (Exception e){
			throw new CalDAV4JException("Problem executing delete method",e);
		}
		
		if (response == null || response.getStatusLine().getStatusCode() != CalDAVStatus.SC_NO_CONTENT){
			MethodUtil.StatusToExceptions(deleteMethod, response);
			throw new CalDAV4JException("Problem executing delete method");
		}
		if(isCacheEnabled())
			cache.removeResource(getHref(path));
	}

	/**
	 * Replace double slashes
	 * @param relativePath Relative Path, who's absolute Path is to be returned.
	 * @return a path with double slashes removed
	 */
	protected String getAbsolutePath(String relativePath){
		return (getCalendarCollectionRoot() + relativePath).replaceAll("/+", "/");
	}

	/**
	 * Useful for retrieving a list of UIDs of all events
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param componentName Component whose property is to be returned
	 * @param propertyName Property whose value is to be returned
	 * @param query Query to specify the Calendars
	 * @return a list of property values of events. 
	 * @throws CalDAV4JException on error
	 * @deprecated maybe create a method in ICalendarUtils or an "asString()" method
	 */
	protected List <String> getComponentProperty(HttpClient httpClient, String componentName, String propertyName, CalendarQuery query)
	throws CalDAV4JException
	{


		List<String> propertyList = new ArrayList<>();
		List<Calendar> calendarList = getCalendarLight(httpClient, query);

		for (Calendar cal : calendarList){
			propertyList.add (
					ICalendarUtils.getPropertyValue(
							cal.getComponent(componentName), propertyName
					)
			);
		}

		return propertyList;
	}



	/**
	 * Return a list of components using REPORT
	 * @param query Query to return the calendars for.
	 * @param httpClient the httpClient which will make the request
	 * @return a new Calendar list with no elements if 0
	 * @throws CalDAV4JException on error
	 */
	public List<Calendar> queryCalendars(HttpClient httpClient, CalendarQuery query)
            throws CalDAV4JException {
		List <Calendar> list = new ArrayList<>();
		for (CalDAVResource<Calendar> cr: getCalDAVResources(httpClient, query)) {
			list.add(cr.getPayload());
		}

		return list;
	}

	/**
	 * Return a list of components using REPORT without
	 * passing through the CaldavResource Cache
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param query Query to get the Calendar from.
	 * @return List of Calendars
	 * @throws CalDAV4JException on error
	 * @deprecated This is still a proposed feature
	 */
	public List<Calendar> getCalendarLight(HttpClient httpClient, CalendarQuery query)
			throws CalDAV4JException {
		List <Calendar> list = new ArrayList<>();

		if (isCacheEnabled()) {
			query.setCalendarDataProp(null);
		}
		HttpCalDAVReportMethod reportMethod = null;
		try {
            reportMethod = methodFactory.createCalDAVReportMethod(getCalendarCollectionRoot(), query, CalDAVConstants.DEPTH_1);
			HttpResponse httpResponse = httpClient.execute(getDefaultHttpHost(reportMethod.getURI()), reportMethod);

            MultiStatusResponse[] set = reportMethod.getResponseBodyAsMultiStatus(httpResponse).getResponses();
            for(MultiStatusResponse response: set){
                String etag = CalendarDataProperty.getEtagfromResponse(response);

                if (isCacheEnabled()) {
                    CalDAVResource<Calendar> resource = getCalDAVResource(httpClient,
                            UrlUtils.stripHost(response.getHref()), etag);

                    list.add(resource.getPayload());

                    // XXX check if getCalDAVResource does its caching job
                    cache.putResource(resource);

                } else {
                    Calendar cal = CalendarDataProperty.getCalendarfromResponse(response);
                    if (cal != null)
                        list.add(cal);
                }
            }
		} catch (Exception he) {
			throw new CalDAV4JException("Problem executing method", he);
		} finally {
            if(reportMethod != null)
                reportMethod.reset();
        }

		return list;
	}

	//
	// MultiGet queries
	//


	/**
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param query Multiget Query to get.
	 * @return Returns a list of Calendar Components represented by the query.
	 * @throws CalDAV4JException on error
	 */
	protected List<Calendar> getComponentByMultiget(HttpClient httpClient, CalendarMultiget query) throws CalDAV4JException {
		if (isCacheEnabled()) {
			query.setCalendarDataProp(null);
		}

		HttpCalDAVReportMethod reportMethod = null;
        List<Calendar> list = new ArrayList<>();

		try {
            reportMethod = methodFactory.createCalDAVReportMethod(getCalendarCollectionRoot(), query, CalDAVConstants.DEPTH_1);
			HttpResponse httpResponse = httpClient.execute(getDefaultHttpHost(reportMethod.getURI()), reportMethod);

            MultiStatusResponse[] e = reportMethod.getResponseBodyAsMultiStatus(httpResponse).getResponses();

            for(MultiStatusResponse response: e){
                CalDAVResource<Calendar> resource = null;

                if (isCacheEnabled()) {
                    String etag = CalendarDataProperty.getEtagfromResponse(response);
                    try{
                        resource =
                                getCalDAVResource(httpClient, UrlUtils.stripHost(response.getHref()), etag);

                        list.add(resource.getPayload());
                    } catch(Exception e1) {
                        log.warn("Unable to get CalDAVResource for etag: " + etag);
                        e1.printStackTrace();
                    }
                } else {
                    list.add(CalendarDataProperty.getCalendarfromResponse(response));
                }
            }

		} catch (Exception he) {
			throw new CalDAV4JException("Problem executing method", he);
		} finally {
            if(reportMethod != null)
                reportMethod.reset();
        }

		return list;
	}

	/**
	 * Implementing calendar multiget with Properties: getetag, calendar-data
	 *
	 * @see <a href="http://tools.ietf.org/html/rfc4791#section-7.9">RFC 4791 Section 7.9</a>
	 *
	 * @param httpClient the httpClient which will make the request
     * @param calendarUris URI's for Multiget
	 * @return List of Calendars based on the uris.
	 * @throws CalDAV4JException on error
	 */
	public List<Calendar> multigetCalendarUris(HttpClient httpClient,
			List<String> calendarUris )
            throws CalDAV4JException {
		// first create the calendar query
		CalendarMultiget query = new CalendarMultiget();
		CalendarData calendarData = new CalendarData();

		query.addProperty(CalDAVConstants.DNAME_GETETAG);
		query.setCalendarDataProp(calendarData);

		query.setHrefs(calendarUris);

		return getComponentByMultiget(httpClient, query);
	}

	/**
	 * Executes a FreeBusyQuery Report as based on
	 * <a href="https://tools.ietf.org/html/rfc4791#section-7.10">RFC 4791 Section 7.10</a>
	 * with a Depth of 1.
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param timeRange timerange to check
	 * @return VFREEBUSY Calendar
	 * @throws CalDAV4JException on error
	 */
	public Calendar getFreeBusyQueryCalendar(HttpClient httpClient, TimeRange timeRange)
			throws CalDAV4JException {
		return this.getFreeBusyQueryCalendar(httpClient, new FreeBusyQuery(timeRange));
	}

	/**
	 * Executes a FreeBusyQuery Report as based on
	 * <a href="https://tools.ietf.org/html/rfc4791#section-7.10">RFC 4791 Section 7.10</a>
	 * with a Depth of 1.
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param freeBusyQuery Query to execute
	 * @return VFREEBUSY Calendar
	 * @throws CalDAV4JException on error
	 */
	public Calendar getFreeBusyQueryCalendar(HttpClient httpClient, FreeBusyQuery freeBusyQuery)
			throws CalDAV4JException {

		HttpCalDAVReportMethod<Calendar> reportMethod = null;

		try {
			reportMethod = methodFactory.createCalDAVReportMethod(getCalendarCollectionRoot(), freeBusyQuery, CalDAVConstants.DEPTH_1);
			HttpResponse response = httpClient.execute(getDefaultHttpHost(reportMethod.getURI()), reportMethod);

			if(reportMethod.succeeded(response))
				return reportMethod.getResponseBodyAsCalendar(response);

		} catch (Exception he) {
			throw new CalDAV4JException("Problem executing method", he);
		} finally {
			if(reportMethod != null)
				reportMethod.reset();
		}

		return null;
	}


	//
	// HEAD method, useful for testing connection
	//

	/**
	 * Uses the HTTP HEAD Method to check if the connection is possible.
	 * @param httpClient HTTPClient to make the request
	 * @return StatusCode
	 * @throws CalDAV4JException when Status is not {@link CalDAVStatus#SC_OK}
	 */
	public int testConnection(HttpClient httpClient)
			throws CalDAV4JException {
		HttpHead method = new HttpHead(getCalendarCollectionRoot());

		HttpResponse response = null;
		try {
			response = httpClient.execute(getDefaultHttpHost(method.getURI()), method);
		} catch (Exception e) {
			throw new CalDAV4JException(e.getMessage(), new Throwable(e.getCause()));
		}

		switch (response.getStatusLine().getStatusCode()) {
			case CalDAVStatus.SC_OK:
				break;
			default:
				throw new BadStatusException(response.getStatusLine().getStatusCode(), method.getMethod(), getCalendarCollectionRoot());
		}
		return response.getStatusLine().getStatusCode();
	}

	//
	// manage ACL TODO
	//

	/**
	 * Uses PROPFIND to return the list of Aces at Calendar Collection Root.
	 * @param httpClient HTTPClient making the request
	 * @return Returns the list of ACL Properties Aces
	 * @throws CalDAV4JException on error
	 */
	public List<AclProperty.Ace> getAces(HttpClient httpClient) throws CalDAV4JException{
		return getAces(httpClient, null);
	}

	/**
	 * Uses PROPFIND to return the list of Aces at the path.
	 *
	 * @param httpClient HTTPClient making the request
	 * @param path Path to Resource
	 * @return Returns the list of ACL Properties Aces
	 * @throws CalDAV4JException on error
	 */
	public List<AclProperty.Ace> getAces(HttpClient httpClient, String path) throws CalDAV4JException{

		DavPropertyNameSet propfind = new DavPropertyNameSet();
		propfind.add(CalDAVConstants.DNAME_ACL);

		HttpPropFindMethod method = null;

		try {
			method = methodFactory.createPropFindMethod(getCalendarCollectionRoot() + UrlUtils.defaultString(path, ""),
					propfind, CalDAVConstants.DEPTH_1);
			HttpResponse response = httpClient.execute(getDefaultHttpHost(method.getURI()), method);

			if(method.succeeded(response))
				return method.getAces(response, method.getURI().toString());
			else {
				MethodUtil.StatusToExceptions(method, response);
				return null;
			}

		} catch (Exception e) {
			throw new CalDAV4JException("Error in PROPFIND " +  getCalendarCollectionRoot(), e);
		} finally {
			if(method != null)
				method.reset();
		}

	}

	/**
	 * Used to Set the Aces at the given path.
	 * @param client Client making the request.
	 * @param aces Aces to set.
	 * @param path Path to Resource
	 * @throws CalDAV4JException on error.
	 */
	public void setAces(HttpClient client, AclProperty.Ace[] aces, String path) throws CalDAV4JException {
		HttpAclMethod method = null;

		try {
			method = methodFactory.createAclMethod(getCalendarCollectionRoot() + UrlUtils.defaultString(path, "")
					, new AclProperty(aces));
			HttpResponse response = client.execute(getDefaultHttpHost(method.getURI()), method);
			int status = response.getStatusLine().getStatusCode();
			switch (status) {
				case CalDAVStatus.SC_OK:
					break;
				case CalDAVStatus.SC_NOT_FOUND:
					throw new ResourceNotFoundException(ResourceNotFoundException.IdentifierType.PATH, method.getURI().toString());
				case CalDAVStatus.SC_UNAUTHORIZED:
				default:
					throw new BadStatusException(status, method.getMethod(),  getCalendarCollectionRoot());
			}

		} catch (IOException e) {
			throw new CalDAV4JException("Error in ACL " +  getCalendarCollectionRoot(), e);
		} finally {
			if(method != null)
				method.reset();
		}

	}

} //end of class
