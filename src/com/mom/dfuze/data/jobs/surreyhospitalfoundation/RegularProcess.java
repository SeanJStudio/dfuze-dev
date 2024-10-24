/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.surreyhospitalfoundation;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;


/**
 * RegularProcess implements a RunBehavior for SurreyHospitalFoundation Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 10/27/2023
 *
 */
public class RegularProcess implements RunSurreyHospitalFoundationBehavior {

	private final String BEHAVIOR_NAME = "Regular Process";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.PREFIX.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.LAST_DONATION_AMOUNT.getName()
			};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Creates gift arrays: LG, LG*1.3, LG*1.6 (rounded up by 5)</li>"
			+ "<li>Creates salutation fields as follows:<br/>dearSal: If fname empty/initial then Friend<br/>paraSal: If fname empty/initial then blank</li>"
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
		createSalutation(userData);
		
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
	
	public void createSalutation(UserData userData) throws ApplicationException {

		String CONJUNCTION_REGEX = "[,]|[ ][a][n][d][ ]|[ ][a][n][d][/][o][r][ ]|[ ][&][ ]|[ ][e][t][ ]|[ ][o][r][ ]|[ ][o][u][ ]|(\\s+)?[/](\\s+)?|(\\s+)?\\+(\\s+)?";
		Pattern pfp1Pattern = Lookup.getPrefixFirstPassPattern();

		for(int i = 0; i < userData.getRecordList().size(); ++ i) {
			Record record = userData.getRecordList().get(i);

			String prefix = record.getPrefix().trim();
			String fnam = record.getFstName().trim();
			String lnam = record.getLstName().trim();
			String fullname = record.getNam1().replaceAll("[^\\p{L}\\s&/\\+\\-,]", "").trim();
			
			Pattern lastNamePattern = Pattern.compile("(^|\\s)" + Pattern.quote(lnam) + "($|\\s)", Pattern.CASE_INSENSITIVE);
			Matcher lnameMatcher = lastNamePattern.matcher(fullname);

			while(lnameMatcher.find())
				fullname = fullname.replaceFirst(lnameMatcher.group(), " ").trim();

			Matcher pfm1 = pfp1Pattern.matcher(fullname);

			if(pfm1.find()) // replace the first prefix to safely break on conjunctions
				fullname = fullname.replaceFirst(pfm1.group(), "").trim();

			// Break on conjunctions
			String[] names = fullname.split(CONJUNCTION_REGEX);

			// Remove other possible prefixes
			for(int j = 0; j < names.length; ++j) {
				names[j] = names[j].trim();
				pfm1 = pfp1Pattern.matcher(names[j]);
				while(pfm1.find()) {
					names[j] = names[j].replaceFirst(pfm1.group(), " ").trim();
				}
			}

			// Loop through names and split on space to make array list of each first name
			ArrayList<String> newNameParts = new ArrayList<>();
			for(int j = 0; j < names.length; ++j) {
				String[] nameParts = names[j].split("\\s+");
				if(nameParts.length > 0)
					if(nameParts[0].length() > 0)
						newNameParts.add(nameParts[0]);
			}

			// Join all the names together
			String newDearSal = "";
			for(int j = 0; j < newNameParts.size(); ++j) {
				if(j == 0)
					newDearSal = newNameParts.get(j);
				else if(j != newNameParts.size() - 1)
					newDearSal += ", " + newNameParts.get(j);
				else
					newDearSal += " and " + newNameParts.get(j);
			}


			String dearSal = newDearSal;
			String paraSal = dearSal;

			if(dearSal.trim().toLowerCase().equalsIgnoreCase("friend") || dearSal.trim().toLowerCase().equalsIgnoreCase("friends")) {
				dearSal = "";
				paraSal = "";
			}

			if(dearSal.length() <= 1 // check for bad sal
					|| dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
					|| dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
					|| dearSal.length() == 2 && !Validators.hasVowel(dearSal)
					) {

				if(fnam.length() >= 2
						&& fnam.substring(1, 2).replaceAll("[\\p{L}']", "").length() == 0
						&& !Validators.areCharactersSame(fnam)
						&& Validators.hasVowel(fnam)
						) { // otherwise change to fname if possible
					dearSal = fnam;
					paraSal = fnam;
				} else if(!prefix.isEmpty() && !lnam.isEmpty()) { // change to prefix last if possible
					dearSal = prefix + " " + lnam;
					paraSal = dearSal;
				} else {	// otherwise friend
					dearSal = "Friend";
					paraSal = "";
				}
			}

			record.setDearSal(dearSal);
			record.setParaSal(paraSal);
		}
	}
	
	public void createGiftArrays(UserData userData) {
		for(int i = 0; i < userData.getRecordList().size(); ++ i) {
			Record record = userData.getRecordList().get(i);
			String lastGift = record.getLstDnAmt().replaceAll("[^0-9\\.]", "");
			
			double lastGiftAmount = (Validators.isNumber(lastGift)) ? Double.parseDouble(lastGift) : 5.0;
			
			if(lastGiftAmount == 0.0)
				lastGiftAmount = 5.0;
			
			double ROUNDING_AMOUNT = 5.0;
			
			String dn1 = "";
			String dn2 = "";
			String dn3 = "";
			String dn4 = "$_________";
			
			if (lastGiftAmount < 1_000) {
				dn1 = String.valueOf(Math.ceil(lastGiftAmount / ROUNDING_AMOUNT) * ROUNDING_AMOUNT);
				dn2 = String.valueOf(Math.ceil(lastGiftAmount * 1.3 / ROUNDING_AMOUNT) * ROUNDING_AMOUNT);
				dn3 = String.valueOf(Math.ceil(lastGiftAmount * 1.6 / ROUNDING_AMOUNT) * ROUNDING_AMOUNT);
				
				if(dn1.equalsIgnoreCase(dn2))
					dn2 = String.valueOf(Integer.parseInt(dn2.replaceAll("\\.0", "")) + 5);
				
				if(dn2.equalsIgnoreCase(dn3))
					dn3 = String.valueOf(Integer.parseInt(dn3.replaceAll("\\.0", "")) + 5);
			}
			
			record.setDn1Amt(dn1);
			record.setDn2Amt(dn2);
			record.setDn3Amt(dn3);
			record.setODnAmt(dn4);
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
