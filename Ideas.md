|Caldav4j - the first java caldav library - is candidate to Google Summer of Code! Here you can see why to join and contribute with your ideas to this wonderful library!  | [![](http://google-summer-of-code.googlecode.com/files/gsoc-2012-logo-color.jpg)](http://socghop.appspot.com/) |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------|


# Why Caldav4j #
  * learn the widespreading standard for calendaring and collaboration used by Apple and Google
  * work with Test Driven Development and Agile metodologies
  * discuss with standard-makers on IETF mailing lists
  * contribute to make world of communication more open

> See more at WhyJoin

# Students #
  * let us know on our mailing lists caldav4j@googlegroups.com
  * spend some time to play with caldav and build caldav4j
  * look at project ideas, to see if some is interesting for you
  * student application will begin 29.March, deadline 9.April

> See [Student Application](GSoCStudentApplication.md)

## Skill ##
  * required:
    * java, xml, tomcat, svn

  * suggested:
    * junit, Maven, [Junit](http://www.slideshare.net/som.mukhopadhyay/test-driven-development-and-junit-presentation), [HTTP/1.1](http://www.slideshare.net/sanjoysanyal/http-basics), RFC understanding, tomcat
    * HTTP protocol

  * moreover you will learn:
    * httpclient java library, jackrabbit webdav library
    * WebDAV, [CalDAV](http://www.slideshare.net/ioggstream/presenting-caldav-draft-1-presentation) protocol, iCalendar RFC5525
    * wireshark/ethereal/tcpdump

  * further resources: [Caldav4j, Funambol and mobile devices](http://www.slideshare.net/ioggstream/integrating-funambol-with-caldav-and-ldap-presentation)


# Ideas #

> ## Android sync plugin ##
  * Summary: extend the Android Sync Plugin for the Android Calendar.apk contributed by Samsung.
  * Level: medium
  * Skill: java, android, gcc
  * Description: the Calendar.apk is a working proof of concept. We want to make it stable and installable without overwriting the default Calendar.apk
  * Strategy: simplify code and dependencies, improve caldav backend and user interface
    1. improve user interface and Calendar menus
    1. remove external libraries' sources from the Calendar.apk project
    1. review all backend methods
    1. more and more junits

> ## Jackrabbit migration ##
  * Summary: migrate the webdav layer from slide to jackrabbit and test with android
  * Level: medium
  * Skill: java, httpclient, http protocol
  * Description: caldav4j is based on top of the old apache-slide library instead of the new jackrabbit one. This is the main obstacle to caldav4j adoption in newer projects.
  * Strategy: develop missing methods in jackrabbit branch
    1. remove slide dependencies from junits, so that we can test our changes
    1. work on the caldav4j-jackrabbit branch implementing missing methods
    1. test new code for android compatibility

> ## CalDAV scheduling support ##
  * Summary: improving CaldDAV scheduling support
  * Level: easy
  * Skill: java, icalendar
  * Description: manage icalendar meeting request/reply with standard ical4j quite boring. We want to develop an helper (a stub still exists) to quickly manage reply/cancel/refresh meeting requests
  * Strategy:
    1. analize standard iTip meeting reply/cancel/refresh request
    1. write a class that create meeting replies conform to iTip
    1. analize caldav scheduling protocol drafts and implicit scheduling
    1. write basic caldav scheduling support methods
    1. test against current caldav implementation: Bedework, Apple calendar server, Google calendar

> ## WebDAV ACI support ##
  * Summary: improving WebDAV ACI management (needed for calendar sharing and scheduling)
  * Level: easy
  * Skill: java, webdav ACI
  * Description: WebDAV ACL management is odd.You can't modify a single ACL but you have
> > to rewrite it from scratch. Example:
    1. jon share a calendar with mary
    1. to share it with frank, he can't "share with frank too";
> > he must:
    1. check calendar permission
    1. create a new ACL containing both mary and frank
    1. replace the old ACL with the new one


> Our target is to wrap all that stuff in an easy to use class. Actually there's a
> slide ACI manager stub.

  * Strategy:
    1. create an interface for ACL management, to be slide and jackrabbit independent
    1. interface contains the new ACL used by caldav scheduling protocol
    1. check different behavior between jackrabbit and slide behaviout:
    1. create an utility to add/remove single acl from a list
    1. write junit test
    1. create methods to get/set ACL
    1. use etag to avoid continuously retrieve stored ACL


> ## Improve cache ##
  * Summary: improve default CalDAV4j cache manager based on EhCache
  * Level: medium
  * Skill: java, webdav ACI
  * Description: actually CalDAV4j have basic cache support, implemented with EhCache.This cache implementation is simple and can be optimised.
  * Strategy: Implementation strategies will be discussed with the students.
    1. The Cached collection manager has junit testing too, so the new implementation should pass the existing tests.

> ## Write a Calendar Manager ##
  * Summary: include an off-the-shelf calendar manager. Actually it's provided as an example.
  * Level: medium
  * Skill: java, http
  * Description: caldav4j doesn't provide an off-the-shelf calendar manager. Now that many parts of the protocol have been supported, it's the time to create an easy caldav store manager to let people with low caldav knowledge to use the library.
  * Strategy:
    1. writing an interface with all the desired methods
    1. write the test class
    1. less methods than the actual example class, but more flexible ones
    1. the new manager will support etags retrieval

> # Your ideas welcome! #
> You're welcome to provide your ideas for caldav4j. You can check the issue list