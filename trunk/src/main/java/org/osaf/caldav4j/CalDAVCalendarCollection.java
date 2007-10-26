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

package org.osaf.caldav4j;

import static org.osaf.caldav4j.util.ICalendarUtils.getMasterEvent;
import static org.osaf.caldav4j.util.ICalendarUtils.getUIDValue;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.CalDAVReportMethod;
import org.osaf.caldav4j.methods.GetMethod;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.model.request.CalendarData;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.model.request.CompFilter;
import org.osaf.caldav4j.model.request.PropFilter;
import org.osaf.caldav4j.model.request.PropProperty;
import org.osaf.caldav4j.model.request.TextMatch;
import org.osaf.caldav4j.model.request.TimeRange;
import org.osaf.caldav4j.model.response.CalDAVResponse;
import org.osaf.caldav4j.util.ICalendarUtils;

/**
 * This class provides a high level API to a calendar collection on a CalDAV server.
 * 
 * @author bobbyrullo
 *
 */
public class CalDAVCalendarCollection {
    
    public static final PropProperty PROP_ETAG = new PropProperty(CalDAVConstants.NS_DAV,
            "D", CalDAVConstants.PROP_GETETAG);
    
    private CalDAV4JMethodFactory methodFactory = null;

    private String calendarCollectionRoot = null;

    private HttpClient httpClient = null;

    private HostConfiguration hostConfiguration = null;

    private String prodId = null;

    private Random random = new Random();
    
    public CalDAVCalendarCollection(){
        
    }
    
    public CalDAVCalendarCollection(String path, HttpClient httpClient,
            HostConfiguration hostConfiguration, CalDAV4JMethodFactory methodFactory, String prodId) {
        this.calendarCollectionRoot = path;
        this.httpClient = httpClient;
        this.hostConfiguration = hostConfiguration;
        this.methodFactory = methodFactory;
        this.prodId = prodId;
    }

    //Configuration Methods

    public HostConfiguration getHostConfiguration() {
        return hostConfiguration;
    }

    public void setHostConfiguration(HostConfiguration hostConfiguration) {
        this.hostConfiguration = hostConfiguration;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CalDAV4JMethodFactory getMethodFactory() {
        return methodFactory;
    }

    public void setMethodFactory(CalDAV4JMethodFactory methodFactory) {
        this.methodFactory = methodFactory;
    }

    public String getCalendarCollectionRoot() {
        return calendarCollectionRoot;
    }

    public void setCalendarCollectionRoot(String path) {
        this.calendarCollectionRoot = path;
    }

    //The interesting methods

    public Calendar getCalendarForEventUID(String uid) throws CalDAV4JException {
        return getCalDAVResourceForEventUID(uid).getCalendar();
    }

    public Calendar getCalendarByPath(String relativePath) throws CalDAV4JException{
        GetMethod getMethod = methodFactory.createGetMethod();
        getMethod.setPath(calendarCollectionRoot + "/" + relativePath);
        
        try {
            httpClient.executeMethod(hostConfiguration, getMethod);
        } catch (Exception e) {
            throw new CalDAV4JException("Problem executing method", e);
        }
        
        if (getMethod.getStatusCode() == WebdavStatus.SC_NOT_FOUND){
            throw new ResourceNotFoundException(
                    ResourceNotFoundException.IdentifierType.PATH, relativePath);
            
        }
        
        if (getMethod.getStatusCode() != WebdavStatus.SC_OK){
            throw new CalDAV4JProtocolException("Bad Status Code: "
                    + getMethod.getStatusCode());
        }
        
        try {
            return getMethod.getResponseBodyAsCalendar();
        } catch (Exception e){
            throw new CalDAV4JException(
                    "Problem parsing response body as an iCalendar", e);
        }
    }
    
    public List<Calendar> getEventResources(Date beginDate, Date endDate)
            throws CalDAV4JException {
        // first create the calendar query
        CalendarQuery query = new CalendarQuery("C", "D");
        
        CalendarData calendarData = new CalendarData("C");
        calendarData.setExpandOrLimitRecurrenceSet(CalendarData.EXPAND);
        calendarData.setRecurrenceSetStart(beginDate);
        calendarData.setRecurrenceSetEnd(endDate);
        
        query.setCalendarDataProp(calendarData);
        CompFilter vCalendarCompFilter = new CompFilter("C");
        vCalendarCompFilter.setName(Calendar.VCALENDAR);

        CompFilter vEventCompFilter = new CompFilter("C");
        vEventCompFilter.setName(Component.VEVENT);
        vEventCompFilter.setTimeRange(new TimeRange("C", beginDate, endDate));

        vCalendarCompFilter.addCompFilter(vEventCompFilter);
        query.setCompFilter(vCalendarCompFilter);

        CalDAVReportMethod reportMethod = methodFactory
                .createCalDAVReportMethod();
        reportMethod.setPath(calendarCollectionRoot);
        reportMethod.setReportRequest(query);
        try {
            httpClient.executeMethod(hostConfiguration, reportMethod);
        } catch (Exception he) {
            throw new CalDAV4JException("Problem executing method", he);
        }

        Enumeration<CalDAVResponse> e = reportMethod.getResponses();
        List<Calendar> list = new ArrayList<Calendar>();
        while (e.hasMoreElements()){
            list.add(e.nextElement().getCalendar());
        }
        
        return list;

    }
    
    /**
     * Deletes an event based on it's uid. If the calendar resource containing the
     * event contains no other VEVENT's, the entire resource will be deleted.
     * 
     * If the uid is for a recurring event, the master event and all exceptions will
     * be deleted
     * 
     * @param uid
     */
    public void deleteEvent(String uid) throws CalDAV4JException{
        CalDAVResource resource = getCalDAVResourceForEventUID(uid);
        Calendar calendar = resource.getCalendar();
        ComponentList eventList = calendar.getComponents().getComponents(Component.VEVENT);
        List<Component> componentsToRemove = new ArrayList<Component>();
        boolean hasOtherEvents = false;
        for (Object o : eventList){
            VEvent event = (VEvent) o;
            String curUID = ICalendarUtils.getUIDValue(event);
            if (!uid.equals(curUID)){
                hasOtherEvents = true;
            } else {
                componentsToRemove.add(event);
            }
        }
        
        if (hasOtherEvents){
            if (componentsToRemove.size() == 0){
                throw new ResourceNotFoundException(
                        ResourceNotFoundException.IdentifierType.UID, uid);
            }
            
            for (Component removeMe : componentsToRemove){
                calendar.getComponents().remove(removeMe);
            }
            put(calendar, stripHost(resource.getResourceMetadata().getHref()),
                    resource.getResourceMetadata().getETag());
            return;
        } else {
            delete(stripHost(resource.getResourceMetadata().getHref()));
        }
    }

    /**
     * Creates a calendar at the specified path 
     *
     */
    public void createCalendar() throws CalDAV4JException{
        MkCalendarMethod mkCalendarMethod = new MkCalendarMethod();
        mkCalendarMethod.setPath(calendarCollectionRoot);
        try {
            httpClient.executeMethod(hostConfiguration, mkCalendarMethod);
            int statusCode = mkCalendarMethod.getStatusCode();
            if (statusCode != WebdavStatus.SC_CREATED){
                throw new CalDAV4JException("Create Failed with Status: "
                        + statusCode + " and body: \n"
                        + mkCalendarMethod.getResponseBodyAsString());
            }
        } catch (Exception e){
            throw new CalDAV4JException("Trouble executing MKCalendar", e);
        }
    }
    
    public void addEvent(VEvent vevent, VTimeZone timezone)
            throws CalDAV4JException {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId(prodId));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        if (timezone != null){
            calendar.getComponents().add(timezone);
        }
        calendar.getComponents().add(vevent);
        
        boolean didIt = false;
        for (int x = 0; x < 3 && !didIt; x++) {
            String resourceName = null;
            if (x == 0) {
                resourceName = ICalendarUtils.getUIDValue(vevent) + ".ics";
            } else {
                resourceName = ICalendarUtils.getUIDValue(vevent) + "-"
                        + random.nextInt() + ".ics";
            }
            PutMethod putMethod = createPutMethodForNewResource(resourceName,
                    calendar);
            try {
                httpClient.executeMethod(getHostConfiguration(), putMethod);
            } catch (Exception e) {
                throw new CalDAV4JException("Trouble executing PUT", e);
            }
            int statusCode = putMethod.getStatusCode();
            
            if (WebdavStatus.SC_CREATED == statusCode){
                didIt = true;
            } else if (WebdavStatus.SC_PRECONDITION_FAILED != statusCode){
                //must be some other problem, throw an exception
                throw new CalDAV4JException("Unexpected status code: "
                        + statusCode + "\n"
                        + putMethod.getResponseBodyAsString());
            }
        }
    }
    
    /**
     *  TODO: Deal with SEQUENCE
     *  TODO: Handle timezone!!! Right now ignoring the param...
     *
     * @param vevent
     * @param timezone
     * @throws CalDAV4JException
     */
    public void udpateMasterEvent(VEvent vevent, VTimeZone timezone)
        throws CalDAV4JException{
        String uid = getUIDValue(vevent);
        CalDAVResource resource = getCalDAVResourceForEventUID(uid);
        Calendar calendar = resource.getCalendar();
        
        //let's find the master event first!
        VEvent originalVEvent = getMasterEvent(calendar, uid);

        calendar.getComponents().remove(originalVEvent);
        calendar.getComponents().add(vevent);
        
        put(calendar,
                stripHost(resource.getResourceMetadata().getHref()),
                resource.getResourceMetadata().getETag());
    }
    
    /**
     * Returns the path to the resource that contains the VEVENT with the 
     * specified uid
     * 
     * TODO a nice optimization would be a cache of uids --> resource paths
     *
     * @param uid 
     */
    protected String getPathToResourceForEventId(String uid) throws CalDAV4JException{
        // first create the calendar query
        CalendarQuery query = new CalendarQuery("C", "D");
        
        query.addProperty(PROP_ETAG);
        
        CompFilter vCalendarCompFilter = new CompFilter("C");
        vCalendarCompFilter.setName(Calendar.VCALENDAR);

        CompFilter vEventCompFilter = new CompFilter("C");
        vEventCompFilter.setName(Component.VEVENT);

        PropFilter propFilter = new PropFilter("C");
        propFilter.setName(Property.UID);
        propFilter.setTextMatch(new TextMatch("C", false, uid));
        vEventCompFilter.addPropFilter(propFilter);

        vCalendarCompFilter.addCompFilter(vEventCompFilter);
        query.setCompFilter(vCalendarCompFilter);

        CalDAVReportMethod reportMethod = methodFactory
                .createCalDAVReportMethod();
        reportMethod.setPath(calendarCollectionRoot);
        reportMethod.setReportRequest(query);
        try {
            httpClient.executeMethod(hostConfiguration, reportMethod);
        } catch (Exception he) {
            throw new CalDAV4JException("Problem executing method", he);
        }

        Enumeration<CalDAVResponse> e = reportMethod.getResponses();
        if (!e.hasMoreElements()) {
            throw new ResourceNotFoundException(
                    ResourceNotFoundException.IdentifierType.UID, uid);
        }
        
        return stripHost(e.nextElement().getHref());
    }
    
    
    /**
     * Returns the path relative to the calendars path given an href
     * 
     * @param href
     * @return
     */
    protected String getRelativePath(String href){
        int start = href.indexOf(calendarCollectionRoot);
        return href.substring(start + calendarCollectionRoot.length() + 1);
    }
    
    protected CalDAVResource getCalDAVResourceForEventUID(String uid) throws CalDAV4JException {
        // first create the calendar query
        CalendarQuery query = new CalendarQuery("C", "D");
        query.setCalendarDataProp(new CalendarData("C"));
        query.addProperty(PROP_ETAG);
        
        CompFilter vCalendarCompFilter = new CompFilter("C");
        vCalendarCompFilter.setName(Calendar.VCALENDAR);

        CompFilter vEventCompFilter = new CompFilter("C");
        vEventCompFilter.setName(Component.VEVENT);

        PropFilter propFilter = new PropFilter("C");
        propFilter.setName(Property.UID);
        propFilter.setTextMatch(new TextMatch("C", false, uid));
        vEventCompFilter.addPropFilter(propFilter);

        vCalendarCompFilter.addCompFilter(vEventCompFilter);
        query.setCompFilter(vCalendarCompFilter);

        CalDAVReportMethod reportMethod = methodFactory
                .createCalDAVReportMethod();
        reportMethod.setPath(calendarCollectionRoot);
        reportMethod.setReportRequest(query);
        try {
            httpClient.executeMethod(hostConfiguration, reportMethod);
        } catch (Exception he) {
            throw new CalDAV4JException("Problem executing method", he);
        }

        Enumeration<CalDAVResponse> e = reportMethod.getResponses();
        if (!e.hasMoreElements()) {
            throw new ResourceNotFoundException(
                    ResourceNotFoundException.IdentifierType.UID, uid);
        }
        
        return new CalDAVResource(e.nextElement());
    }

    protected CalDAVResource getCalDAVResource(String path) throws CalDAV4JException {
        GetMethod getMethod = getMethodFactory().createGetMethod();
        getMethod.setPath(path);
        try {
            httpClient.executeMethod(hostConfiguration, getMethod);
            if (getMethod.getStatusCode() != WebdavStatus.SC_OK){
                throw new CalDAV4JException(
                        "Unexpected Status returned from Server: "
                                + getMethod.getStatusCode());
            }
        } catch (Exception e){
            throw new CalDAV4JException("Problem executing get method",e);
        }
        
        String href = hostConfiguration.getProtocol().getScheme() + ":"
             + hostConfiguration.getHost()
             + (hostConfiguration.getPort() != 80 ? "" + hostConfiguration.getPort() : "")
             + "/"
             + path;
        String etag = getMethod.getResponseHeader("ETag").getValue();
        Calendar calendar = null;
        try {
            calendar = getMethod.getResponseBodyAsCalendar();
        } catch (Exception e){
            throw new CalDAV4JException("Malformed calendar resource returned.", e);
        }
        
        CalDAVResource resource = new CalDAVResource();
        resource.setCalendar(calendar);
        resource.getResourceMetadata().setETag(etag);
        resource.getResourceMetadata().setHref(href);
        
        return resource;
    }
    
    protected void delete(String path) throws CalDAV4JException {
        DeleteMethod deleteMethod = new DeleteMethod(path);
        try {
            httpClient.executeMethod(hostConfiguration, deleteMethod);
            if (deleteMethod.getStatusCode() != WebdavStatus.SC_NO_CONTENT){
                throw new CalDAV4JException(
                        "Unexpected Status returned from Server: "
                                + deleteMethod.getStatusCode());
            }
        } catch (Exception e){
            throw new CalDAV4JException("Problem executing delete method",e);
        }
    }
    
    protected void put(Calendar calendar, String path, String etag)
            throws CalDAV4JException {
        PutMethod putMethod = methodFactory.createPutMethod();
        putMethod.addEtag(etag);
        putMethod.setPath(path);
        putMethod.setIfMatch(true);
        putMethod.setRequestBody(calendar);
        try {
            httpClient.executeMethod(hostConfiguration, putMethod);
            int statusCode = putMethod.getStatusCode();
            if (statusCode!= WebdavStatus.SC_NO_CONTENT
                    && statusCode != WebdavStatus.SC_CREATED) {
                if (statusCode == WebdavStatus.SC_PRECONDITION_FAILED){
                    throw new ResourceOutOfDateException(
                            "Etag was not matched: "
                                    + etag);
                }
            }
        } catch (Exception e){
            throw new CalDAV4JException("Problem executing put method",e);
        }
    }
    
    protected String getAbsolutePath(String relativePath){
        return   calendarCollectionRoot + "/" + relativePath;
    }
    
    protected String stripHost(String href){
        int indexOfColon = href.indexOf(":");
        int index = href.indexOf("/", indexOfColon + 3);
        return href.substring(index);
    }
    
    private PutMethod createPutMethodForNewResource(String resourceName,
            Calendar calendar) {
        PutMethod putMethod = methodFactory.createPutMethod();
        putMethod.setPath(calendarCollectionRoot + "/"
                + resourceName);
        putMethod.setAllEtags(true);
        putMethod.setIfNoneMatch(true);
        putMethod.setRequestBody(calendar);
        return putMethod;
    }
    
}
