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
import com.mom.dfuze.ui.UserDecimalInputDialog;
import com.mom.dfuze.ui.UserInputDialog;


/**
 * RegularProcess implements a RunBehavior for BIS Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/11/2024
 *
 */
public class BIS implements RunGenerosityXBehavior {

	private final String BEHAVIOR_NAME = "BIS";
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
	
	public final Pattern GIFT_FILE_ID_PATTERN = Pattern.compile("donor_id", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_AMOUNT_PATTERN = Pattern.compile("amount", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_DATE_PATTERN = Pattern.compile("date", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_TYPE_PATTERN = Pattern.compile("memo", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_APPEAL_PATTERN = Pattern.compile("designationName", Pattern.CASE_INSENSITIVE);
	
	public final Pattern MONTHLY_DESIGNATION_PATTERN = Pattern.compile("recurring", Pattern.CASE_INSENSITIVE);
	public final Pattern CHILD_SPONSOR_PATTERN = Pattern.compile("CHILD SPONSORSHIP", Pattern.CASE_INSENSITIVE);

	public enum segment {
		TOP("Top"),
		MONTHLY("Monthly"),
		CHILD_SPONSOR("Child Sponsor"),
		FREQUENT("Frequent"),
		LAPSED("Lapsed"),
		DEEP_LAPSED("Deep Lapsed"),
		NEW("New"),
		ACTIVE("Active");
		
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
		
		// create the salutation
		//createSaluation(userData);
		
		// Remove leading zeros to match Giving History
		removeLeadingZeroFromId(userData);
		
		// load gifts file as record list,	infer gift date format to use
		List<Record> giftList = getGiftList(); // dfInId, dfLstDnAmt, dfLstGftDat, dfSeg is set
		String giftDateFormat = DateTimeInferer.inferFormat(giftList, UserData.fieldName.LAST_DONATION_DATE.getName());
				
		// Sort the gift list by latest donation
		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(giftDateFormat);
		giftList.sort(new RecordSorters.CompareByFieldDescAsDate(UserData.fieldName.LAST_DONATION_DATE.getName(), datetimeFormatter));
				
		// Convert the gifts into a gift history map (id, List(history))
		HashMap<String, List<BISGiftHistory>> giftHistoryMap = convertGiftsToMap(giftList, giftDateFormat);
		
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
		
		// Set the gift amounts
		setGiftArrays(userData);
		
		setGiftArrayMetrics(userData);
		
		
		// value priority, high = good
		Analyze.prioritizeRFM(userData);
		
		formatAmounts(userData);
		
		userData.getRecordList().sort(new RecordSorters.CompareByFieldDescAsNumber(UserData.fieldName.PRIORITY.getName()));

		userData.setDfHeaders(new String[] {
				UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
				UserData.fieldName.LAST_DONATION_DATE.getName(),
				UserData.fieldName.FIRST_DONATION_AMOUNT.getName(),
				UserData.fieldName.FIRST_DONATION_DATE.getName(),
				UserData.fieldName.TOTAL_DONATION_AMOUNT.getName(),
				UserData.fieldName.TOTAL_DONATION_AMOUNT_LAST_12_MONTHS.getName(),
				UserData.fieldName.NUMBER_OF_DONATIONS.getName(),
				UserData.fieldName.NUMBER_OF_DONATIONS_LAST_12_MONTHS.getName(),
				UserData.fieldName.LARGEST_DONATION_AMOUNT.getName(),
				UserData.fieldName.PENULTIMATE_AMOUNT.getName(),
				UserData.fieldName.PENULTIMATE_DATE.getName(),
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
				UserData.fieldName.PROVIDE1.getName(),
				UserData.fieldName.PROVIDE2.getName(),
				UserData.fieldName.PROVIDE3.getName(),
				UserData.fieldName.OPEN_DONATION_AMOUNT.getName(),
				UserData.fieldName.PRIORITY.getName()
		});
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
	
	private void removeLeadingZeroFromId(UserData userData) {
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
	
		int idIndex, amountIndex, giftDateIndex, giftTypeIndex, giftAppealIndex = -1;
		
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
		
		// gift type
		dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the Gift Type field.");
		dsd.setValues(headers);

		for(int i = 0; i < headers.length; ++i) {
			if(GIFT_FILE_TYPE_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		giftTypeIndex = dsd.getSelectedValueIndex();
		
		if(!indexSet.add(giftTypeIndex))
			throw new Exception("The Gift Type field has been mapped more than once.");
		
		// appeal
		dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the Designation field.");
		dsd.setValues(headers);

		for(int i = 0; i < headers.length; ++i) {
			if(GIFT_FILE_APPEAL_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		giftAppealIndex = dsd.getSelectedValueIndex();
				
		if(!indexSet.add(giftAppealIndex))
			throw new Exception("The Designation field has been mapped more than once.");
		
		List<Record> giftsList = new ArrayList<>();
		
		giftFile.remove(0); // remove header row
		
		for(int i = 0; i < giftFile.size(); ++i) {
			String id = giftFile.get(i).get(idIndex).replaceAll("[^a-zA-Z0-9_]", "").replaceFirst("^0+", ""); // Remove leading zeros
			String giftAmount = giftFile.get(i).get(amountIndex).replaceAll("[^0-9\\.-]", "");
			String giftDate = giftFile.get(i).get(giftDateIndex).replaceAll("\\d+:.*$", "").trim().replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/").trim();
			String giftType = giftFile.get(i).get(giftTypeIndex).trim();
			String giftAppeal = giftFile.get(i).get(giftAppealIndex).trim();
			
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
					.setRecType(giftType)
					.setSeg(giftAppeal)
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
	private HashMap<String, List<BISGiftHistory>> convertGiftsToMap(List<Record> giftList, String giftDateFormat) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(giftDateFormat);

		// id, giftHistory
		HashMap<String, List<BISGiftHistory>> giftHistoryMap = new HashMap<>();

		for(Record record : giftList) {
			double giftAmount = (Validators.isNumber(record.getLstDnAmt())) ? Double.parseDouble(record.getLstDnAmt()) : 0.0;

			if(Validators.isStringOfDateFormat(record.getLstDnDat(), giftDateFormat)) {
				//System.out.println(record.getLstDnDat() + " vs " + giftDateFormat);
				LocalDate giftDate = LocalDate.parse(record.getLstDnDat(), formatter);
				//System.out.println(giftDate.toString());
				BISGiftHistory history = new BISGiftHistory(
						record.getInId(),
						giftAmount,
						giftDate,
						record.getRecType(),
						record.getSeg()
						);

				if(!giftHistoryMap.containsKey(record.getInId()))
					giftHistoryMap.put(record.getInId(), new ArrayList<BISGiftHistory>());

				giftHistoryMap.get(record.getInId()).add(history);
			}
		}

		return giftHistoryMap;
	}
	
	private void processGifts(UserData userData, HashMap<String, List<BISGiftHistory>> giftHistoryMap) {
		final int MONTHS6 = 6;
		final int MONTHS12 = 12;
		final int MONTHS18 = 18;
		
		final double TOP_DONOR_AMOUNT = 500.0;

		String now = String.valueOf(LocalDate.now());
		
		for(int i = 0; i < userData.getRecordList().size(); ++i) {
			Record record = userData.getRecordList().get(i);
			
			// Default values
			record.setLstDnAmt("0");
			record.setLstDnDat("1900-01-01");
			record.setFstDnAmt("0");
			record.setFstDnDat("1900-01-01");
			record.setTtlDnAmt("0");
			record.setTtlDnAmtLst12Mnths("0");
			record.setNumDn("0");
			record.setLrgDnAmt("0");
			record.setDnAmtArr("");
			record.setRScore("99999");
			record.setFScore("0");
			record.setMScore("0");
			record.setNumDnLst12Mnths("0");
			record.setMonth("");
			record.setAppeal("");
			record.setPenultAmt("0");
			record.setPenultDat("1900-01-01");
			record.setStatus("");
			
			if(!giftHistoryMap.containsKey(record.getInId())) {
				System.out.println("No ID for " + record.getInId());
			}
			
			if(giftHistoryMap.containsKey(record.getInId())) {
				List<BISGiftHistory> giftHistoryList = giftHistoryMap.get(record.getInId());
				
				double totalGiftAmount = 0.0;
				int totalGifts = giftHistoryList.size();
				
				double totalGiftAmountLast6Months = 0.0;
				double totalGiftAmountLast12Months = 0.0;
				int totalGiftsLast12Months = 0;
				double largestGiftMadeLast24Months = 0.0;
				double largestGiftMadeLast18Months = 0.0;
				double largestGiftMadeLast6Months = 0.0;
				double largestGiftMade = 0.0;
				
				ArrayList<Double> giftAmounts = new ArrayList<>();
				
				String commaSeparatedHistory = giftHistoryList
				        .stream()
				        .map(String::valueOf)
				        .collect(Collectors.joining(","));
				
				for(int j = 0; j < giftHistoryList.size(); ++j) {
					BISGiftHistory giftHistory = giftHistoryList.get(j);
					
					totalGiftAmount += giftHistory.getGiftAmount();
					giftAmounts.add(giftHistory.getGiftAmount());
					
					long monthsFromDonation = getMonthsBetween(giftHistory.getGiftDate().toString(), now);
					
					if(monthsFromDonation <= MONTHS6 && monthsFromDonation > -1)
						totalGiftAmountLast6Months += giftHistory.getGiftAmount();
					
					if(monthsFromDonation <= MONTHS12 && monthsFromDonation > -1) {
						++totalGiftsLast12Months;
						totalGiftAmountLast12Months += giftHistory.getGiftAmount();
					}
					
					if(monthsFromDonation <= MONTHS18 && monthsFromDonation > -1)
						if(giftHistory.getGiftAmount() > largestGiftMadeLast18Months)
							largestGiftMadeLast18Months = giftHistory.getGiftAmount();
					
					if(monthsFromDonation <= MONTHS6 && monthsFromDonation > -1)
						if(giftHistory.getGiftAmount() > largestGiftMadeLast6Months)
							largestGiftMadeLast6Months = giftHistory.getGiftAmount();
					
					if(giftHistory.getGiftAmount() > largestGiftMade)
						largestGiftMade = giftHistory.getGiftAmount();
					
					// Set top donors and child sponsors
					Matcher childSponsorMatcher = CHILD_SPONSOR_PATTERN.matcher(giftHistory.getAppeal());
					if(childSponsorMatcher.find())
						record.setSeg(segment.CHILD_SPONSOR.getName());
					
					long daysBetween = ChronoUnit.DAYS.between(giftHistory.getGiftDate(), LocalDate.now());
					
					// identify monthly donors if last gift is within 60 days and monthly id found in appeal
					if(record.getSeg() == null) {
						if(daysBetween <= 60) {
							Matcher monthlyMatcher = MONTHLY_DESIGNATION_PATTERN.matcher(giftHistory.getGiftType());
							if(monthlyMatcher.find())
								record.setSeg(segment.MONTHLY.getName());
						}
					}
					
					// this is the last donation
					if(j == 0) {
						record.setLstDnAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setLstDnDat(giftHistory.getGiftDate().toString());
						record.setRScore(String.valueOf(daysBetween));
						// Set the months from last donation
						record.setMonth(String.valueOf(monthsFromDonation));
					}
					
					// this is the first donation
					if(j == giftHistoryList.size() - 1) {
						record.setFstDnAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setFstDnDat(giftHistory.getGiftDate().toString());
					}
					
					// set penultimates (second last gift)
					if(j == 1 && giftHistoryList.size() > 1) {
						record.setPenultAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setPenultDat(giftHistory.getGiftDate().toString());
					}

				}
				
				double monetarySum = giftAmounts.stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();
				
				if(largestGiftMadeLast6Months >= TOP_DONOR_AMOUNT)
					record.setSeg(segment.TOP.getName());
				
				record.setMScore(String.valueOf(monetarySum));
				record.setFScore(String.valueOf(totalGifts));
				record.setTtlDnAmt(String.valueOf(totalGiftAmount));
				record.setTtlDnAmtLst12Mnths(String.valueOf(totalGiftAmountLast12Months));
				record.setNumDn(String.valueOf(totalGifts));
				record.setLrgDnAmt(String.valueOf(largestGiftMade));
				record.setDnAmtArr(commaSeparatedHistory);
				record.setNumDnLst12Mnths(String.valueOf(totalGiftsLast12Months)); 
			}
			
			
		}
	}	
	
	private void setCampaignCode(UserData userData, String campaignCode) {
		for(Record record : userData.getRecordList()) {
			if(record.getSeg().equalsIgnoreCase(segment.TOP.getName()))
				record.setSegCode(campaignCode + "-MD1");
			else if(record.getSeg().equalsIgnoreCase(segment.MONTHLY.getName()))
				record.setSegCode(campaignCode + "-M1");
			else if(record.getSeg().equalsIgnoreCase(segment.CHILD_SPONSOR.getName()))
				record.setSegCode(campaignCode + "-ZCHILD1");
			else if(record.getSeg().equalsIgnoreCase(segment.ACTIVE.getName()))
				record.setSegCode(campaignCode + "-A");
			else if(record.getSeg().equalsIgnoreCase(segment.FREQUENT.getName()))
				record.setSegCode(campaignCode + "-F0");
			else if(record.getSeg().equalsIgnoreCase(segment.LAPSED.getName()))
				record.setSegCode(campaignCode + "-L0");
			else if(record.getSeg().equalsIgnoreCase(segment.DEEP_LAPSED.getName()))
				record.setSegCode(campaignCode + "-DL");
			else if(record.getSeg().equalsIgnoreCase(segment.NEW.getName()))
				record.setSegCode(campaignCode + "-N0");
		}
	}
	
	// Prompt the user for the donation metric line in asks
	private String getCampaignCode() {
		UserInputDialog uid = new UserInputDialog(UiController.getMainFrame(), "Enter the campaign code. (Ex. XMAS25-01-DM)");
		uid.getTextField().setText("XMAS25-01-DM");
		uid.setVisible(true);

		if(uid.getIsNextPressed())
			return uid.getUserInput().toUpperCase().trim();

		return "";
	}
	
	// Convert the gift arrays to metrics
	private void setGiftArrayMetrics(UserData userData) {
		
		DecimalFormat formatter = new DecimalFormat("#,###.00");
		DecimalFormat provides_formatter = new DecimalFormat("#,###");
		
		for(Record record : userData.getRecordList()) {
			
			String less = "";
			String single = "";
			String plural = "";
			String open = "";
			
			String less1 = "$%s helps provide 1 week of meals for a child";
			String single1 = "$%s provides 1 week of meals for a child.";
			String plural1 = "$%s provides %s weeks of meals for a child.";
			String open1 = "$________ helps provide meals for as many children as possible!";
			
			String less2 = "$%s helps begin the journey for children to reunite with their families.";
			String single2 = "$%s begins the journey to reunite a child with their family";
			String plural2 = "$%s begins the journey for %s children to reunite with their families.";
			String open2 = "$________ helps reunite as many children as possible with their families.";
			
			double costPerUnit;
			double lastGiftAmount = Double.parseDouble(record.getLstDnAmt());
			
			if(lastGiftAmount < 250) {
				costPerUnit = 25.00;
				less = less1;
				single = single1;
				plural = plural1;
				open = open1;
			} else {
				costPerUnit = 250.00;
				less = less2;
				single = single2;
				plural = plural2;
				open = open2;
			}
			
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
	

	// Set the gift arrays
	private void setGiftArrays(UserData userData) {
		final BigDecimal LAST_GIFT_ROUNDING_AMOUNT = new BigDecimal("5.00");

		for(Record record : userData.getRecordList()) {
			double defaultAskAmount = 0.0;
			double lastGiftAmount = 0.0;
			
			if(Validators.isNumber(record.getLstDnAmt()))
				lastGiftAmount = Double.parseDouble(record.getLstDnAmt());

			if(lastGiftAmount < 250)
				defaultAskAmount = 25.00;
			else
				defaultAskAmount = 250.00;

			
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
			
			if(!donorSegment.equalsIgnoreCase(segment.TOP.getName()) && !donorSegment.equalsIgnoreCase(segment.CHILD_SPONSOR.getName())
					&& !donorSegment.equalsIgnoreCase(segment.MONTHLY.getName())) // only create gifts for certain segments
				setGiftArray(record, lastDonationRoundedUpByFive, defaultAskAmount);

		}
	}
	
	// logic to build non lapsed gift arrays
	private void setGiftArray(Record record, Double lastDonationRoundedUpByFive, double defaultAskAmount) {
		int askTier = 1;
		
		// Small default amount multiplier
		double sdam = 1;
		
		if(defaultAskAmount < 10)
			sdam = 4;
		else if(defaultAskAmount < 20)
			sdam = 2;
		
		//if(defaultAskAmount >= 100)
		//	sdam = 0.5;
		
		if(lastDonationRoundedUpByFive < defaultAskAmount * (3.0 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * sdam));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (2.0 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (4.0 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (6.0 * sdam)));
			askTier = 7;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (5.0 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (4.0 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (6.0 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (8.0 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (10.0 * sdam)));
			askTier = 6;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (7.0 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (6.0 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (8.0 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (10.0 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (12.0 * sdam)));
			askTier = 5;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (11.0 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (8.0 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (10.0 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (14.0 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (16.0 * sdam)));
			askTier = 4;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (15.0 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (12.0 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (14.0 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (18.0 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (20.0 * sdam)));
			askTier = 3;
		} else {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (16.0 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (18.0 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (22.0 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (24.0 * sdam)));
			askTier = 2;
		}

		if(record.getSeg().equalsIgnoreCase(segment.ACTIVE.getName()))
			record.setSegCode(record.getSegCode() + askTier);
	}
	
	// Logic to categorize donors into segments
	private void setSegment(UserData userData) {
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
					record.setSeg(segment.ACTIVE.getName());
				else if(Integer.parseInt(record.getNumDnLst12Mnths()) >= FREQUENT_DONATIONS_CRITERIA)
					record.setSeg(segment.FREQUENT.getName());
				else if(monthsFromFirstDonation >= 0 && monthsFromFirstDonation <= NEW_DONOR_MONTHS_CRITERIA)
					record.setSeg(segment.NEW.getName());
				else if(monthsFromLastDonation > DEEP_LAPSED_DONATIONS_CRITERIA)
					record.setSeg(segment.DEEP_LAPSED.getName());
				else if(monthsFromLastDonation > LAPSED_DONATIONS_CRITERIA)
					record.setSeg(segment.LAPSED.getName());
				else
					record.setSeg(segment.ACTIVE.getName());
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
