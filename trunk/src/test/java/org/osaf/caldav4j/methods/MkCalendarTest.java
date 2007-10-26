package org.osaf.caldav4j.methods;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.BaseTestCase;

public class MkCalendarTest extends BaseTestCase {

    public void testCreateRemoveCalendarCollection(){
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath("/home/bobby/TESTY");
//        mk.addPropertyToSet("bobby:","B:funkyzeit", "frumpus");
//        mk.addPropertyToSet("crumpus:","CR:funkyzeit", "crumpus");
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        try {
            http.executeMethod(hostConfig, mk);
        } catch (Exception e){
            assertNull(e);
        }
        
        int statusCode = mk.getStatusCode();
        assertEquals("Status code was " + statusCode + " but it should have been " + WebdavStatus.SC_CREATED,statusCode,WebdavStatus.SC_CREATED);

    }
}
