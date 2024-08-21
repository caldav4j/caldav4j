/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Bobby Rullo, Roberto Polli
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
package com.github.caldav4j.xml;

import com.github.caldav4j.exceptions.DOMValidationException;
import java.util.Collection;
import java.util.Map;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Abstract class for representing objects as Xml */
public abstract class OutputsDOMBase implements OutputsDOM {

    private static final Logger log = LoggerFactory.getLogger(OutputsDOMBase.class);

    /**
     * @return Element Name of representative class
     */
    protected abstract String getElementName();

    /**
     * @return Namespace of the current Element
     */
    protected abstract Namespace getNamespace();

    /**
     * @return Return the associated Namespace URI
     */
    protected String getNamespaceURI() {
        return getNamespace().getURI();
    }

    /**
     * @return Returns the Namespace Qualifier
     */
    protected String getNamespaceQualifier() {
        return getNamespace().getPrefix();
    }

    /**
     * Returns all the associated Children of the Element.
     *
     * @return Collection of Children
     */
    protected abstract Collection<? extends XmlSerializable> getChildren();

    /**
     * Returns the associated name-value attributes with the current element.
     *
     * @return Map of Attributes
     */
    protected abstract Map<String, String> getAttributes();

    /**
     * @return Returns the Text Content of the Element
     */
    protected abstract String getTextContent();

    /**
     * Method for validation of Element. Derived classes should override this to work appropriately.
     *
     * @throws DOMValidationException on validation error
     */
    public void validate() throws DOMValidationException {}

    /**
     * @return Return Qualified name
     */
    public String getQualifiedName() {
        return getNamespaceQualifier() + ":" + getElementName();
    }

    /**
     * Implementing the {@link OutputsDOM} interface, to create a new document based on the children
     * and corresponding attributes
     *
     * @return {@link Document} object representing the class.
     * @throws DOMValidationException on validation error
     * @see OutputsDOM#createNewDocument()
     */
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

    /**
     * We also implement the {@link XmlSerializable} interface to connect with the Jackrabbit's
     * WebDAV implementation.
     *
     * @param document Document to add the Element to.
     * @return Current Element
     */
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

    /**
     * Protected method to add all the children and attributes to the document
     *
     * @param e associated Element
     * @param document associated Document
     */
    protected void fillElement(Element e, Document document) {
        /*
         * Add children elements
         */
        Collection<? extends XmlSerializable> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (XmlSerializable child : children) {
                Element childNode = child.toXml(document);
                e.appendChild(childNode);
            }
        }

        if (getTextContent() != null) {
            e.setTextContent(getTextContent());
        }

        /*
         * Add Attributes
         */
        Map<String, String> attributes = getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                e.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Convenience method for validating all the objects in a collection
     *
     * @param c Collection of objects to validate
     * @throws DOMValidationException on validation error
     */
    protected void validate(Collection<? extends OutputsDOM> c) throws DOMValidationException {
        for (OutputsDOM o : c) {
            o.validate();
        }
    }

    /**
     * Convenience method to throw exception, based on the string.
     *
     * @param m Error string.
     * @throws DOMValidationException on validation error
     */
    protected void throwValidationException(String m) throws DOMValidationException {
        String message = getQualifiedName() + " - " + m;
        throw new DOMValidationException(message);
    }
}
