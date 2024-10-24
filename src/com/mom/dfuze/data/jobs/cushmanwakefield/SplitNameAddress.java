/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.cushmanwakefield;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;


/**
 * RegularProcess implements a RunBehavior for Cushman and Wakefield Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class SplitNameAddress implements RunCushmanWakefieldBehavior {

	private String BEHAVIOR_NAME = "Split Name Address";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.ADDRESS1.getName()
			};
	
	private String DESCRIPTION = "<html>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Splits returns in names and address</li>"
	        + "</ol>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load supplied data file and run job</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		// The users' input data
		String[][] data = userData.getData();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {

			// Get all the fields we can for now	
			String name = data[i][userData.getNam1Index()];
			String add = data[i][userData.getAdd1Index()];

			//Split address lines
			String[] addParts = add.split("\\r?\\n");
			
			int addIndex = 0;
			String add1 = "", add2 = "", add3 = "", add4 = "";
			
			for(int j = 0; j < addParts.length; ++j) {
				String part = addParts[j].trim();
				if(part.length() > 0) {
					switch(++addIndex) {
					case 1:
						add1 = part;
						break;
					case 2:
						add2 = part;
						break;
					case 3:
						add3 = part;
						break;
					case 4:
						add4 = part;
						break;
					default:
						add4 += "\n" + part;
					}
				}
				
			}
			
			String newName = "";
			
			//Split name lines
			String[] nameParts = name.split("\\r?\\n");
			
			for(int j = 0; j < nameParts.length; ++j) {
				if(nameParts[j].trim().length() > 0) {
					if(newName.length() > 0)
						newName += " and " + nameParts[j];
					else
						newName = nameParts[j];
				}
			}
			
			newName = newName.replaceAll("\\s+", " ").trim();

			// Build the Record
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setNam1(newName)
					.setAdd1(add1)
					.setAdd1_2(add2)
					.setAdd2(add3)
					.setAdd2_2(add4)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] {
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.ADDRESS1_2.getName(),
				UserData.fieldName.ADDRESS2.getName(),
				UserData.fieldName.ADDRESS2_2.getName(),
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
