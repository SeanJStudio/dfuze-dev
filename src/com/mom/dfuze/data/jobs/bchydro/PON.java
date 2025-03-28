/**
 * Project: Dfuze
 * File: Standard2020.java
 * Date: Mar 15, 2020
 * Time: 3:10:39 PM
 */
package com.mom.dfuze.data.jobs.bchydro;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;



/**
 * PON implements a RunBehavior for BC Hydro Jobs
 * 
 * @author Sean Johnson
 *         Datacore Mail Management
 *         Date: 03/27/2025
 *
 */
public class PON implements RunBCHydroBehavior {

	private final String BEHAVIOR_NAME = "PON";
	
	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.REFERENCE.getName(),
			UserData.fieldName.DATE_FROM.getName(),
			UserData.fieldName.DATE_TO.getName(),
			UserData.fieldName.TIME_FROM.getName(),
			UserData.fieldName.TIME_TO.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.COMPANY.getName(),
			UserData.fieldName.CARE_OF.getName(),
			UserData.fieldName.APARTMENT_NUMBER.getName(),
			UserData.fieldName.STREET_NUMBER.getName(),
			UserData.fieldName.STREET_NAME.getName(),
			UserData.fieldName.STREET_DEFINER.getName(),
			UserData.fieldName.STREET_DIRECTION.getName(),
			UserData.fieldName.PO_BOX.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName(),
			UserData.fieldName.SERVICE_ADDRESS.getName(),
			UserData.fieldName.SERVICE_CITY.getName(),
			UserData.fieldName.COMMENTS.getName(),
	};

	private String DESCRIPTION = 
					"<html>"
					+ "Description<br/>"
					+ "<ul>"
					+ "<li>Prepares data</li>"
					+ "</ul>"
					+ "Instructions<br/>"
					+ "<ol>"
					+ "<li>Load all supplied data files and run Dfuze job</li>"
					+ "<li>Export data and correct/validate in iAddress using newly appended fields</li>"
					+ "<li>Export from iAddress with correction fields</li>"
					+ "<li>Run Dfuze dedupe with match name exactly on the field dfCodeLine</li>"
					+ "<li>Make report of merge/purge</li>"
					+ "</ol>"

					+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS)
					+ "</html>";
	
	// Days of the week
	public final Pattern MONDAY_PATTERN = Pattern.compile("monday", Pattern.CASE_INSENSITIVE);
	public final Pattern TUESDAY_PATTERN = Pattern.compile("tuesday", Pattern.CASE_INSENSITIVE);
	public final Pattern WEDNESDAY_PATTERN = Pattern.compile("wednesday", Pattern.CASE_INSENSITIVE);
	public final Pattern THURSDAY_PATTERN = Pattern.compile("thursday", Pattern.CASE_INSENSITIVE);
	public final Pattern FRIDAY_PATTERN = Pattern.compile("friday", Pattern.CASE_INSENSITIVE);
	public final Pattern SATURDAY_PATTERN = Pattern.compile("saturday", Pattern.CASE_INSENSITIVE);
	public final Pattern SUNDAY_PATTERN = Pattern.compile("sunday", Pattern.CASE_INSENSITIVE);
	
	// Months of the year
	public final Pattern JANUARY_PATTERN = Pattern.compile("january", Pattern.CASE_INSENSITIVE);
	public final Pattern FEBRUARY_PATTERN = Pattern.compile("february", Pattern.CASE_INSENSITIVE);
	public final Pattern MARCH_PATTERN = Pattern.compile("march", Pattern.CASE_INSENSITIVE);
	public final Pattern APRIL_PATTERN = Pattern.compile("april", Pattern.CASE_INSENSITIVE);
	public final Pattern MAY_PATTERN = Pattern.compile("may", Pattern.CASE_INSENSITIVE);
	public final Pattern JUNE_PATTERN = Pattern.compile("june", Pattern.CASE_INSENSITIVE);
	public final Pattern JULY_PATTERN = Pattern.compile("july", Pattern.CASE_INSENSITIVE);
	public final Pattern AUGUST_PATTERN = Pattern.compile("august", Pattern.CASE_INSENSITIVE);
	public final Pattern SEPTEMBER_PATTERN = Pattern.compile("september", Pattern.CASE_INSENSITIVE);
	public final Pattern OCTOBER_PATTERN = Pattern.compile("october", Pattern.CASE_INSENSITIVE);
	public final Pattern NOVEMBER_PATTERN = Pattern.compile("november", Pattern.CASE_INSENSITIVE);
	public final Pattern DECEMBER_PATTERN = Pattern.compile("december", Pattern.CASE_INSENSITIVE);
	
	// Misc
	public final Pattern OUTAGE_DATE_PATTERN = Pattern.compile("\\d+(\\.|\\s)?\\D+(,)?(\\s)?", Pattern.CASE_INSENSITIVE);
	public final Pattern FIX_COMMENTS_DATE_YEAR_PATTERN = Pattern.compile("(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\\D+(,|\\s)?(,|\\s)?\\d(\\d)?(th|rd)?(,|\\s)?(,|\\s)?", Pattern.CASE_INSENSITIVE);
	public final Pattern COMMENTS_DATE_YEAR_PATTERN = Pattern.compile("(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)\\D+(,|\\s)?(,|\\s)?\\d(\\d)?(th|rd)?(,|\\s)?(,|\\s)?\\d\\d\\d\\d", Pattern.CASE_INSENSITIVE);
	
	public final Pattern FIX_COMMENTS_TIME_SPACING_PATTERN = Pattern.compile("\\d+(?=(\\.)?(a|p)(\\.)?m(\\.)?)", Pattern.CASE_INSENSITIVE);
	public final Pattern AM_PATTERN = Pattern.compile("a(\\.)?m(\\.)?", Pattern.CASE_INSENSITIVE);
	public final Pattern PM_PATTERN = Pattern.compile("p(\\.)?m(\\.)?", Pattern.CASE_INSENSITIVE);

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		userData.autoSetRecordList();						// add the records to the userData list
		userData.autoSetRecordListFields(REQUIRED_FIELDS);	// set record members via required fields
		
		// Reformat the outage date and time fields
		reformatDateFromTo(userData);
		reformatTimeFromTo(userData);
		
		// Create the outage text into dfStatus
		createStatus(userData);
		
		// Create the full name
		createFullName(userData);
		
		// Case the name fields
		caseNames(userData);
		
		// Create address fields
		createAddressFields(userData);
		
		// reformat the comments
		reformatComments(userData);
		
		// Split the reference later to easily add to report
		splitReference(userData);
		
		// Finally create the key to dedupe on
		createDedupeKey(userData);

		userData.setDfHeaders(new String[] {
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.COMPANY.getName(),
				UserData.fieldName.CARE_OF.getName(),
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.ADDRESS2.getName(),
				UserData.fieldName.CITY.getName(),
				UserData.fieldName.PROVINCE.getName(),
				UserData.fieldName.POSTALCODE.getName(),
				UserData.fieldName.STATUS.getName(),
				UserData.fieldName.COMMENTS.getName(),
				UserData.fieldName.CODELINE.getName(),
				UserData.fieldName.MISC1.getName(),
				UserData.fieldName.MISC2.getName(),
		});

	}
	
	private void createDedupeKey(UserData userData) {
		int fileNameIndex = Arrays.asList(userData.getInHeaders()).indexOf("dfFileName");
		for(Record record : userData.getRecordList()) {
			String recordFileName = record.getDfInData()[fileNameIndex];
			String name = record.getNam1();
			String company = record.getCmpny();
			String serviceAdd = record.getServiceAdd();
			
			String dedupeKey = String.format("%s%s%s%s", recordFileName, name, company, serviceAdd).replaceAll(" ", "").toLowerCase();
			record.setCodeLine(dedupeKey);
		}
	}
	
	private void splitReference(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String[] parts = record.getRef().split("-", 2);
			String misc1 = "";
			String misc2 = "";
			
			if(parts.length > 1) {
				misc1 = parts[0];
				if(parts.length == 2)
					misc2 = "\"" + parts[1] + "\"";
			}
			
			record.setMisc1(misc1);
			record.setMisc2(misc2);
		}
	}
	
	private void createAddressFields(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String apt = record.getAptNum();
			String streetNum = record.getStreetNum();
			String streetName = record.getStreetNam();
			String streetDef = record.getStreetDef();
			String streetDir = record.getStreetDir();
			
			String add = (String.format("%s %s %s %s %s", apt, streetNum, streetName, streetDef, streetDir)).replaceAll("  ", " ").trim();
			String add2 = record.getPoBox().replaceAll("  ", " ").trim();
			
			if(add.length() == 0 && add2.length() == 0)
				add = record.getServiceAdd();
			
			String city = record.getCity().replaceAll("  ", " ").trim();
			
			if(city.length() == 0)
				city = record.getServiceCity();
			
			String prov = record.getProv().replaceAll("  ", " ").trim();
			String pc = record.getPCode().replaceAll("  ", " ").trim();
			
			if(prov.length() == 0)
				prov = Common.pc2prov(pc);
			
			record.setAdd1(add);
			record.setAdd2(add2);
			record.setCity(city);
			record.setProv(prov);
			record.setPCode(pc);
		}
	}
	
	private void caseNames(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String name = record.getNam1();
			String company = record.getCmpny();
			String careOf = record.getCareOf();
			
			if(name.equals(name.toLowerCase()) || (name.equals(name.toUpperCase()) && name.length() > 2))
				name = Common.caseName(name);
			
			if(company.equals(company.toLowerCase()) || company.equals(company.toUpperCase()))
				company = Common.caseCompany(company);
			
			if(careOf.equals(careOf.toLowerCase()) || careOf.equals(careOf.toUpperCase()))
				careOf = Common.caseName(careOf);
			
			record.setNam1(name);
			record.setCmpny(company);
			record.setCareOf(careOf);
		}
	}
	
	private void createFullName(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String company = record.getCmpny().trim();
			String fullName = (record.getFstName() + " " + record.getLstName()).replaceAll("  ", "").trim();
			if(fullName.length() > 0 || company.length() > 0)
				record.setNam1(fullName);
			else
				record.setNam1("Occupant");
		}
	}
	
	private String addYear(String date, String year) {
		if(!date.contains(year)) {
			Matcher matcher = OUTAGE_DATE_PATTERN.matcher(date);
			if(matcher.find()) {
				String needle = matcher.group();
				date = date.replaceFirst(needle, needle.replaceAll(",", "").trim() + ", " + year + " ");
			}
		}
		
		return date;
	}
	
	private String getReformattedDate(String date) {
		if(MONDAY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)monday", "Mon");
		else if(TUESDAY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)tuesday", "Tue");
		else if(WEDNESDAY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)wednesday", "Wed");
		else if(THURSDAY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)thursday", "Thu");
		else if(FRIDAY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)friday", "Fri");
		else if(SATURDAY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)saturday", "Sat");
		else if(SUNDAY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)sunday", "Sun");
		
		if(JANUARY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)january", "Jan");
		else if(FEBRUARY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)february", "Feb");
		else if(MARCH_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)march", "Mar");
		else if(APRIL_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)april", "Apr");
		else if(MAY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)may", "May");
		else if(JUNE_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)june", "Jun");
		else if(JULY_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)july", "Jul");
		else if(AUGUST_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)august", "Aug");
		else if(SEPTEMBER_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)september", "Sep");
		else if(OCTOBER_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)october", "Oct");
		else if(NOVEMBER_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)november", "Nov");
		else if(DECEMBER_PATTERN.matcher(date).find())
			date = date.replaceAll("(?i)december", "Dec");
		
		return date;
	}
	
	private boolean isNextYearSoon() {
		boolean isNextYear = LocalDate.now()
                .plusWeeks(3)
                .getYear() != LocalDate.now().getYear();
		
		return isNextYear;
	}
	
	private void reformatDateFromTo(UserData userData) {
		String currentYear = java.time.Year.now().toString();
		boolean isNextYearSoon = isNextYearSoon();
		
		for(Record record : userData.getRecordList()) {
			String dateFrom = getReformattedDate(record.getDateFrom().trim());
			String dateTo = getReformattedDate(record.getDateTo().trim());
			
			if(dateTo.length() > 0 && !isNextYearSoon)
				dateTo = addYear(dateTo, currentYear);
			else
				dateFrom = addYear(dateFrom, currentYear);
			
			record.setDateFrom(dateFrom);
			record.setDateTo(dateTo);
		}
	}
	
	private void reformatComments(UserData userData) {
		String currentYear = java.time.Year.now().toString();
		boolean isNextYearSoon = isNextYearSoon();
		
		for(Record record : userData.getRecordList()) {
			String comments = getReformattedComments(record.getComments(), currentYear, isNextYearSoon);
			record.setComments(comments);
		}
	}
	
	private String getReformattedComments(String comments, String year, boolean isNextYearSoon) {
		Matcher matcher = FIX_COMMENTS_TIME_SPACING_PATTERN.matcher(comments);
		
		while(matcher.find()) {
			String match = matcher.group(0);
			comments = comments.replaceAll(Pattern.quote(match), match + " ");
		}
		
		matcher = AM_PATTERN.matcher(comments);
		
		while(matcher.find()) {
			String match = matcher.group(0);
			comments = comments.replaceAll(Pattern.quote(match), "a.m.");
		}
		
		matcher = PM_PATTERN.matcher(comments);
		
		while(matcher.find()) {
			String match = matcher.group(0);
			comments = comments.replaceAll(Pattern.quote(match), "p.m.");
		}
		
		matcher = COMMENTS_DATE_YEAR_PATTERN.matcher(comments);
		
		if(!isNextYearSoon && !matcher.find()) {
			matcher = FIX_COMMENTS_DATE_YEAR_PATTERN.matcher(comments);
			while(matcher.find()) {
				String match = matcher.group(0);
				String replace = matcher.group(0).trim();
				
				if(replace.endsWith(","))
					replace = replace.substring(0, replace.length() - 2);
				
				replace += ", " + year + " ";
				
				comments = comments.replaceAll(Pattern.quote(match), replace);
			}
		}
		
		comments = comments.replaceAll(":00", "").replaceAll(" \\.", ".").replaceAll("  ", " ").trim();
		
		if(!comments.endsWith("."))
			comments += ".";
			
		return comments;
	}
	
	private String getReformattedTime(String time) {
		return time.replaceAll(":00", "")
				.replaceAll("\\.", "")
				.replaceAll("(?i)am", "a.m.")
				.replaceAll("(?i)pm", "p.m.");
	}
	
	private void reformatTimeFromTo(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String timeFrom = getReformattedTime(record.getTimeFrom().trim());
			String timeTo = getReformattedTime(record.getTimeTo().trim());
			record.setTimeFrom(timeFrom);
			record.setTimeTo(timeTo);
		}
	}
	
	/**
	 * Creates the outage date text to display on the front of the postcard
	 **/
	private void createStatus(UserData userData) {
		for(Record record : userData.getRecordList()) {
			String status = record.getDateFrom();
			
			String dateTo = record.getDateTo();
			String timeFrom = record.getTimeFrom();
			String timeTo = record.getTimeTo();
			
			status += (dateTo.trim().length() > 0) ? ", " + timeFrom + " to " + dateTo + ", " + timeTo : " between " + timeFrom + " to " + timeTo;
			
			status = status.replaceAll("  ", " ").trim();
			
			record.setStatus(status);
		}
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
		return true;
	}

}
