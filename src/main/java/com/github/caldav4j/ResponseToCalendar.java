package com.github.caldav4j;

import org.apache.jackrabbit.webdav.MultiStatusResponse;

import com.github.caldav4j.model.response.CalendarDataProperty;

import net.fortuna.ical4j.model.Calendar;

public class ResponseToCalendar implements ResponseToResource<Calendar> {

    @Override
    public CalDAVResource<Calendar> toResource(MultiStatusResponse response) {

        Calendar calendar = CalendarDataProperty.getCalendarfromResponse(response);
        
        ResourceMetadata resourceMetadata = new ResourceMetadata();
        resourceMetadata.setETag(CalendarDataProperty.getEtagfromResponse(response));
        resourceMetadata.setHref(response.getHref());

        return new CalDAVResource<Calendar>(calendar, resourceMetadata);
    }

}
