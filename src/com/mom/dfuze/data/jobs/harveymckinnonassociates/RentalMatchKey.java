package com.mom.dfuze.data.jobs.harveymckinnonassociates;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

public class RentalMatchKey  implements RunHarveyMckinnonAssociatesBehavior {

	  private String BEHAVIOR_NAME = "Rental MatchKey";
	  private String DESCRIPTION = "11/02/2020\n\n" + "Appends a match Key for rental lists to the end of a file\n\n" + "The match key strips all alphanumerics from the address and concatenates them together";

	  private String[] REQUIRED_FIELDS = { UserData.fieldName.ADDRESS1.getName(), UserData.fieldName.ADDRESS2.getName(),
	          UserData.fieldName.POSTALCODE.getName()};
	  
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
	      
	      String codeLine = add1 + add2;
	      codeLine = codeLine.replaceAll("[^0-9]", "");
	      codeLine += pCode.replaceAll("[^a-zA-Z0-9]","");

	      Record record = new Record.Builder(++counter, data[i], add1, add2, pCode).setCodeLine(codeLine).build();

	      userData.add(record); // add the record the recordList in userData

	    }

	    // set the Header fields that we have used
	    userData.setDfHeaders(new String[] { UserData.fieldName.CODELINE.getName() });

	  }
}
