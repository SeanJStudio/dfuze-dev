package com.mom.dfuze.data.jobs.harveymckinnonassociates;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;


public class BCChildrensHospitalFoundation implements RunHarveyMckinnonAssociatesBehavior {
	private String BEHAVIOR_NAME = "BCCHF";

	private String[] REQUIRED_FIELDS = { 
			UserData.fieldName.IN_ID.getName(),
			UserData.fieldName.PACKAGE_VERSION.getName(), // I'm using this to hold the PACK ID
			UserData.fieldName.APPEAL.getName(),
			UserData.fieldName.LETTER_VERSION.getName(), // I'm using this to hold the LETTER CODE		
			UserData.fieldName.COMPANY.getName(),
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.DEAR_SALUTATION.getName(),
			UserData.fieldName.COMPANY_CONTACT.getName(),
			UserData.fieldName.COMPANY_SALUTATION.getName(),
			
			
			//UserData.fieldName.RECORD_TYPE.getName(),
			
	};
	
	private String DESCRIPTION = "<html>Instructions<br/><ol>"
			+ "<li>Add seeds to supplied data</li><li>Load the supplied data and run</li></ol>"
	        + "Description<br/><ol><li>Builds the salutation</li><li>Builds the codeline</li><li>Builds the barcode</li></ol>"
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
			String dearSal = data[i][userData.getDearSalIndex()];
			String nam1 = data[i][userData.getNam1Index()];
			String cmpny = data[i][userData.getCmpnyIndex()];
			String cmpnyCn = data[i][userData.getCmpnyCnIndex()];
			String cmpnySal = data[i][userData.getCmpnySalIndex()];
			String appeal = data[i][userData.getAppealIndex()];
			//String recType = data[i][userData.getRecTypeIndex()];
			String letCode = data[i][userData.getLetVerIndex()];
			String pkgId = data[i][userData.getPkgVerIndex()];
			String inId = data[i][userData.getInIdIndex()];

			// Initialize the extra fields we want the record to have
			String codeLine = "";
			String bCode = "";

			// what type of record is this? i is individual, o is business
			//boolean recIsIndividual = (recType.trim().equalsIgnoreCase("i")) ? true : false;
			//boolean recIsBusiness = (recType.trim().equalsIgnoreCase("o")) ? true : false;
			boolean recIsBusiness = (cmpny.length() > 0) ? true : false;

			// Fix name1
			if(nam1.trim().equalsIgnoreCase(cmpny.trim()))
				nam1 = "";

			if(recIsBusiness) {
				nam1 = cmpnyCn;
				dearSal = cmpnySal;
			}

			String paraSal = dearSal;

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

			// Build the barcode
			bCode = String.format("%s\t%s\t%s\t%s",inId,appeal,pkgId,letCode);

			// Build the codeline		
			codeLine = String.format("%s %s %s %s",inId,appeal,pkgId,letCode);

			Record record = new Record.Builder(i, data[i], "", "", "")
					.setNam1(nam1)
					.setNam1_2("")
					.setDearSal(dearSal)
					.setParaSal(paraSal)
					.setCmpny(cmpny)
					//.setRecType(recType)
					.setInId(inId)
					.setCodeLine(codeLine)
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