/**
 * Project: Dfuze
 * File: Load.java
 * Date: Oct 15, 2020
 * Time: 5:52:18 PM
 */
package com.mom.dfuze.data.jobs.utility;

import java.util.regex.Pattern;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class Test implements RunUtilityBehavior {

  private final String BEHAVIOR_NAME = "Test";
  protected String DESCRIPTION = "<html>"
  								+ "Description<br/>"
  								+ "<ul>"
  								+ "<li>Loads data without mapping any fields</li>"
  								+ "</ul><br/>Reasons to use Load"
  								+ "<ul>"
  								+ "<li>Merge multiple files, even of different types</li>"
  								+ "<li>Casing conversion</li><li>Encoding correction</li>"
  								+ "<li>Inkjet file creation</li>"
  								+ "<li>Proof file creation</li>"
  								+ "<li>Sample file creation</li>"
  								+ "<li>Multi-file export</li>"
  								+ "<li>File type conversion on export</li>"
  								+ "</ul>"
  								+ "</html>";
  
  protected String[] REQUIRED_FIELDS = {
		  UserData.fieldName.NAME1.getName()
		  };

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


    userData.autoSetRecordList();
	userData.autoSetRecordListFields(REQUIRED_FIELDS);
	
	Pattern pfp1Pattern = Lookup.getPrefixFirstPassPattern();
    
    for (int i = 0; i < userData.getRecordList().size(); i++) {
    	Record record = userData.getRecordList().get(i);
    	String[] normalized = Common.getNewNormalizedDupeNames(record.getNam1(), pfp1Pattern);
    	record.setNam1(normalized[0]);
    	record.setNam2(normalized[1]);
    }

    // set the Header fields that we have used
    userData.setDfHeaders(new String[] {
    		UserData.fieldName.NAME1.getName(),
    		UserData.fieldName.NAME2.getName()
    		});

  }

}
