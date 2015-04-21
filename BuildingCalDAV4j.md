## Get Java 1.6 ##

Caldav4j requires Java 1.6

## Get Maven 2.x ##

[Download](http://maven.apache.org/download.html) and install the latest version of the Maven build tool.

## Get the source ##

Checkout the CalDAV4j [source](http://code.google.com/p/caldav4j/source) code.

## Build without Tests ##

From within the source tree run:
```
mvn -Dmaven.test.skip=true package
```

## Build With Tests ##

Now Caldav4j uses a online caldav server for testing, so you can run your test easily.

The reason I mentioned building without the tests first is because building with the tests is harder than it should be right now, but if you are developing CalDAV4j you should really be running the tests.

To test against you favorite implementation, follow the steps:
  1. Get a caldav server (Bedework, Chandler Server, ..)
    * [Download](http://chandlerproject.org/Developers/DownloadChandlerServer) and install Chandler Server (aka Cosmo)
    * [Download](http://www.bedework.org) and install Bedework
  1. Create The Test User
    * After starting the server, create a user on the server called "test" with a password of "password"
  1. modify the CaldavCredential.java putting your server credential and uris.
    * chandler: http://localhost:8080/chandler/dav
    * bedewor: http://localhost:8080/ucaldav/user
  1. Build
```
mvn package
```

The so-called unit tests are really functional tests - they require the a running CalDAV server to test against.

Making CalDAV4j run and be tested against ANY CalDAV compliant server is a priority for the project.