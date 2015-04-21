## CalDAV4j Overview ##

<table><tr><td>

CalDAV4j is a java library implementing the CalDAV protocol. <br>
<br>
It makes easy to issue complex queries to a caldav server, supporting free-busy and calendar-collections. Moreover supports the standard add/remove of events.<br>
CalDAV4j extends the Android Calendar App with  <a href='BuildAndroid.md'>Caldav Sync implementation for Android</a>.<br>
<br>
While we're moving to <a href='http://jackrabbit.apache.org/jackrabbit-webdav-library.html'>jackrabbit</a>, CalDAV4j is still  based on Slide project's <a href='http://jakarta.apache.org/slide/webdav-client.html'>WebDAV client library</a> (which itself is an extension of the Apache's <a href='http://jakarta.apache.org/commons/httpclient/'>HttpClient</a> library) to allow high level manipulation of CalDAV calendar collections as well as lower level CalDAV protocol interactions.<br>
<br>
</td><td width='15%'>
<b>Main Contributors</b><br />
<a href='http://www.babel.it/en.html'><img src='http://www.babel.it/templates/rt_panacea_j15/images/logo/light/logo.png' /></a>
<a href='http://www.osafoundation.org'><img src='http://upload.wikimedia.org/wikipedia/commons/5/57/Chandler-superdog.png' /></a> OSAFoundation<br>
<br>
</td></tr></table>

CalDAV4j

## Goals ##

  * A high level API: This API works at the level of ical4j objects, and allows for high level operations. All the "hard" protocol work that you need to do to make this happen is hidden from the end user. Supports:
    * an [easy and flexible query language](CreateCaldavQuery.md) (eg. for events in a given date range);
    * recurring event management (eg. updating master events and recurrence instances);

  * A lower level protocol API: Let's you work at the Http/WebDAV/CalDAV protocol level by creating instances of HttpMethods (like GetMethod, MkCalendar, ReportMethod) and executing them.

  * Performance: When possible caches (such as caches of etags and icalendar resources) are maintained so that network chatter and parsing of icalender files is minimized.

  * Compliance: We aim to be implement the following specs
    * [CalDAV spec](http://tools.ietf.org/html/rfc4791) and updates
    * [WebDAV tickets spec](http://www.sharemation.com/~milele/public/dav/draft-ito-dav-ticket-00.txt)
    * [Caldav Scheduling specs](https://datatracker.ietf.org/doc/draft-desruisseaux-caldav-sched/)
  * Reliability: a lot of functional tests and the "rails" for checking your caldav implementation support.

## Join us ##

Consider joining: WhyJoin

## Status ##

CalDAV4j 0.7 is the current (tagged) release.

In 0.8.x (trunk) we're working on
  * fine-grained ACL support 15%
  * small refinement to simplify code
    * remove redundancies 15%
    * parametrized tests for various caldav implementation 20%
  * support for VTODO
  * simplify recurrent event management
  * embedded CalDAV manager
  * branch for jackrabbit support

The 0.7 release includes:
  * junit4 support
  * free-busy query support
  * massive refactoring to simplify code
    * separate exceptions in a given package
  * source jars and javadocs in maven repository


The 0.6 release includes:
  * basic ACL support (get/set)
  * basic scheduling support
  * a standard cache implementation (quick and dirty)

The 0.5 release includes:
  * basic google calendar support
  * a method for easy caldav query creation
  * example classes for connecting to bedework and google calendar
  * better junit testing

The 0.4 release includes:
  * Support for 

&lt;calendar-multiget&gt;

 reports
  * More and more ready-to-use 

&lt;calendar-query&gt;

 methods to search events
  * Code refactoring
    * HttpClient v3.0 support (download Slide for HttpClient v3 here!)
    * More Slide isolation (for a future JackRabbit switch)
    * Better Bedework support

The 0.3 release features include:
  * Support for 

&lt;calendar-query&gt;

 reports
  * Protocol level support for MKCALENDAR, GET, REPORT, PUT
  * High level API for accessing events within a calendar collection
  * Caching of CalDAV resources
  * Ticket Support

Among the things not supported yet are:
  * Free Busy reports
  * High level API for accessing anything other than VEVENTS (like VTODO's for example)
  * ... ?

More information [here](Status.md).
## Building ##

CalDAV4j uses [Maven 2.x](http://maven.apache.org/) as its build system. [Here](BuildingCalDAV4j.md) is some more information on building CalDAV4j


## Using ##

Download the latest JAR file and make sure it's in your classpath. The unit tests are the best place right now to see how to use CalDAV4j. Tutorials and more documentation are forthcoming.

## What's Next ##

  * jackrabbit support
  * android resource bundle
  * scheduling support

The [roadmap](Roadmap.md) will be updated as things flesh out more.

## License & Copyright ##

CalDAV4j is licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0) and copyright 2007 [Open Source Application Foundation](http://osafoundation.org)