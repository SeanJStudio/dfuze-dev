package com.mom.dfuze.data.jobs.utility;

import javax.swing.JOptionPane;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.DataVerificationDialog;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.dedupe.DedupeDialog2;

public class DataVerification implements RunUtilityBehavior {

	  private final String BEHAVIOR_NAME = "Data Verification";
	  protected String[] REQUIRED_FIELDS = {};
	  protected String DESCRIPTION = "<html>Data Verification<br/><br/>"
		  		+ "Description<br/>"
		  		+ "<ul>"
		  		+ "<li>Creates Data Verification Sheet and Records</li>"
		  		+ "</ul>"
		  		+ "Instructions<br/>"
		  		+ "<ul>"
		  		+ "<li>Load data and run</li>"
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
	  public void run(UserData userData) throws Exception {
		  userData.autoSetRecordList();						// add the records to the userData list
		  userData.autoSetRecordListFields(REQUIRED_FIELDS);	// set record members via required fields
		  
		  for(Record record : userData.getRecordList())
			  record.setIsProof("");

	    // set the Header fields that we have used
	    userData.setDfHeaders(new String[] {
	    		UserData.fieldName.IS_PROOF.getName(),
	    		});
	    
	    DataVerificationDialog dataVerificationDialog = new DataVerificationDialog(UiController.getMainFrame());
		dataVerificationDialog.setVisible(true);
		
	  }

	}
