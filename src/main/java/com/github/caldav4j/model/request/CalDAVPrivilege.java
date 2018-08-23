/*
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
package com.github.caldav4j.model.request;

import org.apache.jackrabbit.webdav.security.Privilege;
import com.github.caldav4j.CalDAVConstants;

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
