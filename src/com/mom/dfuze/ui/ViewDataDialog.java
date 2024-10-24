package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


import com.mom.dfuze.data.RowFilterUtil;
import com.mom.dfuze.data.Theme;

import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class ViewDataDialog  extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private JButton btnExportSelectedRows;
	private JTextField textFieldSearch;
	private RecordListTableModel tableModel;
	private JButton btnHelp;
	
	public ViewDataDialog(JFrame frame, String[][] data, String[] headers) {
		setResizable(false);
		getContentPane().setFont(new Font("Segoe UI Semilight", Font.PLAIN, 12));

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("View Data");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 245));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		contentPanel.setLayout(new MigLayout("insets 10 28 28 28, gap 0", "[144px:n:144px][184px:n:184px][184px:n:184px][184px:n:184px][184px:n:184px][14px:n:14px][184px:n:184px]", "[36px:n:36px][28px:n:28px][28px:n:28px][28px:n:28px,grow][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px]"));
		
		btnExportSelectedRows = new JButton("Export Selected Rows");
		btnExportSelectedRows.addActionListener(new ExportSelectedRowsButtonHandler());
		
				JLabel lblNewLabel = new JLabel("Regex pattern search");
				lblNewLabel.setForeground(Theme.TITLE_COLOR);
				lblNewLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
				contentPanel.add(lblNewLabel, "cell 0 0,alignx left,aligny center");
		
		btnHelp = new JButton("?");
		btnHelp.putClientProperty( "JButton.buttonType", "help" );
		btnHelp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnHelp.addActionListener(new HelpButtonHandler());
		contentPanel.add(btnHelp, "cell 1 0");
		btnExportSelectedRows.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		contentPanel.add(btnExportSelectedRows, "cell 6 1,grow");
		
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, "cell 0 3 7 18,grow");

		tableModel = new RecordListTableModel(data, headers);
		table = new JTable(tableModel);

		
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);

		
		textFieldSearch = RowFilterUtil.createRowFilter(table);
		
		contentPanel.add(textFieldSearch, "cell 0 1 5 1,grow");
		textFieldSearch.setColumns(10);
		
		RendererHighlighted renderer = new RendererHighlighted(textFieldSearch);
		table.setDefaultRenderer(Object.class, renderer);
		table.setShowGrid(true);
		table.setGridColor(Color.BLACK);
		
		
		pack();
	    setLocationRelativeTo(frame);
	    
	    textFieldSearch.requestFocus();
		//getContentPane().requestFocusInWindow();

	}
	
	public void setTextFieldSearch(String text) {
		textFieldSearch.setText(text);
	}
	
	private class ExportSelectedRowsButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnExportSelectedRows) {

					if(table.getSelectedRowCount() == 0) {
						JOptionPane.showMessageDialog(ViewDataDialog.this, "No rows have been selected.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					String[][] newData = new String[table.getSelectedRows().length][table.getColumnCount()];
					
					int counter = -1;
					for(int i : table.getSelectedRows())
						newData[++counter] = tableModel.getData()[table.convertRowIndexToModel(i)];


					ExportDataDialog exportDataDialog = new ExportDataDialog(ViewDataDialog.this, UiController.getUserData().getExportHeaders(), newData);
					exportDataDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					exportDataDialog.setModalityType(ModalityType.APPLICATION_MODAL);
					exportDataDialog.setVisible(true);

				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}
	
	public void resizeTable(JTable table) {

		for (int column = 0; column < table.getColumnCount(); column++) {
			TableColumn tableColumn = table.getColumnModel().getColumn(column);
			FontMetrics fm = table.getFontMetrics(table.getFont());
			int charWidth = fm.stringWidth(table.getModel().getColumnName(column));
			int colHeaderWidth = (table.getModel().getColumnName(column).length() + charWidth + 8) + table.getIntercellSpacing().width;
			int preferredWidth = tableColumn.getMinWidth();
			int maxWidth = tableColumn.getMaxWidth();

			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
				Component c = table.prepareRenderer(cellRenderer, row, column);
				int width = c.getPreferredSize().width + table.getIntercellSpacing().width + 8;
				preferredWidth = Math.max(preferredWidth, width);
				table.prepareRenderer(cellRenderer, row, column);
				// We've exceeded the maximum width, no need to check other rows

				if (preferredWidth >= maxWidth) {
					preferredWidth = maxWidth;
					break;
				}
			}

			tableColumn.setPreferredWidth((preferredWidth > colHeaderWidth) ? preferredWidth : colHeaderWidth);

		}
		table.repaint();
	}
	
	private class HelpButtonHandler implements ActionListener {
		private HelpButtonHandler() {}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnHelp) {
				JEditorPane editorPane = new JEditorPane("text/html", 
						"<html><body><pre><b>A regular expression is a sequence of characters that forms a search pattern.\r\n"
						+ "When you search for data in a text, you can use this search pattern to describe what you are searching for.</b>\r\n"
						+ "\r\n"
						+ "<b>Copy & paste the example below to find digits from the start of a line until a space or the end of the line:</b>\r\n"
						+ "^\\d+(?=\\s|$)"
						+ "\r\n\r\n"
						+ "<b>Brackets are used to find a range of characters:</b>\r\n"
						+ "<b>Expression	Description</b>\r\n"
						+ "[abc]		Find one character from the options between the brackets\r\n"
						+ "[^abc]		Find one character NOT between the brackets\r\n"
						+ "[0-9]		Find one character from the range 0 to 9\r\n"
						+ "\r\n"
						+ "<b>Metacharacters are characters with a special meaning:</b>\r\n"
						+ "<b>Metacharacter	Description</b>\r\n"
						+ "|		Find a match for any one of the patterns separated by | as in: cat|dog|fish\r\n"
						+ ".		Find just one instance of any character\r\n"
						+ "^		Finds a match as the beginning of a string as in: ^Hello\r\n"
						+ "$		Finds a match at the end of the string as in: World$\r\n"
						+ "\\d		Find a digit\r\n"
						+ "\\s		Find a whitespace character\r\n"
						+ "\\b		Find a match at the beginning of a word like this: \\bWORD, or at the end of a word like this: WORD\\b\r\n"
						+ "\\uxxxx		Find the Unicode character specified by the hexadecimal number xxxx\r\n"
						+ "\r\n"
						+ "<b>Quantifiers define quantities:</b>\r\n"
						+ "<b>Quantifier	Description</b>\r\n"
						+ "n+		Matches any string that contains at least one n\r\n"
						+ "n*		Matches any string that contains zero or more occurrences of n\r\n"
						+ "n?		Matches any string that contains zero or one occurrences of n\r\n"
						+ "n{x}		Matches any string that contains a sequence of X n's\r\n"
						+ "n{x,y}		Matches any string that contains a sequence of X to Y n's\r\n"
						+ "n{x,}		Matches any string that contains a sequence of at least X n's</pre></body></html>");
				editorPane.setEditable(false);
				JOptionPane.showMessageDialog(ViewDataDialog.this, editorPane, "Regex pattern searching", 1);
			} 
		}
	}
}
