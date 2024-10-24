package com.mom.dfuze.data.jobs.harveymckinnonassociates;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;


public class SimonFraserUniversity implements RunHarveyMckinnonAssociatesBehavior {
	private String BEHAVIOR_NAME = "SFU";

	private String[] REQUIRED_FIELDS = { 
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.SPOUSE_FIRSTNAME.getName(),
			UserData.fieldName.SPOUSE_LASTNAME.getName(),
			UserData.fieldName.ADDRESS1.getName(),		//add1
			UserData.fieldName.ADDRESS1_2.getName(),	//add2
			UserData.fieldName.ADDRESS2.getName(),		//add3
			UserData.fieldName.ADDRESS2_2.getName()		//add4	
	};
	
	private String DESCRIPTION = "<html>Instructions<br/><ol>"
			+ "<li>Add seeds to supplied data</li><li>Load the supplied data and run</li></ol>"
	        + "Description<br/><ol><li>Builds the salutation</li><li>fixes address lines</li></ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		String[][] prefixesForNameSplitting = Lookup.getNameJoiners();

		// The users' input data
		String[][] data = userData.getData();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {

			// Get all the fields we can for now
			String fname = data[i][userData.getFstNameIndex()];
			String lname = data[i][userData.getLstNameIndex()];
			String spouseFname = data[i][userData.getSpouseFstNameIndex()];
			String spouseLname = data[i][userData.getSpouseLstNameIndex()];
			String og_add1 = data[i][userData.getAdd1Index()].replaceAll("\\n", " ").trim();
			String og_add2 = data[i][userData.getAdd1_2Index()].replaceAll("\\n", " ").trim();
			String og_add3 = data[i][userData.getAdd2Index()].replaceAll("\\n", " ").trim();
			String og_add4 = data[i][userData.getAdd2_2Index()].replaceAll("\\n", " ").trim();

			// Fields we will be creating
			//String nam1 = (fname + " " + lname).replaceAll("  ", " ").trim();
			String nam1 = "";
			String dearSal = "";
			String cmpny_add1 = "";
			String cmpny_add2 = "";
			String add1 = "";
			String add2 = "";
			
			/****************************************************
			 * CREATE THE FULL NAME
			 * **************************************************/
			if(spouseFname.trim().length() > 0) { // if there is a spouse name
				if(lname.toLowerCase().trim().equalsIgnoreCase(spouseLname.toLowerCase().trim())) 
					nam1 = (fname + " and " + spouseFname + " " + lname);
				else
					nam1 = (fname + " " + lname + " and " + spouseFname + " " + spouseLname);
			} else { // if there is no spouse name
				nam1 = (fname + " " + lname);
			}
			
			nam1 = nam1.replaceAll("  ", " ").trim();
			
			/****************************************************
			 * CREATE THE SALUTATION
			 * **************************************************/
			
			String fnameSal = "";
			String spouseFnameSal = "";
			
			String[] fnameParts = fname.split("\\s+");
			
			for(String part : fnameParts) {
				String tempPart = part.replaceAll("[^\\p{L}0-9]", "");
				
				if(tempPart.length() > 1)
					fnameSal += part + " ";
			}
			
			String[] spouseFnameParts = spouseFname.split("\\s+");
			
			for(String part : spouseFnameParts) {
				String tempPart = part.replaceAll("[^\\p{L}0-9]", "");
				
				if(tempPart.length() > 1)
					spouseFnameSal += part + " ";
			}
			
			if(spouseFnameSal.length() > 0)
				dearSal = fnameSal + " and " + spouseFnameSal;
			else
				dearSal = fnameSal;
			
			dearSal = dearSal.replaceAll("  ", " ").trim();
				
			/****************************************************
			 * FIX THE ADDRESS LINES
			 * **************************************************/
			if(og_add4.trim().length() > 0) {
				
				boolean comp1Changed = false;
				boolean comp2Changed = false;
				
				if(og_add1.replaceAll("[^0-9]","").length() == 0) {
					cmpny_add1 = og_add1;
					comp1Changed = true;
				}
				
				if(og_add2.replaceAll("[^0-9]","").length() == 0) {
					if(cmpny_add1.length() == 0)
						cmpny_add1 = og_add2;
					else
						cmpny_add2 = og_add2;
					
					comp2Changed = true;
				}
				
				if(!comp1Changed && !comp2Changed)
					throw new Error("Could not fit address lines for record #" + (i+1));
				
				if(!comp1Changed) {
					add1 = og_add1;
					
					if(og_add3.length() > 0)
						add2 = og_add3;
					else {
						add2 = og_add4;
					}
				} else if(!comp2Changed) {
					add1 = og_add2;
					
					if(og_add3.length() > 0)
						add2 = og_add3;
					else {
						add2 = og_add4;
					}
				} else {
					add1 = og_add3;
					add2 = og_add4;
				}
				
			} else if(og_add3.trim().length() > 0) {
				
				boolean comp1Changed = false;
				boolean comp2Changed = false;
				
				if(og_add1.replaceAll("[^0-9]","").length() == 0) {
					cmpny_add1 = og_add1;
					comp1Changed = true;
				}
				
				if(og_add2.replaceAll("[^0-9]","").length() == 0) {
					if(cmpny_add1.length() == 0)
						cmpny_add1 = og_add2;
					else
						cmpny_add2 = og_add2;
					
					comp2Changed = true;
				}
				
				if(!comp1Changed && !comp2Changed)
					throw new Error("Could not fit address lines for record #" + (i+1));
				
				if(!comp1Changed) {
					add1 = og_add1;
					
					if(og_add3.length() > 0)
						add2 = og_add3;
					else {
						add2 = og_add4;
					}
				} else if(!comp2Changed) {
					add1 = og_add2;
					
					if(og_add3.length() > 0)
						add2 = og_add3;
					else {
						add2 = og_add4;
					}
				} else {
					add1 = og_add3;
					add2 = og_add4;
				}				

			
			} else {
				add1 = og_add1;
				add2 = og_add2;
			}

			Record record = new Record.Builder(i, data[i], "", "", "")
					.setNam1(nam1)
					.setNam1_2("")
					.setDearSal(dearSal)
					.setCmpny(cmpny_add1)
					.setCmpny_2(cmpny_add2)
					.setAdd1(add1)
					.setAdd2(add2)
					.build();

			record = Common.splitAddName(record, 36, prefixesForNameSplitting);
			userData.add(record);

		}

		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.NAME1_2.getName(),
				UserData.fieldName.COMPANY.getName(),	// Company address1
				UserData.fieldName.COMPANY_2.getName(),	// Company address2
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.ADDRESS2.getName()
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