package org.osaf.caldav4j.util;

import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.security.Principal;


/**
 * Ace methods for easy Principal settings
 * @author rpolli
 *
 */
public class AceUtils {

	/**
	 * Create an ACE given a Principal
	 * @param p
	 * @return
	 */
/*	public static Ace createAce(Principal p) {
		Ace a = null;
		if (p.getPropertyName()!=null) {
			a = new Ace("property");
			a.setProperty(p.getPropertyName());
		} else if (p.isAll()||p.isAuthenticated()||p.isSelf()||p.isUnauthenticated()) {
			a= new Ace(p.getValue());
		}
		return a;
	}*/

	/**
	 * Retrieve a Caldav Principal from a slide Ace.
	 * If  ace.getprincipal is set to "property", it returns directly the underlying property
	 * @param ace
	 * @return
	 */
	public static Principal getDavPrincipal(AclProperty.Ace ace) {

		return ace.getPrincipal();
	}
}
