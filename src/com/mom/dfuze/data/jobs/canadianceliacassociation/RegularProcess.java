
package com.mom.dfuze.data.jobs.canadianceliacassociation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;

/**
 * Regular Process implements a RunBehavior for CanadianCeliacAssociation Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2022
 *
 */
public class RegularProcess implements RunCanadianCeliacAssociationBehavior {

	private String BEHAVIOR_NAME = "Regular Process";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.PREFIX.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.LAST_DONATION_AMOUNT.getName()
			};
	
	private final double MIN_AMOUNT_1 = 30;
	private final double MIN_AMOUNT_2 = 50;
	private final double MIN_AMOUNT_3 = 60;
	private final double MIN_AMOUNT_4 = 80;
	private final double MIN_AMOUNT_5 = 100;
	private final double MIN_AMOUNT_6 = 200;
	
	private String DESCRIPTION = "<html>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Cleans and creates the name fields to use</li>"
	        + "<li>Calculates the gift arrays:<ol>"
	        + "<li>IF LG &lt; " +  (int) MIN_AMOUNT_3 + " THEN 50, 75, 100</li>"
	        + "<li>IF LG &lt; " + (int) MIN_AMOUNT_4 + " THEN 65, 75, 100</li>"
	        + "<li>IF LG &lt; " + (int) MIN_AMOUNT_5 + " THEN 85, 100, 150</li>"
	        + "<li>IF LG &lt; " + (int) MIN_AMOUNT_6 + " THEN 125, 150, 200</li>"
	        + "<li>IF LG &gt " + (int) MIN_AMOUNT_6 + " THEN $ ________</li>"
	        + "</ol></li>"
	        + "</ol>"
			+ "Instructions<br/><ol>"
			+ "<li>Load data file and run job</li></ol>"
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
			String prefix = data[i][userData.getPrefixIndex()].replaceAll("  ", " ").trim();
			String firstName = data[i][userData.getFstNameIndex()].replaceAll("  ", " ").trim();
			String lastName = data[i][userData.getLstNameIndex()].replaceAll("  ", " ").trim();
			String lastGiftAmt = data[i][userData.getLstDnAmtIndex()].replaceAll("[^0-9\\.]", "");
			
			String fullName = String.format("%s %s %s", prefix, firstName, lastName).replaceAll("  ", " ").trim();
			String dearSal = firstName;
			String paraSal = firstName;
			
			boolean recIsBusiness = (firstName.length() == 0) ? true : false;
			
			if(dearSal.equalsIgnoreCase("friend") || dearSal.equalsIgnoreCase("friends")) {
				paraSal = "";
			}

			if(dearSal.length() == 1
					|| dearSal.length() == 0
					|| dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
					|| dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
					|| dearSal.length() == 2 && !Validators.hasVowel(dearSal)
					) {

				dearSal = (!recIsBusiness) ? "Friend" : "Friends";
				paraSal = "";
			}
			

			// Initialize the last gift amount
			BigDecimal lastGiftAmountAsBigDecimal = new BigDecimal("5.0");
			if(Validators.isNumber(lastGiftAmt))
				lastGiftAmountAsBigDecimal = new BigDecimal(lastGiftAmt);

			// initialize the standard gift amounts
			String dn1Amt = "";
			String dn2Amt = "";
			String dn3Amt = "";
			String oDnAmt = "$______";

			BigDecimal roundingAmount = new BigDecimal("5.0");
			BigDecimal newRoundedLastDonationAmount = lastGiftAmountAsBigDecimal.divide(roundingAmount, 2, RoundingMode.HALF_EVEN)
					.setScale(0, RoundingMode.CEILING).multiply(roundingAmount);

			if (newRoundedLastDonationAmount.doubleValue() < MIN_AMOUNT_1) {
				dn1Amt = "50";
				dn2Amt = "75";
				dn3Amt = "100";
				oDnAmt = "$ ________";
			} else if (newRoundedLastDonationAmount.doubleValue() < MIN_AMOUNT_2) {
				dn1Amt = "50";
				dn2Amt = "75";
				dn3Amt = "100";
				oDnAmt = "$ ________";
			} else if (newRoundedLastDonationAmount.doubleValue() < MIN_AMOUNT_3) {
				dn1Amt = "50";
				dn2Amt = "75";
				dn3Amt = "100";
				oDnAmt = "$ ________";
			} else if (newRoundedLastDonationAmount.doubleValue() < MIN_AMOUNT_4) {
				dn1Amt = "65";
				dn2Amt = "75";
				dn3Amt = "100";
				oDnAmt = "$ ________";
			} else if (newRoundedLastDonationAmount.doubleValue() < MIN_AMOUNT_5) {
				dn1Amt = "85";
				dn2Amt = "100";
				dn3Amt = "150";
				oDnAmt = "$ ________";
			} else if (newRoundedLastDonationAmount.doubleValue() < MIN_AMOUNT_6) {
				dn1Amt = "125";
				dn2Amt = "150";
				dn3Amt = "200";
				oDnAmt = "$ ________";
			} else {
				dn1Amt = "";
				dn2Amt = "";
				dn3Amt = "";
				oDnAmt = "$ ________";
			}


			// Build the Record - use company for name2
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setNam1(fullName)
					.setDearSal(dearSal)
					.setParaSal(paraSal)
					.setDn1Amt(dn1Amt)
					.setDn2Amt(dn2Amt)
					.setDn3Amt(dn3Amt)
					.setODnAmt(oDnAmt)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.PARAGRAPH_SALUTATION.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.OPEN_DONATION_AMOUNT.getName()
				});
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
