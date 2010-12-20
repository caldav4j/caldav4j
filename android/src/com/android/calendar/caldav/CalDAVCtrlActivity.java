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
import android.widget.Toast;


public class CalDAVCtrlActivity extends Activity implements OnClickListener{
	static final String TAG = "CalDAVCtrlActivity";
	private ProgressDialog pg = null;
    Context ctxt = null;
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.caldav);

        Button setup_acc_btn = (Button)findViewById(R.id.setup_acc_btn);
        setup_acc_btn.setOnClickListener(this);

        Button sync_caldav_btn = (Button)findViewById(R.id.sync_caldav_btn);
        sync_caldav_btn.setOnClickListener(this);
		
        Button remove_caldav_btn = (Button)findViewById(R.id.remove_caldav_btn);
        remove_caldav_btn.setOnClickListener(this);
		
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
				case R.id.sync_caldav_btn: {
					pg = ProgressDialog.show (this, "Syncing CalDAV accounts", "please wait..", true, false);

					Message msg = Message.obtain(null, CalDAVService.MSG_SYNC);
					msg.replyTo = mMessenger;
					mService.send(msg);
					
				}
					break;
				case R.id.remove_caldav_btn: {
					pg = ProgressDialog.show (this, "Removing CalDAV accounts", "please wait..", true, false);

					Message msg = Message.obtain(null, CalDAVService.MSG_REMOVE);
					msg.replyTo = mMessenger;
					mService.send(msg);
				}
                break;
				case R.id.setup_acc_btn:
					startActivity (new Intent (this, CalDAVSetupAccActivity.class));
					break;
			}

    	} catch (Exception e) {
            e.printStackTrace();
    	}

    }    

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
				case CalDAVService.MSG_SYNC:
					Log.d (TAG, "sync response " + msg.arg1);
					if (pg != null) {
                        if (msg.arg1 == 0 && msg.obj == null && msg.arg2 != 0) {
                            // update the progress dialog
                            pg.setMessage ("Now Syncing Acc " + msg.arg2);

                        } else {
                            //cancel
                            pg.dismiss ();
                            if (msg.obj != null)
                                Toast.makeText (ctxt, String.format (msg.obj.toString ()), 
                                        Toast.LENGTH_LONG).show ();
                            else
                                Toast.makeText (ctxt, R.string.caldav_done_sync, 
                                        Toast.LENGTH_LONG).show ();
                        }
                    }
					break;
				case CalDAVService.MSG_REMOVE:
					Log.d (TAG, "remove response " + msg.arg1);
					if (pg != null) {
                        if (msg.arg1 == 0 && msg.obj == null && msg.arg2 != 0) {
                            // update the progress dialog
                            pg.setMessage ("Now Removing Acc " + msg.arg2);

                        } else {
                            //cancel
                            pg.dismiss ();
                            if (msg.obj != null)
                                Toast.makeText (ctxt, String.format (msg.obj.toString ()), 
                                        Toast.LENGTH_LONG).show ();
                            else
                                Toast.makeText (ctxt, R.string.caldav_done_remove, 
                                        Toast.LENGTH_LONG).show ();
                        }
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
