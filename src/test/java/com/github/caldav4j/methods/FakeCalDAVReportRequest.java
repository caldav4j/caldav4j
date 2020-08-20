/*
 * Copyright 2011 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Mark Hobson, Roberto Polli
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

package com.github.caldav4j.methods;

import com.github.caldav4j.exceptions.DOMValidationException;
import com.github.caldav4j.model.request.CalDAVReportRequest;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Fake report request used by tests.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class FakeCalDAVReportRequest implements CalDAVReportRequest {
    // OutputDOM methods ------------------------------------------------------

    /** {@inheritDoc} */
    public Element toXml(Document document) {
        return document.createElement("fake-query");
    }

    /** {@inheritDoc} */
    public Document createNewDocument() {
        Document document = null;
        try {
            document = DomUtil.createDocument();
            Element documentElement = toXml(document);
            document.appendChild(documentElement);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return document;
    }

    /** {@inheritDoc} */
    public void validate() throws DOMValidationException {
        // no-op
    }
}
