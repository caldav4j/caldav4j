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
package org.osaf.caldav4j.methods;

import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.model.request.CalDAVReportRequest;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Fake report request used by tests.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id: FakeCalDAVReportRequest.java 294 2011-02-22 11:50:25Z markhobson $
 */
public class FakeCalDAVReportRequest implements CalDAVReportRequest
{
	// OutputDOM methods ------------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public Element outputDOM(Document document) throws DOMValidationException
	{
		return document.createElement("fake-query");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Document createNewDocument(DOMImplementation dom) throws DOMValidationException
	{
		Document document = dom.createDocument(null, null, null);
		
		Element documentElement = outputDOM(document);
		document.appendChild(documentElement);
		
		return document;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void validate() throws DOMValidationException
	{
		// no-op
	}
}
