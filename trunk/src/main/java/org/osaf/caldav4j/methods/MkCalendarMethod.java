package org.osaf.caldav4j.methods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.webdav.lib.methods.XMLResponseMethodBase;
import org.osaf.caldav4j.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MkCalendarMethod extends XMLResponseMethodBase{
    
    /**
     * Map of the properties to set.
     */
    protected List propertiesToSet = new ArrayList();

    // --------------------------------------------------------- Public Methods


    /**
     * 
     */
    public void addPropertyToSet(String namespaceURI, String qualifiedName, String value){
        checkNotUsed();
        Property propertyToSet = new Property();
            propertyToSet.qualifiedName = qualifiedName;
            propertyToSet.value = value;
            propertyToSet.namespace= namespaceURI;
            propertyToSet.namespace = namespaceURI;
            propertiesToSet.add(propertyToSet);
    }

    // --------------------------------------------------- WebdavMethod Methods

    public String getName() {
        return "MKCALENDAR";
    }
    
    /**
     *
     */
    protected String generateRequestBody() {
        if (propertiesToSet.size() == 0 ){
            return null;
        }
        Document document = XMLUtils.createNewDocument(XMLUtils.NS_CALDAV, "C:mkcalendar");
        Node root = document.getFirstChild();
        Element set = document.createElementNS(XMLUtils.NS_DAV, "D:set");
        root.appendChild(set);
        for (Iterator i = propertiesToSet.iterator(); i.hasNext();){
            Element davProp = document.createElementNS(XMLUtils.NS_DAV, "D:prop");
            Property propObject = (Property) i.next();
            Element propToSet = document.createElementNS(propObject.namespace,
                    propObject.qualifiedName);
            Node textNode = document.createTextNode(propObject.value);
            propToSet.appendChild(textNode);
            davProp.appendChild(propToSet);
            set.appendChild(davProp);
        }
        return XMLUtils.toXML(document);
    }
    
    // --------------------------------------------------- Property Inner Class

    private class Property {

        public String qualifiedName = null;
        public String namespace = null;
        public String value = null;

    }
    
    public static void main (String args[]){
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath("/home/bobby/TESTY");
        mk.addPropertyToSet("bobby:","B:funkyzeit", "frumpus");
        mk.addPropertyToSet("crumpus:","CR:funkyzeit", "crumpus");
        System.out.println(mk.generateRequestBody());
        //XMLUtils.
    }
}
