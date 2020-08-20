/*
 * Copyright 2011 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Mark Hobson
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

package com.github.caldav4j.support;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketImplFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;

/**
 * Provides support for testing with HttpClient.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
public final class HttpClientTestUtils {
    // types ------------------------------------------------------------------

    public interface HttpMethodCallback<R, M extends HttpRequestBase, E extends Exception> {
        R getResponse(M method, HttpResponse httpResponse) throws E;
    }

    // constants --------------------------------------------------------------

    private static DelegatingSocketImplFactory delegatingSocketImplFactory;

    // constructors -----------------------------------------------------------

    private HttpClientTestUtils() {
        throw new AssertionError();
    }

    // public methods ---------------------------------------------------------

    public static void setFakeSocketImplFactory() throws IOException {
        setSocketImplFactory(new FakeSocketImplFactory());
    }

    public static void unsetFakeSocketImplFactory() throws IOException {
        setSocketImplFactory(SocksSocketImplFactory.get());
    }

    public static void setFakeSocketImpl(String expectedRequest, String response) {
        SocketImplFactory factory = getSocketImplFactory();

        if (!(factory instanceof FakeSocketImplFactory)) {
            throw new IllegalStateException(
                    "Call setFakeSocketImplFactory() before setFakeSocketImpl(String, String)");
        }

        FakeSocketImplFactory fakeFactory = (FakeSocketImplFactory) factory;
        fakeFactory.setExpectedOutput(expectedRequest);
        fakeFactory.setInput(response);
    }

    public static <R, M extends HttpRequestBase, E extends Exception> R executeMethod(
            int expectedStatus,
            HttpClient httpClient,
            M method,
            HttpMethodCallback<R, M, E> methodCallback)
            throws IOException, E {
        try {
            HttpResponse response = httpClient.execute(method);
            int actualStatus = response.getStatusLine().getStatusCode();
            assertEquals("Response status", expectedStatus, actualStatus);
            if (methodCallback != null) return methodCallback.getResponse(method, response);
            else return null;
        } finally {
            method.reset();
        }
    }

    public static <R, M extends HttpRequestBase, E extends Exception> R executeMethod(
            int expectedStatus, M method, HttpMethodCallback<R, M, E> methodCallback)
            throws IOException, E {
        HttpHost host = new HttpHost("localhost", 80);
        HttpClient client = HttpClients.createDefault();
        return executeMethod(expectedStatus, method, client, host, methodCallback);
    }

    public static <R, M extends HttpRequestBase, E extends Exception> R executeMethod(
            int expectedStatus,
            M method,
            HttpClient httpClient,
            HttpHost httpHost,
            HttpMethodCallback<R, M, E> methodCallback)
            throws IOException, E {
        try {
            HttpResponse response = httpClient.execute(httpHost, method);
            int actualStatus = response.getStatusLine().getStatusCode();
            assertEquals("Response status", expectedStatus, actualStatus);

            return (methodCallback != null) ? methodCallback.getResponse(method, response) : null;
        } finally {
            method.reset();
        }
    }

    // private methods --------------------------------------------------------

    private static SocketImplFactory getSocketImplFactory() {
        if (delegatingSocketImplFactory == null) {
            return null;
        }

        return delegatingSocketImplFactory.getDelegate();
    }

    private static void setSocketImplFactory(SocketImplFactory factory) throws IOException {
        if (delegatingSocketImplFactory == null) {
            delegatingSocketImplFactory = new DelegatingSocketImplFactory(factory);

            // this can only be called once per JVM instance
            Socket.setSocketImplFactory(delegatingSocketImplFactory);
        } else {
            delegatingSocketImplFactory.setDelegate(factory);
        }
    }
}
