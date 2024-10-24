package com.mom.dfuze.data.jobs.harveymckinnonassociates;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.io.MRFGiftReport;

public class MarmotRecoveryFoundation  implements RunHarveyMckinnonAssociatesBehavior {
	private static String BEHAVIOR_NAME = "MRF";
	private static String[] REQUIRED_FIELDS = { 
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.DEAR_SALUTATION.getName(),
			UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
			UserData.fieldName.LAST_DONATION_DATE.getName(),
			UserData.fieldName.SEGMENT_CODE.getName(),
			UserData.fieldName.IN_ID.getName()
	};
	
	private static String DESCRIPTION = "<html>Instructions<br/><ul><li>Add seeds to supplied data</li><li>Load supplied data and run</li></ul>"
			+ "Description<br/><ul><li>Identifies Companies</li><li>Creates Salutation</li><li>Formats Last Gift</li><li>Formats Last Gift Date</li><li>Calculates Gift Array</li></ul>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";

	private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("0.00");
	private static final double ROUND_BY_FIVE = 5.00;
	private static final BigDecimal LAST_GIFT_ROUNDING_AMOUNT = new BigDecimal("1.00");
	private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d");

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		String[][] prefixesForNameSplitting = Lookup.getNameJoiners();
		String[][] companyKeywords = Lookup.getCompanykeywords();

		// The users' input data
		String[][] data = userData.getData();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {

			// Get all the fields we can for now
			String fnam = data[i][userData.getFstNameIndex()];
			String lnam = data[i][userData.getLstNameIndex()];
			String dearSal = data[i][userData.getDearSalIndex()].trim();
			String lstDnAmt = data[i][userData.getLstDnAmtIndex()];
			String lstDnDat = data[i][userData.getLstDnDatIndex()];
			String segCode = data[i][userData.getSegCodeIndex()];
			String inId = data[i][userData.getInIdIndex()];

			// Initialize the extra fields we want the record to have
			String nam1 = (fnam + " " + lnam).trim();
			String codeLine = segCode + "-" + inId;
			String cmpny = "";
			String dn1Amt = "";
			String dn2Amt = "";
			String dn3Amt = "";
			String oDnAmt = "";

			// Build the dear Salutation
			if(dearSal.length() == 1
					|| dearSal.length() == 0
					|| dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
					|| dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
					|| dearSal.length() == 2 && !Validators.hasVowel(dearSal)
					) {

				dearSal = "Friend of the Marmot";
			}

			// MRF does not identify businesses so we must search for them manually.
			if(!nam1.isBlank()) {
				for(int j = 0; j < companyKeywords.length; ++j) {
					String keyword = " " + companyKeywords[j][0] + " ";
					String recordValue = (" " + nam1.toLowerCase() + " ");
					if(recordValue.contains(keyword) || NUMBER_PATTERN.matcher(nam1).find()) {
						cmpny = nam1;
						nam1 = "";
						dearSal = "Friend of the Marmot";
						break;
					}
				}
			}

			// Build the donation asks
			// Strip anything that isn't number related
			String tempLstDnAmt = lstDnAmt.replaceAll("[^0-9\\.]", "");

			// Check if its a number, if not set the default value
			if (!Validators.isNumber(tempLstDnAmt))
				tempLstDnAmt = DEFAULT_AMOUNT.toPlainString();

			// Roundup dn1 to the nearest $1
			BigDecimal newLastGiftAmt = new BigDecimal(tempLstDnAmt).divide(LAST_GIFT_ROUNDING_AMOUNT, 2, RoundingMode.CEILING)
					.setScale(0, RoundingMode.CEILING).multiply(LAST_GIFT_ROUNDING_AMOUNT);

			// determine how the donations should be calculated
			if(newLastGiftAmt.doubleValue() < 15) {
				dn1Amt = "15";
				dn2Amt = "20";
				dn3Amt = "25";
				oDnAmt = "Other $ ______";
			} else if(newLastGiftAmt.doubleValue() < 750) {
				dn1Amt = newLastGiftAmt.toPlainString();
				dn2Amt = String.valueOf(Math.ceil((newLastGiftAmt.intValue() * 1.3) / ROUND_BY_FIVE) * ROUND_BY_FIVE);
				dn3Amt = String.valueOf(Math.ceil((newLastGiftAmt.intValue() * 1.6) / ROUND_BY_FIVE) * ROUND_BY_FIVE);
				oDnAmt = "Other $ ______";
			} else {
				dn1Amt = "";
				dn2Amt = "";
				dn3Amt = "";
				oDnAmt = "$_______";
			}

			Record record = new Record.Builder(i, data[i], "", "", "")
					.setFstName(fnam)
					.setLstName(lnam)
					.setNam1(nam1)
					.setNam1_2("")
					.setCmpny(cmpny)
					.setDearSal(dearSal)
					.setLstDnDat(lstDnDat)
					.setLstDnAmt(lstDnAmt)
					.setDn1Amt(dn1Amt)
					.setDn2Amt(dn2Amt)
					.setDn3Amt(dn3Amt)
					.setODnAmt(oDnAmt)
					.setSegCode(segCode)
					.setCodeLine(codeLine)
					.build();

			//nam1 and nam1_2 must be set for splitAddName
			record = Common.splitAddName(record, 36, prefixesForNameSplitting);

			userData.add(record);

		}

		// Create the gift report in excel
		MRFGiftReport.writeMRFGiftReport(userData.getRecordList());
		
		DecimalFormat decimalFormat = new DecimalFormat("###,###,###.00");
		DecimalFormat numberFormat = new DecimalFormat("###,###,###");
		
		DateTimeFormatter suppliedDateFormat = DateTimeFormatter.ofPattern("MMM d yyyy");
		String newDateFormat = "MMM d, yyyy";
		
		// Loop through the records again and format the last gift and last gift date
		for(int i = 0 ; i < userData.getRecordList().size(); ++i) {
			Record record = userData.getRecordList().get(i);
			String lstDnAmt = record.getLstDnAmt().replaceAll("[^0-9\\.]", "");
			String lstDnDat = record.getLstDnDat();
			
			if(lstDnAmt.contains(".") && Validators.isNumber(lstDnAmt))
				record.setLstDnAmt(decimalFormat.format(Double.valueOf(lstDnAmt)));
			else if(Validators.isWholeNumber(lstDnAmt))
				record.setLstDnAmt(numberFormat.format(Integer.valueOf(lstDnAmt)));
			
			if(Validators.isStringOfDateFormat(lstDnDat, "MMM d yyyy")) {
				LocalDate recordDate = LocalDate.parse(lstDnDat, suppliedDateFormat);
				lstDnDat = recordDate.format(DateTimeFormatter.ofPattern(newDateFormat));
				record.setLstDnDat(" " + lstDnDat); // Added a space so excel doesn't convert
			}
				

		}

		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.NAME1_2.getName(),
				UserData.fieldName.COMPANY.getName(),
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
				UserData.fieldName.LAST_DONATION_DATE.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.OPEN_DONATION_AMOUNT.getName(),
				UserData.fieldName.CODELINE.getName()
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
		return false;
	}

}
