/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.connecthearing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.ExcelCell;
import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.io.FileExporter;
import com.mom.dfuze.io.XLSXWriter;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.dedupe.DedupeDialog2;


/**
 * NonWeeklyStep2 implements a RunBehavior for Connect Hearing Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/21/2023
 *
 */
public class NonWeeklyStep2 implements RunConnectHearingBehavior {

	private final String BEHAVIOR_NAME = "Non-Weekly Step 2";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.IN_ID.getName(),
			UserData.fieldName.PHONE1.getName(),
			UserData.fieldName.PHONE2.getName(),
			UserData.fieldName.MOBILE_PHONE.getName(),
			UserData.fieldName.EMAIL.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName(),
			UserData.fieldName.NCOA.getName(),
			UserData.fieldName.DNM.getName(),
			UserData.fieldName.DECEASED.getName(),
			UserData.fieldName.LIST_NUMBER.getName(),
			};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Removes records that moved out their original province</li>"
			+ "<li>Removes DNM, NIXIES, DECEASED (UC, UN, UD)</li>"
			+ "<li>Dedupes data</li>"
			+ "<li>Merges fname and lname into dfNam1</li>"
			+ "<li>Creates salutation into dfDearSal</li>"
			+ "<li>Adds 2 seeds</li>"
			+ "<li>Exports summary report for job</li>"
			+ "<li>Removes unnecessary iAddress fields</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load file after NCOA from Non-Weekly Step 1</li>"
			+ "<li>Sort exported file after Non-Weekly Step 2 completes</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
			+ "</html>";
	
	private static Pattern IADD_PATTERN = Pattern.compile("^(country|bagbun|dmc|listorder|breaks|oel|barcode)$", Pattern.CASE_INSENSITIVE);
	
	private ArrayList<Record> uncodedRecords = new ArrayList<>();
	private ArrayList<Record> outOfProvRecords = new ArrayList<>();
	private ArrayList<Record> nixieDnmDeceasedRecords = new ArrayList<>();
	private int dupesRemoved = 0;
	
	//
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		NonWeeklyStep1 step1Behaviour = getStep1Behavior();
		List<Record> step1Records = getStep1Records(step1Behaviour);

		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		
		for(Record record : userData.getRecordList()) {
			record.setNam1((record.getFstName() + " " + record.getLstName()).replaceAll("  ", " ").trim());
			createSalutation(record);
			record.setStatus("");
			record.setIsDupe(false);
			record.setDupeGroupId(0);
			record.setDupeGroupSize(0);
		}
		
		removeNcoaOutOfProv(userData, step1Records);
		removeNixieDnmDeceased(userData);
		setUncodedRecords(userData);
		dupe(userData);
		
		exportRemovals(userData);
		writeSummaryReport(step1Behaviour);
		
		addSeeds(userData);
		updateInData(userData);
		removeIAddressFields(userData);
		userData.setDfHeaders(new String[] {UserData.fieldName.NAME1.getName(), UserData.fieldName.DEAR_SALUTATION.getName()});
	}
	
	private void updateInData(UserData userData) {
		for(int i = 0; i < userData.getRecordList().size(); ++i) {
			Record record = userData.getRecordList().get(i);
			
			String[] inData = record.getDfInData();
			inData[userData.getInIdIndex()] = record.getInId();
			inData[userData.getPhone1Index()] = record.getPhone1();
			inData[userData.getPhone2Index()] = record.getPhone2();
			inData[userData.getEmailIndex()] = record.getEmail();
			inData[userData.getFstNameIndex()] = record.getFstName();
			inData[userData.getLstNameIndex()] = record.getLstName();
			inData[userData.getAdd1Index()] = record.getAdd1();
			inData[userData.getAdd2Index()] = record.getAdd2();
			inData[userData.getCityIndex()] = record.getCity();
			inData[userData.getProvIndex()] = record.getProv();
			inData[userData.getPCodeIndex()] = record.getPCode();
			inData[userData.getNcoaIndex()] = record.getNcoa();
			inData[userData.getDnmIndex()] = record.getDnm();
			inData[userData.getDeceasedIndex()] = record.getDeceased();
		
			record.setDfInData(inData);
		}
	}
	
	private void exportRemovals(UserData userData) throws Exception {
		userData.setDfHeaders(new String[] {UserData.fieldName.STATUS.getName()});
		
		if(outOfProvRecords.size() > 0) {
			File fileNcoa = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_NCOA_OUT_OF_PROV" + FileExtensions.XLSX);
			FileExporter.exportData(userData.getExportHeaders(), userData.getExportData(outOfProvRecords), fileNcoa);
		}
		
		if(nixieDnmDeceasedRecords.size() > 0) {
			File fileNixieDnmDeceased = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_NIXIES_DNM_DECEASED" + FileExtensions.XLSX);
			FileExporter.exportData(userData.getExportHeaders(), userData.getExportData(nixieDnmDeceasedRecords), fileNixieDnmDeceased);
		}
		
		if(uncodedRecords.size() > 0) {
			File fileUncoded = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_UNCODED" + FileExtensions.XLSX);
			FileExporter.exportData(userData.getExportHeaders(), userData.getExportData(uncodedRecords), fileUncoded);
		}
	}
	
	private void removeIAddressFields(UserData userData) {
		
		String[] headers = userData.getInHeaders();
		ArrayList<String> newHeaders = new ArrayList<>();
		
		for(int i = 0; i < headers.length; ++i)
			if(!IADD_PATTERN.matcher(headers[i]).find())
				newHeaders.add(headers[i]);
		
		for(int i = 0; i < userData.getRecordList().size(); ++i) {
			Record record = userData.getRecordList().get(i);
			String[] inData = record.getDfInData();
			ArrayList<String> newInData = new ArrayList<>();
			
			for(int j = 0; j < headers.length; ++j)
				if(!IADD_PATTERN.matcher(headers[j]).find())
					newInData.add(inData[j]);
			
			record.setDfInData(newInData.toArray(new String[0]));
		}
		
		userData.setInHeaders(newHeaders.toArray(new String[0]));
			
	}
	
	private void writeSummaryReport(NonWeeklyStep1 step1Behaviour) throws ApplicationException {
		File summaryReport = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_SUMMARY" + FileExtensions.XLSX);
		List<List<ExcelCell>> data = new ArrayList<>();
		
		final String IN_INDEX = "1:2";
		final String UNCODED_INDEX = "1:3";
		final String NCOA_INDEX = "1:4";
		final String BADADDRESS_INDEX = "1:5";
		final String DUPES_INDEX = "1:6";
		final String FINAL_INDEX = "1:7";
		
		final String IN_INDEX_VALUE = "2:2";
		final String UNCODED_INDEX_VALUE = "2:3";
		final String NCOA_INDEX_VALUE = "2:4";
		final String BADADDRESS_INDEX_VALUE = "2:5";
		final String DUPES_INDEX_VALUE = "2:6";
		final String FINAL_INDEX_VALUE = "2:7";
		
		final String SEEDS_INDEX = "3:7";
		
		Font defaultFont = new XSSFFont();
		
		XSSFCellStyle cellStyle = ExcelCell.createCellStyle();
		
		XSSFCellStyle borderCellStyle = ExcelCell.createCellStyle();
		borderCellStyle.setBorderTop(BorderStyle.THIN);
		borderCellStyle.setTopBorderColor(IndexedColors.BLACK.index);
				
		String summaryFormula = "SUM(C3:C7)";
		
		int columnsNum = 4;
		int rowsNum = 8;
		
		for(int row = 0; row < rowsNum; ++row) {
			ArrayList<ExcelCell> excelRow = new ArrayList<>();
			for(int column = 0; column < columnsNum; ++column) {

				String index = column + ":" + row;
				
				ExcelCell excelCell = null;
				
				switch(index) {
				case IN_INDEX:
					excelCell = new ExcelCell("Original", CellType.STRING, cellStyle, defaultFont, false);
					break;
				case UNCODED_INDEX:
					excelCell = new ExcelCell("NQ Removed", CellType.STRING, cellStyle, defaultFont, false);
					break;
				case NCOA_INDEX:
					excelCell = new ExcelCell("NCOA Removed", CellType.STRING, cellStyle, defaultFont, false);
					break;
				case BADADDRESS_INDEX:
					excelCell = new ExcelCell("Bad Address Removed", CellType.STRING, cellStyle, defaultFont, false);
					break;
				case DUPES_INDEX:
					excelCell = new ExcelCell("Dupes Removed", CellType.STRING, cellStyle, defaultFont, false);
					break;
				case FINAL_INDEX:
					excelCell = new ExcelCell("Final", CellType.STRING, cellStyle, defaultFont, false);
					break;
				case IN_INDEX_VALUE:
					excelCell = new ExcelCell(String.valueOf(step1Behaviour.recordsInNum), CellType._NONE, cellStyle, defaultFont, true);
					break;
				case UNCODED_INDEX_VALUE:
					excelCell = new ExcelCell("-" + String.valueOf(uncodedRecords.size()), CellType._NONE, cellStyle, defaultFont, true);
					break;
				case NCOA_INDEX_VALUE:
					excelCell = new ExcelCell("-" + String.valueOf(outOfProvRecords.size() + nixieDnmDeceasedRecords.size()), CellType._NONE, cellStyle, defaultFont, true);
					break;
				case BADADDRESS_INDEX_VALUE:
					excelCell = new ExcelCell("-" + String.valueOf(step1Behaviour.recordsRemoved.size()), CellType._NONE, cellStyle, defaultFont, true);
					break;
				case DUPES_INDEX_VALUE:
					excelCell = new ExcelCell("-" + String.valueOf(dupesRemoved), CellType._NONE, cellStyle, defaultFont, true);
					break;
				case FINAL_INDEX_VALUE:
					excelCell = new ExcelCell(summaryFormula, CellType._NONE, cellStyle, defaultFont, true);
					break;
				case SEEDS_INDEX:
					excelCell = new ExcelCell("plus 2 seeds", CellType._NONE, cellStyle, defaultFont, false);
					break;
				default:
					excelCell = new ExcelCell("", CellType._NONE, cellStyle, defaultFont, false);
					break;
				}
				
				if(column > 0 && column < (columnsNum - 1))
					if(row == (rowsNum - 1))
						excelCell.setCellStyle(borderCellStyle);
				
				excelRow.add(excelCell);
			}
			data.add(excelRow);
		}
		
		XLSXWriter.writeReportRaw(summaryReport, data);
	}
	
	
	private void setUncodedRecords(UserData userData) {
		final String UNCODED_STATUS = "Uncoded";
		
		for(int i = userData.getRecordList().size() - 1; i >= 0 ; --i) {
			if(userData.getRecordList().get(i).getListNum().trim().length() == 0) {
				Record record = userData.getRecordList().remove(i);
				record.setStatus(UNCODED_STATUS);
				uncodedRecords.add(record);
			}
				
		}
	}
	
	public void addSeeds(UserData userData) {
		Record template = userData.getRecordList().get(userData.getRecordList().size() - 1);
		
		String[] in1 = Arrays.copyOf(template.getDfInData(), template.getDfInData().length);
		String[] in2 = Arrays.copyOf(template.getDfInData(), template.getDfInData().length);
		
		Record seed1 = new Record.Builder(9999999, in1, "", "", "")
				.setFstName("Sam")
				.setLstName("Porter Move DM")
				.setNam1("Sam Porter Move DM")
				.setStoreId("CABC345")
				.setInId("C9999999")
				.setAdd1("7550 Lowland Dr")
				.setAdd2("")
				.setCity("Burnaby")
				.setProv("BC")
				.setPCode("V5J 5A4")
				.setPhone1("")
				.setPhone2("")
				.setMobilePhone("")
				.setEmail("")
				.setStatus("")
				.setNcoa("")
				.setDnm("")
				.setDeceased("")
				.build();
		
		Record seed2 = new Record.Builder(8888888, in2, "", "", "")
				.setFstName("Todd")
				.setLstName("Jones Move DM")
				.setNam1("Todd Jones Move DM")
				.setStoreId("CABC345")
				.setInId("C8888888")
				.setAdd1("Connect Hearing Canada")
				.setAdd2("301-1007 Langley St")
				.setCity("Victoria")
				.setProv("BC")
				.setPCode("V8W 1V7")
				.setPhone1("")
				.setPhone2("")
				.setMobilePhone("")
				.setEmail("")
				.setStatus("")
				.setNcoa("")
				.setDnm("")
				.setDeceased("")
				.build();
		
		createSalutation(seed1);
		createSalutation(seed2);
		
		userData.add(seed1);
		userData.add(seed2);
	}
	
	private void dupe(UserData userData) {
		
		userData.setDfHeaders(new String[] {
				UserData.fieldName.NAME1.getName(),
	    		UserData.fieldName.IS_DUPE.getName(),
	    		UserData.fieldName.DUPE_GROUP_ID.getName(),
	    		UserData.fieldName.DUPE_GROUP_SIZE.getName()
	    		});
		
		int beforeDupe = userData.getRecordList().size();
		
		DedupeDialog2 dedupeDialog = new DedupeDialog2(UiController.getMainFrame());
		int index = Arrays.asList(userData.getExportHeaders()).indexOf(UserData.fieldName.NAME1.getName());
		dedupeDialog.getComboBoxName1().setSelectedIndex(index);
		dedupeDialog.getTextFieldNameSimilarityPercentage().setText("90");
		dedupeDialog.getBtnRun().doClick();
		dedupeDialog.setVisible(true);
		
		int afterDupe = userData.getRecordList().size();
		
		dupesRemoved = beforeDupe - afterDupe;
	}
	
	private void createSalutation(Record record) {
		
		String salutation = "";
		
		String fname = record.getFstName();
		String[] fnameParts = fname.split("\\s+");
		
		for(int i = 0; i < fnameParts.length; ++i) {
			String part = fnameParts[i];
			
			if(part.replaceAll("[^a-zA-Z0-9]", "").length() > 1)
				salutation += part + " ";
		}
		
		salutation = salutation.replaceAll("  ", " ").trim();
		
		if(salutation.length() == 0)
			salutation = "Friend";
		
		record.setDearSal(salutation);
	}
	
	private NonWeeklyStep1 getStep1Behavior() throws Exception {
		NonWeeklyStep1 step1Behavior = null;
		
		Job jobRef = new ConnectHearingJob(new com.mom.dfuze.data.jobs.connecthearing.NonWeeklyStep1());
		for(Job tempJob : UiController.getJobSelectDialog().getJobs())
			if(tempJob.getClientName().equalsIgnoreCase(jobRef.getClientName()) && tempJob.getRunBehavior().getRunBehaviorName().equalsIgnoreCase(jobRef.getRunBehavior().getRunBehaviorName()))
				step1Behavior = (NonWeeklyStep1) tempJob.getRunBehavior();

		if(step1Behavior == null)
			throw new Exception("Internal error: Could not find Step 1 for reference");
		
		return step1Behavior;
	}
	
	private List<Record> getStep1Records(NonWeeklyStep1 step1Behavior) throws Exception {
		
		// If there are no records, step 1 was missed
		if(step1Behavior.finalRecordList.size() == 0)
			throw new Exception(String.format("Please run %s before %s without closing Dfuze", step1Behavior.getRunBehaviorName(), this.BEHAVIOR_NAME));
		
		return step1Behavior.finalRecordList;
	}
	
	private void removeNixieDnmDeceased(UserData userData) {
	
		final String DECEASED = "UD";
		final String DECEASED_STATUS = "Deceased";
		
		final String NIXIE = "UN";
		final String NIXIE_STATUS = "Nixie";
		
		final String DNM = "UC";
		final String DNM_STATUS = "Do not mail";
		
		for(int i = userData.getRecordList().size() - 1; i >= 0 ; --i) {
			Record record = userData.getRecordList().get(i);
			
			// check deceased first, nixie or dnm is irrelevant if they aren't alive
			if(record.getDeceased().toLowerCase().equalsIgnoreCase(DECEASED)) {
				record.setStatus(DECEASED_STATUS);
				nixieDnmDeceasedRecords.add(userData.getRecordList().remove(i));
				continue;
			}
			
			if(record.getNcoa().toLowerCase().equalsIgnoreCase(NIXIE)) {
				record.setStatus(NIXIE_STATUS);
				nixieDnmDeceasedRecords.add(userData.getRecordList().remove(i));
				continue;
			}
			
			if(record.getDnm().toLowerCase().equalsIgnoreCase(DNM)) {
				record.setStatus(DNM_STATUS);
				nixieDnmDeceasedRecords.add(userData.getRecordList().remove(i));
				continue;
			}
			
		}
	}
	
	private void removeNcoaOutOfProv(UserData userData, List<Record> step1Records) {
		final String NCOA_MOVER_IDENTIFIER = "a";
		final String MOVED_PROVINCE_STATUS = "Moved out of Province";
		
		for(int i = userData.getRecordList().size() - 1; i >= 0 ; --i) {
			Record record = userData.getRecordList().get(i);
			
			if(record.getNcoa().toLowerCase().startsWith(NCOA_MOVER_IDENTIFIER)) {
				for(Record step1Record : step1Records) {
					if(step1Record.getInId().equals(record.getInId())) {
						if(!step1Record.getProv().equalsIgnoreCase(record.getProv())) {
							record.setStatus(MOVED_PROVINCE_STATUS);
							outOfProvRecords.add(userData.getRecordList().remove(i));
						}
					}
				}
			}
				
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#getRunBehaviorName()
	 */
	@Override
	public String getRunBehaviorName() {
		return BEHAVIOR_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#getDescription()
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#getRequiredFields()
	 */
	@Override
	public String[] getRequiredFields() {
		return REQUIRED_FIELDS;
	}
	
	@Override
	public Boolean isFileNameRequired() {
		return false;
	}

}
