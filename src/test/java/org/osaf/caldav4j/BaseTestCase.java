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

import junit.framework.TestCase;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Base class for CalDAV4j tests.
 */
public abstract class BaseTestCase
    extends TestCase{
    private static final Log log = LogFactory.getLog(BaseTestCase.class);
    
    public static final String CALDAV_SERVER_HOST = "localhost";
    public static final int CALDAV_SERVER_PORT = 8080;
    public static final String CALDAV_SERVER_PROTOCOL = "http";
    public static final String CALDAV_SERVER_WEBDAV_ROOT = "/home/bobby";
    public static final String CALDAV_SERVER_USERNAME = "bobby";
    public static final String CALDAV_SERVER_PASSWORD = "password";
    
    public String getCalDAVServerHost() {
        return CALDAV_SERVER_HOST;
    }
    
    public int getCalDAVServerPort(){
        return CALDAV_SERVER_PORT;
    }
    
    public String getCalDavSeverProtocol(){
        return CALDAV_SERVER_PROTOCOL;
    }
    
    public String getCalDavSeverWebDAVRoot(){
        return CALDAV_SERVER_WEBDAV_ROOT;
    }
    
    public String getCalDavSeverUsername(){
        return CALDAV_SERVER_USERNAME;
    }
    
    public String getCalDavSeverPassword(){
        return CALDAV_SERVER_PASSWORD;
    }
    
    public HttpClient createHttpClient(){

        HttpClient http = new HttpClient();

        Credentials credentials = new UsernamePasswordCredentials(CALDAV_SERVER_USERNAME, CALDAV_SERVER_PASSWORD);
        http.getState().setCredentials(null, null, credentials);
        http.getState().setAuthenticationPreemptive(true);
        return http;
    }
    
    public HostConfiguration createHostConfiguration(){
        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(getCalDAVServerHost(), getCalDAVServerPort());
        return hostConfig;
    }
    
}
