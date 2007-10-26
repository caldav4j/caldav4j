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

package org.osaf.caldav4j.methods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.webdav.lib.methods.XMLResponseMethodBase;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.DOMValidationException;
import org.osaf.caldav4j.model.MkCalendar;
import org.osaf.caldav4j.model.Prop;
import org.osaf.caldav4j.model.PropProperty;
import org.osaf.caldav4j.model.util.XMLUtils;
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
    public void addPropertyToSet(String namespaceURI, String qualifiedName,
            String value) {
        checkNotUsed();
        PropProperty propertyToSet = new PropProperty();
        propertyToSet.setQualifiedName(qualifiedName);
        propertyToSet.setTextContent(value);
        propertyToSet.setNamespaceURI(namespaceURI);
        propertiesToSet.add(propertyToSet);
    }

    // --------------------------------------------------- WebdavMethod Methods

    public String getName() {
        return CalDAVConstants.METHOD_MKCALENDAR;
    }
    
    /**
     *
     */
    protected String generateRequestBody() {
        if (propertiesToSet.size() == 0 ){
            return null;
        }
        
        Prop prop = new Prop("D", propertiesToSet);
        MkCalendar mkCalendar = new MkCalendar("C", "D",prop);
        Document d = null;
        try {
            d = mkCalendar.createNewDocument(XMLUtils
                    .getDOMImplementation());
        } catch (DOMValidationException domve) {
            throw new RuntimeException(domve);
        }
        return XMLUtils.toXML(d);

    }
    
    
    public static void main (String args[]){
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath("/home/bobby/TESTY");
        mk.addPropertyToSet("bobby:","B:funkyzeit", "frumpus");
        mk.addPropertyToSet("bobby:","B:funkyzeit", "famishj");
        mk.addPropertyToSet("crumpus:","CR:funkyzeit", "<CR:crack/>");
        System.out.println(mk.generateRequestBody());
        //XMLUtils.
    }
}
