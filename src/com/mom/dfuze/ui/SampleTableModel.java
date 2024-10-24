/**
 * Project: Dfuze
 * File: DedupeTableModel.java
 * Date: May 12, 2020
 * Time: 8:31:06 PM
 */
package com.mom.dfuze.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.swing.table.AbstractTableModel;

import com.mom.dfuze.data.Record;

/**
 * DedupeTableModel Class used to prepare data for users when viewing duplicates after running a deduplication
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class SampleTableModel extends AbstractTableModel {

	private List<Record> sampleList;
	private String[][] tableData;
	private String[] tableHeaders;

	public SampleTableModel(List<Record> sampleList) {
		this.sampleList = sampleList;
		tableHeaders = UiController.getUserData().getExportHeaders();
		tableData = new String[0][0];
	}
	
	@Override
	public void setValueAt(Object newVal, int row, int col) {
		tableData[row][col] = (String) newVal;
        fireTableCellUpdated(row, col);
    }

	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return tableData.length;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return tableHeaders.length;
	}

	// the column header
	@Override
	public String getColumnName(int column) {
		return tableHeaders[column];
	}

	// if you want to change the columns class
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return tableData[rowIndex][columnIndex];
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sortTableDataAscending(int columnIndex) {
		Arrays.sort(tableData, new ColumnAscendingComparator(columnIndex));
		fireTableDataChanged();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sortTableDataDescending(int columnIndex) {
		Arrays.sort(tableData, new ColumnDescendingComparator(columnIndex));
		fireTableDataChanged();
	}
	
	public String[][] getTableData() {
		return tableData;
	}


	public void addRandomRecord() {
		Random rand = new Random();
	    int upperbound = sampleList.size() - 1;
	    Record record = sampleList.get(rand.nextInt(upperbound));
	    addRecord(record);

		fireTableDataChanged();
	}
	
	public void addRecord(Record record) {
	    List<Record> tempList = new ArrayList<>();
	    tempList.add(record);
	    String[][] newArray = new String[tableData.length+1][tableHeaders.length];
	    String[][] newRecord = new String[0][0];
	    try {
	    	newRecord = UiController.getUserData().getExportData(tempList);
	    	
	    	for(int i = 0; i < tableData.length; ++i)
		    	  for(int j = 0; j < tableData[i].length; ++j)
		    		  newArray[i][j] = tableData[i][j];
		    
		    newArray[newArray.length - 1] = newRecord[0];
		    tableData = newArray;
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			UiController.handle(e);
		}

		fireTableDataChanged();
	}
	
	public void addRecordList(List<Record> recordList){
		
	    String[][] newArray = new String[tableData.length + recordList.size()][tableHeaders.length];

	    try {
	    	String[][] newRecordArray = UiController.getUserData().getExportData(recordList);
	    	
	    	int indexToAdd = 0;
	    	for(indexToAdd = 0; indexToAdd < tableData.length; ++indexToAdd)
		    	  for(int j = 0; j < tableData[indexToAdd].length; ++j)
		    		  newArray[indexToAdd][j] = tableData[indexToAdd][j];
		    
	    	for(int i = 0; i < newRecordArray.length; ++i) {
	    		 newArray[indexToAdd] = newRecordArray[i];
	    		 ++indexToAdd;
	    	}
	    	
		    tableData = newArray;
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			UiController.handle(e);
		}

		fireTableDataChanged();
	}
	
	public void removeRecord(int[] selectedRows) {
		if (selectedRows != null && selectedRows.length > 0) {
			String[][] newArray = new String[tableData.length - selectedRows.length][tableHeaders.length];
			int newArrayCounter = 0;
			for(int i = 0; i < tableData.length; ++i) {
				if(!contains(selectedRows, i))
					newArray[newArrayCounter++] = tableData[i];
			}
			
			tableData = newArray;
			fireTableDataChanged();
		}
	}
	
	public void autoIncrementField(int fieldIndex) {
		int listOrder = 0;
		for(String[] row : tableData)
			row[fieldIndex] = String.valueOf(++listOrder);
		
		fireTableDataChanged();
	}
	
	public void bulkEdit(int fieldIndex, String value) {

		for(String[] row : tableData)
			row[fieldIndex] = value;
		
		fireTableDataChanged();
	}
	
	public void moveRecordUp(int[] selectedRows) {
		if (selectedRows != null && selectedRows.length > 0) {
			if(selectedRows[0] != 0) { // if we aren't at the top of the list
				for(int i : selectedRows) {
					swap(tableData, i-1, i);
				}
			}
		}
		
		fireTableDataChanged();
	}
	
	public void moveRecordDown(int[] selectedRows) {
		if (selectedRows != null && selectedRows.length > 0) {
			if(selectedRows[selectedRows.length - 1] != tableData.length - 1) { // if we aren't at the bottom of the list
				for(int i = selectedRows.length - 1; i >= 0; --i) {
					swap(tableData, selectedRows[i]+1, selectedRows[i]);
				}
			}
		}
		
		fireTableDataChanged();
	}
	
	public static final <T> void swap (T[][] a, int i, int j) {
		  T[] t = a[i];
		  a[i] = a[j];
		  a[j] = t;
		}
	
	public static boolean contains(final int[] arr, final int key) {
	    return Arrays.stream(arr).anyMatch(i -> i == key);
	}
	
	class ColumnAscendingComparator<T extends Comparable<T>> implements Comparator<T[]> {
		  private final int column;

		  ColumnAscendingComparator(int column) { this.column = column; }

		  @Override public int compare(T[] a, T[] b) {
		    return a[column].compareTo(b[column]);
		  }
		}
	
	class ColumnDescendingComparator<T extends Comparable<T>> implements Comparator<T[]> {
		  private final int column;

		  ColumnDescendingComparator(int column) { this.column = column; }

		  @Override public int compare(T[] a, T[] b) {
		    return b[column].compareTo(a[column]);
		  }
		}

}