package org.osaf.caldav4j.methods;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 *
 */
public class NewPropFindTest extends BaseTestCase{
    private static final Logger log = LoggerFactory.getLogger(NewPropFindTest.class);

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        fixture.tearDown();
    }

    @Test
    public void basicTest() throws IOException, DavException {
        HttpClient http = fixture.getHttpClient();;
        HttpHost hostConfig = fixture.getHostConfig();
        DavPropertyNameSet props = new DavPropertyNameSet();
        props.add(DavPropertyName.DISPLAYNAME);

        HttpPropFindMethod p = new HttpPropFindMethod(fixture.getCollectionPath(), props, DavConstants.DEPTH_0);

        HttpResponse response = http.execute(hostConfig, p);
        log.info(response.getStatusLine().toString());
        MultiStatusResponse[] responses = p.getResponseBodyAsMultiStatus(response).getResponses();
        for(MultiStatusResponse r: responses){
            log.info(r.getHref());
        }
        log.info(p.getDisplayName(response, fixture.getCollectionPath()));
    }

    @Test
    public  void testGetAcl() throws IOException, DavException, ParserConfigurationException, CalDAV4JException {
        String collectionPath = fixture.getCollectionPath();
        HttpClient http = fixture.getHttpClient();
        HttpHost hostConfig = fixture.getHostConfig();
        DavPropertyNameSet props = new DavPropertyNameSet();
        //Propfind for only DAV:acl
        props.add(CalDAVConstants.DAV_ACL, CalDAVConstants.NAMESPACE_WEBDAV);


        HttpPropFindMethod p = new HttpPropFindMethod(collectionPath, props, DavConstants.DEPTH_0);

        HttpResponse response = http.execute(hostConfig, p);
        log.info(response.getStatusLine().toString());

        //Get all the processed aces and print them to log.
        List<AclProperty.Ace> aces = p.getAces(response, collectionPath);
        try {
            print_Ace(aces);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }



    private void print_Ace(List<AclProperty.Ace> aces) throws ParserConfigurationException, TransformerException {
        Document document = DomUtil.createDocument();
        for(AclProperty.Ace ace : aces){
            ElementoString(ace.toXml(document));
        }

    }

    private String ElementoString(Element node) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(node);
        transformer.transform(source, result);

        String xmlString = result.getWriter().toString();
        log.info(xmlString);
        return xmlString;
    }
}
