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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	public final Pattern GIFT_FILE_CAMPAIGN_PATTERN = Pattern.compile("(^|\\s+)campaign name(\\s+|$)", Pattern.CASE_INSENSITIVE);

	public enum segment {
		NEW("New"),
		LAPSED("Lapsed"),
		DEEP_LAPSED("Deep Lapsed"),
		FREQUENT("Frequent"),
		TOP("Top"),
		GENERAL("General"),
		MONTHLY("Monthly");
		
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
	
		int idIndex, amountIndex, giftDateIndex, campaignNameIndex = -1;
		
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
		
		List<Record> giftsList = new ArrayList<>();
		
		giftFile.remove(0); // remove header row
		
		for(int i = 0; i < giftFile.size(); ++i) {
			String id = giftFile.get(i).get(idIndex).replaceAll("[^a-zA-Z0-9_]", "").replaceFirst("^0+", ""); // Remove leading zeros
			String giftAmount = giftFile.get(i).get(amountIndex).replaceAll("[^0-9\\.-]", "");
			String giftDate = giftFile.get(i).get(giftDateIndex).replaceAll("\\d+:.*$", "").trim().replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/").trim();
			String giftDesignation = giftFile.get(i).get(campaignNameIndex).trim();
			
			boolean isGiftZero = false;
			
			if(Validators.isNumber(giftAmount)) {
				BigDecimal giftAmountBD = new BigDecimal(giftAmount);
				if (giftAmountBD.equals(BigDecimal.ZERO))
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
						record.getSeg()
						);

				if(!giftHistoryMap.containsKey(record.getInId()))
					giftHistoryMap.put(record.getInId(), new ArrayList<WRAGiftHistory>());

				giftHistoryMap.get(record.getInId()).add(history);
			}
		}

		return giftHistoryMap;
	}
	
	private void processGifts(UserData userData, HashMap<String, List<WRAGiftHistory>> giftHistoryMap) {
		final int MONTHS6 = 6;
		final int MONTHS12 = 12;
		final int MONTHS24 = 24;
		
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
				record.setYear("0"); // Using this to hold the total donation amount of last 24 months
			}
			
			if(giftHistoryMap.containsKey(record.getInId())) {
				List<WRAGiftHistory> giftHistoryList = giftHistoryMap.get(record.getInId());
				
				double totalGiftAmount = 0.0;
				int totalGifts = giftHistoryList.size();
				
				double totalGiftAmountLast6Months = 0.0;
				double totalGiftAmountLast12Months = 0.0;
				int totalGiftsLast12Months = 0;
				double largestGiftMadeLast24Months = 0.0;
				
				ArrayList<Double> giftAmounts = new ArrayList<>();
				
				String commaSeparatedHistory = giftHistoryList
				        .stream()
				        .map(String::valueOf)
				        .collect(Collectors.joining(","));
				
				for(int j = 0; j < giftHistoryList.size(); ++j) {
					WRAGiftHistory giftHistory = giftHistoryList.get(j);
					
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
					
					// This is the last donation made
					if(j == 0) {
						record.setLstDnAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setLstDnDat(giftHistory.getGiftDate().toString());
						long daysBetween = ChronoUnit.DAYS.between(giftHistory.getGiftDate(), LocalDate.now());
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
				
				record.setMScore(String.valueOf(monetarySum));
				record.setFScore(String.valueOf(totalGifts));
				record.setTtlDnAmt(String.valueOf(totalGiftAmount));
				record.setTtlDnAmtLst12Mnths(String.valueOf(totalGiftAmountLast12Months));
				record.setNumDn(String.valueOf(totalGifts));
				record.setLrgDnAmt(String.valueOf(largestGiftMadeLast24Months));
				record.setDnAmtArr(commaSeparatedHistory);
				record.setNumDnLst12Mnths(String.valueOf(totalGiftsLast12Months)); // Using this to hold the number of gifts of last 12 months
				record.setYear(String.valueOf(totalGiftAmountLast6Months)); // Using this to hold the total donation amount of last 6 months
			}
			
			
		}
	}
	
	private void addSeeds(UserData userData) {
		Record template = userData.getRecordList().get(userData.getRecordList().size() - 1);
		
		String[] inDataMatt = Arrays.copyOf(template.getDfInData(), template.getDfInData().length);
		Arrays.fill(inDataMatt, "");
		inDataMatt[userData.getInIdIndex()] = "SEEDER_MATT";
		inDataMatt[userData.getFstNameIndex()] = "Matt";
		inDataMatt[userData.getDearSalIndex()] = "Matt";
		inDataMatt[userData.getLstNameIndex()] = "Hussey";
		inDataMatt[userData.getNam1Index()] = "Matt Hussey";
		inDataMatt[userData.getAdd1Index()] = "4531 Lindholm Road";
		inDataMatt[userData.getCityIndex()] = "Victoria";
		inDataMatt[userData.getProvIndex()] = "BC";
		inDataMatt[userData.getPCodeIndex()] = "V9C 4C5";
		
		String[] inDataBecca = Arrays.copyOf(template.getDfInData(), template.getDfInData().length);
		Arrays.fill(inDataBecca, "");
		inDataBecca[userData.getInIdIndex()] = "SEEDER_BECCA";
		inDataBecca[userData.getFstNameIndex()] = "Becca";
		inDataBecca[userData.getDearSalIndex()] = "Becca";
		inDataBecca[userData.getLstNameIndex()] = "Gust";
		inDataBecca[userData.getNam1Index()] = "Becca Gust";
		inDataBecca[userData.getAdd1Index()] = "11-19330 69 Ave";
		inDataBecca[userData.getCityIndex()] = "Surrey";
		inDataBecca[userData.getProvIndex()] = "BC";
		inDataBecca[userData.getPCodeIndex()] = "V4N 0Z2";
		
		String[] inDataWRA = Arrays.copyOf(template.getDfInData(), template.getDfInData().length);
		Arrays.fill(inDataWRA, "");
		inDataWRA[userData.getInIdIndex()] = "SEEDER_WRA";
		inDataWRA[userData.getFstNameIndex()] = "Wildlife Rescue Association of British Columbia";
		inDataWRA[userData.getDearSalIndex()] = "Friends";
		inDataWRA[userData.getLstNameIndex()] = "";
		inDataWRA[userData.getNam1Index()] = "Wildlife Rescue Association of British Columbia";
		inDataWRA[userData.getAdd1Index()] = "5216 Glencarin Drive";
		inDataWRA[userData.getCityIndex()] = "Burnaby";
		inDataWRA[userData.getProvIndex()] = "BC";
		inDataWRA[userData.getPCodeIndex()] = "V5B 3C1";
		
		Record seedMatt = new Record.Builder(9999999, inDataMatt, "", "", "")
				.setDearSal("Matt")
				.setFstName("Matt")
				.setLstName("Hussey")
				.setNam1("Matt Hussey")
				.setInId("SEEDER_MATT")
				.setSeg(segment.GENERAL.getName())
				.setRecType("")
				.build();
		
		Record seedBecca = new Record.Builder(9999998, inDataBecca, "", "", "")
				.setDearSal("Becca")
				.setFstName("Becca")
				.setLstName("Gust")
				.setNam1("Becca Gust")
				.setInId("SEEDER_BECCA")
				.setSeg(segment.GENERAL.getName())
				.setRecType("")
				.build();
		
		Record seedWRA = new Record.Builder(9999997, inDataWRA, "", "", "")
				.setDearSal("Friends")
				.setFstName("Wildlife Rescue Association of British Columbia")
				.setLstName("")
				.setNam1("Wildlife Rescue Association of British Columbia")
				.setInId("SEEDER_WRA")
				.setSeg(segment.GENERAL.getName())
				.setRecType("")
				.build();
		
		userData.add(seedMatt);
		userData.add(seedBecca);
		userData.add(seedWRA);
	}
	

	private void setCampaignCode(UserData userData, String campaignCode) {
		for(Record record : userData.getRecordList()) {
			if(record.getSeg().equalsIgnoreCase(segment.TOP.getName()))
				record.setSegCode(campaignCode + "T");
			else if(record.getSeg().equalsIgnoreCase(segment.GENERAL.getName()))
				record.setSegCode(campaignCode + "A");
			else if(record.getSeg().equalsIgnoreCase(segment.MONTHLY.getName()))
				record.setSegCode(campaignCode + "M1");
			else if(record.getSeg().equalsIgnoreCase(segment.FREQUENT.getName()))
				record.setSegCode(campaignCode + "Q");
			else if(record.getSeg().equalsIgnoreCase(segment.LAPSED.getName()) || record.getSeg().equalsIgnoreCase(segment.DEEP_LAPSED.getName()))
				record.setSegCode(campaignCode + "L");
			else if(record.getSeg().equalsIgnoreCase(segment.NEW.getName()))
				record.setSegCode(campaignCode + "N");
		}
	}
	
	// Prompt the user for the donation metric line in asks
	private String getCampaignCode() {
		UserInputDialog uid = new UserInputDialog(UiController.getMainFrame(), "Enter the campaign code. (Ex. M241101)");
		uid.getTextField().setText("M241101");
		uid.setVisible(true);

		if(uid.getIsNextPressed())
			return uid.getUserInput().toUpperCase().trim();

		return "";
	}
	
	// Prompt the user for the cost per unit for donation metrics
	private double getCostPerUnit() {
		UserDecimalInputDialog udid = new UserDecimalInputDialog(UiController.getMainFrame(), "Enter the gift metric unit cost. (Ex if 1 unit costs $45, enter 45)");
		udid.getTextField().setText("45");
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
			
			if(donorSegment.equalsIgnoreCase(segment.LAPSED.getName())) // set lapsed gifts
				setLapsedGiftArray2(record, lastDonationRoundedUpByFive, defaultAskAmount);
			else if(!donorSegment.equalsIgnoreCase(segment.MONTHLY.getName())) // set non-monthly gifts, leave monthly blank
				setNonLapsedGiftArray2(record, lastDonationRoundedUpByFive, defaultAskAmount);

		}
	}
	
	// Logic to build lapsed gift arrays
	private void setLapsedGiftArray(Record record, Double lastDonationRoundedUpByFive) {
		int askTier = 1;
		
		if(lastDonationRoundedUpByFive < 85.0) {
			record.setDn1Amt("20");
			record.setDn2Amt("50");
			record.setDn3Amt("100");
			record.setDn4Amt("200");
			askTier = 11;
		} else if(lastDonationRoundedUpByFive < 115.0) {
			record.setDn1Amt("60");
			record.setDn2Amt("100");
			record.setDn3Amt("160");
			record.setDn4Amt("240");
			askTier = 10;
		} else if(lastDonationRoundedUpByFive < 175.0) {
			record.setDn1Amt("60");
			record.setDn2Amt("160");
			record.setDn3Amt("240");
			record.setDn4Amt("320");
			askTier = 9;
		} else if(lastDonationRoundedUpByFive < 225.0) {
			record.setDn1Amt("100");
			record.setDn2Amt("160");
			record.setDn3Amt("200");
			record.setDn4Amt("260");
			askTier = 8;
		} else if(lastDonationRoundedUpByFive < 275.0) {
			record.setDn1Amt("100");
			record.setDn2Amt("200");
			record.setDn3Amt("260");
			record.setDn4Amt("350");
			askTier = 7;
		} else if(lastDonationRoundedUpByFive < 425.0) {
			record.setDn1Amt("100");
			record.setDn2Amt("200");
			record.setDn3Amt("300");
			record.setDn4Amt("400");
			askTier = 6;
		} else if(lastDonationRoundedUpByFive < 775.0) {
			record.setDn1Amt("200");
			record.setDn2Amt("300");
			record.setDn3Amt("520");
			record.setDn4Amt("750");
			askTier = 5;
		} else if(lastDonationRoundedUpByFive < 1125.0) {
			record.setDn1Amt("260");
			record.setDn2Amt("520");
			record.setDn3Amt("1040");
			record.setDn4Amt("1560");
			askTier = 4;
		} else if(lastDonationRoundedUpByFive < 1525.0) {
			record.setDn1Amt("520");
			record.setDn2Amt("1040");
			record.setDn3Amt("1560");
			record.setDn4Amt("2080");
			askTier = 3;
		} else if(lastDonationRoundedUpByFive < 2125.0) {
			record.setDn1Amt("1040");
			record.setDn2Amt("1560");
			record.setDn3Amt("2080");
			record.setDn4Amt("2600");
			askTier = 2;
		}
		
		record.setSegCode(record.getSegCode() + askTier);
	}
	
	// Logic to build lapsed gift arrays
	private void setLapsedGiftArray2(Record record, Double lastDonationRoundedUpByFive, double defaultAskAmount) {
		int askTier = 1;

		if(lastDonationRoundedUpByFive < defaultAskAmount * 2) {
			record.setDn1Amt(String.valueOf(defaultAskAmount / 2.0));
			record.setDn2Amt(String.valueOf(defaultAskAmount));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 2));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 3));
			askTier = 11;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 3) {
			record.setDn1Amt(String.valueOf(defaultAskAmount));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 2));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 3));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 4));
			askTier = 10;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 4) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 3));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 4));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 5));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 6));
			askTier = 9;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 6) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 4));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 5));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 6));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 7));
			askTier = 8;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 7) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 5));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 6));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 7));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 8));
			askTier = 7;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 10) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 6));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 7));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 8));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 10));
			askTier = 6;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 18) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 8));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 12));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 16));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 20));
			askTier = 5;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 22) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 12));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 16));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 20));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 24));
			askTier = 4;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 32) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 16));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 24));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 32));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 40));
			askTier = 3;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 44) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 24));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 32));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 40));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 48));
			askTier = 2;
		}

		record.setSegCode(record.getSegCode() + askTier);
	}
	
	// logic to build non lapsed gift arrays
	private void setNonLapsedGiftArray(Record record, Double lastDonationRoundedUpByFive) {
		int askTier = 1;
		
		if(lastDonationRoundedUpByFive < 85.0) {
			record.setDn1Amt("25");
			record.setDn2Amt("50");
			record.setDn3Amt("100");
			record.setDn4Amt("200");
			askTier = 11;
		} else if(lastDonationRoundedUpByFive < 115.0) {
			record.setDn1Amt("80");
			record.setDn2Amt("100");
			record.setDn3Amt("160");
			record.setDn4Amt("240");
			askTier = 10;
		} else if(lastDonationRoundedUpByFive < 175.0) {
			record.setDn1Amt("120");
			record.setDn2Amt("160");
			record.setDn3Amt("240");
			record.setDn4Amt("320");
			askTier = 9;
		} else if(lastDonationRoundedUpByFive < 225.0) {
			record.setDn1Amt("160");
			record.setDn2Amt("200");
			record.setDn3Amt("260");
			record.setDn4Amt("400");
			askTier = 8;
		} else if(lastDonationRoundedUpByFive < 275.0) {
			record.setDn1Amt("200");
			record.setDn2Amt("260");
			record.setDn3Amt("350");
			record.setDn4Amt("520");
			askTier = 7;
		} else if(lastDonationRoundedUpByFive < 425.0) {
			record.setDn1Amt("200");
			record.setDn2Amt("300");
			record.setDn3Amt("400");
			record.setDn4Amt("500");
			askTier = 6;
		} else if(lastDonationRoundedUpByFive < 775.0) {
			record.setDn1Amt("300");
			record.setDn2Amt("520");
			record.setDn3Amt("750");
			record.setDn4Amt("1040");
			askTier = 5;
		} else if(lastDonationRoundedUpByFive < 1525.0) {
			record.setDn1Amt("520");
			record.setDn2Amt("1040");
			record.setDn3Amt("1560");
			record.setDn4Amt("2080");
			askTier = 4;
		} else if(lastDonationRoundedUpByFive < 2000.0) {
			record.setDn1Amt("1040");
			record.setDn2Amt("1560");
			record.setDn3Amt("2080");
			record.setDn4Amt("2600");
			askTier = 3;
		} else if(lastDonationRoundedUpByFive < 2125.0) {
			record.setDn1Amt("1560");
			record.setDn2Amt("2080");
			record.setDn3Amt("2600");
			record.setDn4Amt("3120");
			askTier = 2;
		}
		
		record.setSegCode(record.getSegCode() + askTier);
	}
	
	// logic to build non lapsed gift arrays
	private void setNonLapsedGiftArray2(Record record, Double lastDonationRoundedUpByFive, double defaultAskAmount) {
		int askTier = 1;

		if(lastDonationRoundedUpByFive < defaultAskAmount * 2) {
			record.setDn1Amt(String.valueOf(defaultAskAmount));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 2));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 3));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 4));
			askTier = 11;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 3) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 2));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 3));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 4));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 5));
			askTier = 10;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 4) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 3));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 4));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 5));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 6));
			askTier = 9;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 6) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 4));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 5));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 6));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 7));
			askTier = 8;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 7) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 5));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 6));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 7));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 8));
			askTier = 7;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 10) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 8));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 9));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 10));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 11));
			askTier = 6;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 18) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 9));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 12));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 14));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 16));
			askTier = 5;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 34) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 20));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 24));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 28));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 32));
			askTier = 4;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 45) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 36));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 42));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 48));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 54));
			askTier = 3;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * 48) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * 46));
			record.setDn2Amt(String.valueOf(defaultAskAmount * 52));
			record.setDn3Amt(String.valueOf(defaultAskAmount * 58));
			record.setDn4Amt(String.valueOf(defaultAskAmount * 64));
			askTier = 2;
		}

		record.setSegCode(record.getSegCode() + askTier);
	}
	
	// Logic to categorize donors into segments
	private void setSegment(UserData userData) {
		final int MAJOR_DONATION_AMOUNT = 500;
		final int FREQUENT_DONATIONS_CRITERIA = 3;
		final int NEW_DONOR_MONTHS_CRITERIA = 6;
		final int LAPSED_DONATIONS_CRITERIA = 24;
		final int DEEP_LAPSED_DONATIONS_CRITERIA = 48;

		String now = String.valueOf(LocalDate.now());
		for(Record record : userData.getRecordList()) {
			long monthsFromFirstDonation = getMonthsBetween(record.getFstDnDat(), now);
			long monthsFromLastDonation = getMonthsBetween(record.getLstDnDat(), now);

			if(record.getSeg() == null) {
				if(record.getInId().toLowerCase().contains("seed"))
					record.setSeg(segment.GENERAL.getName());
				else if(Double.parseDouble(record.getYear()) >= MAJOR_DONATION_AMOUNT) //getYear is total donation amount in last 6 months
					record.setSeg(segment.TOP.getName());
				else if(monthsFromFirstDonation >= 0 && monthsFromFirstDonation <= NEW_DONOR_MONTHS_CRITERIA)
					record.setSeg(segment.NEW.getName());
				else if(Integer.parseInt(record.getNumDnLst12Mnths()) >= FREQUENT_DONATIONS_CRITERIA)
					record.setSeg(segment.FREQUENT.getName());
				else if(monthsFromLastDonation > LAPSED_DONATIONS_CRITERIA)
					if(monthsFromLastDonation < DEEP_LAPSED_DONATIONS_CRITERIA)
						record.setSeg(segment.LAPSED.getName());
					else
						record.setSeg(segment.DEEP_LAPSED.getName());
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
