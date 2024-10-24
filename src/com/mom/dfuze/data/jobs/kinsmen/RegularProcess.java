/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.kinsmen;

import java.math.BigDecimal;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;


/**
 * RegularProcess implements a RunBehavior for Kinsmen Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RegularProcess implements RunKinsmenBehavior {

	private String BEHAVIOR_NAME = "Regular Process";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
			UserData.fieldName.NUMBER_OF_DONATIONS.getName(),
			UserData.fieldName.NAME1.getName()
			};
	
	private final String SEGMENT_SPECIAL = "Special";
	private final String SEGMENT_LAPSED = "Lapsed";
	private final String SEGMENT_NEW = "New";
	
	private static final String DEFAULT_SALUTATION = "Miracle Maker";
	private static final String CONJUNCTION_REGEX = "(?i)[ ][a][n][d][ ]|[ ][a][n][d][/][o][r][ ]|[ ][&][ ]|/(?<!c/)(?!o)|[ ][e][t][ ]|[ ][o][r][ ]|[ ][o][u][ ]|\\+|[,]";
	
	private String DESCRIPTION = "<html>"
	        + "Description<br/><ol><li>Creates gift arrays and reply variables</li></ol>"
			+ "Instructions<br/><ol>"
			+ "<li>Load supplied data file and run job</li></ol>"
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
			String lastGiftAmt = data[i][userData.getLstDnAmtIndex()].replaceAll("[^0-9\\.]", "");
			String numOfDns = data[i][userData.getNumDnIndex()].replaceAll("[^0-9]", "");
			int numOfDonations = (Validators.isNumber(numOfDns)) ? Integer.parseInt(numOfDns) : 0;
			String nam1 = data[i][userData.getNam1Index()].replaceAll("(?i)\\(estate of\\)", "").replaceAll("\\s+", " ").trim();
			String ask1 = "", ask2 = "", ask3 = "";
			String rep1 = "", rep2 = "";
			
			// Generate the salutation
			String sal = "";
			
			String[] people = nam1.split(CONJUNCTION_REGEX);
			
			for(int j = 0; j < people.length; ++j) {
				String[] personParts = people[j].trim().split("\\s+");
				if(j == 0) {
					sal = personParts[0];
				} else if (people.length > 2 && j != people.length - 1) {
					sal += ", " + personParts[0];
				} else {
					sal += " and " + personParts[0];
				}
			}
			
			if(sal.length() <= 1 // check for bad sal
					|| sal.length() >= 2 && sal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
					|| sal.length() == 2 && Validators.areCharactersSame(sal)
					|| sal.length() == 2 && !Validators.hasVowel(sal)
					) {
				sal = DEFAULT_SALUTATION;
			}
			

			// Generate the gift arrays
			BigDecimal lastGiftAmountAsBigDecimal = new BigDecimal("0.0");
			if(Validators.isNumber(lastGiftAmt))
				lastGiftAmountAsBigDecimal = new BigDecimal(lastGiftAmt);
			
			if(lastGiftAmountAsBigDecimal.doubleValue() < 61) {
				ask1 = "35";
				ask2 = "50";
				ask3 = "65";
			} else if(lastGiftAmountAsBigDecimal.doubleValue() < 500) {
				ask1 = String.valueOf(Math.round(lastGiftAmountAsBigDecimal.intValue() / 5.0) * 5);
				ask2 = String.valueOf(Math.round((lastGiftAmountAsBigDecimal.intValue() * 1.1) / 5.0) * 5);
				ask3 = String.valueOf(Math.round((lastGiftAmountAsBigDecimal.intValue() * 1.25) / 5.0) * 5);
			}
			
			// Generate the reply lines
			String tempAsk = (ask3.length() == 0) ? "0" : ask3;
			BigDecimal repAsk = new BigDecimal(tempAsk);
			
			if(repAsk.doubleValue() > 0 && repAsk.doubleValue() < 147) {
				rep1 = "Kyla, I decided to stretch my generosity by making a gift of $147 or more using the “Other” option above.  Please send me a TeleMiracle Teddy Bear.";
				rep2 = "Thank you, Kyla, I will consider this special offer in the future.";
			} else if(repAsk.doubleValue() > 0 && repAsk.doubleValue() < 294) {
				rep1 = "YES! Please send me my TeleMiracle 47 Teddy Bear.";
				rep2 = "No, thank you. Please don't send me a TeleMiracle Teddy Bear.";
			} else {
				rep1 = "YES! Please send me _____ TeleMiracle 47 Teddy Bears.";
				rep2 = "No, thank you. Please don't send me a TeleMiracle Teddy Bear.";
			}
			
			// Generate the segment
			String segment = SEGMENT_LAPSED;
			if(lastGiftAmountAsBigDecimal.doubleValue() >= 500) {
				segment = SEGMENT_SPECIAL;
			} else if (numOfDonations <= 1) {
				segment = SEGMENT_NEW;
			}

			// Build the Record
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setSeg(segment)
					.setDearSal(sal)
					.setDn1Amt(ask1)
					.setDn2Amt(ask2)
					.setDn3Amt(ask3)
					.setProvide1(rep1)
					.setProvide2(rep2)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] {
				UserData.fieldName.SEGMENT.getName(),
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.PROVIDE1.getName(),
				UserData.fieldName.PROVIDE2.getName(),
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
		return false;
	}

}
