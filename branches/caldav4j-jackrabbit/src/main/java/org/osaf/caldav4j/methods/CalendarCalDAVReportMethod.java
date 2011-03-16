package org.osaf.caldav4j.methods;

import net.fortuna.ical4j.model.Calendar;

import org.osaf.caldav4j.exceptions.UnimplementedException;
import org.osaf.caldav4j.model.request.FreeBusyQuery;

public class CalendarCalDAVReportMethod extends CalDAVReportMethod {

	protected CalendarCalDAVReportMethod(String path) {
		super(path);
		throw new RuntimeException("To be implemented");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3913868919287801330L;

	public void setReportRequest(FreeBusyQuery query) {
		// TODO Auto-generated method stub
		throw new UnimplementedException();

	}

	public Calendar getResponseBodyAsCalendar() {
		// TODO Auto-generated method stub
		throw new UnimplementedException();
	}

}
