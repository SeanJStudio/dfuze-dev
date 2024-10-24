/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.lunghealthfoundation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.ui.OptionSelectDialog;
import com.mom.dfuze.ui.UiController;

/**
 * Regular Process implements a RunBehavior for Lung Health Foundation Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RegularProcess implements RunLungHealthFoundationBehavior {

	private String BEHAVIOR_NAME = "Regular Process";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.IN_ID.getName(),
			UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
			UserData.fieldName.DEAR_SALUTATION.getName(),
			UserData.fieldName.NAME1.getName()
			};
	
	private final double MIN_AMOUNT_1 = 250;
	private final double MIN_AMOUNT_2 = 500;
	
	private String DESCRIPTION = "<html>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load BC and ON data and run job</li>"
			+ "</ol>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Calculates the gift arrays:<ol>"
	        + "<li>IF LG &lt; " +  (int) MIN_AMOUNT_1 + " THEN LG*1.25, LG*1.5, LG*2</li>"
	        + "<li>IF LG &lt; " + (int) MIN_AMOUNT_2 + " THEN LG, LG*1.25, LG*1.5</li>"
	        + "<li>IF LG &gt " + (int) MIN_AMOUNT_2 + " THEN $ ________</li>"
	        + "</ol></li>"
	        + "<li>Creates the salutation to use</li>"
	        + "<li>Adds leading zeros to the id for BC</li>"
	        + "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";
	
	private static final String CONJUNCTION_REGEX = "(?i)[ ][a][n][d][ ]|[ ][a][n][d][/][o][r][ ]|[ ][&][ ]|[/]|[ ][e][t][ ]|[ ][o][r][ ]|[ ][o][u][ ]|\\+";
	
	String[][] prefixesFirstPass;

	String[][] companyKeywords;
	
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		// The users' input data
		String[][] data = userData.getData();
		String[] headers = userData.getInHeaders();
		
		prefixesFirstPass = Lookup.getPrefixFirstPass();
		companyKeywords = Lookup.getCompanykeywords();

		//get the filename index
		int fileNameIndex = Arrays.asList(headers).indexOf("dfFileName");
		HashSet<String> fileSet = new HashSet<>();

		//Create a set of the file names
		for (int i = 0; i < data.length; i++) {
			String fileName = data[i][fileNameIndex];
			fileSet.add(fileName);
		}

		// Display the files to the user
		OptionSelectDialog ops = new OptionSelectDialog(UiController.getMainFrame(), "Select the BC file or, close if there is no BC file.", fileSet.toArray(new String[fileSet.size()]));
		ops.setVisible(true);

		String bcFileName = "";

		if(ops.isNextPressed())
			bcFileName = ops.getSelectedOption();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {

			// Get all the fields we can for now
			String id =  data[i][userData.getInIdIndex()];
			String lastGiftAmt = data[i][userData.getLstDnAmtIndex()].replaceAll("[^0-9\\.]", "");
			String sal =  data[i][userData.getDearSalIndex()];
			String fullName =  data[i][userData.getNam1Index()];
			String fileName = data[i][fileNameIndex];
			
			String lastName = getLastName(fullName);
			
			String seg = "";
			
			if(fileName.equals(bcFileName))
				seg = "BC";
			else
				seg = "ON";
			
			if(fileName.equals(bcFileName))
				if(id.length() < 7)
					id = Common.leftPad(id, 7, '0');
			
			// For ON, If supplied salutatation is friend(s), use fullname, else use salutation
			String newSal = (!fileName.equals(bcFileName) && (sal.equalsIgnoreCase("friend") || sal.equalsIgnoreCase("friends"))) ? fullName : sal;

			// Initialize the last gift amount
			BigDecimal lastGiftAmountAsBigDecimal = new BigDecimal("0.0"); 
			BigDecimal minimumGiftAmountAsBigDecimal = new BigDecimal("25.0"); //Minimum ask amount if $25
			
			if(Validators.isNumber(lastGiftAmt))
				lastGiftAmountAsBigDecimal = new BigDecimal(lastGiftAmt);
			
			if(lastGiftAmountAsBigDecimal.doubleValue() < minimumGiftAmountAsBigDecimal.doubleValue()) //Default to minimum if below
				lastGiftAmountAsBigDecimal = minimumGiftAmountAsBigDecimal;

			// initialize the standard gift amounts
			String dn1Amt = "";
			String dn2Amt = "";
			String dn3Amt = "";
			String oDnAmt = "I prefer to give $ ________";
			
			final double ROUND_BY_FIVE = 5.00;
				
			if (lastGiftAmountAsBigDecimal.doubleValue() < MIN_AMOUNT_1) {
				dn1Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 1.25) / ROUND_BY_FIVE) * ROUND_BY_FIVE)); //Round up the values
				dn2Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 1.5) / ROUND_BY_FIVE) * ROUND_BY_FIVE));
				dn3Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 2) / ROUND_BY_FIVE) * ROUND_BY_FIVE));
				oDnAmt = "I prefer to give $ ________";
			} else if (lastGiftAmountAsBigDecimal.doubleValue() < MIN_AMOUNT_2) {
				dn1Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 1) / ROUND_BY_FIVE) * ROUND_BY_FIVE));
				dn2Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 1.25) / ROUND_BY_FIVE) * ROUND_BY_FIVE));
				dn3Amt = String.valueOf((int) (Math.ceil((lastGiftAmountAsBigDecimal.intValue() * 1.5) / ROUND_BY_FIVE) * ROUND_BY_FIVE));
				oDnAmt = "I prefer to give $ ________";
			} else {
				dn1Amt = "";
				dn2Amt = "";
				dn3Amt = "";
				oDnAmt = "Today, I'd like to make a donation of $ ________";
			}

			// Check to see if any of the values are the same and bump up by 5
			if(!dn1Amt.isEmpty() && dn1Amt.equalsIgnoreCase(dn2Amt))
				dn2Amt = String.valueOf((int) (Double.valueOf(dn2Amt) + 5));
			if(!dn2Amt.isEmpty() && dn2Amt.equalsIgnoreCase(dn3Amt))
				dn3Amt = String.valueOf((int) (Double.valueOf(dn3Amt) + 5));

			// Build the Record - use company for name2
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setLstName(lastName)
					.setDearSal(newSal)
					.setDn1Amt(dn1Amt)
					.setDn2Amt(dn2Amt)
					.setDn3Amt(dn3Amt)
					.setODnAmt(oDnAmt)
					.setSeg(seg)
					.setInId(id)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.OPEN_DONATION_AMOUNT.getName(),
				UserData.fieldName.SEGMENT.getName(),
				UserData.fieldName.IN_ID.getName(),
				UserData.fieldName.LASTNAME.getName()
		});
	}
	
	public String getLastName(String name) {
		
		name = name.trim().replaceAll("\\.", "");
		
		LinkedHashSet<String> lastNames = new LinkedHashSet<>();

		//ArrayList<String> lastNames = new ArrayList<>();
		
		// Remove first prefixes
		for(int i = 0; i < prefixesFirstPass.length; ++i) {
			String prefix = prefixesFirstPass[i][0];
			//Check if the prefix matches
			if(name.length() >= prefix.length()) {
				if(name.substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
					name = name.substring(prefix.length());
					break;
				}
			}
		}
		
		name = name.replaceAll(CONJUNCTION_REGEX," \\|\\| ");
		String[] nameParts = name.split(" \\|\\| ");
		
		for(int i = 0; i < nameParts.length; ++i) {
			for(int j = 0; j < prefixesFirstPass.length; ++j) {
				String prefix = prefixesFirstPass[j][0];
				//Check if the prefix matches
				if(nameParts[i].length() >= prefix.length()) {
					if(nameParts[i].substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
						nameParts[i] = nameParts[i].substring(prefix.length());
						break;
					}
				}
			}
			
			
			//
			String[] splitNameParts = nameParts[i].split("\\s+");
			
			// if the current name has more than one part or if there is only one name
			if(splitNameParts.length > 1 || nameParts.length == 1) {
				String partToAdd = splitNameParts[splitNameParts.length - 1];
				
				// Dont add single length char, most likely initial
				if(partToAdd.length() > 1)
					lastNames.add(partToAdd);
			}
		}
		
		// if there is no lastName found yet, force the last element in
		if(lastNames.size() == 0) {
			String tempName = nameParts[nameParts.length - 1];
			String[] tempNameParts = tempName.split("\\s+");
			String tempLastName = tempNameParts[tempNameParts.length - 1];
			lastNames.add(tempLastName);
		}
		
		String lastName = "";
		
		if(lastNames.size() == 1)
			lastName = lastNames.toArray(new String[lastNames.size()])[0];
		else
			for(String ln : lastNames)
				lastName += " and " + ln;

		return lastName.replaceFirst(" and ", "");
		
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
