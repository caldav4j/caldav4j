package com.github.caldav4j.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.Writer;

import com.github.caldav4j.exceptions.ResourceParserException;

public interface ResourceParser<T extends Serializable> {

	T read(InputStream in) throws ResourceParserException, IOException;
	
	void write(T resource, Writer writer) throws IOException;

	String getResponseContentType();
	
}
