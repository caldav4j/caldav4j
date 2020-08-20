/*
 * Copyright 2011 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Mark Hobson, Roberto Polli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.caldav4j.model.request;

import static org.junit.Assert.assertEquals;

import com.github.caldav4j.exceptions.DOMValidationException;
import com.github.caldav4j.util.XMLUtils;
import com.github.caldav4j.xml.OutputsDOM;
import java.text.ParseException;
import net.fortuna.ical4j.model.DateTime;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests {@code FreeBusyQuery}.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 * @see FreeBusyQuery
 */
public class FreeBusyQueryTest {
    // tests ------------------------------------------------------------------

    @Test
    public void createNewDocument() throws ParseException, DOMValidationException {
        FreeBusyQuery query = createFreeBusyQuery("20000101T000000Z", "20000201T000000Z");

        String expected =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                        + "<C:free-busy-query xmlns:C=\"urn:ietf:params:xml:ns:caldav\">"
                        + "<C:time-range end=\"20000201T000000Z\" start=\"20000101T000000Z\"/>"
                        + "</C:free-busy-query>";

        assertCreateNewDocument(expected, query);
    }

    @Test(expected = DOMValidationException.class)
    public void validateWithNoTimeRange() throws DOMValidationException {
        FreeBusyQuery query = createFreeBusyQuery();

        query.validate();
    }

    @Test(expected = DOMValidationException.class)
    public void validateWithInvalidTimeRange() throws DOMValidationException {
        FreeBusyQuery query = createFreeBusyQuery();
        query.setTimeRange(new TimeRange(null, null));

        query.validate();
    }

    @Test
    public void validateWithTimeRange() throws ParseException, DOMValidationException {
        FreeBusyQuery query = createFreeBusyQuery("20000101T000000Z", "20000201T000000Z");

        query.validate(); // Should not throw an exception
    }

    // private methods --------------------------------------------------------

    private static FreeBusyQuery createFreeBusyQuery() {
        return new FreeBusyQuery();
    }

    private static FreeBusyQuery createFreeBusyQuery(String timeRangeStart, String timeRangeEnd)
            throws ParseException {
        FreeBusyQuery query = createFreeBusyQuery();

        TimeRange timeRange =
                new TimeRange(new DateTime(timeRangeStart), new DateTime(timeRangeEnd));
        query.setTimeRange(timeRange);

        return query;
    }

    private static void assertCreateNewDocument(String expected, OutputsDOM output)
            throws DOMValidationException {
        Document document = output.createNewDocument();

        String actual = XMLUtils.toXML(document);

        assertEquals("createNewDocument", expected, actual);
    }
}
