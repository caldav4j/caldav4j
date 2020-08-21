/**
 * This class is an helper for managing events following the iTIP protocol RFC2446
 * http://tools.ietf.org/html/rfc2446 (c) Roberto Polli rpolli@babel.it
 */
package com.github.caldav4j.scheduling.util;

import com.github.caldav4j.exceptions.CalDAV4JException;
import java.util.TimeZone;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ITipUtils {
    private static final Logger log = LoggerFactory.getLogger(ITipUtils.class);
    private static java.util.TimeZone J_TZ_GMT = TimeZone.getTimeZone("GMT");

    public static Calendar ReplyInvitation(Calendar invite, Attendee mySelf, PartStat replyPartStat)
            throws CalDAV4JException {
        return ManageInvitation(invite, mySelf, Method.REPLY, replyPartStat);
    }

    /**
     * Manage an invitation to a meeting (VCOMPONENT), setting METHOD:REPLY ATTENDEE:PARTSTAT...
     *
     * @param invite Calendar containing the invitation
     * @param mySelf Attendee User to check
     * @param responseMethod Type of Response, could be {@link Method#REPLY} or {@link
     *     Method#REQUEST}
     * @param responsePartStat The PartStat object for reponse
     * @return a Calendar object
     * @throws CalDAV4JException on error
     */
    public static Calendar ManageInvitation(
            Calendar invite, Attendee mySelf, Method responseMethod, PartStat responsePartStat)
            throws CalDAV4JException {
        Calendar reply;
        try {
            reply = new Calendar(invite);

            //  if it's not a REQUEST, throw Exception
            if (reply.getProperty(Property.METHOD) != null) {
                if (compareMethod(Method.REQUEST, reply.getMethod())) {

                    // if REPLY
                    if (compareMethod(Method.REPLY, responseMethod)) {
                        // use REPLY to event
                        reply.getProperties().remove(Method.REQUEST);
                        reply.getProperties().add(Method.REPLY);

                        processAttendees(reply, mySelf, responsePartStat);
                    }
                }
            }
            return reply;
        } catch (Exception e) {
            log.warn("Calendar " + invite + "malformed");
            throw new CalDAV4JException(
                    "Calendar " + invite + "malformed", new Throwable("Bad calendar REQUEST"));
        }
    }

    /**
     * Check if Calendar contains the given method, in a faster way (string comparison)
     *
     * @param m Method to compare
     * @param n Method to compare
     * @return true if same else false
     */
    private static boolean compareMethod(Method m, Method n) {
        try {
            return m.getValue().equals(n.getValue());
        } catch (NullPointerException e) {
            return false;
        }
    }

    // remove attendees, returning number of attendees matching user

    /**
     * Remove attendees, returning number of attendees matching user
     *
     * @param c Calendar containing attendees
     * @param user Attendee to match
     * @param action Action to replace for attendee
     * @throws CalDAV4JException on error
     */
    private static void processAttendees(Calendar c, Attendee user, PartStat action)
            throws CalDAV4JException {
        int numAttendees = 0;
        for (Object o : c.getComponents()) {
            if (!(o instanceof VTimeZone)) {

                CalendarComponent cc = (CalendarComponent) o;
                PropertyList attendees = cc.getProperties(Property.ATTENDEE);
                cc.getProperties().removeAll(attendees);

                // remove attendees unmatching user
                while (attendees.size() > numAttendees) {
                    Attendee a = (Attendee) attendees.get(numAttendees);
                    if (!a.getValue().equals(user.getValue())) {
                        attendees.remove(numAttendees);
                    } else {
                        a.getParameters().remove(a.getParameter(Parameter.PARTSTAT));
                        a.getParameters().add(action);
                        numAttendees++;
                    }
                } // attendees

                cc.getProperties().addAll(attendees);
            }
        } // for

        if (numAttendees < 1)
            throw new CalDAV4JException(
                    "Attendee " + user + "not invited to event", new Throwable("Missing attendee"));
    }
}
