/**
 * Project: Dfuze
 * File: DedupeTableModel.java
 * Date: May 12, 2020
 * Time: 8:31:06 PM
 */
package com.mom.dfuze.ui.dedupe;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.ui.UiController;

/**
 * DedupeTableModel Class used to prepare data for users when viewing duplicates after running a deduplication
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class DedupeTableModel extends AbstractTableModel {

  private ArrayList<Record> dupes;
  private HashMap<Integer, ArrayList<Record>> dupeMap;
  private String[] selectedFields;
  private String[][] exportData;
  private String[] exportHeaders;
  private DedupeDialog dedupeDialog;

  public DedupeTableModel(DedupeDialog dedupeDialog, ArrayList<Record> dupes, String[] selectedFields) {
	dupeMap = new HashMap<>();
    this.dupes = dupes;
    this.selectedFields = selectedFields;
    exportData = UiController.getUserData().getDedupeExportData(dupes, selectedFields);
    exportHeaders = UiController.getUserData().getDedupeExportHeaders(selectedFields);
    this.dedupeDialog = dedupeDialog;
    
    for(Record dupe : dupes) {
    	if(!dupeMap.containsKey(dupe.getDupeGroupId())) {
    		ArrayList<Record> list = new ArrayList<>();
    		list.add(dupe);
    		dupeMap.put(dupe.getDupeGroupId(), list);
    	} else {
    		dupeMap.get(dupe.getDupeGroupId()).add(dupe);
    	}
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see javax.swing.table.TableModel#getRowCount()
   */
  @Override
  public int getRowCount() {
    return exportData.length;
  }

  /*
   * (non-Javadoc)
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  @Override
  public int getColumnCount() {
    return exportHeaders.length;
  }

  // the column header
  @Override
  public String getColumnName(int column) {
    return exportHeaders[column];
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
    return exportData[rowIndex][columnIndex];
  }

  /**
   * @param selectedRows
   */
  public void remove(int[] selectedRows) {
    if (selectedRows != null && selectedRows.length > 0) {
      for (int i : selectedRows) {
    	// update the dedupe dialog
    	  if(!dupes.get(i).getIsDupe()) {
    			  dedupeDialog.getLblParentDupesAmount().setText(String.valueOf(Integer.parseInt(dedupeDialog.getLblParentDupesAmount().getText()) - 1));
    			  dedupeDialog.getLblChildDupesAmount().setText(String.valueOf(Integer.parseInt(dedupeDialog.getLblChildDupesAmount().getText()) + 1));
    	  }
    	  
    	  dupes.get(i).setIsDupe(true);
      }
      update();
      fireTableDataChanged();
    }
  }

  /**
   * @param selectedRows
   */
  public void keep(int[] selectedRows) {
    if (selectedRows != null && selectedRows.length > 0) {
      for (int i : selectedRows) {
          // update the dedupe dialog
    	  if(dupes.get(i).getIsDupe()) {
    			  dedupeDialog.getLblParentDupesAmount().setText(String.valueOf(Integer.parseInt(dedupeDialog.getLblParentDupesAmount().getText()) + 1));
    			  dedupeDialog.getLblChildDupesAmount().setText(String.valueOf(Integer.parseInt(dedupeDialog.getLblChildDupesAmount().getText()) - 1));
    	  }
    	  
    	  dupes.get(i).setIsDupe(false);
      }

      update();
      fireTableDataChanged();
    }
  }

  /**
   * @param selectedRows
   */
  public void deflag(int[] selectedRows) {
    if (selectedRows != null && selectedRows.length > 0) {
    	
      for (int i = selectedRows.length - 1; i >= 0; --i) {

    		  if(!dupes.get(selectedRows[i]).getIsDupe())
    			  dedupeDialog.getLblParentDupesAmount().setText(String.valueOf(Integer.parseInt(dedupeDialog.getLblParentDupesAmount().getText()) - 1));
    		  else if(dupes.get(selectedRows[i]).getIsDupe())
    			  dedupeDialog.getLblChildDupesAmount().setText(String.valueOf(Integer.parseInt(dedupeDialog.getLblChildDupesAmount().getText()) - 1));

    	  dedupeDialog.getLblTotalDupesAmount().setText(String.valueOf(Integer.parseInt(dedupeDialog.getLblTotalDupesAmount().getText()) - 1));
    	  
    	  Record removedRecord = dupes.remove(selectedRows[i]);
    	  removedRecord.setIsDupe(false);
    	  
    	  if(dupeMap.containsKey(removedRecord.getDupeGroupId())) {
    		  ArrayList<Record> dupeGroup = dupeMap.get(removedRecord.getDupeGroupId());
    		  int newGroupSize = removedRecord.getDupeGroupSize()  - 1;
    		  
    		  for(Record dupe : dupeGroup)
    			  dupe.setDupeGroupSize(newGroupSize);
    	  }
    	  
    	  removedRecord.setDupeGroupId(0);
    	  removedRecord.setDupeGroupSize(0);
    	  
    	  for(Record record: UiController.getUserData().getRecordList()) {
    		  if(removedRecord.getDfId() == record.getDfId()) {
    			  record.setDupeGroupId(0);
    			  record.setDupeGroupSize(0);
    			  record.setIsDupe(false);
    			  break;
    		  }
    	  }
      }
      
      update();
      fireTableDataChanged();
    }
  }

  public void update() {
    exportData = UiController.getUserData().getDedupeExportData(dupes, selectedFields);
    exportHeaders = UiController.getUserData().getDedupeExportHeaders(selectedFields);
  }
  

}