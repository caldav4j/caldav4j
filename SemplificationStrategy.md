# Some hints to simplify the library #

> ## resources or calendars ##
Actually caldav4j has two interesting items:
  * the ical4j Calendar
  * the CalDAVResource which ties the Calendar with its etag/href

Even if users could be interested in the Calendar object only, many caldav implementations makes users interact with etag/href too.

A semplification could be to let CalDAVResource to be a subclass of Calendar

> ## no custom queries in methods name ##
All queries should be made via GenerateQuery

> ## Test refactoring ##
  * enjoy annotations and parameterized test with Junit4
  * create a testlist related to caldav rfc examples