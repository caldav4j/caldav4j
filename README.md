# CalDAV4j Overview

CalDAV4j is a java library implementing the CalDAV protocol. 

It makes easy to issue complex queries to a caldav server, supporting free-busy and calendar-collections. Moreover supports the standard add/remove of events.
CalDAV4j extends the Android Calendar App with Caldav Sync implementation for Android.

While we're moving to jackrabbit, CalDAV4j is still based on Slide project's WebDAV client library (which itself is an extension of the Apache's HttpClient library) to allow high level manipulation of CalDAV calendar collections as well as lower level CalDAV protocol interactions.

# Main Contributors

 * Par-Tec
 * OSAFoundation
 

# CalDAV4j

## Goals

A high level API: This API works at the level of ical4j objects, and allows for high level operations. All the "hard" protocol work that you need to do to make this happen is hidden from the end user. Supports:

an easy and flexible query language (eg. for events in a given date range);
recurring event management (eg. updating master events and recurrence instances);
A lower level protocol API: Let's you work at the Http/WebDAV/CalDAV protocol level by creating instances of HttpMethods (like GetMethod, MkCalendar, ReportMethod) and executing them.

Performance: When possible caches (such as caches of etags and icalendar resources) are maintained so that network chatter and parsing of icalender files is minimized.

Compliance: We aim to be implement the following specs

  * CalDAV spec and updates
  * WebDAV tickets spec
  * Caldav Scheduling specs
  * Reliability: a lot of functional tests and the "rails" for checking your caldav implementation support.

# Join us

Consider joining: WhyJoin

# Testing

You can test your caldav server replacing the `caldav4jUri` with your one.

```
mvn package -Dcaldav4jUri='https//user:password@hostname.fqdn.com/base/user/collections/'
```

# Status

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
  * The 0.6 release includes:
  
  * basic ACL support (get/set)
  * basic scheduling support
  * a standard cache implementation (quick and dirty)

 The 0.5 release includes:
 
basic google calendar support
a method for easy caldav query creation
example classes for connecting to bedework and google calendar
better junit testing
The 0.4 release includes:

Support for
<calendar-multiget>

reports

More and more ready-to-use
<calendar-query>

methods to search events

Code refactoring
HttpClient v3.0 support (download Slide for HttpClient v3 here!)
More Slide isolation (for a future JackRabbit switch)
Better Bedework support
The 0.3 release features include:

Support for
<calendar-query>

reports

Protocol level support for MKCALENDAR, GET, REPORT, PUT
High level API for accessing events within a calendar collection
Caching of CalDAV resources
Ticket Support
Among the things not supported yet are:

Free Busy reports
High level API for accessing anything other than VEVENTS (like VTODO's for example)
... ?
More information here.


# Building

CalDAV4j uses Maven as its build system. Here is some more information on building CalDAV4j
 

# Using

Download the latest JAR file and make sure it's in your classpath. The unit tests are the best place right now to see how to use CalDAV4j. Tutorials and more documentation are forthcoming.

# What's Next

jackrabbit support
android resource bundle
scheduling support
The roadmap will be updated as things flesh out more.

License & Copyright

CalDAV4j is licensed under the Apache 2.0 License and copyright 2007 Open Source Application Foundation

