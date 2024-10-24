package com.mom.dfuze.data.jobs.peacearchhospital;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Validators;


public class RegularProcess  implements RunPeaceArchHospitalBehavior {
	private String BEHAVIOR_NAME = "Regular Process";
	private String[] REQUIRED_FIELDS = {UserData.fieldName.DONATION1_AMOUNT.getName(), UserData.fieldName.SEGMENT_CODE.getName()};
	
	private String DESCRIPTION = "<html>"
			+ "Description<br/>"
			+ "<ol>"
			+ "Appeal field is used to define each segement:<br><br>"
			+ "<li>" + NEW_DONORS + "<br>A1=LG (RND 5), A2=Other</li><br>"
			+ "<li>" + CARING_CIRCLE_A_DONORS + "<br>A1=LG, A2=LG*25% (R250), A3=LG*50% (R250)</li><br>"
			+ "<li>" + CARING_CIRCLE_C_DONORS + "<br>A1=LG, A2=LG*25% (R25), A3=LG*50% (R25)</li><br>"
			+ "<li>" + RECENT_DONORS + "<br>A1=LG*25% (R25), A2=LG*50% (R25), A3=LG*75% (R25)</li><br>"
			+ "<li>" + CURRENT_DONORS + "<br>A1=LG*25% (R25), A2=LG*50% (R25), A3=LG*75% (R25)</li><br>"
			+ "<li>" + LAPSED_DONORS + "<br>IF (LG&le;50) A1=50, A2=100, A3=150</li><br>"
			+ "<li>" + LAPSED_DONORS + "<br>IF (LG&ge;=50) A1=LG*25%, A2=LG*50%, A3=LG*75%</li>"
			+ "</ol>"
			+ "Instructions<br/>"
			+ "<ul>"
			+ "<li>Map array field to GIFT ASK 1</li>"
			+ "<li>Map appeal field to SEGMENT CODE</li>"
			+ "</ul>"
			+ "</html>";


	// Compile the segment code patterns to match
	private Pattern newDonorsPattern = Pattern.compile("[n][d]$", Pattern.CASE_INSENSITIVE);
	private Pattern CaringCircleADonorsPattern = Pattern.compile("[c][c][a]$", Pattern.CASE_INSENSITIVE);
	private Pattern CaringCircleCDonorsPattern = Pattern.compile("[c][c][c]$", Pattern.CASE_INSENSITIVE);
	private Pattern recentDonorsPattern = Pattern.compile("[r][d]$", Pattern.CASE_INSENSITIVE);
	private Pattern currentDonorsPattern = Pattern.compile("[c][d]$", Pattern.CASE_INSENSITIVE);
	private Pattern lapsedDonorsPattern = Pattern.compile("([l][d][a]|[l][d])$", Pattern.CASE_INSENSITIVE);

	private static final String NEW_DONORS = "New";
	private static final String CARING_CIRCLE_A_DONORS = "Caring Circle A";
	private static final String CARING_CIRCLE_C_DONORS = "Caring Circle C";
	private static final String RECENT_DONORS = "Recent";
	private static final String CURRENT_DONORS = "Current";
	private static final String LAPSED_DONORS = "Lapsed";

	private static final String OTHER_LINE = "Other $__________";
	
	DecimalFormat dec = new DecimalFormat("#0.00");
	
	private static final String REMOVE_NO_CENTS_REGEX = "\\.00";

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		// The users' input data
		String[][] data = userData.getData();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {

			// Get all the fields we can for now
			String arrAmt = data[i][userData.getDn1AmtIndex()].replaceAll("[^0-9\\.]", "");
			String segCode = data[i][userData.getSegCodeIndex()];
			String segName = "";

			// initialize some values
			String dn1Amt = "";
			String dn2Amt = "";
			String dn3Amt = "";
			String dnOAmt = OTHER_LINE;

			// Check if our patterns match, if not, throw an error
			if (newDonorsPattern.matcher(segCode).find())
				segName = NEW_DONORS;
			else if (CaringCircleADonorsPattern.matcher(segCode).find())
				segName = CARING_CIRCLE_A_DONORS;
			else if (CaringCircleCDonorsPattern.matcher(segCode).find())
				segName = CARING_CIRCLE_C_DONORS;
			else if (recentDonorsPattern.matcher(segCode).find())
				segName = RECENT_DONORS;
			else if (currentDonorsPattern.matcher(segCode).find())
				segName = CURRENT_DONORS;
			else if (lapsedDonorsPattern.matcher(segCode).find())
				segName = LAPSED_DONORS;
			else
				throw new Exception(String.format("Unknown segment \"%s\" found at Record %d.", segCode, (i + 2)));

			// Initialize the last gift amount in case none was found
			BigDecimal dn1AmtBigDecimal = new BigDecimal("5.0");

			if(Validators.isNumber(arrAmt))
				dn1AmtBigDecimal = new BigDecimal(arrAmt);

			switch(segName) {
			case NEW_DONORS:
				dn1Amt =  String.valueOf(dec.format(Math.ceil(dn1AmtBigDecimal.doubleValue() / 5.0) * 5.0));
				break;
			case CARING_CIRCLE_A_DONORS:
				dn1Amt = fixZeroAsk(String.valueOf(dec.format(dn1AmtBigDecimal.doubleValue())));
				dn2Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.25) / 250) * 250));
							
				if(dn2Amt.equalsIgnoreCase(dn1Amt) || Double.valueOf(dn2Amt) < Double.valueOf(dn1Amt))
					dn2Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn1Amt, dn2Amt, true);
				
				dn3Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.5) / 250) * 250));
				
				if(dn3Amt.equalsIgnoreCase(dn2Amt) || Double.valueOf(dn3Amt) < Double.valueOf(dn2Amt))
					dn3Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn2Amt, dn3Amt, true);
				
				break;
			case CARING_CIRCLE_C_DONORS:
				dn1Amt = fixZeroAsk(String.valueOf(dec.format(dn1AmtBigDecimal.doubleValue())));
				dn2Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.25) / 25) * 25));
				
				if(dn2Amt.equalsIgnoreCase(dn1Amt) || Double.valueOf(dn2Amt) < Double.valueOf(dn1Amt))
					dn2Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn1Amt, dn2Amt, true);
				
				dn3Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.5) / 25) * 25));
				
				if(dn3Amt.equalsIgnoreCase(dn2Amt) || Double.valueOf(dn3Amt) < Double.valueOf(dn2Amt))
					dn3Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn2Amt, dn3Amt, true);
				
				break;
			case RECENT_DONORS:
				dn1Amt = fixZeroAsk(String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.25) / 25) * 25)));
				
				dn2Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.5) / 25) * 25));
				
				if(dn2Amt.equalsIgnoreCase(dn1Amt) || Double.valueOf(dn2Amt) < Double.valueOf(dn1Amt))
					dn2Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn1Amt, dn2Amt, true);
				
				dn3Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.75) / 25) * 25));
				
				if(dn3Amt.equalsIgnoreCase(dn2Amt) || Double.valueOf(dn3Amt) < Double.valueOf(dn2Amt))
					dn3Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn2Amt, dn3Amt, true);
				
				break;
			case CURRENT_DONORS:
				dn1Amt = fixZeroAsk(String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.25) / 25) * 25)));
				dn2Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.5) / 25) * 25));
				
				if(dn2Amt.equalsIgnoreCase(dn1Amt) || Double.valueOf(dn2Amt) < Double.valueOf(dn1Amt))
					dn2Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn1Amt, dn2Amt, true);
				
				dn3Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.75) / 25) * 25));
				
				if(dn3Amt.equalsIgnoreCase(dn2Amt) || Double.valueOf(dn3Amt) < Double.valueOf(dn2Amt))
					dn3Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn2Amt, dn3Amt, true);
				
				break;
			case LAPSED_DONORS:
				if(dn1AmtBigDecimal.doubleValue() <= 50) {
					dn1Amt = "50.00";
					dn2Amt = "100.00";
					dn3Amt = "150.00";
				} else {
					
					dn1Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.25) / 25) * 25));
					
					dn2Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.5) / 25) * 25));
					
					if(dn2Amt.equalsIgnoreCase(dn1Amt) || Double.valueOf(dn2Amt) < Double.valueOf(dn1Amt))
						dn2Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn1Amt, dn2Amt, true);
					
					dn3Amt = String.valueOf(dec.format(Math.round((dn1AmtBigDecimal.doubleValue() * 1.75) / 25) * 25));
					
					if(dn3Amt.equalsIgnoreCase(dn2Amt) || Double.valueOf(dn3Amt) < Double.valueOf(dn2Amt))
						dn3Amt = fixAsk(dn1AmtBigDecimal.doubleValue() * 0.25, dn2Amt, dn3Amt, true);

				}
				break;
			default:

				break;
			}

			dn1Amt = dn1Amt.replaceAll(REMOVE_NO_CENTS_REGEX, "");
			dn2Amt = dn2Amt.replaceAll(REMOVE_NO_CENTS_REGEX, "");
			dn3Amt = dn3Amt.replaceAll(REMOVE_NO_CENTS_REGEX, "");

			// Build the Record 
			Record record = new Record.Builder(i, data[i], "", "", "").setSeg(segName).setDn1Amt(dn1Amt).setDn2Amt(dn2Amt).setDn3Amt(dn3Amt).setODnAmt(dnOAmt).build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] {
				UserData.fieldName.SEGMENT.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.OPEN_DONATION_AMOUNT.getName()
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
	
	@Override
	public Boolean isFileNameRequired() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#getRequiredFields()
	 */
	@Override
	public String[] getRequiredFields() {
		return REQUIRED_FIELDS;
	}
	
	public String fixZeroAsk(String ask) {
		if(Double.valueOf(ask) == 0)
			ask = "25";
		
		return ask;
	}	
	
	public String fixAsk(Double percentage, String donation1, String donation2, boolean isRounded) {
		
		Double percentageToAdd = percentage;

		while(donation1.equalsIgnoreCase(donation2) || Double.valueOf(donation2) < Double.valueOf(donation1)) {
			if(isRounded)
				donation2 = String.valueOf(dec.format(Math.round((new BigDecimal(donation2).doubleValue() + percentageToAdd) / 25) * 25));
			else
				donation2 = String.valueOf(dec.format(Math.round((new BigDecimal(donation2).doubleValue() + percentageToAdd))));
			
			percentageToAdd += percentage;
		}
		
		return donation2;
	}

}
