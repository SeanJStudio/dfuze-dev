package com.mom.dfuze.data.jobs.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;

public class ParseCityProvPC implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Parse CityProvPC";
	
	protected String[] REQUIRED_FIELDS = {
			UserData.fieldName.CITY.getName()
			};
	
	protected String DESCRIPTION = "<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Parses merged City, Prov/State PC/Zip into seperate parts</li>"
			+ "</ul>"
			+ "<br/>Instructions"
			+ "<ol>"
			+ "<li>Map City, Prov PC to City</li>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";


	private static Pattern CAN_PC_PATTERN = Pattern.compile("(?i)[ABCEGHJ-NPRSTVXY]\\d[ABCEGHJ-NPRSTV-Z][ -]?\\d[ABCEGHJ-NPRSTV-Z]\\d", Pattern.CASE_INSENSITIVE);
	private static Pattern US_ZIP_PATTERN = Pattern.compile("(?i)[0-9]{5}(?:-[0-9]{4})?", Pattern.CASE_INSENSITIVE);
	private static Pattern CITY_PATTERN = Pattern.compile("^.+(?=,)", Pattern.CASE_INSENSITIVE);
	private static Pattern PROV_PATTERN = Pattern.compile("ontario|quebec|nova scotia|new brunswick|manitoba|british columbia|prince edward island|saskatchewan|alberta|newfoundland and labrador|northwest territories|yukon|nunavut|(?<=\\s|^|,|\\.)(o(\\s\\.|\\.\\s|\\.|\\s)?n|q(\\s\\.|\\.\\s|\\.|\\s)?c|n(\\s\\.|\\.\\s|\\.|\\s)?s|n(\\s\\.|\\.\\s|\\.|\\s)?b|m(\\s\\.|\\.\\s|\\.|\\s)?b|b(\\s\\.|\\.\\s|\\.|\\s)?c|p(\\s\\.|\\.\\s|\\.|\\s)?e|s(\\s\\.|\\.\\s|\\.|\\s)?k|a(\\s\\.|\\.\\s|\\.|\\s)?b|n(\\s\\.|\\.\\s|\\.|\\s)?l|n(\\s\\.|\\.\\s|\\.|\\s)?t|y(\\s\\.|\\.\\s|\\.|\\s)?t|n(\\s\\.|\\.\\s|\\.|\\s)?u)(?=\\.|,|\\s|$)", Pattern.CASE_INSENSITIVE);
	private static Pattern STATE_PATTERN = Pattern.compile("alabama|alaska|american samoa|arizona|arkansas|california|colorado|connecticut|delaware|district of columbia|federated states of micronesia|florida|georgia|guam|hawaii|idaho|illinois|indiana|iowa|kansas|kentucky|louisiana|maine|marshall islands|maryland|massachusetts|michigan|minnesota|mississippi|missouri|montana|nebraska|nevada|new hampshire|new jersey|new mexico|new york|north carolina|north dakota|northern marianais|ohio|oklahoma|oregon|palau|pennsylvania|puerto rico|rhode island|south carolina|south dakota|tennessee|texas|utah|vermont|virginia|virgin islands|washington|west virginia|wisconsin|wyoming|(?<=\\s|^|,|\\.)(a(\\s\\.|\\.\\s|\\.|\\s)?l|a(\\s\\.|\\.\\s|\\.|\\s)?k|a(\\s\\.|\\.\\s|\\.|\\s)?s|a(\\s\\.|\\.\\s|\\.|\\s)?z|a(\\s\\.|\\.\\s|\\.|\\s)?r|c(\\s\\.|\\.\\s|\\.|\\s)?a|c(\\s\\.|\\.\\s|\\.|\\s)?o|c(\\s\\.|\\.\\s|\\.|\\s)?t|d(\\s\\.|\\.\\s|\\.|\\s)?e|d(\\s\\.|\\.\\s|\\.|\\s)?c|f(\\s\\.|\\.\\s|\\.|\\s)?m|f(\\s\\.|\\.\\s|\\.|\\s)?l|g(\\s\\.|\\.\\s|\\.|\\s)?a|g(\\s\\.|\\.\\s|\\.|\\s)?u|h(\\s\\.|\\.\\s|\\.|\\s)?i|i(\\s\\.|\\.\\s|\\.|\\s)?d|i(\\s\\.|\\.\\s|\\.|\\s)?l|i(\\s\\.|\\.\\s|\\.|\\s)?n|i(\\s\\.|\\.\\s|\\.|\\s)?a|k(\\s\\.|\\.\\s|\\.|\\s)?s|k(\\s\\.|\\.\\s|\\.|\\s)?y|l(\\s\\.|\\.\\s|\\.|\\s)?a|m(\\s\\.|\\.\\s|\\.|\\s)?e|m(\\s\\.|\\.\\s|\\.|\\s)?h|m(\\s\\.|\\.\\s|\\.|\\s)?d|m(\\s\\.|\\.\\s|\\.|\\s)?a|m(\\s\\.|\\.\\s|\\.|\\s)?i|m(\\s\\.|\\.\\s|\\.|\\s)?n|m(\\s\\.|\\.\\s|\\.|\\s)?s|m(\\s\\.|\\.\\s|\\.|\\s)?o|m(\\s\\.|\\.\\s|\\.|\\s)?t|n(\\s\\.|\\.\\s|\\.|\\s)?e|n(\\s\\.|\\.\\s|\\.|\\s)?v|n(\\s\\.|\\.\\s|\\.|\\s)?h|n(\\s\\.|\\.\\s|\\.|\\s)?j|n(\\s\\.|\\.\\s|\\.|\\s)?m|n(\\s\\.|\\.\\s|\\.|\\s)?y|n(\\s\\.|\\.\\s|\\.|\\s)?c|n(\\s\\.|\\.\\s|\\.|\\s)?d|m(\\s\\.|\\.\\s|\\.|\\s)?p|o(\\s\\.|\\.\\s|\\.|\\s)?h|o(\\s\\.|\\.\\s|\\.|\\s)?k|o(\\s\\.|\\.\\s|\\.|\\s)?r|p(\\s\\.|\\.\\s|\\.|\\s)?w|p(\\s\\.|\\.\\s|\\.|\\s)?a|p(\\s\\.|\\.\\s|\\.|\\s)?r|r(\\s\\.|\\.\\s|\\.|\\s)?i|s(\\s\\.|\\.\\s|\\.|\\s)?c|s(\\s\\.|\\.\\s|\\.|\\s)?d|t(\\s\\.|\\.\\s|\\.|\\s)?n|t(\\s\\.|\\.\\s|\\.|\\s)?x|u(\\s\\.|\\.\\s|\\.|\\s)?t|v(\\s\\.|\\.\\s|\\.|\\s)?t|v(\\s\\.|\\.\\s|\\.|\\s)?a|v(\\s\\.|\\.\\s|\\.|\\s)?i|w(\\s\\.|\\.\\s|\\.|\\s)?a|w(\\s\\.|\\.\\s|\\.|\\s)?v|w(\\s\\.|\\.\\s|\\.|\\s)?i|w(\\s\\.|\\.\\s|\\.|\\s)?y)(?=,|\\.|\\s|$)", Pattern.CASE_INSENSITIVE);

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

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) {

		String[][] data = userData.getData();

		int counter = -1;
		for (int i = 0; i < data.length; i++) {

			String cityProvPC = data[i][userData.getCityIndex()].replaceAll("[\\\"`=\\[\\];'/~\\{\\}\\|:<>\\?!@#\\$%\\^&\\*\\(\\)\\+\\_\\\\]","").replaceAll("\\s+"," ");

			String city = "", pc = "", prov = "";

			Matcher pcMatcher = CAN_PC_PATTERN.matcher(cityProvPC);
			Matcher zipMatcher = US_ZIP_PATTERN.matcher(cityProvPC);

			boolean hasPc = false;
			//boolean hasZip = false;
			
			if(pcMatcher.find()) {
				hasPc = true;
				pc = pcMatcher.group();
				cityProvPC = cityProvPC.replaceAll(pc, "");
			} else if(zipMatcher.find()) {
				//hasZip = true;
				pc = zipMatcher.group();
				cityProvPC = cityProvPC.replaceAll(pc, "");
			}

			Matcher provMatcher = PROV_PATTERN.matcher(cityProvPC);
			Matcher stateMatcher = STATE_PATTERN.matcher(cityProvPC);
			boolean hasProv = false;

			int groupNum = -1;

			while(provMatcher.find()) {
				hasProv = true;
				prov = provMatcher.group();
				cityProvPC = cityProvPC.replaceAll("(?<=^|\\s|,|\\.)" + provMatcher.group(++groupNum) + "(?=$|\\s|,|\\.)", "");
			}

			groupNum = -1;
			if(!hasProv && !hasPc) {
				while(stateMatcher.find()) {
					prov = stateMatcher.group();
					cityProvPC = cityProvPC.replaceAll("(?<=^|\\s|,|\\.)" + stateMatcher.group(++groupNum) + "(?=$|\\s|,|\\.)", "");
				}
			}

			Matcher cityMatcher = CITY_PATTERN.matcher(cityProvPC);

			if(cityMatcher.find()) {
				city = cityMatcher.group().trim();
				cityProvPC = cityProvPC.replaceAll(city, "");
			} else {
				city = cityProvPC;
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

			Record record = new Record.Builder(++counter, data[i], "", "", "").setCity(city).setProv(prov).setPCode(pc).build();

			userData.add(record); // add the record the recordList in userData
		}


		// Set the Header fields that we want to output
		userData.setDfHeaders(new String[] {
				UserData.fieldName.CITY.getName(),
				UserData.fieldName.PROVINCE.getName(),
				UserData.fieldName.POSTALCODE.getName()
		});

	}

}