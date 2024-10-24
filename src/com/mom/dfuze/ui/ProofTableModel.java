/**
 * Project: Dfuze
 * File: DedupeTableModel.java
 * Date: May 12, 2020
 * Time: 8:31:06 PM
 */
package com.mom.dfuze.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;

/**
 * DedupeTableModel Class used to prepare data for users when viewing duplicates after running a deduplication
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class ProofTableModel extends AbstractTableModel {

	private List<Record> proofList;
	private Set<Integer> addedIDs;
	private String[][] tableData;
	private String[] tableHeaders;

	public ProofTableModel() {
		tableHeaders = UiController.getUserData().getExportHeaders();
		proofList = new ArrayList<>();
		tableData = new String[0][0];
		addedIDs = new HashSet<>();
	}
	
	public Set<Integer> getAddedIDs(){
		return addedIDs;
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
	

	public void sortTableDataAscending(boolean isDfField, String fieldName, int fieldIndex) {
		if(isDfField)
			Collections.sort(proofList, new RecordSorters.CompareByFieldAsc(fieldName));
		else
			Collections.sort(proofList, new RecordSorters.CompareByInFieldAsc(fieldIndex));
		
		update();
	}
	
	public void sortTableDataDescending(boolean isDfField, String fieldName, int fieldIndex) {
		if(isDfField)
			Collections.sort(proofList, new RecordSorters.CompareByFieldDesc(fieldName));
		else
			Collections.sort(proofList, new RecordSorters.CompareByInFieldDesc(fieldIndex));
		
		update();
	}
	
	public String[][] getTableData() {
		return tableData;
	}


	public boolean addRandomRecord(){
		Random rand = new Random();
		List<Record> recordList = UiController.getUserData().getRecordList();
	    int upperbound = recordList.size();
	    Record record = recordList.get(rand.nextInt(upperbound));
	    
	    if(addedIDs.size() != recordList.size()) {
	    	while(!addedIDs.add(record.getDfId()))
	    		record = recordList.get(rand.nextInt(upperbound));

	    	proofList.add(record);
	    	update();
	    	return true;
	    } else {
	    	return false;
	    }

	    
	}
	
	public boolean addRecord(Record record) {
		if(!addedIDs.add(record.getDfId()))
			return false;
		
		proofList.add(record);
		update();
		return true;
	}
	
	public void addRecordList(List<Record> recordList) {
		for(Record record: recordList)
			if(addedIDs.add(record.getDfId()))
				proofList.add(record);
		
		update();
	}
	
	
	public void deleteRecord(int[] selectedRows) {
		if (selectedRows != null && selectedRows.length > 0) {
			for(int i = selectedRows.length - 1; i >= 0; --i)
				addedIDs.remove(proofList.remove(selectedRows[i]).getDfId());

			update();
		}
	}
	
	public void moveRecordUp(int[] selectedRows) {
		if (selectedRows != null && selectedRows.length > 0) {
			if(selectedRows[0] != 0) { // if we aren't at the top of the list
				for(int i : selectedRows) {
					Collections.swap(proofList, i-1, i);
				}
			}
		}
		
		update();
	}
	
	public void moveRecordDown(int[] selectedRows) {
		if (selectedRows != null && selectedRows.length > 0) {
			if(selectedRows[selectedRows.length - 1] != tableData.length - 1) { // if we aren't at the bottom of the list
				for(int i = selectedRows.length - 1; i >= 0; --i) {
					Collections.swap(proofList, selectedRows[i]+1, selectedRows[i]);
				}
			}
		}
		
		update();
	}
	
	public void update() {
		try {
			tableData = UiController.getUserData().getExportData(proofList);
		} catch (Exception e) {
			UiController.handle(e);
		}
		fireTableDataChanged();
	  }

}