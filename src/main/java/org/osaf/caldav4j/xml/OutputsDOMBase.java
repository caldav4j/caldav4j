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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class OutputsDOMBase implements OutputsDOM{

    private static final Log log = LogFactory.getLog(OutputsDOMBase.class);
    
    protected abstract String getElementName();

    protected abstract Namespace getNamespace();

    protected String getNamespaceURI() { return getNamespace().getURI(); }

    protected String getNamespaceQualifier() { return getNamespace().getPrefix(); }

    protected abstract Collection<? extends XmlSerializable> getChildren();
    
    protected abstract Map<String, String> getAttributes();

    protected abstract String getTextContent();
    
    public void validate() throws DOMValidationException{
        return;
    }

    public String getQualifiedName(){
        return getNamespaceQualifier() + ":" + getElementName();
    }

    public Document createNewDocument() throws DOMValidationException {
        try {
            validate();
            Document d = DomUtil.createDocument();
            Element root = toXml(d);
            d.appendChild(root);
            return d;
        } catch (Exception e) {
            log.error("Error creating Document.");
            throw new DOMValidationException("Error creating Document.");
        }
    }

    public Element toXml(Document document) {
        Element root = null;
        try {
            validate();
            root = DomUtil.createElement(document, getElementName(), getNamespace());
            fillElement(root, document);
        } catch (DOMValidationException e) {
            log.error("Error creating element. Validation failed.");
            e.printStackTrace();
        }

        return root;
    }

    protected void fillElement(Element e, Document document) throws DOMValidationException{
        /*
         * Add children elements
         */
        Collection<? extends XmlSerializable> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for(XmlSerializable child : children) {
                Element childNode = child.toXml(document);
                e.appendChild(childNode);
            }
        }

        if (getTextContent() != null){
            e.setTextContent(getTextContent());
        }

        /*
         * Add Attributes
         */
        Map<String, String> attributes = getAttributes();
        if(attributes != null && !attributes.isEmpty()) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                e.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * Convienience method for validating all the objects in a collection
     * @param c
     * @throws DOMValidationException
     */
    protected void validate(Collection<? extends OutputsDOM> c) throws DOMValidationException{
        for (Iterator<? extends OutputsDOM> i = c.iterator(); i.hasNext(); ){
            OutputsDOM o = i.next();
            o.validate();
        }
    }
    
    protected void throwValidationException(String m) throws DOMValidationException{
        String message = getQualifiedName() + " - " + m;
        throw new DOMValidationException(message);
    }
}
