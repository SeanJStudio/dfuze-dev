package com.mom.dfuze.data.jobs.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Lookup;
import com.mom.dfuze.data.util.Validators;
import com.mom.dfuze.ui.RentalMappingViewDialog;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserMultiRadioDialog;

public class Rental  implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Rental";
	protected String[] REQUIRED_FIELDS = {};
	protected String DESCRIPTION = "<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Merges common fields into standardized fields</li>"
			+ "<li>Normalize name components</li><li>Identify companies</li>"
			+ "<li>Character casing for individuals and companies</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Click the add file name to data checkbox</li>"
			+ "<li>Ensure data has somewhat descriptive headers</li>"
			+ "<ul>"
			+ "<li>Bad examples: Field1, Field2, Field3</li>"
			+ "<li>Good Examples: firstName, lastName, address</li>"
			+ "</ul>"
			+ "<li>After running, check standardized fields for blank values</li>"
			+ "<ol>"
			+ "</html>";

	private ArrayList<Integer> prefixHeaders = new ArrayList<>();
	private ArrayList<Integer> firstNameHeaders = new ArrayList<>();
	private ArrayList<Integer> middleNameHeaders = new ArrayList<>();
	private ArrayList<Integer> lastNameHeaders = new ArrayList<>();
	//private ArrayList<Integer> salutationHeaders = new ArrayList<>();
	private ArrayList<Integer> fullNameHeaders = new ArrayList<>();
	private ArrayList<Integer> companyHeaders = new ArrayList<>();
	private ArrayList<Integer> address1Headers = new ArrayList<>();
	private ArrayList<Integer> address2Headers = new ArrayList<>();
	private ArrayList<Integer> address3Headers = new ArrayList<>();
	private ArrayList<Integer> cityHeaders = new ArrayList<>();
	private ArrayList<Integer> provinceHeaders = new ArrayList<>();
	private ArrayList<Integer> pcHeaders = new ArrayList<>();
	private ArrayList<Integer> countryHeaders = new ArrayList<>();

	// spouse headers
	private ArrayList<Integer> spousePrefixHeaders = new ArrayList<>();
	private ArrayList<Integer> spouseFirstNameHeaders = new ArrayList<>();
	private ArrayList<Integer> spouseMiddleNameHeaders = new ArrayList<>();
	private ArrayList<Integer> spouseLastNameHeaders = new ArrayList<>();
	//private ArrayList<Integer> spouseSalutationHeaders = new ArrayList<>();
	private ArrayList<Integer> spouseFullNameHeaders = new ArrayList<>();


	public static final Pattern PREFIX_PATTERN = Pattern.compile("^(?!.*([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*).*([p][r][e][f][i]|[p][r][e][f]$|[t][i][t][l])", Pattern.CASE_INSENSITIVE);
	public static final Pattern FIRST_NAME_PATTERN = Pattern.compile("^(?!.*([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*).*((fir|fst).*[n][a][m]|[f](_|\\s|-)?[n][a][m]|[n][a][m].*[1]|[f][n](\\s+)?$|[f][o][r][e][n][a][m][e]|[n][a][m].*([f][n]|[f][s][t]|[f][i][r][s][t]))", Pattern.CASE_INSENSITIVE);
	public static final Pattern MIDDLE_NAME_PATTERN = Pattern.compile("^(?!.*([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*).*(mid.*[n][a][m]|[m](_|\\s|-)?[n][a][m]|[m][n](\\s+)?$|[n][a][m].*([m][n]|[m][i][d]))", Pattern.CASE_INSENSITIVE);
	public static final Pattern LAST_NAME_PATTERN = Pattern.compile("^(?!.*([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*).*((las|lst).*[n][a][m]|(?<!l)[l](_|\\s|-)?[n][a][m]|[n][a][m].*[2]|[l][n](\\s+)?$|[s][u][r][n][a][m][e]|[n][a][m].*([l][n]|[l][s][t]|[l][a][s][t]))", Pattern.CASE_INSENSITIVE);
	public static final Pattern SALUTATION_PATTERN = Pattern.compile("^(?!.*([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*).*([s][a][l][u]|[s][a][l]$|[d][e][a][r]|[g][r][e][e][t])", Pattern.CASE_INSENSITIVE);
	public static final Pattern FULL_NAME_PATTERN = Pattern.compile("^(?!.*([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*).*([f][u].*[n][a][m]|[a][d][d][r][e][s][s][e][e]|^[n][a][m][e](\\s+)?$)", Pattern.CASE_INSENSITIVE);

	// Spouse patterns
	public static final Pattern SPOUSE_PREFIX_PATTERN = Pattern.compile("([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*([p][r][e]|[t][i][t][l])", Pattern.CASE_INSENSITIVE);
	public static final Pattern SPOUSE_FIRST_NAME_PATTERN = Pattern.compile("([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*(fir.*[n][a][m]|[f](_|\\s|-)?[n][a][m]|[n][a][m].*[1]|[f][n](\\s+)?$|[f][o][r][e][n][a][m][e]|[n][a][m].*([f][n]|[f][s][t]|[f][i][r][s][t]))", Pattern.CASE_INSENSITIVE);
	public static final Pattern SPOUSE_MIDDLE_NAME_PATTERN = Pattern.compile("([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*(mid.*[n][a][m]|[m](_|\\s|-)?[n][a][m]|[m][n](\\s+)?$|[n][a][m].*([m][n]|[m][i][d]))", Pattern.CASE_INSENSITIVE);
	public static final Pattern SPOUSE_LAST_NAME_PATTERN = Pattern.compile("([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*(las.*[n][a][m]|(?<!l)[l](_|\\s|-)?[n][a][m]|[n][a][m].*[2]|[l][n](\\s+)?$|[s][u][r][n][a][m][e]|[n][a][m].*([l][n]|[l][s][t]|[l][a][s][t]))", Pattern.CASE_INSENSITIVE);
	public static final Pattern SPOUSE_SALUTATION_PATTERN = Pattern.compile("([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*([s][a][l][u]|[s][a][l]$|[d][e][a][r]|[g][r][e][e][t])", Pattern.CASE_INSENSITIVE);
	public static final Pattern SPOUSE_FULL_NAME_PATTERN = Pattern.compile("([_][s][_]|[s][p][o][u][s][e]|[s][e][c][o][n][d][a][r][y]).*([f][u].*[n][a][m]|[a][d][d][r][e][s][s][e][e]|^[n][a][m][e](\\s+)?$)", Pattern.CASE_INSENSITIVE);


	public static final Pattern COMPANY_PATTERN = Pattern.compile("[c][o][m][p]|[c][m][p](\\s+)?$|[c][m][p][_]|[o][r][g]|[b][u][s]|[b][i][z]|[a][s][s][o][c]", Pattern.CASE_INSENSITIVE);
	public static final Pattern ADDRESS_1_PATTERN = Pattern.compile("[a][d].*[1]|[1].*[a][d](?!.*a)|[a][d][d][r][e][s][s](\\s+)?$|[a][d][d](\\s+)?$|[a][d][d][r](\\s+)?$|[s][t][r][e][e][t](\\s+)?$|[s][t][r][e][e][t].*[1]|[l][i][n][e].*[1]|[a][d][d].*[l][i][n][e](s)?(\\s+)?$", Pattern.CASE_INSENSITIVE);
	public static final Pattern ADDRESS_2_PATTERN = Pattern.compile("[a][d].*[2]|[2].*[a][d]|[s][t][r][e][e][t].*[2]|[l][i][n][e].*[2]", Pattern.CASE_INSENSITIVE);
	public static final Pattern ADDRESS_3_PATTERN = Pattern.compile("[a][d].*[3]|[3].*[a][d]|[s][t][r][e][e][t].*[3]|[l][i][n][e].*[3]", Pattern.CASE_INSENSITIVE);
	public static final Pattern CITY_PATTERN = Pattern.compile("[c][i][t][y]|[m][u][n][i]|[t][o][w]", Pattern.CASE_INSENSITIVE);
	public static final Pattern PROVINCE_PATTERN = Pattern.compile("[p][r][o][v]|[s][t][a][t][e]|[c][o][u][n][t][y]", Pattern.CASE_INSENSITIVE);	
	public static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("[p][o][s][t]|[p][s][t]|(^[c][o][d][e])(\\s+)?$|[p](/|\\s|-|_)?[c](\\s+)?$|[z][i][p]|[p][c][o][d][e]", Pattern.CASE_INSENSITIVE);
	public static final Pattern COUNTRY_PATTERN = Pattern.compile("[c][o][u][n][t][r]|[c][n][t]", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d");

	HashSet<String> usedHeaders = new HashSet<>();

	private HashMap<Pattern, ArrayList<Integer>> patternMap = new HashMap<>();
	private HashMap<Pattern, String> patternNameMap = new HashMap<>();
	private HashMap<String, HashMap<Integer, String>> fileNameFieldsMap = new HashMap<>();

	String[][] prefixesFirstPass;
	String[][] companyKeywords;

	private static final String CONJUNCTION_REGEX = "(?i)[ ][a][n][d][ ]|[ ][a][n][d][/][o][r][ ]|[ ][&][ ]|[ ][e][t][ ]|[ ][o][r][ ]|[ ][o][u][ ]|(\\s+)?[/](\\s+)?|(\\s+)?\\+(\\s+)?";
	private static final Pattern CONJUNCTION_CHECK_AFTER_PREFIX = Pattern.compile("^(and\\s+|&\\s+|or\\s+|et\\s+|ou\\s+|/)", Pattern.CASE_INSENSITIVE);
	private static final Pattern CONJUNCTION_CHECK_NO_SPACE = Pattern.compile("[a][n][d]|[a][n][d][/][o][r]|[&]|[/]|[e][t]|[o][r]|[o][u]|\\+", Pattern.CASE_INSENSITIVE);
	
	public static final int SALUTATION_PREFIX_LASTNAME_FIRSTNAME = 1;
	public static final int SALUTATION_PREFIX_LASTNAME_FIRSTNAME_LASTNAME = 2;
	public static final int SALUTATION_FIRSTNAME_PREFIX_LASTNAME = 3;
	public static final int SALUTATION_FIRSTNAME = 4;
	
	ArrayList<Integer> removedPrefixList = new ArrayList<>();

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

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception{
		
		/******************************
		 * CONFIRM FILENAME IS INCLUDED
		 ******************************/
		if(!Common.hasFileName(userData))
			throw new ApplicationException(String.format("Error: file name is not included\n\nPlease do a new job and ensure the add file name to data checkbox is checked."));

		reset();

		String[][] data = userData.getData();
		String[] headers = userData.getInHeaders();
		
		prefixesFirstPass = Lookup.getPrefixFirstPass();
		companyKeywords = Lookup.getCompanykeywords();

		// init our patterns to search through
		fillPatternMap();
		fillPatternNames();
		
		// Map our headers
		mapFieldIndexes(headers);
		
		//get the filename index
		int fileNameIndex = Arrays.asList(headers).indexOf("dfFileName");
		
		//validate the fields
		for (int i = 0; i < data.length; i++) {
			// Ensure we don't have multiple values available for the same field
			validateFields(data, i, fileNameIndex, headers);
		}
		
		// build a set of filenames
		LinkedHashSet<String> fileSet = new LinkedHashSet<>();

		// to hold the mapping data by filename
		LinkedHashMap<String,String[]> fileNameFieldsDataMap = new LinkedHashMap<>();

		// build the mapped fields by filename for review, linkedHashSet maintains insertion order
		for (int i = 0; i < data.length; i++) {
			String fileName = data[i][fileNameIndex];
			
			fileSet.add(fileName);
			
			if(!fileNameFieldsDataMap.keySet().contains(fileName)) {
				String[] fileNameFieldsData = new String[getPreDefinedHeaders().length];
				Arrays.fill(fileNameFieldsData, "");
				
				for(Entry<Pattern, ArrayList<Integer>> entrySet : patternMap.entrySet()) {
					Pattern pattern = entrySet.getKey();
					for(Integer index : entrySet.getValue()) {
						if(!fileNameFieldsMap.keySet().contains(fileName)) { //Map should have the filename, if it doesn't than no fields were mapped.
							throw new ApplicationException(String.format("Error: No fields could be mapped for %s.", fileName));
						}
						if(fileNameFieldsMap.get(fileName).containsKey(index)) {
							String header = fileNameFieldsMap.get(fileName).get(index);
							String predefinedHeader = patternNameMap.get(pattern);
							
							for(int j = 0; j < getPreDefinedHeaders().length; ++j) {
								if(getPreDefinedHeaders()[j].equals(predefinedHeader)) {
									fileNameFieldsData[j] = header;
								}
							}
							
						}
					}
				}
				
				fileNameFieldsDataMap.put(fileName, fileNameFieldsData);
			}
		}
		
		String[][] fileArrayMappingData = new String[fileNameFieldsDataMap.size()][getPreDefinedHeaders().length];
		
		String fileArray[] = new String[fileSet.size()];
		fileSet.toArray(fileArray);
		
		//put together the final array
		for(int i = 0; i < fileArray.length; ++i) {
			fileArrayMappingData[i] = fileNameFieldsDataMap.get(fileArray[i]);
		}

		// show the mapping for review
		RentalMappingViewDialog rmvd = new RentalMappingViewDialog(getPreDefinedHeaders(), fileArray, fileArrayMappingData);
		rmvd.setVisible(true);
		
		if(rmvd.isCancelPressed())
			return;

		// actually start processing the data
		int counter = -1;
		for (int i = 0; i < data.length; i++) {

			// Initialize our fields
			String prefix = getFieldValue(prefixHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");
			String fnam = getFieldValue(firstNameHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");
			String mnam = getFieldValue(middleNameHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");
			String lnam = getFieldValue(lastNameHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");
			//String sal = getFieldValue(salutationHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");
			String sal = "";
			String fullNam = getFieldValue(fullNameHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");

			// Spouse
			String spousePrefix = getFieldValue(spousePrefixHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");
			String spouseFnam = getFieldValue(spouseFirstNameHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");
			String spouseMnam = getFieldValue(spouseMiddleNameHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");
			String spouseLnam = getFieldValue(spouseLastNameHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");
			//String spouseSal = getFieldValue(spouseSalutationHeaders, data, i);
			String spouseFullNam = getFieldValue(spouseFullNameHeaders, data, i).replaceAll("\\s+", " ").trim().replaceAll("\\.", "");

			lnam = lnam.replaceAll("(?i)[ ][j][r][ ]|[ ][j][r]$|[ ][s][r][ ]|[ ][s][r]$", "");
			fullNam = fullNam.replaceAll("(?i)[ ][j][r][ ]|[ ][j][r]$|[ ][s][r][ ]|[ ][s][r]$", "");
			spouseLnam = spouseLnam.replaceAll("(?i)[ ][j][r][ ]|[ ][j][r]$|[ ][s][r][ ]|[ ][s][r]$", "");
			spouseFullNam = spouseFullNam.replaceAll("(?i)[ ][j][r][ ]|[ ][j][r]$|[ ][s][r][ ]|[ ][s][r]$", "");

			if(spouseFullNam.trim().isEmpty())
				spouseFullNam =  (spousePrefix + " " + spouseFnam + " " + spouseMnam + " " + spouseLnam).replaceAll("\\s+", " ").trim();

			String company = getFieldValue(companyHeaders, data, i).trim();
			String add1 = getFieldValue(address1Headers, data, i).trim();
			String add2 = getFieldValue(address2Headers, data, i).trim();
			String add3 = getFieldValue(address3Headers, data, i).trim();
			String city = getFieldValue(cityHeaders, data, i).trim();
			String prov = getFieldValue(provinceHeaders, data, i).trim();
			String pc = getFieldValue(pcHeaders, data, i).trim();
			//String country = getFieldValue(countryHeaders, data, i);
			
			
			// remove the prefix if it is already part of the name fields
			if(prefix.length() > 0) {
				prefix = prefix.replaceAll("\\s+", " ").trim() +" ";
				fullNam = fullNam.replaceAll("\\s+", " ").trim();
				fnam = fnam.replaceAll("\\s+", " ").trim();
				
				if(fullNam.length() >= prefix.length()) {
					if(prefix.equalsIgnoreCase(fullNam.substring(0, prefix.length()))) {
						if(!CONJUNCTION_CHECK_AFTER_PREFIX.matcher(fullNam.substring(prefix.length())).find()) {
							fullNam = fullNam.substring(prefix.length());
							removedPrefixList.add(counter + 1);
						}
					}
				}
				
				if(fnam.length() >= prefix.length())
					if(prefix.equalsIgnoreCase(fnam.substring(0, prefix.length())))
						if(!CONJUNCTION_CHECK_AFTER_PREFIX.matcher(fnam.substring(prefix.length())).find())
							fnam = fnam.substring(prefix.length());
			} else {
				if(fullNam.length() > 0) {
					for(int j = 0; j < prefixesFirstPass.length; ++j) { 
						String tempPrefix = prefixesFirstPass[j][0];
						//Check if the prefix matches
						if(fullNam.length() >= tempPrefix.length()) {
							if(fullNam.substring(0, tempPrefix.length()).equalsIgnoreCase(tempPrefix)) {
								fullNam = fullNam.substring(tempPrefix.length()).trim();
								prefix = tempPrefix.trim();
								removedPrefixList.add(counter + 1);
								break;
							}
						}
					}
				}
				
				if(fnam.length() > 0) {
					for(int j = 0; j < prefixesFirstPass.length; ++j) { 
						String tempPrefix = prefixesFirstPass[j][0];
						//Check if the prefix matches
						if(fnam.length() >= tempPrefix.length()) {
							if(fnam.substring(0, tempPrefix.length()).equalsIgnoreCase(tempPrefix)) {
								fnam = fnam.substring(tempPrefix.length()).trim();
								prefix = tempPrefix.trim();
								break;
							}
						}
					}
				}
			}
			
			// compare fullname to fname + lname to see which contains more info
			String nameFromParts = (fnam + " " + mnam + " " + lnam).replaceAll("\\s+", " ").trim();
			
			if(!CONJUNCTION_CHECK_AFTER_PREFIX.matcher(fullNam).find())
				if(CONJUNCTION_CHECK_AFTER_PREFIX.matcher(nameFromParts).find())
					fullNam = (prefix + " " + fnam + " " + mnam + " " + lnam).replaceAll("\\s+", " ").trim();
			
			if (nameFromParts.replaceAll(Pattern.quote(lnam), "").trim().length() > fullNam.replaceAll(Pattern.quote(lnam), "").trim().length())
				fullNam = (prefix + " " + fnam + " " + mnam + " " + lnam).replaceAll("\\s+", " ").trim();

			if(!add3.isBlank())
				add2 += " " + add3;

			if(fullNam.trim().isBlank())
				fullNam = (prefix + " " + fnam + " " + mnam + " " + lnam).replaceAll("\\s+", " ").trim();

			if(!spouseFullNam.isBlank())
				fullNam = fullNam + " and " + spouseFullNam;

			boolean wasCompanyFound = false;

			if(!fullNam.isBlank()) {
				for(int j = 0; j < companyKeywords.length; ++j) {
					String keyword = " " + companyKeywords[j][0] + " ";
					String recordValue = (" " + fullNam.toLowerCase() + " ");
					if(recordValue.contains(keyword) || NUMBER_PATTERN.matcher(fullNam).find()) { // if keyword found or has a number
						wasCompanyFound = true;
						break;
					}
				}
			}

			if(wasCompanyFound) {
				company = fullNam;
				prefix = "";
				fullNam = "";
				fnam = "";
				mnam = "";
				lnam = "";
				sal = "Friends";
			}

			if(!company.isBlank() && fullNam.isBlank())
				sal = "Friends";

			if(company.isBlank() && fullNam.isBlank())
				sal = "Friend";

			Record record = new Record.Builder(++counter, data[i], "", "", "")
					.setCmpny(company.replaceAll("\\s+", " ").trim())
					.setNam1(fullNam.replaceAll("\\s+", " ").trim())
					.setFstName(fnam.replaceAll("\\s+", " ").trim())
					.setLstName(lnam.replaceAll("\\s+", " ").trim())
					.setPrefix(prefix.replaceAll("\\s+", " ").trim())
					.setDearSal(sal.replaceAll("\\s+", " ").trim())
					.setAdd1(add1.replaceAll("\\s+", " ").trim())
					.setAdd2(add2.replaceAll("\\s+", " ").trim())
					.setCity(city.replaceAll("\\s+", " ").trim())
					.setProv(prov.replaceAll("\\s+", " ").trim())
					.setPCode(pc.replaceAll("\\s+", " ").trim())
					.build();

			userData.add(record); // add the record the recordList in userData

		}
		
		UserMultiRadioDialog radioDialog = new UserMultiRadioDialog(UiController.getMainFrame(), 4, "How should the salutation be built?");
		radioDialog.setRadioButton1("[Prefix + Lastname]->[Firstname]->[Friend]");
		radioDialog.setRadioButton2("[Prefix + Lastname]->[Firstname + Lastname]->[Friend]");
		radioDialog.setRadioButton3("[Firstname]->[Prefix + Lastname]->[Friend]");
		radioDialog.setRadioButton4("[Firstname]->[Friend]");
		radioDialog.setVisible(true);
		int salutationOption = radioDialog.getSelectedRadioButton();

		for(Record record : userData.getRecordList()) {
			// Create the name
			newPopulateNames(record, salutationOption);
			
			// Remove extra lastnames
			Pattern lnPattern = Pattern.compile("(?<=\\s)" + Pattern.quote(record.getLstName()) + "(?=\\s|$)", Pattern.CASE_INSENSITIVE);
			Matcher lnMatcher = lnPattern.matcher(record.getNam1());
			int matches = 0;
			while(lnMatcher.find())
				++matches;
			
			Matcher lnMatcher2 = lnPattern.matcher(record.getNam1());
			int matchCounter = 0;
			
			if(matches > 1) {
				while(lnMatcher2.find()) {
					++matchCounter;
					record.setNam1(record.getNam1().replaceFirst(Pattern.quote(lnMatcher2.group(0)), "").replaceAll("  ", " ").trim());
					if(matchCounter == matches - 1)
						break;
				}
			}
			
			// Finally case the values
			record.setCmpny(Common.caseCompany(record.getCmpny()));
			record.setNam1(Common.caseName(record.getNam1()));
			record.setFstName(Common.caseName(record.getFstName()));
			record.setLstName(Common.caseName(record.getLstName()));
			record.setPrefix(Common.caseName(record.getPrefix()));
			record.setDearSal(Common.caseName(record.getDearSal()));

		}

		// set the Header fields that we have used
		userData.setDfHeaders(new String[] {
				UserData.fieldName.COMPANY.getName(),
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.PREFIX.getName(),
				UserData.fieldName.FIRSTNAME.getName(),
				UserData.fieldName.LASTNAME.getName(),
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.ADDRESS2.getName(),
				UserData.fieldName.CITY.getName(),
				UserData.fieldName.PROVINCE.getName(),
				UserData.fieldName.POSTALCODE.getName()
		});

	}

	public void mapFieldIndexes(String[] headers) throws Exception{

		for(Entry<Pattern, ArrayList<Integer>> entrySet : patternMap.entrySet()) {
			for(int i = 0; i < headers.length; ++i) {
				if(entrySet.getKey().matcher(headers[i]).find()) {
					entrySet.getValue().add(i);
					if(!usedHeaders.add(headers[i]))
						throw new Exception(String.format("The header %s has been mapped more than once to the following fields:\n%s\n\nPlease change the header to be more concise so it is not mapped more than once.", headers[i], getMappedFieldsOfHeader(i).stream().collect(Collectors.joining("\n"))));

				}
			}
		}

	}
	
	public ArrayList<String> getMappedFieldsOfHeader(int headerIndex) {
		ArrayList<String> mappedFieldList = new ArrayList<>();
		
		for(Entry<Pattern, ArrayList<Integer>> entrySet : patternMap.entrySet()) {
			if(entrySet.getValue().contains(headerIndex))
				mappedFieldList.add(patternNameMap.get(entrySet.getKey()));
		}
		
		return mappedFieldList;
	}
	
	public void fillPatternNames() {
		patternNameMap.put(PREFIX_PATTERN, "PREFIX");
		patternNameMap.put(FIRST_NAME_PATTERN, "FIRSTNAME");
		patternNameMap.put(MIDDLE_NAME_PATTERN, "MIDDLENAME");
		patternNameMap.put(LAST_NAME_PATTERN, "LASTNAME");
		//patternNameMap.put(SALUTATION_PATTERN, "SALUTATION");
		patternNameMap.put(FULL_NAME_PATTERN, "FULLNAME");

		// Spouses
		patternNameMap.put(SPOUSE_PREFIX_PATTERN, "SPOUSE PREFIX");
		patternNameMap.put(SPOUSE_FIRST_NAME_PATTERN, "SPOUSE FIRSTNAME");
		patternNameMap.put(SPOUSE_MIDDLE_NAME_PATTERN, "SPOUSE MIDDLENAME");
		patternNameMap.put(SPOUSE_LAST_NAME_PATTERN, "SPOUSE LASTNAME");
		//patternNameMap.put(SPOUSE_SALUTATION_PATTERN, "SPOUSE SALUTATION");
		patternNameMap.put(SPOUSE_FULL_NAME_PATTERN, "SPOUSE FULLNAME");

		patternNameMap.put(COMPANY_PATTERN, "COMPANY");
		patternNameMap.put(ADDRESS_1_PATTERN, "ADDRESS1");
		patternNameMap.put(ADDRESS_2_PATTERN, "ADDRESS2");
		patternNameMap.put(ADDRESS_3_PATTERN, "ADDRESS3");
		patternNameMap.put(CITY_PATTERN, "CITY");
		patternNameMap.put(PROVINCE_PATTERN, "PROVINCE");
		patternNameMap.put(POSTAL_CODE_PATTERN, "PC");
		patternNameMap.put(COUNTRY_PATTERN, "COUNTRY");
	}
	
	public String[] getPreDefinedHeaders() {
		String[] patternNames =  {"PREFIX", "FIRSTNAME", "MIDDLENAME", "LASTNAME", "FULLNAME", "SPOUSE PREFIX", "SPOUSE FIRSTNAME", "SPOUSE MIDDLENAME", "SPOUSE LASTNAME", "SPOUSE FULLNAME", "COMPANY", "ADDRESS1", "ADDRESS2", "ADDRESS3", "CITY", "PROVINCE", "PC", "COUNTRY"};
		return patternNames;
	}

	public void fillPatternMap() {
		patternMap.put(PREFIX_PATTERN, prefixHeaders);
		patternMap.put(FIRST_NAME_PATTERN, firstNameHeaders);
		patternMap.put(MIDDLE_NAME_PATTERN, middleNameHeaders);
		patternMap.put(LAST_NAME_PATTERN, lastNameHeaders);
		//patternMap.put(SALUTATION_PATTERN, salutationHeaders);
		patternMap.put(FULL_NAME_PATTERN, fullNameHeaders);

		// Spouses
		patternMap.put(SPOUSE_PREFIX_PATTERN, spousePrefixHeaders);
		patternMap.put(SPOUSE_FIRST_NAME_PATTERN, spouseFirstNameHeaders);
		patternMap.put(SPOUSE_MIDDLE_NAME_PATTERN, spouseMiddleNameHeaders);
		patternMap.put(SPOUSE_LAST_NAME_PATTERN, spouseLastNameHeaders);
		//patternMap.put(SPOUSE_SALUTATION_PATTERN, spouseSalutationHeaders);
		patternMap.put(SPOUSE_FULL_NAME_PATTERN, spouseFullNameHeaders);

		patternMap.put(COMPANY_PATTERN, companyHeaders);
		patternMap.put(ADDRESS_1_PATTERN, address1Headers);
		patternMap.put(ADDRESS_2_PATTERN, address2Headers);
		patternMap.put(ADDRESS_3_PATTERN, address3Headers);
		patternMap.put(CITY_PATTERN, cityHeaders);
		patternMap.put(PROVINCE_PATTERN, provinceHeaders);
		patternMap.put(POSTAL_CODE_PATTERN, pcHeaders);
		patternMap.put(COUNTRY_PATTERN, countryHeaders);
	}

	public void validateFields(String[][] data, int i, int fileNameIndex, String[] headers) throws Exception{
		for(Entry<Pattern, ArrayList<Integer>> entrySet : patternMap.entrySet()) {
			ArrayList<String> nonBlankHeaders = new ArrayList<>();
			for(Integer index : entrySet.getValue()) {
				if(!data[i][index].isEmpty()) {
					nonBlankHeaders.add(headers[index]);
										
					//fill the fileNameMap					
					String fileName = data[i][fileNameIndex];

					if(!fileNameFieldsMap.containsKey(fileName)) {
						HashMap<Integer, String> headerIndexMap = new HashMap<>();
						headerIndexMap.put(index, headers[index]);
						fileNameFieldsMap.put(fileName, headerIndexMap);
						
					} else if(!fileNameFieldsMap.get(fileName).containsKey(index)) { // if it doesn't have a header index added yet
						fileNameFieldsMap.get(fileName).put(index, headers[index]);
					}
					
				}
			}
			
			if(nonBlankHeaders.size() > 1)
				throw new Exception(String.format("More than 1 header from %s has been mapped to %s\n\nMapped headers:\n%s\n\nPlease change the header names so only 1 is mapped.", data[i][fileNameIndex], patternNameMap.get(entrySet.getKey()),nonBlankHeaders.stream().collect(Collectors.joining("\n"))));
		}
	}

	public String getFieldValue(ArrayList<Integer> fields, String[][] data, Integer i) {

		for(Integer index : fields)
			if(!data[i][index].isEmpty())
				return data[i][index];

		return "";
	}

	public void newPopulateNames(Record record, int salutationOption) {

		// If name is not empty
		if(!record.getNam1().isEmpty()) {
			ArrayList<String> lastNames = new ArrayList<>();
			ArrayList<String> prefixes = new ArrayList<>();
			String newFirstName = "";
			String newPrefix = "";
			String newLastName = "";
			String newSalutation = "";
			
			// handle the french prefix "M"
			if(record.getPrefix().equalsIgnoreCase("m")) {
				newFirstName = record.getNam1().substring(2);
				prefixes.add("M");
			} else { // remove the first prefix so we can split on "and", "et", etc
				for(int i = 0; i < prefixesFirstPass.length; ++i) { 
					String prefix = prefixesFirstPass[i][0];
					//Check if the prefix matches
					if(record.getNam1().length() >= prefix.length()) {
						if(record.getNam1().substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
							newFirstName = record.getNam1().substring(prefix.length());
							prefixes.add(prefix.trim());
							break;
						}
					}
				}
			}
			
			if(prefixes.size() == 0)
				if(!record.getPrefix().isBlank())
					prefixes.add(record.getPrefix());

			// update the new first name is no prefix was found
			if(newFirstName.isEmpty())
				newFirstName = record.getNam1();

			// Now we can split on conjunctions
			if(newFirstName.trim().split(" ").length > 1)
				newFirstName = newFirstName.replaceAll(CONJUNCTION_REGEX," \\|\\| ");

			//System.out.println(++counter + " " + newFirstName);
			String[] firstNameParts = newFirstName.split(" \\|\\| ");

			// if there was no firstname found, set lastname
			if(newFirstName.trim().split(" ").length == 1) {
				if(record.getFstName().isBlank() || prefixes.size() > 0) {
					lastNames.add(newFirstName);
					newFirstName = "";
				} 
			}

			//System.out.println(Arrays.asList(firstNameParts));
			if(newFirstName.trim().split(" ").length > 1)
				for(int i = 0; i < firstNameParts.length; ++i) {
					for(int j = 0; j < prefixesFirstPass.length; ++j) {
						String prefix = prefixesFirstPass[j][0];
						if(firstNameParts[i].length() >= prefix.length()) {
							if(firstNameParts[i].substring(0, prefix.length()).equalsIgnoreCase(prefix)) {
								firstNameParts[i] = firstNameParts[i].substring(prefix.length());
								prefixes.add(prefix.trim());
								break;
							}
						}
					}

					String[] newFirstNameParts = firstNameParts[i].trim().split("\\s+");

					if(i == 0)
						newFirstName = newFirstNameParts[0];
					else {
						newFirstName += " and " + newFirstNameParts[0];
					}

					if(newFirstNameParts.length > 1) {
						if(newFirstNameParts[newFirstNameParts.length -1].length() > 1) {

							boolean isFound = false;
							for(String lastName : lastNames) {
								if(lastName.trim().equalsIgnoreCase(newFirstNameParts[newFirstNameParts.length - 1].trim())) {
									isFound = true;
									break;
								}
							}
							if(!isFound)
								lastNames.add(newFirstNameParts[newFirstNameParts.length - 1].trim());
						}
					}
				}


			for(int i = 0; i < prefixes.size(); i++) {
				String tempPrefix = prefixes.get(i).toLowerCase().replaceAll("&", "and");
				String[] prefixParts = tempPrefix.split("\\s+");
				String fixedPrefix = "";
				
				for(int j = 0; j < prefixParts.length; ++j) {
					Matcher matcher = CONJUNCTION_CHECK_NO_SPACE.matcher(prefixParts[j]);
					if(!matcher.find()) {
						prefixParts[j] = prefixParts[j].substring(0, 1).toUpperCase() + prefixParts[j].substring(1);
						
						if(!prefixParts[j].equalsIgnoreCase("the")) {
							prefixParts[j] += ".";
						}
					}
					fixedPrefix += prefixParts[j] + " ";
				}

				prefixes.set(i, fixedPrefix.trim());	
			}


			for(int i = 0; i < prefixes.size(); i++) {
				if(i == 0)
					newPrefix = prefixes.get(i);
				else
					newPrefix += " and " + prefixes.get(i);		
			}


			for(int i = 0; i < lastNames.size(); i++) {
				if(i == 0)
					newLastName = lastNames.get(i);
				else
					newLastName += " and " + lastNames.get(i);		
			}
			
			// Build the salutation
			// option1
			if(salutationOption == SALUTATION_PREFIX_LASTNAME_FIRSTNAME) {
				if(lastNames.size() == prefixes.size() && lastNames.size() > 1 &&  prefixes.size() > 1) {
					for(int i = 0; i < prefixes.size(); i++){
						if(i == 0)
							newSalutation += prefixes.get(i) + " " + lastNames.get(i);
						else
							newSalutation += " and " + prefixes.get(i) + " " + lastNames.get(i);
					}
				} else {
					if(lastNames.size() == 1) {
						for(int i = 0; i < prefixes.size(); i++){
							if(i == 0)
								newSalutation = prefixes.get(i);
							else
								newSalutation += " and " + prefixes.get(i);
						}

						if(prefixes.size() > 0) {
							newSalutation += " " + lastNames.get(0);
						}else {
							if(newFirstName.length() > 1) {
								newSalutation = newFirstName;
								if(newSalutation.length() >= 2 && newSalutation.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
										|| newSalutation.length() == 2 && Validators.areCharactersSame(newSalutation)
										|| newSalutation.length() == 2 && !Validators.hasVowel(newSalutation)
										) {
									newSalutation = "Friend";
								}
							} else {
								newSalutation = "Friend";
							}
						}

					} else {
						if(newFirstName.length() > 1) {
							newSalutation = newFirstName;
							if(newSalutation.length() >= 2 && newSalutation.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
									|| newSalutation.length() == 2 && Validators.areCharactersSame(newSalutation)
									|| newSalutation.length() == 2 && !Validators.hasVowel(newSalutation)
									) {
								newSalutation = "Friend";
							}
						} else {
							newSalutation = "Friend";
						}
					}
				}
			}
			
			
			// option2
			if(salutationOption == SALUTATION_PREFIX_LASTNAME_FIRSTNAME_LASTNAME) {
				if(lastNames.size() == prefixes.size() && lastNames.size() > 1 &&  prefixes.size() > 1) {
					for(int i = 0; i < prefixes.size(); i++){
						if(i == 0)
							newSalutation += prefixes.get(i) + " " + lastNames.get(i);
						else
							newSalutation += " and " + prefixes.get(i) + " " + lastNames.get(i);
					}
				} else {
					if(lastNames.size() == 1) {
						for(int i = 0; i < prefixes.size(); i++){
							if(i == 0)
								newSalutation = prefixes.get(i);
							else
								newSalutation += " and " + prefixes.get(i);
						}

						if(prefixes.size() > 0) {
							newSalutation += " " + lastNames.get(0);
						}else {
							if(newFirstName.length() > 1) {
								newSalutation = (newFirstName + " " + lastNames.get(0)).trim();
								if(newSalutation.length() >= 2 && newSalutation.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
										|| newSalutation.length() == 2 && Validators.areCharactersSame(newSalutation)
										|| newSalutation.length() == 2 && !Validators.hasVowel(newSalutation)
										) {
									newSalutation = "Friend";
								}
							} else {
								newSalutation = "Friend";
							}
						}

					} else {
						if(newFirstName.length() > 1) {
							newSalutation = "";

							String[] fParts = newFirstName.split("(\\s+)?(and)(\\s+)?");
							
							for(int j = 0; j < fParts.length; ++j) {
								newSalutation += fParts[j];
								
								if(j != fParts.length - 1)
									newSalutation+= " and ";
							}
							
							
							if(newSalutation.length() >= 2 && newSalutation.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
									|| newSalutation.length() == 2 && Validators.areCharactersSame(newSalutation)
									|| newSalutation.length() == 2 && !Validators.hasVowel(newSalutation)
									) {
								newSalutation = "Friend";
							}
						} else {
							newSalutation = "Friend";
						}
					}
				}
			}
			
			
			// option3
			if(salutationOption == SALUTATION_FIRSTNAME_PREFIX_LASTNAME) {
				newSalutation = newFirstName.replaceAll("\\s+", " ").trim();
				if(newSalutation.length() <= 1
						|| newSalutation.length() >= 2 && newSalutation.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
						|| newSalutation.length() == 2 && Validators.areCharactersSame(newSalutation)
						|| newSalutation.length() == 2 && !Validators.hasVowel(newSalutation)
						) {
					
					if(lastNames.size() == prefixes.size() && lastNames.size() > 1 &&  prefixes.size() > 1) {//
						for(int i = 0; i < prefixes.size(); i++){
							if(i == 0)
								newSalutation += prefixes.get(i) + " " + lastNames.get(i);
							else
								newSalutation += " and " + prefixes.get(i) + " " + lastNames.get(i);
						}
					} else {
						if(lastNames.size() == 1) {
							for(int i = 0; i < prefixes.size(); i++){
								if(i == 0)
									newSalutation = prefixes.get(i);
								else
									newSalutation += " and " + prefixes.get(i);
							}

							if(prefixes.size() > 0) {
								newSalutation += " " + lastNames.get(0);
							}else {
								if(newFirstName.length() > 1) {
									newSalutation = newFirstName;
									if(newSalutation.length() >= 2 && newSalutation.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
											|| newSalutation.length() == 2 && Validators.areCharactersSame(newSalutation)
											|| newSalutation.length() == 2 && !Validators.hasVowel(newSalutation)
											) {
										newSalutation = "Friend";
									}
								} else {
									newSalutation = "Friend";
								}
							}

						} else {
							newSalutation = "Friend";
						}
					}//
				}
			}
			
			// option4
			if(salutationOption == SALUTATION_FIRSTNAME) {
				newSalutation = newFirstName.replaceAll("\\s+", " ").trim();
				if(newSalutation.length() <= 1
						|| newSalutation.length() >= 2 && newSalutation.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
						|| newSalutation.length() == 2 && Validators.areCharactersSame(newSalutation)
						|| newSalutation.length() == 2 && !Validators.hasVowel(newSalutation)
						) {
					newSalutation = "Friend";
				}
			}

			//fix lastnames
			if(newLastName.isBlank()) {
				String[] lastFullNameParts = record.getNam1().split(" ");
				String[] lastFirstNameParts = newFirstName.split(" ");
				if(lastFullNameParts.length > 0 && lastFirstNameParts.length > 0) {
					if(!lastFullNameParts[lastFullNameParts.length -1].equalsIgnoreCase(lastFirstNameParts[lastFirstNameParts.length -1])) {
						newLastName = lastFullNameParts[lastFullNameParts.length -1];
					}
				}
			}

			//update first name
			if(newLastName.isBlank() && newFirstName.isBlank())
				newFirstName = record.getNam1();
				
			if(newPrefix.equalsIgnoreCase("M.") || newPrefix.equalsIgnoreCase("M"))
				record.setNam1(record.getNam1().replaceAll("^M ", "M. "));

			record.setNam1(record.getNam1().replaceAll("\\s+", " ").replaceAll("^-", "").replaceAll("(^|\\s+)Dr ", "Dr. ").replaceAll("Mr/S ", "Mr/s. ").replaceAll("Mrs ", "Mrs. ").replaceAll("Ms ", "Ms. ").replaceAll("Mr ", "Mr. ").replaceAll("(^|\\s+)Mme ", "Mme. ").replaceAll("(^|\\s+)Mlle ", "Mlle. ").replaceAll("&", "and").replaceAll("(^|\\s+)Rev ", "Rev. "));
			record.setFstName(newFirstName.replaceAll("\\s+", " ").replaceAll("^-", "").trim());
			record.setPrefix(newPrefix.replaceAll("\\s+", " ").replaceAll("^-", "").trim());
			record.setLstName(newLastName.replaceAll("\\s+", " ").replaceAll("^-", "").trim());
			record.setDearSal(newSalutation.replaceAll("\\s+", " ").replaceAll("^-", "").trim());
			
			//update prefixes
			String[] newPrefixParts = record.getPrefix().split("\\s+");
				if(removedPrefixList.contains(record.getDfId()))
					if(newPrefixParts[0].length() > record.getNam1().length() || !newPrefixParts[0].equalsIgnoreCase(record.getNam1().substring(0, newPrefixParts[0].length())))
						record.setNam1(newPrefixParts[0] + " " + record.getNam1());

		}
	}


	public void reset() {
		prefixHeaders = new ArrayList<>();
		firstNameHeaders = new ArrayList<>();
		middleNameHeaders = new ArrayList<>();
		lastNameHeaders = new ArrayList<>();
		//salutationHeaders = new ArrayList<>();
		fullNameHeaders = new ArrayList<>();
		companyHeaders = new ArrayList<>();
		address1Headers = new ArrayList<>();
		address2Headers = new ArrayList<>();
		address3Headers = new ArrayList<>();
		cityHeaders = new ArrayList<>();
		provinceHeaders = new ArrayList<>();
		pcHeaders = new ArrayList<>();
		countryHeaders = new ArrayList<>();

		spousePrefixHeaders = new ArrayList<>();
		spouseFirstNameHeaders = new ArrayList<>();
		spouseMiddleNameHeaders = new ArrayList<>();
		spouseLastNameHeaders = new ArrayList<>();
		//spouseSalutationHeaders = new ArrayList<>();
		spouseFullNameHeaders = new ArrayList<>();

		patternMap = new HashMap<>();
		patternNameMap = new HashMap<>();
		fileNameFieldsMap = new HashMap<>();
		usedHeaders = new HashSet<>();

		prefixesFirstPass = null;
		companyKeywords = null;
		removedPrefixList = new ArrayList<>();
	}


}
