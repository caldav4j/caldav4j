package org.osaf.caldav4j.scheduling;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.XProperty;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.PostMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.scheduling.methods.CalDAV4JScheduleMethodFactory;
import org.osaf.caldav4j.scheduling.methods.SchedulePostMethod;
import org.osaf.caldav4j.util.ICalendarUtils;

public class SchedulePostMethodTest extends BaseTestCase {
	private CalDAV4JScheduleMethodFactory scheduleMethodFactory = new CalDAV4JScheduleMethodFactory();
	/**
	 * create a simple meeting POSTing to /Outbox
	 * and process a response
	 */
	public void _testSimpeMeetingInvite_Accept() {
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		Calendar invite = this
		.getCalendarResource("meeting_invitation.ics");
		Uid myUid = new Uid(new DateTime().toString());
		ICalendarUtils.addOrReplaceProperty(invite.getComponent(Component.VEVENT), myUid);
		Calendar refreshEvent = this
		.getCalendarResource("meeting_reply.ics");
		ICalendarUtils.addOrReplaceProperty(refreshEvent.getComponent(Component.VEVENT), myUid);

		SchedulePostMethod request = scheduleMethodFactory.createSchedulePostMethod();
		request.setPath("/pubcaldav/rtsvc");
		request.setHostConfiguration(hostConfig);
		request.setRequestBody(invite);
		try {
			http.executeMethod(request);
			if (request.getStatusCode() != 200) {
				System.out.println("error: " + request.getStatusText()); 
			}
			System.out.println(request.getResponseBodyAsString());
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//refresh invitation
		PostMethod refresh = methodFactory.createPostMethod();
		refresh.setPath("/pubcaldav/rtsvc");
		refresh.setHostConfiguration(hostConfig);
		refresh.setRequestBody(refreshEvent);
		refresh.setRequestHeader("Originator","mailto:r@r.it");
		refresh.setRequestHeader("Recipient","mailto:r@r.it");
		refresh.setRequestHeader("Recipient","mailto:robipolli@gmail.com");
		try {
			http.executeMethod(refresh);
			if (refresh.getStatusCode() != 200) {
				System.out.println("error: " + refresh.getStatusText()); 
			}
			System.out.println(refresh.getResponseBodyAsString());
		} catch (HttpException e) {
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
	public void _testRealTimeScheduling_SimpleMeetingInvitation() throws URISyntaxException {
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		Calendar invite = this
		.getCalendarResource("meeting_invitation.ics");


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
		
		SchedulePostMethod request = scheduleMethodFactory.createSchedulePostMethod();
		request.setPath("/pubcaldav/rtsvc");
		request.setHostConfiguration(hostConfig);
		request.setRequestBody(invite);
		try {
			http.executeMethod(request);
			if (request.getStatusCode() != 200) {
				System.out.println("error: " + request.getStatusText()); 
			}
			System.out.println(request.getResponseBodyAsString());
		} catch (HttpException e) {
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
	 * @throws ParseException 
	 */
	public void testRealTimeScheduling_SimpleMeetingReply() 
		throws URISyntaxException, HttpException, IOException, ParseException 
	{
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		Calendar invite = this
		.getCalendarResource("meeting_invitation.ics");
		
		VEvent event = (VEvent) invite.getComponent(Component.VEVENT);
		
		// r@r.it invites GMAIL
		ICalendarUtils.addOrReplaceProperty(event,new Organizer("mailto:r@r.it"));
		ParameterList needAction = new ParameterList();
		needAction.add(PartStat.NEEDS_ACTION);
		ParameterList accepted = new ParameterList();
		accepted.add(PartStat.ACCEPTED);
		
		event.getProperties().add(new Attendee(needAction, "mailto:robipolli@gmail.com"));
		ICalendarUtils.addOrReplaceProperty(event, 
				new Uid("SAMPLE_INVITE_UID"));

		// POST a meeting in /calendar inviting GMAIL
		System.out.println("REQUEST...");

		PutMethod request = methodFactory.createPutMethod();
		request.setPath(CALDAV_SERVER_WEBDAV_ROOT + "/calendar/" + event.getUid().getValue() + ".ics");
		request.setHostConfiguration(hostConfig);
		request.setRequestBody(invite);

		http.executeMethod(request);
		if (request.getStatusCode() != 200) {
			System.out.println("error: " + request.getStatusText()); 
		}
		System.out.println(request.getResponseBodyAsString());

		// update event like a REPLY
		event.getProperties().remove(new Attendee(needAction, "mailto:r@r.it"));
		event.getProperties().remove(new Attendee(needAction,"mailto:robipolli@gmail.com"));
		event.getProperties().add(new Attendee(accepted, "mailto:robipolli@gmail.com"));
		invite.getProperties().remove(Method.REQUEST);
		invite.getProperties().add(Method.REPLY);
		

		// POST to /rtsvc a REPLY from GMAIL
		System.out.println("REPLY...");
		PostMethod reply = methodFactory.createPostMethod();
		reply.setPath("/pubcaldav/rtsvc");
		reply.setHostConfiguration(hostConfig);
		reply.setRequestBody(invite);
		//reply.setRequestHeader("recipient", "mailto:robipolli@gmail.com");
		reply.setRequestHeader("originator", "mailto:r@r.it");
		reply.setRequestHeader("recipient", "mailto:r@r.it");

		http.executeMethod(reply);
		if (request.getStatusCode() != 200) {
			System.out.println("error: " + reply.getStatusText()); 
		}
		System.out.println(reply.getResponseBodyAsString());


	}

	/**
	 * POST a meeting in an user's inbox
	 * @throws URISyntaxException 
	 */
	public void _testSimpleMeetingInvitation() throws URISyntaxException {
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		Calendar invite = this
		.getCalendarResource("meeting_invitation.ics");


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
																.createSchedulePostMethod();
		request.setPath(CALDAV_SERVER_WEBDAV_ROOT + "/Outbox/");
		request.setHostConfiguration(hostConfig);
		request.setRequestBody(invite);
		try {
			http.executeMethod(request);
			if (request.getStatusCode() != 200) {
				System.out.println("error: " + request.getStatusText()); 
			}
			System.out.println(request.getResponseBodyAsString());
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
