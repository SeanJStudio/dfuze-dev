/**
 * Project: Dfuze
 * File: XLSXReader.java
 * Date: Apr 4, 2020
 * Time: 7:04:47 AM
 */
package com.mom.dfuze.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.InputData;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;

/**
 * ExcelReader class to perform file input operations on Microsoft Excel file formats
 * Uses the Singleton patter to prevent instantiation
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("deprecation")
public class ExcelReader {

  /**
   * Private constructor to prevent instantiation
   */
  private ExcelReader() {
  }

  /**
   * Reads and extracts headers and data from an Excel (1997-2003) .xls file
   * 
   * @param file
   *          the file to read
   * @param userData
   *          the userData to traverse all the Dfuze headers from
   * @return the data in the file as InputData
   * @throws ApplicationException
   *           the exception thrown in case something goes wrong
   * @throws InvalidFormatException
   *           the exception thrown in case something goes wrong
   */
  public static InputData readXLS(File file, String sheetName, boolean createHeaderRow, boolean isEncrypted, String password) throws Exception {
    Workbook workbook = null;
    String[] inHeaders = null;
    String[][] data = null;
    Sheet sheet = null;
    try {

    //  boolean fileIsLocked = !file.renameTo(file); // For some reason you cant use this method for XLS files
      
      //if (fileIsLocked)
      //  throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

      // we create an Workbook object for our Excel File
      if(!isEncrypted)
          workbook = WorkbookFactory.create(file);
      else
    	  workbook = WorkbookFactory.create(file, password);


      CreationHelper createHelper = workbook.getCreationHelper();
      DataFormatter formatter = new DataFormatter();
      CellStyle styledate = workbook.createCellStyle();
      styledate.setDataFormat(createHelper.createDataFormat().getFormat("M/d/yyyy"));

      FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
      // we get first sheet
      int numOfSheets = workbook.getNumberOfSheets();

      for (int i = 0; i < numOfSheets; ++i) {
        if (workbook.getSheetAt(i).getSheetName().equals(sheetName))
          sheet = workbook.getSheetAt(i);
      }

      Row headerRow = sheet.getRow(0);

      int rowNum = sheet.getLastRowNum();
      int colNum = headerRow.getLastCellNum();

      data = new String[rowNum][colNum];
      inHeaders = new String[colNum]; // create new String[] to store fields aka headers
      
      // if the file has a header row
      if(!createHeaderRow) {
	      Set<String> headersDupeCheck = new HashSet<String>();

	      for (int i = 0; i < colNum; ++i) {
	        inHeaders[i] = Common.checkIfDfHeader(headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString(), Arrays.copyOf(headersDupeCheck.toArray(), headersDupeCheck.toArray().length, String[].class), UserData.getAllDfHeaders());

	        if (!headersDupeCheck.add(inHeaders[i].toLowerCase()))
	        	throw new ApplicationException(String.format("Header Fields cannot hold duplicate values.\nThere are multiple columns with the value \"%s\"", inHeaders[i]));
	
	      }
      }
      
      // If the file has no header row
      if(createHeaderRow)
    	  for(int i = 0; i < colNum; ++i)
    		  inHeaders[i] = "F" + (i + 1);

      // used to determine if we need to skip the first row or not
      int rowOffset = (!createHeaderRow) ? 1 : 0;
      
      for (int i = 0; i < rowNum; ++i) {
        Row row = sheet.getRow(i + rowOffset);
        for (int j = 0; j < colNum; ++j) {
          Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
          cell = evaluator.evaluateInCell(cell);
          CellStyle cellStyle = cell.getCellStyle();
          String formatString = cellStyle.getDataFormatString();
          if (formatString.equals("m/d/yy"))
            cell.setCellStyle(styledate);

          String formattedValue = formatter.formatCellValue(cell);
          String value = formattedValue;
          data[i][j] = value;
        }
      }

      return new InputData(inHeaders, data);

    } catch (Exception e) {
      throw new ApplicationException(e.getMessage());
    } finally {
      if (workbook != null)
        workbook.close();
    }

  }

  
  // reads data without validating headers or structure
  public static List<List<String>> readXLSRaw(File file, String sheetName, boolean isEncrypted, String password) throws Exception {
    Workbook workbook = null;
    String[][] data = null;
    Sheet sheet = null;
    try {

      boolean fileIsLocked = !file.renameTo(file);
      if (fileIsLocked)
        throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

      // we create an Workbook object for our Excel File
      if(!isEncrypted)
          workbook = WorkbookFactory.create(file);
      else
    	  workbook = WorkbookFactory.create(file, password);

      CreationHelper createHelper = workbook.getCreationHelper();
      DataFormatter formatter = new DataFormatter();
      CellStyle styledate = workbook.createCellStyle();
      styledate.setDataFormat(createHelper.createDataFormat().getFormat("M/d/yyyy"));

      FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
      // we get first sheet
      int numOfSheets = workbook.getNumberOfSheets();

      for (int i = 0; i < numOfSheets; ++i) {
        if (workbook.getSheetAt(i).getSheetName().equals(sheetName))
          sheet = workbook.getSheetAt(i);
      }

      Row headerRow = sheet.getRow(0);

      int rowNum = sheet.getLastRowNum();
      int colNum = headerRow.getLastCellNum();

      data = new String[rowNum][colNum];

      for (int i = 0; i < rowNum; ++i) {
        Row row = sheet.getRow(i);
        for (int j = 0; j < colNum; ++j) {
          Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
          cell = evaluator.evaluateInCell(cell);
          CellStyle cellStyle = cell.getCellStyle();
          String formatString = cellStyle.getDataFormatString();
          if (formatString.equals("m/d/yy"))
            cell.setCellStyle(styledate);

          String formattedValue = formatter.formatCellValue(cell);
          String value = formattedValue;
          data[i][j] = value;
        }
      }

      List<List<String>> newData = new ArrayList<>();

      for (String[] line : data) {
        newData.add(Arrays.asList(line));
      }

      return newData;

    } catch (Exception e) {
      throw new ApplicationException(e.getMessage());
    } finally {
      if (workbook != null)
        workbook.close();
    }

  }

  /**
   * Reads and extracts headers and data from an Excel (2007-2010) .xlsx file by parsing out the underlying Excel XML file.
   * This allows for large amounts of data to be read without running out of memory
   * 
   * @param file
   *          the file to read
   * @param rId
   *          the relationshipID of the worksheet to read
   * @return the data in the file as InputData
   * @throws Exception
   */
  @SuppressWarnings("unused")
  public static InputData readXLSX(File file, String sheetName, boolean createHeaderRow, boolean isEncrypted, String password) throws Exception {

    InputData inputData = null;
    String[] inHeaders = null;
    String[][] data = null;

    final String ROW = "row";
    final String CELL = "c";
    final String CELL_TYPE = "t";
    final String CELL_TYPE_STRING = "s";
    
    System.out.println(String.format("Reading file %s", file.getName()));

    boolean fileIsLocked = !file.renameTo(file);
    if (fileIsLocked)
      throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

    InputStream dataStream = null;
    OPCPackage pkg = null;
    POIFSFileSystem filesystem = null;
    
    if (isEncrypted) {
    	filesystem = new POIFSFileSystem(file, true);
    	EncryptionInfo info = new EncryptionInfo(filesystem);
    	Decryptor d = Decryptor.getInstance(info);
    	
    	try {
    	    if (!d.verifyPassword(password)) {
    	        throw new RuntimeException("Unable to open encrypted document");
    	    }
    	    dataStream = d.getDataStream(filesystem);

    	} catch (GeneralSecurityException ex) {
    		filesystem.close();
    	    throw new RuntimeException("Unable to process encrypted document", ex);
    	}

    }

    /*
     * Opens a package (archive / xlsx file) with read / write permissions.
     * It is also possible to access it read only, which should be the first
     * choice for read operations in case the file is already accessed by
     * another user. To open read only provide an InputStream instead of a
     * file path.
     */
    if(!isEncrypted)
		pkg = OPCPackage.open(file.getPath());
	else
		pkg = OPCPackage.open(dataStream);
    
    XSSFReader r = new XSSFReader(pkg);

    /*
     * Read the sharedStrings.xml from an xlsx file into an object
     * representation.
     */
    SharedStringsTable sst = (r.getSharedStringsTable() != null) ? r.getSharedStringsTable() : new SharedStringsTable();
    StylesTable stylesTable = (r.getStylesTable() != null) ? r.getStylesTable() : new StylesTable();

    // SharedStringsTable sst = new SharedStringsTable();
    // StylesTable stylesTable = new StylesTable();
    /*
     * Hand a read SharedStringsTable for further reference to the SAXParser
     * and the underlying ContentHandler.
     */

    XMLReader parser = fetchSheetParser();

    /*
     * To look up the Sheet Name / Sheet Order / rID, you need to process
     * the core Workbook stream. Normally it's of the form rId# or rSheet#
     */

    InputStream sheet = null;
    InputSource sheetSource;
    Iterator<InputStream> sheets = r.getSheetsData();
    if (sheets instanceof XSSFReader.SheetIterator) {
      XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) sheets;
      while (sheetIterator.hasNext()) {
        sheet = sheetIterator.next();
        if (sheetIterator.getSheetName().equals(sheetName)) {
          break;
        }
      }
    }

    sheetSource = new InputSource(sheet);

    /*
     * Run through a Sheet using a window of several XML tags instead of
     * attempting to read the whole file into RAM at once. Leaves the
     * handling of file content to the ContentHandler, which is in this case
     * the nested class SheetHandler.
     */
    SheetHandler handler = new SheetHandler(sst, stylesTable, UserData.getAllDfHeaders(), createHeaderRow);
    parser.setContentHandler(handler);

    try {
      parser.parse(sheetSource);
      inHeaders = handler.getHeaders().toArray(new String[0]);
      data = new String[handler.getData().size()][handler.getData().get(0).length];
      
      // if the file has a header row
      if(!createHeaderRow) {
	      for (int i = 0; i < data.length; ++i) {
	        for (int j = 0; j < data[i].length; ++j) {
	          data[i][j] = handler.getData().get(i)[j];
	        }
	      }
      }
      
      //if the file doesn't have a header row
      if(createHeaderRow) {
    	  data = new String[handler.getData().size() + 1][handler.getData().get(0).length];
    	  
    	  for (int i = 0; i < data.length; ++i) {
    		  if(i == 0) {
    			  for (int j = 0; j < inHeaders.length; ++j)
    				  data[i][j] = inHeaders[j];
    		  } else {
	    		  for (int j = 0; j < data[i].length; ++j)
	    			  data[i][j] = handler.getData().get(i - 1)[j];
    		  }
  	      }
    	  
    	  for(int i = 0; i < inHeaders.length; ++i) {
    		  inHeaders[i] = "F" + (i + 1);
    	  }
      }

      inputData = new InputData(inHeaders, data);
      // userData.load(inHeaders, data);

    } catch (Exception err) {
      err.printStackTrace();
      throw new Exception(err.getMessage());

    } finally {
      pkg.close();
      sst.close();
      sheet.close();
      
      if(dataStream != null)
    	  dataStream.close();
      
      if(filesystem != null)
    	  filesystem.close();
    }

    return new InputData(inHeaders, data);

    /*
     * Close the underlying InputStream for a Sheet XML.
     */

  }

  public static XMLReader fetchSheetParser() throws SAXException {
    /*
     * XMLReader parser = XMLReaderFactory
     * .createXMLReader("org.apache.xerces.parsers.SAXParser");
     */

    XMLReader parser = XMLReaderFactory.createXMLReader();

    // SheetHandler handler = new SheetHandler(sst);
    // parser.setContentHandler(handler);
    return parser;
  }

  private static class SheetHandler extends DefaultHandler {
    enum xssfDataType {
      BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
    }

    private xssfDataType nextDataType;
    private SharedStringsTable sst;
    private StylesTable stylesTable;
    private String lastContents;
    private boolean nextIsString;
    private int row = -1;
    private String[] tempRow;
    private int fieldCounter = 0;
    private ArrayList<String[]> data = new ArrayList<String[]>();
    private ArrayList<String> headers = new ArrayList<String>();
    private Set<String> headerDupeCheck = new HashSet<String>();
    private boolean hasError = false;
    private String[] allDfHeaders;
    private int maxCol;
    private int formatIndex;
    private String formatString;
    boolean inlineStr;
    private final DataFormatter formatter;
    
    boolean createHeaderRow;

    private SheetHandler(SharedStringsTable sst, StylesTable stylesTable, String[] allDfHeaders, boolean createHeaderRow) {
      this.sst = sst;
      this.allDfHeaders = allDfHeaders;
      this.stylesTable = stylesTable;

      this.formatter = new DataFormatter(Locale.UK);
      this.createHeaderRow = createHeaderRow;
    }

    public void setMaxCol(int maxCol) {
      this.maxCol = maxCol;
    }

    public ArrayList<String[]> getData() {
      return data;
    }

    public ArrayList<String> getHeaders() {
      return headers;
    }

    @SuppressWarnings("unused")
    public boolean getHasError() {
      return hasError;
    }

    @SuppressWarnings("unused")
	@Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

    	name = name.replaceFirst("x:", "");
    	
      // Im sure theres a better way to figure out the max columns but this shit has been plaguing me forever and this works
      if (name.equals("dimension")) {
        String colRange[] = attributes.getValue("ref").split(":");
        String maxColRange = colRange[colRange.length - 1];
        int maxCol = Integer.valueOf(Common.titleToNumber(maxColRange.replaceAll("\\d*", "")));
        setMaxCol(maxCol);
      }
      


      if (name.equals("row")) {
        row++;
    	  //System.out.println(row);
        if (row > 0) {
          tempRow = new String[headers.size()];
          fieldCounter = 0;
        }

      }

      // c => cell
      if (name.equals("c")) {

        // Eloquently handle missing columns

        // Get the col ref found in XML
        String toColRefString = attributes.getValue("r");

        int toColNum;
        
        // strip the row number and convert the column letters to a number value
        if(toColRefString != null)
        	toColNum = Common.titleToNumber(toColRefString.replaceAll("[^\\D]", ""));
        else
        	toColNum = Common.titleToNumber(Common.toAlphabetic(fieldCounter));

        // If the column WE SHOULD be on is smaller than the col ref found in XML
        if (Common.titleToNumber(Common.toAlphabetic(fieldCounter)) < toColNum) {
          // for (int x = 0; x < toColNum - Common.titleToNumber(Common.toAlphabetic(fieldCounter)); ++x)
          while (Common.titleToNumber(Common.toAlphabetic(fieldCounter)) < toColNum) {
            // System.out.println(Common.titleToNumber(Common.toAlphabetic(fieldCounter)) + " vs " + toColNum +"\n" + "setting index of " +
            // fieldCounter + " to Blank");
            if (row > 0) {
              tempRow[fieldCounter++] = ""; // for the difference, increment our fields with "" to match XML col ref
            } else {
              headers.add("");
              
              if(!createHeaderRow)
            	  if (!headerDupeCheck.add(""))
            		  throw new SAXException(String.format("Header Fields cannot hold duplicate values."));

              fieldCounter++;
              System.out.println("");
            }
          }
        }

        inlineStr = false;

        // System.out.print(attributes.getValue("r") + " - ");
        // Figure out if the value is an index in the SST
        String cellType = attributes.getValue("t");

        // figure out the type of cell
        this.nextDataType = xssfDataType.NUMBER;
        this.formatIndex = -1;
        this.formatString = null;
        String cellStyleStr = attributes.getValue("s");
        
        if ("b".equals(cellType))
          nextDataType = xssfDataType.BOOL;
        else if ("e".equals(cellType))
          nextDataType = xssfDataType.ERROR;
        else if ("inlineStr".equals(cellType))
          nextDataType = xssfDataType.INLINESTR;
        else if ("s".equals(cellType))
          nextDataType = xssfDataType.SSTINDEX;
        else if ("str".equals(cellType))
          nextDataType = xssfDataType.FORMULA;
        else if (cellStyleStr != null) {
          // It's a number, but almost certainly one
          // with a special style or format
          XSSFCellStyle style = null;

          if (cellStyleStr != null) {
            int styleIndex = Integer.parseInt(cellStyleStr);
            style = stylesTable.getStyleAt(styleIndex);
          } else if (stylesTable.getNumCellStyles() > 0) {
            style = stylesTable.getStyleAt(0);
          }

          if (style != null) {
            this.formatIndex = style.getDataFormat();
            this.formatString = style.getDataFormatString();

            
            if ("m/d/yy" == formatString) {
              formatString = "M/d/yyyy";
            }

            if (this.formatString == null)
              this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
            
            
          }
        }

        if (cellType != null && cellType.equals("s")) {
          nextIsString = true;
        } else if (cellType != null && cellType.equals("inlineStr")) {
          inlineStr = true;
        } else {
          nextIsString = false;
        }
      }

      
      
      // Clear contents cache
      lastContents = "";

    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

    	name = name.replaceFirst("x:", "");
    	
      // Process the last contents as required.
      // Do now, as characters() may be called more than once
      if (nextIsString) {
        int idx = Integer.parseInt(lastContents);
        lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();

        nextIsString = false;
      }

      // v => contents of a cell
      // Output after we've seen the string contents
      if (name.equals("v")) {

        // Format the cell, we only really care about numbers or booleans since they show as 0 or 1
        switch (nextDataType) {
        case BOOL:
          char first = lastContents.charAt(0);
          if (first == '0') {
            lastContents = "FALSE";
          } else {
            lastContents = "TRUE";
          }
          break;
        case NUMBER:
          String n = lastContents;
          if (this.formatString != null)
            lastContents = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex, this.formatString);
          break;
        default:
          break;
        }

        if (row == 0) { // if we're on the header row
          
          String headerValue = lastContents;
            
          if(!createHeaderRow) {
	          headerValue = Common.checkIfDfHeader(lastContents, Arrays.copyOf(headerDupeCheck.toArray(), headerDupeCheck.toArray().length, String[].class), allDfHeaders); // get the new value after check if header is dupe
	          
	          if (!headerDupeCheck.add(headerValue.toLowerCase()))
	            throw new SAXException(String.format("Header Fields cannot hold duplicate values.\nThere are multiple columns with the value \"%s\"", headerValue));
          }
          //throw new ApplicationException(String.format("Header Fields cannot hold duplicate values.\nThere are multiple columns with the value \"%s\"", inHeaders[i]));
          headers.add(headerValue); // finally add the header value
          fieldCounter++;

          System.out.println(headerValue);
        } else {
          tempRow[fieldCounter++] = lastContents;
          if (fieldCounter == headers.size()) {
            data.add(tempRow);
          }
        }
      }

      if (name.equals("t") && inlineStr) {
        if (row == 0) { // if we're on the header row
        	
          String headerValue = lastContents;
          
          if(!createHeaderRow) {
	          headerValue = Common.checkIfDfHeader(lastContents, Arrays.copyOf(headerDupeCheck.toArray(), headerDupeCheck.toArray().length, String[].class), allDfHeaders); // get the new value after check if header is dupe
	          
	          if (!headerDupeCheck.add(headerValue.toLowerCase()))
	        	  throw new SAXException(String.format("Header Fields cannot hold duplicate values.\nThere are multiple columns with the value \"%s\"", headerValue));
          }
          
          headers.add(headerValue); // finally add the header value
          fieldCounter++;
          
        } else {
          tempRow[fieldCounter++] = lastContents;
          if (fieldCounter == headers.size()) {
            data.add(tempRow);
          }
        }
      }

      // If we've reached the end of the row and we havent collected the expected amount of fields
      if ((name.equals("row")) && fieldCounter != headers.size()) {
        if (row > 0) {
          while (fieldCounter < headers.size()) {
            tempRow[fieldCounter++] = "";
          }
          data.add(tempRow);
        }
      }

      // Make sure our header row is the length of the max columns
      if (name.equals("row") && row == 0) {
        String blankHeaderValue = "";
        while (headers.size() < maxCol) {
          headers.add(blankHeaderValue);
          if (!headerDupeCheck.add(blankHeaderValue))
            throw new SAXException(String.format("Header Fields cannot hold duplicate values."));

          fieldCounter++;
          System.out.println(blankHeaderValue);
        }

      }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      lastContents += new String(ch, start, length);
    }

  }

  /**
   * Reads and extracts headers and data from an Excel (2007-2010) .xlsx file by parsing out the underlying Excel XML file.
   * This allows for large amounts of data to be read without running out of memory
   * 
   * @param file
   *          the file to read
   * @param rId
   *          the relationshipID of the worksheet to read
   * @return the data in the file as InputData
   * @throws Exception
   */
  @SuppressWarnings("unused")
  public static List<List<String>> readXLSXRaw(File file, String sheetName, boolean isEncrypted, String password) throws Exception {

    String[] inHeaders = null;
    String[][] data = null;

    final String ROW = "row";
    final String CELL = "c";
    final String CELL_TYPE = "t";
    final String CELL_TYPE_STRING = "s";

    boolean fileIsLocked = !file.renameTo(file);
    if (fileIsLocked)
      throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

    InputStream dataStream = null;
    OPCPackage pkg = null;
    POIFSFileSystem filesystem = null;
    
    if (isEncrypted) {
    	filesystem = new POIFSFileSystem(file, true);
    	EncryptionInfo info = new EncryptionInfo(filesystem);
    	Decryptor d = Decryptor.getInstance(info);
    	
    	try {
    	    if (!d.verifyPassword(password)) {
    	        throw new RuntimeException("Unable to open encrypted document");
    	    }
    	    dataStream = d.getDataStream(filesystem);

    	} catch (GeneralSecurityException ex) {
    		filesystem.close();
    	    throw new RuntimeException("Unable to process encrypted document", ex);
    	}

    }

    /*
     * Opens a package (archive / xlsx file) with read / write permissions.
     * It is also possible to access it read only, which should be the first
     * choice for read operations in case the file is already accessed by
     * another user. To open read only provide an InputStream instead of a
     * file path.
     */
    if(!isEncrypted)
		pkg = OPCPackage.open(file.getPath());
	else
		pkg = OPCPackage.open(dataStream);
    
    XSSFReader r = new XSSFReader(pkg);

    /*
     * Read the sharedStrings.xml from an xlsx file into an object
     * representation.
     */
    SharedStringsTable sst = (r.getSharedStringsTable() != null) ? r.getSharedStringsTable() : new SharedStringsTable();
    StylesTable stylesTable = (r.getStylesTable() != null) ? r.getStylesTable() : new StylesTable();

    // SharedStringsTable sst = new SharedStringsTable();
    // StylesTable stylesTable = new StylesTable();
    /*
     * Hand a read SharedStringsTable for further reference to the SAXParser
     * and the underlying ContentHandler.
     */

    XMLReader parser = fetchSheetParser();

    /*
     * To look up the Sheet Name / Sheet Order / rID, you need to process
     * the core Workbook stream. Normally it's of the form rId# or rSheet#
     */

    InputStream sheet = null;
    InputSource sheetSource;
    Iterator<InputStream> sheets = r.getSheetsData();
    if (sheets instanceof XSSFReader.SheetIterator) {
      XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) sheets;
      while (sheetIterator.hasNext()) {
        sheet = sheetIterator.next();
        if (sheetIterator.getSheetName().equals(sheetName)) {
          break;
        }
      }
    }

    sheetSource = new InputSource(sheet);

    /*
     * Run through a Sheet using a window of several XML tags instead of
     * attempting to read the whole file into RAM at once. Leaves the
     * handling of file content to the ContentHandler, which is in this case
     * the nested class SheetHandler.
     */
    SheetHandlerRaw handler = new SheetHandlerRaw(sst, stylesTable);
    parser.setContentHandler(handler);

    try {
      parser.parse(sheetSource);
      inHeaders = handler.getHeaders().toArray(new String[0]);
      data = new String[handler.getData().size()][handler.getData().get(0).length];

      /*
       * for (String[] line : handler.getData()) {
       * System.out.println(Arrays.asList(line));
       * }
       */

      for (int i = 0; i < data.length; ++i) {
        for (int j = 0; j < data[i].length; ++j) {
          data[i][j] = handler.getData().get(i)[j];
        }
      }

      // inputData = new InputData(inHeaders, data);
      // userData.load(inHeaders, data);

    } catch (Exception err) {
      err.printStackTrace();
      throw new Exception(err.getMessage());

    } finally {
      pkg.close();
      sst.close();
      sheet.close();
      
      if(dataStream != null)
    	  dataStream.close();
      
      if(filesystem != null)
    	  filesystem.close();
    }

    List<List<String>> newData = new ArrayList<>();
    newData.add(Arrays.asList(inHeaders));

    for (String[] line : data) {
      newData.add(Arrays.asList(line));
    }

    return newData;

    /*
     * Close the underlying InputStream for a Sheet XML.
     */

  }

  private static class SheetHandlerRaw extends DefaultHandler {
    enum xssfDataType {
      BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
    }

    private xssfDataType nextDataType;
    private SharedStringsTable sst;
    private StylesTable stylesTable;
    private String lastContents;
    private boolean nextIsString;
    private int row = -1;
    private String[] tempRow;
    private int fieldCounter = 0;
    private ArrayList<String[]> data = new ArrayList<String[]>();
    private ArrayList<String> headers = new ArrayList<String>();

    private boolean hasError = false;

    private int maxCol;
    private int formatIndex;
    private String formatString;
    boolean inlineStr;
    private final DataFormatter formatter;

    private SheetHandlerRaw(SharedStringsTable sst, StylesTable stylesTable) {
      this.sst = sst;

      this.stylesTable = stylesTable;

      this.formatter = new DataFormatter(Locale.UK);
    }

    public void setMaxCol(int maxCol) {
      this.maxCol = maxCol;
    }

    public ArrayList<String[]> getData() {
      return data;
    }

    public ArrayList<String> getHeaders() {
      return headers;
    }

    @SuppressWarnings("unused")
    public boolean getHasError() {
      return hasError;
    }

    @SuppressWarnings("unused")
	@Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

    	name = name.replaceFirst("x:", "");
    	
      // Im sure theres a better way to figure out the max columns but this shit has been plaguing me forever and this works
      if (name.equals("dimension")) {
        String colRange[] = attributes.getValue("ref").split(":");
        String maxColRange = colRange[colRange.length - 1];
        int maxCol = Integer.valueOf(Common.titleToNumber(maxColRange.replaceAll("\\d*", "")));
        setMaxCol(maxCol);
      }

      if (name.equals("row")) {
        row++;

        if (row > 0) {
          tempRow = new String[headers.size()];
          fieldCounter = 0;
        }

      }

      // c => cell
      if (name.equals("c")) {

        // Eloquently handle missing columns

        // Get the col ref found in XML
        String toColRefString = attributes.getValue("r");

        int toColNum;
        
        // strip the row number and convert the column letters to a number value
        if(toColRefString != null)
        	toColNum = Common.titleToNumber(toColRefString.replaceAll("[^\\D]", ""));
        else
        	toColNum = Common.titleToNumber(Common.toAlphabetic(fieldCounter));

        // If the column WE SHOULD be on is smaller than the col ref found in XML
        if (Common.titleToNumber(Common.toAlphabetic(fieldCounter)) < toColNum) {
          // for (int x = 0; x < toColNum - Common.titleToNumber(Common.toAlphabetic(fieldCounter)); ++x)
          while (Common.titleToNumber(Common.toAlphabetic(fieldCounter)) < toColNum) {
            // System.out.println(Common.titleToNumber(Common.toAlphabetic(fieldCounter)) + " vs " + toColNum +"\n" + "setting index of " +
            // fieldCounter + " to Blank");
            if (row > 0) {
              tempRow[fieldCounter++] = ""; // for the difference, increment our fields with "" to match XML col ref
            } else {
              headers.add("");

              fieldCounter++;
              System.out.println("");
            }
          }
        }

        inlineStr = false;

        // System.out.print(attributes.getValue("r") + " - ");
        // Figure out if the value is an index in the SST
        String cellType = attributes.getValue("t");

        // figure out the type of cell
        this.nextDataType = xssfDataType.NUMBER;
        this.formatIndex = -1;
        this.formatString = null;
        String cellStyleStr = attributes.getValue("s");

        if ("b".equals(cellType))
          nextDataType = xssfDataType.BOOL;
        else if ("e".equals(cellType))
          nextDataType = xssfDataType.ERROR;
        else if ("inlineStr".equals(cellType))
          nextDataType = xssfDataType.INLINESTR;
        else if ("s".equals(cellType))
          nextDataType = xssfDataType.SSTINDEX;
        else if ("str".equals(cellType))
          nextDataType = xssfDataType.FORMULA;
        else if (cellStyleStr != null) {
          // It's a number, but almost certainly one
          // with a special style or format
          XSSFCellStyle style = null;

          if (cellStyleStr != null) {
            int styleIndex = Integer.parseInt(cellStyleStr);
            style = stylesTable.getStyleAt(styleIndex);
          } else if (stylesTable.getNumCellStyles() > 0) {
            style = stylesTable.getStyleAt(0);
          }

          if (style != null) {
            this.formatIndex = style.getDataFormat();
            this.formatString = style.getDataFormatString();

            if ("m/d/yy" == formatString) {
              formatString = "M/d/yyyy";
            }

            if (this.formatString == null)
              this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
          }
        }

        if (cellType != null && cellType.equals("s")) {
          nextIsString = true;
        } else if (cellType != null && cellType.equals("inlineStr")) {
          inlineStr = true;
        } else {
          nextIsString = false;
        }
      }

      // Clear contents cache
      lastContents = "";

    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

    	name = name.replaceFirst("x:", "");
    	
      // Process the last contents as required.
      // Do now, as characters() may be called more than once
      if (nextIsString) {
        int idx = Integer.parseInt(lastContents);
        lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
        nextIsString = false;
      }

      // v => contents of a cell
      // Output after we've seen the string contents
      if (name.equals("v")) {

        // Format the cell, we only really care about numbers or booleans since they show as 0 or 1
        switch (nextDataType) {
        case BOOL:
          char first = lastContents.charAt(0);
          if (first == '0') {
            lastContents = "FALSE";
          } else {
            lastContents = "TRUE";
          }
          break;
        case NUMBER:
          String n = lastContents;
          if (this.formatString != null)
            lastContents = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex, this.formatString);
          break;
        default:
          break;
        }

        if (row == 0) { // if we're on the header row
          headers.add(lastContents); // finally add the header value
          fieldCounter++;
        } else {
          tempRow[fieldCounter++] = lastContents;
          if (fieldCounter == headers.size()) {
            data.add(tempRow);
          }
        }
      }

      if (name.equals("t") && inlineStr) {
        if (row == 0) { // if we're on the header row
          headers.add(lastContents); // finally add the header value
          fieldCounter++;
        } else {
          tempRow[fieldCounter++] = lastContents;
          if (fieldCounter == headers.size()) {
            data.add(tempRow);
          }
        }
      }

      // If we've reached the end of the row and we havent collected the expected amount of fields
      if (name.equals("row") && fieldCounter != headers.size()) {
        if (row > 0) {
          while (fieldCounter < headers.size()) {
            tempRow[fieldCounter++] = "";
          }
          data.add(tempRow);
        }
      }

      // Make sure our header row is the length of the max columns
      if (name.equals("row") && row == 0) {
        String blankHeaderValue = "";
        while (headers.size() < maxCol) {
          headers.add(blankHeaderValue);
          fieldCounter++;
          System.out.println(blankHeaderValue);
        }

      }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      lastContents += new String(ch, start, length);
    }

  }

  /**
   * Reads and returns a String[] of table names found in a Microsoft Access database file
   * 
   * @param file
   *          the database file to open
   * @return table names as a String[]
   * @throws ApplicationException
   *           throws an Exception in case something goes wrong
   */
  public static String[] readXLSXSheetNames(File file, boolean isEncrypted, String password) throws Exception {
    boolean fileIsLocked = !file.renameTo(file);
    if (fileIsLocked)
      throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

    InputStream dataStream = null;
    POIFSFileSystem filesystem = null;
    
    if (isEncrypted) {
    	filesystem = new POIFSFileSystem(file, true);
    	EncryptionInfo info = new EncryptionInfo(filesystem);
    	Decryptor d = Decryptor.getInstance(info);
    	
    	try {
    	    if (!d.verifyPassword(password)) {
    	        throw new RuntimeException("Unable to open encrypted document");
    	    }
    	    dataStream = d.getDataStream(filesystem);

    	} catch (GeneralSecurityException ex) {
    		filesystem.close();
    	    throw new RuntimeException("Unable to process encrypted document", ex);
    	}

    }

    OPCPackage pkg = null;
    XSSFReader r = null;
    try {
      /*
       * Opens a package (archive / xlsx file) with read / write permissions.
       * It is also possible to access it read only, which should be the first
       * choice for read operations in case the file is already accessed by
       * another user. To open read only provide an InputStream instead of a
       * file path.
       */
    	
    	if(!isEncrypted)
    		pkg = OPCPackage.open(file.getPath());
    	else
    		pkg = OPCPackage.open(dataStream);
      
      r = new XSSFReader(pkg);

      /*
       * To look up the Sheet Names
       */
      ArrayList<String> sheetNames = new ArrayList<>();
      Iterator<InputStream> sheets = r.getSheetsData();
      if (sheets instanceof XSSFReader.SheetIterator) {
        XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) sheets;
        while (sheetIterator.hasNext()) {
          try (InputStream sheet = sheetIterator.next();) {
            sheetNames.add(sheetIterator.getSheetName());
          }
        }
      }
      String[] databaseNames = new String[sheetNames.size()];
      return sheetNames.toArray(databaseNames);

    } catch (Exception e) {
      throw new Exception(e.getMessage());
    } finally {
      if (pkg != null)
        pkg.close();
      
      if(dataStream != null)
    	  dataStream.close();
      
      if(filesystem != null)
    	  filesystem.close();
    }

  }

  public static String[] readXLSSheetNames(File file, boolean isEncrypted, String password) throws Exception {
    Workbook workbook = null;
    String[] sheetNames = null;
    try {

     // boolean fileIsLocked = !file.renameTo(file);

     // if (fileIsLocked)
        //throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

      // we create an Workbook object for our Excel File
      if(!isEncrypted)
          workbook = WorkbookFactory.create(file);
      else
    	  workbook = WorkbookFactory.create(file, password);
    	  

      ArrayList<String> sheetNamesArray = new ArrayList<>();
      int numOfSheets = workbook.getNumberOfSheets();

      for (int i = 0; i < numOfSheets; ++i) {
        sheetNamesArray.add(workbook.getSheetAt(i).getSheetName());
      }

      sheetNames = new String[sheetNamesArray.size()];
      sheetNamesArray.toArray(sheetNames);
      return sheetNames;
    } catch (Exception e) {
      throw new Exception(e);
    } finally {
      if (workbook != null)
        workbook.close();
    }
  }

  public static boolean isXLSXEncrypted(String path) throws IOException {
    POIFSFileSystem poifs = null;
    try {
      try {
        poifs = new POIFSFileSystem(new FileInputStream(path));
      } catch (IOException ex) {

      }
      if (poifs != null)
          poifs.close();
      
      System.out.println("protected");
      return true;
    } catch (OfficeXmlFileException e) {
      System.out.println("not protected");
      if (poifs != null)
          poifs.close();
      
      return false;
    } finally {
      if (poifs != null)
        poifs.close();
    }
  }
  
  public static boolean isXLSEncrypted(String path) {

		try {
			@SuppressWarnings("unused")
			Workbook workbook = WorkbookFactory.create(new File(path));
		} catch (EncryptedDocumentException e) {
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		  
			return false;
  }
  
 
  
}
