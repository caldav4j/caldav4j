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
package com.github.caldav4j.dialect;

import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;

/**
 * A {@code CalDavDialect} for Google CalDAV server.
 *
 * @author <a href="mailto:robipolli@gmail.com">Roberto Polli</a>
 */
public class GoogleCalDavDialect implements CalDavDialect {
    // constants --------------------------------------------------------------

    private static final String PROD_ID_VALUE = "-//Google Inc//Google Calendar 70.9054//EN";

    // constructors -----------------------------------------------------------

    public GoogleCalDavDialect() {
        // CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
    }

    // CalDavDialect methods --------------------------------------------------

    /** {@inheritDoc} */
    public ProdId getProdId() {
        return new ProdId(PROD_ID_VALUE);
    }

    /** {@inheritDoc} */
    public CalScale getDefaultCalScale() {
        return CalScale.GREGORIAN;
    }

    @Override
    public boolean isCreateCollection() {
        return false;
    }
}
