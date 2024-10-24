/**
 * Project: Dfuze
 * File: Load.java
 * Date: Oct 15, 2020
 * Time: 5:52:18 PM
 */
package com.mom.dfuze.data.jobs.utility;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.io.TextWriter;
import com.mom.dfuze.ui.RegisteredMailDialog;
import com.mom.dfuze.ui.UiController;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class RegisteredMail implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Registered Mail";
	protected String[] REQUIRED_FIELDS = {
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName(),
			UserData.fieldName.COUNTRY.getName(),
			UserData.fieldName.LIST_NUMBER.getName()
	};
	
	protected String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Creates the Registered Mail import file for EST 2.0</li>"
			+ "<li>Follow the 3 easy steps when prompted</li>"
			+ "<li>Automatically fixes CA country code</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Ensure ListNumber is unique and in ascending order</li>"
			+ "<li>Candian addresses allowed only</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) + "</html>";
			
	
	private final Pattern US_COUNTRY = Pattern.compile("^us|uniteds|america", Pattern.CASE_INSENSITIVE);
	private final Pattern CA_COUNTRY = Pattern.compile("^can$|^cana|cdn", Pattern.CASE_INSENSITIVE);
	
	private final String US = "US";
	private final String CA = "CA";
	
	private final int RECORD_TYPE = 12;
	private final int FIELD_COUNT = 23;
	
	private final String PPC_DEPOT_CODE = "I186";
	
	public enum registeredMailField {
		RECORD_TYPE(0),
		PLACEHOLDER(1),
		RECORD_ID(2),
		DEPOSIT_LOCATION(3),
		DATE_OF_DEPOSIT(4),
		ARTICLE_NUMBER(5),
		WEIGHT(6),
		COST_CENTRE(7),
		ITEM_COVERAGE_AMOUNT(8),
		ORDER_ID_REF(9),
		ITEM_REFERENCE_2(10),
		NAME(11),
		COMPANY(12),
		ADDRESS1(13),
		ADDRESS2(14),
		CITY(15),
		PROVINCE(16),
		PC(17),
		COUNTRY(18),
		PHONE(19),
		EMAIL1(20),
		EMAIL2(21),
		EMAIL_NOTIFICATION(22);
		
		int index;
		
		private registeredMailField(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
	};
	
	public enum emailNotification {
		ALL(0),
		SHIP_ONLY(1),
		EXCEPTION_ONLY(2),
		DELIVERY_ONLY(3);
		
		int index;
		
		private emailNotification(int index) {
			this.index = index;
		}
		
		public int getValue() {
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
	public void run(UserData userData) throws ApplicationException {

		String[][] data = userData.getData();

		int previousListNum = -9999;
		HashSet<String> listNumSet = new HashSet<>();
		
		int counter = -1;
		for (int i = 0; i < data.length; i++) {
			
			String add1 = data[i][userData.getAdd1Index()];
			String add2 = data[i][userData.getAdd2Index()];
			String city = data[i][userData.getCityIndex()];
			String prov = data[i][userData.getProvIndex()];
			String pcode = data[i][userData.getPCodeIndex()];
			String cntry = data[i][userData.getCntryIndex()];
			String listNum = data[i][userData.getListNumIndex()].replaceAll("[^0-9]", "");
			
			// Ensure the postal code is populated
			if(!isValidPC(pcode))
				throw new ApplicationException(String.format("Invalid postal code on row %d.", (i+1)));
			
			//Ensure the list number is actually a number
			if(!Validators.isWholeNumber(listNum))
				throw new ApplicationException(String.format("Invalid %s on row %d.", UserData.fieldName.LIST_NUMBER.getName(), (i+1)));
			
			// Ensure the data is in ascending order
			if(previousListNum > Integer.parseInt(listNum))
				throw new ApplicationException(String.format("%s isn't in ascending order starting on row %d.", UserData.fieldName.LIST_NUMBER.getName(), (i+1)));
			
			// Ensure ListNumber is unique
			if(!listNumSet.add(listNum))
				throw new ApplicationException(String.format("%s isn't unique starting on row %d.", UserData.fieldName.LIST_NUMBER.getName(), (i+1)));
			
			previousListNum = Integer.parseInt(listNum);
			
			// fix the country code
			cntry = fixCountry(cntry);
			
			// if no country, try to infer
			if(cntry.replaceAll(" ", "").length() == 0 || cntry.replaceAll(" ", "").length() > 2)
				cntry = inferCountry(pcode);
			
			if(!cntry.equalsIgnoreCase(CA))
				throw new ApplicationException(String.format("No 2 char ISO Country code on row %d.", (i+1)));
			
			// make add2 into add1 to avoid weird labels printing
			if(add1.replaceAll("  ", " ").trim().length() == 0) {
				add1 = add2;
				add2 = "";
			}
			
			if(add1.length() == 0 && add2.length() == 0)
				throw new ApplicationException(String.format("No address for record on row %d.", (i+1)));

			Record record = new Record.Builder(++counter, data[i], "", "", "")
					.setAdd1(add1).setAdd2(add2).setCity(city).setProv(prov).setPCode(pcode).setCntry(cntry).setListNum(listNum).build();

			userData.add(record); // add the record the recordList in userData

		}

		// set the Header fields that we have used
		userData.setDfHeaders(new String[] {});

		RegisteredMailDialog registeredMailDialog = new RegisteredMailDialog(UiController.getMainFrame());
		registeredMailDialog.setVisible(true);
		
		// Time to build the file
		String[][] registeredMailData = new String[userData.getRecordList().size()][FIELD_COUNT];
		
		for (String[] row : registeredMailData)
			Arrays.fill(row, "");
		
		for (String[] row : registeredMailData)
			for(int i = registeredMailField.WEIGHT.getIndex(); i < row.length; ++i)
				row[i] = "0";
		
		// init some values we will be reusing, all fields are assumed to be from the source file except the ones mapped
		String serviceCode = registeredMailDialog.getComboBoxService().getSelectedItem().toString();
		
		if(serviceCode.equalsIgnoreCase(RegisteredMailDialog.registeredMailService.LETTERMAIL_STANDARD.getName())) {
			serviceCode = String.valueOf(RegisteredMailDialog.registeredMailService.LETTERMAIL_STANDARD.getServiceCode());
		}else if(serviceCode.equalsIgnoreCase(RegisteredMailDialog.registeredMailService.LETTERMAIL_MACHINEABLE.getName())) {
			serviceCode = String.valueOf(RegisteredMailDialog.registeredMailService.LETTERMAIL_MACHINEABLE.getServiceCode());
		}else if(serviceCode.equalsIgnoreCase(RegisteredMailDialog.registeredMailService.LETTERMAIL_OTHER.getName())) {
			serviceCode = String.valueOf(RegisteredMailDialog.registeredMailService.LETTERMAIL_OTHER.getServiceCode());
		}
		
		ZoneId defaultZoneId = ZoneId.systemDefault();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dropDate = sdf.format(Date.from(registeredMailDialog.getDatePicker().getDate().atStartOfDay(defaultZoneId).toInstant()));
		
		for(int i = 0; i < userData.getRecordList().size(); ++i) {
				Record record = userData.getRecordList().get(i);
			
				registeredMailData[i][registeredMailField.RECORD_TYPE.getIndex()] = String.valueOf(RECORD_TYPE);
				registeredMailData[i][registeredMailField.PLACEHOLDER.getIndex()] = "";
				registeredMailData[i][registeredMailField.RECORD_ID.getIndex()] = record.getListNum();
				registeredMailData[i][registeredMailField.DEPOSIT_LOCATION.getIndex()] = PPC_DEPOT_CODE;
				registeredMailData[i][registeredMailField.DATE_OF_DEPOSIT.getIndex()] = dropDate;
				registeredMailData[i][registeredMailField.ARTICLE_NUMBER.getIndex()] = serviceCode;
				registeredMailData[i][registeredMailField.WEIGHT.getIndex()] = String.valueOf(Double.parseDouble(registeredMailDialog.getTextFieldWeight().getText()));
				registeredMailData[i][registeredMailField.COST_CENTRE.getIndex()] = "";
				registeredMailData[i][registeredMailField.ITEM_COVERAGE_AMOUNT.getIndex()] = "";
				registeredMailData[i][registeredMailField.ORDER_ID_REF.getIndex()] = record.getListNum();
				registeredMailData[i][registeredMailField.ITEM_REFERENCE_2.getIndex()] = "";
				
				String firstName = getInDataValue(registeredMailDialog.getComboBoxFirstName().getSelectedIndex(), record);
				String lastName = getInDataValue(registeredMailDialog.getComboBoxLastName().getSelectedIndex(), record);
				
				registeredMailData[i][registeredMailField.NAME.getIndex()] = (firstName.trim() + " " + lastName.trim()).trim();
				registeredMailData[i][registeredMailField.COMPANY.getIndex()] = getInDataValue(registeredMailDialog.getComboBoxCompany().getSelectedIndex(), record);
				
				registeredMailData[i][registeredMailField.ADDRESS1.getIndex()] = record.getAdd1();
				registeredMailData[i][registeredMailField.ADDRESS2.getIndex()] = record.getAdd2();
				registeredMailData[i][registeredMailField.CITY.getIndex()] = record.getCity();
				registeredMailData[i][registeredMailField.PROVINCE.getIndex()] = record.getProv();
				registeredMailData[i][registeredMailField.PC.getIndex()] = record.getPCode();
				registeredMailData[i][registeredMailField.COUNTRY.getIndex()] = record.getCntry();	
				registeredMailData[i][registeredMailField.PHONE.getIndex()] = getInDataValue(registeredMailDialog.getComboBoxTelephone().getSelectedIndex(), record);
				registeredMailData[i][registeredMailField.EMAIL1.getIndex()] = getInDataValue(registeredMailDialog.getComboBoxEmail().getSelectedIndex(), record);
				registeredMailData[i][registeredMailField.EMAIL2.getIndex()] = "";
				registeredMailData[i][registeredMailField.EMAIL_NOTIFICATION.getIndex()] = String.valueOf(emailNotification.ALL.getValue());
		}
		
		try {
			TextWriter.write(registeredMailDialog.getRegisteredMailFile(), ',', false, new String[0], registeredMailData);
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
	
	private boolean isValidPC(String pc) {
		if(Validators.isValidCanPC(pc))
			return true;
		if(Validators.isValidUSZip(pc))
			return true;
		
		return false;
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
