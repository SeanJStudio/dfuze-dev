package com.mom.dfuze.data.jobs.ritchiebrothers;

import java.util.ArrayList;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

public class RunExtractWordData implements RunRitchieBrothersBehavior {

	private String BEHAVIOR_NAME = "Extract data from word file";
	private String DESCRIPTION = "Copy word document into notepad and add a header row of any value if there isnt one."
			+ "\n\nLoad the text file and delimit by |"
			+ "\n\nThe start of a record is indicated by a space and the end is denoted by \"p:\"";
	private String[] REQUIRED_FIELDS = { };

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

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) {

		String[] blankData = {};
		userData.setInHeaders(blankData);
		String[][] data = userData.getData();

		ArrayList<ArrayList<String>> records = new ArrayList<>();
		ArrayList<String> recordData = new ArrayList<>();

		boolean isEndOfRecord = false;

		int counter = -1;

		for (int i = 0; i < data.length; i++) {

			while(isEndOfRecord) {
				++i;
				if(!isEOF(i, data)) {
					if(data[i][0].isBlank()) {
						isEndOfRecord = false;
						break;
					}
				} else {
					isEndOfRecord = false;
					break;
				}
			}

			if(i < data.length) {
				String str = data[i][0];

				// if blank start new data collection and reset
				if(str.isBlank() && recordData.size() > 0) {
					records.add(recordData);
					recordData = new ArrayList<>();
					continue;
				}

				if(!isEndOfRecordData(str)) {
					recordData.add(str);
				} else {
					isEndOfRecord = true;
				}

			}
		}
		
		for(int i = 0; i < records.size(); ++i) {
			Record record = new Record.Builder(++counter, blankData, "", "", "").build();
			for(int j = 0; j < records.get(i).size(); ++j) {
				String fieldData = records.get(i).get(j);
				switch(j) {
				case 0:
					record.setNam1(fieldData);
					break;
				case 1:
					record.setAdd1(fieldData);
					break;
				case 2:
					record.setAdd1_2(fieldData);
					break;
				case 3:
					record.setAdd2(fieldData);
					break;
				case 4:
					record.setAdd2_2(fieldData);
					break;
				default:
					record.setAdd2_2(record.getAdd2_2() + " " + fieldData);	
				}
			}
			
			if(record.getNam1() == null)
				record.setNam1("");
			if(record.getAdd1() == null)
				record.setAdd1("");
			if(record.getAdd1_2() == null)
				record.setAdd1_2("");
			if(record.getAdd2() == null)
				record.setAdd2("");
			if(record.getAdd2_2() == null)
				record.setAdd2_2("");

			userData.add(record);
		}

		// set the Header fields that we have used
		userData.setDfHeaders(
				new String[] {
						UserData.fieldName.NAME1.getName(),
						UserData.fieldName.ADDRESS1.getName(),
						UserData.fieldName.ADDRESS1_2.getName(),
						UserData.fieldName.ADDRESS2.getName(),
						UserData.fieldName.ADDRESS2_2.getName()
						});


	}

	private boolean isEOF(int i, String[][] userData) {
		if(i < userData.length) {
			return false;
		}

		return true;
	}

	private boolean isEndOfRecordData(String str) {
		if(str.length() >= 2)
			if(str.substring(0, 2).equalsIgnoreCase("p:"))
				return true;

		return false;
	}

}
