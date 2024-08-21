/*
 * Copyright © 2018 Mark Hobson, Roberto Polli
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

package com.github.caldav4j.scheduling.util;

import static org.junit.Assert.assertTrue;

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.util.ICalendarUtils;
import java.net.URI;
import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.immutable.ImmutableMethod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// test resource missing and all test methods are currently ignored
@Ignore
public class ITipUtilsTest extends BaseTestCase {

    private static final Logger log = LoggerFactory.getLogger(ITipUtilsTest.class);

    // load a sample meeting request
    Calendar inviteComplexWithTimezone = getCalendarResource("scheduling/meeting_invitation_1.ics");

    Attendee mySelf = null;
    Attendee nobody = null;

    @Before
    public void setUp() {
        try {
            super.setUp();
            mySelf = new Attendee(new URI("mailto:robipolli@gmail.com"));
            nobody = new Attendee(new URI("mailto:NOBODY"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * given a meeting REQUEST, create the given REPLY
     *
     * @throws URISyntaxException
     */
    @Test
    @Ignore
    public void testRequestReplyAccept() throws URISyntaxException {
        try {
            Calendar reply = null;
            // add a set of attendees
            // process it as ACCEPTED

            reply =
                    ITipUtils.ManageInvitation(
                            inviteComplexWithTimezone,
                            mySelf,
                            ImmutableMethod.REPLY,
                            PartStat.ACCEPTED);

            // check if reply is ok, other attendees stripped off, redundant data removed...
            log.trace("REPLY: " + reply);

            // check if missing attendees cause exception
            try {
                reply =
                        ITipUtils.ManageInvitation(
                                inviteComplexWithTimezone,
                                nobody,
                                ImmutableMethod.REPLY,
                                PartStat.ACCEPTED);
            } catch (CalDAV4JException e) {
                // TODO Auto-generated catch block
                assertTrue(e.getCause().equals(new Throwable("Missing attendee")));
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            assertTrue(false);
            log.error("Missing ATTENDEEs should cause exceptions");
        }
    }

    @Test
    @Ignore
    public void testRequestReplyDeclined() throws URISyntaxException {
        // load a sample meeting request
        Calendar reply = null;

        // process it as DECLINED

        try {
            reply =
                    ITipUtils.ManageInvitation(
                            inviteComplexWithTimezone,
                            mySelf,
                            ImmutableMethod.REPLY,
                            PartStat.DECLINED);

            // check if reply is ok, other attendees stripped off, redundant data removed...
            if (ICalendarUtils.getFirstComponent(reply).getProperties(Property.ATTENDEE).size()
                    > 1) {
                assertTrue("Too many attendees in reply", false);
            } else if (!reply.getProperty(Property.METHOD)
                    .get()
                    .getValue()
                    .equals(ImmutableMethod.REPLY.getValue())) {
                assertTrue("bad METHOD in REPLY" + reply.getProperty(Property.METHOD), false);
            }

            log.trace("REPLY: DECLINED: " + reply);
            // check if reply is ok, other attendees stripped off, redundant data removed...

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            assertTrue(false);
        }
    }

    // TODO to be implemented
    @Test
    @Ignore
    public void testRequestReplyDelegated() {
        // load a sample meeting request
        // add a set of attendees
        // set different partstats
        // process it as DECLINED
        // check if reply is ok, other attendees stripped off, redundant data removed...
    }
}
