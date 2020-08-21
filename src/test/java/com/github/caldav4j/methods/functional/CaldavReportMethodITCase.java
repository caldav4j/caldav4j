package com.github.caldav4j.methods.functional;

import com.github.caldav4j.credential.BedeworkCaldavCredential;
import com.github.caldav4j.credential.CaldavCredential;
import com.github.caldav4j.dialect.BedeworkCalDavDialect;
import com.github.caldav4j.dialect.CalDavDialect;
import com.github.caldav4j.dialect.ChandlerCalDavDialect;
import com.github.caldav4j.methods.CalDAVReportTest;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CaldavReportMethodITCase extends CalDAVReportTest {

    public CaldavReportMethodITCase(CaldavCredential credential, CalDavDialect dialect) {
        this.caldavCredential = credential;
        this.caldavDialect = dialect;
    }

    @Parameters
    public static Collection<Object[]> getCaldavCredentials() {
        return Arrays.asList(
                new Object[][] {
                    {new BedeworkCaldavCredential(), new BedeworkCalDavDialect()},
                    {new CaldavCredential(), new ChandlerCalDavDialect()}
                });
    }
}
