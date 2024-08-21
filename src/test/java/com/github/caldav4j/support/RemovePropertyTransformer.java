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

import java.util.Arrays;
import java.util.List;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.transform.Transformer;

/**
 * Calendar transformer that removes specified properties.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
class RemovePropertyTransformer implements Transformer<Calendar> {
    // fields -----------------------------------------------------------------

    private final List<String> propertyNames;

    // constructors -----------------------------------------------------------

    public RemovePropertyTransformer(String... propertyNames) {
        this.propertyNames = Arrays.asList(propertyNames);
    }

    // Transformer methods ----------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public Calendar transform(Calendar calendar) {
        Calendar newCalendar = deepCopy(calendar);

        apply(newCalendar.getPropertyList());
        apply(newCalendar.getPropertyList());

        return newCalendar;
    }

    // private methods --------------------------------------------------------

    private static Calendar deepCopy(Calendar calendar) {
        return new Calendar(calendar);
    }

    @SuppressWarnings("unchecked")
    private void apply(PropertyList properties) {
        for (String propertyName : propertyNames) {
            properties.removeAll(propertyName);
        }
    }

    private void apply(ComponentList<Component> components) {
        for (Component component : components.getAll()) {
            apply(component);
        }
    }

    private void apply(Component component) {
        apply(component.getPropertyList());
    }

    @Override
    public Calendar apply(Calendar calendar) {
        apply(calendar.getPropertyList());
        return calendar;
    }
}
