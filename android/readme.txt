Author: Sudheer Peddireddy (peddireddy.sudheer@gmail.com)
Date  : Oct 13, 2013

get this source and build it using ant or eclipse.
Install the APK (adb install Caldav.apk)
This APK will sync the CalDAV calendar to your native device calendar.

Testing CalDAV support
----------------------
Open CalDAV app
Here you can see 3 buttons 1. Setup a CalDAV account 2. Sync accounts  (Sync'em) 3. Remove Accounts (Nuke'em)

Hit Setup and edit details if necessary and then hit "Just do it" button.
The Calendar should be synced. You should be able to go back to calendar app and see the events in your calendar. A new Calendar with name "CalDAV" (or what ever you chose) should appear and you should be able to add events to this calendar.

The Sync is not automatic yet. So, you goto CalDAV control screen and hit Sync'em. Now the events should be synced between your device (emulator) and server.

Currently, events can be added, deleted, edited on device and server and can be synced. Only simple fields are supported. No recurrence, exceptions, meeting invitations, etc.

NOTE: The default account used is as follows:
username : caldav4j
password : CalDAV4J
server   : https://hub.chandlerproject.org

You can use these credentials to verify that the events are synced to the server properly.

