/*
 * Copyright 2006 Open Source Applications Foundation
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
package org.osaf.caldav4j.model.response;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.CaldavStatus;

import java.io.IOException;
import java.io.StringReader;
/**
 * 
 * @author pventura_at_babel.it changed getCalendar
 *
 */
public class CalendarDataProperty {

	public static final String ELEMENT_CALENDAR_DATA = "calendar-data";

/*	private Calendar calendar = null;
    private ThreadLocal<CalendarBuilder> calendarBuilderThreadLocal = new ThreadLocal<CalendarBuilder>();

    private CalendarBuilder getCalendarBuilderInstance(){
        ThreadLocal<CalendarBuilder> calendarBuilderThreadLocal = new ThreadLocal<CalendarBuilder>();
        CalendarBuilder builder = calendarBuilderThreadLocal.get();
        if (builder == null){
            builder = new CalendarBuilder();
            calendarBuilderThreadLocal.set(builder);
        }
        return builder;
    }*/

    public static Calendar getCalendarfromProperty(DavProperty property){
        if(property == null) return null;

        Calendar calendar = null;
        String text = property.getValue().toString();

        //text might contain lines breaked only with \n. RFC states that long lines must be delimited by CRLF.
        //@see{http://www.apps.ietf.org/rfc/rfc2445.html#sec-4.1 }
        //this fix the problem occurred when lines are breaked only with \n
        text=text.replaceAll("\n","\r\n").replaceAll("\r\r\n", "\r\n");


        ThreadLocal<CalendarBuilder> calendarBuilderThreadLocal = new ThreadLocal<CalendarBuilder>();
        CalendarBuilder calendarBuilder = calendarBuilderThreadLocal.get();
        if (calendarBuilder == null){
            calendarBuilder = new CalendarBuilder();
            calendarBuilderThreadLocal.set(calendarBuilder);
        }

        StringReader stringReader = new StringReader(text);
        try {
            calendar = calendarBuilder.build(stringReader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }
        calendarBuilderThreadLocal.remove();
        return calendar;
    }

    public static Calendar getCalendarfromResponse(MultiStatusResponse response){
        return getCalendarfromProperty(response.getProperties(CaldavStatus.SC_OK).get(CalDAVConstants.DNAME_CALENDAR_DATA));
    }


    public static String getEtagfromResponse(MultiStatusResponse response){
        return getEtagfromProperty(response.getProperties(CaldavStatus.SC_OK).get(DavPropertyName.GETETAG));
    }

    public static String getEtagfromProperty(DavProperty property){
        if(property == null || !property.getName().equals(DavPropertyName.GETETAG)) return null;
        return property.getValue().toString().replaceAll("^\"|\"$", "");
    }
}
