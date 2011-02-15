package org.osaf.caldav4j.methods;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.util.UrlUtils;

public class OptionsMethod extends org.apache.jackrabbit.webdav.client.methods.OptionsMethod {
    private static final Log log = LogFactory.getLog(GetMethod.class);

    public OptionsMethod(String path) {
    	super(UrlUtils.removeDoubleSlashes(path));
    }
    
    // remove double slashes
    public void setPath(String path) {
    	super.setPath(UrlUtils.removeDoubleSlashes(path));
    }
}
