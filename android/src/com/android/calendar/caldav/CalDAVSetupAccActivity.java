/*
 * Copyright (C) 2006 The Android Open Source Project
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

import com.android.calendar.R;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import android.widget.EditText;
import android.widget.Toast;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.Messenger;
import android.os.Message;
import android.os.Handler;
import android.content.ServiceConnection;
import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;

import android.app.ProgressDialog;


public class CalDAVSetupAccActivity extends Activity implements OnClickListener{
    static final String TAG = "CalDAVSetupAccActivity";
    ProgressDialog pg = null;
    Context ctxt = null;
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.setup_caldav_acc);

        Button save_btn = (Button)findViewById(R.id.save);
        save_btn.setOnClickListener(this);
        ctxt = this;
	    doBindService ();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
	doUnbindService ();
        super.onDestroy();
    }
    public void onClick(View v) {
	    // do something when the button is clicked
    	try {
	    switch (v.getId()) {
	    	case R.id.save: {
		        Bundle bndl = new Bundle ();
		        bndl.putString (CalDAVService.CAL_NAME,
			    ((EditText)findViewById(R.id.caldav_calname)).getText().toString().trim() );
		        bndl.putString (CalDAVService.CAL_USER,
			    ((EditText)findViewById(R.id.caldav_caluser)).getText().toString().trim() );
		        bndl.putString (CalDAVService.CAL_PSWD,
			    ((EditText)findViewById(R.id.caldav_password)).getText().toString().trim() );
		        bndl.putString (CalDAVService.CAL_HOST,
			    ((EditText)findViewById(R.id.caldav_host)).getText().toString().trim() );
		        bndl.putString (CalDAVService.CAL_PROT,
			    ((EditText)findViewById(R.id.caldav_protocol)).getText().toString().trim() );
		        bndl.putInt    (CalDAVService.CAL_PORT, Integer.parseInt (
			     ((EditText)findViewById(R.id.caldav_port)).getText().toString().trim()));
		        bndl.putString (CalDAVService.CAL_HOME,
			    ((EditText)findViewById(R.id.caldav_home)).getText().toString().trim() );
		        bndl.putString (CalDAVService.CAL_COLL,
			    ((EditText)findViewById(R.id.caldav_collection)).getText().toString().trim() );

                pg = ProgressDialog.show (ctxt, "Setting up CalDAV calendar", "In Progress. Please wait..",
                        true, false); 
                Message msg = Message.obtain(null, CalDAVService.MSG_SETUP_ACC, 0, 0, bndl);
                msg.replyTo = mMessenger;
                mService.send(msg);

	    	}
	    	break;
	    }

    	} catch (Exception e) {
            e.printStackTrace();
    	}

    }    
    
    private static final int MSG_PG_DISMISS = 1;
    private static final int MSG_PG_SHOWTXT = 2;

    private Handler pghandler = new Handler () {
        @Override
        public void handleMessage (Message msg) {
            switch (msg.what) {
                case MSG_PG_DISMISS:
                    pg.dismiss ();
                    break;
                case MSG_PG_SHOWTXT:
                    pg.setMessage (msg.obj.toString ());
                    break;
            }
        }
    };

	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
	   @Override
	   public void handleMessage(Message msg) {
	   	switch (msg.what) {
	   	    case CalDAVService.MSG_SETUP_ACC:
	   	    	Log.d (TAG, "acc setup response " + msg.arg1);
			if (msg.arg1 == 0 && msg.arg2 == 0) {
                if (msg.obj != null) {
                    Message pgmsg = Message.obtain(pghandler, MSG_PG_SHOWTXT, 0, 0, msg.obj);
                    pghandler.sendMessage (pgmsg);
                    //pg.setMessage (msg.obj.toString ());
                } else {
                    //pg.dismiss();
                    Message pgmsg = Message.obtain(pghandler, MSG_PG_DISMISS);
                    pghandler.sendMessage (pgmsg);
                    Toast.makeText (ctxt, R.string.caldav_done_setup, 
                            Toast.LENGTH_LONG).show ();
                    finish ();
                }
            } else {
                //pg.dismiss();
                Message pgmsg = Message.obtain (pghandler, MSG_PG_DISMISS);
                pghandler.sendMessage (pgmsg);
                Toast.makeText (ctxt, String.format (msg.obj.toString ()), 
                        Toast.LENGTH_LONG).show ();
                finish ();
            }
	   	    	break;
	   	    default:
	   	    	super.handleMessage(msg);
	   	}
	   }
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
					IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = new Messenger(service);
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
		}
	};

	void doBindService() {
		// Establish a connection with the service.  We use an explicit
		// class name because there is no reason to be able to let other
		// applications replace our component.
		bindService(new Intent(this, CalDAVService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}
}
