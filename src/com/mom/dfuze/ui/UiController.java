/**
 * Project: DFuze
 * File: UiController.java
 * Date: Mar 2, 2020
 * Time: 9:30:10 PM
 */
package com.mom.dfuze.ui;

import java.awt.Desktop;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mom.dfuze.Dfuze;
import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.InputData;
import com.mom.dfuze.data.JobPref;
import com.mom.dfuze.data.JobPrefRemember;
import com.mom.dfuze.data.LastUsedField;
import com.mom.dfuze.data.Record;
// import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.jobs.utility.Rental;
import com.mom.dfuze.data.util.SwingUtils;
import com.mom.dfuze.db.SystemDao;
import com.mom.dfuze.io.AccessReader;
import com.mom.dfuze.io.DbfReader;
import com.mom.dfuze.io.ExcelReader;
import com.mom.dfuze.io.TextReader;
import com.mom.dfuze.ui.dedupe.DedupeDialog;
import com.mom.dfuze.ui.job.DataMappingDialog;


/**
 * UiController uses the MVC pattern to control how the UI should behave across the Dfuze application
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 * 
 *         Model View Controller pattern
 *
 */
public class UiController {

	private static final Logger LOG = LogManager.getLogger(UiController.class);
	private static MainFrame mainFrame;
	private static UserData userData;
	private static JobSelectDialog jobSelectDialog;
	final static Preferences pref = Preferences.userRoot();
	static String userDataFileName = "";

	protected UiController(MainFrame mainFrame) {
		UiController.mainFrame = mainFrame;
		UiController.userData = new UserData();
		jobSelectDialog = new JobSelectDialog(mainFrame, SystemDao.getJobs());
		
		try {
			JobPref.setup(SystemDao.getJobs());
		}catch(Exception e) {
			handle(e);
		}
		
	}

	public static void handle(Exception e) {
		LOG.error(e.getMessage());
		e.printStackTrace();
		displayMessage("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		//new ErrorDialog(e.getMessage());
	}
	
	public static void displayMessage(String title, String info, int option) {
		new MessageDialog(title, info, option);
	}

	public static UserData getUserData() {
		return userData;
	}

	public static MainFrame getMainFrame() {
		return mainFrame;
	}

	protected static class NewJobMenuItemHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				Runnable runner = new Runnable() {
					@Override
					public void run() {

						try {

							jobSelectDialog.setIsLoadDataPressed(false);
							jobSelectDialog.reset();
							mainFrame.getMnJob().setEnabled(false); // in case user presses new job again

							LOG.debug("New Job Menu pressed");

							jobSelectDialog.setLocationRelativeTo(UiController.mainFrame);
							jobSelectDialog.setVisible(true);

							// If the user clicked load
							if (jobSelectDialog.getIsLoadDataPressed()) {
								
								mainFrame.getMntmNewJob().setEnabled(false); // deEnable the option to start a new job in case it takes awhile to load

								// create the file chooser dialog with filters
								JFileChooser fileChooser = new JFileChooser(UserPrefs.getLastUsedFolder());
								fileChooser.setPreferredSize(new Dimension(800,600));
								Action details = fileChooser.getActionMap().get("viewTypeDetails");
								details.actionPerformed(null);

								//https://stackoverflow.com/questions/16429779/start-a-jfilechooser-with-files-ordered-by-date
								JTable table = SwingUtils.getDescendantsOfType(JTable.class, fileChooser).get(0);
								table.getModel().addTableModelListener( new TableModelListener()
								{
								    @Override
								    public void tableChanged(TableModelEvent e)
								    {
								        table.getModel().removeTableModelListener(this);
								        SwingUtilities.invokeLater( () -> table.getRowSorter().toggleSortOrder(3) );
								        SwingUtilities.invokeLater( () -> table.getRowSorter().toggleSortOrder(3) );
								    }
								});

								fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(String.join(" ", FileExtensions.FILE_IMPORT_EXTENSIONS_LIST), "accdb", "csv", "cert-1", "dat",
										"dbf", "mdb", "txt", "xlsx", "xlsm", "xls"));
								fileChooser.setAcceptAllFileFilterUsed(true);

								fileChooser.setMultiSelectionEnabled(true);
								
								

								int returnVal = fileChooser.showOpenDialog(UiController.mainFrame);

								if (returnVal == JFileChooser.APPROVE_OPTION) {

									UiController.mainFrame.setIsJobComplete(false);
									UiController.userData = new UserData(); // Reset the userData
									UiController.mainFrame.getJProgressBar().setIndeterminate(true);

									File[] files;

									files = fileChooser.getSelectedFiles();

									// Set the path for the lastUsedFolder;
									UserPrefs.setLastUsedFolder(files[0].getParent());// pref.put("DEFAULT_PATH", files[0].getParent());
									// setLastUsedFolder(files[0].getAbsolutePath());

									String tempUserDataFileName = (files.length > 1) ? "merged" : files[0].getName();

									if (tempUserDataFileName.contains(".")) // get all text before '.' if found
										tempUserDataFileName = tempUserDataFileName.substring(0, tempUserDataFileName.lastIndexOf('.'));

									// Set the file name of the userData
									setUserDataFileName(tempUserDataFileName);
									
									loadMergedData(files);
									// userData.load(headers, data);

									if (UiController.userData.getData() != null) {
										
										System.out.println("Hey the data isnt empty");
										UiController.mainFrame.getJProgressBar().setIndeterminate(false);
										JobPrefRemember jpr = JobPref.getJobPrefRemember(jobSelectDialog.getSelectedJob());
										DataMappingDialog dataMappingDialog = new DataMappingDialog(UiController.mainFrame, jobSelectDialog.getSelectedJob(), jpr);
										System.out.println("We've made the data mapping dialog");
										dataMappingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
										dataMappingDialog.setModalityType(ModalityType.APPLICATION_MODAL);
										dataMappingDialog.setVisible(true);
										System.out.println("We've made the data mapping dialog");
										
										if (dataMappingDialog.getIsLoadSuccessfullyPressed()) {
											UiController.mainFrame.setIsLoadComplete(true);
											
											// If the user wants to remember fields, remember them
											if(dataMappingDialog.getChckbxRememberFields().isSelected()) {
												jpr.setRemember(true);
												ArrayList<JComboBox<String>> comboBoxes = dataMappingDialog.getComboBoxes();
												ArrayList<LastUsedField> lufList = new ArrayList<>();
												String[] requiredFields = jobSelectDialog.getSelectedJob().getRunBehavior().getRequiredFields();
												for(int i = 0; i < comboBoxes.size(); ++i) {
													LastUsedField luf = new LastUsedField(requiredFields[i], userData.getInHeaders()[comboBoxes.get(i).getSelectedIndex()]);
													lufList.add(luf);
												}
												JobPref.updateLastUsedFieldValues(jobSelectDialog.getSelectedJob(), lufList);
											}else {
												jpr.setRemember(false);
											}
											
											JobPref.updateJobPrefRemember(jpr);
											
											displayMessage("Success", "Data loaded succesfully.\nClick Job->Run Job to continue.", JOptionPane.INFORMATION_MESSAGE);
											//JOptionPane.showMessageDialog(UiController.mainFrame, "Data loaded succesfully", "Success", JOptionPane.INFORMATION_MESSAGE);
										}

									}

								}

							}

						} catch (Exception e) {
							UiController.handle(e);
						} finally {
							mainFrame.getMntmNewJob().setEnabled(true); // reEnable the option to start a new job
							UiController.mainFrame.getJProgressBar().setIndeterminate(false);
							freeMemory();
						}
					}
				};

				Thread mainFrameThread = new Thread(runner, "Code Executer");
				mainFrameThread.start();
			} catch (Exception e) {
				UiController.handle(e);
			} finally {
				UiController.mainFrame.getJProgressBar().setIndeterminate(false);
				freeMemory();
			}
		}

	}

	private static void loadMergedData(File[] files) throws Exception {

		try {
			ArrayList<InputData> inputDataList = new ArrayList<>();
			TableSelectDialog tableSelectDialog = null;
			String databaseName = "";

			for (int i = 0; i < files.length; ++i) {
				tableSelectDialog = null;
				InputData inputData = null;
				databaseName = "";
				
				File file = files[i];
				
				String fileExt = file.getName();
				if (fileExt.contains("."))
					fileExt = fileExt.substring(fileExt.lastIndexOf("."), fileExt.length());

				if (fileExt.toLowerCase().equals(FileExtensions.DBF))
					inputData = DbfReader.read(file);
				else if (fileExt.toLowerCase().equals(FileExtensions.ACCDB) || fileExt.toLowerCase().equals(FileExtensions.MDB)) {
					String[] tableNames = AccessReader.readTableNames(file);
					tableSelectDialog = new TableSelectDialog(UiController.mainFrame, tableNames);
					tableSelectDialog.disableCreateHeaders();
					tableSelectDialog.setModalityType(ModalityType.APPLICATION_MODAL);
					
					if(tableNames.length > 1)
						tableSelectDialog.setVisible(true);
					
					if (tableSelectDialog.isNextPressed() || tableNames.length == 1) {
						for (String tableName : tableSelectDialog.getSelectedTables()) {
							inputData = AccessReader.read(file, tableName);
							if (jobSelectDialog.isChckbxAddFileNameSelected())
								inputData.addFileNameToHeadersAndData(file.getName() + " [" + tableName + "]");
							inputDataList.add(inputData);
						}

					} else {
						throw new Exception("Import cancelled, please restart the job");
					}

				} else if (fileExt.toLowerCase().equals(FileExtensions.XLSX) || fileExt.toLowerCase().equals(FileExtensions.XLSM)) {
					
					String password = "";
					boolean isEncrypted = ExcelReader.isXLSXEncrypted(file.getAbsolutePath());
					
					if(isEncrypted) {
						UserInputDialog newFieldDialog = new UserInputDialog(UiController.getMainFrame(), "Enter password for " + file.getName());
						newFieldDialog.setVisible(true);
						
						if(newFieldDialog.getIsNextPressed())
							password = newFieldDialog.getUserInput();
					}
					
					String[] tableNames = ExcelReader.readXLSXSheetNames(file, isEncrypted, password);
					tableSelectDialog = new TableSelectDialog(UiController.mainFrame, tableNames);
					tableSelectDialog.setModalityType(ModalityType.APPLICATION_MODAL);
					
					if(tableNames.length > 1)
						tableSelectDialog.setVisible(true);
					
					if (tableSelectDialog.isNextPressed() || tableNames.length == 1) {
						for (String tableName : tableSelectDialog.getSelectedTables()) {
							inputData = ExcelReader.readXLSX(file, tableName, tableSelectDialog.isCreateHeaderRowSelected(), isEncrypted, password);
							if (jobSelectDialog.isChckbxAddFileNameSelected())
								inputData.addFileNameToHeadersAndData(file.getName() + " [" + tableName + "]");
							inputDataList.add(inputData);
						}

					} else {
						throw new Exception("Import cancelled, please restart the job");
					}

				} else if (fileExt.toLowerCase().equals(FileExtensions.XLS)) {
					
					String password = "";
					boolean isEncrypted = ExcelReader.isXLSEncrypted(file.getAbsolutePath());
					
					if(isEncrypted) {
						UserInputDialog newFieldDialog = new UserInputDialog(UiController.getMainFrame(), "Enter password for " + file.getName());
						newFieldDialog.setVisible(true);
						
						if(newFieldDialog.getIsNextPressed())
							password = newFieldDialog.getUserInput();
					}
					
					String[] tableNames = ExcelReader.readXLSSheetNames(file, isEncrypted, password);
					tableSelectDialog = new TableSelectDialog(UiController.mainFrame, tableNames);
					tableSelectDialog.setModalityType(ModalityType.APPLICATION_MODAL);
					
					if(tableNames.length > 1)
						tableSelectDialog.setVisible(true);
					
					if (tableSelectDialog.isNextPressed() || tableNames.length == 1) {
						for (String tableName : tableSelectDialog.getSelectedTables()) {
							inputData = ExcelReader.readXLS(file, tableName, tableSelectDialog.isCreateHeaderRowSelected(), isEncrypted, password);
							if (jobSelectDialog.isChckbxAddFileNameSelected())
								inputData.addFileNameToHeadersAndData(file.getName() + " [" + tableName + "]");
							inputDataList.add(inputData);
						}
					} else {
						throw new Exception("Import cancelled, please restart the job");
					}

				} else if (fileExt.toLowerCase().equals(FileExtensions.CSV) || fileExt.toLowerCase().equals(FileExtensions.CERT1) ||fileExt.toLowerCase().equals(FileExtensions.TXT) || fileExt.toLowerCase().equals(FileExtensions.DAT)) {
					String previewText = TextReader.previewText(file);
					DelimiterSelectDialog delimiterSelectDialog = new DelimiterSelectDialog(UiController.mainFrame, previewText);
					
					// Check to see which delimiter is being used
					int countComma = 0, countTab = 0, countPipe = 0, countTilde = 0, countLineFeed = 0;
					
					char[] previewTextCharArray = previewText.toCharArray();
					for(char ch : previewTextCharArray) {
						switch(ch) {
						case ',':
							++countComma;
							break;
						case '\t':
							++countTab;
							break;
						case '|':
							++countPipe;
							break;
						case '~':
							++countTilde;
							break;
						case '\n':
							++countLineFeed;
						default:
							break;
						}
					}
					
					if(countTab > Collections.max(Arrays.asList(countComma, countPipe, countTilde, countLineFeed)))
						delimiterSelectDialog.setDelimiterToTab();
					else if(countPipe > Collections.max(Arrays.asList(countComma, countTab, countTilde, countLineFeed)))
						delimiterSelectDialog.setDelimiterField('|');
					else if(countTilde > Collections.max(Arrays.asList(countComma, countTab, countPipe, countLineFeed)))
						delimiterSelectDialog.setDelimiterField('~');
					else if(countLineFeed > Collections.max(Arrays.asList(countComma, countTab, countPipe))) {
						delimiterSelectDialog.getChckbxFixedWidth().setSelected(true);
						delimiterSelectDialog.disableDelimiters();
					}
					
					// Check if there is a header
					String[] previewLines = previewText.split("\r");

					String headerLine = previewLines[0];
					String[] headerLines = headerLine.split(String.valueOf(delimiterSelectDialog.getDelimiterField()));
					
					boolean hasHeaders = false;
					
					//final Pattern FIRST_NAME_PATTERN = Pattern.compile("^(?!.*([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*).*(fir.*[n][a][m]|[f](_|\\s|-)?[n][a][m]|[n][a][m].*[1]|[f][n]$)", Pattern.CASE_INSENSITIVE);
					//final Pattern FULL_NAME_PATTERN = Pattern.compile("^(?!.*([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*).*([f][u].*[n][a][m]|[a][d][d][r][e][s][s][e][e]|^[n][a][m][e]$)", Pattern.CASE_INSENSITIVE);
					//final Pattern ADDRESS_1_PATTERN = Pattern.compile("[a][d].*[1]|[1].*[a][d]|[a][d][d][r][e][s][s]$|[a][d][d]$|[s][t][r][e][e][t]$|[s][t][r][e][e][t].*[1]|[l][i][n][e].*[1]", Pattern.CASE_INSENSITIVE);
					//final Pattern CITY_PATTERN = Pattern.compile("[c][i][t][y]|[m][u][n][i]|[t][o][w]", Pattern.CASE_INSENSITIVE);
					//final Pattern PROVINCE_PATTERN = Pattern.compile("[p][r][o][v]|[s][t][a][t]", Pattern.CASE_INSENSITIVE);	
					//final Pattern POSTAL_CODE_PATTERN = Pattern.compile("[p][o][s][t]|[p][s][t]|(^[c][o][d][e])|[p](/|\\s|-|_)?[c](\\s+|$)|[z][i][p]|[p][c][o][d][e]", Pattern.CASE_INSENSITIVE);
					final Pattern ID_PATTERN = Pattern.compile("((^|\\s+|_)id(\\s+|$|_))|vip|loyalty|contactcode|customer(\\s+|_)?(id|no)", Pattern.CASE_INSENSITIVE); //Id
					final Pattern FSA_PATTERN = Pattern.compile("FSA", Pattern.CASE_INSENSITIVE);
					
					ArrayList<Pattern> patternList = new ArrayList<>();
					patternList.add(ID_PATTERN);
					patternList.add(Rental.FIRST_NAME_PATTERN);
					patternList.add(Rental.FULL_NAME_PATTERN);
					patternList.add(Rental.ADDRESS_1_PATTERN);
					patternList.add(Rental.CITY_PATTERN);
					patternList.add(Rental.PROVINCE_PATTERN);
					patternList.add(Rental.POSTAL_CODE_PATTERN);
					patternList.add(FSA_PATTERN);
					
					for(Pattern pattern : patternList) {
						for(String header : headerLines) {
							if(pattern.matcher(header).find()) {
								hasHeaders = true;
								break;
							}
						}
					}
					
					if(!hasHeaders)
						delimiterSelectDialog.setChckbxCreateHeaders(true);

					// Finally make the dialog visible
					delimiterSelectDialog.setVisible(true);

					if (delimiterSelectDialog.getIsNextPressed()) { // if the user has selected a delimiter
						boolean createHeaderRow = delimiterSelectDialog.getIsCreateHeaderRowSelected();


						if(delimiterSelectDialog.getChckbxFixedWidth().isSelected()) {
							String data = delimiterSelectDialog.getPreviewText();
							
							String[] rows = data.split("\r\n");

							boolean canContinue = false;

							if(rows.length > 0)
								if(rows[0].length() > 2)
									canContinue = true;

							if(!canContinue)
								throw new Exception("No '|' delimiter detected");


							String line = rows[0];
							line = line.replaceAll("\r|\n","");
							System.out.println(line.length());
							char delimiter = '|';
							
							int startIndex = 0, endIndex = 0;
							int delimiterCount = 0;

							ArrayList<int[]> fieldIndexes = new ArrayList<>();

							for(int j = 0; j < line.length(); ++j){
								
								char c = line.charAt(j);
								System.out.println(j + "\t" + "start");
								if(c == delimiter || j == line.length() - 1){
		
									if(startIndex == 0)
										endIndex = j;
									else if(j == line.length() - 1)
										endIndex = j - (delimiterCount - 1);
									else
										endIndex = j - delimiterCount;
										
									int[] fieldIndex = {startIndex, endIndex};
									
									//System.out.println(startIndex + "\t" + endIndex);
									
									fieldIndexes.add(fieldIndex);
									startIndex = endIndex;
									
									++delimiterCount;
									
								}
								System.out.println("end");

							}
							
							inputData = TextReader.readFixedWidth(file, fieldIndexes, createHeaderRow);

						} else {
							inputData = TextReader.read(file, delimiterSelectDialog.getDelimiterField(), createHeaderRow);
						}
						
					} else {// the user has cancelled the delimiter select dialog
						throw new Exception("Import cancelled, please restart the job");
					}
				} else {
					throw new Exception("The selected file type is not supported.");
				}

				// If tables are read we've already added our files.
				if (tableSelectDialog == null) {
					if (jobSelectDialog.isChckbxAddFileNameSelected())
						inputData.addFileNameToHeadersAndData(file.getName() + databaseName);
					inputDataList.add(inputData);
				}

			}

			System.out.println("Weve collected our files");

			// set to hold unique headers
			Set<String> mergedHeadersSet = new LinkedHashSet<>();
			Set<String> originalMergedHeadersSet = new LinkedHashSet<>();

			// hold the total size of all the records we want to merge
			int sumOfRecords = 0;

			// loop through our files to merge all the headers and find the sum of records
			for (int i = 0; i < inputDataList.size(); ++i) {
				String[] tempHeaders = inputDataList.get(i).getHeaders();
				for (int j = 0; j < tempHeaders.length; ++j) {
					if (mergedHeadersSet.add(tempHeaders[j].toLowerCase()))
						originalMergedHeadersSet.add(tempHeaders[j]);
				}

				// add the total sum of our records
				sumOfRecords += inputDataList.get(i).getData().length;
			}

			// create 2d array to hold our merged data
			String[][] mergedData = new String[sumOfRecords][mergedHeadersSet.size()];
			String[] mergedHeaders = new String[mergedHeadersSet.size()];
			mergedHeadersSet.toArray(mergedHeaders);

			// Original merged header values
			String[] originalMergedHeaders = new String[originalMergedHeadersSet.size()];
			originalMergedHeadersSet.toArray(originalMergedHeaders);

			// fill the merged data with "" so there are no null values
			for (String[] row : mergedData)
				Arrays.fill(row, "");

			// hold the location to add in the merged data
			int startingIndex = 0;

			// loop through our files again to merge the data to the correct columns
			for (int i = 0; i < inputDataList.size(); ++i) {
				String[] tempHeaders = inputDataList.get(i).getHeaders();
				String[][] tempData = inputDataList.get(i).getData();

				// loop through the current files headers
				for (int j = 0; j < tempHeaders.length; ++j) {

					// loop through our merged headers
					for (int k = 0; k < mergedHeaders.length; ++k) {

						// if our headers match
						if (tempHeaders[j].equalsIgnoreCase(mergedHeaders[k])) {

							// set the current row to the starting index to merge our data to in our merged file
							int currentRowToAdd = startingIndex;

							// for every record in our current file, fill in the matching value to the new merged column
							for (int l = 0; l < tempData.length; ++l) {
								mergedData[currentRowToAdd++][k] = tempData[l][j];
							}
						}
					}
				}
				// save the location of the next index to merge data to
				startingIndex += tempData.length;
			}

			userData.load(originalMergedHeaders, mergedData);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}


	protected static class RunJobItemHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {

			try {
				Runnable runner = new Runnable() {
					@Override
					public void run() {

						try {
							UiController.mainFrame.getJProgressBar().setIndeterminate(true);
							UiController.jobSelectDialog.getSelectedJob().run(UiController.userData); // performs unique algorithm and adds records to recordList in
							// userData

							if (userData.getRecordList().size() == 0)
								return;

							UiController.mainFrame.getJProgressBar().setIndeterminate(false);
							displayMessage("Success", "Job completed succesfully.\nClick Data->Export to save your data.", JOptionPane.INFORMATION_MESSAGE);
							//JOptionPane.showMessageDialog(UiController.mainFrame, "Job Complete", "Success", JOptionPane.INFORMATION_MESSAGE);
							UiController.mainFrame.setIsJobComplete(true);

						} catch (Exception e) {
							userData.setRecordList(new ArrayList<Record>());
							UiController.handle(e);
						} finally {
							UiController.mainFrame.getJProgressBar().setIndeterminate(false);
							freeMemory();
						}

					}
				};

				Thread mainFrameThread = new Thread(runner, "Code Executer");
				mainFrameThread.start();

			} catch (Exception e) {
				UiController.handle(e);
			} finally {
				freeMemory();
			}
		}

	}

	protected static class DataExportItemHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {

			try {
				ExportDataDialog exportDataDialog = new ExportDataDialog(UiController.mainFrame, userData.getExportHeaders(), userData.getExportData(userData.getRecordList()));
				exportDataDialog.getChckbxAddDfId().setEnabled(true);
				
				exportDataDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				exportDataDialog.setModalityType(ModalityType.APPLICATION_MODAL);
				exportDataDialog.setVisible(true);

			} catch (Exception e) {
				UiController.handle(e);
			} finally {
				freeMemory();
			}
		}
	}
	
	protected static class DataViewItemHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {

			try {
				
				ViewDataDialog viewDataDialog = new ViewDataDialog(UiController.mainFrame, userData.getExportData(userData.getRecordList()), userData.getExportHeaders());
				viewDataDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				viewDataDialog.setModalityType(ModalityType.APPLICATION_MODAL);
				viewDataDialog.setVisible(true);
				viewDataDialog.setTextFieldSearch(""); // set to blank so the accelerator is removed from search

			} catch (Exception e) {
				UiController.handle(e);
			} finally {
				freeMemory();
			}
		}
	}

	protected static class ToolsDedupeItemHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {

			DedupeDialog dedupeDialog = new DedupeDialog(UiController.mainFrame);
			dedupeDialog.setVisible(true);
			freeMemory();
		}
	}

	protected static class ToolsInkjetMakerItemHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {

			InkjetDialog inkjetDialog = new InkjetDialog(UiController.mainFrame);
			inkjetDialog.setVisible(true);
			freeMemory();
		}
	}

	protected static class ToolsEncodingCorrectionHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {

			EncodingCorrectionDialog characterEncodingCorrectionDialog = new EncodingCorrectionDialog(UiController.mainFrame);
			characterEncodingCorrectionDialog.setVisible(true);
			freeMemory();
		}
	}
	
	protected static class ToolsCasingConversionHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {

			CasingConversionDialog characterCasingDialog = new CasingConversionDialog(UiController.mainFrame);
			characterCasingDialog.setVisible(true);
			freeMemory();
		}
	}
	
	protected static class ToolsProofMakerHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {

			ProofMakerDialog proofDialog = new ProofMakerDialog(UiController.mainFrame);
			proofDialog.setVisible(true);
			freeMemory();
		}
	}
	
	protected static class ToolsSampleMakerHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {

			SampleMakerDialog sampleDialog = new SampleMakerDialog(UiController.mainFrame);
			sampleDialog.setVisible(true);
			freeMemory();
		}
	}

	protected static class QuitMenuItemHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			LOG.debug("Quit menu item pressed.");
			Instant endTime = Instant.now();
			LOG.info("End: " + endTime);
			LOG.info(String.format("Duration: %d ms", Duration.between(Dfuze.getStartTime(), endTime).toMillis()));
			System.exit(0);
		}
	}

	protected static class AboutMenuItemHandler implements ActionListener {

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			LOG.debug("About menu item pressed.");
			final String WEBSITE_PATH = "website/getting_started.html";
			// html content
			JEditorPane editorPane = new JEditorPane("text/html", "<html><body>Dfuze v3<br>Developed by Sean Johnson<br>Mail-O-Matic Services Ltd<br><br>Visit <a href=\"" + WEBSITE_PATH +"\">dfuze</a> for more info</body></html>");

			// handle link events
			editorPane.addHyperlinkListener(new HyperlinkListener()
			{
				@Override
				public void hyperlinkUpdate(HyperlinkEvent e)
				{
					if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
						try {
							File htmlFile = new File("website/getting_started.html");
							Desktop.getDesktop().browse(htmlFile.toURI());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			});
			editorPane.setEditable(false);
			
			JOptionPane.showMessageDialog(UiController.mainFrame, editorPane, "About Dfuze", JOptionPane.INFORMATION_MESSAGE);

			freeMemory();
		}
	}
	
	protected static class ComboBoxDeleteHandler implements KeyListener {
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

	protected static class MainFrameWindowEevntHandler extends WindowAdapter {

		/**
		 * Invoked when a window is in the process of being closed.
		 * The close operation can be overridden at this point.
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			/* Database.getTheInstance().shutdown(); */
			exit(0);
		}

	}

	public static void freeMemory() {
		System.gc();
	}

	public static void exit(int exitCode) {
		Instant endTime = Instant.now();
		LOG.info("End: " + endTime);
		LOG.info(String.format("Duration: %d ms", Duration.between(Dfuze.getStartTime(), endTime).toMillis()));
		System.exit(exitCode);
	}

	public static void setUserDataFileName(String dataFileName) {
		userDataFileName = dataFileName;
	}

	public static String getUserDataFileName() {

		return userDataFileName;
	}

	public static JobSelectDialog getJobSelectDialog() {
		return jobSelectDialog;
	}

}
