package org.osaf.caldav4j.methods;

import org.osaf.caldav4j.util.UrlUtils;


public class DeleteMethod extends org.apache.jackrabbit.webdav.client.methods.DeleteMethod {

	public DeleteMethod(String s) {
		super(UrlUtils.removeDoubleSlashes(s));
	}

    // remove double slashes
    public void setPath(String path) {
    	super.setPath(UrlUtils.removeDoubleSlashes(path));
    }
}
