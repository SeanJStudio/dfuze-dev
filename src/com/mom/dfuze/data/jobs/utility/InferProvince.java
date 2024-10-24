/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.utility;


import com.mom.dfuze.data.UserData;

import com.mom.dfuze.data.util.Common;


/**
 * InferProvince implements a RunBehavior for Utility Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 08/21/2023
 *
 */
public class InferProvince implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Infer Province/State";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.POSTALCODE.getName()
			};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Infers provinces/state from PC/Zipcode</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load data and run</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
			+ "</html>";
		
	//
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		inferProvince(userData);
	
		userData.setDfHeaders(new String[] {UserData.fieldName.PROVINCE.getName()});
	}
	
	public void inferProvince(UserData userData) {
		for(int i = 0; i < userData.getRecordList().size(); ++ i) {
			String province = Common.pc2prov(userData.getRecordList().get(i).getPCode());
			userData.getRecordList().get(i).setProv(province);
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
		return false;
	}

}
