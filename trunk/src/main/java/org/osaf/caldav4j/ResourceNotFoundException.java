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
