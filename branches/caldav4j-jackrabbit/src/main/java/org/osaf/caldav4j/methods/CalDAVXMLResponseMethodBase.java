package org.osaf.caldav4j.methods;

import static org.osaf.caldav4j.CalDAVConstants.NS_CALDAV;
import static org.osaf.caldav4j.CalDAVConstants.NS_DAV;

import java.util.Collection;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.jackrabbit.webdav.client.methods.DavMethodBase;
import org.osaf.caldav4j.model.response.CalDAVResponse;

/**
 * Provide methods to parse caldav xml responsethat
 * XXX uses CalDAVResponse, so applies only to "calendar-data" response  
 * @author rpolli
 *
 */
public abstract class CalDAVXMLResponseMethodBase extends DavMethodBase{
	public CalDAVXMLResponseMethodBase(String uri) {
		super(uri);
	}
	protected Vector<CalDAVResponse> responseTable = null;
	private static Map<QName, Error> errorMap = null;
	private Error error = null;
	protected Collection<String> responseURLs = null;
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
         return getResponseVector().elements();
	}

	public Error getError(){
		return error;
	}

	protected Vector<CalDAVResponse> getResponseVector()  {

		if (responseTable == null) {
			init();
		}
		return responseTable;
	}
	
	protected Hashtable<String, CalDAVResponse> getResponseHashtable() {
		throw new RuntimeException("Unimplemented method");
	}

	protected Collection<String> getResponseURLs() throws IOException {

		if (responseTable == null) {
			init();
		}
		return responseURLs;
	}
	/**
	 * A lot of this code had to be copied from the parent XMLResponseMethodBase, since it's 
	 * initHashtable doesn't allow for new types of Responses.
	 * 
	 * Of course, the same mistake is being made here, so it is a TODO to fix that
	 * @throws IOException 
	 *
	 */
	protected void init(){
    responseTable = new Vector<CalDAVResponse>();
    responseURLs = new Vector<String>();
	}
}
