/*
 * Copyright 2006 Open Source Applications Foundation
 * Copyright © 2018 Ankush Mishra, Roberto Polli
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
package com.github.caldav4j;

import com.github.caldav4j.util.UrlUtils;
import java.io.Serializable;

/**
 * Serializable Metadata for each {@link CalDAVResource} containing currently the ETag, and Href of
 * the Resource.
 */
public class ResourceMetadata implements Serializable {

    private static final long serialVersionUID = -3385356629201926900L;
    private String eTag = null;
    private String href = null;

    public String getETag() {
        return eTag;
    }

    public void setETag(String tag) {
        eTag = tag;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = UrlUtils.removeDoubleSlashes(href);
    }
}
