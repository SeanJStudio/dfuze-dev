package com.mom.dfuze.data.jobs.harveymckinnonassociates;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;


public class CovenantHouse  implements RunHarveyMckinnonAssociatesBehavior {
	private String BEHAVIOR_NAME = "CH";
	
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.PREFIX.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.DEAR_SALUTATION.getName(),
			UserData.fieldName.COMPANY.getName(),
			UserData.fieldName.COMPANY_CONTACT.getName(),
			UserData.fieldName.COMPANY_SALUTATION.getName(),
			UserData.fieldName.RECORD_TYPE.getName()
			};
	
	private String DESCRIPTION = "<html>Instructions<br/><ol><li>Add seeds to supplied data</li>" + 
			  "<li>Load supplied data and run</li></ol>"
			  + "Description<br/><ol><li>Splits long names</li><li>Builds the salutation</li></ol>"
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
			String prefix = data[i][userData.getPrefixIndex()];
			String lnam = data[i][userData.getLstNameIndex()];
			String dearSal = data[i][userData.getDearSalIndex()];
			String paraSal = dearSal;
			String cmpnySal = data[i][userData.getCmpnySalIndex()];
			String cmpnyCn = data[i][userData.getCmpnyCnIndex()];
			String nam1 = data[i][userData.getNam1Index()];
			String cmpny = data[i][userData.getCmpnyIndex()];
			String recType = data[i][userData.getRecTypeIndex()];
			
			// Fix name1
      		if(nam1.trim().equalsIgnoreCase(cmpny.trim()))
      			nam1 = "";
			
			// what type of record is this? i is individual, o is business
			boolean recIsBusiness = (recType.trim().equalsIgnoreCase("o")) ? true : false;
			
			//Fix salutation for companies
			if(recIsBusiness) {
				if(!cmpnySal.isBlank()) {
					dearSal = cmpnySal;
					paraSal = cmpnySal;
				} else {
					dearSal = "friend";
					paraSal = "";
				}
				
				if(nam1.trim().equalsIgnoreCase(cmpny.trim()))
					nam1 = "";
				
				if(!cmpnyCn.trim().isEmpty())
					nam1 = "Attn: " + cmpnyCn;
			}

			//Fix the salutation
			if(dearSal.equalsIgnoreCase("friend") || dearSal.equalsIgnoreCase("friends")) {
				dearSal = "";
				paraSal = "";
			}
			
			if(dearSal.isBlank()) {
				if(!prefix.isBlank() && !lnam.isBlank()) {
					dearSal = (prefix + " " + lnam).trim();
					paraSal = dearSal;
				}
			}

			if(dearSal.length() == 1
					|| dearSal.length() == 0
					|| dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
					|| dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
					|| dearSal.length() == 2 && !Validators.hasVowel(dearSal)
					) {

				if(!prefix.isBlank() && !lnam.isBlank()) {
					dearSal = (prefix + " " + lnam).trim();
					paraSal = dearSal;
				} else {
					dearSal = "Friend";
					paraSal = "";
				}
			}

			
			Record record = new Record.Builder(i, data[i], "", "", "").setNam1(nam1).setNam1_2("").setCmpny(cmpny).setDearSal(dearSal).setParaSal(paraSal).build();

			record = Common.splitAddName(record, 36, prefixesForNameSplitting);
			
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] {
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.NAME1_2.getName(),
				UserData.fieldName.COMPANY.getName(),
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.PARAGRAPH_SALUTATION.getName()
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