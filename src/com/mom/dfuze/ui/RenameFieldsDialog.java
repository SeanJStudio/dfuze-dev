package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.mom.dfuze.data.FieldPref;
import com.mom.dfuze.data.FieldPrefField;
import com.mom.dfuze.data.Theme;

import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;

@SuppressWarnings("serial")
public class RenameFieldsDialog  extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private JButton btnSave;
	private FieldPrefTableModel tableModel;
	private JLabel lblcustomizeYourOwn;
	private JButton btnReset;
	private JSeparator separator;
	
	public RenameFieldsDialog(JFrame frame, ArrayList<FieldPrefField> fpfList) {
		setResizable(false);
		getContentPane().setFont(new Font("Segoe UI Semilight", Font.PLAIN, 12));

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("Rename Fields");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 245));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.SOUTH);
		contentPanel.setLayout(new MigLayout("insets 10 28 28 28, gap 0", "[93px:n:93px][28px:n:28px][186px:n:186px][28px:n:28px][70px:n:70px][186px:n:186px]", "[14px:n:14px][28px:n:28px][28px:n:28px][28px:n:28px,grow][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px]"));
		
				JLabel lblTitle = new JLabel("Rename fields");
				lblTitle.setForeground(Theme.TITLE_COLOR);
				lblTitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
				contentPanel.add(lblTitle, "cell 4 1,alignx left,aligny center");
		
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, "cell 0 1 3 9,grow");

		tableModel = new FieldPrefTableModel(fpfList);
		table = new JTable(tableModel);

		
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table);


		table.setShowGrid(true);
		table.setGridColor(Color.BLACK);
		
		lblcustomizeYourOwn = new JLabel("<html><br>Use your own names for the fields Dfuze appends to exported data.<br><br>To do this, click on the cell next to the field you would like to rename and type your new name.<br><br>Click save after you're done.</html>");
		lblcustomizeYourOwn.setVerticalAlignment(SwingConstants.TOP);
		lblcustomizeYourOwn.setForeground(Color.BLACK);
		lblcustomizeYourOwn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblcustomizeYourOwn, "cell 4 2 2 5,aligny top");
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(new SaveButtonHandler());
		
		separator = new JSeparator();
		contentPanel.add(separator, "cell 4 7 2 2,growx,aligny center");
		
		btnReset = new JButton("Reset");
		btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		contentPanel.add(btnReset, "cell 4 9,alignx left,growy");
		
		btnSave.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		contentPanel.add(btnSave, "cell 5 9,grow");
		
		
		pack();
	    setLocationRelativeTo(frame);
	   

	}
	

	
	private class SaveButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnSave) {
					ArrayList<FieldPrefField> fpfList = new ArrayList<>();
					
					for(String[] row : tableModel.getTableData()) {
						FieldPrefField fpf = new FieldPrefField(row[FieldPref.FIELD_PREFS_DFUZE_INDEX], row[FieldPref.FIELD_PREFS_USER_INDEX]);
						fpfList.add(fpf);
					}
					
					FieldPref.updateFieldPrefs(fpfList);

					// Show message that update was successful

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

}
