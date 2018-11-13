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

import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.xml.OutputsDOMBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *  Specifies a request that includes the WebDAV property values to be set for a calendar
 *  collection resource when it is created.
 * <pre>
 * &lt;!ELEMENT mkcalendar (DAV:set)&gt;
 * </pre>
 * @author bobbyrullo
 *
 */
public class MkCalendar extends OutputsDOMBase{
    public static final String ELEMENT_NAME = "mkcalendar";
    public static final String SET_ELEMENT_NAME = "set";

    private Prop prop = null;

    public MkCalendar(Prop prop){
        this.prop = prop;
    }
    
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    protected Collection<XmlSerializable> getChildren() {
        Collection<XmlSerializable> c  = new ArrayList<>();
        PropProperty set = new PropProperty(SET_ELEMENT_NAME, CalDAVConstants.NAMESPACE_WEBDAV);
        set.addChild(prop);
        c.add(set);
        return c;
    }

    protected String getTextContent() {
        return null;
    }
    protected Map<String, String> getAttributes() {
        return null;
    }
}
