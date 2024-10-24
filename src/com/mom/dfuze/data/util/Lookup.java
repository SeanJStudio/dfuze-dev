package com.mom.dfuze.data.util;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Pattern;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.io.TextReader;

public class Lookup {
	
	private static final char DELIMITER = ',';
	private static final String LOOKUP_FOLDER = "lookups/";
	
	private static final String LOOKUP_PREFIX_FIRST_PASS = LOOKUP_FOLDER + "prefixes_first_pass.csv";
	private static final String LOOKUP_PREFIX_FIRST_PASS_REGEX = LOOKUP_FOLDER + "prefixes_first_pass_regex.csv";
	private static final String LOOKUP_NAME_JOINERS = LOOKUP_FOLDER + "name_joiners.csv";
	private static final String LOOKUP_COMPANY_KEYWORDS = LOOKUP_FOLDER + "company_keywords.csv";
	
	private static final String LOOKUP_STREET_SUFFIXES = LOOKUP_FOLDER + "street_suffixes.csv";
	private static final String LOOKUP_STREET_NAMES = LOOKUP_FOLDER + "primary_street_names.csv";
	private static final String LOOKUP_STREET_DIRECTION_SUFFIXES = LOOKUP_FOLDER + "street_direction_suffixes.csv";
	private static final String LOOKUP_UNIT_SUFFIXES = LOOKUP_FOLDER + "unit_suffixes.csv";
	
	
	private Lookup() {}
	
	public static Pattern getPrefixFirstPassPattern() throws ApplicationException {
		String[][] prefixesFirstPassTest = TextReader.readDataLookup(new File(LOOKUP_PREFIX_FIRST_PASS_REGEX), DELIMITER);
		StringBuilder pfp1 = new StringBuilder();
		
		final String START_REGEX = "(^|\\s+)(";
		final String END_REGEX = ")(\\s+|$)";
		
		pfp1.append(START_REGEX);
		
		for(int i = 0; i < prefixesFirstPassTest.length; ++i) {
			pfp1.append(prefixesFirstPassTest[i][0]);
			
			if(i != prefixesFirstPassTest.length - 1)
				pfp1.append("|");

		}
		
		pfp1.append(END_REGEX);
		Pattern pfp1Pattern = Pattern.compile(pfp1.toString(), Pattern.CASE_INSENSITIVE);
		
		return pfp1Pattern;
	}
	
	public static String[][] getPrefixFirstPass() throws ApplicationException {
		return TextReader.readDataLookup(new File(LOOKUP_PREFIX_FIRST_PASS), DELIMITER);
	}
	
	public static String[][] getNameJoiners() throws ApplicationException {
		return TextReader.readDataLookup(new File(LOOKUP_NAME_JOINERS), DELIMITER);
	}
	
	public static String[][] getCompanykeywords() throws ApplicationException {
		return TextReader.readDataLookup(new File(LOOKUP_COMPANY_KEYWORDS), DELIMITER);
	}
	
	public static Hashtable<String, String> getStreetSuffixes() throws ApplicationException {
		return TextReader.readTextAsHashtable(new File(LOOKUP_STREET_SUFFIXES), DELIMITER);
	}
	
	public static HashSet<String> getStreetNames() throws ApplicationException {
		return TextReader.readTextAsHashSet(new File(LOOKUP_STREET_NAMES), DELIMITER);
	}
	
	public static Hashtable<String, String> getStreetDirectionSuffixes() throws ApplicationException {
		return TextReader.readTextAsHashtable(new File(LOOKUP_STREET_DIRECTION_SUFFIXES), DELIMITER);
	}
	
	public static Hashtable<String, String> getUnitSuffixes() throws ApplicationException {
		return TextReader.readTextAsHashtable(new File(LOOKUP_UNIT_SUFFIXES), DELIMITER);
	}

}
