/*
 * Copyright 2011 Open Source Applications Foundation
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
package org.osaf.caldav4j.model.request;

import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.xml.OutputsDOM;
import org.osaf.caldav4j.xml.OutputsDOMBase;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Writes a CALDAV:free-busy-query REPORT.
 * 
 * <!ELEMENT free-busy-query (time-range)>
 * 
 * <!ELEMENT time-range EMPTY>
 * 
 * <!ATTLIST time-range start CDATA #IMPLIED
 *                      end   CDATA #IMPLIED>
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see <a href="http://tools.ietf.org/html/rfc4791#section-7.10">7.10. CALDAV:free-busy-query REPORT</a>
 */
public class FreeBusyQuery extends OutputsDOMBase implements CalDAVReportRequest
{
	// constants --------------------------------------------------------------
	
	public static final String ELEMENT_NAME = "free-busy-query";
	
	// fields -----------------------------------------------------------------
	
	private TimeRange timeRange;
    private Prop properties = new Prop();
    private int depth = CalDAVConstants.DEPTH_1;
	
	// constructors -----------------------------------------------------------

    public FreeBusyQuery(){

    }

    public FreeBusyQuery(Prop propertyNames,
                         TimeRange timeRange)
    {
        this.timeRange = timeRange;
        properties.addChildren(propertyNames);
    }

	public FreeBusyQuery(DavPropertyNameSet propertyNames,
						 TimeRange timeRange)
	{
        this.timeRange = timeRange;
        properties.addChildren(propertyNames);
	}

    public FreeBusyQuery(Collection<? extends XmlSerializable> propertyNames,
                         TimeRange timeRange)
    {
        this.timeRange = timeRange;
        properties.addChildren(propertyNames);
    }
	
	// OutputsDOMBase methods -------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */

	protected String getElementName()
	{
		return ELEMENT_NAME;
	}
	

	/**
	 * {@inheritDoc}
	 */

	protected Namespace getNamespace()
	{
		return CalDAVConstants.NAMESPACE_CALDAV;
	}
	
	/**
	 * {@inheritDoc}
	 */

	protected Collection<? extends OutputsDOM> getChildren()
	{
		return Collections.singleton(timeRange);
	}

    @Override
    protected Map<String, String> getAttributes() {
        return null;
    }

    @Override
    protected String getTextContent() {
        return null;
    }

    /**
	 * {@inheritDoc}
	 */

	public void validate() throws DOMValidationException
	{
		if (timeRange == null)
		{
			throw new DOMValidationException("Time range cannot be null");
		}
		
		timeRange.validate();
	}
	
	// public methods ---------------------------------------------------------
	
	public TimeRange getTimeRange()
	{
		return timeRange;
	}
	
	public void setTimeRange(TimeRange timeRange)
	{
		this.timeRange = timeRange;
	}

    public void addProperty(XmlSerializable propProperty){
        properties.add(propProperty);
    }

    public void addProperty(String propertyName, Namespace namespace) {
        PropProperty propProperty = new PropProperty(propertyName, namespace);
        properties.add(propProperty);
    }

}
