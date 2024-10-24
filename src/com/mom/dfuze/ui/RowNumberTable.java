package com.mom.dfuze.ui;

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.mom.dfuze.data.Theme;


/*
 *	Use a JTable as a renderer for row numbers of a given main table.
 *  This table must be added to the row header of the scrollpane that
 *  contains the main table.
 */
@SuppressWarnings("serial")
public class RowNumberTable extends JTable
	implements ChangeListener, PropertyChangeListener, TableModelListener
{
	private JTable main;
	private String[] values;

	public RowNumberTable(JTable table, String[] values)
	{
		main = table;
		main.addPropertyChangeListener( this );
		main.getModel().addTableModelListener( this );

		setFocusable( false );
		setAutoCreateColumnsFromModel( false );
		setSelectionModel( main.getSelectionModel() );

		TableColumn column = new TableColumn();
		column.setHeaderValue(" ");
		addColumn( column );
		column.setCellRenderer(new RowNumberRenderer());

		// Change the width to fit all text
		int longestValueWidth = 50;
		
		for(String value : values) {
			FontMetrics fm = table.getFontMetrics(table.getFont());
	        int charWidth = fm.stringWidth(value);
	        int valueWidth = (value.length() + charWidth + 4) + table.getIntercellSpacing().width;
	        if(valueWidth > longestValueWidth)
	        	longestValueWidth = valueWidth;
		}

		getColumnModel().getColumn(0).setPreferredWidth(longestValueWidth);
		setPreferredScrollableViewportSize(getPreferredSize());
		
		this.values = values;

	}

	@Override
	public void addNotify()
	{
		super.addNotify();

		Container c = getParent();

		//  Keep scrolling of the row table in sync with the main table.

		if (c instanceof JViewport)
		{
			JViewport viewport = (JViewport)c;
			viewport.addChangeListener( this );
		}
	}

	/*
	 *  Delegate method to main table
	 */
	@Override
	public int getRowCount()
	{
		return main.getRowCount();
	}

	@Override
	public int getRowHeight(int row)
	{
		int rowHeight = main.getRowHeight(row);

		if (rowHeight != super.getRowHeight(row))
		{
			super.setRowHeight(row, rowHeight);
		}

		return rowHeight;
	}

	/*
	 *  No model is being used for this table so just use the row number
	 *  as the value of the cell.
	 */
	@Override
	public Object getValueAt(int row, int column)
	{
		return values[row];
		//return Integer.toString(row + 1);
	}

	/*
	 *  Don't edit data in the main TableModel by mistake
	 */
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	/*
	 *  Do nothing since the table ignores the model
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {}
//
//  Implement the ChangeListener
//
	public void stateChanged(ChangeEvent e)
	{
		//  Keep the scrolling of the row table in sync with main table

		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane)viewport.getParent();
		scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	}
//
//  Implement the PropertyChangeListener
//
	public void propertyChange(PropertyChangeEvent e)
	{
		//  Keep the row table in sync with the main table

		if ("selectionModel".equals(e.getPropertyName()))
		{
			setSelectionModel( main.getSelectionModel() );
		}

		if ("rowHeight".equals(e.getPropertyName()))
		{
			repaint();
		}

		if ("model".equals(e.getPropertyName()))
		{
			main.getModel().addTableModelListener( this );
			revalidate();
		}
	}

//
//  Implement the TableModelListener
//
	@Override
	public void tableChanged(TableModelEvent e)
	{
		revalidate();
	}

	/*
	 *  Attempt to mimic the table header renderer
	 */
	private static class RowNumberRenderer extends DefaultTableCellRenderer
	{
		public RowNumberRenderer()
		{
			setHorizontalAlignment(JLabel.RIGHT);
		}

		public java.awt.Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (table != null)
			{
				JTableHeader header = table.getTableHeader();

				if (header != null)
				{
					setForeground(Color.decode("#ffffff"));//header.getForeground()
					setBackground(Theme.TITLE_COLOR);//header.getBackground()
					setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));//header.getFont()
				}
			}

			if (isSelected)
			{
				//setFont( getFont().deriveFont(Font.BOLD) );
			}

			setText((value == null) ? "" : value.toString());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			return this;
		}
	}
}