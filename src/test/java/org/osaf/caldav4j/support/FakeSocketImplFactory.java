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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.SocketImpl;
import java.net.SocketImplFactory;

/**
 * Fake {@code SocketImplFactory} test double that creates {@code FakeSocketImpl}s.
 * 
 * @author Mark Hobson
 * @version $Id: FakeSocketImplFactory.java 294 2011-02-22 11:50:25Z markhobson $
 * @see FakeSocketImpl
 */
class FakeSocketImplFactory implements SocketImplFactory
{
	// fields -----------------------------------------------------------------
	
	private InputStream expectedOutput;
	
	private InputStream input;
	
	// SocketImplFactory methods ----------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public SocketImpl createSocketImpl()
	{
		return new FakeSocketImpl(expectedOutput, input);
	}
	
	// public methods ---------------------------------------------------------
	
	public InputStream getExpectedOutput()
	{
		return expectedOutput;
	}
	
	public void setExpectedOutput(String expectedOutput)
	{
		setExpectedOutput(toInputStream(expectedOutput));
	}
	
	public void setExpectedOutput(InputStream expectedOutput)
	{
		this.expectedOutput = expectedOutput;
	}
	
	public InputStream getInput()
	{
		return input;
	}
	
	public void setInput(String input)
	{
		setInput(toInputStream(input));
	}
	
	public void setInput(InputStream input)
	{
		this.input = input;
	}
	
	// private methods --------------------------------------------------------
	
	private static InputStream toInputStream(String string)
	{
		// TODO: use explicit encoding?
		return new ByteArrayInputStream(string.getBytes());
	}
}
