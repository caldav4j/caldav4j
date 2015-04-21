# Introduction #

Caldav4j 0.5 introduces the new CalDAVCollection class, which supports custom queries.

The deprecated CalDAVCollectionManager class is full of redundant code about creating tonns of different queries: now we provide a "language" to describe simple caldav queries.

The target is about REPORT, PROPFIND and maybe MULTIGET methods, as CReate-Update-Delete methods works well enough in this implementation.


# Scope #

Here we list some kind of calendar-query that you can implement, in compliance of RFC4791

  1. Query Components (VEVENT,VTODO)
  * by time-range
  * selecting properties to retrieve (so you have to specify vcalendar.vevent)
  * filtering by component.property value

## Off Scope ##
  1. Query Components filtering for subcomponents properties
  * VTODO.VALARM.time-range=interval

> as you can see from junit code, you can play a bit with GenerateQuery to create those complex queries too.


  1. third



# Results #

The GenerateQuery class is an helper for creating Calendar-Query REPORTs

http://code.google.com/p/caldav4j/source/browse/trunk/src/main/java/org/osaf/caldav4j/util/GenerateQuery.java

Because of the complexity of iCalendar object and relative queries, this class is intended to help the creation of basic queries like "get all VEVENT with THOSE attributes in THIS time-range"

Main schema is a strongly typed class, with helpers/parsers that will create the caldav query.

Experimental methods are made to create one-line queries.

## Usage ##
```
// create a simple query getting UID, ATTENDEE, DTSTART, DTEND
GenerateQuery qg = new GenerateQuery();
gg.setComponent("VEVENT"); // retrieve the whole VEVENT
qg.setComponent("VEVENT : UID, ATTENDEE, DTSTART, DTEND"); // retrieve the given properties

CalendarQuery query=gq.generate();

// create a "complex" query
// start and end values can be empty strings "" or RFC2445-UTC timestamp
qg.setFilter("VEVENT"); //request on VEVENT
qg.setFilter("VEVENT [start;end] : UID==value1 , DTSTART==[start;end],DESCRIPTION==UNDEF, SUMMARY!=not my summary,")


// more complex queries can be made with GenerateQuery
// look at GenerateQueryTest.java
```
http://code.google.com/p/caldav4j/source/browse/trunk/src/test/java/org/osaf/caldav4j/util/GenerateQueryTest.java