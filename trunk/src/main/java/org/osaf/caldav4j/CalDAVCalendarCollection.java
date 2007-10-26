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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.CalDAVReportMethod;
import org.osaf.caldav4j.methods.GetMethod;
import org.osaf.caldav4j.model.request.CalendarData;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.model.request.CompFilter;
import org.osaf.caldav4j.model.request.PropFilter;
import org.osaf.caldav4j.model.request.TextMatch;
import org.osaf.caldav4j.model.request.TimeRange;
import org.osaf.caldav4j.model.response.CalDAVResponse;

public class CalDAVCalendarCollection {
    private CalDAV4JMethodFactory methodFactory = null;

    private String path = null;

    private HttpClient httpClient = null;

    private HostConfiguration hostConfiguration = null;

    public CalDAVCalendarCollection(){
        
    }
    
    public CalDAVCalendarCollection(String path, HttpClient httpClient,
            HostConfiguration hostConfiguration, CalDAV4JMethodFactory methodFactory) {
        this.path = path;
        this.httpClient = httpClient;
        this.hostConfiguration = hostConfiguration;
        this.methodFactory = methodFactory;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //The interesting methods

    public Calendar getCalendar(String uid) throws CalDAV4JException {
        // first create the calendar query
        CalendarQuery query = new CalendarQuery("C", "D");
        query.setCalendarDataProp(new CalendarData("C"));

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
        reportMethod.setPath(path);
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
        
        return e.nextElement().getCalendar();
        
    }

    public Calendar getCalendarByPath(String relativePath) throws CalDAV4JException{
        GetMethod getMethod = methodFactory.createGetMethod();
        getMethod.setPath(path + "/" + relativePath);
        
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
        reportMethod.setPath(path);
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


}
