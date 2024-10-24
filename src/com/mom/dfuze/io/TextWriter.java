/**
 * Project: Dfuze
 * File: TextWriter.java
 * Date: Apr 13, 2020
 * Time: 12:23:16 PM
 */
package com.mom.dfuze.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.simpleflatmapper.lightningcsv.CsvWriter;

import com.mom.dfuze.ApplicationException;

/**
 * TextWriter class to perform file output operations on text file formats
 * Uses the Singleton pattern to prevent instantiation
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class TextWriter {
	
	private static final String CHARSET = "windows-1252";

	/**
	 * Private Constructor for TextWriter Class to prevent instantiation
	 */
	private TextWriter() {
	}

	/**
	 * Creates a text based file
	 * 
	 * @param file
	 *          the text based file to create
	 * @param delimiter
	 *          the delimiter to delimited the data by
	 * @param includeHeader
	 *          boolean value to include headers or not
	 * @param headers
	 *          the headers to set in the file
	 * @param data
	 *          the data to set in the file
	 * @throws ApplicationException
	 *           in case something goes wrong
	 */
	public static void write(File file, char delimiter, boolean includeHeader, String[] headers, String[][] data) throws ApplicationException {

		if (file.exists() && !file.isDirectory()) {
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}

		try {
			BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), CHARSET));
			CsvWriter writer = CsvWriter.dsl().separator(delimiter).alwaysEscape().to(bufferedwriter);
			
			if(includeHeader)
				writer.appendRow(headers);
			
			for (String[] record : data)
				writer.appendRow(record);
			
			bufferedwriter.close();
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		}
	}

	public static void writeRaw(File file, char delimiter, List<List<String>> data) throws ApplicationException {

		if (file.exists() && !file.isDirectory()) {
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}

		try {
			BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), CHARSET));
			CsvWriter writer = CsvWriter.dsl().separator(delimiter).alwaysEscape().to(bufferedwriter);

			for (List<String> record : data) {
				writer.appendRow(record);
			}

			bufferedwriter.close();
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		}
	}

	public static void writeTextNoDelimiter(File file, String string) throws ApplicationException {

		if (file.exists() && !file.isDirectory()) {
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}

		try {
			BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), CHARSET));

			bufferedwriter.write(string);
			bufferedwriter.close();

		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		}
	}

}
