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

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.UserPrefs;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTabbedPane;
import java.awt.Color;

@SuppressWarnings("serial")
public class ParcelDialog extends JDialog {
	
	private File parcelFile;

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
	private JComboBox<String> comboBoxParcelService;
	private JLabel lblParcelReferenceID;
	private JTextField textFieldReference;
	private JLabel lblAppendField;
	private JComboBox<String> comboBoxAppendToReference;
	private ButtonGroup mainOptionsGroup;
	private JCheckBox chckbxLeaveAtDoor;
	private JCheckBox chckbxSignatureRequired;
	private JCheckBox chckbxDoNotSafeDrop;
	private JCheckBox chckbxCardForPickup;

	public enum parcelService {
		XPRESSPOST("Xpresspost", 908),
		REGULAR_PARCEL("Regular", 966),
		EXPEDITED_PARCEL("Expedited", 967),
		PRIORITY("Priority", 1469),
		XPRESSPOST_USA("USA Xpresspost", 1917),
		EXPEDITED_PARCEL_USA("USA Expedited", 6470);

		String name;
		int serviceCode;

		private parcelService(String name, int serviceCode) {
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
	public static final int TAB_PACKAGE = 2;
	public static final int TAB_COMPLETE = 3;


	String[] parcelServices = new String[]{
			parcelService.XPRESSPOST.getName(),
			parcelService.REGULAR_PARCEL.getName(),
			parcelService.EXPEDITED_PARCEL.getName(),
			parcelService.PRIORITY.getName(),
	};

	// Customer Information
	private JPanel panelCustomerInfo;
	private JLabel lblPrefix;
	private JComboBox<String> comboBoxPrefix;
	private JLabel lblFirstName;
	private JComboBox<String> comboBoxFirstName;
	private JLabel lblLastName;
	private JComboBox<String> comboBoxLastName;
	private JLabel lblCompany;
	private JComboBox<String> comboBoxCompany;
	private JLabel lblTelephone;
	private JComboBox<String> comboBoxTelephone;
	private JLabel lblFax;
	private JComboBox<String> comboBoxFax;
	private JLabel lblEmail;
	private JComboBox<String> comboBoxEmail;

	// Package Dimensions
	private JPanel panelPackageDimensions;
	private JCheckBox chckbxUseStaticDimensions;
	private JLabel lblStaticWeight;
	private JTextField textFieldStaticWeight;
	private JLabel lblStaticLength;
	private JTextField textFieldStaticLength;
	private JLabel lblStaticWidth;
	private JTextField textFieldStaticWidth;
	private JLabel lblStaticHeight;
	private JTextField textFieldStaticHeight;
	private JLabel lblWeight;
	private JComboBox<String> comboBoxWeight;
	private JLabel lblLength;
	private JComboBox<String> comboBoxLength;
	private JLabel lblWidth;
	private JComboBox<String> comboBoxWidth;
	private JLabel lblHeight;
	private JComboBox<String> comboBoxHeight;

	// Complete
	private JPanel panelComplete;
	private JButton btnMake;

	public ParcelDialog(JFrame frame) {

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
				new MigLayout("insets 0, gap 0", "[62px:n:62px][42px:n:42px][22px:n:22px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[46px:n:46px][36px:n:36px][28px:n:28px][28px:n:28px][36px:n:36px]"));

		lblParcelService = new JLabel("Service");
		lblParcelService.setForeground(Color.BLACK);
		lblParcelService.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(lblParcelService, "cell 0 1 3 1");

		lblParcelReferenceID = new JLabel("Reference");
		lblParcelReferenceID.setForeground(Color.BLACK);
		lblParcelReferenceID.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(lblParcelReferenceID, "cell 4 1");

		lblAppendField = new JLabel("Append field value to reference");
		lblAppendField.setForeground(Color.BLACK);
		lblAppendField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(lblAppendField, "cell 7 1 5 1");

		comboBoxParcelService = new JComboBox<String>(new DefaultComboBoxModel<String>(parcelServices));
		comboBoxParcelService.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(comboBoxParcelService, "cell 0 2 3 1,grow");
		comboBoxParcelService.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxParcelService.setSelectedIndex(2);

		textFieldReference = new JTextField();
		textFieldReference.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelServiceOptions.add(textFieldReference, "cell 4 2 2 1,grow");
		textFieldReference.setDocument(new JTextFieldLimit(20));
		textFieldReference.setHorizontalAlignment(SwingConstants.LEFT);
		textFieldReference.setColumns(10);

		comboBoxAppendToReference = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxAppendToReference.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxAppendToReference.addKeyListener(new ComboBoxDeleteHandler());
		panelServiceOptions.add(comboBoxAppendToReference, "cell 7 2 5 1,grow");
		comboBoxAppendToReference.setSelectedIndex(-1);

		chckbxSignatureRequired = new JCheckBox("Signature required");
		chckbxSignatureRequired.setForeground(Color.BLACK);
		panelServiceOptions.add(chckbxSignatureRequired, "cell 0 4 3 1");
		chckbxSignatureRequired.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		chckbxCardForPickup = new JCheckBox("Card for pickup");
		chckbxCardForPickup.setForeground(Color.BLACK);
		panelServiceOptions.add(chckbxCardForPickup, "cell 4 4 2 1");
		chckbxCardForPickup.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		chckbxDoNotSafeDrop = new JCheckBox("Do not safe drop");
		chckbxDoNotSafeDrop.setForeground(Color.BLACK);
		panelServiceOptions.add(chckbxDoNotSafeDrop, "cell 7 4 2 1");
		chckbxDoNotSafeDrop.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		chckbxLeaveAtDoor = new JCheckBox("Leave at door");
		chckbxLeaveAtDoor.setForeground(Color.BLACK);
		panelServiceOptions.add(chckbxLeaveAtDoor, "cell 10 4 2 1");
		chckbxLeaveAtDoor.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		//SignatureCheckboxHandler
		chckbxSignatureRequired.addActionListener(new SignatureCheckboxHandler());
		chckbxLeaveAtDoor.addActionListener(new SignatureCheckboxHandler());

		// This allows us to deselect a button
		chckbxLeaveAtDoor.setSelected(true);
		mainOptionsGroup = new ToggleButtonGroup();
		mainOptionsGroup.add(chckbxCardForPickup);
		mainOptionsGroup.add(chckbxDoNotSafeDrop);
		mainOptionsGroup.add(chckbxLeaveAtDoor);

		// CUSTOMER INFO
		panelCustomerInfo = new JPanel();
		panelCustomerInfo.setBorder(null);
		tabbedPane.addTab("2. Customer Information", null, panelCustomerInfo, null);
		tabbedPane.setEnabledAt(1, false);
		panelCustomerInfo.setLayout(
				new MigLayout("insets 0, gap 0", "[62px:n:62px][42px:n:42px][22px:n:22px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[46px:n:46px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px]"));

		lblPrefix = new JLabel("Prefix");
		lblPrefix.setForeground(Color.BLACK);
		lblPrefix.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblPrefix, "cell 0 1 3 1");

		lblFirstName = new JLabel("First Name");
		lblFirstName.setForeground(Color.BLACK);
		lblFirstName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblFirstName, "cell 4 1 2 1");

		lblLastName = new JLabel("Last Name");
		lblLastName.setForeground(Color.BLACK);
		lblLastName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblLastName, "cell 7 1 2 1");

		lblCompany = new JLabel("Company");
		lblCompany.setForeground(Color.BLACK);
		lblCompany.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblCompany, "cell 10 1 2 1");

		comboBoxPrefix = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxPrefix.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxPrefix.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxPrefix.setSelectedIndex(-1);
		comboBoxPrefix.setName(lblPrefix.getText());
		comboBoxPrefix.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxPrefix, "cell 0 2 3 1,grow");

		comboBoxFirstName = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxFirstName.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxFirstName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxFirstName.setSelectedIndex(-1);
		comboBoxFirstName.setName(lblFirstName.getText());
		comboBoxFirstName.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxFirstName, "cell 4 2 2 1,grow");

		comboBoxLastName = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLastName.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLastName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLastName.setSelectedIndex(-1);
		comboBoxLastName.setName(lblLastName.getText());
		comboBoxLastName.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxLastName, "cell 7 2 2 1,grow");

		comboBoxCompany = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxCompany.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxCompany.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxCompany.setSelectedIndex(-1);
		comboBoxCompany.setName(lblCompany.getText());
		comboBoxCompany.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxCompany, "cell 10 2 2 1,grow");

		lblTelephone = new JLabel("Telephone");
		lblTelephone.setForeground(Color.BLACK);
		lblTelephone.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblTelephone, "cell 0 4 3 1");

		lblFax = new JLabel("Fax");
		lblFax.setForeground(Color.BLACK);
		lblFax.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblFax, "cell 4 4 2 1");

		lblEmail = new JLabel("Email");
		lblEmail.setForeground(Color.BLACK);
		lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelCustomerInfo.add(lblEmail, "cell 7 4 2 1");

		comboBoxTelephone = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxTelephone.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxTelephone.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxTelephone.setSelectedIndex(-1);
		comboBoxTelephone.setName(lblTelephone.getText());
		comboBoxTelephone.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxTelephone, "cell 0 5 3 1,grow");

		comboBoxFax = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxFax.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxFax.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxFax.setSelectedIndex(-1);
		comboBoxFax.setName(lblFax.getText());
		comboBoxFax.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxFax, "cell 4 5 2 1,grow");

		comboBoxEmail = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxEmail.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxEmail.setSelectedIndex(-1);
		comboBoxEmail.setName(lblEmail.getText());
		comboBoxEmail.addKeyListener(new ComboBoxDeleteHandler());
		panelCustomerInfo.add(comboBoxEmail, "cell 7 5 2 1,grow");

		// PACKAGE DETAILS
		panelPackageDimensions = new JPanel();
		panelPackageDimensions.setBorder(null);
		tabbedPane.addTab("3. Package Dimensions", null, panelPackageDimensions, null);
		tabbedPane.setEnabledAt(2, false);
		panelPackageDimensions.setLayout(
				new MigLayout("insets 0, gap 0", "[62px:n:62px][42px:n:42px][22px:n:22px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[10px:n:10px][36px:n:36px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px]"));

		chckbxUseStaticDimensions = new JCheckBox("Use static dimensions");
		chckbxUseStaticDimensions.setForeground(Color.BLACK);
		chckbxUseStaticDimensions.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		chckbxUseStaticDimensions.addActionListener(new StaticDimensionsCheckboxHandler());
		panelPackageDimensions.add(chckbxUseStaticDimensions, "cell 0 1 12 1,alignx right,aligny center");

		lblStaticWeight = new JLabel("Static Weight (g)");
		lblStaticWeight.setForeground(Color.BLACK);
		lblStaticWeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblStaticWeight.setEnabled(false);
		panelPackageDimensions.add(lblStaticWeight, "cell 0 2 3 1");

		lblStaticLength = new JLabel("Static Length (cm)");
		lblStaticLength.setForeground(Color.BLACK);
		lblStaticLength.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblStaticLength.setEnabled(false);
		panelPackageDimensions.add(lblStaticLength, "cell 4 2 2 1");

		lblStaticWidth = new JLabel("Static Width (cm)");
		lblStaticWidth.setForeground(Color.BLACK);
		lblStaticWidth.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblStaticWidth.setEnabled(false);
		panelPackageDimensions.add(lblStaticWidth, "cell 7 2 2 1");

		lblStaticHeight = new JLabel("Static Height (cm)");
		lblStaticHeight.setForeground(Color.BLACK);
		lblStaticHeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblStaticHeight.setEnabled(false);
		panelPackageDimensions.add(lblStaticHeight, "cell 10 2 2 1");

		textFieldStaticWeight = new JTextField();
		textFieldStaticWeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		textFieldStaticWeight.setEnabled(false);
		panelPackageDimensions.add(textFieldStaticWeight, "cell 0 3 3 1,grow");
		((AbstractDocument) textFieldStaticWeight.getDocument()).setDocumentFilter(new IntegerDocumentFilter(7));

		textFieldStaticLength = new JTextField();
		textFieldStaticLength.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		textFieldStaticLength.setEnabled(false);
		textFieldStaticLength.setColumns(10);
		panelPackageDimensions.add(textFieldStaticLength, "cell 4 3 2 1,grow");
		((AbstractDocument) textFieldStaticLength.getDocument()).setDocumentFilter(new DecimalDocumentFilter(7));

		textFieldStaticWidth = new JTextField();
		textFieldStaticWidth.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		textFieldStaticWidth.setEnabled(false);
		textFieldStaticWidth.setColumns(10);
		panelPackageDimensions.add(textFieldStaticWidth, "cell 7 3 2 1,grow");
		((AbstractDocument) textFieldStaticWidth.getDocument()).setDocumentFilter(new DecimalDocumentFilter(7));

		textFieldStaticHeight = new JTextField();
		textFieldStaticHeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		textFieldStaticHeight.setEnabled(false);
		textFieldStaticHeight.setColumns(10);
		panelPackageDimensions.add(textFieldStaticHeight, "cell 10 3 2 1,grow");
		((AbstractDocument) textFieldStaticHeight.getDocument()).setDocumentFilter(new DecimalDocumentFilter(7));

		lblWeight = new JLabel("Weight (g)");
		lblWeight.setForeground(Color.BLACK);
		lblWeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelPackageDimensions.add(lblWeight, "cell 0 5 3 1");

		lblLength = new JLabel("Length (cm)");
		lblLength.setForeground(Color.BLACK);
		lblLength.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelPackageDimensions.add(lblLength, "cell 4 5 2 1");

		lblWidth = new JLabel("Width (cm)");
		lblWidth.setForeground(Color.BLACK);
		lblWidth.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelPackageDimensions.add(lblWidth, "cell 7 5 2 1");

		lblHeight = new JLabel("Height (cm)");
		lblHeight.setForeground(Color.BLACK);
		lblHeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		panelPackageDimensions.add(lblHeight, "cell 10 5 2 1");

		comboBoxWeight = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxWeight.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxWeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxWeight.setSelectedIndex(-1);
		comboBoxWeight.setName(lblWeight.getText());
		comboBoxWeight.addKeyListener(new ComboBoxDeleteHandler());
		panelPackageDimensions.add(comboBoxWeight, "cell 0 6 3 1,grow");

		comboBoxLength = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLength.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLength.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLength.setSelectedIndex(-1);
		comboBoxLength.setName(lblLength.getText());
		comboBoxLength.addKeyListener(new ComboBoxDeleteHandler());
		panelPackageDimensions.add(comboBoxLength, "cell 4 6 2 1,grow");

		comboBoxWidth = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxWidth.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxWidth.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxWidth.setSelectedIndex(-1);
		comboBoxWidth.setName(lblWidth.getText());
		comboBoxWidth.addKeyListener(new ComboBoxDeleteHandler());
		panelPackageDimensions.add(comboBoxWidth, "cell 7 6 2 1,grow");

		comboBoxHeight = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxHeight.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxHeight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxHeight.setSelectedIndex(-1);
		comboBoxHeight.setName(lblHeight.getText());
		comboBoxHeight.addKeyListener(new ComboBoxDeleteHandler());
		panelPackageDimensions.add(comboBoxHeight, "cell 10 6 2 1,grow");

		//COMPLETE
		panelComplete = new JPanel();
		panelComplete.setBorder(null);
		tabbedPane.addTab("4. Complete Parcel", null, panelComplete, null);
		tabbedPane.setEnabledAt(3, false);
		panelComplete.setLayout(
				new MigLayout("insets 0, gap 0", "[62px:n:62px][42px:n:42px][22px:n:22px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[10px:n:10px][36px:n:36px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px]"));

		btnMake = new JButton("Make Parcel File");
		btnMake.addActionListener(new MakeParcelFileButtonHandler());
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
		comboBoxList.add(comboBoxPrefix);
		comboBoxList.add(comboBoxFirstName);
		comboBoxList.add(comboBoxLastName);	
		comboBoxList.add(comboBoxCompany);	
		comboBoxList.add(comboBoxTelephone);	
		comboBoxList.add(comboBoxFax);	
		comboBoxList.add(comboBoxEmail);	
		comboBoxList.add(comboBoxWeight);	
		comboBoxList.add(comboBoxLength);	
		comboBoxList.add(comboBoxWidth);	
		comboBoxList.add(comboBoxHeight);
	}

	private void addComboBoxMappingListener() {
		comboBoxPrefix.addItemListener(new ComboBoxMappingListener());
		comboBoxFirstName.addItemListener(new ComboBoxMappingListener());
		comboBoxLastName.addItemListener(new ComboBoxMappingListener());
		comboBoxCompany.addItemListener(new ComboBoxMappingListener());
		comboBoxTelephone.addItemListener(new ComboBoxMappingListener());
		comboBoxFax.addItemListener(new ComboBoxMappingListener());
		comboBoxEmail.addItemListener(new ComboBoxMappingListener());
		comboBoxWeight.addItemListener(new ComboBoxMappingListener());
		comboBoxLength.addItemListener(new ComboBoxMappingListener());
		comboBoxWidth.addItemListener(new ComboBoxMappingListener());
		comboBoxHeight.addItemListener(new ComboBoxMappingListener());
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

	private class MakeParcelFileButtonHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnMake) {
					
					while(true) {
						// time to save the file
						JFileChooser fileChooser = new JFileChooser(UserPrefs.getLastUsedFolder());
						int returnVal = fileChooser.showSaveDialog(ParcelDialog.this);

						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = fileChooser.getSelectedFile();
							String fileName = file.getAbsolutePath();

							if (fileName.toLowerCase().endsWith(".txt"))
								fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".txt"));

							File finalFile  = new File(fileName + ".txt");

							if (finalFile.exists() && finalFile.isFile()) {
								int buttonPressed = JOptionPane.showConfirmDialog(ParcelDialog.this, "File already exists, would you like to overwrite it?",
										"File exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

								if (buttonPressed == JOptionPane.YES_OPTION) {
									parcelFile = finalFile;
									break;
								}
							} else {
								parcelFile = finalFile;
								break;
							}
							
							
						}
					}

					ParcelDialog.this.dispose();
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}


	private class SignatureCheckboxHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == chckbxLeaveAtDoor) {
					if(chckbxLeaveAtDoor.isSelected())
						if(chckbxSignatureRequired.isSelected())
							chckbxSignatureRequired.setSelected(false);
				} else if (e.getSource() == chckbxSignatureRequired) {
					if(chckbxSignatureRequired.isSelected())
						if(chckbxLeaveAtDoor.isSelected())
							mainOptionsGroup.clearSelection();
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}

	private class StaticDimensionsCheckboxHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == chckbxUseStaticDimensions) {
					if(chckbxUseStaticDimensions.isSelected()) {
						lblStaticWeight.setEnabled(true);
						textFieldStaticWeight.setEnabled(true);
						lblStaticLength.setEnabled(true);
						textFieldStaticLength.setEnabled(true);
						lblStaticWidth.setEnabled(true);
						textFieldStaticWidth.setEnabled(true);
						lblStaticHeight.setEnabled(true);
						textFieldStaticHeight.setEnabled(true);

						lblWeight.setEnabled(false);
						comboBoxWeight.setEnabled(false);
						lblLength.setEnabled(false);
						comboBoxLength.setEnabled(false);
						lblWidth.setEnabled(false);
						comboBoxWidth.setEnabled(false);
						lblHeight.setEnabled(false);
						comboBoxHeight.setEnabled(false);

					} else {
						lblStaticWeight.setEnabled(false);
						textFieldStaticWeight.setEnabled(false);
						lblStaticLength.setEnabled(false);
						textFieldStaticLength.setEnabled(false);
						lblStaticWidth.setEnabled(false);
						textFieldStaticWidth.setEnabled(false);
						lblStaticHeight.setEnabled(false);
						textFieldStaticHeight.setEnabled(false);

						lblWeight.setEnabled(true);
						comboBoxWeight.setEnabled(true);
						lblLength.setEnabled(true);
						comboBoxLength.setEnabled(true);
						lblWidth.setEnabled(true);
						comboBoxWidth.setEnabled(true);
						lblHeight.setEnabled(true);
						comboBoxHeight.setEnabled(true);
					}
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}


	public class ToggleButtonGroup extends ButtonGroup {

		private ButtonModel prevModel = chckbxLeaveAtDoor.getModel();
		private boolean isAdjusting = false;
		@Override public void setSelected(ButtonModel m, boolean b) {
			if (isAdjusting) {
				return;
			}
			if (m.equals(prevModel)) {
				isAdjusting = true;
				clearSelection();
				isAdjusting = false;
			} else {
				super.setSelected(m, b);
			}
			prevModel = getSelection();
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
			break;
		case TAB_CUSTOMER:
			if(comboBoxFirstName.getSelectedIndex() == -1 && comboBoxLastName.getSelectedIndex() == -1 && comboBoxCompany.getSelectedIndex() == -1)
				UiController.displayMessage("Warning", "No customer information has been mapped", JOptionPane.WARNING_MESSAGE);
				//JOptionPane.showMessageDialog(ParcelDialog.this, "No customer information has been mapped", "Warning", JOptionPane.WARNING_MESSAGE);
			break;
		case TAB_PACKAGE:
			if(chckbxUseStaticDimensions.isSelected()) {
				if(textFieldStaticWeight.getText().isBlank())
					throw new ApplicationException("Weight must be populated");
				if(textFieldStaticLength.getText().isBlank())
					throw new ApplicationException("Length must be populated");
				if(textFieldStaticWidth.getText().isBlank())
					throw new ApplicationException("Width must be populated");
				if(textFieldStaticHeight.getText().isBlank())
					throw new ApplicationException("Height must be populated");
			} else {
				if(comboBoxWeight.getSelectedIndex() == -1)
					throw new ApplicationException("Weight must be mapped to a field");
				if(comboBoxLength.getSelectedIndex() == -1)
					throw new ApplicationException("Length must be mapped to a field");
				if(comboBoxWidth.getSelectedIndex() == -1)
					throw new ApplicationException("Width must be mapped to a field");
				if(comboBoxHeight.getSelectedIndex() == -1)
					throw new ApplicationException("Height must be mapped to a field");
			}
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
					
					// remove unused field
					Object[] fields = usedFields.toArray();
					for(int i = fields.length - 1; i >= 0; --i) {
						if(comboBox.getSelectedIndex() > -1) {
							if(fields[i].toString().equalsIgnoreCase(comboBox.getSelectedItem().toString())) {
								usedFields.remove(fields[i].toString());
								comboBox.setSelectedIndex(-1);
								break;
							}
						}
					}
					
					
				}
			}
		}

		public void keyTyped(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {}
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

	public JComboBox<String> getComboBoxParcelService() {
		return comboBoxParcelService;
	}

	public void setComboBoxParcelService(JComboBox<String> comboBoxParcelService) {
		this.comboBoxParcelService = comboBoxParcelService;
	}

	public JLabel getLblParcelReferenceID() {
		return lblParcelReferenceID;
	}

	public void setLblParcelReferenceID(JLabel lblParcelReferenceID) {
		this.lblParcelReferenceID = lblParcelReferenceID;
	}

	public JTextField getTextFieldReference() {
		return textFieldReference;
	}

	public void setTextFieldReference(JTextField textFieldReference) {
		this.textFieldReference = textFieldReference;
	}

	public JLabel getLblAppendField() {
		return lblAppendField;
	}

	public void setLblAppendField(JLabel lblAppendField) {
		this.lblAppendField = lblAppendField;
	}

	public JComboBox<String> getComboBoxAppendToReference() {
		return comboBoxAppendToReference;
	}

	public void setComboBoxAppendToReference(JComboBox<String> comboBoxAppendToReference) {
		this.comboBoxAppendToReference = comboBoxAppendToReference;
	}

	public ButtonGroup getMainOptionsGroup() {
		return mainOptionsGroup;
	}

	public void setMainOptionsGroup(ButtonGroup mainOptionsGroup) {
		this.mainOptionsGroup = mainOptionsGroup;
	}

	public JCheckBox getChckbxLeaveAtDoor() {
		return chckbxLeaveAtDoor;
	}

	public void setChckbxLeaveAtDoor(JCheckBox chckbxLeaveAtDoor) {
		this.chckbxLeaveAtDoor = chckbxLeaveAtDoor;
	}

	public JCheckBox getChckbxSignatureRequired() {
		return chckbxSignatureRequired;
	}

	public void setChckbxSignatureRequired(JCheckBox chckbxSignatureRequired) {
		this.chckbxSignatureRequired = chckbxSignatureRequired;
	}

	public JCheckBox getChckbxDoNotSafeDrop() {
		return chckbxDoNotSafeDrop;
	}

	public void setChckbxDoNotSafeDrop(JCheckBox chckbxDoNotSafeDrop) {
		this.chckbxDoNotSafeDrop = chckbxDoNotSafeDrop;
	}

	public JCheckBox getChckbxCardForPickup() {
		return chckbxCardForPickup;
	}

	public void setChckbxCardForPickup(JCheckBox chckbxCardForPickup) {
		this.chckbxCardForPickup = chckbxCardForPickup;
	}

	public JPanel getPanelCustomerInfo() {
		return panelCustomerInfo;
	}

	public void setPanelCustomerInfo(JPanel panelCustomerInfo) {
		this.panelCustomerInfo = panelCustomerInfo;
	}

	public JLabel getLblPrefix() {
		return lblPrefix;
	}

	public void setLblPrefix(JLabel lblPrefix) {
		this.lblPrefix = lblPrefix;
	}

	public JComboBox<String> getComboBoxPrefix() {
		return comboBoxPrefix;
	}

	public void setComboBoxPrefix(JComboBox<String> comboBoxPrefix) {
		this.comboBoxPrefix = comboBoxPrefix;
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

	public JLabel getLblFax() {
		return lblFax;
	}

	public void setLblFax(JLabel lblFax) {
		this.lblFax = lblFax;
	}

	public JComboBox<String> getComboBoxFax() {
		return comboBoxFax;
	}

	public void setComboBoxFax(JComboBox<String> comboBoxFax) {
		this.comboBoxFax = comboBoxFax;
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

	public JPanel getPanelPackageDimensions() {
		return panelPackageDimensions;
	}

	public void setPanelPackageDimensions(JPanel panelPackageDimensions) {
		this.panelPackageDimensions = panelPackageDimensions;
	}

	public JCheckBox getChckbxUseStaticDimensions() {
		return chckbxUseStaticDimensions;
	}

	public void setChckbxUseStaticDimensions(JCheckBox chckbxUseStaticDimensions) {
		this.chckbxUseStaticDimensions = chckbxUseStaticDimensions;
	}

	public JLabel getLblStaticWeight() {
		return lblStaticWeight;
	}

	public void setLblStaticWeight(JLabel lblStaticWeight) {
		this.lblStaticWeight = lblStaticWeight;
	}

	public JTextField getTextFieldStaticWeight() {
		return textFieldStaticWeight;
	}

	public void setTextFieldStaticWeight(JTextField textFieldStaticWeight) {
		this.textFieldStaticWeight = textFieldStaticWeight;
	}

	public JLabel getLblStaticLength() {
		return lblStaticLength;
	}

	public void setLblStaticLength(JLabel lblStaticLength) {
		this.lblStaticLength = lblStaticLength;
	}

	public JTextField getTextFieldStaticLength() {
		return textFieldStaticLength;
	}

	public void setTextFieldStaticLength(JTextField textFieldStaticLength) {
		this.textFieldStaticLength = textFieldStaticLength;
	}

	public JLabel getLblStaticWidth() {
		return lblStaticWidth;
	}

	public void setLblStaticWidth(JLabel lblStaticWidth) {
		this.lblStaticWidth = lblStaticWidth;
	}

	public JTextField getTextFieldStaticWidth() {
		return textFieldStaticWidth;
	}

	public void setTextFieldStaticWidth(JTextField textFieldStaticWidth) {
		this.textFieldStaticWidth = textFieldStaticWidth;
	}

	public JLabel getLblStaticHeight() {
		return lblStaticHeight;
	}

	public void setLblStaticHeight(JLabel lblStaticHeight) {
		this.lblStaticHeight = lblStaticHeight;
	}

	public JTextField getTextFieldStaticHeight() {
		return textFieldStaticHeight;
	}

	public void setTextFieldStaticHeight(JTextField textFieldStaticHeight) {
		this.textFieldStaticHeight = textFieldStaticHeight;
	}

	public JLabel getLblWeight() {
		return lblWeight;
	}

	public void setLblWeight(JLabel lblWeight) {
		this.lblWeight = lblWeight;
	}

	public JComboBox<String> getComboBoxWeight() {
		return comboBoxWeight;
	}

	public void setComboBoxWeight(JComboBox<String> comboBoxWeight) {
		this.comboBoxWeight = comboBoxWeight;
	}

	public JLabel getLblLength() {
		return lblLength;
	}

	public void setLblLength(JLabel lblLength) {
		this.lblLength = lblLength;
	}

	public JComboBox<String> getComboBoxLength() {
		return comboBoxLength;
	}

	public void setComboBoxLength(JComboBox<String> comboBoxLength) {
		this.comboBoxLength = comboBoxLength;
	}

	public JLabel getLblWidth() {
		return lblWidth;
	}

	public void setLblWidth(JLabel lblWidth) {
		this.lblWidth = lblWidth;
	}

	public JComboBox<String> getComboBoxWidth() {
		return comboBoxWidth;
	}

	public void setComboBoxWidth(JComboBox<String> comboBoxWidth) {
		this.comboBoxWidth = comboBoxWidth;
	}

	public JLabel getLblHeight() {
		return lblHeight;
	}

	public void setLblHeight(JLabel lblHeight) {
		this.lblHeight = lblHeight;
	}

	public JComboBox<String> getComboBoxHeight() {
		return comboBoxHeight;
	}

	public void setComboBoxHeight(JComboBox<String> comboBoxHeight) {
		this.comboBoxHeight = comboBoxHeight;
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

	public File getParcelFile() {
		return parcelFile;
	}

	public void setParcelFile(File parcelFile) {
		this.parcelFile = parcelFile;
	}

	

}
