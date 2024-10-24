package com.mom.dfuze.data.jobs.bcaa;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.io.TextWriter;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserInputDialog;

public class Tai implements RunBCAABehavior {

	private String BEHAVIOR_NAME = "TAI";
	private String[] REQUIRED_FIELDS = {};
	private String DESCRIPTION = "<html>Instructions<br/><ol><li>Click add file name to data checkbox</li><li>Click create headers checkbox</li><li>Delimit by ~</li></ol><br/>Notes<br/><ul><li>1 Seed and 5 John Q Samples will automatically be added</li></ul></html>";

	public static final String JQS_FILENAME = "JQS.csv";
	public static final String DUPE_FILENAME = "DUPES.csv";

	private static final int MEMBERSHIP_INDEX = 0;
	private static final int ACCOUNT_INDEX = 1;
	private static final int LAST_FIRST_INDEX = 2;
	private static final int PLAN_INDEX = 3;
	private static final int EFF_DATE_INDEX = 4;
	private static final int EXP_DATE_INDEX = 5;
	private static final int FIRST_LAST_INDEX = 6;
	private static final int ADD1_INDEX = 7;
	private static final int ADD2_INDEX = 8;
	private static final int CITY_PROV_PC_INDEX = 9;

	private static final int CODELINE_1_INDEX = 12;
	private static final int CODELINE_2_INDEX = 11;
	private static final int CODELINE_3_INDEX = 13;
	
	private static Pattern CAN_PC_PATTERN = Pattern.compile("(?i)[ABCEGHJ-NPRSTVXY]\\d[ABCEGHJ-NPRSTV-Z][ -]?\\d[ABCEGHJ-NPRSTV-Z]\\d", Pattern.CASE_INSENSITIVE);
	private static Pattern US_ZIP_PATTERN = Pattern.compile("(?i)[0-9]{5}(?:-[0-9]{4})?", Pattern.CASE_INSENSITIVE);
	private static Pattern CITY_PATTERN = Pattern.compile("^.+(?=,)", Pattern.CASE_INSENSITIVE);
	private static Pattern PROV_PATTERN = Pattern.compile("ontario|quebec|nova scotia|new brunswick|manitoba|british columbia|prince edward island|saskatchewan|alberta|newfoundland and labrador|northwest territories|yukon|nunavut|(?<=\\s|^|,|\\.)(o(\\s\\.|\\.\\s|\\.|\\s)?n|q(\\s\\.|\\.\\s|\\.|\\s)?c|n(\\s\\.|\\.\\s|\\.|\\s)?s|n(\\s\\.|\\.\\s|\\.|\\s)?b|m(\\s\\.|\\.\\s|\\.|\\s)?b|b(\\s\\.|\\.\\s|\\.|\\s)?c|p(\\s\\.|\\.\\s|\\.|\\s)?e|s(\\s\\.|\\.\\s|\\.|\\s)?k|a(\\s\\.|\\.\\s|\\.|\\s)?b|n(\\s\\.|\\.\\s|\\.|\\s)?l|n(\\s\\.|\\.\\s|\\.|\\s)?t|y(\\s\\.|\\.\\s|\\.|\\s)?t|n(\\s\\.|\\.\\s|\\.|\\s)?u)(?=\\.|,|\\s|$)", Pattern.CASE_INSENSITIVE);
	private static Pattern STATE_PATTERN = Pattern.compile("alabama|alaska|american samoa|arizona|arkansas|california|colorado|connecticut|delaware|district of columbia|federated states of micronesia|florida|georgia|guam|hawaii|idaho|illinois|indiana|iowa|kansas|kentucky|louisiana|maine|marshall islands|maryland|massachusetts|michigan|minnesota|mississippi|missouri|montana|nebraska|nevada|new hampshire|new jersey|new mexico|new york|north carolina|north dakota|northern marianais|ohio|oklahoma|oregon|palau|pennsylvania|puerto rico|rhode island|south carolina|south dakota|tennessee|texas|utah|vermont|virginia|virgin islands|washington|west virginia|wisconsin|wyoming|(?<=\\s|^|,|\\.)(a(\\s\\.|\\.\\s|\\.|\\s)?l|a(\\s\\.|\\.\\s|\\.|\\s)?k|a(\\s\\.|\\.\\s|\\.|\\s)?s|a(\\s\\.|\\.\\s|\\.|\\s)?z|a(\\s\\.|\\.\\s|\\.|\\s)?r|c(\\s\\.|\\.\\s|\\.|\\s)?a|c(\\s\\.|\\.\\s|\\.|\\s)?o|c(\\s\\.|\\.\\s|\\.|\\s)?t|d(\\s\\.|\\.\\s|\\.|\\s)?e|d(\\s\\.|\\.\\s|\\.|\\s)?c|f(\\s\\.|\\.\\s|\\.|\\s)?m|f(\\s\\.|\\.\\s|\\.|\\s)?l|g(\\s\\.|\\.\\s|\\.|\\s)?a|g(\\s\\.|\\.\\s|\\.|\\s)?u|h(\\s\\.|\\.\\s|\\.|\\s)?i|i(\\s\\.|\\.\\s|\\.|\\s)?d|i(\\s\\.|\\.\\s|\\.|\\s)?l|i(\\s\\.|\\.\\s|\\.|\\s)?n|i(\\s\\.|\\.\\s|\\.|\\s)?a|k(\\s\\.|\\.\\s|\\.|\\s)?s|k(\\s\\.|\\.\\s|\\.|\\s)?y|l(\\s\\.|\\.\\s|\\.|\\s)?a|m(\\s\\.|\\.\\s|\\.|\\s)?e|m(\\s\\.|\\.\\s|\\.|\\s)?h|m(\\s\\.|\\.\\s|\\.|\\s)?d|m(\\s\\.|\\.\\s|\\.|\\s)?a|m(\\s\\.|\\.\\s|\\.|\\s)?i|m(\\s\\.|\\.\\s|\\.|\\s)?n|m(\\s\\.|\\.\\s|\\.|\\s)?s|m(\\s\\.|\\.\\s|\\.|\\s)?o|m(\\s\\.|\\.\\s|\\.|\\s)?t|n(\\s\\.|\\.\\s|\\.|\\s)?e|n(\\s\\.|\\.\\s|\\.|\\s)?v|n(\\s\\.|\\.\\s|\\.|\\s)?h|n(\\s\\.|\\.\\s|\\.|\\s)?j|n(\\s\\.|\\.\\s|\\.|\\s)?m|n(\\s\\.|\\.\\s|\\.|\\s)?y|n(\\s\\.|\\.\\s|\\.|\\s)?c|n(\\s\\.|\\.\\s|\\.|\\s)?d|m(\\s\\.|\\.\\s|\\.|\\s)?p|o(\\s\\.|\\.\\s|\\.|\\s)?h|o(\\s\\.|\\.\\s|\\.|\\s)?k|o(\\s\\.|\\.\\s|\\.|\\s)?r|p(\\s\\.|\\.\\s|\\.|\\s)?w|p(\\s\\.|\\.\\s|\\.|\\s)?a|p(\\s\\.|\\.\\s|\\.|\\s)?r|r(\\s\\.|\\.\\s|\\.|\\s)?i|s(\\s\\.|\\.\\s|\\.|\\s)?c|s(\\s\\.|\\.\\s|\\.|\\s)?d|t(\\s\\.|\\.\\s|\\.|\\s)?n|t(\\s\\.|\\.\\s|\\.|\\s)?x|u(\\s\\.|\\.\\s|\\.|\\s)?t|v(\\s\\.|\\.\\s|\\.|\\s)?t|v(\\s\\.|\\.\\s|\\.|\\s)?a|v(\\s\\.|\\.\\s|\\.|\\s)?i|w(\\s\\.|\\.\\s|\\.|\\s)?a|w(\\s\\.|\\.\\s|\\.|\\s)?v|w(\\s\\.|\\.\\s|\\.|\\s)?i|w(\\s\\.|\\.\\s|\\.|\\s)?y)(?=,|\\.|\\s|$)", Pattern.CASE_INSENSITIVE);


	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {
		
		/********************************
		 * MAKE SURE FILENAME IS INCLUDED
		 ********************************/
		if(!Common.hasFileName(userData))
			throw new ApplicationException(String.format("Error: file name is not included\n\nPlease do a new job and ensure the add file name to data checkbox is checked."));
		
		/********************************
		 * MAKE SURE HEADERS WERE CREATED
		 ********************************/	
		if(!Common.hasCreatedHeaders(userData))
			throw new ApplicationException(String.format("Error: Headers were not created\n\nPlease do a new job and ensure the create headers checkbox is checked."));
		
		/***************************************************************************
		 * ENSURE ONLY 15 FIELDS EXIST (14 from data, 1 for filename added by dfuze)
		 ***************************************************************************/
		if(userData.getInHeaders().length > 15)
			throw new ApplicationException(String.format("Error: Expected 14 data fields but found " + (userData.getInHeaders().length - 1) + "."));

		// Reset the headers
		String[] newHeaders = Arrays.copyOf(userData.getInHeaders(), userData.getInHeaders().length);
		newHeaders[MEMBERSHIP_INDEX] = "Membership";
		newHeaders[ACCOUNT_INDEX] = "Account";
		newHeaders[LAST_FIRST_INDEX] = "Name";
		newHeaders[PLAN_INDEX] = "Plan";
		newHeaders[EFF_DATE_INDEX] = "Eff_Date";
		newHeaders[EXP_DATE_INDEX] = "Exp_Date";
		newHeaders[FIRST_LAST_INDEX] = "Fullname";
		newHeaders[ADD1_INDEX] = "Orig_Addr1";
		newHeaders[ADD2_INDEX] = "Orig_Addr2";
		newHeaders[CITY_PROV_PC_INDEX] = "City_Prov_PC";

		userData.setInHeaders(newHeaders);

		// The users' input data
		String[][] data = userData.getData();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {
			/********************************
			 * CASE THE NAME COMPONENTS HERE
			 ********************************/
			data[i][LAST_FIRST_INDEX] = Common.caseName(data[i][LAST_FIRST_INDEX]);
			data[i][FIRST_LAST_INDEX] = Common.caseName(data[i][FIRST_LAST_INDEX]);
			
			String lastFirst = data[i][LAST_FIRST_INDEX];
			String nam1 = data[i][FIRST_LAST_INDEX];
			String add1 = data[i][ADD1_INDEX];
			String add2 = data[i][ADD2_INDEX];
			
			//String cityProvPC = data[i][CITY_PROV_PC_INDEX].replaceAll("[\\\"`=\\[\\];'/~\\{\\}\\|:<>\\?!@#\\$%\\^&\\*\\(\\)\\+\\_\\\\]","").replaceAll("\\s+"," ");
			String editedCityProvPC = data[i][CITY_PROV_PC_INDEX].replaceAll("[\\\"`=\\[\\];'/~\\{\\}\\|:<>\\?!@#\\$%\\^&\\*\\(\\)\\+\\_\\\\]","").replaceAll("\\s+"," ");

			String codeLinePart1 = data[i][CODELINE_1_INDEX];
			String codeLinePart2 = data[i][CODELINE_2_INDEX];
			String codeLinePart3 = data[i][CODELINE_3_INDEX];

			String codeLine = codeLinePart1;

			if(!codeLinePart2.isBlank()) {
				if(!codeLine.isBlank())
					codeLine += "-" +codeLinePart2;
				else
					codeLine += codeLinePart2;
			}

			if(!codeLinePart3.isBlank())
				codeLine += "-" +codeLinePart3;

			String fstName = "";
			String lstName = "";

			/********************************
			 * PARSE OUT NAME COMPONENTS HERE
			 ********************************/
			String[] lastFirstParts = lastFirst.split(",\\s+");

			if(lastFirstParts.length > 0) {
				lstName = lastFirstParts[0];
				if(lastFirstParts.length > 1) {
					fstName = lastFirstParts[1];
				}
			}

			/***********************************
			 * PARSE OUT ADDRESS COMPONENTS HERE
			 ***********************************/
			String city = "", pc = "", prov = "";

			Matcher pcMatcher = CAN_PC_PATTERN.matcher(editedCityProvPC);
			Matcher zipMatcher = US_ZIP_PATTERN.matcher(editedCityProvPC);

			boolean hasPc = false;
			
			if(pcMatcher.find()) {
				hasPc = true;
				pc = pcMatcher.group();
				editedCityProvPC = editedCityProvPC.replaceAll(pc, "");
			} else if(zipMatcher.find()) {
				pc = zipMatcher.group();
				editedCityProvPC = editedCityProvPC.replaceAll(pc, "");
			}

			Matcher provMatcher = PROV_PATTERN.matcher(editedCityProvPC);
			Matcher stateMatcher = STATE_PATTERN.matcher(editedCityProvPC);
			boolean hasProv = false;

			int groupNum = -1;

			while(provMatcher.find()) {
				hasProv = true;
				prov = provMatcher.group();
				editedCityProvPC = editedCityProvPC.replaceAll("(?<=^|\\s|,|\\.)" + provMatcher.group(++groupNum) + "(?=$|\\s|,|\\.)", "");
			}

			groupNum = -1;
			if(!hasProv && !hasPc) {
				while(stateMatcher.find()) {
					prov = stateMatcher.group();
					editedCityProvPC = editedCityProvPC.replaceAll("(?<=^|\\s|,|\\.)" + stateMatcher.group(++groupNum) + "(?=$|\\s|,|\\.)", "");
				}
			}

			Matcher cityMatcher = CITY_PATTERN.matcher(editedCityProvPC);

			if(cityMatcher.find()) {
				city = cityMatcher.group().trim();
				editedCityProvPC = editedCityProvPC.replaceAll(city, "");
			} else {
				city = editedCityProvPC;
			}

			// FIX CITY
			city = city.replaceAll("[^A-Za-zÀ-ÖØ-öø-ÿ0-9\\s]", "").replaceAll("\\s+", " ").trim();

			// FIX PROV
			prov = prov.replaceAll("[^A-Za-zÀ-ÖØ-öø-ÿ\\s]", "").replaceAll("\\s+", " ").trim();

			// FIX PC
			if(hasPc) {
				pc = pc.replaceAll("[^A-Za-zÀ-ÖØ-öø-ÿ0-9]", "");
				pc = pc.substring(0, 3) + " " + pc.substring(3);
			}
			
			
			/***********************
			 * BUILD THE RECORD HERE
			 ***********************/
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setFstName(fstName)
					.setLstName(lstName)
					.setNam1(nam1)
					.setAdd1(add1)
					.setAdd2(add2)
					.setCity(city)
					.setProv(prov)
					.setPCode(pc)
					.setCodeLine(codeLine)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] {
				UserData.fieldName.FIRSTNAME.getName(),
				UserData.fieldName.LASTNAME.getName(),
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.ADDRESS2.getName(),
				UserData.fieldName.CITY.getName(),
				UserData.fieldName.PROVINCE.getName(),
				UserData.fieldName.POSTALCODE.getName(),
				UserData.fieldName.CODELINE.getName(),
				UserData.fieldName.DFID.getName()
		});


		// Create a new id to use from where we left off
		int idToUse = data.length - 1;
		int membershipNumToUse = 9999993;

		/************************
		 * ADD BRENDA'S SEED HERE
		 ************************/
		String[] seedArray = Arrays.copyOf(data[0], data[0].length);
		seedArray[MEMBERSHIP_INDEX] = String.valueOf(++membershipNumToUse);
		seedArray[LAST_FIRST_INDEX] = "Porter, Sam";
		seedArray[FIRST_LAST_INDEX] = "Sam Porter";
		seedArray[ADD1_INDEX] = "7550 Lowland Dr";
		seedArray[ADD2_INDEX] = "";
		seedArray[CITY_PROV_PC_INDEX] = "Burnaby BC V5J5A4";

		String seedCodeLinePart1 = seedArray[CODELINE_1_INDEX];
		String seedCodeLinePart2 = seedArray[CODELINE_2_INDEX];
		String seedCodeLinePart3 = seedArray[CODELINE_3_INDEX];
		String seedCodeLine = seedCodeLinePart1;

		if(!seedCodeLinePart2.isBlank())
			if(!seedCodeLine.isBlank())
				seedCodeLine += "-" +seedCodeLinePart2;
			else
				seedCodeLine += seedCodeLinePart2;

		if(!seedCodeLinePart3.isBlank())
			seedCodeLine += "-" +seedCodeLinePart3;

		Record seedRecord = new Record.Builder(++idToUse, seedArray, "", "", "").setFstName("Sam").setLstName("Porter").setNam1("Sam Porter").setAdd1("7550 Lowland Dr").setAdd2("").setCity("Burnaby").setProv("BC").setPCode("V5J5A4").setCodeLine(seedCodeLine).build();
		userData.add(seedRecord);
		
		
		/************************
		 * REMOVE DUPLICATES HERE
		 ************************/
		List<Record> dupeRecordList = new ArrayList<>();
		Set<String> membershipAccIDs = new HashSet<>();

		for(int i = userData.getRecordList().size() -1; i >= 0; --i) {
			Record record = userData.getRecordList().get(i);
			String membershipAccID = record.getDfInData()[MEMBERSHIP_INDEX] + record.getDfInData()[ACCOUNT_INDEX];
			if(!membershipAccIDs.add(membershipAccID))
				dupeRecordList.add(userData.getRecordList().remove(i));
		}
		
		if(dupeRecordList.size() > 0) {
			File dupeFile = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_" + DUPE_FILENAME);

			// Write the JQS to the file
			try {
				TextWriter.write(dupeFile, ',', true, userData.getExportHeaders(), userData.getExportData(dupeRecordList));
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/********************
		 * GET THE JOB NAME
		 ********************/
		String jobName = "";
		UserInputDialog userInputDialog = new UserInputDialog(UiController.getMainFrame(), "Enter the Docket# and Job Name ex. 70684 BCAA TAI");
		userInputDialog.setVisible(true);
		
		if(userInputDialog.getIsNextPressed())
			jobName = userInputDialog.getUserInput();
		else
			throw new ApplicationException(String.format("ERROR: dialog closed"));
		
		/**********************************
		 * CREATE JOHN Q SAMPLES HERE
		 **********************************/
		List<Record> jqsRecordList = new ArrayList<>();
		String[] jqsArray = Arrays.copyOf(data[0], data[0].length);
		jqsArray[LAST_FIRST_INDEX] = "Sample, John";
		jqsArray[FIRST_LAST_INDEX] = "John Sample";
		jqsArray[ADD1_INDEX] = "123 Any St";
		jqsArray[ADD2_INDEX] = jobName;
		jqsArray[CITY_PROV_PC_INDEX] = "City Prov V0V0V0";

		String jqsCodeLinePart1 = jqsArray[CODELINE_1_INDEX];
		String jqsCodeLinePart2 = jqsArray[CODELINE_2_INDEX];
		String jqsCodeLinePart3 = jqsArray[CODELINE_3_INDEX];
		String jqsCodeLine = jqsCodeLinePart1;

		if(!jqsCodeLinePart2.isBlank())
			if(!jqsCodeLine.isBlank())
				jqsCodeLine += "-" +jqsCodeLinePart2;
			else
				jqsCodeLine += jqsCodeLinePart2;

		if(!jqsCodeLinePart3.isBlank())
			jqsCodeLine += "-" +jqsCodeLinePart3;

		for(int i = 0; i < 5; ++i) {
			String[] tempJqsArray = Arrays.copyOf(jqsArray, jqsArray.length);
			tempJqsArray[MEMBERSHIP_INDEX] = String.valueOf(++membershipNumToUse);
			Record record = new Record.Builder(++idToUse, tempJqsArray, "", "", "").setFstName("John").setLstName("Sample").setNam1("John Sample").setAdd1("123 Any St").setAdd2("ENTER JOB NAME HERE").setCity("City").setProv("Prov").setPCode("V0V0V0").setCodeLine(jqsCodeLine).build();
			jqsRecordList.add(record);
		}

		File jqsFile = new File(UserPrefs.getLastUsedFolder() + "\\" + UiController.getUserDataFileName() + "_" + JQS_FILENAME);

		// Write the JQS to the file
		try {
			TextWriter.write(jqsFile, ',', true, userData.getExportHeaders(), userData.getExportData(jqsRecordList));
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		// TODO Auto-generated method stub
		return true;
	}

}