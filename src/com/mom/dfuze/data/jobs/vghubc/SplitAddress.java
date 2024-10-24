/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.vghubc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.dedupe.DedupeDialog;


/**
 * RegularProcess implements a RunBehavior for VGHUBC Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class SplitAddress implements RunVGHUBCBehavior {

	private String BEHAVIOR_NAME = "Split Address";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.ADDRESS1.getName()
			};
	
	private String DESCRIPTION = "<html>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Splits address on returns into 6 address fields</li>"
	        + "</ol>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load supplied data file and run job</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";
	
	private static final Pattern GENERAL_DELIVERY_PATTERN = Pattern.compile(DedupeDialog.FIX_GD_REGEX, Pattern.CASE_INSENSITIVE);

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
			String add = data[i][userData.getAdd1Index()];

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
					.setCmpnyAdd1(cmpnyAdd1)
					.setCmpnyAdd2(cmpnyAdd2)
					.setAdd1(add1)
					.setAdd1_2(add2)
					.setAdd2(add3)
					.setAdd2_2(add4)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] {
				UserData.fieldName.COMPANY_ADDRESS1.getName(),
				UserData.fieldName.COMPANY_ADDRESS2.getName(),
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.ADDRESS1_2.getName(),
				UserData.fieldName.ADDRESS2.getName(),
				UserData.fieldName.ADDRESS2_2.getName()
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
