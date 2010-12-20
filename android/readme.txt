Author: Sudheer Peddireddy (peddireddy.sudheer@gmail.com)
        Samsung Telecom America
Date  : Dec 20, 2010

This document will explain how to build and test CalDAV support in Android Calendar application.

* Since CalendarProvider is not exposed as part of Android SDK, one has to build emulator from source code. Please follow instructions here (http://source.android.com/source/download.html). Make sure you check out froyo branch (I used froyo). (repo init -u git://android.git.kernel.org/platform/manifest.git -b froyo)

* You can debug using eclipse. Please see here (http://source.android.com/source/using-eclipse.html). This page also includes directions to run emulator from the source you just built (cd /path/to/android/root; . build/envsetup.sh; lunch 1;make; emulator;
* You should be able to see emulator and use it. 

Adding CalDAV support
--------------------
Say, froyo is the root of Android source you just checked out.
cd froyo/packages/apps/
rm -rf Calendar
svn co https://caldav4j.googlecode.com/svn/android/ Calendar
Goto froyo directory and make again. It should build.


Testing CalDAV support
----------------------
Open Calendar app
Menu->More->CalDAV control
Here you can see 3 buttons 1. Setup a CalDAV account 2. Sync accounts  (Sync'em) 3. Remove Accounts (Nuke'em)

Hit Setup and edit details if necessary and then hit "Just do it" button.
The Calendar should be synced. You should be able to go back to calendar app and see the events in your calendar. A new Calendar with name "CalDAV" (or what ever you chose) should appear and you should be able to add events to this calendar.

The Sync is not automatic yet. So, you goto CalDAV control screen and hit Sync'em. Now the events should be synced between your device (emulator) and server.

Currently, events can be added, deleted, edited on device and server and can be synced. Only simple fields are supported. No recurrence, exceptions, meeting invitations, etc.

Note that if you restart the emulator, the events will disappear. Goto CalDAV control and Sync again. This is one of the TODO.

NOTE: The default account used is as follows:
username : caldav4j
password : CalDAV4J
server   : https://hub.chandlerproject.org

You can use these credentials to verify that the events are synced to the server properly.


