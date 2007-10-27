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
package org.osaf.caldav4j;

import net.fortuna.ical4j.model.Calendar;

import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.util.QName;
import org.osaf.caldav4j.model.response.CalDAVResponse;

public class CalDAVResource {
    private ResourceMetadata resourceMetadata = null;
    private Calendar calendar = null;
    
    public CalDAVResource(CalDAVResponse response) throws CalDAV4JException{
        this.calendar = response.getCalendar();
        this.resourceMetadata = new ResourceMetadata();
        QName etagQname = new QName(CalDAVConstants.NS_DAV, CalDAVConstants.PROP_GETETAG);
        Property eTagProperty = response.getProperty(etagQname);

        if (eTagProperty != null) {
            this.resourceMetadata.setETag(eTagProperty.getElement()
                    .getTextContent());
        }
        this.resourceMetadata.setHref(response.getHref());
        
    }

    public CalDAVResource(){
        resourceMetadata = new ResourceMetadata();
    }
    
    public void setCalendar(Calendar calendar){
        this.calendar = calendar;
    }
    
    public Calendar getCalendar() {
        return calendar;
    }

    public ResourceMetadata getResourceMetadata() {
        return resourceMetadata;
    }
    
}
