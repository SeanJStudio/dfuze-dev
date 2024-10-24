/**
 * Project: Dfuze
 * File: AccessWriter.java
 * Date: May 28, 2020
 * Time: 8:30:55 PM
 */
package com.mom.dfuze.io;

import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.ColumnBuilder;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;
import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.ui.UiController;

/**
 * AccessWriter class to perform file output operations on Microsoft Access Database file formats
 * Uses the Singleton pattern to prevent instantiation
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class AccessWriter {

	public static final int MAX_VARCHAR_LENGTH = 255;
	public static final int MAX_TABLE_NAME_LENGTH = 64;
	public static final int MAX_FIELD_NAME_LENGTH = 64;

	/**
	 * Private Constructor to prevent instantiation of the Class AccessWriter
	 */
	private AccessWriter() {
	}

	/**
	 * Creates a Microsoft Access Database file
	 * 
	 * @param file
	 *          the databse file to create
	 * @param headers
	 *          the headers to write to the database file
	 * @param data
	 *          the data to write to the database file
	 * @throws Exception
	 */
	public static void write(File file, String[] headers, String[][] data) throws Exception {

		if(file.exists() && !file.isDirectory()) { 
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}


		try {
			Database db = DatabaseBuilder.create(Database.FileFormat.V2000, file);
			Table newTable;

			String fileName = file.getName();

			if (fileName.contains(".")) // get all text before '.' if found
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));

			TableBuilder tableBuilder = new TableBuilder(fileName);

			Set<String> headerNameDupeCheck = new HashSet<>();
			int dupeFieldNum = 0;

			for (int i = 0; i < headers.length; ++i) {
				String fieldName = headers[i].replaceAll("\\.", "");

				if(fieldName.length() > MAX_FIELD_NAME_LENGTH)
					fieldName = fieldName.substring(0,MAX_FIELD_NAME_LENGTH);

				if (fieldName.isEmpty())
					fieldName = "Column " + (i + 1);
				while (!headerNameDupeCheck.add(fieldName)) {
					fieldName = fieldName.substring(0, fieldName.length() - 1) + dupeFieldNum++;
				}

				fieldName = fieldName.replaceAll("!|'|\"|\\[|\\]|\\.", "").trim();

				// Set the column type to memo if our data is too large
				if (UiController.getUserData().getExportMaxFieldLength()[i] <= MAX_VARCHAR_LENGTH)
					tableBuilder.addColumn(new ColumnBuilder(fieldName).setSQLType(Types.VARCHAR).toColumn());
				else
					tableBuilder.addColumn(new ColumnBuilder(fieldName).setType(DataType.MEMO).toColumn());
			}

			List<String[]> records = new ArrayList<>();
			for (int i = 0; i < data.length; ++i) {
				records.add(data[i]);
			}

			newTable = tableBuilder.toTable(db);
			newTable.setAllowAutoNumberInsert(false);
			newTable.addRows(records);
			db.close();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	
	
	public static boolean createDatabase(File file) throws ApplicationException {
		Database newDb = null;
		try {
			if (!file.exists()) {
				newDb = DatabaseBuilder.create(Database.FileFormat.V2000, file);
				newDb.close();
				return true;
			}
			
			return false;

		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		

	}

	/**
	 * Creates a table in a Microsoft Access Database file
	 * 
	 * @param file
	 *          the database file to create a table in
	 * @param headers
	 *          the headers to set in the table to create
	 * @param data
	 *          the data to set in the table to create
	 * @param newTableName
	 *          the String name of the table to create
	 * @throws Exception
	 */
	public static void writeTableToDatabase(File file, String[] headers, String[][] data, String newTableName) throws Exception {

		if(file.exists() && !file.isDirectory()) { 
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}


		try {
			Database db = DatabaseBuilder.open(file);
			Table newTable;

			String[] tableNames = AccessReader.readTableNames(file);
			for (String tableName : tableNames) {
				if (newTableName.trim().equalsIgnoreCase(tableName)) {
					newTableName += "Copy";
					break;
				}
			}

			TableBuilder tableBuilder = new TableBuilder(newTableName);

			Set<String> headerNameDupeCheck = new HashSet<>();
			int dupeFieldNum = 0;

			for (int i = 0; i < headers.length; ++i) {
				String fieldName = headers[i].replaceAll("\\.", "");

				if(fieldName.length() > MAX_FIELD_NAME_LENGTH)
					fieldName = fieldName.substring(0, MAX_FIELD_NAME_LENGTH);

				if (fieldName.isEmpty())
					fieldName = "Column " + (i + 1);
				while (!headerNameDupeCheck.add(fieldName)) {
					fieldName = fieldName.substring(0, fieldName.length() - 1) + dupeFieldNum++;
				}

				fieldName = fieldName.replaceAll("!|'|\"|\\[|\\]|\\.", "").trim();

				// Set the column type to memo if our data is too large
				if (UiController.getUserData().getExportMaxFieldLength()[i] <= MAX_VARCHAR_LENGTH)
					tableBuilder.addColumn(new ColumnBuilder(fieldName).setSQLType(Types.VARCHAR).toColumn());
				else
					tableBuilder.addColumn(new ColumnBuilder(fieldName).setType(DataType.MEMO).toColumn());

			}

			List<String[]> records = new ArrayList<>();
			for (int i = 0; i < data.length; ++i) {
				records.add(data[i]);
			}

			newTable = tableBuilder.toTable(db);
			newTable.setAllowAutoNumberInsert(false);
			newTable.addRows(records);
			db.close();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {

		}
	}

	/**
	 * Creates a table in a Microsoft Access Database file
	 * 
	 * @param file
	 *          the database file to create a table in
	 * @param headers
	 *          the headers to set in the table to create
	 * @param data
	 *          the data to set in the table to create
	 * @param newTableName
	 *          the String name of the table to create
	 * @throws Exception
	 */
	public static void writeTableToDatabase255(File file, String[] headers, String[][] data, String newTableName) throws Exception {

		if(file.exists() && !file.isDirectory()) { 
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}


		try {
			Database db = DatabaseBuilder.open(file);
			Table newTable;

			TableBuilder tableBuilder = new TableBuilder(newTableName);

			Set<String> headerNameDupeCheck = new HashSet<>();
			int dupeFieldNum = 0;

			for (int i = 0; i < headers.length; ++i) {
				String fieldName = headers[i].replaceAll("\\.", "");

				if(fieldName.length() > MAX_FIELD_NAME_LENGTH)
					fieldName = fieldName.substring(0, MAX_FIELD_NAME_LENGTH);

				if (fieldName.isEmpty())
					fieldName = "Column " + (i + 1);
				while (!headerNameDupeCheck.add(fieldName)) {
					fieldName = fieldName.substring(0, fieldName.length() - 1) + dupeFieldNum++;
				}

				fieldName = fieldName.replaceAll("!|'|\"|\\[|\\]|\\.", "").trim();

				tableBuilder.addColumn(new ColumnBuilder(fieldName).setSQLType(Types.VARCHAR).toColumn());


			}

			List<String[]> records = new ArrayList<>();
			for (int i = 0; i < data.length; ++i) {
				records.add(data[i]);
			}

			newTable = tableBuilder.toTable(db);
			newTable.setAllowAutoNumberInsert(false);
			newTable.addRows(records);
			db.close();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {

		}
	}


	public static void dropTable(File file, String tableName) throws Exception {

		if(file.exists() && !file.isDirectory()) { 
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));
		}


		try {
			Database db = DatabaseBuilder.open(file);
			Table tbl=db.getSystemTable("MSysObjects");
			Cursor crsr = tbl.getDefaultCursor();

			Map<String,Object> findCriteria = new HashMap<String,Object>();
			findCriteria.put("Name",tableName);
			findCriteria.put("Type",(short)1);

			if(crsr.findFirstRow(findCriteria)){
				tbl.deleteRow(crsr.getCurrentRow());
				System.out.println("table deleted:" + tableName);
			}else{
				System.out.println("there is no table:" + tableName);
			}

			db.close();

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {

		}
	}

	public static void addRowToTableIfValueNotFound(File file, String tableName, String columnName, String valueToAdd) throws Exception {
		Database db = null;
		try {
			// System.out.println(file.getCanonicalPath() + "\n" + tableName);
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			db = DatabaseBuilder.open(file);
			Table table = db.getTable(tableName);

			boolean isColumnFound = false;

			for (Column column : table.getColumns()) {
				String tempColumnName = column.getName();
				System.out.println("Looking for: " + columnName + " Found: " + tempColumnName);
				if(tempColumnName.equals(columnName)) {
					isColumnFound = true;
					break;
				}
			}

			if(!isColumnFound)
				throw new Exception("Column couldnt be found");

			Column column = table.getColumn(columnName);
			boolean found = false;

			System.out.println("Column Index is: " + column.getColumnIndex() );

			for(Row row : table) {
				String[] tempArr = new String[row.values().size()];
				row.values().toArray(tempArr);
				System.out.println(Arrays.asList(tempArr));
				if(tempArr[column.getColumnIndex()].equals(valueToAdd)) {
					System.out.println("value found");
					found = true;
					break;
				}

			}

			Object[] rowToAdd = new String[table.getColumnCount()];
			Arrays.fill(rowToAdd, "");   
			rowToAdd[column.getColumnIndex()] = valueToAdd;

			if(!found)
				table.addRow(rowToAdd);
			else
				throw new Exception("Value is already in database.");


		}	catch (Exception e) {
			//UiController.handle(e);
			throw new Exception(e.getMessage());
		} finally {
			if(db != null)
				db.close();

		}
	}

	// helpful to avoid the increasing size of .mdb files
	// fileA is
	public static void makeCopy(File file, File newFile) throws ApplicationException {
		Database db = null;
		Database newDb = null;
		try {
			String[] tableNames = AccessReader.readTableNames(file);

			// System.out.println(file.getCanonicalPath() + "\n" + tableName);
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			db = DatabaseBuilder.open(file); 

			if (newFile.exists()) {
				newDb = DatabaseBuilder.open(newFile);
			} else {
				newDb = DatabaseBuilder.create(Database.FileFormat.V2000, newFile);
			}

			for(String tableName : tableNames) {
				Table table = db.getTable(tableName);

				TableBuilder tb = new TableBuilder(tableName);
				for (Column col : table.getColumns()) {
					tb.addColumn(new ColumnBuilder(col.getName()).setFromColumn(col));
				}

				Table newTable = tb.toTable(newDb);

				// ... and copy the rows from the source table
				for (Row row : table) {
					newTable.addRowFromMap(row);
				}
			}

			if(newDb != null)
				newDb.close();

			if(db != null)
				db.close();

		} catch(Exception e) {
			throw new ApplicationException(e);
		}
	}

	public static void deleteRowFromTableIfValueFound(File file, String tableName, String columnName, String valueToRemove) throws Exception{
		Database db = null;
		try {
			// System.out.println(file.getCanonicalPath() + "\n" + tableName);
			boolean fileIsLocked = !file.renameTo(file);
			if (fileIsLocked)
				throw new ApplicationException(String.format("This file is currently in use.\nPlease close the file and try again."));

			db = DatabaseBuilder.open(file);
			Table table = db.getTable(tableName);

			boolean isColumnFound = false;

			for (Column column : table.getColumns()) {
				String tempColumnName = column.getName();
				System.out.println("Looking for: " + columnName + " Found: " + tempColumnName);
				if(tempColumnName.equals(columnName)) {
					isColumnFound = true;
					break;
				}
			}

			if(!isColumnFound)
				throw new Exception("Column couldnt be found");

			Column column = table.getColumn(columnName);

			System.out.println("Column Index is: " + column.getColumnIndex() );

			for(Row row : table) {
				String[] tempArr = new String[row.values().size()];
				row.values().toArray(tempArr);
				System.out.println(Arrays.asList(tempArr));
				if(tempArr[column.getColumnIndex()].equals(valueToRemove)) {
					table.deleteRow(row);
					break;
				}

			}

		}	catch (Exception e) {
			//UiController.handle(e);
			throw new Exception(e.getMessage());
		} finally {
			if(db != null)
				db.close();

		}
	}



}
