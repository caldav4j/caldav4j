/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar.caldav;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Calendar.Attendees;
import android.provider.Calendar.CalendarAlerts;
import android.text.TextUtils;
import android.util.Log;
import android.os.Messenger;

import java.util.HashMap;
import org.apache.http.HttpStatus;
import android.provider.Calendar;
import android.provider.Calendar.Calendars;
import android.provider.Calendar.Events;
import android.text.format.Time;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import net.fortuna.ical4j.model.component.VEvent; 
import org.osaf.caldav4j.util.ICalendarUtils;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.DateTime;

/**
 * This service is used to handle calendar event reminders.
 */
public class CalDAVService extends Service {
    static final boolean DEBUG = true;
    private static final String TAG = "CalDAVService";

    public static final int MSG_SETUP_ACC = 0;
    public static final int MSG_SYNC = 1;
    public static final int MSG_REMOVE = 2;

    public static final int ERR_SETUP_INVALID_ARGS = 1;
    public static final int ERR_SETUP_PROVIDER = 2;
    public static final int ERR_SETUP_CONN = 3;
    public static final int ERR_SETUP_SERVER = 4;
    public static final int ERR_SETUP_CALDAV = 5;
    public static final int ERR_SETUP_ACC_EXISTS = 6;

    public static final String ERR_INVALID_ARGS_STR = "Invliad Information";
    public static final String ERR_PROVIDER_STR = "Provider Error";
    public static final String ERR_CONN_STR = "Connection Error";
    public static final String ERR_SERVER_STR = "Server Error";
    public static final String ERR_NO_CAL_STR = "No Such Calendar, Sir!";
    public static final String ERR_NO_CALENDARS_TO_SYNC = "None Setup, Sir!";
    public static final String ERR_NO_CALENDARS_TO_REMOVE = "None Exist, Sir!";
	public static final String ERR_CALDAV_STR = "CalDAV Error";
	public static final String ERR_ACC_EXISTS_STR = "Thats already setup, Sir";

    public static final int ERR_SYNC_NO_CALENDAR = 11;
    public static final int ERR_SYNC_PROVIDER = 12;
    public static final int ERR_SYNC_CONN = 13;
    public static final int ERR_SYNC_SERVER = 14;
    public static final int ERR_SYNC_NO_CALENDARS = 15;

    public static final int ERR_REM_NO_CALENDARS = 20;

    public static final String CAL_NAME = "name";
    public static final String CAL_USER = "user";
    public static final String CAL_PSWD = "pswd";
    public static final String CAL_HOST = "host";
    public static final String CAL_PROT = "prot";
    public static final String CAL_PORT = "port";
    public static final String CAL_HOME = "home";
    public static final String CAL_COLL = "coll";

    private static final Uri EVENTS_URI = asSyncAdapter (Events.CONTENT_URI);

    public static final String CALDAV_ACC_TYPE = "com.android.calendar.caldav";
    private static Context mContext = null;
    public static Context getContext () {
        //ugly hack so ical4j can read resources!!
        return mContext;
    }
    class IncomingHandler extends Handler {
        public IncomingHandler (Looper looper) {
            super (looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SETUP_ACC:
                    setupAcc (msg);
                    break;
                case MSG_SYNC:
                    sync (msg);
                    break;
                case MSG_REMOVE:
                    remove (msg);

                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    //final Messenger mMessenger = new Messenger(new IncomingHandler());
    Messenger mMessenger = null;
    ContentResolver mCR = null;
    private volatile Looper mServiceLooper;
    private volatile IncomingHandler mServiceHandler;
    
    @Override
    public void onCreate() {
        mCR = getContentResolver ();
        // Check if there are any CalDAV accounts setup.
        // Check if the CalDAV calendars are present in the provider DB. If not, create.
        // Start Sync for all CalDAV calendars.
        mContext = this;
        HandlerThread thread = new HandlerThread("CalDAVService",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new IncomingHandler(mServiceLooper);
        mMessenger = new Messenger(mServiceHandler);

    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit ();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return mMessenger.getBinder();
    }

    private void setupAcc (Message msg) {
        Log.d (TAG, "setupAcc");
        // validate args
        Bundle bndl = (Bundle)msg.obj; 
        String calname  = null;
        String username = null;
        String password = null;
        String host     = null;
    	String protocol = null;
    	String home     = null;
    	String coll     = null;
    	int port = -1;
    	int err = 0;
        int suberr = 0;
		String errstr = null;
    
    	if (bndl != null ) {
        	calname  = bndl.getString (CAL_NAME);
        	username = bndl.getString (CAL_USER);
        	password = bndl.getString (CAL_PSWD);
        	host     = bndl.getString (CAL_HOST);
        	protocol = bndl.getString (CAL_PROT);
        	port     = bndl.getInt    (CAL_PORT);
        	home     = bndl.getString (CAL_HOME);
        	coll     = bndl.getString (CAL_COLL);
    	}
    	if (calname == null || username == null || password == null || host == null ||
    		protocol == null || port <= 0 || home == null || coll == null) {
    		err = ERR_SETUP_INVALID_ARGS;
    	}
    	Log.d (TAG, "Setting up CalDAV acc name " + calname + " user " + username + 
                " password " + password + " host " + host + " protocol " + protocol + 
                " port " + port + " home " + home + " coll " + coll);
    
        Account acc = new Account (this, calname, username, password, host, protocol, home, coll, port);
        if (acc == null || Preferences.getPreferences (this).accountExists (acc)) {
            err = ERR_SETUP_ACC_EXISTS;
        }
        //TODO add a system account

    	// test connection
        CalDAV4jIf caldavif = null;
		if (err == 0) {
            caldavif = new CalDAV4jIf (getAssets ());
			try {
                caldavif.setCredentials ( new CaldavCredential (protocol, host, port, home, coll, username, password));
                suberr = caldavif.testConnection ();
				if (suberr != HttpStatus.SC_OK) {
					err = ERR_SETUP_CONN;
				} else
                    suberr = 0;
			} catch (Exception e) {
				e.printStackTrace ();
                err = ERR_SETUP_CONN;
				errstr = e.toString ();	
			}
		}

        long calid = -1;
        if (err == 0) {
            // store the details persistently.
            acc.save (Preferences.getPreferences (this));

            // add calendar to provider DB
            calid = createCalendar (acc);
            if (calid < 0)
                err = ERR_SETUP_PROVIDER;
        }

        if (err == 0)
            try {
                doSyncCalendar (acc, calid);
            } catch (Exception e) {
                e.printStackTrace ();
                err = ERR_SYNC_SERVER;
                errstr = e.toString ();
            }


    	try {
			if (err != 0 && errstr == null)
				errstr = errString (err);
    		msg.replyTo.send (Message.obtain(null, MSG_SETUP_ACC, err, suberr, errstr));
    	} catch (Exception e) {
        	e.printStackTrace ();
    	}
    }

    private void sync (Message msg) {
		Log.d (TAG, "sync");
		// retrieve caldav accounts

        int err = 0;
        String errstr = null;
        Account [] accs = Preferences.getPreferences (this).getAccounts ();

        try {
            for (Account a : accs) {
                Log.d (TAG, "now syncing " + a.getUrl ());
                msg.replyTo.send (Message.obtain(null, MSG_SYNC, 0, a.getAccountNumber () + 1, null));
                err = doSyncCalendar (a);
            }
        } catch (Exception e) {
            e.printStackTrace ();
            errstr = e.toString ();
            err = ERR_SYNC_SERVER;
        }
        if (accs.length == 0) {
            err = ERR_SYNC_NO_CALENDARS;
            errstr = errString (err);
        }

		try {
			msg.replyTo.send (Message.obtain(null, MSG_SYNC, err, 0, errstr));
		} catch (Exception e) {
	    	e.printStackTrace ();
		}
    }
    private void remove (Message msg) {
		Log.d (TAG, "remove");
		// retrieve caldav accounts

        int err = 0;
        String errstr = null;
        Preferences prefs = Preferences.getPreferences (this);
        Account [] accs = prefs.getAccounts ();

        try {
            for (Account a : accs) {
                Log.d (TAG, "now removing " + a.getUrl ());
                msg.replyTo.send (Message.obtain(null, MSG_REMOVE, 0, a.getAccountNumber () + 1, null));
                err = doRemoveCalendar (a);
                a.delete (prefs);

            }
        } catch (Exception e) {
            e.printStackTrace ();
            errstr = e.toString ();
            err = ERR_SYNC_SERVER;
        }
        if (accs.length == 0) {
            err = ERR_REM_NO_CALENDARS;
            errstr = errString (err);
        }

		try {
			msg.replyTo.send (Message.obtain(null, MSG_REMOVE, err, 0, errstr));
		} catch (Exception e) {
	    	e.printStackTrace ();
		}
    }
	public String errString (int err) { 
		switch (err) {
			case ERR_SETUP_INVALID_ARGS:
				return ERR_INVALID_ARGS_STR;
			case ERR_SETUP_PROVIDER:
			case ERR_SYNC_PROVIDER:
				return ERR_SERVER_STR;
			case ERR_SETUP_CONN:
			case ERR_SYNC_CONN:
				return ERR_CONN_STR;
			case ERR_SETUP_SERVER:
			case ERR_SYNC_SERVER:
				return ERR_SERVER_STR;
			case ERR_SETUP_CALDAV:
				return ERR_CALDAV_STR;
			case ERR_SETUP_ACC_EXISTS:
				return ERR_ACC_EXISTS_STR;
            case ERR_SYNC_NO_CALENDAR:
                return ERR_NO_CAL_STR;
            case ERR_SYNC_NO_CALENDARS:
                return ERR_NO_CALENDARS_TO_SYNC;
            case ERR_REM_NO_CALENDARS:
                return ERR_NO_CALENDARS_TO_REMOVE;
		}
		return null;
	}
    private int doRemoveCalendar (Account acc) throws Exception {
        long calid = getCalendarId (acc);
        if (calid < 0)
            return -ERR_SYNC_PROVIDER;
        doRemoveCalendar (acc, calid);
        return 0;
    }

    private void doRemoveCalendar (Account acc, long calid) throws Exception {
        mCR.delete (ContentUris.withAppendedId (asSyncAdapter (Calendars.CONTENT_URI), calid), null, null);
    }

    private void doSyncCalendar (Account acc, long calid) throws Exception {
        // Get a list of local events
        String [] evproj1 = new String [] {Events._ID, Events._SYNC_ID, Events.DELETED, Events._SYNC_DIRTY};
        HashMap<String, Long> localevs =   new HashMap<String, Long>();
        HashMap<String, Long> removedevs = new HashMap<String, Long>();
        HashMap<String, Long> dirtyevs =   new HashMap<String, Long>();
        HashMap<String, Long> newdevevs =   new HashMap<String, Long>();

        Cursor c = mCR.query (EVENTS_URI, evproj1, Events.CALENDAR_ID + "=" + calid, null, null);

        long tid;
        String tuid = null;
        if (c.moveToFirst ()) {
            do {
                tid = c.getLong (0);
                tuid = c.getString (1);

                if (c.getInt (2) != 0)
                    removedevs.put (tuid, tid);
                else if (tuid == null) { 
                    // generate a UUID
                    tuid = UUID.randomUUID().toString ();
                    newdevevs.put (tuid, tid);
                }else if (c.getInt (3) != 0)
                    dirtyevs.put (tuid, tid);
                else
                    localevs.put (tuid, tid);
            }while (c.moveToNext ());
            c.close ();
        }

        CalDAV4jIf caldavif = new CalDAV4jIf (getAssets ());
        caldavif.setCredentials ( new CaldavCredential (acc.getProtocol (), acc.getHost (), acc.getPort (),
                    acc.getHome (), acc.getCollection (), acc.getUser(), acc.getPassword ()));

        //add new device events to server
        for (String uid: newdevevs.keySet ())
            addEventOnServer (uid, newdevevs.get (uid), caldavif);
        //delete the locally removed events on server
        for (String uid: removedevs.keySet ()) {
            removeEventOnServer (uid, caldavif);
            // clean up provider DB?
            removeLocalEvent (removedevs.get (uid));
        }

        //update the dirty events on server
        for (String uid: dirtyevs.keySet ())
            updateEventOnServer (uid, dirtyevs.get (uid), caldavif);

        // Get events from server
        VEvent [] evs = caldavif.getEvents ();

        // add/update to provider DB
        String [] evproj = new String [] {Events._ID};
        ContentValues cv = new ContentValues ();
        String temp, durstr = null;
        for (VEvent v : evs) {
            cv.clear ();
            durstr = null;

            String uid = ICalendarUtils.getUIDValue (v);
            // XXX Some times the server seem to return the deleted event if we do get events immediately
            // after removing..
            // So ignore the possibility of deleted event on server was modified on server, for now.
            if (removedevs.containsKey (uid))
                continue;

            //TODO: put etag here
            cv.put (Events._SYNC_ID,    uid);
            //UUID
            cv.put (Events._SYNC_DATA,  uid);

            cv.put (Events.CALENDAR_ID, calid);
            cv.put (Events.TITLE,       ICalendarUtils.getSummaryValue (v));
            cv.put (Events.DESCRIPTION, ICalendarUtils.getPropertyValue (v,Property.DESCRIPTION));
            cv.put (Events.EVENT_LOCATION,    ICalendarUtils.getPropertyValue (v,Property.LOCATION));
            String tzid = ICalendarUtils.getPropertyValue (v, Property.TZID);
            if (tzid == null)
                tzid = Time.getCurrentTimezone ();
            cv.put (Events.EVENT_TIMEZONE, tzid);
            long dtstart = parseDateTimeToMillis (ICalendarUtils.getPropertyValue (v,
                            Property.DTSTART), tzid);
            cv.put (Events.DTSTART,    dtstart); 

            temp =  ICalendarUtils.getPropertyValue (v, Property.DTEND);
            if (temp != null)
                cv.put (Events.DTEND,       parseDateTimeToMillis (temp, tzid));
            else {
                temp =  ICalendarUtils.getPropertyValue (v, Property.DURATION);
                durstr = temp;
                if (temp != null) {
                    cv.put (Events.DURATION, durstr);

                    // We still need to calculate and enter DTEND. Otherwise, the Android is not displaying
                    // the event properly
                    Duration dur = new Duration ();
                    dur.parse (temp);
                    cv.put (Events.DTEND,       dtstart + dur.getMillis ());
                }
            }

            //TODO add more fields

            //if the event is already present, update it otherwise insert it
            // TODO find if something changed on server using etag
            Uri euri;
            if (localevs.containsKey (uid) || dirtyevs.containsKey (uid)) {
                if (localevs.containsKey (uid)) {
                    tid = localevs.get (uid);
                    localevs.remove (uid);
                } else {
                    tid = dirtyevs.get (uid);
                    dirtyevs.remove (uid);
                }

                mCR.update (ContentUris.withAppendedId (EVENTS_URI, tid), cv, null, null);
                //clear sync dirty flag
                cv.clear ();
                cv.put (Events._SYNC_DIRTY, 0);
                mCR.update (ContentUris.withAppendedId (EVENTS_URI, tid), cv, null, null);
                Log.d (TAG, "Updated " + uid);
            } else if (!newdevevs.containsKey (uid)){
                euri = mCR.insert (EVENTS_URI, cv);
                Log.d (TAG, "Inserted " + uid);
            }
        }
        // the remaining events in local and dirty event list are no longer on the server. So remove them.
        for (String uid: localevs.keySet ())
            removeLocalEvent (localevs.get (uid));

        //XXX Is this possible?
        /*
        for (String uid: dirtyevs.keySet ())
            removeLocalEvent (dirtyevs[uid]);
         */
    }
    //syncs one calendar
    private int doSyncCalendar (Account acc) throws Exception {
        // Make sure this calendar exists in provider DB.
        long calid = getCalendarId (acc);
        if (calid < 0)
            return -ERR_SYNC_PROVIDER;
        doSyncCalendar (acc, calid);
        return 0;
    }
    //public long createCalendar(EasSyncService service, Account account, Mailbox mailbox) {
    private long createCalendar(Account acc) {
        String name = acc.getName (), account = acc.getEmail (), collection = acc.getCollection (), url =
            acc.getUrl ();
        // Create a Calendar object
        ContentValues cv = new ContentValues();
        cv.put(Calendars.NAME, name);
        cv.put(Calendars.DISPLAY_NAME, name);
        cv.put(Calendars.URL, url);
        cv.put(Calendars._SYNC_ACCOUNT, account);
        cv.put(Calendars._SYNC_ACCOUNT_TYPE, CALDAV_ACC_TYPE);
        cv.put(Calendars._SYNC_ID, collection);
        cv.put(Calendars.SYNC_EVENTS, 1);
        cv.put(Calendars.SELECTED, 0);
        cv.put(Calendars.HIDDEN, 0);
        cv.put(Calendars.COLOR, 0xFF9d50a4);
        cv.put(Calendars.SELECTED, 1);
        // Don't show attendee status if we're the organizer
        cv.put(Calendars.ORGANIZER_CAN_RESPOND, 0);

        cv.put(Calendars.TIMEZONE, Time.getCurrentTimezone());
        cv.put(Calendars.ACCESS_LEVEL, Calendars.OWNER_ACCESS);
        cv.put(Calendars.OWNER_ACCOUNT, account);

        Uri uri = mCR.insert(Calendars.CONTENT_URI, cv);
        if (uri != null) {
            String stringId = uri.getPathSegments().get(1);
            return Long.parseLong(stringId);
        }
        return -1;
    }
    private long getCalendarId (Account acc) {
        String [] calproj = {Calendars._ID};
        String calacc = acc.getEmail ();
        Cursor c = mCR.query (Calendars.CONTENT_URI, calproj, Calendars._SYNC_ACCOUNT +
                "='" + calacc + "' AND " + Calendars._SYNC_ID + "='"+acc.getCollection () + "'", null, null);

        long calid;
        if (c == null || !c.moveToFirst ()) {
            calid = createCalendar (acc);
        } else {
            calid = c.getLong (0);
        }
        c.close ();
        return calid;
    }
    private long parseDateTimeToMillis (String date, String tzid) {
        GregorianCalendar cal = new GregorianCalendar(Integer.parseInt(date.substring(0, 4)),
                Integer.parseInt(date.substring(4, 6)) - 1, Integer.parseInt(date.substring(6, 8)),
                Integer.parseInt(date.substring(9, 11)), Integer.parseInt(date.substring(11, 13)),
                Integer.parseInt(date.substring(13, 15)));
        TimeZone tz = TimeZone.getTimeZone (tzid);
        cal.setTimeZone(tz);
        return cal.getTimeInMillis ();
    }

    private void removeEventOnServer (String uid, CalDAV4jIf caldavif) throws Exception {
        caldavif.removeEv (uid);
    }
    private void updateEventOnServer (String uid, long id, CalDAV4jIf caldavif) throws Exception {
        VEvent ve = getEvFromDB (uid, id);
        if (ve != null) {
            caldavif.updateEv (ve);

            //update DB to clear dirty
            ContentValues cv = new ContentValues ();
            cv.put (Events._SYNC_DIRTY, 0);
            mCR.update (ContentUris.withAppendedId (EVENTS_URI, id), cv, null, null);
        } else
            //XXX this should never happen
            Log.e (TAG, "Could not find event with uid " + uid);

    }
    private VEvent getEvFromDB (String uid, long id) {
        String [] proj = {Events.TITLE, //0
                          Events.DESCRIPTION, //1
                          Events.DTSTART, //2 
                          Events.DTEND, //3
                          Events.EVENT_TIMEZONE, //4
                          Events.DURATION, //5
                          Events.EVENT_LOCATION}; //6

        Cursor c = mCR.query (ContentUris.withAppendedId (EVENTS_URI, id), proj,null, null, null);
        if (c.moveToFirst ()) {
            VEvent ve = new VEvent ();
            ve.getProperties ().add (new Summary (c.getString (0)));
            ve.getProperties ().add (new Description (c.getString (1)));
            ve.getProperties ().add (new DtStart (new DateTime (c.getLong (2))));
            ve.getProperties ().add (new TzId (c.getString (4)));
            ve.getProperties ().add (new Location (c.getString (6)));

            long tl = c.getLong (3);
            if (tl > 0) {
                ve.getProperties ().add (new DtEnd   (new DateTime (tl)));
                Log.d (TAG, "dt end " + tl);
            }else {
                String ts = c.getString (4);
                Log.d (TAG, "dur " + ts);
                if (ts != null) {
                    net.fortuna.ical4j.model.property.Duration dur =  new net.fortuna.ical4j.model.property.Duration ();
                    dur.setValue (ts);
                    ve.getProperties ().add (dur);
                }
            }
            ve.getProperties ().add (new Uid (uid));
            return ve;
        } else
            return null;
    }
    // merge with update func
    private void addEventOnServer (String uid, long id, CalDAV4jIf caldavif) throws Exception {
        VEvent ve = getEvFromDB (uid, id);
        if (ve != null) {
            caldavif.addEv (ve);

            //update DB to clear dirty
            ContentValues cv = new ContentValues ();
            cv.put (Events._SYNC_DIRTY, 0);
            cv.put (Events._SYNC_ID, uid);
            mCR.update (ContentUris.withAppendedId (EVENTS_URI, id), cv, null, null);
        } else
            //XXX this should never happen
            Log.e (TAG, "Could not find event with uid " + uid);
    }
    private void removeLocalEvent (long id) {
        mCR.delete (ContentUris.withAppendedId (EVENTS_URI, id), null, null);
        Log.d (TAG, "removed ev " + id + " which got deleted on server");
    }
    private static Uri asSyncAdapter (Uri uri) {
        return uri.buildUpon().appendQueryParameter (Calendar.CALLER_IS_SYNCADAPTER, "true").build ();
    }
}
