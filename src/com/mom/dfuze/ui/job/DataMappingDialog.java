/**
 * Project: Dfuze
 * File: JobUserDataMappingDialog.java
 * Date: Mar 17, 2020
 * Time: 8:48:18 PM
 */
package com.mom.dfuze.ui.job;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.JobPref;
import com.mom.dfuze.data.JobPrefRemember;
import com.mom.dfuze.data.Theme;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.jobs.utility.Rental;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserInputDialog;

import net.miginfocom.swing.MigLayout;
import javax.swing.JCheckBox;

/**
 * DataMappingDialog allows users to map fields from input data to required fields needs to process a Job and preview the elements in the data
 * This Class will dynamically create the fields to display
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class DataMappingDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private int currentColToAdd = 2;
	private int currentRowToAdd = 2;
	private final int MAX_ROWS = 15;
	private final int MAX_COLUMNS = 6;

	private boolean isLoadSuccessfullyPressed = false;

	ArrayList<JComboBox<String>> comboBoxes = new ArrayList<>();
	HashMap<String,JLabel> labels = new HashMap<>();
	HashSet<String> buildingHeadersDupesCheck = new HashSet<String>();

	private JButton btnLoad;
	private JSeparator separator;

	private JScrollBar scrollBar;
	private JLabel lblPreviewLabel;

	private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXXXXXXXXX";

	private Map<JComboBox<String>, String> mappedFields = new HashMap<>();
	private JList<String> list;
	private JScrollPane scrollPane;

	boolean canMapToFields = true;
	private JLabel lblPreviewDescription;
	private JLabel lblRequiredFieldsLabel;
	private JCheckBox chckbxRememberFields;
	private boolean initialJprState;
	private JobPrefRemember jpr;
	private JButton btnMergeSelectedFields;
	private ArrayList<String[]> dataPreview;
	private JButton btnResetMappedFields;
	
	public final Pattern ID_PATTERN = Pattern.compile("((^|\\s+|_)id(\\s+|$|_))|vip|loyalty|contactcode|customer(\\s+|_)?(id|no)", Pattern.CASE_INSENSITIVE); //Id
	public final Pattern LIST_NUMBER_PATTERN = Pattern.compile("listOrder|seq|listNumber", Pattern.CASE_INSENSITIVE); //ListNumber
	public final Pattern STORE_ID_PATTERN = Pattern.compile("store.*id|cliniccode", Pattern.CASE_INSENSITIVE); //Id
	public final Pattern LANGUAGE_PATTERN = Pattern.compile("lang", Pattern.CASE_INSENSITIVE); //Language
	public final Pattern BARCODE_PATTERN = Pattern.compile("barcode|scan", Pattern.CASE_INSENSITIVE); //Barcode
	public final Pattern RECORD_TYPE_PATTERN = Pattern.compile("key|indicator|contact.*type", Pattern.CASE_INSENSITIVE); //Record type
	public final Pattern LATITUDE_PATTERN = Pattern.compile("geolat|lat(\\s+|$)", Pattern.CASE_INSENSITIVE); //Latitude
	public final Pattern LONGITUDE_PATTERN = Pattern.compile("geolong|long(\\s+|$)", Pattern.CASE_INSENSITIVE); //Longitude
	public final Pattern NCOA_PATTERN = Pattern.compile("ncoa", Pattern.CASE_INSENSITIVE); // ncoa
	public final Pattern DNM_PATTERN = Pattern.compile("dnm", Pattern.CASE_INSENSITIVE); // do not mail
	public final Pattern DECEASED_PATTERN = Pattern.compile("mvup|deceased", Pattern.CASE_INSENSITIVE); //deceased
	public final Pattern SEGMENT_FILTER1_PATTERN = Pattern.compile("seg.*fil.*1", Pattern.CASE_INSENSITIVE); //segment filter 1
	public final Pattern SEGMENT_FILTER2_PATTERN = Pattern.compile("seg.*fil.*2", Pattern.CASE_INSENSITIVE); //segment filter 2
	
	public final Pattern COMPANY_SALUTATION_PATTERN = Pattern.compile("(cn|cmp|comp|org|biz|bus|con|assoc).*sal", Pattern.CASE_INSENSITIVE); //Company salutation
	public final Pattern COMPANY_CONTACT_PATTERN = Pattern.compile("(cn|cmp|comp|org|biz|bus|con|assoc).*(addressee)", Pattern.CASE_INSENSITIVE); //Company contact
	
	public final Pattern LAST_GIFT_PATTERN = Pattern.compile("(?<!date.*)(l(?!rg|argest|iest)|prev|latest).*(?<!mon|mn.*)(gift|gf|don|amt|amount)(?!(\\s+|_)?(dat))", Pattern.CASE_INSENSITIVE); //Last gift
	public final Pattern LAST_GIFT_DATE_PATTERN = Pattern.compile("(l(?!rg|argest|iest)|prev|latest).*(?<!mon|mn.*)(gift|gf|don).*(dat)|(dat).*(l(?!rg|argest)|prev).*(?<!mon|mn.*)(gift|gf|don)", Pattern.CASE_INSENSITIVE); //Last gift date
	public final Pattern FIRST_GIFT_PATTERN = Pattern.compile("(?<!date.*)(f|orig|earliest).*(?<!mon|mn.*)(gift|gf|don|amt|amount)(?!(\\s+|_)?(dat))", Pattern.CASE_INSENSITIVE); //First gift
	public final Pattern FIRST_GIFT_DATE_PATTERN = Pattern.compile("(f|orig|earliest).*(?<!mon|mn.*)(gift|gf|don).*(dat)|(dat).*(f|orig).*(?<!mon|mn.*)(gift|gf|don)", Pattern.CASE_INSENSITIVE); //First gift date
	public final Pattern LARGEST_GIFT_PATTERN = Pattern.compile("(?<!date.*)(lrg|largest|max|top|big).*(?<!mon|mn.*)(gift|gf|don|amt|amount)(?!(\\s+|_)?(dat))", Pattern.CASE_INSENSITIVE); //Largest gift
	public final Pattern LARGEST_GIFT_DATE_PATTERN = Pattern.compile("(lrg|largest|max|top|big).*(?<!mon|mn.*)(gift|gf|don).*(dat)|(dat).*(lrg|largest|max|top).*(?<!mon|mn.*)(gift|gf|don)", Pattern.CASE_INSENSITIVE); //Largest gift date
	public final Pattern SMALLEST_GIFT_PATTERN = Pattern.compile("(?<!date.*)(sml|small|low|min|tiny).*(?<!mon|mn.*)(gift|gf|don|amt|amount)(?!(\\s+|_)?(dat))", Pattern.CASE_INSENSITIVE); //First gift
	public final Pattern SMALLEST_GIFT_DATE_PATTERN = Pattern.compile("(sml|small|low|min|tiny).*(?<!mon|mn.*)(gift|gf|don).*(dat)|(dat).*(sml|small|low|min).*(?<!mon|mn.*)(gift|gf|don)", Pattern.CASE_INSENSITIVE); //First gift date
	public final Pattern TOTAL_GIFTS_PATTERN = Pattern.compile("(ttl|total|count|cnt|sum|cum|life).*(?<!m.*)(gift|gf|don|amt|amount)|(gift|gf|don|amt|amount).*(?<!m.*)(ttl|total|count|cnt|sum|cum|life)", Pattern.CASE_INSENSITIVE); //Total gifts
	
	public final Pattern LAST_MONTHLY_GIFT_PATTERN = Pattern.compile("(?<!date.*)(l(?!rg|argest|iest)|prev|latest).*(m).*(gift|gf|don|amt|amount)(?!(\\s+|_)?(dat))", Pattern.CASE_INSENSITIVE); //Last Monthly gift
	public final Pattern LAST_MONTHLY_GIFT_DATE_PATTERN = Pattern.compile("(l(?!rg|argest|iest)|prev|latest).*(m).*(gift|gf|don).*(dat)|(dat).*(l(?!rg|argest)|prev).*(m).*(gift|gf|don)", Pattern.CASE_INSENSITIVE); //Last Monthly gift date
	public final Pattern FIRST_MONTHLY_GIFT_PATTERN = Pattern.compile("(?<!date.*)(f|orig|earliest).*(m).*(gift|gf|don|amt|amount)(?!(\\s+|_)?(dat))", Pattern.CASE_INSENSITIVE); //First Monthly gift
	public final Pattern FIRST_MONTHLY_GIFT_DATE_PATTERN = Pattern.compile("(f|orig|earliest).*(m).*(gift|gf|don).*(dat)|(dat).*(f|orig).*(m).*(gift|gf|don)", Pattern.CASE_INSENSITIVE); //First Monthly gift date
	public final Pattern LARGEST_MONTHLY_GIFT_PATTERN = Pattern.compile("(?<!date.*)(lrg|largest|max|top|big).*(m).*(gift|gf|don|amt|amount)(?!(\\s+|_)?(dat))", Pattern.CASE_INSENSITIVE); //Largest Monthly gift
	public final Pattern LARGEST_MONTHLY_GIFT_DATE_PATTERN = Pattern.compile("(lrg|largest|max|top|big).*(m).*(gift|gf|don).*(dat)|(dat).*(lrg|largest|max|top).*(m).*(gift|gf|don)", Pattern.CASE_INSENSITIVE); //Largest Monthly gift date
	public final Pattern SMALLEST_MONTHLY_GIFT_PATTERN = Pattern.compile("(?<!date.*)(sml|small|low|min|tiny).*(m).*(gift|gf|don|amt|amount)(?!(\\s+|_)?(dat))", Pattern.CASE_INSENSITIVE); //First Monthly gift
	public final Pattern SMALLEST_MONTHLY_GIFT_DATE_PATTERN = Pattern.compile("(sml|small|low|min|tiny).*(m).*(gift|gf|don).*(dat)|(dat).*(sml|small|low|min).*(m).*(gift|gf|don)", Pattern.CASE_INSENSITIVE); //First Monthly gift date
	public final Pattern TOTAL_MONTHLY_GIFTS_PATTERN = Pattern.compile("(ttl|total|count|cnt|sum|cum|life).*(m).*(gift|gf|don|amt|amount)|(gift|gf|don|amt|amount).*(m).*(ttl|total|count|cnt|sum|cum|life)", Pattern.CASE_INSENSITIVE); //Total gifts


	public DataMappingDialog(JFrame frame, Job job, JobPrefRemember newJpr) throws ApplicationException {
		super(frame, true);
		
		jpr = newJpr;
		initialJprState = jpr.isRemember();
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(890, 678);
		setResizable(false);

		//setTitle("Data mapper");

		getContentPane().setLayout(new BorderLayout());

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		btnLoad = new JButton("Load");
		btnLoad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					boolean isComplete = true;
					
					ArrayList<String> invalidFields = new ArrayList<>();
					
					for (JComboBox<String> comboBox : comboBoxes) {
						if (comboBox.getSelectedIndex() == -1) {
							isComplete = false;
							labels.get(comboBox.getName()).setForeground(new Color(178, 34, 34));
							invalidFields.add(labels.get(comboBox.getName()).getText());
							//
						} else {
							labels.get(comboBox.getName()).setForeground(Color.BLACK);
						}
					}
					
					if(!isComplete)
						throw new ApplicationException(String.format("Map the fields below to continue:  \n\n" + invalidFields.stream().collect(Collectors.joining("\n"))));
						
					isLoadSuccessfullyPressed = true;
					DataMappingDialog.this.dispose();
				} catch (ApplicationException err) {
					UiController.handle(err);
				}

			}
		});

		contentPanel.setLayout(new MigLayout("", "[174px:n:174px][24px:n][93px:n:93px][93px:n:93px][24px:n][93px:n][93px:n][28px:n:28px][93px:n:93px][93px:n:93px]", "[6px:n:6px][24px:n][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px]"));
		

		chckbxRememberFields = new JCheckBox("Remember fields used for this job");
		chckbxRememberFields.setForeground(Color.BLACK);
		chckbxRememberFields.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		chckbxRememberFields.setSelected(jpr.isRemember());

		if (job.getRunBehavior().getRequiredFields().length > 0) {
			btnResetMappedFields = new JButton("Reset");
			btnResetMappedFields.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			contentPanel.add(btnResetMappedFields, "cell 9 1,grow");
			btnResetMappedFields.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					for (JComboBox<String> comboBox : comboBoxes) {
						comboBox.setSelectedIndex(-1);
						labels.get(comboBox.getName()).setForeground(Color.BLACK);
						mappedFields.remove(comboBox); // remove the field from mapping
					}
					
					btnLoad.requestFocusInWindow();

				}
			});
		}
		
		contentPanel.add(btnLoad, "cell 9 19,grow");
		contentPanel.add(chckbxRememberFields, "cell 6 19 3 1,alignx left");
		if (job.getRunBehavior().getRequiredFields().length < MAX_ROWS) {
			contentPanel.setLayout(new MigLayout("", "[160px:n:160px][24px:n][93px:n:93px,grow][93px:n:93px][24px:n][93px:n:93px][93px:n:93px]",
					"[6px:n:6px][24px:n][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px][28px:n:28px]"));

			contentPanel.add(btnLoad, "cell 6 19,grow");
			contentPanel.add(chckbxRememberFields, "cell 3 19 3 1,alignx left");
			
			if (job.getRunBehavior().getRequiredFields().length > 0)
				contentPanel.add(btnResetMappedFields, "cell 6 1,grow");
			
			setSize(648, 678);
		}
		
		btnMergeSelectedFields = new JButton("Merge New Field");
		btnMergeSelectedFields.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnMergeSelectedFields.addActionListener(new MergeSelectedFieldsHandler());
		contentPanel.add(btnMergeSelectedFields, "cell 0 19,grow");

		// Required fields label
		if(job.getRunBehavior().getRequiredFields().length > 0) {
			lblRequiredFieldsLabel = new JLabel("Required Fields");
			lblRequiredFieldsLabel.setForeground(new Color(25, 25, 112));
			lblRequiredFieldsLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
			contentPanel.add(lblRequiredFieldsLabel, "cell 2 1 2 1,alignx left,aligny bottom");
		}

		setLocationRelativeTo(frame);

		lblPreviewLabel = new JLabel("Data Fields");
		lblPreviewLabel.setForeground(new Color(25, 25, 112));
		lblPreviewLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblPreviewLabel, "cell 0 1,alignx left,aligny bottom");

		//lblPreviewDescription = new JLabel("<html>Press left or right to search data</html>");
		lblPreviewDescription = new JLabel(String.format("<html>Record %d of %d</html>", 0, UiController.getUserData().getData().length));
		lblPreviewDescription.setForeground(Color.BLACK);
		lblPreviewDescription.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(lblPreviewDescription, "cell 0 2,alignx left,aligny bottom");

		String[] inHeaders = UiController.getUserData().getInHeaders();
		buildMappingFields(job.getRunBehavior().getRequiredFields(), inHeaders);

		Border border = BorderFactory.createLineBorder(Color.decode("#dbdbdb"));

		scrollPane = new JScrollPane();

		contentPanel.add(scrollPane, "cell 0 3 1 14,grow");

		list = new JList<String>();
		list.setForeground(Theme.TITLE_COLOR);
		list.setBackground(Theme.BG_LIGHT_COLOR);
		list.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		list.setDragEnabled(true);
		list.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// ArrayList<String>
		dataPreview = new ArrayList<>(Arrays.asList(UiController.getUserData().getData()));
		dataPreview.add(0, UiController.getUserData().getInHeaders());
		list.setListData(dataPreview.get(0));
		list.setFixedCellHeight(32);
		scrollPane.setViewportView(list);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if(DataMappingDialog.this.isActive()) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						scrollBar.setValue(scrollBar.getValue() + 1);
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						scrollBar.setValue(scrollBar.getValue() - 1);
					}
				}
				}

				return false;
			}
			

		});
		scrollBar = new JScrollBar();
		scrollBar.putClientProperty( "JScrollBar.showButtons", true );

		scrollBar.setBackground(Color.WHITE);
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		scrollBar.setBlockIncrement(1);

		scrollBar.setMinimum(0);
		scrollBar.setMaximum(dataPreview.size());
		scrollBar.setVisibleAmount(1); // if not set the scroll bar thumb disappears

		scrollBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent ae) {
				if (scrollBar.getValueIsAdjusting())
					return;

				lblPreviewDescription.setText(String.format("<html>Record %d of %d</html>", ae.getValue(), UiController.getUserData().getData().length));
				list.setListData(dataPreview.get(ae.getValue()));
				canMapToFields = false;
				for (JComboBox<String> cb : comboBoxes) {
					int selectedIndex = cb.getSelectedIndex();
					cb.setModel(getClientComboBoxModel(dataPreview.get(ae.getValue())));
					cb.setSelectedIndex(selectedIndex);
				}
				canMapToFields = true;
			}
		});
		scrollBar.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		contentPanel.add(scrollBar, "cell 0 17,grow");
		separator = new JSeparator();
		contentPanel.add(separator, "cell 0 18 10 1,growx,aligny center");
		
		pack();
		
		// Focus on the load button
		getRootPane().setDefaultButton(btnLoad);
		btnLoad.requestFocusInWindow();

	}

	// Tries to add an element to fields to map, return true on success, otherwise false
	public boolean addToMappedFields(JComboBox<String> box) {
		try {
			for (HashMap.Entry<JComboBox<String>, String> mapElement : mappedFields.entrySet()) { // loop through mapped fields
				if (mapElement.getValue().equals(box.getSelectedItem().toString()) && mapElement.getKey() != box) { // if box and map value matches and box
					// address and map key doesn't
					box.setSelectedIndex(-1); // reset the user selection
					mappedFields.remove(box); // remove the field from mapping
					throw new ApplicationException(String.format("Fields cannot hold duplicate values."));
				}
			}
			mappedFields.put(box, box.getSelectedItem().toString()); // if we pass above, add the selection to fields to map
			return true;
		} catch (Exception err) {
			UiController.handle(err);
			return false;
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
					mappedFields.remove(comboBox); // remove the field from mapping
				}
			}
		}

		public void keyTyped(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {}
	}

	// Handler for comboBox mapping
	private class comboBoxMappingHandler implements ActionListener {

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// LOG.debug("Customer List item pressed");
				if (e.getSource() instanceof JComboBox && canMapToFields) {
					@SuppressWarnings("rawtypes")
					JComboBox userComboBox = (JComboBox) e.getSource();
					
					if(userComboBox.getSelectedIndex() != -1) {
						mapComboBoxField(userComboBox);
						labels.get(userComboBox.getName()).setForeground(Color.BLACK);
					}
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}

	}
	
	private class MergeSelectedFieldsHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// LOG.debug("Customer List item pressed");
				if (e.getSource() == btnMergeSelectedFields) {
					
					int[] selectedFieldIndexes = list.getSelectedIndices();
				
					String newFieldName = "";
					String newFieldValue = "";
					
					UserInputDialog newHeaderDialog = new UserInputDialog(UiController.getMainFrame(), "Enter the name for the new field");
					newHeaderDialog.setVisible(true);
					
					if(newHeaderDialog.getIsNextPressed())
						newFieldName = newHeaderDialog.getUserInput();
					else
						return;
					
					if(newFieldName.trim().length() == 0)
						throw new Exception("Field name can't be blank.");
					
					UserInputDialog newFieldDialog = new UserInputDialog(UiController.getMainFrame(), "Enter the format to use for the new field");
					
					newFieldDialog.setChckbxOption("Trim extra spaces");
					newFieldDialog.getChckbxOption().setSelected(true);
					
					String newFieldPattern = "";
					
					for(int i = 0 ; i < selectedFieldIndexes.length; ++i) {
						
						String field = UiController.getUserData().getInHeaders()[selectedFieldIndexes[i]];
						
						if(i != list.getSelectedValuesList().size() - 1)
							newFieldPattern += String.format("[%s] ", field);
						else
							newFieldPattern += String.format("[%s]", field);
					}
					
					newFieldDialog.getTextField().setText(newFieldPattern);
					newFieldDialog.setVisible(true);
					
					if(newFieldDialog.getIsNextPressed())
						newFieldValue = newFieldDialog.getUserInput();
					else
						return;
					
					btnMergeSelectedFields.setEnabled(false);
					btnLoad.setEnabled(false);
					for (JComboBox<String> comboBox : comboBoxes)
						comboBox.setEnabled(false);
					
					String[] inHeaders = UiController.getUserData().getInHeaders();
					
					HashSet<String> uniqueHeaders = new HashSet<>();
					HashSet<String> reservedHeaders = new HashSet<>();
					
					for(String header : inHeaders)
						uniqueHeaders.add(header.toLowerCase());
					
					for(String dfHeader : UserData.getAllDfHeaders())
						reservedHeaders.add(dfHeader.toLowerCase());
					
					if(uniqueHeaders.contains(newFieldName.toLowerCase()))
						throw new Exception("Field name already exists.");
					
					if(reservedHeaders.contains(newFieldName.toLowerCase()))
						throw new Exception("Field name is reserved for Dfuze.");

					String[] newHeaders = new String[inHeaders.length + 1];

					String[][] originalData = UiController.getUserData().getData();
					String[][] newData = new String[originalData.length][newHeaders.length];

					for(int i = 0; i < inHeaders.length; ++i)
						newHeaders[i] = inHeaders[i];

					newHeaders[newHeaders.length - 1] = newFieldName;

					for(int i = 0; i < originalData.length; ++i) {
						for(int j = 0; j < originalData[i].length; ++j) {
							newData[i][j] = originalData[i][j];
						}

						String newValue = newFieldValue;
						
						// Pattern.quote will escape all special characters
						// In replaceAll, the replacement string isn't treated as a regex BUT '$' and '\' have special meanings
						// so we must use quote replacement to escape them
						for(int index : selectedFieldIndexes)
								newValue = newValue.replaceAll("(\\[" + Pattern.quote(newHeaders[index]) + "\\])", Matcher.quoteReplacement(originalData[i][index]));

						if(newFieldDialog.getChckbxOption().isSelected())
							newData[i][newData[i].length - 1] = newValue.replaceAll("  ", " ").trim();
						else
							newData[i][newData[i].length - 1] = newValue;
					}

					UiController.getUserData().load(newHeaders, newData);

					for (JComboBox<String> comboBox : comboBoxes) {
						int previousIndex = comboBox.getSelectedIndex();

						comboBox.setModel(getClientComboBoxModel(newHeaders));
						comboBox.setSelectedIndex(previousIndex);
						comboBox.setEnabled(true);
					}
					
					btnLoad.setEnabled(true);
					btnMergeSelectedFields.setEnabled(true);

					dataPreview = new ArrayList<>(Arrays.asList(UiController.getUserData().getData()));
					dataPreview.add(0, UiController.getUserData().getInHeaders());
					list.setListData(dataPreview.get(0));

					scrollBar.setValue(0);
					lblPreviewDescription.setText(String.format("<html>Record %d of %d</html>", 0, UiController.getUserData().getData().length));

				}
			} catch (Exception err) {
				
				for (JComboBox<String> comboBox : comboBoxes)
					comboBox.setEnabled(true);
				
				btnLoad.setEnabled(true);
				btnMergeSelectedFields.setEnabled(true);
				
				UiController.handle(err);
			}
		}

	}

	// this class solves issues when there are duplicate values in a comboBox
	public class StringWithoutEqual {
		private String text;

		// Constructor
		public StringWithoutEqual(String txt) {
			text = txt;
		}

		// Method
		@Override
		public String toString() {
			return text;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private DefaultComboBoxModel<String> getClientComboBoxModel(String[] array) {

		DefaultComboBoxModel defaultBox = new DefaultComboBoxModel<>();
		for (int i = 0; i < array.length; ++i) {
			defaultBox.addElement(new StringWithoutEqual(array[i]));
		}

		return defaultBox;
	}

	public boolean getIsLoadSuccessfullyPressed() {
		return isLoadSuccessfullyPressed;
	}

	public static String getLabelNameFromDfuzeFieldName(String field) throws ApplicationException {
		String labelName;

		if (field.equals(UserData.fieldName.SEGMENT_PLAN_FILTER_1.getName()))
			labelName = "SEGMENT PLAN FILTER1";
		else if (field.equals(UserData.fieldName.SEGMENT_PLAN_FILTER_2.getName()))
			labelName = "SEGMENT PLAN FILTER2";
		else if (field.equals(UserData.fieldName.DUPE_STREET_ADD1.getName()))
			labelName = "DUPE STREET ADDRESS1";
		else if (field.equals(UserData.fieldName.DUPE_STREET_ADD2.getName()))
			labelName = "DUPE STREET ADDRESS2";
		else if (field.equals(UserData.fieldName.DUPE_META_STREET_ADD1.getName()))
			labelName = "DUPE META STREET ADDRESS1";
		else if (field.equals(UserData.fieldName.DUPE_META_STREET_ADD2.getName()))
			labelName = "DUPE META STREET ADDRESS2";
		else if (field.equals(UserData.fieldName.DUPE_ADD1.getName()))
			labelName = "DUPE ADD1";
		else if (field.equals(UserData.fieldName.DUPE_ADD2.getName()))
			labelName = "DUPE ADD2";
		else if (field.equals(UserData.fieldName.DUPE_PCODE.getName()))
			labelName = "DUPE PCODE";
		else if (field.equals(UserData.fieldName.DUPE_POBOX.getName()))
			labelName = "DUPE POBOX";
		else if (field.equals(UserData.fieldName.DUPE_POBOX_EXTRA.getName()))
			labelName = "DUPE POBOX EXTRA";
		else if (field.equals(UserData.fieldName.DUPE_STREET_DIRECTION.getName()))
			labelName = "DUPE STREET DIRECTION";
		else if (field.equals(UserData.fieldName.DUPE_RR.getName()))
			labelName = "DUPE RR";
		else if (field.equals(UserData.fieldName.DUPE_NAME1.getName()))
			labelName = "DUPE NAME1";
		else if (field.equals(UserData.fieldName.DUPE_NAME1_FIRST_PERSON.getName()))
			labelName = "DUPE NAME1 FIRST PERSON";
		else if (field.equals(UserData.fieldName.DUPE_NAME1_SECOND_PERSON.getName()))
			labelName = "DUPE NAME1 SECOND PERSON";
		else if (field.equals(UserData.fieldName.DUPE_NAME1_NORMALIZED.getName()))
			labelName = "DUPE NAME1 NORMALIZED";
		else if (field.equals(UserData.fieldName.DUPE_NAME2.getName()))
			labelName = "DUPE NAME2";
		else if (field.equals(UserData.fieldName.DUPE_NAME2_FIRST_PERSON.getName()))
			labelName = "DUPE NAME2 FIRST PERSON";
		else if (field.equals(UserData.fieldName.DUPE_NAME2_SECOND_PERSON.getName()))
			labelName = "DUPE NAME2 SECOND PERSON";
		else if (field.equals(UserData.fieldName.DUPE_NAME2_NORMALIZED.getName()))
			labelName = "DUPE NAME2 NORMALIZED";
		else if (field.equals(UserData.fieldName.DUPE_GROUP_ID.getName()))
			labelName = "DUPE GROUP ID";
		else if (field.equals(UserData.fieldName.DUPE_GROUP_SIZE.getName()))
			labelName = "DUPE GROUP SIZE";
		else if (field.equals(UserData.fieldName.IN_ID.getName()))
			labelName = "ID";
		else if (field.equals(UserData.fieldName.LANGUAGE.getName()))
			labelName = "LANG";
		else if (field.equals(UserData.fieldName.BARCODE.getName()))
			labelName = "BARCODE";
		else if (field.equals(UserData.fieldName.RECORD_TYPE.getName()))
			labelName = "RECORD TYPE";
		else if (field.equals(UserData.fieldName.STATUS.getName()))
			labelName = "STATUS";
		else if (field.equals(UserData.fieldName.LIST_NUMBER.getName()))
			labelName = "LIST NUMBER";
		else if (field.equals(UserData.fieldName.DAY.getName()))
			labelName = "DAY";
		else if (field.equals(UserData.fieldName.MONTH.getName()))
			labelName = "MONTH";
		else if (field.equals(UserData.fieldName.YEAR.getName()))
			labelName = "YEAR";
		else if (field.equals(UserData.fieldName.LATITUDE.getName()))
			labelName = "LATITUDE";
		else if (field.equals(UserData.fieldName.LONGITUDE.getName()))
			labelName = "LONGITUDE";
		else if (field.equals(UserData.fieldName.QUANTITY.getName()))
			labelName = "QUANTITY";
		else if (field.equals(UserData.fieldName.PHONE1.getName()))
			labelName = "TELEPHONE1";
		else if (field.equals(UserData.fieldName.PHONE2.getName()))
			labelName = "TELEPHONE2";
		else if (field.equals(UserData.fieldName.MOBILE_PHONE.getName()))
			labelName = "MOBILE PHONE";
		else if (field.equals(UserData.fieldName.EMAIL.getName()))
			labelName = "EMAIL";
		else if (field.equals(UserData.fieldName.STORE_ID.getName()))
			labelName = "STORE ID";
		else if (field.equals(UserData.fieldName.DATE_FROM.getName()))
			labelName = "DATE FROM";
		else if (field.equals(UserData.fieldName.DATE_TO.getName()))
			labelName = "DATE TO";
		else if (field.equals(UserData.fieldName.TIME_FROM.getName()))
			labelName = "TIME FROM";
		else if (field.equals(UserData.fieldName.TIME_TO.getName()))
			labelName = "TIME TO";
		else if (field.equals(UserData.fieldName.REFERENCE.getName()))
			labelName = "REFERENCE";
		else if (field.equals(UserData.fieldName.MISC1.getName()))
			labelName = "MISC 1";
		else if (field.equals(UserData.fieldName.MISC2.getName()))
			labelName = "MISC 2";
		else if (field.equals(UserData.fieldName.PREFIX.getName()))
			labelName = "PREFIX";
		else if (field.equals(UserData.fieldName.SUFFIX.getName()))
			labelName = "SUFFIX";
		else if (field.equals(UserData.fieldName.FIRSTNAME.getName()))
			labelName = "FNAME";
		else if (field.equals(UserData.fieldName.MIDDLENAME.getName()))
			labelName = "MNAME";
		else if (field.equals(UserData.fieldName.LASTNAME.getName()))
			labelName = "LNAME";
		else if (field.equals(UserData.fieldName.NAME1.getName()))
			labelName = "NAME1";
		else if (field.equals(UserData.fieldName.NAME1_2.getName()))
			labelName = "NAME1_2";
		else if (field.equals(UserData.fieldName.NAME2.getName()))
			labelName = "NAME2";
		else if (field.equals(UserData.fieldName.SPOUSE_PREFIX.getName()))
			labelName = "SPOUSE PREFIX";
		else if (field.equals(UserData.fieldName.SPOUSE_SUFFIX.getName()))
			labelName = "SPOUSE SUFFIX";
		else if (field.equals(UserData.fieldName.SPOUSE_FIRSTNAME.getName()))
			labelName = "SPOUSE FNAME";
		else if (field.equals(UserData.fieldName.SPOUSE_MIDDLENAME.getName()))
			labelName = "SPOUSE MNAME";
		else if (field.equals(UserData.fieldName.SPOUSE_LASTNAME.getName()))
			labelName = "SPOUSE LNAME";
		else if (field.equals(UserData.fieldName.SPOUSE_NAME1.getName()))
			labelName = "SPOUSE NAME1";
		else if (field.equals(UserData.fieldName.SPOUSE_NAME2.getName()))
			labelName = "SPOUSE NAME2";
		else if (field.equals(UserData.fieldName.NAME2_2.getName()))
			labelName = "NAME2_2";
		else if (field.equals(UserData.fieldName.COMPANY.getName()))
			labelName = "COMPANY";
		else if (field.equals(UserData.fieldName.COMPANY_2.getName()))
			labelName = "COMPANY2";
		else if (field.equals(UserData.fieldName.COMPANY_CONTACT.getName()))
			labelName = "COMPANY CONTACT";
		else if (field.equals(UserData.fieldName.DEAR_SALUTATION.getName()))
			labelName = "SALUTATION";
		else if (field.equals(UserData.fieldName.PARAGRAPH_SALUTATION.getName()))
			labelName = "PARA SALUTATION";
		else if (field.equals(UserData.fieldName.COMPANY_SALUTATION.getName()))
			labelName = "COMPANY SALUTATION";
		else if (field.equals(UserData.fieldName.COMPANY_ADDRESS1.getName()))
			labelName = "COMPANY ADDRESS1";
		else if (field.equals(UserData.fieldName.COMPANY_ADDRESS2.getName()))
			labelName = "COMPANY ADDRESS2";
		else if (field.equals(UserData.fieldName.ADDRESS1.getName()))
			labelName = "ADDRESS1";
		else if (field.equals(UserData.fieldName.ADDRESS1_2.getName()))
			labelName = "ADDRESS1_2";
		else if (field.equals(UserData.fieldName.ADDRESS2.getName()))
			labelName = "ADDRESS2";
		else if (field.equals(UserData.fieldName.ADDRESS2_2.getName()))
			labelName = "ADDRESS2_2";
		else if (field.equals(UserData.fieldName.CITY.getName()))
			labelName = "CITY";
		else if (field.equals(UserData.fieldName.PROVINCE.getName()))
			labelName = "PROV";
		else if (field.equals(UserData.fieldName.POSTALCODE.getName()))
			labelName = "PCODE";
		else if (field.equals(UserData.fieldName.COUNTRY.getName()))
			labelName = "COUNTRY";
		else if (field.equals(UserData.fieldName.STREET_NUMBER.getName()))
			labelName = "STREET NUMBER";
		else if (field.equals(UserData.fieldName.STREET_DIRECTION.getName()))
			labelName = "STREET DIRECTION";
		else if (field.equals(UserData.fieldName.STREET_NAME.getName()))
			labelName = "STREET NAME";
		else if (field.equals(UserData.fieldName.STREET_DEFINER.getName()))
			labelName = "STREET DEFINER";
		else if (field.equals(UserData.fieldName.APARTMENT_NUMBER.getName()))
			labelName = "APARTMENT NUMBER";
		else if (field.equals(UserData.fieldName.SERVICE_ADDRESS.getName()))
			labelName = "SERVICE ADDRESS";
		else if (field.equals(UserData.fieldName.SERVICE_CITY.getName()))
			labelName = "SERVICE CITY";
		else if (field.equals(UserData.fieldName.PO_BOX.getName()))
			labelName = "PO BOX";
		else if (field.equals(UserData.fieldName.CARE_OF.getName()))
			labelName = "CARE OF";
		else if (field.equals(UserData.fieldName.NCOA.getName()))
			labelName = "NCOA";
		else if (field.equals(UserData.fieldName.DNM.getName()))
			labelName = "DNM";
		else if (field.equals(UserData.fieldName.DECEASED.getName()))
			labelName = "DECEASED";
		else if (field.equals(UserData.fieldName.DONATION_AMOUNT_ARRAY.getName()))
			labelName = "GIFT AMOUNT ARRAY";
		else if (field.equals(UserData.fieldName.DONATION_DATE_ARRAY.getName()))
			labelName = "GIFT DATE ARRAY";
		else if (field.equals(UserData.fieldName.NUMBER_OF_DONATIONS.getName()))
			labelName = "TOTAL NUMBER OF GIFTS";
		else if (field.equals(UserData.fieldName.NUMBER_OF_DONATIONS_LAST_12_MONTHS.getName()))
			labelName = "TOTAL NUMBER OF GIFTS LAST 12 MONTHS";
		else if (field.equals(UserData.fieldName.TOTAL_DONATION_AMOUNT.getName()))
			labelName = "TOTAL GIFT AMOUNT";
		else if (field.equals(UserData.fieldName.TOTAL_DONATION_AMOUNT_LAST_12_MONTHS.getName()))
			labelName = "TOTAL GIFT AMOUNT LAST 12 MONTHS";
		else if (field.equals(UserData.fieldName.TOTAL_DONATION_AMOUNT_CURRENT_YEAR.getName()))
			labelName = "TOTAL GIFT AMOUNT CURRENT YEAR";
		else if (field.equals(UserData.fieldName.TOTAL_DONATION_AMOUNT_LAST_YEAR.getName()))
			labelName = "TOTAL GIFT AMOUNT LAST YEAR";
		else if (field.equals(UserData.fieldName.FIRST_DONATION_AMOUNT.getName()))
			labelName = "FIRST GIFT AMOUNT";
		else if (field.equals(UserData.fieldName.LAST_DONATION_AMOUNT.getName()))
			labelName = "LAST GIFT AMOUNT";
		else if (field.equals(UserData.fieldName.LARGEST_DONATION_AMOUNT.getName()))
			labelName = "LARGEST GIFT AMOUNT";
		else if (field.equals(UserData.fieldName.SMALLEST_DONATION_AMOUNT.getName()))
			labelName = "SMALLEST GIFT AMOUNT";
		else if (field.equals(UserData.fieldName.LAST_MONTHLY_DONATION_AMOUNT.getName()))
			labelName = "LAST MONTHLY GIFT AMOUNT";
		else if (field.equals(UserData.fieldName.FIRST_DONATION_DATE.getName()))
			labelName = "FIRST GIFT DATE";
		else if (field.equals(UserData.fieldName.LAST_DONATION_DATE.getName()))
			labelName = "LAST GIFT DATE";
		else if (field.equals(UserData.fieldName.LAST_MONTHLY_DONATION_DATE.getName()))
			labelName = "LAST MONTHLY GIFT DATE";
		else if (field.equals(UserData.fieldName.PENULTIMATE_AMOUNT.getName()))
			labelName = "PENULTIMATE AMOUNT";
		else if (field.equals(UserData.fieldName.PENULTIMATE_DATE.getName()))
			labelName = "PENULTIMATE DATE";
		else if (field.equals(UserData.fieldName.DONATION1_AMOUNT.getName()))
			labelName = "GIFT ASK1";
		else if (field.equals(UserData.fieldName.DONATION2_AMOUNT.getName()))
			labelName = "GIFT ASK2";
		else if (field.equals(UserData.fieldName.DONATION3_AMOUNT.getName()))
			labelName = "GIFT ASK3";
		else if (field.equals(UserData.fieldName.DONATION4_AMOUNT.getName()))
			labelName = "GIFT ASK4";
		else if (field.equals(UserData.fieldName.OPEN_DONATION_AMOUNT.getName()))
			labelName = "OPEN ASK";
		else if (field.equals(UserData.fieldName.MONTHLY_DONATION1_AMOUNT.getName()))
			labelName = "MONTHLY GIFT ASK1";
		else if (field.equals(UserData.fieldName.MONTHLY_DONATION2_AMOUNT.getName()))
			labelName = "MONTHLY GIFT ASK2";
		else if (field.equals(UserData.fieldName.MONTHLY_DONATION3_AMOUNT.getName()))
			labelName = "MONTHLY GIFT ASK3";
		else if (field.equals(UserData.fieldName.MONTHLY_DONATION4_AMOUNT.getName()))
			labelName = "MONTHLY GIFT ASK4";
		else if (field.equals(UserData.fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName()))
			labelName = "MONTHLY OPEN ASK";
		else if (field.equals(UserData.fieldName.PROVIDE1.getName()))
			labelName = "PROVIDES 1";
		else if (field.equals(UserData.fieldName.PROVIDE2.getName()))
			labelName = "PROVIDES 2";
		else if (field.equals(UserData.fieldName.PROVIDE3.getName()))
			labelName = "PROVIDES 3";
		else if (field.equals(UserData.fieldName.PROVIDE4.getName()))
			labelName = "PROVIDES 4";
		else if (field.equals(UserData.fieldName.PRIORITY.getName()))
			labelName = "PRIORITY";
		else if (field.equals(UserData.fieldName.AB_GROUP.getName()))
			labelName = "A/B GROUP";
		else if (field.equals(UserData.fieldName.APPEAL.getName()))
			labelName = "APPEAL";
		else if (field.equals(UserData.fieldName.SEGMENT.getName()))
			labelName = "SEGMENT";
		else if (field.equals(UserData.fieldName.SEGMENT_CODE.getName()))
			labelName = "SEGMENT CODE";
		else if (field.equals(UserData.fieldName.LETTER_VERSION.getName()))
			labelName = "LETTER VERSION";
		else if (field.equals(UserData.fieldName.PACKAGE_VERSION.getName()))
			labelName = "PACKAGE VERSION";
		else if (field.equals(UserData.fieldName.REPLY_VERSION.getName()))
			labelName = "REPLY VERSION";
		else if (field.equals(UserData.fieldName.CODELINE.getName()))
			labelName = "CODELINE";
		else if (field.equals(UserData.fieldName.MEAN_AMOUNT.getName()))
			labelName = "MEAN GIFT AMOUNT";
		else if (field.equals(UserData.fieldName.MEDIAN.getName()))
			labelName = "MEDIAN";
		else if (field.equals(UserData.fieldName.STANDARD_DEVIATION_AMOUNT.getName()))
			labelName = "STD.DEV GIFT AMOUNT";
		else if (field.equals(UserData.fieldName.RECENCY.getName()))
			labelName = "RECENCY";
		else if (field.equals(UserData.fieldName.FREQUENCY.getName()))
			labelName = "FREQUENCY";
		else if (field.equals(UserData.fieldName.MONETARY.getName()))
			labelName = "MONETARY";
		else if (field.equals(UserData.fieldName.RFM.getName()))
			labelName = "RFM";
		else if (field.equals(UserData.fieldName.LENGTH.getName()))
			labelName = "LENGTH";
		else if (field.equals(UserData.fieldName.WIDTH.getName()))
			labelName = "WIDTH";
		else if (field.equals(UserData.fieldName.HEIGHT.getName()))
			labelName = "HEIGHT";
		else if (field.equals(UserData.fieldName.WEIGHT.getName()))
			labelName = "WEIGHT";
		else {
			throw new ApplicationException(String.format("Could not find a label name for the Dfuze field %s", field));
		}

		return labelName;
	}

	public void buildMappingFields(String fields[], String[] inHeaders) throws ApplicationException {
		HashMap<String, String> lufMap = new HashMap<>();
		
		if(initialJprState)
			lufMap = JobPref.getLastUsedFieldValues(jpr);
		
		// Determines what field to print
		for (int i = 0; i < fields.length; i++) {
			String labelName = getLabelNameFromDfuzeFieldName(fields[i]);

			// System.out.println(fields.length + " and " + i + " with " + inHeaders[i]);

			addLabel(labelName, fields[i]);
			addComboBox(inHeaders, fields[i], labelName, lufMap);
		}
	}

	private void addLabel(String labelName, String fieldName) throws ApplicationException {
		if (currentRowToAdd > MAX_ROWS) {
			if (currentColToAdd == MAX_COLUMNS)
				throw new ApplicationException(String.format("Max Fields to map reached."));
			currentColToAdd = currentColToAdd + 3;
			currentRowToAdd = 2;
		}

		String properties;

		properties = "cell " + currentColToAdd + " " + currentRowToAdd++ + " " + 2 + " " + 1 + ",aligny bottom";

		JLabel label = new JLabel(labelName);
		label.setName(fieldName);
		
		label.setDropTarget(new DropTarget() {
			  public synchronized void drop(DropTargetDropEvent dtde) {
				    try {

				      JLabel newLabel = (JLabel) dtde.getDropTargetContext().getComponent();
				      
				      for(JComboBox<String> comboBox : comboBoxes)
				    	  if(comboBox.getName().equals(newLabel.getName()))
				    		  comboBox.setSelectedIndex(list.getSelectedIndex());
			      

				      dtde.dropComplete(true);
				    } catch (Exception e) {
				      e.printStackTrace();
				      dtde.rejectDrop();
				    }
				  }
				});
		
		label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		label.setForeground(Color.BLACK);
		contentPanel.add(label, properties);
		
		labels.put(fieldName, label);
	}

	private void addComboBox(String[] inHeaders, String fieldName, String labelName, HashMap<String, String> lufMap) throws ApplicationException {
		if (currentRowToAdd > MAX_ROWS) {
			if (currentColToAdd == MAX_COLUMNS)
				throw new ApplicationException(String.format("Max Fields to map reached."));
			currentColToAdd = currentColToAdd + 3;
			currentRowToAdd = 2;
		}

		String properties;

		properties = "cell " + currentColToAdd + " " + currentRowToAdd++ + " " + 2 + " " + 1 + ",grow";

		JComboBox<String> comboBox = new JComboBox<>(getClientComboBoxModel(inHeaders));
		comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBox.setSelectedIndex(-1);
		comboBox.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		comboBox.addActionListener(new comboBoxMappingHandler());
		comboBox.addKeyListener(new ComboBoxDeleteHandler());
		comboBox.setDropTarget(new DropTarget() {
			  public synchronized void drop(DropTargetDropEvent dtde) {
				    try {
	
				      // Get the drop target (the combo box)
				      @SuppressWarnings("unchecked")
					JComboBox<String> comboBox = (JComboBox<String>) dtde.getDropTargetContext().getComponent();

				      // Set the selected index of the combo box to the index of the list item in its list
				      comboBox.setSelectedIndex(list.getSelectedIndex());

				      dtde.dropComplete(true);
				    } catch (Exception e) {
				      e.printStackTrace();
				      dtde.rejectDrop();
				    }
				  }
				});
		
		
		comboBox.setName(fieldName);
		InputMap im = comboBox.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke("DOWN"), "none");
		im.put(KeyStroke.getKeyStroke("UP"), "none");
		contentPanel.add(comboBox, properties);
		comboBoxes.add(comboBox);

		
		// If the user wants to remember fields
		if(initialJprState == true) {
			if(lufMap.containsKey(fieldName)) {
				for(int i = 0; i < inHeaders.length; ++i) {
					if(inHeaders[i].equalsIgnoreCase(lufMap.get(fieldName))) {
						if (!buildingHeadersDupesCheck.contains(inHeaders[i])) {
							comboBox.setSelectedIndex(i);
							mapComboBoxField(comboBox);
							buildingHeadersDupesCheck.add(inHeaders[i]);
							return;
						}
					}
				}
				
				return; // If the required field value was not found, dont try to set it
			}
		}
		
		double closestJaroDistance = 0;
		int closestIndex = 0;
		String closestHeaderValue = inHeaders[0];

		for (int i = 0; i < inHeaders.length; ++i) {
			if (buildingHeadersDupesCheck.contains(inHeaders[i]))
				if (++i == inHeaders.length)
					break;
			
			

			String newHeaderValue = reformatHeader(inHeaders[i]);
			
			//System.out.println(newHeaderValue);
			
			newHeaderValue = newHeaderValue.toLowerCase()
					.replaceAll("(cnapls|cnattrcat|cnadrprfatrcat|cnrelsol|cnph)_(\\d+_)?(\\d+_)?", "") //Raisers Edge prefixes
					.replaceAll("cnbio", "") //Raisers Edge prefixes
					.replaceAll("cnadrsal", "") //Raisers Edge prefixes
					.replaceAll("cnadrpref", "") //Raisers Edge prefixes
					.replaceAll("smrygift", "") //Raisers Edge prefixes
					.replaceAll("in_+", "") //Dfuze and IAddress prefixes
					.replaceAll("_", "")
					.replaceAll("#", "number")
					.replaceAll("  ", " ").trim();
			
			/*
			String newHeaderValue = inHeaders[i].toLowerCase().trim()
					.replaceAll("[c][n][b][i][o][_][k][e][y][_][i][n][d][i][c][a][t][o][r]", "recordtypee")
					.replaceAll("[c][n][r][e][l][c][t][_][1][_][0][1][_][p][r][i][m][a][r][y][_][s][a][l][u][t][a][t][i][o][n]", "company salutation")
					.replaceAll("[c][n][r][e][l][c][t][_][1][_][0][1][_][p][r][i][m][a][r][y][_][a][d][d][r][e][s][s][e][e]", "company rep")
					.replaceAll("[c][n][a][p][l][s][_](\\d+_)?(\\d+_)?", "")
					.replaceAll("^contact addressee$", "company contact")
					.replaceAll("^contact salutation$", "company salutation")
					.replaceAll("^last direct mail gift date$", "last gift date")
					.replaceAll("^last direct mail gift amount$", "last donation amount")
					.replaceAll("^first direct mail gift date$", "first donation date")
					.replaceAll("^first direct mail gift$", "first donation")
					.replaceAll("key indicator", "record type")
					.replaceAll("[d][o][n][c][o][u][n][t]", "total number of gifts")

					.replaceAll("[m][a][x][g][i][f][t]", "largest gift amount")
					.replaceAll("[c][o][n][s][t][i][t][u][e][n][t]", "").replaceAll("[c][o][d][e][l][i][n][e]", "segmentcodee")
					.replaceAll("[v][i][p]", "idd").replaceAll("[l][o][y][a][l][t][y]", "idd").replaceAll("[l][i][n][e]", "")
					.replaceAll("[l][e][t][t][e][r]", "")
					.replaceAll("[l][g][i][f][t]", "last gift")
					.replaceAll("[l][g][f]", "lastgift")
					.replaceAll("[g][f]", "gift")
					.replaceAll("[l][s][t]", "last").replaceAll("[l][r][g]", "largest")

					.replaceAll("[o][r][g]", "company").replaceAll("[t][i][t][l][e]", "prefixx").replaceAll("[d][o][n][a][t][i][o][n]", "giftt")
					.replaceAll("[n][e][w][t][o][f][i][l][e]", "segmentplanfilterr2").replaceAll("(([c][n])|(_+\\d+_+))", "").replaceAll("[c][n]", "")
					.replaceAll("[s][m][r][y][g][i][f][t]", "").replaceAll("[a][d][r][s][a][l]", "").replaceAll("[a][d][r][p][r][f]", "")
					.replaceAll("[b][i][o]", "")
					//NAME
					.replaceAll("[c][n][a][d][r][s][a][l]", "")
					.replaceAll("[c][n][b][i][o]", "")
					.replaceAll(".*[a][d][d][r][e][s][s][e][e].*", "name1")
					.replaceAll("^contact ", "")
					.replaceAll("[f].*[n][a][m].*", "fname")
					.replaceAll("[l].*[n][a][m].*", "lname")
					.replaceAll("[c][u][m][u][l][a][t][i][v][e]", "total").replaceAll("[l][i][f][e][t][i][m][e]", "total").replaceAll("#", "number")
					//NAME1_2
					.replaceAll("[o][p][t][_][l][i][n][e]", "name2") //hma ccpa
					//ADDRESS
					.replaceAll("[a][d][d][r][l][i][n][e]", "address")
					.replaceAll("^[a][d][d]$", "address1")
					.replaceAll("[a][d][d].*(?:1)", "address1")
					.replaceAll("[a][d][d].*(?:2)", "address2")
					.replaceAll("[a][d][d].*(?:3)", "address3")
					//CITY/STATE
					.replaceAll("state", "province")
					//PC
					.replaceAll("[p][o][s][t][a][l]", "pcode")
					.replaceAll("[p][o][s][t][_][c][o]", "pcode")
					.replaceAll("[p][c]$", "pcode")
					.replaceAll("(.*)?zip(.*)?", "pcode")
					// MISCELLANEOUS
					.replaceAll("^[i][n]_+", "")
					.replaceAll("_", "")
					.trim();

			if (newHeaderValue.length() == 0)
				newHeaderValue = "segment plan filterr1";
				*/

			String newLableValue = labelName.toLowerCase().replaceAll(" ", "");

			// System.out.println(newHeaderValue + " vs " + newLableValue);
			double jaroDistance = new JaroWinklerSimilarity().apply(newHeaderValue, newLableValue);
			
			if (jaroDistance > closestJaroDistance) {
				closestJaroDistance = jaroDistance;
				closestIndex = i;
				closestHeaderValue = inHeaders[i];
				
			}

		}

		if (closestJaroDistance >= 0.83 && buildingHeadersDupesCheck.add(closestHeaderValue)) {
			comboBox.setSelectedIndex(closestIndex);
			mapComboBoxField(comboBox);
		}

	}
	
	private String reformatHeader(String header) throws ApplicationException {
		
		if(ID_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.IN_ID.getName());
		if(LIST_NUMBER_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LIST_NUMBER.getName());
		if(STORE_ID_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.STORE_ID.getName());
		if(LANGUAGE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LANGUAGE.getName());
		if(BARCODE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.BARCODE.getName());
		if(RECORD_TYPE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.RECORD_TYPE.getName());
		if(LATITUDE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LATITUDE.getName());
		if(LONGITUDE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LONGITUDE.getName());
		if(NCOA_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.NCOA.getName());
		if(DNM_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.DNM.getName());
		if(DECEASED_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.DECEASED.getName());
		if(SEGMENT_FILTER1_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.SEGMENT_PLAN_FILTER_1.getName());
		if(SEGMENT_FILTER2_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.SEGMENT_PLAN_FILTER_2.getName());
		
		
		if(COMPANY_SALUTATION_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.COMPANY_SALUTATION.getName());
		if(COMPANY_CONTACT_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.COMPANY_CONTACT.getName());

		if(LAST_GIFT_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LAST_DONATION_AMOUNT.getName());
		if(LAST_GIFT_DATE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LAST_DONATION_DATE.getName());
		if(FIRST_GIFT_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.FIRST_DONATION_AMOUNT.getName());
		if(	FIRST_GIFT_DATE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.FIRST_DONATION_DATE.getName());
		if(LARGEST_GIFT_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LARGEST_DONATION_AMOUNT.getName());
		if(SMALLEST_GIFT_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.SMALLEST_DONATION_AMOUNT.getName());
		if(TOTAL_GIFTS_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.TOTAL_DONATION_AMOUNT.getName());		
		if(LAST_MONTHLY_GIFT_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LAST_MONTHLY_DONATION_AMOUNT.getName());
		if(LAST_MONTHLY_GIFT_DATE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LAST_MONTHLY_DONATION_DATE.getName());
		
		if(Rental.PREFIX_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.PREFIX.getName());
		if(Rental.FIRST_NAME_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.FIRSTNAME.getName());
		if(Rental.MIDDLE_NAME_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.MIDDLENAME.getName());
		if(Rental.LAST_NAME_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.LASTNAME.getName());
		if(Rental.FULL_NAME_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.NAME1.getName());
		if(Rental.SPOUSE_PREFIX_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.SPOUSE_PREFIX.getName());
		if(Rental.SPOUSE_FIRST_NAME_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.SPOUSE_FIRSTNAME.getName());
		if(Rental.SPOUSE_MIDDLE_NAME_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.SPOUSE_MIDDLENAME.getName());
		if(Rental.SPOUSE_LAST_NAME_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.SPOUSE_LASTNAME.getName());
		if(Rental.COMPANY_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.COMPANY.getName());
		if(Rental.ADDRESS_1_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.ADDRESS1.getName());
		if(Rental.ADDRESS_2_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.ADDRESS2.getName());
		if(Rental.CITY_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.CITY.getName());
		if(Rental.PROVINCE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.PROVINCE.getName());
		if(Rental.POSTAL_CODE_PATTERN.matcher(header).find())
			return getLabelNameFromDfuzeFieldName(UserData.fieldName.POSTALCODE.getName());
		
		return header;
	}

	private void mapComboBoxField(JComboBox<String> inComboBox) {
		for (JComboBox<String> comboBox : comboBoxes) {
			if (comboBox.getName().equals(inComboBox.getName())) {

				// if we find the comboBox and can add it the list, set the current index to selection index, otherwise set to UserData.DEFAULT_INDEX
				if (comboBox.getName().equals(UserData.fieldName.SEGMENT_PLAN_FILTER_1.getName()))
					UiController.getUserData().setSpFilt1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SEGMENT_PLAN_FILTER_2.getName()))
					UiController.getUserData().setSpFilt2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_STREET_ADD1.getName()))
					UiController.getUserData().setDupeStreetAdd1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_STREET_ADD2.getName()))
					UiController.getUserData().setDupeStreetAdd2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_META_STREET_ADD1.getName()))
					UiController.getUserData().setDupeMetaStreetAdd1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_META_STREET_ADD2.getName()))
					UiController.getUserData().setDupeMetaStreetAdd2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_ADD1.getName()))
					UiController.getUserData().setDupeAdd1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_ADD2.getName()))
					UiController.getUserData().setDupeAdd2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_PCODE.getName()))
					UiController.getUserData().setDupePCodeIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_POBOX.getName()))
					UiController.getUserData().setDupePOBoxIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_POBOX_EXTRA.getName()))
					UiController.getUserData().setDupePOBoxExtraIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_STREET_DIRECTION.getName()))
					UiController.getUserData().setDupeStreetDirectionIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_RR.getName()))
					UiController.getUserData().setDupeRRIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_NAME1.getName()))
					UiController.getUserData().setDupeName1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_NAME1_FIRST_PERSON.getName()))
					UiController.getUserData().setDupeName1FirstPersonIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_NAME1_SECOND_PERSON.getName()))
					UiController.getUserData().setDupeName1SecondPersonIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_NAME1_NORMALIZED.getName()))
					UiController.getUserData().setDupeName1NormalizedIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_NAME2.getName()))
					UiController.getUserData().setDupeName2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_NAME2_FIRST_PERSON.getName()))
					UiController.getUserData().setDupeName2FirstPersonIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_NAME2_SECOND_PERSON.getName()))
					UiController.getUserData().setDupeName2SecondPersonIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_NAME2_NORMALIZED.getName()))
					UiController.getUserData().setDupeName2NormalizedIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);	
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_GROUP_ID.getName()))
					UiController.getUserData().setDupeGroupIdIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DUPE_GROUP_SIZE.getName()))
					UiController.getUserData().setDupeGroupSizeIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.IN_ID.getName()))
					UiController.getUserData().setInIdIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LANGUAGE.getName()))
					UiController.getUserData().setLangIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.BARCODE.getName()))
					UiController.getUserData().setBarCodeIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.RECORD_TYPE.getName()))
					UiController.getUserData().setRecTypeIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.STATUS.getName()))
					UiController.getUserData().setStatusIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LIST_NUMBER.getName()))
					UiController.getUserData().setListNumIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DAY.getName()))
					UiController.getUserData().setDayIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MONTH.getName()))
					UiController.getUserData().setMonthIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.YEAR.getName()))
					UiController.getUserData().setYearIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LATITUDE.getName()))
					UiController.getUserData().setLatitudeIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LONGITUDE.getName()))
					UiController.getUserData().setLongitudeIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.QUANTITY.getName()))
					UiController.getUserData().setQuantityIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PHONE1.getName()))
					UiController.getUserData().setPhone1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PHONE2.getName()))
					UiController.getUserData().setPhone2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MOBILE_PHONE.getName()))
					UiController.getUserData().setMobilePhoneIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.EMAIL.getName()))
					UiController.getUserData().setEmailIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.STORE_ID.getName()))
					UiController.getUserData().setStoreIdIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DATE_FROM.getName()))
					UiController.getUserData().setDateFromIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DATE_TO.getName()))
					UiController.getUserData().setDateToIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.TIME_FROM.getName()))
					UiController.getUserData().setTimeFromIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.TIME_TO.getName()))
					UiController.getUserData().setTimeToIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.REFERENCE.getName()))
					UiController.getUserData().setRefIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MISC1.getName()))
					UiController.getUserData().setMisc1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MISC2.getName()))
					UiController.getUserData().setMisc2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PREFIX.getName()))
					UiController.getUserData().setPrefixIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SUFFIX.getName()))
					UiController.getUserData().setSuffixIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.FIRSTNAME.getName()))
					UiController.getUserData().setFstNameIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MIDDLENAME.getName()))
					UiController.getUserData().setMidNameIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LASTNAME.getName()))
					UiController.getUserData().setLstNameIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.NAME1.getName()))
					UiController.getUserData().setNam1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.NAME1_2.getName()))
					UiController.getUserData().setNam1_2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.NAME2.getName()))
					UiController.getUserData().setNam2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SPOUSE_PREFIX.getName()))
					UiController.getUserData().setSpousePrefixIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SPOUSE_SUFFIX.getName()))
					UiController.getUserData().setSpouseSuffixIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SPOUSE_FIRSTNAME.getName()))
					UiController.getUserData().setSpouseFstNameIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SPOUSE_MIDDLENAME.getName()))
					UiController.getUserData().setSpouseMidNameIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SPOUSE_LASTNAME.getName()))
					UiController.getUserData().setSpouseLstNameIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SPOUSE_NAME1.getName()))
					UiController.getUserData().setSpouseNam1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SPOUSE_NAME2.getName()))
					UiController.getUserData().setSpouseNam2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.NAME2_2.getName()))
					UiController.getUserData().setNam2_2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.COMPANY.getName()))
					UiController.getUserData().setCmpnyIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.COMPANY_2.getName()))
					UiController.getUserData().setCmpny_2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.COMPANY_CONTACT.getName()))
					UiController.getUserData().setCmpnyCnIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DEAR_SALUTATION.getName()))
					UiController.getUserData().setDearSalIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PARAGRAPH_SALUTATION.getName()))
					UiController.getUserData().setParaSalIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.COMPANY_SALUTATION.getName()))
					UiController.getUserData().setCmpnySalIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.COMPANY_ADDRESS1.getName()))
					UiController.getUserData().setCmpnyAdd1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.COMPANY_ADDRESS2.getName()))
					UiController.getUserData().setCmpnyAdd2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.ADDRESS1.getName()))
					UiController.getUserData().setAdd1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.ADDRESS1_2.getName()))
					UiController.getUserData().setAdd1_2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.ADDRESS2.getName()))
					UiController.getUserData().setAdd2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.ADDRESS2_2.getName()))
					UiController.getUserData().setAdd2_2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.CITY.getName()))
					UiController.getUserData().setCityIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PROVINCE.getName()))
					UiController.getUserData().setProvIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.POSTALCODE.getName()))
					UiController.getUserData().setPCodeIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.COUNTRY.getName()))
					UiController.getUserData().setCntryIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.STREET_NUMBER.getName()))
					UiController.getUserData().setStreetNumIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.STREET_DIRECTION.getName()))
					UiController.getUserData().setStreetDirIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.STREET_NAME.getName()))
					UiController.getUserData().setStreetNamIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.STREET_DEFINER.getName()))
					UiController.getUserData().setStreetDefIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.APARTMENT_NUMBER.getName()))
					UiController.getUserData().setAptNumIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SERVICE_ADDRESS.getName()))
					UiController.getUserData().setServiceAddIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SERVICE_CITY.getName()))
					UiController.getUserData().setServiceCityIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PO_BOX.getName()))
					UiController.getUserData().setPoBoxIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.CARE_OF.getName()))
					UiController.getUserData().setCareOfIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.NCOA.getName()))
					UiController.getUserData().setNcoaIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DNM.getName()))
					UiController.getUserData().setDnmIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DECEASED.getName()))
					UiController.getUserData().setDeceasedIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DONATION_AMOUNT_ARRAY.getName()))
					UiController.getUserData().setDnAmtArrIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DONATION_DATE_ARRAY.getName()))
					UiController.getUserData().setDnDatArrIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.NUMBER_OF_DONATIONS.getName()))
					UiController.getUserData().setNumDnIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.NUMBER_OF_DONATIONS_LAST_12_MONTHS.getName()))
					UiController.getUserData().setNumDnLst12MnthsIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.TOTAL_DONATION_AMOUNT.getName()))
					UiController.getUserData().setTtlDnAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.TOTAL_DONATION_AMOUNT_LAST_12_MONTHS.getName()))
					UiController.getUserData().setTtlDnAmtLst12MnthsIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.TOTAL_DONATION_AMOUNT_CURRENT_YEAR.getName()))
					UiController.getUserData().setTtlDnAmtCrntYrIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.TOTAL_DONATION_AMOUNT_LAST_YEAR.getName()))
					UiController.getUserData().setTtlDnAmtLstYrIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.FIRST_DONATION_AMOUNT.getName()))
					UiController.getUserData().setFstDnAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LAST_DONATION_AMOUNT.getName()))
					UiController.getUserData().setLstDnAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LARGEST_DONATION_AMOUNT.getName()))
					UiController.getUserData().setLrgDnAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SMALLEST_DONATION_AMOUNT.getName()))
					UiController.getUserData().setSmlDnAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LAST_MONTHLY_DONATION_AMOUNT.getName()))
					UiController.getUserData().setLMDnAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.FIRST_DONATION_DATE.getName()))
					UiController.getUserData().setFstDnDatIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LAST_DONATION_DATE.getName()))
					UiController.getUserData().setLstDnDatIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LAST_MONTHLY_DONATION_DATE.getName()))
					UiController.getUserData().setLMDnDatIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PENULTIMATE_AMOUNT.getName()))
					UiController.getUserData().setPenultAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PENULTIMATE_DATE.getName()))
					UiController.getUserData().setPenultDatIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DONATION1_AMOUNT.getName()))
					UiController.getUserData().setDn1AmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DONATION2_AMOUNT.getName()))
					UiController.getUserData().setDn2AmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DONATION3_AMOUNT.getName()))
					UiController.getUserData().setDn3AmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.DONATION4_AMOUNT.getName()))
					UiController.getUserData().setDn4AmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.OPEN_DONATION_AMOUNT.getName()))
					UiController.getUserData().setODnAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MONTHLY_DONATION1_AMOUNT.getName()))
					UiController.getUserData().setMDn1AmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MONTHLY_DONATION2_AMOUNT.getName()))
					UiController.getUserData().setMDn2AmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MONTHLY_DONATION3_AMOUNT.getName()))
					UiController.getUserData().setMDn3AmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MONTHLY_DONATION4_AMOUNT.getName()))
					UiController.getUserData().setMDn4AmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName()))
					UiController.getUserData().setMODnAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PROVIDE1.getName()))
					UiController.getUserData().setProvide1Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PROVIDE2.getName()))
					UiController.getUserData().setProvide2Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PROVIDE3.getName()))
					UiController.getUserData().setProvide3Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PROVIDE4.getName()))
					UiController.getUserData().setProvide4Index(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PRIORITY.getName()))
					UiController.getUserData().setPriorityIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.AB_GROUP.getName()))
					UiController.getUserData().setABGroupIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.APPEAL.getName()))
					UiController.getUserData().setAppealIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SEGMENT.getName()))
					UiController.getUserData().setSegIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.SEGMENT_CODE.getName()))
					UiController.getUserData().setSegCodeIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LETTER_VERSION.getName()))
					UiController.getUserData().setLetVerIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.PACKAGE_VERSION.getName()))
					UiController.getUserData().setPkgVerIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.REPLY_VERSION.getName()))
					UiController.getUserData().setRepVerIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.CODELINE.getName()))
					UiController.getUserData().setCodeLineIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MEAN_AMOUNT.getName()))
					UiController.getUserData().setMeanAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MEDIAN.getName()))
					UiController.getUserData().setMedianIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.STANDARD_DEVIATION_AMOUNT.getName()))
					UiController.getUserData().setSDevAmtIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.RECENCY.getName()))
					UiController.getUserData().setRScoreIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.FREQUENCY.getName()))
					UiController.getUserData().setFScoreIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.MONETARY.getName()))
					UiController.getUserData().setMScoreIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.RFM.getName()))
					UiController.getUserData().setRfmScoreIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.LENGTH.getName()))
					UiController.getUserData().setLengthIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.WIDTH.getName()))
					UiController.getUserData().setWidthIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.HEIGHT.getName()))
					UiController.getUserData().setHeightIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
				else if (comboBox.getName().equals(UserData.fieldName.WEIGHT.getName()))
					UiController.getUserData().setWeightIndex(addToMappedFields(comboBox) ? comboBox.getSelectedIndex() : UserData.DEFAULT_INDEX);
			}
		}
	}

	public JCheckBox getChckbxRememberFields() {
		return chckbxRememberFields;
	}

	public void setChckbxRememberFields(JCheckBox chckbxRememberFields) {
		this.chckbxRememberFields = chckbxRememberFields;
	}

	public boolean isInitialJprState() {
		return initialJprState;
	}

	public void setInitialJprState(boolean initialJprState) {
		this.initialJprState = initialJprState;
	}

	public ArrayList<JComboBox<String>> getComboBoxes() {
		return comboBoxes;
	}

	public void setComboBoxes(ArrayList<JComboBox<String>> comboBoxes) {
		this.comboBoxes = comboBoxes;
	}
	
	

}
