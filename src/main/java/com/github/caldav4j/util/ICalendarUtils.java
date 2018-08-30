/*
 * Copyright 2005 Open Source Applications Foundation
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

package com.github.caldav4j.util;

import com.github.caldav4j.exceptions.CalDAV4JException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.Uid;
import com.github.caldav4j.CalDAVResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Calendar Utility functions
 */
public class ICalendarUtils {
	private static final Logger log = LoggerFactory.getLogger(ICalendarUtils.class);

	private static final java.util.TimeZone J_TZ_GMT = TimeZone.getTimeZone("GMT");

	/**
	 * Creates an iCal4J DateTime. The values for year, month, day, hour,
	 * minutes, seconds and milliseconds should be set way that you specify them
	 * in a java.util.Calendar - which means zero indexed months for example
	 * (eg. January is '0').
	 *
	 * Note that the TimeZone is not a java.util.TimeZone but an iCal4JTimeZone
	 *
	 * @param year Year
	 * @param month Month
	 * @param day Day
	 * @param hour Hour
	 * @param minutes Minutes
	 * @param seconds Seconds
	 * @param milliseconds MilliSeconds
	 * @param tz TimeZone
	 * @param utc UTC Enabled
	 * @return {@link DateTime} object
	 */
	public static DateTime createDateTime(int year, int month, int day,
	                                      int hour, int minutes, int seconds, int milliseconds, TimeZone tz,
	                                      boolean utc) {
		DateTime dateTime = new DateTime();
		setFields(dateTime, year, month, day, hour, minutes, seconds,
				milliseconds, tz, utc);
		return dateTime;
	}

	/**
	 * Creates an iCal4J DateTime. The values for year, month, day, hour,
	 * and minutes, should be set way that you specify them
	 * in a java.util.Calendar - which means zero indexed months for example
	 * (eg. January is '0').
	 * <p>
	 * Note that the TimeZone is not a java.util.TimeZone but an iCal4JTimeZone
	 *
	 * @param year    Year
	 * @param month   Month
	 * @param day     Day
	 * @param hour    Hour
	 * @param minutes Minutes
	 * @param tz      TimeZone
	 * @param utc     UTC Enabled
	 * @return {@link DateTime} object
	 */
	public static DateTime createDateTime(int year, int month, int day,
	                                      int hour, int minutes, TimeZone tz, boolean utc) {
		DateTime dateTime = new DateTime();
		setFields(dateTime, year, month, day, hour, minutes, 0, 0, tz, utc);
		return dateTime;

	}

	/**
	 * Creates an iCal4J Date. The values for year, month, day should be set way that
	 * you specify them in a java.util.Calendar - which means zero indexed months for example
	 * (eg. January is '0').
	 *
	 * Note that the TimeZone is not a java.util.TimeZone but an iCal4JTimeZone
	 *
	 * @param year Year
	 * @param month Month
	 * @param day Day
	 * @return {@link Date} object
	 */
	public static Date createDate(int year, int month, int day) {
		Date date = new Date();
		setFields(date, year, month, day, 0, 0, 0, 0, null, false);
		return date;

	}

	/**
	 * Creates an iCal4J DateTime. The values for year, month, day, hour,
	 * and minutes, should be set way that you specify them
	 * in a java.util.Calendar - which means zero indexed months for example
	 * (eg. January is '0').
	 *
	 * Note that the TimeZone is not a java.util.TimeZone but an iCal4JTimeZone
	 *
	 * @param year Year
	 * @param month Month
	 * @param day Day
	 * @param tz TimeZone
	 * @param utc UTC Enabled
	 * @return {@link DateTime} object
	 */
	public static Date createDateTime(int year, int month, int day,
	                                  TimeZone tz, boolean utc) {
		DateTime date = new DateTime();
		setFields(date, year, month, day, 0, 0, 0, 0, tz, utc);
		return date;

	}

	/**
	 * Returns the first event in the Calendar
	 *
	 * @param calendar Calendar containing Events
	 * @return VEvent Object representing the first event, else null if not present.
	 */
	public static VEvent getFirstEvent(net.fortuna.ical4j.model.Calendar calendar){
		return (VEvent) calendar.getComponents().getComponent(Component.VEVENT);
	}

	/**
	 * Get first non-timezone component
	 * @param resource CalDavResource to retrieve from.
	 * @param component Component to retrieve.
	 * @return null if not present
	 */
	public static CalendarComponent getFirstComponent(CalDAVResource resource, String component) {
		return resource.getCalendar().getComponent(component);
	}

	/**
	 * Return the first non-timezone component of a calendar:
	 *
	 * @param calendar Calendar
	 * @return Component
	 * @throws CalDAV4JException exception if calendar contains different types of event
	 */
	//TODO use a parameter to eventually skip VTimeZone
	public static CalendarComponent getFirstComponent(net.fortuna.ical4j.model
			                                                  .Calendar calendar) throws CalDAV4JException {
		return getFirstComponent(calendar, true);
	}

	/**
	 * Return the first component of a calendar, if it's the timezone, then based on the
	 * skipTimezone value, the Component will be skipped or returned.
	 *
	 * @param calendar Calendar
	 * @param skipTimezone To skip the Timezone Component
	 * @return Component
	 * @throws CalDAV4JException on error
	 */
	public static CalendarComponent getFirstComponent(net.fortuna.ical4j.model.Calendar calendar,
	                                                  boolean skipTimezone) throws CalDAV4JException {
		// XXX this works only if the ics is a caldav resource
		CalendarComponent ret = null;
		String compType = null;

		for (Object component : calendar.getComponents()) {

			if (!skipTimezone) {
				ret =  (CalendarComponent) component;
			} else if (! (component instanceof VTimeZone)) {
				// skip timezones
				if (ret == null) {
					ret = (CalendarComponent) component;
					compType = ret.getClass().getName();
				} else if (! compType.equals(component.getClass().getName()) ) {
					throw new CalDAV4JException("Can't get first component: "
							+ "Calendar contains different kinds of component");
				}

			}
		}
		return ret;
	}

	/**
	 * Get a Calendar UID value: as in Caldav, a Caldav Calendar Resource should have an
	 * unique UID value
	 * @param calendar Calendar
	 * @return UID Value
	 * @throws CalDAV4JException on error
	 */
	public static String getUIDValue(net.fortuna.ical4j.model.Calendar calendar) throws CalDAV4JException {
		return getUIDValue(getFirstComponent(calendar));
	}

	/**
	 * Set a Calendar UID value: as in Caldav, a Caldav Calendar Resource should have an
	 * unique UID value for a flexible method
	 * @see #addOrReplaceProperty
	 * @param calendar Calendar
	 * @param uid UID to set for the Calendar.
	 */
	public static void setUIDValue(net.fortuna.ical4j.model.Calendar calendar,
	                               String uid) {
		for (Object c : calendar.getComponents()) {
			if (c!=null && ! (c  instanceof VTimeZone)) {
				addOrReplaceProperty((Component) c, new Uid(uid));
			}
		}
	}

	/**
	 * @param event Event to check.
	 * @return Returns the Summary Value of the Event.
	 */
	public static String getSummaryValue(VEvent event) {
		return getPropertyValue(event, Property.SUMMARY);
	}

	/**
	 * @param component Component to check
	 * @return UID Value of Component.
	 */
	public static String getUIDValue(Component component) {
		return getPropertyValue(component, Property.UID);
	}

	/**
	 * @param component Component containing the property
	 * @param propertyName Name of Property to be returned.
	 * @return Return the Property represented by the name, null if not found.
	 */
	public static String getPropertyValue(Component component, String propertyName){
		Property property = component.getProperties().getProperty(propertyName);
		return property == null ? null : property.getValue();
	}

	/**
	 * Set fields based on the values to a Calendar
	 * @param date Date
	 * @param year Year
	 * @param month Month
	 * @param day Day
	 * @param hour Hour
	 * @param minutes Minutes
	 * @param seconds Seconds
	 * @param milliseconds MilliSeconds
	 * @param tz TimeZone
	 * @param utc UTC Enabled.
	 */
	private static void setFields(Date date, int year, int month, int day,
	                              int hour, int minutes, int seconds, int milliseconds, TimeZone tz,
	                              boolean utc){

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.setTimeZone(tz == null ? J_TZ_GMT : tz);
		if (date instanceof DateTime){
			if (utc) {
				((DateTime)date).setUtc(utc);
			} else if (tz != null){
				((DateTime)date).setTimeZone(tz);
			}
			calendar.set(Calendar.HOUR, hour);
			calendar.set(Calendar.MINUTE, minutes);
			calendar.set(Calendar.SECOND, seconds);
			calendar.set(Calendar.MILLISECOND, milliseconds);
		}
		date.setTime(calendar.getTimeInMillis());
	}

	/**
	 * @param c Component
	 * @param propName Property to check in Component.
	 * @return Returns true if Component contains the property
	 */
	public static boolean hasProperty(Component c, String propName){
		PropertyList l = c.getProperties().getProperties(propName);
		return l != null && l.size() > 0;
	}

	/**
	 * Returns the "master" VEvent - one that does not have a RECURRENCE-ID
	 *
	 * @param calendar Calendar from where the Master Event is supposed to be retrieved
	 * @param uid UID of the VEvent
	 * @return VEvent that does not have Recurrence ID
	 */
	public static VEvent getMasterEvent(net.fortuna.ical4j.model.Calendar calendar, String uid){
		ComponentList clist = calendar.getComponents().getComponents(Component.VEVENT);
		for (Object o : clist){
			VEvent curEvent = (VEvent) o;
			String curUid = getUIDValue(curEvent);
			if (uid.equals(curUid) && !hasProperty(curEvent, Property.RECURRENCE_ID) ){
				return curEvent;
			}
		}
		return null;
	}

	/**
	 * Returns the "master" VEvent - one that does not have a RECURRENCE-ID
	 *
	 * @param calendar Calendar from where the Master Event is supposed to be retrieved
	 * @return VEvent that does not have Recurrence ID
	 */
	public static VTimeZone getTimezone(net.fortuna.ical4j.model.Calendar calendar){
		ComponentList clist = calendar.getComponents().getComponents(Component.VTIMEZONE);
		for (Object o : clist){
			return (VTimeZone) o;
		}
		return null;
	}

	/**
	 *  Returns the "master" Component - one that does not have a RECURRENCE-ID
	 * @param calendar Calendar
	 * @param uid UID of Component
	 * @return Component with UID and no Recurrence ID
	 */
	// TODO create junit
	public static CalendarComponent getMasterComponent(net.fortuna.ical4j.model.Calendar calendar, String uid){
		ComponentList clist = calendar.getComponents();
		for (Object o : clist){
			CalendarComponent curEvent = (CalendarComponent) o;
			String curUid = getUIDValue(curEvent);
			if (uid.equals(curUid) && !hasProperty(curEvent, Property.RECURRENCE_ID)) {
				return curEvent;
			}
		}
		return null;
	}

	/**
	 * @param calendar Component
	 * @param uid UID of Component
	 * @param recurrenceId Recurrence ID
	 * @return Component with corresponding Recurrence ID
	 */
	// TODO create junit
	public static CalendarComponent getComponentOccurence(net.fortuna.ical4j.model.Calendar calendar, String uid, String recurrenceId){
		ComponentList clist = calendar.getComponents();
		for (Object o : clist){
			CalendarComponent curEvent = (CalendarComponent) o;
			String curUid = getUIDValue(curEvent);
			String curRid = getPropertyValue(curEvent, Property.RECURRENCE_ID);
			if (uid.equals(curUid) && UrlUtils.equalsIgnoreCase(recurrenceId, curRid) ){
				return curEvent;
			}
		}
		return null;
	}

	/**
	 * @param calendar Calendar
	 * @param uid UID of Component
	 * @param recurrenceId Recurrence ID
	 * @return the modified calendar
	 * @throws ParseException on error parsing Recurrence
	 */
	/// TODO create junit
	public static net.fortuna.ical4j.model.Calendar removeOccurrence(net.fortuna.ical4j.model.Calendar calendar,
	                                                                 String uid, String recurrenceId) throws ParseException {
		ComponentList clist = calendar.getComponents();
		CalendarComponent master = null;
		CalendarComponent toBeRemoved = null;
		for (Object o : clist){
			CalendarComponent curEvent = (CalendarComponent) o;
			if ( (master==null) && ! (o instanceof VTimeZone)) {
				master = curEvent;
			}
			String curUid = getUIDValue(curEvent);
			String curRid = getPropertyValue(curEvent, Property.RECURRENCE_ID);
			if (uid.equals(curUid) && UrlUtils.equalsIgnoreCase(recurrenceId, curRid) ){
				toBeRemoved = curEvent;
				break;
			}
		}
		if (toBeRemoved!=null) {
			clist.remove(toBeRemoved);
		}
		if (master != null) {
			ExDate x = new ExDate();
			x.setValue(recurrenceId);
			master.getProperties().add(x);
		}
		return calendar;
	}

	/**
	 * Adds or replaces the property in a component.
	 * @param component Component to modify.
	 * @param property Property to add or update.
	 */
	public static void addOrReplaceProperty(Component component, Property property){
		Property oldProp = component.getProperties().getProperty(property.getName());
		if (oldProp != null){
			component.getProperties().remove(oldProp);
		}

		component.getProperties().add(property);
	}

}
