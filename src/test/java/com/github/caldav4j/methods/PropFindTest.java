/*
 * Copyright © 2018 Ankush Mishra, Roberto Polli
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

import static org.junit.Assert.assertEquals;

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.model.request.CalDAVPrivilege;
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
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.security.Principal;
import org.apache.jackrabbit.webdav.security.Privilege;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Ignore // to be run under functional
public class PropFindTest extends BaseTestCase {

    private static final Logger log = LoggerFactory.getLogger(PropFindTest.class);

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
    @Ignore
    public void testGetAcl()
            throws CalDAV4JException,
                    IOException,
                    TransformerException,
                    ParserConfigurationException {
        // TODO here we should use fixture.getHttpClient()
        String path = fixture.getCollectionPath();
        HttpClient http = fixture.getHttpClient();
        ;
        HttpHost hostConfig = fixture.getHostConfig();

        DavPropertyNameSet set = new DavPropertyNameSet();
        set.add(CalDAVConstants.DNAME_ACL);

        HttpPropFindMethod propfind = new HttpPropFindMethod(path, set, CalDAVConstants.DEPTH_0);

        try {
            HttpResponse response = http.execute(hostConfig, propfind);

            AclProperty aclProperty = propfind.getAcl(response, path);
            /*
             * response
             *   href
             *   propstat
             *      prop
             *         acl
             *            ace
             *               principal
             *                  property
             *                     owner
             *               grant
             *                  privilege
             *               inherited
             *                  href
             *            ace
             *            ,,,
             */

            print_Xml(aclProperty);
            List<AclProperty.Ace> aces = propfind.getAces(response, path);
            print_Xml(aces.get(0));

            log.info("There are aces # " + aces.size());
            for (AclProperty.Ace ace : aces) {
                print_Xml(ace.getPrincipal());
                log.info(
                        ace.isGrant()
                                ? "Grant Ace"
                                : ace.isDeny() ? "Deny Ace" : "Not Grant or Deny");
            } // aces

            for (AclProperty.Ace ace : aces) {
                log.info("ace:");
                print_Xml(ace);
                log.info("inherited by: " + ace.getInheritedHref() + ";" + "principal is: ");
                print_Xml(ace.getPrincipal());

                Privilege[] privs = ace.getPrivileges();

                for (Privilege priv : privs)
                    log.info(
                            "further elements: "
                                    + "ns:"
                                    + priv.getNamespace().toString()
                                    + ":"
                                    + priv.getName()
                                    + "; ");
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        Privilege[] privileges = {Privilege.PRIVILEGE_READ};
        AclProperty.Ace test =
                AclProperty.createGrantAce(
                        Principal.getPropertyPrincipal(
                                DavPropertyName.create(DavPropertyName.XML_OWNER)),
                        privileges,
                        false,
                        false,
                        null);
        print_Xml(test);
    }

    @Test
    @Ignore
    public void testGetAcl_1() throws IOException {
        String path = fixture.getCollectionPath();
        HttpClient http = createHttpClient();
        HttpHost hostConfig = createHostConfiguration();

        DavPropertyNameSet set = new DavPropertyNameSet();
        set.add(CalDAVConstants.DNAME_ACL);
        set.add(CalDAVConstants.DNAME_DISPLAYNAME);
        set.add(CalDAVConstants.DNAME_CALENDAR_DESCRIPTION);

        HttpPropFindMethod propfind = new HttpPropFindMethod(path, set, CalDAVConstants.DEPTH_0);

        try {
            HttpResponse response = http.execute(hostConfig, propfind);

            AclProperty responses = propfind.getAcl(response, path);

            log.info("new Property element");
            List<AclProperty.Ace> aces = propfind.getAces(response, path);
            log.info("There are aces # " + aces.size());
            print_ListAce(aces);

            Privilege[] privileges = {Privilege.PRIVILEGE_READ};
            AclProperty.Ace test =
                    AclProperty.createGrantAce(
                            Principal.getHrefPrincipal(path), privileges, false, false, null);
            print_Xml(test);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws CalDAV4JException
     */
    // TODO: this test will work only on bedework which has a set of permission set
    @Ignore
    @Test
    public void testNewPropfind()
            throws CalDAV4JException, IOException, ParserConfigurationException, DavException {
        log.info("New Propfind");
        HttpClient http = createHttpClient();
        HttpHost hostConfig = createHostConfiguration();

        DavPropertyNameSet set = new DavPropertyNameSet();
        set.add(CalDAVConstants.DNAME_ACL);
        set.add(CalDAVConstants.DNAME_DISPLAYNAME);
        set.add(CalDAVConstants.DNAME_CALENDAR_DESCRIPTION);

        HttpPropFindMethod propfind =
                new HttpPropFindMethod(fixture.getCollectionPath(), set, CalDAVConstants.DEPTH_0);

        try {
            HttpResponse response = http.execute(hostConfig, propfind);

            // check that Calendar-description and DisplayName matches
            log.debug(
                    "DisplayName: "
                            + propfind.getDisplayName(response, fixture.getCollectionPath()));
            assertEquals(
                    caldavCredential.collection.replaceAll("/$", ""),
                    propfind.getDisplayName(response, fixture.getCollectionPath())
                            .replaceAll("/$", ""));

            log.debug(
                    "CalendarDescription: "
                            + propfind.getCalendarDescription(
                                    response, fixture.getCollectionPath()));
            assertEquals(
                    CALENDAR_DESCRIPTION,
                    propfind.getCalendarDescription(response, fixture.getCollectionPath()));

            // check that ACLs matches
            List<AclProperty.Ace> aces = propfind.getAces(response, fixture.getCollectionPath());
            log.info("There are aces # " + aces.size());

            for (AclProperty.Ace ace : aces) {
                assertEquals("/user", ace.getInheritedHref());
                /*
                            switch (k) {
                case :
                	Principal pdav = AceUtils.getDavPrincipal(ace);
                	pdav.isOwner();
                	assertEquals("property", ace.getPrincipal());
                	assertEquals("owner", ace.getProperty().getLocalName());
                	Privilege p = (Privilege) aces[k].enumeratePrivileges().nextElement();
                	assertEquals("all", p.getName());
                	break;

                case 1:
                	assertEquals(CalDAVConstants.DAV_PRINCIPAL_AUTHENTICATED, ace.getPrincipal());
                	p = (Privilege) ace.enumeratePrivileges().nextElement();
                	assertTrue( p.getName().contains(CalDAVConstants.CALDAV_PRIVILEGE_READ_FREE_BUSY));
                	break;
                default:
                	break;
                }*/
                print_Xml(ace);
            }

        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    /** FIXME This test won't check for the result, just run some ACL methods */
    @Test
    public void testAclMethod() throws IOException {
        log.info("New Propfind");
        HttpClient http = fixture.getHttpClient();
        HttpHost hostConfig = fixture.getHostConfig();

        Privilege[] privileges = {
            CalDAVPrivilege.SCHEDULE_DELIVER, Privilege.PRIVILEGE_READ, Privilege.PRIVILEGE_WRITE
        };
        AclProperty.Ace ace =
                AclProperty.createGrantAce(
                        Principal.getPropertyPrincipal(
                                DavPropertyName.create(DavPropertyName.XML_OWNER)),
                        privileges,
                        false,
                        false,
                        null);
        AclProperty aclProperty = new AclProperty(new AclProperty.Ace[] {ace});
        HttpAclMethod method = new HttpAclMethod(fixture.getCollectionPath(), aclProperty);

        try {
            http.execute(hostConfig, method);

            DavPropertyNameSet set = new DavPropertyNameSet();
            set.add(CalDAVConstants.DNAME_ACL);
            set.add(CalDAVConstants.DNAME_DISPLAYNAME);
            set.add(CalDAVConstants.DNAME_CALENDAR_DESCRIPTION);
            // verify output
            HttpPropFindMethod propfind =
                    new HttpPropFindMethod(
                            fixture.getCollectionPath(), set, CalDAVConstants.DEPTH_0);

            HttpResponse response = http.execute(hostConfig, propfind);

            log.info("post setacl returns: ");
            print_Xml(propfind.getResponseBodyAsMultiStatus(response));
            // TODO check returned ACIS
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    // private methods
    //
    private void print_ListAce(List<org.apache.jackrabbit.webdav.security.AclProperty.Ace> aces)
            throws ParserConfigurationException, TransformerException {
        for (org.apache.jackrabbit.webdav.security.AclProperty.Ace ace : aces) {
            print_Xml(ace);
        }
    }

    private void print_Xml(XmlSerializable ace)
            throws TransformerException, ParserConfigurationException {
        Document document = DomUtil.createDocument();
        ElementoString(ace.toXml(document));
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
