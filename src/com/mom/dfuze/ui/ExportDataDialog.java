/**
 * Project: Dfuze
 * File: ExportDataDialog.java
 * Date: May 28, 2020
 * Time: 3:03:22 PM
 */
package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.StringUtils;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.Theme;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.io.AccessWriter;
import com.mom.dfuze.io.DbfWriter;
import com.mom.dfuze.io.TextWriter;
import com.mom.dfuze.io.XLSXWriter;

import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import java.awt.Component;

/**
 * ExportDataDialog to allow users to customize how loaded data should be exported
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class ExportDataDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField delimiterField;
	private JLabel lblDelimiterLabel;
	private JComboBox<String> comboBoxDelimiter;
	private JLabel lblChar;
	private JCheckBox chckbxAddDfId;
	private JCheckBox chckbxSplitData;
	private JLabel lblSplitDataDescription;
	private JComboBox<String> comboBoxSplitData;
	private JLabel lblSplitDataPreview;
	private JSeparator separator_1;
	private JComboBox<String> comboBoxFileType;
	private JButton btnExportData;

	public static final String TAB_DELIMITER = "Tab";
	public static final String CHAR_DELIMITER = "Char";

	private String fileExtension;
	private boolean isExportDataPressed = false;
	private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXXX";
	private static final String STRIP_IN_REGEX = "(?i)([I][N]__)+";

	//private static final String ACCDB = ".accdb";
	//private static final String CSV = ".csv";
	//private static final String DAT = ".dat";
	//private static final String DBF = ".dbf";
	//private static final String MDB = ".mdb";
	//private static final String TABLE = "table in database";
	//private static final String TXT = ".txt";
	//private static final String XLSX = ".xlsx";

	private static final int MAX_FILE_SPLIT_SIZE = 100;

	private String[] headers;
	private String[][] data;

	public ExportDataDialog(Component frame, String[] headers, String[][] data) {

		this.headers = headers;
		this.data = data;

		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setResizable(false);
		setTitle("Export Data");

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 10 28 20 28, gap 0", "[40px:n:40px][40px:n:40px][40px:n:40px][28px:n:28px][40px:n:40px][40px:n:40px][28px:n:28px][40px:n:40px]", "[36px:n:36px][28px:n:28px][14px:n:14px][36px:n:36px][28px:n:28px][28px:n:28px][28px:n:28px][14px:n:14px][28px:n:28px][14px:n:14px][36px:n:36px][28px:n:28px]"));

		JLabel lblExport = new JLabel("Export data as");
		lblExport.setForeground(Theme.TITLE_COLOR);
		lblExport.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblExport, "cell 0 0 3 1,aligny center");

		lblDelimiterLabel = new JLabel("Delimiter");
		lblDelimiterLabel.setForeground(Theme.TITLE_COLOR);
		lblDelimiterLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		lblDelimiterLabel.setEnabled(false);
		contentPanel.add(lblDelimiterLabel, "cell 4 0 2 1,alignx left,aligny center");

		comboBoxDelimiter = new JComboBox<String>(getClientComboBoxModel(new String[] { CHAR_DELIMITER, TAB_DELIMITER }));
		comboBoxDelimiter.setForeground(new Color(0, 0, 0));
		comboBoxDelimiter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxDelimiter.setEnabled(false);
		comboBoxDelimiter.addActionListener(new ComboBoxDelimiterHandler());
		contentPanel.add(comboBoxDelimiter, "cell 4 1 2 1,grow");			

		lblChar = new JLabel(CHAR_DELIMITER);
		lblChar.setForeground(Theme.TITLE_COLOR);
		lblChar.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		lblChar.setEnabled(false);
		contentPanel.add(lblChar, "cell 7 0,aligny center");

		String[] fileTypes = FileExtensions.FILE_EXPORT_EXTENSIONS_LIST.toArray(new String[0]);
		Arrays.sort(fileTypes);
		comboBoxFileType = new JComboBox<String>(getClientComboBoxModel(fileTypes));

		if(FileExtensions.FILE_EXPORT_EXTENSIONS_LIST.contains(UserPrefs.getLastUsedFileExtension()))
			comboBoxFileType.setSelectedItem(UserPrefs.getLastUsedFileExtension());

		comboBoxFileType.setForeground(new Color(0, 0, 0));
		comboBoxFileType.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(comboBoxFileType, "cell 0 1 3 1,grow");
		comboBoxFileType.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);

		comboBoxFileType.addActionListener(new ComboBoxFileTypeHandler());

		delimiterField = new JTextField();
		delimiterField.setForeground(new Color(0, 0, 0));
		delimiterField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		delimiterField.setHorizontalAlignment(SwingConstants.CENTER);
		delimiterField.setDocument(new JTextFieldLimit(1));
		delimiterField.setText(",");
		contentPanel.add(delimiterField, "cell 7 1,grow");
		delimiterField.setColumns(1);
		delimiterField.setEnabled(false);

		setSettingsFromUserPreferences();

		JSeparator separator = new JSeparator();
		contentPanel.add(separator, "cell 0 3 8 1,growx,aligny center");

		chckbxSplitData = new JCheckBox("Split Data");
		chckbxSplitData.setForeground(Color.BLACK);
		chckbxSplitData.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		chckbxSplitData.addActionListener(new ChckbxSplitDataHandler());
		contentPanel.add(chckbxSplitData, "cell 0 4 2 1");

		lblSplitDataDescription = new JLabel("<html>For each unique value found in the field below, a new file will be created containing only the records that share the same value. A max of " + MAX_FILE_SPLIT_SIZE + " files may be created.</html>");
		lblSplitDataDescription.setForeground(Color.BLACK);
		lblSplitDataDescription.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblSplitDataDescription.setEnabled(false);
		contentPanel.add(lblSplitDataDescription, "cell 0 5 8 2");

		comboBoxSplitData = new JComboBox<String>(new DefaultComboBoxModel<String>(headers));
		comboBoxSplitData.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxSplitData.setSelectedIndex(-1);
		comboBoxSplitData.setEnabled(false);
		comboBoxSplitData.addActionListener(new ComboBoxSplitDataHandler());
		contentPanel.add(comboBoxSplitData, "cell 0 8 3 1,grow");
		comboBoxSplitData.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);

		lblSplitDataPreview = new JLabel("");
		lblSplitDataPreview.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblSplitDataPreview.setForeground(Theme.TITLE_COLOR);
		lblSplitDataPreview.setEnabled(false);
		contentPanel.add(lblSplitDataPreview, "cell 4 8 4 1");

		separator_1 = new JSeparator();
		contentPanel.add(separator_1, "cell 0 10 8 1,growx");

		chckbxAddDfId = new JCheckBox("Export Dfuze ID");
		chckbxAddDfId.setEnabled(false);
		chckbxAddDfId.setForeground(Color.BLACK);
		chckbxAddDfId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(chckbxAddDfId, "cell 0 11 4 1");
		
		for(String header : headers) {
			if(header.equals(UserData.fieldName.DFID.getName())) {
				chckbxAddDfId.setEnabled(false);
			}
		}

		btnExportData = new JButton("Export Data");
		btnExportData.setBackground(new Color(255, 255, 255));
		btnExportData.setForeground(new Color(0, 0, 0));
		btnExportData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		contentPanel.add(btnExportData, "cell 5 11 3 1,grow");

		btnExportData.addActionListener(new ExportDataButtonHandler());
				
		pack();
	    setLocationRelativeTo(frame);
	    btnExportData.requestFocusInWindow();
	}


	//////////////////////////////////////////////////////////////////////////
	//=======================================================================
	// HANDLERS
	//=======================================================================
	/////////////////////////////////////////////////////////////////////////

	private class ComboBoxSplitDataHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == comboBoxSplitData) {
					if(comboBoxSplitData.getSelectedIndex() > -1) {

						SortedSet<String> uniqueValues = getUniqueFieldValues();

						lblSplitDataPreview.setText(String.format("%d file(s) will be made", uniqueValues.size()));

					} else {
						lblSplitDataPreview.setText("");
					}
				}
			} catch (Exception err) {
				UiController.handle(err);
			}

		}
	}

	private SortedSet<String> getUniqueFieldValues(){

		SortedSet<String> uniqueValues = new TreeSet<>();

		for(String[] record : data) {
			String value = record[comboBoxSplitData.getSelectedIndex()];
			uniqueValues.add(value);
		}

		return uniqueValues;
	}

	private class ChckbxSplitDataHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == chckbxSplitData) {
					if(chckbxSplitData.isSelected()) {
						lblSplitDataDescription.setEnabled(true);
						comboBoxSplitData.setEnabled(true);
						lblSplitDataPreview.setEnabled(true);
					} else {
						lblSplitDataDescription.setEnabled(false);
						comboBoxSplitData.setEnabled(false);
						comboBoxSplitData.setSelectedIndex(-1);
						lblSplitDataPreview.setText("");
						lblSplitDataPreview.setEnabled(false);
					}
				}

			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}

	private class ComboBoxDelimiterHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == comboBoxDelimiter) {
					String selectedDelimiter = comboBoxDelimiter.getSelectedItem().toString();
					if (selectedDelimiter.equalsIgnoreCase(CHAR_DELIMITER)) {
						delimiterField.setEnabled(true);
						lblChar.setEnabled(true);
					} else {
						delimiterField.setEnabled(false);
						lblChar.setEnabled(false);
					}
				}

			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}


	private class ComboBoxFileTypeHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == comboBoxFileType) {
					String selectedFile = comboBoxFileType.getSelectedItem().toString();
					if (selectedFile.equals(FileExtensions.CSV) || selectedFile.equals(FileExtensions.TXT) || selectedFile.equals(FileExtensions.DAT)) {
						lblDelimiterLabel.setEnabled(true);
						comboBoxDelimiter.setEnabled(true);
						if (comboBoxDelimiter.getSelectedItem().toString().equalsIgnoreCase(CHAR_DELIMITER)) {
							lblChar.setEnabled(true);
							delimiterField.setEnabled(true);
						}
					} else {
						lblDelimiterLabel.setEnabled(false);
						comboBoxDelimiter.setEnabled(false);
						delimiterField.setEnabled(false);
						lblChar.setEnabled(false);
					}

				}

			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}



	private class ExportDataButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean hasError = false;
			
			boolean hasDfId = false;
			String[] currentHeaders = UiController.getUserData().getExportHeaders();
			String[] currentDfHeaders = UiController.getUserData().getDfHeaders();
			
			try {
				if (e.getSource() == btnExportData) {
					if (comboBoxFileType.getSelectedIndex() == -1)
						throw new ApplicationException(String.format("File format must be selected."));

					boolean isTextFile = false;

					String selectedFile = comboBoxFileType.getSelectedItem().toString();
					if (selectedFile.equals(FileExtensions.CSV) || selectedFile.equals(FileExtensions.TXT) || selectedFile.equals(FileExtensions.DAT)) {
						if (delimiterField.getText().equals("")) {
							throw new ApplicationException(String.format("Delimiter value can't be blank."));
						}

						isTextFile = true;
					}

					boolean isDataSplit = chckbxSplitData.isSelected();
					

					if(isDataSplit && comboBoxSplitData.getSelectedIndex() == -1)
						throw new ApplicationException(String.format("ERROR: A field must be selected to split the data."));

					if(isDataSplit && getUniqueFieldValues().size() > MAX_FILE_SPLIT_SIZE)
						throw new ApplicationException(String.format("ERROR: Cannot make more than %d files.", MAX_FILE_SPLIT_SIZE));

					disableUi();

					String fileExtension = comboBoxFileType.getSelectedItem().toString();
					FileNameExtensionFilter filter;

					// handles table in database database
					boolean isTableIntoDatabseSelected = fileExtension.equals("table in database") ? true : false;
					if (isTableIntoDatabseSelected)
						filter = new FileNameExtensionFilter(FileExtensions.ACCDB + " " + FileExtensions.MDB, "accdb", "mdb");
					else
						filter = new FileNameExtensionFilter(fileExtension, fileExtension.replaceAll("\\.", ""));


					JFileChooser fileChooser = new JFileChooser(UserPrefs.getLastUsedFolder());
					fileChooser.addChoosableFileFilter(filter);
					fileChooser.setAcceptAllFileFilterUsed(true);
					int returnVal;

					// handles table in database
					if (!isTableIntoDatabseSelected)
						returnVal = fileChooser.showSaveDialog(ExportDataDialog.this);
					else
						returnVal = fileChooser.showOpenDialog(ExportDataDialog.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						//UserPrefs.setLastUsedFolder(file.getParent());
						String fileName = file.getAbsolutePath();
						// handles table in database
						if (!isTableIntoDatabseSelected && fileName.toLowerCase().contains(fileExtension)) // get all text before extension if found
							fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(fileExtension));

						// handles table in database
						file = (!isTableIntoDatabseSelected) ? new File(fileName + fileExtension) : new File(fileName);

						if (!isTableIntoDatabseSelected && file.exists() && file.isFile() && !isDataSplit) {
							int buttonPressed = JOptionPane.showConfirmDialog(ExportDataDialog.this, "File already exists, would you like to overwrite it?",
									"File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

							if (buttonPressed != JOptionPane.YES_OPTION) {
								return;
							}
						}

						
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							
							if(chckbxAddDfId.isSelected()) {
								
								for(String header : headers) {
									if(header.equals(UserData.fieldName.DFID.getName())) {
										hasDfId = true;
										break;
									}
								}
								
								if(!hasDfId) {
									String[] newHeaders = new String[currentHeaders.length + 1];
									String[] newDfHeaders = new String[currentDfHeaders.length + 1];
									
									for(int i = 0; i < currentHeaders.length; ++i)
										newHeaders[i] = currentHeaders[i];
									
									for(int i = 0; i < currentDfHeaders.length; ++i)
										newDfHeaders[i] = currentDfHeaders[i];
		
									newHeaders[newHeaders.length - 1] = UserData.fieldName.DFID.getName();
									newDfHeaders[newDfHeaders.length - 1] = UserData.fieldName.DFID.getName();
									
									UiController.getUserData().setDfHeaders(newDfHeaders);

									data = UiController.getUserData().getExportData(UiController.getUserData().getRecordList());
									headers = newHeaders;
								}
							}
							
							boolean hasDuplicateFields = false;

								HashSet<String> uniqueHeaders = new HashSet<>();

								for(int i = headers.length - 1; i >= 0; --i) {
									if(!uniqueHeaders.add(headers[i].replaceAll(STRIP_IN_REGEX, ""))) {
										hasDuplicateFields = true;
										uniqueHeaders.add(headers[i]);
									} else {
										headers[i] = headers[i].replaceAll(STRIP_IN_REGEX, "");
									}
								}

								if(hasDuplicateFields)
									UiController.displayMessage("Warning", "One or more fields could not be stripped as it would result in a duplicate field name", JOptionPane.WARNING_MESSAGE);

							// create and validate multiple files here
							List<File> files = new ArrayList<>();
							List<List<String[]>> recordLists = new ArrayList<>();
							SortedSet<String> uniqueValues = new TreeSet<>();

							if(isDataSplit) {
								uniqueValues = getUniqueFieldValues();

								if(uniqueValues.size() > MAX_FILE_SPLIT_SIZE)
									throw new ApplicationException(String.format("ERROR: Cannot make more than %d files.", MAX_FILE_SPLIT_SIZE));

								for(String value : uniqueValues) {
									List<String[]> tempList = new ArrayList<>();

									for(String[] record : data) {
										String tempValue = record[comboBoxSplitData.getSelectedIndex()];
										if(value.equalsIgnoreCase(tempValue)) {
											tempList.add(record);
										}
									}


									recordLists.add(tempList);
								}

								DateTimeFormatter idtf = DateTimeFormatter.ofPattern("MMddyyyyHHmmss");
								LocalDateTime now = LocalDateTime.now();

								if(uniqueValues.size() > 0) {
									for(String segment : uniqueValues) {
										// ensure only valid characters are present in file names
										segment = StringUtils.stripAccents(segment).replaceAll("[^a-zA-Z0-9 -_]", "");

										File tempFile = new File(fileName + " " + segment + fileExtension);
										if(tempFile.exists() && tempFile.isFile())
											tempFile  = new File(fileName + " " + segment + " " + idtf.format(now) + fileExtension);
										
										files.add(tempFile);
									}
								}
							}

							switch (fileExtension) {
							case FileExtensions.MDB:
							case FileExtensions.ACCDB:
								if(!isDataSplit)
									AccessWriter.write(file, headers, data);
								else
									for(int j = 0; j < recordLists.size(); ++j)
										AccessWriter.write(files.get(j), headers, recordLists.get(j).stream().toArray(String[][]::new));
								break;
							case FileExtensions.TABLE:

								ExportTableNamerDialog tableNamerDialog = new ExportTableNamerDialog(ExportDataDialog.this, file);
								tableNamerDialog.setVisible(true);
								if(tableNamerDialog.getIsComplete()) {
									if(!isDataSplit)
										AccessWriter.writeTableToDatabase(file, headers, data, tableNamerDialog.getTableName());
									else
										for(int j = 0; j < recordLists.size(); ++j)
											AccessWriter.writeTableToDatabase(file, headers, recordLists.get(j).stream().toArray(String[][]::new), tableNamerDialog.getTableName() + " " + uniqueValues.toArray(new String[0])[j]);

									tableNamerDialog = null;
								}

								if(tableNamerDialog != null && !tableNamerDialog.getIsComplete())
									hasError = true;
								break;
							case FileExtensions.DBF:
								if(!isDataSplit) {
									if (!DbfWriter.write(file, headers, data)) 
										return;
								} else {
									for (int j = 0; j < recordLists.size(); ++j) {
										if (!DbfWriter.write(files.get(j), headers, recordLists.get(j).stream().toArray(String[][]::new)))
											return;
									}
								}

								break;
							case FileExtensions.XLSX:
								if(!isDataSplit)
									XLSXWriter.write(file, headers, data, false, false);
								else
									for(int j = 0; j < recordLists.size(); ++j)
										XLSXWriter.write(files.get(j), headers, recordLists.get(j).stream().toArray(String[][]::new), false, false);

								break;
							case FileExtensions.TXT:
							case FileExtensions.DAT:
							case FileExtensions.CSV:
								if(!isDataSplit)
									TextWriter.write(file, ExportDataDialog.this.getDelimiterField(), true, headers, data);
								else
									for(int j = 0; j < recordLists.size(); ++j)
										TextWriter.write(files.get(j), ExportDataDialog.this.getDelimiterField(), true, headers, recordLists.get(j).stream().toArray(String[][]::new));
								break;
							default:
								break;
							}
						}

						if (!hasError) {
							System.out.println(file.getAbsolutePath());

							UserPrefs.setLastUsedFolder(file.getParent());
							UserPrefs.setLastUsedFileExtension(fileExtension);

							if(isTextFile)
								UserPrefs.setLastUsedFileDelimiter(String.valueOf(ExportDataDialog.this.getDelimiterField()));
							else
								UserPrefs.setLastUsedFileDelimiter("");
							
							if(chckbxAddDfId.isSelected() && !hasDfId) {
								UiController.getUserData().setDfHeaders(currentDfHeaders);
								headers = currentHeaders;
							}

							isExportDataPressed = true;
							UiController.displayMessage("Success", "Export complete", JOptionPane.INFORMATION_MESSAGE);
							//JOptionPane.showMessageDialog(ExportDataDialog.this, "Export complete.", "Complete", JOptionPane.INFORMATION_MESSAGE);
							ExportDataDialog.this.dispose();
						}
					}
				}

			} catch (Exception err) {
				hasError = true;
				UiController.handle(err);
			} finally {
				enableUi();
			}

		}
	}

	private void disableUi() {
		delimiterField.setEnabled(false);
		lblDelimiterLabel.setEnabled(false);
		comboBoxDelimiter.setEnabled(false);
		lblChar.setEnabled(false);
		chckbxAddDfId.setEnabled(false);
		chckbxSplitData.setEnabled(false);
		lblSplitDataDescription.setEnabled(false);
		comboBoxSplitData.setEnabled(false);
		lblSplitDataPreview.setEnabled(false);
		comboBoxFileType.setEnabled(false);
		btnExportData.setEnabled(false);
	}

	public void enableUi() {

		// Only enable if the selected extension is text based
		String fileExtension = comboBoxFileType.getSelectedItem().toString();
		if(fileExtension.equals(FileExtensions.CSV) || fileExtension.equals(FileExtensions.DAT) || fileExtension.equals(FileExtensions.TXT)) {
			delimiterField.setEnabled(true);
			lblDelimiterLabel.setEnabled(true);
			comboBoxDelimiter.setEnabled(true);
			lblChar.setEnabled(true);
		}

		chckbxAddDfId.setEnabled(true);
		chckbxSplitData.setEnabled(true);
		chckbxSplitData.setSelected(false);
		lblSplitDataDescription.setEnabled(false);
		comboBoxSplitData.setEnabled(false);
		comboBoxSplitData.setSelectedIndex(-1);
		lblSplitDataPreview.setEnabled(true);
		comboBoxFileType.setEnabled(true);
		btnExportData.setEnabled(true);
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public boolean getIsNextPressed() {
		return isExportDataPressed;
	}

	public void setIsNextPressed(boolean isNextPressed) {
		this.isExportDataPressed = isNextPressed;
	}

	public char getDelimiterField() {
		if (comboBoxDelimiter.getSelectedItem().toString().equalsIgnoreCase(CHAR_DELIMITER))
			return delimiterField.getText().charAt(0);

		return '\t';
	}

	private DefaultComboBoxModel<String> getClientComboBoxModel(String[] array) {
		return new DefaultComboBoxModel<>(array);
	}

	private void setSettingsFromUserPreferences() {
		if(UserPrefs.getLastUsedFileDelimiter().length() > 0) {
			comboBoxDelimiter.setEnabled(true);
			lblDelimiterLabel.setEnabled(true);
			if(UserPrefs.getLastUsedFileDelimiter().equalsIgnoreCase("\t")) {
				comboBoxDelimiter.setSelectedItem(TAB_DELIMITER);
			} else {
				comboBoxDelimiter.setSelectedItem(CHAR_DELIMITER);
				delimiterField.setText(UserPrefs.getLastUsedFileDelimiter());
				delimiterField.setEnabled(true);
				lblChar.setEnabled(true);
			}	
		}
	}
	
	

	public JComboBox<String> getComboBoxSplitData() {
		return comboBoxSplitData;
	}

	public JCheckBox getChckbxSplitData() {
		return chckbxSplitData;
	}

	public JButton getBtnExportData() {
		return btnExportData;
	}

	public JCheckBox getChckbxAddDfId() {
		return chckbxAddDfId;
	}

	public void setChckbxAddDfId(JCheckBox chckbxAddDfId) {
		this.chckbxAddDfId = chckbxAddDfId;
	}

	public JComboBox<String> getComboBoxDelimiter() {
		return comboBoxDelimiter;
	}

	public void setComboBoxDelimiter(JComboBox<String> comboBoxDelimiter) {
		this.comboBoxDelimiter = comboBoxDelimiter;
	}

	public JComboBox<String> getComboBoxFileType() {
		return comboBoxFileType;
	}

	public void setComboBoxFileType(JComboBox<String> comboBoxFileType) {
		this.comboBoxFileType = comboBoxFileType;
	}

	public void setDelimiterField(JTextField delimiterField) {
		this.delimiterField = delimiterField;
	}

	public void setChckbxSplitData(JCheckBox chckbxSplitData) {
		this.chckbxSplitData = chckbxSplitData;
	}

	public void setBtnExportData(JButton btnExportData) {
		this.btnExportData = btnExportData;
	}
	
	

}
