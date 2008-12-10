/**
 * This class is an helper for managing events following 
 * the iTIP protocol RFC2446 http://tools.ietf.org/html/rfc2446
 * (c) Roberto Polli rpolli@babel.it
 */
package org.osaf.caldav4j.scheduling.util;


import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.TimeZone;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.util.ICalendarUtils;

public class ITipUtils {
	private static final Log log = LogFactory.getLog(ITipUtils.class);    
	private static java.util.TimeZone J_TZ_GMT = TimeZone.getTimeZone("GMT");

	/**
	 * Manage an invitation to a meeting (VCOMPONENT), setting
	 *  METHOD:REPLY
	 *  ATTENDEE:PARTSTAT...
	 * 
	 * @param invite
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws CalDAV4JException 
	 */
	public static Calendar ManageInvitation(Calendar invite, Attendee mySelf, String action) 
	throws ParseException, IOException, URISyntaxException, CalDAV4JException 
	{
		Calendar reply = new Calendar(invite);
		//  if it's not a REQUEST, throw Exception
		if (reply.getProperty(Property.METHOD) != null ) {
			if (checkMethod(Method.REQUEST,reply) ) {
				if (Method.REPLY.getValue().equals(action)) {
					// use REPLY to event
					reply.getProperties().remove(Method.REQUEST);
					reply.getProperties().add(Method.REPLY);

					// Except if I'm not invited
					if (processAttendees(reply, mySelf, action)<1)
						throw new CalDAV4JException("Attendee " + mySelf + "not invited to event");



					// set the right PartStat


				}
			}
		} 

		// if I'm not an ATTENDEE throw Exception
		// if action is not valid, throw Exception

	}

	// check if Calendar contains the given method, in a faster way (string comparison)
	private static boolean checkMethod(Method m, Calendar c) {
		try {
			return m.getValue().equals(c.getProperty(Property.METHOD).getValue());
		} catch (NullPointerException e) {
			return false;
		}
	}

	// remove attendees, returning number of attendees matching user
	private static int processAttendees(Calendar c, Attendee user, String action) {
		PropertyList attendees = null;
		for (Object o : c.getComponents()) {
			if (! (o instanceof VTimeZone)) {

				CalendarComponent cc = (CalendarComponent) o;
				attendees = cc.getProperties(Property.ATTENDEE);

				//remove attendees unmatching user
				for (int i=0; i<attendees.size(); i++) {
					Attendee a = (Attendee) attendees.get(i);
					if (! a.getValue().equals(user.getValue())) {
						attendees.remove(i);
					}  else {
						a.getParameters().remove(a.getParameter(Parameter.PARTSTAT));
						a.getParameters().add(new PartStat(action));
					}
				} // attendees

			}
		} // for
		return attendees.size();
	}
}
