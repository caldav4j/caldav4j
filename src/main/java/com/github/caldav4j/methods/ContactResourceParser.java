package com.github.caldav4j.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import com.github.caldav4j.exceptions.ResourceParserException;
import com.github.caldav4j.model.ContactInfo;

public class ContactResourceParser implements ResourceParser<ContactInfo> {

	@Override
	public ContactInfo read(InputStream in) throws ResourceParserException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(ContactInfo resource, Writer writer) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getResponseContentType() {
		throw new UnsupportedOperationException();
	}

}
