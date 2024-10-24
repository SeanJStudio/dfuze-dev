/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.gffinancial;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.io.FileExporter;
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
public class VRM implements RunGFFinancialBehavior {

	private final String BEHAVIOR_NAME = "VRM";
	
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.IN_ID.getName(), // holds the loan number
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName()
	};
	
	private String DESCRIPTION = 
			"<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>tldr; LEFT JOIN MTG m ON m.loan_number = v.loan_number</li>"
			+ "<li>Matches on loan number from mortgage members to VRM</li>"
			+ "<li>Copies VRM info over to mortgage member records</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<li>Ensure data has columns 'loan number' and 'member name' in both files</li>"
			+ "<li>Load the mortgage member and VRM file together and run</li>"
			+ "<li>Map loan number to ID</li>"
			+ "<li>When prompted, select the VRM file</li></ul>"
			+ "</html>";
	
	private ArrayList<Record> unmatchedList = new ArrayList<>();


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
		
		ArrayList<Record> vrmList = new ArrayList<>(); // VRM file holding financial data
		ArrayList<Record> memberList = new ArrayList<>(); // Member file holding member data
		
		unmatchedList = new ArrayList<>(); // Reset list on run
		
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
		
		OptionSelectDialog osd = new OptionSelectDialog(UiController.getMainFrame(), "Select the VRM file", options);
		osd.setVisible(true);
		
		String vrmFileName = "";
		
		if(osd.isNextPressed())
			vrmFileName = osd.getSelectedOption();
		else
			throw new Exception("No option selected.");	

		for (int i = 0; i < data.length; i++) {

			String fname = Common.caseName(data[i][userData.getFstNameIndex()].replaceAll("  ", " ").trim());
			String lname = Common.caseName(data[i][userData.getLstNameIndex()].replaceAll("  ", " ").trim());
			String add1 = data[i][userData.getAdd1Index()];
			String city = data[i][userData.getCityIndex()];
			String prov = data[i][userData.getProvIndex()];
			String pc = data[i][userData.getPCodeIndex()];
			String loan = data[i][userData.getInIdIndex()];
			String fileName = data[i][fileNameIndex];

			String fullName = Common.caseName((fname + " " + lname).replaceAll("  ", " ").trim());

			Record record = new Record.Builder(i, data[i], "", "", "")
					.setNam1(fullName)
					.setFstName(fname)
					.setLstName(lname)
					.setAdd1(add1)
					.setCity(city)
					.setProv(prov)
					.setPCode(pc)
					.setInId(loan)
					.build();
			
			if(fileName.equalsIgnoreCase(vrmFileName))
				vrmList.add(record);
			else
				memberList.add(record);
		}
		
		// set the Header fields that we have used
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.FIRSTNAME.getName(),
				UserData.fieldName.LASTNAME.getName(),
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.CITY.getName(),
				UserData.fieldName.PROVINCE.getName(),
				UserData.fieldName.POSTALCODE.getName(),
				});
		
		matchMembers(memberList, vrmList, userData);
		exportUnmatched(userData);
		
		/*int newIndex = -1;
		
		for(int i = 0; i < vrmList.size(); ++i) {
			boolean isMatched = false;
			Record vrmRecord = vrmList.get(i);
			for(int j = 0; j < memberList.size(); ++j) {
				Record memberRecord = memberList.get(j);
				if(vrmRecord.getInId().equalsIgnoreCase(memberRecord.getInId())) {
					memberRecord.setDfInData(vrmRecord.getDfInData());
					
					for(int k = 0; k < vrmRecord.getDfInData().length; ++k) {
						vrmRecord.getDfInData()[k] = vrmRecord.getDfInData()[k].replaceAll("\\[\\$\\$\\]", "\\$ ").trim();
					}
					
					memberRecord.setDfId(++newIndex);
					userData.add(memberRecord);
					isMatched = true;
					break;
				}
			}
			//if(!isMatched)
			//unmatchedList.add(null)
		}*/
		

		
	}
	
	// match members to the VRM file
	private void matchMembers(List<Record> memberList, List<Record> vrmList, UserData userData) {
		int newIndex = -1;
		
		for(int i = 0; i < memberList.size(); ++i) {
			boolean isMatched = false;
			Record memberRecord = memberList.get(i);
			
			for(int j = 0; j < vrmList.size(); ++j) {
				Record vrmRecord = vrmList.get(j);
				
				if(!memberRecord.getInId().equalsIgnoreCase(vrmRecord.getInId()))
						continue;
				
				for(int k = 0; k < vrmRecord.getDfInData().length; ++k)
					vrmRecord.getDfInData()[k] = vrmRecord.getDfInData()[k].replaceAll("\\[\\$\\$\\]", "\\$ ").trim();

				memberRecord.setDfInData(vrmRecord.getDfInData());
				memberRecord.setDfId(++newIndex);
				userData.add(memberRecord);
				
				isMatched = true;
				break;
			}
			
			if(!isMatched)
				unmatchedList.add(memberRecord);
		}
	}
	
	// Export members who could not be matched
	private void exportUnmatched(UserData userData) throws Exception {
		if(unmatchedList.size() == 0) return;

		File file = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_UNMATCHED" + FileExtensions.XLSX);
		FileExporter.exportData(userData.getExportHeaders(), userData.getExportData(unmatchedList), file);
	}

}
