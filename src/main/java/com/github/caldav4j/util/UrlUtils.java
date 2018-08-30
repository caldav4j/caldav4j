/*
 * Copyright 2007 Open Source Applications Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.caldav4j.util;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Contains the Utility functions for URLs
 */
public class UrlUtils {

	/**
	 * Strip hosts from the href.
	 *
	 * @param href Link to remove the host from
	 * @return Stripped string.
	 */
	public static String stripHost(String href){
		if (!href.startsWith("http")){
			return href;
		}
		int indexOfColon = href.indexOf(":");
		int index = href.indexOf("/", indexOfColon + 3);
		return href.substring(index);
	}

	/**
	 * Remove Double Slashes from given string.
	 *
	 * @param s Input String
	 * @return String with Double Slashes removed.
	 */
	public static String removeDoubleSlashes(String s) {
		return s.replaceAll("([^:])/{2,}", "$1/");
	}

	public static String ensureTrailingSlash(String s) {
		return (s.endsWith("/"))? s.concat("/") : s;
	}

	/**
	 * Return the Value of the Header
	 *
	 * @param response     HTTP Response
	 * @param headerName Name of header
	 * @return Value of header
	 */
	public static String getHeaderPrettyValue(HttpResponse response, String headerName) {
		if (response != null && headerName != null) {
			Header header  = response.getFirstHeader(headerName);
			if (header != null) {
				return header.getValue();
			}
		}

		return null;
	}

	/**
	 * Parse {@link InputStream} to String
	 * @param is {@link InputStream} object
	 * @return String from the object
	 */
	public static String parseISToString(InputStream is){
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader din = new BufferedReader(new InputStreamReader(is));

			String line = null;
			while((line=din.readLine()) != null){
				sb.append(line);
				sb.append("\n");
			}
		} catch(Exception ex){
			ex.getMessage();
		}finally{
			try{
				is.close();
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}

	/**
	 * Compare two strings without while ignoring their case.
	 *
	 * @param str1 String 1
	 * @param str2 String 2
	 * @return True if equal
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
	}

	/**
	 * @param str String to check.
	 * @param defaultStr Default String.
	 * @return Returns default string if string is null.
	 */
	public static String defaultString(String str, String defaultStr) {
		return (str == null ? defaultStr : str);
	}

	/**
	 * Checks if given String is blank.
	 * @param str String to check
	 * @return True if blank.
	 */
	public static boolean isBlank(String str) {
		return (str == null || (str.length()) == 0 || "".equals(str.trim()));
	}

	/**
	 * Checks if given String is not blank.
	 * @param str String to check
	 * @return True if not blank.
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
}
