/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.missionbonaccuiel;

import java.util.regex.Pattern;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

/**
 * Standard2020 implements a RunBehavior for Mission Bon Accuiel Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RegularProcess implements RunMissionBonAccuielBehavior {

	private final String BEHAVIOR_NAME = "Regular Process";
	private String[] REQUIRED_FIELDS = {};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Creates the barcode and priority fields</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ul>"
			+ "<li>Add seeds to supplied data</li>"
			+ "<li>Load supplied data and run</li>"
			+ "</ul>"
			+ "</html>";

	private static Pattern CID_PATTERN = Pattern.compile("[c][i][d]", Pattern.CASE_INSENSITIVE);
	private static Pattern OCCASION_PATTERN = Pattern.compile("[o][c][c][a][s][i][o][n]", Pattern.CASE_INSENSITIVE);
	private static Pattern GROUP_PATTERN = Pattern.compile("[g][r][o][u][p][_][n][a][m][e]|[m][o][t][i][v]", Pattern.CASE_INSENSITIVE);
	private static Pattern ANCHOR3_PATTERN = Pattern.compile("[a][n][c][h][o][r][3]", Pattern.CASE_INSENSITIVE);
	
	private static Pattern SEED_PATTERN = Pattern.compile("[s][e][e][d]", Pattern.CASE_INSENSITIVE);

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
	public void run(UserData userData) throws Exception {

		/**************************
		 * VALIDATE THE FIELDS HERE
		 **************************/
		boolean hasCID = false, hasOccasion = false, hasGroup = false, hasAnchor3 = false;
		int cidIndex = -1, occasionIndex = -1, groupIndex = -1, anchor3Index = -1;

		for(int i = 0; i < userData.getInHeaders().length; ++i) {
			String header = userData.getInHeaders()[i];
			if(CID_PATTERN.matcher(header).find()) {
				hasCID = true;
				cidIndex = i;
			}else if(OCCASION_PATTERN.matcher(header).find()) {
				hasOccasion = true;
				occasionIndex = i;
			} else if(GROUP_PATTERN.matcher(header).find()) {
				hasGroup = true;
				groupIndex = i;
			} else if(ANCHOR3_PATTERN.matcher(header).find()) {
				hasAnchor3 = true;
				anchor3Index = i;
			}
		}

		if(!hasCID)
			throw new ApplicationException(String.format("ERROR: Could not find the field cid"));
		if(!hasOccasion)
			throw new ApplicationException(String.format("ERROR: Could not find the field Occasion Code"));
		if(!hasGroup)
			throw new ApplicationException(String.format("ERROR: Could not find the field group_name"));
		if(!hasAnchor3)
			throw new ApplicationException(String.format("ERROR: Could not find the field anchor3"));

		String[][] data = userData.getData();

		for (int i = 0; i < data.length; i++) {
			String cid = data[i][cidIndex];
			String occassion = data[i][occasionIndex];
			String group = data[i][groupIndex];
			String anchor3 = data[i][anchor3Index];
			
			// Create the barcode
			String barCode = cid.replaceAll(" ", "") + "," + occassion + "," + group + ",C";
			
			// Create he priority
			if(anchor3.isBlank())
				anchor3 = "888888";
			
			if(SEED_PATTERN.matcher(cid).find())
				anchor3 = "999999";

			Record record = new Record.Builder(i, data[i], "", "", "").setBarCode(barCode).setPriority(anchor3).build();

			userData.add(record); // add the record the recordList in userData
		}

		// set the Header fields that we have used
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.PRIORITY.getName(),
				UserData.fieldName.BARCODE.getName() 
				});

	}

}
