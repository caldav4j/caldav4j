package com.github.caldav4j.methods;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.model.request.CalendarDescription;
import com.github.caldav4j.model.request.DisplayName;
import com.github.caldav4j.model.request.MkCalendar;
import com.github.caldav4j.model.request.Prop;
import com.github.caldav4j.util.CalDAVStatus;
import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.w3c.dom.Document;

public class HttpMkCalendarMethod extends BaseDavRequest {

    protected MkCalendar mkCalendar = null;

    // --------------------------------------------------------- Public Methods

    /**
     * Default Constructor, makes a calendar at URI.
     *
     * @param uri Location to the CalendarResource
     */
    public HttpMkCalendarMethod(String uri) {
        super(URI.create(uri));
    }

    /**
     * Default Constructor, makes a calendar at URI.
     *
     * @param uri Location to the CalendarResource
     */
    public HttpMkCalendarMethod(URI uri) {
        super(uri);
    }

    /**
     * Create a calendar with the MkCalendar object and properties.
     *
     * @param uri Location to the CalendarResource
     * @param m MkCalendar object representing the properties
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod(URI uri, MkCalendar m) throws IOException {
        super(uri);
        if (m != null) this.mkCalendar = m;
        processRequest();
    }

    /**
     * Create a calendar with the MkCalendar object and properties.
     *
     * @param uri Location to the CalendarResource
     * @param m MkCalendar object representing the properties
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod(String uri, MkCalendar m) throws IOException {
        this(URI.create(uri), m);
    }

    /**
     * Convenience method to create a calendar with the defined properties.
     *
     * @param uri Location to the CalendarResource
     * @param displayName Display Name of Calendar
     * @param description Description of Calendar
     * @param descriptionLang Language of Description.
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod(
            URI uri, String displayName, String description, String descriptionLang)
            throws IOException {
        super(uri);
        Prop p = new Prop();
        if (displayName != null) p.add(new DisplayName(displayName));

        if (description != null) p.add(new CalendarDescription(description, descriptionLang));
        mkCalendar = new MkCalendar(p);
        processRequest();
    }

    /**
     * Convenience method to create a calendar with the defined properties.
     *
     * @param uri Location to the CalendarResource
     * @param displayName Display Name of Calendar
     * @param description Description of Calendar
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod(URI uri, String displayName, String description)
            throws IOException {
        this(uri, displayName, description, null);
    }

    /**
     * Convenience method to create a calendar with the defined properties.
     *
     * @param uri Location to the CalendarResource
     * @param displayName Display Name of Calendar
     * @param description Description of Calendar
     * @param descriptionLang Language of Description.
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod(
            String uri, String displayName, String description, String descriptionLang)
            throws IOException {
        this(URI.create(uri), displayName, description, descriptionLang);
    }

    /**
     * Convenience method to create a calendar with the defined properties.
     *
     * @param uri Location to the CalendarResource
     * @param displayName Display Name of Calendar
     * @param description Description of Calendar
     * @throws IOException if error occurred during parsing of parameters
     */
    public HttpMkCalendarMethod(String uri, String displayName, String description)
            throws IOException {
        this(uri, displayName, description, null);
    }

    /**
     * Sets the Request Entity, and if successful, also sets the corresponding request headers.
     *
     * @throws IOException on error.
     */
    private void processRequest() throws IOException {
        try {
            Document d = DomUtil.createDocument();
            d.appendChild(mkCalendar.toXml(d));
            setEntity(XmlEntity.create(d));
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getMethod() {
        return CalDAVConstants.METHOD_MKCALENDAR;
    }

    /** {@inheritDoc} */
    @Override
    public boolean succeeded(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == CalDAVStatus.SC_CREATED;
    }
}
