/**
 * Project: Dfuze
 * File: InputData.java
 * Date: Jun 9, 2020
 * Time: 8:25:04 PM
 */
package com.mom.dfuze.data;

import java.util.Arrays;

/**
 * InputData Class used to abstract various forms of imported data into a standardized format
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class InputData {

  private String[] headers;
  private String[][] data;
  private String fileName;

  /**
   * Constructor for Objects of Class InputData
   * 
   * @param message
   *          the exception message.
   */
  public InputData(String[] headers, String[][] data) {
    setHeaders(headers);
    setData(data);
  }

  /**
   * @param headers
   *          the headers to set
   */
  public void setHeaders(String[] headers) {
    this.headers = headers;
  }

  /**
   * @param data
   *          the data to set
   */
  public void setData(String[][] data) {
    this.data = data;
  }
  
  public void setFileName(String fileName) {
	  this.fileName = fileName;
  }

  /**
   * @return headers as a String[]
   */
  public String[] getHeaders() {
    return headers;
  }

  /**
   * @return data as a String[][]
   */
  public String[][] getData() {
    return data;
  }
  
  public String getFileName() {
	  return fileName;
  }
  
  public void addFileNameToHeadersAndData(String fileName) {
	  if(headers != null) {
		  String fileHeader = "dfFileName";
		  for(int i = 0; i < headers.length; ++i) {
			  if(headers[i].equalsIgnoreCase(fileHeader)) {
				  headers[i] = "IN__" + headers[i];
				  break;
			  }
		  }
		  headers = Arrays.copyOf(headers, headers.length + 1);
		  headers[headers.length-1] = fileHeader;
	  }
	  
	  String[][] newData = new String[data.length][headers.length];
	  for(int i = 0; i < data.length; ++i) {
		  String[] newRow = Arrays.copyOf(data[i], data[i].length + 1);
		  newRow[newRow.length-1] = fileName;
		  newData[i] = newRow;
	  }
	  data = newData; 
  }

}
