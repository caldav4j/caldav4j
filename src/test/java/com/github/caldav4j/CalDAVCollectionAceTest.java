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

import com.github.caldav4j.cache.EhCacheResourceCache;
import com.github.caldav4j.functional.support.CaldavFixtureHarness;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** TODO re-implement test using deprecated methods using current methods */
public class CalDAVCollectionAceTest extends BaseTestCase {
    public CalDAVCollectionAceTest() {
        super();
    }

    protected static final Logger log = LoggerFactory.getLogger(CalDAVCollectionAceTest.class);

    // cache
    private static final String HREF_TO_RESOURCE_CACHE = "hrefToResourceCache";
    private static final String UID_TO_HREF_CACHE = "uidToHrefCache";
    private EhCacheResourceCache myCache = null;

    public static final Integer TEST_TIMEOUT = 3600;
    public static final boolean TEST_READ = true;
    public static final boolean TEST_WRITE = true;
    public static final Integer TEST_VISITS = CalDAVConstants.INFINITY;

    public static final String TEST_TIMEOUT_UNITS = "Second";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        CaldavFixtureHarness.provisionGoogleEvents(fixture);

        // TODO: Not Entirely sure, why uncachedCollection is required
        uncachedCollection = CaldavFixtureHarness.createCollectionFromFixture(fixture);
        uncachedCollection.disableSimpleCache();
    }

    @After
    public void tearDown() throws Exception {
        CaldavFixtureHarness.removeSimpleCache();

        fixture.tearDown();
    }

    /*
     * make a OPTIONS  requesto to caldav server
     * @throws Exception
     *
    @Test
    public void testGetOptions() throws Exception {
    	List<Header> headerList = uncachedCollection.getOptions(fixture.getHttpClient());

    	for (Header h : headerList) {
    		log.info(h.getName() + ":" + h.getValue());
    	}

    	if (uncachedCollection.allows(fixture.getHttpClient(), "MKCOL", headerList)) {
    		log.info("MKCOL exists");
    	}
    	if (uncachedCollection.allows(fixture.getHttpClient(), "REPORT", headerList)) {
    		log.info("REPORT exists");
    	}
    	if (uncachedCollection.allows(fixture.getHttpClient(), "NOOP", headerList)) {
    		log.info("NOOP exists");
    	}
    }*/

    //
    // private
    //

}
