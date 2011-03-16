package org.osaf.caldav4j.functional;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.osaf.caldav4j.credential.BedeworkCaldavCredential;
import org.osaf.caldav4j.credential.CaldavCredential;
import org.osaf.caldav4j.dialect.BedeworkCalDavDialect;
import org.osaf.caldav4j.dialect.CalDavDialect;
import org.osaf.caldav4j.dialect.ChandlerCalDavDialect;

@RunWith(Parameterized.class)
public class FreeBusyQueryFunctionalTestSuite extends FreeBusyQueryFunctionalTest {
	public FreeBusyQueryFunctionalTestSuite(CaldavCredential credential,
			CalDavDialect dialect) {
		super(credential, dialect);
	}

	@Parameters
	public static Collection<Object[]> getCaldavCredentials() {
		return Arrays.asList(new Object[][] {
				{new CaldavCredential(), new ChandlerCalDavDialect()}
				// ,{new BedeworkCaldavCredential(), new BedeworkCalDavDialect()}
				// TODO Google doesn't support FB
				// , {new GCaldavCredential(), new GoogleCalDavDialect()} 

		});
	}

	
}
