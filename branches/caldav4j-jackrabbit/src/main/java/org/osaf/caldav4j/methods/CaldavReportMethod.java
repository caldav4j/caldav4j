package org.osaf.caldav4j.methods;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.Status;
import org.apache.jackrabbit.webdav.client.methods.ReportMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.CalDAVResource;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.model.response.CalDAVResponse;
import org.osaf.caldav4j.util.CaldavStatus;
import org.osaf.caldav4j.util.MethodUtil;


/**
 * Implement a CaldavReportMethod
 * @author rpolli
 *
 */
public class CaldavReportMethod extends ReportMethod implements CalDAVConstants{
	private static final Log log = LogFactory.getLog(CaldavReportMethod.class);

	/**
	 * This should be called only by the factory
	 * @param uri
	 * @param reportInfo
	 * @throws IOException
	 */
	protected CaldavReportMethod(String uri, ReportInfo reportInfo)
	throws IOException {
		super(uri, reportInfo);
	}

	CalendarBuilder builder = null;

	List<String> responseURIs = new ArrayList<String>();

	public List<CalDAVResponse> getResponses() throws CalDAV4JException {

		switch (getStatusCode()) {
		case CaldavStatus.SC_MULTI_STATUS:
			try {
				return parseMultiStatus();
			} catch (Exception e1) {
				throw new CalDAV4JException("Error parsing response", e1);
			}
		case CaldavStatus.SC_CONFLICT:
		case CaldavStatus.SC_FORBIDDEN:        	
			//create error response
			break;
		default:
			try {
				MethodUtil.StatusToExceptions(this);
			} catch (CalDAV4JException e) {}
			break;

		}

		return null;


	}

	private String getETag(DavPropertySet pset) {
		DavProperty<?> etag =  pset.get(CalDAVConstants.ELEM_GETETAG,
				DavConstants.NAMESPACE);
		if (etag != null) {
			return (String) etag.getValue();
		}
		return null;
	}
	private String getCalendarData(DavPropertySet pset) {
		DavProperty<?> etag =  pset.get(CalDAVConstants.CALDAV_CALENDAR_DATA,
				CalDAVConstants.NAMESPACE_CALDAV);
		if (etag != null) {
			return (String) etag.getValue();
		}
		return null;
	}
	synchronized private List<CalDAVResponse>  parseMultiStatus() throws IOException, DavException {
		List<CalDAVResponse> davResponses = new ArrayList<CalDAVResponse>();

		MultiStatus multistatus = getResponseBodyAsMultiStatus();
		MultiStatusResponse responses[] = multistatus.getResponses();
		for (MultiStatusResponse r : responses) {
			String href = r.getHref();
			responseURIs.add(href);
			Status[] statuses = r.getStatus();
			for (Status s : statuses) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("url %s status: %d ", href, s.getStatusCode()));
				}

				DavPropertySet propSet = r.getProperties(s.getStatusCode());
				String prop = getCalendarData(propSet);
				String eTagProperty = getETag(propSet);
				if (href != null && prop != null && eTagProperty != null) {
					net.fortuna.ical4j.model.Calendar c;
					try {
						c = builder.build(new StringReader(prop));
						CalDAVResource res = new CalDAVResource(c, eTagProperty, href);
						System.err.println(res.getCalendar());

					} catch (ParserException e1) {
						log.error("Error parsing calendar", e1)	;
					} catch (IOException e) {
						log.error("Error parsing calendar", e);					}
				}	
			}
		}
		return davResponses;
	}
	/** 
	 * this method should be used only via the factory 
	 * 
	 * @param builder
	 */
	protected void setCalendarBuilder(CalendarBuilder builder) {
		this.builder = builder;		
	}
}
