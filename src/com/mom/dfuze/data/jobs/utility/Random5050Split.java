package com.mom.dfuze.data.jobs.utility;

import java.util.Collections;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserMultiRadioDialog;

public class Random5050Split implements RunUtilityBehavior {

	  private final String BEHAVIOR_NAME = "Random 50/50 Split";
	  
	  protected String[] REQUIRED_FIELDS = {
			  UserData.fieldName.SEGMENT.getName()
	  };
	  
	  protected String DESCRIPTION = "<html>"
	  		+ "Description<br/>"
	  		+ "<ul>"
	  		+ "<li>Creates dfABGroup field</li>"
	  		+ "<li>Randomly and evenly distributes each unique value from mapped field into dfABGroup with value of A and B or C and T</li>"
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
	  public void run(UserData userData) throws ApplicationException {

	    String[][] data = userData.getData();

	    int counter = -1;
	    for (int i = 0; i < data.length; i++) {

	      String seg = data[i][userData.getSegIndex()];

	      Record record = new Record.Builder(++counter, data[i], "", "", "").setSeg(seg).build();

	      userData.add(record); // add the record the recordList in userData
	    }

	    // Randomize our list
	    Collections.shuffle(userData.getRecordList());
	    
	    // Now lets sort it on the segment
	    Collections.sort(userData.getRecordList(), new RecordSorters.CompareByFieldAsc(UserData.fieldName.SEGMENT.getName()));
	    
	    // Create a dialog window with 2 selections
	    UserMultiRadioDialog radioDialog = new UserMultiRadioDialog(UiController.getMainFrame(), 2, "Choose how to identify the split groups");
	    
	    // Set their values
	    radioDialog.setRadioButton1("A and B");
	    radioDialog.setRadioButton2("C and T");
	    
	    radioDialog.setVisible(true);
	    
	    // make sure a selection was made
	    if(!radioDialog.getIsNextPressed())
	    	throw new ApplicationException("No Selection made.");
	    
	    // Capture the selection made
	    String group1, group2 = "";
	    if(radioDialog.getRadio1().isSelected()) {
	    	group1 = "A";
	    	group2 = "B";
	    } else {
	    	group1 = "C";
	    	group2 = "T";
	    }
	    
	    // Assign group identifiers to each record
	    int i = 0;
	    for(Record record: userData.getRecordList()) {
	    	if(i % 2 == 0)
	    		record.setABGroup(group1);
	    	else
	    		record.setABGroup(group2);
	    	
	    	++i;
	    }
	    
	    // Sort our list back to the original order
	    Collections.sort(userData.getRecordList(), new RecordSorters.CompareByDfId());

	    
	    // Set the Header fields that we want to output
	    userData.setDfHeaders(new String[] {UserData.fieldName.AB_GROUP.getName()});

	  }

	}