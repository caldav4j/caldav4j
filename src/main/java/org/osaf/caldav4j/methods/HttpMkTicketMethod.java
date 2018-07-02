package org.osaf.caldav4j.methods;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.exceptions.CalDAV4JProtocolException;
import org.osaf.caldav4j.exceptions.DOMValidationException;
import org.osaf.caldav4j.model.request.TicketRequest;
import org.osaf.caldav4j.model.response.TicketResponse;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.UrlUtils;
import org.osaf.caldav4j.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class HttpMkTicketMethod extends BaseDavRequest {

	private static final Logger log = LoggerFactory.getLogger(HttpMkTicketMethod.class);
	
    private TicketRequest ticketRequest;

    private Document responseDocument = null;

    protected Vector<String> responseNames = null;

    protected DocumentBuilder builder = null;
	
    public HttpMkTicketMethod(String path, TicketRequest ticketRequest) {
    	super(URI.create(UrlUtils.removeDoubleSlashes(path)));
        this.ticketRequest = ticketRequest;
    }
    
    public void setPath(String path){
    	super.setURI(URI.create(UrlUtils.removeDoubleSlashes(path)));
    }

    @Override
    public String getMethod() {
        return CalDAVConstants.METHOD_MKTICKET;
    }
    
    public TicketRequest getTicketRequest() {
        return ticketRequest;
    }

    public void setTicketRequest(TicketRequest ticketRequest) {
        this.ticketRequest = ticketRequest;
    }

    public Document getResponseDocument() {
        return this.responseDocument;
    }

    /** In httpclient 3.1 this was a protected method which was called automatically. 
     * In httpclient 4 this method must be called explicitly to set the request entity. */
    /**
     * Creates a DOM Representation of the TicketRequest, turns into XML and
     * sets as the XML entity
     */
    public void generateRequestBody() {
        Document doc = null;
        try {
            doc = ticketRequest.createNewDocument();
        } catch (DOMValidationException domve) {
            log.error("Error trying to create DOM from MkTicketMethod: ",
                            domve);
            throw new RuntimeException(domve);
        }

        ContentType ct = ContentType.create(CalDAVConstants.CONTENT_TYPE_TEXT_XML,Charset.forName("UTF-8")); 
        setEntity(new StringEntity(XMLUtils.toPrettyXML(doc),ct));        
    }
    
    
    //TODO !A! modify so that this is called
    /** For httpclient 3.1 this was a protected method that was called automatically before the request 
     *  was executed. With httpclient 4 this method needs to be called explicitly.  */
    public void addRequestHeaders() 
    {
    	addHeader(CalDAVConstants.HEADER_CONTENT_TYPE, CalDAVConstants.CONTENT_TYPE_TEXT_XML);
    }
    

    @Override
    public boolean succeeded(HttpResponse response) {
 	   int statusCode = response.getStatusLine().getStatusCode();
 	   return statusCode == CaldavStatus.SC_OK;
    }

    
    /**
     * Returns a TicketResponse from the response body.
     * 
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws CalDAV4JProtocolException -
     *             If the Response Body is not XML
     * @throws CalDAV4JException -
     *             if the Response Body is unavailable
     */
    public TicketResponse getResponseBodyAsTicketResponse(HttpResponse response) throws IOException,
            SAXException, CalDAV4JProtocolException, CalDAV4JException {
        Header header = getFirstHeader("Content-Type");
        String contentType = (header != null) ? header.getValue() : ""; 
        if (!contentType.startsWith("text/xml")) {
            log.error("Content type must be \"text/xml\" to parse as an "
                    + "xml resource. Type was: " + contentType);
            throw new CalDAV4JProtocolException(
                    "Content type must be \"text/xml\" to parse as an "
                            + "xml resource");
        }
        
        this.responseDocument = getResponseBodyAsDocument(response.getEntity());
        
        int statusCode = response.getStatusLine().getStatusCode();

        if (CaldavStatus.SC_OK == statusCode) {
            Element docElement = this.responseDocument.getDocumentElement();

            TicketResponse tr = XMLUtils
                    .createTicketResponseFromDOM(docElement);

            return tr;
        }
        return null;

    }    
    
    


}
