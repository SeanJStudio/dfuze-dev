/**
 * Project: Dfuze
 * File: DataDump.java
 * Date: Aug 7, 2020
 * Time: 2:32:04 AM
 */
package com.mom.dfuze.data.jobs.utility;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.io.DataDumpReport;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class DataDump implements RunUtilityBehavior {

  private String BEHAVIOR_NAME = "Data dump";
  private String[] REQUIRED_FIELDS = { UserData.fieldName.SEGMENT.getName() };
  
  private String DESCRIPTION = "<html>"
  		+ "Instructions<br/>"
  		+ "<ol><li>Load the data file, map the segment field and run</li></ol>"
  		+ "Description<br/>"
  		+ "<ul>"
  		+ "<li>Auto-generates a .xlsx file to display the amount of blank values found in each field of the data. Also shows each segment and how many were found.</li>"
  		+ "</ul>"
  		+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";



  /*
   * (non-Javadoc)
   * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
   */
  @Override
  public void run(UserData userData) throws Exception {
    String[][] data = userData.getData();

    int counter = -1;
    for (int i = 0; i < data.length; i++) {

      String seg = data[i][userData.getSegIndex()];

      Record record = new Record.Builder(++counter, data[i], "", "", "").setSeg(seg).build();

      userData.add(record); // add the record the recordList in userData

    }

    // Create the data summary report in excel
    DataDumpReport.writeDataDumpReport(userData);

    // set the Header fields that we have used
    userData.setDfHeaders(new String[] {});
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
