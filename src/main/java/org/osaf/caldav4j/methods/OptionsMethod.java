package org.osaf.caldav4j.methods;

import org.osaf.caldav4j.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptionsMethod extends org.apache.jackrabbit.webdav.client.methods.OptionsMethod {
    private static final Logger log = LoggerFactory.getLogger(OptionsMethod.class);

    public OptionsMethod(String uri) {
    	super(UrlUtils.removeDoubleSlashes(uri));
    }
    
    // remove double slashes
    public void setPath(String path) {
    	super.setPath(UrlUtils.removeDoubleSlashes(path));
    }
}
