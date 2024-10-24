package com.mom.dfuze.data.jobs.bcaa;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.io.TextWriter;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserInputDialog;

public class PotSweetner implements RunBCAABehavior {

	private String BEHAVIOR_NAME = "Pot Sweetner";
	
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.DEAR_SALUTATION.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.MIDDLENAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName(),
			};
	
	private String DESCRIPTION = "<html>Instructions<br/><ol><li>Click add file name to data checkbox</li></ol><br/>Description<br/><ul><li>Corrects BCAA seed data alignment</li><li>Cases name fields</li><li>Adds dfNam1 field</li><li>Adds dfID field</li><li>Adds dfFileName field</li><li>Adds 1 seed for Brenda</li><li>Creates John Q Samples file</li></ul>"+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)+"</html>";

	public static final String JQS_FILENAME = "JQS.csv";
	public static final String DUPE_FILENAME = "DUPES.csv";

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		/********************************
		 * MAKE SURE FILENAME IS INCLUDED
		 ********************************/
		if(!Common.hasFileName(userData))
			throw new ApplicationException(String.format("Error: file name is not included\n\nPlease do a new job and ensure the add file name to data checkbox is checked."));

		// The users' input data
		String[][] data = userData.getData();
		
		/****************
		 * FIX SEEDS HERE
		 ****************/
		for (int i = 0; i < data.length; ++i) {
			String fnam = data[i][userData.getFstNameIndex()];
			String mnam = data[i][userData.getMidNameIndex()];
			String lnam = data[i][userData.getLstNameIndex()];
			
			if(fnam.isBlank() && lnam.isBlank() && !mnam.isBlank())  // shift data to the left by 1 column
				for(int j = userData.getFstNameIndex(); j < data[i].length; ++j)
					data[i][j - 1] = data[i][j];
		
		}
		
		/****************
		 * FIND ID FIELDS
		 ****************/
		ArrayList<Integer> idFields = new ArrayList<>();
		for(int i = 0; i < userData.getInHeaders().length; ++i) {
			if(userData.getInHeaders()[i].matches("(?i)(^id|_id|\\s+id)"))
				idFields.add(i);
		}
		

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {
			/********************************
			 * CASE THE NAME COMPONENTS HERE
			 ********************************/
			data[i][userData.getDearSalIndex()] = Common.caseName(data[i][userData.getDearSalIndex()]);
			data[i][userData.getFstNameIndex()] = Common.caseName(data[i][userData.getFstNameIndex()]);
			data[i][userData.getMidNameIndex()] = Common.caseName(data[i][userData.getMidNameIndex()]);
			data[i][userData.getLstNameIndex()] = Common.caseName(data[i][userData.getLstNameIndex()]);
			
			String prefix = data[i][userData.getDearSalIndex()];
			String fnam = data[i][userData.getFstNameIndex()];
			String mnam = data[i][userData.getMidNameIndex()];
			String lnam = data[i][userData.getLstNameIndex()];
			String nam1 = Common.caseName((prefix + " " + fnam + " " + mnam + " " + lnam).replaceAll("\\s+", " ").trim());
			
			/***********************
			 * BUILD THE RECORD HERE
			 ***********************/
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setNam1(nam1)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] {
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.DFID.getName()
		});


		// Create a new id to use from where we left off
		int idToUse = data.length - 1;
		int membershipNumToUse = 9999999;

		/************************
		 * ADD BRENDA'S SEED HERE
		 ************************/
		String[] seedArray = Arrays.copyOf(data[0], data[0].length);
		seedArray[userData.getDearSalIndex()] = "";
		seedArray[userData.getFstNameIndex()] = "Sam";
		seedArray[userData.getMidNameIndex()] = "";
		seedArray[userData.getLstNameIndex()] = "Porter";
		
		seedArray[userData.getAdd1Index()] = "7550 Lowland Dr";
		seedArray[userData.getAdd2Index()] = "";
		seedArray[userData.getCityIndex()] = "Burnaby";
		seedArray[userData.getProvIndex()] = "BC";
		seedArray[userData.getPCodeIndex()] = "V5J5A4";
		
		for(Integer tmpID : idFields)
			seedArray[tmpID] = String.valueOf(membershipNumToUse);

		Record seedRecord = new Record.Builder(++idToUse, seedArray, "", "", "").setNam1("Sam Porter").build();
		userData.add(seedRecord);
		
		
		/************************
		 * REMOVE DUPLICATES HERE
		 ************************/
		/*
		List<Record> dupeRecordList = new ArrayList<>();
		Set<String> membershipAccIDs = new HashSet<>();

		for(int i = userData.getRecordList().size() -1; i >= 0; --i) {
			Record record = userData.getRecordList().get(i);
			String membershipAccID = record.getDfInData()[MEMBERSHIP_INDEX] + record.getDfInData()[ACCOUNT_INDEX];
			if(!membershipAccIDs.add(membershipAccID))
				dupeRecordList.add(userData.getRecordList().remove(i));
		}
		
		if(dupeRecordList.size() > 0) {
			File dupeFile = new File(UiController.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_" + DUPE_FILENAME);

			// Write the JQS to the file
			try {
				TextWriter.write(dupeFile, ',', userData.getExportHeaders(), userData.getExportData(dupeRecordList));
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		/********************
		 * GET THE JOB NAME
		 ********************/
		String jobName = "";
		UserInputDialog userInputDialog = new UserInputDialog(UiController.getMainFrame(), "Enter the Docket# and Job Name ex. 70684 BCAA POT SWEETNER");
		userInputDialog.setVisible(true);
		
		if(userInputDialog.getIsNextPressed())
			jobName = userInputDialog.getUserInput();
		else
			throw new ApplicationException(String.format("ERROR: dialog closed"));
		
		/**********************************
		 * CREATE JOHN Q SAMPLES HERE
		 **********************************/
		List<Record> jqsRecordList = new ArrayList<>();
		String[] jqsArray = Arrays.copyOf(data[0], data[0].length);

		jqsArray[userData.getDearSalIndex()] = "";
		jqsArray[userData.getFstNameIndex()] = "John";
		jqsArray[userData.getMidNameIndex()] = "";
		jqsArray[userData.getLstNameIndex()] = "Sample";
		
		jqsArray[userData.getAdd1Index()] = "123 Any St";
		jqsArray[userData.getAdd2Index()] = jobName;
		jqsArray[userData.getCityIndex()] = "City";
		jqsArray[userData.getProvIndex()] = "Prov";
		jqsArray[userData.getPCodeIndex()] = "V0V 0V0";

		for(int i = 0; i < 5; ++i) {
			String[] tempJqsArray = Arrays.copyOf(jqsArray, jqsArray.length);
			
			for(Integer tmpID : idFields) {
				tempJqsArray[tmpID] = String.valueOf(--membershipNumToUse);
			}
			
			Record record = new Record.Builder(++idToUse, tempJqsArray, "", "", "").setNam1("John Sample").build();
			jqsRecordList.add(record);
		}

		File jqsFile = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_" + JQS_FILENAME);

		// Write the JQS to the file
		try {
			TextWriter.write(jqsFile, ',', true, userData.getExportHeaders(), userData.getExportData(jqsRecordList));
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		// TODO Auto-generated method stub
		return true;
	}

}