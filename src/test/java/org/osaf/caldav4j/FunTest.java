package org.osaf.caldav4j;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.Value;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osaf.caldav4j.functional.support.CaldavFixtureHarness;
import org.osaf.caldav4j.methods.HttpPropFindMethod;
import org.osaf.caldav4j.util.CaldavStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunTest extends BaseTestCase {
	public FunTest() {
		super();
	}

	private static final Logger log = LoggerFactory.getLogger(FunTest.class);


	@Before
	public void setUp() throws Exception {
		super.setUp();

		CaldavFixtureHarness.provisionSimpleEvents(fixture);
	}

	@After
	public void tearDown() throws Exception {
		fixture.tearDown();
	}

	@Test
	public void testFun() throws Exception{
		HttpClient http = createHttpClient();
		HttpHost hostConfig = createHostConfiguration();

		DavPropertyNameSet set = new DavPropertyNameSet();
        DavPropertyName resourcetype= DavPropertyName.create("resourcetype");
		set.add(resourcetype);
		HttpPropFindMethod propFindMethod = new HttpPropFindMethod(fixture.getCollectionPath(), set,
				CalDAVConstants.DEPTH_INFINITY);

		HttpResponse httpResponse = http.execute(hostConfig, propFindMethod);
		MultiStatusResponse[] e = propFindMethod.getResponseBodyAsMultiStatus(httpResponse).getResponses();

        for(MultiStatusResponse response : e){
            DavPropertySet properties = response.getProperties(CaldavStatus.SC_OK);
            log.info("HREF: " + response.getHref());
            for(DavProperty property: properties) {
                String nodeName = property.getName().toString();
                log.info("nodename: " + nodeName + "\n"
                        + "value: " + property.getValue());
            }
        }
	}

	public static void main (String args[]){
		try {
			Recur recur  = new Recur("FREQ=HOURLY");
			DateTime startDate = new DateTime("20060101T010000Z");
			DateTime endDate =   new DateTime("20060105T050000Z");
			DateTime baseDate =  new DateTime("20050101T033300");
			DateList dateList 
			= recur.getDates(baseDate, startDate, endDate, Value.DATE_TIME);
			for (int x = 0; x < dateList.size(); x++){
				DateTime d = (DateTime) dateList.get(x);
				log.info(d.toString());
			}
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}




}
