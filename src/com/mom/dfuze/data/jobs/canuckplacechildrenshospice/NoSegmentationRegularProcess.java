/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.canuckplacechildrenshospice;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;

/**
 * Summer2020ABSplit impliments a RunBehavior for Canucks Place Childrens Hospice Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class NoSegmentationRegularProcess implements RunCanuckPlaceChildrensHospiceBehavior {

	private String BEHAVIOR_NAME = "No Segmentation Regular Process";

	private String[] REQUIRED_FIELDS = { 
			UserData.fieldName.DEAR_SALUTATION.getName(),
			UserData.fieldName.PREFIX.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.LAST_DONATION_AMOUNT.getName()
			};
	
	private String DESCRIPTION = "<html>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Calculates the gift arrays</li>"
	        + "<li>Builds the salutation</li>"
	        + "</ol>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load all supplied data files and run job</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		// The users' input data
		String[][] data = userData.getData();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {

			// Get all the fields we can for now
			String dearSal = data[i][userData.getDearSalIndex()];
			String paraSal = dearSal;
			String prefix = data[i][userData.getPrefixIndex()];
			String fnam = data[i][userData.getFstNameIndex()];
			String lnam = data[i][userData.getLstNameIndex()];
			String nam1 = data[i][userData.getNam1Index()];
			String lastGiftAmt = data[i][userData.getLstDnAmtIndex()].replaceAll("[^0-9\\.]", "");

			// Initialize the last gift amount
			BigDecimal lastGiftAmountAsBigDecimal = new BigDecimal("5.0");
			if(Validators.isNumber(lastGiftAmt))
				lastGiftAmountAsBigDecimal = new BigDecimal(lastGiftAmt);

			// initialize the standard gift amounts
			String dn1Amt = "";
			String dn2Amt = "";
			String dn3Amt = "";
			String oDnAmt = "$ ________";

			BigDecimal roundingAmount = new BigDecimal("5.0");
			BigDecimal newRoundedLastDonationAmount = lastGiftAmountAsBigDecimal.divide(roundingAmount, 2, RoundingMode.HALF_EVEN)
					.setScale(0, RoundingMode.CEILING).multiply(roundingAmount);

			if (lastGiftAmountAsBigDecimal.doubleValue() < 20.00) {
				dn1Amt = "20";
				dn2Amt = "30";
				dn3Amt = "40";
				oDnAmt = "$ ________";
			} else if (lastGiftAmountAsBigDecimal.doubleValue() < 500.00) {
				dn1Amt = String.valueOf(Math.round(newRoundedLastDonationAmount.intValue() / 5.0) * 5);
				dn2Amt = String.valueOf(Math.round((newRoundedLastDonationAmount.intValue() * 1.2) / 5.0) * 5);
				dn3Amt = String.valueOf(Math.round((newRoundedLastDonationAmount.intValue() * 1.4) / 5.0) * 5);
				oDnAmt = "$ ________";
			} else {
				dn1Amt = "";
				dn2Amt = "";
				dn3Amt = "";
				oDnAmt = "Here's my special gift of $__________";
			}

			// Check to see if any of the values are the same and bump up by 5
			if(!dn1Amt.isEmpty() && dn1Amt.equalsIgnoreCase(dn2Amt))
				dn2Amt = String.valueOf(Integer.parseInt(dn2Amt) + 5);
			if(!dn2Amt.isEmpty() && dn2Amt.equalsIgnoreCase(dn3Amt))
				dn3Amt = String.valueOf(Integer.parseInt(dn3Amt) + 5);

			// Initialize the monthly donation amounts
			String mDn1Amt = "";
			String mDn2Amt = "";
			String mDn3Amt = "";
			String mODnAmt = "Other $ ________";

			if (lastGiftAmountAsBigDecimal.doubleValue() < 100) {
				mDn1Amt = "10";
				mDn2Amt = "15";
				mDn3Amt = "20";
			} else if (Math.round(lastGiftAmountAsBigDecimal.intValue() / 5.0) * 5 < 250) {
				mDn1Amt = "15";
				mDn2Amt = "25";
				mDn3Amt = "35";
			} else if (Math.round(lastGiftAmountAsBigDecimal.intValue() / 5.0) * 5 < 500) {
				mDn1Amt = "25";
				mDn2Amt = "50";
				mDn3Amt = "75";
			} else if (Math.round(lastGiftAmountAsBigDecimal.intValue() / 5.0) * 5 < 1000) {
				mDn1Amt = "50";
				mDn2Amt = "75";
				mDn3Amt = "100";
			}else {
				mDn1Amt = "100";
				mDn2Amt = "125";
				mDn3Amt = "150";
			}

			// Fix the salutations
			if(dearSal.length() <= 1
					|| dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[a-zA-Z']", "").length() > 0
					|| dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
					|| dearSal.length() == 2 && !Validators.hasVowel(dearSal)
					) {
					
				if(!prefix.isEmpty() && !lnam.isEmpty()) {
					dearSal = prefix + " " + lnam;
					paraSal = dearSal;
				}else {
					dearSal = "Friend";
					paraSal = "";
				}
			}

			// Build the Record - use company for name2
			Record record = new Record.Builder(i, data[i], "", "", "").setDearSal(dearSal).setParaSal(paraSal).setFstName(fnam).setLstName(lnam)
					.setNam1(nam1).setLstDnAmt(lastGiftAmt).setDn1Amt(dn1Amt).setDn2Amt(dn2Amt).setDn3Amt(dn3Amt).setODnAmt(oDnAmt)
					.setMDn1Amt(mDn1Amt).setMDn2Amt(mDn2Amt).setMdDn3Amt(mDn3Amt).setMODnAmt(mODnAmt).build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { UserData.fieldName.DEAR_SALUTATION.getName(), UserData.fieldName.PARAGRAPH_SALUTATION.getName(),
				UserData.fieldName.NAME1.getName(), UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(), UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(), UserData.fieldName.OPEN_DONATION_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_DONATION1_AMOUNT.getName(), UserData.fieldName.MONTHLY_DONATION2_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_DONATION3_AMOUNT.getName(), UserData.fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName() });
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
		// TODO Auto-generated method stub
		return false;
	}

}
