package com.github.caldav4j.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import com.github.caldav4j.exceptions.AuthorizationException;
import com.github.caldav4j.exceptions.BadStatusException;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.exceptions.ResourceNotFoundException;
import com.github.caldav4j.exceptions.ResourceNotFoundException.IdentifierType;
import com.github.caldav4j.exceptions.ResourceOutOfDateException;

/**
 * Method Utilities
 */
public class MethodUtil {
	
	/**
	 * Throws various exceptions depending on the status &gt;= 400 of the given method
	 * @param method Method causing error
	 * @param response Response containing error
	 * @return Should not return, instead it should throw an exception.
	 * @throws CalDAV4JException or an extended class to represent the status.
	 */
	public static int StatusToExceptions(HttpRequestBase method, HttpResponse response) throws CalDAV4JException {
		if (method != null && response != null) {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 300) {				
				switch (status) {
				case CalDAVStatus.SC_CONFLICT:
					throw new ResourceOutOfDateException("Conflict accessing: " + method.getURI() );
				case CalDAVStatus.SC_NOT_FOUND:
					throw new ResourceNotFoundException(IdentifierType.PATH, method.getURI().toString());
				case CalDAVStatus.SC_UNAUTHORIZED:
					throw new AuthorizationException("Unauthorized accessing " + method.getURI() );
				case CalDAVStatus.SC_PRECONDITION_FAILED:
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
