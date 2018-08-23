package com.github.caldav4j.scheduling;

import java.io.IOException;
import java.net.URISyntaxException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.XProperty;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;
import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.HttpPostMethod;
import com.github.caldav4j.methods.HttpPutMethod;
import com.github.caldav4j.model.request.CalendarRequest;
import com.github.caldav4j.scheduling.methods.CalDAV4JScheduleMethodFactory;
import com.github.caldav4j.scheduling.methods.SchedulePostMethod;
import com.github.caldav4j.scheduling.util.ITipUtils;
import com.github.caldav4j.util.ICalendarUtils;

// TODO: work in progress
@Ignore
public class SchedulePostMethodTest extends BaseTestCase {


	private CalDAV4JScheduleMethodFactory scheduleMethodFactory = new CalDAV4JScheduleMethodFactory();

	HttpClient http = createHttpClient();
	HttpHost hostConfig = createHostConfiguration();
	
	public static final String BEDEWORK_RTSVC_URL = "/pubcaldav/rtsvc";
	/**
	 * create a simple meeting POSTing to /Outbox
	 * and process a response
	 */
    @Test
	public void testSimpeMeetingInvite_Accept() {


		Calendar invite = this
		.getCalendarResource("scheduling/meeting_invitation.ics");
		Uid myUid = new Uid(new DateTime().toString());
		ICalendarUtils.addOrReplaceProperty(invite.getComponent(Component.VEVENT), myUid);
		Calendar refreshEvent = this
		.getCalendarResource("scheduling/meeting_reply.ics");
		ICalendarUtils.addOrReplaceProperty(refreshEvent.getComponent(Component.VEVENT), myUid);

	    CalendarRequest cr = new CalendarRequest(invite);
		SchedulePostMethod request = scheduleMethodFactory.createSchedulePostMethod(BEDEWORK_RTSVC_URL, cr);
		try {
			HttpResponse response = http.execute(hostConfig, request);
			if (response.getStatusLine().getStatusCode() != 200) {
				log.info("error: " + response.getStatusLine().getReasonPhrase());
			}
			log.info(EntityUtils.toString(response.getEntity()));
		} catch (org.apache.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//refresh invitation
	    cr.setCalendar(refreshEvent);
		HttpPostMethod refresh = fixture.getMethodFactory().createPostMethod(BEDEWORK_RTSVC_URL, cr);
		refresh.addHeader("Originator","mailto:r@r.it");
		refresh.addHeader("Recipient","mailto:r@r.it");
		refresh.addHeader("Recipient","mailto:robipolli@gmail.com");
		try {
			HttpResponse response = http.execute(hostConfig, refresh);
			if (response.getStatusLine().getStatusCode() != 200) {
				log.info("error: " + response.getStatusLine().getReasonPhrase());
			}
			log.info(EntityUtils.toString(response.getEntity()));
		} catch (org.apache.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * POST a meeting in r@r.it Outbox using this user to provide invitation to g@r.it
	 * @throws URISyntaxException 
	 */
    @Test
	public void testRealTimeScheduling_SimpleMeetingInvitation() throws URISyntaxException {


		Calendar invite = this
		.getCalendarResource("scheduling/meeting_invitation.ics");


		// replace fields from template
		VEvent event = (VEvent) invite.getComponent(Component.VEVENT);
		ICalendarUtils.addOrReplaceProperty(event,
				new Organizer("mailto:rpolli@babel.it")
		);
		ParameterList plist = new ParameterList();
		plist.add(new PartStat("NEED-ACTION"));

		ICalendarUtils.addOrReplaceProperty(event, 
				new Attendee(plist, "mailto:g@r.it"));
		event.getProperties().add(new Attendee(plist, "mailto:roberto.polli@babel.it"));
		event.getProperties().add(new Attendee(plist, "mailto:robipolli@gmail.com"));


		ICalendarUtils.addOrReplaceProperty(event, 
				new Uid(new DateTime().toString()));

		CalendarRequest cr = new CalendarRequest(invite);
		SchedulePostMethod request = scheduleMethodFactory.createSchedulePostMethod(BEDEWORK_RTSVC_URL, cr);

		try {
			HttpResponse response = http.execute(hostConfig, request);
			if (response.getStatusLine().getStatusCode() != 200) {
				log.info("error: " + response.getStatusLine().getReasonPhrase());
			}
			log.info(EntityUtils.toString(response.getEntity()));
		} catch (org.apache.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * process a REPLY retrieved from email
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws HttpException
	 */
    @Test
	public void testRealTimeScheduling_SimpleMeetingReply() 
	throws URISyntaxException, HttpException, IOException, CalDAV4JException
	{
		Calendar invite = BaseTestCase
		.getCalendarResource("scheduling/meeting_invitation.ics");

		VEvent event = (VEvent) invite.getComponent(Component.VEVENT);


		// r@r.it invites GMAIL
		ICalendarUtils.addOrReplaceProperty(event,new Organizer("mailto:r@r.it"));


		for (int j=0; j<10; j++) {
			Uid myUid = new Uid(new DateTime().toString() + j);
			ICalendarUtils.addOrReplaceProperty(event, myUid);
			// Create meeting in /calendar 
			log.info("PUT...");

			CalendarRequest cr = new CalendarRequest(invite);
			HttpPutMethod request = fixture.getMethodFactory().createPutMethod(caldavCredential.home + "/calendar/" + event.getUid().getValue() + ".ics", cr);

			HttpResponse response = http.execute(hostConfig, request);
			if (response.getStatusLine().getStatusCode() != 200) {
				log.info("error: " + response.getStatusLine().getReasonPhrase());
			}
			log.info(EntityUtils.toString(response.getEntity()));

			// update event like a REPLY from robipolli@gmail.com
			Calendar responseCalendar = ITipUtils.ReplyInvitation(invite, new Attendee("mailto:robipolli@gmail.com"), PartStat.ACCEPTED);

			// POST to /rtsvc a REPLY from GMAIL
			log.info("REPLY...#" + j);
			HttpPostMethod reply = fixture.getMethodFactory().createPostMethod(BEDEWORK_RTSVC_URL, new CalendarRequest(responseCalendar));
			reply.addHeader("originator", "mailto:r@r.it");
			reply.addHeader("recipient", "mailto:r@r.it");

			response = http.execute(hostConfig, reply);
			if (response.getStatusLine().getStatusCode() != 200) {
				log.info("error: " + response.getStatusLine().getReasonPhrase());
			}
			log.info(EntityUtils.toString(response.getEntity()));
		}

	}

	/**
	 * POST a meeting in an user's inbox
	 * @throws URISyntaxException 
	 */
    @Test
	public void testSimpleMeetingInvitation() throws URISyntaxException {
		HttpClient http = createHttpClient();
		HttpHost hostConfig = createHostConfiguration();

		Calendar invite = this
		.getCalendarResource("scheduling/meeting_invitation.ics");


		VEvent event = (VEvent) invite.getComponent(Component.VEVENT);
		ICalendarUtils.addOrReplaceProperty(
				event,
				new Organizer("mailto:rpolli@babel.it")
		);
		ParameterList plist = new ParameterList();
		plist.add(new PartStat("NEED-ACTION"));

		ICalendarUtils.addOrReplaceProperty(event, 
				new Attendee(plist, "mailto:r@r.it"));
		ICalendarUtils.addOrReplaceProperty(event,
				new XProperty("X-BEDEWORK-SUBMITTEDBY", "r@r.it"));
		ICalendarUtils.addOrReplaceProperty(event, 
				new Uid(new DateTime().toString()));

		SchedulePostMethod request = scheduleMethodFactory
		.createSchedulePostMethod(caldavCredential.home + "/Outbox/", new CalendarRequest(invite));
		try {
			HttpResponse response = http.execute(hostConfig, request);
			if (response.getStatusLine().getStatusCode() != 200) {
				log.info("error: " + response.getStatusLine().getReasonPhrase());
			}
			log.info(EntityUtils.toString(response.getEntity()));
		} catch (org.apache.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
