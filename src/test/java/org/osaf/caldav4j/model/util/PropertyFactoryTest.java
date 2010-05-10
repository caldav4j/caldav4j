package org.osaf.caldav4j.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Property;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.model.request.PropProperty;
import org.osaf.caldav4j.util.XMLUtils;

public class PropertyFactoryTest {
    protected static final Log log = LogFactory.getLog(PropertyFactoryTest.class);

	@Test
	public void createProperty() throws CalDAV4JException {
		PropProperty p = null;
		
		p = PropertyFactory.createProperty(PropertyFactory.ACL);
		log.info(XMLUtils.prettyPrint(p));
		
		p = PropertyFactory.createProperty(PropertyFactory.OWNER);
		log.info(XMLUtils.prettyPrint(p));
	}
}
