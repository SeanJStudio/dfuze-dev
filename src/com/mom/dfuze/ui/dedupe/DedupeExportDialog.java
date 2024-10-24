/**
 * Project: Dfuze
 * File: DedupeExportDialog.java
 * Date: May 17, 2020
 * Time: 8:44:38 AM
 */
package com.mom.dfuze.ui.dedupe;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.ExcelCell;
import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.io.AccessWriter;
import com.mom.dfuze.io.DbfWriter;
import com.mom.dfuze.io.TextWriter;
import com.mom.dfuze.io.XLSXWriter;
import com.mom.dfuze.ui.ExportTableNamerDialog;
import com.mom.dfuze.ui.JTextFieldLimit;
import com.mom.dfuze.ui.UiController;

import net.miginfocom.swing.MigLayout;
import java.awt.Color;

/**
 * DedupeExportDialog to allow users to customize how deduplications should be exported
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class DedupeExportDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField delimiterField;
	private String fileExtension;
	private boolean isNextPressed = false;
	private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXXX";
	private static final String STRIP_IN_REGEX = "(?i)([I][N]__)+";
	private JCheckBox chckbxIncludeOriginalsCheckBox;
	private JLabel lblDelimiterLabel;
	private JComboBox<String> comboBoxDelimiter;
	private JLabel lblChar;
	private JComboBox<String> comboBoxFileType;

	public static final String TAB_DELIMITER = "Tab";
	public static final String CHAR_DELIMITER = "Char";


	public DedupeExportDialog(JDialog dialog, ArrayList<Record> dupes) {

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("Export Duplicates");
		setResizable(false);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 10 28 20 28, gap 0", "[40px:n:40px][40px:n:40px][40px:n:40px][28px:n:28px][40px:n:40px][40px:n:40px][28px:n:28px][40px:n:40px]", "[36px:n:36px][28px:n:28px][14px:n:14px][36px:n:36px][10px:n:10px][28px:n:28px]"));

		JLabel lblExport = new JLabel("Export dupes as");
		lblExport.setForeground(new Color(25, 25, 112));
		lblExport.setFont(new java.awt.Font("Segoe UI Semibold", java.awt.Font.PLAIN, 14));
		contentPanel.add(lblExport, "cell 0 0 3 1,aligny center");

		lblDelimiterLabel = new JLabel("Delimiter");
		lblDelimiterLabel.setForeground(new Color(25, 25, 112));
		lblDelimiterLabel.setFont(new java.awt.Font("Segoe UI Semibold", java.awt.Font.PLAIN, 14));
		lblDelimiterLabel.setEnabled(false);
		contentPanel.add(lblDelimiterLabel, "cell 4 0 2 1,alignx left,aligny center");

		comboBoxDelimiter = new JComboBox<String>(getClientComboBoxModel(new String[] { CHAR_DELIMITER, TAB_DELIMITER }));
		comboBoxDelimiter.setForeground(new Color(0, 0, 0));
		comboBoxDelimiter.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
		comboBoxDelimiter.setEnabled(false);
		contentPanel.add(comboBoxDelimiter, "cell 4 1 2 1,grow");

		lblChar = new JLabel(CHAR_DELIMITER);
		lblChar.setForeground(new Color(25, 25, 112));
		lblChar.setFont(new java.awt.Font("Segoe UI Semibold", java.awt.Font.PLAIN, 14));
		lblChar.setEnabled(false);
		contentPanel.add(lblChar, "cell 7 0,aligny center");

		comboBoxDelimiter.addActionListener(new ComboBoxDelimiterHandler());

		String[] fileTypes = FileExtensions.FILE_EXPORT_EXTENSIONS_LIST.toArray(new String[0]);
		Arrays.sort(fileTypes);
		comboBoxFileType = new JComboBox<String>(getClientComboBoxModel(fileTypes));

		if(FileExtensions.FILE_EXPORT_EXTENSIONS_LIST.contains(UserPrefs.getLastUsedFileExtension()))
			comboBoxFileType.setSelectedItem(UserPrefs.getLastUsedFileExtension());

		comboBoxFileType.setForeground(new Color(0, 0, 0));
		comboBoxFileType.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
		contentPanel.add(comboBoxFileType, "cell 0 1 3 1,grow");
		comboBoxFileType.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);

		comboBoxFileType.addActionListener(new ComboBoxFileTypeHandler());

		delimiterField = new JTextField();
		delimiterField.setForeground(new Color(0, 0, 0));
		delimiterField.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
		delimiterField.setHorizontalAlignment(SwingConstants.CENTER);
		delimiterField.setDocument(new JTextFieldLimit(1));
		delimiterField.setText(",");
		contentPanel.add(delimiterField, "cell 7 1,grow");
		delimiterField.setColumns(10);
		delimiterField.setEnabled(false);

		setSettingsFromUserPreferences();

		JSeparator separator = new JSeparator();
		contentPanel.add(separator, "cell 0 3 8 1,growx,aligny center");

		chckbxIncludeOriginalsCheckBox = new JCheckBox("Include originals");
		chckbxIncludeOriginalsCheckBox.setSelected(true);
		chckbxIncludeOriginalsCheckBox.setForeground(Color.BLACK);
		chckbxIncludeOriginalsCheckBox.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
		contentPanel.add(chckbxIncludeOriginalsCheckBox, "cell 0 5 3 1");

		JButton btnNextButton = new JButton("Export Dupes");
		btnNextButton.setBackground(new Color(255, 255, 255));
		btnNextButton.setForeground(new Color(0, 0, 0));
		btnNextButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
		contentPanel.add(btnNextButton, "cell 4 5 4 1,grow");
		btnNextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean hasError = false;
				try {

					if (e.getSource() == btnNextButton) {
						if (comboBoxFileType.getSelectedIndex() == -1)
							throw new ApplicationException(String.format("File format must be selected."));

						ArrayList<Record> dupesToExport = new ArrayList<>(dupes);
						if (!chckbxIncludeOriginalsCheckBox.isSelected())
							for (int i = dupesToExport.size() - 1; i >= 0; --i)
								if (!dupesToExport.get(i).getIsDupe())
									dupesToExport.remove(i);

						if (dupesToExport.size() == 0) {
							UiController.displayMessage("Error", "No records are available to export", JOptionPane.ERROR_MESSAGE);
							DedupeExportDialog.this.dispose();
							return;
						}

						boolean isTextFile = false;

						String selectedFile = comboBoxFileType.getSelectedItem().toString();
						if (selectedFile.equals(FileExtensions.CSV) || selectedFile.equals(FileExtensions.TXT) || selectedFile.equals(FileExtensions.DAT)) {
							if (delimiterField.getText().equals("")) {
								throw new ApplicationException(String.format("Delimiter value can't be blank."));
							}

							isTextFile = true;
						}

						String fileExtension = comboBoxFileType.getSelectedItem().toString();
						FileNameExtensionFilter filter;

						// handles table in database
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
							returnVal = fileChooser.showSaveDialog(DedupeExportDialog.this);
						else
							returnVal = fileChooser.showOpenDialog(DedupeExportDialog.this);

						if (returnVal == JFileChooser.APPROVE_OPTION) {

							String[] headers = UiController.getUserData().getExportHeaders();
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
								//JOptionPane.showMessageDialog(DedupeExportDialog.this, "NOTICE: One or more fields could not be stripped as it would result in a duplicate field name.", "Duplicate fields", JOptionPane.INFORMATION_MESSAGE);


							File file = fileChooser.getSelectedFile();
							//UserPrefs.setLastUsedFolder(file.getParent());
							String fileName = file.getAbsolutePath();
							// handles table in database
							if (!isTableIntoDatabseSelected && fileName.toLowerCase().contains(fileExtension)) // get all text before extension if found
								fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(fileExtension));

							// handles table in database
							file = (!isTableIntoDatabseSelected) ? new File(fileName + fileExtension) : new File(fileName);

							if (!isTableIntoDatabseSelected && file.exists() && file.isFile()) {
								int buttonPressed = JOptionPane.showConfirmDialog(DedupeExportDialog.this, "File already exists, would you like to overwrite it?",
										"File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

								if (buttonPressed != JOptionPane.YES_OPTION) {
									return;
								}
							}

							switch (fileExtension) {
							case FileExtensions.MDB:
							case FileExtensions.ACCDB:
								AccessWriter.write(file, headers, UiController.getUserData().getExportData(dupesToExport));
								break;
							case FileExtensions.TABLE:
								ExportTableNamerDialog tableNamerDialog = new ExportTableNamerDialog(DedupeExportDialog.this, file);
								tableNamerDialog.setVisible(true);
								if(tableNamerDialog.getIsComplete()) {
									AccessWriter.writeTableToDatabase(file, headers,
											UiController.getUserData().getExportData(dupesToExport),  tableNamerDialog.getTableName());
									tableNamerDialog = null;
								}

								if(tableNamerDialog != null && !tableNamerDialog.getIsComplete())
									hasError = true;
								break;
							case FileExtensions.DBF:
								if (!DbfWriter.write(file, headers, UiController.getUserData().getExportData(dupesToExport)))
									return;
								break;
							case FileExtensions.XLSX:
								writeExcelReport(file, headers, UiController.getUserData().getExportData(dupesToExport));
								//XLSXWriter.write(file, headers, UiController.getUserData().getExportData(dupesToExport), false, false);
								break;
							case FileExtensions.TXT:
							case FileExtensions.DAT:
							case FileExtensions.CSV:
								TextWriter.write(file, DedupeExportDialog.this.getDelimiterField(), true, headers,
										UiController.getUserData().getExportData(dupesToExport));
								break;
							default:
								break;
							}

							if (!hasError) {
								UserPrefs.setLastUsedFolder(file.getParent());
								UserPrefs.setLastUsedFileExtension(fileExtension);

								if(isTextFile)
									UserPrefs.setLastUsedFileDelimiter(String.valueOf(DedupeExportDialog.this.getDelimiterField()));
								else
									UserPrefs.setLastUsedFileDelimiter("");

								isNextPressed = true;
								UiController.displayMessage("Success", "Export complete", JOptionPane.PLAIN_MESSAGE);
								//JOptionPane.showMessageDialog(DedupeExportDialog.this, "Export complete.", "Complete", JOptionPane.INFORMATION_MESSAGE);
								DedupeExportDialog.this.dispose();
							}
						}
					}

				} catch (Exception err) {
					UiController.handle(err);
				}
			}
		});

		pack();
		setLocationRelativeTo(dialog);
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

	public String getFileExtension() {
		return fileExtension;
	}

	public boolean getIsNextPressed() {
		return isNextPressed;
	}

	public char getDelimiterField() {
		if (comboBoxDelimiter.getSelectedItem().toString().equalsIgnoreCase("char"))
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
	
	private void writeExcelReport(File file, String[] headers, String[][] dupesToExport) {

		 // setup the report
		  List<List<ExcelCell>> report = new ArrayList<>();
		  
		  // setup the styling
		  XSSFCellStyle defaultStyle = ExcelCell.createCellStyle();
		  XSSFCellStyle headerStyle = ExcelCell.createCellStyle();
		  XSSFCellStyle rowEvenStyle = ExcelCell.createCellStyle();
		  
		  boolean isFormula = false;
		  
		  Font defaultFont = new XSSFFont();
		  Font headerFont = new XSSFFont();
		  headerFont.setBold(true);
		  headerFont.setColor(IndexedColors.WHITE.index);
		  
		  headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		  headerStyle.setFont(headerFont);
		  headerStyle.setFillForegroundColor(IndexedColors.BLACK.index);
		  
		  rowEvenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		  
		  @SuppressWarnings("deprecation")
		  XSSFColor rowColor = new XSSFColor(new java.awt.Color(220,220,220));
		  rowEvenStyle.setFillForegroundColor(rowColor);
		  
		  ArrayList<ExcelCell> rowDataFields = new ArrayList<>();
		  int isDupeIndex = 0;
		  for (int i = 0; i < headers.length; ++i) {
			  
			  if(headers[i].equalsIgnoreCase(UserData.fieldName.IS_DUPE.getName()))
				  isDupeIndex = i;
			  
			  ExcelCell cellDataField = new ExcelCell(headers[i], CellType.STRING, headerStyle, headerFont, isFormula);
			  rowDataFields.add(cellDataField);
		  }
		  report.add(rowDataFields);
		  
		  for (int i = 0; i < dupesToExport.length; ++i) {
			  ArrayList<ExcelCell> rowDataFieldValues = new ArrayList<>();
			  
			  XSSFCellStyle styleToUse = null;
			  if(dupesToExport[i][isDupeIndex].equalsIgnoreCase("true"))
				  styleToUse = rowEvenStyle;
			  else
				  styleToUse = defaultStyle;
				  
			  for(int j = 0; j < dupesToExport[i].length; ++j) {
				  ExcelCell cellDataField = new ExcelCell(dupesToExport[i][j], CellType.STRING, styleToUse, defaultFont, isFormula);
				  rowDataFieldValues.add(cellDataField);
			  }
			  report.add(rowDataFieldValues);
		  }
		  
		  try {
			  XLSXWriter.writeReportRaw(file, report);
		  } catch (ApplicationException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
	}

}
