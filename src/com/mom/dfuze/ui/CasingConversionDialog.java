package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.Theme;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JSeparator;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JProgressBar;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class CasingConversionDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JScrollPane scrollPane;
	private JList<String> list;
	private JLabel lblTitle;
	private JLabel lblDescription;
	private JButton btnConvert;
	private JRadioButton rdbtnPersonNames;
	private JRadioButton rdbtnCompanyNames;
	private JRadioButton rdbtnAutoDetectNames;
	private ButtonGroup btnGroupCasingRules;
	private JLabel lblCasingRules;

	private String[][] companyKeywords;
	private JRadioButton rdbtnUpperCase;
	private JRadioButton rdbtnLowerCase;
	private JRadioButton rdbtnProperCase;
	private JProgressBar progressBar;
	private JSeparator separator_1;
	
	private Thread processThread = null;
	private JSeparator separator;
	private JCheckBox chckbxCaseIfLowerOrUpper;

	public CasingConversionDialog(JFrame frame) {
		super(frame, true);
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 10 28 20 28, gap 0", "[61.33px:n:61.33px][61.33px:n:61.33px][61.33px:n:61.33px][36px:n:36px][64px:n:64px][64px:n:64px][14px:n:14px][64px:n:64px][64px:n:64px]", "[36px:n:36px][28px:n:28px,grow][28px:n:28px][48px:n:48px][36px:n:36px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][36px:n:36px][28px:n:28px]"));

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPanel.add(scrollPane, "cell 0 0 3 9,grow");

		list = new JList<>();
		list.setForeground(Color.BLACK);
		list.setListData(UiController.getUserData().getExportHeaders());
		list.setSelectedIndex(0);
		list.setFixedCellHeight(32);
		list.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		scrollPane.setViewportView(list);

		lblTitle = new JLabel("Casing Conversion");
		lblTitle.setForeground(Theme.TITLE_COLOR);
		lblTitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblTitle, "cell 4 0 5 1,aligny center");

		lblDescription = new JLabel("<html>Choose fields from the left and how the casing should be converted below. Select multiple fields by holding down the Ctrl or Shift key.</html>");
		lblDescription.setForeground(Color.BLACK);
		lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblDescription, "cell 4 1 5 2,alignx left,aligny bottom");

		separator_1 = new JSeparator();
		contentPanel.add(separator_1, "cell 4 3 5 1,growx");

		lblCasingRules = new JLabel("Options");
		lblCasingRules.setForeground(Theme.TITLE_COLOR);
		lblCasingRules.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblCasingRules, "cell 4 4,aligny top");
		
		btnGroupCasingRules = new ButtonGroup();
		
		chckbxCaseIfLowerOrUpper = new JCheckBox("Skip mixed case values");
		chckbxCaseIfLowerOrUpper.setToolTipText("Skips values that are not all upper or lower case. Ex. APPLE or apple");
		chckbxCaseIfLowerOrUpper.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(chckbxCaseIfLowerOrUpper, "cell 5 4 4 1,alignx center,aligny top");

		rdbtnPersonNames = new JRadioButton("Person Names");
		rdbtnPersonNames.setSelected(true);
		rdbtnPersonNames.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(rdbtnPersonNames, "cell 4 5 2 1,growx,aligny bottom");
		btnGroupCasingRules.add(rdbtnPersonNames);

		btnConvert = new JButton("Convert");
		btnConvert.setBackground(Color.WHITE);
		btnConvert.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnConvert.addActionListener(new CaseButtonHandler());

		rdbtnLowerCase = new JRadioButton("lowercase");
		rdbtnLowerCase.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(rdbtnLowerCase, "cell 7 5 2 1,growx,aligny bottom");	
		btnGroupCasingRules.add(rdbtnLowerCase);

		rdbtnCompanyNames = new JRadioButton("Company Names");
		rdbtnCompanyNames.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(rdbtnCompanyNames, "cell 4 6 2 1,growx,aligny bottom");
		btnGroupCasingRules.add(rdbtnCompanyNames);

		rdbtnUpperCase = new JRadioButton("UPPERCASE");
		rdbtnUpperCase.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(rdbtnUpperCase, "cell 7 6 2 1,growx,aligny bottom");
		btnGroupCasingRules.add(rdbtnUpperCase);

		rdbtnAutoDetectNames = new JRadioButton("Auto-Detect Names");
		rdbtnAutoDetectNames.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(rdbtnAutoDetectNames, "cell 4 7 2 1,growx,aligny bottom");
		btnGroupCasingRules.add(rdbtnAutoDetectNames);

		rdbtnProperCase = new JRadioButton("ProperCase");
		rdbtnProperCase.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(rdbtnProperCase, "cell 7 7 2 1,growx,aligny bottom");
		btnGroupCasingRules.add(rdbtnProperCase);
		
		separator = new JSeparator();
		contentPanel.add(separator, "cell 0 9 9 1,growx");

		progressBar  = new JProgressBar();
		progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		progressBar.setStringPainted(true);

		contentPanel.add(progressBar, "cell 0 10 6 1,grow");
		contentPanel.add(btnConvert, "cell 7 10 2 1,grow");

		pack();
	    setLocationRelativeTo(frame);
		getContentPane().requestFocusInWindow();
	}

	private class CaseButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnConvert) {
					List<String> selectedFields = list.getSelectedValuesList();

					if(selectedFields.size() < 1)
						throw new ApplicationException(String.format("ERROR: No fields were selected."));
					
					/////
					
					Runnable runner = new Runnable() {
						@Override
						public void run() {

							try {
								disableUi();

								if(rdbtnAutoDetectNames.isSelected())
									companyKeywords = Lookup.getCompanykeywords();

								List<Record> recordList = UiController.getUserData().getRecordList();

								progressBar.setValue(0);
								progressBar.setMaximum(recordList.size() * selectedFields.size());

								for(int fieldIndex = 0; fieldIndex < selectedFields.size(); ++fieldIndex) {
									String selectedField = selectedFields.get(fieldIndex);
									boolean isDfField = false;
									boolean isInField = false;
									int inIndexToUse = -1;

									for(String dfField : UiController.getUserData().getDfHeaders()) {
										if(selectedField.equalsIgnoreCase(dfField)) {
											isDfField = true;
											break;
										}
									}

									if(!isDfField) {
										for(int i = 0; i < UiController.getUserData().getInHeaders().length; ++i) {
											if(UiController.getUserData().getInHeaders()[i].equalsIgnoreCase(selectedField)) {
												isInField = true;
												inIndexToUse = i;
												break;
											}
										}
									}

									for(int j = 0; j < recordList.size(); ++j) {
										Record record = recordList.get(j);
										if(isDfField) {
											String oldValue = UserData.getRecordFieldByName(selectedField, record);
											oldValue = convertCasing(oldValue);
											UserData.setRecordFieldByName(selectedField, oldValue, record);
										} else if(isInField) {
											String[] array = record.getDfInData();
											String oldValue = array[inIndexToUse];
											oldValue = convertCasing(oldValue);
											array[inIndexToUse] = oldValue;
											record.setDfInData(array);
										}

										progressBar.setValue(progressBar.getValue() + 1);

									}
								}

								UiController.displayMessage("Success", "Casing conversion complete", JOptionPane.INFORMATION_MESSAGE);
								//JOptionPane.showMessageDialog(CasingConversionDialog.this, "Casing conversion complete", "Success", JOptionPane.INFORMATION_MESSAGE);

							} catch (Exception e) {
								UiController.handle(e);
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								enableUi();
								progressBar.setValue(progressBar.getMaximum());
							}
						}
					};

					processThread = new Thread(runner, "Code Executer");
					processThread.start();
					
					////

					
				}
			} catch (Exception err) {
				UiController.handle(err);
			} finally {
				enableUi();
				progressBar.setValue(progressBar.getMaximum());
			}
		}
	}

	private void disableUi() {
		list.setEnabled(false);
		scrollPane.setEnabled(false);
		btnConvert.setEnabled(false);
		rdbtnPersonNames.setEnabled(false);
		rdbtnCompanyNames.setEnabled(false);
		rdbtnAutoDetectNames.setEnabled(false);
		rdbtnLowerCase.setEnabled(false);
		rdbtnUpperCase.setEnabled(false);
		rdbtnProperCase.setEnabled(false);
		chckbxCaseIfLowerOrUpper.setEnabled(false);
	}

	private void enableUi() {
		list.setEnabled(true);
		scrollPane.setEnabled(true);
		btnConvert.setEnabled(true);
		rdbtnPersonNames.setEnabled(true);
		rdbtnCompanyNames.setEnabled(true);
		rdbtnAutoDetectNames.setEnabled(true);
		rdbtnLowerCase.setEnabled(true);
		rdbtnUpperCase.setEnabled(true);
		rdbtnProperCase.setEnabled(true);
		chckbxCaseIfLowerOrUpper.setEnabled(true);
	}


	private String convertCasing(String value) {
		if(chckbxCaseIfLowerOrUpper.isSelected())
			if(!value.equals(value.toLowerCase()) && !value.equals(value.toUpperCase()))
				return value;
		
		if(rdbtnPersonNames.isSelected())
			return Common.caseName(value);
		else if (rdbtnCompanyNames.isSelected())
			return Common.caseCompany(value);
		else if (rdbtnUpperCase.isSelected())
			return value.toUpperCase();
		else if (rdbtnLowerCase.isSelected())
			return value.toLowerCase();
		else if (rdbtnProperCase.isSelected())
			return Common.caseProper(value);
		else if(rdbtnAutoDetectNames.isSelected()) {
			boolean isCompany = false;

			for(int j = 0; j < companyKeywords.length; ++j) {
				String keyword = " " + companyKeywords[j][0] + " ";
				String recordValue = (" " + value.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "") + " ");
				if(!recordValue.isBlank() && recordValue.contains(keyword)) {
					isCompany = true;
					break;
				}
			}

			if(value.matches("\\d"))
				isCompany = true;

			if(isCompany)
				return Common.caseCompany(value);
			else
				return Common.caseName(value);

		}

		return value;
	}


}
