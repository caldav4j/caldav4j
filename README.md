# CalDAV4j Overview

|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Build Status | [![Build Status](https://travis-ci.org/caldav4j/caldav4j.svg?branch=master)](https://travis-ci.org/caldav4j/caldav4j)                                                       |
| Maven        | [![Maven Status](https://maven-badges.herokuapp.com/maven-central/com.github.caldav4j/caldav4j/badge.svg)](https://search.maven.org/artifact/com.github.caldav4j/caldav4j/) |
| License      | [![Apache 2.0 License](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)                                                               |
|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|





CalDAV4j is a java library implementing the CalDAV protocol for the client side implementation.

CalDAV4j makes it easy to issue complex queries to a caldav server, supporting free-busy and calendar-collections. Moreover, it supports the standard adding and removal of events.

# CalDAV4j

## Goals

 - A high level API to work with CalDAV: This API works at the level of iCal4j objects, and allows for high level operations. All the "hard" protocol work that you need to do to make this happen is hidden from the end user.

 - A flexible query language support: An easy and flexible query language (eg. for events in a given date range); and Recurring event management (eg. updating master events and recurrence instances).

 - Low Level API Access: Let's you work at the Http/WebDAV/CalDAV protocol level by creating instances of HttpMethods (like GetMethod, MkCalendar, ReportMethod) and executing them.

 - Performance: When possible, caches (such as caches of etags and icalendar resources) are maintained so that network chatter and parsing of icalender files is minimized.

 - Compliance: We aim to be compliant with the following specs:
   * CalDAV spec and updates. ([RFC 4791](https://tools.ietf.org/html/rfc4791), [RFC 7809](https://tools.ietf.org/html/rfc7809))
   * CalDAV Scheduling specs. (Work in Progress, [RFC 6638](https://tools.ietf.org/html/rfc6638))
   * Reliability: Many functional tests and the "rails" for checking your caldav implementation support.

# Using CalDAV4j in your code

CalDav4j is currently on Maven Central, thus to add it, you can add a dependancy by adding the following to the `pom.xml`:

```xml
<dependency>
    <groupId>com.github.caldav4j</groupId>
    <artifactId>caldav4j</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Gradle:

```
compile 'com.github.caldav4j:caldav4j:1.0-SNAPSHOT'
```

For more information on how to add CalDAV4j into your code, please refer to: https://search.maven.org/artifact/com.github.caldav4j/caldav4j/

The unit tests are currently best place right now to see how to use CalDAV4j. Tutorials and more documentation are forthcoming.

# Building

CalDAV4j uses Maven as its build system. To build this project, one must simply install through maven, while skipping all the tests:

```sh
mvn install -DskipTests
```

# Testing

During package creation, you can test your caldav server replacing the `caldav4jUri` with your own custom one.

```sh
mvn package -Dcaldav4jUri='https//user:password@hostname.fqdn.com/base/user/collections/'
```

A Docker image of a caldav server, Bedework (~900MB) is distributed via Docker Hub:

```sh
docker run -d -p 8080:8080 ioggstream/bedework
mvn clean package -Dcaldav4jUri='http//vbede:bedework@localhost:8080/ucaldav/user/vbede/'
```

# License

CalDAV4j is licensed under the Apache 2.0 License
 - Copyright © 2005-2007 Open Source Application Foundation
 - Copyright © 2007 - Present Individual Contributors

# Contributing Organizations and Individuals

 * Organizations
   - Par-Tec
   - OSAFoundation (No longer contributing)
 * Individuals
   - Roberto Polli (@ioggstream)
   - Ankush Mishra (@The_Antimist)
   - Mark Hobson (@markhobson)
   - Bobby Rullo
   - @alexander233

# Changelog

CalDAV4j 0.9.1 is the current (tagged) release and 1.0-SNAPSHOT is the current development branch.

```
In 1.0.x (http4client-dev):

  * Migration from Apache HttpClient 3.x to HttpClient 4.x
  * Update java version to 8, and iCal4j to v3.0.1
  * Change the package structure from `org.osaf.caldav4j` to `com.github.caldav4j`
  * More consistent API throughout, with Apache Jackrabbit WebDAV

In 0.9.x (master):

  * major cleanup, deprecated methods removal
  * based on jackrabbit

In 0.8.x includes:

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

  * Support for <calendar-multiget> reports
  * More and more ready-to-use <calendar-query> methods to search events
  * Code refactoring
  * HttpClient v3.0 support (download Slide for HttpClient v3 here!)
  * More Slide isolation (for a future JackRabbit switch)
  * Better Bedework support

The 0.3 release features include:

  * Support for <calendar-query> reports
  * Protocol level support for MKCALENDAR, GET, REPORT, PUT
  * High level API for accessing events within a calendar collection
  * Caching of CalDAV resources
  * Ticket Support
```
