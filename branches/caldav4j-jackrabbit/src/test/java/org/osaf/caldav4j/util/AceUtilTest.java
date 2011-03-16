package org.osaf.caldav4j.util;

import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.security.AclProperty.Ace;
import org.apache.jackrabbit.webdav.security.Principal;
import org.apache.jackrabbit.webdav.security.Privilege;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.junit.Test;


public class AceUtilTest {

	@Test
	public void createAce() {
		Principal principal = Principal.getAuthenticatedPrincipal();
		Privilege privileges[] = new Privilege[] {};
		privileges[0] =  Privilege.getPrivilege("read", Namespace.XML_NAMESPACE);
		boolean invert = true;
		boolean isProtected = false;
		
		Ace a = AclProperty.createGrantAce(principal, privileges, invert, isProtected, null);
		System.out.println(a.toString());
		
	}
}
