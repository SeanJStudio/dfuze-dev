/**
 * Project: Dfuze
 * File: SegmentationReport.java
 * Date: Jul 21, 2020
 * Time: 8:42:45 AM
 */
package com.mom.dfuze.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.ExcelCell;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.SegmentPlan;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.ui.UiController;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class SegmentationReport {
  public static final String SEGMENTATION_REPORT_FILENAME = "dfuze_segmentation_report.xlsx";

  /**
   * This method assumes the segmentPlanList parameter is unsorted
   * This method assumes the recordList parameter has been segmented
   * 
   * @param segmentPlanList
   * @param recordList
   */
  public static void writeHMASegmentationReport(List<SegmentPlan> unsortedSegmentPlanList, List<Record> segmentedRecordList) {

    final String[] SEGMENTATION_REPORT_HEADER = new String[] { "SEGMENT_CODE", "SEGMENT_NAME", "IMPORTED_QTY", "M/P_#1", "DONT_MAIL_INTL",
            "NO ADDRESS", "QTY", "SUPPRESSION", "PUTBACK", "QTY" };
    
    XSSFWorkbook wb = new XSSFWorkbook();
    
    Font defaultFont = wb.createFont();
    XSSFCellStyle cellStyle = wb.createCellStyle();
    try {wb.close();} catch(IOException e) {}
    
    ExcelCell blankCell = new ExcelCell("", CellType.STRING, cellStyle, defaultFont, false);
    final ExcelCell[] blankLine = new ExcelCell[] { blankCell, blankCell, blankCell, blankCell, blankCell, blankCell, blankCell, blankCell, blankCell,
            blankCell };
    final int HEADER_ROW = 1;

    // Add +2 to include a blank row as a spacer and then write write the total sums of each applicable column on another row
    ExcelCell[][] segmentationReportOutput = new ExcelCell[unsortedSegmentPlanList.size() + 2][SEGMENTATION_REPORT_HEADER.length];

    int i;
    for (i = 0; i < unsortedSegmentPlanList.size(); ++i) {
      int sumOfSegmentFound = 0;
      SegmentPlan segPlan = unsortedSegmentPlanList.get(i);

      for (int j = 0; j < segmentedRecordList.size(); ++j) {
        Record record = segmentedRecordList.get(j);

        if (segPlan.getSegmentCode().trim().equalsIgnoreCase(record.getSegCode().trim()))
          sumOfSegmentFound++;
      }

      // offset current row by header row and increment by 1 because excel is not zero based
      int currentRow = HEADER_ROW + (i + 1);

      String qty1Forumla = String.format("=+C%d-D%d-E%d-F%d", currentRow, currentRow, currentRow, currentRow);
      String qty2Forumla = String.format("=+G%d-H%d+I%d", currentRow, currentRow, currentRow);

      int rowIndex = -1;
      // set the report values in the report output
      segmentationReportOutput[i][++rowIndex] = new ExcelCell(segPlan.getSegmentCode(), CellType.STRING, cellStyle, defaultFont, false); // Segment
                                                                                                                                          // Code
      segmentationReportOutput[i][++rowIndex] = new ExcelCell(segPlan.getSegmentName(), CellType.STRING, cellStyle, defaultFont, false); // Segment
                                                                                                                                          // Name
      segmentationReportOutput[i][++rowIndex] = new ExcelCell(String.valueOf(sumOfSegmentFound), CellType.NUMERIC, cellStyle, defaultFont, true); // Imported
                                                                                                                                                   // Qty
      segmentationReportOutput[i][++rowIndex] = new ExcelCell("0", CellType.NUMERIC, cellStyle, defaultFont, false); // M/P #1
      segmentationReportOutput[i][++rowIndex] = new ExcelCell("0", CellType.NUMERIC, cellStyle, defaultFont, false); // Do Not Mail INTL
      segmentationReportOutput[i][++rowIndex] = new ExcelCell("0", CellType.NUMERIC, cellStyle, defaultFont, false); // No Address
      segmentationReportOutput[i][++rowIndex] = new ExcelCell(qty1Forumla, CellType.NUMERIC,cellStyle, defaultFont, true); // QTY1
      segmentationReportOutput[i][++rowIndex] = new ExcelCell("0", CellType.NUMERIC, cellStyle, defaultFont, false); // Suppression
      segmentationReportOutput[i][++rowIndex] = new ExcelCell("0", CellType.NUMERIC, cellStyle, defaultFont, false); // PUTBACK
      segmentationReportOutput[i][++rowIndex] = new ExcelCell(qty2Forumla, CellType.NUMERIC, cellStyle, defaultFont, true); // QTY2
    }

    // Create the summary data
    int currentRowNum = HEADER_ROW + (i + 1) - 1;
    String importedQtySum = String.format("SUM(C2:C%d)", currentRowNum);
    String mp1Sum = String.format("SUM(D2:D%d)", currentRowNum);
    String doNotMailIntlSum = String.format("SUM(E2:E%d)", currentRowNum);
    String noAddressSum = String.format("SUM(F2:F%d)", currentRowNum);
    String qty1Sum = String.format("SUM(G2:G%d)", currentRowNum);
    String suppressionSum = String.format("SUM(H2:H%d)", currentRowNum);
    String putbackSum = String.format("SUM(I2:I%d)", currentRowNum);
    String qty2Sum = String.format("SUM(J2:J%d)", currentRowNum);

    segmentationReportOutput[i++] = blankLine;
    int rowIndex = -1;
    // set the sums in the report output
    segmentationReportOutput[i][++rowIndex] = new ExcelCell("", CellType.NUMERIC, cellStyle, defaultFont, true); // Segment Code
    segmentationReportOutput[i][++rowIndex] = new ExcelCell("", CellType.NUMERIC, cellStyle, defaultFont, true); // Segment Name
    segmentationReportOutput[i][++rowIndex] = new ExcelCell(importedQtySum, CellType.NUMERIC, cellStyle, defaultFont, true); // Imported Qty
    segmentationReportOutput[i][++rowIndex] = new ExcelCell(mp1Sum, CellType.NUMERIC, cellStyle, defaultFont, true); // M/P #1
    segmentationReportOutput[i][++rowIndex] = new ExcelCell(doNotMailIntlSum, CellType.NUMERIC, cellStyle, defaultFont, true); // Do Not Mail INTL
    segmentationReportOutput[i][++rowIndex] = new ExcelCell(noAddressSum, CellType.NUMERIC, cellStyle, defaultFont, true); // No Address Sum
    segmentationReportOutput[i][++rowIndex] = new ExcelCell(qty1Sum, CellType.NUMERIC, cellStyle, defaultFont, true); // QTY1
    segmentationReportOutput[i][++rowIndex] = new ExcelCell(suppressionSum, CellType.NUMERIC, cellStyle, defaultFont, true); // Suppression
    segmentationReportOutput[i][++rowIndex] = new ExcelCell(putbackSum, CellType.NUMERIC, cellStyle, defaultFont, true); // PUTBACK
    segmentationReportOutput[i][++rowIndex] = new ExcelCell(qty2Sum, CellType.NUMERIC, cellStyle, defaultFont, true); // QTY2

    File file = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_" + SEGMENTATION_REPORT_FILENAME);

    if (file.exists() && file.isFile()) {
      int buttonPressed = JOptionPane.showConfirmDialog(UiController.getMainFrame(),
              "Segmentation report already exists, would you like to overwrite it?", "File exists", JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.QUESTION_MESSAGE);

      if (buttonPressed != JOptionPane.YES_OPTION) {
        JOptionPane.showMessageDialog(UiController.getMainFrame(), "Segmentation will be affixed with _copy", "Segmentation report",
                JOptionPane.INFORMATION_MESSAGE);
        file = new File(UserPrefs.getLastUsedFolder() + "\\" + file.getName().substring(0, file.getName().lastIndexOf('.')) + "_copy.xlsx");
      }
    }

    // Write the data to the file
    try {
      XLSXWriter.writeReport(file, SEGMENTATION_REPORT_HEADER, segmentationReportOutput);
    } catch (ApplicationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
