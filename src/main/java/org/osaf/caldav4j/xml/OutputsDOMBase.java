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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class OutputsDOMBase implements OutputsDOM{

    protected abstract String getElementName();
    
    protected abstract String getNamespaceQualifier() ;

    protected abstract String getNamespaceURI();

    protected abstract Collection getChildren();
    
    protected abstract Map getAttributes();
    
    protected String getQualifiedName() {
        return getNamespaceQualifier() + ":" + getElementName();
    }
    
    protected abstract String getTextContent();
    
    public Element outputDOM(Document document) {
        Element e = document.createElementNS(getNamespaceURI(),
                getQualifiedName());
        
        fillElement(e);
        return e;
    }
    
    public Document createNewDocument(DOMImplementation domImplementation)
            throws DOMException {
        Document d =  domImplementation.createDocument(getNamespaceURI(),
                getQualifiedName(), null);
        
        Element root =(Element) d.getFirstChild();
        
        fillElement(root);
        return d;
    }

    protected void fillElement(Element e){

        /*
         * Add children elements 
         */
        if (getChildren() != null && getChildren().size() != 0) {
            Iterator i = getChildren().iterator();
            while (i.hasNext()) {
                OutputsDOM node = (OutputsDOM) i.next();
                Element childNode = node.outputDOM(e.getOwnerDocument());
                e.appendChild(childNode);
            }
        }
               
        if (getTextContent() != null){
            e.setTextContent(getTextContent());
        }
        
        /*
         * Add Attributes
         */
        Map attributes = getAttributes();
        if (attributes != null && attributes.size() > 0){
            Iterator i = attributes.keySet().iterator();
            while (i.hasNext()){
                Object key = i.next();
                Object value = attributes.get(key);
                e.setAttribute(key.toString(), value.toString());
            }
        }
    }
}
