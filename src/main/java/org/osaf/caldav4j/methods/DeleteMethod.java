/*
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

package org.osaf.caldav4j.methods;

import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.UrlUtils;

/**
 * Method which Implements the HTTP DELETE method along with Etag setting.
 */
public class DeleteMethod extends org.apache.jackrabbit.webdav.client.methods.DeleteMethod {

    /**
     * Default Constructor to delete at location specified by uri.
     * @param uri The location to the resource to be deleted.
     */
	public DeleteMethod(String uri) {
		super(UrlUtils.removeDoubleSlashes(uri));
	}

    /**
     * Constructor which takes in ETag along with URI.
     * This is for deleting a resource only with the specific ETag
     * @param uri Location to the resource on the server
     * @param etag ETag of the resource.
     */
    public DeleteMethod(String uri, String etag){
        super(UrlUtils.removeDoubleSlashes(uri));
        setETag(etag);
    }

	/**
	 * @see org.apache.commons.httpclient.HttpMethodBase#setPath(String)
	 */
	public void setPath(String path) {
    	super.setPath(UrlUtils.removeDoubleSlashes(path));
    }

    /**
     * Sets the Request ETag for the DELETE method.
     * @param etag ETag to set
     */
    public void setETag(String etag){
        if(etag != null && !etag.equals(""))
            setRequestHeader(CalDAVConstants.HEADER_IF_MATCH, etag);
    }
}
