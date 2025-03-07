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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.jobs.generosityx.AdraUkraine.segment;
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
 * RegularProcess implements a RunBehavior for Adra Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/11/2024
 *
 */
public class Adra implements RunGenerosityXBehavior {

	private final String BEHAVIOR_NAME = "Adra";
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
	
	public final Pattern GIFT_FILE_ID_PATTERN = Pattern.compile("(^|\\s+)ConstituentLookupId(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_AMOUNT_PATTERN = Pattern.compile("(^|\\s+)amount(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_DATE_PATTERN = Pattern.compile("(^|\\s+)date(\\s+|$)", Pattern.CASE_INSENSITIVE);
	public final Pattern GIFT_FILE_CAMPAIGN_PATTERN = Pattern.compile("(^|\\s+)appeal(\\s+|$)", Pattern.CASE_INSENSITIVE);
	
	public final Pattern MONTHLY_DESIGNATION_PATTERN = Pattern.compile("(^|\\s+)monthly(\\s+|$)", Pattern.CASE_INSENSITIVE);
	
	private static Pattern PROV_PATTERN = Pattern.compile("ontario|quebec|nova scotia|new brunswick|manitoba|british columbia|prince edward island|saskatchewan|alberta|newfoundland and labrador|northwest territories|yukon|nunavut|(?<=\\s|^|,|\\.)(o(\\s\\.|\\.\\s|\\.|\\s)?n|q(\\s\\.|\\.\\s|\\.|\\s)?c|n(\\s\\.|\\.\\s|\\.|\\s)?s|n(\\s\\.|\\.\\s|\\.|\\s)?b|m(\\s\\.|\\.\\s|\\.|\\s)?b|b(\\s\\.|\\.\\s|\\.|\\s)?c|p(\\s\\.|\\.\\s|\\.|\\s)?e|s(\\s\\.|\\.\\s|\\.|\\s)?k|a(\\s\\.|\\.\\s|\\.|\\s)?b|n(\\s\\.|\\.\\s|\\.|\\s)?l|n(\\s\\.|\\.\\s|\\.|\\s)?t|y(\\s\\.|\\.\\s|\\.|\\s)?t|n(\\s\\.|\\.\\s|\\.|\\s)?u)(?=\\.|,|\\s|$)", Pattern.CASE_INSENSITIVE);
	private static Pattern STATE_PATTERN = Pattern.compile("alabama|alaska|american samoa|arizona|arkansas|california|colorado|connecticut|delaware|district of columbia|federated states of micronesia|florida|georgia|guam|hawaii|idaho|illinois|indiana|iowa|kansas|kentucky|louisiana|maine|marshall islands|maryland|massachusetts|michigan|minnesota|mississippi|missouri|montana|nebraska|nevada|new hampshire|new jersey|new mexico|new york|north carolina|north dakota|northern marianais|ohio|oklahoma|oregon|palau|pennsylvania|puerto rico|rhode island|south carolina|south dakota|tennessee|texas|utah|vermont|virginia|virgin islands|washington|west virginia|wisconsin|wyoming|(?<=\\s|^|,|\\.)(a(\\s\\.|\\.\\s|\\.|\\s)?l|a(\\s\\.|\\.\\s|\\.|\\s)?k|a(\\s\\.|\\.\\s|\\.|\\s)?s|a(\\s\\.|\\.\\s|\\.|\\s)?z|a(\\s\\.|\\.\\s|\\.|\\s)?r|c(\\s\\.|\\.\\s|\\.|\\s)?a|c(\\s\\.|\\.\\s|\\.|\\s)?o|c(\\s\\.|\\.\\s|\\.|\\s)?t|d(\\s\\.|\\.\\s|\\.|\\s)?e|d(\\s\\.|\\.\\s|\\.|\\s)?c|f(\\s\\.|\\.\\s|\\.|\\s)?m|f(\\s\\.|\\.\\s|\\.|\\s)?l|g(\\s\\.|\\.\\s|\\.|\\s)?a|g(\\s\\.|\\.\\s|\\.|\\s)?u|h(\\s\\.|\\.\\s|\\.|\\s)?i|i(\\s\\.|\\.\\s|\\.|\\s)?d|i(\\s\\.|\\.\\s|\\.|\\s)?l|i(\\s\\.|\\.\\s|\\.|\\s)?n|i(\\s\\.|\\.\\s|\\.|\\s)?a|k(\\s\\.|\\.\\s|\\.|\\s)?s|k(\\s\\.|\\.\\s|\\.|\\s)?y|l(\\s\\.|\\.\\s|\\.|\\s)?a|m(\\s\\.|\\.\\s|\\.|\\s)?e|m(\\s\\.|\\.\\s|\\.|\\s)?h|m(\\s\\.|\\.\\s|\\.|\\s)?d|m(\\s\\.|\\.\\s|\\.|\\s)?a|m(\\s\\.|\\.\\s|\\.|\\s)?i|m(\\s\\.|\\.\\s|\\.|\\s)?n|m(\\s\\.|\\.\\s|\\.|\\s)?s|m(\\s\\.|\\.\\s|\\.|\\s)?o|m(\\s\\.|\\.\\s|\\.|\\s)?t|n(\\s\\.|\\.\\s|\\.|\\s)?e|n(\\s\\.|\\.\\s|\\.|\\s)?v|n(\\s\\.|\\.\\s|\\.|\\s)?h|n(\\s\\.|\\.\\s|\\.|\\s)?j|n(\\s\\.|\\.\\s|\\.|\\s)?m|n(\\s\\.|\\.\\s|\\.|\\s)?y|n(\\s\\.|\\.\\s|\\.|\\s)?c|n(\\s\\.|\\.\\s|\\.|\\s)?d|m(\\s\\.|\\.\\s|\\.|\\s)?p|o(\\s\\.|\\.\\s|\\.|\\s)?h|o(\\s\\.|\\.\\s|\\.|\\s)?k|o(\\s\\.|\\.\\s|\\.|\\s)?r|p(\\s\\.|\\.\\s|\\.|\\s)?w|p(\\s\\.|\\.\\s|\\.|\\s)?a|p(\\s\\.|\\.\\s|\\.|\\s)?r|r(\\s\\.|\\.\\s|\\.|\\s)?i|s(\\s\\.|\\.\\s|\\.|\\s)?c|s(\\s\\.|\\.\\s|\\.|\\s)?d|t(\\s\\.|\\.\\s|\\.|\\s)?n|t(\\s\\.|\\.\\s|\\.|\\s)?x|u(\\s\\.|\\.\\s|\\.|\\s)?t|v(\\s\\.|\\.\\s|\\.|\\s)?t|v(\\s\\.|\\.\\s|\\.|\\s)?a|v(\\s\\.|\\.\\s|\\.|\\s)?i|w(\\s\\.|\\.\\s|\\.|\\s)?a|w(\\s\\.|\\.\\s|\\.|\\s)?v|w(\\s\\.|\\.\\s|\\.|\\s)?i|w(\\s\\.|\\.\\s|\\.|\\s)?y)(?=,|\\.|\\s|$)", Pattern.CASE_INSENSITIVE);

	public enum segment {
		NEW("New"),
		LAPSED("Lapsed"),
		FREQUENT("Frequent"),
		TOP("HVD"),
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
		HashMap<String, List<AdraGiftHistory>> giftHistoryMap = convertGiftsToMap(giftList, giftDateFormat);
		
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
		double defaultAskAmount = getCostPerUnit();
		setGiftArrays(userData, defaultAskAmount);
		
		// Ask the user if the ask4 should be doubled
		int isAsk4Doubled = JOptionPane.showConfirmDialog(UiController.getMainFrame(),
				"Have we been asked to multiply ask4 by 2?", "Ask4 x 2", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				
		if(isAsk4Doubled == JOptionPane.YES_OPTION)
			doubleAsk4(userData);
		
		// Ask the user if the gifts should be doubled
		int isMatchCampaign = JOptionPane.showConfirmDialog(UiController.getMainFrame(), "Is this a doubling match campaign?\n\nEx. $5 DOUBLES to $10",
					"Doubling Match Campaign", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		UiController.displayMessage("Warning", "Slowly answer the following prompts as you can't go back!", JOptionPane.WARNING_MESSAGE);
		
		// Set the metrics, either doubled or normal
		if(isMatchCampaign == JOptionPane.YES_OPTION)
			setMatchedGiftArrayMetrics(userData, defaultAskAmount); // doubled
		else
			setGiftArrayMetrics(userData, defaultAskAmount);		// normal
		
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
	
	private void createSaluation(UserData userData) {
		for(Record record : userData.getRecordList()) {
			boolean isIndividual = record.getRecType().toLowerCase().trim().startsWith("i");
			String dearSal = "";
			
			if(!isIndividual) {
				dearSal = "Friends";
			} else {
				String[] nameParts = record.getNam1().split("\\s+");
				dearSal = (nameParts.length > 0) ? nameParts[0] : "";
				
				//Fix the salutation
				if(dearSal.length() <= 1
						|| dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
						|| dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
						|| dearSal.length() == 2 && !Validators.hasVowel(dearSal)
						) {

					dearSal = "Friend";
				}
			}
			
			record.setDearSal(dearSal);
		}
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
	
	private void doubleAsk4(UserData userData) {
		for(Record record : userData.getRecordList()) {
			if(Validators.isNumber(record.getDn4Amt())) {
				double ask4 = Double.parseDouble(record.getDn4Amt());
				ask4 = ask4 * 2;
				record.setDn4Amt(String.valueOf(ask4));
			}
		}
	}
	
	// Fix city and province
	private void fixCityProvince(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String newCity = "";
			String prov = "";
			String oldCity = record.getCity().replaceAll(",+",",");
			int commas = oldCity.length() - oldCity.replaceAll(",","").length();
			if(commas > 0) {
				String[] cityParts = oldCity.split(",");
				prov = cityParts[cityParts.length - 1].trim();
				for(int i = 0; i < cityParts.length - 1; ++i) {
					newCity += cityParts[i];
					if(i != cityParts.length - 2)
						newCity += ", ";
				}
			} else {
				Matcher provMatcher = PROV_PATTERN.matcher(oldCity);
				Matcher stateMatcher = STATE_PATTERN.matcher(oldCity);
				boolean hasProv = false;

				int groupNum = -1;

				while(provMatcher.find()) {
					hasProv = true;
					prov = provMatcher.group().trim();
					newCity = oldCity.replaceAll("(?<=^|\\s|,|\\.)" + provMatcher.group(++groupNum) + "(?=$|\\s|,|\\.)", "").trim();
				}

				groupNum = -1;
				if(!hasProv) {
					while(stateMatcher.find()) {
						prov = stateMatcher.group().trim();
						newCity = oldCity.replaceAll("(?<=^|\\s|,|\\.)" + stateMatcher.group(++groupNum) + "(?=$|\\s|,|\\.)", "").trim();
					}
				}
				
				if(newCity.length() == 0 && prov.length() == 0)
					newCity = oldCity;
			}
			record.setCity(newCity);
			record.setProv(prov);
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
			String id = giftFile.get(i).get(idIndex).replaceAll("[^a-zA-Z0-9_]", "").replaceFirst("^0+", ""); // Remove leading zeros
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
	private HashMap<String, List<AdraGiftHistory>> convertGiftsToMap(List<Record> giftList, String giftDateFormat) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(giftDateFormat);

		// id, giftHistory
		HashMap<String, List<AdraGiftHistory>> giftHistoryMap = new HashMap<>();

		for(Record record : giftList) {
			double giftAmount = (Validators.isNumber(record.getLstDnAmt())) ? Double.parseDouble(record.getLstDnAmt()) : 0.0;

			if(Validators.isStringOfDateFormat(record.getLstDnDat(), giftDateFormat)) {
				//System.out.println(record.getLstDnDat() + " vs " + giftDateFormat);
				LocalDate giftDate = LocalDate.parse(record.getLstDnDat(), formatter);
				//System.out.println(giftDate.toString());
				AdraGiftHistory history = new AdraGiftHistory(
						record.getInId(),
						giftAmount,
						giftDate,
						record.getSeg()
						);

				if(!giftHistoryMap.containsKey(record.getInId()))
					giftHistoryMap.put(record.getInId(), new ArrayList<AdraGiftHistory>());

				giftHistoryMap.get(record.getInId()).add(history);
			}
		}

		return giftHistoryMap;
	}
	
	private void processGifts(UserData userData, HashMap<String, List<AdraGiftHistory>> giftHistoryMap) {
		final int MONTHS6 = 6;
		final int MONTHS12 = 12;
		final int MONTHS24 = 24;

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
			record.setLrgDnAmt("0"); // largest donation amount in last 2 years;
			record.setDnAmtArr("");
			record.setRScore("99999");
			record.setFScore("0");
			record.setMScore("0");
			record.setNumDnLst12Mnths("0");
			record.setYear("0"); // Using this to hold the total donation amount of last 6 months
			record.setMonth("");
			record.setAppeal("");
			record.setPenultAmt("0");
			record.setPenultDat("1900-01-01");
			
			if(!giftHistoryMap.containsKey(record.getInId())) {
				System.out.println("No ID for " + record.getInId());
			}
			
			if(giftHistoryMap.containsKey(record.getInId())) {
				List<AdraGiftHistory> giftHistoryList = giftHistoryMap.get(record.getInId());
				
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
					AdraGiftHistory giftHistory = giftHistoryList.get(j);
					
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
					
					// identify monthly donors if last gift is within 60 days and monthly id found in appeal
					if(daysBetween <= 60) {
						Matcher monthlyMatcher = MONTHLY_DESIGNATION_PATTERN.matcher(giftHistory.getGiftCampaign());
						if(monthlyMatcher.find())
							record.setSeg(segment.MONTHLY.getName());
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
				
				record.setMScore(String.valueOf(monetarySum));
				record.setFScore(String.valueOf(totalGifts));
				record.setTtlDnAmt(String.valueOf(totalGiftAmount));
				record.setTtlDnAmtLst12Mnths(String.valueOf(totalGiftAmountLast12Months));
				record.setNumDn(String.valueOf(totalGifts));
				record.setLrgDnAmt(String.valueOf(largestGiftMadeLast24Months));
				record.setDnAmtArr(commaSeparatedHistory);
				record.setNumDnLst12Mnths(String.valueOf(totalGiftsLast12Months)); 
				record.setYear(String.valueOf(totalGiftAmountLast6Months)); // Using this to hold the total donation amount of last 6 months
			}
			
			
		}
	}	
	
	private void setCampaignCode(UserData userData, String campaignCode) {
		for(Record record : userData.getRecordList()) {
			if(record.getSeg().equalsIgnoreCase(segment.TOP.getName()))
				record.setSegCode(campaignCode + "-HVD");
			else if(record.getSeg().equalsIgnoreCase(segment.GENERAL.getName()))
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
		UserInputDialog uid = new UserInputDialog(UiController.getMainFrame(), "Enter the campaign code. (Ex. 2024-LEB-BRE)");
		uid.getTextField().setText("2024-LEB-BRE");
		uid.setVisible(true);

		if(uid.getIsNextPressed())
			return uid.getUserInput().toUpperCase().trim();

		return "";
	}
	
	// Prompt the user for the cost per unit for donation metrics
	private double getCostPerUnit() {
		UserDecimalInputDialog udid = new UserDecimalInputDialog(UiController.getMainFrame(), "Enter the gift metric unit cost. (Ex if 1 unit costs $40, enter 40)");
		udid.getTextField().setText("40");
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
		
		String less = "$%s helps provide Emergency Supplies to 1 Family.";
		String single = "$%s provides Emergency Supplies to 1 Family.";
		String plural = "$%s provides Emergency Supplies to %s Families.";
		String open = "$________ to help as many Families as possible.";
		
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
			
			String less = "$%s DOUBLES to $%s to help provide Emergency Supplies to 1 Family.";
			String single = "$%s DOUBLES to $%s to provide Emergency Supplies to 1 Family.";
			String plural = "$%s DOUBLES to $%s to provide Emergency Supplies to %s Families.";
			String open = "$________ DOUBLES to help as many Families as possible.";
			
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
		
		if(record.getSeg().equalsIgnoreCase(segment.GENERAL.getName()))
			record.setSegCode(record.getSegCode() + askTier);
	}
	
	// Logic to build lapsed gift arrays
	private void setLapsedGiftArray2(Record record, Double lastDonationRoundedUpByFive, double defaultAskAmount) {
		int askTier = 1;
		
		// Small default amount multiplier
		int sdam = 1;
				
		if(defaultAskAmount < 10)
			sdam = 4;
		else if(defaultAskAmount < 20)
			sdam = 2;

		if(lastDonationRoundedUpByFive < defaultAskAmount * (2 * sdam)) {
			record.setDn1Amt(String.valueOf(Math.ceil((defaultAskAmount * sdam) / 2.0))); // Round up to nearest dollar
			record.setDn2Amt(String.valueOf(defaultAskAmount * sdam));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (2 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (3 * sdam)));
			askTier = 11;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (3 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * sdam));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (2 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (3 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (4 * sdam)));
			askTier = 10;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (4 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (3 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (4 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (5 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (6 * sdam)));
			askTier = 9;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (6 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (4 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (5 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (6 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (7 * sdam)));
			askTier = 8;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (7 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (5 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (6 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (7 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (8 * sdam)));
			askTier = 7;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (10 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (6 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (7 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (8 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (10 * sdam)));
			askTier = 6;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (18 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (8 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (12 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (16 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (20 * sdam)));
			askTier = 5;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (22 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (12 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (16 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (20 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (24 * sdam)));
			askTier = 4;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (32 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (16 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (24 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (32 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (40 * sdam)));
			askTier = 3;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (44 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (24 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (32 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (40 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (48 * sdam)));
			askTier = 2;
		}

		if(record.getSeg().equalsIgnoreCase(segment.GENERAL.getName()))
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
		
		// Small default amount multiplier
		int sdam = 1;
		
		if(defaultAskAmount < 10)
			sdam = 4;
		else if(defaultAskAmount < 20)
			sdam = 2;

		if(lastDonationRoundedUpByFive < defaultAskAmount * (2 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * sdam));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (2 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (3 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (4 * sdam)));
			askTier = 11;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (3 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (2 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (3 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (4 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (5 * sdam)));
			askTier = 10;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (4 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (3 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (4 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (5 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (6 * sdam)));
			askTier = 9;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (6 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (4 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (5 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (6 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (7 * sdam)));
			askTier = 8;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (7 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (5 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (6 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (7 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (8 * sdam)));
			askTier = 7;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (10 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (8 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (9 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (10 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (11 * sdam)));
			askTier = 6;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (18 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (9 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (12 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (14 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (16 * sdam)));
			askTier = 5;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (34 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (20 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (24 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (28 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (32 * sdam)));
			askTier = 4;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (45 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (36 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (42 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (48 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (54 * sdam)));
			askTier = 3;
		} else if(lastDonationRoundedUpByFive < defaultAskAmount * (48 * sdam)) {
			record.setDn1Amt(String.valueOf(defaultAskAmount * (46 * sdam)));
			record.setDn2Amt(String.valueOf(defaultAskAmount * (52 * sdam)));
			record.setDn3Amt(String.valueOf(defaultAskAmount * (58 * sdam)));
			record.setDn4Amt(String.valueOf(defaultAskAmount * (64 * sdam)));
			askTier = 2;
		}

		if(record.getSeg().equalsIgnoreCase(segment.GENERAL.getName()))
			record.setSegCode(record.getSegCode() + askTier);
	}
	
	// Logic to categorize donors into segments
	/*private void setSegment(UserData userData) {
		final int DONATION_MONTHS_WINDOW = 13;
		final int MIN_TOP_DONATION = 900;
		
		final String MONTHLY_DONOR_ACTIVE = "active";
		final String MONTHLY_DONOR_LAPSED = "lapsed";
		
		for(Record record : userData.getRecordList()) {
			String monthlyDonorIndicator = record.getRecType().toLowerCase().trim();
			
			long lifetimeMonths = getMonthsBetween(record.getFstDnDat(), record.getLstDnDat());
			String now = String.valueOf(LocalDate.now()).replaceAll("[^0-9]", "/");
			long monthsFromLastDonation = getMonthsBetween(record.getLstDnDat(), now);
			
			if(monthlyDonorIndicator.equalsIgnoreCase(MONTHLY_DONOR_ACTIVE) || monthlyDonorIndicator.equalsIgnoreCase(MONTHLY_DONOR_LAPSED))
				record.setSeg(segment.MONTHLY.getName());
			else if(TOP_PATTERN.matcher(record.getRfmScore()).find()
					&& Double.parseDouble(record.getLstDnAmt()) >= MIN_TOP_DONATION
					&& monthsFromLastDonation <= DONATION_MONTHS_WINDOW)
				record.setSeg(segment.TOP.getName());
			else if(NEW_PATTERN.matcher(record.getRfmScore()).find() 
					&& Double.parseDouble(record.getNumDn()) <= 2 
					&& lifetimeMonths <= DONATION_MONTHS_WINDOW)
				record.setSeg(segment.NEW.getName());
			else if(LAPSED_PATTERN.matcher(record.getRfmScore()).find() || monthsFromLastDonation > DONATION_MONTHS_WINDOW)
				record.setSeg(segment.LAPSED.getName());
			else if(FREQUENT_PATTERN.matcher(record.getRfmScore()).find() && Double.parseDouble(record.getNumDn()) > 3)
				record.setSeg(segment.FREQUENT.getName());
			else 
				record.setSeg(segment.GENERAL.getName());
			
		}
			
	}*/
	
	// Logic to categorize donors into segments
	private void setSegment(UserData userData) {
		final int MAJOR_DONATION_AMOUNT = 1000;
		final int FREQUENT_DONATIONS_CRITERIA = 3;
		final int NEW_DONOR_MONTHS_CRITERIA = 6;
		final int LAPSED_DONATIONS_CRITERIA = 24;

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


	// Sets the recency score
	/*private void setRScore(UserData userData) {
		String format = DateTimeInferer.inferFormat(userData, UserData.fieldName.LAST_DONATION_DATE.getName());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		LocalDate now = LocalDate.now();

		for(Record record : userData.getRecordList()) {
			record.setRScore("0");

			if (Validators.isStringOfDateFormat(record.getLstDnDat(), format)) {
				LocalDate recordDate = LocalDate.parse(record.getLstDnDat(), formatter);
				long daysBetween = ChronoUnit.DAYS.between(recordDate, now);
				record.setRScore(String.valueOf(daysBetween));
			}
		}
	}
	
		// clean the date inputs
	private void cleanDonationDates(UserData userData) {
		for(Record record : userData.getRecordList()) {
			record.setLstDnDat(record.getLstDnDat().replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/"));
			record.setFstDnDat(record.getFstDnDat().replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/"));
		}
	}


	// set the frequency score
	private void setFScore(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String numOfDonations = record.getNumDn().replaceAll("[^0-9\\.]", "");

			if(Validators.isNumber(numOfDonations))
				record.setFScore(numOfDonations);
			else
				record.setFScore("0");
		}
	}

	// set the monetary score
	private void setMScore(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String lastDonation = record.getLstDnAmt().replaceAll("[^0-9\\.]", "");

			if(Validators.isNumber(lastDonation))
				record.setMScore(lastDonation);
			else
				record.setMScore("0.0");
		}
	}


	// set the first donation date
	private void setFstDonationDate(UserData userData) {
		String format = DateTimeInferer.inferFormat(userData, UserData.fieldName.FIRST_DONATION_DATE.getName());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		LocalDate now = LocalDate.now();

		for(Record record : userData.getRecordList()) {
			if (Validators.isStringOfDateFormat(record.getFstDnDat(), format)) {
				LocalDate recordDate = LocalDate.parse(record.getFstDnDat(), formatter);
				long daysBetween = ChronoUnit.DAYS.between(recordDate, now);
				record.setDay(String.valueOf(daysBetween));
			}
		}
	}*/


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
