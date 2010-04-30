package org.osaf.caldav4j.model.response;

import org.osaf.caldav4j.CalDAVConstants;


public class Ace extends org.apache.webdav.lib.Ace {

	public Ace(String principal) {
		super(principal);
	}

	public Ace(String principal, boolean negative, boolean protectedAce,
			boolean inherited, String inheritedFrom) {
		super(principal, negative, protectedAce, inherited, inheritedFrom);
	}

	public Ace(String principal, boolean negative, boolean protectedAce,
			boolean inheritable) {
		super(principal, negative, protectedAce, inheritable);
	}

	public static Ace createAce(Principal p) {
		Ace a = null;
		if (p.getPropertyName()!=null) {
			a = new Ace("property");
			a.setProperty(p.getPropertyName());
		} 
		return a;		
	}
	
	
	public Principal getDavPrincipal() {
		Principal p = new Principal();
		String pString = super.getPrincipal();
		if ("property".equals(pString)) {
			p.setPropertyName(super.getProperty());			
		}
		return p;
	}
}
