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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import javax.swing.JList;
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
public class ProofMakerDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private final JButton btnRandom = new JButton("Add Random");
	private final JButton btnRemoveRecord = new JButton("Delete Records");
	private final JButton btnUnique = new JButton("Add Unique");
	private final JButton btnMakeProofFile = new JButton("Make Proof File");
	private final JButton btnMoveRecordUp = new JButton("Move Records Up");
	private final JButton btnMoveRecordDown = new JButton("Move Records Down");

	private ProofTableModel proofTableModel;

	private Set<String> sortedFields = new HashSet<>();

	private final JScrollPane scrollPane;
	private final JScrollPane scrollPane_1 = new JScrollPane();

	private final JButton btnLongest = new JButton("Add Longest");
	private final JButton btnShortest = new JButton("Add Shortest");
	private final JButton btnBlanks = new JButton("Add Least Blanks");
	private final JButton btnMostBlanks = new JButton("Add Most Blanks");
	private final JList<String> list = new JList<>();
	private final JProgressBar progressBar = new JProgressBar();

	private Thread processThread;
	private final JLabel lblRecordNumber = new JLabel("0 records in file");

	public ProofMakerDialog(JFrame frame) {
		setResizable(false);
		getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 12));

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("Proof Maker");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 245));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		contentPanel.setLayout(new MigLayout("insets 10 28 20 28, gap 0", "[184px:n:184px][28px:n:28px][184px:n:184px][28px:n:28px][184px:n:184px][28px:n:28px][184px:n:184px]", "[][14px:n:14px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px,grow][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][14px:n:14px][28px:n:28px][28px:n:28px][28px:n:28px]"));

		proofTableModel = new ProofTableModel();
		table = new JTable(proofTableModel);
		table.setRowHeight(28);
		table.getTableHeader().addMouseListener(new SelectColumnHandler());
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setShowGrid(true);

		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		
		btnRandom.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnRandom.addActionListener(new AddRandomRecordButtonHandler());
		contentPanel.add(btnRandom, "cell 2 0,grow");
		clickOnKey( btnRandom, "AddRandomRecordButtonHandler", KeyEvent.VK_R );

		btnLongest.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnLongest.addActionListener(new AddLongestButtonHandler());
		contentPanel.add(btnLongest, "cell 0 2,grow");
		clickOnKey( btnLongest, "AddLongestButtonHandler", KeyEvent.VK_L );
		
		btnShortest.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnShortest.addActionListener(new AddShortestButtonHandler());
		contentPanel.add(btnShortest, "cell 0 0,grow");
		clickOnKey( btnShortest, "AddShortestButtonHandler", KeyEvent.VK_S );
		
		btnUnique.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnUnique.addActionListener(new AddUniqueRecordButtonHandler());

		contentPanel.add(btnUnique, "cell 2 2,grow");
		clickOnKey( btnUnique, "AddUniqueRecordButtonHandler", KeyEvent.VK_U );
		btnRemoveRecord.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnRemoveRecord.addActionListener(new RemoveRecordButtonHandler());
		
		btnBlanks.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnBlanks.addActionListener(new AddLeastBlanksRecordButtonHandler());
		contentPanel.add(btnBlanks, "cell 4 2,grow");
		clickOnKey( btnBlanks, "AddLeastBlanksRecordButtonHandler", KeyEvent.VK_B );
		
		btnMostBlanks.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMostBlanks.addActionListener(new AddMostBlanksRecordButtonHandler());
		contentPanel.add(btnMostBlanks, "cell 4 0,grow");
		clickOnKey( btnMostBlanks, "AddMostBlanksRecordButtonHandler", KeyEvent.VK_M );

		contentPanel.add(btnRemoveRecord, "cell 6 2,grow");

		clickOnKey( btnRemoveRecord, "RemoveRecordButtonHandler", KeyEvent.VK_D );
		contentPanel.add(scrollPane, "cell 2 4 5 11,grow");
		scrollPane.setViewportView(table);

		contentPanel.add(scrollPane_1, "cell 0 4 1 11,grow");
		list.setForeground(Color.BLACK);
		list.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		list.setListData(UiController.getUserData().getExportHeaders());
		list.setFixedCellHeight(32);
		list.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		scrollPane_1.setViewportView(list);
		btnMakeProofFile.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMakeProofFile.addActionListener(new MakeSampleFileButtonHandler());

		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(progressBar, "cell 0 16 7 1,grow");
		btnMoveRecordDown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMoveRecordDown.addActionListener(new MoveRecordDownButtonHandler());

		btnMoveRecordUp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMoveRecordUp.addActionListener(new MoveRecordUpButtonHandler());

		contentPanel.add(btnMoveRecordUp, "cell 0 18,grow");

		contentPanel.add(btnMoveRecordDown, "cell 2 18,grow");
		lblRecordNumber.setForeground(Theme.TITLE_COLOR);
		lblRecordNumber.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		contentPanel.add(lblRecordNumber, "cell 4 18,alignx right");


		contentPanel.add(btnMakeProofFile, "cell 6 18,grow");

		setToolTips();
		
		pack();
	    setLocationRelativeTo(frame);
		getContentPane().requestFocusInWindow();
	}

	private void disableUi() {
		table.setEnabled(false);
		btnRandom.setEnabled(false);
		btnRemoveRecord.setEnabled(false);
		btnUnique.setEnabled(false);
		btnMakeProofFile.setEnabled(false);
		btnMoveRecordUp.setEnabled(false);
		btnMoveRecordDown.setEnabled(false);
		btnShortest.setEnabled(false);
		btnLongest.setEnabled(false);
		btnBlanks.setEnabled(false);
		btnMostBlanks.setEnabled(false);
		list.setEnabled(false);
	}

	private void enableUi() {
		table.setEnabled(true);
		btnRandom.setEnabled(true);
		btnRemoveRecord.setEnabled(true);
		btnUnique.setEnabled(true);
		btnMakeProofFile.setEnabled(true);
		btnMoveRecordUp.setEnabled(true);
		btnMoveRecordDown.setEnabled(true);
		btnShortest.setEnabled(true);
		btnLongest.setEnabled(true);
		btnBlanks.setEnabled(true);
		btnMostBlanks.setEnabled(true);
		list.setEnabled(true);
	}

	private class AddRandomRecordButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnRandom) {
				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
							disableUi();

							if(!proofTableModel.addRandomRecord())
								throw new Exception("All the records have been added already.");

							resizeTable(table);
							updateRecordNum();
						} catch (Exception err) {
							UiController.handle(err);
						} finally {
							progressBar.setValue(progressBar.getMaximum());
							enableUi();
						}

					}
				};


				processThread = new Thread(runner, "Code Executer");
				processThread.start();
			}
		}
	}

	private class AddLeastBlanksRecordButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnBlanks) {
				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
							disableUi();
							progressBar.setValue(0);
							progressBar.setMaximum(UiController.getUserData().getRecordList().size());
							List<String> selectedFieldsMaxNonBlanks = list.getSelectedValuesList();

							if(selectedFieldsMaxNonBlanks.size() == 0)
								throw new Exception("No field is selected.\nPlease select a field from the list on the left.");

							int maxNumOfNonBlanks = 0;
							Record finalMaxNonBlanksRecord = null;

							int[] indices = list.getSelectedIndices();

							for(Record record : UiController.getUserData().getRecordList()) {

								int tempNumOfNonBlanks = 0;

								for(int fieldIndex = 0; fieldIndex < selectedFieldsMaxNonBlanks.size(); ++fieldIndex) {
									String selectedField = selectedFieldsMaxNonBlanks.get(fieldIndex);

									if(Common.isDfField(selectedField)) {
										String value = UserData.getRecordFieldByName(selectedField, record);
										if(!value.isBlank()) 
											++tempNumOfNonBlanks;

									} else {
										String value = record.getDfInData()[indices[fieldIndex]];
										if(!value.isBlank()) 
											++tempNumOfNonBlanks;

									}
								}

								if(tempNumOfNonBlanks > maxNumOfNonBlanks || finalMaxNonBlanksRecord == null) {
									maxNumOfNonBlanks = tempNumOfNonBlanks;
									finalMaxNonBlanksRecord = record;
								}
								progressBar.setValue(progressBar.getValue() + 1);
							}

							if(!proofTableModel.addRecord(finalMaxNonBlanksRecord))
								throw new Exception("The record with the least blanks has already been added.");

							resizeTable(table);
							updateRecordNum();
						} catch (Exception err) {
							UiController.handle(err);
						} finally {
							progressBar.setValue(progressBar.getMaximum());
							enableUi();
						}

					}
				};

				processThread = new Thread(runner, "Code Executer");
				processThread.start();


			}
		}
	}
	
	private class AddMostBlanksRecordButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnMostBlanks) {
				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
							disableUi();
							progressBar.setValue(0);
							progressBar.setMaximum(UiController.getUserData().getRecordList().size());
							List<String> selectedFieldsMaxBlanks = list.getSelectedValuesList();

							if(selectedFieldsMaxBlanks.size() == 0)
								throw new Exception("No field is selected.\nPlease select a field from the list on the left.");

							int maxNumOfBlanks = 0;
							Record finalMaxBlanksRecord = null;

							int[] indices = list.getSelectedIndices();

							for(Record record : UiController.getUserData().getRecordList()) {

								int tempNumOfBlanks = 0;

								for(int fieldIndex = 0; fieldIndex < selectedFieldsMaxBlanks.size(); ++fieldIndex) {
									String selectedField = selectedFieldsMaxBlanks.get(fieldIndex);
									String value = (Common.isDfField(selectedField)) ? UserData.getRecordFieldByName(selectedField, record) : record.getDfInData()[indices[fieldIndex]];
									if(value.isBlank()) 
										++tempNumOfBlanks;
								}

								if(tempNumOfBlanks > maxNumOfBlanks || finalMaxBlanksRecord == null) {
									maxNumOfBlanks = tempNumOfBlanks;
									finalMaxBlanksRecord = record;
								}
								progressBar.setValue(progressBar.getValue() + 1);
							}

							if(finalMaxBlanksRecord != null && !proofTableModel.addRecord(finalMaxBlanksRecord))
								throw new Exception("The record with the most blanks has already been added.");

							resizeTable(table);
							updateRecordNum();
						} catch (Exception err) {
							UiController.handle(err);
						} finally {
							progressBar.setValue(progressBar.getMaximum());
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

				////

				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
							disableUi();
							progressBar.setValue(0);
							progressBar.setMaximum(UiController.getUserData().getRecordList().size());

							List<String> selectedFields = list.getSelectedValuesList();
							int[] selectedFieldIndexes = list.getSelectedIndices();

							if(selectedFields.size() == 0)
								throw new Exception("No field is selected.\nPlease select a field from the list on the left.");

							HashSet<String> uniqueValues = new HashSet<>();
							List<Record> recordsToAdd = new ArrayList<>();
							
							// randomize the data
							Collections.shuffle(UiController.getUserData().getRecordList());
							
							for(Record record : UiController.getUserData().getRecordList()) {
								StringBuilder sb = new StringBuilder();
								
								for(int i = 0; i < selectedFields.size(); ++i) {
									if(Common.isDfField(selectedFields.get(i))) {
										sb.append(UserData.getRecordFieldByName(selectedFields.get(i), record).trim().toLowerCase());
									} else {
										sb.append(record.getDfInData()[selectedFieldIndexes[i]].trim().toLowerCase());
									}
								}
								
								if(!proofTableModel.getAddedIDs().contains(record.getDfId()) && uniqueValues.add(sb.toString()))
									recordsToAdd.add(record);
								
								progressBar.setValue(progressBar.getValue() + 1);
								
							}

							if(recordsToAdd.size() > 0)
								proofTableModel.addRecordList(recordsToAdd);
							else
								throw new Exception("All the records have been added already.");
							
							resizeTable(table);
							updateRecordNum();
						} catch (Exception err) {
							UiController.handle(err);
						} finally {
							progressBar.setValue(progressBar.getMaximum());
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

	private class AddLongestButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnLongest) {

				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
							disableUi();
							List<String> selectedFieldsLongestCharacter = list.getSelectedValuesList();

							progressBar.setValue(0);
							progressBar.setMaximum(UiController.getUserData().getRecordList().size() * selectedFieldsLongestCharacter.size());

							if(selectedFieldsLongestCharacter.size() == 0)
								throw new Exception("No field is selected.\nPlease select a field from the list on the left.");

							int longestLength = 0;
							Record longestRecord = null;

							int[] indices = list.getSelectedIndices();

							for(int fieldIndex = 0; fieldIndex < selectedFieldsLongestCharacter.size(); ++fieldIndex) {
								String selectedField = selectedFieldsLongestCharacter.get(fieldIndex);

								if(Common.isDfField(selectedField)) {
									for(Record record : UiController.getUserData().getRecordList()) {
										String value = UserData.getRecordFieldByName(selectedField, record);
										if(longestRecord == null || value.length() > longestLength) {
											longestRecord = record;
											longestLength = value.length();
										}
										progressBar.setValue(progressBar.getValue() + 1);
									}
								} else {
									for(Record record : UiController.getUserData().getRecordList()) {
										String value = record.getDfInData()[indices[fieldIndex]];
										if(longestRecord == null || value.length() > longestLength) {
											longestRecord = record;
											longestLength = value.length();
										}
										progressBar.setValue(progressBar.getValue() + 1);
									}
								}
							}

							if(!proofTableModel.addRecord(longestRecord))
								throw new Exception("The longest record has already been added.");

							resizeTable(table);
							updateRecordNum();
						} catch (Exception err) {
							UiController.handle(err);
						} finally {
							progressBar.setValue(progressBar.getMaximum());
							enableUi();
						}

					}
				};

				processThread = new Thread(runner, "Code Executer");
				processThread.start();
			}
		}
	}
	
	private class AddShortestButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnShortest) {

				Runnable runner = new Runnable() {
					@Override
					public void run() {
						try {
							disableUi();
							List<String> selectedFields = list.getSelectedValuesList();

							progressBar.setValue(0);
							progressBar.setMaximum(UiController.getUserData().getRecordList().size() * selectedFields.size());

							if(selectedFields.size() == 0)
								throw new Exception("No field is selected.\nPlease select a field from the list on the left.");

							int shortestLength = 999999;
							Record shortestRecord = null;

							int[] indices = list.getSelectedIndices();

							for(int fieldIndex = 0; fieldIndex < selectedFields.size(); ++fieldIndex) {
								String selectedField = selectedFields.get(fieldIndex);

								for(Record record : UiController.getUserData().getRecordList()) {
									String value = (Common.isDfField(selectedField)) ? UserData.getRecordFieldByName(selectedField, record) : record.getDfInData()[indices[fieldIndex]];

									if(shortestRecord == null || value.length() < shortestLength) {
										if(value.length() > 0) {
											shortestRecord = record;
											shortestLength = value.length();
										}
									}
									progressBar.setValue(progressBar.getValue() + 1);
								}
							}

							if(shortestRecord == null)
								throw new Exception("All the records are blank!");
							else if(!proofTableModel.addRecord(shortestRecord))
								throw new Exception("The longest record has already been added.");

							resizeTable(table);
							updateRecordNum();
						} catch (Exception err) {
							UiController.handle(err);
						} finally {
							progressBar.setValue(progressBar.getMaximum());
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

						stopEditingTable();
						proofTableModel.deleteRecord(table.getSelectedRows());
						resizeTable(table);
						updateRecordNum();
					}
				};

				processThread = new Thread(runner, "Code Executer");
				processThread.start();
			}
		}
	}

	private class MoveRecordUpButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnMoveRecordUp) {
				int[] selectedRows = table.getSelectedRows();
				stopEditingTable();
				proofTableModel.moveRecordUp(table.getSelectedRows());
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
				proofTableModel.moveRecordDown(table.getSelectedRows());
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
				int clickedIndex = table.convertColumnIndexToModel(table.columnAtPoint(e.getPoint()));
				String fieldName = table.getColumnName(clickedIndex);
				if (e.getClickCount() == 2) {
					if(sortedFields.add(fieldName)) {
						proofTableModel.sortTableDataAscending(Common.isDfField(fieldName), fieldName, clickedIndex);
					} else {
						proofTableModel.sortTableDataDescending(Common.isDfField(fieldName), fieldName, clickedIndex);
						sortedFields.remove(fieldName);
					}
				}
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
				if (e.getSource() == btnMakeProofFile) {

					if(proofTableModel.getRowCount() == 0) {
						UiController.displayMessage("Error", "No records have been added to the proof file yet.", JOptionPane.ERROR_MESSAGE);
						//JOptionPane.showMessageDialog(ProofMakerDialog.this, "No records have been added to the proof file yet.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					ExportDataDialog exportDataDialog = new ExportDataDialog(ProofMakerDialog.this, UiController.getUserData().getExportHeaders(), proofTableModel.getTableData());
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
	@SuppressWarnings("deprecation")
	public static void clickOnKey(final AbstractButton button, String actionName, int key) {
		button.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke( key, InputEvent.ALT_MASK ), actionName);

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
		btnShortest.setToolTipText("Adds the record with the shortest character length and isnt blank from the selected fields.");
		btnLongest.setToolTipText("Adds the record with the longest character length from the selected fields.");
		btnBlanks.setToolTipText("Adds the record with the least amount of blanks from the selected fields.");
		btnRandom.setToolTipText("Adds a random record.");
		btnRemoveRecord.setToolTipText("Removes selected records.");
		btnUnique.setToolTipText("Adds records with unique values from the chosen fields.");
		btnMoveRecordUp.setToolTipText("Moves selected records up in the proof file.");
		btnMoveRecordDown.setToolTipText("Moves selected records down in the proof file.");
	}

}
