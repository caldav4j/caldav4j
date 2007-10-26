/*
 * Copyright 2005 Open Source Applications Foundation
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

package org.osaf.caldav4j.model.request;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.DOMValidationException;
import org.osaf.caldav4j.xml.OutputsDOM;
import org.osaf.caldav4j.xml.OutputsDOMBase;

/**
 *  <!ELEMENT text-match #PCDATA>
 *
 *  <!ATTLIST text-match caseless (yes|no)>
 *  
 * @author bobbyrullo
 * 
 */
public class TextMatch extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "text-match";
    public static final String ATTR_CASELESS = "caseless";
    public static final String ATTR_VALUE_YES = "yes";
    public static final String ATTR_VALUE_NO  = "no";
    
    private String caldavNamespaceQualifier = null;
    private String textToMatch = null;
    private Boolean caseless = null;
    
    public TextMatch(String caldavNamespaceQualifier, Boolean caseless,
            String textToMatch) {
        this.caldavNamespaceQualifier = caldavNamespaceQualifier;
        this.caseless = caseless;
        this.textToMatch = textToMatch;

    }

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected String getNamespaceQualifier() {
        return caldavNamespaceQualifier;
    }

    protected String getNamespaceURI() {
        return CalDAVConstants.NS_CALDAV;
    }

    protected Collection<OutputsDOM> getChildren() {
        return null;
    }

    protected String getTextContent() {
        return textToMatch;
    }
    
    protected Map<String, String> getAttributes() {
        Map<String, String> m = null;
        if (caseless != null) {
            m = new HashMap<String, String>();
            m.put(ATTR_CASELESS, caseless.booleanValue() ? ATTR_VALUE_YES
                    : ATTR_VALUE_NO);

        }
        return m;
    }

    public Boolean getCaseless() {
        return caseless;
    }

    public void setCaseless(Boolean caseless) {
        this.caseless = caseless;
    }

    public String getTextToMatch() {
        return textToMatch;
    }

    public void setTextToMatch(String textToMatch) {
        this.textToMatch = textToMatch;
    }
    
    /**
     * <!ELEMENT text-match #PCDATA>
     * 
     * <!ATTLIST text-match caseless (yes|no)>
     */
    public void validate() throws DOMValidationException{
        return;
    }
}
