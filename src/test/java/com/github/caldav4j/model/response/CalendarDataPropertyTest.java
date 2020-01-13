package com.github.caldav4j.model.response;

import net.fortuna.ical4j.model.Calendar;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Mehdi Teymourlouie (mehdi.teymourlouie@gmail.com)
 * on 11/12/18.
 */
public class CalendarDataPropertyTest {

    @Test
    public void getCalendarfromProperty() {
        DavProperty davProperty = null;
        Calendar calendar = CalendarDataProperty.getCalendarfromProperty(davProperty);
        assertNull("Calendar object for null property should be null object", calendar);


        davProperty = new DefaultDavProperty("calendar-data", null, Namespace.getNamespace("urn:ietf:params:xml:ns:caldav"));
        calendar = CalendarDataProperty.getCalendarfromProperty(davProperty);
        assertNull("Calendar object for a property with null value should be null object", calendar);
    }

    @Test
    public void getEtagfromProperty() {
        DavProperty davProperty = null;
        String etag = CalendarDataProperty.getEtagfromProperty(davProperty);
        assertNull("ETag for null property should be null", etag);

        davProperty = new DefaultDavProperty(DavPropertyName.GETETAG, null);
        etag = CalendarDataProperty.getEtagfromProperty(davProperty);
        assertNull("ETag for a property  with null value should be null", etag);

        davProperty = new DefaultDavProperty(DavPropertyName.DISPLAYNAME, null);
        etag = CalendarDataProperty.getEtagfromProperty(davProperty);
        assertNull("ETag for a property  with a name different than GETETAG should be null", etag);

        final String value = "C=3@U=0785e49e-f480-40dd-9666-fda84e012cba";
        davProperty = new DefaultDavProperty(DavPropertyName.GETETAG, value);
        etag = CalendarDataProperty.getEtagfromProperty(davProperty);
        assertEquals("etag value is not correct", value, etag);
    }
}
