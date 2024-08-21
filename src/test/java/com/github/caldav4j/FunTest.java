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

package com.github.caldav4j;

import com.github.caldav4j.functional.support.CaldavFixtureHarness;
import com.github.caldav4j.methods.HttpPropFindMethod;
import com.github.caldav4j.util.CalDAVStatus;
import java.time.LocalDateTime;
import java.util.List;
import net.fortuna.ical4j.model.Recur;
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
    public void testFun() throws Exception {
        HttpClient http = createHttpClient();
        HttpHost hostConfig = createHostConfiguration();

        DavPropertyNameSet set = new DavPropertyNameSet();
        DavPropertyName resourcetype = DavPropertyName.create("resourcetype");
        set.add(resourcetype);
        HttpPropFindMethod propFindMethod =
                new HttpPropFindMethod(
                        fixture.getCollectionPath(), set, CalDAVConstants.DEPTH_INFINITY);

        HttpResponse httpResponse = http.execute(hostConfig, propFindMethod);
        MultiStatusResponse[] e =
                propFindMethod.getResponseBodyAsMultiStatus(httpResponse).getResponses();

        for (MultiStatusResponse response : e) {
            DavPropertySet properties = response.getProperties(CalDAVStatus.SC_OK);
            log.info("HREF: " + response.getHref());
            for (DavProperty property : properties) {
                String nodeName = property.getName().toString();
                log.info("nodename: " + nodeName + "\n" + "value: " + property.getValue());
            }
        }
    }

    public static void main(String args[]) {
        try {
            Recur<LocalDateTime> recur = new Recur<>("FREQ=HOURLY");
            LocalDateTime startDate = LocalDateTime.parse("20060101T010000Z");
            LocalDateTime endDate = LocalDateTime.parse("20060105T050000Z");
            LocalDateTime baseDate = LocalDateTime.parse("20050101T033300");
            List<LocalDateTime> dateList = recur.getDates(baseDate, startDate, endDate);
            for (LocalDateTime d : dateList) {
                log.info(d.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
