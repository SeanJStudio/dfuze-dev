/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.lunghealthfoundation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.io.FileIngestor;

import com.mom.dfuze.ui.UiController;

/**
 * Regular Process implements a RunBehavior for Lung Health Foundation Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class CPLookup implements RunLungHealthFoundationBehavior {

	private String BEHAVIOR_NAME = "CPLookup";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.DUPE_ADD1.getName(),
			UserData.fieldName.DUPE_ADD2.getName(),
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.POSTALCODE.getName()
			};
	
	private String DESCRIPTION = "<html>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load client file, then when prompted, cp db</li>"
			+ "</ol>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>lookup<ol>"
	        + "</ol></li>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";
	
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		// add records to userData, set userData record members via required fields
		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		
		List<CPData> cpList = getCPList();
		HashMap<String, List<CPData>> cpMap = convertCPToMap(cpList);
		
		findPC(userData, cpMap);

		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.POSTALCODE.getName(),
		});
	}
	
	private List<CPData> getCPList() throws Exception {
		JOptionPane.showMessageDialog(
				UiController.getMainFrame(),
				"Please load the CP DB file now.",
				"Load CP DB",
				JOptionPane.INFORMATION_MESSAGE);
		
		List<List<String>> giftFile = FileIngestor.ingest();	// get the CP file	
	
		int fromIndex = 0, toIndex = 1, lookupIndex = 2, pcIndex = 3;
		
		List<CPData> cpList = new ArrayList<>();
		
		giftFile.remove(0); // remove header row
		
		for(int i = 0; i < giftFile.size(); ++i) {
			String stringFrom = giftFile.get(i).get(fromIndex);
			String stringTo = giftFile.get(i).get(toIndex);
			String lookup = giftFile.get(i).get(lookupIndex);
			String pc = giftFile.get(i).get(pcIndex);
			
			int cpFrom = -1;
			int cpTo = -1;
			
			if(Validators.isWholeNumber(stringFrom))
				cpFrom = Integer.parseInt(stringFrom);
			
			if(Validators.isWholeNumber(stringTo))
				cpTo = Integer.parseInt(stringTo);
			
			CPData cpRecord = new CPData(cpFrom, cpTo, lookup, pc);
			
			cpList.add(cpRecord);
		}
		
		return cpList;
	}
	
	private void findPC(UserData userData, HashMap<String, List<CPData>> cpMap) {
		for(Record clientRecord : userData.getRecordList()) {
			
			if(!clientRecord.getPCode().isBlank())
				continue;
				
			int clientFrom = -1;
			int clientTo = -1;
			
			if(Validators.isWholeNumber(clientRecord.getDupeAdd1()))
				clientFrom = Integer.parseInt(clientRecord.getDupeAdd1());
			
			if(Validators.isWholeNumber(clientRecord.getDupeAdd2()))
				clientTo = Integer.parseInt(clientRecord.getDupeAdd2());
			
			if(cpMap.containsKey(clientRecord.getAdd1())) {
				for(CPData cpRecord : cpMap.get(clientRecord.getAdd1())) {
					if(clientFrom >= cpRecord.getFrom() && clientTo <= cpRecord.getTo()) {
						clientRecord.setPCode(cpRecord.getPc());
						break;
					}
					
				}
			}
			
		}
	}
	
	private HashMap<String, List<CPData>> convertCPToMap(List<CPData> cpList) throws Exception {
		HashMap<String, List<CPData>> cpMap = new HashMap<>();
		for(CPData cpRecord : cpList) {
		
			if(cpMap.containsKey(cpRecord.getLookup())) {
				cpMap.get(cpRecord.getLookup()).add(cpRecord);
			} else {
				ArrayList<CPData> fromToList = new ArrayList<>();
				fromToList.add(cpRecord);
				cpMap.put(cpRecord.getLookup(), fromToList);
			}
				
		}
		
		return cpMap;
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
