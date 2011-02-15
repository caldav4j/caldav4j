package org.osaf.caldav4j.util;

import static org.junit.Assert.*;

import org.junit.*;

public class UrlUtilsTest  {
	
	@Test
	public void stripDoubleSlashes() {
	
		String uris[] = {"http://a/b/c/", "http://a//b/c//",  "http:///a/b/c///", "http://a///b/c/" };
		String EXPECTED = "http://a/b/c/";
		for (String s : uris) {
			String s1 = UrlUtils.removeDoubleSlashes(s);
			assertEquals(EXPECTED,s1);
			assertEquals(EXPECTED, UrlUtils.removeDoubleSlashes(s1));
		}
	}
	@Test
	public void stripDoesntRegress() {
	
		String uris[] = {"/a/b/c/", "/a//b/c//",  "/a///b/c///", "/a///b////c///" };
		String EXPECTED = "/a/b/c/";
		for (String s : uris) {
			String s1 = UrlUtils.removeDoubleSlashes(s);
			assertEquals(EXPECTED,s1);
			assertEquals(EXPECTED, UrlUtils.removeDoubleSlashes(s1));
		}
	}

}
