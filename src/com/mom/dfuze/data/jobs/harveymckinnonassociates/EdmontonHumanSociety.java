package com.mom.dfuze.data.jobs.harveymckinnonassociates;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.SegmentPlan;
import com.mom.dfuze.data.SegmentPlanSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.io.GiftReport;
import com.mom.dfuze.io.SegmentPlanReader;
import com.mom.dfuze.io.SegmentationReport;
import com.mom.dfuze.ui.UiController;


public class EdmontonHumanSociety implements RunHarveyMckinnonAssociatesBehavior {
	private final String SEGMENT_PLAN = "1";
	private String BEHAVIOR_NAME = "EHS";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.DEAR_SALUTATION.getName(),
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.FIRST_DONATION_DATE.getName(),
			UserData.fieldName.FIRST_DONATION_AMOUNT.getName(),
			UserData.fieldName.LAST_DONATION_DATE.getName(),
			UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
			UserData.fieldName.NUMBER_OF_DONATIONS.getName(),
			UserData.fieldName.RECORD_TYPE.getName(),
			UserData.fieldName.SEGMENT_PLAN_FILTER_1.getName(),
			UserData.fieldName.SEGMENT_PLAN_FILTER_2.getName() 
	};
	
	private String DESCRIPTION = "<html>Instructions<br/>"
			+ "<ol>"
			+ "<li>Add seeds to supplied data</li>"
			+ "<li>Any dates should be in m/d/yyyy format</li>"
			+ "<li>Load the supplied data and run</li>"
			+ "</ol>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Creates the salutation fields</li>"
	        + "<li>Splits the addressee fields into 2 fields</li>"
	        + "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		List<List<String>> segmentPlanData = SegmentPlanReader.readSegmentPlanData(SEGMENT_PLAN);
		List<SegmentPlan> originalSegmentPlanList = SegmentPlanReader.createSegmentPlanList(SEGMENT_PLAN, segmentPlanData);

		List<SegmentPlan> segmentPlanList = originalSegmentPlanList;

		String[][] prefixesForNameSplitting = Lookup.getNameJoiners();

		// check if we were able to get the segmentPlan
		// maybe I should make a function like.. validateSegmentPlan(), sounds easier to remember to add
		// Or make it part of the job..
		if (segmentPlanList == null)
			return;

		Collections.sort(segmentPlanList, new SegmentPlanSorters.CompareByFilter1Filter2PriorityFromGiftDateDescending());

		List<Record> unsegmentedRecordList = new ArrayList<>();

		// The users' input data
		String[][] data = userData.getData();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {
			
			@SuppressWarnings("unused")
			boolean isSeed = false;

			// Get all the fields we can for now
			String fnam = data[i][userData.getFstNameIndex()].replaceAll("  ", " ").trim();
			String dearSal = data[i][userData.getDearSalIndex()].replaceAll("  ", " ").trim();
			String nam1 = data[i][userData.getNam1Index()].replaceAll("  ", " ").trim();
			String fDnDat = data[i][userData.getFstDnDatIndex()];
			String lDnDat = data[i][userData.getLstDnDatIndex()];
			String fstDnAmt = data[i][userData.getFstDnAmtIndex()];
			String lstDnAmt = data[i][userData.getLstDnAmtIndex()];
			String dnNum = data[i][userData.getNumDnIndex()];
			String recType = data[i][userData.getRecTypeIndex()];
			String spFilt1 = data[i][userData.getSpFilt1Index()];
			String spFilt2 = data[i][userData.getSpFilt2Index()];

			// Initialize the extra fields we want the record to have
			String priority = "";
			String segCode = "";
			String segName = "";
			String letVer = "";
			String pkgVer = "";
			String repVer = "";

			String dn1Amt = "";
			String dn2Amt = "";
			String dn3Amt = "";
			String oDnAmt = "";
			String mDn1Amt = "";
			String mDn2Amt = "";
			String mDn3Amt = "";
			String mODnAmt = "";
			
			boolean isRecord2YearsOrOlder = false;
			
			// Get the year from the last donation date
			if(Validators.isValidMDYYYYDate(lDnDat)){
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

		        // Parse the string to LocalDate
		        LocalDate lastDonationlocalDate = LocalDate.parse(lDnDat, formatter);
		        int recordYear = lastDonationlocalDate.getYear();
		        int currentYear = Year.now().getValue();
		        
		        if(recordYear < currentYear - 1)
		        	isRecord2YearsOrOlder = true;
			}


			// what type of record is this? i is individual, o is business
			boolean recIsIndividual = (recType.trim().equalsIgnoreCase("i")) ? true : false;
			boolean recIsBusiness = (recType.trim().equalsIgnoreCase("o")) ? true : false;

			String paraSal = dearSal;
			
			if(recIsBusiness) {
				if(dearSal.equalsIgnoreCase(nam1)) {
					dearSal = "Friend";
					paraSal = "";
				}
			}

			//Fix the salutation
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
						dearSal = "Friend";
						paraSal = "";
					} else { // if fnam good, use it
						dearSal = fnam;
						paraSal = fnam;
					}
				}
			}

			// if friend, no parasal
			if(dearSal.equalsIgnoreCase("friend") || dearSal.equalsIgnoreCase("friends")) {
				paraSal = "";
			}
			
			// Start of Segmentation
			boolean criteriaFound = false;

			// loop through the segment plans to segment our records
			for (int j = 0; j < segmentPlanList.size(); ++j) {
				SegmentPlan sp = segmentPlanList.get(j);
				boolean isSeedCriteria = (!sp.getIsBusiness() && !sp.getIsIndividual() && sp.getSegmentPlanFilter1().trim().equals("")
						&& sp.getSegmentPlanFilter2().trim().equals("")) ? true : false;

				// check if the record is a seed
				if (isSeedCriteria) {
					if (!recIsIndividual && !recIsBusiness && spFilt1.trim().isEmpty() && spFilt2.trim().isEmpty()) {
						// System.out.println(nam1 + " " + recIsIndividual + " " + recIsBusiness + " " + sp.getIsBusiness() + " " + sp.getIsIndividual());
						criteriaFound = true;
						isSeed = true;
					} else {
						continue;
					}
				}

				// check if both of our filters match
				// filters will override everything else
				if (sp.getSegmentPlanFilter1().trim().length() > 0 && sp.getSegmentPlanFilter2().trim().length() > 0) {
					if (spFilt1.trim().equalsIgnoreCase(sp.getSegmentPlanFilter1().trim())
							&& spFilt2.trim().equalsIgnoreCase(sp.getSegmentPlanFilter2().trim())) {
						criteriaFound = true;
					} else {
						continue;
					}
				}

				// check if our filter 1 matches
				// filters will override everything else
				if (sp.getSegmentPlanFilter1().trim().length() > 0) {
					if (spFilt1.trim().equalsIgnoreCase(sp.getSegmentPlanFilter1().trim())) {
						criteriaFound = true;
					} else {
						continue;
					}
				}

				// check if our filter 2 matches
				// filters will override everything else
				if (sp.getSegmentPlanFilter2().trim().length() > 0) {
					if (spFilt2.trim().equalsIgnoreCase(sp.getSegmentPlanFilter2().trim())) {
						criteriaFound = true;
					} else {
						continue;
					}
				}
				// System.out.println("checked seeds" + j);

				// if we haven't found the criteria yet
				if (!criteriaFound) {

					if (!sp.getSegmentPlanFilter1().trim().equals(""))
						if (!spFilt1.equals(sp.getSegmentPlanFilter1().trim()))
							continue;

				if (sp.getIsIndividual() && sp.getIsBusiness())
					if (!recIsIndividual && !recIsBusiness)
							continue;

					// check if individuals are part of the criteria
					if (sp.getIsIndividual() && !sp.getIsBusiness())
						if (!recIsIndividual)
							continue;

					// check if business' are part of the criteria
					if (sp.getIsBusiness() && !sp.getIsIndividual())
						if (!recIsBusiness)
							continue;

					// System.out.println("checked biz" + j);
					String giftDateUsed = "";

					// Check which gift date the segment plan says to use
					if (sp.getGiftDateUsed().equalsIgnoreCase("f"))
						giftDateUsed = fDnDat;
					else
						giftDateUsed = lDnDat;

					boolean recordHasDate = (Validators.isValidMDYYYYDate(giftDateUsed)) ? true : false;
					// System.out.println(recordHasDate);
					// System.out.println("checked if record has date" + j);
					// Check if the record meets the segment plan from gift date criteria
					if (!recordHasDate)
						continue;

					SimpleDateFormat sdfrmt = new SimpleDateFormat("M/d/yyyy");
					sdfrmt.setLenient(false);

					Date recordSimpleDate = sdfrmt.parse(giftDateUsed);
					Date spFromSimpleDate = sdfrmt.parse(sp.getFromGiftDate());
					Date spToSimpleDate = sdfrmt.parse(sp.getToGiftDate());

					// System.out.println(recordSimpleDate.toString() + " compared to" + spFromSimpleDate.toString() + " and " + spToSimpleDate.toString());

					if (recordSimpleDate.compareTo(spFromSimpleDate) < 0 || recordSimpleDate.compareTo(spToSimpleDate) > 0)
						continue;
					// System.out.println("Validated gift dates" + j);

					String giftUsed = "";

					// Check which gift date the segment plan says to use
					if (sp.getGiftUsed().equalsIgnoreCase("f"))
						giftUsed = fstDnAmt;
					else
						giftUsed = lstDnAmt;

					giftUsed = giftUsed.replaceAll("[^0-9\\.]", "");

					boolean recordHasGift = (Validators.isNumber(giftUsed)) ? true : false;

					// Check if the record meets the segment plan gift criteria
					if (!recordHasGift)
						continue;

					BigDecimal spFromGiftBigDecimal = new BigDecimal(sp.getFromGift());
					BigDecimal spToGiftBigDecimal = new BigDecimal(sp.getToGift());
					BigDecimal recordGiftBigDecimal = new BigDecimal(giftUsed);

					if (recordGiftBigDecimal.compareTo(spFromGiftBigDecimal) < 0 || recordGiftBigDecimal.compareTo(spToGiftBigDecimal) > 0)
						continue;
					// System.out.println("Validated gifts" + j);

					dnNum = dnNum.replaceAll("[^0-9\\.]", "");

					if(!Validators.isNumber(dnNum))
						dnNum = "0";

					BigDecimal dnNumAsBigDecimal = new BigDecimal(dnNum);
					BigDecimal spFromNumOfGiftsBigDecimal = new BigDecimal(sp.getFromNumOfGifts());
					BigDecimal spToNumOfGiftsBigDecimal = new BigDecimal(sp.getToNumOfGifts());

					if (dnNumAsBigDecimal.compareTo(spFromNumOfGiftsBigDecimal) < 0 || dnNumAsBigDecimal.compareTo(spToNumOfGiftsBigDecimal) > 0)
						continue;
					// System.out.println("Validated number of gifts" + j);

				}

				// System.out.println("We met the criteria" + j);
				/*
				 * Record now meets the segment criteria
				 */

				criteriaFound = true;

				// Check if the segment plan calls for the standard gift array matrix to be used
				if (sp.getIsStandardAsk() || sp.getIsActiveAsk()) {


					// Handles the standard gift array matrix
					if (sp.getIsStandardAsk()) {		
						
						lstDnAmt = lstDnAmt.replaceAll("[^0-9\\.]", ""); 
						Double lstDnToCheck = (Validators.isNumber(lstDnAmt)) ? Double.valueOf(lstDnAmt) : 0.00;// Don't use the rounded ask when verifying amount
						DecimalFormat formatter = new DecimalFormat("#,###.00");
						String formattedLastDonation = formatter.format(lstDnToCheck).replaceAll("\\.00", "");
						
						boolean hasInvalidLastGift = false;
						
						if (!Validators.isNumber(lstDnAmt))
							hasInvalidLastGift = true;
						
						BigDecimal newLastGiftAmt = new BigDecimal(lstDnToCheck).setScale(0, RoundingMode.CEILING).divide(new BigDecimal("5"), 0, RoundingMode.UP).multiply(new BigDecimal("5"));

						//BigDecimal newLastGiftAmt = new BigDecimal(lstDnToCheck).divide(LAST_GIFT_ROUNDING_AMOUNT, 2, RoundingMode.CEILING)
								//.setScale(0, RoundingMode.CEILING).multiply(LAST_GIFT_ROUNDING_AMOUNT);
						
							if (hasInvalidLastGift) {
								dn1Amt = "";
								dn2Amt = "";
								dn3Amt = "";
								oDnAmt = "Other $ _________";
							} else if (lstDnToCheck < 1000.00) {
								dn1Amt = newLastGiftAmt.toPlainString();
								dn2Amt = newLastGiftAmt.multiply(new BigDecimal("1.1")).setScale(0, RoundingMode.CEILING).divide(new BigDecimal("5"), 0, RoundingMode.UP).multiply(new BigDecimal("5")).toPlainString();
								dn3Amt = newLastGiftAmt.multiply(new BigDecimal("1.25")).setScale(0, RoundingMode.CEILING).divide(new BigDecimal("5"), 0, RoundingMode.UP).multiply(new BigDecimal("5")).toPlainString();
								oDnAmt = "Other $ _________";
							//} else if (isRecord2YearsOrOlder) {
							//	dn1Amt = "$ _________";
							//	dn2Amt = "You kindly supported us with a gift of $" + formattedLastDonation +" previously.";
							//	dn3Amt = "";
							//	oDnAmt = "";
							} else {
								dn1Amt = "";
								dn2Amt = "";
								dn3Amt = "";
								oDnAmt = "$ _________";
							}
						
					}

					if (sp.getIsActiveAsk()) {
						
						lstDnAmt = lstDnAmt.replaceAll("[^0-9\\.]", ""); 
						Double lstDnToCheck = (Validators.isNumber(lstDnAmt)) ? Double.valueOf(lstDnAmt) : 0.00;// Don't use the rounded ask when verifying amount
						DecimalFormat formatter = new DecimalFormat("#,###.00");
						String formattedLastDonation = formatter.format(lstDnToCheck).replaceAll("\\.00", "");
						
						boolean hasInvalidLastGift = false;
						
						if (!Validators.isNumber(lstDnAmt))
							hasInvalidLastGift = true;

						BigDecimal newLastGiftAmt = new BigDecimal(lstDnToCheck).setScale(0, RoundingMode.CEILING).divide(new BigDecimal("5"), 0, RoundingMode.UP).multiply(new BigDecimal("5"));

						//BigDecimal newLastGiftAmt = new BigDecimal(lstDnToCheck).divide(LAST_GIFT_ROUNDING_AMOUNT, 2, RoundingMode.CEILING)
								//.setScale(0, RoundingMode.CEILING).multiply(LAST_GIFT_ROUNDING_AMOUNT);
						
							if (hasInvalidLastGift) {
								dn1Amt = "";
								dn2Amt = "";
								dn3Amt = "";
								oDnAmt = "Other $ _________";
							} else if (lstDnToCheck < 1000.00) {
								dn1Amt = newLastGiftAmt.toPlainString();
								dn2Amt = newLastGiftAmt.multiply(new BigDecimal("1.1")).setScale(0, RoundingMode.CEILING).divide(new BigDecimal("5"), 0, RoundingMode.UP).multiply(new BigDecimal("5")).toPlainString();
								dn3Amt = newLastGiftAmt.multiply(new BigDecimal("1.25")).setScale(0, RoundingMode.CEILING).divide(new BigDecimal("5"), 0, RoundingMode.UP).multiply(new BigDecimal("5")).toPlainString();
								oDnAmt = "Other $ _________";
							//} else if (isRecord2YearsOrOlder) {
							//	dn1Amt = "$ _________";
							//	dn2Amt = "You kindly supported us with a gift of $" + formattedLastDonation +" previously.";
							//	dn3Amt = "";
							//	oDnAmt = "";
							} else {
								dn1Amt = "";
								dn2Amt = "";
								dn3Amt = "";
								oDnAmt = "$ _________";
							}

					}

				} else { // otherwise use the gift matrix from the segment plan
					dn1Amt = sp.getStaticAsk1();
					dn2Amt = sp.getStaticAsk2();
					dn3Amt = sp.getStaticAsk3();
					oDnAmt = sp.getAskOpen();
				}

				// Check if the segment plan calls for the standard monthly gift array matrix to be used
				if (sp.getIsStandardMonthlyAsk()) {
					final BigDecimal DEFAULT_AMOUNT = new BigDecimal("0.00");

					lstDnAmt = lstDnAmt.replaceAll("[^0-9\\.]", "");

					if (lstDnAmt.equals("") || lstDnAmt.equals("."))
						lstDnAmt = DEFAULT_AMOUNT.toPlainString();

					BigDecimal newLastGiftAmt = new BigDecimal(lstDnAmt);

					if (newLastGiftAmt.doubleValue() < 100.00) {
						mDn1Amt = "5";
						mDn2Amt = "10";
						mDn3Amt = "15";
						mODnAmt = "Other $ _________";
					} else if (newLastGiftAmt.doubleValue() < 400.00) {
						mDn1Amt = "10";
						mDn2Amt = "15";
						mDn3Amt = "20";
						mODnAmt = "Other $ _________";
					} else if (newLastGiftAmt.doubleValue() < 1000.00) {
						mDn1Amt = "25";
						mDn2Amt = "55";
						mDn3Amt = "84";
						mODnAmt = "Other $ _________";
					} else if (newLastGiftAmt.doubleValue() < 2000.00) {
						mDn1Amt = "84";
						mDn2Amt = "100";
						mDn3Amt = "120";
						mODnAmt = "Other $ _________";
					} else {
						mDn1Amt = "";
						mDn2Amt = "";
						mDn3Amt = "";
						mODnAmt = "$ _________";
					}
				} else if (sp.getIsActiveMonthlyAsk()) {
					final BigDecimal DEFAULT_AMOUNT = new BigDecimal("0.00");

					lstDnAmt = lstDnAmt.replaceAll("[^0-9\\.]", "");

					if (lstDnAmt.equals("") || lstDnAmt.equals("."))
						lstDnAmt = DEFAULT_AMOUNT.toPlainString();

					BigDecimal newLastGiftAmt = new BigDecimal(lstDnAmt);

					if (newLastGiftAmt.doubleValue() < 100.00) {
						mDn1Amt = "5";
						mDn2Amt = "10";
						mDn3Amt = "15";
						mODnAmt = "Other $ _________";
					} else if (newLastGiftAmt.doubleValue() < 400.00) {
						mDn1Amt = "10";
						mDn2Amt = "15";
						mDn3Amt = "20";
						mODnAmt = "Other $ _________";
					} else if (newLastGiftAmt.doubleValue() < 1000.00) {
						mDn1Amt = "25";
						mDn2Amt = "55";
						mDn3Amt = "84";
						mODnAmt = "Other $ _________";
					} else if (newLastGiftAmt.doubleValue() < 2000.00) {
						mDn1Amt = "84";
						mDn2Amt = "100";
						mDn3Amt = "120";
						mODnAmt = "Other $ _________";
					} else {
						mDn1Amt = "";
						mDn2Amt = "";
						mDn3Amt = "";
						mODnAmt = "$ _________";
					}

				} else {
					mDn1Amt = sp.getStaticMonthlyAsk1();
					mDn2Amt = sp.getStaticMonthlyAsk2();
					mDn3Amt = sp.getStaticMonthlyAsk3();
					mODnAmt = sp.getMonthlyAskOpen();
				}

				priority = String.valueOf(sp.getPriority());
				segCode = sp.getSegmentCode();
				segName = sp.getSegmentName();
				letVer = sp.getLetterVersion();
				pkgVer = sp.getPackageVersion();
				repVer = sp.getReplyVersion();
				break;

			}
			// End of Segmentation
			
			// If donation equals another donation and is not blank, increment donation by 5 dollars
		    if (!dn2Amt.isEmpty() && dn2Amt.equals(dn1Amt))
		    	dn2Amt = String.valueOf(Double.valueOf(dn2Amt) + 5);
		    
		    if (!dn3Amt.isEmpty() && dn3Amt.equals(dn2Amt))
		    	dn3Amt = String.valueOf(Double.valueOf(dn3Amt) + 5);
			
		    Record record = new Record.Builder(i, data[i], "", "", "").setNam1(nam1).setNam1_2("").setFstName(fnam)
					.setDearSal(dearSal).setParaSal(paraSal).setFstDnDat(fDnDat).setLstDnDat(lDnDat).setFstDnAmt(fstDnAmt)
					.setLstDnAmt(lstDnAmt).setRecType(recType).setSpFilt1(spFilt1).setPriority(priority).setSegCode(segCode).setSeg(segName)
					.setLetVer(letVer).setPkgVer(pkgVer).setRepVer(repVer).setDn1Amt(dn1Amt).setDn2Amt(dn2Amt).setDn3Amt(dn3Amt).setODnAmt(oDnAmt)
					.setMDn1Amt(mDn1Amt).setMDn2Amt(mDn2Amt).setMdDn3Amt(mDn3Amt).setMODnAmt(mODnAmt).build();

			if(!recIsBusiness)
				record = Common.splitAddName(record, 36, prefixesForNameSplitting);
			
			userData.add(record);
			
			// will need to get the segment name of how far we made
			// then get all the segment plans that match that name
			// sort by priority ascending, the first element is the one we will assign to
			if (!criteriaFound)
				unsegmentedRecordList.add(record);
		}
		
		// let the user know if we couldn't segment the records for whatever the reason may be
		if (unsegmentedRecordList.size() > 0) {
			JOptionPane.showMessageDialog(UiController.getMainFrame(), String.format(
					"%d records did not meet the segmentation criteria.\nPlease review the plan or manually segment the %d records.\n These records will have a blank value in %s.",
					unsegmentedRecordList.size(), unsegmentedRecordList.size(), UserData.fieldName.SEGMENT_CODE.getName()), "Unsegemented Records Found",
					JOptionPane.INFORMATION_MESSAGE);
		}

		// Create the data summary report in excel
		SegmentationReport.writeHMASegmentationReport(originalSegmentPlanList, userData.getRecordList());

		// Create the gift report in excel
		GiftReport.writeHMAGiftReport(userData.getRecordList());

		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.NAME1.getName(), UserData.fieldName.NAME1_2.getName(), UserData.fieldName.DEAR_SALUTATION.getName(), UserData.fieldName.PARAGRAPH_SALUTATION.getName(), UserData.fieldName.PRIORITY.getName(),
				UserData.fieldName.SEGMENT_CODE.getName(), UserData.fieldName.SEGMENT.getName(), UserData.fieldName.LETTER_VERSION.getName(),
				UserData.fieldName.PACKAGE_VERSION.getName(), UserData.fieldName.REPLY_VERSION.getName(), UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(), UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.OPEN_DONATION_AMOUNT.getName(), UserData.fieldName.MONTHLY_DONATION1_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_DONATION2_AMOUNT.getName(), UserData.fieldName.MONTHLY_DONATION3_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName()
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