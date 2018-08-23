package com.github.caldav4j.methods.functional;

import java.util.Arrays;
import java.util.Collection;

import com.github.caldav4j.credential.BedeworkCaldavCredential;
import com.github.caldav4j.credential.CaldavCredential;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.github.caldav4j.dialect.BedeworkCalDavDialect;
import com.github.caldav4j.dialect.CalDavDialect;
import com.github.caldav4j.dialect.ChandlerCalDavDialect;
import com.github.caldav4j.methods.OptionsTest;

@RunWith(Parameterized.class)
public class OptionITCase extends OptionsTest {
	
    public OptionITCase(CaldavCredential credential, CalDavDialect dialect) {
    	this.caldavCredential = credential;
    	this.caldavDialect = dialect;	
    }

	@Parameters
	public static Collection<Object[]> getCaldavCredentials() {
		return Arrays.asList(new Object[][] {
				{new BedeworkCaldavCredential(), new BedeworkCalDavDialect()},
				{new CaldavCredential(), new ChandlerCalDavDialect()}
		});
	}
}
