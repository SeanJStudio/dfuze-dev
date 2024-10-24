package com.mom.dfuze.data.jobs.harveymckinnonassociates;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;


public class LionsFoundationDogGuides implements RunHarveyMckinnonAssociatesBehavior {
	private String BEHAVIOR_NAME = "LCDG";

	private String[] REQUIRED_FIELDS = { 
			UserData.fieldName.PREFIX.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.DEAR_SALUTATION.getName()		
	};
	
	private String DESCRIPTION = "<html>Instructions<br/>"
			+ "<ol>"
			+ "<li>Load the supplied data and run</li>"
			+ "</ol>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Creates standardized name fields</li>"
	        + "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		// add records to userData, set userData record members via required fields
		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		
		caseNames(userData);
		updateSalutation(userData);
		splitNames(userData);
		
		userData.setDfHeaders(new String[] {
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.NAME1_2.getName(),
	    		UserData.fieldName.DEAR_SALUTATION.getName(),
	    		UserData.fieldName.PARAGRAPH_SALUTATION.getName(),
		});
	}
	
	private void splitNames(UserData userData) throws Exception {
		String[][] prefixesForNameSplitting = Lookup.getNameJoiners();
		final int MAX_LENGTH = 36;
		for(Record record : userData.getRecordList()) {
			record.setNam1_2(""); // need name1_2 before splitting
			record = Common.splitAddName(record, MAX_LENGTH, prefixesForNameSplitting);
		}
	}
	
	private void caseNames(UserData userData) {
		for(Record record : userData.getRecordList()) {
			record.setFstName(Common.caseName(record.getFstName()));
			record.setLstName(Common.caseName(record.getLstName()));
			record.setNam1(Common.caseName(record.getNam1()));
		}
	}
	
	private void updateSalutation(UserData userData) {
		for(Record record : userData.getRecordList()) {
			
			String prefix = record.getPrefix();
			String fnam = record.getFstName();
			String lnam = record.getLstName();
			String dearSal = record.getDearSal();
			String paraSal = dearSal;
			
			if(dearSal.length() == 1
		    		  || dearSal.length() == 0
		    		  || dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
		    		  || dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
		    		  || dearSal.length() == 2 && !Validators.hasVowel(dearSal)
		    		  ) {

		    	  // if bad salutation, check fnam
		    	  if(fnam.length() > 0) {
		    		  if(fnam.length() == 1
		    	    		  || fnam.length() == 0
		    	    		  || fnam.length() >= 2 && fnam.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
		    	    		  || fnam.length() == 2 && Validators.areCharactersSame(fnam)
		    	    		  || fnam.length() == 2 && !Validators.hasVowel(fnam)
		    	    		  ) {
		    			  
		    			  // if bad fnam, check prefix and lastname
		    	    	  if(prefix.length() > 0 && lnam.length() > 0) {
		    	    		  dearSal = prefix + " " + lnam;
		    	    		  paraSal = prefix + " " + lnam;
		    	    	  } else { // if bad prefix and lnam, do friend
		    	    		  dearSal = "Friend";
		    	        	  paraSal = "";
		    	    	  }
		    	      } else { // if fnam good, use it
		    	    	  dearSal = fnam;
		    	    	  paraSal = fnam;
		    	      }
		    	  }
		      }
			
			record.setDearSal(dearSal);
			record.setParaSal(paraSal);
		}
			
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