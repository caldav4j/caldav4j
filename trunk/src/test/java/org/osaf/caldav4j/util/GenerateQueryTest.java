package org.osaf.caldav4j.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.fortuna.ical4j.model.Component;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.DOMValidationException;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.CalDAVReportMethod;
import org.osaf.caldav4j.methods.PutGetTest;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.util.GenerateQuery;
import org.osaf.caldav4j.util.XMLUtils;
import org.w3c.dom.Document;

public class GenerateQueryTest extends BaseTestCase {
    private static final Log log = LogFactory.getLog(PutGetTest.class);
    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
    
	
	
	public GenerateQueryTest() {
		super();
		// TODO Auto-generated constructor stub
	}
    protected void setUp() throws Exception {    	
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();

    }
    private CalDAVReportMethod reportMethod = methodFactory.createCalDAVReportMethod();


    private String printQuery(CalendarQuery query) {			
        try {
    		query.validate();
        	Document doc = query.createNewDocument(XMLUtils
                    .getDOMImplementation());
			return XMLUtils.toPrettyXML(doc);
        	
        } catch (DOMValidationException domve) {
            log.error("Error trying to create DOM from CalDAVReportRequest: ", domve);
            throw new RuntimeException(domve);
        } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
		
    }
    
    
    /**
     * basic VEVENT CompFilter
     */
    public void testFilter_VEVENT() {
		try {    	
			log.info("Filter: VEVENT");
			GenerateQuery gq = new GenerateQuery();		
			gq.setFilter("VEVENT");
			

			log.info(printQuery(gq.generateQuery()));
			
			// and now test the constructor
			gq = new GenerateQuery(null,Component.VEVENT);		
			
			log.info(printQuery(gq.generateQuery()));
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

    }
    public void testComp_VEVENT() {

		try {
			log.info("Comp: VEVENT");
			GenerateQuery gq = new GenerateQuery();		
			gq.setComponent(Component.VEVENT);
	
			log.info("Set component:\n" + printQuery(gq.generateQuery()));
			
			// and now test the constructor
			final String VEVENT_PROPERTIES_BY_TIMERANGE_F = "VEVENT [20060104T000000Z;20060105T000000Z]";
			final String VEVENT_PROPERTIES_BY_TIMERANGE_C = "VEVENT : UID,DTSTART,DTEND,RRULE,RDATE,DURATION";
			gq = new GenerateQuery(VEVENT_PROPERTIES_BY_TIMERANGE_C, VEVENT_PROPERTIES_BY_TIMERANGE_F);		
		
			log.info("Constructor:\n" + printQuery(gq.generateQuery()));
			
			gq.setNoCalendarData(true);
			log.info("no calendar-data:\n" + printQuery(gq.generateQuery()));			
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

    }    
    // Creating Calendar-Query like the RFC's one

    public void testQuery_TODO()  {
		try {
			List<String> a = new ArrayList<String> ();
			a.add("STATUS!=CANCELLED");
			a.add("COMPLETED==UNDEF");
			a.add("DTSTART==[;20080810]");
			
	
			GenerateQuery gq = new GenerateQuery();	
			gq.setFilter("VTODO", a);

			log.info(printQuery(gq.generateQuery()));
			
			// and now the constructor
			String fquery = "VTODO : STATUS!=CANCELLED , COMPLETED==UNDEF , DTSTART==[;20080810]";
			gq = new GenerateQuery(null, fquery);	

			log.info(printQuery(gq.generateQuery()));
			
			// and now the constructor
			fquery = "VTODO [20060106T100000Z;20060106T100000Z]: STATUS!=CANCELLED , COMPLETED==UNDEF , DTSTART==[;20080810]";
			gq = new GenerateQuery(null, fquery);	

			log.info(printQuery(gq.generateQuery()));
			
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }

    
    public void testQuery_ATTENDEE()  {
		try {
			log.info("VEVENT + ATTENDEE:");
			List<String> a = new ArrayList<String> ();
			a.add("ATTENDEE==mailto:lisa@example.com");
			
			GenerateQuery gq = new GenerateQuery();
			gq.setFilter("VEVENT", a);
		
			log.info("setFilter()"+printQuery(gq.generateQuery()));
			
			gq = new GenerateQuery(null,"VEVENT : ATTENDEE==mailto:lisa@example.com");	
		
			log.info("Constructor:"+printQuery(gq.generateQuery()));			
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
    }

    /**
     * queries for nested components: there's no syntax for this, 
     *  so you have to use a bit of magic 
     * VTODO : VALARM 
     */
    public void testQuery_VALARM()  {
		try {
			CalendarQuery compoundQuery = null;
			
			// create the external comp-filter
			GenerateQuery gq = new GenerateQuery();	
			gq.setFilter("VTODO", null);
			compoundQuery = gq.generateQuery();
			log.info(printQuery(gq.generateQuery()));
			
			// add the inner filter (VALARM in time-range) to the VTODO one
			gq = new GenerateQuery(null, "VALARM [20060106T100000Z;20060106T170000Z]");
			compoundQuery.getCompFilter().getCompFilters().get(0).addCompFilter(
					gq.generateQuery().getCompFilter().getCompFilters().get(0)
					);
			
			log.info(printQuery(compoundQuery));
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    public void testFilterProperties()  {
		try {
			List<String> a = new ArrayList<String> ();
			a.add("UID==DSDDAS123-D32423-42332-dasdsafwe");
			a.add("X-PLUTO-SPADA!=fsdfsdfds");
			a.add("SUMMARY!=CDSDafsd");
			a.add("DESCRIPTION==UNDEF");
			// a.add("DTSTART==[13082008,14082008]");
			
	
			GenerateQuery gq = new GenerateQuery();	
			gq.setFilter("VEVENT", a);

			log.info(printQuery(gq.generateQuery()));
			
			String fquery = "VEVENT:UID==DSDDAS123-D32423-42332-dasdsafwe" 
				+ ",X-PLUTO-SPADA!=fsdfsdfds"
				+ ",SUMMARY!=CDSDafsd"
				+ ",DESCRIPTION==UNDEF";
				
			gq = new GenerateQuery(null, fquery);	
			log.info(printQuery(gq.generateQuery()));
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
}
