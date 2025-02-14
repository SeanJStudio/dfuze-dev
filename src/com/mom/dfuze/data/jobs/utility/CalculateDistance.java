/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.utility;

import java.util.ArrayList;
import java.util.HashSet;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.OptionSelectDialog;
import com.mom.dfuze.ui.UiController;

/**
 * RegularProcess implements a RunBehavior for GFFinancial Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class CalculateDistance implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Calculate Distance";
	
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName(),
			UserData.fieldName.LATITUDE.getName(), // lat
			UserData.fieldName.LONGITUDE.getName()	 // long
	};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Calculates distance and determines how many addresses are within 10km</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<li>Load multiples data files together and run</li>"
			+ "<li>When prompted, select the main file</li>"
			+ "</ul>"
			+ "</html>";


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
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {


		String[][] data = userData.getData();
		
		ArrayList<Record> mainList = new ArrayList<>();
		ArrayList<Record> secondaryList = new ArrayList<>();
		
		int fileNameIndex = -1;
		
		for(int i = 0; i < userData.getInHeaders().length; ++i) {
			if(userData.getInHeaders()[i].equalsIgnoreCase("dfFileName")) {
				fileNameIndex = i;
				break;
			}
		}
		
		if(fileNameIndex == -1)
			throw new Exception("dfFileName could not be found.");
		
		HashSet<String> fileNameSet = new HashSet<>();
		
		for (int i = 0; i < data.length; i++) {
			String fileName = data[i][fileNameIndex];
			fileNameSet.add(fileName);
		}
		
		String options[] = new String[fileNameSet.size()];
		fileNameSet.toArray(options);
		
		OptionSelectDialog osd = new OptionSelectDialog(UiController.getMainFrame(), "Select the main file", options);
		osd.setVisible(true);
		
		String fileToUse = "";
		
		if(osd.isNextPressed())
			fileToUse = osd.getSelectedOption();
		else
			throw new Exception("No option selected.");	

		for (int i = 0; i < data.length; i++) {

			String latitude = data[i][userData.getLatitudeIndex()];
			String longitude = data[i][userData.getLongitudeIndex()];
			String fileName = data[i][fileNameIndex];
			
			String add1 = data[i][userData.getAdd1Index()];
			String add2 = data[i][userData.getAdd2Index()];
			String city = data[i][userData.getCityIndex()];
			String prov = data[i][userData.getProvIndex()];
			String pc = data[i][userData.getPCodeIndex()];
			
			String weight = "";

			Record record = new Record.Builder(i, data[i], "", "", "")
					.setAdd1(add1)
					.setAdd2(add2)
					.setCity(city)
					.setProv(prov)
					.setPCode(pc)
					.setLatitude(latitude)
					.setLongitude(longitude)
					.setWeight(weight)
					.build();
			
			if(fileName.equalsIgnoreCase(fileToUse))
				mainList.add(record);
			else
				secondaryList.add(record);
		}
		
		// set the Header fields that we have used
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.WEIGHT.getName()
				});
		
		int newIndex = -1;
		
		final String KM = "K";
		
		for(int i = 0; i < mainList.size(); ++i) {
			Record mainRec = mainList.get(i);
			for(int j = 0; j < secondaryList.size(); ++j) {
				Record secRec = secondaryList.get(j);
				
				double mainRecLat = Double.valueOf(mainRec.getLatitude());
				double mainRecLong = Double.valueOf(mainRec.getLongitude());
				double secRecLat = Double.valueOf(secRec.getLatitude());
				double secRecLong = Double.valueOf(secRec.getLongitude());
				
				double distance = Common.distance(mainRecLat, mainRecLong, secRecLat, secRecLong, KM);
				
				String mainKey = mainRec.getAdd1() + mainRec.getAdd2() + mainRec.getPCode();
				String secKey = secRec.getAdd1() + secRec.getAdd2() + secRec.getPCode();
				
				if(!mainKey.equals(secKey)) {
					if(distance <= 10.00) {
						if(mainRec.getWeight().equals(""))
							mainRec.setWeight((secRec.getAdd1() + " ").trim() + secRec.getAdd2());
						else
							mainRec.setWeight(mainRec.getWeight() + ", " + (secRec.getAdd1() + " ").trim() + secRec.getAdd2());
					}
				}
			}
			
			mainRec.setDfId(++newIndex);
			userData.add(mainRec);
		}
		
	}

}
