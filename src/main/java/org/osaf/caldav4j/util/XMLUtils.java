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

package org.osaf.caldav4j.util;

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.model.response.TicketResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class XMLUtils {
	private static final Logger log = LoggerFactory.getLogger(XMLUtils.class);

	/**
	 * Creates a new xml DOM Document using a DOM 3.0 DOM Implementation
	 *
	 * @return a new document
	 */
	public static Document createNewDocument() throws ParserConfigurationException {
		return  DomUtil.createDocument();
	}

	/**
	 * Serializes a DOM Document to XML
	 * 
	 * @param document
	 *            a DOM document
	 * @return the Document serialized to XML
	 */
	public static String toXML(Document document) {
		StringWriter writer = new StringWriter();
		try {
			DomUtil.transformDocument(document, writer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}

    /**
     * Updated to use Transformer, instead of XmlSerializer because it is deprecated
     * @param doc
     * @return
     */
	public static String toPrettyXML(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(domSource, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
	}

	/**
	 * Create a string representation of an DOM document
	 */
	public static String prettyPrint(XmlSerializable xml) {
		try {
			Document doc = DomUtil.createDocument();
			doc.appendChild(xml.toXml(doc));
			return XMLUtils.toPrettyXML(doc);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;		
	}

	/**
	 * Takes a ticketinfo element and creates/returns it as a TicketResponse
	 * Object
	 * 
	 * @param element
	 * @return
	 */
	public static TicketResponse createTicketResponseFromDOM(Element element) {
		TicketResponse tr = new TicketResponse();

		NodeList list = element.getElementsByTagNameNS(
				CalDAVConstants.NS_XYTHOS, CalDAVConstants.ELEM_ID);
		Element temp = (Element) list.item(0);
		tr.setID(temp.getTextContent());

		list = element.getElementsByTagNameNS(CalDAVConstants.NS_DAV,
				CalDAVConstants.ELEM_HREF);
		temp = (Element) list.item(0);
		tr.setOwner(temp.getTextContent());

		list = element.getElementsByTagNameNS(CalDAVConstants.NS_XYTHOS,
				CalDAVConstants.ELEM_TIMEOUT);
		temp = (Element) list.item(0);
		String tempTO = temp.getTextContent();

		// Parses the timeout element's value into units and value in form Second-99999, otherwise no timeout (Infinite)
		int idx=tempTO.indexOf('-');
		if (idx!=-1)
		{
			// Store the Parsed Values
			//  default timeout is Integer("")
			tr.setUnits(tempTO.substring(0,idx));
			tr.setTimeout(new Integer(tempTO.substring(idx+1)));
		}
		list = element.getElementsByTagNameNS(CalDAVConstants.NS_XYTHOS,
				CalDAVConstants.ELEM_VISITS);
		temp = (Element) list.item(0);
		String visits = temp.getTextContent();
		Integer visitsInt = null;
		if (visits.equals(CalDAVConstants.INFINITY_STRING)) {
			visitsInt = CalDAVConstants.INFINITY;
		} else {
			visitsInt = new Integer(visits);
		}
		tr.setVisits(visitsInt);

		if (element.getElementsByTagNameNS(CalDAVConstants.NS_DAV,
				CalDAVConstants.ELEM_READ) != null) {
			tr.setRead(true);
		} else {
			tr.setRead(false);
		}

		if (element.getElementsByTagNameNS(CalDAVConstants.NS_DAV,
				CalDAVConstants.ELEM_WRITE) != null) {
			tr.setWrite(true);
		} else {
			tr.setWrite(false);
		}

		return tr;
	}

}
