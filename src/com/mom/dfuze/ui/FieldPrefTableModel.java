/**
 * Project: Dfuze
 * File: DedupeTableModel.java
 * Date: May 12, 2020
 * Time: 8:31:06 PM
 */
package com.mom.dfuze.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.mom.dfuze.data.FieldPref;
import com.mom.dfuze.data.FieldPrefField;

/**
 * DedupeTableModel Class used to prepare data for users when viewing duplicates after running a deduplication
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class FieldPrefTableModel extends AbstractTableModel {

	private String[][] tableData;
	private String[] tableHeaders;

	public FieldPrefTableModel(ArrayList<FieldPrefField> fpfList) {

		tableHeaders = FieldPref.FIELD_PREFS_HEADERS;
		String[][] data = new String[fpfList.size()][FieldPref.FIELD_PREFS_HEADERS.length];

		int counter = 0;
		for(FieldPrefField fpf : fpfList) {
			data[counter][FieldPref.FIELD_PREFS_DFUZE_INDEX] = fpf.getDfuzeFieldName();
			data[counter][FieldPref.FIELD_PREFS_USER_INDEX] = fpf.getUserFieldName();
			++counter;
		}
		
		tableData = data;
	}
	
	@Override
	public void setValueAt(Object newVal, int row, int col) {
		tableData[row][col] = (String) newVal;
        fireTableCellUpdated(row, col);
    }

	@Override
	public boolean isCellEditable(int row, int column) {
		if(column == 1)
			return true;
		else
			return false;
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
	
	
	public String[][] getTableData() {
		return tableData;
	}


	public void update(ArrayList<FieldPrefField> fpfList) {

		String[][] data = new String[fpfList.size()][FieldPref.FIELD_PREFS_HEADERS.length];

		int counter = 0;
		for(FieldPrefField fpf : fpfList) {
			data[counter][FieldPref.FIELD_PREFS_DFUZE_INDEX] = fpf.getDfuzeFieldName();
			data[counter][FieldPref.FIELD_PREFS_USER_INDEX] = fpf.getUserFieldName();
			++counter;
		}
		
		tableData = data;

		fireTableDataChanged();
	}
	


}