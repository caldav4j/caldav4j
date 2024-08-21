/*
 * Copyright 2005 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Bobby Rullo
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

import com.github.caldav4j.CalDAVConstants;
import java.util.Collection;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

/**
 * Defines a container DAV:prop Property. Based on the {@link PropProperty}
 *
 * @param <T> Type of Value of the Property
 */
public class Prop<T> extends PropProperty<T> {
    public static final String ELEMENT_NAME = "prop";

    /** Default Constructor */
    public Prop() {
        super(ELEMENT_NAME, CalDAVConstants.NAMESPACE_WEBDAV);
    }

    /**
     * Create a Property with children
     *
     * @param children Children of this property.
     */
    public Prop(Collection<XmlSerializable> children) {
        super(ELEMENT_NAME, null, CalDAVConstants.NAMESPACE_WEBDAV, null, children);
    }

    /**
     * Create a Property with children
     *
     * @param children Children of this property.
     */
    public Prop(DavPropertyNameSet children) {
        super(ELEMENT_NAME, null, CalDAVConstants.NAMESPACE_WEBDAV, null, children);
    }

    /**
     * Create a Property with children
     *
     * @param children Children of this property.
     */
    public Prop(DavPropertySet children) {
        super(ELEMENT_NAME, null, CalDAVConstants.NAMESPACE_WEBDAV, null, children);
    }

    /**
     * @return Return true if children are empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.isChildrenEmpty();
    }

    /**
     * Add child to this property
     *
     * @param prop Child to add
     */
    public void add(XmlSerializable prop) {
        this.addChild(prop);
    }

    /**
     * Remove individual children from this property
     *
     * @param prop Prop to remove
     */
    public void remove(XmlSerializable prop) {
        this.removeChild(prop);
    }
}
