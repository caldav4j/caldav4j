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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleDOMOutputtingObject extends OutputsDOMBase{
    
    private String elementName;
    private String namespaceQualifier;
    private String namespaceURI;
    private String textContent;
    private List children = new ArrayList();
    
    public Collection getChildren() {
        return children;
    }

    public void setNamespaceQualifier(String namespaceQualifier) {
        this.namespaceQualifier = namespaceQualifier;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public String getElementName() {
        return elementName;
    }
    
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getNamespaceQualifier() {
        return namespaceQualifier;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }
    
    public void setChildren(List children) {
        this.children = children;
    }
    
    public void addChild(OutputsDOM outputsDOM) {
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
        this.namespaceQualifier = qualifier;
        this.elementName = localName;
    }

}
