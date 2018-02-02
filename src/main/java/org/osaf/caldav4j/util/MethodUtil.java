package org.osaf.caldav4j.util;

import org.apache.commons.httpclient.HttpMethod;
import org.osaf.caldav4j.exceptions.*;
import org.osaf.caldav4j.exceptions.ResourceNotFoundException.IdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Method Utilities
 */
public class MethodUtil {
	private static final Logger log = LoggerFactory.getLogger(MethodUtil.class);

	/**
	 * Throws various exceptions depending on the status of &gt;= 400 of the given method
	 * @param method HTTPMethod
	 * @return Status Code is successful.
	 * @throws CalDAV4JException based on the Status Code
	 */
	public static int StatusToExceptions(HttpMethod method) throws CalDAV4JException {
		if (method != null) {
			int status = method.getStatusCode();
			if (log.isDebugEnabled()) {
				try {
					log.debug("Server returned " + method.getResponseBodyAsString());
				} catch (IOException e) {
					throw new CalDAV4JException("Error retrieving server response", e);
				}
			}
			if (status >= 300) {
				switch (status) {
					case CaldavStatus.SC_CONFLICT:
						throw new ResourceOutOfDateException("Conflict accessing: " + method.getPath() );
					case CaldavStatus.SC_NOT_FOUND:
						throw new ResourceNotFoundException(IdentifierType.PATH, method.getPath());
					case CaldavStatus.SC_UNAUTHORIZED:
						throw new AuthorizationException("Unauthorized accessing " + method.getPath() );
					case CaldavStatus.SC_PRECONDITION_FAILED:
						throw new ResourceOutOfDateException("Resource out of date: " + method.getPath());
					default:
						throw new BadStatusException(status, method.getName(), method.getPath());
				}
			}
			return status;
		}
		throw new CalDAV4JException("Null method");
	}
}
