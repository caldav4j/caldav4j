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

package com.github.caldav4j.functional.support;

import static com.github.caldav4j.support.HttpMethodCallbacks.nullCallback;

import com.github.caldav4j.BaseTestCase;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.TestConstants;
import com.github.caldav4j.credential.CaldavCredential;
import com.github.caldav4j.dialect.CalDavDialect;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpDeleteMethod;
import com.github.caldav4j.methods.HttpMkCalendarMethod;
import com.github.caldav4j.methods.HttpPutMethod;
import com.github.caldav4j.model.request.CalendarRequest;
import com.github.caldav4j.support.HttpClientTestUtils;
import com.github.caldav4j.support.HttpClientTestUtils.HttpMethodCallback;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.UrlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.webdav.client.methods.HttpMkcol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides fixture support for CalDAV functional tests.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public class CalDavFixture {
    // fields -----------------------------------------------------------------
    protected static final Logger log = LoggerFactory.getLogger(CalDavFixture.class);

    private HttpClient httpClient;

    private CalDAV4JMethodFactory methodFactory;

    private String collectionPath;

    private List<String> deleteOnTearDownPaths;

    private CalDavDialect dialect;

    private HttpHost hostConfig;

    // public methods ---------------------------------------------------------

    /**
     * Configure httpclient and eventually create the base calendar collection according to
     * CaldavDialect
     *
     * @param credential
     * @param dialect
     * @throws IOException
     */
    public void setUp(CaldavCredential credential, CalDavDialect dialect) throws IOException {
        setUp(credential, dialect, false);
    }

    public void setUp(
            CaldavCredential credential, CalDavDialect dialect, boolean skipCreateCollection)
            throws IOException {
        hostConfig = new HttpHost(credential.host, credential.port, credential.protocol);
        httpClient = configureHttpClient(credential);

        methodFactory = new CalDAV4JMethodFactory();
        collectionPath = UrlUtils.removeDoubleSlashes(credential.home + credential.collection);
        deleteOnTearDownPaths = new ArrayList<String>();
        this.dialect = dialect;

        // eventually make collection, unless skipCreateCollection
        if (!skipCreateCollection && (getDialect() != null) && getDialect().isCreateCollection()) {
            makeCalendar("");
        }
    }

    public void tearDown() throws IOException {
        // clean up in reverse order

        Collections.reverse(deleteOnTearDownPaths);

        for (String path : deleteOnTearDownPaths) {
            delete(path);
        }
    }

    public void makeCalendar(String relativePath) throws IOException {
        // Note Google Calendar Doesn't support creating a calendar
        HttpMkCalendarMethod method = methodFactory.createMkCalendarMethod(relativePath);
        method.setHeader(
                CalDAVConstants.HEADER_CONTENT_TYPE, CalDAVConstants.CONTENT_TYPE_CALENDAR);

        executeMethod(CalDAVStatus.SC_CREATED, method, true);
    }

    public void makeCollection(String relativePath) throws IOException {
        HttpMkcol method = new HttpMkcol(relativePath);

        executeMethod(CalDAVStatus.SC_CREATED, method, true);
    }

    public void putEvent(String relativePath, VEvent event) throws IOException {
        CalendarRequest cr = new CalendarRequest();
        cr.setCalendar(event);
        HttpPutMethod method = methodFactory.createPutMethod(relativePath, cr);

        executeMethod(CalDAVStatus.SC_CREATED, method, true);
    }

    public void delete(String relativePath) throws IOException {
        HttpDeleteMethod method = new HttpDeleteMethod(relativePath);

        executeMethod(CalDAVStatus.SC_NO_CONTENT, method, false);
    }

    public void delete(String path, boolean isAbsolutePath) throws IOException {
        HttpDeleteMethod method = new HttpDeleteMethod(path);

        executeMethod(CalDAVStatus.SC_NO_CONTENT, method, false, nullCallback(), isAbsolutePath);
    }

    public HttpResponse executeMethod(
            int expectedStatus, HttpRequestBase method, boolean deleteOnTearDown)
            throws IOException {
        return executeMethod(expectedStatus, method, deleteOnTearDown, nullCallback());
    }

    public <R, M extends HttpRequestBase, E extends Exception> R executeMethod(
            int expectedStatus,
            M method,
            boolean deleteOnTearDown,
            HttpMethodCallback<R, M, E> methodCallback)
            throws IOException, E {
        String relativePath = method.getURI().toString();

        // prefix path with collection path
        method.setURI(URI.create(collectionPath).resolve(method.getURI()));

        R response =
                HttpClientTestUtils.executeMethod(
                        expectedStatus, httpClient, method, methodCallback);

        if (deleteOnTearDown) {
            deleteOnTearDownPaths.add(relativePath);
        }

        return response;
    }

    public <R, M extends HttpRequestBase, E extends Exception> R executeMethod(
            int expectedStatus,
            M method,
            boolean deleteOnTearDown,
            HttpMethodCallback<R, M, E> methodCallback,
            boolean absolutePath)
            throws IOException, E {
        String relativePath = method.getURI().toString();

        // prefix path with collection path
        if (!absolutePath) {
            method.setURI(URI.create(collectionPath + relativePath));
        }

        R response =
                HttpClientTestUtils.executeMethod(
                        expectedStatus, httpClient, method, methodCallback);

        if (deleteOnTearDown) {
            deleteOnTearDownPaths.add(relativePath);
        }

        return response;
    }

    // private methods --------------------------------------------------------

    private static HttpClient configureHttpClient(final CaldavCredential credential) {
        // HttpClient 4 requires a Cred providers, to be added during creation of client
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(credential.user, credential.password));

        // Default Host setting
        HttpRoutePlanner routePlanner =
                new DefaultRoutePlanner(DefaultSchemePortResolver.INSTANCE) {

                    @Override
                    public HttpRoute determineRoute(
                            final HttpHost target,
                            final HttpRequest request,
                            final HttpContext context)
                            throws HttpException {
                        return super.determineRoute(
                                target != null
                                        ? target
                                        : new HttpHost(
                                                credential.host,
                                                credential.port,
                                                credential.protocol),
                                request,
                                context);
                    }
                };

        HttpClientBuilder builder =
                HttpClients.custom()
                        .setDefaultCredentialsProvider(credsProvider)
                        .setRoutePlanner(routePlanner);

        if (credential.getProxyHost() != null) {
            builder.setProxy(
                    new HttpHost(
                            credential.getProxyHost(),
                            (credential.getProxyPort() > 0) ? credential.getProxyPort() : 8080));
        }

        return builder.build();
    }

    public void setDialect(CalDavDialect dialect) {
        this.dialect = dialect;
    }

    public CalDavDialect getDialect() {
        return dialect;
    }

    protected void mkcalendar(String path) {

        try {
            HttpMkCalendarMethod mk =
                    new HttpMkCalendarMethod(path, null, TestConstants.CALENDAR_DESCRIPTION, "en");
            executeMethod(CalDAVStatus.SC_CREATED, mk, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void mkcol(String path) {
        HttpMkcol mk = new HttpMkcol(path);
        try {
            executeMethod(CalDAVStatus.SC_CREATED, mk, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * FIXME this put updates automatically the timestamp of the event
     * @param resourceFileName
     * @param path
     */
    public void put(String resourceFileName, String path) {
        HttpPutMethod put = methodFactory.createPutMethod(path, new CalendarRequest());
        put.setHeader(CalDAVConstants.HEADER_CONTENT_TYPE, CalDAVConstants.CONTENT_TYPE_CALENDAR);
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resourceFileName);
        String event = UrlUtils.parseISToString(stream);
        event = event.replaceAll("DTSTAMP:.*", "DTSTAMP:" + new DateTime(true).toString());
        log.debug(new DateTime(true).toString());
        // log.trace(event);

        try {
            put.setEntity(new StringEntity(event));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        log.debug("\nPUT " + put.getURI());
        try {
            HttpResponse response = executeMethod(CalDAVStatus.SC_CREATED, put, true);

            int statusCode = response.getStatusLine().getStatusCode();

            switch (statusCode) {
                case CalDAVStatus.SC_CREATED:
                case CalDAVStatus.SC_NO_CONTENT:
                    break;
                case CalDAVStatus.SC_PRECONDITION_FAILED:
                    log.error("item exists?");
                    break;
                case CalDAVStatus.SC_CONFLICT:
                    log.error("conflict: item still on server");
                default:
                    String respBody = EntityUtils.toString(response.getEntity());
                    log.error(respBody);
                    throw new Exception(
                            "trouble executing PUT of "
                                    + resourceFileName
                                    + "\nresponse:"
                                    + respBody);
            }
        } catch (Exception e) {
            log.info("Error while put():" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * remove an event on a caldav store using UID.ics
     *
     * @throws IOException
     */
    public void caldavDel(String s) throws IOException {
        String resPath = getCaldavPutPath(s);
        delete(resPath);
    }

    /** put an event on a caldav store using UID.ics */
    public void caldavPut(String s) {
        String resPath = getCaldavPutPath(s);

        put(s, resPath);
    }

    /** put an event on a caldav store using UID.ics This method returns the path assocuated. */
    /*public String getCaldavPutPath(String s) {
        Calendar cal = BaseTestCase.getCalendarResource(s);

        String resPath = // collectionPath + "/" +
                cal.getComponent("VEVENT").getProperty("UID").getValue() + ".ics";
        return resPath;
    }*/

    public String getCaldavPutPath(String s) {
        Calendar cal = BaseTestCase.getCalendarResource(s);

        // Safely unwrap the Optional and handle the case where the component is not present
        Optional<CalendarComponent> optionalComponent = cal.getComponent("VEVENT");

        if (optionalComponent.isPresent()) {
            Optional<Property> property = optionalComponent.get().getProperty("UID");

            if (property.isPresent()) {
                return property.get().getValue() + ".ics";
            }
        }

        return null;
    }

    public CalDAV4JMethodFactory getMethodFactory() {
        return methodFactory;
    }

    public void setMethodFactory(CalDAV4JMethodFactory methodFactory) {
        this.methodFactory = methodFactory;
    }

    public String getCollectionPath() {
        return collectionPath;
    }

    public void setCollectionPath(String collectionPath) {
        this.collectionPath = collectionPath;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpHost getHostConfig() {
        return hostConfig;
    }

    public void setHostConfig(HttpHost hostConfig) {
        this.hostConfig = hostConfig;
    }
}
