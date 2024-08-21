/*
 * Copyright © 2018 Ankush Mishra
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

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.methods.HttpPostMethod;
import com.github.caldav4j.methods.HttpPutMethod;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;

/**
 * This class is a wrapper for the all the properties of the POST and PUT Method requests.
 *
 * @see HttpPostMethod
 * @see HttpPutMethod
 * @author Ankush Mishra
 */
public class CalendarRequest {

    private Calendar calendar = null;
    private Charset charset = null;

    private Set<String> etags = new HashSet<>();
    private boolean ifMatch = false;
    private boolean ifNoneMatch = false;
    private boolean allEtags = false;

    public CalendarRequest() {}

    /**
     * Contructor to create a Calendar Based Request body, based on the parameters.
     *
     * <p>Note: If both, ifMatch and ifNoneMatch are set to true, then ifMatch will be selected.
     *
     * @param calendar Calendar body of the request to set
     * @param etags The set of eTags to match, that will be used in "if-none-match" or "if-match" if
     *     the ifMatch or ifNoneMatch properties are set. Note a quoted string should be provided.
     * @param ifMatch If true the "if-match" conditional header is set
     * @param ifNoneMatch If true the "if-none-match" conditional header
     * @param allEtags Enable all etags, instead of specific ones.
     * @param charset Charset to encode the calendar in. If not provided the JVM default charset is
     *     used.
     */
    public CalendarRequest(
            Calendar calendar,
            Charset charset,
            Set<String> etags,
            boolean ifMatch,
            boolean ifNoneMatch,
            boolean allEtags) {
        this.calendar = calendar;
        this.etags = etags;
        this.ifMatch = ifMatch;
        this.ifNoneMatch = ifNoneMatch;
        this.allEtags = allEtags;
        this.charset = charset;
    }

    public CalendarRequest(Calendar calendar) {
        this.calendar = calendar;
    }

    public CalendarRequest(
            Calendar calendar, Set<String> etags, boolean ifMatch, boolean ifNoneMatch) {
        this.calendar = calendar;
        this.etags = etags;
        this.ifMatch = ifMatch;
        this.ifNoneMatch = ifNoneMatch;
    }

    public CalendarRequest(
            Calendar calendar, boolean ifMatch, boolean ifNoneMatch, boolean allEtags) {
        this.calendar = calendar;
        this.ifMatch = ifMatch;
        this.ifNoneMatch = ifNoneMatch;
        this.allEtags = allEtags;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setCalendar(VEvent vevent, VTimeZone vtimeZone, String prodId) {
        if (prodId == null) prodId = CalDAVConstants.PROC_ID_DEFAULT;

        Calendar calendar = new Calendar();
        calendar.add(new ProdId(prodId));
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);
        calendar.add(vevent);
        if (vtimeZone != null) {
            calendar.add(vtimeZone);
        }
        this.calendar = calendar;
    }

    public void setCalendar(VEvent event, String prodId) {
        setCalendar(event, null, prodId);
    }

    public void setCalendar(VEvent event) {
        setCalendar(event, null, null);
    }

    public Set<String> getEtags() {
        return etags;
    }

    public void setEtags(Set<String> etags) {
        this.etags = etags;
    }

    public void addEtag(String etag) {
        etags.add(etag);
    }

    public boolean isIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(boolean ifMatch) {
        this.ifMatch = ifMatch;
    }

    public boolean isIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(boolean ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public boolean isAllEtags() {
        return allEtags;
    }

    public void setAllEtags(boolean allEtags) {
        this.allEtags = allEtags;
    }

    public Charset getCharset() {
        if (charset == null) charset = Charset.defaultCharset();

        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
