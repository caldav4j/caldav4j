/*
 * Copyright 2011 Open Source Applications Foundation
 * Copyright © 2018 Mark Hobson, Roberto Polli
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

import java.net.SocketImpl;
import java.net.SocketImplFactory;

/**
 * {@code SocketImplFactory} that delegates to another implementation.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
class DelegatingSocketImplFactory implements SocketImplFactory {
    // fields -----------------------------------------------------------------

    private SocketImplFactory delegate;

    // constructors -----------------------------------------------------------

    public DelegatingSocketImplFactory(SocketImplFactory delegate) {
        setDelegate(delegate);
    }

    // SocketImplFactory methods ----------------------------------------------

    /** {@inheritDoc} */
    public SocketImpl createSocketImpl() {
        return delegate.createSocketImpl();
    }

    // public methods ---------------------------------------------------------

    public SocketImplFactory getDelegate() {
        return delegate;
    }

    public void setDelegate(SocketImplFactory delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate cannot be null");
        }

        this.delegate = delegate;
    }
}
