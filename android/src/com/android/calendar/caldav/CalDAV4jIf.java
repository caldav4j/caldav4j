package com.android.calendar.caldav;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

import org.osaf.caldav4j.CalDAVCalendarCollection;
import org.osaf.caldav4j.CalDAVCollection;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.exceptions.ResourceNotFoundException;
import org.osaf.caldav4j.util.ICalendarUtils;

import android.content.res.AssetManager;
import android.util.Log;
import java.util.ArrayList;

public class CalDAV4jIf extends BaseTestCase {
    private AssetManager assMgr = null;
    private static final String LOGTAG = "CalDAV4jIf";

	public InputStream getResourceAsStream (String res) {
        InputStream stream = null;
        try {
        	stream = assMgr.open(res, AssetManager.ACCESS_RANDOM);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Problems opening file:" + res + "\n" + e);           	
        }
        return stream;
	}
    public Calendar getCalendarResource(String resourceName) {
        Calendar cal;
        InputStream stream = getResourceAsStream(resourceName);
        CalendarBuilder cb = new CalendarBuilder();
        
        try {
            cal = cb.build(stream);
        } catch (Exception e) {        	
            throw new RuntimeException("Problems opening file:" + resourceName + "\n" + e);
        }
        
        return cal;
    } 


	public CalDAV4jIf (AssetManager assmgr) {
		assMgr = assmgr;
	}

	private CalDAVCalendarCollection createCalDAVCalendarCollection() {
        CalDAVCalendarCollection calendarCollection = new CalDAVCalendarCollection(
        		COLLECTION_PATH, createHostConfiguration(), methodFactory,
                CalDAVConstants.PROC_ID_DEFAULT);
        return calendarCollection;
    }

	public int testConnection () throws Exception {
	    CalDAVCollection calendarCollection = createCalDAVCollection();

	    // test with the right collection is ok
	    int actual = calendarCollection.testConnection(httpClient);
	    Log.d (LOGTAG, "testConnection " + actual);
	    return actual;
	}
    
    public VEvent [] getEvents () throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        Date beginDate = ICalendarUtils.createDateTime(2010, 1, 1, null, true);
        Date endDate = ICalendarUtils.createDateTime(2011, 1, 1, null, true);
        List<Calendar> l = calendarCollection.getEventResources(httpClient,
                beginDate, endDate);

        ArrayList<VEvent> evs = new ArrayList<VEvent>();

        for (Calendar calendar : l) {
            ComponentList vevents = calendar.getComponents().getComponents(
                    Component.VEVENT);
            VEvent ve = (VEvent) vevents.get(0);
            /*
            Log.d (LOGTAG, "uid " + ICalendarUtils.getUIDValue(ve));
            Log.d (LOGTAG, "begin " + ICalendarUtils.getPropertyValue(ve, Property.DTSTART));
            Log.d (LOGTAG, "end " + ICalendarUtils.getPropertyValue(ve, Property.DTEND));
            Log.d (LOGTAG, "duration " + ICalendarUtils.getPropertyValue(ve, Property.DURATION));
            Log.d (LOGTAG, "summary " + ICalendarUtils.getSummaryValue(ve));
            */
            evs.add (ve);
        }
        return evs.toArray (new VEvent[] {});
    }

    public void removeEv (String uid) throws Exception {
		//del(COLLECTION_PATH + "/" + uid + ".ics");
        // XXX The above does not seem to work always
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        calendarCollection.deleteEvent(httpClient, uid);
        Log.d (LOGTAG, "Removed " + uid + " on server");
    }

    public void updateEv (VEvent ve) throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        calendarCollection.updateMasterEvent(httpClient, ve, null);
        Log.d (LOGTAG, "Updated " + ICalendarUtils.getUIDValue (ve) + " on server");
    }
    public void addEv (VEvent ve) throws Exception {
        CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
        calendarCollection.addEvent(httpClient, ve, null);
        Log.d (LOGTAG, "Added " + ICalendarUtils.getUIDValue (ve) + " on server");
    }
}
