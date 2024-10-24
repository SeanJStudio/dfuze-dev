/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.utility;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

import com.mom.dfuze.data.util.Common;


/**
 * FixApartmentHypens implements a RunBehavior for Utility Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 08/21/2023
 *
 */
public class FixApartmentHyphens implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Fix Apartment Hyphens";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Adds the hyphen between apartment and street number</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load data and run</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
			+ "</html>";
	
	public final Pattern APT_MISSING_HYPHEN_PATTERN = Pattern.compile("(?<=^\\d+(\\D)?)\\s+(?=\\d+\\s+(?!TH(\\s+|$|-|\\.+|,)|ND|RD|STREET|ST(\\s+|$|-|\\.+|,)|ROAD|BOUL|BLVD|AV|WAY|CRES|LINE|RANGE|RN|RG|ROUTE|RT|HIGHWAY|HIGH(\\s+|$|-|\\.+|,)|HW|HIW|TOWN|TWN|RUE|TRAIL|TRL|CONC|CN))", Pattern.CASE_INSENSITIVE);
	
	//
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		fixAddress(userData);
	
		userData.setDfHeaders(new String[] {});
	}
	
	public void fixAddress(UserData userData) {
		for(int i = 0; i < userData.getRecordList().size(); ++ i) {
			Record record = userData.getRecordList().get(i);
			
			String add1 = record.getAdd1().replaceAll("\\p{Pd}", "-");
			String add2 = record.getAdd2().replaceAll("\\p{Pd}", "-");
			
			Matcher missingHyphenMatcher1 = APT_MISSING_HYPHEN_PATTERN.matcher(add1);
			Matcher missingHyphenMatcher2 = APT_MISSING_HYPHEN_PATTERN.matcher(add2);

			if(missingHyphenMatcher1.find())
				add1 = add1.replaceFirst(missingHyphenMatcher1.group(), "-");

			if(missingHyphenMatcher2.find())
				add2 = add2.replaceFirst(missingHyphenMatcher2.group(), "-");
			
			record.getDfInData()[userData.getAdd1Index()] = add1;
			record.getDfInData()[userData.getAdd2Index()] = add2;
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
