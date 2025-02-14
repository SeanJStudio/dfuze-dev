/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.io.FileExporter;

import com.mom.dfuze.ui.UiController;


/**
 * NonWeeklyStep1 implements a RunBehavior for Connect Hearing Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/18/2023
 *
 */
public class RemoveBadAddresses implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Remove/Clean Bad Addresses";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName()
			};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Strips PC/Zip from Province, City into PC</li>"
			+ "<li>Strips PC from Add1, Add2 into PC</li>"
			+ "<li>Infers blank Provinces from PC</li>"
			+ "<li>Removes and exports bad address records (blanks, etc.)</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Correct data in iAddress before running</li>"
			+ "<li>Load corrected data and run</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
			+ "</html>";
	
	public enum status {
		BAD_ADDRESS("Bad Address (Text Only/Blank)"),
		BAD_ADDRESS_NUMERIC("Bad Address (Numeric Only)"),
		BLANK_CITY_PROV_PC("Bad Address (No City, Province and Postal Code)"),
		NO_PC("Bad Address (Bad/No Postal Code)");
		

		String name;

		private status(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public ArrayList<Record> recordsRemoved;
	public int recordsInNum = 0;
	
	private static Pattern PC_PATTERN = Pattern.compile("(?i)[a-zA-Z]\\d[a-zA-Z]\\d[a-zA-Z]\\d", Pattern.CASE_INSENSITIVE);
	private static Pattern ZIP_PATTERN = Pattern.compile("(?i)[0-9]{5}(?:[0-9]{4})?", Pattern.CASE_INSENSITIVE);
	private static Pattern GD_PATTERN = Pattern.compile("general delivery|gd|(^|\\s+)gen(\\s+|$)", Pattern.CASE_INSENSITIVE);
	private static Pattern SANTA_PATTERN = Pattern.compile("h(o|0)h(o|0)", Pattern.CASE_INSENSITIVE);
	//private static Pattern UNKNOWN_PATTERN = Pattern.compile("(^|\\s+)(unknown|unk|address|null|blank|need new)(\\s+|$)", Pattern.CASE_INSENSITIVE);
	private static String APT_REGEX = "((trlr|trailer|lot|apartment|appartement|apt|suite|suit|ste|spc|space|room|rm|office|ofc|unit|bureau|piece|(?<=^|\\s+)ph|(?<=^|\\s+)th)(?=\\s+|$|#|-|\\d+))";
	
	//
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		recordsRemoved = new ArrayList<>();
		
		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		initStatus(userData);
		
		// save for report
		recordsInNum = userData.getRecordList().size();

		// iterate backwards to safely remove bad addresses
		for(int i = userData.getRecordList().size() - 1; i >= 0; --i) {
			Record record = userData.getRecordList().get(i);

			fixPcZipInProv(record); 
			fixPcZipInCity(record);
			fixPcInAddress(record);
			
			if(isAddressBlank(record)) {
				Record removedRecord = userData.getRecordList().remove(i);
				removedRecord.setStatus(status.BAD_ADDRESS.getName());
				recordsRemoved.add(removedRecord);
				continue;
			}

			if(isAddressAllNumbers(record)) {
				Record removedRecord = userData.getRecordList().remove(i);
				removedRecord.setStatus(status.BAD_ADDRESS_NUMERIC.getName());
				recordsRemoved.add(removedRecord);
				continue;
			}
			
			if(hasSanta(record))
				record.setPCode("");

			record.setProv(inferProvince(record));
			
			if(isBlankCityProvPc(record)) {
				Record removedRecord = userData.getRecordList().remove(i);
				removedRecord.setStatus(status.BLANK_CITY_PROV_PC.getName());
				recordsRemoved.add(removedRecord);
				continue;
			}
			
			if(!hasPC(record)) {
				Record removedRecord = userData.getRecordList().remove(i);
				removedRecord.setStatus(status.NO_PC.getName());
				recordsRemoved.add(removedRecord);
				continue;
			}
				
			
			/*if(isAddressUnknown(record)) {
				Record removedRecord = userData.getRecordList().remove(i);
				removedRecord.setStatus(status.BAD_ADDRESS.getName());
				recordsRemoved.add(removedRecord);
				continue;
			}*/

		}
			
		updateInData(userData);
		exportRemovals(userData);
		userData.setDfHeaders(new String[] {});
	}
	
	private void exportRemovals(UserData userData) throws Exception {
		userData.setDfHeaders(new String[] {UserData.fieldName.STATUS.getName()});
		
		if(recordsRemoved.size() > 0) {
			File file = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_BAD_ADDRESSES" + FileExtensions.XLSX);
			FileExporter.exportData(userData.getExportHeaders(), userData.getExportData(recordsRemoved), file);
			JOptionPane.showMessageDialog(UiController.getMainFrame(), String.format("%d bad address records removed.", recordsRemoved.size()), "Results", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(UiController.getMainFrame(), String.format("No bad addresses found", recordsRemoved.size()), "Results", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void initStatus(UserData userData) {
		for(int i = 0; i < userData.getRecordList().size(); ++i)
			userData.getRecordList().get(i).setStatus("");
	}	
	
	private void updateInData(UserData userData) {	
		
		for(int i = 0; i < userData.getRecordList().size(); ++i) {
			Record record = userData.getRecordList().get(i);
			
			String[] inData = record.getDfInData();
			
			inData[userData.getAdd1Index()] = record.getAdd1();
			inData[userData.getAdd2Index()] = record.getAdd2();
			inData[userData.getCityIndex()] = record.getCity();
			inData[userData.getProvIndex()] = record.getProv();
			inData[userData.getPCodeIndex()] = record.getPCode();
		
			record.setDfInData(inData);
		}
	}
	
	private boolean hasPC(Record record) {
		Matcher pCodeMatcher = PC_PATTERN.matcher(record.getPCode().replaceAll("[^A-Za-z0-9]", ""));
		
		if(pCodeMatcher.find())
			return true;
		
		Matcher zipMatcher = ZIP_PATTERN.matcher(record.getPCode());
		
		if(zipMatcher.find())
			return true;
		
		return false;
	}
	
	private String inferProvince(Record record) {
		String prov = Common.pc2prov(record.getPCode());
		
		if(!prov.isEmpty() && record.getProv().replaceAll("[^a-zA-Z]", "").length() != 2)
			if(!record.getPCode().toLowerCase().startsWith("x0x") && !record.getPCode().toLowerCase().startsWith("x1x")) //generic bad postal codes can't be trusted
				return prov;

		return record.getProv();
	}
	
	/*private boolean isAddressUnknown(Record record) {
		Matcher unknownAddressMatcher1 = UNKNOWN_PATTERN.matcher(record.getAdd1());
		Matcher unknownAddressMatcher2 = UNKNOWN_PATTERN.matcher(record.getAdd2());
		
		if(unknownAddressMatcher1.find())
			return true;
		
		if(unknownAddressMatcher2.find())
			return true;
		
		return false;
	}*/
	
	private boolean isBlankCityProvPc(Record record) {
		if(record.getCity().isEmpty() && record.getProv().isEmpty() && record.getPCode().isEmpty())
			return true;
		
		return false;
	}
		
	private boolean hasSanta(Record record) {
		if(SANTA_PATTERN.matcher(record.getPCode()).find())
			return true;
		
		return false;
	}
	
	private void fixPcZipInProv(Record record) {
		Matcher pCodeMatcher = PC_PATTERN.matcher(record.getProv());
		
		if(pCodeMatcher.find()) {
			if(record.getPCode().replaceAll("[^a-zA-Z0-9]", "").length() < 6) {
				record.setPCode(pCodeMatcher.group());
				record.setProv("");
				return;
			}
		}
		
		Matcher zipMatcher = ZIP_PATTERN.matcher(record.getProv());
		
		if(zipMatcher.find()) {
			if(record.getPCode().replaceAll("[^a-zA-Z0-9]", "").length() < 5) {
				record.setPCode(zipMatcher.group());
				record.setProv("");
				return;
			}
		}
		
	}
	
	private void fixPcZipInCity(Record record) {
		Matcher pCodeMatcher = PC_PATTERN.matcher(record.getCity());
		
		if(pCodeMatcher.find()) {
			if(record.getPCode().isEmpty()) {
				record.setPCode(pCodeMatcher.group());
				record.setCity(record.getCity().replaceAll(pCodeMatcher.group(), ""));
				return;
			}
		}
		
		Matcher zipMatcher = ZIP_PATTERN.matcher(record.getCity());
		
		if(zipMatcher.find()) {
			if(record.getPCode().isEmpty()) {
				record.setPCode(zipMatcher.group());
				record.setCity(record.getCity().replaceAll(zipMatcher.group(), ""));
				return;
			}
		}
		
	}
	
	private void fixPcInAddress(Record record) {
		Matcher pCodeMatcher1 = PC_PATTERN.matcher(record.getAdd1());
		Matcher pCodeMatcher2 = PC_PATTERN.matcher(record.getAdd2());
		
		if(pCodeMatcher1.find()) {
			if(record.getPCode().isEmpty()) {
				record.setPCode(pCodeMatcher1.group());
				record.setAdd1(record.getAdd1().replaceAll(pCodeMatcher1.group(), ""));
			}
		}
		
		if(pCodeMatcher2.find()) {
			if(record.getPCode().isEmpty()) {
				record.setPCode(pCodeMatcher2.group());
				record.setAdd2(record.getAdd2().replaceAll(pCodeMatcher2.group(), ""));
			}
		}
		
	}
	
	private boolean isAddressBlank(Record record) {
		String add1 = record.getAdd1().toLowerCase();
		String add2 = record.getAdd2().toLowerCase();
		
		if(!GD_PATTERN.matcher(add1).find() && !GD_PATTERN.matcher(add2).find()) {
			add1 = add1.replaceAll(APT_REGEX, "").trim();
			add2 = add2.replaceAll(APT_REGEX, "").trim();
			
			Matcher pCodeMatcher1 = PC_PATTERN.matcher(add1);
			Matcher pCodeMatcher2 = PC_PATTERN.matcher(add2);
			
			if(pCodeMatcher1.find())
				add1 = add1.replaceAll(pCodeMatcher1.group(), "");
			
			if(pCodeMatcher2.find())
				add2 = add2.replaceAll(pCodeMatcher2.group(), "");
			
			if(add1.replaceAll("[^0-9]", "").length() == 0 && add2.replaceAll("[^0-9]", "").length() == 0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isAddressAllNumbers(Record record) {
		String add1 = record.getAdd1().replaceAll("[^a-zA-Z0-9]", "");
		String add2 = record.getAdd2().replaceAll("[^a-zA-Z0-9]", "");
		
		if(add1.length() == add1.replaceAll("[^0-9]", "").length()) {
			if(add2.length() == add2.replaceAll("[^0-9]", "").length()) {
				return true;
			}
		}
		
		return false;
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
