package com.github.caldav4j.methods;

import com.github.caldav4j.model.ContactInfo;

public class CardDAVMethodFactory extends DAVMethodFactory<ContactInfo> {

	@Override
	protected ResourceParser<ContactInfo> buildResourceParser() {
		return new ContactResourceParser();
	}

}
