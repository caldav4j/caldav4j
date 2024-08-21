/*
 * Copyright 2011 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Mark Hobson, Roberto Polli
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

package com.github.caldav4j.functional.support;

import com.github.caldav4j.CalDAVCollection;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.TestConstants;
import com.github.caldav4j.cache.EhCacheResourceCache;
import com.github.caldav4j.exceptions.CacheException;
import net.sf.ehcache.CacheManager;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaldavFixtureHarness implements TestConstants {

    protected static final Logger log = LoggerFactory.getLogger(CaldavFixtureHarness.class);

    public static CalDAVCollection createCollectionFromFixture(CalDavFixture fixture) {
        HttpHost conf = new HttpHost(fixture.getHostConfig());

        return new CalDAVCollection(
                fixture.getCollectionPath(),
                conf,
                fixture.getMethodFactory(),
                CalDAVConstants.PROC_ID_DEFAULT);
    }

    /**
     * @param fixture
     */
    public static void provisionGoogleEvents(CalDavFixture fixture) {
        provisionEvents(
                fixture,
                new String[] {
                    ICS_GOOGLE_DAILY_NY_5PM_PATH,
                    ICS_GOOGLE_ALL_DAY_JAN1_PATH,
                    ICS_GOOGLE_NORMAL_PACIFIC_1PM_PATH,
                    ICS_GOOGLE_SINGLE_EVENT_PATH
                });
    }

    public static void provisionSimpleEvents(CalDavFixture fixture) {
        provisionEvents(
                fixture,
                new String[] {
                    ICS_DAILY_NY_5PM_PATH,
                    ICS_ALL_DAY_JAN1_PATH,
                    ICS_NORMAL_PACIFIC_1PM_PATH,
                    ICS_SINGLE_EVENT_PATH,
                    ICS_FLOATING_JAN2_7PM_PATH
                });
    }

    private static void provisionEvents(CalDavFixture fixture, String[] events) {
        for (String eventPath : events) {
            fixture.caldavPut(eventPath);
        }
    }

    /** */
    public static EhCacheResourceCache createSimpleCache() throws CacheException {
        // initialize cache
        CacheManager cacheManager = CacheManager.create();
        EhCacheResourceCache myCache = EhCacheResourceCache.createSimpleCache();
        return myCache;
    }

    /** */
    public static void removeSimpleCache() {
        CacheManager cacheManager = CacheManager.create();
        cacheManager.removeCache(UID_TO_HREF_CACHE);
        cacheManager.removeCache(HREF_TO_RESOURCE_CACHE);
        cacheManager.shutdown();
    }
}
