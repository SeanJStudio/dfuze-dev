/**
 * Project: Dfuze
 * File: RunMingSort.java
 * Date: Mar 10, 2020
 * Time: 6:08:50 PM
 */
package com.mom.dfuze.data.jobs.ritchiebrothers;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;

/**
 * RunSplitFieldsBy45 implements a RunBehavior for Ritchie Brothers Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RunSplitFieldsBy45 implements RunRitchieBrothersBehavior {

  private String BEHAVIOR_NAME = "Split Fields by 45 characters";
  private String DESCRIPTION = "Splits the company, address1, and address2 fields by 45 characters or less if the limit is reached within the middle of a word.";
  private String[] REQUIRED_FIELDS = { UserData.fieldName.ADDRESS1.getName(), UserData.fieldName.ADDRESS2.getName(),
          UserData.fieldName.POSTALCODE.getName(), UserData.fieldName.COMPANY.getName() };

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

    final int MAX_SIZE = 45;

    final int FIRST_HALF = 0; // the first element in the array
    final int SECOND_HALF = 1; // the second element in the array

    int counter = 0;

    for (int i = 0; i < data.length; i++) {

      String cmpnyHalves[] = Common.splitString(data[i][userData.getCmpnyIndex()], MAX_SIZE);
      String cmpny = cmpnyHalves[FIRST_HALF];
      String cmpny_2 = cmpnyHalves[SECOND_HALF];

      String add1Halves[] = Common.splitString(data[i][userData.getAdd1Index()], MAX_SIZE);
      String add1 = add1Halves[FIRST_HALF];
      String add1_2 = add1Halves[SECOND_HALF];

      String add2Halves[] = Common.splitString(data[i][userData.getAdd2Index()], MAX_SIZE);
      String add2 = add2Halves[FIRST_HALF];
      String add2_2 = add2Halves[SECOND_HALF];

      String pCode = data[i][userData.getPCodeIndex()];

      Record record = new Record.Builder(++counter, data[i], data[i][userData.getAdd1Index()], data[i][userData.getAdd2Index()],
              data[i][userData.getPCodeIndex()]).setCmpny(cmpny).setCmpny_2(cmpny_2).setAdd1(add1).setAdd1_2(add1_2).setAdd2(add2).setAdd2_2(add2_2)
                      .setPCode(pCode).build();

      userData.add(record); // add the record the recordList in userData

    }

    // set the Header fields that we have used
    userData.setDfHeaders(
            new String[] { UserData.fieldName.COMPANY.getName(), UserData.fieldName.COMPANY_2.getName(), UserData.fieldName.ADDRESS1.getName(),
                    UserData.fieldName.ADDRESS1_2.getName(), UserData.fieldName.ADDRESS2.getName(), UserData.fieldName.ADDRESS2_2.getName() });

  }

}
