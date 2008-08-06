package org.osaf.caldav4j.example;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.httpclient.HttpException;
import org.osaf.caldav4j.methods.DeleteMethod;
import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.CalDAVCalendarCollection;
import org.osaf.caldav4j.CaldavCredential;
import org.osaf.caldav4j.ResourceNotFoundException;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.GetMethod;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.util.CalendarComparator;
import org.osaf.caldav4j.util.ICalendarUtils;
import org.osaf.caldav4j.util.CaldavStatus;;


/**
 * this class binds a CalendarCollection (a folder of events) to a Caldav Client
 * giving a complete Caldav Browser
 * @author rpolli@babel.it
 *
 */
public class CalDavCollectionManager extends CalDAVCalendarCollection {

	private BaseCaldavClient client = null;
	private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

	
	/**
	 * associates a CalendarCollection to a BaseCalDavClient, 
	 * pointing to a default folder
	 */
	public CalDavCollectionManager(BaseCaldavClient c){
		super();
		client=c;

    	setHostConfiguration(c.hostConfig);
    	setMethodFactory(methodFactory);
    	
		setCalendarCollectionRoot();
	}
	
	// BaseCaldavClient Wrappers
	public String getUsername() {
		return client.getCalDavSeverUsername();
	}
	
    public void deletePath(String path)
    	 {
        DeleteMethod delete = new DeleteMethod(path);
        try {
			client.executeMethod(getHostConfiguration(), delete);
		} catch (HttpException e) {
			// TODO catch and throw a Caldav4jException
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    /**
     * assumes that URL is function of uid
     * @param uid
     * @throws CalDAV4JException
     */
    public void deleteComponentByUid(String uid) 
    	throws CalDAV4JException {
	    	if ((uid!=null)  && (! "".equals(uid))){
	    		deletePath(getCalendarCollectionRoot()+"/" +uid+".ics");
	    		return;
	    	}
	    	throw new CalDAV4JException("Item not found"+getCalendarCollectionRoot()+"/" +uid+".ics");
    }
    
    /**
     * crea una nuova cartella in path
     * @param path
     * @return 0 if ok statusCode on error
     * @throws Exception
     */
    public int mkDirectory(String path){
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath(path);
       
        try {
			client.executeMethod(getHostConfiguration(), mk);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int statusCode = mk.getStatusCode();
        return  ( statusCode == CaldavStatus.SC_CREATED)  ? 0 : statusCode;
       
    }
        
    /**
     * list file/folders 
     * @param path
     * @return
     * @throws IOException 
     */
    public  int listCalendar(String path) throws IOException {
        //now let's try and get it, make sure it's there
        GetMethod get = new GetMethod();
        get.setPath(path);
        
        try {
			client.executeMethod(getHostConfiguration(), get);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println( "listCalendar()" +get.getResponseBodyAsString() );

        int statusCode = get.getStatusCode();
        return  ( statusCode == CaldavStatus.SC_OK)  ? 0 : statusCode;
        
    }    
    

  
    /** 
     * @param beginDate
     * @param endDate
     * @return a collection of events in the given time-interval
     * @throws CalDAV4JException 
     */
    public List<Calendar> getEventResources(Date beginDate, Date endDate)
    	throws CalDAV4JException
    {
    	CalDAV4JMethodFactory mf = new CalDAV4JMethodFactory();
		setMethodFactory(mf);

		return getEventResources(client, beginDate, endDate);
    }

    /**
     * @param y year
     * @param m month
     * @return a list of event in the given month/year
     * @throws CalDAV4JException 
     */
    public List<Calendar> getEventResourcesByMonth(int y, int m) throws CalDAV4JException {
    	Date beginDate = ICalendarUtils.createDateTime(y, m, 0, null, true); // uses no timezone, and UTC
    	
    	GregorianCalendar c = new GregorianCalendar();
    	c.setTime(beginDate);
    	c.add(GregorianCalendar.MONTH, 1);
    	
    	Date endDate = ICalendarUtils.createDateTime(c.get(GregorianCalendar.YEAR),c.get(GregorianCalendar.MONTH), c.get(GregorianCalendar.DATE),null, true);
    	
    	return getEventResources(beginDate, endDate);
    }
    
    /** sort a Calendar list by date
     * 
     * @param calendars
     * @return
     */
    public List<Calendar> sortByStartDate(List<Calendar> calendars) {
    	Collections.sort(calendars, new CalendarComparator());
    	return calendars;
    	
    }
    /**
     * 
     * @return VEvent marked with an UID if success, null on error
     * TODO should throw more exceptions
     * @throws CalDAV4JException 
     */
    public VEvent addEvent(VEvent ve, VTimeZone vtz)
    	throws CalDAV4JException  
    {

		if (ve.getProperty(Property.UID) == null) {
			Uid uid = new Uid( new DateTime().toString()
					+"-" + UUID.randomUUID().toString()
					+"-" + getUsername() );
			
			ve.getProperties().add(uid);
		}

		addEvent(client, ve, null);
		return ve;

    }
    /**
     * modify an event with the same Uid of the given one
     * @param ve
     * @param vtz
     * @return ve on success, null on failure
     * @throws ValidationException 
     */
    public VEvent editEvent(VEvent ve, VTimeZone vtz)
    	throws ValidationException
    {
    	try {
    		ICalendarUtils.addOrReplaceProperty(ve, new DtStamp());
    		ve.validate();
    		updateMasterEvent(client, ve, vtz);
			return ve;
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    /** sets Collection home .. ;) */
    public void setCalendarCollectionRoot(String path) {
    	if (path == null) {
    		path = client.getCalDavSeverWebDAVRoot()+"/"+ getUsername()+"/";
    	} 
    	super.setCalendarCollectionRoot(path);
	}
    public void setCalendarCollectionRoot() {
    	setCalendarCollectionRoot(null);
	}
    public String getCalendarCollectionRoot() {
    	return super.getCalendarCollectionRoot();
    }
    
    public void setRelativePath(String path) {
    	String home = client.getCalDavSeverWebDAVRoot()+"/"+getUsername()+"/";
    	if (path == null) {
    		path = home;
    	} else {
    		home += path;
    	}
    	
    	super.setCalendarCollectionRoot(home);
	}
    
    /**
     * @see super()
     * @param uid
     * @return Calendar if object exists, null if not exists
     * 
     * @throws CalDAV4JException
     */
    public Calendar getCalendarForEventUID(String uid)
    	throws CalDAV4JException, ResourceNotFoundException {
    	return super.getCalendarForEventUID(client, uid);
    }


    
    /**
     * @see super
     * @param propertyName
     * @param beginDate
     * @param endDate
     * @throws CalDAV4JException
     * @Deprecated  {@link getComponentPropertyByTimestamp} 
     */
    public List <String> getEventPropertyByTimestamp(String propertyName, Date beginDate, Date endDate)
    	throws CalDAV4JException 
    {
    	return super.getEventPropertyByTimestamp(client, propertyName, beginDate, endDate);
    }
    
    /**
     * see super
     * @param componentName
     * @param propertyName
     * @param propertyFilter
     * @param beginDate
     * @param endDate
     * @throws CalDAV4JException if can't connect
     */
    public List <String> getComponentPropertyByTimestamp(String componentName, String propertyName, String propertyFilter, Date beginDate, Date endDate)
    	throws CalDAV4JException 
    {
    	return super.getComponentPropertyByTimestamp(client, componentName, propertyName, propertyFilter, beginDate, endDate);
    }
    
	/**
	 * 
	 * @param uid
	 * @return
	 * @throws CalDAV4JException if can't connect
	 * @throws ResourceNotFoundException if can't find object
	 * @see super
	 */
	public String getPathToResourceForEventId(String uid) 
		throws CalDAV4JException, ResourceNotFoundException {
			return  super.getPathToResourceForEventId(client, uid);
	}
	
	
	
	
	
	
	// ******************* test test test ******************
    /**
     * Test method
     * @param args
     * @throws CalDAV4JException
     * @throws SocketException
     * @throws URISyntaxException
     */
		public static void main(String[] args) throws CalDAV4JException, SocketException, URISyntaxException {
			
			final String CALDAV_SERVER = CaldavCredential.CALDAV_SERVER_HOST;
			final String CALDAV_PORT = String.valueOf(CaldavCredential.CALDAV_SERVER_PORT); 
			final String CALDAV_USER = CaldavCredential.CALDAV_SERVER_USERNAME; 
			final String CALDAV_PASS = CaldavCredential.CALDAV_SERVER_PASSWORD;
			
			System.out.println("Creating Caldav Client..");
			BaseCaldavClient cli = new BaseCaldavClient(CALDAV_SERVER, CALDAV_PORT,
					CaldavCredential.CALDAV_SERVER_PROTOCOL,
					CaldavCredential.CALDAV_SERVER_WEBDAV_ROOT, CALDAV_USER, CALDAV_PASS);
			
			System.out.println("Opening a collection..");
        	CalDavCollectionManager cdm = new CalDavCollectionManager(cli);
        	cdm.setRelativePath("events");
        	
        	Random r = new Random();
        	int newDay = r.nextInt(30);
        	
			System.out.println("create a dummy event..");
        	// create date for event
           	Date beginDate = ICalendarUtils.createDateTime(2007, 8, 9, newDay,0, null, true);
    		Date endDate = ICalendarUtils.createDateTime(2007, 8, 7, null, true);
    		Dur duration = new Dur("3H"); 
    		
    		// test for getByTimestamp
    		Date startDTSTART = ICalendarUtils.createDateTime(2007, 5, 7, null, true);
    		Date endDTSTART = ICalendarUtils.createDateTime(2007, 11, 10, null, true);

    		// create new event
        	VEvent nve = new VEvent(beginDate, duration, "My new event");
        	cdm.addEvent(nve, null);
        	
			System.out.println("modify the event..");
        	// modify event
        	Description d = new Description("Caldav4j Event");
        	
        	ParameterList pl = new ParameterList();
        	pl.add(PartStat.ACCEPTED);
        	Attendee invitato = new Attendee(pl, "mailto:rpolli@example.com");
        	
        	
        	ICalendarUtils.addOrReplaceProperty(nve, nve.getUid());
        	ICalendarUtils.addOrReplaceProperty(nve, new Summary("I changed the summary!"));
        	ICalendarUtils.addOrReplaceProperty(nve, invitato);
        	ICalendarUtils.addOrReplaceProperty(nve, invitato);
        	ICalendarUtils.addOrReplaceProperty(nve, d);
        	
        	// re-get event
			System.out.println("Retrieve the event..");
        	Calendar pippo =  cdm.getCalendarForEventUID( ((HttpClient) cdm.client), nve.getUid().getValue());
        	try {
				cdm.editEvent(nve, null);
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("dump the event..\n"+pippo.toString()+"\n");


			List <String> lc = cdm.getEventPropertyByTimestamp( cdm.client, Property.UID, beginDate, endDate);
			for (String cal : lc) {
				System.out.println( "UID="+cal);
			} 
        }
    

    

}