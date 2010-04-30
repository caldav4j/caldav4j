package org.osaf.caldav4j.model.response;

import org.apache.webdav.lib.PropertyName;

/*
Principal encapsulates the DAV:principal element which identifies the principal to which this ACE applies. RFC 3744 defines the following structure for this element:

	 <!ELEMENT principal (href | all | authenticated | unauthenticated | property | self)>
*/
public class Principal  {

	private String href;
	private PropertyName propertyName;
	private boolean all, authenticated, unauthenticated, self;
	
	private String[] principalValues = new String[] {"all", "authenticated", "unauthenticated", "self"};

	public Principal() {
		super();
	}
	public Principal(String defaultPrincipal) {
		super();
		for (int i=0;i<principalValues.length;i++) {
			switch (i) {
			case 0:
				setAll(true);
				break;
			case 1:
				setAuthenticated(true);
					break;
			case 2:
				setUnauthenticated(true);
				break;
			case 3:
				setSelf(true);
			default:
				break;
			}
		}		
	}
	public boolean isAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
	}
	public boolean isAuthenticated() {
		return authenticated;
	}
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	public boolean isSelf() {
		return self;
	}
	public void setSelf(boolean self) {
		this.self = self;
	}
	public String getHref() {
		return href;
	};
	public PropertyName getPropertyName() {
		return propertyName;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	public void setPropertyName(PropertyName property) {
		this.propertyName = property;
	}
	public void setUnauthenticated(boolean unauthenticated) {
		this.unauthenticated = unauthenticated;
	}
	public boolean isUnauthenticated() {
		return unauthenticated;
	}
	
}
