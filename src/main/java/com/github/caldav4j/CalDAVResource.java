/*
 * Copyright 2006 Open Source Applications Foundation
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
 */
package com.github.caldav4j;

import com.github.caldav4j.model.ContactInfo;
import com.github.caldav4j.model.response.CalendarDataProperty;
import net.fortuna.ical4j.model.Calendar;
import org.apache.jackrabbit.webdav.MultiStatusResponse;

import java.io.Serializable;

/**
 * A serializable class representing the Calendar along with the associated metadata. Used for
 * storing into the cache.
 */
public class CalDAVResource<T extends Serializable> implements Serializable {
    
	private static final long serialVersionUID = -2607152240683030192L;
	private ResourceMetadata resourceMetadata = null;
	private T payload = null;

    /**
     * Construct Resource based on the parameters.
     * @param payload Calendar
     * @param etag ETag of the Calendar Resource
     * @param href Href of the Calendar Resource
     */
    public CalDAVResource(T payload, String etag, String href){
        this.payload = payload;
        ResourceMetadata rm = new ResourceMetadata();
        rm.setETag(etag);
        rm.setHref(href);
        this.resourceMetadata = rm;
    }

    /**
     * Construct Resource based on the parameters.
     * @param payload Calendar
     * @param resourceMetadata Metadata of the Calendar Resource
     * @param href Href of the Calendar Resource
     */
    public CalDAVResource(T payload, ResourceMetadata resourceMetadata){
        this.payload = payload;
        this.resourceMetadata = resourceMetadata;
    }

	/**
	 * Default constructor
	 */
	public CalDAVResource(){
		resourceMetadata = new ResourceMetadata();
	}

	public void setCalendar(Calendar calendar){
		this.payload = (T) calendar;
	}

    public Calendar getCalendar() {
        return (Calendar) payload;
    }

    public ContactInfo getContact() {
        return (ContactInfo) payload;
    }

	public ResourceMetadata getResourceMetadata() {
		return resourceMetadata;
	}

}