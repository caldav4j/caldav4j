package org.osaf.caldav4j.methods;

import static org.osaf.caldav4j.CalDAVConstants.NS_CALDAV;
import static org.osaf.caldav4j.CalDAVConstants.NS_DAV;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.webdav.lib.methods.XMLResponseMethodBase;
import org.apache.webdav.lib.util.DOMUtils;
import org.apache.webdav.lib.util.QName;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.model.response.CalDAVResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provide methods to parse caldav xml responsethat
 * XXX uses CalDAVResponse, so applies only to "calendar-data" response  
 * @author rpolli
 *
 */
public abstract class CalDAVXMLResponseMethodBase extends XMLResponseMethodBase{
    private Hashtable<String, CalDAVResponse> responseHashtable = null;
    private static Map<QName, Error> errorMap = null;
    private Error error = null;
    
    public enum ErrorType{PRECONDITION, POSTCONDITON}

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
    
    /**
     * Return an enumeration containing the responses.
     *
     * @return An enumeration containing objects implementing the
     * ResponseEntity interface
     */
    public Enumeration<CalDAVResponse> getResponses() {
        return getResponseHashtable().elements();
    }
    
    public Error getError(){
        return error;
    }
    
    protected Hashtable<String, CalDAVResponse> getResponseHashtable() {
        checkUsed();
        if (responseHashtable == null) {
            initHashtable();
        }
        return responseHashtable;
    }

    protected Vector<String> getResponseURLs() {
        checkUsed();
        if (responseHashtable == null) {
            initHashtable();
        }
        return responseURLs;
    }
    /**
     * A lot of this code had to be copied from the parent XMLResponseMethodBase, since it's 
     * initHashtable doesn't allow for new types of Responses.
     * 
     * Of course, the same mistake is being made here, so it is a TODO to fix that
     *
     */
    @SuppressWarnings("unchecked")
    private void initHashtable(){
        responseHashtable = new Hashtable<String, CalDAVResponse>();
        responseURLs = new Vector<String>();
        // Also accept OK sent by buggy servers in reply to a PROPFIND
        // or REPORT (Xythos, Catacomb, ...?).
        int statusCode = getStatusCode();
        if (statusCode == WebdavStatus.SC_MULTI_STATUS) {


            Document rdoc = getResponseDocument();

            NodeList list = null;
            if (rdoc != null) {
                Element multistatus = getResponseDocument().getDocumentElement();
                list = multistatus.getChildNodes();
            }

            if (list != null) {
                for (int i = 0; i < list.getLength(); i++) {
                    try {
                        Element child = (Element) list.item(i);
                        String name = DOMUtils.getElementLocalName(child);
                        String namespace = DOMUtils.getElementNamespaceURI
                            (child);
                        if (Response.TAG_NAME.equals(name) &&
                            "DAV:".equals(namespace)) {
                            CalDAVResponse response =
                                new CalDAVResponse(child);
                            String href = response.getHref() ;
                            responseHashtable.put(href,response);
                            responseURLs.add(href);
                        }
                    } catch (ClassCastException e) {
                    }
                }
            }
        } else if (statusCode == WebdavStatus.SC_CONFLICT || statusCode == WebdavStatus
                .SC_FORBIDDEN){
            Document rdoc = getResponseDocument();
            Element errorElement = rdoc.getDocumentElement();
            
            // first make sure that the element is actually an error.
            if (!errorElement.getNamespaceURI().equals(NS_DAV)
                    || !errorElement.getLocalName().equals(ELEMENT_ERROR)) {
                Node condition = errorElement.getChildNodes().item(0);
                error = errorMap.get(new QName(condition.getNamespaceURI(),
                        condition.getLocalName()));
            }
        }
    }
}
