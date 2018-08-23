package com.github.caldav4j.util;

import com.github.caldav4j.exceptions.*;
import com.github.caldav4j.exceptions.ResourceNotFoundException.IdentifierType;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Method Utilities
 */
public class MethodUtil {
	private static final Logger log = LoggerFactory.getLogger(MethodUtil.class);
	
	/**
	 * Throws various exceptions depending on the status >= 400 of the given method
	 * @param method
	 * @return
	 * @throws CalDAV4JException
	 * @throws  
	 */
	public static int StatusToExceptions(HttpRequestBase method, HttpResponse response) throws CalDAV4JException {
		if (method != null && response != null) {
			int status = response.getStatusLine().getStatusCode();
			if (log.isDebugEnabled()) {
				try {
					log.debug("Server returned " + EntityUtils.toString(response.getEntity(),"UTF-8"));
				} catch (IOException e) {
					throw new CalDAV4JException("Error retrieving server response", e);
				}
			}
			if (status >= 300) {				
				switch (status) {
				case CaldavStatus.SC_CONFLICT:
					throw new ResourceOutOfDateException("Conflict accessing: " + method.getURI() );
				case CaldavStatus.SC_NOT_FOUND:
					throw new ResourceNotFoundException(IdentifierType.PATH, method.getURI().toString());
				case CaldavStatus.SC_UNAUTHORIZED:
					throw new AuthorizationException("Unauthorized accessing " + method.getURI() );
				case CaldavStatus.SC_PRECONDITION_FAILED:
					throw new ResourceOutOfDateException("Resource out of date: " + method.getURI());
				default:
					throw new BadStatusException(status, method.getMethod(), method.getURI().toString());
				} 
			}
			return status;	
		}
		throw new CalDAV4JException("Null method or null response");
	}
	
}
