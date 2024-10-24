/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.io.FileExporter;

import com.mom.dfuze.ui.UiController;


/**
 * RemoveNonLowerMainland implements a RunBehavior for Connect Hearing Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/18/2023
 *
 */
public class RemoveNonLowerMainland implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Remove Non Lower Mainland";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.POSTALCODE.getName()
			};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Removes records not in lower mainland based on PC</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Load data and run</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
			+ "</html>";
	
	public final Pattern LOWER_MAINLAND_PATTERN = Pattern.compile(
			"^("
			+ "V1M"
			+ "|V2P|V2R|V2S|V2T|V2W|V2X|V2Y|V2Z"
			+ "|V3A|V3B|V3C|V3E|V3G|V3H|V3J|V3K|V3L|V3M|V3N|V3P|V3R|V3S|V3T|V3V|V3W|V3X|V3Y|V3Z"
			+ "|V4A|V4B|V4C|V4E|V4G|V4H|V4J|V4K|V4L|V4M|V4N|V4P|V4R|V4W|V4X|V4Y|V4Z"
			+ "|V5A|V5B|V5C|V5E|V5G|V5H|V5J|V5K|V5L|V5M|V5N|V5P|V5R|V5S|V5T|V5V|V5W|V5X|V5Y|V5Z"
			+ "|V6A|V6B|V6C|V6E|V6G|V6H|V6J|V6K|V6L|V6M|V6N|V6P|V6R|V6S|V6T|V6V|V6W|V6X|V6Y|V6Z"
			+ "|V7A|V7B|V7C|V7E|V7G|V7H|V7J|V7K|V7L|V7M|V7N|V7P|V7R|V7S|V7T|V7V|V7W|V7X|V7Y"
			+ ")$", Pattern.CASE_INSENSITIVE);


	public ArrayList<Record> recordsRemoved;
	
	//
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		recordsRemoved = new ArrayList<>();
		
		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);

		// iterate backwards to safely remove bad fsa
		for(int i = userData.getRecordList().size() - 1; i >= 0; --i) {
			Record record = userData.getRecordList().get(i);

			String pc = record.getPCode().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
			String fsa = (pc.length() >= 3) ? pc.substring(0, 3) : "";
			
			Matcher fsaMatcher = LOWER_MAINLAND_PATTERN.matcher(fsa);
			
			if(!fsaMatcher.find()) {
				Record removedRecord = userData.getRecordList().remove(i);
				recordsRemoved.add(removedRecord);
				continue;
			}
		}
		
		userData.setDfHeaders(new String[] {});
		exportRemovals(userData);

	}
	
	private void exportRemovals(UserData userData) throws Exception {
	
		if(recordsRemoved.size() > 0) {
			File file = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_OUTSIDE_LOWER_MAINLAND" + FileExtensions.XLSX);
			FileExporter.exportData(userData.getExportHeaders(), userData.getExportData(recordsRemoved), file);
			JOptionPane.showMessageDialog(UiController.getMainFrame(), String.format("%d non lower mainland records removed.", recordsRemoved.size()), "Results", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(UiController.getMainFrame(), String.format("No non lower mainland records found", recordsRemoved.size()), "Results", JOptionPane.INFORMATION_MESSAGE);
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
