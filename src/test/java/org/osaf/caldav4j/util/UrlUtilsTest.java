package org.osaf.caldav4j.util;

import static org.junit.Assert.*;

import org.junit.*;

public class UrlUtilsTest  {
	
	@Test
	public void stripDoubleSlashes() {
	
		String uris[] = {"http://a/b/c/", "http://a//b/c//",  "http:///a/b/c///", "http://a///b/c/" };
		String EXPECTED = "http://a/b/c/";
		for (String s : uris) {
			assertEquals(EXPECTED, UrlUtils.removeDoubleSlashes(s));
		}
	}

}
