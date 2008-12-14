package org.osaf.caldav4j.scheduling.util;

import java.awt.image.ReplicateScaleFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.CalDAV4JException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Method;
import junit.framework.TestCase;

public class ITipUtilsTest extends BaseTestCase {
	// load a sample meeting request
	Calendar inviteComplexWithTimezone = this
	.getCalendarResource("iCal-20081127-092400.ics"); //meeting_invitation.ics");
	/**
	 * given a meeting REQUEST, create the given REPLY
	 * @throws URISyntaxException 
	 *  
	 */
	public void testRequestReplyAccept() throws URISyntaxException {
		try {
			Calendar reply = null;
			// add a set of attendees
			Attendee mySelf = new Attendee(new URI("mailto:rpolli@babel.it"));
			// set different partstats
			// process it as ACCEPTED

			reply =  ITipUtils.ManageInvitation(inviteComplexWithTimezone, mySelf, Method.REPLY, PartStat.ACCEPTED);

			// check if reply is ok, other attendees stripped off, redundant data removed...
			System.out.println("REPLY: " + reply );

			// check if missing attendees cause exception
			mySelf = new Attendee(new URI("mailto:NOBODY"));
			try {
				reply =  ITipUtils.ManageInvitation(inviteComplexWithTimezone, mySelf, Method.REPLY, PartStat.ACCEPTED);
			} catch (CalDAV4JException e) {
				// TODO Auto-generated catch block
				assertTrue( e.getCause().equals(new Throwable("Missing attendee")));
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			assertTrue(false);
		}

	}

	public void testRequestReplyDeclined() throws URISyntaxException {
		// load a sample meeting request
		Calendar reply = null;

		// add a set of attendees
		// set different partstats
		// process it as DECLINED
		Attendee mySelf = new Attendee(new URI("mailto:rpolli@babel.it"));
		// set different partstats
		// process it as ACCEPTED
		try {
			reply =  ITipUtils.ManageInvitation(inviteComplexWithTimezone, mySelf, Method.REPLY, PartStat.DECLINED);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// check if reply is ok, other attendees stripped off, redundant data removed...
		System.out.println("REPLY: DECLINED: " + reply );
		// check if reply is ok, other attendees stripped off, redundant data removed...
	}

	// TODO to be implemented
	public void _testRequestReplyDelegated() {
		// load a sample meeting request
		// add a set of attendees
		// set different partstats
		// process it as DECLINED
		// check if reply is ok, other attendees stripped off, redundant data removed...
	}
}
