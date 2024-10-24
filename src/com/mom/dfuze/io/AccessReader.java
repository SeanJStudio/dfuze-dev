/**
 * Project: Dfuze
 * File: AccessReader.java
 * Date: May 26, 2020
 * Time: 5:31:27 AM
 */
package com.mom.dfuze.io;

import java.io.File;
import java.io.FileReader;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.InputData;
import com.mom.dfuze.data.SegmentPlan;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.DelimiterSelectDialog;
import com.mom.dfuze.ui.UiController;

/**
 * AccessReader class to perform file input operations on Microsoft Access Database file formats
 * Uses the Singleton patter to prevent instantiation
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class AccessReader {

	/**
	 * Private constructor to prevent instantiation of AccessReader class
	 */
	private AccessReader() {

	}

	/**
	 * Reads and extracts headers and data from a database table
	 * 
	 * @param file
	 *          the database file
	 * @param userData
	 *          the userData to to extract all the dfHeaders from
	 * @param tableName
	 *          the tableName in the database to open
	 * @return the data in the table as InputData
	 * @throws Exception
	 */
	public static InputData read(File file, String tableName) throws Exception {
		String[] inHeaders = null;
		String[][] data = null;
		Database db = null;
		try {
			// System.out.println(file.getCanonicalPath() + "\n" + tableName);
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			db = DatabaseBuilder.open(file);    
			Table table = db.getTable(tableName);

			int numberOfFields = table.getColumnCount(); // get the amount of fields as an int
			// System.out.println(table.getRowCount());
			inHeaders = new String[numberOfFields]; // create new String[] to store fields aka headers
			int headerIndex = -1;

			for (Column column : table.getColumns()) {
				String columnName = column.getName();
				inHeaders[++headerIndex] = columnName;
			}
			
			
			// table.getRows() can return the wrong amount of rows from metadata, instead we must manually check https://sourceforge.net/p/jackcess/bugs/108/
			Cursor cursor = table.getDefaultCursor();
			cursor.reset();
			int rowCount = 0;
			while(cursor.moveToNextRow()) {
				rowCount++;
			}

			data = new String[rowCount][numberOfFields]; // 2-d array to store each row and then each field value
			
			int rowCounter = 0;
			for (Row row : table) {

				int columnCounter = 0;

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

				for (Object value : row.values()) {
					if (value == null)
						data[rowCounter][columnCounter++] = "";
					else {
						switch(table.getColumns().get(columnCounter).getType()) {
						case SHORT_DATE_TIME:
						case EXT_DATE_TIME:
							data[rowCounter][columnCounter++] = row.getLocalDateTime(table.getColumns().get(columnCounter - 1).getName()).format(formatter);
							break;
						default:
							data[rowCounter][columnCounter++] = value.toString();
							break;
						}
					}
				}

				rowCounter++;
			}

			db.close();

			for (int i = 0; i < inHeaders.length; ++i)
				inHeaders[i] = Common.checkIfDfHeader(inHeaders[i], new String[0], UserData.getAllDfHeaders());

			// userData.load(inHeaders, data);

		} catch (Exception e) {
			
			if(db != null)
				db.close();
			
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new Exception(e.getMessage());
		}

		return new InputData(inHeaders, data);
	}

	public static List<List<String>> readRaw(File file, String tableName) throws Exception {
		String[] inHeaders = null;
		String[][] data = null;
		Database db = null;
		try {
			// System.out.println(file.getCanonicalPath() + "\n" + tableName);
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			db = DatabaseBuilder.open(file);
			Table table = db.getTable(tableName);

			int numberOfFields = table.getColumnCount(); // get the amount of fields as an int
			// System.out.println(table.getRowCount());
			inHeaders = new String[numberOfFields]; // create new String[] to store fields aka headers
			int headerIndex = -1;

			for (Column column : table.getColumns()) {
				String columnName = column.getName();
				inHeaders[++headerIndex] = columnName;
			}

			data = new String[table.getRowCount()][numberOfFields]; // 2-d array to store each row and then each field value
			int rowCounter = 0;
			for (Row row : table) {

				int columnCounter = 0;

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

				for (Object value : row.values()) {
					if (value == null)
						data[rowCounter][columnCounter++] = "";
					else {
						switch(table.getColumns().get(columnCounter).getType()) {
						case SHORT_DATE_TIME:
						case EXT_DATE_TIME:
							data[rowCounter][columnCounter++] = row.getLocalDateTime(table.getColumns().get(columnCounter - 1).getName()).format(formatter);
							break;
						default:
							data[rowCounter][columnCounter++] = value.toString();
							break;
						}
					}
				}

				rowCounter++;
			}

			db.close();

			// userData.load(inHeaders, data);

		} catch (Exception e) {
			if(db != null)
				db.close();
			
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new Exception(e.getMessage());
		}

		List<List<String>> newData = new ArrayList<>();
		newData.add(Arrays.asList(inHeaders));

		for (String[] line : data) {
			newData.add(Arrays.asList(line));
		}

		return newData;
	}

	public static String[][] readAsStringArray(File file, String tableName) throws ApplicationException {
		String[] inHeaders = null;
		String[][] data = null;
		Database db = null;
		try {
			// System.out.println(file.getCanonicalPath() + "\n" + tableName);
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			db = DatabaseBuilder.open(file);
			Table table = db.getTable(tableName);

			int numberOfFields = table.getColumnCount(); // get the amount of fields as an int
			// System.out.println(table.getRowCount());
			inHeaders = new String[numberOfFields]; // create new String[] to store fields aka headers
			int headerIndex = -1;

			for (Column column : table.getColumns()) {
				String columnName = column.getName();
				inHeaders[++headerIndex] = columnName;
			}

			data = new String[table.getRowCount()][numberOfFields]; // 2-d array to store each row and then each field value
			int rowCounter = 0;
			for (Row row : table) {

				int columnCounter = 0;
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

				for (Object value : row.values()) {
					if (value == null)
						data[rowCounter][columnCounter++] = "";
					else {
						switch(table.getColumns().get(columnCounter).getType()) {
						case SHORT_DATE_TIME:
						case EXT_DATE_TIME:
							data[rowCounter][columnCounter++] = row.getLocalDateTime(table.getColumns().get(columnCounter - 1).getName()).format(formatter);
							break;
						default:
							data[rowCounter][columnCounter++] = value.toString();
							break;
						}
					}
				}

				rowCounter++;
			}

			db.close();

			// userData.load(inHeaders, data);

		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new ApplicationException(e.getMessage());
		}

		return data;
	}

	/**
	 * Reads segmentPlan data from a Microsoft Access Database file into a List<SegmentPlan>
	 * Each row in the data represents a SegmentPlan Object
	 * 
	 * @param segmentPlanName
	 *          the name of the segmentPlan table in the segmentPlan Database
	 * @return List<SegmentPlan>, the list of segmentPlans from the database
	 * @throws ApplicationException
	 *           throws an Exception in case something goes wrong
	 */
	public static List<SegmentPlan> readSegmentPlanData(String segmentPlanName) throws ApplicationException {
		List<SegmentPlan> segmentPlanList = null;

		String segmentPlanPath = "segment-plans/segment-plans.mdb";
		try {
			JOptionPane.showMessageDialog(UiController.getMainFrame(), String.format("Please import the %s segment plan.\n", segmentPlanName),
					"Import Segment Plan", JOptionPane.INFORMATION_MESSAGE);
			JFileChooser fileChooser = new JFileChooser(UserPrefs.getLastUsedFolder());
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".csv .dat .txt", "csv", "dat", "txt"));
			fileChooser.setAcceptAllFileFilterUsed(true);

			int returnVal = fileChooser.showOpenDialog(UiController.getMainFrame());
			File file = null;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
				System.out.println(file.getCanonicalPath());
				String previewText = TextReader.previewText(file);
				DelimiterSelectDialog delimiterSelectDialog = new DelimiterSelectDialog(UiController.getMainFrame(), previewText);
				delimiterSelectDialog.getChckbxFixedWidth().setEnabled(false);
				delimiterSelectDialog.setVisible(true);

				if (!delimiterSelectDialog.getIsNextPressed()) // if the user has selected a delimiter
					throw new ApplicationException(String.format("Job aborted because the import was cancelled"));

				char delimiter = delimiterSelectDialog.getDelimiterField();

				FileReader in = new FileReader(file);
				List<List<String>> arr = org.simpleflatmapper.csv.CsvParser.separator(delimiter).stream(in).map(Arrays::asList).collect(Collectors.toList());
				in.close();

				File segmentPlanfile = new File(segmentPlanPath);
				System.out.println(segmentPlanfile.getCanonicalPath());
				String[] tableNames = readTableNames(segmentPlanfile);

				boolean hasTableName = false;
				for (String tableName : tableNames) {
					if (tableName.trim().equalsIgnoreCase(segmentPlanName.trim())) {
						hasTableName = true;
						break;
					}
				}

				if (!hasTableName)
					throw new ApplicationException(String.format("The %s segment plan table could not be found in the database.", segmentPlanName));
				System.out.println("read the tables");
				/*
				 * /
				 */

				Database db = DatabaseBuilder.open(segmentPlanfile);

				System.out.println("opened the db");
				Table table = db.getTable(segmentPlanName);
				System.out.println("got the table");

				int numberOfFields = table.getColumnCount(); // get the amount of fields as an int
				// System.out.println(table.getRowCount());
				String[] requiredSegmentPlanFields = new String[numberOfFields]; // create new String[] to store fields aka headers
				int headerIndex = -1;

				for (Column column : table.getColumns()) {
					String columnName = column.getName();
					requiredSegmentPlanFields[++headerIndex] = columnName;
				}
				db.close();

				/*
				 * /
				 */

				String[] inHeaders = new String[arr.get(0).size()];
				inHeaders = arr.get(0).toArray(inHeaders);

				arr.remove(0);

				if (requiredSegmentPlanFields.length != inHeaders.length)
					throw new ApplicationException(
							String.format("Was expecting %d fields but found %d in the segment plan", requiredSegmentPlanFields.length, inHeaders.length));

				for (int i = 0; i < requiredSegmentPlanFields.length; ++i) {

					if (!requiredSegmentPlanFields[i].equals(inHeaders[i]))
						throw new ApplicationException(
								String.format("Found %s but was expecting required segment plan field of %s at column %d in the import file", inHeaders[i],
										requiredSegmentPlanFields[i], (i + 1)));

					boolean fieldFoundInSegmentPlanDb = false;
					for (String field : SegmentPlan.allSegmentPlanHeaders) {
						if (requiredSegmentPlanFields[i].equals(field)) {
							fieldFoundInSegmentPlanDb = true;
							break;
						}
					}

					if (!fieldFoundInSegmentPlanDb) {
						throw new ApplicationException(String.format(
								"Could not find the unkown required segment plan field of %s in the Segment Plan field database", requiredSegmentPlanFields[i]));
					}

				}

				segmentPlanList = new ArrayList<>();

				for (int k = 0; k < arr.size(); ++k) {
					SegmentPlan segmentPlan = new SegmentPlan();
					for (int l = 0; l < arr.get(k).size(); ++l) {
						String fieldToSet = inHeaders[l];
						String fieldValue = arr.get(k).get(l);
						if (fieldValue == null)
							fieldValue = "";

						System.out.println(fieldToSet + " " + fieldValue);
						try {
							if (fieldToSet.equals(SegmentPlan.fieldName.PRIORITY.getName()))
								segmentPlan.setPriority(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.SEGMENT_CODE.getName()))
								segmentPlan.setSegmentCode(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.SEGMENT_NAME.getName()))
								segmentPlan.setSegmentName(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.IS_INDIVIDUAL.getName()))
								segmentPlan.setIsIndividual(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.IS_BUSINESS.getName()))
								segmentPlan.setIsBusiness(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.GIFT_DATE_USED.getName()))
								segmentPlan.setGiftDateUsed(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.FROM_GIFT_DATE.getName()))
								segmentPlan.setFromGiftDate(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.TO_GIFT_DATE.getName()))
								segmentPlan.setToGiftDate(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.FROM_GIFT_DATE_MONTHS.getName()))
								segmentPlan.setFromGiftDateMonths(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.TO_GIFT_DATE_MONTHS.getName()))
								segmentPlan.setToGiftDateMonths(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.GIFT_USED.getName()))
								segmentPlan.setGiftUsed(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.FROM_GIFT.getName()))
								segmentPlan.setFromGift(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.TO_GIFT.getName()))
								segmentPlan.setToGift(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.FROM_NUM_OF_GIFTS.getName()))
								segmentPlan.setFromNumOfGifts(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.TO_NUM_OF_GIFTS.getName()))
								segmentPlan.setToNumOfGifts(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.LETTER_VERSION.getName()))
								segmentPlan.setLetterVersion(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.PACKAGE_VERSION.getName()))
								segmentPlan.setPackageVersion(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.REPLY_VERSION.getName()))
								segmentPlan.setReplyVersion(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.IS_STANDARD_ASK.getName()))
								segmentPlan.setIsStandardAsk(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.IS_ACTIVE_ASK.getName()))
								segmentPlan.setIsActiveAsk(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_ASK_1.getName()))
								segmentPlan.setStaticAsk1(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_ASK_2.getName()))
								segmentPlan.setStaticAsk2(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_ASK_3.getName()))
								segmentPlan.setStaticAsk3(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.ASK_OPEN.getName()))
								segmentPlan.setAskOpen(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.IS_STANDARD_MONTHLY_ASK.getName()))
								segmentPlan.setIsStandardMonthlyAsk(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.IS_ACTIVE_MONTHLY_ASK.getName()))
								segmentPlan.setIsActiveMonthlyAsk(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_MONTHLY_ASK_1.getName()))
								segmentPlan.setStaticMonthlyAsk1(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_MONTHLY_ASK_2.getName()))
								segmentPlan.setStaticMonthlyAsk2(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_MONTHLY_ASK_3.getName()))
								segmentPlan.setStaticMonthlyAsk3(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.MONTHLY_ASK_OPEN.getName()))
								segmentPlan.setMonthlyAskOpen(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.SEGMENT_PLAN_FILTER_1.getName()))
								segmentPlan.setSegmentPlanFilter1(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.SEGMENT_PLAN_FILTER_2.getName()))
								segmentPlan.setSegmentPlanFilter2(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.IS_SUPPRESSED.getName()))
								segmentPlan.setIsSuppressed(fieldValue);
							else if (fieldToSet.equals(SegmentPlan.fieldName.ONE_AND_ALL_CCM_SEGMENT_DESCRIPTION.getName()))
								segmentPlan.setOneAndAllCCMSegmentDescription(fieldValue);
							else {
								throw new ApplicationException(String.format("Field of %s doesn't have a setter in the segmentPlanFactory", fieldToSet));
							}
						} catch (Exception e) {
							throw new ApplicationException(String.format("Error on Line %d at field %d\n%s", k + 2, l + 1, e.getMessage()));
						}

					}

					if (segmentPlan.getIsStandardAsk() && segmentPlan.getIsActiveAsk())
						throw new ApplicationException(String.format("Only one of %s and %s can be set to \"Y\"", SegmentPlan.fieldName.IS_STANDARD_ASK.getName(),
								SegmentPlan.fieldName.IS_ACTIVE_ASK.getName()));

					if (segmentPlan.getIsStandardMonthlyAsk() && segmentPlan.getIsActiveMonthlyAsk())
						throw new ApplicationException(String.format("Only one of %s and %s can be set to \"Y\"",
								SegmentPlan.fieldName.IS_STANDARD_MONTHLY_ASK.getName(), SegmentPlan.fieldName.IS_ACTIVE_MONTHLY_ASK.getName()));

					segmentPlanList.add(segmentPlan);
				}

			} else {
				throw new ApplicationException(String.format("Import was cancelled, please re-run"));
			}

		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		} finally {

		}
		return segmentPlanList;
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
	public static String[] readTableNames(File file) throws Exception {
		String[] databaseNames;
		try {
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			Database db = DatabaseBuilder.open(file);
			databaseNames = new String[db.getTableNames().size()];
			databaseNames = db.getTableNames().toArray(databaseNames);
			db.close();
			return databaseNames;
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
	}

}