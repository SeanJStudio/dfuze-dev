/**
 * Project: Dfuze
 * File: TextReader.java
 * Date: Apr 11, 2020
 * Time: 11:31:32 AM
 */
package com.mom.dfuze.io;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;
import com.ibm.icu.text.CharsetDetector;
import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.InputData;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.UiController;

/**
 * TextReader class to perform file input operations on text based file formats
 * Uses the Singleton patter to prevent instantiation
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class TextReader {

	/**
	 * Private Constructor for TextReader class to prevent instantiation
	 */
	private TextReader() {
	}

	/**
	 * Reads and extracts headers and data from a text based file
	 * 
	 * @param file
	 *          the text file to read
	 * @param delimiter
	 *          the value to delimit the text by
	 * @param userData
	 *          the userData to traverse the Dfuze headers from
	 * @return the data in the table as InputData
	 * @throws ApplicationException
	 *           in case something goes wrong
	 */
	public static InputData read(File file, char delimiter, boolean createHeaderRow) throws ApplicationException {

		String[] inHeaders = null;
		String[][] data = null;

		UnicodeBOMInputStream ubis = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;

		try {
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			String charsetName = getCharset(file);

			fis = new FileInputStream(file);
			ubis = new UnicodeBOMInputStream(fis);
			isr = new InputStreamReader(ubis, charsetName);
			
			// see https://stackoverflow.com/questions/1835430/byte-order-mark-screws-up-file-reading-in-java/1835529#1835529
			ubis.skipBOM();

			// get the data as a list of lists
			List<List<String>> arr = org.simpleflatmapper.csv.CsvParser.separator(delimiter).stream(isr).map(Arrays::asList).collect(Collectors.toList());

			isr.close();
			ubis.close();
			fis.close();
			
			isr = null;
			ubis = null;
			fis = null;
			
			// Get the number of fields
			int numberOfFields = 0;
			for(List<String> list : arr)
				if(list.size() > numberOfFields)
					numberOfFields = list.size();

			List<List<String>> deepArr = new ArrayList<>();
			
			// create a deep copy of the list
			for(int i = 0; i < arr.size(); ++i) {
				
				List<String> temp = arr.get(i);
					
				deepArr.add(new ArrayList<>(temp));

				while(deepArr.get(i).size() < numberOfFields)
					deepArr.get(i).add("");
			}
			
			// Release some memory
			arr = null;

			// initialize the headers
			inHeaders = new String[numberOfFields];
			Set<String> headersDupeCheck = new HashSet<String>();

			// if the file has header row
			if(!createHeaderRow) {
			//Check for dupes

			for (int i = 0; i < numberOfFields; ++i) {
				inHeaders[i] = Common.checkIfDfHeader(deepArr.get(0).get(i), Arrays.copyOf(headersDupeCheck.toArray(), headersDupeCheck.toArray().length, String[].class), UserData.getAllDfHeaders());

				if (!headersDupeCheck.add(inHeaders[i].toLowerCase())) {
					throw new ApplicationException(String.format("Header Fields cannot hold duplicate values.\nThere are multiple columns with the value \"%s\"", inHeaders[i]));
				}
			}

			deepArr.remove(0);
			}
			
			// If the file doesn't have a header row, create the headers
			if(createHeaderRow) 
				for(int i = 0; i < numberOfFields; ++i)
					inHeaders[i] = "F" + (i + 1);

			data = new String[deepArr.size()][numberOfFields];
			
			for (int i = 0; i < deepArr.size(); ++i) {
				for (int j = 0; j < deepArr.get(i).size(); ++j) {
					data[i][j] = deepArr.get(i).get(j);
				}
			}
			
			// Release some memory
			deepArr = null;

		} catch (IOException e) {
			UiController.handle(e);
		} finally {
			try {
				if(fis != null)
					fis.close();
				if(isr != null)
					isr.close();
				if(ubis != null)
					ubis.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}

		return new InputData(inHeaders, data);

	}


	public static String previewText(File file) throws ApplicationException {
		// String previewText = "";
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = null;
		FileInputStream fis = null;
		UnicodeBOMInputStream ubis = null;
		try {

			String charsetName = getCharset(file);

			fis = new FileInputStream(file);
			ubis = new UnicodeBOMInputStream(fis);
			isr = new InputStreamReader(ubis, charsetName);
			
			ubis.skipBOM();

			char[] buffer = new char[4096];

			for(int len; (len = isr.read(buffer)) > 0;)
				sb.append(buffer, 0, len);

		} catch (IOException e) {
			UiController.handle(e);
		} finally {
			try {
				if(fis != null)
					fis.close();
				if(isr != null)
					isr.close();
				if(ubis != null)
					ubis.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}
		
		

		return sb.toString();
		//return previewText;

	}
	
	public static InputData readFixedWidth(File file, ArrayList<int[]> fieldIndexes, boolean createHeaderRow) throws ApplicationException {

		String[] inHeaders = null;
		String[][] data = null;

		UnicodeBOMInputStream ubis = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;

		try {
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			String charsetName = getCharset(file);

			fis = new FileInputStream(file);
			ubis = new UnicodeBOMInputStream(fis);
			isr = new InputStreamReader(ubis, charsetName);
			
			// see https://stackoverflow.com/questions/1835430/byte-order-mark-screws-up-file-reading-in-java/1835529#1835529
			ubis.skipBOM();
			
			char delimiter = (char) 28; // a character that wont be found

			// get the data as a list of lists
			List<List<String>> arr = org.simpleflatmapper.csv.CsvParser.separator(delimiter).stream(isr).map(Arrays::asList).collect(Collectors.toList());
			
			
			
			isr.close();
			ubis.close();
			fis.close();
			
			isr = null;
			ubis = null;
			fis = null;
			
			// Get the number of fields
			int numberOfFields = fieldIndexes.size();
			
			List<List<String>> deepArr = new ArrayList<>();
			
			// create a deep copy of the list
			for(int i = 0; i < arr.size(); ++i) {
				
				List<String> fixedArr = arr.get(i);
				for(int j = 0; j < fixedArr.size(); ++j) {
					fixedArr.set(j, fixedArr.get(j).replaceAll("’|‘", "'").replaceAll("“|”", "\""));
				}

				ArrayList<String> temp = new ArrayList<>();
				
				for(int j = 0; j < fieldIndexes.size(); ++j) {
					int end = fieldIndexes.get(j)[1];
					
					if(end > fixedArr.get(0).length())
						end = fixedArr.get(0).length();
					
					String field = fixedArr.get(0).substring(fieldIndexes.get(j)[0], end);
					temp.add(field.trim());
				}
				
				deepArr.add(temp);

			}
			
			// Release some memory
			arr = null;

			// initialize the headers
			inHeaders = new String[numberOfFields];
			Set<String> headersDupeCheck = new HashSet<String>();

			// if the file has header row
			if(!createHeaderRow) {
			//Check for dupes

			for (int i = 0; i < numberOfFields; ++i) {
				inHeaders[i] = Common.checkIfDfHeader(deepArr.get(0).get(i), Arrays.copyOf(headersDupeCheck.toArray(), headersDupeCheck.toArray().length, String[].class), UserData.getAllDfHeaders());

				if (!headersDupeCheck.add(inHeaders[i].toLowerCase())) {
					throw new ApplicationException(String.format("Header Fields cannot hold duplicate values.\nThere are multiple columns with the value \"%s\"", inHeaders[i]));
				}
			}

			deepArr.remove(0);
			}
			
			// If the file doesn't have a header row, create the headers
			if(createHeaderRow) 
				for(int i = 0; i < numberOfFields; ++i)
					inHeaders[i] = "F" + (i + 1);

			data = new String[deepArr.size()][numberOfFields];
			
			for (int i = 0; i < deepArr.size(); ++i) {
				for (int j = 0; j < deepArr.get(i).size(); ++j) {
					data[i][j] = deepArr.get(i).get(j);
				}
			}
			
			// Release some memory
			deepArr = null;

		} catch (IOException e) {
			UiController.handle(e);
		} finally {
			try {
				if(fis != null)
					fis.close();
				if(isr != null)
					isr.close();
				if(ubis != null)
					ubis.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}

		return new InputData(inHeaders, data);

	}


	/**
	 * Reads and returns a multidimensional String Array holding prefix values
	 * 
	 * @param file
	 *          the file of prefixes to read
	 * @param delimiter
	 *          the delimiter to delimit the data by
	 * @return the prefixes as a String[][]
	 * @throws ApplicationException
	 *           in case something goes wrong
	 */
	public static String[][] readDataLookup(File file, char delimiter) throws ApplicationException {
		String[][] data = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;
		UnicodeBOMInputStream ubis = null;
		try {

			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			String charsetName = getCharset(file);

			fis = new FileInputStream(file);
			ubis = new UnicodeBOMInputStream(fis);
			isr = new InputStreamReader(ubis, charsetName);
			
			ubis.skipBOM();

			List<List<String>> arr = org.simpleflatmapper.csv.CsvParser.separator(delimiter).stream(isr).map(Arrays::asList).collect(Collectors.toList());

			int numberOfFields = arr.get(0).size();

			arr.remove(0);
			data = new String[arr.size()][numberOfFields];

			for (int i = 0; i < arr.size(); ++i)
				for (int j = 0; j < arr.get(i).size(); ++j)
					data[i][j] = arr.get(i).get(j);
			
			// Release memory
			arr = null;

		} catch (IOException e) {
			UiController.handle(e);
		} finally {
			try {
				if(fis != null)
					fis.close();
				if(isr != null)
					isr.close();
				if(ubis != null)
					ubis.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}
		return data;
	}
	
	public static HashSet<String> readTextAsHashSet(File file, char delimiter) throws ApplicationException {
		InputStreamReader isr = null;
		FileInputStream fis = null;
		UnicodeBOMInputStream ubis = null;
		HashSet<String> set = new HashSet<>();
		try {

			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file needed for readTextAsHashSet is currently in use.\nPlease close the file and try again."));

			String charsetName = getCharset(file);

			fis = new FileInputStream(file);
			ubis = new UnicodeBOMInputStream(fis);
			isr = new InputStreamReader(ubis, charsetName);
			
			ubis.skipBOM();

			List<List<String>> arr = org.simpleflatmapper.csv.CsvParser.separator(delimiter).stream(isr).map(Arrays::asList).collect(Collectors.toList());
			
			if(arr.size() < 1)
				throw new ApplicationException(String.format("The file used in readTextAsHashSet has no data."));

			int numberOfFields = arr.get(0).size();
			
			if(numberOfFields != 1)
				throw new ApplicationException(String.format("The file used in readTextAsHashSet should contain one and only one field."));
			
			//Remove the header row
			arr.remove(0);
			
			if(arr.size() < 1)
				throw new ApplicationException(String.format("The file used in readTextAsHashSet has no data after removing the header row."));

			for (int i = 0; i < arr.size(); ++i)
				set.add(arr.get(i).get(0));
			
			// Release memory
			arr = null;

		} catch (IOException e) {
			UiController.handle(e);
		} finally {
			try {
				if(fis != null)
					fis.close();
				if(isr != null)
					isr.close();
				if(ubis != null)
					ubis.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}
		return set;	
	}
	
	public static Hashtable<String, String> readTextAsHashtable(File file, char delimiter) throws ApplicationException {
		InputStreamReader isr = null;
		FileInputStream fis = null;
		UnicodeBOMInputStream ubis = null;
		Hashtable<String, String> table = new Hashtable<>();
		try {

			//boolean fileIsLocked = !file.renameTo(file);
			//if (fileIsLocked)
				//throw new ApplicationException(String.format("This file needed for readTextAsHashtable is currently in use.\nPlease close the file and try again."));

			String charsetName = getCharset(file);

			fis = new FileInputStream(file);
			ubis = new UnicodeBOMInputStream(fis);
			isr = new InputStreamReader(ubis, charsetName);
			
			ubis.skipBOM();

			List<List<String>> arr = org.simpleflatmapper.csv.CsvParser.separator(delimiter).stream(isr).map(Arrays::asList).collect(Collectors.toList());
			
			if(arr.size() < 1)
				throw new ApplicationException(String.format("The file used in readTextAsHashtable has no data."));

			int numberOfFields = arr.get(0).size();
			
			if(numberOfFields != 2)
				throw new ApplicationException(String.format("The file used in readTextAsHashtable should contain two and only two fields."));
			
			//Remove the header row
			arr.remove(0);
			
			if(arr.size() < 1)
				throw new ApplicationException(String.format("The file used in readTextAsHashtable has no data after removing the header row."));

			for (int i = 0; i < arr.size(); ++i)
				table.put(arr.get(i).get(0),arr.get(i).get(1));

			// Release memory
			arr = null;

		} catch (IOException e) {
			UiController.handle(e);
		} finally {
			try {
				if(fis != null)
					fis.close();
				if(isr != null)
					isr.close();
				if(ubis != null)
					ubis.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}
		return table;	
	}

	public static String getCharset(File file) throws ApplicationException{

		String charsetName = "UTF-8";
		//String charsetName = "windows-1252";

		// Try to figure out which encoding is being used in the file  
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			CharsetDetector cd = new CharsetDetector();
			cd.setText(bis);
			
			charsetName = cd.detect().getName();
			
			System.out.println(charsetName);
			
			if(charsetName.equalsIgnoreCase("ISO-8859-1"))	// This encoding causes types of apostrophes to display as '?'
				charsetName = "windows-1252";				// Therefore change to UTF-8
			
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		} finally {
			try {
				if(fis != null)
					fis.close();
				if(bis != null)
					bis.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}

		return charsetName;
	}


}
