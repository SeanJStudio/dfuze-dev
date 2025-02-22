/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.generosityx;


import java.io.File;
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

import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Analyze;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.DateTimeInferer;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.io.FileExporter;
import com.mom.dfuze.io.FileIngestor;
import com.mom.dfuze.ui.DropdownSelectDialog;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserDecimalInputDialog;
import com.mom.dfuze.ui.UserInputDialog;


/**
 * ItIsWritten implements a RunBehavior for GenerosityX Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 11/15/2023
 *
 */
public class ItIsWritten implements RunGenerosityXBehavior {

	private final String BEHAVIOR_NAME = "It Is Written";
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.IN_ID.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.COMPANY.getName(),
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName(),
			UserData.fieldName.RECORD_TYPE.getName() // Use this to hold the monthly identifier
	};

	private String DESCRIPTION = 
					"<html>"
					+ "Description<br/>"
					+ "<ul>"
					+ "<li>RFM analysis is used to segment donors</li>"
					+ "</ul>"
					+ "Instructions<br/>"
					+ "<ol>"
					+ "<li>Load the donor data file</li>"
					+ "<li>Map record type to the monthly identifier (Recurring donor status)</li>"
					+ "<li>Load the gifts data file when prompted</li>"
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
	
	public final Pattern GIFT_FILE_ID_PATTERN = Pattern.compile("(^|\\s+)id(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_AMOUNT_PATTERN = Pattern.compile("(^|\\s+)amount(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_DATE_PATTERN = Pattern.compile("(^|\\s+)date(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_TRANSACTION_TYPE_PATTERN = Pattern.compile("(^|\\s+)transaction(\\s+|$)", Pattern.CASE_INSENSITIVE);
	
	//public final Pattern GENERAL_DESIGNATION_PATTERN = Pattern.compile("(^|\\s+)general(\\s+|$)", Pattern.CASE_INSENSITIVE);
	//public final Pattern IMPACT_DESIGNATION_PATTERN = Pattern.compile("(^|\\s+)impact(\\s+|$)", Pattern.CASE_INSENSITIVE);

	public enum segment {
		NEW("New"),
		LAPSED("Lapsed"),
		FREQUENT("Frequent"),
		TOP("Top"),
		ACTIVE("Active"),
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

		// add records to userData, set userData record members via required fields
		userData.autoSetRecordList();
		userData.autoSetRecordListFields(REQUIRED_FIELDS);
		
		// Create full name
		setName(userData);
		
		// Create salutation
		setSalutation(userData);
		
		// Add the seeds
		addSeeds(userData);
		
		// Clean the address
		cleanAddresses(userData);
		
		// load gifts file as record list,	infer gift date format to use
		List<Record> giftList = getGiftList(); // dfInId, dfLstDnAmt, dfLstGftDat, dfSeg is set
		String giftDateFormat = DateTimeInferer.inferFormat(giftList, UserData.fieldName.LAST_DONATION_DATE.getName());
		
		// Sort the gift list by latest donation
		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(giftDateFormat);
		giftList.sort(new RecordSorters.CompareByFieldDescAsDate(UserData.fieldName.LAST_DONATION_DATE.getName(), datetimeFormatter));
		
		// Convert the gifts into a gift history map (id, List(history))
		HashMap<String, List<IIWGiftHistory>> giftHistoryMap = convertGiftsToMap(giftList, giftDateFormat);
		
		// process gift history into main donor list
		processGifts(userData, giftHistoryMap);
		
		// build RFM score
		setRFM(userData);
		
		//min max rfm
		Analyze.minMaxRFM(userData);
		
		int hasGiftArraysButtonPressed = JOptionPane.showConfirmDialog(UiController.getMainFrame(),
				"Does this campaign require gift arrays?",
				"Gift Calculations", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if(hasGiftArraysButtonPressed == JOptionPane.YES_OPTION) {
			
			int hasGiftMetricsButtonPressed = JOptionPane.showConfirmDialog(UiController.getMainFrame(),
					"Does this campaign require gift metrics?\n\nEx. $30 helps reach 120 people",
					"Gift Calculations", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			setSegment2(userData);								// Place into categories
			setCampaignCode(userData, getCampaignCode());		// Set the campaign code
			setGiftArrays(userData); 							// Set the gift amounts
			if(hasGiftMetricsButtonPressed == JOptionPane.YES_OPTION)
				setGiftArrayMetrics(userData, getCostPerUnit()); 	// Set the metrics via user input
		} else {
			setSegment2(userData);
			setCampaignCode2(userData, getCampaignCode());
			// Fill in placeholder values
			for(Record record : userData.getRecordList()) {
				record.setDn1Amt("");
				record.setDn2Amt("");
				record.setDn3Amt("");
				record.setDn4Amt("");
				record.setODnAmt("");
			}
		}
		
		// value priority, high = good
		Analyze.prioritizeRFM(userData);
		
		userData.getRecordList().sort(new RecordSorters.CompareByFieldDescAsNumber(UserData.fieldName.PRIORITY.getName()));

		userData.setDfHeaders(new String[] {
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.COMPANY.getName(),
				UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
				UserData.fieldName.LAST_DONATION_DATE.getName(),
				UserData.fieldName.FIRST_DONATION_AMOUNT.getName(),
				UserData.fieldName.FIRST_DONATION_DATE.getName(),
				UserData.fieldName.TOTAL_DONATION_AMOUNT.getName(),
				UserData.fieldName.NUMBER_OF_DONATIONS.getName(),
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
				UserData.fieldName.OPEN_DONATION_AMOUNT.getName(),
				UserData.fieldName.PRIORITY.getName()
		});
		
		if(hasGiftArraysButtonPressed == JOptionPane.YES_OPTION)
			removeZeroGiftRecords(userData);
	}
	
	private void cleanAddresses(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String[] inData = record.getDfInData();
			inData[userData.getAdd1Index()] = inData[userData.getAdd1Index()].replaceAll("\"", "");
			record.setDfInData(inData);
		}
			
	}
	
	private void removeZeroGiftRecords(UserData userData) throws Exception {
		
		List<Record> removed = new ArrayList<>();
		
		for(int i = userData.getRecordList().size() - 1; i >= 0; --i) {
			Record record = userData.getRecordList().get(i);
			
			if(record.getNumDn().equalsIgnoreCase("0"))
				if(!record.getDfInData()[userData.getInIdIndex()].toLowerCase().contains("seed"))
					removed.add(userData.getRecordList().remove(i));
		}
		
		if(removed.size() == 0)
			return;
		
		File file = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_NO_GIFTS" + FileExtensions.XLSX);
		
		FileExporter.exportData(userData.getExportHeaders(), userData.getExportData(removed), file);
		
		JOptionPane.showMessageDialog(UiController.getMainFrame(), String.format("%d records with zero gifts removed and exported.", removed.size()), "Results", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void addSeeds(UserData userData) {
		Record template = userData.getRecordList().get(userData.getRecordList().size() - 1);
		
		String[] inDataMatt = Arrays.copyOf(template.getDfInData(), template.getDfInData().length);
		Arrays.fill(inDataMatt, "");
		inDataMatt[userData.getInIdIndex()] = "SEEDER_MATT";
		inDataMatt[userData.getFstNameIndex()] = "Matt";
		inDataMatt[userData.getLstNameIndex()] = "Hussey";
		inDataMatt[userData.getCmpnyIndex()] = "";
		inDataMatt[userData.getAdd1Index()] = "4531 Lindholm Road";
		inDataMatt[userData.getCityIndex()] = "Victoria";
		inDataMatt[userData.getProvIndex()] = "BC";
		inDataMatt[userData.getPCodeIndex()] = "V9C 4C5";
		
		String[] inDataBecca = Arrays.copyOf(template.getDfInData(), template.getDfInData().length);
		Arrays.fill(inDataBecca, "");
		inDataBecca[userData.getInIdIndex()] = "SEEDER_BECCA";
		inDataBecca[userData.getFstNameIndex()] = "Becca";
		inDataBecca[userData.getLstNameIndex()] = "Gust";
		inDataBecca[userData.getCmpnyIndex()] = "";
		inDataBecca[userData.getAdd1Index()] = "11-19330 69 Ave";
		inDataBecca[userData.getCityIndex()] = "Surrey";
		inDataBecca[userData.getProvIndex()] = "BC";
		inDataBecca[userData.getPCodeIndex()] = "V4N 0Z2";
		
		String[] inDataIIW = Arrays.copyOf(template.getDfInData(), template.getDfInData().length);
		Arrays.fill(inDataIIW, "");
		inDataIIW[userData.getInIdIndex()] = "SEEDER_IIW";
		inDataIIW[userData.getFstNameIndex()] = "";
		inDataIIW[userData.getLstNameIndex()] = "";
		inDataIIW[userData.getCmpnyIndex()] = "It Is Written";
		inDataIIW[userData.getAdd1Index()] = "Box 2010";
		inDataIIW[userData.getCityIndex()] = "Oshawa";
		inDataIIW[userData.getProvIndex()] = "ON";
		inDataIIW[userData.getPCodeIndex()] = "L1H 7V4";
		
		Record seedMatt = new Record.Builder(9999999, inDataMatt, "", "", "")
				.setDearSal("Matt")
				.setFstName("Matt")
				.setLstName("Hussey")
				.setCmpny("")
				.setNam1("Matt Hussey")
				.setInId("SEEDER_MATT")
				.setRecType("")
				.build();
		
		Record seedBecca = new Record.Builder(9999998, inDataBecca, "", "", "")
				.setDearSal("Becca")
				.setFstName("Becca")
				.setLstName("Gust")
				.setCmpny("")
				.setNam1("Becca Gust")
				.setInId("SEEDER_BECCA")
				.setRecType("")
				.build();
		
		Record seedIIW = new Record.Builder(9999997, inDataIIW, "", "", "")
				.setDearSal("Friend")
				.setFstName("")
				.setLstName("")
				.setCmpny("It Is Written")
				.setNam1("")
				.setInId("SEEDER_IIW")
				.setRecType("")
				.build();
		
		userData.add(seedMatt);
		userData.add(seedBecca);
		userData.add(seedIIW);
	}
	
	// Trims extra spaces, cases first/last names and combines them
	private void setName(UserData userData) {
		for(Record record : userData.getRecordList())
			record.setNam1(Common.caseName(record.getFstName() + " " + record.getLstName()).replaceAll("  ", " ").trim());
	}
	
	private void setSalutation(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String dearSal = Common.caseName(record.getFstName()).replaceAll("  ", " ").trim();
			if(dearSal.length() <= 1 // check for bad sal
					|| dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
					|| dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
					|| dearSal.length() == 2 && !Validators.hasVowel(dearSal)
					) {
				dearSal = "Friend";
			}
			record.setDearSal(dearSal);
		}
	}
	
	private List<Record> getGiftList() throws Exception {
		JOptionPane.showMessageDialog(
				UiController.getMainFrame(),
				"Please load the associated gift file now.",
				"Load gifts",
				JOptionPane.INFORMATION_MESSAGE);
		
		List<List<String>> giftFile = FileIngestor.ingest();	// get the gift file	
		HashSet<Integer> indexSet = new HashSet<>();
	
		int idIndex, amountIndex, giftDateIndex, designationIndex = -1;
		
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
		dsd = new DropdownSelectDialog(UiController.getMainFrame(), headers, "Select the Transcation Type field.");
		dsd.setValues(headers);

		for(int i = 0; i < headers.length; ++i) {
			if(GIFT_FILE_TRANSACTION_TYPE_PATTERN.matcher(headers[i]).find()) {
				dsd.getComboBoxValues().setSelectedIndex(i);
				break;
			}
		}

		dsd.setVisible(true);

		if(!dsd.isNextPressed())
			throw new Exception("Selection cancelled, please restart job.");

		designationIndex = dsd.getSelectedValueIndex();
		
		if(!indexSet.add(designationIndex))
			throw new Exception("The gift designation field has been mapped more than once.");
		
		List<Record> giftsList = new ArrayList<>();
		
		giftFile.remove(0); // remove header row
		
		for(int i = 0; i < giftFile.size(); ++i) {
			String id = giftFile.get(i).get(idIndex).replaceAll("[^a-zA-Z0-9_]", "");
			String giftAmount = giftFile.get(i).get(amountIndex).replaceAll("[^0-9\\.-]", "");
			String giftDate = giftFile.get(i).get(giftDateIndex).replaceAll("\\d+:.*$", "").trim().replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/").trim();
			String giftType = giftFile.get(i).get(designationIndex).trim().toLowerCase();
			
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
					.setSeg(giftType)
					.build();
			
			
			if(giftAmount.contains("-")) // Skip negative dollar amounts
				System.out.println("Skipping negative amount of: " + giftAmount);
			else if(!giftType.equalsIgnoreCase("donation")) // skip non "donation" gifts
				System.out.println("Skipping non donation of: " + giftType);
			else if(giftAmount.length() == 0 || isGiftZero) // Skip empty gifts
				System.out.println("Skipping empty amount");
			else
				giftsList.add(record);
		}
		
		return giftsList;
	}
	
	// converts a list of gifts into a HashMap
	private HashMap<String, List<IIWGiftHistory>> convertGiftsToMap(List<Record> giftList, String giftDateFormat) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(giftDateFormat);
		
		// id, giftHistory
		HashMap<String, List<IIWGiftHistory>> giftHistoryMap = new HashMap<>();
		
		for(Record record : giftList) {
			double giftAmount = (Validators.isNumber(record.getLstDnAmt())) ? Double.parseDouble(record.getLstDnAmt()) : 0.0;
			
			if(Validators.isStringOfDateFormat(record.getLstDnDat(), giftDateFormat)) {
				LocalDate giftDate = LocalDate.parse(record.getLstDnDat(), formatter);
				IIWGiftHistory history = new IIWGiftHistory(
						record.getInId(),
						giftAmount,
						giftDate,
						record.getSeg()
						);
				
				if(!giftHistoryMap.containsKey(record.getInId()))
					giftHistoryMap.put(record.getInId(), new ArrayList<IIWGiftHistory>());
				
				giftHistoryMap.get(record.getInId()).add(history);
			}
		}
		
		return giftHistoryMap;
	}
	
	private void processGifts(UserData userData, HashMap<String, List<IIWGiftHistory>> giftHistoryMap) {
		final int MONTHS24 = 24;
		final int MONTHS12 = 12;
		String now = String.valueOf(LocalDate.now());
		
		for(int i = 0; i < userData.getRecordList().size(); ++i) {
			Record record = userData.getRecordList().get(i);
			
			if(!giftHistoryMap.containsKey(record.getInId())) {
				record.setLstDnAmt("0.0");
				record.setLstDnDat("1900-01-01");
				record.setFstDnAmt("0.0");
				record.setFstDnDat("1900-01-01");
				record.setTtlDnAmt("0.0");
				record.setNumDn("0");
				record.setDnAmtArr("");
				record.setRScore("99999");
				record.setFScore("0");
				record.setMScore("0");
				record.setQuantity("0"); // Using this to hold the number of gifts in last 12 months
				record.setYear("0"); // Using this to hold the total donation amount of last 24 months
			}
			
			if(giftHistoryMap.containsKey(record.getInId())) {
				List<IIWGiftHistory> giftHistoryList = giftHistoryMap.get(record.getInId());
				
				double totalGiftAmount = 0.0;
				int totalGifts = giftHistoryList.size();
				
				double totalGiftAmountLast24Months = 0.0;
				int totalGiftsLast12Months = 0;
				
				ArrayList<Double> giftAmounts = new ArrayList<>();
				
				String commaSeparatedHistory = giftHistoryList
				        .stream()
				        .map(String::valueOf)
				        .collect(Collectors.joining(","));
				
				for(int j = 0; j < giftHistoryList.size(); ++j) {
					IIWGiftHistory giftHistory = giftHistoryList.get(j);
					
					totalGiftAmount += giftHistory.getGiftAmount();
					giftAmounts.add(giftHistory.getGiftAmount());
					
					long monthsFromDonation = getMonthsBetween(giftHistory.getGiftDate().toString(), now);
					
					if(monthsFromDonation <= MONTHS24)
						totalGiftAmountLast24Months += giftHistory.getGiftAmount();
					
					if(monthsFromDonation <= MONTHS12)
						++totalGiftsLast12Months;
					
					if(j == 0) {
						record.setLstDnAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setLstDnDat(giftHistory.getGiftDate().toString());
						long daysBetween = ChronoUnit.DAYS.between(giftHistory.getGiftDate(), LocalDate.now());
						record.setRScore(String.valueOf(daysBetween));
					}
					
					if(j == giftHistoryList.size() - 1) {
						record.setFstDnAmt(String.valueOf(giftHistory.getGiftAmount()));
						record.setFstDnDat(giftHistory.getGiftDate().toString());
					}

				}
				
				// set the package version and letter versions
				/*if(record.getRecType().toLowerCase().contains("true")) {
					record.setPkgVer(segment.MONTHLY.getName());
					record.setLetVer(segment.MONTHLY.getName());
				} else {
					record.setPkgVer("General");
					record.setLetVer("General");
				}*/
				
				double monetarySum = giftAmounts.stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();
				
				record.setMScore(String.valueOf(monetarySum));
				record.setFScore(String.valueOf(totalGifts));
				record.setTtlDnAmt(String.valueOf(totalGiftAmount));
				record.setNumDn(String.valueOf(totalGifts));
				record.setDnAmtArr(commaSeparatedHistory);
				record.setQuantity(String.valueOf(totalGiftsLast12Months)); // Using this to hold the number of gifts of last 12 months
				record.setYear(String.valueOf(totalGiftAmountLast24Months)); // Using this to hold the total donation amount of last 24 months
			}
			
			
		}
	}
	
	private double calculateMedian(ArrayList<Double> numArrayList) {
		double defaultValue = 0.0;
		
		if(numArrayList.size() == 0)
			return defaultValue;
		
		Double[] numArray = numArrayList.toArray(new Double[0]);
		Arrays.sort(numArray);
		double median;
		
		if (numArray.length % 2 == 0)
		    median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
		else
		    median = (double) numArray[numArray.length/2];
		
		return median;
	}
	
	// Sets the priority of a record based on RFM where R is weighted * 2
	private void setPriority(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String[] rfmParts = record.getRfmScore().split("_");
			int sum = 0;
			for(int i = 0; i < rfmParts.length; ++i)
				if(Validators.isNumber(rfmParts[i]))
					if(i == 0) // double recency values to keep new donors
						sum += Integer.parseInt(rfmParts[i])*5;
					else if(i == 1)
						sum += Integer.parseInt(rfmParts[i])*3;
					else
						sum += Integer.parseInt(rfmParts[i])*2;
						
			
			sum *= 1000000;
			
			String tempLastDonation = record.getLstDnAmt().replaceAll("[^0-9\\.]", "");
			
			if(Validators.isNumber(tempLastDonation))
				sum += Double.parseDouble(tempLastDonation);
			
			if(record.getInId().toLowerCase().contains("seed"))
				sum = 999999999;
			
			record.setPriority(String.valueOf(sum));
		}
	}
	
	private void setCampaignCode(UserData userData, String campaignCode) {
		
		for(Record record : userData.getRecordList()) {
			if(record.getSeg().equalsIgnoreCase(segment.TOP.getName()))
				record.setSegCode(campaignCode + "-MD1");
			else if(record.getSeg().equalsIgnoreCase(segment.ACTIVE.getName()))
				record.setSegCode(campaignCode + "-A");
			else if(record.getSeg().equalsIgnoreCase(segment.MONTHLY.getName()))
				record.setSegCode(campaignCode + "-M1");
			else if(record.getSeg().equalsIgnoreCase(segment.FREQUENT.getName()))
				record.setSegCode(campaignCode + "-F0");
			else if(record.getSeg().equalsIgnoreCase(segment.LAPSED.getName()))
				record.setSegCode(campaignCode + "-L0");
			else if(record.getSeg().equalsIgnoreCase(segment.NEW.getName()))
				record.setSegCode(campaignCode + "-N0");
		}
	}
	
	private void setCampaignCode2(UserData userData, String campaignCode) {
		
		for(Record record : userData.getRecordList()) {
			if(record.getSeg().equalsIgnoreCase(segment.TOP.getName()))
				record.setSegCode(campaignCode + "-MD1");
			else if(record.getSeg().equalsIgnoreCase(segment.ACTIVE.getName()))
				record.setSegCode(campaignCode + "-A");
			else if(record.getSeg().equalsIgnoreCase(segment.MONTHLY.getName()))
				record.setSegCode(campaignCode + "-M1");
			else if(record.getSeg().equalsIgnoreCase(segment.FREQUENT.getName()))
				record.setSegCode(campaignCode + "-F0");
			else if(record.getSeg().equalsIgnoreCase(segment.LAPSED.getName()))
				record.setSegCode(campaignCode + "-L0");
			else if(record.getSeg().equalsIgnoreCase(segment.NEW.getName()))
				record.setSegCode(campaignCode + "-N0");
		}
	}
	
	
	// Prompt the user for the donation metric line in asks
	private String getCampaignCode() {
		UserInputDialog uid = new UserInputDialog(UiController.getMainFrame(), "Enter the campaign code (Ex. NOV23-01-DM)");
		uid.getTextField().setText("NOV23-01-DM");
		uid.setVisible(true);

		if(uid.getIsNextPressed()) {
			String campaignCode = uid.getUserInput().toUpperCase().trim();
			if(campaignCode.endsWith("-"))
				campaignCode = campaignCode.substring(0, campaignCode.length() - 1);
			return campaignCode;
		}

		return "";
	}
	
	// Prompt the user for the cost per unit for donation metrics
	private double getCostPerUnit() {
		UserDecimalInputDialog udid = new UserDecimalInputDialog(UiController.getMainFrame(), "Enter the gift metric unit cost. (Ex if 1 unit costs $0.25, enter 0.25)");
		udid.getTextField().setText("0.25");
		udid.setVisible(true);
		
		double costPerUnit = 0.25;
		
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
		String less = "$%s helps reach people through our TV program so they can hear the Gospel.";
		//String single = "$%s helps reach 120 people through our TV program so they can hear the Gospel.";
		String plural = "$%s reaches %s people through our TV program so they can hear the Gospel.";
		String open = "$________ to reach as many people as possible through our TV program so they can hear the Gospel.";
		
		less = getGiftMetricLine("Enter the gift metric line to use for less than 1 unit.", less).trim();
		//single = getGiftMetricLine("Enter the gift metric line to use for 1 unit.", single).trim();
		plural = getGiftMetricLine("Enter the gift metric line to use for more than 1 unit.", plural).trim();
		open = getGiftMetricLine("Enter the gift metric line to use for open asks.", open).trim();
		
		DecimalFormat formatter = new DecimalFormat("#,###");
		double MINIMUM_AMOUNT = 30.0;
		
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
				if(dn1 < MINIMUM_AMOUNT) {
					record.setDn1Amt(String.format(less, formatter.format(dn1)));
				} else {
					record.setDn1Amt(String.format(plural, formatter.format(dn1), formatter.format(difference1)));
				}
				
				// Donation 2
				if(dn2 < MINIMUM_AMOUNT) {
					record.setDn2Amt(String.format(less, formatter.format(dn2)));
				} else {
					record.setDn2Amt(String.format(plural, formatter.format(dn2), formatter.format(difference2)));
				}
				
				// Donation 3
				if(dn3 < MINIMUM_AMOUNT) {
					record.setDn3Amt(String.format(less, formatter.format(dn3)));
				} else {
					record.setDn3Amt(String.format(plural, formatter.format(dn3), formatter.format(difference3)));
				}
				
				// Donation 4
				if(dn4 < MINIMUM_AMOUNT) {
					record.setDn4Amt(String.format(less, formatter.format(dn4)));
				} else {
					record.setDn4Amt(String.format(plural, formatter.format(dn4), formatter.format(difference4)));
				}
				
			}
			
			record.setODnAmt(open);
		}
	}
	
	// Set the gift arrays
	private void setGiftArrays(UserData userData) {
		final BigDecimal LAST_GIFT_ROUNDING_AMOUNT = new BigDecimal("5.00");
		
		for(Record record : userData.getRecordList()) {
			// initialize default values
			record.setDn1Amt("");
			record.setDn2Amt("");
			record.setDn3Amt("");
			record.setDn4Amt("");
			record.setODnAmt("");
			
			String donorSegment = record.getSeg();
			
			String tempLastDonation = record.getLstDnAmt().replaceAll("[^0-9\\.]", "");
			
			if(!Validators.isNumber(tempLastDonation)) // make sure its a valid number
				tempLastDonation = String.valueOf(LAST_GIFT_ROUNDING_AMOUNT.doubleValue());
			
			// last donation rounded up to nearest 5
			Double lastDonationRoundedUpByFive = new BigDecimal(tempLastDonation).divide(LAST_GIFT_ROUNDING_AMOUNT, 2, RoundingMode.CEILING)
					.setScale(0, RoundingMode.CEILING).multiply(LAST_GIFT_ROUNDING_AMOUNT).doubleValue();
			
			if(donorSegment.equalsIgnoreCase(segment.LAPSED.getName())) // set lapsed gifts
				setLapsedGiftArray(record, lastDonationRoundedUpByFive);
			else if(!donorSegment.equalsIgnoreCase(segment.MONTHLY.getName())) // set non-monthly gifts, leave monthly blank
				setNonLapsedGiftArray(record, lastDonationRoundedUpByFive);

		}
	}
	
	// Logic to build lapsed gift arrays
	private void setLapsedGiftArray(Record record, Double lastDonationRoundedUpByFive) {
	
		if(lastDonationRoundedUpByFive < 90.0) {
			record.setDn1Amt("15");
			record.setDn2Amt("30");
			record.setDn3Amt("60");
			record.setDn4Amt("90");
		} else if(lastDonationRoundedUpByFive < 180.0) {
			record.setDn1Amt("60");
			record.setDn2Amt("120");
			record.setDn3Amt("210");
			record.setDn4Amt("270");
		} else if(lastDonationRoundedUpByFive < 240.0) {
			record.setDn1Amt("120");
			record.setDn2Amt("180");
			record.setDn3Amt("240");
			record.setDn4Amt("270");
		} else if(lastDonationRoundedUpByFive < 270.0) {
			record.setDn1Amt("150");
			record.setDn2Amt("210");
			record.setDn3Amt("270");
			record.setDn4Amt("330");
		} else if(lastDonationRoundedUpByFive < 450.0) {
			record.setDn1Amt("180");
			record.setDn2Amt("270");
			record.setDn3Amt("360");
			record.setDn4Amt("460");
		} else if(lastDonationRoundedUpByFive < 900.0) {
			record.setDn1Amt("210");
			record.setDn2Amt("300");
			record.setDn3Amt("450");
			record.setDn4Amt("600");
		}
	}
	
	// logic to build non lapsed gift arrays
	private void setNonLapsedGiftArray(Record record, Double lastDonationRoundedUpByFive) {
		
		int askTier = 1;
		
		if(lastDonationRoundedUpByFive < 90.0) {
			record.setDn1Amt("30");
			record.setDn2Amt("60");
			record.setDn3Amt("90");
			record.setDn4Amt("120");
			
			askTier = 7;
			
		} else if(lastDonationRoundedUpByFive < 180.0) {
			record.setDn1Amt("60");
			record.setDn2Amt("120");
			record.setDn3Amt("180");
			record.setDn4Amt("240");
			
			askTier = 6;
			
		} else if(lastDonationRoundedUpByFive < 240.0) {
			record.setDn1Amt("90");
			record.setDn2Amt("180");
			record.setDn3Amt("260");
			record.setDn4Amt("350");
			
			askTier = 5; 
			
		} else if(lastDonationRoundedUpByFive < 270.0) {
			record.setDn1Amt("120");
			record.setDn2Amt("210");
			record.setDn3Amt("300");
			record.setDn4Amt("400");
			
			askTier = 4; 
			
		} else if(lastDonationRoundedUpByFive < 450.0) {
			record.setDn1Amt("240");
			record.setDn2Amt("400");
			record.setDn3Amt("500");
			record.setDn4Amt("700");
			
			askTier = 3;
			
		} else if(lastDonationRoundedUpByFive < 900.0) {
			record.setDn1Amt("300");
			record.setDn2Amt("500");
			record.setDn3Amt("740");
			record.setDn4Amt("1000");
			
			askTier = 2; 
		}
		
		if(record.getSeg().equalsIgnoreCase(segment.ACTIVE.getName()))
			record.setSegCode(record.getSegCode() + askTier);
		
	}
	
	// Logic to categorize donors into segments
	private void setSegment(UserData userData) {
		final int DONATION_MONTHS_WINDOW = 13;
		final int MIN_TOP_DONATION = 900;
		final int MIN_TOP_TOTAL_DONATION = 3000;
		
		for(Record record : userData.getRecordList()) {
			String monthlyDonorIndicator = record.getRecType().toLowerCase().trim();
			
			long lifetimeMonths = getMonthsBetween(record.getFstDnDat(), record.getLstDnDat());
			String now = String.valueOf(LocalDate.now());
			long monthsFromLastDonation = getMonthsBetween(record.getLstDnDat(), now);
			
			if(record.getInId().toLowerCase().contains("seed"))
				record.setSeg(segment.ACTIVE.getName());
			else if(monthlyDonorIndicator.contains("true"))
				record.setSeg(segment.MONTHLY.getName());
			else if(TOP_PATTERN.matcher(record.getRfmScore()).find()
					&& (Double.parseDouble(record.getLstDnAmt()) >= MIN_TOP_DONATION || (Double.parseDouble(record.getTtlDnAmt()) >= MIN_TOP_TOTAL_DONATION))
					&& monthsFromLastDonation <= DONATION_MONTHS_WINDOW)
				record.setSeg(segment.TOP.getName());
			else if(NEW_PATTERN.matcher(record.getRfmScore()).find() 
					&& Double.parseDouble(record.getNumDn()) <= 2 
					&& lifetimeMonths >= 0 && lifetimeMonths <= DONATION_MONTHS_WINDOW)
				record.setSeg(segment.NEW.getName());
			else if(LAPSED_PATTERN.matcher(record.getRfmScore()).find() || monthsFromLastDonation > DONATION_MONTHS_WINDOW)
				record.setSeg(segment.LAPSED.getName());
			else if(FREQUENT_PATTERN.matcher(record.getRfmScore()).find() && Double.parseDouble(record.getNumDn()) > 3)
				record.setSeg(segment.FREQUENT.getName());
			else 
				record.setSeg(segment.ACTIVE.getName());
			
		}
			
	}
	
	private void setSegment2(UserData userData) {
		final int MAJOR_DONATION_AMOUNT = 3000;
		final int NEW_DONOR_MONTHS_CRITERIA = 6;
		final int FREQUENT_DONATIONS_CRITERIA = 3;
		String now = String.valueOf(LocalDate.now());
		for(Record record : userData.getRecordList()) {
			String monthlyDonorIndicator = record.getRecType().toLowerCase().trim();
			long monthsFromFirstDonation = getMonthsBetween(record.getFstDnDat(), now);
			
			if(record.getInId().toLowerCase().contains("seed"))
				record.setSeg(segment.ACTIVE.getName());
			else if(monthlyDonorIndicator.contains("true"))
				record.setSeg(segment.MONTHLY.getName());
			else if(Double.parseDouble(record.getYear()) >= MAJOR_DONATION_AMOUNT)
				record.setSeg(segment.TOP.getName());
			else if(monthsFromFirstDonation >= 0 && monthsFromFirstDonation <= NEW_DONOR_MONTHS_CRITERIA)
				record.setSeg(segment.NEW.getName());
			else if(Integer.parseInt(record.getQuantity()) >= FREQUENT_DONATIONS_CRITERIA)
				record.setSeg(segment.FREQUENT.getName());
			else if(Double.parseDouble(record.getYear()) == 0)
				record.setSeg(segment.LAPSED.getName());
			else
				record.setSeg(segment.ACTIVE.getName());
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
		startDate = startDate.replaceAll("[^0-9]", "-");
		endDate = endDate.replaceAll("[^0-9]", "-");
		
		if(!Validators.isStringOfDateFormat(startDate, "yyyy-MM-dd")) {
			System.out.println("Invalid startdate of " + startDate);
			return -1;
		} if(!Validators.isStringOfDateFormat(endDate, "yyyy-MM-dd")) {
			System.out.println("Invalid enddate of " + endDate);
			return -1;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
