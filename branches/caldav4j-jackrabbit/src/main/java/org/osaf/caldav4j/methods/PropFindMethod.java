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

import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.webdav.DavException;

import org.apache.jackrabbit.webdav.xml.DomUtil;

import org.w3c.dom.Element;

import org.apache.jackrabbit.webdav.security.AclProperty.Ace;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
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
    protected MultiStatus multiStatus;
    private int depth;
    
    public enum ErrorType{PRECONDITION, POSTCONDITON}

   
    public PropFindMethod(String path) throws IOException {
       super(path);
   }
    
    public void setPropFindRequest(OutputsDOM myprop) {
        this.propFindRequest = myprop;
    }
    /**
     * Generate additional headers needed by the request.
     *
     * @param state State token
     * @param conn The connection being used to make the request.
     */
    public void addRequestHeaders(HttpState state, HttpConnection conn)
    throws IOException, HttpException 
    {
       //first add headers generate RequestEntity or 
       //addContentLengthRequestHeader() will mess up things > result "400 Bad Request"
       //can not override generateRequestBody(), because called to often
        setRequestEntity(new ByteArrayRequestEntity(generateRequestBytes()));
        super.addRequestHeaders(state, conn);

        switch (depth) {
        case DEPTH_0:
            super.setRequestHeader("Depth", "0");
            break;
        case DEPTH_1:
            super.setRequestHeader("Depth", "1");
            break;
        case DEPTH_INFINITY:
            super.setRequestHeader("Depth", CalDAVConstants.INFINITY_STRING);
            break;
        }

        if (getRequestHeader(CalDAVConstants.HEADER_CONTENT_TYPE) == null) {
         addRequestHeader(CalDAVConstants.HEADER_CONTENT_TYPE,CalDAVConstants.CONTENT_TYPE_TEXT_XML);
        }
    }
    /**
     * Generates a request body from the calendar query.
     */
    protected byte[] generateRequestBytes() {
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
    public DavProperty<?> getAcl(String urlPath) {
    	return getWebDavProperty(urlPath, new QName(NS_DAV, "acl"));
    }
    public List<Ace> getAces(String urlPath) throws CalDAV4JException {
       DavProperty<?> acl = getAcl(urlPath);
       
       try {
         org.w3c.dom.Document d = DomUtil.createDocument();
          //Element root = (Element) d.getFirstChild();
          Element els = acl.toXml(d);
          AclProperty aclp = AclProperty.createFromXml( els);
          
          List<Ace> aces =  aclp.getValue();
          return aces;
      } catch (ParserConfigurationException e) {
         throw new CalDAV4JException("Error gettinh ACLs. PROPFIND status is: ?",e);
      } catch (DavException e) {
         throw new CalDAV4JException("Error gettinh ACLs. PROPFIND status is: ?",e);
      }
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
    	     new QName(NS_DAV,CalDAVConstants.DAV_DISPLAYNAME));
    	if (p != null) {
    		return  p.getValue().toString();
    	} else {
    		return "";
    	}
    }

    private DavProperty<?> getWebDavProperty(String urlPath, QName property) {
       if (multiStatus == null)
          return  null;
      for (MultiStatusResponse response : multiStatus.getResponses()){
         int status = HttpStatus.SC_OK;// && status <= HttpStatus.SC_MULTI_STATUS) {
         DavPropertySet set =response.getProperties(status);
         if(set ==null)continue;
         DavProperty<?> dp=set.get(property.getLocalPart(),
               Namespace.getNamespace(  property.getPrefix(),property.getNamespaceURI()));
         if(dp ==null)continue;
         Object  data = dp.getValue();
        if(data ==null)continue;
         return dp;
      }
      return null;
    }
  
    protected DavProperty<?> getResponseProperty(QName property) {
     for (DavProperty<?> response :responseTable){
       String name =  response.getName()==null?"":response.getName().getName();
       //String val =  "" +response.getValue();
       if(property.getLocalPart().equals(name)){
          return response;
       }
     }
     return null;      
    }
    
    protected Collection<String> getResponseURLs() {
        checkUsed();
    
        return responseURLs;
    }
    
    public Collection<DavProperty<?>> getResponseTable() {
      return responseTable;
   }
    
    @Override
    protected void processMultiStatusBody(MultiStatus multiStatus, HttpState httpState, HttpConnection httpConnection) {
      this. multiStatus=multiStatus;
      for (MultiStatusResponse response : multiStatus.getResponses()){
         responseURLs.add( response.getHref());
         DavPropertySet set =response.getProperties(HttpStatus.SC_OK);
         if(set ==null)continue;
         for (DavProperty<?> prop : set) {
            responseTable.add(prop);
         }
      }
      
       
    }

    /**
     * Depth setter.
     *
     * @param depth New depth value
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Depth getter.
     *
     * @return int depth value
     */
    public int getDepth() {
        return depth;
    }
    
}
