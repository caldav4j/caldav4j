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

package org.osaf.caldav4j;

public class ResourceNotFoundException extends CalDAV4JException {

    public enum IdentifierType{
        UID,
        PATH;
    }
    
    public ResourceNotFoundException(IdentifierType identifierType, String identifier) {
        super(createMessage(identifierType, identifier));
    }
    
    public ResourceNotFoundException(IdentifierType identifierType,
            String identifier, Throwable cause) {
        super(createMessage(identifierType, identifier), cause);
    }
    
    private static String createMessage(IdentifierType identifierType,
            String identifier) {
        return "Could not find resource for the " + identifierType + " '"
                + identifier + "'";
    }
}
