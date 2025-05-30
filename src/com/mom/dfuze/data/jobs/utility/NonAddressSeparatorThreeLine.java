/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.utility;


import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;



/**
 * NonAddressSeperatorThreeLine implements a RunBehavior for 0 - Utility
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 05/30/2025
 *
 */
public class NonAddressSeparatorThreeLine implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "3-Line Non-Address Separator";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.ADDRESS3.getName(),
			};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Moves Non-Address information into its own fields</li>"
			+ "<li>Useful to retain extra info before using iAddress</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load data and run</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
			+ "</html>";
	
	private static Pattern MISC_ADDRESS_PATTERN = Pattern.compile("(^|\\s)(general delivery|gd|gen|po|box|floor|basement|bsmnt|garage|side|door|building|bldg|station|rear|entrance|rue|chemain|succ|tour|ph|trlr|trailer|lot|apartment|condo|appartement|appt|suite|suit|ste|spc|space|room|rm|office|ofc|unit|bureau|piece|group|grp|coup|gr|gp|site|apt|pmb|box|rmb|hwy|highway|county)(\\s|$)|^\\p{L}$", Pattern.CASE_INSENSITIVE);
	
	//
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		
		separateLines(userData);
		organizeAddressLines(userData);
			
		userData.setDfHeaders(new String[] {
				UserData.fieldName.COMPANY_ADDRESS1.getName(),
				UserData.fieldName.COMPANY_ADDRESS2.getName(),
				UserData.fieldName.COMPANY_ADDRESS3.getName(),
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.ADDRESS2.getName(),
				UserData.fieldName.ADDRESS3.getName(),
		});
	}
	
	public boolean hasStreetKeyword(String address, Hashtable<String, String> streetSuffixes, HashSet<String> streetNames) {

		String[] parts = StringUtils.stripAccents(address).toLowerCase().replaceAll("\\n", " ").replaceAll("\\r", " ").replaceAll("[^a-zA-Z\\s]", "").split("\\s+");

		for(String part : parts) {
			if(streetSuffixes.keySet().contains(part))
				part = streetSuffixes.get(part);
			if(streetNames.contains(part))
				return true;
		}
		
		return false;
	}
	
	public void separateLines(UserData userData) throws Exception{
		Hashtable<String, String> streetSuffixes = Lookup.getStreetSuffixes();
		HashSet<String> streetNames = Lookup.getStreetNames();
		
		for(Record record : userData.getRecordList()) {
			record.setCmpnyAdd1("");
			record.setCmpnyAdd2("");
			record.setCmpnyAdd3("");
			
			String add1 = record.getAdd1();
			String add2 = record.getAdd2();
			String add3 = record.getAdd3();
			
			if(!MISC_ADDRESS_PATTERN.matcher(add1.replaceAll("\\.","")).find()) {
				if(add1.trim().isBlank() || add1.replaceAll("[^0-9]", "").isBlank()) {
					if(!hasStreetKeyword(add1, streetSuffixes, streetNames)) {
						record.setCmpnyAdd1(add1);
						record.setAdd1("");
					}
				}
			}
			
			if(!MISC_ADDRESS_PATTERN.matcher(add2.replaceAll("\\.","")).find()) {
				if(add2.trim().isBlank() || add2.replaceAll("[^0-9]", "").isBlank()) {
					if(!hasStreetKeyword(add2, streetSuffixes, streetNames)) {
						if(record.getCmpnyAdd1().isBlank())
							record.setCmpnyAdd1(add2);
						else
							record.setCmpnyAdd2(add2);
						
						record.setAdd2("");
					}
				}
			}
			
			if(!MISC_ADDRESS_PATTERN.matcher(add3.replaceAll("\\.","")).find()) {
				if(add3.trim().isBlank() || add3.replaceAll("[^0-9]", "").isBlank()) {
					if(!hasStreetKeyword(add3, streetSuffixes, streetNames)) {
						if(record.getCmpnyAdd1().isBlank())
							record.setCmpnyAdd1(add3);
						else if(record.getCmpnyAdd2().isBlank())
							record.setCmpnyAdd2(add3);
						else
							record.setCmpnyAdd3(add3);
						
						record.setAdd3("");
					}
				}
			}
			
		}
	}
	
	public void organizeAddressLines(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String add1 = record.getAdd1();
			String add2 = record.getAdd2();
			String add3 = record.getAdd3();
			
			
			if(!add2.isBlank()) {
				if(add1.isBlank()) {
					record.setAdd1(add2);
					record.setAdd2("");
				}
			}
			
			if(!add3.isBlank()) {
				if(add2.isBlank() && !add1.isBlank()) {
					record.setAdd2(add3);
					record.setAdd3("");
				} else if (add1.isBlank()) {
					record.setAdd1(add3);
					record.setAdd3("");
				}
			}

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
