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

package com.github.caldav4j.model.request;

import org.apache.jackrabbit.webdav.xml.Namespace;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.DOMValidationException;
import com.github.caldav4j.xml.OutputsDOM;
import com.github.caldav4j.xml.OutputsDOMBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Specifies a substring match on a property or parameter value.
 * <pre>
 * &lt;!ELEMENT text-match #PCDATA&gt;
 * &lt;!ATTLIST text-match caseless (yes|no)&gt;
 * &lt;!ATTLIST text-match negate-conditon (yes|no)&gt;
 * &lt;!ATTLIST text-match collation (i;octet|i;ascii-casemap)&gt;
 * </pre>
 *  Note: The caseless attribute is mostly ignored by servers.
 *  
 * @author bobbyrullo
 * @see <a href=http://tools.ietf.org/html/rfc4791#section-9.7.5>RFC 4791 Section 9.7.5</a>
 */
public class TextMatch extends OutputsDOMBase {
    
    public static final String ELEMENT_NAME = "text-match";
    public static final String ATTR_CASELESS = "caseless";
    public static final String ATTR_NEGATE_CONDITION = "negate-condition";
    public static final String ATTR_COLLATION = "collation";
    
    public static final String ATTR_VALUE_YES = "yes";
    public static final String ATTR_VALUE_NO  = "no";
    
    public static final String ATTR_VALUE_COLLATION_ASCII = "i;ascii-casemap"; 
    public static final String ATTR_VALUE_COLLATION_OCT = "i;octet";
    
    private String collation = null;

    private String textToMatch = null;
    private Boolean caseless = null;
    private Boolean negateCondition = null;

    public TextMatch(Boolean caseless, Boolean negateCondition,
    		String collation,
            String textToMatch) {

        this.caseless = caseless;
        this.negateCondition = negateCondition;
        this.textToMatch = textToMatch;
        
        // this.collation = "i;octet";
        // RFC states default collation is i;ascii-casemap
        if (collation == null) {        	
            this.collation = ATTR_VALUE_COLLATION_ASCII;        	
        } else {
            this.collation = collation;
        }

    }
    

    protected String getElementName() {
        return ELEMENT_NAME;
    }

    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CALDAV;
    }

    protected Collection<OutputsDOM> getChildren() {
        return null;
    }

    protected String getTextContent() {
        return textToMatch;
    }
    
    protected Map<String, String> getAttributes() {
        Map<String, String> m = null;
        m = new HashMap<String, String>();

        if (caseless != null) {
            m.put(ATTR_CASELESS, caseless ? ATTR_VALUE_YES
                    : ATTR_VALUE_NO);
        }
        if ((negateCondition != null) &&  negateCondition ) {
            m.put(ATTR_NEGATE_CONDITION, negateCondition ? ATTR_VALUE_YES
                    : ATTR_VALUE_NO);
        }
        
        if (collation != null) {
        	m.put(ATTR_COLLATION, collation);
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
     * <pre>
     * &lt;!ELEMENT text-match #PCDATA&gt;
     * &lt;!ATTLIST text-match caseless (yes|no)&gt;
     * </pre>
     */
    public void validate() throws DOMValidationException{
        return;
    }
}
