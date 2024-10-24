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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.io.FileExporter;
import com.mom.dfuze.io.FileIngestor;
import com.mom.dfuze.ui.DropdownSelectDialog;
import com.mom.dfuze.ui.UiController;


/**
 * NonWeeklyStep1 implements a RunBehavior for Connect Hearing Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/18/2023
 *
 */
public class NonWeeklyStep1 implements RunConnectHearingBehavior {

	private final String BEHAVIOR_NAME = "Non-Weekly Step 1";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.STORE_ID.getName(),
			UserData.fieldName.IN_ID.getName(),
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName(),
			UserData.fieldName.PHONE1.getName(),
			UserData.fieldName.PHONE2.getName(),
			UserData.fieldName.MOBILE_PHONE.getName(),
			UserData.fieldName.EMAIL.getName()
			};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Removes blank lines at the end of each database</li>"
			+ "<li>Re-aligns certain fields when misaligned</li>"
			+ "<li>Strips PC/Zip from Province, City into PC</li>"
			+ "<li>Strips PC from Add1, Add2 into PC</li>"
			+ "<li>Strips \"Kelowna\" from Address into City</li>"
			+ "<li>Fills blank city with \"Kelowna\" for FSAs (V1-PCWXYZ)</li>"
			+ "<li>Infers blank Provinces from PC</li>"
			+ "<li>Removes bad address records (blanks, emails, etc.)</li>"
			+ "<li>Looks up missing phones from master list</li>"
			+ "<li>Removes records with no phone numbers</li>"
			+ "<li>Removes dates in PC</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Add storeId field if not included and Run supplied file</li>"
			+ "<li>Keep the master client list handy for phone lookup</li>"
			+ "<li>Export data when done and dont close Dfuze</li>"
			+ "<li>Do NCOA/DNM/DECEASED in iAddress without any purging</li>"
			+ "<li>Do iAddress fake sort without any purging and export</li>"
			+ "<li>Run Generic Step 2 when done NCOA</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
			+ "</html>";
	
	public enum status {
		BAD_ADDRESS("Bad Address"),
		NO_CLINIC_PHONE("No Clinic/Clinic Phone");

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
	public List<Record> finalRecordList = new ArrayList<>();
	
	private int masterIdIndex = 0, masterPhoneIndex = 0;
	
	private static Pattern PC_PATTERN = Pattern.compile("(?i)[a-zA-Z]\\d[a-zA-Z][ -]?\\d[a-zA-Z]\\d", Pattern.CASE_INSENSITIVE);
	private static Pattern ZIP_PATTERN = Pattern.compile("(?i)[0-9]{5}(?:-[0-9]{4})?", Pattern.CASE_INSENSITIVE);
	private static Pattern GD_PATTERN = Pattern.compile("general delivery|gd|(^|\\s+)gen(\\s+|$)", Pattern.CASE_INSENSITIVE);
	private static Pattern SANTA_PATTERN = Pattern.compile("h(o|0)h(o|0)", Pattern.CASE_INSENSITIVE);
	private static Pattern EMAIL_PATTERN = Pattern.compile("(?<=[(^|\\s)])(\\d|\\D)+@.*\\.+(\\D\\D\\d|\\D\\D|\\D)(?=\\s|$)", Pattern.CASE_INSENSITIVE);
	private static String APT_REGEX = "((trlr|trailer|lot|apartment|appartement|apt|suite|suit|ste|spc|space|room|rm|office|ofc|unit|bureau|piece|(?<=^|\\s+)ph|(?<=^|\\s+)th)(?=\\s+|$|#|-|\\d+))";
	private static Pattern ID_HEADER_PATTERN = Pattern.compile("storecode|cliniccode", Pattern.CASE_INSENSITIVE);
	private static Pattern PHONE_HEADER_PATTERN = Pattern.compile("phone", Pattern.CASE_INSENSITIVE);
	private static Pattern PHONE_PATTERN = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$" 
		      + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$" 
		      + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$", Pattern.CASE_INSENSITIVE);
	
	//
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		recordsInNum = 0;
		recordsRemoved = new ArrayList<>();
		
		String[][] data = userData.getData();

		for(int i = 0; i < data.length; ++i) {
			String clinicId = data[i][userData.getStoreIdIndex()].trim();
			String customerId = data[i][userData.getInIdIndex()].trim();
			String add1 = data[i][userData.getAdd1Index()].trim();
			String add2 = data[i][userData.getAdd2Index()].trim();
			String city = data[i][userData.getCityIndex()].trim();
			String prov = data[i][userData.getProvIndex()].trim();
			String pCode = data[i][userData.getPCodeIndex()].trim();
			String phone1 = data[i][userData.getPhone1Index()].trim();
			String phone2 = data[i][userData.getPhone2Index()].trim();
			String mobile = data[i][userData.getMobilePhoneIndex()].trim();
			String email = data[i][userData.getEmailIndex()].trim();
			
			//fix misaligned data
			if(PHONE_PATTERN.matcher(email).find()) {
				if(userData.getEmailIndex() == userData.getPhone1Index() - 1) {
					pCode = prov;
					prov = city;
					city = add2;
					add2 = add1;
					add1 = mobile;
					mobile = phone2;
					phone2 = phone1;
					phone1 = email;
				}
			}
			
			int nonEmptyFields = 0;
			
			// Determine how many fields are populated for the record
			for(int j = 0; j < data[i].length; ++j)
				if(data[i][j].length() > 0)
					++nonEmptyFields;
			
			// If there are 2 or less populated fields, skip the record
			if(nonEmptyFields <= 2) {
				System.out.println(Arrays.toString(data[i]));
					continue;
			}
			
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setStoreId(clinicId)
					.setInId(customerId)
					.setAdd1(add1)
					.setAdd2(add2)
					.setCity(city)
					.setProv(prov)
					.setPCode(pCode)
					.setPhone1(phone1)
					.setPhone2(phone2)
					.setMobilePhone(mobile)
					.setEmail(email)
					.setStatus("")
					.build();
			
			userData.add(record); // add the record the recordList in userData
			++recordsInNum;
		}
		
		// save for report
		recordsInNum = userData.getRecordList().size();
		System.out.println(recordsInNum);

		// iterate backwards to safely remove bad addresses
		for(int i = userData.getRecordList().size() - 1; i >= 0; --i) {
			Record record = userData.getRecordList().get(i);
			
			fixDateInPc(record);
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
				removedRecord.setStatus(status.BAD_ADDRESS.getName());
				recordsRemoved.add(removedRecord);
				continue;
			}
			
			if(hasSanta(record))
				record.setPCode("");

			record.setProv(inferProvince(record));
			
			fixKelownaInAddress(record);
			fixMissingCityKelowna(record);
			fixMissingProvKelowna(record);
			
			if(isBlankCityProvPc(record)) {
				Record removedRecord = userData.getRecordList().remove(i);
				removedRecord.setStatus(status.BAD_ADDRESS.getName());
				recordsRemoved.add(removedRecord);
				continue;
			}

		}

		// remove records without a clinic phone in the master list
		int buttonPressed = JOptionPane.showConfirmDialog(UiController.getMainFrame(), "Do you need import the master store list to verify the clinic has a phone number?",
				"Master Phone verification", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (buttonPressed == JOptionPane.YES_OPTION)
			masterPhonePurge(userData);
			
		finalRecordList = userData.getRecordList();
		updateInData(userData);
		
		// set the Header fields that we have used
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.STATUS.getName()
				});
		
		File file = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_BAD_ADDRESS" + FileExtensions.XLSX);
		
		FileExporter.exportData(userData.getExportHeaders(), userData.getExportData(recordsRemoved), file);
		
		userData.setDfHeaders(new String[] {});
		
		finalRecordList = userData.getRecordList();

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
			inData[userData.getPhone1Index()] = record.getPhone1();
			inData[userData.getPhone2Index()] = record.getPhone2();
			inData[userData.getMobilePhoneIndex()] = record.getMobilePhone();
			
			record.setDfInData(inData);
		}
	}
	
	private void masterPhonePurge(UserData userData) throws Exception {

		List<List<String>> masterFile = FileIngestor.ingest();	// get the master file

		mapMasterIndexes(masterFile);

		HashMap<String, List<String>> masterPhoneMap = indexMasterPhones(masterFile);

		// match the clinicCode to the master clinicCode and remove if there is no phone number for the clinic
		for(int i = userData.getRecordList().size() - 1; i >= 0; --i) {
			Record record = userData.getRecordList().get(i);

			// if the record phone is invalid, look it up in masterPhoneMap
				if(!masterPhoneMap.containsKey(record.getStoreId())) {
					Record removedRecord = userData.getRecordList().remove(i);
					removedRecord.setStatus(status.NO_CLINIC_PHONE.getName());
					recordsRemoved.add(removedRecord);
				}
		}
		
	}
	
	private HashMap<String, List<String>> indexMasterPhones(List<List<String>> masterFile){
		HashMap<String, List<String>> masterPhoneMap = new HashMap<>();
		
		for(int i = 1; i < masterFile.size(); ++i) {
			
			String phone = masterFile.get(i).get(masterPhoneIndex);

			if(isPhoneValid(phone))
				masterPhoneMap.put(masterFile.get(i).get(masterIdIndex), masterFile.get(i));
		}
			
		return masterPhoneMap;
	}
	
	private boolean isPhoneValid(String phone) {
		phone = phone.replaceAll("[^1-9]", "");
		return phone.length() > 0;
	}

	private void mapMasterIndexes(List<List<String>> masterFile) throws Exception {
		String[] headers = masterFile.get(0).toArray(new String[0]);

		DropdownSelectDialog dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the StoreCode field for lookup.");

		for(int i = 0; i < headers.length; ++i) {
			if(ID_HEADER_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		masterIdIndex = dsd.getSelectedValueIndex();

		dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the phone field for lookup.");
		dsd.setValues(headers);

		for(int i = 0; i < headers.length; ++i) {
			if(PHONE_HEADER_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		masterPhoneIndex = dsd.getSelectedValueIndex();

	}
	
	private void fixDateInPc(Record record) {
		if(record.getPCode().contains("/"))
			record.setPCode("");
	}
	
	private void fixMissingProvKelowna(Record record) {
		if(record.getProv().isEmpty())
			if(record.getCity().toLowerCase().contains("kelowna"))
				record.setProv("BC");
	}
	
	private void fixMissingCityKelowna(Record record) {

		// if the city is already populated, return
		if(!record.getCity().replaceAll("[^\\p{L}]", "").isEmpty())
			return;

		String pc = record.getPCode().replaceAll("[^\\p{L}0-9]","").toLowerCase();

		if(pc.length() >= 3)
			pc = pc.substring(0, 3);

		switch(pc) {
		case "v1p":
		case "v1v":
		case "v1w":
		case "v1x":
		case "v1y":
		case "v1z":
			record.setCity("Kelowna");
			break;
		default:
			break;
		}
	}
	
	private void fixKelownaInAddress(Record record) {
		String add1 = record.getAdd1().toLowerCase().replaceAll("[^\\p{L}]", "");
		String add2 = record.getAdd2().toLowerCase().replaceAll("[^\\p{L}]", "");
		
		if(add1.equalsIgnoreCase("kelowna")) {
			record.setAdd1("");
			if(record.getCity().trim().length() <= 4)
				record.setCity("Kelowna");
		}
		
		if(add2.equalsIgnoreCase("kelowna")) {
			record.setAdd2("");
			if(record.getCity().trim().length() <= 4)
				record.setCity("Kelowna");
		}
	}
	
	private String inferProvince(Record record) {
		String prov = Common.pc2prov(record.getPCode());
		
		if(!prov.isEmpty() && record.getProv().replaceAll("[^\\p{L}]", "").length() != 2)
			if(!record.getPCode().toLowerCase().startsWith("x0x") && !record.getPCode().toLowerCase().startsWith("x1x")) //generic bad postal codes can't be trusted
				return prov;

		return record.getProv();
	}
	
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
			if(record.getPCode().replaceAll("[^\\p{L}0-9]", "").length() < 6) {
				record.setPCode(pCodeMatcher.group());
				record.setProv("");
				return;
			}
		}
		
		Matcher zipMatcher = ZIP_PATTERN.matcher(record.getProv());
		
		if(zipMatcher.find()) {
			if(record.getPCode().replaceAll("[^\\p{L}0-9]", "").length() < 5) {
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
			
			Matcher emailMatcher1 = EMAIL_PATTERN.matcher(add1);
			Matcher emailMatcher2 = EMAIL_PATTERN.matcher(add2);
			
			if(emailMatcher1.find())
				add1 = add1.replaceAll(emailMatcher1.group(), "");
			
			if(emailMatcher2.find())
				add2 = add2.replaceAll(emailMatcher2.group(), "");
			
			if(add1.replaceAll("[^0-9]", "").length() == 0 && add2.replaceAll("[^0-9]", "").length() == 0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isAddressAllNumbers(Record record) {
		String add1 = record.getAdd1().replaceAll("[^\\p{L}0-9]", "");
		String add2 = record.getAdd2().replaceAll("[^\\p{L}0-9]", "");
		
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
		return true;
	}

}
