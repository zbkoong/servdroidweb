package org.servDroid.util;

import java.util.ArrayList;
import java.util.Iterator;

public class Encoding {

	private static ArrayList<String[]> specialCharacters;
	static {
		specialCharacters = new ArrayList<String[]>();
		specialCharacters.add(new String[] { " ", "%20" });
		specialCharacters.add(new String[] { "$", "%24" });
		specialCharacters.add(new String[] { "&", "%26" });
		specialCharacters.add(new String[] { "`", "%60" });
		specialCharacters.add(new String[] { ":", "%3A" });
		specialCharacters.add(new String[] { "<", "%3C" });
		specialCharacters.add(new String[] { ">", "%3E" });
		specialCharacters.add(new String[] { "[", "%5B" });
		specialCharacters.add(new String[] { "]", "%5D" });
		specialCharacters.add(new String[] { "{", "%7B" });
		specialCharacters.add(new String[] { "}", "%7D" });
		specialCharacters.add(new String[] { "\"", "%22" });
		specialCharacters.add(new String[] { "+", "%2B" });
		specialCharacters.add(new String[] { "#", "%23" });
		specialCharacters.add(new String[] { "%", "%25" });
		specialCharacters.add(new String[] { "@", "%40" });
		specialCharacters.add(new String[] { "/", "%2F" });
		specialCharacters.add(new String[] { ";", "%3B" });
		specialCharacters.add(new String[] { "=", "%3D" });
		specialCharacters.add(new String[] { "?", "%3F" });
		specialCharacters.add(new String[] { "\\", "%5C" });
		specialCharacters.add(new String[] { "^", "%5E" });
		specialCharacters.add(new String[] { "|", "%7C" });
		specialCharacters.add(new String[] { "~", "%7E" });
		specialCharacters.add(new String[] { "'", "%27" });
		specialCharacters.add(new String[] { ",", "%2C" });
	}

	public static String codeURL(String string) {
		Iterator<String[]> iterator = specialCharacters.iterator();

		while (iterator.hasNext()) {
			String[] strings = (String[]) iterator.next();
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

}
