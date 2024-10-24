package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;

import com.github.lgooddatepicker.components.DatePicker;
import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Validators;

import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTabbedPane;
import java.awt.Color;

@SuppressWarnings("serial")
public class RegisteredMailDialog extends JDialog {
	
	private File registeredMailFile;

	// ui internal
	private final JPanel contentPanel = new JPanel();
	private JTabbedPane tabbedPane;
	private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXX";
	private JSeparator separator;
	private JButton btnNext;
	private JButton btnBack;
	private ArrayList<JComboBox<String>> comboBoxList = new ArrayList<>();
	private HashSet<String> usedFields = new HashSet<>();

	// Service Options
	private JPanel panelServiceOptions;
	private JLabel lblParcelService;
	private JComboBox<String> comboBoxService;
	private JLabel lblDropDate;
	private DatePicker datePicker;
	private JTextField textFieldWeight;
	private JLabel lblWeight;

	public enum registeredMailService {
		LETTERMAIL_STANDARD("Lettermail STD", 20),
		LETTERMAIL_MACHINEABLE("Lettermail MACH", 1861),
		LETTERMAIL_OTHER("Lettermail OTH", 2);

		String name;
		int serviceCode;

		private registeredMailService(String name, int serviceCode) {
			this.name = name;
			this.serviceCode = serviceCode;
		}

		public String getName() {
			return name;
		}

		public int getServiceCode() {
			return serviceCode;
		}

	};


	public static final int TAB_SERVICE = 0;
	public static final int TAB_CUSTOMER = 1;
	public static final int TAB_COMPLETE = 2;

	String[] parcelServices = new String[]{
			registeredMailService.LETTERMAIL_STANDARD.getName(),
			registeredMailService.LETTERMAIL_MACHINEABLE.getName(),
			registeredMailService.LETTERMAIL_OTHER.getName()
	};

	// Customer Information
	private JPanel panelCustomerInfo;
	private JLabel lblFirstName;
	private JComboBox<String> comboBoxFirstName;
	private JLabel lblLastName;
	private JComboBox<String> comboBoxLastName;
	private JLabel lblTelephone;
	private JComboBox<String> comboBoxTelephone;
	private JLabel lblEmail;
	private JComboBox<String> comboBoxEmail;

	// Complete
	private JPanel panelComplete;
	private JButton btnMake;
	private JLabel lblCompany;
	private JComboBox<String> comboBoxCompany;


	public RegisteredMailDialog(JFrame frame) {

		setResizable(false);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("Parcel Maker");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new LineBorder(new Color(192, 192, 192)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 20 28 20 28, gap 0", "[62px:n:62px][42px:n:42px][22px:n:22px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[100px:n:100px][6px:n:6px][36px:n:36px][4px:n:4px][36px:n:36px][28px:n:28px][28px:n:28px][28px:n:28px][10px:n:10px][28px:n:28px]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPanel.add(tabbedPane, "cell 0 0 12 1,grow");

		// SERVICE OPTIONS
		panelServiceOptions = new JPanel();
		panelServiceOptions.setBorder(null);
		tabbedPane.addTab("1. Service Options", null, panelServiceOptions, null);
		panelServiceOptions.setLayout(
				new MigLayout("insets 0, gap 0", "[62px:n:62px][42px:n:42px][22px:n:22px][28px:n:28px][62px:n:62px][31px:n:31px][31px:n:31px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[46px:n:46px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px]"));

		lblParcelService = new JLabel("Service");
		lblParcelService.setForeground(Color.BLACK);
		lblParcelService.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(lblParcelService, "cell 0 1 3 1");
		
		lblDropDate = new JLabel("Drop Date");
		lblDropDate.setForeground(Color.BLACK);
		lblDropDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(lblDropDate, "cell 4 1");
		
		lblWeight = new JLabel("Weight (g)");
		lblWeight.setForeground(Color.BLACK);
		lblWeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(lblWeight, "cell 11 1 2 1");

		comboBoxService = new JComboBox<String>(new DefaultComboBoxModel<String>(parcelServices));
		comboBoxService.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(comboBoxService, "cell 0 2 3 1,grow");
		comboBoxService.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxService.setSelectedIndex(0);
		
		datePicker = new DatePicker();
		datePicker.getComponentDateTextField().setEditable(false);
		datePicker.getComponentDateTextField().setFont(new Font("Segoe UI", Font.PLAIN, 11));
		datePicker.getComponentToggleCalendarButton().setFont(new Font("Segoe UI", Font.PLAIN, 11));
		datePicker.getComponentToggleCalendarButton().setText("    Pick date    ");
		panelServiceOptions.add(datePicker, "cell 4 2 6 1,grow");

		datePicker.setDateToToday();

		textFieldWeight = new JTextField();
		textFieldWeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(textFieldWeight, "cell 11 2 2 1,grow");
		((AbstractDocument) textFieldWeight.getDocument()).setDocumentFilter(new DecimalDocumentFilter(7));

		// CUSTOMER INFO
		panelCustomerInfo = new JPanel();
		panelCustomerInfo.setBorder(null);
		tabbedPane.addTab("2. Customer Information", null, panelCustomerInfo, null);
		tabbedPane.setEnabledAt(1, false);
		panelCustomerInfo.setLayout(
				new MigLayout("insets 0, gap 0", "[62px:n:62px][42px:n:42px][22px:n:22px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px,grow][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[46px:n:46px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px]"));

		lblFirstName = new JLabel("First Name");
		lblFirstName.setForeground(Color.BLACK);
		lblFirstName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblFirstName, "cell 0 1 2 1");

		lblLastName = new JLabel("Last Name");
		lblLastName.setForeground(Color.BLACK);
		lblLastName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblLastName, "cell 4 1 2 1");
		
		lblCompany = new JLabel("Company");
		lblCompany.setForeground(Color.BLACK);
		lblCompany.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblCompany, "cell 7 1 2 1");

		lblTelephone = new JLabel("Telephone");
		lblTelephone.setForeground(Color.BLACK);
		lblTelephone.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblTelephone, "cell 10 1 2 1");
		
		lblEmail = new JLabel("Email");
		lblEmail.setForeground(Color.BLACK);
		lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblEmail, "cell 0 4 3 1");
		
		comboBoxFirstName = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxFirstName.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxFirstName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxFirstName.setSelectedIndex(-1);
		comboBoxFirstName.setName(lblFirstName.getText());
		comboBoxFirstName.addKeyListener(new ComboBoxDeleteHandler());

		panelCustomerInfo.add(comboBoxFirstName, "cell 0 2 3 1,grow");

		comboBoxLastName = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLastName.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLastName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLastName.setSelectedIndex(-1);
		comboBoxLastName.setName(lblLastName.getText());
		comboBoxLastName.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxLastName, "cell 4 2 2 1,grow");

		comboBoxTelephone = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxTelephone.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxTelephone.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxTelephone.setSelectedIndex(-1);
		comboBoxTelephone.setName(lblTelephone.getText());
		comboBoxTelephone.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxTelephone, "cell 10 2 2 1,grow");
		
		comboBoxCompany = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxCompany.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxCompany.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxCompany.setSelectedIndex(-1);
		comboBoxCompany.setName(lblCompany.getText());
		comboBoxCompany.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxCompany, "cell 7 2 2 1,grow");

		comboBoxEmail = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxEmail.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxEmail.setSelectedIndex(-1);
		comboBoxEmail.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxEmail, "cell 0 5 3 1,grow");
		comboBoxEmail.setName(lblEmail.getText());

		//COMPLETE
		panelComplete = new JPanel();
		panelComplete.setBorder(null);
		tabbedPane.addTab("3. Complete Registered Mail", null, panelComplete, null);
		tabbedPane.setEnabledAt(2, false);
		panelComplete.setLayout(
				new MigLayout("insets 0, gap 0", "[62px:n:62px][42px:n:42px][22px:n:22px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[10px:n:10px][36px:n:36px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px]"));

		btnMake = new JButton("Make Registered Mail File");
		btnMake.addActionListener(new MakeRegisteredMailFileButtonHandler());
		panelComplete.add(btnMake, "cell 1 5 10 1,grow");
		btnMake.setFont(new Font("Segoe UI", Font.PLAIN, 14));


		// Main
		separator = new JSeparator();
		contentPanel.add(separator, "cell 0 7 12 1,growx");

		btnBack = new JButton("<< Back");
		btnBack.setEnabled(false);
		btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnBack.addActionListener(new BtnNavHandler());
		contentPanel.add(btnBack, "cell 0 9 3 1,grow");

		btnNext = new JButton("Next >>");
		btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnNext.addActionListener(new BtnNavHandler());
		contentPanel.add(btnNext, "cell 10 9 2 1,grow");

		fillComboBoxList();
		addComboBoxMappingListener();

		pack();
		setLocationRelativeTo(frame);
		getContentPane().requestFocusInWindow();
	}

	private void fillComboBoxList() {
		comboBoxList.add(comboBoxFirstName);
		comboBoxList.add(comboBoxLastName);
		comboBoxList.add(comboBoxTelephone);
		comboBoxList.add(comboBoxEmail);
	}

	private void addComboBoxMappingListener() {
		comboBoxFirstName.addItemListener(new ComboBoxMappingListener());
		comboBoxLastName.addItemListener(new ComboBoxMappingListener());
		comboBoxTelephone.addItemListener(new ComboBoxMappingListener());
		comboBoxEmail.addItemListener(new ComboBoxMappingListener());
	}

	private class ComboBoxMappingListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent event) {
			try {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					@SuppressWarnings("unchecked")
					JComboBox<String> comboBox = (JComboBox<String>) event.getSource();

					// remove unused fields
					Object[] fields = usedFields.toArray();
					for(int i = fields.length - 1; i >= 0; --i) {
						boolean wasFound = false;
						for(JComboBox<String> cb : comboBoxList) {
							if(cb.getSelectedIndex() > -1) {
								if(fields[i].toString().equalsIgnoreCase(cb.getSelectedItem().toString())) {
									wasFound = true;
									break;
								}
							}
						}

						if(!wasFound) 
							usedFields.remove(fields[i].toString());
					}

					// Try to map the field
					if(comboBox.getSelectedIndex() > -1) {
						if(!usedFields.add(comboBox.getSelectedItem().toString())) {
							comboBox.setSelectedIndex(-1);
							throw new ApplicationException(String.format("Fields cannot hold duplicate values."));
						}
					}

				}
			} catch (Exception err) {
				UiController.handle(err);
			}

		}
	}

	private class MakeRegisteredMailFileButtonHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnMake) {
					
					while(true) {
						// time to save the file
						JFileChooser fileChooser = new JFileChooser(UserPrefs.getLastUsedFolder());
						int returnVal = fileChooser.showSaveDialog(RegisteredMailDialog.this);

						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = fileChooser.getSelectedFile();
							String fileName = file.getAbsolutePath();

							if (fileName.toLowerCase().endsWith(".txt"))
								fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".txt"));

							File finalFile  = new File(fileName + ".txt");

							if (finalFile.exists() && finalFile.isFile()) {
								int buttonPressed = JOptionPane.showConfirmDialog(RegisteredMailDialog.this, "File already exists, would you like to overwrite it?",
										"File exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

								if (buttonPressed == JOptionPane.YES_OPTION) {
									registeredMailFile = finalFile;
									break;
								}
							} else {
								registeredMailFile = finalFile;
								break;
							}
							
							
						}
					}

					RegisteredMailDialog.this.dispose();
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}


	private class BtnNavHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnNext) {
					if(tabbedPane.getSelectedIndex() < tabbedPane.getTabCount() - 1) {
						validateCurrentTab(tabbedPane.getSelectedIndex());
						tabbedPane.setEnabledAt(tabbedPane.getSelectedIndex(), false);
						tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
						tabbedPane.setEnabledAt(tabbedPane.getSelectedIndex(), true);
					}

				} else if (e.getSource() == btnBack) {

					if(tabbedPane.getSelectedIndex() > 0) {
						tabbedPane.setEnabledAt(tabbedPane.getSelectedIndex(), false);
						tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() - 1);
						tabbedPane.setEnabledAt(tabbedPane.getSelectedIndex(), true);
					}

				}

				if(tabbedPane.getSelectedIndex() < tabbedPane.getTabCount() - 1)
					btnNext.setEnabled(true);
				else 
					btnNext.setEnabled(false);

				if(tabbedPane.getSelectedIndex() > 0)
					btnBack.setEnabled(true);
				else 
					btnBack.setEnabled(false);


			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}

	private void validateCurrentTab(int tabIndex) throws ApplicationException {
		switch(tabbedPane.getSelectedIndex()) {
		case TAB_SERVICE:
			if(datePicker.getDateStringOrEmptyString().length() == 0)
				throw new ApplicationException(String.format("%s must be selected", lblDropDate.getText()));
			
			if(textFieldWeight.getText().length() == 0)
				throw new ApplicationException(String.format("%s must be entered", lblWeight.getText()));
			
			if(!Validators.isNumber(textFieldWeight.getText()))
				throw new ApplicationException(String.format("%s is invalid.", lblWeight.getText()));
				
			if(Double.parseDouble(textFieldWeight.getText()) < 1)
				throw new ApplicationException(String.format("%s must be >= 1", lblWeight.getText()));
				
			break;
		case TAB_CUSTOMER:
			if(comboBoxFirstName.getSelectedIndex() == -1 && comboBoxLastName.getSelectedIndex() == -1 && comboBoxCompany.getSelectedIndex() == -1)
				UiController.displayMessage("Warning", "No customer information has been mapped", JOptionPane.WARNING_MESSAGE);
			break;
		case TAB_COMPLETE:
			break;
		default:
			break;
		}
	}
	
	private class ComboBoxDeleteHandler implements KeyListener {
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == 127 || key == 8) {
				if(e.getSource() instanceof JComboBox) {
					@SuppressWarnings("rawtypes")
					JComboBox comboBox = (JComboBox) e.getSource();
					comboBox.setSelectedIndex(-1);
				}
			}
		}

		public void keyTyped(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {}
	}

	public File getRegisteredMailFile() {
		return registeredMailFile;
	}

	public void setRegisteredMailFile(File registeredMailFile) {
		this.registeredMailFile = registeredMailFile;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public void setTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

	public JSeparator getSeparator() {
		return separator;
	}

	public void setSeparator(JSeparator separator) {
		this.separator = separator;
	}

	public JButton getBtnNext() {
		return btnNext;
	}

	public void setBtnNext(JButton btnNext) {
		this.btnNext = btnNext;
	}

	public JButton getBtnBack() {
		return btnBack;
	}

	public void setBtnBack(JButton btnBack) {
		this.btnBack = btnBack;
	}

	public ArrayList<JComboBox<String>> getComboBoxList() {
		return comboBoxList;
	}

	public void setComboBoxList(ArrayList<JComboBox<String>> comboBoxList) {
		this.comboBoxList = comboBoxList;
	}

	public HashSet<String> getUsedFields() {
		return usedFields;
	}

	public void setUsedFields(HashSet<String> usedFields) {
		this.usedFields = usedFields;
	}

	public JPanel getPanelServiceOptions() {
		return panelServiceOptions;
	}

	public void setPanelServiceOptions(JPanel panelServiceOptions) {
		this.panelServiceOptions = panelServiceOptions;
	}

	public JLabel getLblParcelService() {
		return lblParcelService;
	}

	public void setLblParcelService(JLabel lblParcelService) {
		this.lblParcelService = lblParcelService;
	}

	public JComboBox<String> getComboBoxService() {
		return comboBoxService;
	}

	public void setComboBoxService(JComboBox<String> comboBoxService) {
		this.comboBoxService = comboBoxService;
	}

	public JLabel getLblDropDate() {
		return lblDropDate;
	}

	public void setLblDropDate(JLabel lblDropDate) {
		this.lblDropDate = lblDropDate;
	}

	public DatePicker getDatePicker() {
		return datePicker;
	}

	public void setDatePicker(DatePicker datePicker) {
		this.datePicker = datePicker;
	}

	public JTextField getTextFieldWeight() {
		return textFieldWeight;
	}

	public void setTextFieldWeight(JTextField textFieldWeight) {
		this.textFieldWeight = textFieldWeight;
	}

	public JLabel getLblWeight() {
		return lblWeight;
	}

	public void setLblWeight(JLabel lblWeight) {
		this.lblWeight = lblWeight;
	}

	public JPanel getPanelCustomerInfo() {
		return panelCustomerInfo;
	}

	public void setPanelCustomerInfo(JPanel panelCustomerInfo) {
		this.panelCustomerInfo = panelCustomerInfo;
	}

	public JLabel getLblFirstName() {
		return lblFirstName;
	}

	public void setLblFirstName(JLabel lblFirstName) {
		this.lblFirstName = lblFirstName;
	}

	public JComboBox<String> getComboBoxFirstName() {
		return comboBoxFirstName;
	}

	public void setComboBoxFirstName(JComboBox<String> comboBoxFirstName) {
		this.comboBoxFirstName = comboBoxFirstName;
	}

	public JLabel getLblLastName() {
		return lblLastName;
	}

	public void setLblLastName(JLabel lblLastName) {
		this.lblLastName = lblLastName;
	}

	public JComboBox<String> getComboBoxLastName() {
		return comboBoxLastName;
	}

	public void setComboBoxLastName(JComboBox<String> comboBoxLastName) {
		this.comboBoxLastName = comboBoxLastName;
	}

	public JLabel getLblTelephone() {
		return lblTelephone;
	}

	public void setLblTelephone(JLabel lblTelephone) {
		this.lblTelephone = lblTelephone;
	}

	public JComboBox<String> getComboBoxTelephone() {
		return comboBoxTelephone;
	}

	public void setComboBoxTelephone(JComboBox<String> comboBoxTelephone) {
		this.comboBoxTelephone = comboBoxTelephone;
	}

	public JLabel getLblEmail() {
		return lblEmail;
	}

	public void setLblEmail(JLabel lblEmail) {
		this.lblEmail = lblEmail;
	}

	public JComboBox<String> getComboBoxEmail() {
		return comboBoxEmail;
	}

	public void setComboBoxEmail(JComboBox<String> comboBoxEmail) {
		this.comboBoxEmail = comboBoxEmail;
	}

	public JPanel getPanelComplete() {
		return panelComplete;
	}

	public void setPanelComplete(JPanel panelComplete) {
		this.panelComplete = panelComplete;
	}

	public JButton getBtnMake() {
		return btnMake;
	}

	public void setBtnMake(JButton btnMake) {
		this.btnMake = btnMake;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public JLabel getLblCompany() {
		return lblCompany;
	}

	public void setLblCompany(JLabel lblCompany) {
		this.lblCompany = lblCompany;
	}

	public JComboBox<String> getComboBoxCompany() {
		return comboBoxCompany;
	}

	public void setComboBoxCompany(JComboBox<String> comboBoxCompany) {
		this.comboBoxCompany = comboBoxCompany;
	}

	

}
