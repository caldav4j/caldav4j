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
package com.github.caldav4j.xml;

import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import com.github.caldav4j.exceptions.DOMValidationException;
import org.w3c.dom.Document;

/**
 * Class for easy implementation and extension of XML properties.
 */
public interface OutputsDOM extends XmlSerializable {

	/**
	 * Interface for creating new documents in XML
	 *
	 * @return Resultant XML Dom document
	 * @throws DOMValidationException on error
	 */
	public Document createNewDocument() throws DOMValidationException;

	/**
	 * For the validation of the current representation.
	 * @throws DOMValidationException on error
	 */
	public void validate() throws DOMValidationException;
}
