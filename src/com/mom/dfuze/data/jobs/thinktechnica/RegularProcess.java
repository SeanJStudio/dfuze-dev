package com.mom.dfuze.data.jobs.thinktechnica;

import java.util.ArrayList;
import java.util.List;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Validators;

public class RegularProcess implements RunThinkTechnicaBehavior {

	  private final String BEHAVIOR_NAME = "Regular Process";
	  protected String DESCRIPTION = "<html>Description<br/><ul><li>Fixes data</li></ul></html>";
	  protected String[] REQUIRED_FIELDS = {};
	  
	  private static final int
	  	COMPANY_INDEX = 0,
	  	DESC_INDEX = 1,
	  	ITEM_INDEX = 2,
	  	QTY_INDEX = 3,
	  	TOTAL_QTY_INDEX = 4;
	  
	  private static final String
	  	COMPANY_HEADER = "Company",
		DESC_HEADER = "Description",
		ITEM_HEADER = "Item No",
		QTY_HEADER = "Quantity",
		TOTAL_QTY_HEADER = "Total Quantity";
	  
	  private static final String[] newHeaders = new String[] {
			  COMPANY_HEADER,
			  DESC_HEADER,
			  ITEM_HEADER,
			  QTY_HEADER,
			  TOTAL_QTY_HEADER
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
	  public void run(UserData userData) {

	    String[][] data = userData.getData();
	    
	    userData.setInHeaders(newHeaders);
	    
	    int counter = -1;
	    int recordCounter = -1;
	    
	    List<String[]> newList = new ArrayList<>();
	    
	    for (int i = 0; i < data.length; i++) {
	    	String cmpny = data[i][COMPANY_INDEX];
	    	String desc = data[i][DESC_INDEX];
	    	String item = data[i][ITEM_INDEX];
	    	String qtyString = data[i][QTY_INDEX];
	    	
	    	int qty = 0;
	    	
	    	if(Validators.isNumber(qtyString))
	    		qty = (int) Double.parseDouble(qtyString);
	    	
	    	if(cmpny.length() > 0) {	    		
	    		String[] newData = new String[] {cmpny,"","","","0"};
	    		newList.add(newData);
	    		++recordCounter;
	    	} else {
	    		newList.get(recordCounter)[DESC_INDEX] += desc + "|";
	    		newList.get(recordCounter)[ITEM_INDEX] += item + "|";
	    		newList.get(recordCounter)[QTY_INDEX] += qty + "|";
	    		
	    		int oldQty = Integer.parseInt(newList.get(recordCounter)[TOTAL_QTY_INDEX]);
	    		newList.get(recordCounter)[TOTAL_QTY_INDEX] = String.valueOf(oldQty + qty);
	    	}
	     
	    }
	    
	    for(int i = 0; i < newList.size(); ++i) {
	    	for(int j = 0; j < newList.get(i).length; ++j) {
	    		newList.get(i)[j] = newList.get(i)[j].replaceAll("\\|$", "");
	    	}
	    }
	    
	    for(String[] line : newList) {
	    	Record record = new Record.Builder(++counter, line, "", "", "").build();
	    	userData.add(record);
	    }

	    // set the Header fields that we have used
	    userData.setDfHeaders(new String[] {});

	  }

	}