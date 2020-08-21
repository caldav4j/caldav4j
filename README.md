 # CalDAV4j Overview

|              |                                                                                                                                                                             |
|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Build Status | [![Build Status](https://circleci.com/gh/caldav4j/caldav4j.svg?style=shield)](https://circleci.com/gh/caldav4j/caldav4j)                                        |
| Maven        | [![Maven Status](https://maven-badges.herokuapp.com/maven-central/com.github.caldav4j/caldav4j/badge.svg)](https://search.maven.org/artifact/com.github.caldav4j/caldav4j/) |
| License      | [![Apache 2.0 License](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)                                                               |





CalDAV4j is a java library implementing the CalDAV protocol for the client side implementation.

CalDAV4j makes it easy to issue complex queries to a caldav server, supporting free-busy and calendar-collections. Moreover, it supports the standard adding and removal of events.

# CalDAV4j

## Goals

 - _High-Level CalDAV API's:_ This API works at the level of iCal4j objects, and allows for high level operations. All the "hard" protocol work that you need to do to make this happen is hidden from the end user.

 - _Low Level API Access:_ Let's you work at the Http/WebDAV/CalDAV protocol level by creating instances of HttpMethods (like GetMethod, MkCalendar, ReportMethod, etc.) and executing them.

 - _Flexible query language support:_ An easy and flexible query language (eg. for events in a given date range); and Recurring event management (eg. updating master events and recurrence instances).

 - _Performance:_ When possible, caches (such as caches of etags and icalendar resources) are maintained so that network chatter and parsing of icalender files is minimized.

 - _Compliance:_ We aim to be compliant with the following specs:
   * CalDAV spec and updates. ([RFC 4791](https://tools.ietf.org/html/rfc4791), [RFC 7809](https://tools.ietf.org/html/rfc7809))
   * CalDAV Scheduling specs. (Work in Progress, [RFC 6638](https://tools.ietf.org/html/rfc6638))
   * Reliability: Many functional tests and the "rails" for checking your caldav implementation support.

# Using CalDAV4j in your code

CalDav4j is available currently on Maven Central, thus to add it, you can add a dependancy by adding the following to the `pom.xml`:

```xml
<dependency>
    <groupId>com.github.caldav4j</groupId>
    <artifactId>caldav4j</artifactId>
    <version>0.9.2</version>
</dependency>
```

Gradle:

```
compile 'com.github.caldav4j:caldav4j:0.9.2'
```

For more information on how to add CalDAV4j into your code, please refer to: https://search.maven.org/artifact/com.github.caldav4j/caldav4j/

There are two versions currently `0.9.2` (Stable based on HttpClient 3.x) and `1.0.0-rc.1` (Dev branch based on HttpClient 4.x) depending on which version of HttpClient you want to use will determine the version to use.

# Documentation

The unit tests are currently best place right now to see how to use CalDAV4j. Documentation can be found on the [wiki](https://github.com/caldav4j/caldav4j/wiki)

# Building

CalDAV4j uses Maven as its build system. To build this project, one must simply install through maven, while skipping all the tests:

```sh
mvn install -DskipTests
```

# Testing

During package creation, you can test your caldav server replacing the `caldav4jUri` with your own custom one.

```sh
mvn package -Dcaldav4jUri='https://user:password@hostname.fqdn.com/base/user/collections/'
```

A Docker image of a caldav server, Bedework (~900MB) is distributed via Docker Hub:

```sh
docker run -d -p 8080:8080 ioggstream/bedework
mvn clean package -Dcaldav4jUri='http://vbede:bedework@localhost:8080/ucaldav/user/vbede/'
```

Before committing, always reformat your code

```sh
mvn -e spotless:apply
```

# License

CalDAV4j is licensed under the Apache 2.0 License
 - Copyright © 2005-2011 Open Source Application Foundation
 - Copyright © 2011 - Present Individual Contributors

# Contributing Organizations and Individuals

 * Organizations
   - Par-Tec
   - OSAFoundation (No longer contributing)
 * Individuals
   - Roberto Polli ([@ioggstream](https://github.com/ioggstream))
   - Ankush Mishra ([@TheAntimist](https://github.com/TheAntimist))
   - Mark Hobson ([@markhobson](https://github.com/markhobson))
   - Bobby Rullo
   - [@alexander233](https://github.com/alexander233)

# Current Status

CalDAV4j 0.9.2 is the current (tagged) release and 1.0.0-rc.1 is the current development branch.

For the complete changelog, refer to this [page](https://github.com/caldav4j/caldav4j/wiki/Changelog).
