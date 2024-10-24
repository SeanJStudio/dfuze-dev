/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.ridgemeadowshospital;



import java.math.BigDecimal;

import java.util.Arrays;
import java.util.HashSet;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;

import com.mom.dfuze.ui.OptionSelectDialog;
import com.mom.dfuze.ui.UiController;

/**
 * Regular Process implements a RunBehavior for Ridge Meadows Hospital Foundation Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RegularProcess implements RunRidgeMeadowsHospitalBehavior {

	private String BEHAVIOR_NAME = "Regular Process";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
			};
	
	private String DESCRIPTION = "<html>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Calculates the gift arrays</li>"
	        + "</ol>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load supplied data files/tables and run job</li>"
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
		String[] headers = userData.getInHeaders();
		
		//get the filename index
		int fileNameIndex = Arrays.asList(headers).indexOf("dfFileName");
		HashSet<String> fileSet = new HashSet<>();
		
		//Create a set of the file names
		for (int i = 0; i < data.length; i++) {
			String fileName = data[i][fileNameIndex];
			fileSet.add(fileName);
		}
		
		// Display the files to the user
		OptionSelectDialog ops = new OptionSelectDialog(UiController.getMainFrame(), "Select the Monthly file below, or close if there is no monthly file.", fileSet.toArray(new String[fileSet.size()]));
		ops.setVisible(true);
		
		String monthlyFileName = "";
		
		if(ops.isNextPressed())
			monthlyFileName = ops.getSelectedOption();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {

			// Get all the fields we can for now
			String lastGiftAmt = data[i][userData.getLstDnAmtIndex()].replaceAll("[^0-9\\.]", "");
			String fileName = data[i][fileNameIndex];

			// Initialize the last gift amount
			BigDecimal lastGiftAmountAsBigDecimal = new BigDecimal("0.0"); 
			
			if(Validators.isNumber(lastGiftAmt))
				lastGiftAmountAsBigDecimal = new BigDecimal(lastGiftAmt);

			// initialize the standard gift amounts
			String dn1Amt = "";
			String dn2Amt = "";
			String dn3Amt = "";
			String dn4Amt = "";
			
			final double ROUND_BY_FIVE = 5.00;
			if (lastGiftAmountAsBigDecimal.doubleValue() <= 35.00) {
				dn1Amt = "35";
				dn2Amt = "50";
				dn3Amt = "75";
				dn4Amt = "100";
			} else if (lastGiftAmountAsBigDecimal.doubleValue() < 500.00) {
				dn1Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 1) / ROUND_BY_FIVE) * ROUND_BY_FIVE)); //Round up the values
				dn2Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 1.5) / ROUND_BY_FIVE) * ROUND_BY_FIVE));
				dn3Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 2) / ROUND_BY_FIVE) * ROUND_BY_FIVE));
				dn4Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 3) / ROUND_BY_FIVE) * ROUND_BY_FIVE));
			} else {
				dn1Amt = "500";
				dn2Amt = "625";
				dn3Amt = "750";
				dn4Amt = "1000";
			}

			// Check to see if any of the values are the same and bump up by 5
			if(!dn1Amt.isEmpty() && dn1Amt.equalsIgnoreCase(dn2Amt))
				dn2Amt = String.valueOf(Double.valueOf(dn2Amt) + 5);
			if(!dn2Amt.isEmpty() && dn2Amt.equalsIgnoreCase(dn3Amt))
				dn3Amt = String.valueOf(Double.valueOf(dn3Amt) + 5);
			if(!dn3Amt.isEmpty() && dn3Amt.equalsIgnoreCase(dn4Amt))
				dn4Amt = String.valueOf(Double.valueOf(dn4Amt) + 5);
			
			String packageVersion = "REGULAR";
			
			if(fileName.equals(monthlyFileName)) {
				dn1Amt = "";
				dn2Amt = "";
				dn3Amt = "";
				dn4Amt = "";
				
				packageVersion = "MONTHLY";
			}

			// Build the Record - use company for name2
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setDn1Amt(dn1Amt)
					.setDn2Amt(dn2Amt)
					.setDn3Amt(dn3Amt)
					.setDn4Amt(dn4Amt)
					.setPkgVer(packageVersion)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.DONATION4_AMOUNT.getName(),
				UserData.fieldName.PACKAGE_VERSION.getName()
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
		return true;
	}

}
