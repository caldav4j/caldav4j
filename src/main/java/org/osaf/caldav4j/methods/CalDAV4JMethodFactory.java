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

import org.osaf.caldav4j.CalDAVConstants;

public class CalDAV4JMethodFactory {

    String procID = CalDAVConstants.PROC_ID_DEFAULT;
    private boolean validatingOutputter = false;
    
    private ThreadLocal<CalendarBuilder> calendarBuilderThreadLocal = new ThreadLocal<CalendarBuilder>();
    private CalendarOutputter calendarOutputter = null;
    
    public CalDAV4JMethodFactory(){
        
    }
    
    public String getProcID() {
        return procID;
    }

    public void setProcID(String procID) {
        this.procID = procID;
    }

    public PutMethod createPutMethod(){
        PutMethod putMethod = new PutMethod();
        putMethod.setProcID(procID);
        putMethod.setCalendarOutputter(getCalendarOutputterInstance());
        return putMethod;
    }
    
    public MkCalendarMethod createMkCalendarMethod(){
        MkCalendarMethod mkCalendarMethod = new MkCalendarMethod();
        return mkCalendarMethod;
    }
    
    public GetMethod createGetMethod(){
        GetMethod getMethod = new GetMethod();
        getMethod.setCalendarBuilder(getCalendarBuilderInstance());
        return getMethod;
    }
    
    public boolean isCalendarValidatingOutputter() {
        return validatingOutputter;
    }

    public void setCalendarValidatingOutputter(boolean validatingOutputter) {
        this.validatingOutputter = validatingOutputter;
    }
    
    private synchronized CalendarOutputter getCalendarOutputterInstance(){
        if (calendarOutputter == null){
            calendarOutputter = new CalendarOutputter(validatingOutputter);
        }
        return calendarOutputter;
    }
    
    private CalendarBuilder getCalendarBuilderInstance(){
        CalendarBuilder builder = calendarBuilderThreadLocal.get();
        if (builder == null){
            builder = new CalendarBuilder();
            calendarBuilderThreadLocal.set(builder);
        }
        return builder;
    }
}
