/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.vghubc;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.ui.dedupe.DedupeDialog;

/**
 * RegularProcess implements a RunBehavior for VGHUBC Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RegularProcess implements RunVGHUBCBehavior {

	private String BEHAVIOR_NAME = "Regular Process";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.LETTER_VERSION.getName(),
			UserData.fieldName.REPLY_VERSION.getName(),
			UserData.fieldName.DONATION1_AMOUNT.getName(),
			UserData.fieldName.DONATION2_AMOUNT.getName(),
			UserData.fieldName.DONATION3_AMOUNT.getName(),
			UserData.fieldName.DONATION4_AMOUNT.getName(),
			UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
			UserData.fieldName.ADDRESS1.getName()
			};
	
	private String DESCRIPTION = "<html>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Populates ask amounts from LetVar into dfLetVer</li>"
	        + "<li>Populates last gift amount for RepVar into dfRepVer</li>"
	        + "<li>Populates last gift amount for Ask1 into dfDn1Amt</li>"
	        + "<li>Analyzes single asks to identify variations into dfSegCode</li>"
	        + "<li>Adds dollar sign to single asks</li>"
	        + "<li>Splits address on returns into 2 company and 4 address fields</li>"
	        + "</ol>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Check for inconsistancies in telvar and asks before running</li>"
			+ "<li>Map the LetVar field with placeholders, if any, to " + UserData.fieldName.LETTER_VERSION.getName() + "</li>"
			+ "<li>Map the RepVar field with placeholders, if any, to " + UserData.fieldName.REPLY_VERSION.getName() + "</li>"
			+ "<li>Load all supplied data files and run job</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";
	
	private static final Pattern GENERAL_DELIVERY_PATTERN = Pattern.compile(DedupeDialog.FIX_GD_REGEX, Pattern.CASE_INSENSITIVE);
	
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		// composite key from ask values with int to letter value pair
		HashMap<String, String> askVersions = new HashMap<>();
		
		// init the value pair for the ask key
		int askPair = 0;
		
		DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

		// The users' input data
		String[][] data = userData.getData();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {

			// Get all the fields we can for now
			String letVer = data[i][userData.getLetVerIndex()];
			String repVer = data[i][userData.getRepVerIndex()];
			
			String add = data[i][userData.getAdd1Index()];
			
			String ask1 = data[i][userData.getDn1AmtIndex()];
			String ask2 = data[i][userData.getDn2AmtIndex()];
			String ask3 = data[i][userData.getDn3AmtIndex()];
			String ask4 = data[i][userData.getDn4AmtIndex()];
			
			String lastGiftAmt = data[i][userData.getLstDnAmtIndex()].replaceAll("[^0-9\\.]", "");
			
			double newLG = 0;
			if(Validators.isNumber(lastGiftAmt)) {
				newLG = Double.parseDouble(lastGiftAmt);
			}
			
			StringBuilder askKey = new StringBuilder();
			
			// looking for non number values
			if(!Validators.isNumber(ask4))
				askKey.append(ask4.replaceAll("\\s|_|<|>|\\(|\\)", ""));
			else
				askKey.append("X");
			
			if(!Validators.isNumber(ask3))
				askKey.append(ask3.replaceAll("\\s|_|<|>|\\(|\\)", ""));
			else
				askKey.append("X");
			
			if(!Validators.isNumber(ask2))
				askKey.append(ask2.replaceAll("\\s|_|<|>|\\(|\\)", ""));
			else
				askKey.append("X");
			
			if(!Validators.isNumber(ask1))
				askKey.append(ask1.replaceAll("\\s|_|<|>|\\(|\\)", ""));
			else
				askKey.append("X");
			
			// if not present, add the key and generate the version value
			if(!askVersions.containsKey(askKey.toString())) {
				//System.out.println(askKey.toString());
				askVersions.put(askKey.toString(), Common.toAlphabetic(askPair++));
			}

			// version has been determined, now we can update placeholder values
			letVer = letVer.replaceAll("\\$",""); // data comes inconsistent, solution is to remove dollar signs and add in after

			letVer = letVer.replaceAll("(?i)(<)?Ask(\\s+)?1(>)?", Matcher.quoteReplacement("$"+ask1)).replaceAll("(?i)(<)?Ask(\\s+)?2(>)?", Matcher.quoteReplacement("$"+ask2)).replaceAll("(?i)(<)?Ask(\\s+)?3(>)?", Matcher.quoteReplacement("$"+ask3)).replaceAll("  ", " ").trim();
			
			
			repVer = repVer.replaceFirst("(?i)(\\()?(<)?last(\\s+)?gift(>)?", decimalFormat.format(newLG).replaceAll("\\.00", ""));
			ask1 = ask1.replaceFirst("(?i)(\\()?(<)?last(\\s+)?gift(>)?", decimalFormat.format(newLG).replaceAll("\\.00", ""));

			// add dollar signs
			if(Validators.isNumber(ask4))
				ask4 = "$" + ask4;
			if(Validators.isNumber(ask3))
				ask3 = "$" + ask3;
			if(Validators.isNumber(ask2))
				ask2 = "$" + ask2;
			if(Validators.isNumber(ask1))
				ask1 = "$" + ask1;
			
			//Split address lines
			String[] addParts = add.split("\\r?\\n");
			
			int addIndex = 0;
			String cmpnyAdd1 = "", cmpnyAdd2 = "", add1 = "", add2 = "", add3 = "", add4 = "";
			
			for(int j = 0; j < addParts.length; ++j) {
				String part = addParts[j].trim();
				if(part.length() > 0) {
					switch(++addIndex) {
					case 1:
						add1 = part;
						break;
					case 2:
						add2 = part;
						break;
					case 3:
						add3 = part;
						break;
					case 4:
						add4 = part;
						break;
					default:
						add4 += ", " + part;
					}
				}
				
			}
			
			// reformat address here
			Matcher gd_matcher_add1 = GENERAL_DELIVERY_PATTERN.matcher(add1);
			
			// If an address line does not contain a number or general delivery, move it to comp add 1
			if(add1.replaceAll("[^0-9]", "").length() == 0)
				if(!gd_matcher_add1.find()) {
					cmpnyAdd1 = add1;
					add1 = add2;
					add2 = add3;
					add3 = add4;
					add4 = "";
				}
			
			// If an address line does not contain a number or general delivery, move it to comp add 2
			Matcher gd_matcher_add2 = GENERAL_DELIVERY_PATTERN.matcher(add1);
			if(add1.replaceAll("[^0-9]", "").length() == 0)
				if(!gd_matcher_add2.find()) {
					cmpnyAdd2 = add1;
					add1 = add2;
					add2 = add3;
					add3 = add4;
					add4 = "";
				}
			
			// If add1 is empty, we must revert cmpnyAdd1 back to add1
			if(add1.length() == 0) {
				add1 = cmpnyAdd1;
				cmpnyAdd1 = "";
				if(cmpnyAdd2.length() > 0) {
					if(add2.length() == 0) {
						add2 = cmpnyAdd2;
						cmpnyAdd2 = "";
					}
				}
			}
				
			// Build the Record
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setDn1Amt(ask1)
					.setDn2Amt(ask2)
					.setDn3Amt(ask3)
					.setDn4Amt(ask4)
					.setCmpnyAdd1(cmpnyAdd1)
					.setCmpnyAdd2(cmpnyAdd2)
					.setAdd1(add1)
					.setAdd1_2(add2)
					.setAdd2(add3)
					.setAdd2_2(add4)
					.setSegCode(askVersions.get(askKey.toString()))
					.setLetVer(letVer)
					.setRepVer(repVer)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] {
				UserData.fieldName.SEGMENT_CODE.getName(),
				UserData.fieldName.LETTER_VERSION.getName(),
				UserData.fieldName.REPLY_VERSION.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.DONATION4_AMOUNT.getName(),
				UserData.fieldName.COMPANY_ADDRESS1.getName(),
				UserData.fieldName.COMPANY_ADDRESS2.getName(),
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.ADDRESS1_2.getName(),
				UserData.fieldName.ADDRESS2.getName(),
				UserData.fieldName.ADDRESS2_2.getName(),
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
