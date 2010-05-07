/**
 * 
 */
package org.osaf.caldav4j.example;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rpolli
 *
 */
public class ParseQueryTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String aa = "a : b : c";
		String c[] = aa.split(";",2);
		
		Pattern f = Pattern.compile("(.+?)([!=]=)(\\[(.*?),(.*?)\\]|([^\\]].+))");
		Pattern intervalPattern = Pattern.compile("\\[(.*?),(.*?)\\]");
		
		List<String> a = new ArrayList<String> ();
		a.add("UID==[1231,]");
		a.add("X-PLUTO-SPADA==fsdfsdfds");
		a.add("UID!=CDSDafsd");
		a.add("DTSTART==[1231,321423]");
		a.add("DTSTART==[,321423]");
		for (String b:a) {
			
			Matcher expressionMatcher = f.matcher(b);
			if (expressionMatcher.matches()) {
				if (expressionMatcher.groupCount() == 5) {
					// time-range 
				} else if (expressionMatcher.groupCount() == 3) {
					// simple matcher
				} else {
					throw new Exception("Bad Query syntax:" + b);
				}
				
				
				String z = expressionMatcher.group(0);
				System.out.println("expression= "+z);
				System.out.println("\tfield: "+expressionMatcher.group(1));
				System.out.println("\taction: "+expressionMatcher.group(2));
				System.out.println("\tvalue: "+expressionMatcher.group(3));
				System.out.println("v4: "+expressionMatcher.group(4));
				System.out.println("v5: "+expressionMatcher.group(5));

			}
		}
		
	}

}
