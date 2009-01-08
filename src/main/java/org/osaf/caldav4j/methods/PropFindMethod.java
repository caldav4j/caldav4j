/*
 * Copyright 2006 Open Source Applications Foundation
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
package org.osaf.caldav4j.methods;

import static org.osaf.caldav4j.util.UrlUtils.stripHost;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.properties.PropertyFactory;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.DOMValidationException;
import org.osaf.caldav4j.model.request.CalDAVReportRequest;
import org.osaf.caldav4j.model.request.Prop;
import org.osaf.caldav4j.model.response.TicketDiscoveryProperty;
import org.osaf.caldav4j.util.XMLUtils;
import org.osaf.caldav4j.xml.OutputsDOM;
import org.osaf.caldav4j.xml.OutputsDOMBase;
import org.w3c.dom.Document;

/**
 * This method is overwritten in order to register the ticketdiscovery element
 * with the PropertyFactory.
 * 
 * @author EdBindl
 * 
 */
public class PropFindMethod extends
        org.apache.webdav.lib.methods.PropFindMethod {
    private static final Log log = LogFactory
    	.getLog(PropFindMethod.class);
    private OutputsDOM reportRequest;

    /**
     * Registers the TicketDiscoveryProperty with the PropertyFactory
     */
    static {
        try {
            PropertyFactory.register(CalDAVConstants.NS_XYTHOS,
                    CalDAVConstants.ELEM_TICKETDISCOVERY,
                    TicketDiscoveryProperty.class);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not register TicketDiscoveryProperty!", e);
        }
    }
    
    public PropFindMethod() {
        super();
    }
    
    public PropFindMethod(String path, Enumeration propertyNames) {
        super(path, propertyNames);
    }
    
    /**
     * Returns an enumeration of <code>Property</code> objects.
     */
    public Enumeration getResponseProperties(String urlPath) {
        checkUsed();

        Response response = (Response) getResponseHashtable().get(urlPath);
        if (response == null){
            response = (Response) getResponseHashtable().get(stripHost(urlPath));
        }
        if (response != null) {
            return response.getProperties();
        } else {
            return (new Vector()).elements();
        }

    }
    
    public void setReportRequest(OutputsDOM myprop) {
        this.reportRequest = myprop;
    }

    /**
     * Generates a request body from the calendar query.
     */
    protected String generateRequestBody() {
        Document doc = null;
        try {
            doc = reportRequest.createNewDocument(XMLUtils
                    .getDOMImplementation());
        } catch (DOMValidationException domve) {
            log.error("Error trying to create DOM from CalDAVPropfindRequest: ", domve);
            throw new RuntimeException(domve);
        }
        return XMLUtils.toPrettyXML(doc);
    }
}
