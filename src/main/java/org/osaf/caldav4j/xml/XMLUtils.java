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

package org.osaf.caldav4j.xml;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class XMLUtils {
    
    private static DOMImplementation implementation = null;
    
    static {
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry
                    .newInstance();
            implementation = registry.getDOMImplementation("XML 3.0");
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not instantiate a DOMImplementation!", e);
        }
    }
    
    /**
     * Creates a new xml DOM Document using a DOM 3.0 DOM Implementation
     * 
     * @param namespaceURI
     *            the default XML Namespace for the document
     * @param qualifiedName
     *            the qualified name of the root element
     * @return a new document
     */
    public static Document createNewDocument(String namespaceURI,
            String qualifiedName) {

        Document document = implementation.createDocument(namespaceURI,
                qualifiedName, null);
        return document;
    }
    
    /**
     * Serializes a DOM Document to XML 
     * @param document a DOM document
     * @return the Document serialized to XML
     */
    public static String toXML(Document document){
        DOMImplementationLS domLS = (DOMImplementationLS) implementation;
        LSSerializer serializer = domLS.createLSSerializer();
        String s = serializer.writeToString(document);
        return s;
    }    
    
    public static DOMImplementation getDOMImplementation(){
        return implementation;
    }
    
    public static void main (String[] args){
        Document d = createNewDocument("DAV:", "D:dude");
        Node root = d.getFirstChild();
        Element ele1 = d.createElement("NO");
        Element ele2 = d.createElement("YES");

        String s = toXML(d);
        System.out.println(s);
    }
}
