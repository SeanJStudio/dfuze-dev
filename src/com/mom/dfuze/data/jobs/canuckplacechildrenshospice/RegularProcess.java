/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.canuckplacechildrenshospice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Validators;

/**
 * RegularProcess implements a RunBehavior for Canucks Place Childrens Hospice Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RegularProcess implements RunCanuckPlaceChildrensHospiceBehavior {

	private String BEHAVIOR_NAME = "Regular Process";

	private String[] REQUIRED_FIELDS = {
			UserData.fieldName.DEAR_SALUTATION.getName(),
			UserData.fieldName.PREFIX.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.SEGMENT_CODE.getName(),
			UserData.fieldName.LAST_DONATION_AMOUNT.getName()
			};
	
	private String DESCRIPTION = "<html>"
	        + "Description<br/>"
	        + "<ol>"
	        + "<li>Segments the data by analyzing the codeline</li>"
	        + "<li>Calculates the single and monthly gift arrays</li>"
	        + "<li>Fixes salutations to [prefix + lastname]->[firstname]->[friend]</li>"
	        + "</ol>"
			+ "Instructions<br/>"
			+ "<ol>"
			+ "<li>Add seeds to data before Dfuze</li>"
			+ "<ul><li>Put 'Seed' in 'Codeline'</li></ul>"
			+ "<li>Add the 'Codeline' for rental files before Dfuze</li>"
			+ "<ul><li>The 'Codeline' should be like 'CCM 24 23-23 ACQ HOLAP'</li></ul>"
			+ "</ol>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";
	
	// Campaign specific segments
	final static String SEG_TELETHON_ACTIVE = "Telethon Active";
	final static String SEG_TELETHON_CURRENT = "Telethon Current";
	final static String SEG_TELETHON_LAPSED = "Telethon Lapsed";
	final static String SEG_TELETHON = "Telethon";
	final static String SEG_TRIBUTE = "Tribute";
	final static String SEG_5050 = "5050";
	final static String SEG_MONTHLY_UPGRADE = "Monthly Upgrade";
	final static String SEG_REACTIVATION = "Reactivation";
	final static String SEG_CONVERSION = "Conversion";
	
	// Regular segments
	final static String SEG_ACTIVE = "Active";
	final static String SEG_CURRENT = "Current";
	final static String SEG_LONG_LAPSED = "Long Lapsed";
	final static String SEG_SUPER_LAPSED = "Super Lapsed";
	final static String SEG_LAPSED = "Lapsed";
	final static String SEG_LEADER_ACTIVE = "Leader Active";
	final static String SEG_LEADER_CURRENT = "Leader Current";
	final static String SEG_LEADER_LAPSED = "Leader Lapsed";
	final static String SEG_LEADER = "Leader";
	final static String SEG_ORGANIZATION = "Organization";
	final static String SEG_MONTHLY = "Monthly";
	final static String SEG_NEW_MONTHLY = "New Monthly";
	final static String SEG_PRM = "PRM";
	final static String SEG_SEED = "Seed";
	final static String SEG_ACQ = "Acquisition";
	
	// Campaign specific segment patterns
	Pattern telethonActivePattern = Pattern.compile("^[t][a]", Pattern.CASE_INSENSITIVE);							//active telethon
	Pattern telethonCurrentPattern = Pattern.compile("^[t][c]", Pattern.CASE_INSENSITIVE);							//active telethon
	Pattern telethonLapsedPattern = Pattern.compile("^[t][l]", Pattern.CASE_INSENSITIVE);							//lapsed telethon
	Pattern telethonPattern = Pattern.compile("[t][e][l]", Pattern.CASE_INSENSITIVE);								//telethon
	Pattern tributePattern = Pattern.compile("^[t][m]|[t][r][i][b]", Pattern.CASE_INSENSITIVE);						//tribute
	Pattern fiftyFiftyPattern = Pattern.compile("^[5][0]|[5][0][5][0]", Pattern.CASE_INSENSITIVE);					//5050
	Pattern monthlyUpgradePattern = Pattern.compile("[m][o][n].*[u][p][g][d]", Pattern.CASE_INSENSITIVE);			//Monthly Upgrade
	Pattern reactivationPattern = Pattern.compile("[l][p][s][m][a]", Pattern.CASE_INSENSITIVE);						//Monthly Reactivation
	Pattern conversionPattern = Pattern.compile("[c][o][n][v]", Pattern.CASE_INSENSITIVE);							//Conversion
	
	// Regular segment patterns
	Pattern activePattern = Pattern.compile("[a][c][t][i][n][d]|^[H][A]", Pattern.CASE_INSENSITIVE);				//active
	Pattern currentPattern = Pattern.compile("[c][u][r][i][n][d]|^[H][C]", Pattern.CASE_INSENSITIVE);				//current
	Pattern superLapsedPattern = Pattern.compile("[s][l][p][s][i][n][d]", Pattern.CASE_INSENSITIVE);				//super lapsed
	Pattern longLapsedPattern = Pattern.compile("[l][l][p][s][i][n][d]|^[H][S][L]", Pattern.CASE_INSENSITIVE);		//long lapsed
	Pattern lapsedPattern = Pattern.compile("[l][p][s][i][n][d]|^[H][L]", Pattern.CASE_INSENSITIVE);				//lapsed
	Pattern leaderActivePattern = Pattern.compile("^[L][A]", Pattern.CASE_INSENSITIVE);								//leadership
	Pattern leaderCurrentPattern = Pattern.compile("^[L][C]", Pattern.CASE_INSENSITIVE);							//leadership
	Pattern leaderLapsedPattern = Pattern.compile("^[L][L]", Pattern.CASE_INSENSITIVE);								//leadership
	Pattern leaderPattern = Pattern.compile("[l][d][r]", Pattern.CASE_INSENSITIVE);									//leadership
	Pattern orgPattern = Pattern.compile("[o][r][g]", Pattern.CASE_INSENSITIVE);									//corporate
	Pattern monthlyPattern = Pattern.compile("[m][o][n](?!.*[u][p][g][d])|^[M][A]", Pattern.CASE_INSENSITIVE);		//monthly
	Pattern newMonthlyPattern = Pattern.compile("^[n][m]", Pattern.CASE_INSENSITIVE);								//new monthly
	Pattern PRMPattern = Pattern.compile("[p][r][m]", Pattern.CASE_INSENSITIVE);									//PRM
	Pattern seedPattern = Pattern.compile("[s][e][e][d]", Pattern.CASE_INSENSITIVE);								//seed
	Pattern acquisitionPattern = Pattern.compile("(^|\\s)[a][c][q](\\s|$)", Pattern.CASE_INSENSITIVE);				//acquisition
	
	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws Exception {

		// The users' input data
		String[][] data = userData.getData();

		// Loop through the users' input data
		for (int i = 0; i < data.length; ++i) {

			// Get all the fields we can for now
			String segCode = data[i][userData.getSegCodeIndex()];
			String dearSal = data[i][userData.getDearSalIndex()].replaceAll("\"", "");
			String paraSal = dearSal;
			String prefix = data[i][userData.getPrefixIndex()];
			String fnam = data[i][userData.getFstNameIndex()];
			String lnam = data[i][userData.getLstNameIndex()];
			String nam1 = data[i][userData.getNam1Index()].replaceAll("\"", "");
			String cmpny = "";
			String lastGiftAmt = data[i][userData.getLstDnAmtIndex()].replaceAll("[^0-9\\.]", "");
			String seg = "";

			// Check if our patterns match, if not, throw an error
			// Check for campaign specific segments first
			if (telethonActivePattern.matcher(segCode).find())
				seg = SEG_TELETHON_ACTIVE;
			else if (telethonCurrentPattern.matcher(segCode).find())
				seg = SEG_TELETHON_CURRENT;
			else if (telethonLapsedPattern.matcher(segCode).find())
				seg = SEG_TELETHON_LAPSED;
			else if (telethonPattern.matcher(segCode).find())
				seg = SEG_TELETHON;
			else if (tributePattern.matcher(segCode).find())
				seg = SEG_TRIBUTE;
			else if (fiftyFiftyPattern.matcher(segCode).find())
				seg = SEG_5050;
			else if (monthlyUpgradePattern.matcher(segCode).find()) //
				seg = SEG_MONTHLY_UPGRADE;
			else if (reactivationPattern.matcher(segCode).find())
				seg = SEG_REACTIVATION;
			else if (conversionPattern.matcher(segCode).find())
				seg = SEG_CONVERSION;
			else if (activePattern.matcher(segCode).find())
				seg = SEG_ACTIVE;
			else if (currentPattern.matcher(segCode).find())
				seg = SEG_CURRENT;
			else if (longLapsedPattern.matcher(segCode).find())
				seg = SEG_LONG_LAPSED;
			else if (superLapsedPattern.matcher(segCode).find())
				seg = SEG_SUPER_LAPSED;
			else if (lapsedPattern.matcher(segCode).find())
				seg = SEG_LAPSED;
			else if (leaderActivePattern.matcher(segCode).find())//
				seg = SEG_LEADER_ACTIVE;
			else if (leaderCurrentPattern.matcher(segCode).find())//
				seg = SEG_LEADER_CURRENT;
			else if (leaderLapsedPattern.matcher(segCode).find())
				seg = SEG_LEADER_LAPSED;
			else if (leaderPattern.matcher(segCode).find())
				seg = SEG_LEADER;
			else if (leaderPattern.matcher(segCode).find())
				seg = SEG_LEADER;
			else if (orgPattern.matcher(segCode).find())
				seg = SEG_ORGANIZATION;
			else if (monthlyPattern.matcher(segCode).find())
				seg = SEG_MONTHLY;
			else if (newMonthlyPattern.matcher(segCode).find())
				seg = SEG_NEW_MONTHLY;
			else if (PRMPattern.matcher(segCode).find())
				seg = SEG_PRM;
			else if (seedPattern.matcher(segCode).find() || segCode.length() == 0)
				seg = SEG_SEED;
			else if (acquisitionPattern.matcher(segCode).find())
				seg = SEG_ACQ;
			else
				throw new Exception(String.format("Unknown segment code \"%s\" found at Record %d.", segCode, (i + 1)));
			
			// If the record is a seed, update the segment and segment code
			if (seg.equalsIgnoreCase(SEG_SEED)) {
				seg = SEG_ACTIVE;
				segCode = "SEED";
			}
			
			/////
			
			// company names are in the name field
			if (seg.equalsIgnoreCase(SEG_ORGANIZATION)) {
				cmpny = nam1;
				nam1 = "";
				if (dearSal.replaceAll(" ", "").equalsIgnoreCase(cmpny.replaceAll(" ", "")))
					dearSal = "";
			}

			// 5050 doesn't include name1, combine prefix, fname and lname
			if(nam1.isEmpty())
				nam1 = String.format("%s %s %s", prefix, fnam, lnam).replaceAll("  ", " ").trim();

			String fullName = fnam + lnam;

			// Fix the salutations
			if(dearSal.toLowerCase().replaceAll("[^\\p{L}]", "").equals((fullName).toLowerCase().replaceAll("[^\\p{L}]", ""))) {
				dearSal = "";
				paraSal = "";
			}	

			if(dearSal.length() <= 1 // check for bad sal
					|| dearSal.length() >= 2 && dearSal.substring(1, 2).replaceAll("[\\p{L}']", "").length() > 0
					|| dearSal.length() == 2 && Validators.areCharactersSame(dearSal)
					|| dearSal.length() == 2 && !Validators.hasVowel(dearSal)
					) {

				if(!prefix.isEmpty() && !lnam.isEmpty()) { // change to prefix last if possible
					dearSal = prefix + " " + lnam;
					paraSal = dearSal;
				}else if(fnam.length() >= 2
						&& fnam.substring(1, 2).replaceAll("[\\p{L}']", "").length() == 0
						&& !Validators.areCharactersSame(fnam)
						&& Validators.hasVowel(fnam)
						) { // otherwise change to fname if possible
					dearSal = fnam;
					paraSal = fnam;
				} else {	// otherwise friend
					dearSal = (!seg.equalsIgnoreCase(SEG_ORGANIZATION)) ? "Friend" : "Friends";
					paraSal = "";
				}
			}
			
			

			/*// Update dear salutation and paragraph salutation for organizations
			if (seg.equalsIgnoreCase(SEG_ORGANIZATION) || (cmpny.length() > 0 && cmpny.equalsIgnoreCase(dearSal)) ) {
				if (!fullName.replaceAll(" ", "").equalsIgnoreCase(cmpny.replaceAll(" ", ""))) {
					if(!prefix.isEmpty() && !lnam.isEmpty()) { // change to prefix last if possible
						dearSal = prefix + " " + lnam;
						paraSal = dearSal;
					}else if(fnam.length() >= 2
							&& fnam.substring(1, 2).replaceAll("[\\p{L}']", "").length() == 0
							&& !Validators.areCharactersSame(fnam)
							&& Validators.hasVowel(fnam)
							) { // otherwise change to fname if possible
						dearSal = fnam;
						paraSal = fnam;
					} else {	// otherwise friend
						dearSal = "Friend";
						paraSal = "";
					}
				} else {
					dearSal = "Friends";
					paraSal = "";
				}
			}*/

			/////

			// Initialize the last gift amount
			BigDecimal lastGiftAmountAsBigDecimal = new BigDecimal("5.0");
			if(Validators.isNumber(lastGiftAmt))
				lastGiftAmountAsBigDecimal = new BigDecimal(lastGiftAmt);

			// initialize the standard gift amounts
			String dn1Amt = "";
			String dn2Amt = "";
			String dn3Amt = "";
			String oDnAmt = "$ ________";

			BigDecimal roundingAmount = new BigDecimal("5.0");
			BigDecimal newRoundedLastDonationAmount = lastGiftAmountAsBigDecimal.divide(roundingAmount, 2, RoundingMode.HALF_EVEN)
					.setScale(0, RoundingMode.CEILING).multiply(roundingAmount);

			if (lastGiftAmountAsBigDecimal.doubleValue() < 20.00) {
				dn1Amt = "20";
				dn2Amt = "30";
				dn3Amt = "40";
				oDnAmt = "$ ________";
			} else if (lastGiftAmountAsBigDecimal.doubleValue() < 500.00) {
				dn1Amt = String.valueOf(Math.ceil(newRoundedLastDonationAmount.intValue() / 5.0) * 5); // ROUNDING outputs, not floor or ceiling
				dn2Amt = String.valueOf(Math.ceil((newRoundedLastDonationAmount.intValue() * 1.2) / 5.0) * 5);
				dn3Amt = String.valueOf(Math.ceil((newRoundedLastDonationAmount.intValue() * 1.4) / 5.0) * 5);
				oDnAmt = "$ ________";
			} else {
				dn1Amt = "";
				dn2Amt = "";
				dn3Amt = "";
				oDnAmt = "Here's my special gift of $__________";
			}

			// Check to see if any of the values are the same and bump up by 5
			if(!dn1Amt.isEmpty() && dn1Amt.equalsIgnoreCase(dn2Amt))
				dn2Amt = String.valueOf(Double.valueOf(dn2Amt) + 5);
			if(!dn2Amt.isEmpty() && dn2Amt.equalsIgnoreCase(dn3Amt))
				dn3Amt = String.valueOf(Double.valueOf(dn3Amt) + 5);

			// Initialize the monthly donation amounts
			String mDn1Amt = "";
			String mDn2Amt = "";
			String mDn3Amt = "";
			String mODnAmt = "Other $ ________";

			if (lastGiftAmountAsBigDecimal.doubleValue() < 100) {
				mDn1Amt = "10";
				mDn2Amt = "15";
				mDn3Amt = "20";
			} else if (newRoundedLastDonationAmount.intValue() < 250) {
				mDn1Amt = "15";
				mDn2Amt = "25";
				mDn3Amt = "35";
			} else if (newRoundedLastDonationAmount.intValue() < 500) {
				mDn1Amt = "25";
				mDn2Amt = "50";
				mDn3Amt = "75";
			} else if (newRoundedLastDonationAmount.intValue() < 1000) {
				mDn1Amt = "50";
				mDn2Amt = "75";
				mDn3Amt = "100";
			}else {
				mDn1Amt = "100";
				mDn2Amt = "125";
				mDn3Amt = "150";
			}
			
			double roundedLMG = Math.round(lastGiftAmountAsBigDecimal.doubleValue());
			
			// Update the monthly gift array for the monthly segment
			if(seg.equalsIgnoreCase(SEG_MONTHLY) || seg.equalsIgnoreCase(SEG_NEW_MONTHLY)) {
				mDn1Amt =  String.valueOf(5 * (Math.round((double) roundedLMG / 5)));
				mDn2Amt = String.valueOf(5 * (Math.round((double) (roundedLMG + (roundedLMG * 0.25)) / 5)));
				mDn3Amt = String.valueOf(5 * (Math.round((double) (roundedLMG + (roundedLMG * 0.50)) / 5)));
			}
			
			// Update the monthly gift array for the monthly upgrade segment
			if(seg.equalsIgnoreCase(SEG_MONTHLY_UPGRADE)) {
				mDn1Amt = String.valueOf(5 * (Math.round((double) (roundedLMG + (roundedLMG * 0.20)) / 5)));
				
				if(Double.parseDouble(mDn1Amt) <= roundedLMG)
					mDn1Amt = String.valueOf(5 * (Math.round((double) (roundedLMG + 5) / 5)));
				
				mDn2Amt = String.valueOf(5 * (Math.round((double) (roundedLMG + (roundedLMG * 0.40)) / 5)));				
				mDn3Amt = String.valueOf(5 * (Math.round((double) (roundedLMG + (roundedLMG * 0.60)) / 5)));
			}
			
			// Update the gift arrays for the 5050 segment and Acquisition
			if(seg.equalsIgnoreCase(SEG_5050) || seg.equalsIgnoreCase(SEG_ACQ)) {
				dn1Amt = "20";
				dn2Amt = "30";
				dn3Amt = "40";
				
				mDn1Amt = "10";
				mDn2Amt = "15";
				mDn3Amt = "20";
			}
			
			// Fix any monthly amounts that may be the same
			if(!mDn1Amt.isEmpty() && (mDn1Amt.equalsIgnoreCase(mDn2Amt) || Double.parseDouble(mDn2Amt) <= Double.parseDouble(mDn1Amt)))
				mDn2Amt = String.valueOf(Double.parseDouble(mDn1Amt) + 5);
			if(!mDn2Amt.isEmpty() && (mDn2Amt.equalsIgnoreCase(mDn3Amt) || Double.parseDouble(mDn3Amt) <= Double.parseDouble(mDn2Amt)))
				mDn3Amt = String.valueOf(Double.parseDouble(mDn2Amt) + 5);
			
			// Update the monthly gift array for the reactivation segment
			// Doing this last because LMG-15%
			if(seg.equalsIgnoreCase(SEG_REACTIVATION)) {
				if(newRoundedLastDonationAmount.doubleValue() <= 10) {
					mDn1Amt = "5";
					mDn2Amt = "10";
					mDn3Amt = "15";
				} else {
					mDn1Amt = String.valueOf(newRoundedLastDonationAmount.doubleValue() - 5);
					mDn2Amt = String.valueOf(newRoundedLastDonationAmount.doubleValue());
					mDn3Amt = String.valueOf(newRoundedLastDonationAmount.doubleValue() + 5);
				}
			}
			
			// Finally set the priority
			String priority = lastGiftAmountAsBigDecimal.toString();
			
			if (segCode.equalsIgnoreCase("SEED"))
				priority = "99999999";
			else if (seg.equalsIgnoreCase(SEG_MONTHLY) || seg.equalsIgnoreCase(SEG_MONTHLY_UPGRADE) || seg.equalsIgnoreCase(SEG_NEW_MONTHLY))
				priority = "88888888";
			

			// Build the Record - use company for name2
			Record record = new Record.Builder(i, data[i], "", "", "")
					.setDearSal(dearSal)
					.setParaSal(paraSal)
					.setFstName(fnam)
					.setLstName(lnam)
					.setNam1(nam1)
					.setNam2(cmpny)
					.setSegCode(segCode)
					.setSeg(seg)
					.setLstDnAmt(lastGiftAmt)
					.setDn1Amt(dn1Amt)
					.setDn2Amt(dn2Amt)
					.setDn3Amt(dn3Amt)
					.setODnAmt(oDnAmt)
					.setMDn1Amt(mDn1Amt)
					.setMDn2Amt(mDn2Amt)
					.setMdDn3Amt(mDn3Amt)
					.setMODnAmt(mODnAmt)
					.setPriority(priority)
					.build();

			// add the record the recordList in userData
			userData.add(record);
		}


		// set the Header fields that we want to export
		userData.setDfHeaders(new String[] { 
				UserData.fieldName.DEAR_SALUTATION.getName(),
				UserData.fieldName.PARAGRAPH_SALUTATION.getName(),
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.NAME2.getName(),
				UserData.fieldName.SEGMENT.getName(),
				UserData.fieldName.SEGMENT_CODE.getName(),
				UserData.fieldName.LAST_DONATION_AMOUNT.getName(),
				UserData.fieldName.DONATION1_AMOUNT.getName(),
				UserData.fieldName.DONATION2_AMOUNT.getName(),
				UserData.fieldName.DONATION3_AMOUNT.getName(),
				UserData.fieldName.OPEN_DONATION_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_DONATION1_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_DONATION2_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_DONATION3_AMOUNT.getName(),
				UserData.fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName(),
				UserData.fieldName.PRIORITY.getName()
				});
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
