import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.model.request.FreeBusyQuery;
import com.github.caldav4j.model.request.TimeRange;
import com.github.caldav4j.util.XMLUtils;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.util.MapTimeZoneCache;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Freebusy Query Example
 */
public class FreeBusyQueryExample {
	static {
		// Disable TimeZone caching through JCache
		System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache.class.getName());
	}
	public static void main(String[] args) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();

		FreeBusyQuery fb = new FreeBusyQuery();
		Date start = new DateTime("20000101T000000Z"); // ical4j Date objects
		Date end = new DateTime("20000201T000000Z");
		fb.setTimeRange(new TimeRange(start, end)); // Set the time range

		// Print the XML to STDOUT
		System.out.println(XMLUtils.toPrettyXML(fb.createNewDocument()));

		// Initialize the method, with the report, query
		HttpCalDAVReportMethod method = new HttpCalDAVReportMethod("http://path/to/my/caldav/repo", fb);
		method.setCalendarBuilder(new CalendarBuilder());

		HttpResponse response = httpclient.execute(method);
		if (method.succeeded(response)) {
			Calendar calendar = method.getResponseBodyAsCalendar(response)
		}
	}
}