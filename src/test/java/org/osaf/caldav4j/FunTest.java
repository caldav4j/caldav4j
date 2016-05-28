package org.osaf.caldav4j;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.Value;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.methods.PropFindMethod;
import org.apache.webdav.lib.methods.XMLResponseMethodBase.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osaf.caldav4j.functional.support.CaldavFixtureHarness;
import org.osaf.caldav4j.methods.HttpClient;

import java.util.Enumeration;
import java.util.Vector;

public class FunTest extends BaseTestCase {
	public FunTest() {
		super();
	}

	private static final Log log = LogFactory
	.getLog(FunTest.class);


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
		HostConfiguration hostConfig = createHostConfiguration();

		PropFindMethod propFindMethod = new PropFindMethod();
		PropertyName propName = new PropertyName(CalDAVConstants.NS_DAV, "resourcetype");
		propFindMethod.setDepth(CalDAVConstants.DEPTH_INFINITY);
		propFindMethod.setPath(caldavCredential.home + "collection_changeme/");
		propFindMethod.setType(PropFindMethod.BY_NAME);
		Vector<PropertyName> v = new Vector<PropertyName>();
		v.add(propName);
		propFindMethod.setPropertyNames(v.elements());
		http.executeMethod(hostConfig, propFindMethod);
		Enumeration<Response> e = propFindMethod.getResponses();
		while (e.hasMoreElements()){
			Response response = (Response) e.nextElement();
			Enumeration<Property> eProp = response.getProperties();
			while (eProp.hasMoreElements()){
				Property property = (Property) eProp.nextElement();
				String nodeName = property.getElement().getNodeName();
				String localName = property.getElement().getLocalName();
				String tagName = property.getElement().getTagName();
				String namespaceURI = property.getElement().getNamespaceURI();
				log.info("nodename: " + nodeName);
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
				log.info(d);
			}
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}




}
