
CalDAV4j


WHAT IS CalDAV4j?
==============
CalDAV4j is a protocol library used to access CalDAV servers. It consists of a
very high level API for manipulating calendar collections, with methods for 
creating, updating, deleting, and searching for events and hides away the messy
details of the protocol. This abstract API is implemented a lower-level API 
which is at the http method level, implementing methods like GET, REPORT, MKCALENDAR
and PUT for their use in CalDAV calendar collections. 

CalDAV4j uses iCal4j to read and write icalendar streams and deal with 
icalendar objects as native Java objects.

CalDAV4j extends the Slide projects WebDAV client library, which itself extends
the HttpClient http client library. 

MORE INFO
=========
CalDAV4j's project page is at
<http://wiki.osafoundation.org/bin/view/Projects/CalDAV4jHome>.

Issues are tracked at <http://bugzilla.osafoundation.org/>.

Feel free to ask questions and report problems to
cosmo@osafoundation.org. Sign up at
<http://lists.osafoundation.org/mailman/listinfo/cosmo>.

More info about iCal4j can be found at:
<http://ical4j.sourceforge.net/>

More HttpClient can be found at:
<http://jakarta.apache.org/commons/httpclient/2.0/index.html>

More info about the Slide WebDAV client can be found at:
<http://jakarta.apache.org/slide/webdav-client.html>

The latest CalDAV spec is here:
<http://ietfreport.isoc.org/idref/draft-dusseault-caldav/>


Copyright 2006 Open Source Applications Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
