package com.mom.dfuze.data.jobs.harveymckinnonassociates;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;


public class SpecialOlympicsCanada implements RunHarveyMckinnonAssociatesBehavior {
	private String BEHAVIOR_NAME = "SOC";

	private String[] REQUIRED_FIELDS = { 
			UserData.fieldName.PREFIX.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.MIDDLENAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.COMPANY.getName(),
			UserData.fieldName.APPEAL.getName(),
			UserData.fieldName.SEGMENT_CODE.getName(),
			UserData.fieldName.BARCODE.getName()			
	};
	
	private String DESCRIPTION = "<html>Instructions<br/><ol>"
			+ "<li>Add seeds to supplied data</li><li>Load the supplied data and run</li></ol>"
	        + "Description<br/><ol><li>Builds the fullname and salutation</li><li>Builds the barcode</li></ol>"
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
			String prefix = data[i][userData.getPrefixIndex()].replaceAll("  ", " ").trim();
			String fname = data[i][userData.getFstNameIndex()].replaceAll("  ", " ").trim();
			String mname = data[i][userData.getMidNameIndex()].replaceAll("  ", " ").trim();
			String lname = data[i][userData.getLstNameIndex()].replaceAll("  ", " ").trim();
			String cmpny = data[i][userData.getCmpnyIndex()].replaceAll("  ", " ").trim();
			String apeal = data[i][userData.getAppealIndex()];
			String segCode = data[i][userData.getSegCodeIndex()];
			String scanline = data[i][userData.getBarCodeIndex()];

			// Initialize the extra fields we want the record to have
			String nam1 = String.format("%s %s %s %s", prefix, fname, mname, lname).replaceAll("  ", " ").trim();
			String dearSal = fname;
			String paraSal = dearSal;
			String codeline = String.format("%s-%s", apeal, segCode);
			String bCode = "";

			boolean recIsBusiness = (cmpny.length() > 0) ? true : false;

			// Fix name1
			if(nam1.trim().equalsIgnoreCase(cmpny.trim()))
				nam1 = "";

			//Fix the salutation
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

			 StringBuilder sb = new StringBuilder(scanline)
					 	.insert(9," ")
			 			.insert(6," ")
			 			.insert(3," ");

			bCode = sb.toString();

			Record record = new Record.Builder(i, data[i], "", "", "")
					.setNam1(nam1)
					.setNam1_2("")
					.setCmpny(cmpny)
					.setDearSal(dearSal)
					.setParaSal(paraSal)
					.setCodeLine(codeline)
					.setBarCode(bCode)
					.build();

			record = Common.splitAddName(record, 36, prefixesForNameSplitting);
			userData.add(record);

		}

		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.NAME1_2.getName(),
				UserData.fieldName.COMPANY.getName(),
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.PARAGRAPH_SALUTATION.getName(),
				UserData.fieldName.CODELINE.getName(),
				UserData.fieldName.BARCODE.getName()
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