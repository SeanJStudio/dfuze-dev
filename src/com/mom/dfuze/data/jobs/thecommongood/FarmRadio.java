/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.thecommongood;


import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;


/**
 * FarmRadio implements a RunBehavior for TheCommonGood Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 10/10/2023
 *
 */
public class FarmRadio implements RunTheCommonGoodBehavior {

	private final String BEHAVIOR_NAME = "Regular Process";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.LAST_DONATION_AMOUNT.getName()
			};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Creates gift arrays</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load data and run</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
			+ "</html>";
		
	//
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		createGiftArrays(userData);
		userData.setDfHeaders(new String[] {
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_DONATION1_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_DONATION2_AMOUNT.getName()
				});
	}
	
	public void createGiftArrays(UserData userData) {
		for(int i = 0; i < userData.getRecordList().size(); ++ i) {
			Record record = userData.getRecordList().get(i);
			String lastGift = record.getLstDnAmt().replaceAll("[^0-9\\.]", "");
			
			double lastGiftAmount = (Validators.isNumber(lastGift)) ? Double.parseDouble(lastGift) : 0.0;
			double ROUNDING_AMOUNT = 5.0;
			double MONTHLY_ROUNDING_AMOUNT = 1.0;
			
			String dn1 = "";
			String dn2 = "";
			String dn3 = "";
			double mdn1Amount = Math.ceil((lastGiftAmount / 12.0) / MONTHLY_ROUNDING_AMOUNT) * MONTHLY_ROUNDING_AMOUNT;
			double mdn2Amount = Math.ceil((lastGiftAmount / 10.0) / MONTHLY_ROUNDING_AMOUNT) * MONTHLY_ROUNDING_AMOUNT;
			
			if(mdn1Amount < 1)
				mdn1Amount = 1.0;
			if(mdn2Amount <= mdn1Amount)
				mdn2Amount = mdn1Amount + 1.0;
			
			String mdn1 = String.valueOf(mdn1Amount);
			String mdn2 = String.valueOf(mdn2Amount);
			
			if (lastGiftAmount <= 35) {
				dn1 = "35";
				dn2 = "50";
				dn3 = "65";
			} else if (lastGiftAmount < 61) {
				dn1 = String.valueOf(Math.ceil(lastGiftAmount / ROUNDING_AMOUNT) * ROUNDING_AMOUNT);
				dn2 = String.valueOf(Math.ceil(lastGiftAmount + 10.0 / ROUNDING_AMOUNT) * ROUNDING_AMOUNT);
				dn3 = String.valueOf(Math.ceil(lastGiftAmount + 20.0 / ROUNDING_AMOUNT) * ROUNDING_AMOUNT);
			} else {
				dn1 = String.valueOf(Math.ceil(lastGiftAmount / ROUNDING_AMOUNT) * ROUNDING_AMOUNT);
				dn2 = String.valueOf(Math.ceil(lastGiftAmount * 1.1 / ROUNDING_AMOUNT) * ROUNDING_AMOUNT);
				dn3 = String.valueOf(Math.ceil(lastGiftAmount * 1.25 / ROUNDING_AMOUNT) * ROUNDING_AMOUNT);
			}
			
			record.setDn1Amt(dn1);
			record.setDn2Amt(dn2);
			record.setDn3Amt(dn3);
			record.setMDn1Amt(mdn1);
			record.setMDn2Amt(mdn2);
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
