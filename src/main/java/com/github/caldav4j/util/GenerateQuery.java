package com.github.caldav4j.util;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.exceptions.CalDAV4JProtocolException;
import com.github.caldav4j.exceptions.DOMValidationException;
import com.github.caldav4j.model.request.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

/**
 * Copyright 2008 Roberto Polli
 *
 * <p>This class is a helper class for creating Calendar-Query REPORTs because of the complexity of
 * iCalendar object and relative queries, this class is intended to help the creation of basic
 * queries like get all VEVENT with THOSE attributes in THIS time-range
 *
 * <p>main schema is a strongly typed class, with helpers/parsers that will create the caldav query
 *
 * <p>usage:
 *
 * <pre>
 * GenerateQuery qg = new GenerateQuery();
 * qg.setComponent("VEVENT"); // retrieve the whole VEVENT
 * qg.setComponent("VEVENT : UID, ATTENDEE, DTSTART, DTEND"); // retrieve the given properties
 *
 * qg.setFilter("VEVENT"); //request on VEVENT
 * qg.setFilter("VEVENT [start;end] : UID==value1 , DTSTART==[start;end], DESCRIPTION==UNDEF, SUMMARY!=not my summary,")
 * </pre>
 *
 * start and end values can be empty strings "" or RFC2445-UTC timestamp
 *
 * <p><b>Note: This class is still experimental</b>
 *
 * @since 0.5
 */
public class GenerateQuery {

    private static final Logger log = LoggerFactory.getLogger(GenerateQuery.class);
    // component attributes
    private String requestedComponent = null; // VEVENT, VTODO
    private List<String> requestedComponentProperties =
            new ArrayList<>(); // a list of properties to be retrieved

    // Nested object queries should be managed nesting two generated queries
    private String filterComponent = null; // VEVENT, VTODO
    private List<String> filterComponentProperties = new ArrayList<>();
    private Date timeRangeStart = null;
    private Date timeRangeEnd = null;
    private boolean allProp = true;
    private boolean noCalendarData = false;

    public void setNoCalendarData(boolean p) {
        this.noCalendarData = p;
    }

    // other settings: collation
    private String collation = null; // use TextMatch default value
    // comp flags
    // TODO limit-recurrence-set, limit-freebusy-set, get-etag

    private Date recurrenceSetEnd;
    private Date recurrenceSetStart;

    private Integer expandOrLimit;

    /**
     * Create a GenerateQuery object with the given parameters NB: DON'T use spaces in comp and
     * filter unless you REALLY need spaces
     *
     * @param component COMPONENT : PROP1,PROP2,..,PROPn
     * @param filterComponent COMPONENT : PROP1==VALUE1,PROP2!=VALUE2
     * @throws CalDAV4JException on error parsing
     */
    public GenerateQuery(String component, String filterComponent) throws CalDAV4JException {
        setComponent(component);
        setFilter(filterComponent);
    }

    /** Default Constructor */
    public GenerateQuery() {}

    /**
     * Constructor with Comp and CompFilter
     *
     * <p>Note: This is too low level.
     *
     * @param c COMPONENT, such as VEVENT, VCALENDAR ...
     * @param cProp PROP1,PROP2,..,PROPn for the Component
     * @param cFilter COMP-FILTER, to be used
     * @param pFilter Properties of the COMP-FILTER
     */
    protected GenerateQuery(String c, List<String> cProp, String cFilter, List<String> pFilter) {
        this(c, cProp, cFilter, pFilter, null);
    }

    /**
     * Constructor with Comp, CompFilter and collation
     *
     * @param c COMPONENT, such as VEVENT, VCALENDAR ...
     * @param cProp PROP1,PROP2,..,PROPn for the Component
     * @param cFilter COMP-FILTER, to be used
     * @param pFilter Properties of the COMP-FILTER
     * @param collation Collation to be set, for example: "i;octet" or "i;ascii-casemap"
     */
    protected GenerateQuery(
            String c, List<String> cProp, String cFilter, List<String> pFilter, String collation) {

        setComponent(c, cProp);
        setFilter(cFilter, pFilter);
        this.collation = collation;
    }

    /**
     * Constructor with Comp & CompFilter as arrays
     *
     * @deprecated Use the other constructors
     */
    private GenerateQuery(String c, String cProp[], String cFilter, String pFilter[]) {

        this(
                c,
                cProp != null ? Arrays.asList(cProp) : null,
                cFilter,
                pFilter != null ? Arrays.asList(pFilter) : null);
    }

    /**
     * Validator TODO this method should provide at least a basic validation of the calendar-query
     *
     * @return Returns true if valid, false otherwise.
     * @deprecated This method does nothing but returning true!!!
     */
    public boolean validate() {
        return true;
    }

    /**
     * Set the component to retrieve: VEVENT, VTODO VEVENT : UID, DTSTART, DTEND,
     *
     * @param component Component to retrieve
     */
    public void setComponent(String component) {
        if (component != null) {
            String cl[] = null;
            String c[] = component.trim().split("\\s*:\\s*", 2);

            setRequestedComponent(c[0]);

            // if a list of properties is specified, then remove the allprop tag
            if (c.length > 1) {
                allProp = false;
                cl = c[1].trim().split("\\s*,\\s*");
                this.requestedComponentProperties = Arrays.asList(cl);
            }
        }
    }

    /**
     * Set the component and properties to retrieve: VEVENT, VTODO
     *
     * @param component Component
     * @param props Related Properties
     */
    public void setComponent(String component, List<String> props) {
        if (component != null) {
            setRequestedComponent(component);
            this.requestedComponentProperties = props;
        }
    }

    /** Transform the requested component properties fields in a PropComp value */
    private Comp getComp() {
        Comp vCalendarComp = new Comp();
        vCalendarComp.setName(Calendar.VCALENDAR);

        if (requestedComponent != null) {
            Comp vEventComp = new Comp();
            vEventComp.setName(requestedComponent);

            if (requestedComponentProperties.isEmpty()) {
                vEventComp.setAllProp(true);
            } else {
                for (String propertyName : requestedComponentProperties) {
                    // add properties to VCALENDAR.VEVENT
                    vEventComp.addProp(
                            new CalDAVProp(
                                    propertyName, false, false)); // @see modification to CalDAVProp
                }
            }
            // add only one component...maybe more ;)
            List<Comp> comps = new ArrayList<>();

            try {
                vEventComp.validate();
                comps.add(vEventComp);
            } catch (DOMValidationException e) {
                log.warn("Comp not valid");
            }
            vCalendarComp.setComps(comps);
        } else {
            vCalendarComp.setAllProp(true);
        }

        return vCalendarComp;
    }

    /**
     * Set the component to filter: VEVENT, VTODO syntax:
     *
     * <pre>
     * VTODO [;] : UID==1231423423145231 , DTSTART=[1214313254324;$3214234231] , SUMMARY!=Caldav4j
     * </pre>
     *
     * @param filterComponent Filter Component to set
     * @throws CalDAV4JException on error parsing Time
     */
    public void setFilter(String filterComponent) throws CalDAV4JException {
        if (filterComponent != null) {
            String c[] = filterComponent.split("\\s*:\\s*", 2); // split string in two
            Pattern compFilterString = Pattern.compile("(.+?)\\s*(\\[(.*?);(.*?)\\])?");

            Matcher m = compFilterString.matcher(c[0]);
            if (m.matches()) {
                setFilterComponent(m.group(1));

                // a time-range filter
                if (m.group(4) != null) {
                    timeRangeStart = parseTime(m.group(3));
                    timeRangeEnd = parseTime(m.group(4));
                }

                if (c.length > 1) {
                    String cl[] = c[1].trim().split("\\s*,\\s*");
                    this.filterComponentProperties = Arrays.asList(cl);
                }
            }
        }
    }

    /**
     * Set the component and properties to filter: VEVENT, VTODO
     *
     * @param filterComponent Filter Component
     * @param props Properties of the Filter
     */
    public void setFilter(String filterComponent, List<String> props) {
        if (filterComponent != null) {
            setFilterComponent(filterComponent);
            setFilterComponentProperties(props);
        }
    }

    /**
     * Transform filter component properties into a List of PropFilter this method parses
     *
     * @throws CalDAV4JException on parsing error
     */
    private List<PropFilter> getPropFilters() throws CalDAV4JException {
        List<PropFilter> pf = new ArrayList<>();
        Pattern filter = Pattern.compile("(.+?)([!=]=)(\\[(.*?);(.*?)\\]|([^\\]].+))");

        for (String p : this.filterComponentProperties) {
            String name = null;
            Boolean isDefined = null;
            boolean negateCondition = false;
            Date timeRangeStart = null, timeRangeEnd = null;
            Boolean isTextmatchcaseless = true;
            String textmatchString = null;

            //
            // parse: UID==3oij312po3214432 , DESCRIPTION!=Spada , DTSTART==[b;e]
            //
            Matcher str = filter.matcher(p);

            if (str.matches() && (str.group(3) != null)) {
                name = str.group(1);
                negateCondition = "!=".equals(str.group(2));

                if (str.group(4) == null) {
                    // standard filter
                    if ("UNDEF".equals(str.group(3))) {
                        isDefined = false;
                    } else {
                        textmatchString = str.group(3);
                    }
                } else if (str.group(5) != null) {
                    // a time-range filter
                    timeRangeStart = parseTime(str.group(4));
                    timeRangeEnd = parseTime(str.group(5));
                }

                List<String> componentList =
                        Arrays.asList(
                                Component.VALARM,
                                Component.VEVENT,
                                Component.VFREEBUSY,
                                Component.VJOURNAL,
                                Component.VTIMEZONE,
                                Component.VTODO,
                                Component.VVENUE);

                if (!componentList.contains(name)) {
                    pf.add(
                            new PropFilter(
                                    name,
                                    isDefined,
                                    timeRangeStart,
                                    timeRangeEnd,
                                    isTextmatchcaseless,
                                    negateCondition,
                                    this.collation,
                                    textmatchString,
                                    null));
                } else {
                    // if there, filter is invalid: we needed a comp-filter, not prop-filter
                }
            } else {
                // not a valid filter
                log.warn("Not a valid filter");
            }
        }
        return pf;
    }

    /** Transform filters in a CompFilter */
    private CompFilter getFilter() throws CalDAV4JException {

        // search for VCALENDAR matching...
        CompFilter vCalendarCompFilter = new CompFilter();
        vCalendarCompFilter.setName(Calendar.VCALENDAR);

        // parse filterComponent
        if (this.filterComponent != null) {
            CompFilter vEventCompFilter =
                    new CompFilter(
                            this.filterComponent,
                            false,
                            timeRangeStart,
                            timeRangeEnd, /// isDefined, dateStart, dateEnd
                            null,
                            getPropFilters().size() == 0 ? null : getPropFilters());
            try {
                vEventCompFilter.validate();
                vCalendarCompFilter.addCompFilter(vEventCompFilter);
            } catch (DOMValidationException e) {
                // if filter is bad, don't add nothing
                log.warn("CompFilter is not valid");
            }
        }

        return vCalendarCompFilter;
    }

    /**
     * Create a CalendarQuery
     *
     * @return Returns the CalendaryQuery object.
     * @throws CalDAV4JException on error
     * @deprecated Use generate() instead;
     */
    public CalendarQuery generateQuery() throws CalDAV4JException {
        return generate();
    }

    /**
     * This should parse QueryGenerator attributes and create the CalendarQuery
     *
     * @return The generated CalendarQuery
     * @throws CalDAV4JException on parsing errors
     */
    public CalendarQuery generate() throws CalDAV4JException {

        CalendarQuery query = new CalendarQuery();
        query.addProperty(CalDAVConstants.DNAME_GETETAG);

        if (!noCalendarData) {
            // TODO limit-recurrence-set
            CalendarData calendarData = new CalendarData();
            if (recurrenceSetEnd != null || recurrenceSetStart != null) {
                calendarData.setExpandOrLimitRecurrenceSet(expandOrLimit);
                calendarData.setRecurrenceSetStart(recurrenceSetStart);
                calendarData.setRecurrenceSetEnd(recurrenceSetEnd);
            }
            calendarData.setComp(getComp());

            query.setCalendarDataProp(calendarData);
        } else {
            if (allProp) {
                // only set the allprop tag if no calendar-data is included, otherwise it's
                // set on the calendar-data comp if needed
                query.addProperty(CalDAVConstants.DNAME_ALLPROP);
            }

            if (this.recurrenceSetEnd != null || this.recurrenceSetStart != null) {
                throw new CalDAV4JProtocolException(
                        "Bad query: you set noCalendarData but you have limit-recurrence-set");
            }
        }
        query.setCompFilter(getFilter());
        query.validate();

        return query;
    }

    /**
     * Pretty Print the XML document.
     *
     * @return String containing the XML
     */
    public String prettyPrint() {
        // query.validate();
        try {
            Document doc = generate().createNewDocument();
            return XMLUtils.toPrettyXML(doc);

        } catch (DOMValidationException domve) {
            throw new RuntimeException(domve);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    // getters+setters:

    //  remove trailing spaces from queries and get better input

    /**
     * @param c Set the component, also this removes the trailing spaces
     */
    public void setRequestedComponent(String c) {
        if (c != null) {
            this.requestedComponent = c.trim();
        }
    }

    /**
     * @param c Set the Filter Component, after removing trailing spaces.
     */
    public void setFilterComponent(String c) {
        if (c != null) {
            this.filterComponent = c.trim();
        }
    }

    /**
     * @param a Set Filter Properties
     */
    public void setFilterComponentProperties(List<String> a) {
        // if passed variable is null, a new ArrayList<String> remains
        if (a != null) {
            this.filterComponentProperties = a;
        }
    }

    /**
     * @param requestedComponentProperties Set Component Properties
     */
    public void setRequestedComponentProperties(List<String> requestedComponentProperties) {
        if (requestedComponentProperties != null) {
            this.requestedComponentProperties = requestedComponentProperties;
        }
    }

    /**
     * @param start Start of Range
     * @param end End of Range
     */
    public void setTimeRange(Date start, Date end) {
        this.timeRangeStart = start;
        this.timeRangeEnd = end;
    }

    // TODO testme

    /**
     * @param start Start of Range
     * @param end End of Range
     * @param expandOrLimit Set mode to EXPAND or LIMIT, values can be {@link CalendarData#EXPAND}
     *     or {@link CalendarData#LIMIT}
     */
    public void setRecurrenceSet(String start, String end, Integer expandOrLimit) {
        if (UrlUtils.isNotBlank(start)) {
            try {
                this.recurrenceSetStart = parseTime(start);
            } catch (CalDAV4JException e) {
                // TODO write a log class
                e.printStackTrace();
            }
        }
        if (UrlUtils.isNotBlank(end)) {
            try {
                this.recurrenceSetEnd = parseTime(end);
            } catch (CalDAV4JException e) {
                // TODO write a log class
                e.printStackTrace();
            }
        }

        switch (expandOrLimit) {
            case 1:
            case 0:
                this.expandOrLimit = expandOrLimit;
                break;
            default:
                // TODO error validating
                break;
        }
    }

    /**
     * @param query Query to be parsed
     * @return Return the xml query as string
     * @throws DOMValidationException on exception
     */
    public static String printQuery(CalendarQuery query) throws DOMValidationException {
        try {
            query.validate();
            Document doc = query.createNewDocument();
            return XMLUtils.toPrettyXML(doc);

        } catch (DOMException e) {
            throw new DOMValidationException(e.getMessage(), e);
        }
    }

    /**
     * Parses a string to a Date using the following syntax:
     *
     * <ul>
     *   <li>null or "" to null
     *   <li>parsable to Date(parsable)
     *   <li>NOW to Date(true) UTC
     * </ul>
     *
     * @param time Time String as specified above
     * @return Date(time)
     * @throws CalDAV4JException on parse errors
     */
    private Date parseTime(String time) throws CalDAV4JException {
        if (time != null && !"".equals(time)) {
            if ("NOW".equals(time)) {
                return new DateTime(true);
            } else {
                try {
                    if (time.length() > 8) {
                        return new DateTime(time);
                    } else {
                        return new Date(time);
                    }

                } catch (ParseException e) {
                    throw new CalDAV4JException("Unparsable date format in query:" + time, e);
                }
            }
        }

        return null;
    }
}
//
