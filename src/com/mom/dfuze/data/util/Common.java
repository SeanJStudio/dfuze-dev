/**
 * Project: Dfuze
 * File: Common.java
 * Date: Mar 29, 2020
 * Time: 3:07:50 PM
 */
package com.mom.dfuze.data.util;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Nysiis;
import org.apache.commons.codec.language.RefinedSoundex;
import org.apache.commons.codec.language.bm.BeiderMorseEncoder;
import org.apache.commons.lang3.StringUtils;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.job.DataMappingDialog;

/**
 * Common class to hold global utility methods
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class Common {

	// private constructor to prevent instantiation
	private Common() {

	}
	
	public static String arrayFieldsToHTMLList(String[] array) {
		StringBuilder html = new StringBuilder();
		html.append("");
		
		try {
		if(array != null && array.length > 0) {
			html.append("Required Fields <ol>");
			
			for(String string : array)
					html.append(String.format("<li>%s</li>", DataMappingDialog.getLabelNameFromDfuzeFieldName(string)));
			
			html.append("</ol>");
		}
		} catch (ApplicationException e) {
			UiController.handle(e);
		}
		return html.toString();
	}

	public static boolean isDfField(String fieldToFind) {
		for(String field : UserData.allDfHeaders)
			if(field.equalsIgnoreCase(fieldToFind))
				return true;

		return false;
	}

	public static boolean hasCreatedHeaders(UserData userData) {
		for(String header : userData.getInHeaders())
			if(header.equalsIgnoreCase("F1"))
				return true;

		return false;	
	}

	public static boolean hasFileName(UserData userData) {
		for(String header : userData.getInHeaders())
			if(header.equalsIgnoreCase("dfFileName"))
				return true;

		return false;
	}

	public static String caseCompany(String company) {
		Pattern specialChar = Pattern.compile("[\\p{P}\\p{S}|0-9]", 2);
		Pattern prefSufChar = Pattern.compile("(st|by|mr|mrs|ms|jr|sr|dr)(mr|mrs|ms|jr|sr|dr)?(s)?", 2);
		String[] companyParts = company.trim().split("\\s+");
		StringBuilder casedCompany = new StringBuilder("");
		for (int i = 0; i < companyParts.length; i++) {
			String newPart = companyParts[i].toLowerCase();
			if (newPart.length() > 0) {
				newPart = String.valueOf(newPart.substring(0, 1).toUpperCase()) + newPart.substring(1);
				if (!Validators.hasVowel(newPart) && 
						!prefSufChar.matcher(newPart.replaceAll("[^\\p{L}]", "")).find())
					newPart = newPart.toUpperCase(); 
			} 
			char[] newPartChars = newPart.toCharArray();
			boolean shouldNextBeCap = false;
			for (int j = 0; j < newPartChars.length; j++) {
				if (shouldNextBeCap) {
					newPartChars[j] = Character.toUpperCase(newPartChars[j]);
					shouldNextBeCap = false;
				} 
				if (specialChar.matcher(String.valueOf(newPartChars[j])).find())
					shouldNextBeCap = true; 
			} 
			newPart = new String(newPartChars);
			if (newPart.length() > 4 && 
					newPart.substring(0, 2).equalsIgnoreCase("mc"))
				newPart = String.valueOf(newPart.substring(0, 2)) + newPart.substring(2, 3).toUpperCase() + newPart.substring(3); 
			if (newPart.length() > 5 && 
					newPart.substring(0, 3).equalsIgnoreCase("mac"))
				newPart = String.valueOf(newPart.substring(0, 3)) + newPart.substring(3, 4).toUpperCase() + newPart.substring(4); 
			if (newPart.length() > 6 && 
					newPart.substring(0, 4).equalsIgnoreCase("fitz"))
				newPart = String.valueOf(newPart.substring(0, 4)) + newPart.substring(4, 5).toUpperCase() + newPart.substring(5); 
			if (newPart.equalsIgnoreCase("and") || newPart.equalsIgnoreCase("et") || newPart.equalsIgnoreCase("of") || newPart.equalsIgnoreCase("or") || newPart.equalsIgnoreCase("ou"))
				newPart = newPart.toLowerCase(); 
			if (i == newPartChars.length - 1 && newPartChars.length > 1 && (
					newPart.equalsIgnoreCase("ii") || 
					newPart.equalsIgnoreCase("iii") || 
					newPart.equalsIgnoreCase("iv") || 
					newPart.equalsIgnoreCase("vi") || 
					newPart.equalsIgnoreCase("vii") || 
					newPart.equalsIgnoreCase("viii") || 
					newPart.equalsIgnoreCase("ix")))
				newPart = newPart.toUpperCase(); 
			casedCompany.append(String.valueOf(newPart) + " ");
		} 
		return casedCompany.toString()
				.replaceAll("(?i)(?<=^|\\s)abc(?=$|\\s)", "ABC")
				.replaceAll("(?i)(?<=^|\\s)adb(?=$|\\s)", "ADB")
				.replaceAll("(?i)(?<=^|\\s)aes(?=$|\\s)", "AES")
				.replaceAll("(?i)(?<=^|\\s)ame(?=$|\\s)", "AME")
				.replaceAll("(?i)(?<=^|\\s)aro(?=$|\\s)", "ARO")
				.replaceAll("(?i)(?<=^|\\s)am(?=$|\\s)", "AM")
				.replaceAll("(?i)(?<=^|\\s)amc(?=$|\\s)", "AMC")
				.replaceAll("(?i)(?<=^|\\s)acme(?=$|\\s)", "ACME")
				.replaceAll("(?i)(?<=^|\\s)at&t(?=$|\\s)", "AT&T")
				.replaceAll("(?i)(?<=^|\\s)bba(?=$|\\s)", "BBA")
				.replaceAll("(?i)(?<=^|\\s)bmo(?=$|\\s)", "BMO")
				.replaceAll("(?i)(?<=^|\\s)bcaa(?=$|\\s|')", "BCAA")
				.replaceAll("(?i)(?<=^|\\s)bcit(?=$|\\s|')", "BCIT")
				.replaceAll("(?i)(?<=^|\\s)bgis(?=$|\\s)", "BGIS")
				.replaceAll("(?i)(?<=^|\\s)cma(?=$|\\s)", "CMA")
				.replaceAll("(?i)(?<=^|\\s)cpa(?=$|\\s)", "CPA")
				.replaceAll("(?i)(?<=^|\\s)cibc(?=$|\\s)", "CIBC")
				.replaceAll("(?i)(?<=^|\\s)dba(?=$|\\s)", "DBA")
				.replaceAll("(?i)(?<=^|\\s)ehp(?=$|\\s)", "EHP")
				.replaceAll("(?i)(?<=^|\\s)ems(?=$|\\s)", "EMS")
				.replaceAll("(?i)(?<=^|\\s)eC(?=$|\\s)", "EC")
				.replaceAll("(?i)(?<=^|\\s)ep(?=$|\\s)", "EP")
				.replaceAll("(?i)(?<=^|\\s)er(?=$|\\s)", "ER")
				.replaceAll("(?i)(?<=^|\\s)eo(?=$|\\s)", "EO")
				.replaceAll("(?i)(?<=^|\\s)epi(?=$|\\s)", "EPI")
				.replaceAll("(?i)(?<=^|\\s)exp(?=$|\\s)", "EXP")
				.replaceAll("(?i)(?<=^|\\s)ge(?=$|\\s)", "GE")
				.replaceAll("(?i)(?<=^|\\s)hma(?=$|\\s)", "HMA")
				.replaceAll("(?i)(?<=^|\\s)hcma(?=$|\\s)", "HCMA")
				.replaceAll("(?i)(?<=^|\\s)itg(?=$|\\s)", "ITG")
				.replaceAll("(?i)(?<=^|\\s)ipg(?=$|\\s)", "IPG")
				.replaceAll("(?i)(?<=^|\\s)ism(?=$|\\s)", "ISM")
				.replaceAll("(?i)(?<=^|\\s)ibm(?=$|\\s)", "IBM")
				.replaceAll("(?i)(?<=^|\\s)irs(?=$|\\s)", "IRS")
				.replaceAll("(?i)(?<=^|\\s)icbc(?=$|\\s)", "ICBC")
				.replaceAll("(?i)(?<=^|\\s)ircc(?=$|\\s)", "IRCC")
				.replaceAll("(?i)(?<=^|\\s)kia(?=$|\\s)", "KIA")
				.replaceAll("(?i)(?<=^|\\s)lha(?=$|\\s)", "LHA")
				.replaceAll("(?i)(?<=^|\\s)ok(?=$|\\s)", "OK")
				.replaceAll("(?i)(?<=^|\\s)ubc(?=$|\\s|')", "UBC")
				.replaceAll("(?i)(?<=^|\\s)ufcw(?=$|\\s)", "UFCW")
				.replaceAll("(?i)(?<=^|\\s)ups(?=$|\\s)", "UPS")
				.replaceAll("(?i)(?<=^|\\s)usps(?=$|\\s)", "USPS")
				.replaceAll("(?i)(?<=^|\\s)ulc(?=$|\\s)", "ULC")
				.replaceAll("(?i)(?<=^|\\s)sap(?=$|\\s)", "SAP")
				.replaceAll("(?i)(?<=^|\\s)sba(?=$|\\s)", "SBA")
				.replaceAll("(?i)(?<=^|\\s)sfu(?=$|\\s|')", "SFU")
				.replaceAll("(?i)(?<=^|\\s)qe(?=$|\\s)", "QE")
				.replaceAll("(?i)(?<=^|\\s)rti(?=$|\\s)", "RTI")
				.replaceAll("(?i)(?<=^|\\s)vgi(?=$|\\s)", "VGI")
				.replaceAll("(?i)(?<=^|\\s)vha(?=$|\\s)", "VHA")
				.replaceAll("(?i)(?<=^|\\s)ymca(?=$|\\s)", "YMCA")
				.replaceAll("(?i)(?<=^|\\s)CTR(?=$|\\s|\\.)", "Ctr")
				.replaceAll("(?i)(?<=^|\\s)LTD(?=$|\\s|\\.)", "Ltd")
				.replaceAll("(?i)(?<=^|\\s)paypal(?=$|\\s)", "PayPal")
				.replaceAll("(?i)(?<=^|\\s)fundex(?=$|\\s)", "FundEX")
				.replaceAll("(?i)(?<=^|\\s)fedex(?=$|\\s)", "FedEx")
				.replaceAll("(?i)(?<=^|\\s)ebay(?=$|\\s)", "eBay")
				.replaceAll("(?i)(?<=[\\p{L}])bio(?=$|\\s)", "Bio")
				.replaceAll("(?i)(?<=^|\\s)gym(?=$|\\s)", "Gym")
				.replaceAll("(?i)('s\\s)", "'s ")
				.replaceAll("(?i)(`s\\s)", "`s ")
				.replaceAll("(?i)('s$)", "'s")
				.replaceAll("(?i)(`s$)", "`s")
				.replaceAll("(?<!^)(D')", "d'")
				.replaceAll("(?<!^)(D`)", "d`")
				.replaceAll("(?i)(and/or)", "and/or")
				.replaceAll("(?i)(et/ou)", "et/ou")
				.replaceAll("(?i)(or/and)", "or/and")
				.replaceAll("(?i)(ou/et)", "ou/et")
				.replaceAll("(?i)(?<=[0-9])th(?=$|\\s)", "th")
				.replaceAll("(?i)(?<=[0-9])rd(?=$|\\s)", "rd")
				.replaceAll("(?i)(?<=[0-9])st(?=$|\\s)", "st")
				.replaceAll("(?i)\\.ca(?=$|\\s)", ".ca")
				.replaceAll("(?i)\\.com(?=$|\\s)", ".com")
				.replaceAll("(?i)\\.net(?=$|\\s)", ".net")
				.replaceAll("(?i)\\.org(?=$|\\s)", ".org")
				.replaceAll("(?i)\\.io(?=$|\\s)", ".io")
				.replaceAll("\\s+", " ")
				.trim();
	}

	public static String caseName(String text) {
		Pattern specialChar = Pattern.compile("[\\p{P}\\p{S}|0-9]", 2);
		String[] casedFullNameParts = text.split("\\s+");
		StringBuilder casedFullName = new StringBuilder("");
		for (int i = 0; i < casedFullNameParts.length; i++) {
			String newPart = casedFullNameParts[i].toLowerCase();
			if (newPart.length() > 0)
				newPart = String.valueOf(newPart.substring(0, 1).toUpperCase()) + newPart.substring(1); 
			char[] newPartChars = newPart.toCharArray();
			boolean shouldNextBeCap = false;
			for (int j = 0; j < newPartChars.length; j++) {
				if (shouldNextBeCap) {
					newPartChars[j] = Character.toUpperCase(newPartChars[j]);
					shouldNextBeCap = false;
				} 
				if (specialChar.matcher(String.valueOf(newPartChars[j])).find())
					shouldNextBeCap = true; 
			} 
			newPart = new String(newPartChars);
			if (newPart.length() > 4 && 
					newPart.substring(0, 2).equalsIgnoreCase("mc"))
				newPart = String.valueOf(newPart.substring(0, 2)) + newPart.substring(2, 3).toUpperCase() + newPart.substring(3); 
			if (newPart.length() > 5 && 
					newPart.substring(0, 3).equalsIgnoreCase("mac"))
				newPart = String.valueOf(newPart.substring(0, 3)) + newPart.substring(3, 4).toUpperCase() + newPart.substring(4); 
			if (newPart.length() > 6 && 
					newPart.substring(0, 4).equalsIgnoreCase("fitz"))
				newPart = String.valueOf(newPart.substring(0, 4)) + newPart.substring(4, 5).toUpperCase() + newPart.substring(5); 
			if (newPart.equalsIgnoreCase("and") || newPart.equalsIgnoreCase("et") || newPart.equalsIgnoreCase("of") || newPart.equalsIgnoreCase("or") || newPart.equalsIgnoreCase("ou"))
				newPart = newPart.toLowerCase(); 
			if (i != 0 && i != casedFullNameParts.length - 1 && (
					newPart.equalsIgnoreCase("da") || 
					newPart.equalsIgnoreCase("de") || 
					newPart.equalsIgnoreCase("du") || 
					newPart.equalsIgnoreCase("le") || 
					newPart.equalsIgnoreCase("la") || 
					newPart.equalsIgnoreCase("del") || 
					newPart.equalsIgnoreCase("den") || 
					newPart.equalsIgnoreCase("der") || 
					newPart.equalsIgnoreCase("des") || 
					newPart.equalsIgnoreCase("die") || 
					newPart.equalsIgnoreCase("dos") || 
					newPart.equalsIgnoreCase("duc") || 
					newPart.equalsIgnoreCase("ihr") || 
					newPart.equalsIgnoreCase("sur") || 
					newPart.equalsIgnoreCase("und") || 
					newPart.equalsIgnoreCase("van") || 
					newPart.equalsIgnoreCase("von")))
				newPart = newPart.toLowerCase(); 
			if (i == casedFullNameParts.length - 1 && casedFullNameParts.length > 1 && (
					newPart.equalsIgnoreCase("ii") || 
					newPart.equalsIgnoreCase("iii") || 
					newPart.equalsIgnoreCase("iv") || 
					newPart.equalsIgnoreCase("vi") || 
					newPart.equalsIgnoreCase("vii") || 
					newPart.equalsIgnoreCase("viii") || 
					newPart.equalsIgnoreCase("ix")))
				newPart = newPart.toUpperCase(); 
			casedFullName.append(String.valueOf(newPart) + " ");
		} 
		return casedFullName.toString()
				.replaceAll("(?i)('s\\s)", "'s ")
				.replaceAll("(?i)(`s\\s)", "`s ")
				.replaceAll("(?i)('s$)", "'s")
				.replaceAll("(?i)(`s$)", "`s")
				.replaceAll("(?<!^)(D')", "d'")
				.replaceAll("(?<!^)(D`)", "d`")
				.replaceAll("(?i)(and/or)", "and/or")
				.replaceAll("(?i)(et/ou)", "et/ou")
				.replaceAll("(?i)(or/and)", "or/and")
				.replaceAll("(?i)(ou/et)", "ou/et")
				.replaceAll("(?i)(?<=[0-9])th(?=$|\\s)", "th")
				.replaceAll("(?i)(?<=[0-9])rd(?=$|\\s)", "rd")
				.replaceAll("(?i)(?<=[0-9])st(?=$|\\s)", "st")
				.replaceAll("(?i)\\.ca(?=$|\\s)", ".ca")
				.replaceAll("(?i)\\.com(?=$|\\s)", ".com")
				.replaceAll("(?i)\\.net(?=$|\\s)", ".net")
				.replaceAll("(?i)\\.org(?=$|\\s)", ".org")
				.replaceAll("(?i)\\.io(?=$|\\s)", ".io")
				.replaceAll("\\s+", " ")
				.trim();
	}

	public static String caseProper(String text) {
		Pattern specialChar = Pattern.compile("[\\p{P}\\p{S}|0-9]", 2);
		String[] casedFullNameParts = text.split("\\s+");
		StringBuilder casedFullName = new StringBuilder("");
		for (int i = 0; i < casedFullNameParts.length; i++) {
			String newPart = casedFullNameParts[i].toLowerCase();
			char[] newPartChars = newPart.toCharArray();
			boolean shouldNextBeCap = false;
			for (int j = 0; j < newPartChars.length; j++) {
				if (shouldNextBeCap) {
					newPartChars[j] = Character.toUpperCase(newPartChars[j]);
					shouldNextBeCap = false;
				} 
				if (specialChar.matcher(String.valueOf(newPartChars[j])).find())
					shouldNextBeCap = true; 
			} 
			newPart = new String(newPartChars);
			casedFullName.append(String.valueOf(newPart) + " ");
		} 
		return casedFullName.toString()
				.replaceAll("(?i)(['][s][ ])", "'s ")
				.replaceAll("(?i)(['][s]$)", "'s")
				.replaceAll("\\s+", " ")
				.trim();
	}

	public static String rightPad(String str, int num, char rep) {
		return String.format("%1$-" + num + "s", str).replace(' ', rep);
	}
	
	public static String leftPad(String str, int num, char rep) {
		return String.format("%1$" + num + "s", str).replace(' ', rep);
	}

	public static String replaceLast(String text, String regex, String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}

	public static String correctPCode(String pCode) {
		final int RADIX = 10;
		StringBuilder sb = new StringBuilder(pCode.replaceAll(" ", "").replaceAll("[^\\p{L}0-9]", "").replaceAll("-.*", "").toLowerCase());	  
		final int CA_PC_LENGTH = 6;
		final int US_PC_LENGTH = 5;
		boolean isCanPc = sb.length() == CA_PC_LENGTH;
		boolean isUsPc = (!isCanPc) ? true : false;
		int[] letterIndexes = new int[]{0,2,4};
		int[] numberIndexes = new int[]{1,3,5};

		Map<Integer, Character> similarSymbols = new HashMap<>();
		similarSymbols.put(0, 'o');
		similarSymbols.put(1, 'l');
		similarSymbols.put(2, 'z');
		similarSymbols.put(3, 'e');
		similarSymbols.put(4, 'a');
		similarSymbols.put(5, 's');
		similarSymbols.put(8, 'b');

		// Correct Canadian Postal code OCR errors
		if(isCanPc) {
			for(int index : letterIndexes) {
				if(Validators.isNumber(String.valueOf(sb.charAt(index)))) {
					int number = Integer.parseInt(String.valueOf(sb.charAt(index)));
					if(similarSymbols.containsKey(number)) {
						sb.setCharAt(index, similarSymbols.get(index));
					}
				}
			}

			for(int index : numberIndexes) {
				if(!Validators.isNumber(String.valueOf(sb.charAt(index)))) {
					char letter = sb.charAt(index);
					if(similarSymbols.containsValue(letter)) {
						for(Entry<Integer, Character> entry : similarSymbols.entrySet()) {
							if(entry.getValue().equals(letter)) {
								sb.setCharAt(index, Character.forDigit(entry.getKey(), RADIX));
								break;
							}
						}

					}
				}
			}

		}

		// Grab the first 5 digits and prepad with leading zeros for US PC
		if(isUsPc) {
			if(sb.length() > US_PC_LENGTH)
				sb.delete(US_PC_LENGTH, sb.length());

			while(sb.length() < US_PC_LENGTH)
				sb.insert(0,"0");
		}

		//System.out.println("OLD: " + pCode + "\tNEW: " + sb.toString());

		return sb.toString();

	}

	public static String fixZip(String zip) {
		zip = zip.replaceAll("[^0-9-]", "");
		StringBuilder newZip = new StringBuilder();

		if(zip.contains("-")) {
			String[] zipParts = zip.split("-");

			if(zipParts.length >= 2) {
				newZip.append(zipParts[0]);

				while(newZip.length() < 5)
					newZip.insert(0, "0");

				newZip.append("-" + zipParts[1]);

				while(newZip.length() < 10)
					newZip.insert(6, "0");

			} else {
				newZip.append(zipParts[0]);

				while(newZip.length() < 5)
					newZip.insert(0, "0");
			}
		} else {
			if(zip.length() > 5) {
				newZip.append(zip.substring(0,4) + "-");
				newZip.append(zip.substring(5));

				while(newZip.length() < 10)
					newZip.insert(6, "0");

				while(newZip.length() > 10)
					newZip.deleteCharAt(newZip.length() - 1);
			} else {
				newZip.append(zip);

				while(newZip.length() < 5)
					newZip.insert(0, "0");

			}
		}

		return newZip.toString();
	}

	public static boolean areSubstringsEqual(String s1, String s2, int length) {
		int tempLength = (s1.length() < s2.length()) ? s1.length() : s2.length();
		tempLength = (tempLength < length) ? tempLength : length;

		if(tempLength < 5)
			return false;

		return s1.substring(0, tempLength).equalsIgnoreCase(s2.substring(0, tempLength));
	}

	public static String valueAsExcelTextFormatFormula(String value) {
		return "=TEXT(\"" + value + "\",)";
	}

	public static String valueAsExcelNumberFormatFormula(String value) {
		if (!value.isEmpty())
			return "=VALUE(\"" + value + "\")";
		else
			return "=TEXT(\"" + value + "\")";
	}

	/**
	 * Calculate the standard deviation from a BigDecimal[]
	 * 
	 * @param numArray
	 *          the List of BigDecimals to calculate the standard deviation from
	 * @return standardDeviation as a BigDecimal
	 */
	public static BigDecimal calculateStandardDeviation(List<BigDecimal> numArray) {
		BigDecimal sum = new BigDecimal("0.0"), newSum = new BigDecimal("0.0");
		int length = numArray.size();

		for (BigDecimal num : numArray) {
			sum = sum.add(num);
		}

		BigDecimal mean = sum.divide(BigDecimal.valueOf(length), 2, RoundingMode.HALF_UP);

		for (BigDecimal num : numArray) {
			newSum = newSum.add(num.subtract(mean).pow(2));

		}

		return newSum.divide(BigDecimal.valueOf(length), 2, RoundingMode.HALF_UP).sqrt(MathContext.DECIMAL128);
	}

	/**
	 * Gets the max Integer value from an int[]
	 * 
	 * @param array
	 *          the array of Integers to traverse
	 * @return maxValue as an int
	 */
	public static int getMaxValueInIntArray(int array[]) {
		return Arrays.stream(array).max().getAsInt();
	}

	// Splits a string by a given number of characters into two elements
	/**
	 * Splits a String by n characters
	 * 
	 * @param firstHalf
	 *          the presumed first half of the string to split
	 * @param maxLength
	 *          the max length the string can be
	 * @return the splitToSplit as a String[] that holds 2 elements. 1 for the first half and 1 for the second half
	 */
	public static String[] splitString(String stringToSplit, int maxLength) {
		final int NUM_TO_SPLIT = maxLength;
		if (stringToSplit.length() <= NUM_TO_SPLIT) {
			return new String[] { stringToSplit, "" };
		}
		int charNum = 0;
		String firstHalf = "";
		String secondHalf = "";

		String[] arrOfStr = stringToSplit.trim().split(" ");
		for (int i = 0; i < arrOfStr.length; i++) {

			if (charNum + arrOfStr[i].length() <= NUM_TO_SPLIT) {
				firstHalf += arrOfStr[i] + " ";
				charNum = firstHalf.length();
			} else {
				secondHalf += arrOfStr[i] + " ";
				charNum = firstHalf.length() + secondHalf.length();
			}

		}

		return new String[] { firstHalf.trim(), secondHalf.trim() };
	}

	// Split nam1 into nam1_2 by nameJoiners when > maxChar on the largest index found.
	public static Record splitAddName(Record record, int maxChar, String[][] prefixesForNameSplitting) throws ApplicationException {
		final int PREFIX_FOR_NAMESPLIT_ELEMENT = 0;

		int prefixIndexFound = -1;
		String prefixFound = "";

		if (record.getNam1() != null) {
			String nam1 = record.getNam1();
			if (nam1.length() > maxChar) {
				for (int x = 0; x < prefixesForNameSplitting.length; ++x) {
					String prefixSplitValue = prefixesForNameSplitting[x][PREFIX_FOR_NAMESPLIT_ELEMENT];
					int indexLocation = nam1.toLowerCase().lastIndexOf(prefixSplitValue);
					if(indexLocation > prefixIndexFound && !nam1.substring(0, prefixSplitValue.length() - 1).toLowerCase().equals(prefixSplitValue)) {
						prefixIndexFound = indexLocation;
						prefixFound = prefixSplitValue;
					}
				}
				
				if (prefixIndexFound != -1 && !nam1.substring(0, prefixFound.length() - 1).toLowerCase().equals(prefixFound)) {
					record.setNam1(nam1.substring(0, prefixIndexFound + prefixFound.length()).trim());
					record.setNam1_2(nam1.substring(prefixIndexFound + prefixFound.length()).trim());
				}
			}
		}
		return record;
	}

	/**
	 * Checks if a header string value is the same as an internal Dfuze header value
	 * 
	 * @param stringToFind
	 *          the string to search for
	 * @param dupeNum
	 *          the current number of dupelicates
	 * @param allDfHeaders
	 *          the String[] of all the Dfuze UserData headers
	 * @return the stringToFind as the new new header value
	 */
	public static String checkIfDfHeader(String stringToFind,String[] inHeaders, String[] allDfHeaders) {

		for (String header : allDfHeaders) {
			if (stringToFind.equals(header)) {
				stringToFind = "IN__" + stringToFind;
			}
		}

		for (String header : inHeaders) {
			if (stringToFind.equals(header)) {
				stringToFind = "IN__" + stringToFind;
			}
		}

		return stringToFind;
	}

	/**
	 * Gets the Metaphone3 encoded string
	 * 
	 * @param str
	 *          the string to encode
	 * @return str as a Metaphone3 encoded String
	 */
	public static String getMetaphone3(String str) {
		Metaphone3 m3 = new Metaphone3(str);
		m3.Encode();
		return m3.GetMetaph();
	}

	/**
	 * Gets the Refined Soundex encoded String
	 * 
	 * @param str
	 *          the string to encode
	 * @return str as an refinedSoundex encoded String
	 */
	public static String getRefinedSoundex(String str) {
		try {
			return new RefinedSoundex().soundex(str);
		} catch (Exception e) {
			return str;
		}
		
	}

	public static String getBeiderMorse(String str) {
		try {
			return new BeiderMorseEncoder().encode(str);
		} catch (Exception e) {

		}
		return "";
	}

	/**
	 * Gets the NYSIIS encoded string
	 * 
	 * @param str
	 *          the string to encode
	 * @return str as an NYSIIS encoded string
	 */
	public static String getNysiis(String str) {
		return new Nysiis(true).encode(str);
	}

	/**
	 * Getst the DoubleMethphone encoded String
	 * 
	 * @param str
	 *          the String to encode
	 * @return str as a DoubleMetaphone encoded String
	 */
	public static String getDoubleMetaphone(String str) {
		DoubleMetaphone m2 = new DoubleMetaphone();
		String newStr = m2.doubleMetaphone(str);

		return (newStr == null) ? "" : newStr;
	}

	// If possible, gets the first initial
	/**
	 * Gets the initials for the first and last name in a string, otherwise returns blank
	 * 
	 * @param str
	 *          the string to get the initials from
	 * @return str with the initial values as a String
	 */
	public static String getInitials(String str) {
		String[] nameParts = str.replaceAll("[^\\p{L}0-9\\s]", "").split(" ");
		String initial = "";

		if (nameParts[0].length() > 0)
			initial = nameParts[0].substring(0, 1);
		if (nameParts.length > 1)
			initial += " " + nameParts[nameParts.length - 1].substring(0, 1);

		return initial;
	}

	/**
	 * Splits a string by " " and gets the first three characters of the first element plus the first character of the last element if available
	 * 
	 * @param str
	 *          the String to get the initials from
	 * @return str with the initial values as a String
	 */
	public static String getFirstLastInitials(String str) {

		String[] nameParts = str.trim().replaceAll("[^\\p{L}0-9\\s]", "").split(" ");
		String initial = "";

		if (nameParts[0].length() > 2)
			initial = nameParts[0].substring(0, 3);
		else if (nameParts[0].length() > 0)
			initial = nameParts[0].substring(0, 1);

		return (nameParts.length > 1) ? initial + " " + nameParts[nameParts.length - 1].substring(0, 1) : initial;
	}

	/**
	 * Reverses the elements of a string split by " "
	 * 
	 * @param str
	 *          the String to reverse
	 * @return str with the reversed values as a String
	 */
	public static String getReversedString(String str) {
		String[] nameParts = str.replaceAll("[^\\p{L}0-9\\s]", "").split(" ");

		StringBuilder sb = new StringBuilder();
		for (int i = nameParts.length - 1; i >= 0; --i) {
			sb.append(nameParts[i] + " ");
		}

		return sb.toString().trim();
	}

	/* Normalizes a name by removing prefixes occurring throughout the name and returning a 2-d array containing split names if found */
	/**
	 * Normalizes and returns a name split into two elements by removing prefixes and conjunctions which may occur throughout the name String.
	 * If a conjunction is found, the returned 2 element String[] will contain a name in each element
	 * 
	 * @param name
	 *          the name to normalize
	 * @param prefixes_first_pass
	 *          the first pass of prefixes to remove
	 * @param prefixes_second_pass
	 *          the second pass of prefixes and conjunctions
	 * @return name as a String[] with 2 elements
	 */
	public static String[] getOldNormalizedDupeNames(String name, String[][] prefixes_first_pass, String[][] prefixes_second_pass) {
		String normalName = name.toLowerCase();
		final int PREFIX_ELEMENT = 0;
		normalName = normalName.replaceAll("[-]", " ").trim();
		normalName = normalName.replaceAll("[ ][ ]", " ").trim();
		normalName = normalName.replaceAll("[^\\p{L}\\s]", "");

		String normalNames[] = null;
		boolean isNameNormalized = false;

		for (int i = 0; i < prefixes_first_pass.length; ++i) {
			int length = (normalName.length() < prefixes_first_pass[i][PREFIX_ELEMENT].length()) ? normalName.length()
					: prefixes_first_pass[i][PREFIX_ELEMENT].length();
			if (normalName.substring(0, length).equals(prefixes_first_pass[i][PREFIX_ELEMENT])) {
				isNameNormalized = true;
				normalName = normalName.replaceFirst(prefixes_first_pass[i][PREFIX_ELEMENT], "");
			}

			normalName = normalName.trim();

			for (int j = 0; j < prefixes_second_pass.length; ++j) {
				normalNames = normalName.split(prefixes_second_pass[j][PREFIX_ELEMENT]);

				if (normalNames != null && normalNames.length > 1) {
					for(int k = 0; k < normalNames.length; ++k)
						normalNames[k] = removeInitials(normalNames[k]);

					return normalNames;
				}
			}

			if (isNameNormalized)
				return new String[] { removeInitials(normalName), "" };
		}

		return new String[] { removeInitials(normalName), "" };
	}
	
	public static String[] getNewNormalizedDupeNames(String name, Pattern prefixes_first_pass) {
		
		String CONJUNCTION_REGEX = "[ ][a][n][d][ ]|[ ][a][n][d][/][o][r][ ]|[ ][&][ ]|[ ][e][t][ ]|[ ][o][r][ ]|[ ][o][u][ ]|(\\s+)?[/](\\s+)?|(\\s+)?\\+(\\s+)?";
		
		String normalName = StringUtils.stripAccents(name)
				.toLowerCase()
				.replaceAll("[-]", "")
				.replaceAll("[^\\p{L}\\s&/\\+]", "")
				.replaceAll("[ ][j][r][ ]|[ ][j][r]$|[ ][s][r][ ]|[ ][s][r]$", "")
				.replaceAll("\\s+", " ").trim();
		
		Matcher pfm1 = prefixes_first_pass.matcher(normalName);

		if(pfm1.find()) // replace the first prefix to safely break on conjunctions
			normalName = normalName.replaceFirst(pfm1.group(), "").trim();
		
		String[] names = normalName.split(CONJUNCTION_REGEX);
		
		for(int i = 0; i < names.length; ++i) { // remove any prefixes and initials
			pfm1 = prefixes_first_pass.matcher(names[i]);
			while(pfm1.find())
				names[i] = names[i].replaceFirst(pfm1.group(), " ").trim();
			
			names[i] = names[i].replaceAll("[^\\p{L}\\s]", "");
			names[i] = removeInitials(names[i]);
		}
		
		String familyName = ""; // fix for john & jean smith -> john smith & jean smith
		
		if(names.length >= 2) { // find the family name
			String lastPerson = names[names.length - 1];
			String[] lastPersonParts = lastPerson.split("\\s+");
			
			if(lastPersonParts.length > 1) { // default to using the last name
				familyName = lastPersonParts[lastPersonParts.length - 1];
			} else if(names.length >= 2) { // otherwise grab the second name
				String secondPerson = names[1];
				String[] secondPersonParts = secondPerson.split("\\s+");
				if(secondPersonParts.length > 1) 
					familyName = secondPersonParts[secondPersonParts.length - 1];
			}
			
		} else {
			if (names.length == 1)
				return new String[] {names[0], ""}; // only one name, return it
			else
				return new String[] {"", ""}; // No names! return empty
		}
		
		for(int i = 0; i < names.length; ++i) { // add family name to records without last name
			String[] personParts = names[i].split("\\s+");
			
			if(personParts.length == 1) // if there is no last name
				if(familyName.length() > 0)
					names[i] = names[i] + " " + familyName;
		}
		
		return names;
	}

	// convert integer to column letters
	public static String toAlphabetic(int i) {
		if (i < 0) {
			return "-" + toAlphabetic(-i - 1);
		}

		int quot = i / 26;
		int rem = i % 26;
		char letter = (char) ('A' + rem);
		if (quot == 0) {
			return "" + letter;
		} else {
			return toAlphabetic(quot - 1) + letter;
		}
	}

	// Convert column letters to an integer
	public static int titleToNumber(String s) {
		int result = 0;
		for (char c : s.toCharArray()) {
			result = result * 26 + (c - 'A') + 1;
		}
		return result;
	}

	public static int extractInt(String s) {
		String num = s.replaceAll("\\D", "");
		// return 0 if no digits found
		return num.isEmpty() ? 0 : Integer.parseInt(num);
	}

	public static BigDecimal extractBigDecimal(String s) {
		String num = s.replaceAll("\\D", "");
		// return 0 if no digits found
		return num.isEmpty() ? new BigDecimal("0") : new BigDecimal(num);
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static String getFileExtension(File file) {
		String extension = "";

		int i = file.getName().lastIndexOf('.');
		if (i > 0) {
			extension = file.getName().substring(i + 1);
		}

		return extension;
	}

	public static String removeInitials(String name) {

		String[] nameParts = name.split("\\s+");
		StringBuilder sb = new StringBuilder();
		String nameWithoutInitials = "";

		if(nameParts.length > 1 && nameParts[0].length() > 1) {
			for(String part : nameParts) {
				if(part.length() != 1)
					sb.append(part + " ");

			}
			nameWithoutInitials = sb.toString().trim();
		}

		if(nameWithoutInitials.length() > 0)
			return nameWithoutInitials;

		return name;

	}
	
	public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			if (unit.equals("K")) {
				dist = dist * 1.609344;
			} else if (unit.equals("N")) {
				dist = dist * 0.8684;
			}
			return (dist);
		}
	}
	
	public static String pc2prov(String pc) {
		boolean isUSA = false;
		int zipcode = -1;
		String province = "";
		
		pc = pc.toUpperCase().trim().replaceAll("[^a-zA-Z0-9-]", "").replaceFirst("^0+(?!$)", "");
		
		if(Validators.isWholeNumber(pc.replaceAll("[^a-zA-Z0-9]", ""))) {
			isUSA = true;
			pc = pc.split("-")[0];
			if(pc.length() > 5)
				pc = pc.substring(0, 5);
			else if (pc.length() == 0)
				pc = "0";
			
			zipcode = Integer.valueOf(pc);
		}
				
		if(!isUSA) {
			String firstLetter = (pc.length() > 0) ? pc.substring(0, 1) : "";
			
			switch(firstLetter) {
			case "A":
				province = "NL";
				break;
			case "B":
				province = "NS";
				break;
			case "C":
				province = "PE";
				break;
			case "E":
				province = "NB";
				break;
			case "G":
			case "H":
			case "J":
				province = "QC";
				break;
			case "K":
			case "L":
			case "M":
			case "N":
			case "P":
				province = "ON";
				break;
			case "R":
				province = "MB";
				break;
			case "S":
				province = "SK";
				break;
			case "T":
				province = "AB";
				break;
			case "V":
				province = "BC";
				break;
			case "X":
				province = "NT";
				break;
			case "Y":
				province = "YT";
				break;
			default:
				break;
			}
			
		} else if (zipcode > -1){
			/* Code cases alphabetized by state */
			  if (zipcode >= 35000 && zipcode <= 36999) {
			      province = "AL";
			      //province = "Alabama";
			  } else if (zipcode >= 99500 && zipcode <= 99999) {
			      province = "AK";
			      //province = "Alaska";
			  } else if (zipcode >= 85000 && zipcode <= 86999) {
			      province = "AZ";
			      //province = "Arizona";
			  } else if (zipcode >= 71600 && zipcode <= 72999) {
			      province = "AR";
			      //province = "Arkansas";
			  } else if (zipcode >= 90000 && zipcode <= 96699) {
			      province = "CA";
			      //province = "California";
			  } else if (zipcode >= 80000 && zipcode <= 81999) {
			      province = "CO";
			      //province = "Colorado";
			  } else if ((zipcode >= 6000 && zipcode <= 6389) || (zipcode >= 6391 && zipcode <= 6999)) {
			      province = "CT";
			      //province = "Connecticut";
			  } else if (zipcode >= 19700 && zipcode <= 19999) {
			      province = "DE";
			      //province = "Delaware";
			  } else if (zipcode >= 32000 && zipcode <= 34999) {
			      province = "FL";
			      //province = "Florida";
			  } else if ( (zipcode >= 30000 && zipcode <= 31999) || (zipcode >= 39800 && zipcode <= 39999) ) {
			      province = "GA";
			      //province = "Georgia";
			  } else if (zipcode >= 96700 && zipcode <= 96999) {
			      province = "HI";
			      //province = "Hawaii";
			  } else if (zipcode >= 83200 && zipcode <= 83999 && zipcode != 83414) {
			      province = "ID";
			      //province = "Idaho";
			  } else if (zipcode >= 60000 && zipcode <= 62999) {
			      province = "IL";
			      //province = "Illinois";
			  } else if (zipcode >= 46000 && zipcode <= 47999) {
			      province = "IN";
			      //province = "Indiana";
			  } else if (zipcode >= 50000 && zipcode <= 52999) {
			      province = "IA";
			      //province = "Iowa";
			  } else if (zipcode >= 66000 && zipcode <= 67999) {
			      province = "KS";
			      //province = "Kansas";
			  } else if (zipcode >= 40000 && zipcode <= 42999) {
			      province = "KY";
			      //province = "Kentucky";
			  } else if (zipcode >= 70000 && zipcode <= 71599) {
			      province = "LA";
			      //province = "Louisiana";
			  } else if (zipcode >= 3900 && zipcode <= 4999) {
			      province = "ME";
			      //province = "Maine";
			  } else if (zipcode >= 20600 && zipcode <= 21999) {
			      province = "MD";
			      //province = "Maryland";
			  } else if ( (zipcode >= 1000 && zipcode <= 2799) || (zipcode == 5501) || (zipcode == 5544 ) ) {
			      province = "MA";
			      //province = "Massachusetts";
			  } else if (zipcode >= 48000 && zipcode <= 49999) {
			      province = "MI";
			      //province = "Michigan";
			  } else if (zipcode >= 55000 && zipcode <= 56899) {
			      province = "MN";
			      //province = "Minnesota";
			  } else if (zipcode >= 38600 && zipcode <= 39999) {
			      province = "MS";
			      //province = "Mississippi";
			  } else if (zipcode >= 63000 && zipcode <= 65999) {
			      province = "MO";
			      //province = "Missouri";
			  } else if (zipcode >= 59000 && zipcode <= 59999) {
			      province = "MT";
			      //province = "Montana";
			  } else if (zipcode >= 27000 && zipcode <= 28999) {
			      province = "NC";
			      //province = "North Carolina";
			  } else if (zipcode >= 58000 && zipcode <= 58999) {
			      province = "ND";
			      //province = "North Dakota";
			  } else if (zipcode >= 68000 && zipcode <= 69999) {
			      province = "NE";
			      //province = "Nebraska";
			  } else if (zipcode >= 88900 && zipcode <= 89999) {
			      province = "NV";
			      //province = "Nevada";
			  } else if (zipcode >= 3000 && zipcode <= 3899) {
			      province = "NH";
			      //province = "New Hampshire";
			  } else if (zipcode >= 7000 && zipcode <= 8999) {
			      province = "NJ";
			      //province = "New Jersey";
			  } else if (zipcode >= 87000 && zipcode <= 88499) {
			      province = "NM";
			      //province = "New Mexico";
			  } else if ( (zipcode >= 10000 && zipcode <= 14999) || (zipcode == 6390) || (zipcode == 501) || (zipcode == 544) ) {
			      province = "NY";
			      //province = "New York";
			  } else if (zipcode >= 43000 && zipcode <= 45999) {
			      province = "OH";
			      //province = "Ohio";
			  } else if ((zipcode >= 73000 && zipcode <= 73199) || (zipcode >= 73400 && zipcode <= 74999) ) {
			      province = "OK";
			      //province = "Oklahoma";
			  } else if (zipcode >= 97000 && zipcode <= 97999) {
			      province = "OR";
			      //province = "Oregon";
			  } else if (zipcode >= 15000 && zipcode <= 19699) {
			      province = "PA";
			      //province = "Pennsylvania";
			  } else if (zipcode >= 300 && zipcode <= 999) {
			      province = "PR";
			      //province = "Puerto Rico";
			  } else if (zipcode >= 2800 && zipcode <= 2999) {
			      province = "RI";
			      //province = "Rhode Island";
			  } else if (zipcode >= 29000 && zipcode <= 29999) {
			      province = "SC";
			      //province = "South Carolina";
			  } else if (zipcode >= 57000 && zipcode <= 57999) {
			      province = "SD";
			      //province = "South Dakota";
			  } else if (zipcode >= 37000 && zipcode <= 38599) {
			      province = "TN";
			      //province = "Tennessee";
			  } else if ( (zipcode >= 75000 && zipcode <= 79999) || (zipcode >= 73301 && zipcode <= 73399) ||  (zipcode >= 88500 && zipcode <= 88599) ) {
			      province = "TX";
			      //province = "Texas";
			  } else if (zipcode >= 84000 && zipcode <= 84999) {
			      province = "UT";
			      //province = "Utah";
			  } else if (zipcode >= 5000 && zipcode <= 5999) {
			      province = "VT";
			      //province = "Vermont";
			  } else if ( (zipcode >= 20100 && zipcode <= 20199) || (zipcode >= 22000 && zipcode <= 24699) || (zipcode == 20598) ) {
			      province = "VA";
			      //province = "Virginia";
			  } else if ( (zipcode >= 20000 && zipcode <= 20099) || (zipcode >= 20200 && zipcode <= 20599) || (zipcode >= 56900 && zipcode <= 56999) ) {
			      province = "DC";
			      //province = "Washington DC";
			  } else if (zipcode >= 98000 && zipcode <= 99499) {
			      province = "WA";
			      //province = "Washington";
			  } else if (zipcode >= 24700 && zipcode <= 26999) {
			      province = "WV";
			      //province = "West Virginia";
			  } else if (zipcode >= 53000 && zipcode <= 54999) {
			      province = "WI";
			      //province = "Wisconsin";
			  } else if ( (zipcode >= 82000 && zipcode <= 83199) || zipcode == 83414 ) {
			      province = "WY";
			      //province = "Wyoming";
			  }
		}
		
		return province;
	
	}
	

}
