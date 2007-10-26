package org.osaf.caldav4j.xml;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class XMLUtils {
    
    public static final String NS_CALDAV = "urn:ietf:params:xml:ns:caldav";
    public static final String NS_DAV = "DAV";
    
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
}
