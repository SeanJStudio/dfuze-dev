/**
 * Project: Dfuze
 * File: Load.java
 * Date: Oct 15, 2020
 * Time: 5:52:18 PM
 */
package com.mom.dfuze.data.jobs.utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class CleanPSMFiles implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "CleanPSM";
	protected String DESCRIPTION = "<html>fix file list</html>";
	protected String[] REQUIRED_FIELDS = {};
	
	//start
	private final static Pattern PATH_PATTERN = Pattern.compile("[ ][D][i][r][e][c][t][o][r][y][ ][o][f][ ][Y][:]", Pattern.CASE_INSENSITIVE);
	
	//end
	private final static Pattern FILE_LINE_PATTERN = Pattern.compile("\\d+ File\\(s\\)", Pattern.CASE_INSENSITIVE);
	
	private final static Pattern DATE_PATTERN = Pattern.compile("\\d+/\\d+/\\d+", Pattern.CASE_INSENSITIVE);
	private final static Pattern TIME_PATTERN = Pattern.compile("\\d\\d\\:\\d\\d\\s\\D\\D(?=\\s)", Pattern.CASE_INSENSITIVE);
	private final static Pattern SIZE_PATTERN = Pattern.compile("           \\d+(,)?\\d+", Pattern.CASE_INSENSITIVE);
	private final static Pattern FILE_PATTERN = Pattern.compile("[^ ]+(?=\\.psmd)", Pattern.CASE_INSENSITIVE);
	
	private final static int PATH_INDEX = 0;
	private final static int DATE_INDEX = 1;
	private final static int TIME_INDEX = 2;
	private final static int SIZE_INDEX = 3;
	private final static int FILE_INDEX = 4;
	
	
	
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

		String[][] data = userData.getData();
		
		String[] blankData = {};
		userData.setInHeaders(blankData);
		
		String[] newData = new String[5];
		ArrayList<String[]> records = new ArrayList<>();
		
		Boolean isStarted = false;
		String currentPath = "";

		int counter = -1;
		for (int i = 0; i < data.length; i++) {
			String input = data[i][0];
			
			if(!isStarted) {
				if(PATH_PATTERN.matcher(input).find()) {
					isStarted = true;
					currentPath = input;
					continue;
				}
			}
			
			if(isStarted) {				
				if(FILE_PATTERN.matcher(input).find()) {
					Matcher dateMatcher = DATE_PATTERN.matcher(input);
					Matcher timeMatcher = TIME_PATTERN.matcher(input);
					Matcher sizeMatcher = SIZE_PATTERN.matcher(input);
					Matcher fileMatcher = FILE_PATTERN.matcher(input);
					
					System.out.println(input);
					newData[PATH_INDEX] = currentPath;
					newData[DATE_INDEX] = (dateMatcher.find()) ? dateMatcher.group() : "";
					newData[TIME_INDEX] = (timeMatcher.find()) ? timeMatcher.group() : "";
					newData[SIZE_INDEX] = (sizeMatcher.find()) ? sizeMatcher.group() : "";
					newData[FILE_INDEX] = (fileMatcher.find()) ? fileMatcher.group() : "";
					records.add(newData);
					newData = new String[5];
				}
				
				if(FILE_LINE_PATTERN.matcher(input).find()) {
					currentPath = "";
					isStarted = false;
				}
			}

		}
		
		for(int i = 0; i < records.size(); ++i) {
			Record record = new Record.Builder(++counter, blankData, "", "", "")
					.setNam1(records.get(i)[PATH_INDEX])
					.setNam1_2(records.get(i)[DATE_INDEX])
					.setNam2(records.get(i)[TIME_INDEX])
					.setNam2_2(records.get(i)[SIZE_INDEX])
					.setCmpny(records.get(i)[FILE_INDEX])
					.build();
			
			userData.add(record); // add the record the recordList in userData
		}

		// set the Header fields that we have used
		userData.setDfHeaders(new String[] {
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.NAME1_2.getName(),
				UserData.fieldName.NAME2.getName(),
				UserData.fieldName.NAME2_2.getName(),
				UserData.fieldName.COMPANY.getName(),
				});

	}

}
