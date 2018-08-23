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
package com.github.caldav4j.xml;

import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Used to represent an element for the XML representation.
 *
 * @deprecated Use PropProperty instead.
 */
public class SimpleDOMOutputtingObject extends OutputsDOMBase{
    
    private String elementName = null;
    private Namespace namespace = null;
    private String textContent = null;
    private List<XmlSerializable> children = new ArrayList<XmlSerializable>();
    private Map<String, String> attributes = new HashMap<String, String>();
    
    public SimpleDOMOutputtingObject(){
        
    }

    public SimpleDOMOutputtingObject(String namespaceU, String namespaceQ, String elementName) {
        this(Namespace.getNamespace(namespaceQ, namespaceU), elementName, null);
    }

    public SimpleDOMOutputtingObject(String namespaceU,
                                     String namespaceQ, String elementName, Map<String, String> attributes) {
        this(Namespace.getNamespace(namespaceQ, namespaceU), elementName, attributes);
    }

    public SimpleDOMOutputtingObject(Namespace namespace, String elementName) {
        this(namespace, elementName, null);
    }
    
    public SimpleDOMOutputtingObject(Namespace namespace
            , String elementName, Map<String,String> attributes) {
        this.namespace = namespace;
        this.elementName = elementName;
        this.attributes = attributes;
    }
    
    public Collection<? extends XmlSerializable> getChildren() {
        return children;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public String getElementName() {
        return elementName;
    }
    
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public Namespace getNamespace() {
        return namespace;
    }
    
    public void setChildren(List<XmlSerializable> children) {
        this.children = children;
    }
    
    public void addChild(XmlSerializable outputsDOM) {
        children.add(outputsDOM);
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
    
    public void setQualifiedName(String qualifiedName){
        int colonLocation = qualifiedName.indexOf(":");
        String qualifier = qualifiedName.substring(0, colonLocation);
        String localName = qualifiedName.substring(colonLocation + 1);
        this.namespace = Namespace.getNamespace(qualifier, namespace.getURI());
        this.elementName = localName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    public void addAttribute(String key, String value){
        attributes.put(key, value);
    }
}


