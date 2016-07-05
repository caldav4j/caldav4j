package org.osaf.caldav4j.methods;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log log = LogFactory.getLog(PropFindTest.class);

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
        HostConfiguration hostConfig = http.getHostConfiguration();
        DavPropertyNameSet props = new DavPropertyNameSet();
        props.add(DavPropertyName.DISPLAYNAME);

        PropFindMethod p = new PropFindMethod(fixture.getCollectionPath(), props, DavConstants.DEPTH_0);

        http.executeMethod(hostConfig, p);
        log.info(p.getStatusLine());
        MultiStatusResponse[] responses = p.getResponseBodyAsMultiStatus().getResponses();
        for(MultiStatusResponse r: responses){
            log.info(r.getHref().equals(fixture.getCollectionPath()));
        }
        log.info(p.getDisplayName(fixture.getCollectionPath()));
    }

    @Test
    public  void testGetAcl() throws IOException, DavException, ParserConfigurationException, CalDAV4JException {
        String collectionPath = fixture.getCollectionPath();
        HttpClient http = fixture.getHttpClient();
        HostConfiguration hostConfig = http.getHostConfiguration();
        DavPropertyNameSet props = new DavPropertyNameSet();
        //Propfind for only DAV:acl
        props.add(CalDAVConstants.DAV_ACL, CalDAVConstants.NAMESPACE_WEBDAV);


        PropFindMethod p = new PropFindMethod(collectionPath, props, DavConstants.DEPTH_0);

        http.executeMethod(hostConfig, p);
        log.info(p.getStatusLine());

        //Get all the processed aces and print them to log.
        List<AclProperty.Ace> aces = p.getAces(collectionPath);
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

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(node);
        transformer.transform(source, result);

        String xmlString = result.getWriter().toString();
        log.info(xmlString);
        return xmlString;
    }
}
