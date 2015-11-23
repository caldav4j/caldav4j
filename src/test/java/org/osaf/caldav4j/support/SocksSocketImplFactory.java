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
package org.osaf.caldav4j.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketImpl;
import java.net.SocketImplFactory;

/**
 * {@code SocketImplFactory} that produces {@code java.net.SocksSocketImpl} socket implementations.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id$
 */
class SocksSocketImplFactory implements SocketImplFactory
{
	// constants --------------------------------------------------------------
	
	private static final SocksSocketImplFactory INSTANCE = new SocksSocketImplFactory();
	
	private static final String SOCKET_IMPL_CLASS_NAME = "java.net.SocksSocketImpl";
	
	// SocketImplFactory methods ----------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public SocketImpl createSocketImpl()
	{
		try
		{
			Class<? extends SocketImpl> socketImplClass = Class.forName(SOCKET_IMPL_CLASS_NAME)
				.asSubclass(SocketImpl.class);
			
			Constructor<? extends SocketImpl> constructor = socketImplClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			
			return constructor.newInstance();
		}
		catch (ClassNotFoundException exception)
		{
			throw new IllegalStateException("Cannot find SocketImpl class: " + SOCKET_IMPL_CLASS_NAME);
		}
		catch (NoSuchMethodException exception)
		{
			throw new IllegalStateException("Missing default constructor for SocketImpl class: "
				+ SOCKET_IMPL_CLASS_NAME);
		}
		catch (InstantiationException exception)
		{
			throw new IllegalStateException("Error instantiating SocketImpl class: " + SOCKET_IMPL_CLASS_NAME,
				exception);
		}
		catch (IllegalAccessException exception)
		{
			throw new IllegalStateException("Error instantiating SocketImpl class: " + SOCKET_IMPL_CLASS_NAME,
				exception);
		}
		catch (InvocationTargetException exception)
		{
			throw new IllegalStateException("Error instantiating SocketImpl class: " + SOCKET_IMPL_CLASS_NAME,
				exception.getCause());
		}
	}
	
	// public methods ---------------------------------------------------------
	
	public static SocksSocketImplFactory get()
	{
		return INSTANCE;
	}
}
