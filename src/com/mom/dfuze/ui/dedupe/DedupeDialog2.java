package com.mom.dfuze.ui.dedupe;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.ui.IntegerDocumentFilter;
import com.mom.dfuze.ui.UiController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

@SuppressWarnings("serial")
public class DedupeDialog2 extends JDialog {
	private final JPanel contentPanel = new JPanel();

	private JLabel lblName;

	private JLabel lblName2;

	private JComboBox<String> comboBoxName1;

	private JComboBox<String> comboBoxName2;

	private JComboBox<String> comboBoxPriority;

	private JRadioButton rdbtnAsc;

	private JRadioButton rdbtnDesc;

	private JButton btnRun;

	private JButton btnView;

	private JButton btnExport;

	private JButton btnDelete;

	private JProgressBar progressBar;

	private JLabel lblTotalDupes;

	private JLabel lblTotalRecordsAmount;

	private JLabel lblTotalDupesAmount;

	private JLabel lblParentDupes;

	private JLabel lblParentDupesAmount;

	private JLabel lblChildDupes;

	private JLabel lblChildDupesAmount;

	private JLabel lblElapsedTime;

	private JLabel lblElapsedTimeAmount;

	private Timer timer;

	private String killPriority;

	private DedupeViewDialog2 dedupeViewDialog;

	private DedupeExportDialog dedupeExportDialog;

	private ArrayList<Record> allDupeRecordsList;
	private ArrayList<Record> allDupeRecordsList1;
	private ArrayList<Record> allDupeRecordsList2;
	private ArrayList<Record> allDupeRecordsList3;
	private ArrayList<Record> allDupeRecordsList4;
	
	private static AtomicInteger duplicateAddressCounter = new AtomicInteger(0);
	private static AtomicInteger preprocessingAddressCounter = new AtomicInteger(0);
	private static AtomicInteger parentDupesCounter = new AtomicInteger(0);
	private static AtomicInteger childDupesCounter = new AtomicInteger(0);
	private static AtomicInteger totalDupesCounter = new AtomicInteger(0);

	private int percentageForNameSimilarity = 84;

	//private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXX";

	static final String FIX_HYPHENS = "\\p{Pd}";

	static final String ADD_STRIP_REGEX = "[^a-zA-Z]";

	static final Pattern ADD_NUM_PATTERN = Pattern.compile("^\\d+((\\D)?(\\s)?-(\\s)?\\d+)?", 2);

	static final String ADD_PO_BOX_REGEX = "(p(\\s)?o(\\s)?box|p(\\s)?0(\\s)?box|p(\\s)?o(\\s#|#\\s|#|\\s|-|(?=\\d+))|box(\\s)?(?=\\D)|comp|cp|rmb|lb)(\\s+)?(\\D)?\\d+";

	static final Pattern ADD_PO_BOX_PATTERN = Pattern.compile("(p(\\s)?o(\\s)?box|p(\\s)?0(\\s)?box|p(\\s)?o(\\s#|#\\s|#|\\s|-|(?=\\d+))|box(\\s)?(?=\\D)|comp|cp|rmb|pmb|lb)(\\s+)?(\\D)?\\d+", 2);

	static final String ADD_PO_BOX_EXTRA_REGEX = "(group|grp|coup|gr|gp)\\s?(\\D$|\\D\\s)?\\s?\\d+|site\\s?\\d+|(^|\\s)s\\d+";

	static final Pattern ADD_PO_BOX_EXTRA_PATTERN = Pattern.compile("(group|grp|coup|gr|gp)\\s?(\\D$|\\D\\s)?\\s?\\d+|site\\s?\\d+|(^|\\s)s\\d+", 2);

	static final String ADD_RR_REGEX = "(rr|rfd)\\s?#?\\d*(\\s?\\D$|\\D\\s)?";

	static final Pattern ADD_RR_PATTERN = Pattern.compile("(^|\\s)(rr|rfd)\\s?#?\\d*(\\s?\\D$|\\D\\s)?", 2);

	static final Pattern ADD_APT_PATTERN = Pattern.compile("(apt)(\\s+)?(#|\\D)?(\\s+)?\\d+", 2);

	static final String FIX_APT_REGEX = "((trlr|trailer|lot|apartment|condo|appartement|suite|suit|ste|spc|space|room|rm|office|ofc|unit|bureau|piece|(?<=^|\\s+)ph|(?<=^|\\s+)th)(?=\\s+|$|#|-|\\d+))";

	static final String FIX_US_APT_REGEX = "(?<!(\\d+\\s+(?=(no|#))|apt|pmb|box|rmb|(\\s+|^)?p(\\s+)?o(\\s+)?\\d|comp|cp|(group|grp|coup|gr|gp)\\s?(\\D$|\\D\\s)?\\s?\\d+|site\\s?\\d+|(^|\\s)s\\d+).*)((#|no)(?=(\\s+)?(\\D)?\\d+(\\D?)$))";

	static final String REMOVE_APT_REGEX = "^(\\D)?(\\s+)?(\\D)?(\\s+)?\\d+(\\D)?(\\s+)?(\\D)?(\\s+)?-";

	static final Pattern APT_MISSING_HYPHEN_PATTERN = Pattern.compile("(?<=^\\d+(\\D)?)\\s+(?=\\d+\\s+(?!TH(\\s+|$|-|\\.+|,)|ND|RD|STREET|ST(\\s+|$|-|\\.+|,)|ROAD|BLVD|AVE|WAY|CRES|LINE|RANGE|RN|RG|ROUTE|RT|HIGHWAY|HIGH(\\s+|$|-|\\.+|,)|HW|HIW|TOWN|TWN|TRAIL|TRL|CONC|CN))", Pattern.CASE_INSENSITIVE);
	
	static final Pattern APT_WRONG_HYPHEN_PATTERN = Pattern.compile("(?<=^\\d+)(\\s+)?-(\\s+)?(?=\\d+\\s+(TH(\\s+|$|-|\\.+|,)|ND|RD|STREET|ST(\\s+|$|-|\\.+|,)|ROAD|BLVD|AVE|WAY|CRES|LINE|RANGE|RN|RG|ROUTE|RT|HIGHWAY|HIGH(\\s+|$|-|\\.+|,)|HW|HIW|TOWN|TWN|TRAIL|TRL|CONC|CN))",Pattern.CASE_INSENSITIVE);
	
	static final Pattern REMOVE_APT_PATTERN = Pattern.compile("^(\\D)?(\\s+)?(\\D)?(\\s+)?\\d+(\\D)?(\\s+)?(\\D)?(\\s+)?-", 2);

	static final String REMOVE_FLOOR_REGEX = "(?i)\\d+(\\D+)?(\\s+)?(floor|etage)";

	static final String REMOVE_BUZZER_REGEX = "(buzzer(\\s)?(number|num)?|buzz(\\s)?(code)?)(\\s#|\\s|#)?\\d+";

	static final String REMOVE_NUM_ORDINAL_REGEX = "(?<=[0-9])[a-zA-Z]+";

	static final String REMOVE_NUM_PRE_ORDINAL_REGEX = "[a-zA-Z]+(?=\\d+)";

	static final String LEADING_ZERO_REGEX = "^0+(?!$)";

	static final String FIX_HYPHEN_REGEX = "(\\s+)?-(\\s+)?";

	static final Pattern REPLACE_HWY_NUM_PATTERN = Pattern.compile("(hwy|highway)\\s?\\d+", 2);

	static final Pattern REPLACE_COUNTY_ROAD_NUM_PATTERN = Pattern.compile("county\\s(road|rd)\\s?\\d+", 2);

	public static final String FIX_GD_REGEX = "(?<=\\s|^)gd(?=\\s|$)|(gen|gn)(\\D+)?(?=\\s|$)(\\s)?((del|dl)(\\D+)?(?=\\s|$))";

	private Map<JComboBox<String>, String> mappedFields = new HashMap<>();

	Thread dedupeThread = null;

	boolean isDialogClosed = false;

	private JLabel lblResults;

	private JLabel lblStatus;

	private JCheckBox chckbxExactNamesOnly;

	private JLabel lblDedupeBy;

	private JRadioButton rdbtnDedupeAddress;

	private JRadioButton rdbtnDedupeName;

	private ButtonGroup priorityRadioGroup;

	private ButtonGroup deduplicateRecordByRadioGroup;

	private JCheckBox chckbxKillFilePriority;

	private JLabel lblPercentageForNameSimilarity;

	private JTextField textFieldNameSimilarityPercentage;

	private JLabel labelPercentage;

	private JLabel lblIfName1Blank;

	private JComboBox<String> comboBoxIfName1Blank;

	private JLabel lblPriorityValue;

	private JComboBox<String> comboBoxIfName2Blank;

	private JLabel lblIfName2Blank;

	private JLabel lblNames;

	private JSeparator separator;

	private JSeparator separator_1;

	private JSeparator separator_3;

	private JSeparator separator_4;

	private JSeparator separator_5;

	private JLabel lblPrioritySorting;

	private JLabel lblPriorityFormat;

	private JComboBox<String> comboBoxPriorityFormat;

	private JLabel lblPriorityDateFormat;

	private JTextField textFieldPriorityDateFormat;

	private enum PRIORITY_FORMATS {
		NUMBER, DATE, TEXT;
	}

	private boolean isName1Selected = false;

	private boolean isName2Selected = false;

	private boolean isIfName1BlankSelected = false;

	private boolean isIfName2BlankSelected = false;

	private boolean isDfName1Set = false;

	private boolean isDfName2Set = false;

	private boolean isDfIfName1BlankSelected = false;

	private boolean isDfIfName2BlankSelected = false;

	private boolean isPrioritySelected = false;

	private boolean isDfPrioritySet = false;

	private JButton btnPriorityDateFormatHelp;

	private JButton btnDedupeOptionHelp;

	private JRadioButton rdbtnDedupePC;

	private JCheckBox chckbxIgnoreApt;

	private JCheckBox chckbxMatchOnNameSimilarityOnly;

	private JPanel panel;

	private JPanel panel_1;
	private JCheckBox chckbxIncludeBlankNames;
	private JCheckBox chckbxConvertPcToFsa;

	public enum dupeInfo {

		DUPE_STREET_ADD1("dfDuSAdd1"),
		DUPE_STREET_ADD2("dfDuSAdd2"),
		DUPE_META_STREET_ADD1("dfDuMSAdd1"),
		DUPE_META_STREET_ADD2("dfDuMSAdd2"),
		DUPE_ADD1("Add1St#"),
		DUPE_ADD2("Add2St#"),
		DUPE_PCODE("PC"),
		DUPE_POBOX("POBox"),
		DUPE_POBOX_EXTRA("POBoxExtra"),
		DUPE_RR("RuralRd"),
		DUPE_NAME1("Name1"),
		DUPE_NAME1_FIRST_PERSON("Name1Person1"),
		DUPE_NAME1_SECOND_PERSON("Name1Person2"),
		DUPE_NAME1_NORMALIZED("Name1Normalized"),
		DUPE_NAME2("Name2"),
		DUPE_NAME2_FIRST_PERSON("Name2Person1"),
		DUPE_NAME2_SECOND_PERSON("Name2Person2"),
		DUPE_NAME2_NORMALIZED("Name2Normalized");

		String name;

		private dupeInfo(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	};

	public DedupeDialog2(JFrame frame) {
		super(frame, true);
		setBackground(new Color(255, 255, 255));
		setTitle("Record Deduplication");
		setResizable(false);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(2);
		getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setForeground(new Color(0, 0, 0));
		this.contentPanel.setBackground(UIManager.getColor("menu"));
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, "Center");
		this.contentPanel.setLayout((LayoutManager)new MigLayout("insets 0, gap 0", "[28px:n:28px][60px:n:60px][60px:n:60px][18px:n:18px][20px:n:20px][20px:n:20px][20px:n:20px][20px:n:20px][20px:n:20px][20px:n:20px][28px:n:28px][14px:n:14px][28px:n:28px][120px:n:120px][14px:n:14px][120px:n:120px][28px:n:28px]", "[12px:n:12px][36px:n:36px][36px:n:36px][36px:n:36px][36px:n:36px][10px:n:10px][36px:n:36px][28px:n:28px][10px:n:10px,grow][28px:n:28px][28px:n:28px][10px:n:10px][36px:n:36px][36px:n:36px][36px:n:36px][36px:n:36px][28px:n:28px,grow][10px:n:10px][36px:n:36px][28px:n:28px][12px:n:12px][36px:n:36px][36px:n:36px][28px:n:28px][14px:n:14px]"));
		this.priorityRadioGroup = new ButtonGroup();
		this.deduplicateRecordByRadioGroup = new ButtonGroup();
		this.lblNames = new JLabel("Name Settings");
		this.lblNames.setForeground(new Color(25, 25, 112));
		this.lblNames.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		this.contentPanel.add(this.lblNames, "cell 1 1 2 1,aligny center");
		this.separator_3 = new JSeparator();
		this.separator_3.setOrientation(1);
		this.contentPanel.add(this.separator_3, "cell 11 1 1 19,alignx center,growy");
		this.lblPercentageForNameSimilarity = new JLabel("Jaro-Winkler similarity threshold");
		this.lblPercentageForNameSimilarity.setForeground(Color.BLACK);
		this.lblPercentageForNameSimilarity.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblPercentageForNameSimilarity.setToolTipText("How similar two names must be to match using the Jaro-Winkler algorithm.");  
		this.contentPanel.add(this.lblPercentageForNameSimilarity, "cell 1 2 5 1,alignx left");

		this.textFieldNameSimilarityPercentage = new JTextField();
		this.textFieldNameSimilarityPercentage.setForeground(Color.BLACK);
		this.textFieldNameSimilarityPercentage.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.textFieldNameSimilarityPercentage.setHorizontalAlignment(0);
		((AbstractDocument)this.textFieldNameSimilarityPercentage.getDocument()).setDocumentFilter((DocumentFilter)new IntegerDocumentFilter(3));
		this.textFieldNameSimilarityPercentage.setToolTipText("How similar two names must be to match using the Jaro-Winkler algorithm."); 
		this.contentPanel.add(this.textFieldNameSimilarityPercentage, "cell 6 2 2 1,growx,aligny center");
		this.textFieldNameSimilarityPercentage.setColumns(10);

		String userPercentage = UserPrefs.getLastUsedDedupeMatchPercentage();

		if(Validators.isNumber(userPercentage))
			this.textFieldNameSimilarityPercentage.setText(userPercentage);
		else
			this.textFieldNameSimilarityPercentage.setText(String.valueOf(this.percentageForNameSimilarity));

		this.labelPercentage = new JLabel("%");
		this.labelPercentage.setForeground(Color.BLACK);
		this.labelPercentage.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(this.labelPercentage, "cell 8 2,alignx right,aligny center");
		
		this.chckbxExactNamesOnly = new JCheckBox("Match names exactly");
		this.chckbxExactNamesOnly.setForeground(Color.BLACK);
		this.chckbxExactNamesOnly.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.chckbxExactNamesOnly.addActionListener(new DedupeChckbxExactNamesOnlyHandler());
		
		chckbxConvertPcToFsa = new JCheckBox("Convert PC to FSA");
		chckbxConvertPcToFsa.setToolTipText("Use FSA instead of PC to find matches");
		chckbxConvertPcToFsa.setForeground(Color.BLACK);
		chckbxConvertPcToFsa.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(chckbxConvertPcToFsa, "cell 15 3,aligny bottom");
		this.chckbxExactNamesOnly.setToolTipText("A match will be made only if the two names are exactly the same."); 
		this.contentPanel.add(this.chckbxExactNamesOnly, "cell 1 4 3 1,aligny center");
		
		this.chckbxIgnoreApt = new JCheckBox("Ignore Apt #");
		this.chckbxIgnoreApt.setToolTipText("Ignores apartment #'s when matching records on the same Postal Code and Address.");
		this.chckbxIgnoreApt.setForeground(Color.BLACK);
		this.chckbxIgnoreApt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(this.chckbxIgnoreApt, "cell 15 2,aligny bottom");
		this.rdbtnDedupePC = new JRadioButton("PC");
		this.rdbtnDedupePC.setToolTipText("Matches records on the same Postal Code.");
		this.rdbtnDedupePC.setForeground(Color.BLACK);
		this.rdbtnDedupePC.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.rdbtnDedupePC.addActionListener(new DedupeRadioButtonHandler());
		this.contentPanel.add(this.rdbtnDedupePC, "cell 13 3,aligny bottom");
		this.deduplicateRecordByRadioGroup.add(this.rdbtnDedupePC);
		
		this.chckbxMatchOnNameSimilarityOnly = new JCheckBox("Exclude NYSIIS, Metaphone3, Refined Soundex");
		chckbxMatchOnNameSimilarityOnly.setForeground(Color.BLACK);
		chckbxMatchOnNameSimilarityOnly.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.chckbxMatchOnNameSimilarityOnly.setToolTipText("Only uses Jaro-Winkler algorithm to find a match. (Excludes NYSIIS, Metaphone3 and Refined Soundex)"); 
		this.contentPanel.add(this.chckbxMatchOnNameSimilarityOnly, "cell 1 3 10 1,aligny center");
		this.chckbxMatchOnNameSimilarityOnly.addActionListener(new DedupeChckbxMatchOnNameSimilarityOnlyHandler());
		
		chckbxIncludeBlankNames = new JCheckBox("Include blank names");
		chckbxIncludeBlankNames.setToolTipText("Records with blank names will be included.");
		chckbxIncludeBlankNames.setForeground(Color.BLACK);
		chckbxIncludeBlankNames.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(chckbxIncludeBlankNames, "cell 4 4 7 1");
		
		this.separator_5 = new JSeparator();
		this.contentPanel.add(this.separator_5, "cell 13 6 3 1,growx,aligny center");
		this.lblDedupeBy = new JLabel("Match On");
		this.lblDedupeBy.setForeground(new Color(25, 25, 112));
		this.lblDedupeBy.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		this.contentPanel.add(this.lblDedupeBy, "cell 13 1,aligny center");
		this.rdbtnDedupeAddress = new JRadioButton("PC + Address");
		this.rdbtnDedupeAddress.setToolTipText("Matches records on the same Postal Code and Address.");
		this.rdbtnDedupeAddress.setForeground(Color.BLACK);
		this.rdbtnDedupeAddress.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.rdbtnDedupeAddress.setSelected(true);
		this.rdbtnDedupeAddress.addActionListener(new DedupeRadioButtonHandler());
		this.contentPanel.add(this.rdbtnDedupeAddress, "cell 13 2,aligny bottom");
		this.deduplicateRecordByRadioGroup.add(this.rdbtnDedupeAddress);
		
		this.rdbtnDedupeName = new JRadioButton("Name");
		this.rdbtnDedupeName.setToolTipText("Matches records on the same Name.");
		this.rdbtnDedupeName.setForeground(Color.BLACK);
		this.rdbtnDedupeName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.rdbtnDedupeName.addActionListener(new DedupeRadioButtonHandler());
		this.contentPanel.add(this.rdbtnDedupeName, "cell 13 4,aligny bottom");
		this.deduplicateRecordByRadioGroup.add(this.rdbtnDedupeName);
		
		lblName = new JLabel("Name1");
		lblName.setForeground(Color.BLACK);
		lblName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblName.setToolTipText("<html>The name field to use when looking for dupes.<br>Should be a full name or company.</html>");
		this.contentPanel.add(lblName, "cell 1 6 2 1,aligny center");

		this.comboBoxName1 = new JComboBox<>(new DefaultComboBoxModel<>(UiController.getUserData().getExportHeaders()));
		this.comboBoxName1.setToolTipText("<html>The name field to use when looking for dupes.<br>Should be a full name or company.</html>");
		comboBoxName1.addKeyListener(new ComboBoxDeleteHandler());
		this.comboBoxName1.setForeground(Color.BLACK);
		this.comboBoxName1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.comboBoxName1.setSelectedIndex(-1);
		this.comboBoxName1.setPrototypeDisplayValue("XXXXXXXXX");
		this.comboBoxName1.addActionListener(new ComboBoxMappingHandler());
		comboBoxName1.addKeyListener(new ComboBoxDeleteHandler());

		this.lblIfName1Blank = new JLabel("Blank Name1 use");
		this.lblIfName1Blank.setToolTipText("<html>If name1 is blank, use this name field when looking for dupes.<br>Should be a full name or company.</html>");
		lblIfName1Blank.setForeground(Color.BLACK);
		this.lblIfName1Blank.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(this.lblIfName1Blank, "cell 4 6 6 1,aligny center");

		this.contentPanel.add(this.comboBoxName1, "cell 1 7 2 1,grow");
		this.comboBoxIfName1Blank = new JComboBox<>(new DefaultComboBoxModel<>(UiController.getUserData().getExportHeaders()));
		this.comboBoxIfName1Blank.setToolTipText("<html>If name1 is blank, use this name field when looking for dupes.<br>Should be a full name or company.</html>");
		this.comboBoxIfName1Blank.setSelectedIndex(-1);
		this.comboBoxIfName1Blank.setForeground(Color.BLACK);
		this.comboBoxIfName1Blank.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.comboBoxIfName1Blank.setPrototypeDisplayValue("XXXXXXXXX");
		comboBoxIfName1Blank.addKeyListener(new ComboBoxDeleteHandler());
		this.contentPanel.add(this.comboBoxIfName1Blank, "cell 4 7 6 1,grow");

		this.lblResults = new JLabel("Results");
		this.lblResults.setForeground(new Color(25, 25, 112));
		this.lblResults.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		this.contentPanel.add(this.lblResults, "cell 13 7,aligny center");

		addWindowListener(new WindowClosingHandler());

		this.panel = new JPanel();
		this.contentPanel.add(this.panel, "cell 13 9 3 6,grow");
		this.panel.setLayout((LayoutManager)new MigLayout("insets 0, gap 0", "[86px:n:86px][14px:n:14px][106px:n:106px][14px:n:14px]", "[36px:n:36px][36px:n:36px][36px:n:36px][36px:n:36px][36px:n:36px]"));
		JLabel lblTotalRecords = new JLabel("Total records");
		this.panel.add(lblTotalRecords, "cell 0 0");
		lblTotalRecords.setForeground(Color.BLACK);
		lblTotalRecords.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblTotalRecordsAmount = new JLabel("0");
		this.panel.add(this.lblTotalRecordsAmount, "cell 1 0");
		this.lblTotalRecordsAmount.setForeground(Color.BLACK);
		this.lblTotalRecordsAmount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblParentDupes = new JLabel("Parent dupes");
		this.lblParentDupes.setToolTipText("The duplicate records to be kept in the data.");
		this.panel.add(this.lblParentDupes, "cell 0 1");
		this.lblParentDupes.setForeground(Color.BLACK);
		this.lblParentDupes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblParentDupesAmount = new JLabel("0");
		this.panel.add(this.lblParentDupesAmount, "cell 1 1");
		this.lblParentDupesAmount.setForeground(Color.BLACK);
		this.lblParentDupesAmount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblChildDupes = new JLabel("Child dupes");
		this.lblChildDupes.setToolTipText("The duplicate records to be removed from the data.");
		
		this.panel.add(this.lblChildDupes, "cell 0 2");
		this.lblChildDupes.setForeground(Color.BLACK);
		this.lblChildDupes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblChildDupesAmount = new JLabel("0");
		this.panel.add(this.lblChildDupesAmount, "cell 1 2");
		this.lblChildDupesAmount.setForeground(Color.BLACK);
		this.lblChildDupesAmount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblTotalDupes = new JLabel("Total dupes");
		this.panel.add(this.lblTotalDupes, "cell 0 3");
		this.lblTotalDupes.setForeground(Color.BLACK);
		this.lblTotalDupes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblTotalDupesAmount = new JLabel("0");
		this.panel.add(this.lblTotalDupesAmount, "cell 1 3");
		this.lblTotalDupesAmount.setForeground(Color.BLACK);
		this.lblTotalDupesAmount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblElapsedTime = new JLabel("Elapsed time");
		this.panel.add(this.lblElapsedTime, "cell 0 4");
		this.lblElapsedTime.setForeground(Color.BLACK);
		this.lblElapsedTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.lblElapsedTimeAmount = new JLabel("0 sec");
		this.panel.add(this.lblElapsedTimeAmount, "cell 1 4");
		this.lblElapsedTimeAmount.setForeground(Color.BLACK);
		this.lblElapsedTimeAmount.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		lblName2 = new JLabel("Name2");
		this.lblName2.setToolTipText("<html>The name field to use when looking for dupes.<br>Should be a full name or company.</html>");
		lblName2.setForeground(Color.BLACK);
		lblName2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(lblName2, "cell 1 9 2 1,aligny center");

		this.lblIfName2Blank = new JLabel("Blank Name2 use");
		this.lblIfName2Blank.setToolTipText("<html>If name2 is blank, use this name field when looking for dupes.<br>Should be a full name or company.</html>");
		lblIfName2Blank.setForeground(Color.BLACK);
		this.lblIfName2Blank.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(this.lblIfName2Blank, "cell 4 9 6 1,aligny center");

		this.comboBoxName2 = new JComboBox<>(new DefaultComboBoxModel<>(UiController.getUserData().getExportHeaders()));
		this.comboBoxName2.setToolTipText("<html>The name field to use when looking for dupes.<br>Should be a full name or company.</html>");
		this.comboBoxName2.setForeground(Color.BLACK);
		this.comboBoxName2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.comboBoxName2.setSelectedIndex(-1);
		this.comboBoxName2.setPrototypeDisplayValue("XXXXXXXXX");
		this.comboBoxName2.addActionListener(new ComboBoxMappingHandler());
		comboBoxName2.addKeyListener(new ComboBoxDeleteHandler());
		this.contentPanel.add(this.comboBoxName2, "cell 1 10 2 1,grow");

		this.comboBoxIfName2Blank = new JComboBox<>(new DefaultComboBoxModel<>(UiController.getUserData().getExportHeaders()));
		this.comboBoxIfName2Blank.setToolTipText("<html>If name2 is blank, use this name field when looking for dupes.<br>Should be a full name or company.</html>");
		this.comboBoxIfName2Blank.setForeground(Color.BLACK);
		this.comboBoxIfName2Blank.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.comboBoxIfName2Blank.setSelectedIndex(-1);
		this.comboBoxIfName2Blank.setPrototypeDisplayValue("XXXXXXXXX");
		comboBoxIfName2Blank.addKeyListener(new ComboBoxDeleteHandler());
		this.contentPanel.add(this.comboBoxIfName2Blank, "cell 4 10 6 1,grow");

		this.separator = new JSeparator();
		this.contentPanel.add(this.separator, "cell 1 12 9 1,growx,aligny center");
		JLabel lblPriority = new JLabel("Priority Settings");
		lblPriority.setForeground(new Color(25, 25, 112));
		lblPriority.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		this.contentPanel.add(lblPriority, "cell 1 13 2 1,aligny center");
		this.chckbxKillFilePriority = new JCheckBox("Match on top priority only (Suppression File)");
		this.chckbxKillFilePriority.setToolTipText("<html>Will only find dupes on records with the top priority.<br>(Ascending/Descending sorting will change results)</html>");
		this.chckbxKillFilePriority.setForeground(Color.BLACK);
		this.chckbxKillFilePriority.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.chckbxKillFilePriority.addActionListener(new DedupeChkbxKillFileHandler());
		this.contentPanel.add(this.chckbxKillFilePriority, "cell 1 14 10 1,aligny center");
		this.separator_4 = new JSeparator();
		this.contentPanel.add(this.separator_4, "cell 13 15 3 1,growx,aligny center");

		this.comboBoxPriority = new JComboBox<>(new DefaultComboBoxModel<>(UiController.getUserData().getExportHeaders()));
		this.comboBoxPriority.setToolTipText("<html>The field to use for priority sorting.</html>");
		this.comboBoxPriority.setForeground(Color.BLACK);
		this.comboBoxPriority.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.comboBoxPriority.setSelectedIndex(-1);
		this.comboBoxPriority.setPrototypeDisplayValue("XXXXXXXXX");
		//this.comboBoxPriority.addActionListener(new ComboBoxMappingHandler());
		this.comboBoxPriority.addActionListener(new ComboBoxPriorityHandler());
		comboBoxPriority.addKeyListener(new ComboBoxDeleteHandler());

		this.lblPriorityValue = new JLabel("Priority value");
		this.lblPriorityValue.setToolTipText("<html>The field to use for priority sorting.</html>");
		lblPriorityValue.setForeground(Color.BLACK);
		this.lblPriorityValue.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(this.lblPriorityValue, "cell 1 15 2 1,aligny center");

		this.lblPrioritySorting = new JLabel("Priority sorting");
		this.lblPrioritySorting.setToolTipText("<html>How the values should be sorted.<br><br>1, 2, 3 (Ascending)<br><br>3, 2, 1 (Descending)</html>");
		lblPrioritySorting.setForeground(Color.BLACK);
		this.lblPrioritySorting.setEnabled(false);
		this.lblPrioritySorting.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		this.contentPanel.add(this.lblPrioritySorting, "cell 4 15 6 1,aligny center");
		this.contentPanel.add(this.comboBoxPriority, "cell 1 16 2 1,grow");

		this.rdbtnAsc = new JRadioButton("asc");
		this.rdbtnAsc.setToolTipText("<html>Ascending priority order. Ex. 1, 2, 3</html>");
		this.rdbtnAsc.setForeground(Color.BLACK);
		this.rdbtnAsc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.rdbtnAsc.setSelected(true);
		this.rdbtnAsc.setEnabled(false);
		this.contentPanel.add(this.rdbtnAsc, "cell 4 16 3 1,aligny center");
		this.priorityRadioGroup.add(this.rdbtnAsc);
		
		this.rdbtnDesc = new JRadioButton("desc");
		this.rdbtnDesc.setToolTipText("<html>Descending priority order. Ex. 3, 2, 1</html>");
		this.rdbtnDesc.setForeground(Color.BLACK);
		this.rdbtnDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.rdbtnDesc.setEnabled(false);
		this.contentPanel.add(this.rdbtnDesc, "cell 7 16 3 1,aligny center");
		this.priorityRadioGroup.add(this.rdbtnDesc);

		this.panel_1 = new JPanel();
		this.contentPanel.add(this.panel_1, "cell 13 16 3 4,grow");
		this.panel_1.setLayout((LayoutManager)new MigLayout("insets 0, gap 0", "[120px:n:120px][14px:n:14px][120px:n:120px]", "[14px:n:14px][28px:n:36px][28px:n:28px][28px:n:28px]"));
		this.btnRun = new JButton("Run");
		this.btnRun.setToolTipText("Begin searching for duplicates.");
		this.panel_1.add(this.btnRun, "cell 0 1,grow");
		this.btnRun.setBackground(new Color(255, 255, 255));
		this.btnRun.setForeground(new Color(0, 0, 0));
		this.btnRun.setFont(new Font("Segoe UI", 0, 14));
		this.btnView = new JButton("View");
		this.btnView.setToolTipText("View/Modify deduplication results.");
		this.panel_1.add(this.btnView, "cell 2 1,grow");
		this.btnView.setBackground(new Color(255, 255, 255));
		this.btnView.setForeground(new Color(0, 0, 0));
		this.btnView.setFont(new Font("Segoe UI", 0, 14));
		this.btnView.setEnabled(false);
		this.btnExport = new JButton("Export");
		this.btnExport.setToolTipText("Export deduplication results.");
		this.panel_1.add(this.btnExport, "cell 0 3,grow");
		this.btnExport.setBackground(new Color(255, 255, 255));
		this.btnExport.setForeground(new Color(0, 0, 0));
		this.btnExport.setFont(new Font("Segoe UI", 0, 14));
		this.btnExport.addActionListener(new ExportButtonHandler());
		this.btnExport.setEnabled(false);
		this.btnDelete = new JButton("Delete");
		this.btnDelete.setToolTipText("Delete child dupes from the data.");
		this.panel_1.add(this.btnDelete, "cell 2 3,grow");
		this.btnDelete.setBackground(new Color(255, 255, 255));
		this.btnDelete.setForeground(new Color(0, 0, 0));
		this.btnDelete.setFont(new Font("Segoe UI", 0, 14));
		this.btnDelete.addActionListener(new DeleteButtonHandler());
		this.btnDelete.setEnabled(false);
		this.btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (e.getSource() == DedupeDialog2.this.btnView) {
						LinkedHashSet<String> selectedFields = new LinkedHashSet<>();
						if (DedupeDialog2.this.comboBoxName1.getSelectedIndex() > -1)
							selectedFields.add(DedupeDialog2.this.comboBoxName1.getSelectedItem().toString()); 
						if (DedupeDialog2.this.comboBoxName2.getSelectedIndex() > -1)
							selectedFields.add(DedupeDialog2.this.comboBoxName2.getSelectedItem().toString()); 
						if (DedupeDialog2.this.comboBoxIfName1Blank.getSelectedIndex() > -1)
							selectedFields.add(DedupeDialog2.this.comboBoxIfName1Blank.getSelectedItem().toString()); 
						if (DedupeDialog2.this.comboBoxIfName2Blank.getSelectedIndex() > -1)
							selectedFields.add(DedupeDialog2.this.comboBoxIfName2Blank.getSelectedItem().toString()); 
						if (DedupeDialog2.this.comboBoxPriority.getSelectedIndex() > -1)
							selectedFields.add(DedupeDialog2.this.comboBoxPriority.getSelectedItem().toString()); 
						DedupeDialog2.this.dedupeViewDialog = new DedupeViewDialog2(DedupeDialog2.this, DedupeDialog2.this.allDupeRecordsList, (String[])selectedFields.toArray((Object[])new String[0]));
						DedupeDialog2.this.dedupeViewDialog.setDefaultCloseOperation(2);
						DedupeDialog2.this.dedupeViewDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
						DedupeDialog2.this.dedupeViewDialog.setVisible(true);
					} 
				} catch (Exception err) {
					UiController.handle(err);
				} 
			}
		});
		this.btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == DedupeDialog2.this.btnRun) {
					if(UiController.getUserData().getRecordList().size() == 0) {
						UiController.displayMessage("Warning", "There are no records remaining!", JOptionPane.WARNING_MESSAGE);
					} else {
						try {
							DedupeDialog2.this.disableUI();
							Runnable runner = new Runnable() {
								
								public void run() {
									
									try {
										
										// reset internal fields
										reset();
										progressBar.setMinimum(0);
										progressBar.setMaximum(UiController.getUserData().getRecordList().size());
										Instant startTime = Instant.now();
										timer = new Timer(0, e -> {
											Instant endTime = Instant.now();
											//try {
											//	Thread.sleep(1000L);
											//} catch (InterruptedException e1) {
											//	e1.printStackTrace();
											//} 
											lblElapsedTimeAmount.setText(String.format("%.2f sec", new Object[] { Double.valueOf(Duration.between(startTime, endTime).toMillis() / 1000.0D) }));
										});
										
										timer.start();
										for (Record record : UiController.getUserData().getRecordList()) {
											record.setIsDupe(false); 
											record.setDupeGroupId(0);
											record.setDupeGroupSize(0);
										}
										
										setupDedupe(UiController.getUserData());
										Pattern pfp1Pattern = Lookup.getPrefixFirstPassPattern();
										
										// Split the data into two parts
						                List<Record> recordList = UiController.getUserData().getRecordList();
						                preprocessAddresses(recordList);
						    			Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByDupePCodeDupeAdd1DupeAdd2());
						    			
						                //int middleIndex = recordList.size() / 2;
						    			int size = recordList.size();
						                int firstIntersection = recordList.size() / 4;
						                int secondIntersection = recordList.size() / 2;
						                int thirdIntersection = 3 * recordList.size() / 4;
						                
						                List<Record> data1;
						                List<Record> data2;
						                List<Record> data3;
						                List<Record> data4;
						                
						                if(!rdbtnDedupeName.isSelected() && size >= 4) {
							                data1 = new ArrayList<>(recordList.subList(0, firstIntersection));
							                data2 = new ArrayList<>(recordList.subList(firstIntersection, secondIntersection));
							                data3 = new ArrayList<>(recordList.subList(secondIntersection, thirdIntersection));
							                data4 = new ArrayList<>(recordList.subList(thirdIntersection, size));
						                } else {
						                	data1 = recordList;
							                data2 = new ArrayList<>();
							                data3 = new ArrayList<>();
							                data4 = new ArrayList<>();
						                }
						                
						                if(data1.size() > 0 && data2.size() > 0) {
						                	String data1PC = data1.get(data1.size() - 1).getDupePCode();
						                	String data2PC = data2.get(0).getDupePCode();
						                	if(data1PC.equalsIgnoreCase(data2PC)) {
						                		for(int i = data1.size() - 1; i >= 0; --i) {
						                			Record d1rec = data1.get(i);
						                			if(d1rec.getDupePCode().equalsIgnoreCase(data2PC))
						                				data2.add(0, data1.remove(i));
						                			else
						                				break;
						                				
						                		}
						                	}
						                }
						                
						                if(data2.size() > 0 && data3.size() > 0) {
						                	String data2PC = data2.get(data2.size() - 1).getDupePCode();
						                	String data3PC = data3.get(0).getDupePCode();
						                	if(data2PC.equalsIgnoreCase(data3PC)) {
						                		for(int i = data2.size() - 1; i >= 0; --i) {
						                			Record d2rec = data2.get(i);
						                			if(d2rec.getDupePCode().equalsIgnoreCase(data3PC))
						                				data3.add(0, data2.remove(i));
						                			else
						                				break;
						                				
						                		}
						                	}
						                }
						                
						                if(data3.size() > 0 && data4.size() > 0) {
						                	String data3PC = data3.get(data3.size() - 1).getDupePCode();
						                	String data4PC = data4.get(0).getDupePCode();
						                	if(data3PC.equalsIgnoreCase(data4PC)) {
						                		for(int i = data3.size() - 1; i >= 0; --i) {
						                			Record d3rec = data3.get(i);
						                			if(d3rec.getDupePCode().equalsIgnoreCase(data4PC))
						                				data4.add(0, data3.remove(i));
						                			else
						                				break;
						                				
						                		}
						                	}
						                }
						               
						             
   
						                // Create Runnable tasks for each part
						                Runnable dedupeTask1 = new Runnable() {
						                    public void run() {
						                    	try {
						                    		dedupe(data1, allDupeRecordsList1, pfp1Pattern);
						                    	} catch (Exception e) {
													UiController.handle(e);
													e.printStackTrace();
												} finally {
	
												}
						                    }
						                };

						                Runnable dedupeTask2 = new Runnable() {
						                    public void run() {
						                    	try {
						                        dedupe(data2, allDupeRecordsList2, pfp1Pattern);
						                    	} catch (Exception e) {
													UiController.handle(e);
													e.printStackTrace();
												} finally {
	
												}
						                    }
						                };
						                
						                Runnable dedupeTask3 = new Runnable() {
						                    public void run() {
						                    	try {
						                        dedupe(data3, allDupeRecordsList3, pfp1Pattern);
						                    	} catch (Exception e) {
													UiController.handle(e);
													e.printStackTrace();
												} finally {
	
												}
						                    }
						                };
						                
						                Runnable dedupeTask4 = new Runnable() {
						                    public void run() {
						                    	try {
						                        dedupe(data4, allDupeRecordsList4, pfp1Pattern);
						                    	} catch (Exception e) {
													UiController.handle(e);
													e.printStackTrace();
												} finally {
	
												}
						                    }
						                };

						                // Start both tasks in separate threads
						                Thread thread1 = new Thread(dedupeTask1, "Dedupe Thread 1");
						                Thread thread2 = new Thread(dedupeTask2, "Dedupe Thread 2");
						                Thread thread3 = new Thread(dedupeTask3, "Dedupe Thread 3");
						                Thread thread4 = new Thread(dedupeTask4, "Dedupe Thread 4");

						                thread1.start();
						                thread2.start();
						                thread3.start();
						                thread4.start();

						                // Wait for both threads to finish
						                thread1.join();
						                thread2.join();
						                thread3.join();
						                thread4.join();
						              						                
						                // safely add the dupes together
						                allDupeRecordsList.addAll(allDupeRecordsList1);
						                allDupeRecordsList.addAll(allDupeRecordsList2);
						                allDupeRecordsList.addAll(allDupeRecordsList3);
						                allDupeRecordsList.addAll(allDupeRecordsList4);
						                
						                // reset the groupIds
						                fixFinalDupeGroupIdAndSize(allDupeRecordsList);
						                
									} catch (Exception e) {
										UiController.handle(e);
										e.printStackTrace();
									} finally {
										progressBar.setIndeterminate(false);
										timer.stop();
										lblStatus.setText("Complete");
										enableUI();
									} 
									
								}
							};
							DedupeDialog2.this.dedupeThread = new Thread(runner, "Code Executer");
							DedupeDialog2.this.dedupeThread.start();
						 
						} catch (Exception err) {
							UiController.handle(err);
						} finally {
							
						}
					}
				}
			}
		});
		
		this.lblPriorityFormat = new JLabel("Priority format");
		this.lblPriorityFormat.setToolTipText("<html>The data format of the chosen priority field.</html>");
		lblPriorityFormat.setForeground(Color.BLACK);
		this.lblPriorityFormat.setEnabled(false);
		this.lblPriorityFormat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(this.lblPriorityFormat, "cell 1 18 2 1,aligny center");

		this.lblPriorityDateFormat = new JLabel("Priority date format");
		this.lblPriorityDateFormat.setToolTipText("<html>Enter the date format of the chosen priority field.</html>");
		lblPriorityDateFormat.setForeground(Color.BLACK);
		this.lblPriorityDateFormat.setEnabled(false);
		this.lblPriorityDateFormat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(this.lblPriorityDateFormat, "cell 4 18 6 1,aligny center");
		this.comboBoxPriorityFormat = new JComboBox<>(
				new DefaultComboBoxModel<>(Arrays.toString((Object[])PRIORITY_FORMATS.values()).replaceAll("^.|.$", "").split(", ")));
		this.comboBoxPriorityFormat.setToolTipText("<html>The data format of the chosen priority field.</html>");
		this.comboBoxPriorityFormat.setEnabled(false);
		this.comboBoxPriorityFormat.setPrototypeDisplayValue("XXXXXXXXX");
		this.comboBoxPriorityFormat.setForeground(Color.BLACK);
		this.comboBoxPriorityFormat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(this.comboBoxPriorityFormat, "cell 1 19 2 1,grow");
		this.comboBoxPriorityFormat.addActionListener(new ComboBoxPriorityFormatHandler());
		this.textFieldPriorityDateFormat = new JTextField();
		this.textFieldPriorityDateFormat.setToolTipText("<html>Enter the custom date format to use for the chosen priority field.</html>");
		textFieldPriorityDateFormat.setForeground(Color.BLACK);
		this.textFieldPriorityDateFormat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.textFieldPriorityDateFormat.setEnabled(false);
		this.contentPanel.add(this.textFieldPriorityDateFormat, "cell 4 19 4 1,grow");
		this.textFieldPriorityDateFormat.setColumns(10);
		this.btnPriorityDateFormatHelp = new JButton("?");
		btnPriorityDateFormatHelp.putClientProperty( "JButton.buttonType", "help" );
		this.btnPriorityDateFormatHelp.setBackground(new Color(255, 255, 255));
		this.btnPriorityDateFormatHelp.setFont(new Font("Segoe UI", 0, 12));
		this.btnPriorityDateFormatHelp.setEnabled(false);
		this.btnPriorityDateFormatHelp.addActionListener(new PriorityDateFormatHelpButtonHandler());
		this.contentPanel.add(this.btnPriorityDateFormatHelp, "cell 8 19 2 1,alignx right,aligny center");
		this.btnDedupeOptionHelp = new JButton("?");
		btnDedupeOptionHelp.putClientProperty( "JButton.buttonType", "help" );
		this.btnDedupeOptionHelp.setBackground(new Color(255, 255, 255));
		this.btnDedupeOptionHelp.setFont(new Font("Segoe UI", 0, 12));
		this.btnDedupeOptionHelp.addActionListener(new DedupeOptionHelpButtonHandler());
		this.contentPanel.add(this.btnDedupeOptionHelp, "cell 15 1,alignx right,aligny center");
		this.separator_1 = new JSeparator();
		this.contentPanel.add(this.separator_1, "cell 1 21 15 1,growx");
		this.lblStatus = new JLabel("");
		this.lblStatus.setForeground(Color.DARK_GRAY);
		this.lblStatus.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		this.contentPanel.add(this.lblStatus, "flowx,cell 1 22 15 1,aligny center");
		this.progressBar = new JProgressBar();
		this.progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		this.contentPanel.add(this.progressBar, "cell 1 23 15 1,grow");
		this.progressBar.setStringPainted(true);
		pack();
		setLocationRelativeTo(frame);
		getContentPane().requestFocusInWindow();
	}

	public boolean addToMappedFields(JComboBox<String> box){
		try {
			this.mappedFields.put(box, box.getSelectedItem().toString());
			return true;
		} catch (Exception err) {
			UiController.handle(err);
			return false;
		} 
	}
	
	public void setupDedupe(UserData userData) throws ApplicationException{
		this.progressBar.setValue(0);
		this.lblTotalRecordsAmount.setText(String.valueOf(userData.getRecordList().size()));
		this.lblTotalDupesAmount.setText("0");
		this.lblParentDupesAmount.setText("0");
		this.lblChildDupesAmount.setText("0");
		
		this.allDupeRecordsList = new ArrayList<>();
		this.allDupeRecordsList1 = new ArrayList<>();
		this.allDupeRecordsList2 = new ArrayList<>();
		this.allDupeRecordsList3 = new ArrayList<>();
		this.allDupeRecordsList4 = new ArrayList<>();
		
		duplicateAddressCounter.set(0);
		preprocessingAddressCounter.set(0);
		parentDupesCounter.set(0);
		childDupesCounter.set(0);
		totalDupesCounter.set(0);
		
		this.isName1Selected = (this.comboBoxName1.getSelectedIndex() > -1);
		this.isName2Selected = (this.comboBoxName2.getSelectedIndex() > -1);
		this.isIfName1BlankSelected = (this.comboBoxIfName1Blank.getSelectedIndex() > -1);
		this.isIfName2BlankSelected = (this.comboBoxIfName2Blank.getSelectedIndex() > -1);
		this.isPrioritySelected = (this.comboBoxPriority.getSelectedIndex() > -1);
		if (this.isName1Selected || this.isName2Selected || this.isPrioritySelected) {
			byte b;
			int i;
			String[] arrayOfString;
			for (i = (arrayOfString = userData.getDfHeaders()).length, b = 0; b < i; ) {
				String header = arrayOfString[b];
				if (this.isName1Selected && this.comboBoxName1.getSelectedItem().toString().equalsIgnoreCase(header))
					this.isDfName1Set = true; 
				if (this.isName2Selected && this.comboBoxName2.getSelectedItem().toString().equalsIgnoreCase(header))
					this.isDfName2Set = true; 
				if (this.isIfName1BlankSelected && this.comboBoxIfName1Blank.getSelectedItem().toString().equalsIgnoreCase(header))
					this.isDfIfName1BlankSelected = true; 
				if (this.isIfName2BlankSelected && this.comboBoxIfName2Blank.getSelectedItem().toString().equalsIgnoreCase(header))
					this.isDfIfName2BlankSelected = true; 
				if (this.isPrioritySelected && this.comboBoxPriority.getSelectedItem().toString().equalsIgnoreCase(header))
					this.isDfPrioritySet = true; 
				b++;
			} 
		}
		
		try {
			sortRecordsForDedupe(userData.getRecordList(), this.isDfPrioritySet);
		} catch(Exception err) {
			throw new ApplicationException(err);
		}
		
		this.killPriority = null;
		if (this.chckbxKillFilePriority.isSelected())
			if (!this.isDfPrioritySet) {
				this.killPriority = ((Record)userData.getRecordList().get(0)).getDfInData()[this.comboBoxPriority.getSelectedIndex()];
			} else {
				this.killPriority = UserData.getRecordFieldByName(this.comboBoxPriority.getName(), (Record) userData.getRecordList().get(0));
			}  
	}
	

	public void dedupe(List<Record> recordListPartition, ArrayList<Record> partitionAllDupeRecordsList, Pattern pfp1Pattern) throws ApplicationException {
		
		try {

			List<Record> sortedArray = recordListPartition;
			int dataSize = UiController.getUserData().getRecordList().size();

			Set<Integer> allDupeIds = new HashSet<>();
			ArrayList<List<Record>> dupeRecordGroups = new ArrayList<>();
			List<Record> currentDupesToAdd = new ArrayList<>();
			boolean isNameSelected = (this.isName1Selected || this.isName2Selected);
			JaroWinklerSimilarity jaroDistance = new JaroWinklerSimilarity();
			double minimumJaroDistance = 0.99D;
			int substringLengthToMatch = 6;
			if (this.rdbtnDedupeAddress.isSelected() || this.rdbtnDedupePC.isSelected()) {
				for (int i = 0; i < sortedArray.size(); i++) {
					this.lblStatus.setText(String.format("Searching for duplicate addresses on record %d / %d", new Object[] { duplicateAddressCounter.incrementAndGet(), dataSize }));
					if (this.isDialogClosed)
						return; 
					boolean dupeWasFound = false;
					while (allDupeIds.contains(Integer.valueOf(((Record)sortedArray.get(i)).getDfId()))) {
						++i;
						this.lblStatus.setText(String.format("Searching for duplicate addresses on record %d / %d", new Object[] { duplicateAddressCounter.incrementAndGet(), dataSize }));
						this.progressBar.setValue(duplicateAddressCounter.get());
						if (i >= sortedArray.size())
							break; 
					} 
					if (i >= sortedArray.size())
						break; 
					Record tempRec = sortedArray.get(i);
					String tempRecAdd1 = tempRec.getDupeAdd1();
					String tempRecAdd2 = tempRec.getDupeAdd2();
					boolean isTempRecAdd1Empty = tempRecAdd1.isEmpty();
					boolean isTempRecAdd2Empty = tempRecAdd2.isEmpty();
					
					if (currentDupesToAdd.size() > 0) {
						dupeRecordGroups.add(currentDupesToAdd);
						if (!isNameSelected && !this.chckbxKillFilePriority.isSelected()) {
							this.lblParentDupesAmount.setText(String.valueOf(parentDupesCounter.incrementAndGet()));
							this.lblChildDupesAmount.setText(String.valueOf(childDupesCounter.addAndGet(currentDupesToAdd.size() - 1)));
							this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.addAndGet(currentDupesToAdd.size())));
						} 
					} 
					currentDupesToAdd = new ArrayList<>();
					boolean isDone = false;
					int j = i;
					while (!isDone && j < sortedArray.size()) {
						for (j = i + 1; j < sortedArray.size(); ) {
							do {

							} while (allDupeIds.contains(Integer.valueOf(((Record)sortedArray.get(j)).getDfId())) && 
									++j < sortedArray.size());
							if (j >= sortedArray.size())
								break; 
							Record nextRec = sortedArray.get(j);
							String nextRecAdd1 = nextRec.getDupeAdd1();
							String nextRecAdd2 = nextRec.getDupeAdd2();
							
							boolean isNextRecAdd1Empty = nextRecAdd1.isEmpty();
							boolean isNextRecAdd2Empty = nextRecAdd2.isEmpty();
							
							//System.out.println();
							//System.out.println(tempRecAdd1);
							//System.out.println(tempRec.getDupeMetaStreetAdd1());
							//System.out.println(tempRecAdd2);
							//System.out.println(tempRec.getDupeMetaStreetAdd2());
							//System.out.println();
							//System.out.println(nextRecAdd1);
							//System.out.println(nextRec.getDupeMetaStreetAdd1());
							//System.out.println(nextRecAdd2);
							//System.out.println(nextRec.getDupeMetaStreetAdd2());

							boolean dupeAddFound = false;
							if (tempRec.getDupePCode().equals(nextRec.getDupePCode())) {
								if (this.rdbtnDedupePC.isSelected()) {
									dupeAddFound = true; 
								}
								if (!this.rdbtnDedupePC.isSelected()) {
									if (!isPoBoxRRSameAsAddress(tempRec, nextRec) && !isPoBoxRRSameAsAddress(nextRec, tempRec))
										if ((!isTempRecAdd1Empty && !isTempRecAdd2Empty) || (!isNextRecAdd1Empty && !isNextRecAdd2Empty)) {
											boolean canContinue = false;
											if (!isTempRecAdd1Empty && tempRecAdd1.equalsIgnoreCase(nextRecAdd1))
												if (jaroDistance.apply(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd1()).doubleValue() > minimumJaroDistance || 
														Common.areSubstringsEqual(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd1(), 
																substringLengthToMatch) || 
														tempRec.getDupeMetaStreetAdd1().isEmpty() || nextRec.getDupeMetaStreetAdd1().isEmpty())
													canContinue = true;
											//System.out.println("1 " + canContinue);
											if (!isTempRecAdd1Empty && tempRecAdd1.equalsIgnoreCase(nextRecAdd2))
												if (jaroDistance.apply(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd2()).doubleValue() > minimumJaroDistance || 
														Common.areSubstringsEqual(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd2(), 
																substringLengthToMatch) || 
														tempRec.getDupeMetaStreetAdd1().isEmpty() || nextRec.getDupeMetaStreetAdd2().isEmpty())
													canContinue = true;
											//System.out.println("2 " + canContinue + "\n" + tempRecAdd1 + "\n" + nextRecAdd2);
											if ((isTempRecAdd1Empty && (isNextRecAdd1Empty || isNextRecAdd2Empty)) || (
													canContinue && (tempRecAdd2.equalsIgnoreCase(nextRecAdd1) || tempRecAdd2.equalsIgnoreCase(nextRecAdd2)))) {
												canContinue = false;
												if (!isNextRecAdd2Empty && tempRecAdd2.equalsIgnoreCase(nextRecAdd1))
													if (jaroDistance.apply(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd1()).doubleValue() > minimumJaroDistance || 
															Common.areSubstringsEqual(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd1(), 
																	substringLengthToMatch) || 
															tempRec.getDupeMetaStreetAdd2().isEmpty() || nextRec.getDupeMetaStreetAdd1().isEmpty())
														canContinue = true;
												//System.out.println("3 " + canContinue);
												if (!isTempRecAdd2Empty && tempRecAdd2.equalsIgnoreCase(nextRecAdd2))
													if (jaroDistance.apply(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd2()).doubleValue() > minimumJaroDistance || 
															Common.areSubstringsEqual(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd2(), 
																	substringLengthToMatch) || 
															tempRec.getDupeMetaStreetAdd2().isEmpty() || nextRec.getDupeMetaStreetAdd2().isEmpty())
														canContinue = true;
												//System.out.println("4 " + canContinue);
											} else if (tempRec.getDupePOBox().isEmpty() && nextRec.getDupePOBox().isEmpty()) {
												canContinue = false;
											} 
											if (canContinue)
												dupeAddFound = true; 

										} else {
											if (!isTempRecAdd1Empty) {
												if (tempRecAdd1.equalsIgnoreCase(nextRecAdd1) && (
														jaroDistance.apply(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd1()).doubleValue() > minimumJaroDistance || 
														jaroDistance.apply(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd2()).doubleValue() > minimumJaroDistance || 
														Common.areSubstringsEqual(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd1(), substringLengthToMatch)))
													dupeAddFound = true; 
												if (tempRecAdd1.equalsIgnoreCase(nextRecAdd2) && (
														jaroDistance.apply(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd2()).doubleValue() > minimumJaroDistance || 
														jaroDistance.apply(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd1()).doubleValue() > minimumJaroDistance || 
														Common.areSubstringsEqual(tempRec.getDupeMetaStreetAdd1(), nextRec.getDupeMetaStreetAdd2(), 
																substringLengthToMatch)))
													dupeAddFound = true; 
											} 
											if (!isTempRecAdd2Empty) {
												if (tempRecAdd2.equalsIgnoreCase(nextRecAdd1) && (
														jaroDistance.apply(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd1()).doubleValue() > minimumJaroDistance || 
														jaroDistance.apply(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd2()).doubleValue() > minimumJaroDistance || 
														Common.areSubstringsEqual(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd1(), 
																substringLengthToMatch)))
													dupeAddFound = true; 
												if (tempRecAdd2.equalsIgnoreCase(nextRecAdd2) && (
														jaroDistance.apply(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd2()).doubleValue() > minimumJaroDistance || 
														jaroDistance.apply(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd1()).doubleValue() > minimumJaroDistance || 
														Common.areSubstringsEqual(tempRec.getDupeMetaStreetAdd2(), nextRec.getDupeMetaStreetAdd2(), 
																substringLengthToMatch)))
													dupeAddFound = true; 
											} 
											if (!dupeAddFound && 
													tempRec.getDupePOBox().isBlank() && tempRec.getDupePOBoxExtra().isBlank() && tempRec.getDupeRR().isBlank() && 
													nextRec.getDupePOBox().isBlank() && nextRec.getDupePOBoxExtra().isBlank() && nextRec.getDupeRR().isBlank() && 
													isTempRecAdd1Empty && isTempRecAdd2Empty && isNextRecAdd1Empty && isNextRecAdd2Empty)
												if (!tempRec.getAdd1().isBlank() && (jaroDistance.apply(Common.getNysiis(tempRec.getDupeMetaStreetAdd1()), 
														Common.getNysiis(nextRec.getDupeMetaStreetAdd1())).doubleValue() > 0.6D || 
														jaroDistance.apply(Common.getNysiis(tempRec.getDupeMetaStreetAdd1()), 
																Common.getNysiis(nextRec.getDupeMetaStreetAdd2())).doubleValue() > 0.6D)) {
													dupeAddFound = true;
												} else if (!tempRec.getAdd2().isBlank() && (jaroDistance.apply(Common.getNysiis(tempRec.getDupeMetaStreetAdd2()), 
														Common.getNysiis(nextRec.getDupeMetaStreetAdd1())).doubleValue() > 0.6D || 
														jaroDistance.apply(Common.getNysiis(tempRec.getDupeMetaStreetAdd2()), 
																Common.getNysiis(nextRec.getDupeMetaStreetAdd2())).doubleValue() > 0.6D)) {
													dupeAddFound = true;
												}
										}
									//handle rrs
									if((!dupeAddFound && 
											tempRec.getDupePOBox().isBlank() && tempRec.getDupePOBoxExtra().isBlank() && !tempRec.getDupeRR().isBlank() && 
											nextRec.getDupePOBox().isBlank() && nextRec.getDupePOBoxExtra().isBlank() && !nextRec.getDupeRR().isBlank() && 
											isTempRecAdd1Empty && isTempRecAdd2Empty && isNextRecAdd1Empty && isNextRecAdd2Empty)) {
										if(tempRec.getDupeRR().equalsIgnoreCase(nextRec.getDupeRR()))
										dupeAddFound = true;
									}
									if (tempRec.getAdd1().trim().equalsIgnoreCase(nextRec.getAdd1().trim()) && 
											tempRec.getAdd2().trim().equalsIgnoreCase(nextRec.getAdd2().trim()))
										dupeAddFound = true; 
									if (tempRec.getAdd1().trim().equalsIgnoreCase(nextRec.getAdd2().trim()) && 
											tempRec.getAdd2().trim().equalsIgnoreCase(nextRec.getAdd1().trim()))
										dupeAddFound = true; 
									if (!tempRec.getDupePOBox().isEmpty() && !nextRec.getDupePOBox().isEmpty())
										if (tempRec.getDupePOBox().equalsIgnoreCase(nextRec.getDupePOBox())) {
											dupeAddFound = false;
											if (!isTempRecAdd1Empty && (
													tempRec.getDupeAdd1().equalsIgnoreCase(nextRec.getDupeAdd1()) || tempRec.getDupeAdd1().equalsIgnoreCase(nextRec.getDupeAdd2())))
												dupeAddFound = true; 
											if (!isTempRecAdd2Empty && (
													tempRec.getDupeAdd2().equalsIgnoreCase(nextRec.getDupeAdd1()) || tempRec.getDupeAdd2().equalsIgnoreCase(nextRec.getDupeAdd2())))
												dupeAddFound = true; 
											if (isTempRecAdd1Empty && isTempRecAdd2Empty)
												dupeAddFound = true; 
											if (isNextRecAdd1Empty && isNextRecAdd2Empty)
												dupeAddFound = true; 
										} else {
											dupeAddFound = false;
										}  
									if (!tempRec.getDupeRR().isEmpty() && !nextRec.getDupeRR().isEmpty() && 
											!tempRec.getDupeRR().equalsIgnoreCase(nextRec.getDupeRR()))
										dupeAddFound = false; 
									if (!tempRec.getDupeRR().isEmpty() && !nextRec.getDupeRR().isEmpty() && 
											tempRec.getDupeRR().equalsIgnoreCase(nextRec.getDupeRR()) && 
											!tempRec.getDupePOBox().equals(nextRec.getDupePOBox()))
										dupeAddFound = false; 
									if (!tempRec.getDupePOBoxExtra().isEmpty() && tempRec.getDupePOBoxExtra().equalsIgnoreCase(nextRec.getDupePOBoxExtra()) && 
											!tempRec.getDupePOBox().equals(nextRec.getDupePOBox()))
										dupeAddFound = false; 
									if (!tempRec.getDupePOBoxExtra().isEmpty() && !nextRec.getDupePOBoxExtra().isEmpty() && 
											!tempRec.getDupePOBoxExtra().equalsIgnoreCase(nextRec.getDupePOBoxExtra()))
										dupeAddFound = false; 
									// End of PC+Address dupe check
									
								} 
							} 
							if (dupeAddFound) {
								dupeWasFound = true;
								if (allDupeIds.contains(Integer.valueOf(((Record)sortedArray.get(i)).getDfId()))) {
									for (int l = dupeRecordGroups.size() - 1; l >= 0; l--) {
										List<Record> recordList = dupeRecordGroups.get(l);
										for (int k = recordList.size() - 1; k >= 0; k--) {
											if (((Record)recordList.get(k)).getDfId() == ((Record)sortedArray.get(i)).getDfId() && 
													allDupeIds.add(Integer.valueOf(((Record)sortedArray.get(j)).getDfId()))) {
												recordList.add(sortedArray.get(j));
												if (!isNameSelected && !this.chckbxKillFilePriority.isSelected()) {
													this.lblChildDupesAmount.setText(String.valueOf(childDupesCounter.incrementAndGet()));
													this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.incrementAndGet()));
												} 
											} 
										} 
									} 
								} else if (allDupeIds.add(Integer.valueOf(((Record)sortedArray.get(j)).getDfId()))) {
									currentDupesToAdd.add(sortedArray.get(j));
								} else {
									for (int l = dupeRecordGroups.size() - 1; l >= 0; l--) {
										List<Record> recordList = dupeRecordGroups.get(l);
										for (int k = recordList.size() - 1; k >= 0; k--) {
											if (((Record)recordList.get(k)).getDfId() == ((Record)sortedArray.get(j)).getDfId() && 
													allDupeIds.add(Integer.valueOf(((Record)sortedArray.get(i)).getDfId()))) {
												recordList.add(sortedArray.get(i));
												if (!isNameSelected && !this.chckbxKillFilePriority.isSelected()) {
													this.lblChildDupesAmount.setText(String.valueOf(childDupesCounter.incrementAndGet()));
													this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.incrementAndGet()));
												} 
												isDone = true;
											} 
										} 
									} 
								} 
							} else {
								isDone = true;
							} 
							j++;
						} 
					} 
					if (dupeWasFound && 
							allDupeIds.add(Integer.valueOf(((Record)sortedArray.get(i)).getDfId())))
						currentDupesToAdd.add(0, sortedArray.get(i)); 
					this.progressBar.setValue(this.progressBar.getValue() + 1);
				} 
				if (currentDupesToAdd.size() > 0) {
					dupeRecordGroups.add(currentDupesToAdd);
					if (!isNameSelected && !this.chckbxKillFilePriority.isSelected()) {
						this.lblParentDupesAmount.setText(String.valueOf(parentDupesCounter.incrementAndGet()));
						this.lblChildDupesAmount.setText(String.valueOf(childDupesCounter.addAndGet(currentDupesToAdd.size() - 1)));
						this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.addAndGet(currentDupesToAdd.size())));
					} 
				} 
			} 
			if (this.rdbtnDedupeName.isSelected())
				dupeRecordGroups.add(sortedArray); 
			if (!isNameSelected)
				for (List<Record> recordList : dupeRecordGroups) {
					sortRecordsForDedupe(recordList, this.isDfPrioritySet);
					if (!this.chckbxKillFilePriority.isSelected())
						for (int i = 0; i < recordList.size(); i++) {
							if (i == 0) {
								((Record)recordList.get(i)).setIsDupe(false);
							} else {
								((Record)recordList.get(i)).setIsDupe(true);
							} 
							partitionAllDupeRecordsList.add(recordList.get(i));
						}  
					if (this.chckbxKillFilePriority.isSelected()) {
						ArrayList<Record> tempKillDupeArray = new ArrayList<>();
						int i;
						for (i = 0; i < recordList.size(); i++) {
							String tempPriority;
							Record record = recordList.get(i);
							if (this.isDfPrioritySet) {
								tempPriority = UserData.getRecordFieldByName(this.comboBoxPriority.getName(), record);
								//tempPriority = record.getPriority();
							} else {
								tempPriority = record.getDfInData()[this.comboBoxPriority.getSelectedIndex()];
							} 
							if (i == 0) {
								if (this.killPriority != null && tempPriority.equalsIgnoreCase(this.killPriority)) {
									tempKillDupeArray.add(record);
								} else {
									break;
								} 
							} else if (this.killPriority != null && !tempPriority.equalsIgnoreCase(this.killPriority)) {
								tempKillDupeArray.add(record);
							} 
						} 
						if (tempKillDupeArray.size() > 1) {
							for (i = 0; i < tempKillDupeArray.size(); i++) {
								if (i == 0) {
									this.lblParentDupesAmount.setText(String.valueOf(parentDupesCounter.incrementAndGet()));
									((Record)tempKillDupeArray.get(i)).setIsDupe(false);
								} else {
									((Record)tempKillDupeArray.get(i)).setIsDupe(true);
								} 
								partitionAllDupeRecordsList.add(tempKillDupeArray.get(i));
							} 
							this.lblChildDupesAmount.setText(String.valueOf(childDupesCounter.addAndGet(tempKillDupeArray.size() - 1)));
							this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.addAndGet(tempKillDupeArray.size())));
						} 
					} 
				}  
			if (isNameSelected) {
				preprocessNames(dupeRecordGroups, pfp1Pattern);
				for (List<Record> list : dupeRecordGroups)
					list.sort((Comparator<? super Record>)new RecordSorters.CompareByDupeName1Name2LengthDesc()); 
				if (this.chckbxKillFilePriority.isSelected())
					for (List<Record> list : dupeRecordGroups)
						sortRecordsForDedupe(list, this.isDfPrioritySet);  
				findNameDupes(dupeRecordGroups, partitionAllDupeRecordsList);
			} 

			// The problem is that allDupeRecordsList is used in the tableViewModel, not dupeRecordGroups
			addExtraDupeInfo(dupeRecordGroups); // Adds dupeGroupId, dupeGroupSize, and dupeInfo
			addExtraDupeInfoStepTwo(partitionAllDupeRecordsList); // Fixes wrong group sizes and random groupIds
			
			// Remove dupe info from records that may not have met the name criteria
			Set<Integer> idHashSet = new HashSet<>();
			
			for(Record dupeRecord : partitionAllDupeRecordsList)
				idHashSet.add(dupeRecord.getDfId());
			
			for(Record record: recordListPartition)
				if(!idHashSet.contains(record.getDfId())) {
					record.setDupeGroupId(0);
	    			record.setDupeGroupSize(0);
	    			record.setIsDupe(false);
				}
			

		} catch (Exception err) {
			err.printStackTrace();
			throw new ApplicationException(err);
		} finally {

		} 
	}

	private void preprocessAddresses(List<Record> recordList) throws ApplicationException {
		int dataSize = UiController.getUserData().getRecordList().size();
		if (this.rdbtnDedupeName.isSelected())
			for (int i = 0; i < recordList.size(); i++) {
				Record record = recordList.get(i);
				record.setDupeAdd1("");
				record.setDupeAdd2("");
				record.setDupeMetaStreetAdd1("");
				record.setDupeMetaStreetAdd2("");
				record.setDupePCode("");
				record.setDupePOBox("");
				record.setDupePOBoxExtra("");
				record.setDupeRR("");
				record.setDupeStreetDirection("");
			}  
		if (this.rdbtnDedupeAddress.isSelected() || this.rdbtnDedupePC.isSelected()) {
			this.progressBar.setValue(0);
			
			Hashtable<String, String> streetSuffixes = Lookup.getStreetSuffixes();
			HashSet<String> primaryStreetNames = Lookup.getStreetNames();
			Hashtable<String, String> streetDirectionSuffixes = Lookup.getStreetDirectionSuffixes();
			Hashtable<String, String> unitSuffixes = Lookup.getUnitSuffixes();
			
			RuleBasedNumberFormat ruleBasedNumberFormat = new RuleBasedNumberFormat(new Locale("EN", "US"), 1);

			for (int i = 0; i < recordList.size(); i++) {
				this.progressBar.setValue(preprocessingAddressCounter.incrementAndGet());
				this.lblStatus.setText(String.format("Pre-processing address for record %d / %d", new Object[] { preprocessingAddressCounter.get(), dataSize }));
				Record record = recordList.get(i);

				String preAdd1 = StringUtils.stripAccents(record.getAdd1()).replaceAll("\\r"," ").replaceAll("\\n"," ").replaceAll("\\.","").replaceAll("(?<=\\d)(?=[A-Za-z])", " ").replaceAll("(?<=[A-Za-z])(?=\\d)", " ").replaceAll("\\b[A-Za-z]\\b", "").replaceAll("(?i)(?<=\\d)\\s*ieme\\b|^\\s*ieme\\b|\\bieme\\s*$", "").replaceAll("\\s+", " ").trim();
				String preAdd2 = StringUtils.stripAccents(record.getAdd2()).replaceAll("\\r"," ").replaceAll("\\n"," ").replaceAll("\\.","").replaceAll("(?<=\\d)(?=[A-Za-z])", " ").replaceAll("(?<=[A-Za-z])(?=\\d)", " ").replaceAll("\\b[A-Za-z]\\b", "").replaceAll("(?i)(?<=\\d)\\s*ieme\\b|^\\s*ieme\\b|\\bieme\\s*$", "").replaceAll("\\s+", " ").trim();
				
				//Fix missing apt hyphens
				Matcher missingHyphenMatcher1 = APT_MISSING_HYPHEN_PATTERN.matcher(preAdd1);
				Matcher missingHyphenMatcher2 = APT_MISSING_HYPHEN_PATTERN.matcher(preAdd2);

				if(missingHyphenMatcher1.find())
					preAdd1 = preAdd1.replaceFirst(missingHyphenMatcher1.group(), "-");

				if(missingHyphenMatcher2.find())
					preAdd2 = preAdd2.replaceFirst(missingHyphenMatcher2.group(), "-");
				
				//Fix incorrectly added hyphens
				Matcher incorrectHyphenMatcher1 = APT_WRONG_HYPHEN_PATTERN.matcher(preAdd1);
				Matcher incorrectHyphenMatcher2 = APT_WRONG_HYPHEN_PATTERN.matcher(preAdd2);
				
				if(incorrectHyphenMatcher1.find())
					preAdd1 = preAdd1.replaceFirst(incorrectHyphenMatcher1.group(), " ");

				if(incorrectHyphenMatcher2.find())
					preAdd2 = preAdd2.replaceFirst(incorrectHyphenMatcher2.group(), " ");
				
				System.out.println(preAdd1);

				String DupeAlphaStreetAdd1 = preAdd1.toLowerCase().replaceAll("\\p{Pd}", "-").replaceAll("(?i)\\d+(\\D+)?(\\s+)?(floor|etage)", "").replaceAll("number|(?<=^|\\s)num(?=\\d|$|\\s)", "no").replaceAll("\\s+", " ").trim();
				String DupeAlphaStreetAdd2 = preAdd2.toLowerCase().replaceAll("\\p{Pd}", "-").replaceAll("(?i)\\d+(\\D+)?(\\s+)?(floor|etage)", "").replaceAll("number|(?<=^|\\s)num(?=\\d|$|\\s)", "no").replaceAll("\\s+", " ").trim();
				boolean isAddressSame = false;

				if (DupeAlphaStreetAdd2.equalsIgnoreCase(DupeAlphaStreetAdd1)) {
					DupeAlphaStreetAdd2 = "";
					isAddressSame = true;
				} 

				Matcher matcherAdd1FixHwy = REPLACE_HWY_NUM_PATTERN.matcher(DupeAlphaStreetAdd1);
				Matcher matcherAdd2FixHwy = REPLACE_HWY_NUM_PATTERN.matcher(DupeAlphaStreetAdd2);

				if (matcherAdd1FixHwy.find())
					DupeAlphaStreetAdd1 = DupeAlphaStreetAdd1.replaceAll(matcherAdd1FixHwy.group(), String.valueOf(matcherAdd1FixHwy.group().replaceAll("[^\\d]", "")) + " highway"); 
				if (matcherAdd2FixHwy.find())
					DupeAlphaStreetAdd2 = DupeAlphaStreetAdd2.replaceAll(matcherAdd2FixHwy.group(), String.valueOf(matcherAdd2FixHwy.group().replaceAll("[^\\d]", "")) + " highway"); 

				DupeAlphaStreetAdd1 = DupeAlphaStreetAdd1.replaceAll("(?<=\\s|^)gd(?=\\s|$)|(gen|gn)(\\D+)?(?=\\s|$)(\\s)?((del|dl)(\\D+)?(?=\\s|$))", "general delivery").replaceAll("(\\s+)?-(\\s+)?", "-").replaceAll("(?i)\\d+(\\D+)?(\\s+)?(floor|etage)", "").replaceAll("(?<=\\s|^)sh(?=\\s|$)", "state highway").replaceAll("(?<=\\s|^)number(\\s|$)", "").replaceAll("casa(?=\\s)", "no").replaceAll("campo(?=\\s)", "").replaceAll(FIX_APT_REGEX, "apt").replaceAll("(?<!(\\d+\\s+(?=(no|#))|apt|pmb|box|rmb|lb|(\\s+|^)?p(\\s+)?o(\\s+)?\\d|comp|cp|(group|grp|coup|gr|gp)\\s?(\\D$|\\D\\s)?\\s?\\d+|site\\s?\\d+|(^|\\s)s\\d+).*)((#|no)(?=(\\s+)?(\\D)?\\d+(\\D?)$))", "apt").replaceAll("\\(|\\)|_|\\+|\\*|#|,|\\.", " ").replaceAll("  ", " ").replaceAll(FIX_APT_REGEX, "apt").trim();
				DupeAlphaStreetAdd2 = DupeAlphaStreetAdd2.replaceAll("(?<=\\s|^)gd(?=\\s|$)|(gen|gn)(\\D+)?(?=\\s|$)(\\s)?((del|dl)(\\D+)?(?=\\s|$))", "general delivery").replaceAll("(\\s+)?-(\\s+)?", "-").replaceAll("(?i)\\d+(\\D+)?(\\s+)?(floor|etage)", "").replaceAll("(?<=\\s|^)sh(?=\\s|$)", "state highway").replaceAll("(?<=\\s|^)number(\\s|$)", "").replaceAll("casa(?=\\s)", "no").replaceAll("campo(?=\\s)", "").replaceAll(FIX_APT_REGEX, "apt").replaceAll("(?<!(\\d+\\s+(?=(no|#))|apt|pmb|box|rmb|lb|(\\s+|^)?p(\\s+)?o(\\s+)?\\d|comp|cp|(group|grp|coup|gr|gp)\\s?(\\D$|\\D\\s)?\\s?\\d+|site\\s?\\d+|(^|\\s)s\\d+).*)((#|no)(?=(\\s+)?(\\D)?\\d+(\\D?)$))", "apt").replaceAll("\\(|\\)|_|\\+|\\*|#|,|\\.", " ").replaceAll("  ", " ").replaceAll(FIX_APT_REGEX, "apt").trim();

				Matcher matcherAdd1FixCountyRoad = REPLACE_COUNTY_ROAD_NUM_PATTERN.matcher(DupeAlphaStreetAdd1);
				Matcher matcherAdd2FixCountyRoad = REPLACE_COUNTY_ROAD_NUM_PATTERN.matcher(DupeAlphaStreetAdd2);

				if (matcherAdd1FixCountyRoad.find())
					DupeAlphaStreetAdd1 = DupeAlphaStreetAdd1.replaceAll(matcherAdd1FixCountyRoad.group(), String.valueOf(matcherAdd1FixCountyRoad.group().replaceAll("[^\\d]", "")) + " county road"); 
				if (matcherAdd2FixCountyRoad.find())
					DupeAlphaStreetAdd2 = DupeAlphaStreetAdd2.replaceAll(matcherAdd2FixCountyRoad.group(), String.valueOf(matcherAdd2FixCountyRoad.group().replaceAll("[^\\d]", "")) + " county road"); 
				
				Matcher add1StreetApt = ADD_APT_PATTERN.matcher(DupeAlphaStreetAdd1);
				Matcher add2StreetApt = ADD_APT_PATTERN.matcher(DupeAlphaStreetAdd2);
				
				//System.out.println(DupeAlphaStreetAdd1);
				//System.out.println(DupeAlphaStreetAdd2);

				// This removes the unit number as long as it does not start with the unit number
				if (add1StreetApt.find()) {
					record.setMisc1(add1StreetApt.group());
					if(!DupeAlphaStreetAdd1.startsWith(add1StreetApt.group()) || DupeAlphaStreetAdd1.equalsIgnoreCase(add1StreetApt.group()))
						DupeAlphaStreetAdd1 = DupeAlphaStreetAdd1.replaceAll(add1StreetApt.group(), "");
				}
						

				if (add2StreetApt.find()) {
					record.setMisc2(add2StreetApt.group());
					if(!DupeAlphaStreetAdd2.startsWith(add2StreetApt.group()) || DupeAlphaStreetAdd2.equalsIgnoreCase(add2StreetApt.group()))
						DupeAlphaStreetAdd2 = DupeAlphaStreetAdd2.replaceAll(add2StreetApt.group(), "");
				}
						
				
				//System.out.println(DupeAlphaStreetAdd1);
				//System.out.println(DupeAlphaStreetAdd2);

				Matcher matcherAdd1POBox = ADD_PO_BOX_PATTERN.matcher(DupeAlphaStreetAdd1);
				Matcher matcherAdd2POBox = ADD_PO_BOX_PATTERN.matcher(DupeAlphaStreetAdd2);
				Matcher matcherAdd1POBoxExtra = ADD_PO_BOX_EXTRA_PATTERN.matcher(DupeAlphaStreetAdd1);
				Matcher matcherAdd2POBoxExtra = ADD_PO_BOX_EXTRA_PATTERN.matcher(DupeAlphaStreetAdd2);
				Matcher matcherAdd1RR = ADD_RR_PATTERN.matcher(DupeAlphaStreetAdd1);
				Matcher matcherAdd2RR = ADD_RR_PATTERN.matcher(DupeAlphaStreetAdd2);

				if (matcherAdd1POBox.find()) {
					record.setDupePOBox(matcherAdd1POBox.group().replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
					DupeAlphaStreetAdd1 = DupeAlphaStreetAdd1.replaceAll(matcherAdd1POBox.group(), "");
				} else if (matcherAdd2POBox.find()) {
					record.setDupePOBox(matcherAdd2POBox.group().replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
					DupeAlphaStreetAdd2 = DupeAlphaStreetAdd2.replaceAll(matcherAdd2POBox.group(), "");
				} 

				if (record.getDupePOBox() == null)
					record.setDupePOBox(""); 
				if (record.getDupePOBox().isEmpty())
					record.setDupePOBox(""); 

				if (matcherAdd1POBoxExtra.find()) {
					record.setDupePOBoxExtra(matcherAdd1POBoxExtra.group().replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
					DupeAlphaStreetAdd1 = DupeAlphaStreetAdd1.replaceAll(matcherAdd1POBoxExtra.group(), "");
				} else if (matcherAdd2POBoxExtra.find()) {
					record.setDupePOBoxExtra(matcherAdd2POBoxExtra.group().replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
					DupeAlphaStreetAdd2 = DupeAlphaStreetAdd2.replaceAll(matcherAdd2POBoxExtra.group(), "");
				} else {
					record.setDupePOBoxExtra("");

				} 
				if (record.getDupePOBoxExtra().replaceAll("[^0-9]", "").isEmpty())
					record.setDupePOBoxExtra(""); 
				if (matcherAdd1RR.find()) {
					record.setDupeRR(matcherAdd1RR.group().replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
					DupeAlphaStreetAdd1 = DupeAlphaStreetAdd1.replaceAll(matcherAdd1RR.group(), "");
				} else if (matcherAdd2RR.find()) {
					record.setDupeRR(matcherAdd2RR.group().replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
					DupeAlphaStreetAdd2 = DupeAlphaStreetAdd2.replaceAll(matcherAdd2RR.group(), "");

				} 
				if (record.getDupeRR() == null)
					record.setDupeRR(""); 

				String dupeAdd1Normalized = DupeAlphaStreetAdd1;
				String dupeAdd2Normalized = DupeAlphaStreetAdd2;

				String[] dupeAlphaArr1 = DupeAlphaStreetAdd1.replaceAll("(buzzer(\\s)?(number|num)?|buzz(\\s)?(code)?)(\\s#|\\s|#)?\\d+", "").replaceAll("(?i)\\d+(\\D+)?(\\s+)?(floor|etage)", "").replaceAll("[a-zA-Z]+(?=\\d+)", "").replaceAll("(?<=[0-9])[a-zA-Z]+", "").replaceFirst("^\\D+(?=\\d+)", "").replaceAll("^\\d+(-\\d+)?", "").split("\\s+");
				String newDupeAplha1 = "";

				byte b1;
				int k;
				String[] arrayOfString1;
				for (k = (arrayOfString1 = dupeAlphaArr1).length, b1 = 0; b1 < k; ) {
					String str = arrayOfString1[b1];
					if (streetSuffixes.keySet().contains(str))
						str = streetSuffixes.get(str); 
					if (streetDirectionSuffixes.keySet().contains(str))
						str = streetDirectionSuffixes.get(str); 
					if (unitSuffixes.keySet().contains(str))
						str = unitSuffixes.get(str); 
					if (Validators.isWholeNumber(str))
						str = ruleBasedNumberFormat.format(Integer.parseInt(str)).toLowerCase(); 
					if (str.length() > 2)
						newDupeAplha1 = String.valueOf(newDupeAplha1) + str + " "; 
					b1++;
				} 

				newDupeAplha1 = newDupeAplha1.replaceAll("first", "one")
						.replaceAll("second", "two")
						.replaceAll("third", "three")
						.replaceAll("fifth", "five")
						.replaceAll("eigth", "eight")
						.replaceAll("ninth", "nine")
						.replaceAll("twelfth", "twelve")
						.replaceAll("th(?=\\s|$)", "")
						.replaceAll("hundred", "hun");
				String[] dupeAlphaArr2 = DupeAlphaStreetAdd2.replaceAll("(buzzer(\\s)?(number|num)?|buzz(\\s)?(code)?)(\\s#|\\s|#)?\\d+", "").replaceAll("(?i)\\d+(\\D+)?(\\s+)?(floor|etage)", "").replaceAll("[a-zA-Z]+(?=\\d+)", "").replaceAll("(?<=[0-9])[a-zA-Z]+", "").replaceFirst("^\\D+(?=\\d+)", "").replaceAll("^\\d+(-\\d+)?", "").split(" ");

				String newDupeAplha2 = "";
				byte b2;
				int m;
				String[] arrayOfString2;
				for (m = (arrayOfString2 = dupeAlphaArr2).length, b2 = 0; b2 < m; ) {
					String str = arrayOfString2[b2];
					if (streetSuffixes.keySet().contains(str))
						str = streetSuffixes.get(str); 
					if (streetDirectionSuffixes.keySet().contains(str))
						str = streetDirectionSuffixes.get(str); 
					if (unitSuffixes.keySet().contains(str))
						str = unitSuffixes.get(str); 
					if (Validators.isWholeNumber(str))
						str = ruleBasedNumberFormat.format(Integer.parseInt(str)).toLowerCase(); 
					if (str.length() > 2)
						newDupeAplha2 = String.valueOf(newDupeAplha2) + str + " "; 
					b2++;

				} 
				newDupeAplha2 = newDupeAplha2.replaceAll("first", "one")
						.replaceAll("second", "two")
						.replaceAll("third", "three")
						.replaceAll("fifth", "five")
						.replaceAll("eigth", "eight")
						.replaceAll("ninth", "nine")
						.replaceAll("twelfth", "twelve")
						.replaceAll("th(?=\\s|$)", "")
						.replaceAll("hundred", "hun");

				newDupeAplha1 = newDupeAplha1
						.replaceAll("\\d+(\\s#|\\s|#)?apt|apt(\\s#|\\s|#\\s|#)?\\d+", "")
						.replaceAll("(?<=\\s|^)sh(?=\\s|$)", "state highway")
						.replaceAll("rue(s)?(?=\\s|$)", "")
						.replaceAll("(^|\\s)des\\s", " ")
						.replaceAll("[^a-zA-Z\\s]", "");

				newDupeAplha2 = newDupeAplha2
						.replaceAll("\\d+(\\s#|\\s|#)?apt|apt(\\s#|\\s|#\\s|#)?\\d+", "")
						.replaceAll("(?<=\\s|^)sh(?=\\s|$)", "state highway")
						.replaceAll("rue(s)?(?=\\s|$)", "")
						.replaceAll("(^|\\s)des\\s", " ")
						.replaceAll("[^a-zA-Z\\s]", "");

				dupeAlphaArr1 = newDupeAplha1.replaceAll("\\s+", " ").trim().split("\\s+");
				dupeAlphaArr2 = newDupeAplha2.replaceAll("\\s+", " ").trim().split("\\s+");

				int j;
				for (j = 0; j < dupeAlphaArr1.length; j++) {
					String str = dupeAlphaArr1[j];
					if (primaryStreetNames.contains(str) && 
							j > 0) {
						newDupeAplha1 = dupeAlphaArr1[j - 1];
						break;
					} 
				} 

				dupeAlphaArr1 = newDupeAplha1.replaceAll("\\s+", " ").trim().split("\\s+");
				if (dupeAlphaArr1.length > 1)
					for (j = 0; j < dupeAlphaArr1.length; j++) {
						String str = dupeAlphaArr1[j];
						if (primaryStreetNames.contains(str))
							newDupeAplha1 = newDupeAplha1.replaceAll(str, ""); 
					}  

				for (j = 0; j < dupeAlphaArr2.length; j++) {
					String str = dupeAlphaArr2[j];
					if (primaryStreetNames.contains(str) && 
							j > 0) {
						newDupeAplha2 = dupeAlphaArr2[j - 1];
						break;
					} 
				} 

				dupeAlphaArr2 = newDupeAplha2.replaceAll("\\s+", " ").trim().split("\\s+");

				if (dupeAlphaArr2.length > 1)
					for (j = 0; j < dupeAlphaArr2.length; j++) {
						String str = dupeAlphaArr2[j];
						if (primaryStreetNames.contains(str))
							newDupeAplha2 = newDupeAplha2.replaceAll(str, ""); 

					}
				
				record.setDupeMetaStreetAdd1(newDupeAplha1.replaceAll("s(?=\\s|$)", "").replaceAll("[^a-zA-Z0-9]", "").replaceAll("[aeiuy]", "").replaceAll("rs", "r").replaceAll("sc", "s").replaceAll("(.)\\1+", "$1"));
				record.setDupeMetaStreetAdd2(newDupeAplha2.replaceAll("s(?=\\s|$)", "").replaceAll("[^a-zA-Z0-9]", "").replaceAll("[aeiuy]", "").replaceAll("rs", "r").replaceAll("sc", "s").replaceAll("(.)\\1+", "$1"));
				
				Matcher matcherAdd1 = ADD_NUM_PATTERN.matcher(dupeAdd1Normalized.replaceAll("[^a-zA-Z0-9\\s-]", "").replaceAll("(buzzer(\\s)?(number|num)?|buzz(\\s)?(code)?)(\\s#|\\s|#)?\\d+", "").replaceAll("(?i)\\d+(\\D+)?(\\s+)?(floor|etage)", "").replaceFirst("^\\D+(?=\\d+)", ""));
				Matcher matcherAdd2 = ADD_NUM_PATTERN.matcher(dupeAdd2Normalized.replaceAll("[^a-zA-Z0-9\\s-]", "").replaceAll("(buzzer(\\s)?(number|num)?|buzz(\\s)?(code)?)(\\s#|\\s|#)?\\d+", "").replaceAll("(?i)\\d+(\\D+)?(\\s+)?(floor|etage)", "").replaceFirst("^\\D+(?=\\d+)", ""));

				String dupeAdd1 = "";

				if (matcherAdd1.find())
					dupeAdd1 = matcherAdd1.group(); 

				String dupeAdd2 = "";

				if (matcherAdd2.find())
					dupeAdd2 = matcherAdd2.group(); 
				
				if (this.chckbxIgnoreApt.isSelected()) {
					dupeAdd1 = dupeAdd1.replaceAll("^(\\D)?(\\s+)?(\\D)?(\\s+)?\\d+(\\D)?(\\s+)?(\\D)?(\\s+)?-", "");
					dupeAdd2 = dupeAdd2.replaceAll("^(\\D)?(\\s+)?(\\D)?(\\s+)?\\d+(\\D)?(\\s+)?(\\D)?(\\s+)?-", "");
				}
				
				// Extra Apt info for analytics
				/////////////////////////////////////////////////////////////
				Matcher tableauApt1Matcher = REMOVE_APT_PATTERN.matcher(dupeAdd1);
				Matcher tableauApt2Matcher = REMOVE_APT_PATTERN.matcher(dupeAdd2);
				
				if(tableauApt1Matcher.find())
					if(record.getMisc1().length() == 0)
						record.setMisc1(tableauApt1Matcher.group());
				
				if(tableauApt2Matcher.find())
					if(record.getMisc2().length() == 0)
						record.setMisc2(tableauApt2Matcher.group());
				/////////////////////////////////////////////////////////////

				record.setDupeAdd1(dupeAdd1.replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
				record.setDupeAdd2(dupeAdd2.replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
				record.setDupePCode(Common.correctPCode(record.getPCode()).replaceFirst("^0+(?!$)", ""));
				
				// convert postal codes to FSA, leave ZIP codes as is
				if(chckbxConvertPcToFsa.isSelected())
					if(!Validators.isNumber(record.getDupePCode()) && record.getDupePCode().length() >= 3)
						record.setDupePCode(record.getDupePCode().substring(0, 3));

				if (!this.chckbxIgnoreApt.isSelected()) {
					String add1AptString = preAdd1.toLowerCase().replaceAll("\\p{Pd}", "-").replaceAll("number|(?<=^|\\s)num(?=\\d|$|\\s)", "no").replaceAll(FIX_APT_REGEX, "apt").replaceAll("(?<!(\\d+\\s+(?=(no|#))|apt|pmb|box|rmb|lb|(\\s+|^)?p(\\s+)?o(\\s+)?\\d|comp|cp|(group|grp|coup|gr|gp)\\s?(\\D$|\\D\\s)?\\s?\\d+|site\\s?\\d+|(^|\\s)\\d+).*)((#|no)(?=(\\s+)?(\\D)?\\d+(\\D?)$))", "apt").replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll(FIX_APT_REGEX, "apt");
					String add2AptString = "";
					
					if (!isAddressSame)
						add2AptString = preAdd2.toLowerCase().replaceAll("\\p{Pd}", "-").replaceAll("number|(?<=^|\\s)num(?=\\d|$|\\s)", "no").replaceAll(FIX_APT_REGEX, "apt").replaceAll("(?<!(\\d+\\s+(?=(no|#))|apt|pmb|box|rmb|lb|(\\s+|^)?p(\\s+)?o(\\s+)?\\d|comp|cp|(group|grp|coup|gr|gp)\\s?(\\D$|\\D\\s)?\\s?\\d+|site\\s?\\d+|(^|\\s)\\d+).*)((#|no)(?=(\\s+)?(\\D)?\\d+(\\D?)$))", "apt").replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll(FIX_APT_REGEX, "apt"); 

					Matcher add1Apt = ADD_APT_PATTERN.matcher(add1AptString);
					Matcher add2Apt = ADD_APT_PATTERN.matcher(add2AptString);
					
					Matcher add1AptAlreadyExists = REMOVE_APT_PATTERN.matcher(preAdd1.replaceFirst("^\\D+(?=\\d+)", ""));
					Matcher add2AptAlreadyExists = REMOVE_APT_PATTERN.matcher(isAddressSame ? "" : preAdd2.replaceFirst("^\\D+(?=\\d+)", ""));

					boolean boolAdd1AptAlreadyExists = add1AptAlreadyExists.find();
					boolean boolAdd2AptAlreadyExists = add2AptAlreadyExists.find();
					
					String add1AptValue = "";
					@SuppressWarnings("unused")
					String add2AptValue = "";

					if (add1Apt.find() && !boolAdd1AptAlreadyExists) {
						add1AptValue = String.valueOf(add1Apt.group().replaceAll("[^0-9]", "").replaceAll("^0+(?!$)", ""));
						if (!record.getDupeAdd1().isBlank()) {
							record.setDupeAdd1(add1AptValue + record.getDupeAdd1());
						} else if (!boolAdd2AptAlreadyExists && !record.getDupeAdd2().isBlank()) {
							add1AptValue = String.valueOf(add1Apt.group());
							record.setDupeAdd2(add1AptValue + record.getDupeAdd2());
						}
					}

					if (add2Apt.find() && !boolAdd2AptAlreadyExists) {
						add2AptValue = String.valueOf(add2Apt.group().replaceAll("[^0-9]", "").replaceAll("^0+(?!$)", ""));
						if(add1AptValue.isBlank()) { // ONLY ADD APT ONCE
							if (!record.getDupeAdd2().isBlank()) {
								record.setDupeAdd2(String.valueOf(add2Apt.group().replaceAll("[^0-9]", "").replaceAll("^0+(?!$)", "")) + record.getDupeAdd2());
							} else if (!boolAdd1AptAlreadyExists && !record.getDupeAdd1().isBlank()) {
								record.setDupeAdd1(String.valueOf(add2Apt.group().replaceAll("[^0-9]", "").replaceAll("^0+(?!$)", "")) + record.getDupeAdd1());
							}
						}
					}
					
					// Extra Apt info for analytics
					/////////////////////////////////////////////////////////////
					if(record.getMisc1().length() == 0)
						record.setMisc1(add1AptValue);
					if(record.getMisc2().length() == 0)
						record.setMisc2(add2AptValue);
					/////////////////////////////////////////////////////////////
				} 


				if (!record.getDupeMetaStreetAdd1().isBlank())
					record.setDupeMetaStreetAdd1(Common.rightPad(record.getDupeMetaStreetAdd1(), 15, 'X')); 

				if (!record.getDupeMetaStreetAdd2().isBlank())
					record.setDupeMetaStreetAdd2(Common.rightPad(record.getDupeMetaStreetAdd2(), 15, 'X')); 
				
				System.out.println(record.getDupeAdd1());
				System.out.println(newDupeAplha1);
				System.out.println(newDupeAplha2);
				System.out.println(record.getDupeMetaStreetAdd1());
				System.out.println(record.getDupeRR());
				System.out.println();
			} 
			this.progressBar.setValue(0);
		} 
	}

	private void findNameDupes(ArrayList<List<Record>> dupeAddressGroups, ArrayList<Record> partitionAllDupeRecordsList) throws Exception {
		String minimumEditDistanceString = this.textFieldNameSimilarityPercentage.getText();
		double mimumEditDistance = this.percentageForNameSimilarity;
		int editDistanceAsInt = 0;
		if (Validators.isWholeNumber(minimumEditDistanceString)) {
			if (minimumEditDistanceString.length() > 1) {
				editDistanceAsInt = Integer.parseInt(minimumEditDistanceString);
				minimumEditDistanceString = String.valueOf(editDistanceAsInt);
			} 
			if (minimumEditDistanceString.length() == 1)
				minimumEditDistanceString = "0" + minimumEditDistanceString; 
			if (editDistanceAsInt >= 100) {
				minimumEditDistanceString = "1.00";
			} else {
				minimumEditDistanceString = "0." + minimumEditDistanceString;
			} 
			mimumEditDistance = Double.valueOf(minimumEditDistanceString).doubleValue();
		} 
		if (dupeAddressGroups.size() > 0) {
			Set<Integer> allDupeIds = new HashSet<>();
			ArrayList<List<Record>> dupeRecordGroups = new ArrayList<>();
 
			this.progressBar.setStringPainted(false);
			this.progressBar.setIndeterminate(true);
			for (List<Record> list : dupeAddressGroups) {
				for (int i = 0; i < list.size(); i++) {
					if (this.chckbxExactNamesOnly.isSelected()) {
						while (allDupeIds.contains(Integer.valueOf(((Record)list.get(i)).getDfId()))) {
							//this.progressBar.setValue(this.progressBar.getValue() + 1);
							if (++i >= list.size())
								break; 
						} 
						if (i >= list.size())
							break; 
					} 
					if (this.chckbxKillFilePriority.isSelected()) {
						String tempPriority;
						if (this.isDfPrioritySet) {
							tempPriority = UserData.getRecordFieldByName(this.comboBoxPriority.getName(), (Record)list.get(i));
							//tempPriority = ((Record)list.get(i)).getPriority();
						} else {
							tempPriority = ((Record)list.get(i)).getDfInData()[this.comboBoxPriority.getSelectedIndex()];
						} 
						while (!tempPriority.equalsIgnoreCase(this.killPriority)) {
							//this.progressBar.setValue(this.progressBar.getValue() + 1);
							if (++i >= list.size())
								break; 
							if (this.isDfPrioritySet) {
								tempPriority = UserData.getRecordFieldByName(this.comboBoxPriority.getName(), (Record)list.get(i));
								//tempPriority = ((Record)list.get(i)).getPriority();
								continue;
							} 
							tempPriority = ((Record)list.get(i)).getDfInData()[this.comboBoxPriority.getSelectedIndex()];
						} 
						if (i >= list.size())
							break; 
					} 
					Record tempRec = list.get(i);
					List<Record> currentDupesToAdd = new ArrayList<>();
					//this.progressBar.setValue(this.progressBar.getValue() + 1);
					this.lblStatus.setText("Searching for name dupes");
					boolean dupeWasFound = false;
					int startIndex = i + 1;
					for (int j = startIndex; j < list.size() && (
							j != i || 
							++j < list.size()); j++) {
						if (this.chckbxExactNamesOnly.isSelected()) {
							do {

							} while (allDupeIds.contains(Integer.valueOf(((Record)list.get(j)).getDfId())) && 
									++j < list.size());
							if (j >= list.size())
								break; 
						} 
						if (this.chckbxKillFilePriority.isSelected()) {
							String tempPriority;
							if (this.isDfPrioritySet) {
								tempPriority = UserData.getRecordFieldByName(this.comboBoxPriority.getName(), (Record)list.get(j));
								//tempPriority = ((Record)list.get(j)).getPriority();
							} else {
								tempPriority = ((Record)list.get(j)).getDfInData()[this.comboBoxPriority.getSelectedIndex()];
							} 
							while (tempPriority.equalsIgnoreCase(this.killPriority) && 
									++j < list.size()) {
								if (this.isDfPrioritySet) {
									tempPriority = UserData.getRecordFieldByName(this.comboBoxPriority.getName(), (Record)list.get(j));
									//tempPriority = ((Record)list.get(j)).getPriority();
									continue;
								} 
								tempPriority = ((Record)list.get(j)).getDfInData()[this.comboBoxPriority.getSelectedIndex()];
							} 
							if (j >= list.size())
								break; 
						} 
						boolean dupeNameFound = false;
						Record nextRec = list.get(j);
						if (this.chckbxExactNamesOnly.isSelected()) {
							String tempRecName1 = tempRec.getDupeName1();
							String tempRecName2 = tempRec.getDupeName2();
							String nextRecName1 = nextRec.getDupeName1();
							String nextRecName2 = nextRec.getDupeName2();
							if (this.isIfName1BlankSelected) {
								if (tempRecName1.trim().isEmpty())
									if (this.isDfIfName1BlankSelected) {
										tempRecName1 = UserData.getRecordFieldByName(this.comboBoxIfName1Blank.getSelectedItem().toString(), tempRec);
									} else {
										tempRecName1 = tempRec.getDfInData()[this.comboBoxIfName1Blank.getSelectedIndex()];
									}  
								if (nextRecName1.trim().isEmpty())
									if (this.isDfIfName1BlankSelected) {
										nextRecName1 = UserData.getRecordFieldByName(this.comboBoxIfName1Blank.getSelectedItem().toString(), nextRec);
									} else {
										nextRecName1 = nextRec.getDfInData()[this.comboBoxIfName1Blank.getSelectedIndex()];
									}  
							} 
							if (this.isIfName2BlankSelected) {
								if (tempRecName2.trim().isEmpty())
									if (this.isDfIfName2BlankSelected) {
										tempRecName2 = UserData.getRecordFieldByName(this.comboBoxIfName2Blank.getSelectedItem().toString(), tempRec);
									} else {
										tempRecName2 = tempRec.getDfInData()[this.comboBoxIfName2Blank.getSelectedIndex()];
									}  
								if (nextRecName2.trim().isEmpty())
									if (this.isDfIfName2BlankSelected) {
										nextRecName2 = UserData.getRecordFieldByName(this.comboBoxIfName2Blank.getSelectedItem().toString(), nextRec);
									} else {
										nextRecName2 = nextRec.getDfInData()[this.comboBoxIfName2Blank.getSelectedIndex()];
									}  
							} 
							if (this.rdbtnDedupeAddress.isSelected() || this.rdbtnDedupePC.isSelected()) {
								
								// include blank names
								if(chckbxIncludeBlankNames.isSelected()) {
									if(this.isName1Selected) {
										if(tempRecName1.isEmpty() || nextRecName1.isEmpty())
											dupeNameFound = true;
									}
									if(this.isName2Selected) {
										if(tempRecName2.isEmpty() || nextRecName2.isEmpty())
											dupeNameFound = true;
									}
								}
								
								if (!dupeNameFound || ((!tempRecName1.isEmpty() || !tempRecName2.isEmpty()) && (!nextRecName1.isEmpty() || !nextRecName2.isEmpty())))
									if (!tempRecName1.isEmpty() && !tempRecName2.isEmpty() && !nextRecName1.isEmpty() && !nextRecName2.isEmpty()) {
										if (tempRecName1.equalsIgnoreCase(nextRecName1) && 
												tempRecName2.equalsIgnoreCase(nextRecName2)) {
											dupeNameFound = true;
										} else {
											continue;
										} 
									} else if (!tempRecName1.isEmpty() && tempRecName2.isEmpty() && (nextRecName1.isEmpty() || nextRecName2.isEmpty())) {
										if (tempRecName1.equalsIgnoreCase(nextRecName1) && nextRecName2.isEmpty())
											dupeNameFound = true; 
									} else if (!tempRecName2.isEmpty() && tempRecName1.isEmpty() && (nextRecName1.isEmpty() || nextRecName2.isEmpty()) && 
											tempRecName2.equalsIgnoreCase(nextRecName2) && nextRecName2.isEmpty()) {
										dupeNameFound = true;
									}  
							} else if (this.rdbtnDedupeName.isSelected() && 
									tempRecName1.equalsIgnoreCase(nextRecName1) && 
									tempRecName2.equalsIgnoreCase(nextRecName2)) {
								dupeNameFound = true;
							}
							
							
						} else if (!this.chckbxExactNamesOnly.isSelected()) {
							String tempRecName1FirstPerson = tempRec.getDupeName1FirstPerson();
							String tempRecName1SecondPerson = tempRec.getDupeName1SecondPerson();
							String tempRecName1Normalized = tempRec.getDupeName1Normalized();
							String tempRecName2FirstPerson = tempRec.getDupeName2FirstPerson();
							String tempRecName2SecondPerson = tempRec.getDupeName2SecondPerson();
							String tempRecName2Normalized = tempRec.getDupeName2Normalized();
							String nextRecName1FirstPerson = nextRec.getDupeName1FirstPerson();
							String nextRecName1SecondPerson = nextRec.getDupeName1SecondPerson();
							String nextRecName1Normalized = nextRec.getDupeName1Normalized();
							String nextRecName2FirstPerson = nextRec.getDupeName2FirstPerson();
							String nextRecName2SecondPerson = nextRec.getDupeName2SecondPerson();
							String nextRecName2Normalized = nextRec.getDupeName2Normalized();
							if (this.isIfName1BlankSelected) {
								if (tempRecName1FirstPerson.trim().isEmpty())
									if (this.isDfIfName1BlankSelected) {
										tempRecName1FirstPerson = StringUtils.stripAccents(UserData.getRecordFieldByName(this.comboBoxIfName1Blank.getSelectedItem().toString(), tempRec));
									} else {
										tempRecName1FirstPerson = StringUtils.stripAccents(tempRec.getDfInData()[this.comboBoxIfName1Blank.getSelectedIndex()]);
									}  
								if (nextRecName1FirstPerson.trim().isEmpty())
									if (this.isDfIfName1BlankSelected) {
										nextRecName1FirstPerson = StringUtils.stripAccents(UserData.getRecordFieldByName(this.comboBoxIfName1Blank.getSelectedItem().toString(), nextRec));
									} else {
										nextRecName1FirstPerson = StringUtils.stripAccents(nextRec.getDfInData()[this.comboBoxIfName1Blank.getSelectedIndex()]);
									}  
							} 
							if (this.isIfName2BlankSelected) {
								if (tempRecName2FirstPerson.trim().isEmpty())
									if (this.isDfIfName2BlankSelected) {
										tempRecName2FirstPerson = StringUtils.stripAccents(UserData.getRecordFieldByName(this.comboBoxIfName2Blank.getSelectedItem().toString(), tempRec));
									} else {
										tempRecName2FirstPerson = StringUtils.stripAccents(tempRec.getDfInData()[this.comboBoxIfName2Blank.getSelectedIndex()]);
									}  
								if (nextRecName2FirstPerson.trim().isEmpty())
									if (this.isDfIfName2BlankSelected) {
										nextRecName2FirstPerson = StringUtils.stripAccents(UserData.getRecordFieldByName(this.comboBoxIfName2Blank.getSelectedItem().toString(), nextRec));
									} else {
										nextRecName2FirstPerson = StringUtils.stripAccents(nextRec.getDfInData()[this.comboBoxIfName2Blank.getSelectedIndex()]);
									}  
							}
							
							// include blank names
							if(chckbxIncludeBlankNames.isSelected()) {
								if(this.isName1Selected) {
									if(tempRecName1Normalized.isEmpty() || nextRecName1Normalized.isEmpty())
										dupeNameFound = true;
								}
								if(this.isName2Selected) {
									if(tempRecName2Normalized.isEmpty() || nextRecName2Normalized.isEmpty())
										dupeNameFound = true;
								}
							}
							
							if (!dupeNameFound || ((!tempRecName1FirstPerson.isEmpty() || !tempRecName2FirstPerson.isEmpty()) && (
									!nextRecName1FirstPerson.isEmpty() || !nextRecName2FirstPerson.isEmpty()))) {
								//System.out.println("r1 n1 p1 vs r2 n1 p1");
								if (!tempRecName1FirstPerson.isEmpty() && !nextRecName1FirstPerson.isEmpty())
									if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
										if (tempRecName1FirstPerson.length() == 1 || (
												tempRecName1FirstPerson.length() > 1 && tempRecName1FirstPerson.substring(1, 2).equals(" "))) {
											if (Common.getInitials(tempRecName1FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName1FirstPerson)))
												dupeNameFound = true; 
										} else if (nextRecName1FirstPerson.length() == 1 || (
												nextRecName1FirstPerson.length() > 1 && nextRecName1FirstPerson.substring(1, 2).equals(" "))) {
											if (Common.getInitials(tempRecName1FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName1FirstPerson)))
												dupeNameFound = true; 
										} else if (Common.getFirstLastInitials(tempRecName1FirstPerson)
												.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1FirstPerson))) {
											dupeNameFound = true;
										} else if (Common.getNysiis(tempRecName1FirstPerson).equalsIgnoreCase(Common.getNysiis(nextRecName1FirstPerson))) {
											dupeNameFound = true;
										} else if (Common.getMetaphone3(tempRecName1FirstPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName1FirstPerson))) {
											dupeNameFound = true;
										} else if (Common.getRefinedSoundex(tempRecName1FirstPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName1FirstPerson))) {
											dupeNameFound = true;
										} else if (tempRecName1FirstPerson.equalsIgnoreCase(Common.getReversedString(nextRecName1FirstPerson))) {
											dupeNameFound = true;
										} else if (tempRecName1FirstPerson.length() > 2 && (
												new JaroWinklerSimilarity()).apply(tempRecName1FirstPerson, nextRecName1FirstPerson).doubleValue() >= mimumEditDistance) {
											dupeNameFound = true;
										} 
									} else if (tempRecName1FirstPerson.length() > 2 && (
											new JaroWinklerSimilarity()).apply(tempRecName1FirstPerson, nextRecName1FirstPerson).doubleValue() >= mimumEditDistance) {
										dupeNameFound = true;
									}  
								if (!dupeNameFound) {
									//System.out.println("r1 n1 p2 vs r2 n1 p1");
									if (!tempRecName1SecondPerson.isEmpty() && !nextRecName1FirstPerson.isEmpty())
										if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
											if (tempRecName1SecondPerson.length() == 1 || (
													tempRecName1SecondPerson.length() > 1 && tempRecName1SecondPerson.substring(1, 2).equals(" "))) {
												if (Common.getInitials(tempRecName1SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName1FirstPerson)))
													dupeNameFound = true; 
											} else if (nextRecName1FirstPerson.length() == 1 || (
													nextRecName1FirstPerson.length() > 1 && nextRecName1FirstPerson.substring(1, 2).equals(" "))) {
												if (Common.getInitials(tempRecName1SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName1FirstPerson)))
													dupeNameFound = true; 
											} else if (Common.getFirstLastInitials(tempRecName1SecondPerson)
													.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1FirstPerson))) {
												dupeNameFound = true;
											} else if (Common.getNysiis(tempRecName1SecondPerson).equalsIgnoreCase(Common.getNysiis(nextRecName1FirstPerson))) {
												dupeNameFound = true;
											} else if (Common.getMetaphone3(tempRecName1SecondPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName1FirstPerson))) {
												dupeNameFound = true;
											} else if (Common.getRefinedSoundex(tempRecName1SecondPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName1FirstPerson))) {
												dupeNameFound = true;
											} else if (tempRecName1SecondPerson.equalsIgnoreCase(Common.getReversedString(nextRecName1FirstPerson))) {
												dupeNameFound = true;
											} else if (tempRecName1SecondPerson.length() > 2 && (
													new JaroWinklerSimilarity()).apply(tempRecName1SecondPerson, nextRecName1FirstPerson).doubleValue() >= mimumEditDistance) {
												dupeNameFound = true;
											} 
										} else if (tempRecName1SecondPerson.length() > 2 && (
												new JaroWinklerSimilarity()).apply(tempRecName1SecondPerson, nextRecName1FirstPerson).doubleValue() >= mimumEditDistance) {
											dupeNameFound = true;
										}
										if (!dupeNameFound) {
											//System.out.println("r1 n1 p2 vs r2 n1 p2");
											if (!tempRecName1SecondPerson.isEmpty() && !nextRecName1SecondPerson.isEmpty())
												if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
													if (tempRecName1SecondPerson.length() == 1 || (
															tempRecName1SecondPerson.length() > 1 && tempRecName1SecondPerson.substring(1, 2).equals(" "))) {
														if (Common.getInitials(tempRecName1SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName1SecondPerson)))
															dupeNameFound = true; 
													} else if (nextRecName1SecondPerson.length() == 1 || (
															nextRecName1SecondPerson.length() > 1 && nextRecName1SecondPerson.substring(1, 2).equals(" "))) {
														if (Common.getInitials(tempRecName1SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName1SecondPerson)))
															dupeNameFound = true; 
													} else if (Common.getFirstLastInitials(tempRecName1SecondPerson)
															.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1FirstPerson))) {
														dupeNameFound = true;
													} else if (Common.getNysiis(tempRecName1SecondPerson).equalsIgnoreCase(Common.getNysiis(nextRecName1SecondPerson))) {
														dupeNameFound = true;
													} else if (Common.getMetaphone3(tempRecName1SecondPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName1SecondPerson))) {
														dupeNameFound = true;
													} else if (Common.getRefinedSoundex(tempRecName1SecondPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName1SecondPerson))) {
														dupeNameFound = true;
													} else if (tempRecName1SecondPerson.equalsIgnoreCase(Common.getReversedString(nextRecName1SecondPerson))) {
														dupeNameFound = true;
													} else if (tempRecName1SecondPerson.length() > 2 && (
															new JaroWinklerSimilarity()).apply(tempRecName1SecondPerson, nextRecName1SecondPerson).doubleValue() >= mimumEditDistance) {
														dupeNameFound = true;
													} 
												} else if (tempRecName1SecondPerson.length() > 2 && (
														new JaroWinklerSimilarity()).apply(tempRecName1SecondPerson, nextRecName1SecondPerson).doubleValue() >= mimumEditDistance) {
													dupeNameFound = true;
												}
											if (!dupeNameFound) {
												//System.out.println("r1 n1 p1 vs r2 n1 p2");
												if (!tempRecName1FirstPerson.isEmpty() && !nextRecName1SecondPerson.isEmpty())
													if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
														if (tempRecName1FirstPerson.length() == 1 || (
																tempRecName1FirstPerson.length() > 1 && tempRecName1FirstPerson.substring(1, 2).equals(" "))) {
															if (Common.getInitials(tempRecName1FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName1SecondPerson)))
																dupeNameFound = true; 
														} else if (nextRecName1SecondPerson.length() == 1 || (
																nextRecName1SecondPerson.length() > 1 && nextRecName1SecondPerson.substring(1, 2).equals(" "))) {
															if (Common.getInitials(tempRecName1FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName1SecondPerson)))
																dupeNameFound = true; 
														} else if (Common.getFirstLastInitials(tempRecName1FirstPerson)
																.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1FirstPerson))) {
															dupeNameFound = true;
														} else if (Common.getNysiis(tempRecName1FirstPerson).equalsIgnoreCase(Common.getNysiis(nextRecName1SecondPerson))) {
															dupeNameFound = true;
														} else if (Common.getMetaphone3(tempRecName1FirstPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName1SecondPerson))) {
															dupeNameFound = true;
														} else if (Common.getRefinedSoundex(tempRecName1FirstPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName1SecondPerson))) {
															dupeNameFound = true;
														} else if (tempRecName1FirstPerson.equalsIgnoreCase(Common.getReversedString(nextRecName1SecondPerson))) {
															dupeNameFound = true;
														} else if (tempRecName1FirstPerson.length() > 2 && (
																new JaroWinklerSimilarity()).apply(tempRecName1FirstPerson, nextRecName1SecondPerson).doubleValue() >= mimumEditDistance) {
															dupeNameFound = true;
														} 
													} else if (tempRecName1SecondPerson.length() > 2 && (
															new JaroWinklerSimilarity()).apply(tempRecName1FirstPerson, nextRecName1SecondPerson).doubleValue() >= mimumEditDistance) {
														dupeNameFound = true;
													}
											if (!dupeNameFound) {
												//System.out.println("r1 n1 p1 vs r2 n2 p2");
												if (!tempRecName1FirstPerson.isEmpty() && !nextRecName2FirstPerson.isEmpty())
													if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
														if (tempRecName1FirstPerson.length() == 1 || (
																tempRecName1FirstPerson.length() > 1 && tempRecName1FirstPerson.substring(1, 2).equals(" "))) {
															if (Common.getInitials(tempRecName1FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName2FirstPerson)))
																dupeNameFound = true; 
														} else if (nextRecName2FirstPerson.length() == 1 || (
																nextRecName2FirstPerson.length() > 1 && nextRecName2FirstPerson.substring(1, 2).equals(" "))) {
															if (Common.getInitials(tempRecName1FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName2FirstPerson)))
																dupeNameFound = true; 
														} else if (Common.getFirstLastInitials(tempRecName1FirstPerson)
																.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName2FirstPerson))) {
															dupeNameFound = true;
														} else if (Common.getNysiis(tempRecName1FirstPerson).equalsIgnoreCase(Common.getNysiis(nextRecName2FirstPerson))) {
															dupeNameFound = true;
														} else if (Common.getMetaphone3(tempRecName1FirstPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName2FirstPerson))) {
															dupeNameFound = true;
														} else if (Common.getRefinedSoundex(tempRecName1FirstPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName2FirstPerson))) {
															dupeNameFound = true;
														} else if (tempRecName1FirstPerson.equalsIgnoreCase(Common.getReversedString(nextRecName2FirstPerson))) {
															dupeNameFound = true;
														} else if (tempRecName1FirstPerson.length() > 2 && (
																new JaroWinklerSimilarity()).apply(tempRecName1FirstPerson, nextRecName2FirstPerson).doubleValue() >= mimumEditDistance) {
															dupeNameFound = true;
														} 
													} else if (tempRecName1FirstPerson.length() > 2 && (
															new JaroWinklerSimilarity()).apply(tempRecName1FirstPerson, nextRecName2FirstPerson).doubleValue() >= mimumEditDistance) {
														dupeNameFound = true;
													}  
												if (!dupeNameFound) {
													//System.out.println("r1 n1 p2 vs r2 n2 p1");
													if (!tempRecName1SecondPerson.isEmpty() && !nextRecName2FirstPerson.isEmpty())
														if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
															if (tempRecName1SecondPerson.length() == 1 || (
																	tempRecName1SecondPerson.length() > 1 && tempRecName1SecondPerson.substring(1, 2).equals(" "))) {
																if (Common.getInitials(tempRecName1SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName2FirstPerson)))
																	dupeNameFound = true; 
															} else if (nextRecName2FirstPerson.length() == 1 || (
																	nextRecName2FirstPerson.length() > 1 && nextRecName2FirstPerson.substring(1, 2).equals(" "))) {
																if (Common.getInitials(tempRecName1SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName2FirstPerson)))
																	dupeNameFound = true; 
															} else if (Common.getFirstLastInitials(tempRecName1SecondPerson)
																	.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName2FirstPerson))) {
																dupeNameFound = true;
															} else if (Common.getNysiis(tempRecName1SecondPerson).equalsIgnoreCase(Common.getNysiis(nextRecName2FirstPerson))) {
																dupeNameFound = true;
															} else if (Common.getMetaphone3(tempRecName1SecondPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName2FirstPerson))) {
																dupeNameFound = true;
															} else if (Common.getRefinedSoundex(tempRecName1SecondPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName2FirstPerson))) {
																dupeNameFound = true;
															} else if (tempRecName1SecondPerson.equalsIgnoreCase(Common.getReversedString(nextRecName2FirstPerson))) {
																dupeNameFound = true;
															} else if (tempRecName1SecondPerson.length() > 2 && (
																	new JaroWinklerSimilarity()).apply(tempRecName1SecondPerson, nextRecName2FirstPerson).doubleValue() >= mimumEditDistance) {
																dupeNameFound = true;
															} 
														} else if (tempRecName1SecondPerson.length() > 2 && (
																new JaroWinklerSimilarity()).apply(tempRecName1SecondPerson, nextRecName2FirstPerson).doubleValue() >= mimumEditDistance) {
															dupeNameFound = true;
														}  
													if (!dupeNameFound) {
														//System.out.println("r1 n1 p2 vs r2 n2 p2");
														if (!tempRecName1SecondPerson.isEmpty() && !tempRecName2SecondPerson.isEmpty())
															if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
																if (tempRecName1SecondPerson.length() == 1 || (
																		tempRecName1SecondPerson.length() > 1 && tempRecName1SecondPerson.substring(1, 2).equals(" "))) {
																	if (Common.getInitials(tempRecName1SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName2SecondPerson)))
																		dupeNameFound = true; 
																} else if (nextRecName2SecondPerson.length() == 1 || (
																		nextRecName2SecondPerson.length() > 1 && nextRecName2SecondPerson.substring(1, 2).equals(" "))) {
																	if (Common.getInitials(tempRecName1SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName2SecondPerson)))
																		dupeNameFound = true; 
																} else if (Common.getFirstLastInitials(tempRecName1SecondPerson)
																		.equalsIgnoreCase(Common.getFirstLastInitials(tempRecName2SecondPerson))) {
																	dupeNameFound = true;
																} else if (Common.getNysiis(tempRecName1SecondPerson).equalsIgnoreCase(Common.getNysiis(nextRecName2SecondPerson))) {
																	dupeNameFound = true;
																} else if (Common.getMetaphone3(tempRecName1SecondPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName2SecondPerson))) {
																	dupeNameFound = true;
																} else if (Common.getRefinedSoundex(tempRecName1SecondPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName2SecondPerson))) {
																	dupeNameFound = true;
																} else if (tempRecName1SecondPerson.equalsIgnoreCase(Common.getReversedString(nextRecName2SecondPerson))) {
																	dupeNameFound = true;
																} else if (tempRecName1SecondPerson.length() > 2 && (
																		new JaroWinklerSimilarity()).apply(tempRecName1SecondPerson, nextRecName2SecondPerson).doubleValue() >= mimumEditDistance) {
																	dupeNameFound = true;
																} 
															} else if (tempRecName1SecondPerson.length() > 2 && (
																	new JaroWinklerSimilarity()).apply(tempRecName1SecondPerson, nextRecName2SecondPerson).doubleValue() >= mimumEditDistance) {
																dupeNameFound = true;
															}  
														if (!dupeNameFound) {
															if (!tempRecName2FirstPerson.isEmpty() && !nextRecName1FirstPerson.isEmpty())
																if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
																	if (tempRecName2FirstPerson.length() == 1 || (
																			tempRecName2FirstPerson.length() > 1 && tempRecName2FirstPerson.substring(1, 2).equals(" "))) {
																		if (Common.getInitials(tempRecName2FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName1FirstPerson)))
																			dupeNameFound = true; 
																	} else if (nextRecName1FirstPerson.length() == 1 || (
																			nextRecName1FirstPerson.length() > 1 && nextRecName1FirstPerson.substring(1, 2).equals(" "))) {
																		if (Common.getInitials(tempRecName2FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName1FirstPerson)))
																			dupeNameFound = true; 
																	} else if (Common.getFirstLastInitials(tempRecName2FirstPerson)
																			.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1FirstPerson))) {
																		dupeNameFound = true;
																	} else if (Common.getNysiis(tempRecName2FirstPerson).equalsIgnoreCase(Common.getNysiis(nextRecName1FirstPerson))) {
																		dupeNameFound = true;
																	} else if (Common.getMetaphone3(tempRecName2FirstPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName1FirstPerson))) {
																		dupeNameFound = true;
																	} else if (Common.getRefinedSoundex(tempRecName2FirstPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName1FirstPerson))) {
																		dupeNameFound = true;
																	} else if (tempRecName2FirstPerson.equalsIgnoreCase(Common.getReversedString(nextRecName1FirstPerson))) {
																		dupeNameFound = true;
																	} else if (tempRecName2FirstPerson.length() > 2 && (
																			new JaroWinklerSimilarity()).apply(tempRecName2FirstPerson, nextRecName1FirstPerson).doubleValue() >= mimumEditDistance) {
																		dupeNameFound = true;
																	} 
																} else if (tempRecName2FirstPerson.length() > 2 && (
																		new JaroWinklerSimilarity()).apply(tempRecName2FirstPerson, nextRecName1FirstPerson).doubleValue() >= mimumEditDistance) {
																	dupeNameFound = true;
																}  
															if (!dupeNameFound) {
																if (!tempRecName2FirstPerson.isEmpty() && !nextRecName1SecondPerson.isEmpty())
																	if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
																		if (tempRecName2FirstPerson.length() == 1 || (
																				tempRecName2FirstPerson.length() > 1 && tempRecName2FirstPerson.substring(1, 2).equals(" "))) {
																			if (Common.getInitials(tempRecName2FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName1SecondPerson)))
																				dupeNameFound = true; 
																		} else if (nextRecName1SecondPerson.length() == 1 || (
																				nextRecName1SecondPerson.length() > 1 && nextRecName1SecondPerson.substring(1, 2).equals(" "))) {
																			if (Common.getInitials(tempRecName2FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName1SecondPerson)))
																				dupeNameFound = true; 
																		} else if (Common.getFirstLastInitials(tempRecName2FirstPerson)
																				.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1SecondPerson))) {
																			dupeNameFound = true;
																		} else if (Common.getNysiis(tempRecName2FirstPerson).equalsIgnoreCase(Common.getNysiis(nextRecName1SecondPerson))) {
																			dupeNameFound = true;
																		} else if (Common.getMetaphone3(tempRecName2FirstPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName1SecondPerson))) {
																			dupeNameFound = true;
																		} else if (Common.getRefinedSoundex(tempRecName2FirstPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName1SecondPerson))) {
																			dupeNameFound = true;
																		} else if (tempRecName2FirstPerson.equalsIgnoreCase(Common.getReversedString(nextRecName2SecondPerson))) {
																			dupeNameFound = true;
																		} else if (tempRecName2FirstPerson.length() > 2 && (
																				new JaroWinklerSimilarity()).apply(tempRecName2FirstPerson, nextRecName2SecondPerson).doubleValue() >= mimumEditDistance) {
																			dupeNameFound = true;
																		} 
																	} else if (tempRecName2FirstPerson.length() > 2 && (
																			new JaroWinklerSimilarity()).apply(tempRecName2FirstPerson, nextRecName2SecondPerson).doubleValue() >= mimumEditDistance) {
																		dupeNameFound = true;
																	}  
																if (!dupeNameFound) {
																	if (!tempRecName2SecondPerson.isEmpty() && !nextRecName1FirstPerson.isEmpty())
																		if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
																			if (nextRecName2SecondPerson.length() == 1 || (
																					nextRecName2SecondPerson.length() > 1 && nextRecName2SecondPerson.substring(1, 2).equals(" "))) {
																				if (Common.getInitials(nextRecName2SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName1FirstPerson)))
																					dupeNameFound = true; 
																			} else if (nextRecName1FirstPerson.length() == 1 || (
																					nextRecName1FirstPerson.length() > 1 && nextRecName1FirstPerson.substring(1, 2).equals(" "))) {
																				if (Common.getInitials(nextRecName2SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName1FirstPerson)))
																					dupeNameFound = true; 
																			} else if (Common.getFirstLastInitials(tempRecName2SecondPerson)
																					.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1FirstPerson))) {
																				dupeNameFound = true;
																			} else if (Common.getNysiis(tempRecName2SecondPerson).equalsIgnoreCase(Common.getNysiis(nextRecName1FirstPerson))) {
																				dupeNameFound = true;
																			} else if (Common.getMetaphone3(tempRecName2SecondPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName1FirstPerson))) {
																				dupeNameFound = true;
																			} else if (Common.getRefinedSoundex(tempRecName2SecondPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName1FirstPerson))) {
																				dupeNameFound = true;
																			} else if (tempRecName2SecondPerson.equalsIgnoreCase(Common.getReversedString(nextRecName1FirstPerson))) {
																				dupeNameFound = true;
																			} else if (tempRecName2SecondPerson.length() > 2 && (
																					new JaroWinklerSimilarity()).apply(tempRecName2SecondPerson, nextRecName1FirstPerson).doubleValue() >= mimumEditDistance) {
																				dupeNameFound = true;
																			} 
																		} else if (tempRecName2SecondPerson.length() > 2 && (
																				new JaroWinklerSimilarity()).apply(tempRecName2SecondPerson, nextRecName1FirstPerson).doubleValue() >= mimumEditDistance) {
																			dupeNameFound = true;
																		}  
																	if (!dupeNameFound) {
																		if (!tempRecName2SecondPerson.isEmpty() && !nextRecName1SecondPerson.isEmpty())
																			if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
																				if (tempRecName2SecondPerson.length() == 1 || (
																						tempRecName2SecondPerson.length() > 1 && tempRecName2SecondPerson.substring(1, 2).equals(" "))) {
																					if (Common.getInitials(tempRecName2SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName1SecondPerson)))
																						dupeNameFound = true; 
																				} else if (nextRecName1SecondPerson.length() == 1 || (
																						nextRecName1SecondPerson.length() > 1 && nextRecName1SecondPerson.substring(1, 2).equals(" "))) {
																					if (Common.getInitials(tempRecName2SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName1SecondPerson)))
																						dupeNameFound = true; 
																				} else if (Common.getFirstLastInitials(tempRecName2SecondPerson)
																						.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1SecondPerson))) {
																					dupeNameFound = true;
																				} else if (Common.getNysiis(tempRecName2SecondPerson).equalsIgnoreCase(Common.getNysiis(nextRecName1SecondPerson))) {
																					dupeNameFound = true;
																				} else if (Common.getMetaphone3(tempRecName2SecondPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName1SecondPerson))) {
																					dupeNameFound = true;
																				} else if (Common.getRefinedSoundex(tempRecName2SecondPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName1SecondPerson))) {
																					dupeNameFound = true;
																				} else if (tempRecName2SecondPerson.equalsIgnoreCase(Common.getReversedString(nextRecName1SecondPerson))) {
																					dupeNameFound = true;
																				} else if (tempRecName2SecondPerson.length() > 2 && (
																						new JaroWinklerSimilarity()).apply(tempRecName2SecondPerson, nextRecName1SecondPerson).doubleValue() >= mimumEditDistance) {
																					dupeNameFound = true;
																				} 
																			} else if (tempRecName2SecondPerson.length() > 2 && (
																					new JaroWinklerSimilarity()).apply(tempRecName2SecondPerson, nextRecName1SecondPerson).doubleValue() >= mimumEditDistance) {
																				dupeNameFound = true;
																			}  
																		if (!dupeNameFound) {
																			if (!tempRecName2FirstPerson.isEmpty() && !nextRecName2FirstPerson.isEmpty())
																				if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
																					if (tempRecName2FirstPerson.length() == 1 || (
																							tempRecName2FirstPerson.length() > 1 && tempRecName2FirstPerson.substring(1, 2).equals(" "))) {
																						if (Common.getInitials(tempRecName2FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName2FirstPerson)))
																							dupeNameFound = true; 
																					} else if (nextRecName2FirstPerson.length() == 1 || (
																							nextRecName2FirstPerson.length() > 1 && nextRecName2FirstPerson.substring(1, 2).equals(" "))) {
																						if (Common.getInitials(tempRecName2FirstPerson).equalsIgnoreCase(Common.getInitials(nextRecName2FirstPerson)))
																							dupeNameFound = true; 
																					} else if (Common.getFirstLastInitials(tempRecName2FirstPerson)
																							.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName2FirstPerson))) {
																						dupeNameFound = true;
																					} else if (Common.getNysiis(tempRecName2FirstPerson).equalsIgnoreCase(Common.getNysiis(nextRecName2FirstPerson))) {
																						dupeNameFound = true;
																					} else if (Common.getMetaphone3(tempRecName2FirstPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName2FirstPerson))) {
																						dupeNameFound = true;
																					} else if (Common.getRefinedSoundex(tempRecName2FirstPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName2FirstPerson))) {
																						dupeNameFound = true;
																					} else if (tempRecName2FirstPerson.equalsIgnoreCase(Common.getReversedString(nextRecName2FirstPerson))) {
																						dupeNameFound = true;
																					} else if (tempRecName2FirstPerson.length() > 2 && (
																							new JaroWinklerSimilarity()).apply(tempRecName2FirstPerson, nextRecName2FirstPerson).doubleValue() >= mimumEditDistance) {
																						dupeNameFound = true;
																					} 
																				} else if (tempRecName2FirstPerson.length() > 2 && (
																						new JaroWinklerSimilarity()).apply(tempRecName2FirstPerson, nextRecName2FirstPerson).doubleValue() >= mimumEditDistance) {
																					dupeNameFound = true;
																				}  
																			if (!dupeNameFound) {
																				if (!tempRecName2SecondPerson.isEmpty() && !nextRecName2SecondPerson.isEmpty())
																					if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
																						if (tempRecName2SecondPerson.length() == 1 || (
																								tempRecName2SecondPerson.length() > 1 && tempRecName2SecondPerson.substring(1, 2).equals(" "))) {
																							if (Common.getInitials(tempRecName2SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName2SecondPerson)))
																								dupeNameFound = true; 
																						} else if (nextRecName2SecondPerson.length() == 1 || (
																								nextRecName2SecondPerson.length() > 1 && nextRecName2SecondPerson.substring(1, 2).equals(" "))) {
																							if (Common.getInitials(tempRecName2SecondPerson).equalsIgnoreCase(Common.getInitials(nextRecName2SecondPerson)))
																								dupeNameFound = true; 
																						} else if (Common.getFirstLastInitials(tempRecName2SecondPerson)
																								.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName2SecondPerson))) {
																							dupeNameFound = true;
																						} else if (Common.getNysiis(tempRecName2SecondPerson).equalsIgnoreCase(Common.getNysiis(nextRecName2SecondPerson))) {
																							dupeNameFound = true;
																						} else if (Common.getMetaphone3(tempRecName2SecondPerson).equalsIgnoreCase(Common.getMetaphone3(nextRecName2SecondPerson))) {
																							dupeNameFound = true;
																						} else if (Common.getRefinedSoundex(tempRecName2SecondPerson).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName2SecondPerson))) {
																							dupeNameFound = true;
																						} else if (tempRecName2SecondPerson.equalsIgnoreCase(Common.getReversedString(nextRecName2SecondPerson))) {
																							dupeNameFound = true;
																						} else if (tempRecName2SecondPerson.length() > 2 && (
																								new JaroWinklerSimilarity()).apply(tempRecName2SecondPerson, nextRecName2SecondPerson).doubleValue() >= mimumEditDistance) {
																							dupeNameFound = true;
																						} 
																					} else if (tempRecName2SecondPerson.length() > 2 && (
																							new JaroWinklerSimilarity()).apply(tempRecName2SecondPerson, nextRecName2SecondPerson).doubleValue() >= mimumEditDistance) {
																						dupeNameFound = true;
																					}  
																				if (!dupeNameFound) {
																					if (!tempRecName1Normalized.isEmpty())
																						if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
																							if (Common.getFirstLastInitials(tempRecName1Normalized).equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1Normalized))) {
																								dupeNameFound = true;
																							} else if (Common.getFirstLastInitials(tempRecName1Normalized)
																									.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName2Normalized))) {
																								dupeNameFound = true;
																							} else if (tempRecName1Normalized.length() > 2) {
																								if ((new JaroWinklerSimilarity()).apply(tempRecName1Normalized, nextRecName1Normalized).doubleValue() >= mimumEditDistance) {
																									dupeNameFound = true;
																								} else if ((new JaroWinklerSimilarity()).apply(tempRecName1Normalized, nextRecName2Normalized).doubleValue() >= mimumEditDistance) {
																									dupeNameFound = true;
																								} 
																							} 
																						} else if (tempRecName1Normalized.length() > 2) {
																							if ((new JaroWinklerSimilarity()).apply(tempRecName1Normalized, nextRecName1Normalized).doubleValue() >= mimumEditDistance) {
																								dupeNameFound = true;
																							} else if ((new JaroWinklerSimilarity()).apply(tempRecName1Normalized, nextRecName2Normalized).doubleValue() >= mimumEditDistance) {
																								dupeNameFound = true;
																							} 
																						}  
																					if (!dupeNameFound)
																						if (!tempRecName2Normalized.isEmpty())
																							if (!this.chckbxMatchOnNameSimilarityOnly.isSelected()) {
																								if (Common.getFirstLastInitials(tempRecName2Normalized).equalsIgnoreCase(Common.getFirstLastInitials(nextRecName1Normalized))) {
																									dupeNameFound = true;
																								} else if (Common.getFirstLastInitials(tempRecName2Normalized)
																										.equalsIgnoreCase(Common.getFirstLastInitials(nextRecName2Normalized))) {
																									dupeNameFound = true;
																								} else if (tempRecName2Normalized.length() > 2) {
																									if ((new JaroWinklerSimilarity()).apply(tempRecName2Normalized, nextRecName1Normalized).doubleValue() >= mimumEditDistance) {
																										dupeNameFound = true;
																									} else if ((new JaroWinklerSimilarity()).apply(tempRecName2Normalized, nextRecName2Normalized).doubleValue() >= mimumEditDistance) {
																										dupeNameFound = true;
																									} 
																								} 
																							} else if (tempRecName2Normalized.length() > 2) {
																								if ((new JaroWinklerSimilarity()).apply(tempRecName2Normalized, nextRecName1Normalized).doubleValue() >= mimumEditDistance) {
																									dupeNameFound = true;
																								} else if ((new JaroWinklerSimilarity()).apply(tempRecName2Normalized, nextRecName2Normalized).doubleValue() >= mimumEditDistance) {
																									dupeNameFound = true;
																								} 
																							}   
																				} 
																			} 
																		} 
																	} 
																} 
															}
														}
													} 
												} 
											} 
										} 
									} 
								} 
							} 
						}

							
						if (dupeNameFound) {
							dupeWasFound = true;
							if (allDupeIds.contains(Integer.valueOf(((Record)list.get(i)).getDfId()))) {
								for (int l = dupeRecordGroups.size() - 1; l >= 0; l--) {
									List<Record> recordList = dupeRecordGroups.get(l);
									for (int k = recordList.size() - 1; k >= 0; k--) {
										if (((Record)recordList.get(k)).getDfId() == ((Record)list.get(i)).getDfId() && 
												allDupeIds.add(Integer.valueOf(((Record)list.get(j)).getDfId()))) {
											recordList.add(list.get(j));
											if (!this.chckbxKillFilePriority.isSelected()) {
												this.lblChildDupesAmount.setText(String.valueOf(childDupesCounter.incrementAndGet()));
												this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.incrementAndGet()));
											} 
										} 
									} 
								} 
							} else if (allDupeIds.add(Integer.valueOf(((Record)list.get(j)).getDfId()))) {
								currentDupesToAdd.add(list.get(j));
							} else {
								for (int l = dupeRecordGroups.size() - 1; l >= 0; l--) {
									List<Record> recordList = dupeRecordGroups.get(l);
									for (int k = recordList.size() - 1; k >= 0; k--) {
										if (((Record)recordList.get(k)).getDfId() == ((Record)list.get(j)).getDfId() && 
												allDupeIds.add(Integer.valueOf(((Record)list.get(i)).getDfId()))) {
											recordList.add(list.get(i));
											if (!this.chckbxKillFilePriority.isSelected()) {
												this.lblChildDupesAmount.setText(String.valueOf(childDupesCounter.incrementAndGet()));
												this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.incrementAndGet()));
											} 
										} 
									} 
								} 
							} 
						} 
						continue;
					} 
					if (dupeWasFound && 
							allDupeIds.add(Integer.valueOf(((Record)list.get(i)).getDfId())))
						currentDupesToAdd.add(0, list.get(i)); 
					if (currentDupesToAdd.size() > 1) {
						dupeRecordGroups.add(currentDupesToAdd);
						if (!this.chckbxKillFilePriority.isSelected()) {
							this.lblParentDupesAmount.setText(String.valueOf(parentDupesCounter.incrementAndGet()));
							this.lblChildDupesAmount.setText(String.valueOf(childDupesCounter.addAndGet(currentDupesToAdd.size() - 1)));
							this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.addAndGet(currentDupesToAdd.size())));
						} 
					} 
				} 
			} 
			for (List<Record> recordList : dupeRecordGroups) {
				sortRecordsForDedupe(recordList, this.isDfPrioritySet);

				if (!this.chckbxKillFilePriority.isSelected()) {
					for (int i = 0; i < recordList.size(); i++) {
						if (i == 0) {
							((Record)recordList.get(i)).setIsDupe(false);
						} else {
							((Record)recordList.get(i)).setIsDupe(true);
						} 
						
						partitionAllDupeRecordsList.add(recordList.get(i));
					}  
				}
				if (this.chckbxKillFilePriority.isSelected()) {
					ArrayList<Record> tempKillDupeArray = new ArrayList<>();
					boolean hasNonKillPriority = false;
					int i;
					for (i = 0; i < recordList.size(); i++) {
						String tempPriority;
						Record record = recordList.get(i);
						if (this.isDfPrioritySet) {
							tempPriority = UserData.getRecordFieldByName(this.comboBoxPriority.getName(), record);
							//tempPriority = record.getPriority();
						} else {
							tempPriority = record.getDfInData()[this.comboBoxPriority.getSelectedIndex()];
						} 
						if (i == 0) {
							if (this.killPriority != null && tempPriority.equalsIgnoreCase(this.killPriority)) {
								tempKillDupeArray.add(record);
							} else {
								break;
							} 
						} else {
							tempKillDupeArray.add(record);
							if (!tempPriority.equalsIgnoreCase(this.killPriority))
								hasNonKillPriority = true; 
						} 
					} 
					if (tempKillDupeArray.size() > 1 && hasNonKillPriority)
						for (i = 0; i < tempKillDupeArray.size(); i++) {
							String tempPriority;
							Record record = tempKillDupeArray.get(i);
							if (this.isDfPrioritySet) {
								tempPriority = UserData.getRecordFieldByName(this.comboBoxPriority.getName(), record);
								//tempPriority = record.getPriority();
							} else {
								tempPriority = record.getDfInData()[this.comboBoxPriority.getSelectedIndex()];
							} 
							if (tempPriority.equalsIgnoreCase(this.killPriority)) {
								this.lblParentDupesAmount.setText(String.valueOf(parentDupesCounter.incrementAndGet()));
								this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.incrementAndGet()));
								((Record)tempKillDupeArray.get(i)).setIsDupe(false);
							} else {
								this.lblChildDupesAmount.setText(String.valueOf(childDupesCounter.incrementAndGet()));
								this.lblTotalDupesAmount.setText(String.valueOf(totalDupesCounter.incrementAndGet()));
								((Record)tempKillDupeArray.get(i)).setIsDupe(true);
							} 
							partitionAllDupeRecordsList.add(tempKillDupeArray.get(i));
						}  
				} 
			}
		} 
		
	}

	private void preprocessNames(ArrayList<List<Record>> dupeRecordGroups, Pattern prefixPattern) {
		if (dupeRecordGroups.size() > 0) {
			this.lblStatus.setText("Pre-processing names");
			this.progressBar.setIndeterminate(true);
			
			for (List<Record> recordList : dupeRecordGroups) {
				for (int i = 0; i < recordList.size(); i++) {
					Record record = recordList.get(i);
					record.setDupeName1FirstPerson("");
					record.setDupeName1SecondPerson("");
					record.setDupeName1Normalized("");
					record.setDupeName2FirstPerson("");
					record.setDupeName2SecondPerson("");
					record.setDupeName2Normalized("");
					record.setDupeName1("");
					record.setDupeName2("");
					
					if (this.chckbxExactNamesOnly.isSelected()) {
						if (this.isDfName1Set) {
							record.setDupeName1(UserData.getRecordFieldByName(this.comboBoxName1.getSelectedItem().toString(), record).trim());
						} else if (this.isName1Selected) {
							record.setDupeName1(record.getDfInData()[this.comboBoxName1.getSelectedIndex()].trim());
						} 
						if (this.isDfName2Set) {
							record.setDupeName2(UserData.getRecordFieldByName(this.comboBoxName2.getSelectedItem().toString(), record).trim());
						} else if (this.isName2Selected) {
							record.setDupeName2(record.getDfInData()[this.comboBoxName2.getSelectedIndex()].trim());
						} 
					}
					
					if (!this.chckbxExactNamesOnly.isSelected()) {
						if (this.isDfName1Set) {
							String dupeName1 = UserData.getRecordFieldByName(this.comboBoxName1.getSelectedItem().toString(), record);
							record.setDupeName1(dupeName1);
							String[] tempRecNameParts = Common.getNewNormalizedDupeNames(dupeName1, prefixPattern);
							//String[] tempRecNameParts = Common.getNormalizedNames(dupeName1, prefixesFirstPass, prefixesSecondPass);
							record.setDupeName1FirstPerson(tempRecNameParts[0]);
							record.setDupeName1SecondPerson(tempRecNameParts[1]);
							record.setDupeName1Normalized((String.valueOf(tempRecNameParts[0]) + " " + tempRecNameParts[1]).trim());
						} else if (this.isName1Selected) {
							String dupeName1 = record.getDfInData()[this.comboBoxName1.getSelectedIndex()];
							record.setDupeName1(dupeName1);
							String[] tempRecNameParts = Common.getNewNormalizedDupeNames(dupeName1, prefixPattern);
							//String[] tempRecNameParts = Common.getNormalizedNames(dupeName1, prefixesFirstPass, 
							//prefixesSecondPass);
							record.setDupeName1FirstPerson(tempRecNameParts[0]);
							record.setDupeName1SecondPerson(tempRecNameParts[1]);
							record.setDupeName1Normalized((String.valueOf(tempRecNameParts[0]) + " " + tempRecNameParts[1]).trim());
						} 
						if (this.isDfName2Set) {
							String dupeName2 = UserData.getRecordFieldByName(this.comboBoxName2.getSelectedItem().toString(), record);
							record.setDupeName2(dupeName2);
							String[] tempRecNameParts2 = Common.getNewNormalizedDupeNames(dupeName2, prefixPattern);
							//String[] tempRecNameParts2 = Common.getNormalizedNames(dupeName2, prefixesFirstPass, prefixesSecondPass);
							record.setDupeName2FirstPerson(tempRecNameParts2[0]);
							record.setDupeName2SecondPerson(tempRecNameParts2[1]);
							record.setDupeName2Normalized((String.valueOf(tempRecNameParts2[0]) + " " + tempRecNameParts2[1]).trim());
						} else if (this.isName2Selected) {
							String dupeName2 = record.getDfInData()[this.comboBoxName2.getSelectedIndex()];
							record.setDupeName2(dupeName2);
							String[] tempRecName2Parts = Common.getNewNormalizedDupeNames(dupeName2, prefixPattern);
							//String[] tempRecName2Parts = Common.getNormalizedNames(dupeName2, prefixesFirstPass, prefixesSecondPass);
							record.setDupeName2FirstPerson(tempRecName2Parts[0]);
							record.setDupeName2SecondPerson(tempRecName2Parts[1]);
							record.setDupeName2Normalized((String.valueOf(tempRecName2Parts[0]) + " " + tempRecName2Parts[1]).trim());
						} 
					} 
					record.setDupeName1FirstPerson(StringUtils.stripAccents(record.getDupeName1FirstPerson()));
					record.setDupeName1SecondPerson(StringUtils.stripAccents(record.getDupeName1SecondPerson()));
					record.setDupeName1Normalized(StringUtils.stripAccents(record.getDupeName1Normalized()));
					record.setDupeName2FirstPerson(StringUtils.stripAccents(record.getDupeName2FirstPerson()));
					record.setDupeName2SecondPerson(StringUtils.stripAccents(record.getDupeName2SecondPerson()));
					record.setDupeName2Normalized(StringUtils.stripAccents(record.getDupeName2Normalized()));
					record.setDupeName1(StringUtils.stripAccents(record.getDupeName1()));
					record.setDupeName2(StringUtils.stripAccents(record.getDupeName2()));
				} 
			} 
		} 
	}
	
	private void fixFinalDupeGroupIdAndSize(ArrayList<Record> dupes) {
		int dupeGroupId = 1;
		boolean isEnd = false;
		
		for(int i = 0; i < dupes.size(); ++i) {
			Record record = dupes.get(i);
			if(i == 0) {
				record.setDupeGroupId(dupeGroupId);
			} else if(!record.getIsDupe()) {
				if(!isEnd)
					record.setDupeGroupId(dupeGroupId);
				else
					record.setDupeGroupId(++dupeGroupId);
			} else {
				record.setDupeGroupId(dupeGroupId);
				isEnd = true;
			}
		}
		
		// Fix the group size
		HashMap<Integer, Integer> groupSizeMap = new HashMap<>();
		for(int i = 0; i < dupes.size(); ++i) {
			Record record = dupes.get(i);
			int id = record.getDupeGroupId();
			if(!groupSizeMap.containsKey(id))
				groupSizeMap.put(id, 1);
			else
				groupSizeMap.put(id, groupSizeMap.get(id) + 1);
		}
		
		for(int i = 0; i < dupes.size(); ++i) {
			Record record = dupes.get(i);
			int id = record.getDupeGroupId();
			record.setDupeGroupSize(groupSizeMap.get(id));
		}
		
	}

	private void addExtraDupeInfo(ArrayList<List<Record>> dupeRecordGroups) {
		int dupeGroupId = 0;

		HashMap<Integer, Record> recordMap = new HashMap<>();

		for(List<Record> list : dupeRecordGroups)
			for(Record record : list)
				recordMap.put(record.getDfId(), record);

		for(List<Record> dupeGroup : dupeRecordGroups) {
			++dupeGroupId;
			for(int i = 0; i < dupeGroup.size(); ++i) {
				Record record = recordMap.get(dupeGroup.get(i).getDfId());
				record.setDupeGroupId(dupeGroupId);
				record.setDupeGroupSize(dupeGroup.size());
				//record.setDupeInfo("");
			}
		}

	}

	//allDupeRecordsList
	private void addExtraDupeInfoStepTwo(ArrayList<Record> allDupeRecordsList) {
		int dupeGroupId = 0;

		HashMap<Integer, ArrayList<Record>> recordMap = new HashMap<>();

		for(int i = 0; i < allDupeRecordsList.size(); ++i) {

			if(!recordMap.containsKey(allDupeRecordsList.get(i).getDupeGroupId()))
				recordMap.put(allDupeRecordsList.get(i).getDupeGroupId(), new ArrayList<Record>());

			recordMap.get(allDupeRecordsList.get(i).getDupeGroupId()).add(allDupeRecordsList.get(i));
		}

		for (Map.Entry<Integer, ArrayList<Record>> entry : recordMap.entrySet()) {
			++dupeGroupId;
			for(int j = 0; j < entry.getValue().size(); ++j) {
				Record record = entry.getValue().get(j);
				record.setDupeGroupId(dupeGroupId);
				record.setDupeGroupSize(entry.getValue().size());
				//System.out.println(record.toString());
			}
		}

	}


	private class ComboBoxMappingHandler implements ActionListener {
		private ComboBoxMappingHandler() {}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() instanceof JComboBox) {
					@SuppressWarnings("unchecked")
					JComboBox<String> userComboBox = (JComboBox<String>)e.getSource();

					if(userComboBox.getSelectedIndex() > -1) {
						DedupeDialog2.this.addToMappedFields(userComboBox);

						if (e.getSource() == DedupeDialog2.this.comboBoxName1 || e.getSource() == DedupeDialog2.this.comboBoxName2)
							DedupeDialog2.this.btnRun.setEnabled(true); 
					}
				} 
			} catch (Exception err) {
				UiController.handle(err);
			} 
		}
	}

	private class ComboBoxPriorityHandler implements ActionListener {
		private ComboBoxPriorityHandler() {}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() instanceof JComboBox) {
					@SuppressWarnings("unchecked")
					JComboBox<String> userComboBox = (JComboBox<String>)e.getSource();

					if (userComboBox.getSelectedIndex() > -1) {
						DedupeDialog2.this.lblPrioritySorting.setEnabled(true);
						DedupeDialog2.this.rdbtnAsc.setEnabled(true);
						DedupeDialog2.this.rdbtnDesc.setEnabled(true);
						DedupeDialog2.this.lblPriorityFormat.setEnabled(true);
						DedupeDialog2.this.comboBoxPriorityFormat.setEnabled(true);

						if (!DedupeDialog2.this.rdbtnDedupeName.isSelected())
							DedupeDialog2.this.btnRun.setEnabled(true); 
						if (DedupeDialog2.this.rdbtnDedupeName.isSelected() && (DedupeDialog2.this.comboBoxName1.getSelectedIndex() > -1 || DedupeDialog2.this.comboBoxName2.getSelectedIndex() > -1))
							DedupeDialog2.this.btnRun.setEnabled(true); 
						if (DedupeDialog2.this.comboBoxPriorityFormat.getSelectedItem().toString().equalsIgnoreCase(DedupeDialog2.PRIORITY_FORMATS.DATE.toString())) {
							DedupeDialog2.this.lblPriorityDateFormat.setEnabled(true);
							DedupeDialog2.this.textFieldPriorityDateFormat.setEnabled(true);
							DedupeDialog2.this.btnPriorityDateFormatHelp.setEnabled(true);	
						}


					} else if (userComboBox.getSelectedIndex() == -1) {
						DedupeDialog2.this.lblPrioritySorting.setEnabled(false);
						DedupeDialog2.this.rdbtnAsc.setEnabled(false);
						DedupeDialog2.this.rdbtnDesc.setEnabled(false);
						DedupeDialog2.this.lblPriorityFormat.setEnabled(false);
						DedupeDialog2.this.comboBoxPriorityFormat.setEnabled(false);
						DedupeDialog2.this.btnRun.setEnabled(false); 
						DedupeDialog2.this.btnRun.setEnabled(false); 
						DedupeDialog2.this.lblPriorityDateFormat.setEnabled(false);
						DedupeDialog2.this.textFieldPriorityDateFormat.setEnabled(false);
						DedupeDialog2.this.btnPriorityDateFormatHelp.setEnabled(false);			
					}
				} 
			} catch (Exception err) {
				UiController.handle(err);
			} 
		}
	}

	private class DedupeRadioButtonHandler implements ActionListener {
		private DedupeRadioButtonHandler() {}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == DedupeDialog2.this.rdbtnDedupeName)
					if (DedupeDialog2.this.comboBoxName1.getSelectedIndex() <= -1 && DedupeDialog2.this.comboBoxName2.getSelectedIndex() <= -1) {
						DedupeDialog2.this.btnRun.setEnabled(false);
					} else {
						DedupeDialog2.this.btnRun.setEnabled(true);
					}
				
				if (e.getSource() == DedupeDialog2.this.rdbtnDedupeAddress || e.getSource() == DedupeDialog2.this.rdbtnDedupePC) {
					DedupeDialog2.this.btnRun.setEnabled(true);
					DedupeDialog2.this.chckbxConvertPcToFsa.setEnabled(true);
				} else {
					DedupeDialog2.this.chckbxConvertPcToFsa.setEnabled(false);
					DedupeDialog2.this.chckbxConvertPcToFsa.setSelected(false);
				}
				
				if (e.getSource() == DedupeDialog2.this.rdbtnDedupeAddress) {
					DedupeDialog2.this.chckbxIgnoreApt.setEnabled(true);
				} else {
					DedupeDialog2.this.chckbxIgnoreApt.setEnabled(false);
					DedupeDialog2.this.chckbxIgnoreApt.setSelected(false);
				} 
			} catch (Exception err) {
				UiController.handle(err);
			} 
		}
	}

	private class DedupeChckbxExactNamesOnlyHandler implements ActionListener {
		private DedupeChckbxExactNamesOnlyHandler() {}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == DedupeDialog2.this.chckbxExactNamesOnly) {
					if (DedupeDialog2.this.chckbxExactNamesOnly.isSelected()) {
						DedupeDialog2.this.textFieldNameSimilarityPercentage.setEnabled(false);
						DedupeDialog2.this.lblPercentageForNameSimilarity.setEnabled(false);
						DedupeDialog2.this.labelPercentage.setEnabled(false);
						DedupeDialog2.this.chckbxMatchOnNameSimilarityOnly.setEnabled(false);
					} 
					if (!DedupeDialog2.this.chckbxExactNamesOnly.isSelected()) {
						DedupeDialog2.this.textFieldNameSimilarityPercentage.setEnabled(true);
						DedupeDialog2.this.lblPercentageForNameSimilarity.setEnabled(true);
						DedupeDialog2.this.labelPercentage.setEnabled(true);
						DedupeDialog2.this.chckbxMatchOnNameSimilarityOnly.setEnabled(true);
					} 
				} 
			} catch (Exception err) {
				UiController.handle(err);
			} 
		}
	}

	private class DedupeChckbxMatchOnNameSimilarityOnlyHandler implements ActionListener {
		private DedupeChckbxMatchOnNameSimilarityOnlyHandler() {}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == DedupeDialog2.this.chckbxMatchOnNameSimilarityOnly) {
					if (DedupeDialog2.this.chckbxMatchOnNameSimilarityOnly.isSelected())
						DedupeDialog2.this.chckbxExactNamesOnly.setEnabled(false); 
					if (!DedupeDialog2.this.chckbxMatchOnNameSimilarityOnly.isSelected())
						DedupeDialog2.this.chckbxExactNamesOnly.setEnabled(true); 
				} 
			} catch (Exception err) {
				UiController.handle(err);
			} 
		}
	}

	private class DedupeChkbxKillFileHandler implements ActionListener {
		private DedupeChkbxKillFileHandler() {}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == DedupeDialog2.this.chckbxKillFilePriority)
					if (DedupeDialog2.this.chckbxKillFilePriority.isSelected())
						if (DedupeDialog2.this.comboBoxPriority.getSelectedIndex() == -1) {
							DedupeDialog2.this.btnRun.setEnabled(false);
						} else if (!DedupeDialog2.this.rdbtnDedupeName.isSelected()) {
							DedupeDialog2.this.btnRun.setEnabled(true);
						} else if (DedupeDialog2.this.comboBoxName1.getSelectedIndex() > -1 || DedupeDialog2.this.comboBoxName2.getSelectedIndex() > -1) {
							DedupeDialog2.this.btnRun.setEnabled(true);
						}   
				if (!DedupeDialog2.this.chckbxKillFilePriority.isSelected())
					if (!DedupeDialog2.this.rdbtnDedupeName.isSelected()) {
						DedupeDialog2.this.btnRun.setEnabled(true);
					} else if (DedupeDialog2.this.comboBoxName1.getSelectedIndex() > -1 || DedupeDialog2.this.comboBoxName2.getSelectedIndex() > -1) {
						DedupeDialog2.this.btnRun.setEnabled(true);
					}  
			} catch (Exception err) {
				UiController.handle(err);
			} 
		}
	}

	private class ComboBoxPriorityFormatHandler implements ActionListener {
		private ComboBoxPriorityFormatHandler() {}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == DedupeDialog2.this.comboBoxPriorityFormat)
					if (DedupeDialog2.this.comboBoxPriorityFormat.getSelectedItem().toString().equalsIgnoreCase(DedupeDialog2.PRIORITY_FORMATS.DATE.toString())) {
						DedupeDialog2.this.lblPriorityDateFormat.setEnabled(true);
						DedupeDialog2.this.textFieldPriorityDateFormat.setEnabled(true);
						DedupeDialog2.this.btnPriorityDateFormatHelp.setEnabled(true);
					} else {
						DedupeDialog2.this.lblPriorityDateFormat.setEnabled(false);
						DedupeDialog2.this.textFieldPriorityDateFormat.setEnabled(false);
						DedupeDialog2.this.btnPriorityDateFormatHelp.setEnabled(false);
					}  
			} catch (Exception err) {
				UiController.handle(err);
			} 
		}
	}

	private class ExportButtonHandler implements ActionListener {
		private ExportButtonHandler() {}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == DedupeDialog2.this.btnExport && 
						DedupeDialog2.this.allDupeRecordsList.size() > 0) {
					DedupeDialog2.this.dedupeExportDialog = new DedupeExportDialog(DedupeDialog2.this, DedupeDialog2.this.allDupeRecordsList);
					DedupeDialog2.this.dedupeExportDialog.setVisible(true);
				} 
			} catch (Exception err) {
				UiController.handle(err);
			} 
		}
	}

	private class PriorityDateFormatHelpButtonHandler implements ActionListener {
		private PriorityDateFormatHelpButtonHandler() {}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == DedupeDialog2.this.btnPriorityDateFormatHelp) {
				JEditorPane editorPane = new JEditorPane("text/html", 
						"<html><body><pre><b>To avoid errors please double check that your format is correct</b>\r\n\r\n  Symbol  Meaning                     Presentation      Examples\r\n  ------  -------                     ------------      -------\r\n   y       year-of-era                 year              2004; 04\r\n   M       month-of-year               number/text       7; 07; Jul; July; J\r\n   d       day-of-month                number            10\r\n\r\n\r\n  <b>Examples</b>\r\n\r\n  Input                               Format\r\n  -----                               ------\r\n   1/12/2020                           d/M/yyyy\r\n   01/12/2020                          dd/MM/yyyy\r\n   1-Dec-20                            d-MMM-yy </pre></body></html>");
				editorPane.setEditable(false);
				JOptionPane.showMessageDialog(DedupeDialog2.this, editorPane, "Date formats", 1);
			} 
		}
	}

	private class DedupeOptionHelpButtonHandler implements ActionListener {
		private DedupeOptionHelpButtonHandler() {}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == DedupeDialog2.this.btnDedupeOptionHelp) {
				JEditorPane editorPane = new JEditorPane("text/html", 
						"<html><body><pre>Option                  Meaning                                                 Example            \r\n------                  -------                                                 -------            \r\n                                                                                                   \r\n Address + PC            This will find records with the same PC and adresss.    Ex. 123 St V0V 0V0\r\n                         *Note names can still be included with this option.         123 St V0V 0V0\r\n                                                                                                   \r\n PC                      This will find records with the same PC.                Ex. V0V 0V0       \r\n                         *Note names can still be included with this option.         V0V 0V0       \r\n                                                                                                   \r\n Name                    This will find records with the same or similar name.   Ex. Sean          \r\n                         *Note a name field must be selected before running.         Shawn         \r\n </pre></body></html>");
				editorPane.setEditable(false);
				JOptionPane.showMessageDialog(DedupeDialog2.this, editorPane, "Find dedupe options", 1);
			} 
		}
	}

	private class DeleteButtonHandler implements ActionListener {
		private DeleteButtonHandler() {}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == DedupeDialog2.this.btnDelete && 
						DedupeDialog2.this.allDupeRecordsList.size() > 0) {
					int buttonPressed = JOptionPane.showConfirmDialog(DedupeDialog2.this, "Would you like to export before deleting?", "Delete Duplicates", 
							1, 3);
					if (buttonPressed == 0) {
						DedupeDialog2.this.dedupeExportDialog = new DedupeExportDialog(DedupeDialog2.this, DedupeDialog2.this.allDupeRecordsList);
						DedupeDialog2.this.dedupeExportDialog.setVisible(true);
					} 
					if (buttonPressed == 1 || (DedupeDialog2.this.dedupeExportDialog != null && DedupeDialog2.this.dedupeExportDialog.getIsNextPressed())) {
						Runnable runner = new Runnable() {
							public void run() {
								try {
									disableUI();
									progressBar.setMinimum(0);
									progressBar.setValue(0);
									int numOfDupesToRemove = 0;
									int recordRemoved = 0;
									for (Record record : allDupeRecordsList) {
										if (record.getIsDupe())
											numOfDupesToRemove++; 
									} 
									progressBar.setMaximum(numOfDupesToRemove);
									for (int i = 0; i < allDupeRecordsList.size(); i++) {
										for (int j = UiController.getUserData().getRecordList().size() - 1; j >= 0; j--) {
											if (((Record)allDupeRecordsList.get(i)).getIsDupe() && (
													(Record)allDupeRecordsList.get(i)).getDfId() == ((Record)UiController.getUserData().getRecordList().get(j)).getDfId()) {
												lblStatus.setText(String.format("Removing record %d / %d", new Object[] { Integer.valueOf(++recordRemoved), Integer.valueOf(numOfDupesToRemove) }));
												UiController.getUserData().getRecordList().remove(j);
												progressBar.setValue(progressBar.getValue() + 1);
											} 
										} 
									}
									UiController.displayMessage("Success", String.format("%d records were deleted.", numOfDupesToRemove), JOptionPane.PLAIN_MESSAGE);
									//JOptionPane.showMessageDialog(dedupeViewDialog, "Duplicate records successfully deleted.", "Success", 1);
								} catch (Exception err) {
									UiController.handle(err);
								} finally {
									lblStatus.setText("Ready for deduplication");
									progressBar.setValue(0);
									allDupeRecordsList.clear();
									lblTotalRecordsAmount.setText("0");
									lblTotalDupesAmount.setText("0");
									lblParentDupesAmount.setText("0");
									lblChildDupesAmount.setText("0");
									lblElapsedTimeAmount.setText("0 sec");
									enableUI();
								} 
							}
						};
						Thread t = new Thread(runner, "Code Executer");
						t.start();
					} 
				} 
			} catch (Exception err) {
				UiController.handle(err);
			} 
		}
	}

	private class WindowClosingHandler extends WindowAdapter {

		private WindowClosingHandler() {}

		public void windowClosing(WindowEvent e) {
			DedupeDialog2.this.isDialogClosed = true;

			if(Validators.isNumber(textFieldNameSimilarityPercentage.getText()))
				UserPrefs.setLastUsedDedupeMatchPercentage(textFieldNameSimilarityPercentage.getText());
		}
	}


	public void disableUI() {
		lblPercentageForNameSimilarity.setEnabled(false);
		labelPercentage.setEnabled(false);
		textFieldNameSimilarityPercentage.setEnabled(false);
		chckbxExactNamesOnly.setEnabled(false);
		chckbxIncludeBlankNames.setEnabled(false);
		chckbxMatchOnNameSimilarityOnly.setEnabled(false);
		lblName.setEnabled(false);
		lblIfName1Blank.setEnabled(false);
		lblName2.setEnabled(false);
		lblIfName2Blank.setEnabled(false);
		comboBoxName1.setEnabled(false);
		comboBoxName2.setEnabled(false);
		comboBoxIfName1Blank.setEnabled(false);
		comboBoxIfName2Blank.setEnabled(false);
		chckbxKillFilePriority.setEnabled(false);
		comboBoxPriority.setEnabled(false);
		rdbtnAsc.setEnabled(false);
		rdbtnDesc.setEnabled(false);
		comboBoxPriorityFormat.setEnabled(false);
		textFieldPriorityDateFormat.setEnabled(false);
		btnPriorityDateFormatHelp.setEnabled(false);
		lblPriorityValue.setEnabled(false);
		lblPrioritySorting.setEnabled(false);
		lblPriorityFormat.setEnabled(false);
		rdbtnDedupeAddress.setEnabled(false);
		rdbtnDedupePC.setEnabled(false);
		rdbtnDedupeName.setEnabled(false);
		btnDedupeOptionHelp.setEnabled(false);
		chckbxIgnoreApt.setEnabled(false);
		chckbxConvertPcToFsa.setEnabled(false);
		btnRun.setEnabled(false);
		btnView.setEnabled(false);
		btnExport.setEnabled(false);
		btnDelete.setEnabled(false);

	}

	public void enableUI() {
		if (chckbxExactNamesOnly.isSelected()) {
			textFieldNameSimilarityPercentage.setEnabled(false);
			chckbxMatchOnNameSimilarityOnly.setEnabled(false);
		} else {
			textFieldNameSimilarityPercentage.setEnabled(true);
			chckbxMatchOnNameSimilarityOnly.setEnabled(true);
		} 
		if (chckbxMatchOnNameSimilarityOnly.isSelected()) {
			chckbxExactNamesOnly.setEnabled(false);
		} else {
			chckbxExactNamesOnly.setEnabled(true);
		} 
		lblPercentageForNameSimilarity.setEnabled(true);
		labelPercentage.setEnabled(true);
		chckbxIncludeBlankNames.setEnabled(true);
		lblName.setEnabled(true);
		lblIfName1Blank.setEnabled(true);
		lblName2.setEnabled(true);
		lblIfName2Blank.setEnabled(true);
		comboBoxName1.setEnabled(true);
		comboBoxName2.setEnabled(true);
		comboBoxIfName1Blank.setEnabled(true);
		comboBoxIfName2Blank.setEnabled(true);
		chckbxKillFilePriority.setEnabled(true);
		comboBoxPriority.setEnabled(true);
		lblPriorityValue.setEnabled(true);
		if (comboBoxPriority.getSelectedIndex() > -1) {
			lblPrioritySorting.setEnabled(true);
			lblPriorityFormat.setEnabled(true);
			rdbtnAsc.setEnabled(true);
			rdbtnDesc.setEnabled(true);
			comboBoxPriorityFormat.setEnabled(true);
			if (comboBoxPriorityFormat.getSelectedItem().toString().equalsIgnoreCase(PRIORITY_FORMATS.DATE.toString())) {
				textFieldPriorityDateFormat.setEnabled(true);
				btnPriorityDateFormatHelp.setEnabled(true);
			} 
		} 
		rdbtnDedupeAddress.setEnabled(true);
		rdbtnDedupePC.setEnabled(true);
		rdbtnDedupeName.setEnabled(true);
		btnDedupeOptionHelp.setEnabled(true);
		if (rdbtnDedupeAddress.isSelected())
			chckbxIgnoreApt.setEnabled(true); 
		if (rdbtnDedupeAddress.isSelected() || rdbtnDedupePC.isSelected())
			chckbxConvertPcToFsa.setEnabled(true); 
		btnRun.setEnabled(true);
		btnView.setEnabled(true);
		btnExport.setEnabled(true);
		btnDelete.setEnabled(true);
	}

	public void sortRecordsForDedupe(List<Record> recordList, boolean isDfPrioritySet) throws Exception {
		if (this.isPrioritySelected) {
			if (this.rdbtnAsc.isSelected())
				if (this.comboBoxPriorityFormat.getSelectedItem().toString().equalsIgnoreCase(PRIORITY_FORMATS.TEXT.toString())) {
					if (!isDfPrioritySet) {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByInFieldAsc(this.comboBoxPriority.getSelectedIndex()));
					} else {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByFieldAsc(this.comboBoxPriority.getSelectedItem().toString()));
					} 
				} else if (this.comboBoxPriorityFormat.getSelectedItem().toString().equalsIgnoreCase(PRIORITY_FORMATS.NUMBER.toString())) {
					if (!isDfPrioritySet) {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByInFieldAscAsNumber(this.comboBoxPriority.getSelectedIndex()));
					} else {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByFieldAscAsNumber(this.comboBoxPriority.getSelectedItem().toString()));
					} 
				} else if (this.comboBoxPriorityFormat.getSelectedItem().toString().equalsIgnoreCase(PRIORITY_FORMATS.DATE.toString())) {
					DateTimeFormatter dateTimeFormatter = null;
					try {
						dateTimeFormatter = DateTimeFormatter.ofPattern(this.textFieldPriorityDateFormat.getText());
					} catch (Exception e) {
						throw new Exception("Invalid date format entered");
					} 
					if (!isDfPrioritySet) {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByInFieldAscAsDate(this.comboBoxPriority.getSelectedIndex(), dateTimeFormatter));
					} else {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByFieldAscAsDate(this.comboBoxPriority.getSelectedItem().toString(), dateTimeFormatter));
					} 
				}  
			if (this.rdbtnDesc.isSelected())
				if (this.comboBoxPriorityFormat.getSelectedItem().toString().equalsIgnoreCase(PRIORITY_FORMATS.TEXT.toString())) {
					if (!isDfPrioritySet) {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByInFieldDesc(this.comboBoxPriority.getSelectedIndex()));
					} else {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByFieldDesc(this.comboBoxPriority.getSelectedItem().toString()));
					} 
				} else if (this.comboBoxPriorityFormat.getSelectedItem().toString().equalsIgnoreCase(PRIORITY_FORMATS.NUMBER.toString())) {
					if (!isDfPrioritySet) {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByInFieldDescAsNumber(this.comboBoxPriority.getSelectedIndex()));
					} else {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByFieldDescAsNumber(this.comboBoxPriority.getSelectedItem().toString()));
					} 
				} else if (this.comboBoxPriorityFormat.getSelectedItem().toString().equalsIgnoreCase(PRIORITY_FORMATS.DATE.toString())) {
					DateTimeFormatter dateTimeFormatter = null;
					try {
						dateTimeFormatter = DateTimeFormatter.ofPattern(this.textFieldPriorityDateFormat.getText());
					} catch (Exception e) {
						throw new Exception("Invalid date format entered");
					} 
					if (!isDfPrioritySet) {
						Collections.sort(recordList, (Comparator<? super Record>)new RecordSorters.CompareByInFieldDescAsDate(this.comboBoxPriority.getSelectedIndex(), dateTimeFormatter));
					} else {
						Collections.sort(recordList, 
								(Comparator<? super Record>)new RecordSorters.CompareByFieldDescAsDate(this.comboBoxPriority.getSelectedItem().toString(), dateTimeFormatter));
					} 
				}  
		} 
	}

	public boolean isPoBoxRRSameAsAddress(Record record1, Record record2) {
		boolean isSame = false;
		if (record1.getDupeAdd1().equalsIgnoreCase(record2.getDupeAdd1()) || record1.getDupeAdd1().equalsIgnoreCase(record2.getDupeAdd2()) || 
				record1.getDupeAdd2().equalsIgnoreCase(record2.getDupeAdd1()) || record1.getDupeAdd2().equalsIgnoreCase(record2.getDupeAdd2())) {
			if (!record1.getDupePOBox().isEmpty())
				if (record1.getDupePOBox().equalsIgnoreCase(record2.getDupeAdd1())) {
					isSame = true;
				} else if (record1.getDupePOBox().equalsIgnoreCase(record2.getDupeAdd2())) {
					isSame = true;
				}  
			if (!record1.getDupePOBoxExtra().isEmpty())
				if (record1.getDupePOBoxExtra().equalsIgnoreCase(record2.getDupeAdd1())) {
					isSame = true;
				} else if (record1.getDupePOBoxExtra().equalsIgnoreCase(record2.getDupeAdd2())) {
					isSame = true;
				}  
			if (!record1.getDupeRR().isEmpty())
				if (record1.getDupeRR().equalsIgnoreCase(record2.getDupeAdd1())) {
					isSame = true;
				} else if (record1.getDupeRR().equalsIgnoreCase(record2.getDupeAdd2())) {
					isSame = true;
				}  
		} 
		return isSame;
	}

	private class ComboBoxDeleteHandler implements KeyListener {
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == 127 || key == 8) {
				if(e.getSource() instanceof JComboBox) {
					@SuppressWarnings("rawtypes")
					JComboBox comboBox = (JComboBox) e.getSource();

					if (comboBox == DedupeDialog2.this.comboBoxName1 || comboBox == DedupeDialog2.this.comboBoxName2)
						if (DedupeDialog2.this.rdbtnDedupeName.isSelected())
							DedupeDialog2.this.btnRun.setEnabled(false);


					comboBox.setSelectedIndex(-1);
					mappedFields.remove(comboBox); // remove the field from mapping
				}
			}
		}

		public void keyTyped(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {}
	}

	public void reset() {
		this.killPriority = null;
		this.isName1Selected = false;
		this.isName2Selected = false;
		this.isIfName1BlankSelected = false;
		this.isIfName2BlankSelected = false;
		this.isDfName1Set = false;
		this.isDfName2Set = false;
		this.isDfIfName1BlankSelected = false;
		this.isDfIfName2BlankSelected = false;
		this.isPrioritySelected = false;
		this.isDfPrioritySet = false;
	}

	public JLabel getLblTotalDupesAmount() {
		return this.lblTotalDupesAmount;
	}

	public JLabel getLblParentDupesAmount() {
		return this.lblParentDupesAmount;
	}

	public JLabel getLblChildDupesAmount() {
		return this.lblChildDupesAmount;
	}

	public JComboBox<String> getComboBoxName1() {
		return this.comboBoxName1;
	}

	public JComboBox<String> getComboBoxName2() {
		return this.comboBoxName2;
	}
	
	public JCheckBox getChckbxKillFilePriority() {
		return chckbxKillFilePriority;
	}

	public boolean isDfPrioritySet() {
		return isDfPrioritySet;
	}

	public String getKillPriority() {
		return killPriority;
	}

	public JComboBox<String> getComboBoxPriority() {
		return this.comboBoxPriority;
	}

	public JComboBox<String> getComboBoxIfName1Blank() {
		return this.comboBoxIfName1Blank;
	}

	public JComboBox<String> getComboBoxIfName2Blank() {
		return this.comboBoxIfName2Blank;
	}

	public JComboBox<String> getComboBoxPriorityFormat() {
		return this.comboBoxPriorityFormat;
	}

	public ArrayList<Record> getAllDupeRecordsList() {
		return allDupeRecordsList;
	}

	public JCheckBox getChckbxExactNamesOnly() {
		return chckbxExactNamesOnly;
	}

	public JButton getBtnRun() {
		return btnRun;
	}

	public JCheckBox getChckbxMatchOnNameSimilarityOnly() {
		return chckbxMatchOnNameSimilarityOnly;
	}

	public void setChckbxMatchOnNameSimilarityOnly(JCheckBox chckbxMatchOnNameSimilarityOnly) {
		this.chckbxMatchOnNameSimilarityOnly = chckbxMatchOnNameSimilarityOnly;
	}

	public JTextField getTextFieldNameSimilarityPercentage() {
		return textFieldNameSimilarityPercentage;
	}

	public void setTextFieldNameSimilarityPercentage(JTextField textFieldNameSimilarityPercentage) {
		this.textFieldNameSimilarityPercentage = textFieldNameSimilarityPercentage;
	}




}
