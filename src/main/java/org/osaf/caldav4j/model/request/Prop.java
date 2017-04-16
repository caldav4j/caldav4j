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
package org.osaf.caldav4j.model.request;

import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.osaf.caldav4j.CalDAVConstants;

import java.util.Collection;

/**
 * Defines a container DAV:prop Property.
 *
 * @param <T>
 */

public class Prop<T> extends PropProperty<T>{
    public static final String ELEMENT_NAME = "prop";

    public Prop( ){ super(ELEMENT_NAME, CalDAVConstants.NAMESPACE_WEBDAV); }

    public Prop(Collection<XmlSerializable> children){
        super(ELEMENT_NAME, null, CalDAVConstants.NAMESPACE_WEBDAV, null, children);
    }

    public Prop(DavPropertyNameSet children){
        super(ELEMENT_NAME, null, CalDAVConstants.NAMESPACE_WEBDAV, null, children);
    }

    public Prop(DavPropertySet children){
        super(ELEMENT_NAME, null, CalDAVConstants.NAMESPACE_WEBDAV, null, children);
    }

    public boolean isEmpty(){ return this.isChildrenEmpty(); }

    public void add(XmlSerializable prop){
        this.addChild(prop);
    }

    public void remove(XmlSerializable prop){
        this.removeChild(prop);
    }
}
