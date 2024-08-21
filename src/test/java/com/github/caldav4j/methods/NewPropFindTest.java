/*
 * Copyright © 2018 Ankush Mishra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.caldav4j.methods;

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.CalDAV4JException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ankush Mishra
 */
public class NewPropFindTest extends BaseTestCase {
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
        HttpClient http = fixture.getHttpClient();
        ;
        HttpHost hostConfig = fixture.getHostConfig();
        DavPropertyNameSet props = new DavPropertyNameSet();
        props.add(DavPropertyName.DISPLAYNAME);

        HttpPropFindMethod p =
                new HttpPropFindMethod(fixture.getCollectionPath(), props, DavConstants.DEPTH_0);

        HttpResponse response = http.execute(hostConfig, p);
        log.info(response.getStatusLine().toString());
        MultiStatusResponse[] responses = p.getResponseBodyAsMultiStatus(response).getResponses();
        for (MultiStatusResponse r : responses) {
            log.info(r.getHref());
        }
        log.info(p.getDisplayName(response, fixture.getCollectionPath()));
    }

    @Test
    public void testGetAcl()
            throws IOException, DavException, ParserConfigurationException, CalDAV4JException {
        String collectionPath = fixture.getCollectionPath();
        HttpClient http = fixture.getHttpClient();
        HttpHost hostConfig = fixture.getHostConfig();
        DavPropertyNameSet props = new DavPropertyNameSet();
        // Propfind for only DAV:acl
        props.add(CalDAVConstants.DAV_ACL, CalDAVConstants.NAMESPACE_WEBDAV);

        HttpPropFindMethod p = new HttpPropFindMethod(collectionPath, props, DavConstants.DEPTH_0);

        HttpResponse response = http.execute(hostConfig, p);
        log.info(response.getStatusLine().toString());

        // Get all the processed aces and print them to log.
        List<AclProperty.Ace> aces = p.getAces(response, collectionPath);
        try {
            print_Ace(aces);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void print_Ace(List<AclProperty.Ace> aces)
            throws ParserConfigurationException, TransformerException {
        Document document = DomUtil.createDocument();
        for (AclProperty.Ace ace : aces) {
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
