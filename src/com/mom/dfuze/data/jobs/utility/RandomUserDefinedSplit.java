package com.mom.dfuze.data.jobs.utility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserIntegerInputDialog;
import com.mom.dfuze.ui.UserMultiRadioDialog;

public class RandomUserDefinedSplit implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Random User Defined Split";
	
	protected String[] REQUIRED_FIELDS = {
				UserData.fieldName.SEGMENT.getName()
			};
	
	protected String DESCRIPTION = "<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Creates dfABGroup field</li>"
			+ "<li>Randomly and evenly distributes each unique value from mapped field into dfABGroup with value of A and B or C and T</li>"
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
	public void run(UserData userData) throws ApplicationException {
		
		// Create a dialog window with 2 selections
	    UserMultiRadioDialog radioDialog = new UserMultiRadioDialog(UiController.getMainFrame(), 2, "Choose how to identify the split groups");
	    
	    // Set their values
	    radioDialog.setRadioButton1("A and B");
	    radioDialog.setRadioButton2("C and T");
	    
	    radioDialog.setVisible(true);
	    
	    // make sure a selection was made
	    if(!radioDialog.getIsNextPressed())
	    	throw new ApplicationException("No Selection made.");
	    
	    // Capture the selection made
	    String group1, group2 = "";
	    if(radioDialog.getRadio1().isSelected()) {
	    	group1 = "A";
	    	group2 = "B";
	    } else {
	    	group1 = "C";
	    	group2 = "T";
	    }

		Map<String, Integer> segmentCountMap = new HashMap<>();

		String[][] data = userData.getData();

		int counter = -1;
		for (int i = 0; i < data.length; i++) {

			String seg = data[i][userData.getSegIndex()];

			if (!segmentCountMap.containsKey(seg))
				segmentCountMap.put(seg, 1);
			else
				segmentCountMap.put(seg, segmentCountMap.get(seg) + 1);

			Record record = new Record.Builder(++counter, data[i], "", "", "").setABGroup(group1).setSeg(seg).build();

			userData.add(record); // add the record the recordList in userData
		}

		// Randomize our list
		Collections.shuffle(userData.getRecordList());

		// Now lets sort it on the segment
		Collections.sort(userData.getRecordList(), new RecordSorters.CompareByFieldAsc(UserData.fieldName.SEGMENT.getName()));
		
		//
		UserIntegerInputDialog userInputDialog = new UserIntegerInputDialog(UiController.getMainFrame(), "Enter the percentage to use for the control split");
		
		userInputDialog.setVisible(true);
		
		int percentToUse = 0;
		
		if(userInputDialog.getIsNextPressed())
			percentToUse = userInputDialog.getUserInput();

		int segCount = 1;
		
		// The last segment encountered
		String lastSeg = userData.getRecordList().get(0).getSeg();
		
		// The total amount of record that should be group1 for the current segment
		int segSplitCount = (int) roundPercent((percentToUse * segmentCountMap.get(lastSeg)) / 100);
		
		for(Record record: userData.getRecordList()) {
			
			// If a new segment is found, reset
			if(!record.getSeg().equals(lastSeg)) {
				segCount = 1;
				lastSeg = record.getSeg();
				segSplitCount = (int) roundPercent((percentToUse * segmentCountMap.get(lastSeg)) / 100);
			}
			
			// If the current segment count is greater than %, change it to group2
			if(segCount > segSplitCount)
				record.setABGroup(group2);

			++segCount;
		}

		// Sort our list back to the original order
		Collections.sort(userData.getRecordList(), new RecordSorters.CompareByDfId());

		// Set the Header fields that we want to output
		userData.setDfHeaders(new String[] {UserData.fieldName.AB_GROUP.getName()});

	}
	
	private static long roundPercent(double number) {
		if(number < 1.0)
			return 1;
		else
			return Math.round(number);
	}

}