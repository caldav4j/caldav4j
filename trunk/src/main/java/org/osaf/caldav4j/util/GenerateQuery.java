package org.osaf.caldav4j.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern; 
import java.util.regex.Matcher; 

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;

import org.osaf.caldav4j.model.request.CalDAVProp;
import org.osaf.caldav4j.model.request.CalendarData;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.model.request.Comp;
import org.osaf.caldav4j.model.request.CompFilter;
import org.osaf.caldav4j.model.request.Prop;
import org.osaf.caldav4j.model.request.PropFilter;



/** 
 * 
 *Copyright 2008 Roberto Polli
 * this class is an helper for creating Calendar-Query REPORTs
 * 
 * because of the complexity of iCalendar object and relative queries,
 * this class is intended to help the creation of basic queries like
 * get all VEVENT with THOSE attributes in THIS time-range
 * 
 * usage:
 * 
 * QueryGenerator qg = new QueryGenerator();
 * qg.setComponent("VEVENT"); // retrieve the whole VEVENT
 * qg.setComponent("VEVENT", {'UID', 'ATTENDEE', 'DTSTART', 'DTEND'}); // retrieve the whole VEVENT
 * 
 * qg.setFilter("VEVENT"); //request on VEVENT
 * qg.setFilterTimeRange(start,end);
 * 
 * query for property value
 * VEVENT.UID == VALUE (by collation)
 * could be: 
 * { 'UID==VALUE','DTSTART==[a,b]', 'DESCRIPTION==UNDEF'  }
 */

public class GenerateQuery  {
	
	// attributes
	private  String caldavNameSpaceQualifier = "C";
	
	// component attributes
	private String requestedComponent = null; // VEVENT, VTODO
	private List<String> requestedComponentProperties = new ArrayList<String>(); // a list of properties to be retrieved 
		
	// comp flags
	//TODO limit-recurrence-set, limit-freebusy-set, get-etag
	
	//filter (all child of VCALENDAR)
	// TODO how can I manage nested object queries?
	private String filterComponent = null; // VEVENT, VTODO
	private String filterComponentTimeRange = null;
	private List<String> filterComponentProperties = new ArrayList<String>();
	
	/** constructor 
	 * 
	 */
	public GenerateQuery() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * validator
	 * this method should provide at least a basic validation of the calendar-query
	 */
	public boolean validate() {
		return true;
	}
	
	/**
	 * set the component to retrieve: VEVENT, VTODO
	 */
	public void setComponent(String component) {
		this.requestedComponent = component;
	}
	/**
	 * set the component and properties to retrieve: VEVENT, VTODO
	 */
	public void setComponent(String component, List<String> props) {
		if (component != null) {
			this.requestedComponent = component;
			this.requestedComponentProperties = props;
		}
	}
	
	/**
	 * transform the requestedComponentProperties fields in a PropComp value 
	 */
	private Comp getComp() {
		Comp vCalendarComp = new Comp("C");
		vCalendarComp.setName(Calendar.VCALENDAR);
		
		if (requestedComponent != null) {
			Comp vEventComp = new Comp("C");
			vEventComp.setName(requestedComponent);
			
			for (String propertyName : requestedComponentProperties ) {
				// add properties to VCALENDAR.VEVENT
				// TODO a for cycle
				vEventComp.addProp(new CalDAVProp("C", "name", propertyName, false, false)); // @see modification to CalDAVProp
			}
			// add only one component...maybe more ;)
			List <Comp> comps = new ArrayList<Comp> ();
			comps.add(vEventComp);
			vCalendarComp.setComps(comps);

		}
		return vCalendarComp;
	}
	
	
	/**
	 * set the component to filter: VEVENT, VTODO
	 */
	public void setFilter(String filterComponent) {
		this.filterComponent = filterComponent;
	}
	/**
	 * set the component and properties to filter: VEVENT, VTODO
	 */
	public void setFilter(String filterComponent, List<String> props) {
		this.filterComponent = filterComponent;
		this.requestedComponentProperties = props;
	}	
	
	/** 
	 * transform filterComponentProperties in a List of PropFilter
	 * @throws ParseException 
	 */ 
	private List<PropFilter> getPropFilters() 
	throws ParseException {
		List<PropFilter> pf = new ArrayList<PropFilter>();
		Pattern timePattern = Pattern.compile("[(.*),(.*)]");
		Pattern filter = Pattern.compile("(.*?)([!=]=)(.*)");

		
		for (String p : this.filterComponentProperties) {
			String name = null;
			boolean isDefined = true;
			Date timeRangeStart = null, timeRangeEnd = null; 
			Boolean  textmatchcaseless = true;
			String textmatchString = null;
			
			//
			// parse UID==3oij312po3214432 and DESCRIPTION!=Spada
			//
			Matcher str = filter.matcher(p);
			
			if (str.matches() && (str.groupCount()==3 )) {
				name = str.group(1);
				
				Matcher matchTime = timePattern.matcher(str.group(3));
				if (matchTime.matches()) {
					timeRangeStart = new Date(matchTime.group(1));
					timeRangeEnd   = new Date(matchTime.group(2));
				} else if ( "UNDEF".equals(str.group(3)) ) {
					isDefined = false;
				} else {
					textmatchString = str.group(3);
				}
				
				pf.add(new PropFilter(caldavNameSpaceQualifier, name, isDefined,
						timeRangeStart, timeRangeEnd, 
						textmatchcaseless, textmatchString, "!=".equals(str.group(2)), null));
			}
			
		}
		return pf;
	}
	
	/** 
	 * transform filters in a CompFilter
	 */ 
	private CompFilter getFilter() {
		// search for VCALENDAR matching...
		CompFilter vCalendarCompFilter = new CompFilter("C");
		vCalendarCompFilter.setName(Calendar.VCALENDAR);

		CompFilter vEventCompFilter = new CompFilter("C");
		vEventCompFilter.setName(this.filterComponent);

		// TODO check the support from the caldav server. bedework is ok
		// XXX if endDate is undefined, set it one year later
		// set the filter name Property.LAST_MODIFIED
//		if (filterComponentTimeRange != null) {
//			PropFilter pFilter = new PropFilter("C");
//			pFilter.setName(propertyFilter);
//			if (endDate == null) {
//				endDate = new DateTime(beginDate.getTime()+86400*364);
//				((DateTime)endDate).setUtc(true);
//			}
//			pFilter.setTimeRange(beginDate, endDate);
	
//			vEventCompFilter.addPropFilter(pFilter);
//		}
		vCalendarCompFilter.addCompFilter(vEventCompFilter);
		
		return vCalendarCompFilter;

	}
	
	/**
	 * this should parse QueryGenerator attributes
	 * and create the CalendarQuery
	 */
	public CalendarQuery generateQuery() {
//		//first create the calendar query
		CalendarQuery query = new CalendarQuery("C", "D");		
//		
//		query.addProperty(PROP_ETAG);
//
//		// create the query fields 
//		CalendarData calendarData = new CalendarData("C");
//
//		Comp vCalendarComp = new Comp("C");
//		vCalendarComp.setName(Calendar.VCALENDAR);
//		
//		Comp vEventComp = new Comp("C");
//		vEventComp.setName(requestedComponent);
//		
//		// add properties to VCALENDAR.VEVENT
//		// TODO a for cycle
//		vEventComp.addProp(new CalDAVProp("C", "name", propertyName, false, false)); // @see modification to CalDAVProp
//		
//		// add only one component...maybe more ;)
//		List <Comp> comps = new ArrayList<Comp> ();
//		comps.add(vEventComp);
//		vCalendarComp.setComps(comps);
//		calendarData.setComp(vCalendarComp);
//		query.setCalendarDataProp(calendarData);
//
//		// search for events matching...
//		CompFilter vCalendarCompFilter = new CompFilter("C");
//		vCalendarCompFilter.setName(Calendar.VCALENDAR);
//
//		CompFilter vEventCompFilter = new CompFilter("C");
//		vEventCompFilter.setName(requestedComponent);
//
//		// TODO check the support from the caldav server. bedework is ok
//		// XXX if endDate is undefined, set it one year later
//		// set the filter name Property.LAST_MODIFIED
//		PropFilter pFilter = new PropFilter("C");
//		pFilter.setName(propertyFilter);
//		if (endDate == null) {
//			endDate = new DateTime(beginDate.getTime()+86400*364);
//			((DateTime)endDate).setUtc(true);
//		}
//		pFilter.setTimeRange(beginDate, endDate);
//
//		vEventCompFilter.addPropFilter(pFilter);
//		vCalendarCompFilter.addCompFilter(vEventCompFilter);
//		query.setCompFilter(vCalendarCompFilter);

		
		return query;
	}

  

}
// 
