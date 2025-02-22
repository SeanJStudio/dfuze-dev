/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.generosityx;



import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

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

import com.mom.dfuze.data.util.Analyze;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.DateTimeInferer;
import com.mom.dfuze.data.util.Validators;

import com.mom.dfuze.io.FileIngestor;
import com.mom.dfuze.ui.DropdownSelectDialog;
import com.mom.dfuze.ui.UiController;



/**
 * RegularProcess implements a RunBehavior for Monthly Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/11/2024
 *
 */
public class MBA implements RunGenerosityXBehavior {

	private final String BEHAVIOR_NAME = "MBA";
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
					+ "</ol>"

					+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
					+ "</html>";
	
	public final Pattern GIFT_FILE_ID_PATTERN = Pattern.compile("(^|\\s+)No client(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_AMOUNT_PATTERN = Pattern.compile("(^|\\s+)Montant(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_DATE_PATTERN = Pattern.compile("(^|\\s+)Date(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_CAMPAIGN_PATTERN = Pattern.compile("(^|\\s+)Donor Type(\\s+|$)", Pattern.CASE_INSENSITIVE);
	
	public final Pattern MONTHLY_DESIGNATION_PATTERN = Pattern.compile("monthly", Pattern.CASE_INSENSITIVE);

	public enum segment {
		HVD("High-Value Donor"),
		LAPSED("Lapsed"),
		MONTHLY("Monthly"),
		LBUNTY("LBUNTY"),
		GENERAL("General");
		
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

		userData.autoSetRecordList();						// add the records to the userData list
		userData.autoSetRecordListFields(REQUIRED_FIELDS);	// set record members via required fields
		
		// load gifts file as record list,	infer gift date format to use
		List<Record> giftList = getGiftList(); // dfInId, dfLstDnAmt, dfLstGftDat, dfSeg is set
		String giftDateFormat = DateTimeInferer.inferFormat(giftList, UserData.fieldName.LAST_DONATION_DATE.getName());
				
		// Sort the gift list by latest donation
		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(giftDateFormat);
		giftList.sort(new RecordSorters.CompareByFieldDescAsDate(UserData.fieldName.LAST_DONATION_DATE.getName(), datetimeFormatter));
				
		// Convert the gifts into a gift history map (id, List(history))
		HashMap<String, List<MBAGiftHistory>> giftHistoryMap = convertGiftsToMap(giftList, giftDateFormat);
		
		// process gift history into main donor list
		processGifts(userData, giftHistoryMap);
		
		// build RFM score
		setRFM(userData);
		
		//min max rfm
		Analyze.minMaxRFM(userData);
		
		// Place into categories
		setSegment(userData);
		
		// value priority, high = good
		Analyze.prioritizeRFM(userData);

		userData.getRecordList().sort(new RecordSorters.CompareByFieldDescAsNumber(UserData.fieldName.PRIORITY.getName()));

		userData.setDfHeaders(new String[] {
				UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
				UserData.fieldName.LAST_DONATION_DATE.getName(),
				UserData.fieldName.FIRST_DONATION_AMOUNT.getName(),
				UserData.fieldName.FIRST_DONATION_DATE.getName(),
				UserData.fieldName.TOTAL_DONATION_AMOUNT.getName(),
				UserData.fieldName.TOTAL_DONATION_AMOUNT_LAST_12_MONTHS.getName(),
				UserData.fieldName.NUMBER_OF_DONATIONS.getName(),
				UserData.fieldName.LARGEST_DONATION_AMOUNT.getName(),
				UserData.fieldName.DONATION_AMOUNT_ARRAY.getName(),
				UserData.fieldName.RECENCY.getName(),
				UserData.fieldName.FREQUENCY.getName(),
				UserData.fieldName.MONETARY.getName(),
				UserData.fieldName.RFM.getName(),
				UserData.fieldName.SEGMENT.getName(),
				UserData.fieldName.PRIORITY.getName()
		});
		
		//removeZeroGiftRecords(userData);
	}
	
	private List<Record> getGiftList() throws Exception {
		JOptionPane.showMessageDialog(
				UiController.getMainFrame(),
				"Please load the associated gift file now.",
				"Load gifts",
				JOptionPane.INFORMATION_MESSAGE);
		
		List<List<String>> giftFile = FileIngestor.ingest();	// get the gift file	
		HashSet<Integer> indexSet = new HashSet<>();
	
		int idIndex, amountIndex, giftDateIndex, appealIndex = -1;
		
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
		
		// designation
		dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the Appeal Name field.");
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

		appealIndex = dsd.getSelectedValueIndex();
		
		if(!indexSet.add(appealIndex))
			throw new Exception("The appeal name field has been mapped more than once.");
		
		List<Record> giftsList = new ArrayList<>();
		
		giftFile.remove(0); // remove header row
		
		for(int i = 0; i < giftFile.size(); ++i) {
			String id = giftFile.get(i).get(idIndex);
			String giftAmount = giftFile.get(i).get(amountIndex).replaceAll("[^0-9\\.-]", "");
			String giftDate = giftFile.get(i).get(giftDateIndex).replaceAll("\\d+:.*$", "").trim().replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/").trim();
			String giftDesignation = giftFile.get(i).get(appealIndex).trim();
			
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
	private HashMap<String, List<MBAGiftHistory>> convertGiftsToMap(List<Record> giftList, String giftDateFormat) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(giftDateFormat);

		// id, giftHistory
		HashMap<String, List<MBAGiftHistory>> giftHistoryMap = new HashMap<>();

		for(Record record : giftList) {
			double giftAmount = (Validators.isNumber(record.getLstDnAmt())) ? Double.parseDouble(record.getLstDnAmt()) : 0.0;

			if(Validators.isStringOfDateFormat(record.getLstDnDat(), giftDateFormat)) {
				//System.out.println(record.getLstDnDat() + " vs " + giftDateFormat);
				LocalDate giftDate = LocalDate.parse(record.getLstDnDat(), formatter);
				//System.out.println(giftDate.toString());
				MBAGiftHistory history = new MBAGiftHistory(
						record.getInId(),
						giftAmount,
						giftDate,
						record.getSeg()
						);

				if(!giftHistoryMap.containsKey(record.getInId()))
					giftHistoryMap.put(record.getInId(), new ArrayList<MBAGiftHistory>());

				giftHistoryMap.get(record.getInId()).add(history);
			}
		}

		return giftHistoryMap;
	}
	
	private void processGifts(UserData userData, HashMap<String, List<MBAGiftHistory>> giftHistoryMap) {
		final int MONTHS6 = 6;
		final int MONTHS12 = 12;
		final int MONTHS24 = 24;

		String now = String.valueOf(LocalDate.now());
		int currentYear = LocalDate.now().getYear();
		int lastYear = LocalDate.now().getYear() - 1;
		
		int nogifts = 0;
		
		for(int i = 0; i < userData.getRecordList().size(); ++i) {
			Record record = userData.getRecordList().get(i);
			
			if(!giftHistoryMap.containsKey(record.getInId())) {
				++nogifts;
				//System.out.println("No ID for " + record.getInId());
				record.setLstDnAmt("0");
				record.setLstDnDat("1900-01-01");
				record.setFstDnAmt("0");
				record.setFstDnDat("1900-01-01");
				record.setTtlDnAmt("0");
				record.setTtlDnAmtLst12Mnths("0");
				record.setTtlDnAmtCrntYr("0");
				record.setTtlDnAmtLstYr("0");
				record.setNumDn("0");
				record.setLrgDnAmt("0"); // largest donation amount in last 2 years;
				record.setDnAmtArr("");
				record.setRScore("99999");
				record.setFScore("0");
				record.setMScore("0");
				record.setQuantity("0"); // Using this to hold the number of gifts in last 12 months
				record.setYear("0"); // Using this to hold the total donation amount of last 24 months
			}
			
			if(giftHistoryMap.containsKey(record.getInId())) {
				List<MBAGiftHistory> giftHistoryList = giftHistoryMap.get(record.getInId());
				
				double totalGiftAmount = 0.0;
				int totalGifts = giftHistoryList.size();
				
				double totalGiftAmountLast6Months = 0.0;
				double totalGiftAmountLast12Months = 0.0;
				int totalGiftsLast12Months = 0;
				double totalGiftAmountCurrentYear = 0.0;
				double totalGiftAmountLastYear = 0.0;
				double largestGiftMadeLast24Months = 0.0;
				
				ArrayList<Double> giftAmounts = new ArrayList<>();
				
				String commaSeparatedHistory = giftHistoryList
				        .stream()
				        .map(String::valueOf)
				        .collect(Collectors.joining(","));
				
				for(int j = 0; j < giftHistoryList.size(); ++j) {
					MBAGiftHistory giftHistory = giftHistoryList.get(j);
					
					totalGiftAmount += giftHistory.getGiftAmount();
					giftAmounts.add(giftHistory.getGiftAmount());
					
					long monthsFromDonation = getMonthsBetween(giftHistory.getGiftDate().toString(), now);
					
					if(monthsFromDonation <= MONTHS6 && monthsFromDonation > -1)
						totalGiftAmountLast6Months += giftHistory.getGiftAmount();
					
					if(monthsFromDonation <= MONTHS12 && monthsFromDonation > -1) {
						++totalGiftsLast12Months;
						totalGiftAmountLast12Months += giftHistory.getGiftAmount();
					}
					
					if(monthsFromDonation <= MONTHS24 && monthsFromDonation > -1)
						if(giftHistory.getGiftAmount() > largestGiftMadeLast24Months)
							largestGiftMadeLast24Months = giftHistory.getGiftAmount();
					
					long daysBetween = ChronoUnit.DAYS.between(giftHistory.getGiftDate(), LocalDate.now());
					
					if(giftHistory.getGiftDate().getYear() == currentYear)
						totalGiftAmountCurrentYear += giftHistory.getGiftAmount();
					else if(giftHistory.getGiftDate().getYear() == lastYear)
						totalGiftAmountLastYear += giftHistory.getGiftAmount();
					
					// identify monthly donors if last gift is within 60 days and monthly id found in appeal
					if(daysBetween <= 60) {
						Matcher monthlyMatcher = MONTHLY_DESIGNATION_PATTERN.matcher(giftHistory.getGiftCampaign());
						if(monthlyMatcher.find()) {
							System.out.println("monthly");
							record.setSeg(segment.MONTHLY.getName());
						}
					}
					
					if(j == 0) {
						record.setLstDnAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setLstDnDat(giftHistory.getGiftDate().toString());
						record.setRScore(String.valueOf(daysBetween));
					}
					
					if(j == giftHistoryList.size() - 1) {
						record.setFstDnAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setFstDnDat(giftHistory.getGiftDate().toString());
					}

				}
				
				double monetarySum = giftAmounts.stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();
				
				record.setMScore(String.valueOf(monetarySum));
				record.setFScore(String.valueOf(totalGifts));
				record.setTtlDnAmt(String.valueOf(totalGiftAmount));
				record.setTtlDnAmtLst12Mnths(String.valueOf(totalGiftAmountLast12Months));
				record.setTtlDnAmtCrntYr(String.valueOf(totalGiftAmountCurrentYear));
				record.setTtlDnAmtLstYr(String.valueOf(totalGiftAmountLastYear));
				record.setNumDn(String.valueOf(totalGifts));
				record.setLrgDnAmt(String.valueOf(largestGiftMadeLast24Months));
				record.setDnAmtArr(commaSeparatedHistory);
				record.setQuantity(String.valueOf(totalGiftsLast12Months)); // Using this to hold the number of gifts of last 12 months
				record.setYear(String.valueOf(totalGiftAmountLast6Months)); // Using this to hold the total donation amount of last 6 months
			}
			
			
		}
		
		System.out.println(nogifts);
	}

	// Logic to categorize donors into segments
	private void setSegment(UserData userData) {
		final int MAJOR_DONATION_AMOUNT = 500;
		final int LAPSED_DONATIONS_CRITERIA = 24;

		String now = String.valueOf(LocalDate.now());
		
		for(Record record : userData.getRecordList()) {
			long monthsFromLastDonation = getMonthsBetween(record.getLstDnDat(), now);
			double currentYearDonation = Double.parseDouble(record.getTtlDnAmtCrntYr());
			double lastYearDonation = Double.parseDouble(record.getTtlDnAmtLstYr());
			
			if(record.getSeg() == null) {
				if(Double.parseDouble(record.getYear()) >= MAJOR_DONATION_AMOUNT) //getYear is total donation amount in last 6 months
					record.setSeg(segment.HVD.getName());
				else if(currentYearDonation == 0.0 && lastYearDonation > 0.0)
					record.setSeg(segment.LBUNTY.getName());
				else if(monthsFromLastDonation > LAPSED_DONATIONS_CRITERIA)
					record.setSeg(segment.LAPSED.getName());
				else
					record.setSeg(segment.GENERAL.getName());
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
