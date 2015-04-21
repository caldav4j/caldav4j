# Introduction #

Please, please, please: if you have troubles, check this page carefully and remember:
  * caldav4j is tested against
    * a public Cosmo / Chandler server
    * Bedework 3.4.1.1

tests ok against: bedework 3.5, davical (svn)

almost working with: darwin calendar server (apple)

  * caldav4j requires Java 1.6, but recently we tested ok with 1.5 using xerces
  * caldav4j requires the jakarta-slide jar provided on this site
  * before submitting issues on list please, run junits


# FAQ #


## FAQ 0 ##
Q: Can't download caldav4j 0.6+

A: Use maven and add https://caldav4j.googlecode.com/svn/maven/ to your repos.

Q: Isn't maven a build tool? How can I use it for downloading?

A: Yes, maven is a build tool. Learn a bit of maven in 5 minutes here http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
and remember: caldav4j is open source, you should know how to build it.

## FAQ 1 ##

Q: Can't compile caldav4j

A: build without test
# mvn package -Dmaven.test.skip=true

## FAQ 2 ##
Q: Caldav4j won't work with my caldav server

A: Run junit tests against your caldav server. At least some tests (eg. list events) should work. Caldav4j test creates and queries for events in a given user caldav path.
ex. the following configuration

"http://calendar.example.com/ucaldav/user/rpolli/"

user: rpolli

password: mypasswd

calendar: "collection/"

  * login to http://rpolli:mypasswd@calendar.example.com/ucaldav/user/rpolli/
  * create the calendar "collection/" in the above path
  * creates, deletes, searches calendars into  http://rpolli:mypasswd@calendar.example.com/ucaldav/user/rpolli/collection/

### FAQ 2.1 ###
Q: How to build a caldav client?

A: Please check junits and code in [example/ directory](http://code.google.com/p/caldav4j/source/browse/#svn/trunk/src/example/org/osaf/caldav4j/example)

### FAQ 2.2 ###
Q: I cannot run the tests, getting errors like org.osaf.caldav4j.exceptions.CalDAV4JException: Unexpected status code: 400

A: Ensure you're pointing to the right caldav uri, eventually testing the uri with your browser.
eg.
  * this is a correct bedework uri: "http://calendar.example.com/ucaldav/user/rpolli/"
  * this is a WRONG uri: "http://calendar.example.com/user/rpolli/"


## FAQ 3 ##
Q: I got some strange XML error/exception while running caldav4j

A: use java 1.6 or test that you have a suitable version of xerces(2.7+)

## FAQ 4 ##
Q: caldav4j passes the test but won't work on my application

A: check your environment: java version, httpclient version, slide version. To avoid issues, use maven in your project. **You need to use the slide provided by this site: it's patched to run with httpclient-3.0 and httpclient-3.1**

## FAQ 5 ##
Q: how can I query for events like...

A: see the CreateCaldavQuery page. As of now caldav/webdav protocol doesn't support disjuntive search (eg. something like all events where SUMMARY=A OR SUMMARY=B )

## FAQ 6 ##
Q: how to submit new code?

A: as caldav4j needs to be reliable, we'd like to have each class tested. So contributors need to write a test case (or a test class if it's not present) and then write the patch.
The test class ensures the patch won't change the old behavior.
**It's important not to refactor code when submitting patches, so that we can apply it easily an quickly**

## FAQ 7 ##
Q: can I use caldav4j on Android?

A: yes. Samsung has written Caldav support for Android Calendar  using  caldav4j. You can get more info here: BuildAndroid

## FAQ 8 ##
Q: how can I trace the communication between client and server?

A: Tracing the client-server communication is crucial to debug your application. The best way to do it is to:
  * set httpclient.wire logging to DEBUG in [src/test/resources/log4j.xml](http://code.google.com/p/caldav4j/source/browse/trunk/src/test/resources/log4j.xml)
```
  <logger name="httpclient.wire">
    <level value="DEBUG"/>
  </logger>
```
  * check if request-responses are caldav compliant