/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.utility;


import java.util.regex.Pattern;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;



/**
 * NonWeeklyStep1 implements a RunBehavior for Connect Hearing Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/18/2023
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
	
	private static Pattern GD_PATTERN = Pattern.compile("general delivery|gd|(^|\\s+)gen(\\s+|$)", Pattern.CASE_INSENSITIVE);
	
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
	
	public void separateLines(UserData userData) {
		for(Record record : userData.getRecordList()) {
			record.setCmpnyAdd1("");
			record.setCmpnyAdd2("");
			record.setCmpnyAdd3("");
			
			String add1 = record.getAdd1();
			String add2 = record.getAdd2();
			String add3 = record.getAdd3();
			
			if(!GD_PATTERN.matcher(add1).find()) {
				if(add1.trim().isBlank() || add1.replaceAll("[^0-9]", "").isBlank()) {
					record.setCmpnyAdd1(add1);
					record.setAdd1("");
				}
			}
			
			if(!GD_PATTERN.matcher(add2).find()) {
				if(add2.trim().isBlank() || add2.replaceAll("[^0-9]", "").isBlank()) {
					if(record.getCmpnyAdd1().isBlank())
						record.setCmpnyAdd1(add2);
					else
						record.setCmpnyAdd2(add2);
					
					record.setAdd2("");
				}
			}
			
			if(!GD_PATTERN.matcher(add3).find()) {
				if(add3.trim().isBlank() || add3.replaceAll("[^0-9]", "").isBlank()) {
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
