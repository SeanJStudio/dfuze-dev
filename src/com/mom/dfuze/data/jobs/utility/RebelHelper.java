/**
 * Project: Dfuze
 * File: Load.java
 * Date: Oct 15, 2020
 * Time: 5:52:18 PM
 */
package com.mom.dfuze.data.jobs.utility;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

/**
 * @author Sean Johnson, A00940079
 *
 */
public class RebelHelper implements RunUtilityBehavior {

  private final String BEHAVIOR_NAME = "RebelHelper";
  protected String DESCRIPTION = "<html>Description"
  		+ "<br/>"
  		+ "<ul>"
  		+ "<li>Helps with Rebe;</li>"
  		+ "</ul>"
  		+ "</html>";
  protected String[] REQUIRED_FIELDS = {UserData.fieldName.SEGMENT.getName()};
  
  private static final Pattern ROUTE_PATTERN = Pattern.compile("([G][D]|[L][B]|[S][S]|[R][R]|[L][C]|[D][R]|[C][F]|[M][R])\\d+", Pattern.CASE_INSENSITIVE);

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

    int counter = -1;
    for (int i = 0; i < data.length; i++) {
    	String routeDesc = data[i][userData.getSegIndex()];
    	
    	String[] routeParts = routeDesc.split("\\s+");
    	ArrayList<String> routes = new ArrayList<>();
    	
    	for(String part: routeParts) {
    		if(ROUTE_PATTERN.matcher(part).find()) {
    			routes.add(part.toUpperCase());
    		}
    	}

    	String delimRoutes = String.join(",", routes);
    	
    	Record record = new Record.Builder(++counter, data[i], "", "", "").setSeg(delimRoutes).build();
	
    	userData.add(record); // add the record the recordList in userData

    }

    // set the Header fields that we have used
    userData.setDfHeaders(new String[] {
    		UserData.fieldName.SEGMENT.getName()
    });

  }

}
