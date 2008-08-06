package org.osaf.caldav4j.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osaf.caldav4j.BaseTestCase;
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


    private void printQuery(CalendarQuery query) {
    	try {
			//query.validate();
			
	        Document doc = null;
	        try {
	            doc = query.createNewDocument(XMLUtils
	                    .getDOMImplementation());
	        } catch (DOMValidationException domve) {
	            log.error("Error trying to create DOM from CalDAVReportRequest: ", domve);
	            throw new RuntimeException(domve);
	        }

			System.out.println( XMLUtils.toPrettyXML(doc));
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
    
    public void testQuery_VEVENT() {
    	
		GenerateQuery gq = new GenerateQuery();
		
		gq.setFilter("VEVENT");
		try {
			printQuery(gq.generateQuery());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    // Creating Calendar-Query like the RFC's one

    public void testQuery_TODO()  {
		List<String> a = new ArrayList<String> ();
		a.add("STATUS!=CANCELLED");
		a.add("COMPLETED==UNDEF");
		a.add("DTSTART==[,20080810]");
		

		GenerateQuery gq = new GenerateQuery();	
		gq.setFilter("VTODO", a);
		try {
			printQuery(gq.generateQuery());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    public void testQuery_ATTENDEE()  {
		List<String> a = new ArrayList<String> ();
		a.add("ATTENDEE==mailto:lisa@example.com");
		
		GenerateQuery gq = new GenerateQuery();	
		gq.setFilter("VEVENT", a);
		
		try {
			printQuery(gq.generateQuery());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

    public void testQuery_VALARM()  {
		List<String> a = new ArrayList<String> ();
		a.add("VALARM==[20060106T100000Z,20060106T170000Z]");
		
		GenerateQuery gq = new GenerateQuery();	
		gq.setFilter("VTODO", a);
		try {
			printQuery(gq.generateQuery());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    public void testFilterProperties()  {
		List<String> a = new ArrayList<String> ();
		a.add("UID==DSDDAS123-D32423-42332-dasdsafwe");
		a.add("X-PLUTO-SPADA!=fsdfsdfds");
		a.add("SUMMARY!=CDSDafsd");
		a.add("DESCRIPTION==UNDEF");
		// a.add("DTSTART==[13082008,14082008]");
		

		GenerateQuery gq = new GenerateQuery();	
		gq.setFilter("VEVENT", a);
		try {
			printQuery(gq.generateQuery());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
}
