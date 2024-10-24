/**
 * Project: Dfuze
 * File: Load.java
 * Date: Oct 15, 2020
 * Time: 5:52:18 PM
 */
package com.mom.dfuze.data.jobs.utility;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.io.TextWriter;
import com.mom.dfuze.ui.ParcelDialog;
import com.mom.dfuze.ui.UiController;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class Parcel implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Parcel";
	protected String[] REQUIRED_FIELDS = {
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName(),
			UserData.fieldName.COUNTRY.getName()
	};
	
	protected String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Creates the parcel import file for EST 2.0</li>"
			+ "<li>Only requires basic address fields</li>"
			+ "<li>Follow the 4 easy steps when prompted</li>"
			+ "<li>Automatically fixes CA/US country codes and US zipcodes</li>"
			+ "</ul>"
			+ "Instructions<br/><ol><li>If dif size pkgs, create weight/length/width/height fields in data</li></ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
			+"</html>";
	
	private final Pattern US_COUNTRY = Pattern.compile("^us|uniteds|america", Pattern.CASE_INSENSITIVE);
	private final Pattern CA_COUNTRY = Pattern.compile("^can$|^cana|cdn", Pattern.CASE_INSENSITIVE);
	
	private final String US = "US";
	private final String CA = "CA";
	
	private final int RECORD_TYPE = 3;
	private final int FIELD_COUNT = 44;
	
	public enum parcelField {
		RECORD_TYPE(0),
		ORDER_ID(1),
		CLIENT_ID(2),
		TITLE(3),
		FIRST_NAME(4),
		LAST_NAME(5),
		DEPARTMENT(6),
		COMPANY(7),
		ADDITIONAL_ADD(8),
		ADDRESS1(9),
		ADDRESS2(10),
		CITY(11),
		PROVINCE(12),
		PC(13),
		COUNTRY(14),
		PHONE(15),
		FAX(16),
		EMAIL(17),
		WEIGHT(18),
		SERVICE(19),
		LENGTH(20),
		WIDTH(21),
		HEIGHT(22),
		DOCUMENT(23),
		OVERSIZE(24),
		DELIVERY_CONFIRM(25),
		SIGNATURE_CONFIRM(26),
		PLACEHOLDER_1(27),
		DONT_SAFEDROP(28),
		CARD_PICKUP(29),
		PROVE_AGE_18(30),
		PROVE_AGE_19(31),
		LEAVE_AT_DOOR(32),
		REGISTERED(33),
		PROVE_IDENTITY(34),
		PLACEHOLDER_2(35),
		DELIVER_TO_PO(36),
		DESTINATION_PO_ID(37),
		PLACEHOLDER_3(38),
		PLACEHOLDER_4(39),
		NOTIFY_RECIPIENT(40),
		INSURED_AMOUNT(41),
		COD_VALUE(42),
		PLACEHOLDER_5(43);
		
		int index;
		
		private parcelField(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
	};

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

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) {

		String[][] data = userData.getData();

		int counter = -1;
		for (int i = 0; i < data.length; i++) {
			
			String add1 = data[i][userData.getAdd1Index()];
			String add2 = data[i][userData.getAdd2Index()];
			String city = data[i][userData.getCityIndex()];
			String prov = data[i][userData.getProvIndex()];
			String pcode = data[i][userData.getPCodeIndex()];
			String cntry = data[i][userData.getCntryIndex()];
			
			// fix the country code
			cntry = fixCountry(cntry);
			
			// if no country, try to infer
			if(cntry.replaceAll(" ", "").length() == 0 || cntry.replaceAll(" ", "").length() > 2)
				cntry = inferCountry(pcode);
			
			// fix US zipcode
			if(cntry.equalsIgnoreCase(US))
				pcode = Common.fixZip(pcode);

			Record record = new Record.Builder(++counter, data[i], "", "", "")
					.setAdd1(add1).setAdd2(add2).setCity(city).setProv(prov).setPCode(pcode).setCntry(cntry).build();

			userData.add(record); // add the record the recordList in userData

		}

		// set the Header fields that we have used
		userData.setDfHeaders(new String[] {});

		ParcelDialog parcelDialog = new ParcelDialog(UiController.getMainFrame());
		parcelDialog.setVisible(true);
		
		// Time to build the file
		String[][] parcelData = new String[userData.getRecordList().size()][FIELD_COUNT];
		
		for (String[] row : parcelData)
			Arrays.fill(row, "");
		
		for (String[] row : parcelData)
			for(int i = parcelField.WEIGHT.getIndex(); i < row.length; ++i)
				row[i] = "0";
		
		// init some values we will be reusing, all fields are assumed to be from the source file except the ones mapped
		String serviceCode = parcelDialog.getComboBoxParcelService().getSelectedItem().toString();
		String serviceCodeUSA = String.valueOf(ParcelDialog.parcelService.EXPEDITED_PARCEL_USA.getServiceCode());
		
		if(serviceCode.equalsIgnoreCase(ParcelDialog.parcelService.EXPEDITED_PARCEL.getName())) {
			serviceCode = String.valueOf(ParcelDialog.parcelService.EXPEDITED_PARCEL.getServiceCode());
		}else if(serviceCode.equalsIgnoreCase(ParcelDialog.parcelService.PRIORITY.getName())) {
			serviceCode = String.valueOf(ParcelDialog.parcelService.PRIORITY.getServiceCode());
		}else if(serviceCode.equalsIgnoreCase(ParcelDialog.parcelService.REGULAR_PARCEL.getName())) {
			serviceCode = String.valueOf(ParcelDialog.parcelService.REGULAR_PARCEL.getServiceCode());
		}else if(serviceCode.equalsIgnoreCase(ParcelDialog.parcelService.XPRESSPOST.getName())) {
			serviceCode = String.valueOf(ParcelDialog.parcelService.XPRESSPOST.getServiceCode());
			serviceCodeUSA = String.valueOf(ParcelDialog.parcelService.XPRESSPOST_USA.getServiceCode());
		}
		
		String orderID = parcelDialog.getTextFieldReference().getText();
		
		if(parcelDialog.getComboBoxAppendToReference().getSelectedIndex() == -1)
			if(!orderID.isBlank())
				orderID += " - ";
		
		String signatureRequired = (parcelDialog.getChckbxSignatureRequired().isSelected()) ? "1" : "0";
		String cardForPickup = (parcelDialog.getChckbxCardForPickup().isSelected()) ? "1" : "0";
		String doNotSafeDrop = (parcelDialog.getChckbxDoNotSafeDrop().isSelected()) ? "1" : "0";
		String leaveAtDoor = (parcelDialog.getChckbxLeaveAtDoor().isSelected()) ? "1" : "0";
		
		for(int i = 0; i < userData.getRecordList().size(); ++i) {
				Record record = userData.getRecordList().get(i);
			
				parcelData[i][parcelField.RECORD_TYPE.getIndex()] = String.valueOf(RECORD_TYPE);
				parcelData[i][parcelField.SERVICE.getIndex()] = serviceCode;	
				parcelData[i][parcelField.ORDER_ID.getIndex()] = (parcelDialog.getComboBoxAppendToReference().getSelectedIndex() > -1) ? (orderID + " " + record.getDfInData()[parcelDialog.getComboBoxAppendToReference().getSelectedIndex()].trim() + " - " + (i+1)).trim() : orderID + (i+1);
				parcelData[i][parcelField.SIGNATURE_CONFIRM.getIndex()] = signatureRequired;
				parcelData[i][parcelField.CARD_PICKUP.getIndex()] = cardForPickup;
				parcelData[i][parcelField.DONT_SAFEDROP.getIndex()] = doNotSafeDrop;
				parcelData[i][parcelField.LEAVE_AT_DOOR.getIndex()] = leaveAtDoor;
				
				parcelData[i][parcelField.TITLE.getIndex()] = getInDataValue(parcelDialog.getComboBoxPrefix().getSelectedIndex(), record);
				parcelData[i][parcelField.FIRST_NAME.getIndex()] = getInDataValue(parcelDialog.getComboBoxFirstName().getSelectedIndex(), record);
				parcelData[i][parcelField.LAST_NAME.getIndex()] = getInDataValue(parcelDialog.getComboBoxLastName().getSelectedIndex(), record);
				parcelData[i][parcelField.COMPANY.getIndex()] = getInDataValue(parcelDialog.getComboBoxCompany().getSelectedIndex(), record);
				parcelData[i][parcelField.PHONE.getIndex()] = getInDataValue(parcelDialog.getComboBoxTelephone().getSelectedIndex(), record);
				parcelData[i][parcelField.FAX.getIndex()] = getInDataValue(parcelDialog.getComboBoxFax().getSelectedIndex(), record);
				parcelData[i][parcelField.EMAIL.getIndex()] = getInDataValue(parcelDialog.getComboBoxEmail().getSelectedIndex(), record);
				
				parcelData[i][parcelField.ADDRESS1.getIndex()] = record.getAdd1();
				parcelData[i][parcelField.ADDRESS2.getIndex()] = record.getAdd2();
				parcelData[i][parcelField.CITY.getIndex()] = record.getCity();
				parcelData[i][parcelField.PROVINCE.getIndex()] = record.getProv();
				parcelData[i][parcelField.PC.getIndex()] = record.getPCode();
				parcelData[i][parcelField.COUNTRY.getIndex()] = record.getCntry();
				
				// update USA records
				if(record.getCntry().equalsIgnoreCase(US)) {
					parcelData[i][parcelField.SERVICE.getIndex()] = serviceCodeUSA;
					if(parcelData[i][parcelField.PHONE.getIndex()].length() == 0)
						parcelData[i][parcelField.PHONE.getIndex()]= "0000000000";
				}

				if(parcelDialog.getChckbxUseStaticDimensions().isSelected()) {
					parcelData[i][parcelField.WEIGHT.getIndex()] = parcelDialog.getTextFieldStaticWeight().getText();
					parcelData[i][parcelField.LENGTH.getIndex()] = parcelDialog.getTextFieldStaticLength().getText();
					parcelData[i][parcelField.WIDTH.getIndex()] = parcelDialog.getTextFieldStaticWidth().getText();
					parcelData[i][parcelField.HEIGHT.getIndex()] = parcelDialog.getTextFieldStaticHeight().getText();

				} else {
					parcelData[i][parcelField.WEIGHT.getIndex()] = getInDataValue(parcelDialog.getComboBoxWeight().getSelectedIndex(), record);
					parcelData[i][parcelField.LENGTH.getIndex()] = getInDataValue(parcelDialog.getComboBoxLength().getSelectedIndex(), record);
					parcelData[i][parcelField.WIDTH.getIndex()] = getInDataValue(parcelDialog.getComboBoxWidth().getSelectedIndex(), record);
					parcelData[i][parcelField.HEIGHT.getIndex()] = getInDataValue(parcelDialog.getComboBoxHeight().getSelectedIndex(), record);
				}
					
		}
		
		try {
			TextWriter.write(parcelDialog.getParcelFile(), ',', false, new String[0], parcelData);
		} catch (ApplicationException e) {
			UiController.handle(e);
		}

	}
	
	private String getInDataValue(int index, Record record) {
		if(index > -1)
			return record.getDfInData()[index];
		
		return "";
	}
	
	private String inferCountry(String pc) {
		if(Validators.isValidCanPC(pc))
			return CA;
		
		if(Validators.isValidUSZip(pc))
			return US;
		
		return "";
	}
	
	private String fixCountry(String country) {
		Matcher matcherUS = US_COUNTRY.matcher(country.replaceAll("[^a-zA-Z]", ""));
		Matcher matcherCA = CA_COUNTRY.matcher(country.replaceAll("[^a-zA-Z]", ""));
		
		if(matcherCA.find())
			return CA;
		else if(matcherUS.find())
			return US;
		
		return country;
	}

}
