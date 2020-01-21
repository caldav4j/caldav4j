import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.model.request.CalendarData;
import com.github.caldav4j.model.request.CalendarQuery;
import com.github.caldav4j.model.request.CompFilter;
import com.github.caldav4j.model.response.CalendarDataProperty;
import com.github.caldav4j.util.XMLUtils;
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
 * Calendar Query through the CalDAVReport method to retrieve all Events from the calendar
 */
public class CalendarQueryExample {
	public static void main(String[] args) {
		// Create a set of Dav Properties to query
		DavPropertyNameSet properties = new DavPropertyNameSet();
		properties.add(DavPropertyName.GETETAG);

		// Create a Component filter for VCALENDAR and VEVENT
		CompFilter vcalendar = new CompFilter(Calendar.VCALENDAR);
		vcalendar.addCompFilter(new CompFilter(Component.VEVENT));

		// Create a Query XML object with the above properties
		CalendarQuery query = new CalendarQuery(properties, vcalendar, new CalendarData(), false, false);

		/*
		<C:calendar-query xmlns:C="urn:ietf:params:xml:ns:caldav">
		  <D:prop xmlns:D="DAV:">
		    <D:getetag/>
		    <C:calendar-data/>
		  </D:prop>
		  <C:filter>
		    <C:comp-filter name="VCALENDAR">
		      <C:comp-filter name="VEVENT"/>
		    </C:comp-filter>
		  </C:filter>
		</C:calendar-query>
		*/
		// Print to STDOUT the generated Query
		System.out.println(XMLUtils.prettyPrint(query));

		HttpCalDAVReportMethod method = null;

		try {
			method = new HttpCalDAVReportMethod("path://to/caldav/calendar", query, CalDAVConstants.DEPTH_1);
			CloseableHttpClient client = HttpClients.createDefault();

			// Execute the method
			HttpResponse httpResponse = client.execute(method);

			// If successful
			if (method.succeeded(httpResponse)) {
				// Retrieve all multistatus responses
				MultiStatusResponse[] multiStatusResponses = method.getResponseBodyAsMultiStatus(httpResponse).getResponses();

				// Iterate through all responses
				for (MultiStatusResponse response : multiStatusResponses) {
					// If the individual calendar request was succesful
					if (response.getStatus()[0].getStatusCode() == SC_OK) {
						// Retrieve ETag and  Calendar from response
						String etag = CalendarDataProperty.getEtagfromResponse(response);
						Calendar ical = CalendarDataProperty.getCalendarfromResponse(response);

						// Print to output
						System.out.println("Calendar at " + response.getHref() + " with ETag: " + etag);
						System.out.println(ical);
					}
				}
			}
		} catch (Exception e) {
			// No-op
		} finally {
			if (method != null) {
				method.reset();
			}
		}
	}
}
