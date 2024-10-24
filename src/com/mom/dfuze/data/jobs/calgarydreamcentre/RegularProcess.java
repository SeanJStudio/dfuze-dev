/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.calgarydreamcentre;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;


/**
 * RegularProcess implements a RunBehavior for Calgary Dream Centre Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RegularProcess implements RunCalgaryDreamCentreBehavior {

	private String BEHAVIOR_NAME = "Regular Process";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.POSTALCODE.getName()
			};
	
	private String DESCRIPTION = "<html>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Reformats names ex. Johnson, Sean -> Sean Johnson</li>"
	        + "<li>Infers province from first letter of postal code</li>"
	        + "</ol>"
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
			String name = data[i][userData.getNam1Index()].replaceAll("\\s+", " ").trim();
			String pc = data[i][userData.getPCodeIndex()];
			
			// Initialize fields for the name parts
			String newName = "", firstName = "", lastName = "", dearSal = "";
			
			//Split name lines
			String[] nameParts = name.split(",+");
			
			// 0 commas means its a company
			if(nameParts.length == 1) {
				newName = nameParts[0].trim();
				dearSal = "Friends";
			}

			// 1+ commas means its a person
			if(nameParts.length > 1) {
				firstName = nameParts[nameParts.length-1].trim();
				dearSal = nameParts[nameParts.length-1].trim();
				
				// iterate name parts excluding the last part
				for(int j = 0; j < nameParts.length - 1; ++j) {
					if(lastName.length() == 0)
						lastName = nameParts[j];
					else
						lastName += " " + nameParts[j];
				}
				
				lastName = lastName.replaceAll("\\s+", " ").trim();
				
				newName = (firstName + " " + lastName).replaceAll("\\s+", " ").trim();
			}
			
			// Fix the salutations to Friend if the first name is ugly
			if(dearSal.length() == 1
   					|| dearSal.length() == 0
   					|| dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
   					|| dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
   					|| dearSal.length() == 2 && !Validators.hasVowel(dearSal)
   					) {

   					dearSal = "Friend";
   			}
			
			// infer the province from the postal code
			String province = Common.pc2prov(pc);

			// Build the Record
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setProv(province)
					.setFstName(firstName)
					.setLstName(lastName)
					.setDearSal(dearSal)
					.setNam1(newName)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] {
				UserData.fieldName.PROVINCE.getName(),
				UserData.fieldName.FIRSTNAME.getName(),
				UserData.fieldName.LASTNAME.getName(),
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.NAME1.getName(),
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
