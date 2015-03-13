/*
 * Copyright 2011 Open Source Applications Foundation
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
package org.osaf.caldav4j.dialect;

import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * A {@code CalDavDialect} for Chandler's Cosmo CalDAV server.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id: ChandlerCalDavDialect.java 305 2011-02-23 10:59:11Z robipolli@gmail.com $
 */
public class ChandlerCalDavDialect implements CalDavDialect
{
	// constants --------------------------------------------------------------
	
	private static final String PROD_ID_VALUE = "-//Open Source Applications Foundation//NONSGML Chandler Server//EN";
	
	// constructors -----------------------------------------------------------
	
	public ChandlerCalDavDialect()
	{
		// Chandler returns invalid newlines on wrapped properties
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
	}
	
	// CalDavDialect methods --------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public ProdId getProdId()
	{
		return new ProdId(PROD_ID_VALUE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public CalScale getDefaultCalScale()
	{
		return CalScale.GREGORIAN;
	}

	@Override
	public boolean isCreateCollection() {
		return true;
	}
}
