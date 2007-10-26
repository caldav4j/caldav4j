package org.osaf.caldav4j;

public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable t){
        super(message, t);
        RuntimeException r;
    }
}
