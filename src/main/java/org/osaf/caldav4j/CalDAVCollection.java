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
 * 
 * 
 */

package org.osaf.caldav4j;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.AclMethod;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.osaf.caldav4j.exceptions.BadStatusException;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.exceptions.ResourceNotFoundException;
import org.osaf.caldav4j.exceptions.ResourceNotFoundException.IdentifierType;
import org.osaf.caldav4j.exceptions.ResourceOutOfDateException;
import org.osaf.caldav4j.methods.*;
import org.osaf.caldav4j.model.request.*;
import org.osaf.caldav4j.model.response.CalendarDataProperty;
import org.osaf.caldav4j.model.response.TicketDiscoveryProperty;
import org.osaf.caldav4j.model.response.TicketResponse;
import org.osaf.caldav4j.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import static org.osaf.caldav4j.util.ICalendarUtils.getMasterEvent;
import static org.osaf.caldav4j.util.ICalendarUtils.getUIDValue;
import static org.osaf.caldav4j.util.UrlUtils.stripHost;

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
public class CalDAVCollection extends CalDAVCalendarCollectionBase{
	private static final Logger log = LoggerFactory.getLogger(CalDAVCollection.class);
	// configuration settings

	public CalDAVCollection(){

	}

	/**
	 * Creates a new CalDAVCalendar collection with the specified parameters
	 * 
	 * @param path The path to the collection 
	 * @param hostConfiguration Host information for the CalDAV Server 
	 * @param methodFactory methodFactory to obtail HTTP methods from
	 * @param prodId String identifying who creates the iCalendar objects
	 */
	public CalDAVCollection(String path,
			HostConfiguration hostConfiguration,
			CalDAV4JMethodFactory methodFactory, String prodId) {
		setCalendarCollectionRoot(path);
		this.hostConfiguration = hostConfiguration;
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
            throws CalDAV4JException, IOException {
		// implement it using a simplequery: here we don't need meta-data/tags

		return getCalDAVResourceForEventUID(httpClient, uid).getCalendar();
	}

	/**
	 * Gets an icalendar object by GET
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param icsRelativePath the path, relative to the collection path
	 * @return the Calendar object at the specified path
	 * @throws CalDAV4JException
	 */
	public Calendar getCalendar(HttpClient httpClient, String icsRelativePath) 
	throws CalDAV4JException{
		return getCalDAVResource(httpClient, getAbsolutePath(icsRelativePath)).getCalendar();
	}
	
	/**
	 * Retrieve a single calendar by UID / COMPONENT using REPORT
	 * @param httpClient the httpClient which will make the request
	 * @param component
	 * @param uid
	 * @param recurrenceId
	 * @return  The Calendar with the given UID. null if not found
	 * @throws CalDAV4JException
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
	 * @param uid
	 * @throws CalDAV4JException
	 * 
	 * TODO this method should be refined with recurrenceid
	 */
	public void delete(HttpClient httpClient, String component, String uid)
	throws CalDAV4JException{


		CalDAVResource resource = getCalDAVResourceByUID(httpClient, component, uid);
		Calendar calendar = resource.getCalendar();
		ComponentList eventList = calendar.getComponents().getComponents(component);

		// get a list of components to remove
		List<Component> componentsToRemove = new ArrayList<Component>();
		boolean hasOtherEvents = false;
		for (Object o : eventList){
			CalendarComponent event = (CalendarComponent) o;
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
			put(httpClient, calendar, stripHost(resource.getResourceMetadata().getHref()),
					resource.getResourceMetadata().getETag());
			return;
		} else {
			delete(httpClient, stripHost(resource.getResourceMetadata().getHref()));
		}
	}


	/**
	 * Creates a calendar at the specified path 
	 * @param httpClient the httpClient which will make the request
	 */
	public void createCalendar(HttpClient httpClient) throws CalDAV4JException{
		MkCalendarMethod mkCalendarMethod = new MkCalendarMethod(getCalendarCollectionRoot());

		try {
			httpClient.executeMethod(hostConfiguration, mkCalendarMethod);
			int statusCode = mkCalendarMethod.getStatusCode();
			if (statusCode != CaldavStatus.SC_CREATED){
				MethodUtil.StatusToExceptions(mkCalendarMethod);
			}
		} catch (Exception e) {
			throw new CalDAV4JException("Trouble executing MKCalendar", e);
		} finally {
            mkCalendarMethod.releaseConnection();
        }
    }

    /**
     * @param httpClient the httpClient which will make the request
     * @param calendar iCal body to place on the server
     * @param path Path to the new/old resource
     * @param etag ETag if updation of calendar has to take place.
     * @throws CalDAV4JException
     */
    void put(HttpClient httpClient, Calendar calendar, String path,
             String etag)
            throws CalDAV4JException {
        PutMethod putMethod = methodFactory.createPutMethod();
        putMethod.addEtag(etag);
        putMethod.setPath(path);
        putMethod.setIfMatch(true);
        putMethod.setRequestBody(calendar);
        try {
            httpClient.executeMethod(hostConfiguration, putMethod);
            int statusCode = putMethod.getStatusCode();
            switch(statusCode) {
                case CaldavStatus.SC_NO_CONTENT:
                case CaldavStatus.SC_CREATED:
                    break;
                case CaldavStatus.SC_PRECONDITION_FAILED:
                    throw new ResourceOutOfDateException("Etag was not matched: "+ etag);
                default:
                    throw new BadStatusException(statusCode, putMethod.getName(), path);
            }

            Header h = putMethod.getResponseHeader("ETag");

            String newEtag = null;
            if (h != null) {
                newEtag = h.getValue();
            } else {
                newEtag = getETag(httpClient, path);
            }
            cache.putResource(new CalDAVResource(calendar, newEtag, getHref(putMethod.getPath())));
        } catch (ResourceOutOfDateException e){
            throw e;
        } catch (BadStatusException e){
            throw e;
        } catch (Exception e){
            throw new CalDAV4JException("Problem executing put method",e);
        } finally {
            putMethod.releaseConnection();
        }
    }

	/**
	 * Adds a new Calendar with the given Component and VTimeZone to the collection.
	 * 
	 * Tries to use the event UID followed by ".ics" as the name of the 
	 * resource, otherwise will use the UID followed by a random number and 
	 * ".ics" 
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param vevent The VEvent to put in the Calendar
	 * 
	 * @param timezone The VTimeZone of the VEvent if it references one, 
	 *                 otherwise null
	 * @throws CalDAV4JException
	 */
	public void add(HttpClient httpClient, CalendarComponent vevent, VTimeZone timezone)
	throws CalDAV4JException {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId(prodId));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		if (timezone != null){
			calendar.getComponents().add(timezone);
		}
		calendar.getComponents().add(vevent);

		add(httpClient, calendar);
	}

	/**
	 * adds a calendar object to caldav collection using UID.ics as file name
	 * @param httpClient the httpClient which will make the request
	 * @param c
	 * @throws CalDAV4JException
	 */
	public void add(HttpClient httpClient, Calendar c) 
	throws CalDAV4JException {

		//
		// retry 3 times while caldav server returns PRECONDITION_FAILED
		//
		boolean didIt = false;
		for (int x = 0; x < 3 && !didIt; x++) {
			String resourceName = null;
			String uid = ICalendarUtils.getUIDValue(c);

			// change UID at second attempt
			if (x > 0) {
				uid += "-"+random.nextInt();				
				ICalendarUtils.setUIDValue(c, uid);
			}

			// TODO move all these lines into ICalendarUtils
			uid = ICalendarUtils.getUIDValue(c);		
			if (uid == null) {
				uid = random.nextLong() + "-" + random.nextLong();
				ICalendarUtils.setUIDValue(c, uid);
			}

			PutMethod putMethod = createPutMethodForNewResource(uid + ".ics", c);
			try {
				httpClient.executeMethod(getHostConfiguration(), putMethod);

				String etag = UrlUtils.getHeaderPrettyValue(putMethod, HEADER_ETAG);

				if(etag == null){
                    etag = getETag(httpClient, putMethod.getPath());
				}

				CalDAVResource calDAVResource = new CalDAVResource(c,	etag, getHref((putMethod.getPath())));

				cache.putResource(calDAVResource);

			} catch (Exception e) {
				throw new CalDAV4JException("Trouble executing PUT", e);
			}
			
			int statusCode = putMethod.getStatusCode();
			switch (statusCode) {
			case CaldavStatus.SC_CREATED:			
			case CaldavStatus.SC_NO_CONTENT:
				didIt = true;
				break;
			default:
				MethodUtil.StatusToExceptions(putMethod);
			} 
		} //for
	}

	/**
	 * Updates the resource containing the VEvent with the same UID as the given 
	 * VEvent with the given VEvent
	 * 
	 *  TODO: Deal with SEQUENCE
	 *
	 * @param httpClient the httpClient which will make the request
	 * @param vevent the vevent to update
	 * @param timezone The VTimeZone of the VEvent if it references one, 
	 *                 otherwise null
	 * @throws CalDAV4JException
	 */
	public void updateMasterEvent(HttpClient httpClient, VEvent vevent, VTimeZone timezone)
            throws CalDAV4JException, IOException {
		String uid = getUIDValue(vevent);
		CalDAVResource resource = getCalDAVResourceByUID(httpClient, Component.VEVENT, uid);
		Calendar calendar = resource.getCalendar();

		//let's find the master event first!
		VEvent originalVEvent = getMasterEvent(calendar, uid);

		calendar.getComponents().remove(originalVEvent);
		calendar.getComponents().add(vevent);

		put(httpClient, calendar,
				stripHost(resource.getResourceMetadata().getHref()),
				resource.getResourceMetadata().getETag());
	}
	/**
	 * Creates a ticket for the specified resource and returns the ticket id.
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param relativePath the path, relative to the collection path for 
	 *                     which to grant the ticket on
	 * @param visits
	 * @param timeout
	 * @param read
	 * @param write
	 * @return The id of the created ticket
	 * @throws CalDAV4JException
	 *             Is thrown if the execution of the MkTicketMethod fails
	 */
	public String createTicket(HttpClient httpClient, String relativePath,
			Integer visits, Integer timeout, boolean read, boolean write)
	throws CalDAV4JException {
		TicketRequest ticketRequest = new TicketRequest();
		ticketRequest.setVisits(visits);
		ticketRequest.setTimeout(timeout);
		ticketRequest.setRead(read);
		ticketRequest.setWrite(write);

		// Make the ticket
		MkTicketMethod mkTicketMethod = methodFactory.createMkTicketMethod(getAbsolutePath(relativePath), ticketRequest);
		try {
			httpClient.executeMethod(hostConfiguration, mkTicketMethod);
			int statusCode = mkTicketMethod.getStatusCode();
			if (statusCode != CaldavStatus.SC_OK) {
				throw new CalDAV4JException("Create Ticket Failed with Status: "
						+ statusCode + " and body: \n"
						+ mkTicketMethod.getResponseBodyAsString());
			}
		} catch (Exception e) {
			throw new CalDAV4JException("Trouble executing MKTicket", e);
		} finally {
			mkTicketMethod.releaseConnection();
		}

		TicketResponse ticketResponse = null;

		try {
			ticketResponse = mkTicketMethod.getResponseBodyAsTicketResponse();
		} catch (Exception e) {
			throw new CalDAV4JException("Trouble handling MkTicket Response", e);
		}

		return ticketResponse.getID();

	}

	/**
	 * Deletes the specified ticket on the specified resource.
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param relativePath the path, relative to the collection path for
	 *                     which to revoke the ticket 
	 * @param ticketId the ticketID which to revoke
	 * @throws CalDAV4JException
	 *             Is thrown if the execution of the DelTicketMethod fails
	 */
	public void deleteTicket(HttpClient httpClient, String relativePath, String ticketId)
	throws CalDAV4JException {
		DelTicketMethod delTicketMethod = methodFactory.createDelTicketMethod(getAbsolutePath(relativePath), ticketId);

		try {
			httpClient.executeMethod(hostConfiguration, delTicketMethod);
			int statusCode = delTicketMethod.getStatusCode();
			if (statusCode != CaldavStatus.SC_NO_CONTENT) {
				throw new CalDAV4JException(
						"Delete Ticket Failed with Status: " + statusCode
						+ " and body: \n"
						+ delTicketMethod.getResponseBodyAsString());
			}
		} catch (Exception e) {
			throw new CalDAV4JException("Trouble executing DelTicket", e);
		} finally {
			delTicketMethod.releaseConnection();
		}

	}

	/**
	 * Returns all the ticket ID's from all tickets the requesting user has
	 * permision to view on a resource.
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param relativePath the path, relative to the collection path for which
	 *                     to get the tickets
	 * @return
	 * @throws CalDAV4JException
	 */
	public List<String> getTicketsIDs(HttpClient httpClient, String relativePath)
            throws CalDAV4JException {

		PropFindMethod propFindMethod = null;

        List<String> ticketIDList = new ArrayList<String>();

        try{
            DavPropertyNameSet propertyNames = new DavPropertyNameSet();
            propertyNames.add(CalDAVConstants.DNAME_TICKETDISCOVERY);
            propertyNames.add("owner", CalDAVConstants.NAMESPACE_WEBDAV);

            propFindMethod = methodFactory.createPropFindMethod(getAbsolutePath(relativePath),
                    propertyNames, CalDAVConstants.DEPTH_0);
            httpClient.executeMethod(hostConfiguration, propFindMethod);

            int statusCode = propFindMethod.getStatusCode();

            if (statusCode != CaldavStatus.SC_MULTI_STATUS) {
                throw new CalDAV4JException("PropFind Failed with Status: "
                        + statusCode + " and body: \n"
                        + propFindMethod.getResponseBodyAsString());
            }

            String href = getHref(getAbsolutePath(relativePath));
            MultiStatusResponse responses = propFindMethod.getResponseBodyAsMultiStatusResponse(href);

            TicketDiscoveryProperty ticketDiscoveryProp = new TicketDiscoveryProperty(responses);
            ticketIDList.addAll(ticketDiscoveryProp.getTicketIDs());


        } catch (Exception e){
            log.error("Unable to perform PROPFIND Method:" + httpClient.getHostConfiguration().getHost());
        } finally {
            if(propFindMethod != null)
                propFindMethod.releaseConnection();
        }

        return ticketIDList;
	}


	/**
	 * get a CalDAVResource by UID
	 * it tries
	 *  - first by a REPORT
	 *  - then by GET /path
	 * @param httpClient the httpClient which will make the request
	 * @param uid
	 * @return
	 * @throws Exception 
	 * @deprecated this query is too specialized @see{getCalDAVResourceByUID()}
	 */
	private CalDAVResource getCalDAVResourceForEventUID(
			HttpClient httpClient, String uid) throws CalDAV4JException {

		return getCalDAVResourceByUID(httpClient, Component.VEVENT, uid);
	}

	/**
	 *  
	 * it tries
	 *  - first by a REPORT
	 *  - then by GET /path checking that UID=filename
	 *  TODO another strategy can be to
	 *   - first by GET /path and check UID
	 *   - else try by report
	 *   as the first case is the most common, I avoid overload the server with REPORT
	 * @param httpClient the httpClient which will make the request
	 * @param component
	 * @param uid
	 * @return a Caldav resource containing the component type with the given uid
	 */
	protected CalDAVResource getCalDAVResourceByUID(
			HttpClient httpClient, String component, String uid)
            throws CalDAV4JException, ResourceNotFoundException {

		//first check the cache!
		String href = cache.getHrefForEventUID(uid);
		CalDAVResource resource = null;

		if (href != null) {
			resource = getCalDAVResource(httpClient, stripHost(href));

			if (resource != null) {
				return resource;
			}
		} else {
			// check if there's an event with the standard caldav url
			// TODO this method retrieves a VTIMEZONE on google calendar, due to a google-bug.
			//  check current behaviour!!!
			try {		
				resource = getCalDAVResource(httpClient, getAbsolutePath(uid+".ics") );
				if (uid.equals(getUIDValue(ICalendarUtils.getFirstComponent(resource, component)))) {
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


		List<CalDAVResource> cr;
		cr = getCalDAVResources(httpClient, gq.generate());
		try {
			resource = cr.get(0);
			if (uid.equals(getUIDValue(ICalendarUtils.getFirstComponent(resource, component)))) {
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
	 * GET the resource at the given path. Will check the cache first, and compare that to the
	 * latest etag obtained using a HEAD request.
	 * 
	 * if calendar resource in cache is void, retrieve directly from server (avoid get etag only)
	 * @param httpClient the httpClient which will make the request
	 * @param path
	 * @return
	 * @throws CalDAV4JException
	 * FIXME testme
	 */
	protected CalDAVResource getCalDAVResource(HttpClient httpClient,
			String path) throws CalDAV4JException {
		CalDAVResource calDAVResource = cache.getResource(getHref(path));
		if (calDAVResource == null || calDAVResource.getCalendar() == null) {
			return getCalDAVResourceFromServer(httpClient, path);
		} else {
			String currentEtag = getETag(httpClient, path);
			return getCalDAVResource(httpClient, path, currentEtag);
		}
	}

	/**
	 * Gets the resource for the given href. Will check the cache first, and if a cached
	 * version exists that has the etag provided it will be returned. Otherwise, it goes
	 * to the server for the resource.
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param path
	 * @param currentEtag
	 * @return
	 * @throws CalDAV4JException
	 */
	protected CalDAVResource getCalDAVResource(HttpClient httpClient,
			String path, String currentEtag) throws CalDAV4JException {

		//first try getting from the cache
		CalDAVResource calDAVResource = cache.getResource(getHref(path));

		//ok, so we got the resource...but has it been changed recently?
		if (calDAVResource != null 
				&& calDAVResource.getCalendar() != null) { // FIXME calDAVResource's calendar should not be null!
			String cachedEtag = calDAVResource.getResourceMetadata().getETag();
			if (cachedEtag.equals(currentEtag)){
				return calDAVResource;
			}
		}

		//either the etag was old, or it wasn't in the cache so let's get it
		//from the server       
		return getCalDAVResourceFromServer(httpClient, path);

	}

	/**
	 * Gets a CalDAVResource (not a mere timezone) from the server - in other words DOES NOT check the cache.
	 * Adds the new resource to the cache, replacing any pre-existing version.
	 * On Google Caldav Server, this method skips VTIMEZONE resources as they are used as tombstones 
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param path
	 * @return CalDAVResource
	 * @throws CalDAV4JException
	 */
	protected CalDAVResource getCalDAVResourceFromServer(HttpClient httpClient,
			String path) throws CalDAV4JException {
		CalDAVResource calDAVResource = null;
		GetMethod getMethod = getMethodFactory().createGetMethod();
		getMethod.setPath(path);
		try {
			httpClient.executeMethod(hostConfiguration, getMethod);

            if (getMethod.getStatusCode() != CaldavStatus.SC_OK){
                MethodUtil.StatusToExceptions(getMethod);
                throw new BadStatusException(getMethod);
            }

            String href = getHref(path);
            String etag = getMethod.getResponseHeader(HEADER_ETAG).getValue();
            Calendar calendar = null;

            try {
                calendar = getMethod.getResponseBodyAsCalendar();
            } catch (ParserException pe) {
                if (! isTolerantParsing()) {
                    throw pe;
                }
                CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, false);
                CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false);
                CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true);
                CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, false);
                calendar = getMethod.getResponseBodyAsCalendar();
            }

            calDAVResource = new CalDAVResource();
            calDAVResource.setCalendar(calendar);
            calDAVResource.getResourceMetadata().setETag(etag);
            calDAVResource.getResourceMetadata().setHref(href);

            cache.putResource(calDAVResource);
		} catch (BadStatusException e){
            throw e;
        }
        catch (Exception e){
			throw new CalDAV4JException("Problem executing get method",e);
		} finally {
            getMethod.releaseConnection();
        }

		return calDAVResource;			
	}



	protected void delete(HttpClient httpClient, String path)
	throws CalDAV4JException {
		DeleteMethod deleteMethod = new DeleteMethod(path);
		try {
			httpClient.executeMethod(hostConfiguration, deleteMethod);			
		} catch (Exception e){
			throw new CalDAV4JException("Problem executing delete method",e);
		}
		
		if (deleteMethod.getStatusCode() != CaldavStatus.SC_NO_CONTENT){
			MethodUtil.StatusToExceptions(deleteMethod);
			throw new CalDAV4JException("Problem executing delete method");
		}
		cache.removeResource(getHref(path));
	}

	/**
	 * Replace double slashes
	 * @param relativePath Relative Path, who's absolute Path is to be returned.
	 * @return a path with double slashes removed
	 */
	protected String getAbsolutePath(String relativePath){
		return   (getCalendarCollectionRoot() + "/" + relativePath).replaceAll("/+", "/");
	}


	/**
	 * retrieve etags using HEAD /path/to/resource.ics
	 * If, HEAD fails, then use a Multiget Query.
	 * @param httpClient the httpClient which will make the request
	 * @param path Path to the Calendar
	 * @return ETag for calendar
	 * @throws CalDAV4JException
	 */
	protected String getETag(HttpClient httpClient, String path) throws CalDAV4JException{
		HeadMethod headMethod = new HeadMethod(path);

		try {
			httpClient.executeMethod(hostConfiguration, headMethod);
			int statusCode = headMethod.getStatusCode();

			if (statusCode == CaldavStatus.SC_NOT_FOUND) {
				throw new ResourceNotFoundException(
						ResourceNotFoundException.IdentifierType.PATH, path);
			}

			if (statusCode != CaldavStatus.SC_OK){
				throw new CalDAV4JException(
						"Unexpected Status returned from Server: "
						+ headMethod.getStatusCode());
			}
		} catch (IOException e) {
			String message = hostConfiguration.getHostURL()+ headMethod.getPath();
			throw new CalDAV4JException("Problem executing HEAD method on: "+ message,e);
		}

		Header h = headMethod.getResponseHeader(HEADER_ETAG);
		String etag = null;
		if (h != null) {
			etag = h.getValue();
		} else {
            DavPropertyNameSet props = new DavPropertyNameSet();
            props.add(DavPropertyName.GETETAG);
            CalendarMultiget query = new CalendarMultiget(props, null, false, false);
            query.addHref(path);

            MultiStatus multiStatus = getResponseforQuery(httpClient, query);
            for(MultiStatusResponse response : multiStatus.getResponses()){
                if(response.getStatus()[0].getStatusCode() == CaldavStatus.SC_OK){
                    etag = CalendarDataProperty.getEtagfromResponse(response);
                }
            }
        }

		return etag;
	}




	/**
	 * Useful for retrieving a list of UIDs of all events
	 * 
	 * @param httpClient the httpClient which will make the request
	 * @param componentName Component whose property is to be returned
	 * @param propertyName Property whose value is to be returned
	 * @param query Query to specify the Calendars
	 * @return a list of property values of events. 
	 * @throws CalDAV4JException
	 * @deprecated maybe create a method in ICalendarUtils or an "asString()" method
	 */
	protected List <String> getComponentProperty(HttpClient httpClient, String componentName, String propertyName, CalendarQuery query)
	throws CalDAV4JException
	{


		List<String> propertyList = new ArrayList<String>();
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
	 * @return a new Calendar list with no elements if 0
	 * @throws CalDAV4JException
	 */
	public List<Calendar> queryCalendars(HttpClient httpClient, CalendarQuery query)
            throws CalDAV4JException {
		List <Calendar> list = new ArrayList<Calendar>();
		for (CalDAVResource cr: getCalDAVResources(httpClient, query)) {
			list.add(cr.getCalendar());
		}

		return list;
	}

	/**
	 * return a list of components using REPORT without
	 * passing thru CaldavResource
     * @param httpClient the httpClient which will make the request
	 * @param query Query to get the Calendar from.
	 * @return
	 * @throws CalDAV4JException
	 * @deprecated This is still a proposed feature
	 */
	public List<Calendar> getCalendarLight(HttpClient httpClient, CalendarQuery query)
			throws CalDAV4JException {
		List <Calendar> list = new ArrayList<Calendar>();

		if (isCacheEnabled()) {
			query.setCalendarDataProp(null);
		}
		CalDAVReportMethod reportMethod = null;
		try {
            reportMethod = methodFactory.createCalDAVReportMethod(getCalendarCollectionRoot(), query);
			httpClient.executeMethod(getHostConfiguration(), reportMethod);

            MultiStatusResponse[] set = reportMethod.getResponseBodyAsMultiStatus().getResponses();
            for(MultiStatusResponse response: set){
                String etag = CalendarDataProperty.getEtagfromResponse(response);

                if (isCacheEnabled()) {
                    CalDAVResource resource = getCalDAVResource(httpClient,
                            stripHost(response.getHref()), etag);
                    Calendar cal = resource.getCalendar();

                    list.add(resource.getCalendar());

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
                reportMethod.releaseConnection();
        }

		return list;
	}

    /**
     * Get Responses for a specific ReportMethod Query
     * @param httpClient Client which makes the request.
     * @param query Query for the Report Method to execute.
     * @return MultiStatus Reponse for the Query
     */
	public MultiStatus getResponseforQuery(HttpClient httpClient, CalDAVReportRequest query) throws CalDAV4JException {

		CalDAVReportMethod reportMethod = null;
		try {
            reportMethod = methodFactory.createCalDAVReportMethod(getCalendarCollectionRoot(), query);
			httpClient.executeMethod(getHostConfiguration(), reportMethod);

            if(reportMethod.succeeded())
                return reportMethod.getResponseBodyAsMultiStatus();

		} catch (Exception he) {
			throw new CalDAV4JException("Problem executing method", he);
		} finally {
            if(reportMethod != null)
                reportMethod.releaseConnection();
        }

        return null;
	}

	/**
	 * return a list of caldav resources. 
	 * All other methods should use this one
	 * 
	 * The use of caching changes the behavior of this method.
	 * if cache is not enable, returns a list of CalDAVResource parsed from the response
	 * if cache is enabled, foreach   HREF returned by server:
	 *   -  retrieve the resource using getCaldavReource(client, string), this method checks cache
	 *   -  
	 * @param httpClient the httpClient which will make the request
	 * @param query Query to get the CalDAV resources for
	 * @return List of CalDAVResource's
	 * @throws CalDAV4JException
	 */
	protected List<CalDAVResource> getCalDAVResources(HttpClient httpClient, CalendarQuery query)
            throws CalDAV4JException {
		boolean usingCache = isCacheEnabled();
		if (usingCache) {
			query.setCalendarDataProp(null);
			log.debug("Using cache, so I am removing calendar data");
		}
		log.trace("Executing query: "  + GenerateQuery.printQuery(query));

		CalDAVReportMethod reportMethod = null;

        List<CalDAVResource> list = new ArrayList<CalDAVResource>();
		try {
            reportMethod = methodFactory.createCalDAVReportMethod(getCalendarCollectionRoot(),
                    query);
			httpClient.executeMethod(getHostConfiguration(), reportMethod);

            log.trace("Parsing response.. " );

            MultiStatusResponse[] responses = reportMethod.getResponseBodyAsMultiStatus().getResponses();
            for(MultiStatusResponse response: responses){
                String etag = CalendarDataProperty.getEtagfromResponse(response);

                if (usingCache) {
                    CalDAVResource resource = getCalDAVResource(httpClient,
                            stripHost(response.getHref()), etag);
                    list.add(resource);
                    cache.putResource(resource);
                } else {
                    if (response != null) {
                        list.add(new CalDAVResource(response));
                    }
                }
            }

		} catch (ConnectException connEx) {
			// TODO getHostURL is synchronized
			throw new CalDAV4JException("Can't connecto to "+
					getHostConfiguration().getHostURL(), connEx.getCause());
		} catch (Exception he) {
			throw new CalDAV4JException("Problem executing method", he);
		} finally {
            if(reportMethod != null)
                reportMethod.releaseConnection();
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
	 * @return a list of Calendar, each followed by a status
	 * @throws CalDAV4JException
	 */
	protected List<Calendar> getComponentByMultiget(HttpClient httpClient, CalendarMultiget query) throws CalDAV4JException {
		if (isCacheEnabled()) {
			query.setCalendarDataProp(null);
		}

		CalDAVReportMethod reportMethod = null;
        List<Calendar> list = new ArrayList<Calendar>();

		try {
            reportMethod = methodFactory.createCalDAVReportMethod(getCalendarCollectionRoot(), query);
			httpClient.executeMethod(getHostConfiguration(), reportMethod);

            MultiStatusResponse[] e = reportMethod.getResponseBodyAsMultiStatus().getResponses();

            for(MultiStatusResponse response: e){
                CalDAVResource resource = null;

                if (isCacheEnabled()) {
                    String etag = CalendarDataProperty.getEtagfromResponse(response);
                    try{
                        resource =
                                getCalDAVResource(httpClient, stripHost(response.getHref()), etag);

                        list.add(resource.getCalendar());
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
                reportMethod.releaseConnection();
        }

		return list;
	}

	/**
	 * Implementing calendar multiget
	 * @link { http://tools.ietf.org/html/rfc4791#section-7.9 }
     * with Properties: getetag, calendar-data
	 * @author rpolli
	 * @param httpClient the httpClient which will make the request
     * @param calendarUris URI's for Multiget
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


	//
	// HEAD method, useful for testing connection
	//
	public int testConnection(HttpClient httpClient)
	throws CalDAV4JException {
		HeadMethod method = new HeadMethod();
		method.setPath(getCalendarCollectionRoot());
		try {
			httpClient.executeMethod(hostConfiguration,method);
		} catch (Exception e) {
			throw new CalDAV4JException(e.getMessage(), new Throwable(e.getCause()));			
		}

		switch (method.getStatusCode()) {
		case CaldavStatus.SC_OK:
			break;
		default:
			throw new BadStatusException(method.getStatusCode(), method.getName(), getCalendarCollectionRoot());
		}
		return method.getStatusCode();
	}

	//
	// manage ACL TODO
	//
	public List<AclProperty.Ace> getAces(HttpClient httpClient) throws CalDAV4JException{
		return getAces(httpClient, null);
	}

	public List<AclProperty.Ace> getAces(HttpClient httpClient, String path) throws CalDAV4JException{

        DavPropertyNameSet propfind = new DavPropertyNameSet();
        propfind.add(CalDAVConstants.DNAME_ACL);

        PropFindMethod method = null;

        try {
            method = methodFactory.createPropFindMethod(getCalendarCollectionRoot() + UrlUtils.defaultString(path, ""),
                    propfind, CalDAVConstants.DEPTH_0);
			httpClient.executeMethod(getHostConfiguration(), method);

            int status =  method.getStatusCode();

            switch (status) {
                case CaldavStatus.SC_MULTI_STATUS:
                    return method.getAces(method.getPath());
                default:
                    MethodUtil.StatusToExceptions(method);
                    return null;
            }

		} catch (Exception e) {
			throw new CalDAV4JException("Error in PROPFIND " +  getCalendarCollectionRoot(), e);
		} finally {
            if(method != null)
                method.releaseConnection();
        }

	}

	public void setAces(HttpClient client, AclProperty.Ace[] aces, String path) throws CalDAV4JException {
		AclMethod method = null;

		try {
            method = new AclMethod(getCalendarCollectionRoot() + UrlUtils.defaultString(path, "")
                    , new AclProperty(aces));
			client.executeMethod(method);
			int status = method.getStatusCode();
			switch (status) {
			case CaldavStatus.SC_OK:
				break;
			case CaldavStatus.SC_NOT_FOUND:
				throw new ResourceNotFoundException(IdentifierType.PATH, method.getPath());
			case CaldavStatus.SC_UNAUTHORIZED:
			default:
				throw new BadStatusException(status, method.getName(),  getCalendarCollectionRoot());
			}

		} catch (HttpException e) {
			throw new CalDAV4JException("Error in ACL " +  getCalendarCollectionRoot(), e);
		} catch (IOException e) {
			throw new CalDAV4JException("Error in ACL " +  getCalendarCollectionRoot(), e);
		} finally {
            if(method != null)
                method.releaseConnection();
        }

	}

} //end of class
