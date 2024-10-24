/**
 * Project: Dfuze
 * File: Validators.java
 * Date: Sep 24, 2020
 * Time: 9:33:12 AM
 */
package com.mom.dfuze.data.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class Validators {

	// A1A1A1
	private final static Pattern CAN_PC_PATTERN = Pattern.compile("[a-zA-Z][0-9][a-zA-Z][0-9][a-zA-Z][0-9]");
	
	// 0000, Only checking for 4 digits
	private final static Pattern US_ZIP_PATTERN = Pattern.compile("[0-9][0-9][0-9][0-9]");
	
	private final static Character[] vowels = { 'a', 'e', 'i', 'o', 'u', 'y'};

	// Prevent instantiation
	private Validators() {
	};
	
	public static boolean isValidCanPC(String pc) {
		return CAN_PC_PATTERN.matcher(pc.replaceAll("[^a-zA-Z0-9]", "")).find();
	}
	
	public static boolean isValidUSZip(String pc) {
		return US_ZIP_PATTERN.matcher(pc.replaceAll("[^a-zA-Z0-9]", "")).find();
	}

	/**
	 * Returns true if the date String is in M/d/yyyy format, otherwise false
	 * 
	 * @param strDate
	 *          the date String to validate
	 * @return strDate as a Boolean
	 */
	public static boolean isValidMDYYYYDate(String strDate) {

		/*
		 * Set preferred date format,
		 * For example MM-dd-yyyy, MM.dd.yyyy,dd.MM.yyyy etc.
		 */
		SimpleDateFormat sdfrmt = new SimpleDateFormat("M/d/yyyy");
		sdfrmt.setLenient(false);
		/*
		 * Create Date object
		 * parse the string into date
		 */
		try {
			@SuppressWarnings("unused")
			Date javaDate = sdfrmt.parse(strDate);
			// System.out.println(strDate + " is valid date format");
		}
		/* Date format is invalid */
		catch (Exception e) {
			// System.out.println(strDate + " is Invalid Date format");
			return false;
		}
		/* Return true if date format is valid */
		return true;

	}

	/**
	 * Returns true if the date String is in M/d/yyyy format, otherwise false
	 * 
	 * @param strDate
	 *          the date String to validate
	 * @return strDate as a Boolean
	 */
	public static boolean isValidDMYYYYDate(String strDate) {

		/*
		 * Set preferred date format,
		 * For example MM-dd-yyyy, MM.dd.yyyy,dd.MM.yyyy etc.
		 */
		SimpleDateFormat sdfrmt = new SimpleDateFormat("d/M/yyyy");
		sdfrmt.setLenient(false);
		/*
		 * Create Date object
		 * parse the string into date
		 */
		try {
			@SuppressWarnings("unused")
			Date javaDate = sdfrmt.parse(strDate);
			// System.out.println(strDate + " is valid date format");
		}
		/* Date format is invalid */
		catch (Exception e) {
			// System.out.println(strDate + " is Invalid Date format");
			return false;
		}
		/* Return true if date format is valid */
		return true;

	}

	public static boolean isStringOfDateFormat(String strDate, String dateFormat) {
		try {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
			LocalDate.parse(strDate, dateFormatter);
		}
		/* Date format is invalid */
		catch (Exception e) {
			return false;
		}
		/* Return true if date format is valid */
		return true; 
	}

	/**
	 * Returns true if the is a valid Number, otherwise false
	 * 
	 * @param numberString
	 *          the number String to check
	 * @return numberString as a Boolean
	 */
	@SuppressWarnings("unused")
	public static boolean isNumber(String numberString) {


		try {
			Double num = Double.parseDouble(numberString);
		} catch (Exception e) {
			return false;
		}
		
		return true;

	}

	/**
	 * Returns true if the wholeNumber String is a valid whole number, otherwise false
	 * 
	 * @param wholeNumber
	 *          the whole number String to check
	 * @return wholeNumber as a Boolean
	 */

	public static boolean isWholeNumber(String wholeNumber) {
		try {
			Integer.parseInt(wholeNumber);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	public static boolean hasVowel(String string) {
		String loweredString = StringUtils.stripAccents(string.toLowerCase().replaceAll("[^\\p{L}]",""));
		
		for(char c : loweredString.toCharArray()) {
			if(Arrays.stream(vowels).anyMatch(x -> x == c))
				return true; 
		}
		return false;
	}

	public static boolean hasConsonant(String string) {
		String loweredString = StringUtils.stripAccents(string.toLowerCase().replaceAll("[^\\p{L}]",""));
		
		for(char c : loweredString.toCharArray()) {
			if(!Arrays.stream(vowels).anyMatch(x -> x == c))
				return true; 
		}
		return false;
	}

	public static boolean areCharactersSame(String string) {
	    Set<Character> s1 = new HashSet<Character>(); 
	     
	    String s = string.toLowerCase().replaceAll("[^\\p{L}]", "");

	    for(int i = 0; i < s.length(); i++)
	        s1.add(s.charAt(i));

	    if (s1.size() == 1)
	        return true;
	    else
	        return false;
	}

}
