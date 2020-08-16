package org.osaf.caldav4j.example;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.model.request.CalendarData;
import com.github.caldav4j.model.request.CalendarMultiget;
import com.github.caldav4j.model.request.CompFilter;
import com.github.caldav4j.model.response.CalendarDataProperty;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;

import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * Retrieve calendars by Multiget Query, given a list of hrefs
 */
public class MultigetMethodExample {
	public static void main(String[] args) {
		String[] hrefs = new String[]{"path://to/calendar/1.ics", "path://to/calendar/2.ics"};

		// Retrieve ETag of Calendar
		DavPropertyNameSet properties = new DavPropertyNameSet();
		properties.add(DavPropertyName.GETETAG);

		// Retrieve CalendarData as well
		CalendarData calendarData = new CalendarData();

		// Retrieve Calendar and Events
		CompFilter vcalendar = new CompFilter(Calendar.VCALENDAR);
		vcalendar.addCompFilter(new CompFilter(Component.VEVENT));
		CalendarMultiget query = new CalendarMultiget(properties, calendarData, false, false);

		// Add list of Calendar URLs
		for (String href : hrefs) {
			query.addHref(href);
		}

		HttpCalDAVReportMethod method = null;

		try {
			// Initiate CalDAVReportMethod with query at Depth 1
			method = new HttpCalDAVReportMethod("path://to/calendar", query, CalDAVConstants.DEPTH_1);
			// Create HttpClient
			CloseableHttpClient client = HttpClients.createDefault();

			// Execute method
			HttpResponse httpResponse = client.execute(method);

			// If response was successful
			if (method.succeeded(httpResponse)) {
				// Process each response
				for (MultiStatusResponse response : method.getResponseBodyAsMultiStatus(httpResponse).getResponses()) {
					// If success
					if (response.getStatus()[0].getStatusCode() == SC_OK) {
						// Retrieve ETag and Calendar from response
						String href = response.getHref();
						String etag = CalendarDataProperty.getEtagfromResponse(response);
						Calendar ical = CalendarDataProperty.getCalendarfromResponse(response);

						// Print to output
						System.out.println("Calendar at " + href + " with ETag: " + etag);
						System.out.println(ical);
					}
				}
			}
		} catch (Exception e) {}
		finally {
			if (method != null) {
				method.reset();
			}
		}

	}
}
