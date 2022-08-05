package org.grits.toolbox.ms.annotation.glycan.composition.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class containing methods to facilitate text processing.
 * 
 * @author Alessio Ceroni (a.ceroni@imperial.ac.uk)
 */

public class TextUtils {

	private TextUtils() {
	}

	/**
	 * Returns Integer value of the text if the text is integer, ohterwise {@code -1}
	 * @param text String to be parsed as Integer value
	 * @return Integer value of the text if the text is integer, ohterwise {@code -1}
	 */
	static public Integer parseInteger(String text) {
		if ( !isInteger(text) )
			return -1;

		return Integer.parseInt(text);
	}

	/**
	 * Returns {@code true} for "yes" or "true", {@code false} for "no" or "false", or {@code null} for the other.
	 * @param text String to be parsed as Boolean value
	 * @return {@code true} for "yes" or "true", {@code false} for "no" or "false", or {@code null} for the other
	 */
	static public Boolean parseBoolean(String text) {
		if ( text == null || text.isEmpty() )
			return null;

		Boolean.parseBoolean(text);
		if ( text.toLowerCase().equals("yes") || text.toLowerCase().equals("true") )
			return true;
		if ( text.toLowerCase().equals("no") || text.toLowerCase().equals("false") )
			return false;
		return null;
	}

	static public List<String> parseStringArray(String str) {
		if( str.equals("-") || str.equals("none") || str.equals("empty") )
			return new ArrayList<>();

		return tokenize(str, ",");
	}

	/**
	 * Return <code>true</code> if the text represent an integer number.
	 */
	static public boolean isInteger(String text) {
		if (text == null || text.length() == 0)
			return false;

		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Return <code>true</code> if the text represent a positive integer number.
	 */
	static public boolean isPositiveInteger(String text) {
		if (text == null || text.length() == 0)
			return false;

		try {
			return (Integer.parseInt(text) >= 0);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Invert the order of the character in a string and return the result.
	 */
	static public String invert(String str) {
		if (str == null || str.length() == 0)
			return str;

		StringBuilder ret = new StringBuilder();
		for (int i = str.length() - 1; i >= 0; i--)
			ret.append(str.charAt(i));

		return ret.toString();
	}

	/**
	 * Remove a character from a string and return the result.
	 */
	static public String delete(String str, char c) {
		if (str == null || str.length() == 0)
			return str;

		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != c)
				ret.append(str.charAt(i));
		}

		return ret.toString();
	}

	/**
	 * Remove the instances of a certain character from the beginning and end of a
	 * string and return the result.
	 */
	static public String squeeze(String str, char c) {
		if (str == null || str.length() == 0)
			return str;

		int start, end;
		for (start = 0; start < str.length() && str.charAt(start) == c; start++)
			;
		if (start == str.length())
			return "";
		for (end = str.length(); end > 0 && str.charAt(end - 1) == c; end--)
			;
		return str.substring(start, end);
	}

	/**
	 * Delete all repeated instances of a certain character in a string and return
	 * the result.
	 */
	static public String squeezeAll(String str, char c) {
		if (str == null || str.length() == 0)
			return str;

		StringBuilder ret = new StringBuilder();

		char last_char = 0;
		for (int i = 0; i < str.length(); i++) {
			if (i == 0 || str.charAt(i) != c || str.charAt(i) != last_char)
				ret.append(str.charAt(i));
			last_char = str.charAt(i);
		}

		return ret.toString();
	}

	/**
	 * Remove spacing characters from the beginning and the end of a string and
	 * return the result.
	 */
	static public String trim(String str) {
		if (str == null || str.length() == 0)
			return str;

		str = squeeze(str, ' ');
		str = squeeze(str, '\t');
		str = squeeze(str, '\n');
		str = squeeze(str, '\r');

		return str;
	}

	/**
	 * Split a string in a list of tokens delimited by a specified character.
	 * 
	 * @param delims the list of characters to be used as delimiters
	 * @return the list of tokens
	 */
	static public List<String> tokenize(String str, String delims) {
		List<String> out = new ArrayList<>();
		if (str == null || str.length() == 0 || delims == null || delims.length() == 0)
			return out;

		StringBuilder token = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			if (delims.indexOf(str.charAt(i)) != -1) {
				if (token.length() > 0) {
					out.add(token.toString());
					token = new StringBuilder(str.length());
				}
			} else {
				token.append(str.charAt(i));
			}
		}
		if (token.length() > 0)
			out.add(token.toString());

		return out;
	}
}
