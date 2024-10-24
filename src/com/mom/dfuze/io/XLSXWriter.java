/**
 * Project: Dfuze
 * File: XlsxWriter.java
 * Date: Apr 4, 2020
 * Time: 7:38:13 AM
 */
package com.mom.dfuze.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.Dfuze;
import com.mom.dfuze.data.ExcelCell;

/**
 * XlsxWriter class to perform file output operations on Microsoft Excel 2007-2010 .xlsx file formats
 * Uses the Singleton pattern to prevent instantiation
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class XLSXWriter {

	private static final String INV_CHAR = "\\\\|\\/|\\*|\\?|\\:|\\[|\\]|'";

	/**
	 * Private constructor for XlsxWriter Class to prevent instantiation
	 */
	private XLSXWriter() {
	}

	/**
	 * Creates a Microsoft Excel 2007-2010 .xlsx file
	 * 
	 * @param file
	 *          the file to create
	 * @param headers
	 *          the headers to set in the file
	 * @param data
	 *          the data to set in the file
	 * @throws ApplicationException
	 *           in case something goes wrong
	 */
	public static void write(File file, String[] headers, String[][] data, Boolean formatAsFormulas, Boolean formatAsString) throws ApplicationException {

		if(file.exists() && !file.isDirectory()) { 
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}


		FileOutputStream outputStream = null;
		Workbook workbook = null;
		XSSFWorkbook xwb = null;

		try {

			String fileName = file.getAbsolutePath(); // get the path

			if (fileName.contains(".")) // get all text before '.' if found
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));

			file = new File(fileName + ".xlsx"); // make sure its an xlsx file

			outputStream = new FileOutputStream(file);

			xwb = new XSSFWorkbook();
			xwb.getProperties().getCoreProperties().setCreator(Dfuze.APP_NAME);    
			workbook = new SXSSFWorkbook(xwb, 1);

			Sheet sheet = workbook.createSheet(file.getName().substring(0, file.getName().lastIndexOf('.')).replaceAll(INV_CHAR, ""));
			FormulaEvaluator fEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
			sheet.setDefaultColumnWidth(15);

			DataFormat format = workbook.createDataFormat();
			CellStyle cellStringStyle = workbook.createCellStyle();
			cellStringStyle.setDataFormat(format.getFormat(ExcelCell.format.STRING.getFormat()));

			Row header = sheet.createRow(0);

			for (int i = 0; i < headers.length; i++) {
				Cell headerCell = header.createCell(i);
				headerCell.setCellValue(headers[i]);

				if(formatAsString)
					headerCell.setCellStyle(cellStringStyle);
			}

			for (int i = 0; i < data.length; ++i) {
				Row row = sheet.createRow(i + 1); // + 1 to account for header row
				for (int j = 0; j < data[i].length; ++j) {
					Cell cell = row.createCell(j);
					if (formatAsFormulas)
						cell.setCellFormula(data[i][j]);
					else
						cell.setCellValue(data[i][j]);

					fEvaluator.clearAllCachedResultValues();

					if(formatAsString)
						cell.setCellStyle(cellStringStyle);
				}
			}
			workbook.setForceFormulaRecalculation(true);
			workbook.write(outputStream);

		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		} finally {
			try {
				if(workbook != null)
					workbook.close();

				if(xwb != null)
					xwb.close();

				if(outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}
	}

	/***/
	public static void writeReport (File file, String[] headers, ExcelCell[][] data) throws ApplicationException {
		if(file.exists() && !file.isDirectory()) { 
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}


		FileOutputStream outputStream = null;
		Workbook workbook = null;
		XSSFWorkbook xwb = null;

		try {

			String fileName = file.getAbsolutePath(); // get the path

			if (fileName.contains(".")) // get all text before '.' if found
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));

			file = new File(fileName + ".xlsx"); // make sure its an xlsx file

			outputStream = new FileOutputStream(file);

			xwb = new XSSFWorkbook();
			xwb.getProperties().getCoreProperties().setCreator(Dfuze.APP_NAME);    
			workbook = new SXSSFWorkbook(xwb, 1);

			Sheet sheet = workbook.createSheet(file.getName().substring(0, file.getName().lastIndexOf('.')).replaceAll(INV_CHAR, ""));
			FormulaEvaluator fEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
			DataFormat format = workbook.createDataFormat();
			CellStyle cellDateStyle = workbook.createCellStyle();
			CellStyle cellStringStyle = workbook.createCellStyle();
			CellStyle cellGeneralStyle = workbook.createCellStyle();

			cellDateStyle.setDataFormat(format.getFormat(ExcelCell.format.DATE.getFormat()));
			cellStringStyle.setDataFormat(format.getFormat(ExcelCell.format.STRING.getFormat()));
			cellGeneralStyle.setDataFormat(format.getFormat(ExcelCell.format.GENERAL.getFormat()));

			sheet.setDefaultColumnWidth(15);

			Row header = sheet.createRow(0);

			for (int i = 0; i < headers.length; i++) {
				Cell headerCell = header.createCell(i);
				headerCell.setCellValue(headers[i]);
			}

			for (int i = 0; i < data.length; ++i) {
				Row row = sheet.createRow(i + 1); // + 1 to account for header row
				for (int j = 0; j < data[i].length; ++j) {
					Cell cell = row.createCell(j);
					ExcelCell excelCell = data[i][j];
					String value = excelCell.getCellValue();

					switch (excelCell.getCellType()) {
					case STRING:
						cell.setCellStyle(cellStringStyle);
						break;
					case NUMERIC:

						/*
						 * switch (excelCell.getCellFormat()) {
						 * case DATE:
						 * if (!value.isEmpty())
						 * value = "DATEVALUE(\"" + value + "\")";
						 * cell.setCellStyle(cellDateStyle);
						 * break;
						 * default:
						 * break;
						 * }
						 */
						if (excelCell.getIsForumla())
							cell.setCellFormula(value);
						else
							cell.setCellFormula(value.replaceAll("[^0-9\\.]", ""));
						break;
					case _NONE:
					default:
						cell.setCellStyle(cellGeneralStyle);
						break;
					}

					if (excelCell.getIsForumla())
						cell.setCellFormula(value);
					else
						cell.setCellValue(value);

					fEvaluator.clearAllCachedResultValues();
				}
			}
			workbook.setForceFormulaRecalculation(true);
			workbook.write(outputStream);

		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		} finally {
			try {
				if(workbook != null)
					workbook.close();

				if(xwb != null)
					xwb.close();

				if(outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}
	}

	public static void writeReportRaw (File file, List<List<ExcelCell>> data) throws ApplicationException {
		if(file.exists() && !file.isDirectory()) { 
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}

		FileOutputStream outputStream = null;
		Workbook workbook = null;
		XSSFWorkbook xwb = null;

		try {

			String fileName = file.getAbsolutePath(); // get the path

			if (fileName.contains(".")) // get all text before '.' if found
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));

			file = new File(fileName + ".xlsx"); // make sure its an xlsx file

			outputStream = new FileOutputStream(file);

			xwb = new XSSFWorkbook();
			xwb.getProperties().getCoreProperties().setCreator(Dfuze.APP_NAME);    
			workbook = new SXSSFWorkbook(xwb, 1);

			Sheet sheet = workbook.createSheet(file.getName().substring(0, file.getName().lastIndexOf('.')).replaceAll(INV_CHAR, ""));
			FormulaEvaluator fEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

			DataFormat format = workbook.createDataFormat();
			
			short stringFormat = format.getFormat(ExcelCell.format.STRING.getFormat());
			short generalFormat = format.getFormat(ExcelCell.format.GENERAL.getFormat());
			//short numberFormat = format.getFormat(ExcelCell.format.GENERAL.getFormat());
			
			
			
			sheet.setDefaultColumnWidth(20);
			
			//CellStyle cellDateStyle = workbook.createCellStyle();
			//CellStyle cellStringStyle = workbook.createCellStyle();
			//CellStyle cellGeneralStyle = workbook.createCellStyle();

			//cellDateStyle.setDataFormat(format.getFormat(ExcelCell.format.DATE.getFormat()));
			//cellStringStyle.setDataFormat(format.getFormat(ExcelCell.format.STRING.getFormat()));
			//cellGeneralStyle.setDataFormat(format.getFormat(ExcelCell.format.GENERAL.getFormat()));

			for (int i = 0; i < data.size(); ++i) {
				Row row = sheet.createRow(i);
				for (int j = 0; j < data.get(i).size(); ++j) {

					Cell cell = row.createCell(j);
					ExcelCell excelCell = data.get(i).get(j);
					String value = excelCell.getCellValue();
					
					CellStyle cellStyle = workbook.createCellStyle();
					if(excelCell.getCellStyle() != null)
						cellStyle.cloneStyleFrom(excelCell.getCellStyle()); 
					
					Font font = workbook.createFont();
					font.setBold(excelCell.getCellFont().getBold());
					font.setColor(excelCell.getCellFont().getColor());
					
					cellStyle.setFont(font);
					
					switch (excelCell.getCellType()) {
					case STRING:
						cellStyle.setDataFormat(stringFormat);
						break;
					case NUMERIC:
						cellStyle.setDataFormat(generalFormat);
						break;
					case _NONE:
					default:
						cellStyle.setDataFormat(generalFormat);
						break;
					}

					// allow formulas
					if (excelCell.getIsForumla())
						cell.setCellFormula(value);
					else
						cell.setCellValue(value);

					//styling
					cell.setCellStyle(cellStyle);

					fEvaluator.clearAllCachedResultValues();
				}
			}
			workbook.setForceFormulaRecalculation(true);
			workbook.write(outputStream);

		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		} finally {
			try {
				if(workbook != null)
					workbook.close();

				if(xwb != null)
					xwb.close();

				if(outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}
	}

}
