/**
 * Project: Dfuze
 * File: Load.java
 * Date: Oct 15, 2020
 * Time: 5:52:18 PM
 */
package com.mom.dfuze.data.jobs.utility;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class Load implements RunUtilityBehavior {

  private final String BEHAVIOR_NAME = "0 - Load";
  
  protected String[] REQUIRED_FIELDS = {};
  
  protected String DESCRIPTION = "<html>"
  		+ "Description<br/>"
  		+ "<ul>"
  		+ "<li>Loads data without mapping any fields</li>"
  		+ "</ul>"
  		+ "<br/>Reasons to use Load"
  		+ "<ul>"
  		+ "<li>Merge multiple files, even of different types</li>"
  		+ "<li>Casing conversion</li>"
  		+ "<li>Encoding correction</li>"
  		+ "<li>Data Verification creation</li>"
  		+ "<li>Inkjet file creation</li>"
  		+ "<li>Proof file creation</li>"
  		+ "<li>Sample file creation</li>"
  		+ "<li>Multi-file export</li>"
  		+ "<li>File type conversion on export</li>"
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
  public void run(UserData userData) {

    String[][] data = userData.getData();

    int counter = -1;
    for (int i = 0; i < data.length; i++) {

      Record record = new Record.Builder(++counter, data[i], "", "", "").build();

      userData.add(record); // add the record the recordList in userData

    }

    // set the Header fields that we have used
    userData.setDfHeaders(new String[] {});

  }

}
