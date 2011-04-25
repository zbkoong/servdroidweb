package org.servDroid.util;

import java.util.ArrayList;
import java.util.Iterator;

public class Encoding {

	private static ArrayList<String[]> specialCharacters;
	static {
		specialCharacters = new ArrayList<String[]>();

		specialCharacters.add(new String[] { "%", "%25" });
		specialCharacters.add(new String[] { " ", "%20" });
		specialCharacters.add(new String[] { "!", "%21" });
		specialCharacters.add(new String[] { "\"", "%22" });
		// specialCharacters.add(new String[] { "#", "%23" });
		// specialCharacters.add(new String[] { "$", "%24" });

		// specialCharacters.add(new String[] { "&", "%26" });
		specialCharacters.add(new String[] { "'", "%27" });
		specialCharacters.add(new String[] { "(", "%28" });
		specialCharacters.add(new String[] { ")", "%29" });
		specialCharacters.add(new String[] { "*", "%2A" });
		// specialCharacters.add(new String[] { "+", "%2B" });
		specialCharacters.add(new String[] { ",", "%2C" });
		// specialCharacters.add(new String[] { "-", "%2D" });
		// specialCharacters.add(new String[] { ".", "%2E" });
		// specialCharacters.add(new String[] { "/", "%2F" });
		// specialCharacters.add(new String[] { "0", "%30" });
		// specialCharacters.add(new String[] { "1", "%31" });
		// specialCharacters.add(new String[] { "2", "%32" });
		// specialCharacters.add(new String[] { "3", "%33" });
		// specialCharacters.add(new String[] { "4", "%34" });
		// specialCharacters.add(new String[] { "5", "%35" });
		// specialCharacters.add(new String[] { "6", "%36" });
		// specialCharacters.add(new String[] { "7", "%37" });
		// specialCharacters.add(new String[] { "8", "%38" });
		// specialCharacters.add(new String[] { "9", "%39" });
		specialCharacters.add(new String[] { ":", "%3A" });
		specialCharacters.add(new String[] { ";", "%3B" });
		specialCharacters.add(new String[] { "<", "%3C" });
		// specialCharacters.add(new String[] { "=", "%3D" });
		specialCharacters.add(new String[] { ">", "%3E" });
		// specialCharacters.add(new String[] { "?", "%3F" });
		specialCharacters.add(new String[] { "@", "%40" });
		// specialCharacters.add(new String[] { "A", "%41" });
		// specialCharacters.add(new String[] { "B", "%42" });
		// specialCharacters.add(new String[] { "C", "%43" });
		// specialCharacters.add(new String[] { "D", "%44" });
		// specialCharacters.add(new String[] { "E", "%45" });
		// specialCharacters.add(new String[] { "F", "%46" });
		// specialCharacters.add(new String[] { "G", "%47" });
		// specialCharacters.add(new String[] { "H", "%48" });
		// specialCharacters.add(new String[] { "I", "%49" });
		// specialCharacters.add(new String[] { "J", "%4A" });
		// specialCharacters.add(new String[] { "K", "%4B" });
		// specialCharacters.add(new String[] { "L", "%4C" });
		// specialCharacters.add(new String[] { "M", "%4D" });
		// specialCharacters.add(new String[] { "N", "%4E" });
		// specialCharacters.add(new String[] { "O", "%4F" });
		// specialCharacters.add(new String[] { "P", "%50" });
		// specialCharacters.add(new String[] { "Q", "%51" });
		// specialCharacters.add(new String[] { "R", "%52" });
		// specialCharacters.add(new String[] { "S", "%53" });
		// specialCharacters.add(new String[] { "T", "%54" });
		// specialCharacters.add(new String[] { "U", "%55" });
		// specialCharacters.add(new String[] { "V", "%56" });
		// specialCharacters.add(new String[] { "W", "%57" });
		// specialCharacters.add(new String[] { "X", "%58" });
		// specialCharacters.add(new String[] { "Y", "%59" });
		// specialCharacters.add(new String[] { "Z", "%5A" });
		specialCharacters.add(new String[] { "[", "%5B" });
		specialCharacters.add(new String[] { "\\", "%5C" });
		specialCharacters.add(new String[] { "]", "%5D" });
		specialCharacters.add(new String[] { "^", "%5E" });
		// specialCharacters.add(new String[] { "_", "%5F" });
		specialCharacters.add(new String[] { "`", "%60" });
		// specialCharacters.add(new String[] { "a", "%61" });
		// specialCharacters.add(new String[] { "b", "%62" });
		// specialCharacters.add(new String[] { "c", "%63" });
		// specialCharacters.add(new String[] { "d", "%64" });
		// specialCharacters.add(new String[] { "e", "%65" });
		// specialCharacters.add(new String[] { "f", "%66" });
		// specialCharacters.add(new String[] { "g", "%67" });
		// specialCharacters.add(new String[] { "h", "%68" });
		// specialCharacters.add(new String[] { "i", "%69" });
		// specialCharacters.add(new String[] { "j", "%6A" });
		// specialCharacters.add(new String[] { "k", "%6B" });
		// specialCharacters.add(new String[] { "l", "%6C" });
		// specialCharacters.add(new String[] { "m", "%6D" });
		// specialCharacters.add(new String[] { "n", "%6E" });
		// specialCharacters.add(new String[] { "o", "%6F" });
		// specialCharacters.add(new String[] { "p", "%70" });
		// specialCharacters.add(new String[] { "q", "%71" });
		// specialCharacters.add(new String[] { "r", "%72" });
		// specialCharacters.add(new String[] { "s", "%73" });
		// specialCharacters.add(new String[] { "t", "%74" });
		// specialCharacters.add(new String[] { "u", "%75" });
		// specialCharacters.add(new String[] { "v", "%76" });
		// specialCharacters.add(new String[] { "w", "%77" });
		// specialCharacters.add(new String[] { "x", "%78" });
		// specialCharacters.add(new String[] { "y", "%79" });
		// specialCharacters.add(new String[] { "z", "%7A" });
		specialCharacters.add(new String[] { "{", "%7B" });
		specialCharacters.add(new String[] { "|", "%7C" });
		specialCharacters.add(new String[] { "}", "%7D" });
		// specialCharacters.add(new String[] { "~", "%7E" });
		specialCharacters.add(new String[] { "¢", "%A2" });
		specialCharacters.add(new String[] { "£", "%A3" });
		specialCharacters.add(new String[] { "¥", "%A5" });
		// specialCharacters.add(new String[] { "|", "%A6" });
		specialCharacters.add(new String[] { "§", "%A7" });
		specialCharacters.add(new String[] { "«", "%AB" });
		specialCharacters.add(new String[] { "¬", "%AC" });
		specialCharacters.add(new String[] { "¯", "%AD" });
		specialCharacters.add(new String[] { "º", "%B0" });
		specialCharacters.add(new String[] { "±", "%B1" });
		specialCharacters.add(new String[] { "ª", "%B2" });
		specialCharacters.add(new String[] { ",", "%B4" });
		specialCharacters.add(new String[] { "µ", "%B5" });
		specialCharacters.add(new String[] { "»", "%BB" });
		specialCharacters.add(new String[] { "¼", "%BC" });
		specialCharacters.add(new String[] { "½", "%BD" });
		specialCharacters.add(new String[] { "¿", "%BF" });
		specialCharacters.add(new String[] { "À", "%C0" });
		specialCharacters.add(new String[] { "Á", "%C1" });
		specialCharacters.add(new String[] { "Â", "%C2" });
		specialCharacters.add(new String[] { "Ã", "%C3" });
		specialCharacters.add(new String[] { "Ä", "%C4" });
		specialCharacters.add(new String[] { "Å", "%C5" });
		specialCharacters.add(new String[] { "Æ", "%C6" });
		specialCharacters.add(new String[] { "Ç", "%C7" });
		specialCharacters.add(new String[] { "È", "%C8" });
		specialCharacters.add(new String[] { "É", "%C9" });
		specialCharacters.add(new String[] { "Ê", "%CA" });
		specialCharacters.add(new String[] { "Ë", "%CB" });
		specialCharacters.add(new String[] { "Ì", "%CC" });
		specialCharacters.add(new String[] { "Í", "%CD" });
		specialCharacters.add(new String[] { "Î", "%CE" });
		specialCharacters.add(new String[] { "Ï", "%CF" });
		specialCharacters.add(new String[] { "Ð", "%D0" });
		specialCharacters.add(new String[] { "Ñ", "%D1" });
		specialCharacters.add(new String[] { "Ò", "%D2" });
		specialCharacters.add(new String[] { "Ó", "%D3" });
		specialCharacters.add(new String[] { "Ô", "%D4" });
		specialCharacters.add(new String[] { "Õ", "%D5" });
		specialCharacters.add(new String[] { "Ö", "%D6" });
		specialCharacters.add(new String[] { "Ø", "%D8" });
		specialCharacters.add(new String[] { "Ù", "%D9" });
		specialCharacters.add(new String[] { "Ú", "%DA" });
		specialCharacters.add(new String[] { "Û", "%DB" });
		specialCharacters.add(new String[] { "Ü", "%DC" });
		specialCharacters.add(new String[] { "Ý", "%DD" });
		specialCharacters.add(new String[] { "Þ", "%DE" });
		specialCharacters.add(new String[] { "ß", "%DF" });
		specialCharacters.add(new String[] { "à", "%E0" });
		specialCharacters.add(new String[] { "á", "%E1" });
		specialCharacters.add(new String[] { "â", "%E2" });
		specialCharacters.add(new String[] { "ã", "%E3" });
		specialCharacters.add(new String[] { "ä", "%E4" });
		specialCharacters.add(new String[] { "å", "%E5" });
		specialCharacters.add(new String[] { "æ", "%E6" });
		specialCharacters.add(new String[] { "ç", "%E7" });
		specialCharacters.add(new String[] { "è", "%E8" });
		specialCharacters.add(new String[] { "é", "%E9" });
		specialCharacters.add(new String[] { "ê", "%EA" });
		specialCharacters.add(new String[] { "ë", "%EB" });
		specialCharacters.add(new String[] { "ì", "%EC" });
		specialCharacters.add(new String[] { "í", "%ED" });
		specialCharacters.add(new String[] { "î", "%EE" });
		specialCharacters.add(new String[] { "ï", "%EF" });
		specialCharacters.add(new String[] { "ð", "%F0" });
		specialCharacters.add(new String[] { "ñ", "%F1" });
		specialCharacters.add(new String[] { "ò", "%F2" });
		specialCharacters.add(new String[] { "ó", "%F3" });
		specialCharacters.add(new String[] { "ô", "%F4" });
		specialCharacters.add(new String[] { "õ", "%F5" });
		specialCharacters.add(new String[] { "ö", "%F6" });
		specialCharacters.add(new String[] { "÷", "%F7" });
		specialCharacters.add(new String[] { "ø", "%F8" });
		specialCharacters.add(new String[] { "ù", "%F9" });
		specialCharacters.add(new String[] { "ú", "%FA" });
		specialCharacters.add(new String[] { "û", "%FB" });
		specialCharacters.add(new String[] { "ü", "%FC" });
		specialCharacters.add(new String[] { "ý", "%FD" });
		specialCharacters.add(new String[] { "þ", "%FE" });
		specialCharacters.add(new String[] { "ÿ", "%FF" });

	}

	public static String codeURL(String string) {
		Iterator<String[]> iterator = specialCharacters.iterator();

		while (iterator.hasNext()) {
			String[] strings = (String[]) iterator.next();
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	public static String decodeURL(String string) {
		Iterator<String[]> iterator = specialCharacters.iterator();

		while (iterator.hasNext()) {
			String[] strings = (String[]) iterator.next();
			string = string.replace(strings[1], strings[0]);
		}

		return string;
	}

}
