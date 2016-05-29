package org.osaf.caldav4j.model.request;

import org.apache.jackrabbit.webdav.security.Privilege;
import org.osaf.caldav4j.CalDAVConstants;

/**
 * Class for all the CalDAV Scheduling Privileges. To get the rest of the Privileges
 * see {@link Privilege}
 */
public class CalDAVPrivilege {

	// Standard CalDAV  Privileges
	public static final Privilege READ_FREE_BUSY =
			Privilege.getPrivilege( "read-free-busy", CalDAVConstants.NAMESPACE_CALDAV);


	public static final Privilege SCHEDULE_DELIVER =
			Privilege.getPrivilege( "schedule-deliver", CalDAVConstants.NAMESPACE_CALDAV);


	public static final Privilege SCHEDULE_DELIVER_INVITE =
			Privilege.getPrivilege( "schedule-deliver-invite", CalDAVConstants.NAMESPACE_CALDAV);


	public static final Privilege SCHEDULE_DELIVER_REPLY =
			Privilege.getPrivilege( "schedule-deliver-reply", CalDAVConstants.NAMESPACE_CALDAV);


	public static final Privilege SCHEDULE_QUERY_FREEBUSY =
			Privilege.getPrivilege( "schedule-query-freebusy", CalDAVConstants.NAMESPACE_CALDAV);


	public static final Privilege SCHEDULE_SEND =
			Privilege.getPrivilege( "schedule-send", CalDAVConstants.NAMESPACE_CALDAV);


	public static final Privilege SCHEDULE_SEND_INVITE =
			Privilege.getPrivilege( "schedule-send-invite", CalDAVConstants.NAMESPACE_CALDAV);


	public static final Privilege SCHEDULE_SEND_REPLY =
			Privilege.getPrivilege( "schedule-send-reply", CalDAVConstants.NAMESPACE_CALDAV);


	public static final Privilege SCHEDULE_SEND_FREEBUSY =
			Privilege.getPrivilege( "schedule-send-freebusy", CalDAVConstants.NAMESPACE_CALDAV);

}
