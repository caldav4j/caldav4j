/*
 * Copyright 2011 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Mark Hobson
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
package com.github.caldav4j.model.request;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.DOMValidationException;
import com.github.caldav4j.xml.OutputsDOM;
import com.github.caldav4j.xml.OutputsDOMBase;
import org.apache.jackrabbit.webdav.xml.Namespace;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Writes a CALDAV:free-busy-query REPORT.
 * <pre>
 * &lt;!ELEMENT free-busy-query (time-range)&gt;
 * &lt;!ELEMENT time-range EMPTY&gt;
 * &lt;!ATTLIST time-range start CDATA #IMPLIED
 *                      end   CDATA #IMPLIED&gt;
 * </pre>
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
	
	// constructors -----------------------------------------------------------

    public FreeBusyQuery(){

    }

	public FreeBusyQuery(TimeRange timeRange) {
    	this.timeRange = timeRange;
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

	/**
	 * {@inheritDoc}
	 */
    @Override
    protected Map<String, String> getAttributes() {
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
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

}
