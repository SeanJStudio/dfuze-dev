package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DataVerificationDialog extends JDialog {
	// combo boxes
	private JComboBox<String> comboBoxLine1;
	private JComboBox<String> comboBoxLine2;
	private JComboBox<String> comboBoxLine3;
	private JComboBox<String> comboBoxLine4;
	private JComboBox<String> comboBoxName1;
	private JComboBox<String> comboBoxName2;
	private JComboBox<String> comboBoxName3;
	private JComboBox<String> comboBoxName4;
	private JComboBox<String> comboBoxAddress1;
	private JComboBox<String> comboBoxAddress2;
	private JComboBox<String> comboBoxAddress3;
	private JComboBox<String> comboBoxAddress4;
	private JComboBox<String> comboBoxCity;
	private JComboBox<String> comboBoxProv;
	private JComboBox<String> comboBoxPC;
	private JComboBox<String> comboBoxCountry;
	private JComboBox<String> comboBoxListOrder;

	// labels
	private JLabel lblLine1;
	private JLabel lblLine2;
	private JLabel lblLine3;
	private JLabel lblLine4;
	private JLabel lblName1;
	private JLabel lblName2;
	private JLabel lblName3;
	private JLabel lblName4;
	private JLabel lblAddress1;
	private JLabel lblAddress2;
	private JLabel lblAddress3;
	private JLabel lblAddress4;
	private JLabel lblCity;
	private JLabel lblProv;
	private JLabel lblPC;
	private JLabel lblCountry;
	private JLabel lblListOrder;
	
	private LinkedHashMap<String, JComboBox<String>> lineMap = new LinkedHashMap<>();
	private LinkedHashMap<String, JComboBox<String>> nameMap = new LinkedHashMap<>();
	private LinkedHashMap<String, JComboBox<String>> addressMap = new LinkedHashMap<>();
	
	// buttons
	private JButton btnReset;
	private JButton btnMake;

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
	
	private JLabel lblOptions;
	private JLabel lblTitle;
	
	// Details
	private JLabel lblJobNo;
	private JTextField textFieldJobNo;
	
	private JLabel lblJobName;
	private JTextField textFieldJobName;
	
	// patterns
	private final static Pattern COUNTRY_CAN_PATTERN = Pattern.compile("canada|canad|canda|cdn|(^|\\s+)can(\\s+|$)|(^|\\s+)ca(\\s+|$)", Pattern.CASE_INSENSITIVE);
	private final static Pattern COUNTRY_USA_PATTERN = Pattern.compile("estados|america|states|(^|\\s+)usa(\\s+|$)|(^|\\s+)us(\\s+|$)", Pattern.CASE_INSENSITIVE);
	private final static Pattern ZIP_PATTERN = Pattern.compile("\\d\\d\\d\\d\\d-\\d\\d\\d\\d|\\d\\d\\d\\d-\\d\\d\\d\\d|\\d\\d\\d\\d\\d\\d\\d\\d\\d$|\\d\\d\\d\\d\\d$|\\d\\d\\d\\d$", Pattern.CASE_INSENSITIVE);
	private final static Pattern STATE_PATTERN = Pattern.compile("alabama|alaska|american samoa|arizona|arkansas|california|colorado|connecticut|delaware|district of columbia|federated states of micronesia|florida|georgia|guam|hawaii|idaho|illinois|indiana|iowa|kansas|kentucky|louisiana|maine|marshall islands|maryland|massachusetts|michigan|minnesota|mississippi|missouri|montana|nebraska|nevada|new hampshire|new jersey|new mexico|new york|north carolina|north dakota|northern marianais|ohio|oklahoma|oregon|palau|pennsylvania|puerto rico|rhode island|south carolina|south dakota|tennessee|texas|utah|vermont|virginia|virgin islands|washington|west virginia|wisconsin|wyoming|(?<=\\s|^|,|\\.)(a(\\s\\.|\\.\\s|\\.|\\s)?l|a(\\s\\.|\\.\\s|\\.|\\s)?k|a(\\s\\.|\\.\\s|\\.|\\s)?s|a(\\s\\.|\\.\\s|\\.|\\s)?z|a(\\s\\.|\\.\\s|\\.|\\s)?r|c(\\s\\.|\\.\\s|\\.|\\s)?a|c(\\s\\.|\\.\\s|\\.|\\s)?o|c(\\s\\.|\\.\\s|\\.|\\s)?t|d(\\s\\.|\\.\\s|\\.|\\s)?e|d(\\s\\.|\\.\\s|\\.|\\s)?c|f(\\s\\.|\\.\\s|\\.|\\s)?m|f(\\s\\.|\\.\\s|\\.|\\s)?l|g(\\s\\.|\\.\\s|\\.|\\s)?a|g(\\s\\.|\\.\\s|\\.|\\s)?u|h(\\s\\.|\\.\\s|\\.|\\s)?i|i(\\s\\.|\\.\\s|\\.|\\s)?d|i(\\s\\.|\\.\\s|\\.|\\s)?l|i(\\s\\.|\\.\\s|\\.|\\s)?n|i(\\s\\.|\\.\\s|\\.|\\s)?a|k(\\s\\.|\\.\\s|\\.|\\s)?s|k(\\s\\.|\\.\\s|\\.|\\s)?y|l(\\s\\.|\\.\\s|\\.|\\s)?a|m(\\s\\.|\\.\\s|\\.|\\s)?e|m(\\s\\.|\\.\\s|\\.|\\s)?h|m(\\s\\.|\\.\\s|\\.|\\s)?d|m(\\s\\.|\\.\\s|\\.|\\s)?a|m(\\s\\.|\\.\\s|\\.|\\s)?i|m(\\s\\.|\\.\\s|\\.|\\s)?n|m(\\s\\.|\\.\\s|\\.|\\s)?s|m(\\s\\.|\\.\\s|\\.|\\s)?o|m(\\s\\.|\\.\\s|\\.|\\s)?t|n(\\s\\.|\\.\\s|\\.|\\s)?e|n(\\s\\.|\\.\\s|\\.|\\s)?v|n(\\s\\.|\\.\\s|\\.|\\s)?h|n(\\s\\.|\\.\\s|\\.|\\s)?j|n(\\s\\.|\\.\\s|\\.|\\s)?m|n(\\s\\.|\\.\\s|\\.|\\s)?y|n(\\s\\.|\\.\\s|\\.|\\s)?c|n(\\s\\.|\\.\\s|\\.|\\s)?d|m(\\s\\.|\\.\\s|\\.|\\s)?p|o(\\s\\.|\\.\\s|\\.|\\s)?h|o(\\s\\.|\\.\\s|\\.|\\s)?k|o(\\s\\.|\\.\\s|\\.|\\s)?r|p(\\s\\.|\\.\\s|\\.|\\s)?w|p(\\s\\.|\\.\\s|\\.|\\s)?a|p(\\s\\.|\\.\\s|\\.|\\s)?r|r(\\s\\.|\\.\\s|\\.|\\s)?i|s(\\s\\.|\\.\\s|\\.|\\s)?c|s(\\s\\.|\\.\\s|\\.|\\s)?d|t(\\s\\.|\\.\\s|\\.|\\s)?n|t(\\s\\.|\\.\\s|\\.|\\s)?x|u(\\s\\.|\\.\\s|\\.|\\s)?t|v(\\s\\.|\\.\\s|\\.|\\s)?t|v(\\s\\.|\\.\\s|\\.|\\s)?a|v(\\s\\.|\\.\\s|\\.|\\s)?i|w(\\s\\.|\\.\\s|\\.|\\s)?a|w(\\s\\.|\\.\\s|\\.|\\s)?v|w(\\s\\.|\\.\\s|\\.|\\s)?i|w(\\s\\.|\\.\\s|\\.|\\s)?y)(?=,|\\.|\\s|$)", Pattern.CASE_INSENSITIVE);
	
	public enum reason {
		FIRST_RECORD("First Record"),
		LAST_RECORD("Last Record"),
		MIN_LINES("Min Lines"),
		MAX_LINES("Max Lines"),
		SHORTEST_ADDRESS("Shortest Address"),
		LONGEST_ADDRESS("Longest Address"),
		SHORTEST_NAME("Shortest Name"),
		LONGEST_NAME("Longest Name"),
		RANDOM("Random");
		
		String name;

		private reason(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	};
	
	
	public DataVerificationDialog(JFrame frame) {

		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("DV Maker");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 245));
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 10 28 20 28, gap 0", "[62px:n:62px][42px:n:42px,grow][22px:n:22px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px][28px:n:28px][62px:n:62px][62px:n:62px]", "[6px:n:6px][36px:n:36px][4px:n:4px][28px:n:28px][28px:n:28px][28px:n:28px][36px:n:36px][4px:n:4px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px][28px:n:28px][36px:n:36px][28px:n:28px]"));
		
		lblOptions = new JLabel("Details");
		lblOptions.setForeground(Theme.TITLE_COLOR);
		lblOptions.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblOptions, "cell 0 1");

		separatorSettingsTop = new JSeparator();
		contentPanel.add(separatorSettingsTop, "cell 1 1 11 1,growx,aligny center");
		
		lblJobNo = new JLabel("Job No");
		lblJobNo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblJobNo, "cell 0 3,alignx left,aligny top");
		
		lblJobName = new JLabel("Job Name");
		lblJobName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblJobName, "cell 4 3,alignx left,aligny top");

		lblMakeMultipleFiles = new JLabel("Make a DV for each unique value found in:");
		lblMakeMultipleFiles.setForeground(Color.BLACK);
		lblMakeMultipleFiles.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblMakeMultipleFiles, "cell 7 3 5 1,alignx left,aligny top");

		comboBoxMakeMultipleFiles = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxMakeMultipleFiles.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxMakeMultipleFiles.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxMakeMultipleFiles.setSelectedIndex(-1);
		comboBoxMakeMultipleFiles.addActionListener(new ComboBoxMultipleFilesHandler());
		
		textFieldJobNo = new JTextField();
		textFieldJobNo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(textFieldJobNo, "cell 0 4 3 1,grow");
		textFieldJobNo.setColumns(10);
		((AbstractDocument) textFieldJobNo.getDocument()).setDocumentFilter(new IntegerDocumentFilter(6));
		
		textFieldJobName = new JTextField();
		textFieldJobName.setColumns(10);
		contentPanel.add(textFieldJobName, "cell 4 4 2 1,grow");
		contentPanel.add(comboBoxMakeMultipleFiles, "cell 7 4 2 1,grow");

		lblMakeMultipleFilesPreview = new JLabel("");
		lblMakeMultipleFilesPreview.setForeground(Theme.TITLE_COLOR);
		lblMakeMultipleFilesPreview.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblMakeMultipleFilesPreview, "cell 10 4 2 1,alignx center,aligny center");

		separatorSettingsBottom = new JSeparator();
		contentPanel.add(separatorSettingsBottom, "cell 1 6 11 1,growx");
		
		lblTitle = new JLabel("Fields");
		lblTitle.setForeground(Theme.TITLE_COLOR);
		lblTitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblTitle, "cell 0 6");

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
		
		lblName1 = new JLabel("NAME1");
		lblName1.setForeground(Color.BLACK);
		lblName1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblName1, "cell 0 11");
		
		lblName2 = new JLabel("NAME2");
		lblName2.setForeground(Color.BLACK);
		lblName2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblName2, "cell 4 11");
		
		lblName3 = new JLabel("NAME3");
		lblName3.setForeground(Color.BLACK);
		lblName3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblName3, "cell 7 11");
		
		lblName4 = new JLabel("NAME4");
		lblName4.setForeground(Color.BLACK);
		lblName4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblName4, "cell 10 11");
		
		comboBoxName1 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxName1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxName1.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxName1.setSelectedIndex(-1);
		comboBoxName1.addItemListener(new ComboBoxMappingListener());
		comboBoxName1.setName(lblName1.getText());
		contentPanel.add(comboBoxName1, "cell 0 12 3 1,grow");
		
		comboBoxName2 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxName2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxName2.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxName2.setSelectedIndex(-1);
		comboBoxName2.addItemListener(new ComboBoxMappingListener());
		comboBoxName2.setName(lblName2.getText());
		contentPanel.add(comboBoxName2, "cell 4 12 2 1,grow");
		
		comboBoxName3 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxName3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxName3.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxName3.setSelectedIndex(-1);
		comboBoxName3.addItemListener(new ComboBoxMappingListener());
		comboBoxName3.setName(lblName3.getText());
		contentPanel.add(comboBoxName3, "cell 7 12 2 1,grow");
		
		comboBoxName4 = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxName4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxName4.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxName4.setSelectedIndex(-1);
		comboBoxName4.addItemListener(new ComboBoxMappingListener());
		comboBoxName4.setName(lblName4.getText());
		contentPanel.add(comboBoxName4, "cell 10 12 2 1,grow");

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

		lblListOrder = new JLabel("LISTORDER");
		lblListOrder.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblListOrder.setForeground(new Color(220, 20, 60));
		contentPanel.add(lblListOrder, "cell 0 20 12 1,aligny center");

		comboBoxListOrder = new JComboBox<String>(new DefaultComboBoxModel<String>(UiController.getUserData().getExportHeaders()));
		comboBoxListOrder.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxListOrder.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBoxListOrder.setSelectedIndex(-1);
		comboBoxListOrder.addItemListener(new ComboBoxMappingListener());
		comboBoxListOrder.addActionListener(new ComboBoxListOrderHandler());
		comboBoxListOrder.setName(lblListOrder.getText());
		contentPanel.add(comboBoxListOrder, "cell 0 21 3 1,grow");

		JSeparator separatorBottom = new JSeparator();
		contentPanel.add(separatorBottom, "cell 0 23 12 1,growx,aligny center");

		btnReset = new JButton("Reset Fields");
		btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnReset.addActionListener(new ResetButtonHandler());
		contentPanel.add(btnReset, "cell 0 24 3 1,grow");

		btnMake = new JButton("Make Data Verification");
		btnMake.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMake.addActionListener(new CreateButtonHandler());
		
				lblSubTitle = new JLabel("LISTORDER is required");
				lblSubTitle.setForeground(new Color(220, 20, 60));
				lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
				contentPanel.add(lblSubTitle, "cell 4 24 2 1,alignx right");
		contentPanel.add(btnMake, "cell 7 24 5 1,grow");
		
		fillComboBoxList();
		fillLineMap();
		fillNameMap();
		fillAddressMap();
		
		pack();
	    setLocationRelativeTo(frame);
		getContentPane().requestFocusInWindow();
	}
	
	private void fillLineMap() {
		lineMap.put(lblLine1.getText(), comboBoxLine1); 
		lineMap.put(lblLine2.getText(), comboBoxLine2);
		lineMap.put(lblLine3.getText(), comboBoxLine3);
		lineMap.put(lblLine4.getText(), comboBoxLine4);
	}
	
	private void fillNameMap() {
		nameMap.put(lblName1.getText(), comboBoxName1); 
		nameMap.put(lblName2.getText(), comboBoxName2);
		nameMap.put(lblName3.getText(), comboBoxName3);
		nameMap.put(lblName4.getText(), comboBoxName4);
	}
	
	private void fillAddressMap() {
		addressMap.put(lblAddress1.getText(), comboBoxAddress1); 
		addressMap.put(lblAddress2.getText(), comboBoxAddress2);
		addressMap.put(lblAddress3.getText(), comboBoxAddress3);
		addressMap.put(lblAddress4.getText(), comboBoxAddress4);
		addressMap.put(lblCity.getText(), comboBoxCity);
		addressMap.put(lblProv.getText(), comboBoxProv);
		addressMap.put(lblPC.getText(), comboBoxPC);
		addressMap.put(lblCountry.getText(), comboBoxCountry);
	}

	private void fillComboBoxList() {
		comboBoxList.add(comboBoxLine1);
		comboBoxList.add(comboBoxLine2);
		comboBoxList.add(comboBoxLine3);
		comboBoxList.add(comboBoxLine4);
		comboBoxList.add(comboBoxName1);
		comboBoxList.add(comboBoxName2);
		comboBoxList.add(comboBoxName3);
		comboBoxList.add(comboBoxName4);
		comboBoxList.add(comboBoxAddress1);
		comboBoxList.add(comboBoxAddress2);
		comboBoxList.add(comboBoxAddress3);
		comboBoxList.add(comboBoxAddress4);
		comboBoxList.add(comboBoxCity);
		comboBoxList.add(comboBoxProv);
		comboBoxList.add(comboBoxPC);
		comboBoxList.add(comboBoxCountry);
		comboBoxList.add(comboBoxListOrder);
	}

	private void disableUi() {
		for(JComboBox<String> cb : comboBoxList)
			cb.setEnabled(false);

		textFieldJobNo.setEnabled(false);
		textFieldJobName.setEnabled(false);
		comboBoxMakeMultipleFiles.setEnabled(false);
		btnReset.setEnabled(false);
		btnMake.setEnabled(false);
	}

	private void enableUi() {
		for(JComboBox<String> cb : comboBoxList)
			cb.setEnabled(true);

		textFieldJobNo.setEnabled(true);
		textFieldJobName.setEnabled(true);
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

					comboBoxMakeMultipleFiles.setSelectedIndex(-1);
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}
	
	// Checks if the "make file for each unique value" field is an internal dfuze field or not
	private boolean isUniqueFieldDfField() {
		for(String dfField : UiController.getUserData().getDfHeaders()) {
			if(dfField.equalsIgnoreCase(comboBoxMakeMultipleFiles.getSelectedItem().toString())) {
				return true;
			}
		}
		return false;
	}
	
	// Gets a treeset of unique segments in insertion order
	private SortedSet<String> getUniqueFileSegments(boolean isUniqueFieldDfField){
		SortedSet<String> uniqueFileSegments = new TreeSet<>();

			if(isUniqueFieldDfField) {
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
			
			return uniqueFileSegments;
	}
	
	// Places multiple record lists into a list based on the unique field values
	public List<List<Record>> getUniqueFileRecordLists(SortedSet<String> uniqueFileSegments, boolean isDfField){
		List<List<Record>> recordLists = new ArrayList<>();
		
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
		
		return recordLists;
	}
	
	private ArrayList<String> getLinesFromList(Record record, LinkedHashMap<String, JComboBox<String>> linesMap, HashMap<String, Integer> nonDfFieldIndexes) {
		ArrayList<String> list = new ArrayList<>();
		String cpp = "";
		String country = "";
		
		for(Map.Entry<String, JComboBox<String>> entry : linesMap.entrySet()) {
			String selectedField = entry.getValue().getSelectedItem().toString();
			String value = getValueFromFieldName(record, selectedField, nonDfFieldIndexes).trim();
			
			// handle address fields separately
			if(entry.getKey().equalsIgnoreCase(lblCity.getText()))
				cpp += " " + value;
			else if(entry.getKey().equalsIgnoreCase(lblProv.getText()))
				cpp += " " + value;
			else if(entry.getKey().equalsIgnoreCase(lblPC.getText()))
				cpp += "  " + value;
			else if(entry.getKey().equalsIgnoreCase(lblCountry.getText()))
				country += value;
			else
				list.add(value);
		}
		
		cpp = cpp.trim();
		
		if(cpp.length() > 0)
			list.add(cpp);
		if(country.length() > 0)
			list.add(country);
		
		return list;		
	}
	
	
	private void verifyData(List<List<Record>> recordLists) {
		HashMap<String, Integer> nonDfFieldIndexes = getNonDfFieldIndexes();
		ArrayList<HashMap<Integer, String>> reasonList = new ArrayList<>();
		
		boolean hasCity = false;
		boolean hasProv = false;
		boolean hasPc = false;
		boolean hasCPP = false;
		boolean hasCountry = false;
		
		if(comboBoxCity.getSelectedIndex() > -1)
			hasCity = true;
		if(comboBoxProv.getSelectedIndex() > -1)
			hasProv = true;
		if(comboBoxPC.getSelectedIndex() > -1)
			hasPc = true;
		if(hasCity && hasProv && hasPc)
			hasCPP = true;
		if(comboBoxCountry.getSelectedIndex() > -1)
			hasCountry = true;

		
		for(List<Record> recordList : recordLists) {
			// track
			int counter = 0;
			
			// Destination total
			int recordNum = recordList.size();
			int canadianNum = 0;
			int usaNum = 0;
			int intNum = 0;
			
			// reason
			int firstRecordId = -1;
			int lastRecordId = -1;
			
			int minLines = 999999;
			int minLinesId = -1;
			
			int maxLines = 0;
			int maxLinesId = -1;
			
			int shortestAddress = 999999;
			int shortestAddressId = -1;
			
			int longestAddress = 0;
			int longestAddressId = -1;
			
			int shortestName = 999999;
			int shortestNameId = -1;
			
			int longestName = 0;
			int longestNameId = -1;
			
			// key is the record id, value is a map of key reason and value arraylist (address block)
			LinkedHashMap<Integer, HashMap<String, ArrayList<String>>> reasonMap = new LinkedHashMap<>();
			
			HashMap<Integer, ArrayList<String>> idAddressMap = new HashMap<>();
			
			for(Record record : recordList) {
				// get the lineLists
				ArrayList<String> lineList = getLinesFromList(record, lineMap, nonDfFieldIndexes);
				ArrayList<String> nameList = getLinesFromList(record, nameMap, nonDfFieldIndexes);
				ArrayList<String> addressList = getLinesFromList(record, addressMap, nonDfFieldIndexes);
				ArrayList<String> combinedList = new ArrayList<>();
				combinedList.addAll(lineList);
				combinedList.addAll(nameList);
				combinedList.addAll(addressList);
				
				int lines = combinedList.size();
				
				// Get the country
				if(hasCountry) {
					String country = StringUtils.stripAccents(getValueFromFieldName(record, comboBoxCountry.getSelectedItem().toString(), nonDfFieldIndexes)).replaceAll("[^a-zA-Z]", "").trim();
					if(country.length() > 0) {
						if(COUNTRY_CAN_PATTERN.matcher(country).find())
							++canadianNum;
						else if(COUNTRY_USA_PATTERN.matcher(country).find())
							++usaNum;
						else
							++intNum;
					} else {
						++canadianNum;
					}
				} else {
					if(hasPc) {
						String pc = StringUtils.stripAccents(getValueFromFieldName(record, comboBoxPC.getSelectedItem().toString(), nonDfFieldIndexes)).replaceAll("[^a-zA-Z0-9]", "").trim();
						if(ZIP_PATTERN.matcher(pc).find())
							++usaNum;
						else
							++canadianNum;
					} else if (hasProv) {
						String prov = StringUtils.stripAccents(getValueFromFieldName(record, comboBoxProv.getSelectedItem().toString(), nonDfFieldIndexes)).replaceAll("[^a-zA-Z0-9\\s]", "").trim();
						if(STATE_PATTERN.matcher(prov).find())
							++usaNum;
						else
							++canadianNum;
					} else {
						++canadianNum;
					}
				}
				
				// get reasons
				
				// first and last records
				if(counter == 0)
					firstRecordId = record.getDfId();
				else if(counter == recordList.size() - 1)
					lastRecordId = record.getDfId();
				
				// min lines
				if(lines < minLines) {
					minLines = lines;
					minLinesId = record.getDfId();
				}
				
				// max lines
				if(lines > maxLines) {
					maxLines = lines;
					maxLinesId = record.getDfId();
				}
				
				// shortest and longest address
				for(String line : addressList) {
					if(line.length() < shortestAddress) {
						shortestAddress = line.length();
						shortestAddressId = record.getDfId();
					}
					if(line.length() > longestAddress) {
						longestAddress = line.length();
						longestAddressId = record.getDfId();
					}
				}
				
				// shortest and longest name
				for(String line : nameList) {
					if(line.length() < shortestName) {
						shortestName = line.length();
						shortestNameId = record.getDfId();
					}
					if(line.length() > longestName) {
						longestName = line.length();
						longestNameId = record.getDfId();
					}
				}
				
				
				idAddressMap.put(record.getDfId(), combinedList);
				++counter;
			}
			
			// add the records we have tracked
			// if a record has been added, append to the reason like + " " + reason
			// if the amount of records is less than 10, fill in with random records until 10 or no records left
			// finally add to the master reason map of all lists
			
		}
	}
	
	String getValueFromFieldName(Record record, String fieldValueToGet, HashMap<String, Integer> nonDfFieldIndexes) {
		if(nonDfFieldIndexes.containsKey(fieldValueToGet))
			return record.getDfInData()[nonDfFieldIndexes.get(fieldValueToGet)];
		
		return UserData.getRecordFieldByName(fieldValueToGet, record);
	}
	
	private HashMap<String, Integer> getNonDfFieldIndexes() {
		HashMap<String, Integer> nonDfFieldIndexes = new HashMap<>();
		for (String usedField : usedFields) {
			boolean isDfField = false;
			for(String dfField : UiController.getUserData().getDfHeaders()) {
				if(usedField.equalsIgnoreCase(dfField)) {
					isDfField = true;
					break;
				}
			}
			
			if(!isDfField) {
				for(int i = 0; i < UiController.getUserData().getExportHeaders().length; ++i) {
					if(usedField.equalsIgnoreCase(UiController.getUserData().getExportHeaders()[i])) {
						nonDfFieldIndexes.put(usedField, i);
						break;
					}
				}
			}
		}
		
		return nonDfFieldIndexes;
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
					
					//================================================================
					// PLACE RECORDS INTO GROUPS BASED ON UNIQUE FIELD VALUES
					//================================================================
					
					List<List<Record>> recordLists = new ArrayList<>();
					
					SortedSet<String> uniqueFileSegments = new TreeSet<>();
					
					if(comboBoxMakeMultipleFiles.getSelectedIndex() > -1) {
						boolean isDfField = isUniqueFieldDfField();
						uniqueFileSegments = getUniqueFileSegments(isDfField);
						recordLists = getUniqueFileRecordLists(uniqueFileSegments, isDfField);
					} else {
						recordLists.add(UiController.getUserData().getRecordList());
					}
									
					//============================================
					// FOR EACH LIST, GENERATE THE DATA AND REPORT
					//============================================
					
					
					

				}
			} catch (Exception err) {
				UiController.handle(err);
			} finally {
				enableUi();
			}
		}
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

	public JComboBox<String> getComboBoxName1() {
		return comboBoxName1;
	}

	public JComboBox<String> getComboBoxName2() {
		return comboBoxName2;
	}

	public JComboBox<String> getComboBoxName3() {
		return comboBoxName3;
	}

	public JComboBox<String> getComboBoxName4() {
		return comboBoxName4;
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

	public JTextField getTextFieldJobNo() {
		return textFieldJobNo;
	}

	public JTextField getTextFieldJobName() {
		return textFieldJobName;
	}


}
