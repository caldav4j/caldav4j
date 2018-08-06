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

/**
 * Class containing utility functions for XML related work.
 */
public class XMLUtils {
	private static final Logger log = LoggerFactory.getLogger(XMLUtils.class);

	/**
	 * Creates a new xml DOM Document using a DOM 3.0 DOM Implementation
	 *
	 * @return a new document
	 * @throws ParserConfigurationException on erroneous state, mostly should not happen.
	 */
	public static Document createNewDocument() throws ParserConfigurationException {
		return  DomUtil.createDocument();
	}

	/**
	 * Serializes a DOM Document to XML
	 *
	 * @param document a DOM document
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
	 * Print the Document provided as "pretty" XML. This has been updated to use
	 * Transformer for it's task
	 *
	 * @param doc Document to transform
	 * @return String representation of the XML document
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
	 *
	 * @param xml XmlSerializable object
	 * @return Return the String representation.
	 */
	public static String prettyPrint(XmlSerializable xml) {
		try {
			Document doc = DomUtil.createDocument();
			doc.appendChild(xml.toXml(doc));
			return XMLUtils.toPrettyXML(doc);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
