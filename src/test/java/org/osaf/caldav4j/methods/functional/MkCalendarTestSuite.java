package org.osaf.caldav4j.methods.functional;

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
import org.osaf.caldav4j.methods.MkCalendarTest;

@RunWith(Parameterized.class)
public class MkCalendarTestSuite extends MkCalendarTest {
	
    public MkCalendarTestSuite(CaldavCredential credential, CalDavDialect dialect) {
    	this.caldavCredential = credential;
    	this.caldavDialect = dialect;	
    }

	@Parameters
	public static Collection<Object[]> getCaldavCredentials() {
		return Arrays.asList(new Object[][] {
			//	{new BedeworkCaldavCredential(), new BedeworkCalDavDialect()},
				{new CaldavCredential(), new ChandlerCalDavDialect()}
		});
	}
}
