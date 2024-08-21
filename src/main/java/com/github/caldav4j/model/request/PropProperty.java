/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra
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
package com.github.caldav4j.model.request;

import java.util.*;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A simple, customizable DAV property with children. This can be used with practically any node,
 * this has been extended from {@link DefaultDavProperty} to include the ability to add Attributes
 * and Children.
 *
 * <p>ex. &lt;D:ACL&gt;&lt;/D:ACL&gt;
 *
 * @author Ankush Mishra
 * @see DefaultDavProperty
 */
public class PropProperty<T> extends DefaultDavProperty<T> {

    private Map<String, String> attributes = null;
    private Collection<XmlSerializable> children = new ArrayList<>();

    /**
     * Create a Property from the parameters
     *
     * @param namespaceURI Namespace URI
     * @param namespaceQualifier Namespace qualifier
     * @param propertyName Property Name
     */
    public PropProperty(String namespaceURI, String namespaceQualifier, String propertyName) {
        this(namespaceURI, namespaceQualifier, propertyName, null);
    }

    /**
     * Create a Property from the parameters
     *
     * @param namespaceURI Namespace URI
     * @param namespaceQualifier Namespace qualifier
     * @param propertyName Property Name
     * @param children Children of property
     */
    public PropProperty(
            String namespaceURI,
            String namespaceQualifier,
            String propertyName,
            Collection<XmlSerializable> children) {

        this(
                propertyName,
                null,
                Namespace.getNamespace(namespaceQualifier, namespaceURI),
                null,
                children);
    }

    /**
     * Create a Property from the parameters
     *
     * @param name Property Name
     * @param namespace Namespace of the property.
     */
    public PropProperty(String name, Namespace namespace) {
        super(name, null, namespace);
    }

    /**
     * Create a Property from the parameters
     *
     * @param name Property Name
     * @param namespace Namespace of the property.
     * @param value Value of the property
     */
    public PropProperty(String name, T value, Namespace namespace) {
        super(name, value, namespace);
    }

    /**
     * Create a Property from the parameters
     *
     * @param name Property Name
     * @param namespace Namespace of the property.
     * @param value Value of the property
     * @param attributes Map of Key-value pairs serving as attributes
     */
    public PropProperty(String name, T value, Namespace namespace, Map<String, String> attributes) {
        super(name, value, namespace);
        this.attributes = attributes;
    }

    /**
     * Create a Property from the parameters
     *
     * @param name Property Name
     * @param namespace Namespace of the property.
     * @param value Value of the property
     * @param attributes Map of Key-value pairs serving as attributes
     * @param children Collection of Children of property
     */
    public PropProperty(
            String name,
            T value,
            Namespace namespace,
            Map<String, String> attributes,
            Collection<XmlSerializable> children) {
        super(name, value, namespace);
        this.attributes = attributes;
        this.children = children;
    }

    /**
     * Create a Property from the parameters
     *
     * @param name Property Name
     * @param namespace Namespace of the property.
     * @param value Value of the property
     * @param attributes Map of Key-value pairs serving as attributes
     * @param children DavPropertyNameSet of Children of property
     */
    public PropProperty(
            String name,
            T value,
            Namespace namespace,
            Map<String, String> attributes,
            DavPropertyNameSet children) {
        super(name, value, namespace);
        this.attributes = attributes;
        this.addChildren(children);
    }

    /**
     * Create a Property from the parameters
     *
     * @param name Property Name
     * @param namespace Namespace of the property.
     * @param value Value of the property
     * @param attributes Map of Key-value pairs serving as attributes
     * @param children DavPropertySet of Children of property
     */
    public PropProperty(
            String name,
            T value,
            Namespace namespace,
            Map<String, String> attributes,
            DavPropertySet children) {
        super(name, value, namespace);
        this.attributes = attributes;
        this.addChildren(children);
    }

    /**
     * @return Return the map of attributes of the property
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Add attribute to current element.
     *
     * @param key Key of Attribute
     * @param value Value of the Key
     */
    public void addAttribute(String key, String value) {
        if (attributes == null) attributes = new HashMap<>();
        attributes.put(key, value);
    }

    /**
     * Add individual child to property.
     *
     * @param property Property to add.
     */
    public void addChild(XmlSerializable property) {
        if (this.getValue() != null)
            throw new java.lang.IllegalStateException(
                    "Adding of children not allowed, when value is non null");

        children.add(property);
    }

    /**
     * Add collection of Children to property
     *
     * @param collection Collection to add
     */
    public void addChildren(Collection<? extends XmlSerializable> collection) {
        if (collection == null || collection.isEmpty()) return;

        children.addAll(collection);
    }

    /**
     * Add collection of Children to property
     *
     * @param propertyNames Set to add as children
     */
    public void addChildren(DavPropertyNameSet propertyNames) {
        if (propertyNames == null || propertyNames.isEmpty()) return;

        children.addAll(propertyNames.getContent());
    }

    /**
     * Add collection of Children to property
     *
     * @param properties PropertySet to add.
     */
    public void addChildren(DavPropertySet properties) {
        if (properties == null || properties.isEmpty()) return;

        for (DavProperty property : properties) {
            children.add(property);
        }
    }

    /**
     * Add collection of Prop as children to property
     *
     * @param prop Prop to add.
     */
    public void addChildren(Prop<?> prop) {
        this.children.addAll(prop.getChildren());
    }

    /**
     * @return Returns the Collection of children.
     */
    public Collection<? extends XmlSerializable> getChildren() {
        return children;
    }

    /**
     * Remove individual child as specified by prop
     *
     * @param prop Property to remove
     */
    public void removeChild(XmlSerializable prop) {
        if (children != null && prop != null) children.remove(prop);
    }

    /**
     * Remove multiple child as specified by the parameter
     *
     * @param props PropertyNameSet to remove
     */
    public void removeChildren(DavPropertyNameSet props) {
        if (children != null && props != null) children.removeAll(props.getContent());
    }

    /**
     * Remove multiple child as specified by the parameter
     *
     * @param props PropertySet to remove
     */
    public void removeChildren(DavPropertySet props) {
        if (children != null && props != null)
            for (DavProperty prop : props) {
                children.remove(prop);
            }
    }

    /**
     * Remove multiple child as specified by the parameter
     *
     * @param props Collection to remove
     */
    public void removeChildren(Collection<? extends XmlSerializable> props) {
        children.removeAll(props);
    }

    /**
     * @return Return true, if no children exist, else false.
     */
    public boolean isChildrenEmpty() {
        return (children == null || children.isEmpty());
    }

    /**
     * Returns Children object from the {@link DavProperty} property
     *
     * @param property Property to retrieve children from.
     * @return Collection of DavProperties, with all the child properties, of the property, from the
     *     Value of the property.
     */
    public static Collection<DavProperty> getChildrenfromValue(DavProperty property) {

        Collection<DavProperty> coll = new ArrayList<>();
        if (property != null) {

            if (property.getValue() instanceof DavProperty) {
                coll.add((DavProperty) property.getValue());
                return coll;
            } else if (property.getValue() instanceof Node[]) {
                for (Node e : (Node[]) property.getValue()) {
                    if (e instanceof Element)
                        coll.add(DefaultDavProperty.createFromXml((Element) e));
                }
                return coll;
            } else if (property.getValue() instanceof Collection) {

                for (Object entry : ((Collection<?>) property.getValue())) {
                    if (entry instanceof DavProperty) {
                        coll.add((DavProperty) entry);
                    } else if (entry instanceof Element) {
                        coll.add(DefaultDavProperty.createFromXml((Element) entry));
                    }
                }
            }
        }

        return coll;
    }

    /**
     * Add children elements
     *
     * @param e Element to fill
     * @param document Document to add to the element
     */
    protected void fillElement(Element e, Document document) {
        Collection<? extends XmlSerializable> children = getChildren();
        if (children != null) {
            for (XmlSerializable node : children) {
                Element childNode = node.toXml(document);
                e.appendChild(childNode);
            }
        }
    }

    /**
     * @see XmlSerializable#toXml(Document)
     * @param document to which this element belongs.
     * @return Element containing this property.
     */
    public Element toXml(Document document) {
        Element elem = getName().toXml(document);

        // Set Attributes for the current Property
        if (attributes != null) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                elem.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        T value = getValue();
        // todo: improve....
        if (value != null) {
            if (value instanceof XmlSerializable) {
                elem.appendChild(((XmlSerializable) value).toXml(document));
            } else if (value instanceof Node) {
                Node n = document.importNode((Node) value, true);
                elem.appendChild(n);
            } else if (value instanceof Node[]) {
                for (int i = 0; i < ((Node[]) value).length; i++) {
                    Node n = document.importNode(((Node[]) value)[i], true);
                    elem.appendChild(n);
                }
            } else if (value instanceof Collection) {
                for (Object entry : ((Collection<?>) value)) {
                    if (entry instanceof XmlSerializable) {
                        elem.appendChild(((XmlSerializable) entry).toXml(document));
                    } else if (entry instanceof Node) {
                        Node n = document.importNode((Node) entry, true);
                        elem.appendChild(n);
                    } else {
                        DomUtil.setText(elem, entry.toString());
                    }
                }
            } else {
                DomUtil.setText(elem, value.toString());
            }
        } else {
            fillElement(elem, document);
        }

        return elem;
    }

    /**
     * @return Return the Property Iterator to iterate through all the children
     */
    public PropPropertyIterator Iterator() {
        return new PropPropertyIterator();
    }

    // Iterator of all children

    /** Iterator class for iterating through the children. */
    protected class PropPropertyIterator {

        private Iterator<XmlSerializable> iter;

        private PropPropertyIterator() {
            this.iter = children.iterator();
        }

        public XmlSerializable nextPropertyName() {
            return iter.next();
        }

        public void remove() {
            iter.remove();
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public XmlSerializable next() {
            return iter.next();
        }
    }
}
