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
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class EncodingCorrectionDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JList<String> list;
	private JLabel lblTitle;
	private JLabel lblDescription;
	private JButton btnCorrect;
	private Map<String, String> encodingMap;
	private JProgressBar progressBar;
	private JScrollPane scrollPane;
	
	Thread processThread = null;
	private JLabel lblNewLabel;
	private JSeparator separator_1;
	private JLabel lblExampleDescription;
	private JSeparator separator;

	public EncodingCorrectionDialog(JFrame frame) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setTitle("Encoding Correction");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 245));
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 10 28 20 28, gap 0", "[184px:n:184px][36px:n:36px][68px:n:68px][68px:n:68px][14px:n:14px][68px:n:68px][68px:n:68px]", "[36px:n:36px][28px:n:28px,grow][28px:n:28px][48px:n:48px][36px:n:36px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][36px:n:36px][28px:n:28px]"));

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPanel.add(scrollPane, "cell 0 0 1 9,grow");

		list = new JList<>();
		list.setForeground(Color.BLACK);
		list.setListData(UiController.getUserData().getExportHeaders());
		list.setSelectedIndex(0);
		list.setFixedCellHeight(32);
		list.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		scrollPane.setViewportView(list);

		lblTitle = new JLabel("Encoding Correction");
		lblTitle.setForeground(Theme.TITLE_COLOR);
		lblTitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblTitle, "cell 2 0 5 1,aligny center");

		lblDescription = new JLabel("<html>Choose fields from the left to fix characters incorrectly encoded from UTF-8 to Windows-1252. Select multiple fields by holding down the Ctrl or \r\nShift key.</html>");
		lblDescription.setForeground(Color.BLACK);
		lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblDescription, "cell 2 1 5 2,alignx left,aligny bottom");
		
				btnCorrect = new JButton("Correct");
				btnCorrect.setBackground(Color.WHITE);
				btnCorrect.setFont(new Font("Segoe UI", Font.PLAIN, 14));
				btnCorrect.addActionListener(new CorrectButtonHandler());
						
						separator_1 = new JSeparator();
						contentPanel.add(separator_1, "cell 2 3 5 1,growx");
						
						lblNewLabel = new JLabel("Example");
						lblNewLabel.setForeground(Theme.TITLE_COLOR);
						lblNewLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
						contentPanel.add(lblNewLabel, "cell 2 4 2 1,aligny top");
						
						lblExampleDescription = new JLabel("Ã© wil be converted to é");
						lblExampleDescription.setForeground(Color.BLACK);
						lblExampleDescription.setFont(new Font("Segoe UI", Font.PLAIN, 11));
						contentPanel.add(lblExampleDescription, "cell 2 5 5 1,aligny top");
				
				separator = new JSeparator();
				contentPanel.add(separator, "cell 0 9 7 1,growx");
				
				progressBar = new JProgressBar();
				progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
				progressBar.setStringPainted(true);
				contentPanel.add(progressBar, "cell 0 10 4 1,grow");
				contentPanel.add(btnCorrect, "cell 5 10 2 1,grow");

		encodingMap = new LinkedHashMap<>();
		fillHashMap();

		pack();
	    setLocationRelativeTo(frame);
		getContentPane().requestFocusInWindow();
	}

	private class CorrectButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnCorrect) {
					List<String> selectedFields = list.getSelectedValuesList();

					if(selectedFields.size() < 1)
						throw new ApplicationException(String.format("ERROR: No fields were selected."));
					
					///
					
					Runnable runner = new Runnable() {
						@Override
						public void run() {

							try {
								disableUi();

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
											oldValue = correctEncoding(oldValue);
											UserData.setRecordFieldByName(selectedField, oldValue, record);
										} else if(isInField) {
											String[] array = record.getDfInData();
											String oldValue = array[inIndexToUse];
											oldValue = correctEncoding(oldValue);
											array[inIndexToUse] = oldValue;
											record.setDfInData(array);
										}

										progressBar.setValue(progressBar.getValue() + 1);

									}
								}

								UiController.displayMessage("Success", "Character encoding correction complete", JOptionPane.INFORMATION_MESSAGE);
								//JOptionPane.showMessageDialog(EncodingCorrectionDialog.this, "Character encoding correction complete", "Success", JOptionPane.INFORMATION_MESSAGE);

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
					
					///
					
					
				}
			} catch (Exception err) {
				UiController.handle(err);
			} finally {
				enableUi();
			}
		}
	}

	
	private void disableUi() {
		list.setEnabled(false);
		scrollPane.setEnabled(false);
		btnCorrect.setEnabled(false);
	}
	
	private void enableUi() {
		list.setEnabled(true);
		scrollPane.setEnabled(true);
		btnCorrect.setEnabled(true);
	}
	
	private void fillHashMap() {
		encodingMap.put("â€š", "‚");
		encodingMap.put("â€ž", "„");
		encodingMap.put("â€˜", "‘");
		encodingMap.put("â€™", "’");
		encodingMap.put("â€œ", "“");
		encodingMap.put("â€“", "–");
		encodingMap.put("â€”", "—");
		encodingMap.put("â„¢", "™");
		encodingMap.put("Ë†", "ˆ");
		encodingMap.put("â€", "”");
		encodingMap.put("Ëœ", "˜");
		encodingMap.put("Å¡", "š");
		encodingMap.put("Å¾", "ž");
		encodingMap.put("Å¸", "Ÿ");
		encodingMap.put("Â¡", "¡");
		encodingMap.put("Â¨", "¨");
		encodingMap.put("Â©", "©");
		encodingMap.put("Â­", "­");
		encodingMap.put("Â´", "´");
		encodingMap.put("Â·", "·");
		encodingMap.put("Ã€", "À");
		encodingMap.put("Ã‚", "Â");
		encodingMap.put("Ãƒ", "Ã");
		encodingMap.put("Ã„", "Ä");
		encodingMap.put("Ã…", "Å");
		encodingMap.put("Ã‡", "Ç");
		encodingMap.put("Ãˆ", "È");
		encodingMap.put("Ã‰", "É");
		encodingMap.put("ÃŠ", "Ê");
		encodingMap.put("Ã‹", "Ë");
		encodingMap.put("ÃŒ", "Ì");
		encodingMap.put("ÃŽ", "Î");
		encodingMap.put("Ã‘", "Ñ");
		encodingMap.put("Ã’", "Ò");
		encodingMap.put("Ã“", "Ó");
		encodingMap.put("Ã”", "Ô");
		encodingMap.put("Ã•", "Õ");
		encodingMap.put("Ã–", "Ö");
		encodingMap.put("Ã™", "Ù");
		encodingMap.put("Ã›", "Û");
		encodingMap.put("Ã¡", "á");
		encodingMap.put("Ã¢", "â");
		encodingMap.put("Ã£", "ã");
		encodingMap.put("Ã¤", "ä");
		encodingMap.put("Ã¥", "å");
		encodingMap.put("Ã§", "ç");
		encodingMap.put("Ã¨", "è");
		encodingMap.put("Ã©", "é");
		encodingMap.put("Ãª", "ê");
		encodingMap.put("Ã«", "ë");
		encodingMap.put("Ã¬", "ì");
		encodingMap.put("Ã®", "î");
		encodingMap.put("Ã¯", "ï");
		encodingMap.put("Ã°", "ð");
		encodingMap.put("Ã±", "ñ");
		encodingMap.put("Ã²", "ò");
		encodingMap.put("Ã³", "ó");
		encodingMap.put("Ã´", "ô");
		encodingMap.put("Ãµ", "õ");
		encodingMap.put("Ã¶", "ö");
		encodingMap.put("Ã¹", "ù");
		encodingMap.put("Ãº", "ú");
		encodingMap.put("Ã»", "û");
		encodingMap.put("Ã¼", "ü");
		encodingMap.put("Ã½", "ý");
		encodingMap.put("Ã¿", "ÿ");
	}
	
	/*
	private void fillHashMap() {
		encodingMap.put("Ã€", "À");
		encodingMap.put("Ã‚", "Â");
		encodingMap.put("Ãƒ", "Ã");
		encodingMap.put("Ã„", "Ä");
		encodingMap.put("Ã…", "Å");
		encodingMap.put("Ã†", "Æ");
		encodingMap.put("Ã‡", "Ç");
		encodingMap.put("Ãˆ", "È");
		encodingMap.put("Ã‰", "É");
		encodingMap.put("ÃŠ", "Ê");
		encodingMap.put("Ã‹", "Ë");
		encodingMap.put("ÃŽ", "Î");
		encodingMap.put("Ã‘", "Ñ");
		encodingMap.put("Ã’", "Ò");
		encodingMap.put("Ã“", "Ó");
		encodingMap.put("Ã”", "Ô");
		encodingMap.put("Ã•", "Õ");
		encodingMap.put("Ã–", "Ö");
		encodingMap.put("Ã—", "×");
		encodingMap.put("Ã˜", "Ø");
		encodingMap.put("Ã™", "Ù");
		encodingMap.put("Ã›", "Û");
		encodingMap.put("Ãœ", "Ü");
		encodingMap.put("ÃŸ", "ß");
		encodingMap.put("Ã¡", "á");
		encodingMap.put("Ã¢", "â");
		encodingMap.put("Ã£", "ã");
		encodingMap.put("Ã¤", "ä");
		encodingMap.put("Ã¥", "å");
		encodingMap.put("Ã¦", "æ");
		encodingMap.put("Ã§", "ç");
		encodingMap.put("Ã¨", "è");
		encodingMap.put("Ã©", "é");
		encodingMap.put("Ãª", "ê");
		encodingMap.put("Ã«", "ë");
		encodingMap.put("Ã¬", "ì");
		encodingMap.put("Ã®", "î");
		encodingMap.put("Ã¯", "ï");
		encodingMap.put("Ã°", "ð");
		encodingMap.put("Ã±", "ñ");
		encodingMap.put("Ã²", "ò");
		encodingMap.put("Ã³", "ó");
		encodingMap.put("Ã´", "ô");
		encodingMap.put("Ãµ", "õ");
		encodingMap.put("Ã¶", "ö");
		encodingMap.put("Ã·", "÷");
		encodingMap.put("Ã¸", "ø");
		encodingMap.put("Ã¹", "ù");
		encodingMap.put("Ãº", "ú");
		encodingMap.put("Ã»", "û");
		encodingMap.put("Ã¼", "ü");
		encodingMap.put("Ã½", "ý");
		encodingMap.put("Ã¾", "þ");
		encodingMap.put("Ã¿", "ÿ");
		encodingMap.put("Ă§", "ç");
		encodingMap.put("ĂŠ", "é");
		encodingMap.put("Ăť", "û");
		encodingMap.put("Ă´", "ô");
		encodingMap.put("ĂŽ", "î");
		encodingMap.put("ĂŞ", "ê");
		encodingMap.put("Ă˘", "â");
		encodingMap.put("Ă˛", "ò");
		encodingMap.put("ĂŹ", "ì");
		encodingMap.put("Ă¨", "è");
		encodingMap.put("ĂŻ", "ï");
		encodingMap.put("Ã", "à");
		encodingMap.put("Ă", "à");
	}*/
	
	/*private void oldFillHashMap() {
		// original bad values
		encodingMap.put("Ã§", "ç");
		encodingMap.put("Ã©", "é");
		encodingMap.put("Ã»", "û");
		encodingMap.put("Ã´", "ô");
		encodingMap.put("Ã®", "î");
		encodingMap.put("Ãª", "ê");
		encodingMap.put("Ã¢", "â");
		encodingMap.put("Ã¹", "ù");
		encodingMap.put("Ã²", "ò");
		encodingMap.put("Ã¬", "ì");
		encodingMap.put("Ã¨", "è");
		encodingMap.put("Ã ", "à");
		encodingMap.put("Ã¼", "ü");
		encodingMap.put("Ã¯", "ï");
		encodingMap.put("Ã«", "ë");
		encodingMap.put("ã§", "ç");
		encodingMap.put("ã©", "é");
		encodingMap.put("ã»", "û");
		encodingMap.put("ã´", "ô");
		encodingMap.put("ã®", "î");
		encodingMap.put("ãª", "ê");
		encodingMap.put("ã¢", "â");
		encodingMap.put("ã¹", "ù");
		encodingMap.put("ã²", "ò");
		encodingMap.put("ã¬", "ì");
		encodingMap.put("ã¨", "è");
		encodingMap.put("ã ", "à");
		encodingMap.put("ã¼", "ü");
		encodingMap.put("ã¯", "ï");
		encodingMap.put("ã«", "ë");
		
		// automatic encoded values
		encodingMap.put("Ă§", "ç");
		encodingMap.put("ĂŠ", "é");
		encodingMap.put("Ăť", "û");
		encodingMap.put("Ă´", "ô");
		encodingMap.put("ĂŽ", "î");
		encodingMap.put("ĂŞ", "ê");
		encodingMap.put("Ă˘", "â");
		encodingMap.put("Ăš", "ù");
		encodingMap.put("Ă˛", "ò");
		encodingMap.put("ĂŹ", "ì");
		encodingMap.put("Ă¨", "è");
		encodingMap.put("Ă ", "à");
		encodingMap.put("Ăź", "ü");
		encodingMap.put("ĂŻ", "ï");
		encodingMap.put("ĂŤ", "ë");
		encodingMap.put("ă§", "ç");
		encodingMap.put("ăŠ", "é");
		encodingMap.put("ăť", "û");
		encodingMap.put("ă´", "ô");
		encodingMap.put("ăŽ", "î");
		encodingMap.put("ăŞ", "ê");
		encodingMap.put("ă˘", "â");
		encodingMap.put("ăš", "ù");
		encodingMap.put("ă˛", "ò");
		encodingMap.put("ăŹ", "ì");
		encodingMap.put("ă¨", "è");
		encodingMap.put("ă ", "à");
		encodingMap.put("ăź", "ü");
		encodingMap.put("ăŻ", "ï");
		encodingMap.put("ăŤ", "ë");

	}*/
	
	private String correctEncoding(String value) {
		for(Map.Entry<String, String> entry : encodingMap.entrySet()) {
			value = value.replaceAll("(?i)" + Pattern.quote(entry.getKey()), Matcher.quoteReplacement(entry.getValue()));
		}
		
		return value;
	}


}
