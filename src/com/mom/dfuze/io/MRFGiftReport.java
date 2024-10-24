package com.mom.dfuze.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.ExcelCell;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.ui.UiController;

public class MRFGiftReport {
	  public static final String GIFT_REPORT_FILENAME = "dfuze_gift_report.xlsx";

	  /**
	   * This method assumes the segmentPlanList parameter is unsorted
	   * This method assumes the recordList parameter has been segmented
	   * 
	   * @param segmentPlanList
	   * @param recordList
	   */
	  public static void writeMRFGiftReport(List<Record> recordList) {

	    final String[] GIFT_REPORT_HEADER = new String[] { 
	            UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
	            UserData.fieldName.LAST_DONATION_DATE.getName(),
	            UserData.fieldName.DONATION1_AMOUNT.getName(),
	            UserData.fieldName.DONATION2_AMOUNT.getName(),
	            UserData.fieldName.DONATION3_AMOUNT.getName(),
	            UserData.fieldName.OPEN_DONATION_AMOUNT.getName(),
	            UserData.fieldName.SEGMENT_CODE.getName() };

	    Map<String, Record> uniqueGivingBySegementMap = new HashMap<>();

	    for (int i = 0; i < recordList.size(); ++i) {
	      Record record = recordList.get(i);
	      String compositeGivingKey = record.getLstDnAmt() + record.getSegCode(); // made from lastDonationAmount and segmentCode

	      if (!uniqueGivingBySegementMap.containsKey(compositeGivingKey))
	        uniqueGivingBySegementMap.put(compositeGivingKey, record);
	    }

	    List<Record> sortedRecordList = new ArrayList<>();
	    for (Entry<String, Record> entry : uniqueGivingBySegementMap.entrySet()) {
	      sortedRecordList.add(entry.getValue());
	    }
	    Collections.sort(sortedRecordList, new RecordSorters.CompareBySegCodeLastDonationAmount());

	    ExcelCell[][] giftReportOutput = new ExcelCell[sortedRecordList.size()][GIFT_REPORT_HEADER.length];
	    
	    Font defaultFont = new XSSFFont();
	    XSSFCellStyle cellStyle = ExcelCell.createCellStyle();
	    int row = 0;

	    for (int i = 0; i < sortedRecordList.size(); ++i) {
	      int col = -1;
	      Record record = sortedRecordList.get(i);

	      giftReportOutput[row][++col] = new ExcelCell(record.getLstDnAmt(), CellType.NUMERIC, cellStyle, defaultFont, false);
	      giftReportOutput[row][++col] = new ExcelCell(record.getLstDnDat(), CellType.STRING, cellStyle, defaultFont, false);
	      giftReportOutput[row][++col] = new ExcelCell(record.getDn1Amt(), CellType.NUMERIC, cellStyle, defaultFont, false);
	      giftReportOutput[row][++col] = new ExcelCell(record.getDn2Amt(), CellType.NUMERIC, cellStyle, defaultFont, false);
	      giftReportOutput[row][++col] = new ExcelCell(record.getDn3Amt(), CellType.NUMERIC, cellStyle, defaultFont, false);
	      giftReportOutput[row][++col] = new ExcelCell(record.getODnAmt(), CellType._NONE, cellStyle, defaultFont, false);
	      giftReportOutput[row][++col] = new ExcelCell(record.getSegCode(), CellType.STRING, cellStyle, defaultFont, false);
	      ++row;
	    }

	    File file = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_" + GIFT_REPORT_FILENAME);

	    if (file.exists() && file.isFile()) {
	      int buttonPressed = JOptionPane.showConfirmDialog(UiController.getMainFrame(), "Gift report already exists, would you like to overwrite it?",
	              "File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

	      if (buttonPressed != JOptionPane.YES_OPTION) {
	        JOptionPane.showMessageDialog(UiController.getMainFrame(), "Gift report will be affixed with _copy", "Gift report",
	                JOptionPane.INFORMATION_MESSAGE);
	        
	        file = new File(UserPrefs.getLastUsedFolder() + "\\" + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_copy.xlsx");
	      }
	    }

	    // Write the data to the file
	    try {
	      XLSXWriter.writeReport(file, GIFT_REPORT_HEADER, giftReportOutput);
	    } catch (ApplicationException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	  }

	}
