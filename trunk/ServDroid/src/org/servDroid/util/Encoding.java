package org.servDroid.util;

import java.util.ArrayList;
import java.util.Iterator;

public class Encoding {

//	private static ArrayList<String[]> specialCharacters;
//	static {
//		specialCharacters = new ArrayList<String[]>();
//
//		specialCharacters.add(new String[] { "%", "%25" });
//		specialCharacters.add(new String[] { " ", "%20" });
//		specialCharacters.add(new String[] { "!", "%21" });
//		specialCharacters.add(new String[] { "\"", "%22" });
//		// specialCharacters.add(new String[] { "#", "%23" });
//		// specialCharacters.add(new String[] { "$", "%24" });
//
//		// specialCharacters.add(new String[] { "&", "%26" });
//		specialCharacters.add(new String[] { "'", "%27" });
//		specialCharacters.add(new String[] { "(", "%28" });
//		specialCharacters.add(new String[] { ")", "%29" });
//		specialCharacters.add(new String[] { "*", "%2A" });
//		// specialCharacters.add(new String[] { "+", "%2B" });
//		specialCharacters.add(new String[] { ",", "%2C" });
//		// specialCharacters.add(new String[] { "-", "%2D" });
//		// specialCharacters.add(new String[] { ".", "%2E" });
//		// specialCharacters.add(new String[] { "/", "%2F" });
//		// specialCharacters.add(new String[] { "0", "%30" });
//		// specialCharacters.add(new String[] { "1", "%31" });
//		// specialCharacters.add(new String[] { "2", "%32" });
//		// specialCharacters.add(new String[] { "3", "%33" });
//		// specialCharacters.add(new String[] { "4", "%34" });
//		// specialCharacters.add(new String[] { "5", "%35" });
//		// specialCharacters.add(new String[] { "6", "%36" });
//		// specialCharacters.add(new String[] { "7", "%37" });
//		// specialCharacters.add(new String[] { "8", "%38" });
//		// specialCharacters.add(new String[] { "9", "%39" });
//		specialCharacters.add(new String[] { ":", "%3A" });
//		specialCharacters.add(new String[] { ";", "%3B" });
//		specialCharacters.add(new String[] { "<", "%3C" });
//		// specialCharacters.add(new String[] { "=", "%3D" });
//		specialCharacters.add(new String[] { ">", "%3E" });
//		// specialCharacters.add(new String[] { "?", "%3F" });
//		specialCharacters.add(new String[] { "@", "%40" });
//		// specialCharacters.add(new String[] { "A", "%41" });
//		// specialCharacters.add(new String[] { "B", "%42" });
//		// specialCharacters.add(new String[] { "C", "%43" });
//		// specialCharacters.add(new String[] { "D", "%44" });
//		// specialCharacters.add(new String[] { "E", "%45" });
//		// specialCharacters.add(new String[] { "F", "%46" });
//		// specialCharacters.add(new String[] { "G", "%47" });
//		// specialCharacters.add(new String[] { "H", "%48" });
//		// specialCharacters.add(new String[] { "I", "%49" });
//		// specialCharacters.add(new String[] { "J", "%4A" });
//		// specialCharacters.add(new String[] { "K", "%4B" });
//		// specialCharacters.add(new String[] { "L", "%4C" });
//		// specialCharacters.add(new String[] { "M", "%4D" });
//		// specialCharacters.add(new String[] { "N", "%4E" });
//		// specialCharacters.add(new String[] { "O", "%4F" });
//		// specialCharacters.add(new String[] { "P", "%50" });
//		// specialCharacters.add(new String[] { "Q", "%51" });
//		// specialCharacters.add(new String[] { "R", "%52" });
//		// specialCharacters.add(new String[] { "S", "%53" });
//		// specialCharacters.add(new String[] { "T", "%54" });
//		// specialCharacters.add(new String[] { "U", "%55" });
//		// specialCharacters.add(new String[] { "V", "%56" });
//		// specialCharacters.add(new String[] { "W", "%57" });
//		// specialCharacters.add(new String[] { "X", "%58" });
//		// specialCharacters.add(new String[] { "Y", "%59" });
//		// specialCharacters.add(new String[] { "Z", "%5A" });
//		specialCharacters.add(new String[] { "[", "%5B" });
//		specialCharacters.add(new String[] { "\\", "%5C" });
//		specialCharacters.add(new String[] { "]", "%5D" });
//		specialCharacters.add(new String[] { "^", "%5E" });
//		// specialCharacters.add(new String[] { "_", "%5F" });
//		specialCharacters.add(new String[] { "`", "%60" });
//		// specialCharacters.add(new String[] { "a", "%61" });
//		// specialCharacters.add(new String[] { "b", "%62" });
//		// specialCharacters.add(new String[] { "c", "%63" });
//		// specialCharacters.add(new String[] { "d", "%64" });
//		// specialCharacters.add(new String[] { "e", "%65" });
//		// specialCharacters.add(new String[] { "f", "%66" });
//		// specialCharacters.add(new String[] { "g", "%67" });
//		// specialCharacters.add(new String[] { "h", "%68" });
//		// specialCharacters.add(new String[] { "i", "%69" });
//		// specialCharacters.add(new String[] { "j", "%6A" });
//		// specialCharacters.add(new String[] { "k", "%6B" });
//		// specialCharacters.add(new String[] { "l", "%6C" });
//		// specialCharacters.add(new String[] { "m", "%6D" });
//		// specialCharacters.add(new String[] { "n", "%6E" });
//		// specialCharacters.add(new String[] { "o", "%6F" });
//		// specialCharacters.add(new String[] { "p", "%70" });
//		// specialCharacters.add(new String[] { "q", "%71" });
//		// specialCharacters.add(new String[] { "r", "%72" });
//		// specialCharacters.add(new String[] { "s", "%73" });
//		// specialCharacters.add(new String[] { "t", "%74" });
//		// specialCharacters.add(new String[] { "u", "%75" });
//		// specialCharacters.add(new String[] { "v", "%76" });
//		// specialCharacters.add(new String[] { "w", "%77" });
//		// specialCharacters.add(new String[] { "x", "%78" });
//		// specialCharacters.add(new String[] { "y", "%79" });
//		// specialCharacters.add(new String[] { "z", "%7A" });
//		specialCharacters.add(new String[] { "{", "%7B" });
//		specialCharacters.add(new String[] { "|", "%7C" });
//		specialCharacters.add(new String[] { "}", "%7D" });
//		// specialCharacters.add(new String[] { "~", "%7E" });
//		specialCharacters.add(new String[] { "Â¢", "%A2" });
//		specialCharacters.add(new String[] { "Â£", "%A3" });
//		specialCharacters.add(new String[] { "Â¥", "%A5" });
//		// specialCharacters.add(new String[] { "|", "%A6" });
//		specialCharacters.add(new String[] { "Â§", "%A7" });
//		specialCharacters.add(new String[] { "Â«", "%AB" });
//		specialCharacters.add(new String[] { "Â¬", "%AC" });
//		specialCharacters.add(new String[] { "Â¯", "%AD" });
//		specialCharacters.add(new String[] { "Âº", "%B0" });
//		specialCharacters.add(new String[] { "Â±", "%B1" });
//		specialCharacters.add(new String[] { "Âª", "%B2" });
//		specialCharacters.add(new String[] { ",", "%B4" });
//		specialCharacters.add(new String[] { "Âµ", "%B5" });
//		specialCharacters.add(new String[] { "Â»", "%BB" });
//		specialCharacters.add(new String[] { "Â¼", "%BC" });
//		specialCharacters.add(new String[] { "Â½", "%BD" });
//		specialCharacters.add(new String[] { "Â¿", "%BF" });
//		specialCharacters.add(new String[] { "Ã€", "%C0" });
//		specialCharacters.add(new String[] { "Ã�", "%C1" });
//		specialCharacters.add(new String[] { "Ã‚", "%C2" });
//		specialCharacters.add(new String[] { "Ãƒ", "%C3" });
//		specialCharacters.add(new String[] { "Ã„", "%C4" });
//		specialCharacters.add(new String[] { "Ã…", "%C5" });
//		specialCharacters.add(new String[] { "Ã†", "%C6" });
//		specialCharacters.add(new String[] { "Ã‡", "%C7" });
//		specialCharacters.add(new String[] { "Ãˆ", "%C8" });
//		specialCharacters.add(new String[] { "Ã‰", "%C9" });
//		specialCharacters.add(new String[] { "ÃŠ", "%CA" });
//		specialCharacters.add(new String[] { "Ã‹", "%CB" });
//		specialCharacters.add(new String[] { "ÃŒ", "%CC" });
//		specialCharacters.add(new String[] { "Ã�", "%CD" });
//		specialCharacters.add(new String[] { "ÃŽ", "%CE" });
//		specialCharacters.add(new String[] { "Ã�", "%CF" });
//		specialCharacters.add(new String[] { "Ã�", "%D0" });
//		specialCharacters.add(new String[] { "Ã‘", "%D1" });
//		specialCharacters.add(new String[] { "Ã’", "%D2" });
//		specialCharacters.add(new String[] { "Ã“", "%D3" });
//		specialCharacters.add(new String[] { "Ã”", "%D4" });
//		specialCharacters.add(new String[] { "Ã•", "%D5" });
//		specialCharacters.add(new String[] { "Ã–", "%D6" });
//		specialCharacters.add(new String[] { "Ã˜", "%D8" });
//		specialCharacters.add(new String[] { "Ã™", "%D9" });
//		specialCharacters.add(new String[] { "Ãš", "%DA" });
//		specialCharacters.add(new String[] { "Ã›", "%DB" });
//		specialCharacters.add(new String[] { "Ãœ", "%DC" });
//		specialCharacters.add(new String[] { "Ã�", "%DD" });
//		specialCharacters.add(new String[] { "Ãž", "%DE" });
//		specialCharacters.add(new String[] { "ÃŸ", "%DF" });
//		specialCharacters.add(new String[] { "Ã ", "%E0" });
//		specialCharacters.add(new String[] { "Ã¡", "%E1" });
//		specialCharacters.add(new String[] { "Ã¢", "%E2" });
//		specialCharacters.add(new String[] { "Ã£", "%E3" });
//		specialCharacters.add(new String[] { "Ã¤", "%E4" });
//		specialCharacters.add(new String[] { "Ã¥", "%E5" });
//		specialCharacters.add(new String[] { "Ã¦", "%E6" });
//		specialCharacters.add(new String[] { "Ã§", "%E7" });
//		specialCharacters.add(new String[] { "Ã¨", "%E8" });
//		specialCharacters.add(new String[] { "Ã©", "%E9" });
//		specialCharacters.add(new String[] { "Ãª", "%EA" });
//		specialCharacters.add(new String[] { "Ã«", "%EB" });
//		specialCharacters.add(new String[] { "Ã¬", "%EC" });
//		specialCharacters.add(new String[] { "Ã­", "%ED" });
//		specialCharacters.add(new String[] { "Ã®", "%EE" });
//		specialCharacters.add(new String[] { "Ã¯", "%EF" });
//		specialCharacters.add(new String[] { "Ã°", "%F0" });
//		specialCharacters.add(new String[] { "Ã±", "%F1" });
//		specialCharacters.add(new String[] { "Ã²", "%F2" });
//		specialCharacters.add(new String[] { "Ã³", "%F3" });
//		specialCharacters.add(new String[] { "Ã´", "%F4" });
//		specialCharacters.add(new String[] { "Ãµ", "%F5" });
//		specialCharacters.add(new String[] { "Ã¶", "%F6" });
//		specialCharacters.add(new String[] { "Ã·", "%F7" });
//		specialCharacters.add(new String[] { "Ã¸", "%F8" });
//		specialCharacters.add(new String[] { "Ã¹", "%F9" });
//		specialCharacters.add(new String[] { "Ãº", "%FA" });
//		specialCharacters.add(new String[] { "Ã»", "%FB" });
//		specialCharacters.add(new String[] { "Ã¼", "%FC" });
//		specialCharacters.add(new String[] { "Ã½", "%FD" });
//		specialCharacters.add(new String[] { "Ã¾", "%FE" });
//		specialCharacters.add(new String[] { "Ã¿", "%FF" });
//
//	}
//
//	public static String codeURL(String string) {
//		Iterator<String[]> iterator = specialCharacters.iterator();
//
//		while (iterator.hasNext()) {
//			String[] strings = (String[]) iterator.next();
//			string = string.replace(strings[0], strings[1]);
//		}
//
//		return string;
//	}
//
//	public static String decodeURL(String string) {
//		Iterator<String[]> iterator = specialCharacters.iterator();
//
//		while (iterator.hasNext()) {
//			String[] strings = (String[]) iterator.next();
//			string = string.replace(strings[1], strings[0]);
//		}
//
//		return string;
//	}
	
	 public static String encode(String input) {
	        StringBuilder resultStr = new StringBuilder();
	        for (char ch : input.toCharArray()) {
	            if (isUnsafe(ch)) {
	                resultStr.append('%');
	                resultStr.append(toHex(ch / 16));
	                resultStr.append(toHex(ch % 16));
	            } else {
	                resultStr.append(ch);
	            }
	        }
	        return resultStr.toString();
	    }

	    private static char toHex(int ch) {
	        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
	    }

	    private static boolean isUnsafe(char ch) {
	        if (ch > 128 || ch < 0)
	            return true;
	        return " %$&+,:;=?@<>#%".indexOf(ch) >= 0;
	    }


}
