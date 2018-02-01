package org.osaf.caldav4j.methods;

import org.osaf.caldav4j.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the OPTIONS HTTP Method.
 *
 * @see org.apache.jackrabbit.webdav.client.methods.OptionsMethod
 */
public class OptionsMethod extends org.apache.jackrabbit.webdav.client.methods.OptionsMethod {
	private static final Logger log = LoggerFactory.getLogger(OptionsMethod.class);

	/**
	 * @param uri URI to resource
	 */
	public OptionsMethod(String uri) {
		super(UrlUtils.removeDoubleSlashes(uri));
	}

	/**
	 * @see org.apache.commons.httpclient.HttpMethodBase#setPath(String)
	 */
	public void setPath(String path) {
		super.setPath(UrlUtils.removeDoubleSlashes(path));
	}
}
