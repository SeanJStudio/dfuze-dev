/**
 * Project: Dfuze
 * File: HMADataDumpReport.java
 * Date: Aug 7, 2020
 * Time: 3:08:40 AM
 */
package com.mom.dfuze.io;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.TreeMap;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.ExcelCell;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.ui.UiController;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class DataDumpReport {
  public static final String DATA_DUMP_REPORT_FILENAME = "dfuze_data_dump_report.xlsx";
  private static final String STRIP_IN_REGEX = "(?i)([I][N]__)+";
  
  public static void writeDataDumpReport(UserData userData) {
	  
	  // randomize the records
	  List<Record> randomizedRecordList = new ArrayList<Record>(userData.getRecordList());
	  Collections.shuffle(randomizedRecordList);
	  
	  // strip 'IN__' from the data headers
	  String[] inFieldHeaders = userData.getInHeaders();
	  HashSet<String> uniqueHeaders = new HashSet<>();
	  for(int i = inFieldHeaders.length - 1; i >= 0; --i)
		  if(!uniqueHeaders.add(inFieldHeaders[i].replaceAll(STRIP_IN_REGEX, "")))
			  uniqueHeaders.add(inFieldHeaders[i]);
		  else
			  inFieldHeaders[i] = inFieldHeaders[i].replaceAll(STRIP_IN_REGEX, "");
	  
	  // setup the report
	  List<List<ExcelCell>> report = new ArrayList<>();
	  
	  // setup the styling
	  XSSFCellStyle defaultStyle = ExcelCell.createCellStyle();
	  XSSFCellStyle headerStyle = ExcelCell.createCellStyle();
	  XSSFCellStyle rowEvenStyle = ExcelCell.createCellStyle();
	  
	  Font defaultFont = new XSSFFont();
	  Font headerFont = new XSSFFont();
	  headerFont.setBold(true);
	  headerFont.setColor(IndexedColors.WHITE.index);
	  
	  headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	  headerStyle.setFont(headerFont);
	  headerStyle.setFillForegroundColor(IndexedColors.BLACK.index);
	  
	  rowEvenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	  
	  @SuppressWarnings("deprecation")
	  XSSFColor rowColor = new XSSFColor(new java.awt.Color(220,220,220));
	  rowEvenStyle.setFillForegroundColor(rowColor);
	  
	  boolean isFormula = false;
	  
	  ArrayList<ExcelCell> rowBlank = new ArrayList<>();
	  ExcelCell cellBlank = new ExcelCell("", CellType.STRING, defaultStyle, defaultFont, isFormula);
	  rowBlank.add(cellBlank);
	  
	  // set the first element of the array to the number of data records; this will be the first cell.
	  String sumOfRecords = String.format("%d records", userData.getRecordList().size());
	  
	  // write the number of records to the report
	  ArrayList<ExcelCell> rowNumOfRecords = new ArrayList<>();
	  ExcelCell cellNumOfRecords = new ExcelCell(sumOfRecords, CellType.STRING, defaultStyle, defaultFont, isFormula);
	  rowNumOfRecords.add(cellNumOfRecords);
	  report.add(rowNumOfRecords);

	  // prepare the records to write to the report
	  ArrayList<Record> randomRecordSelection = new ArrayList<>();
	  int MAX_SEGMENTS = 3;

	  for (int i = 0; i < randomizedRecordList.size(); ++i) {
		  Record randomRecord = randomizedRecordList.get(i);
		  long numOfSeg = randomRecordSelection.stream().map(Record::getSeg).filter(randomRecord.getSeg()::equals).count();
		  if(numOfSeg < MAX_SEGMENTS) {
			  randomRecordSelection.add(randomizedRecordList.get(i));
		  }
	  }

	  // Sort the randomRecords to look nice
	  Collections.sort(randomRecordSelection, new RecordSorters.CompareBySeg());

	  // Initialize the blankFieldMap to hold the inFieldHeaderArrayIndex as the key and the number of blanks as the value
	  Map<Integer, Integer> blankFieldMap = new HashMap<>();

	  for (int i = 0; i < inFieldHeaders.length; ++i)
		  blankFieldMap.put(i, 0);

	  // Populate the blankFieldMap from the input data
	  for (int i = 0; i < userData.getRecordList().size(); ++i) {

		  Record record = userData.getRecordList().get(i);
		  for (int j = 0; j < inFieldHeaders.length; ++j) {
			  if (record.getDfInData()[j].trim().isEmpty()) {
				  int numOfOccurances = blankFieldMap.get(j);
				  blankFieldMap.put(j, ++numOfOccurances);
			  }
		  }

	  }

	  // Initialize the segmentMap to hold the segment as the key and the number of occurrences as the value
	  Map<String, Integer> segmentMap = new HashMap<>();

	  // Populate the segmentMap from the input data
	  for (int i = 0; i < userData.getRecordList().size(); ++i) {
		  String segment = userData.getRecordList().get(i).getSeg().trim();
		  
		  if (segmentMap.containsKey(segment)) {
			  int numOfOccurances = segmentMap.get(segment);
			  segmentMap.put(segment, ++numOfOccurances);
		  } else {
			  segmentMap.put(segment, 1);
		  }
	  }
	  
	  // add a blank row to the report
	  report.add(rowBlank);
	  
	  // write the data fields as a row to the report
	  ArrayList<ExcelCell> rowDataFields = new ArrayList<>();

	  for (int i = 0; i < inFieldHeaders.length; ++i) {
		  ExcelCell cellDataField = new ExcelCell(inFieldHeaders[i], CellType.STRING, headerStyle, headerFont, isFormula);
		  rowDataFields.add(cellDataField);
	  }
	  report.add(rowDataFields);


	  // Add the data from the random record selection
	  for (int i = 0; i < randomRecordSelection.size(); ++i) {
		  
		  ArrayList<ExcelCell> rowDataRecord = new ArrayList<>();
		  
		  for (int j = 0; j < inFieldHeaders.length; ++j) {
			  String recordDataFieldValue = randomRecordSelection.get(i).getDfInData()[j];
			  ExcelCell cellDataField = new ExcelCell(recordDataFieldValue, CellType.STRING, defaultStyle, defaultFont,  isFormula);
			  rowDataRecord.add(cellDataField);
		  }
		  
		  XSSFCellStyle styleToUse = null;
		  if((i % 2) == 0)
			  styleToUse = defaultStyle;
		  else
			  styleToUse = rowEvenStyle;
		  
		  for(ExcelCell cell : rowDataRecord)
			  cell.setCellStyle(styleToUse);
		  
		  report.add(rowDataRecord);
	  }

	  // add blank rows to the report
	  report.add(rowBlank);
	  report.add(rowBlank);
	  report.add(rowBlank);
	  
	  // add the blanks and segments headers
	  String[] blanksAndSegmentsHeader = { "EMPTY FIELDS", "QTY", "","SEGMENTS", "QTY" };
	  ArrayList<ExcelCell> rowBlanksAndSegmentsHeader = new ArrayList<>();
	  
	  for (int i = 0; i < blanksAndSegmentsHeader.length; ++i) {
		  String fieldName = blanksAndSegmentsHeader[i];
		  XSSFCellStyle styleToUse = null;
		  Font fontToUse = null;
		  boolean isHeader = !fieldName.equals("");
		  
		  if (isHeader) {
			  styleToUse = headerStyle;
			  fontToUse = headerFont;
		  }else {
			  styleToUse = defaultStyle;
			  fontToUse = defaultFont;
		  }

		  ExcelCell cellBlanksAndSegments = new ExcelCell(fieldName, CellType.STRING, styleToUse, fontToUse, isFormula);
		  rowBlanksAndSegmentsHeader.add(cellBlanksAndSegments);
	  }
	  report.add(rowBlanksAndSegmentsHeader);
	  
	  // setup and write the blanks and segment counts
	  ArrayList<ArrayList<ExcelCell>> listOfBlanksAndSegmentsRows = new ArrayList<>();
	  
	  int blankFieldMapCounter = 0;
	  for(Entry<Integer, Integer> entry : blankFieldMap.entrySet()) {
		  ArrayList<ExcelCell> rowBlanksAndSegmentsValue = new ArrayList<>();
		  
		  XSSFCellStyle styleToUse = null;
		  if((blankFieldMapCounter++ % 2) == 0)
			  styleToUse = defaultStyle;
		  else
			  styleToUse = rowEvenStyle;
		  
		  String blankDataFieldName = inFieldHeaders[entry.getKey()];
		  ExcelCell cellblankDataFieldName = new ExcelCell(blankDataFieldName, CellType.STRING, styleToUse, defaultFont, isFormula);
		  rowBlanksAndSegmentsValue.add(cellblankDataFieldName);
		  
		  String blankDataFieldCount = String.valueOf(entry.getValue());
		  ExcelCell cellblankDataFieldCount = new ExcelCell(blankDataFieldCount, CellType.NUMERIC, styleToUse, defaultFont, isFormula);
		  rowBlanksAndSegmentsValue.add(cellblankDataFieldCount);
		  rowBlanksAndSegmentsValue.add(cellBlank);
		  
		  listOfBlanksAndSegmentsRows.add(rowBlanksAndSegmentsValue);
	  }

	  Map<String, Integer> sortedMap = new TreeMap<String, Integer>(segmentMap);
	  boolean isAddRow = (listOfBlanksAndSegmentsRows.size() > sortedMap.entrySet().size()) ? false : true;
	  int sumOfSegments = 0, blanksAndSegmentsRowIndex = 0;

	  int sortedMapCounter = 0;
	  for(Entry<String, Integer> entry : sortedMap.entrySet()) {
		  sumOfSegments += entry.getValue();
		  
		  XSSFCellStyle styleToUse = null;
		  if((sortedMapCounter++ % 2) == 0)
			  styleToUse = defaultStyle;
		  else
			  styleToUse = rowEvenStyle;
		  
		  ArrayList<ExcelCell> rowSegmentsValue = (isAddRow) ? new ArrayList<>() : listOfBlanksAndSegmentsRows.get(blanksAndSegmentsRowIndex++);

		  rowSegmentsValue.add( new ExcelCell(entry.getKey(), CellType.STRING, styleToUse, defaultFont, isFormula) );
		  rowSegmentsValue.add( new ExcelCell(String.valueOf(entry.getValue()), CellType.NUMERIC, styleToUse, defaultFont, isFormula) );
		  
		  if(isAddRow)
			  listOfBlanksAndSegmentsRows.add(rowSegmentsValue);
	  }
	  
	  
	  ArrayList<ExcelCell> rowSegmentSum = new ArrayList<>();
	  if(isAddRow) {
		  rowSegmentSum.add(cellBlank);
		  rowSegmentSum.add(cellBlank);
		  rowSegmentSum.add(cellBlank);
		  rowSegmentSum.add(cellBlank);
		  rowSegmentSum.add( new ExcelCell("Total: " + String.valueOf(sumOfSegments), CellType.STRING, defaultStyle, defaultFont, isFormula) );
	  } else {
		 ArrayList<ExcelCell> row = listOfBlanksAndSegmentsRows.get(sortedMap.entrySet().size());
		 row.add(cellBlank);
		 row.add( new ExcelCell("Total: " + String.valueOf(sumOfSegments), CellType.STRING, defaultStyle, defaultFont, isFormula) );
	  }
	  
	  for(ArrayList<ExcelCell> row : listOfBlanksAndSegmentsRows)
		  report.add(row);
	  
	  if(isAddRow)
		  report.add(rowSegmentSum);
	  
	  File file = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_" + DATA_DUMP_REPORT_FILENAME);

	  if (file.exists() && file.isFile()) {
		  int buttonPressed = JOptionPane.showConfirmDialog(UiController.getMainFrame(), "Data dump file already exists, would you like to overwrite it?",
				  "File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

		  if (buttonPressed != JOptionPane.YES_OPTION) {
			  DateTimeFormatter idtf = DateTimeFormatter.ofPattern("MMddyyyyHHmmss");
			  LocalDateTime now = LocalDateTime.now();
			  file = new File(UserPrefs.getLastUsedFolder() + "\\" + file.getName().substring(0, file.getName().lastIndexOf('.')) + idtf.format(now) + ".xlsx");
		  }
	  }


	  // Write the data to the file
	  try {
		  XLSXWriter.writeReportRaw(file, report);
	  } catch (ApplicationException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }

  }

}
