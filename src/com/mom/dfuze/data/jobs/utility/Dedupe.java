package com.mom.dfuze.data.jobs.utility;

import javax.swing.JOptionPane;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.dedupe.DedupeDialog2;

public class Dedupe implements RunUtilityBehavior {

	  private final String BEHAVIOR_NAME = "Dedupe";
	  protected String[] REQUIRED_FIELDS = { UserData.fieldName.ADDRESS1.getName(), UserData.fieldName.ADDRESS2.getName(), UserData.fieldName.POSTALCODE.getName()};
	  protected String DESCRIPTION = "<html>Dedupe 2.0<br/><br/>"
		  		+ "Description<br/>"
		  		+ "<ul>"
		  		+ "<li>Identify dupes by Address + PC, PC, or Name</li>"
		  		+ "<li>Advanced address normalization</li>"
		  		+ "<li>Enhanced rural address matching</li>"
		  		+ "<li>Phonetic, edit-distance and proprietary name matching</li>"
		  		+ "<li>Identify multiple names in one record</li>"
		  		+ "<li>Priority sorting in 3 different data types</li>"
		  		+ "<li>Review and modify dedupe results</li>"
		  		+ "<li>And more!</li>"
		  		+ "</ul>"
		  		+ "Instructions<br/>"
		  		+ "<ul>"
		  		+ "<li>Load corrected data and run</li>"
		  		+ "</ul>"
		  		+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";


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
	  public void run(UserData userData) {

	    String[][] data = userData.getData();

	    int counter = -1;
	    for (int i = 0; i < data.length; i++) {

	      String add1 = data[i][userData.getAdd1Index()];
	      String add2 = data[i][userData.getAdd2Index()];
	      String pCode = data[i][userData.getPCodeIndex()];

	      Record record = new Record.Builder(++counter, data[i], add1, add2, pCode)
	    		  .setIsDupe(false)
	    		  .setDupeGroupId(0)
	    		  .setDupeGroupSize(0)
	    		  .setMisc1("")
	    		  .setMisc2("")
	    		  .build();

	      userData.add(record); // add the record the recordList in userData

	    }

	    // set the Header fields that we have used
	    userData.setDfHeaders(new String[] {
	    		UserData.fieldName.IS_DUPE.getName(),
	    		UserData.fieldName.DUPE_GROUP_ID.getName(),
	    		UserData.fieldName.DUPE_GROUP_SIZE.getName(),
	    		//UserData.fieldName.MISC1.getName(),
	    		//UserData.fieldName.MISC2.getName()
	    		});
	    
	    DedupeDialog2 dedupeDialog = new DedupeDialog2(UiController.getMainFrame());
		dedupeDialog.setVisible(true);
		
		// Ask the user if they would like to remove the kill file records
		if(userData.getRecordList().size() == 0) {
			UiController.displayMessage("Warning", "There are no records remaining!", JOptionPane.WARNING_MESSAGE);
			
		} else if(dedupeDialog.getChckbxKillFilePriority().isSelected() && dedupeDialog.getKillPriority() != null) {
			int buttonPressed = JOptionPane.showConfirmDialog(UiController.getMainFrame(), "Would you like to remove the kill file records?",
	                "Remove Kills", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

	        if (buttonPressed == JOptionPane.YES_OPTION) {
	        	
	        	// loop through the records in reverse
	        	for (int j = userData.getRecordList().size() - 1; j >= 0; j--) {
	        		Record record = userData.getRecordList().get(j);
	        		String recordValue = null;
	        		
	        		// get the record priority value
	        		if (!dedupeDialog.isDfPrioritySet()) {
	        			recordValue = (record).getDfInData()[dedupeDialog.getComboBoxPriority().getSelectedIndex()];
					} else {
						recordValue = UserData.getRecordFieldByName(dedupeDialog.getComboBoxPriority().getName(), record);
					}
	        		
	        		// remove the record if it matches the kill priority
	        		if (recordValue.equals(dedupeDialog.getKillPriority()))
	        			userData.getRecordList().remove(j);
	        	}
	        	
	        	if(userData.getRecordList().size() == 0)
	    			UiController.displayMessage("Warning", "There are no records remaining!", JOptionPane.WARNING_MESSAGE);
	        }
		}
			

	  }

	}
