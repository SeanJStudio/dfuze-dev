/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.generosityx;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.jobs.generosityx.AdraUkraine.segment;
import com.mom.dfuze.data.util.Analyze;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.DateTimeInferer;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.io.FileIngestor;
import com.mom.dfuze.ui.DropdownSelectDialog;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserDecimalInputDialog;
import com.mom.dfuze.ui.UserInputDialog;
import com.mom.dfuze.ui.job.DataMappingDialog;


/**
 * RegularProcess implements a RunBehavior for Wildlife Rescue Association Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 07/10/2023
 *
 */
public class WildlifeRescueAssociation implements RunGenerosityXBehavior {

	private final String BEHAVIOR_NAME = "Wildlife Rescue Association";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.IN_ID.getName()
	};
	
	private String DESCRIPTION = 
					"<html>"
					+ "Description<br/>"
					+ "<ul>"
					+ "<li>RFM analysis is used to segment donors</li>"
					+ "</ul>"
					+ "Instructions<br/>"
					+ "<ol>"
					+ "<li>Load the supplied data file</li>"
					+ "<li>Map Reference Number to the " + getIdName() + " field.</li>"
					//+ "<li>Map record type to the monthly identifier (Recurring donor status)</li>"
					+ "<li>Enter the campaign code when prompted</li>"
					+ "<li>Enter the cost per unit metric when prompted</li>"
					+ "<li>Enter gift metrics for &lt;1, ==1, &gt;1, and open when prompted</li>"
					+ "</ol>"

					+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
					+ "</html>";
	
	public final Pattern TOP_PATTERN = Pattern.compile("(2|3|4|5)_(2|3|4|5)_(4|5)", Pattern.CASE_INSENSITIVE);
	public final Pattern FREQUENT_PATTERN = Pattern.compile("(4|5)_(4|5)_(1|2)", Pattern.CASE_INSENSITIVE);
	public final Pattern NEW_PATTERN = Pattern.compile("(4|5)_(1|2|3|4|5)_(1|2|3|4|5)", Pattern.CASE_INSENSITIVE);
	public final Pattern LAPSED_PATTERN = Pattern.compile("(1)_(1|2|3|4|5)_(1|2|3|4|5)", Pattern.CASE_INSENSITIVE);
	
	public final Pattern GIFT_FILE_ID_PATTERN = Pattern.compile("(^|\\s+)reference(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_AMOUNT_PATTERN = Pattern.compile("(^|\\s+)amount(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_DATE_PATTERN = Pattern.compile("(^|\\s+)date(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_CAMPAIGN_PATTERN = Pattern.compile("campaign", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_RECURRING_PATTERN = Pattern.compile("Primary GAU", Pattern.CASE_INSENSITIVE);
	
	public final Pattern MONTHLY_DONOR_PATTERN = Pattern.compile("recurring", Pattern.CASE_INSENSITIVE);
	public final Pattern FINDER_DONOR_PATTERN = Pattern.compile("finder", Pattern.CASE_INSENSITIVE);
	

	public enum segment {
		HVD("HVD"),
		MLD("MLD"),
		LEGACY("Legacy Prospect"),
		MONTHLY("Monthly"),
		FREQUENT("Frequent"),
		ACTIVE_GENERAL("Active General"),
		ACTIVE_DM("Active DM"),
		GENERAL_DM("General DM"),
		GENERAL("General"),
		NEW("New"),
		LAPSED("Lapsed"),
		FINDER("Finders");
		
		String name;

		private segment(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	};
	
	//
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		// add the records to the userData list
		userData.autoSetRecordList();
		
		// set record members via required fields
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		
		// Remove leading zeros to match Giving History
		removeLeadingZeroFromDataId(userData);
		
		// create the full name field
		//setName(userData);
		
		// create the salutation
		//setSalutation(userData);
		
		// Add the seeds
		//addSeeds(userData);
		
		// cleanup the date
		//cleanDonationDates(userData);
		
		// load gifts file as record list,	infer gift date format to use
		List<Record> giftList = getGiftList(); // dfInId, dfLstDnAmt, dfLstGftDat, dfSeg is set
		String giftDateFormat = DateTimeInferer.inferFormat(giftList, UserData.fieldName.LAST_DONATION_DATE.getName());
				
		// Sort the gift list by latest donation
		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(giftDateFormat);
		giftList.sort(new RecordSorters.CompareByFieldDescAsDate(UserData.fieldName.LAST_DONATION_DATE.getName(), datetimeFormatter));
				
		// Convert the gifts into a gift history map (id, List(history))
		HashMap<String, List<WRAGiftHistory>> giftHistoryMap = convertGiftsToMap(giftList, giftDateFormat);
		
		// process gift history into main donor list
		processGifts(userData, giftHistoryMap);
		
		// build RFM score
		setRFM(userData);
		
		//min max rfm
		Analyze.minMaxRFM(userData);
		
		// Place into categories
		setSegment(userData);

		// Set the campaign code
		setCampaignCode(userData, getCampaignCode());

		double defaultAskAmount = getCostPerUnit();
		// Set the gift amounts
		setGiftArrays(userData, defaultAskAmount);

		// Are metrics needed?
		int hasGiftMetricsButtonPressed = JOptionPane.showConfirmDialog(UiController.getMainFrame(),
				"Does this campaign require gift metrics?\n\nEx. $500 provides a week of groceries for 5 families",
				"Gift Calculations", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if(hasGiftMetricsButtonPressed == JOptionPane.YES_OPTION) {

			// Are the gifts going to be matched? (doubled)
			int isMatchCampaign = JOptionPane.showConfirmDialog(UiController.getMainFrame(), "Is this a doubling match campaign?\n\nEx. $5 DOUBLES to $10",
					"Doubling Match Campaign", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			UiController.displayMessage("Warning", "Slowly answer the following prompts as you can't go back!", JOptionPane.WARNING_MESSAGE);

			if(isMatchCampaign == JOptionPane.YES_OPTION)
				setMatchedGiftArrayMetrics(userData, defaultAskAmount);
			else
				setGiftArrayMetrics(userData, defaultAskAmount);
		}		


		// value priority, high = good
		Analyze.prioritizeRFM(userData);
		
		formatAmounts(userData);
		
		userData.getRecordList().sort(new RecordSorters.CompareByFieldDescAsNumber(UserData.fieldName.PRIORITY.getName()));

		userData.setDfHeaders(new String[] {
				//UserData.fieldName.NAME1.getName(),
				//UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
				UserData.fieldName.LAST_DONATION_DATE.getName(),
				UserData.fieldName.FIRST_DONATION_AMOUNT.getName(),
				UserData.fieldName.FIRST_DONATION_DATE.getName(),
				UserData.fieldName.TOTAL_DONATION_AMOUNT.getName(),
				UserData.fieldName.TOTAL_DONATION_AMOUNT_LAST_12_MONTHS.getName(),
				UserData.fieldName.NUMBER_OF_DONATIONS.getName(),
				UserData.fieldName.NUMBER_OF_DONATIONS_LAST_12_MONTHS.getName(),
				UserData.fieldName.LARGEST_DONATION_AMOUNT.getName(),
				UserData.fieldName.DONATION_AMOUNT_ARRAY.getName(),
				UserData.fieldName.RECENCY.getName(),
				UserData.fieldName.FREQUENCY.getName(),
				UserData.fieldName.MONETARY.getName(),
				UserData.fieldName.RFM.getName(),
				UserData.fieldName.SEGMENT.getName(),
				UserData.fieldName.SEGMENT_CODE.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.DONATION4_AMOUNT.getName(),
				UserData.fieldName.PROVIDE1.getName(),
				UserData.fieldName.PROVIDE2.getName(),
				UserData.fieldName.PROVIDE3.getName(),
				UserData.fieldName.PROVIDE4.getName(),
				UserData.fieldName.OPEN_DONATION_AMOUNT.getName(),
				UserData.fieldName.PRIORITY.getName()
		});
		
		//removeZeroGiftRecords(userData);
	}
	
	private void formatAmounts(UserData userData) {
		DecimalFormat formatter = new DecimalFormat("$#,###.00");//formatter.format(dn1).replaceAll("\\.0+$", "")
		for(Record record : userData.getRecordList()) {
			record.setLstDnAmt(formatter.format(Double.parseDouble(record.getLstDnAmt())).replaceAll("\\.0+$", ""));
			record.setFstDnAmt(formatter.format(Double.parseDouble(record.getFstDnAmt())).replaceAll("\\.0+$", ""));
			record.setTtlDnAmt(formatter.format(Double.parseDouble(record.getTtlDnAmt())).replaceAll("\\.0+$", ""));
			record.setTtlDnAmtLst12Mnths(formatter.format(Double.parseDouble(record.getTtlDnAmtLst12Mnths())).replaceAll("\\.0+$", ""));
			record.setLrgDnAmt(formatter.format(Double.parseDouble(record.getLrgDnAmt())).replaceAll("\\.0+$", ""));
			
			if(record.getLstDnAmt().equals("$"))
				record.setLstDnAmt("$0");
			
			if(record.getFstDnAmt().equals("$"))
				record.setFstDnAmt("$0");
			
			if(record.getTtlDnAmt().equals("$"))
				record.setTtlDnAmt("$0");
			
			if(record.getTtlDnAmtLst12Mnths().equals("$"))
				record.setTtlDnAmtLst12Mnths("$0");
			
			if(record.getLrgDnAmt().equals("$"))
				record.setLrgDnAmt("$0");
			
			if(record.getDn1Amt().length() > 0) {
				record.setDn1Amt(formatter.format(Double.parseDouble(record.getDn1Amt())).replaceAll("\\.0+$", ""));
				record.setDn2Amt(formatter.format(Double.parseDouble(record.getDn2Amt())).replaceAll("\\.0+$", ""));
				record.setDn3Amt(formatter.format(Double.parseDouble(record.getDn3Amt())).replaceAll("\\.0+$", ""));
				record.setDn4Amt(formatter.format(Double.parseDouble(record.getDn4Amt())).replaceAll("\\.0+$", ""));
			}
		}
	}
	
	private String getIdName() {
		String idField = UserData.fieldName.IN_ID.getName();
		try {
			
			return DataMappingDialog.getLabelNameFromDfuzeFieldName(idField);
		} catch(Exception e) {
			return idField;
		}
	}
	
	private void removeLeadingZeroFromDataId(UserData userData) {
		for(Record record : userData.getRecordList())
				record.setInId(record.getInId().replaceFirst("^0+", ""));
	}
	
	private List<Record> getGiftList() throws Exception {
		JOptionPane.showMessageDialog(
				UiController.getMainFrame(),
				"Please load the associated gift file now.",
				"Load gifts",
				JOptionPane.INFORMATION_MESSAGE);
		
		List<List<String>> giftFile = FileIngestor.ingest();	// get the gift file	
		HashSet<Integer> indexSet = new HashSet<>();
	
		int idIndex, amountIndex, giftDateIndex, campaignNameIndex, recurringStatusIndex = -1;
		
		String[] headers = giftFile.get(0).toArray(new String[0]);

		//id
		DropdownSelectDialog dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the Donor ID field.");

		for(int i = 0; i < headers.length; ++i) {
			if(GIFT_FILE_ID_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		idIndex = dsd.getSelectedValueIndex();
		
		if(!indexSet.add(idIndex))
			throw new Exception("The id field has been mapped more than once.");

		// gift amount
		dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the Gift Amount field.");
		dsd.setValues(headers);

		for(int i = 0; i < headers.length; ++i) {
			if(GIFT_FILE_AMOUNT_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		amountIndex = dsd.getSelectedValueIndex();
		
		if(!indexSet.add(amountIndex))
			throw new Exception("The gift amount field has been mapped more than once.");
		
		// gift date
		dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the Gift Date field.");
		dsd.setValues(headers);

		for(int i = 0; i < headers.length; ++i) {
			if(GIFT_FILE_DATE_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		giftDateIndex = dsd.getSelectedValueIndex();
		
		if(!indexSet.add(giftDateIndex))
			throw new Exception("The gift date field has been mapped more than once.");
		
		// campaign
		dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the Campaign Name field.");
		dsd.setValues(headers);

		for(int i = 0; i < headers.length; ++i) {
			if(GIFT_FILE_CAMPAIGN_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		campaignNameIndex = dsd.getSelectedValueIndex();
		
		if(!indexSet.add(campaignNameIndex))
			throw new Exception("The campaign name field has been mapped more than once.");
		
		// recurringStatus
		dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the recurring GAU field.");
		dsd.setValues(headers);

		for(int i = 0; i < headers.length; ++i) {
			if(GIFT_FILE_RECURRING_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		recurringStatusIndex = dsd.getSelectedValueIndex();
				
		if(!indexSet.add(recurringStatusIndex))
			throw new Exception("The recurring GAU field has been mapped more than once.");
		
		List<Record> giftsList = new ArrayList<>();
		
		giftFile.remove(0); // remove header row
		
		for(int i = 0; i < giftFile.size(); ++i) {
			String id = giftFile.get(i).get(idIndex).replaceAll("[^a-zA-Z0-9_]", "").replaceFirst("^0+", ""); // Remove leading zeros
			String giftAmount = giftFile.get(i).get(amountIndex).replaceAll("[^0-9\\.-]", "");
			String giftDate = giftFile.get(i).get(giftDateIndex).replaceAll("\\d+:.*$", "").trim().replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/").trim();
			String giftDesignation = giftFile.get(i).get(campaignNameIndex).trim();
			String recurringStatus = giftFile.get(i).get(recurringStatusIndex).trim();
			
			boolean isGiftZero = false;
			
			if(Validators.isNumber(giftAmount)) {
				BigDecimal giftAmountBD = new BigDecimal(giftAmount);
				if (giftAmountBD.compareTo(BigDecimal.ZERO) == 0)
					isGiftZero = true;
			} else {
				isGiftZero = true;
			}
			
			Record record = new Record.Builder(i, giftFile.get(i).toArray(new String[0]), "", "", "")
					.setInId(id)
					.setLstDnAmt(giftAmount)
					.setLstDnDat(giftDate)
					.setSeg(giftDesignation)
					.setStatus(recurringStatus)
					.build();
			
			if(giftAmount.contains("-")) // Skip negative dollar amounts
				System.out.println("Skipping negative amount of: " + giftAmount);
			else if(giftAmount.length() == 0 || isGiftZero) // Skip empty gifts
				System.out.println("Skipping empty amount");
			else
				giftsList.add(record);
		}
		
		return giftsList;
	}

	// converts a list of gifts into a HashMap
	private HashMap<String, List<WRAGiftHistory>> convertGiftsToMap(List<Record> giftList, String giftDateFormat) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(giftDateFormat);

		// id, giftHistory
		HashMap<String, List<WRAGiftHistory>> giftHistoryMap = new HashMap<>();

		for(Record record : giftList) {
			double giftAmount = (Validators.isNumber(record.getLstDnAmt())) ? Double.parseDouble(record.getLstDnAmt()) : 0.0;

			if(Validators.isStringOfDateFormat(record.getLstDnDat(), giftDateFormat)) {
				//System.out.println(record.getLstDnDat() + " vs " + giftDateFormat);
				LocalDate giftDate = LocalDate.parse(record.getLstDnDat(), formatter);
				//System.out.println(giftDate.toString());
				WRAGiftHistory history = new WRAGiftHistory(
						record.getInId(),
						giftAmount,
						giftDate,
						record.getSeg(),
						record.getStatus()
						);

				if(!giftHistoryMap.containsKey(record.getInId()))
					giftHistoryMap.put(record.getInId(), new ArrayList<WRAGiftHistory>());

				giftHistoryMap.get(record.getInId()).add(history);
			}
		}

		return giftHistoryMap;
	}
	
	private void processGifts(UserData userData, HashMap<String, List<WRAGiftHistory>> giftHistoryMap) {
		final int MLD_AMOUNT = 500;
		final int HVD_AMOUNT = 1000;
		final int MONTHS6 = 6;
		final int MONTHS12 = 12;
		final int MONTHS24 = 24;
		final int MONTHS36 = 36;
		int currentYear = Year.now().getValue();
		
		String now = String.valueOf(LocalDate.now());
		
		for(int i = 0; i < userData.getRecordList().size(); ++i) {
			Record record = userData.getRecordList().get(i);
			
			if(!giftHistoryMap.containsKey(record.getInId())) {
				System.out.println("No ID for " + record.getInId());
				record.setLstDnAmt("0.0");
				record.setLstDnDat("1900-01-01");
				record.setFstDnAmt("0.0");
				record.setFstDnDat("1900-01-01");
				record.setTtlDnAmt("0.0");
				record.setTtlDnAmtLst12Mnths("0");
				record.setNumDn("0");
				record.setNumDnLst12Mnths("0");
				record.setLrgDnAmt("0"); // largest donation amount in last 2 years;
				record.setDnAmtArr("");
				record.setRScore("99999");
				record.setFScore("0");
				record.setMScore("0");
				record.setStatus("");
				//record.setYear("0"); // Using this to hold the total donation amount of last 6 months
			}
			
			if(giftHistoryMap.containsKey(record.getInId())) {
				List<WRAGiftHistory> giftHistoryList = giftHistoryMap.get(record.getInId());
				
				double totalGiftAmount = 0.0;
				int totalGifts = giftHistoryList.size();
				
				double totalGiftAmountLast6Months = 0.0;
				double totalGiftAmountLast12Months = 0.0;
				int totalGiftsLast12Months = 0;
				double largestGiftMade = 0.0;
				double largestGiftMadeLast24Months = 0.0;
				double largestGiftMadeLast36Months = 0.0;
				double largestGiftCalendarYear = 0.0;
				
				ArrayList<Double> giftAmounts = new ArrayList<>();
				
				String commaSeparatedHistory = giftHistoryList
				        .stream()
				        .map(String::valueOf)
				        .collect(Collectors.joining(","));
				
				for(int j = 0; j < giftHistoryList.size(); ++j) {
					WRAGiftHistory giftHistory = giftHistoryList.get(j);
					
					totalGiftAmount += giftHistory.getGiftAmount();
					giftAmounts.add(giftHistory.getGiftAmount());
					
					int giftYear = giftHistory.getGiftDate().getYear();
					long monthsFromDonation = getMonthsBetween(giftHistory.getGiftDate().toString(), now);
					
					// Largest gift in calendar year
					if(giftYear == currentYear)
						if(giftHistory.getGiftAmount() > largestGiftCalendarYear)
							largestGiftCalendarYear = giftHistory.getGiftAmount();
					
					if(giftHistory.getGiftAmount() > largestGiftMade)
						largestGiftMade = giftHistory.getGiftAmount();
					
					if(monthsFromDonation <= MONTHS6 && monthsFromDonation > -1)
						totalGiftAmountLast6Months += giftHistory.getGiftAmount();
					
					if(monthsFromDonation <= MONTHS12 && monthsFromDonation > -1) {
						++totalGiftsLast12Months;
						totalGiftAmountLast12Months += giftHistory.getGiftAmount();
					}
					
					if(monthsFromDonation <= MONTHS24 && monthsFromDonation > -1)
						if(giftHistory.getGiftAmount() > largestGiftMadeLast24Months)
							largestGiftMadeLast24Months = giftHistory.getGiftAmount();
					
					if(monthsFromDonation <= MONTHS36 && monthsFromDonation > -1)
						if(giftHistory.getGiftAmount() > largestGiftMadeLast36Months)
							largestGiftMadeLast36Months = giftHistory.getGiftAmount();
					
					long daysBetween = ChronoUnit.DAYS.between(giftHistory.getGiftDate(), LocalDate.now());
					
					// identify monthly donors if last gift is within 60 days and monthly id found in appeal
					if(daysBetween <= 60) {
						Matcher monthlyMatcher = MONTHLY_DONOR_PATTERN.matcher(giftHistory.getRecurringStatus());
						if(monthlyMatcher.find())
							record.setSeg(segment.MONTHLY.getName());
					}
					
					// Identify finders if last gift is within 90 days
					//if(daysBetween <= 90) {
					//	Matcher finderMatcher = FINDER_DONOR_PATTERN.matcher(giftHistory.getGiftCampaign());
					//	if(finderMatcher.find())
					//		record.setStatus(segment.FINDER.getName());
					//}
					
					// This is the last donation made
					if(j == 0) {
						record.setLstDnAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setLstDnDat(giftHistory.getGiftDate().toString());
						record.setRScore(String.valueOf(daysBetween));
					}
					
					// This is the first donation made
					if(j == giftHistoryList.size() - 1) {
						record.setFstDnAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setFstDnDat(giftHistory.getGiftDate().toString());
					}

				}
				
				double monetarySum = giftAmounts.stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();
				
				if(largestGiftCalendarYear >= HVD_AMOUNT)
					record.setSeg(segment.HVD.getName());
				else if(largestGiftMadeLast36Months >= MLD_AMOUNT)
					record.setSeg(segment.MLD.getName());
				
				record.setMScore(String.valueOf(monetarySum));
				record.setFScore(String.valueOf(totalGifts));
				record.setTtlDnAmt(String.valueOf(totalGiftAmount));
				record.setTtlDnAmtLst12Mnths(String.valueOf(totalGiftAmountLast12Months));
				record.setNumDn(String.valueOf(totalGifts));
				record.setLrgDnAmt(String.valueOf(largestGiftMade));
				record.setDnAmtArr(commaSeparatedHistory);
				record.setNumDnLst12Mnths(String.valueOf(totalGiftsLast12Months)); // Using this to hold the number of gifts of last 12 months
			}
			
			
		}
	}

	private void setCampaignCode(UserData userData, String campaignCode) {
		for(Record record : userData.getRecordList()) {
			if(record.getSeg().equalsIgnoreCase(segment.HVD.getName()) ||
				record.getSeg().equalsIgnoreCase(segment.MLD.getName()) ||
				record.getSeg().equalsIgnoreCase(segment.LEGACY.getName()))
				record.setSegCode(campaignCode + "-T1");
			else if(record.getSeg().equalsIgnoreCase(segment.MONTHLY.getName()))
				record.setSegCode(campaignCode + "-M1");
			else if(record.getSeg().equalsIgnoreCase(segment.FREQUENT.getName()))
				record.setSegCode(campaignCode + "-F0");
			else if(record.getSeg().equalsIgnoreCase(segment.ACTIVE_GENERAL.getName()))
				record.setSegCode(campaignCode + "-A");
			else if(record.getSeg().equalsIgnoreCase(segment.ACTIVE_DM.getName()) ||
				record.getSeg().equalsIgnoreCase(segment.GENERAL_DM.getName()) ||
				record.getSeg().equalsIgnoreCase(segment.GENERAL.getName()))
				record.setSegCode(campaignCode + "-A");
			else if(record.getSeg().equalsIgnoreCase(segment.NEW.getName()))
				record.setSegCode(campaignCode + "-N0");
			else if(record.getSeg().equalsIgnoreCase(segment.LAPSED.getName()))
				record.setSegCode(campaignCode + "-L0");
			else if(record.getSeg().equalsIgnoreCase(segment.FINDER.getName()))
				record.setSegCode(campaignCode + "-F9");
		}
	}
	
	// Prompt the user for the donation metric line in asks
	private String getCampaignCode() {
		UserInputDialog uid = new UserInputDialog(UiController.getMainFrame(), "Enter the campaign code. (Ex. M251101)");
		uid.getTextField().setText("M251101");
		uid.setVisible(true);

		if(uid.getIsNextPressed())
			return uid.getUserInput().toUpperCase().trim();

		return "";
	}
	
	// Prompt the user for the cost per unit for donation metrics
	private double getCostPerUnit() {
		UserDecimalInputDialog udid = new UserDecimalInputDialog(UiController.getMainFrame(), "Enter the gift metric unit cost. (Ex if 1 unit costs $30, enter 30)");
		udid.getTextField().setText("30");
		udid.setVisible(true);
		
		double costPerUnit = 1.0;
		
		if(udid.getIsNextPressed())
			costPerUnit = udid.getUserInput();
		
		return costPerUnit;
	}
	
	// Prompt the user for the donation metric line in gift asks
	private String getGiftMetricLine(String description, String inputLine) {
		UserInputDialog uid = new UserInputDialog(UiController.getMainFrame(), description);
		uid.getTextField().setText(inputLine);
		uid.setVisible(true);
		
		if(uid.getIsNextPressed())
			return uid.getUserInput();
		
		return "";
	}
	
	// Convert the gift arrays to metrics
	private void setGiftArrayMetrics(UserData userData, double costPerUnit) {
		
		String less = "$%s helps provide 1 day of medical treatment for an animal in need.";
		String single = "$%s provides 1 day of medical treatment for an animal in need.";
		String plural = "$%s provides %s days of medical treatment for an animal in need.";
		String open = "$________ to provide medical treatment for as many animals as possible.";
		
		String formattedCostPerUnit = String.valueOf(costPerUnit).replaceAll("\\.0+$", "");
		
		less = getGiftMetricLine("Enter the sentence to use when asks are < $" + formattedCostPerUnit, less).trim();
		single = getGiftMetricLine("Enter the sentence to use when asks are = $" + formattedCostPerUnit, single).trim();
		plural = getGiftMetricLine("Enter the sentence to use when asks are > $" + formattedCostPerUnit, plural).trim();
		open = getGiftMetricLine("Enter the sentence to use for open asks.", open).trim();
		
		DecimalFormat formatter = new DecimalFormat("#,###.00");
		DecimalFormat provides_formatter = new DecimalFormat("#,###");
		
		for(Record record : userData.getRecordList()) {
			if(Validators.isNumber(record.getDn1Amt())) {
				double dn1 = Double.parseDouble(record.getDn1Amt());
				double dn2 = Double.parseDouble(record.getDn2Amt());
				double dn3 = Double.parseDouble(record.getDn3Amt());
				double dn4 = Double.parseDouble(record.getDn4Amt());
				int difference1 = (int) (dn1 / costPerUnit);
				int difference2 = (int) (dn2 / costPerUnit);
				int difference3 = (int) (dn3 / costPerUnit);
				int difference4 = (int) (dn4 / costPerUnit);
				
				// Donation 1
				if(difference1 < 1) {
					record.setProvide1(String.format(less, formatter.format(dn1).replaceAll("\\.0+$", "")));
				} else if(difference1 < 2) {
					record.setProvide1(String.format(single, formatter.format(dn1).replaceAll("\\.0+$", "")));
				} else if(difference1 >= 2) {
					record.setProvide1(String.format(plural, formatter.format(dn1).replaceAll("\\.0+$", ""), provides_formatter.format(difference1)));
				}
				
				// Donation 2
				if(difference2 < 1) {
					record.setProvide2(String.format(less, formatter.format(dn2).replaceAll("\\.0+$", "")));
				} else if(difference2 < 2) {
					record.setProvide2(String.format(single, formatter.format(dn2).replaceAll("\\.0+$", "")));
				} else if(difference2 >= 2) {
					record.setProvide2(String.format(plural, formatter.format(dn2).replaceAll("\\.0+$", ""), provides_formatter.format(difference2)));
				}
				
				// Donation 3
				if(difference3 < 1) {
					record.setProvide3(String.format(less, formatter.format(dn3).replaceAll("\\.0+$", "")));
				} else if(difference3 < 2) {
					record.setProvide3(String.format(single, formatter.format(dn3).replaceAll("\\.0+$", "")));
				} else if(difference3 >= 2) {
					record.setProvide3(String.format(plural, formatter.format(dn3).replaceAll("\\.0+$", ""), provides_formatter.format(difference3)));
				}
				
				// Donation 4
				if(difference4 < 1) {
					record.setProvide4(String.format(less, formatter.format(dn4).replaceAll("\\.0+$", "")));
				} else if(difference4 < 2) {
					record.setProvide4(String.format(single, formatter.format(dn4).replaceAll("\\.0+$", "")));
				} else if(difference4 >= 2) {
					record.setProvide4(String.format(plural, formatter.format(dn4).replaceAll("\\.0+$", ""), provides_formatter.format(difference4)));
				}
				
			}
			
			record.setODnAmt(open);
		}
	}
	
	// Convert the gift arrays to metrics
		private void setMatchedGiftArrayMetrics(UserData userData, double costPerUnit) {
			
			String less = "$%s DOUBLES to $%s to help nurture a baby animal for 1 day.";
			String single = "$%s DOUBLES to $%s to nurture a baby animal for 1 day.";
			String plural = "$%s DOUBLES to $%s to nurture a baby animal for %s days.";
			String open = "$________ DOUBLES to nurture a baby animal.";

			String formattedCostPerUnit = String.valueOf(costPerUnit).replaceAll("\\.0+$", "");
			
			less = getGiftMetricLine("Enter the sentence to use when asks are < $" + formattedCostPerUnit, less).trim();
			single = getGiftMetricLine("Enter the sentence to use when asks are = $" + formattedCostPerUnit, single).trim();
			plural = getGiftMetricLine("Enter the sentence to use when asks are > $" + formattedCostPerUnit, plural).trim();
			open = getGiftMetricLine("Enter the sentence to use for open asks.", open).trim();
			
			DecimalFormat formatter = new DecimalFormat("#,###.00");
			DecimalFormat provides_formatter = new DecimalFormat("#,###");
			
			for(Record record : userData.getRecordList()) {
				if(Validators.isNumber(record.getDn1Amt())) {
					double dn1 = Double.parseDouble(record.getDn1Amt());
					double dn2 = Double.parseDouble(record.getDn2Amt());
					double dn3 = Double.parseDouble(record.getDn3Amt());
					double dn4 = Double.parseDouble(record.getDn4Amt());
					
					double dn1X2 = Double.parseDouble(record.getDn1Amt()) * 2;
					double dn2X2 = Double.parseDouble(record.getDn2Amt()) * 2;
					double dn3X2 = Double.parseDouble(record.getDn3Amt()) * 2;
					double dn4X2 = Double.parseDouble(record.getDn4Amt()) * 2;
					
					int difference1 = (int) (dn1X2 / costPerUnit);
					int difference2 = (int) (dn2X2 / costPerUnit);
					int difference3 = (int) (dn3X2 / costPerUnit);
					int difference4 = (int) (dn4X2 / costPerUnit);
					
					// Donation 1
					if(difference1 < 1) {
						record.setProvide1(String.format(less, formatter.format(dn1).replaceAll("\\.0+$", ""), formatter.format(dn1X2).replaceAll("\\.0+$", "")));
					} else if(difference1 < 2) {
						record.setProvide1(String.format(single, formatter.format(dn1).replaceAll("\\.0+$", ""), formatter.format(dn1X2).replaceAll("\\.0+$", "")));
					} else if(difference1 >= 2) {
						record.setProvide1(String.format(plural, formatter.format(dn1).replaceAll("\\.0+$", ""), formatter.format(dn1X2).replaceAll("\\.0+$", ""), provides_formatter.format(difference1)));
					}
					
					// Donation 2
					if(difference2 < 1) {
						record.setProvide2(String.format(less, formatter.format(dn2).replaceAll("\\.0+$", ""), formatter.format(dn2X2).replaceAll("\\.0+$", "")));
					} else if(difference2 < 2) {
						record.setProvide2(String.format(single, formatter.format(dn2).replaceAll("\\.0+$", ""), formatter.format(dn2X2).replaceAll("\\.0+$", "")));
					} else if(difference2 >= 2) {
						record.setProvide2(String.format(plural, formatter.format(dn2).replaceAll("\\.0+$", ""), formatter.format(dn2X2).replaceAll("\\.0+$", ""), provides_formatter.format(difference2)));
					}
					
					// Donation 3
					if(difference3 < 1) {
						record.setProvide3(String.format(less, formatter.format(dn3).replaceAll("\\.0+$", ""), formatter.format(dn3X2).replaceAll("\\.0+$", "")));
					} else if(difference3 < 2) {
						record.setProvide3(String.format(single, formatter.format(dn3).replaceAll("\\.0+$", ""), formatter.format(dn3X2).replaceAll("\\.0+$", "")));
					} else if(difference3 >= 2) {
						record.setProvide3(String.format(plural, formatter.format(dn3).replaceAll("\\.0+$", ""), formatter.format(dn3X2).replaceAll("\\.0+$", ""), provides_formatter.format(difference3)));
					}
					
					// Donation 4
					if(difference4 < 1) {
						record.setProvide4(String.format(less, formatter.format(dn4).replaceAll("\\.0+$", ""), formatter.format(dn4X2).replaceAll("\\.0+$", "")));
					} else if(difference4 < 2) {
						record.setProvide4(String.format(single, formatter.format(dn4).replaceAll("\\.0+$", ""), formatter.format(dn4X2).replaceAll("\\.0+$", "")));
					} else if(difference4 >= 2) {
						record.setProvide4(String.format(plural, formatter.format(dn4).replaceAll("\\.0+$", ""), formatter.format(dn4X2).replaceAll("\\.0+$", ""), provides_formatter.format(difference4)));
					}
					
				}
				
				record.setODnAmt(open);
			}
		}
	
	// Set the gift arrays
	private void setGiftArrays(UserData userData, double defaultAskAmount) {
		final BigDecimal LAST_GIFT_ROUNDING_AMOUNT = new BigDecimal("5.00");

		for(Record record : userData.getRecordList()) {
			// initialize default values
			record.setDn1Amt("");
			record.setDn2Amt("");
			record.setDn3Amt("");
			record.setDn4Amt("");
			record.setProvide1("");
			record.setProvide2("");
			record.setProvide3("");
			record.setProvide4("");
			record.setODnAmt("");
			
			String donorSegment = record.getSeg();
			
			String tempLastDonation = record.getLstDnAmt().replaceAll("[^0-9\\.]", "");
			
			if(!Validators.isNumber(tempLastDonation)) // make sure its a valid number
				tempLastDonation = String.valueOf(LAST_GIFT_ROUNDING_AMOUNT.doubleValue());
			
			// last donation rounded up to nearest 5
			Double lastDonationRoundedUpByFive = new BigDecimal(tempLastDonation).divide(LAST_GIFT_ROUNDING_AMOUNT, 2, RoundingMode.CEILING)
					.setScale(0, RoundingMode.CEILING).multiply(LAST_GIFT_ROUNDING_AMOUNT).doubleValue();
			
			if(!donorSegment.equalsIgnoreCase(segment.MONTHLY.getName())
				&& !donorSegment.equalsIgnoreCase(segment.HVD.getName())
				&& !donorSegment.equalsIgnoreCase(segment.MLD.getName())
				&& !donorSegment.equalsIgnoreCase(segment.LEGACY.getName()))
					setGiftArray(record, lastDonationRoundedUpByFive, defaultAskAmount);

		}
	}
	
	// logic to build non lapsed gift arrays
		private void setGiftArray(Record record, Double lastDonationRoundedUpByFive, double defaultAskAmount) {
			int askTier = 1;
			
			// Small default amount multiplier
			int sdam = 1;
			
			if(defaultAskAmount < 10)
				sdam = 4;
			else if(defaultAskAmount < 20)
				sdam = 2;
			
			if(lastDonationRoundedUpByFive < defaultAskAmount * (3 * sdam)) {
				record.setDn1Amt(String.valueOf(defaultAskAmount * sdam));
				record.setDn2Amt(String.valueOf(defaultAskAmount * (2 * sdam)));
				record.setDn3Amt(String.valueOf(defaultAskAmount * (3 * sdam)));
				record.setDn4Amt(String.valueOf(defaultAskAmount * (4 * sdam)));
				askTier = 7;
			} else if(lastDonationRoundedUpByFive < defaultAskAmount * (5 * sdam)) {
				record.setDn1Amt(String.valueOf(defaultAskAmount * (3 * sdam)));
				record.setDn2Amt(String.valueOf(defaultAskAmount * (4 * sdam)));
				record.setDn3Amt(String.valueOf(defaultAskAmount * (5 * sdam)));
				record.setDn4Amt(String.valueOf(defaultAskAmount * (6 * sdam)));
				askTier = 6;
			} else if(lastDonationRoundedUpByFive < defaultAskAmount * (7 * sdam)) {
				record.setDn1Amt(String.valueOf(defaultAskAmount * (5 * sdam)));
				record.setDn2Amt(String.valueOf(defaultAskAmount * (7 * sdam)));
				record.setDn3Amt(String.valueOf(defaultAskAmount * (8 * sdam)));
				record.setDn4Amt(String.valueOf(defaultAskAmount * (10 * sdam)));
				askTier = 5;
			} else if(lastDonationRoundedUpByFive < defaultAskAmount * (11 * sdam)) {
				record.setDn1Amt(String.valueOf(defaultAskAmount * (8 * sdam)));
				record.setDn2Amt(String.valueOf(defaultAskAmount * (10 * sdam)));
				record.setDn3Amt(String.valueOf(defaultAskAmount * (12 * sdam)));
				record.setDn4Amt(String.valueOf(defaultAskAmount * (14 * sdam)));
				askTier = 4;
			} else if(lastDonationRoundedUpByFive < defaultAskAmount * (15 * sdam)) {
				record.setDn1Amt(String.valueOf(defaultAskAmount * (12 * sdam)));
				record.setDn2Amt(String.valueOf(defaultAskAmount * (14 * sdam)));
				record.setDn3Amt(String.valueOf(defaultAskAmount * (16 * sdam)));
				record.setDn4Amt(String.valueOf(defaultAskAmount * (18 * sdam)));
				askTier = 3;
			} else {
				record.setDn1Amt(String.valueOf(defaultAskAmount * (16 * sdam)));
				record.setDn2Amt(String.valueOf(defaultAskAmount * (18 * sdam)));
				record.setDn3Amt(String.valueOf(defaultAskAmount * (20 * sdam)));
				record.setDn4Amt(String.valueOf(defaultAskAmount * (22 * sdam)));
				askTier = 2;
			}

			if(record.getSeg().equalsIgnoreCase(segment.ACTIVE_GENERAL.getName()))
				record.setSegCode(record.getSegCode() + askTier);
		}
	
	// Logic to categorize donors into segments
	private void setSegment(UserData userData) {
		final int FREQUENT_DONATIONS_CRITERIA = 3;
		final int NEW_DONOR_MONTHS_CRITERIA = 6;
		final int LAPSED_DONATIONS_CRITERIA = 24;

		String now = String.valueOf(LocalDate.now());
		for(Record record : userData.getRecordList()) {
			long monthsFromFirstDonation = getMonthsBetween(record.getFstDnDat(), now);
			long monthsFromLastDonation = getMonthsBetween(record.getLstDnDat(), now);

			if(record.getSeg() == null) {
				if(Integer.parseInt(record.getNumDnLst12Mnths()) >= FREQUENT_DONATIONS_CRITERIA)
					record.setSeg(segment.FREQUENT.getName());
				else if(monthsFromFirstDonation >= 0 && monthsFromFirstDonation <= NEW_DONOR_MONTHS_CRITERIA)
					record.setSeg(segment.NEW.getName());
				else if(monthsFromLastDonation >= LAPSED_DONATIONS_CRITERIA)
						record.setSeg(segment.LAPSED.getName());
				else
					record.setSeg(segment.ACTIVE_GENERAL.getName());
			}
		}
	}

	// Sets the R, F, and M scores
	private void setRFM(UserData userData) {
		final int PARTITIONS = 5;

		int numOfRecords = userData.getRecordList().size();
		int maxGroupSize = (int) Math.ceil(numOfRecords/PARTITIONS);
		int counter = 0;
		int currentScore = 5;

		// sort record on recency (low = good; high = bad)
		userData.getRecordList().sort(new RecordSorters.CompareByFieldAscAsNumber(UserData.fieldName.RECENCY.getName()));

		for(Record record : userData.getRecordList()) {
			if(++counter > maxGroupSize) {
				counter = 0;
				--currentScore;
			}

			record.setRfmScore(String.valueOf(currentScore));
		}

		counter = 0;
		currentScore = 5;

		// sort record on frequency (high = good)
		userData.getRecordList().sort(new RecordSorters.CompareByFieldDescAsNumber(UserData.fieldName.FREQUENCY.getName()));

		for(Record record : userData.getRecordList()) {
			if(++counter > maxGroupSize) {
				counter = 0;
				--currentScore;
			}
			record.setRfmScore(record.getRfmScore() + "_" + String.valueOf(currentScore));
		}

		counter = 0;
		currentScore = 5;

		// sort record on monetary (high = good)
		userData.getRecordList().sort(new RecordSorters.CompareByFieldDescAsNumber(UserData.fieldName.TOTAL_DONATION_AMOUNT.getName()));
		userData.getRecordList().sort(new RecordSorters.CompareByFieldDescAsNumber(UserData.fieldName.MONETARY.getName()));

		for(Record record : userData.getRecordList()) {
			if(++counter > maxGroupSize) {
				counter = 0;
				--currentScore;
			}
			record.setRfmScore(record.getRfmScore() + "_" + String.valueOf(currentScore));
		}

		counter = 0;
		currentScore = 5;

	}
	
	// calculates the months between two dates
	private long getMonthsBetween(String startDate, String endDate) {
		
		startDate = startDate.replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/");
		endDate = endDate.replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/");
		
		if(!Validators.isStringOfDateFormat(startDate, "yyyy/MM/dd")) {
			System.out.println("Invalid date of " + startDate);
			return -1;
		}
		if(!Validators.isStringOfDateFormat(endDate, "yyyy/MM/dd")) {
			System.out.println("Invalid date of " + endDate);
			return -1;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate start = LocalDate.parse(startDate, formatter);
		LocalDate end = LocalDate.parse(endDate, formatter);
		
		long monthsBetween = ChronoUnit.MONTHS.between(
			     YearMonth.from(start), 
			     YearMonth.from(end)
			);
		
		return monthsBetween;

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
