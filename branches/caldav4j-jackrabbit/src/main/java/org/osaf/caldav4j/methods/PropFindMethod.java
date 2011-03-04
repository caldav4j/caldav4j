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

import static org.osaf.caldav4j.CalDAVConstants.NS_CALDAV;
import static org.osaf.caldav4j.CalDAVConstants.NS_DAV;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.util.XMLUtils;
import org.osaf.caldav4j.xml.OutputsDOM;
import org.w3c.dom.Document;

public class PropFindMethod extends org.apache.jackrabbit.webdav.client.methods.PropFindMethod {
    private static final Log log = LogFactory
    	.getLog(PropFindMethod.class);
    private OutputsDOM propFindRequest;
    
    protected Vector<DavProperty<?>> responseTable = new Vector<DavProperty<?>>();
    protected Collection<String> responseURLs = new Vector<String>();
    private static Map<QName, Error> errorMap = null;
    private Error error = null;
    
    public enum ErrorType{PRECONDITION, POSTCONDITON}

   
    public PropFindMethod(String path) throws IOException {
       super(path);
   }
    
    public void setPropFindRequest(OutputsDOM myprop) {
        this.propFindRequest = myprop;
    }

    /**
     * Generates a request body from the calendar query.
     */
    protected byte[] generateRequestBody() {
        Document doc = null;
        try {
            doc = propFindRequest.createNewDocument(XMLUtils
                    .getDOMImplementation());
        } catch (DOMValidationException domve) {
            log.error("Error trying to create DOM from CalDAVPropfindRequest: ", domve);
            throw new RuntimeException(domve);
        }
        return XMLUtils.toPrettyXML(doc).getBytes();
    }

    
    /**
     * Precondtions and Postconditions
     * @author bobbyrullo
     *
     */
    public enum Error {
        SUPPORTED_CALENDAR_DATA(ErrorType.PRECONDITION, NS_CALDAV, "supported-calendar-data"),
        VALID_FILTER(ErrorType.PRECONDITION, NS_CALDAV, "valid-filter"),
        NUMBER_OF_MATCHES_WITHIN_LIMITS(ErrorType.POSTCONDITON, NS_DAV, "number-of-matches-within-limits");
        
        private final ErrorType errorType;
        private final String namespaceURI;
        private final String elementName;
        
        Error(ErrorType errorType, String namespaceURI, String elementName){
            this.errorType = errorType;
            this.namespaceURI = namespaceURI;
            this.elementName = elementName;
        }
        
        public ErrorType errorType() { return errorType; }
        public String namespaceURI() { return namespaceURI; }
        public String elementName(){ return elementName; }
        
    }
    
    static {
        errorMap = new HashMap<QName, Error>();
        for (Error error : Error.values()) {
            errorMap.put(new QName(error.namespaceURI(), error.elementName()),
                    error);
        }
    }
    
    public static final String ELEMENT_ERROR ="error";
    

    
    public Error getError(){
        return error;
    }
    
    /**
     * return the AclProperty relative to a given url
     * @author rpolli
     * @param urlPath
     * @return AclProperty xml response or null if missing
     */
    public org.apache.jackrabbit.webdav.security.AclProperty getAcl(String urlPath) {
    	return (AclProperty) getWebDavProperty(urlPath, new QName(NS_DAV, "acl"));
    }
    public List<org.apache.jackrabbit.webdav.security.AclProperty.Ace> getAces(String urlPath) throws CalDAV4JException {

    	AclProperty acls = (AclProperty) getWebDavProperty(urlPath,new QName(NS_DAV, "acl"));
    	if (acls != null) {
        	return acls.getValue();
	
    	}
    	throw new CalDAV4JException("Error gettinh ACLs. PROPFIND status is: ?");
    }
    public String getCalendarDescription(String urlPath) {
       DavProperty<?> p= getWebDavProperty(urlPath, 
             new QName(NS_CALDAV,CalDAVConstants.CALDAV_CALENDAR_DESCRIPTION));
        if (p != null) {
           return  p.getValue().toString();
        } else {
           return "";
        }
    }
    public String getDisplayName(String urlPath) {
       DavProperty<?> p= getWebDavProperty(urlPath, 
    	     new QName(NS_CALDAV,CalDAVConstants.DAV_DISPLAYNAME));
    	if (p != null) {
    		return  p.getValue().toString();
    	} else {
    		return "";
    	}
    }

    private DavProperty<?> getWebDavProperty(String urlPath, QName property) {

      for (MultiStatusResponse response : multiStatus.getResponses()){
         int status = HttpStatus.SC_OK;// && status <= HttpStatus.SC_MULTI_STATUS) {
         DavPropertySet set =response.getProperties(status);
         if(set ==null)continue;
         DavProperty<?> dp=set.get(CalDAVConstants.DAV_PROP,
               Namespace.getNamespace(  property.getPrefix(),property.getNamespaceURI()));
         if(dp ==null)continue;
         Object  caldata = dp.getValue();
        if(caldata ==null)continue;
         return dp;
      }
      return null;
    }
  

    protected Collection<String> getResponseURLs() {
        checkUsed();
    
        return responseURLs;
    }
    
    protected MultiStatus multiStatus;
    @Override
    protected void processMultiStatusBody(MultiStatus multiStatus, HttpState httpState, HttpConnection httpConnection) {
      this. multiStatus=multiStatus;
      for (MultiStatusResponse response : multiStatus.getResponses()){
         responseURLs.add( response.getHref());
      }
       
    }
}
