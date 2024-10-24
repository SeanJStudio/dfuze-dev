/**
 * Project: Dfuze
 * File: Load.java
 * Date: Oct 15, 2020
 * Time: 5:52:18 PM
 */
package com.mom.dfuze.data.jobs.utility;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserIntegerInputDialog;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class SplitName implements RunUtilityBehavior {

  private final String BEHAVIOR_NAME = "SplitName";
  protected String[] REQUIRED_FIELDS = {UserData.fieldName.NAME1.getName()};
  protected String DESCRIPTION = "<html>Description"
  		+ "<br/>"
  		+ "<ul>"
  		+ "<li>Splits name into 2 fields based on length</li>"
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
		return false;
	}

  /*
   * (non-Javadoc)
   * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
   */
  @Override
  public void run(UserData userData) throws Exception {

    String[][] data = userData.getData();
    
    String[][] prefixesForNameSplitting = Lookup.getNameJoiners();
    
	// add user window to input character length
	UserIntegerInputDialog uiid = new UserIntegerInputDialog(UiController.getMainFrame(), "Enter the max number of characters the name should have");
	uiid.setVisible(true);
	
	int maxChar = 36;
	
	if(uiid.getIsNextPressed())
		maxChar = uiid.getUserInput();

    int counter = -1;
    for (int i = 0; i < data.length; i++) {
    	String nam1 = data[i][userData.getNam1Index()];

    	Record record = new Record.Builder(++counter, data[i], "", "", "").setNam1(nam1).setNam1_2("").build();
    	
    	record = Common.splitAddName(record, maxChar, prefixesForNameSplitting);
    	
    	userData.add(record); // add the record the recordList in userData

    }

    // set the Header fields that we have used
    userData.setDfHeaders(new String[] {
    		UserData.fieldName.NAME1.getName(),
    		UserData.fieldName.NAME1_2.getName()
    });

  }

}
