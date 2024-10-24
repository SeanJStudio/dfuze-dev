/**
 * Project: DFuze
 * File: DbfReader.java
 * Date: Mar 2, 2020
 * Time: 8:20:36 PM
 */
package com.mom.dfuze.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.InputData;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;

/**
 * DbfReader Class to perform file input operations on DBF database file
 * Uses the Singleton patter to prevent instantiation
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 * 
 *         https://github.com/albfernandez/javadbf
 *
 */
public class DbfReader {

  /**
   * Private constructor to prevent instantiation
   */
  private DbfReader() {

  }

  /**
   * Reads and extracts headers and data from a DBF database file
   * 
   * @param file the database file to read
   * @param userData the userData to get and traverse the Dfuze header values
   * @return  the data in the table as InputData
   * @throws ApplicationException throws Exception in case something goes wrong
   */
  public static InputData read(File file) throws ApplicationException {
    DBFReader reader = null;
    FileInputStream fileInputStream = null;
    InputData inputData = null;
    try {

      boolean fileIsLocked = !file.renameTo(file);
      if (fileIsLocked)
        throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

      // IBM863 to handle French characters
      // May need to revise this to allow users to select which charset to use, but for now it should be fine
      reader = new DBFReader(fileInputStream = new FileInputStream(file), Charset.forName("IBM863"));
      int numberOfFields = reader.getFieldCount(); // get the amount of fields as an int
      String[] inHeaders = new String[numberOfFields]; // create new String[] to store fields aka headers

      for (int i = 0; i < numberOfFields; ++i) {
        DBFField field = reader.getField(i); // get the field
        inHeaders[i] = field.getName(); // add the field to headers array
      }

      String[][] data = new String[reader.getRecordCount()][numberOfFields]; // 2-d array to store each row and then each field value
      DBFRow row;
      int counter = 0;

      while ((row = reader.nextRow()) != null) {
        for (int i = 0; i < numberOfFields; ++i) {
        	String value = row.getString(inHeaders[i]);
        	if(value == null)
        		value = "";
          data[counter][i] = value; // loop through rows to store each field value
        }
        ++counter; // increment
      }

      for (int i = 0; i < inHeaders.length; ++i)
        inHeaders[i] = Common.checkIfDfHeader(inHeaders[i], new String[0], UserData.getAllDfHeaders());

      inputData = new InputData(inHeaders, data);
      // userData.load(inHeaders, data);

    } catch (IOException e) {
      throw new ApplicationException(e.getMessage());
    } finally {
      try {
        fileInputStream.close();
        reader.close();
      } catch (IOException e) {

      }
    }
    reader.close();
    return inputData;
  }
  
  /**
   * Reads and extracts headers and data from a DBF database file
   * 
   * @param file the database file to read
   * @param userData the userData to get and traverse the Dfuze header values
   * @return  the data in the table as InputData
   * @throws ApplicationException throws Exception in case something goes wrong
   */
  public static List<List<String>> readRaw(File file) throws ApplicationException {
    DBFReader reader = null;
    FileInputStream fileInputStream = null;
    String[] inHeaders = null;
	String[][] data = null;
    try {

      boolean fileIsLocked = !file.renameTo(file);
      if (fileIsLocked)
        throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

      // IBM863 to handle French characters
      // May need to revise this to allow users to select which charset to use, but for now it should be fine
      reader = new DBFReader(fileInputStream = new FileInputStream(file), Charset.forName("IBM863"));
      int numberOfFields = reader.getFieldCount(); // get the amount of fields as an int
      inHeaders = new String[numberOfFields]; // create new String[] to store fields aka headers

      for (int i = 0; i < numberOfFields; ++i) {
        DBFField field = reader.getField(i); // get the field
        inHeaders[i] = field.getName(); // add the field to headers array
      }

      data = new String[reader.getRecordCount()][numberOfFields]; // 2-d array to store each row and then each field value
      DBFRow row;
      int counter = 0;

      while ((row = reader.nextRow()) != null) {
        for (int i = 0; i < numberOfFields; ++i) {
        	String value = row.getString(inHeaders[i]);
        	if(value == null)
        		value = "";
          data[counter][i] = value; // loop through rows to store each field value
        }
        ++counter; // increment
      }

      for (int i = 0; i < inHeaders.length; ++i)
        inHeaders[i] = Common.checkIfDfHeader(inHeaders[i], new String[0], UserData.getAllDfHeaders());

    } catch (IOException e) {
      throw new ApplicationException(e.getMessage());
    } finally {
      try {
        fileInputStream.close();
        reader.close();
      } catch (IOException e) {

      }
    }
    reader.close();
    
    List<List<String>> newData = new ArrayList<>();
	newData.add(Arrays.asList(inHeaders));

	for (String[] line : data) {
		newData.add(Arrays.asList(line));
	}

	return newData;
  }

}