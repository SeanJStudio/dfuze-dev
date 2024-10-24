package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.Theme;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.io.TextWriter;
import com.mom.dfuze.io.XLSXWriter;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class InkjetDialog extends JDialog {
	// combo boxes
	private JComboBox<String> comboBoxLine1;
	private JComboBox<String> comboBoxLine2;
	private JComboBox<String> comboBoxLine3;
	private JComboBox<String> comboBoxLine4;
	private JComboBox<String> comboBoxLine5;
	private JComboBox<String> comboBoxLine6;
	private JComboBox<String> comboBoxLine7;
	private JComboBox<String> comboBoxLine8;
	private JComboBox<String> comboBoxAddress1;
	private JComboBox<String> comboBoxAddress2;
	private JComboBox<String> comboBoxAddress3;
	private JComboBox<String> comboBoxAddress4;
	private JComboBox<String> comboBoxCity;
	private JComboBox<String> comboBoxProv;
	private JComboBox<String> comboBoxPC;
	private JComboBox<String> comboBoxCountry;
	private JComboBox<String> comboBoxDMC;
	private JComboBox<String> comboBoxBagBundle;
	private JComboBox<String> comboBoxBreaks;
	private JComboBox<String> comboBoxListOrder;

	// labels
	private JLabel lblLine1;
	private JLabel lblLine2;
	private JLabel lblLine3;
	private JLabel lblLine4;
	private JLabel lblLine5;
	private JLabel lblLine6;
	private JLabel lblLine7;
	private JLabel lblLine8;
	private JLabel lblAddress1;
	private JLabel lblAddress2;
	private JLabel lblAddress3;
	private JLabel lblAddress4;
	private JLabel lblCity;
	private JLabel lblProv;
	private JLabel lblPC;
	private JLabel lblCountry;
	private JLabel lblDMC;
	private JLabel lblBagBundle;
	private JLabel lblBreaks;
	private JLabel lblListOrder;
	
	// lineHeaders
	private ArrayList<String> lineNames = new ArrayList<>();
	// lineComboBoxes
	private ArrayList<JComboBox<String>> lineComboBoxes = new ArrayList<>();

	// buttons
	private JButton btnReset;
	private JButton btnMake;

	// Checkboxes
	private JCheckBox chckbxStripAccents;
	private JCheckBox chckbxUppercase;
	private JCheckBox chckbxAddCommaToCity;

	// ui internal
	private final JPanel contentPanel = new JPanel();
	private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXX";

	// internal
	private ArrayList<JComboBox<String>> comboBoxList = new ArrayList<>();
	private HashSet<String> usedFields = new HashSet<>();

	private JLabel lblSubTitle;
	private JLabel lblMakeMultipleFiles;
	private JComboBox<String> comboBoxMakeMultipleFiles;
	private JLabel lblMakeMultipleFilesPreview;
	private JSeparator separatorSettingsBottom;
	private JSeparator separatorSettingsTop;
	
	private final String CITY_PROV_PC = "CITY PROV PC";
	
	private HashSet<String> addressFields = new HashSet<>();
	private JLabel lblOptions;
	private JLabel lblTitle;




	public InkjetDialog(JFrame frame) {


		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		//addWindowListener(new WindowClosingHandler());

		setTitle("Inkjet Maker");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 245));
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 10 28 20 28, gap 0", "[62px:n:62px][42px:n:42px][22px:n:22px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[6px:n:6px][36px:n:36px][4px:n:4px][28px:n:28px][28px:n:28px][28px:n:28px][36px:n:36px][4px:n:4px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px][28px:n:28px][36px:n:36px][28px:n:28px]"));
		
		lblOptions = new JLabel("Inkjet Options");
		lblOptions.setForeground(Theme.TITLE_COLOR);
		lblOptions.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblOptions, "cell 0 1 2 1");

		separatorSettingsTop = new JSeparator();
		contentPanel.add(separatorSettingsTop, "cell 2 1 10 1,growx,aligny center");

		chckbxUppercase = new JCheckBox("Uppercase");
		chckbxUppercase.setForeground(Color.BLACK);
		chckbxUppercase.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		chckbxUppercase.setBackground(new Color(245, 245, 245));
		contentPanel.add(chckbxUppercase, "cell 0 3 2 1,aligny center");
		
		chckbxAddCommaToCity = new JCheckBox("Add comma to CITY");
		chckbxAddCommaToCity.setForeground(Color.BLACK);
		chckbxAddCommaToCity.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		chckbxAddCommaToCity.setBackground(new Color(245, 245, 245));
		contentPanel.add(chckbxAddCommaToCity, "cell 3 3 3 1");

		lblMakeMultipleFiles = new JLabel("Make a file for each unique value found in");
		lblMakeMultipleFiles.setForeground(Color.BLACK);
		lblMakeMultipleFiles.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblMakeMultipleFiles, "cell 7 3 5 1,alignx left,aligny top");

		chckbxStripAccents = new JCheckBox("Strip accents");
		chckbxStripAccents.setForeground(Color.BLACK);
		chckbxStripAccents.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		chckbxStripAccents.setBackground(new Color(245, 245, 245));
		contentPanel.add(chckbxStripAccents, "cell 0 4,aligny bottom");

		comboBoxMakeMultipleFiles = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxMakeMultipleFiles.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxMakeMultipleFiles.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxMakeMultipleFiles.setSelectedIndex(-1);
		comboBoxMakeMultipleFiles.addActionListener(new ComboBoxMultipleFilesHandler());
		contentPanel.add(comboBoxMakeMultipleFiles, "cell 7 4 2 1,grow");

		lblMakeMultipleFilesPreview = new JLabel("");
		lblMakeMultipleFilesPreview.setForeground(Theme.TITLE_COLOR);
		lblMakeMultipleFilesPreview.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblMakeMultipleFilesPreview, "cell 10 4 2 1,alignx center,aligny center");

		separatorSettingsBottom = new JSeparator();
		contentPanel.add(separatorSettingsBottom, "cell 2 6 10 1,growx");
		
		lblTitle = new JLabel("Inkjet Fields");
		lblTitle.setForeground(Theme.TITLE_COLOR);
		lblTitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblTitle, "cell 0 6 2 1");

		lblLine1 = new JLabel("LINE1");
		lblLine1.setForeground(Color.BLACK);
		lblLine1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblLine1, "cell 0 8 2 1,aligny center");

		lblLine2 = new JLabel("LINE2");
		lblLine2.setForeground(Color.BLACK);
		lblLine2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblLine2, "cell 4 8,aligny center");

		lblLine3 = new JLabel("LINE3");
		lblLine3.setForeground(Color.BLACK);
		lblLine3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblLine3, "cell 7 8,aligny center");

		lblLine4 = new JLabel("LINE4");
		lblLine4.setForeground(Color.BLACK);
		lblLine4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblLine4, "cell 10 8,alignx left,aligny center");

		comboBoxLine1 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLine1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLine1.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLine1.setSelectedIndex(-1);
		comboBoxLine1.addItemListener(new ComboBoxMappingListener());
		comboBoxLine1.setName(lblLine1.getText());
		contentPanel.add(comboBoxLine1, "cell 0 9 3 1,grow");

		comboBoxLine2 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLine2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLine2.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLine2.setSelectedIndex(-1);
		comboBoxLine2.addItemListener(new ComboBoxMappingListener());
		comboBoxLine2.setName(lblLine2.getText());
		contentPanel.add(comboBoxLine2, "cell 4 9 2 1,grow");

		comboBoxLine3 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLine3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLine3.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLine3.setSelectedIndex(-1);
		comboBoxLine3.addItemListener(new ComboBoxMappingListener());
		comboBoxLine3.setName(lblLine3.getText());
		contentPanel.add(comboBoxLine3, "cell 7 9 2 1,grow");

		comboBoxLine4 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLine4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLine4.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLine4.setSelectedIndex(-1);
		comboBoxLine4.addItemListener(new ComboBoxMappingListener());
		comboBoxLine4.setName(lblLine4.getText());
		contentPanel.add(comboBoxLine4, "cell 10 9 2 1,grow");
		
		lblLine5 = new JLabel("LINE5");
		lblLine5.setForeground(Color.BLACK);
		lblLine5.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblLine5, "cell 0 11");
		
		lblLine6 = new JLabel("LINE6");
		lblLine6.setForeground(Color.BLACK);
		lblLine6.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblLine6, "cell 4 11");
		
		lblLine7 = new JLabel("LINE7");
		lblLine7.setForeground(Color.BLACK);
		lblLine7.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblLine7, "cell 7 11");
		
		lblLine8 = new JLabel("LINE8");
		lblLine8.setForeground(Color.BLACK);
		lblLine8.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblLine8, "cell 10 11");
		
		comboBoxLine5 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLine5.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLine5.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLine5.setSelectedIndex(-1);
		comboBoxLine5.addItemListener(new ComboBoxMappingListener());
		comboBoxLine5.setName(lblLine5.getText());
		contentPanel.add(comboBoxLine5, "cell 0 12 3 1,grow");
		
		comboBoxLine6 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLine6.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLine6.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLine6.setSelectedIndex(-1);
		comboBoxLine6.addItemListener(new ComboBoxMappingListener());
		comboBoxLine6.setName(lblLine6.getText());
		contentPanel.add(comboBoxLine6, "cell 4 12 2 1,grow");
		
		comboBoxLine7 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLine7.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLine7.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLine7.setSelectedIndex(-1);
		comboBoxLine7.addItemListener(new ComboBoxMappingListener());
		comboBoxLine7.setName(lblLine7.getText());
		contentPanel.add(comboBoxLine7, "cell 7 12 2 1,grow");
		
		comboBoxLine8 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxLine8.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxLine8.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxLine8.setSelectedIndex(-1);
		comboBoxLine8.addItemListener(new ComboBoxMappingListener());
		comboBoxLine8.setName(lblLine8.getText());
		contentPanel.add(comboBoxLine8, "cell 10 12 2 1,grow");

		lblAddress1 = new JLabel("ADDRESS1");
		lblAddress1.setForeground(Color.BLACK);
		lblAddress1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblAddress1, "cell 0 14 2 1,aligny center");

		lblAddress2 = new JLabel("ADDRESS2");
		lblAddress2.setForeground(Color.BLACK);
		lblAddress2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblAddress2, "cell 4 14 2 1,aligny center");

		lblAddress3 = new JLabel("ADDRESS3");
		lblAddress3.setForeground(Color.BLACK);
		lblAddress3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblAddress3, "cell 7 14,aligny center");

		lblAddress4 = new JLabel("ADDRESS4");
		lblAddress4.setForeground(Color.BLACK);
		lblAddress4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblAddress4, "cell 10 14,aligny center");

		comboBoxAddress1 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxAddress1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxAddress1.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxAddress1.setSelectedIndex(-1);
		comboBoxAddress1.addItemListener(new ComboBoxMappingListener());
		comboBoxAddress1.setName(lblAddress1.getText());
		contentPanel.add(comboBoxAddress1, "cell 0 15 3 1,grow");

		comboBoxAddress2 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxAddress2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxAddress2.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxAddress2.setSelectedIndex(-1);
		comboBoxAddress2.addItemListener(new ComboBoxMappingListener());
		comboBoxAddress2.setName(lblAddress2.getText());
		contentPanel.add(comboBoxAddress2, "cell 4 15 2 1,grow");

		comboBoxAddress3 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxAddress3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxAddress3.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxAddress3.setSelectedIndex(-1);
		comboBoxAddress3.addItemListener(new ComboBoxMappingListener());
		comboBoxAddress3.setName(lblAddress3.getText());
		contentPanel.add(comboBoxAddress3, "cell 7 15 2 1,grow");

		comboBoxAddress4 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxAddress4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxAddress4.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxAddress4.setSelectedIndex(-1);
		comboBoxAddress4.addItemListener(new ComboBoxMappingListener());
		comboBoxAddress4.setName(lblAddress4.getText());
		contentPanel.add(comboBoxAddress4, "cell 10 15 2 1,grow");

		lblCity = new JLabel("CITY");
		lblCity.setForeground(Color.BLACK);
		lblCity.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblCity, "cell 0 17 2 1,aligny center");

		lblProv = new JLabel("PROVINCE");
		lblProv.setForeground(Color.BLACK);
		lblProv.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblProv, "cell 4 17 2 1,aligny center");

		lblPC = new JLabel("PC");
		lblPC.setForeground(Color.BLACK);
		lblPC.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblPC, "cell 7 17,aligny center");

		lblCountry = new JLabel("COUNTRY");
		lblCountry.setForeground(Color.BLACK);
		lblCountry.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblCountry, "cell 10 17,aligny center");

		comboBoxCity = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxCity.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxCity.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxCity.setSelectedIndex(-1);
		comboBoxCity.addItemListener(new ComboBoxMappingListener());
		comboBoxCity.setName(lblCity.getText());
		contentPanel.add(comboBoxCity, "cell 0 18 3 1,grow");

		comboBoxProv = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxProv.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxProv.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxProv.setSelectedIndex(-1);
		comboBoxProv.addItemListener(new ComboBoxMappingListener());
		comboBoxProv.setName(lblProv.getText());
		contentPanel.add(comboBoxProv, "cell 4 18 2 1,grow");

		comboBoxPC = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxPC.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxPC.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxPC.setSelectedIndex(-1);
		comboBoxPC.addItemListener(new ComboBoxMappingListener());
		comboBoxPC.setName(lblPC.getText());
		contentPanel.add(comboBoxPC, "cell 7 18 2 1,grow");

		comboBoxCountry = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxCountry.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxCountry.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxCountry.setSelectedIndex(-1);
		comboBoxCountry.addItemListener(new ComboBoxMappingListener());
		comboBoxCountry.setName(lblCountry.getText());
		contentPanel.add(comboBoxCountry, "cell 10 18 2 1,grow");

		lblDMC = new JLabel("DMC");
		lblDMC.setForeground(Color.BLACK);
		lblDMC.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblDMC, "cell 0 20 2 1,aligny center");

		lblBagBundle = new JLabel("BAGBUNDLE");
		lblBagBundle.setForeground(Color.BLACK);
		lblBagBundle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblBagBundle, "cell 4 20 2 1,aligny center");

		lblBreaks = new JLabel("BREAKS");
		lblBreaks.setForeground(Color.BLACK);
		lblBreaks.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblBreaks, "cell 7 20 2 1,aligny center");

		lblListOrder = new JLabel("LISTORDER");
		lblListOrder.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblListOrder.setForeground(new Color(220, 20, 60));
		contentPanel.add(lblListOrder, "cell 10 20 2 1,aligny center");

		comboBoxDMC = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxDMC.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxDMC.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxDMC.setSelectedIndex(-1);
		comboBoxDMC.addItemListener(new ComboBoxMappingListener());
		comboBoxDMC.setName(lblDMC.getText());
		contentPanel.add(comboBoxDMC, "cell 0 21 3 1,grow");

		comboBoxBagBundle = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxBagBundle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxBagBundle.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxBagBundle.setSelectedIndex(-1);
		comboBoxBagBundle.addItemListener(new ComboBoxMappingListener());
		comboBoxBagBundle.setName(lblBagBundle.getText());
		contentPanel.add(comboBoxBagBundle, "cell 4 21 2 1,grow");

		comboBoxBreaks = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxBreaks.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxBreaks.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxBreaks.setSelectedIndex(-1);
		comboBoxBreaks.addItemListener(new ComboBoxMappingListener());
		comboBoxBreaks.setName(lblBreaks.getText());
		contentPanel.add(comboBoxBreaks, "cell 7 21 2 1,grow");

		comboBoxListOrder = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxListOrder.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxListOrder.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxListOrder.setSelectedIndex(-1);
		comboBoxListOrder.addItemListener(new ComboBoxMappingListener());
		comboBoxListOrder.addActionListener(new ComboBoxListOrderHandler());
		comboBoxListOrder.setName(lblListOrder.getText());
		contentPanel.add(comboBoxListOrder, "cell 10 21 2 1,grow");

		JSeparator separatorBottom = new JSeparator();
		contentPanel.add(separatorBottom, "cell 0 23 12 1,growx,aligny center");

		btnReset = new JButton("Reset");
		btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnReset.addActionListener(new ResetButtonHandler());
		contentPanel.add(btnReset, "cell 0 24 3 1,grow");

		btnMake = new JButton("Make Inkjet File");
		btnMake.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMake.addActionListener(new CreateButtonHandler());
		
				lblSubTitle = new JLabel("LISTORDER is required");
				lblSubTitle.setForeground(new Color(220, 20, 60));
				lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
				contentPanel.add(lblSubTitle, "cell 4 24 2 1,alignx right");
		contentPanel.add(btnMake, "cell 7 24 5 1,grow");

		fillComboBoxList();
		setAddressFields();
		
		fillLineHeaders();
		fillLineComboBoxes();
		
		pack();
	    setLocationRelativeTo(frame);
		getContentPane().requestFocusInWindow();
	}

	private void fillComboBoxList() {
		comboBoxList.add(comboBoxLine1);
		comboBoxList.add(comboBoxLine2);
		comboBoxList.add(comboBoxLine3);
		comboBoxList.add(comboBoxLine4);
		comboBoxList.add(comboBoxLine5);
		comboBoxList.add(comboBoxLine6);
		comboBoxList.add(comboBoxLine7);
		comboBoxList.add(comboBoxLine8);
		comboBoxList.add(comboBoxAddress1);
		comboBoxList.add(comboBoxAddress2);
		comboBoxList.add(comboBoxAddress3);
		comboBoxList.add(comboBoxAddress4);
		comboBoxList.add(comboBoxCity);
		comboBoxList.add(comboBoxProv);
		comboBoxList.add(comboBoxPC);
		comboBoxList.add(comboBoxCountry);
		comboBoxList.add(comboBoxDMC);
		comboBoxList.add(comboBoxBagBundle);
		comboBoxList.add(comboBoxBreaks);
		comboBoxList.add(comboBoxListOrder);
	}
	
	private void setAddressFields() {
		addressFields.add(lblAddress1.getText());
		addressFields.add(lblAddress2.getText());
		addressFields.add(lblAddress3.getText());
		addressFields.add(lblAddress4.getText());
		addressFields.add(CITY_PROV_PC);
	}
	
	private void fillLineHeaders() {
		lineNames.add(lblLine1.getText());
		lineNames.add(lblLine2.getText());
		lineNames.add(lblLine3.getText());
		lineNames.add(lblLine4.getText());
		lineNames.add(lblLine5.getText());
		lineNames.add(lblLine6.getText());
		lineNames.add(lblLine7.getText());
		lineNames.add(lblLine8.getText());
	}
	
	private void fillLineComboBoxes() {
		lineComboBoxes.add(comboBoxLine1);
		lineComboBoxes.add(comboBoxLine2);
		lineComboBoxes.add(comboBoxLine3);
		lineComboBoxes.add(comboBoxLine4);
		lineComboBoxes.add(comboBoxLine5);
		lineComboBoxes.add(comboBoxLine6);
		lineComboBoxes.add(comboBoxLine7);
		lineComboBoxes.add(comboBoxLine8);
	}

	private void disableUi() {
		for(JComboBox<String> cb : comboBoxList)
			cb.setEnabled(false);

		chckbxUppercase.setEnabled(false);
		chckbxStripAccents.setEnabled(false);
		chckbxAddCommaToCity.setEnabled(false);
		comboBoxMakeMultipleFiles.setEnabled(false);
		btnReset.setEnabled(false);
		btnMake.setEnabled(false);
	}

	private void enableUi() {
		for(JComboBox<String> cb : comboBoxList)
			cb.setEnabled(true);

		chckbxUppercase.setEnabled(true);
		chckbxStripAccents.setEnabled(true);
		chckbxAddCommaToCity.setEnabled(true);
		comboBoxMakeMultipleFiles.setEnabled(true);
		btnReset.setEnabled(true);
		btnMake.setEnabled(true);
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
	

	private class ComboBoxMultipleFilesHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == comboBoxMakeMultipleFiles) {
					if(comboBoxMakeMultipleFiles.getSelectedIndex() > -1) {
						boolean isDfField = false;

						for(String dfField : UiController.getUserData().getDfHeaders()) {
							if(dfField.equalsIgnoreCase(comboBoxMakeMultipleFiles.getSelectedItem().toString())) {
								isDfField = true;
								break;
							}
						}

						HashSet<String> uniqueValues = new HashSet<>();

						if(isDfField) {
							for(Record record : UiController.getUserData().getRecordList()) {
								String value = UserData.getRecordFieldByName(comboBoxMakeMultipleFiles.getSelectedItem().toString(), record);
								uniqueValues.add(value);
							}
						} else {
							for(Record record : UiController.getUserData().getRecordList()) {
								String value = record.getDfInData()[comboBoxMakeMultipleFiles.getSelectedIndex()];
								uniqueValues.add(value);
							}
						}
						
						lblMakeMultipleFilesPreview.setText(String.format("%d file(s) will be made", uniqueValues.size()));

					} else {
						lblMakeMultipleFilesPreview.setText("");
					}
				}
			} catch (Exception err) {
				UiController.handle(err);
			}

		}
	}

	private class ComboBoxListOrderHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == comboBoxListOrder)  {
					if(comboBoxListOrder.getSelectedIndex() > -1) {
						lblListOrder.setForeground(Color.BLACK);
						lblSubTitle.setText("");
					} else {
						lblListOrder.setForeground(new Color(220, 20, 60));
						lblSubTitle.setText(lblListOrder.getText() + " is required");
					}
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}

	private class ResetButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnReset) {
					//Reset the combo boxes
					for(JComboBox<String> cb : comboBoxList) {
						cb.setSelectedIndex(-1);
					}

					// Reset the used fields
					usedFields = new HashSet<>();

					// Reset the settings
					chckbxUppercase.setSelected(false);
					chckbxStripAccents.setSelected(false);
					comboBoxMakeMultipleFiles.setSelectedIndex(-1);
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}

	private class CreateButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnMake) {

					// Check if at least one field was mapped
					if(usedFields.size() == 0)
						throw new ApplicationException(String.format("At least one field must be mapped."));

					// Check if LISTORDER was mapped
					if(comboBoxListOrder.getSelectedIndex() == -1)
						throw new ApplicationException(String.format(lblListOrder.getText() + " must be mapped."));

					// Disable so nothing bad happens
					disableUi();
					
					//=======================================
					// START OF MULTIPLE LIST GATHERING
					//=======================================
					
					List<List<Record>> recordLists = new ArrayList<>();
					
					SortedSet<String> uniqueFileSegments = new TreeSet<>();
					
					if(comboBoxMakeMultipleFiles.getSelectedIndex() > -1) {
						boolean isDfField = false;

						for(String dfField : UiController.getUserData().getDfHeaders()) {
							if(dfField.equalsIgnoreCase(comboBoxMakeMultipleFiles.getSelectedItem().toString())) {
								isDfField = true;
								break;
							}
						}

						if(isDfField) {
							for(Record record : UiController.getUserData().getRecordList()) {
								String value = UserData.getRecordFieldByName(comboBoxMakeMultipleFiles.getSelectedItem().toString(), record);
								uniqueFileSegments.add(value);
							}
						} else {
							for(Record record : UiController.getUserData().getRecordList()) {
								String value = record.getDfInData()[comboBoxMakeMultipleFiles.getSelectedIndex()];
								uniqueFileSegments.add(value);
							}
						}
						
						for(String value : uniqueFileSegments) {
							List<Record> tempList = new ArrayList<>();
							if(isDfField) {
								for(Record record : UiController.getUserData().getRecordList()) {
									String tempValue = UserData.getRecordFieldByName(comboBoxMakeMultipleFiles.getSelectedItem().toString(), record);
									if(value.equalsIgnoreCase(tempValue)) {
										tempList.add(record);
									}
								}
							} else {
								for(Record record : UiController.getUserData().getRecordList()) {
									String tempValue = record.getDfInData()[comboBoxMakeMultipleFiles.getSelectedIndex()];
									if(value.equalsIgnoreCase(tempValue)) {
										tempList.add(record);
									}
								}
							}
							
							recordLists.add(tempList);
						}
					} else {
						recordLists.add(UiController.getUserData().getRecordList());
					}
					
					//=======================================
					// END OF MULTIPLE LIST GATHERING
					//=======================================

					//=======================================
					// START OF HEADER/DATA MANIPULATION
					//=======================================
					
					// Create a list of String[][] to hold our different segments
					List<String[][]> modifiedArrayLists = new ArrayList<>();
					
					// Check if city, prov, pc is selected so we can combine the fields
					boolean isCityProvPCSelected = false;
					if(comboBoxCity.getSelectedIndex() > -1)
						if(comboBoxProv.getSelectedIndex() > -1)
							if(comboBoxPC.getSelectedIndex() > -1)
								isCityProvPCSelected = true;

					// put everything in the right order for renaming
					ArrayList<String> newHeaders = new ArrayList<>();
					for(JComboBox<String> box : comboBoxList)
						if(box.getSelectedIndex() > -1)
							if(usedFields.contains(box.getSelectedItem().toString()))
								newHeaders.add(box.getSelectedItem().toString());

					String[]originalHeaders = newHeaders.toArray(new String[0]);


					// Fill the data
					for(List<Record> list : recordLists) {
						String[][] inkjetData = new String[list.size()][originalHeaders.length];
						
					for(int i = 0; i < originalHeaders.length; ++i) {
						boolean isDfField = false;
						for(String dfField : UiController.getUserData().getDfHeaders()) {
							if(dfField.equalsIgnoreCase(originalHeaders[i])) {
								isDfField = true;
								break;
							}
						}

						int indexToGet = 0;
						for(int x = 0; x < UiController.getUserData().getExportHeaders().length; ++x) {
							if(originalHeaders[i].equalsIgnoreCase(UiController.getUserData().getExportHeaders()[x])) {
								indexToGet = x;
								break;
							}
						}

						
							if(isDfField)
								for(int j = 0; j < list.size(); ++j)
									inkjetData[j][i] = UserData.getRecordFieldByName(originalHeaders[i], list.get(j));
							else 
								for(int j = 0; j < list.size(); ++j)
									inkjetData[j][i] = list.get(j).getDfInData()[indexToGet];
						}
					
					modifiedArrayLists.add(inkjetData);

					}

					// Update the headers to standardized header names		
					for(int i = 0; i < originalHeaders.length; ++i)
						for(JComboBox<String> box : comboBoxList) 
							if(box.getSelectedIndex() > -1)
								if(box.getSelectedItem().toString().equalsIgnoreCase(originalHeaders[i]))
									originalHeaders[i] = box.getName();

					int finalHeaderSize = originalHeaders.length;

					if(isCityProvPCSelected)
						finalHeaderSize = originalHeaders.length - 2; //Subtract two to remove prov and pc
					
					//=======================================
					// END OF HEADER/DATA MANIPULATION
					//=======================================
					
					
					
					//=======================================
					// START OF DATA MANIPULATION
					//=======================================
					
					String[] reportInkjetHeaders = new String[finalHeaderSize];
					
					List<String[][]> finalArrayLists = new ArrayList<>();

					for(String[][] recordList : modifiedArrayLists) {
						String[][] outputData = new String[recordList.length][finalHeaderSize];
						String[] tempFinalHeaders = new String[finalHeaderSize];
						String[] tempOriginalInkjetHeaders = new String[originalHeaders.length];
						System.arraycopy(originalHeaders, 0, tempOriginalInkjetHeaders, 0,originalHeaders.length);

						int i = 0;
						int newIndex = 0;
						for(; i < tempOriginalInkjetHeaders.length; ++i) {
	
							boolean shouldSkip = false;
	
							for(int j = 0; j < outputData.length; ++j) {

								outputData[j][newIndex] = recordList[j][i];
								
								// Set the comma if no prov or pc
								if(chckbxAddCommaToCity.isSelected() && tempOriginalInkjetHeaders[i].equalsIgnoreCase(lblCity.getText())) {
									String city = recordList[j][i].trim();
									
									if(!city.isBlank()) {
										outputData[j][newIndex] = city + ",";
									}
								}
	
								if(isCityProvPCSelected && tempOriginalInkjetHeaders[i].equalsIgnoreCase(lblCity.getText())) {
									shouldSkip = true;
	
									String city = recordList[j][i].trim();
									String prov = recordList[j][i+1].trim();
									String pc = recordList[j][i+2].trim();
	
									String cityProvPC = "";
	
									if(!city.isBlank()) {
										
										if(chckbxAddCommaToCity.isSelected()) { // don't leave a trailing comma
											if(!prov.isBlank() || !pc.isBlank()) {
												city += ",";
											}
										}
										
										cityProvPC += city;
									}
	
									if(!prov.isBlank())
										cityProvPC += " " + prov;
	
									if(!pc.isBlank())
										cityProvPC += "  " + pc;
	
									cityProvPC = cityProvPC.trim();
									outputData[j][newIndex] = cityProvPC;
	
								}
							}
	
							if(shouldSkip)
								tempOriginalInkjetHeaders[i] = CITY_PROV_PC;
	
							tempFinalHeaders[newIndex] = tempOriginalInkjetHeaders[i];
	
							if(shouldSkip) {
								i++;
								i++;
							}
	
							newIndex++;
						}
	
						String[] finalInkjetHeaders = tempFinalHeaders;
						reportInkjetHeaders = finalInkjetHeaders;
						String[][] finalInkjetData = outputData;
	
						// Remove the accents if required
						if(chckbxStripAccents.isSelected()){
							for(int j = 0; j < finalInkjetHeaders.length; ++j)
								finalInkjetHeaders[j] = StringUtils.stripAccents(finalInkjetHeaders[j]);
	
							for(int j = 0; j < finalInkjetData.length; ++j)
								for(int k = 0; k < finalInkjetData[j].length; ++k)
									finalInkjetData[j][k] = StringUtils.stripAccents(finalInkjetData[j][k]);
						}
	
						// Uppercase if required (FIX to not uppercase DMC)
						if(chckbxUppercase.isSelected()){
							for(int j = 0; j < finalInkjetHeaders.length; ++j)
								finalInkjetHeaders[j] = finalInkjetHeaders[j].toUpperCase();
	
							for(int j = 0; j < finalInkjetData.length; ++j)
								for(int k = 0; k < finalInkjetData[j].length; ++k)
									finalInkjetData[j][k] = finalInkjetData[j][k].toUpperCase();
						}
						
						
						finalArrayLists.add(finalInkjetData);
					}
					
					//=======================================
					// END OF DATA MANIPULATION
					//=======================================

					//=======================================
					// START OF FILES CREATION
					//=======================================

					// time to save the file
					JFileChooser fileChooser = new JFileChooser(UserPrefs.getLastUsedFolder());
					int returnVal = fileChooser.showSaveDialog(InkjetDialog.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						String fileName = file.getAbsolutePath();

						if (fileName.toLowerCase().endsWith(".xlsx"))
							fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".xlsx"));
						else if (fileName.toLowerCase().endsWith(".xlsm"))
							fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".xlsm"));
						else if (fileName.toLowerCase().endsWith(".accdb"))
							fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".accdb"));
						else if (fileName.toLowerCase().endsWith(".mdb"))
							fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".mdb"));
						else if (fileName.toLowerCase().endsWith(".dbf"))
							fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".dbf"));
						else if (fileName.toLowerCase().endsWith(".txt"))
							fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".txt"));
						else if (fileName.toLowerCase().endsWith(".csv"))
							fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".csv"));
						else if (fileName.toLowerCase().endsWith(".dat"))
							fileName = fileName.substring(0, fileName.toLowerCase().lastIndexOf(".dat"));


						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
						DateTimeFormatter idtf = DateTimeFormatter.ofPattern("MMddyyyyHHmmss");
						LocalDateTime now = LocalDateTime.now();
						
						List<File> files = new ArrayList<>();
						
						if(uniqueFileSegments.size() > 0) {
							for(String segment : uniqueFileSegments) {
								// ensure only valid characters are present in file names
								segment = StringUtils.stripAccents(segment).replaceAll("[^a-zA-Z0-9 -_]", "");
								
								File tempFile = new File(fileName + " " + segment + ".xlsx");
								if(tempFile.exists() && tempFile.isFile())
									tempFile = new File(fileName + " " + segment + " " + idtf.format(now) + ".xlsx");
								
								files.add(tempFile);
							}
						} else {
							File tempFile  = new File(fileName + ".xlsx");
							if(tempFile.exists() && tempFile.isFile())
								tempFile  = new File(fileName + " " + idtf.format(now) + ".xlsx");
							files.add(tempFile);
						}
						
						
						
						for(int j = 0; j < finalArrayLists.size(); ++j)
							XLSXWriter.write(files.get(j), reportInkjetHeaders, finalArrayLists.get(j), false, true);
						
						
						//=======================================
						// END OF FILES CREATION
						//=======================================
						
						//=======================================
						// START OF REPORT CREATION
						//=======================================

						File inkjetReportFile = new File(fileName + " Inkjet layout " + idtf.format(now) + ".txt");
						final String spacer = "    ";

						StringBuffer inkjetReport = new StringBuffer();
						inkjetReport.append("\n");
						inkjetReport.append("File(s) to Inkjet\n");
						inkjetReport.append("-----------------\n");
						inkjetReport.append("Root Path: \n\n");
						
						for(int j = 0; j < finalArrayLists.size(); ++j)
							inkjetReport.append(files.get(j).getName() + " - " + finalArrayLists.get(j).length + " Records\n");
							
						inkjetReport.append("\n\n");
						inkjetReport.append("Date Generated\n");
						inkjetReport.append("-----------------\n");
						inkjetReport.append(dtf.format(now) + "\n");
						inkjetReport.append("\n\n\n");
						
						inkjetReport.append(String.format("%17s","Field Number"));
						inkjetReport.append(spacer);
						inkjetReport.append(String.format("%-32s","Field Name"));
						inkjetReport.append(spacer);
						inkjetReport.append(String.format("%-32s","Original Name"));
						inkjetReport.append("\n");
						
						inkjetReport.append(String.format("%-17s", "").replace(' ', '-'));
						inkjetReport.append(spacer);
						inkjetReport.append(String.format("%-32s", "").replace(' ', '-'));
						inkjetReport.append(spacer);
						inkjetReport.append(String.format("%-32s", "").replace(' ', '-'));
						inkjetReport.append("\n");

						for(int j = 0; j < reportInkjetHeaders.length; ++j) {
							inkjetReport.append(String.format("%17s",(j + 1)+"."));
							inkjetReport.append(spacer);
							inkjetReport.append(String.format("%-32s",reportInkjetHeaders[j]));
							inkjetReport.append(spacer);
							
							// Add the original field for line items
							if(lineNames.contains(reportInkjetHeaders[j])) {
								for(JComboBox<String> comboBox : lineComboBoxes) {
									if(comboBox.getName().equalsIgnoreCase(reportInkjetHeaders[j])) {
										inkjetReport.append(String.format("%-32s",comboBox.getSelectedItem().toString()));
										break;
									}
								}
							}
							
							inkjetReport.append("\n");
							
						}

						inkjetReport.append("\n\n");

						int maxLines = 0;
						String maxLinesListOrder = "";
						String maxLinesFrom = "";

						int maxChar = 0;
						String maxCharListOrder = "";
						String maxCharFrom = "";
						
						for(int h = 0; h < finalArrayLists.size(); ++h) {
							for(int j = 0; j < finalArrayLists.get(h).length; ++j) {
								int tempMaxLine = 0;
								for(int k = 0; k < finalArrayLists.get(h)[j].length; ++k) {
									if(!reportInkjetHeaders[k].equalsIgnoreCase(lblDMC.getText())
											&& !reportInkjetHeaders[k].equalsIgnoreCase(lblBagBundle.getText())
											&& !reportInkjetHeaders[k].equalsIgnoreCase(lblBreaks.getText())
											&& !reportInkjetHeaders[k].equalsIgnoreCase(lblListOrder.getText())) 
									{
										if(finalArrayLists.get(h)[j][k].length() > 0)
											++tempMaxLine;
	
										if(finalArrayLists.get(h)[j][k].length() > maxChar) {
											maxChar = finalArrayLists.get(h)[j][k].length();
											maxCharListOrder = finalArrayLists.get(h)[j][reportInkjetHeaders.length - 1];
											maxCharFrom = files.get(h).getName();
										}
	
									}
	
								}
	
								if(tempMaxLine > maxLines) {
									maxLines = tempMaxLine;
									maxLinesListOrder = finalArrayLists.get(h)[j][reportInkjetHeaders.length - 1];
									maxLinesFrom = files.get(h).getName();
								}
							}
						}

						inkjetReport.append(String.format("%25s", "Max " + maxLines + " address lines:"));
						inkjetReport.append(" ");
						inkjetReport.append(lblListOrder.getText() + " #" + maxLinesListOrder + " from " + maxLinesFrom + " \n");
						
						inkjetReport.append(String.format("%25s", "Max field " + maxChar + " characters:"));
						inkjetReport.append(" ");
						inkjetReport.append(lblListOrder.getText() + " #" + maxCharListOrder + " from " + maxCharFrom + " \n");

						inkjetReport.append("\n\n");
						inkjetReport.append(String.format("%25s", "Please provide proofs to:"));
						inkjetReport.append(" ");
						inkjetReport.append("___________________________");

						
						// Finally write the report
						TextWriter.writeTextNoDelimiter(inkjetReportFile, inkjetReport.toString());
						
						//=======================================
						// END OF REPORT CREATION
						//=======================================

						JOptionPane.showMessageDialog(InkjetDialog.this, "Inkjet file export complete", "Success", JOptionPane.INFORMATION_MESSAGE);
					}
					
					
					

				}
			} catch (Exception err) {
				UiController.handle(err);
			} finally {
				enableUi();
			}
		}
	}
	
	

	public JCheckBox getChckbxUppercase() {
		return chckbxUppercase;
	}

	public JComboBox<String> getComboBoxLine1() {
		return comboBoxLine1;
	}

	public JComboBox<String> getComboBoxLine2() {
		return comboBoxLine2;
	}

	public JComboBox<String> getComboBoxLine3() {
		return comboBoxLine3;
	}

	public JComboBox<String> getComboBoxLine4() {
		return comboBoxLine4;
	}

	public JComboBox<String> getComboBoxLine5() {
		return comboBoxLine5;
	}

	public JComboBox<String> getComboBoxLine6() {
		return comboBoxLine6;
	}

	public JComboBox<String> getComboBoxLine7() {
		return comboBoxLine7;
	}

	public JComboBox<String> getComboBoxLine8() {
		return comboBoxLine8;
	}

	public JComboBox<String> getComboBoxAddress1() {
		return comboBoxAddress1;
	}

	public JComboBox<String> getComboBoxAddress2() {
		return comboBoxAddress2;
	}

	public JComboBox<String> getComboBoxAddress3() {
		return comboBoxAddress3;
	}

	public JComboBox<String> getComboBoxAddress4() {
		return comboBoxAddress4;
	}

	public JComboBox<String> getComboBoxCity() {
		return comboBoxCity;
	}

	public JComboBox<String> getComboBoxProv() {
		return comboBoxProv;
	}

	public JComboBox<String> getComboBoxPC() {
		return comboBoxPC;
	}

	public JComboBox<String> getComboBoxCountry() {
		return comboBoxCountry;
	}

	public JComboBox<String> getComboBoxDMC() {
		return comboBoxDMC;
	}

	public JComboBox<String> getComboBoxBagBundle() {
		return comboBoxBagBundle;
	}

	public JComboBox<String> getComboBoxBreaks() {
		return comboBoxBreaks;
	}

	public JComboBox<String> getComboBoxListOrder() {
		return comboBoxListOrder;
	}

	public ArrayList<JComboBox<String>> getComboBoxList() {
		return comboBoxList;
	}

	public JComboBox<String> getComboBoxMakeMultipleFiles() {
		return comboBoxMakeMultipleFiles;
	}

	public JButton getBtnMake() {
		return btnMake;
	}



}
