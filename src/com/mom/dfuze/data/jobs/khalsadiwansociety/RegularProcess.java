package com.mom.dfuze.data.jobs.khalsadiwansociety;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.ExportDataDialog;
import com.mom.dfuze.ui.InkjetDialog;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.dedupe.DedupeDialog;

/*
 * 10/14/2022
 * 
 * */

public class RegularProcess implements RunKhalsaDiwanSocietyBehavior {

	private final String BEHAVIOR_NAME = "Envelope/Card Maker";
	protected String[] REQUIRED_FIELDS = {
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName()
			};
	
	protected String DESCRIPTION = "<html>"
			+ "Description"
			+ "<ul>"
			+ "<li>Groups records by last name and address</li>"
			+ "<li>Sorts the data into packages by amount of people in family</li>"
			+ "<li>Auto exports the envelope package inkjet files</li>"
			+ "<li>Auto exports the card package files</li>"
			+ "</ul>"
			+ "Instructions"
			+ "<ol>"
			+ "<li>Correct the addresses</li>"
			+ "<li>Load the corrected data and run the job</li>"
			+ "<li>No need to manually export after job completes</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";
	
	private boolean isComplete = false;
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
	public void run(UserData userData) throws ApplicationException {


		String[][] data = userData.getData();
		
		for (int i = 0; i < data.length; i++) {
			
			// Get all the fields we can for now
			String fnam = data[i][userData.getFstNameIndex()];
			String lnam = data[i][userData.getLstNameIndex()].toLowerCase().trim();
			String add1 = data[i][userData.getAdd1Index()];
			String add2 = data[i][userData.getAdd2Index()];
			String city = data[i][userData.getCityIndex()];
			String prov = data[i][userData.getProvIndex()];
			String pcode = data[i][userData.getPCodeIndex()];
			
			String nam1 = Common.caseName(fnam + " " + lnam).replaceAll("\\s+", " ").trim();
			String nam2 = Common.caseName(fnam + " " + lnam).replaceAll("\\s+", " ").trim();

			Record record = new Record.Builder(i, data[i], "", "", "")
					.setFstName(fnam)
					.setLstName(lnam)
					.setNam1(nam1)
					.setNam2(nam2)
					.setAdd1(add1)
					.setAdd2(add2)
					.setCity(city)
					.setProv(prov)
					.setPCode(pcode)
					.setSegCode("")
					.setPkgVer("1") // give everyone a default of 1 package
					.setListNum("")
					.setIsDupe(false)
					.build();
			
			userData.add(record); // add the record the recordList in userData

		}
		
		// set the Header fields that we have used
		userData.setDfHeaders(new String[] {
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.NAME2.getName(),
				UserData.fieldName.SEGMENT_CODE.getName(),
				UserData.fieldName.PACKAGE_VERSION.getName(),
				UserData.fieldName.LENGTH.getName(),
				UserData.fieldName.LIST_NUMBER.getName(),
				UserData.fieldName.IS_DUPE.getName()
		});
		
		
		// Must create a new thread in order to use the dupe window
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                	
                	/***********************
            		 * DUPE
            		 * *********************/
            		// Make the dedupe window but dont display it
            		DedupeDialog dedupeDialog = new DedupeDialog(UiController.getMainFrame());
            		
            		System.out.println(dedupeDialog.getBtnRun().isEnabled());
            		
            		// Exact Names only
            		dedupeDialog.getChckbxExactNamesOnly().setSelected(true);
            		
            		// Set the field to the last name
            		dedupeDialog.getComboBoxName1().setSelectedIndex(Arrays.asList(userData.getExportHeaders()).indexOf(UserData.fieldName.LASTNAME.getName()));
            		
            		// Click the run Button
            		dedupeDialog.getBtnRun().doClick();
            		System.out.println(dedupeDialog.getBtnRun().isEnabled());
            		
            		// Loop until the dedupe completes by checking if the button is enabled
            		while(!dedupeDialog.getBtnRun().isEnabled()) {
            			try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            			if(dedupeDialog.getBtnRun().isEnabled())
            				break;
            		}
            		
            		System.out.println("dupe complete");
            		
          		
            		/***********************
            		 * DUPE GROUP CODES
            		 * *********************/
            		
            		// Dedupe should be complete
            		ArrayList<Record> dupeRecords = dedupeDialog.getAllDupeRecordsList();
            		
            		// Set the segCode (dupeGroup) and pkgVer (Number of people/dupes)
            		int segCode = -1;
            		
            		System.out.println("creating segcodes");
            		for(int j = 0; j < dupeRecords.size(); ++j) {
            			
            			if(!dupeRecords.get(j).getIsDupe()) 
            				++segCode;

            			dupeRecords.get(j).setSegCode(Common.toAlphabetic(segCode));
            		}
         		
            		
            		/***********************
            		 * MULTI PACKAGE VER CODES
            		 * *********************/
            		System.out.println("creating pkg vers");
            		
            		// segCode ver, the list
            		Map<String, List<Record>> dupeRecordsByPackageNum = dupeRecords.stream().collect(Collectors.groupingBy(Record::getSegCode));
            		for(Map.Entry<String, List<Record>> entry : dupeRecordsByPackageNum.entrySet()) {
            			for(Record record : entry.getValue()) {
            				record.setPkgVer(String.valueOf(entry.getValue().size())); // set the pkg ver to the size of the dupes
            			}
            		}
            		
            		/***********************
            		 * CREATE THE ENV NAME
            		 * *********************/
            		for(Map.Entry<String, List<Record>> entry : dupeRecordsByPackageNum.entrySet())
            			for(Record record : entry.getValue()) 
            				record.setNam1(Common.caseName(entry.getValue().get(0).getLstName() + " Family"));
 
            		
            		System.out.println("updating values");
            			for(Map.Entry<String, List<Record>> entry : dupeRecordsByPackageNum.entrySet()) {
            				for(Record nextRecord : entry.getValue()) {
            					for(Record record : userData.getRecordList()) {
            					if(record.getDfId() == nextRecord.getDfId()) {
            						record.setNam1(nextRecord.getNam1());
            						record.setSegCode(nextRecord.getSegCode());
            						record.setPkgVer(nextRecord.getPkgVer());
            						break;
            					}
            				}
            			}
            		}
            			
            			
            			// SORT ON PACKAGE
            			Collections.sort(userData.getRecordList(), (Comparator<? super Record>)new RecordSorters.CompareByFieldAscAsNumber(UserData.fieldName.PACKAGE_VERSION.getName()));
            			
            			// Add the LISTORDER TO LENGTH AS AN ID
            			for(int j = 0; j < userData.getRecordList().size(); ++j) {
            				userData.getRecordList().get(j).setLength(String.valueOf(j+1));
            			}
            			

            			/****************************
                		 * CREATE THE ENVELOPE INKJET
                		 * **************************/
            			HashSet<String> tempDupeGroup = new HashSet<>();
            			ArrayList<Record> removedRecords = new ArrayList<>();
            			
            			// remove extra multis from in memory data
            			for(int j = userData.getRecordList().size()-1 ; j >= 0; --j) {
            				Record tempRec = userData.getRecordList().get(j);
            				if(tempRec.getSegCode().length() > 0) {
            					if(!tempDupeGroup.add(tempRec.getSegCode()))
            						removedRecords.add(userData.getRecordList().remove(j));
            				}
            			}
            			
            			// Set the LISTORDER TO LISTNUM
            			String prevPkg = userData.getRecordList().get(0).getPkgVer();
            			int tempListOrder = 0;
            			for(int j = 0; j < userData.getRecordList().size(); ++j) {
            				if(!userData.getRecordList().get(j).getPkgVer().equalsIgnoreCase(prevPkg)) {
            					tempListOrder = 0;
            				}
            				prevPkg = userData.getRecordList().get(j).getPkgVer();
            				userData.getRecordList().get(j).setListNum(String.valueOf(++tempListOrder));
            			}
            			
            			JOptionPane.showMessageDialog(UiController.getMainFrame(), "Enter the default Inkjet filename when prompted.", "Inkjet Maker", JOptionPane.INFORMATION_MESSAGE);
            			
            			InkjetDialog inkjetDialog = new InkjetDialog(UiController.getMainFrame());
            			
            			inkjetDialog.getComboBoxLine1().setSelectedIndex(Arrays.asList(userData.getExportHeaders()).indexOf(UserData.fieldName.SEGMENT_CODE.getName()));
            			inkjetDialog.getComboBoxLine2().setSelectedIndex(Arrays.asList(userData.getExportHeaders()).indexOf(UserData.fieldName.NAME1.getName()));
            			inkjetDialog.getComboBoxAddress1().setSelectedIndex(userData.getAdd1Index());
            			inkjetDialog.getComboBoxAddress2().setSelectedIndex(userData.getAdd2Index());
            			inkjetDialog.getComboBoxCity().setSelectedIndex(userData.getCityIndex());
            			inkjetDialog.getComboBoxProv().setSelectedIndex(userData.getProvIndex());
            			inkjetDialog.getComboBoxPC().setSelectedIndex(userData.getPCodeIndex());
            			inkjetDialog.getComboBoxListOrder().setSelectedIndex(Arrays.asList(userData.getExportHeaders()).indexOf(UserData.fieldName.LIST_NUMBER.getName()));
            			inkjetDialog.getComboBoxMakeMultipleFiles().setSelectedIndex(Arrays.asList(userData.getExportHeaders()).indexOf(UserData.fieldName.PACKAGE_VERSION.getName()));
            			inkjetDialog.getBtnMake().doClick();
            			
            			while(!inkjetDialog.getBtnMake().isEnabled()) {
                			try {
    							Thread.sleep(1000);
    						} catch (InterruptedException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
                			if(inkjetDialog.getBtnMake().isEnabled())
                				break;
                		}
            			
            			/****************************
                		 * CREATE/EXPORT TICKET FILES
                		 * **************************/
            			
            			// add the removed records back in
            			for(Record record : removedRecords)
            				userData.getRecordList().add(record);

            			// SORT ON LENGTH
            			Collections.sort(userData.getRecordList(), (Comparator<? super Record>)new RecordSorters.CompareByFieldAscAsNumber(UserData.fieldName.LENGTH.getName()));
            			
            			// Set the ListNumber for tickets
            			prevPkg = userData.getRecordList().get(0).getPkgVer();
            			tempListOrder = 0;
            			for(int j = 0; j < userData.getRecordList().size(); ++j) {
            				if(!userData.getRecordList().get(j).getPkgVer().equalsIgnoreCase(prevPkg)) {
            					tempListOrder = 0;
            				}
            				prevPkg = userData.getRecordList().get(j).getPkgVer();
            				userData.getRecordList().get(j).setListNum(String.valueOf(++tempListOrder));
            				userData.getRecordList().get(j).setLength("");
            			}
            			
            			//Remove Length and isDupe
            			userData.setDfHeaders(new String[] {
            					UserData.fieldName.NAME1.getName(),
            					UserData.fieldName.NAME2.getName(),
            					UserData.fieldName.SEGMENT_CODE.getName(),
            					UserData.fieldName.PACKAGE_VERSION.getName(),
            					UserData.fieldName.LIST_NUMBER.getName(),
            			});
            			
            			JOptionPane.showMessageDialog(UiController.getMainFrame(), "Enter the default card filename when prompted.", "Card Maker", JOptionPane.INFORMATION_MESSAGE);
            			
            			ExportDataDialog edd = null;
            			
            			try {
							edd = new ExportDataDialog(UiController.getMainFrame(), userData.getExportHeaders(), userData.getExportData(userData.getRecordList()));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            			
            			edd.getChckbxSplitData().setSelected(true);
            			
            			edd.getComboBoxSplitData().setSelectedIndex(Arrays.asList(userData.getExportHeaders()).indexOf(UserData.fieldName.PACKAGE_VERSION.getName()));
            			
            			edd.getBtnExportData().doClick();
            			
            			while(!edd.getBtnExportData().isEnabled()) {
                			try {
    							Thread.sleep(1000);
    						} catch (InterruptedException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
                			if(edd.getBtnExportData().isEnabled())
                				break;
                		}
            			
            			isComplete = true;
                }
            });

		

            while(!isComplete) {
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    			if(isComplete)
    				break;
    		}
		
		isComplete = false;
		

	}

}


