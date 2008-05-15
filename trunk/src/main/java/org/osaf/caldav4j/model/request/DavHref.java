package org.osaf.caldav4j.model.request;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.DOMValidationException;
import org.osaf.caldav4j.xml.OutputsDOM;
import org.osaf.caldav4j.xml.OutputsDOMBase;


 /* Copyright 2008 Babel srl
 *  http://tools.ietf.org/html/rfc2518#section-12.3     
 *      <!ELEMENT href (#PCDATA)>
 *  
 * @author rpolli@babel.it
 * 
 */
public class DavHref extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "href";
    
    private String davNamespaceQualifier = null;
    private String uri = null;

    public DavHref(String davNapespaceQualifier, String uri) {
        this.davNamespaceQualifier = davNapespaceQualifier;
        this.uri = uri.toString();
    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected String getNamespaceQualifier() {
        return davNamespaceQualifier;
    }

    protected String getNamespaceURI() {
        return CalDAVConstants.NS_DAV;
    }
    
    protected String getUri() {
		return uri;
	}
    
    protected void setUri(String u) {
    	uri=u;
	}
    
	protected String getTextContent() {
		// TODO Auto-generated method stub
		return uri.toString();
	}
    
   
    /**
     *   <!ELEMENT comp-filter (is-defined | time-range)?
     *                        comp-filter* prop-filter*>
     *                        
     *   <!ATTLIST comp-filter name CDATA #REQUIRED> 
     */
    public void validate() throws DOMValidationException{
    	return;
    }

	@Override
	protected Map<String, String> getAttributes() {
		// TODO Auto-generated method stub
        Map<String, String> m = null;
        m = new HashMap<String, String>();
		return m;
	}

	@Override
	protected Collection<? extends OutputsDOM> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}
