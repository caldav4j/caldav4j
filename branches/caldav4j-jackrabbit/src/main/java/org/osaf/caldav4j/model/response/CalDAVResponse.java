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
 */
package org.osaf.caldav4j.model.response;

import java.io.StringReader;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import org.osaf.caldav4j.exceptions.CalDAV4JException;


public class //CalDAVMultiResponse 
//extends 
CalDAVResponse //implements ResponseEntity 
{


 
    private Calendar calendar = null;
    private String calData;
    private ThreadLocal<CalendarBuilder> calendarBuilderThreadLocal = new ThreadLocal<CalendarBuilder>();
    public CalDAVResponse(String calData) {
       super();
       this.calData=calData;
   }
    private CalendarBuilder getCalendarBuilderInstance(){
       CalendarBuilder builder = calendarBuilderThreadLocal.get();
       if (builder == null){
           builder = new CalendarBuilder();
           calendarBuilderThreadLocal.set(builder);
       }
       return builder;
   }

    public Calendar getCalendar() throws CalDAV4JException {
       if (calendar != null) {
          return calendar;
       }

       String text = calData;//getElement().getTextContent();
       text.trim();
       
       
       //text might contain lines breaked only with \n. RFC states that long lines must be delimited by CRLF.
       //@see{http://www.apps.ietf.org/rfc/rfc2445.html#sec-4.1 }
       //this fix the problem occurred when lines are breaked only with \n 
       text=text.replaceAll("\n","\r\n").replaceAll("\r\r\n", "\r\n");
       
       // FIXME
       
//     Pattern noDayLight = Pattern.compile("BEGIN:VTIMEZONE.*END:VTIMEZONE", Pattern.DOTALL);
//     Matcher m = noDayLight.matcher(text);
//     text = m.replaceAll("");
       StringReader stringReader = new StringReader(text);
       //System.out.println(text); // FIXME debug
       try {
          calendar = getCalendarBuilderInstance().build(stringReader);
          stringReader = null;
          return calendar;
       } catch (Exception e) {
          throw new CalDAV4JException("Problem building calendar", e);
       }
    }
 
}
