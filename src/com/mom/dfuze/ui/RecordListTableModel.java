package com.mom.dfuze.ui;
import javax.swing.table.AbstractTableModel;


@SuppressWarnings("serial")
public class RecordListTableModel extends AbstractTableModel {

	private String[][] data;
	private String[] headers;
	
	public RecordListTableModel(String[][] data, String[] headers) {
		this.data = data;
		this.headers = headers;
	}
	
    public boolean isCellEditable(int row, int column){  
        return false;  
    }

	@Override
	public int getRowCount() {
		if(data != null)
			return data.length;
		else
			return 0;
	}
	

		@Override
		public String getColumnName(int column) {
			return headers[column];
		}

	@Override
	public int getColumnCount() {
		if(headers != null)
			return headers.length;
		else
			return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return data[rowIndex][columnIndex];
	}
	
	public String[][] getData(){
		return data;
	}

}