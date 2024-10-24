/**
 * Project: Dfuze
 * File: DbfWriter.java
 * Date: Mar 30, 2020
 * Time: 1:08:25 AM
 */
package com.mom.dfuze.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.ui.UiController;

/**
 * DbfWriter class to perform file output operations on DBF Database file formats
 * Uses the Singleton pattern to prevent instantiation
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class DbfWriter {

  private static final int MAX_FIELD_LENGTH = 10;

  /**
   * Private constructor to prevent instantiation
   */
  private DbfWriter() {
  }

  /**
   * Creates a DBF Database file
   * 
   * @param file
   *          the file to create
   * @param headers
   *          the headers to set in the DBF database file
   * @param data
   *          the data to set in the DBF databse file
   * @return if the operation was successful as a Boolean
   * @throws ApplicationException
   *           throws Exception in case something goes wrong
   */
  public static boolean write(File file, String[] headers, String[][] data) throws ApplicationException {
	  if(file.exists() && !file.isDirectory()) { 
		    boolean fileIsLocked = !file.renameTo(file);
		    if (fileIsLocked)
		      throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}

    String fileName = file.getAbsolutePath(); // get the path
    boolean hasMemoryLoss = false;

    if (fileName.contains(".")) // get all text before '.' if found
      fileName = fileName.substring(0, fileName.lastIndexOf('.'));

    file = new File(fileName + ".dbf"); // make sure its a dbf file

    FileOutputStream fileOutputStream = null;
    DBFWriter writer = null;

    try {
		System.out.println("writing");
      int[] maxFieldLength = UiController.getUserData().getExportMaxFieldLength();

      DBFField[] fields = new DBFField[headers.length];
      Set<String> headerNameDupeCheck = new HashSet<>();
      int dupeFieldNum = 0;
      for (int i = 0; i < headers.length; i++) {

        fields[i] = new DBFField();
        String fieldName = (headers[i].length()) > MAX_FIELD_LENGTH ? headers[i].substring(0, MAX_FIELD_LENGTH) : headers[i];
        if (fieldName.isEmpty())
          fieldName = "Column " + (i + 1);
        while (!headerNameDupeCheck.add(fieldName)) {
          fieldName = fieldName.substring(0, fieldName.length() - String.valueOf(dupeFieldNum).length()) + dupeFieldNum++;
        }

        fields[i].setName(fieldName);
        fields[i].setType(DBFDataType.CHARACTER);

        if (maxFieldLength[i] <= 254) {
          fields[i].setLength(maxFieldLength[i]);
        } else {
          hasMemoryLoss = true;
          fields[i].setLength(254);
        }

        System.out.println(fields[i].getName());
      }

      if (hasMemoryLoss) {
        int buttonPressed = JOptionPane.showConfirmDialog(UiController.getMainFrame(),
                "Fields are greater than 254 characters which will result in memory loss. Would you still like to export?", "Memory Loss",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (buttonPressed != JOptionPane.YES_OPTION) {
          return false;
        }
      }

      writer = new DBFWriter(fileOutputStream = new FileOutputStream(file, false));// false so we create a file, not append
      writer.setFields(fields);

      for (int i = 0; i < data.length; i++) {
        Object rowData[] = new Object[headers.length];
        for (int j = 0; j < headers.length; j++) {
          rowData[j] = data[i][j];
        }
        writer.addRecord(rowData);
      };
      writer.close();
      fileOutputStream.close();
      return true;

    } catch (IOException e) {
      throw new ApplicationException(e.getMessage());
    } finally {

    }
  }

}
