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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.util.HashMap;
import java.util.Map;

/**
 * Fake {@code SocketImpl} test double that expects a given output and produces a stub input.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id: FakeSocketImpl.java 294 2011-02-22 11:50:25Z markhobson $
 */
class FakeSocketImpl extends SocketImpl
{
	// fields -----------------------------------------------------------------
	
	private final InputStream expectedOutput;
	
	private final InputStream input;
	
	private final ByteArrayOutputStream output;
	
	private final Map<Integer, Object> optionValuesById;
	
	// constructors -----------------------------------------------------------
	
	public FakeSocketImpl(InputStream expectedOutput, InputStream input)
	{
		this.expectedOutput = expectedOutput;
		this.input = input;
		
		output = new ByteArrayOutputStream();
		optionValuesById = new HashMap<Integer, Object>();
	}

	// SocketImpl methods -----------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void create(boolean stream) throws IOException
	{
		// no-op
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void connect(String host, int port) throws IOException
	{
		// no-op
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void connect(InetAddress address, int port) throws IOException
	{
		// no-op
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void connect(SocketAddress address, int timeout) throws IOException
	{
		// no-op
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void bind(InetAddress host, int port) throws IOException
	{
		// no-op
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void listen(int backlog) throws IOException
	{
		// no-op
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void accept(SocketImpl s) throws IOException
	{
		// no-op
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected InputStream getInputStream() throws IOException
	{
		return input;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OutputStream getOutputStream() throws IOException
	{
		return output;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int available() throws IOException
	{
		return getInputStream().available();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void close() throws IOException
	{
		getInputStream().close();
		getOutputStream().close();
		
		// TODO: use explicit encoding?
		assertEquals("Output", new String(toBytes(expectedOutput)), new String(output.toByteArray()));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void sendUrgentData(int data) throws IOException
	{
		// TODO: implement?
		throw new UnsupportedOperationException();
	}
	
	// SocketOptions methods --------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public void setOption(int optID, Object value) throws SocketException
	{
		optionValuesById.put(optID, value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object getOption(int optID) throws SocketException
	{
		return optionValuesById.get(optID);
	}
	
	// private methods --------------------------------------------------------
	
	private static byte[] toBytes(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		byte[] buffer = new byte[1024 * 4];
		int n;
		
		while ((n = in.read(buffer, 0, buffer.length)) != -1)
		{
			out.write(buffer, 0, n);
		}
		
		return out.toByteArray();
	}
}
