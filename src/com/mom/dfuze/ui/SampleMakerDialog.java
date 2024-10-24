/**
 * Project: Dfuze
 * File: DedupeViewDialog.java
 * Date: May 10, 2020
 * Time: 8:54:37 PM
 */
package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.Theme;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;

import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;


/**
 * SampleMakerDialog to allow users to make sample files
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class SampleMakerDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private final JButton btnBulkEdit = new JButton("Bulk-Edit Field (Alt+E)");
	private final JButton btnRandom = new JButton("Add Random (Alt+R)");
	private final JButton btnRemoveRecord = new JButton("Delete Record (Alt+D)");
	private final JLabel lblNewLabel = new JLabel("<html>Chose a field from the dropdown below and click \"Add Unique\" to find and add unqiue records to the sample file.</html>");
	private final JComboBox<String> comboBoxFindUniqueRecords;
	private final JButton btnUnique = new JButton("Add Unique (Alt+U)");
	private final JButton btnSequence = new JButton("Sequence Field (Alt+S)");
	private final JSeparator separator_1 = new JSeparator();
	private final JSeparator separator_1_1 = new JSeparator();
	private final JButton btnMakeSampleFile = new JButton("Make Sample File");
	private final JButton btnMoveRecordUp = new JButton("Move Records Up");
	private final JButton btnMoveRecordDown = new JButton("Move Records Down");

	private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXX";

	private SampleTableModel sampleTableModel;

	private Set<String> sortedFields = new HashSet<>();

	private int clickedColumn = -1;

	private final JProgressBar progressBar = new JProgressBar();

	private Thread processThread;
	private final JLabel lblRecordNumber = new JLabel("0 records in file");

	public SampleMakerDialog(JFrame frame) {
		setResizable(false);
		getContentPane().setFont(new Font("Segoe UI Semilight", Font.PLAIN, 12));

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(802, 608);
		setLocationRelativeTo(frame);

		setTitle("Sample Maker");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 245));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		contentPanel.setLayout(new MigLayout("insets 10 28 20 28, gap 0", "[184px:n:184px][14px:n:14px][14px:n:14px][174px:n:174px][36px:n:36px][61.33px:n:61.33px][61.33px:n:61.33px][61.33px:n:61.33px][36px:n:36px][184px:n:184px]", "[12px:n:12px][28px:n:28px][14px:n:14px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][14px:n:14px][28px:n:28px][28px:n:28px][28px:n:28px]"));
		setSampleTableModel();
		table = new JTable(sampleTableModel);
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent e) {
				table.setRowSelectionAllowed(true);
				table.setColumnSelectionAllowed(false);

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});


		table.getTableHeader().addMouseListener(new SelectColumnHandler());
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setRowHeight(32);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setShowGrid(true);

		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		separator_1.setOrientation(SwingConstants.VERTICAL);

		contentPanel.add(separator_1, "cell 4 1 1 3,alignx center,growy");
		btnRandom.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnRandom.addActionListener(new AddRandomRecordButtonHandler());

		contentPanel.add(btnRandom, "cell 5 1 3 1,grow");
		separator_1_1.setOrientation(SwingConstants.VERTICAL);

		contentPanel.add(separator_1_1, "cell 8 1 1 3,alignx center,growy");
		lblNewLabel.setForeground(Color.BLACK);
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		contentPanel.add(lblNewLabel, "cell 0 1 4 1,alignx center,aligny center");
		btnBulkEdit.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnBulkEdit.addActionListener(new BulkEditButtonHandler());

		contentPanel.add(btnBulkEdit, "cell 9 1,grow");
		btnSequence.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnSequence.addActionListener(new AutoIncrementFieldButtonHandler());

		contentPanel.add(btnSequence, "cell 9 3,grow");

		comboBoxFindUniqueRecords = new JComboBox<>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxFindUniqueRecords.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxFindUniqueRecords.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxFindUniqueRecords.setSelectedIndex(-1);
		contentPanel.add(comboBoxFindUniqueRecords, "cell 0 3,grow");
		btnUnique.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnUnique.addActionListener(new AddUniqueRecordButtonHandler());

		contentPanel.add(btnUnique, "cell 2 3 2 1,grow");
		btnRemoveRecord.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnRemoveRecord.addActionListener(new RemoveRecordButtonHandler());

		contentPanel.add(btnRemoveRecord, "cell 5 3 3 1,grow");
		contentPanel.add(scrollPane, "cell 0 5 10 11,grow");
		scrollPane.setViewportView(table);
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		contentPanel.add(progressBar, "cell 0 17 10 1,grow");
		btnMakeSampleFile.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMakeSampleFile.addActionListener(new MakeSampleFileButtonHandler());
		btnMoveRecordUp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMoveRecordUp.addActionListener(new MoveRecordUpButtonHandler());

		contentPanel.add(btnMoveRecordUp, "cell 0 19,grow");
		btnMoveRecordDown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMoveRecordDown.addActionListener(new MoveRecordDownButtonHandler());

		contentPanel.add(btnMoveRecordDown, "cell 3 19,grow");
		lblRecordNumber.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblRecordNumber.setForeground(Theme.TITLE_COLOR);

		contentPanel.add(lblRecordNumber, "cell 5 19 3 1,alignx right");

		contentPanel.add(btnMakeSampleFile, "cell 9 19,grow");

		clickOnKey( btnUnique, "AddUniqueRecordButtonHandler", KeyEvent.VK_U );
		clickOnKey( btnRandom, "AddRandomRecordButtonHandler", KeyEvent.VK_R );
		clickOnKey( btnRemoveRecord, "RemoveRecordButtonHandler", KeyEvent.VK_D );
		clickOnKey( btnBulkEdit, "BulkEditButtonHandler", KeyEvent.VK_E );
		clickOnKey( btnSequence, "AutoIncrementFieldButtonHandler", KeyEvent.VK_S );

		setToolTips();
		
		pack();
		getContentPane().requestFocusInWindow();
	}
	
	private void disableUi(){
		table.setEnabled(false);
		btnRandom.setEnabled(false);
		btnRemoveRecord.setEnabled(false);
		btnBulkEdit.setEnabled(false);
		btnSequence.setEnabled(false);
		btnUnique.setEnabled(false);
		btnMakeSampleFile.setEnabled(false);
		btnMoveRecordUp.setEnabled(false);
		btnMoveRecordDown.setEnabled(false);
	}
	
	private void enableUi(){
		table.setEnabled(true);
		btnRandom.setEnabled(true);
		btnRemoveRecord.setEnabled(true);
		btnBulkEdit.setEnabled(true);
		btnSequence.setEnabled(true);
		btnUnique.setEnabled(true);
		btnMakeSampleFile.setEnabled(true);
		btnMoveRecordUp.setEnabled(true);
		btnMoveRecordDown.setEnabled(true);
	}

	private class AddRandomRecordButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnRandom) {
				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
						stopEditingTable();
						disableUi();
						sampleTableModel.addRandomRecord();
						resizeTable(table);
						
						updateRecordNum();
						} catch(Exception e) {
							UiController.handle(e);
						} finally {
							enableUi();
						}
					}
				};

				processThread = new Thread(runner, "Code Executer");
				processThread.start();
					
			}
		}
	}

	private class AddUniqueRecordButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnUnique) {
				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
						disableUi();
						progressBar.setValue(0);
						progressBar.setMaximum(UiController.getUserData().getRecordList().size());
						if(comboBoxFindUniqueRecords.getSelectedIndex() > -1) {
							stopEditingTable();
							boolean uniqueIsDf = Common.isDfField(comboBoxFindUniqueRecords.getSelectedItem().toString());

							HashSet<String> uniqueValues = new HashSet<>();
							List<Record> recordsToAdd = new ArrayList<>();
							
							// randomize the data
							Collections.shuffle(UiController.getUserData().getRecordList());

							if(uniqueIsDf) {
								for(Record record : UiController.getUserData().getRecordList()) {
									String value = UserData.getRecordFieldByName(comboBoxFindUniqueRecords.getSelectedItem().toString(), record);
									if(uniqueValues.add(value))
										recordsToAdd.add(record);

									progressBar.setValue(progressBar.getValue() + 1);
								}
							} else {
								for(Record record : UiController.getUserData().getRecordList()) {
									String value = record.getDfInData()[comboBoxFindUniqueRecords.getSelectedIndex()];
									if(uniqueValues.add(value))
										recordsToAdd.add(record);

									progressBar.setValue(progressBar.getValue() + 1);
								}
							}

							if(recordsToAdd.size() > 0)
								sampleTableModel.addRecordList(recordsToAdd);
							else
								throw new Exception("All the records have been added already.");
							
							Thread.sleep(100);
							
							resizeTable(table);
							
							updateRecordNum();
						} else {
							UiController.displayMessage("Error", "No field was selected.\nChoose a field from the dropdown on the left and try again.", JOptionPane.ERROR_MESSAGE);
							//JOptionPane.showMessageDialog(SampleMakerDialog.this, "No field was selected.\nChoose a field from the dropdown on the left and try again.", "Error", JOptionPane.ERROR_MESSAGE);
						}
						} catch(Exception e) {
							UiController.handle(e);
						} finally {
							Collections.sort(UiController.getUserData().getRecordList(), (Comparator<? super Record>)new RecordSorters.CompareByDfId());
							enableUi();
						}
					}
				};

				processThread = new Thread(runner, "Code Executer");
				processThread.start();

			}

		}
	}

	private class RemoveRecordButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnRemoveRecord) {
				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
						stopEditingTable();
						disableUi();
						sampleTableModel.removeRecord(table.getSelectedRows());
						resizeTable(table);
						
						updateRecordNum();
						} catch(Exception e) {
							UiController.handle(e);
						} finally {
							enableUi();
						}
					}
				};

				processThread = new Thread(runner, "Code Executer");
				processThread.start();
			}

		}
	}

	private class AutoIncrementFieldButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnSequence) {
				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
						stopEditingTable();
						disableUi();
						if(table.getColumnSelectionAllowed()) {
							sampleTableModel.autoIncrementField(clickedColumn);
							resizeTable(table);
							
						} else {
							UiController.displayMessage("Error", "No column was selected.\nClick on a column header and try again.", JOptionPane.ERROR_MESSAGE);
							//JOptionPane.showMessageDialog(SampleMakerDialog.this, "No column was selected.\nClick on a column header and try again.", "Error", JOptionPane.ERROR_MESSAGE);
						}
						} catch(Exception e) {
							UiController.handle(e);
						} finally {
							enableUi();
						}
					}
				};

				processThread = new Thread(runner, "Code Executer");
				processThread.start();

			}
		}
	}

	private class BulkEditButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnBulkEdit) {
				stopEditingTable();
				disableUi();
				if(table.getColumnSelectionAllowed()) {
					UserInputDialog userInputDialog = new UserInputDialog(UiController.getMainFrame(), "Enter the value for " + table.getColumnName(clickedColumn));
					userInputDialog.setVisible(true);
					if(userInputDialog.getIsNextPressed()) {
						sampleTableModel.bulkEdit(clickedColumn, userInputDialog.getUserInput());
						resizeTable(table);
						
					}
				} else {
					UiController.displayMessage("Error", "No column was selected.\nClick on a column header and try again.", JOptionPane.ERROR_MESSAGE);
					//JOptionPane.showMessageDialog(SampleMakerDialog.this, "No column was selected.\nClick on a column header and try again.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				enableUi();
			}
		}
	}

	private class MoveRecordUpButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnMoveRecordUp) {
				int[] selectedRows = table.getSelectedRows();
				stopEditingTable();
				sampleTableModel.moveRecordUp(table.getSelectedRows());
				if(selectedRows.length > 0 && selectedRows[0] != 0) {
					for(int i : selectedRows) {
						int selection = i - 1;
						table.getSelectionModel().addSelectionInterval(selection, selection);
					}
				} else {
					for(int i : selectedRows) {
						int selection = i;
						table.getSelectionModel().addSelectionInterval(selection, selection);
					}
				}

			}
		}
	}

	private class MoveRecordDownButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnMoveRecordDown) {
				int[] selectedRows = table.getSelectedRows();
				stopEditingTable();
				sampleTableModel.moveRecordDown(table.getSelectedRows());
				if(selectedRows.length > 0 && selectedRows[selectedRows.length - 1] != table.getRowCount() - 1) {
					for(int i : selectedRows) {
						int selection = i + 1;
						table.getSelectionModel().addSelectionInterval(selection, selection);
					}
				} else {
					for(int i : selectedRows) {
						int selection = i;
						table.getSelectionModel().addSelectionInterval(selection, selection);
					}
				}
			}
		}
	}

	private class SelectColumnHandler implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if(table.getRowCount() > 0) {
				stopEditingTable();
				int clickedIndex = table.convertColumnIndexToModel(table.columnAtPoint(e.getPoint()));
				clickedColumn = clickedIndex;
				if (e.getClickCount() == 2) {
					if(sortedFields.add(table.getColumnName(clickedIndex))) {
						sampleTableModel.sortTableDataAscending(clickedIndex);
					} else {
						sampleTableModel.sortTableDataDescending(clickedIndex);
						sortedFields.remove(table.getColumnName(clickedIndex));
					}
				}

				table.setRowSelectionAllowed(false);
				table.setColumnSelectionAllowed(true);

				table.setColumnSelectionInterval(clickedIndex, clickedIndex); //selects which column will have all its rows selected
				table.setRowSelectionInterval(0, table.getRowCount() - 1); //once column has been selected, select all rows from 0 to the end of that column
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}

	private class MakeSampleFileButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnMakeSampleFile) {

					if(sampleTableModel.getRowCount() == 0) {
						UiController.displayMessage("Error", "No records have been added to the sample file yet.", JOptionPane.ERROR_MESSAGE);
						//JOptionPane.showMessageDialog(SampleMakerDialog.this, "No records have been added to the sample file yet.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					ExportDataDialog exportDataDialog = new ExportDataDialog(SampleMakerDialog.this, UiController.getUserData().getExportHeaders(), sampleTableModel.getTableData());
					exportDataDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					exportDataDialog.setModalityType(ModalityType.APPLICATION_MODAL);
					exportDataDialog.setVisible(true);

				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}

	public void setSampleTableModel() {
		sampleTableModel = new SampleTableModel(UiController.getUserData().getRecordList());
	}

	public void resizeTable(JTable table) {
		progressBar.setValue(0);
		int progressMax = (table.getRowCount() > 0 ? table.getRowCount() * table.getColumnCount() : 1);
		progressBar.setMaximum(progressMax);

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
					progressBar.setValue(progressBar.getValue() + (table.getRowCount() - (row + 1)));
					break;
				}
				progressBar.setValue(progressBar.getValue() + 1);
			}

			tableColumn.setPreferredWidth((preferredWidth > colHeaderWidth) ? preferredWidth : colHeaderWidth);

		}

		progressBar.setValue(progressBar.getMaximum());
		
		table.repaint();
	}

	//https://stackoverflow.com/questions/4240740/shortcut-key-for-jbutton-without-using-alt-key
	public static void clickOnKey(final AbstractButton button, String actionName, int key) {
		button.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke( key, InputEvent.ALT_DOWN_MASK ), actionName);

		button.getActionMap().put(actionName, new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				button.doClick();
			}
		} );
	}

	// Fix for when user is editing a cell and has chosen a command
	public void stopEditingTable() {
		if (table.isEditing()) 
			table.getCellEditor().cancelCellEditing();
	}

	private void updateRecordNum() {
		lblRecordNumber.setText(String.format("%d records in file", table.getRowCount()));
	}
	
	private void setToolTips() {
		btnBulkEdit.setToolTipText("Bulk edits every record value in a selected column.");
		btnSequence.setToolTipText("Adds a sequence number to every record in a selected column.");
		btnRandom.setToolTipText("Adds a random record.");
		btnRemoveRecord.setToolTipText("Removes selected records.");
		btnUnique.setToolTipText("Adds records with unique values from the chosen field.");
		btnMoveRecordUp.setToolTipText("Moves selected records up in the sample file.");
		btnMoveRecordDown.setToolTipText("Moves selected records down in the sample file.");
	}

}
