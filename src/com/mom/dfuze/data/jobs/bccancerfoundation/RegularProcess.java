package com.mom.dfuze.data.jobs.bccancerfoundation;
import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;

public class RegularProcess implements RunBCCancerFoundationBehavior {

	private final String BEHAVIOR_NAME = "Regular Process";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.IN_ID.getName(),
			UserData.fieldName.APPEAL.getName(),
			UserData.fieldName.PACKAGE_VERSION.getName()
			};
	
	private String DESCRIPTION = "<html>Description<br/>"
			+ "<ul>"
			+ "<li>Splits 'Comments' field into gift array fields ex.<br/>'35-50-75' --> dfDnAmt1 = 35, dfDnAmt2 = 50, dfDnAmt3 = 75<br/><br/></li>"
			+ "<li>Creates the barcode from the following fields:<br/>ID, Appeal, FundID, LetterCode, PackageVersion</li>"
			+ "</ul>"
			//+ "Instructions<br/>"
			//+ "<ol>"
			//+ "<li>When prompted, enter the letterCode to use ex. DM4</li>"
			//+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)+"</html>";
	
	//private static Pattern CID_PATTERN = Pattern.compile("[c][i][d]", Pattern.CASE_INSENSITIVE);
	
	private static final String COMMENTS_FIELD = "CnApls_1_01_Comments";
	
	private static final String FUND_ID = "0NSUR001";
	

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		/************************************
		 * CONFIRM WE HAVE THE COMMENTS FIELD
		 ***********************************/
		int commentsIndex = -1;
		for(int i = 0; i < userData.getInHeaders().length; ++i) {
			String header = userData.getInHeaders()[i];

			if(header.equalsIgnoreCase(COMMENTS_FIELD)) {
				commentsIndex = i;
				break;
			}
		}

		if(commentsIndex < 0)
			throw new ApplicationException(String.format("ERROR: Could not find the field %s", COMMENTS_FIELD));
		

		/********************
		 * GET THE LETTERCODE
		 ********************/
		String letterCode = "DM";
		//UserInputDialog userInputDialog = new UserInputDialog(UiController.getMainFrame(), "LetterCode ex. DM4");
		//userInputDialog.setVisible(true);
		
		//if(userInputDialog.getIsNextPressed())
			//letterCode = userInputDialog.getUserInput();
		//else
			//throw new ApplicationException(String.format("ERROR: dialog closed"));
		
		/********************
		 * GET THE FUNDID
		 ********************/
		String fundId = FUND_ID;
		//UserInputDialog getFundIdDialog = new UserInputDialog(UiController.getMainFrame(), "Enter the Fund ID ex. " + FUND_ID);
		//getFundIdDialog.getTextField().setText(FUND_ID);
		//getFundIdDialog.setVisible(true);
		
		//if(getFundIdDialog.getIsNextPressed())
		//	fundId = getFundIdDialog.getUserInput();
		//else
			//throw new ApplicationException(String.format("ERROR: dialog closed"));

		String[][] data = userData.getData();

		for (int i = 0; i < data.length; i++) {
			String id = data[i][userData.getInIdIndex()];
			String appeal = data[i][userData.getAppealIndex()];
			String pkg = data[i][userData.getPkgVerIndex()];
			String comments = data[i][commentsIndex];
			
			String[] giftArray = comments.split("-");
			
			if(giftArray.length != 3)
				throw new ApplicationException(String.format("Error: was expecting 3 elements for the gift array but found %d.", giftArray.length));
			
			String dn1Amt = giftArray[0];
			String dn2Amt = giftArray[1];
			String dn3Amt = giftArray[2];
			
			String barCode = String.format("*%s$I%s$I%s$I%s$I%s*", id, appeal, fundId, letterCode, pkg);

			Record record = new Record.Builder(i, data[i], "", "", "")
					.setLetVer(letterCode)
					.setBarCode(barCode)
					.setDn1Amt(dn1Amt)
					.setDn2Amt(dn2Amt)
					.setDn3Amt(dn3Amt)
					.build();

			userData.add(record); // add the record the recordList in userData
		}

		// set the Header fields that we have used
		userData.setDfHeaders(new String[] {
				UserData.fieldName.LETTER_VERSION.getName(),
				UserData.fieldName.BARCODE.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName()
				});

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
		// TODO Auto-generated method stub
		return true;
	}

}