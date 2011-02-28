package org.osaf.caldav4j.exceptions;

public class MethodFactoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5414405598035696033L;

	public MethodFactoryException(Throwable cause) {
		super(cause);
	}

	public MethodFactoryException(String s) {
		super(s);
	}
	/*
	 public public MethodFactoryException(String fmt, Object...objects) {

		super(String.format(fmt, objects));
	}
	 */
}
