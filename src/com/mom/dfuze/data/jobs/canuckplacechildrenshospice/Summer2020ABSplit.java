/**
 * Project: Dfuze
 * File: Summer2020.java
 * Date: Jun 1, 2020
 * Time: 11:58:05 AM
 */
package com.mom.dfuze.data.jobs.canuckplacechildrenshospice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;

/**
 * Summer2020ABSplit impliments a RunBehavior for Canucks Place Childrens Hospice Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class Summer2020ABSplit implements RunCanuckPlaceChildrensHospiceBehavior {

  private String BEHAVIOR_NAME = "Summer2020 A/B Split";
  private String DESCRIPTION = "06/06/2020  -  T I P : Segment Code is codeline\n\n" + "S T E P S   P E R F O R M E D   I N   D F U Z E :\n\n"
          + "1)\nSplits gift and date arrays while ensuring they are of the same size. Uses 0.00 if no last gift was found.\n\n"
          + "2)\nCalculates the Mean and Standard Deviation from gift Amount Array.\n\n"
          + "3)\nConverts the epoch date from gift Date Array to determine last gift date and matches it to the corresponding gift amount.\n\n"
          + "4)\nCalculates the Group B gift array based on steps 2-3 and applies it to everyone.\n\n"
          + "5)\nCalculates the monthly gift arrays for all records.\n\n"
          + "6)\nString matches on segCode (codeline) to determine segment and name.\n\n"
          + "7)\nUpdates dearSal to \"Friend\" and paraSal to \"\" if no contact is found for Organization records.\n\n"
          + "8)\nSplits each segment into their own group and randomizes the order of records.\n\n"
          + "9)\nEven records are assigned to group A and odd records to B before merging segments back together.\n\n"
          + "10)\nUpdates group A records to have the standard gift array based on their last gift amount.";

  private String[] REQUIRED_FIELDS = { UserData.fieldName.ADDRESS1.getName(), UserData.fieldName.ADDRESS2.getName(),
          UserData.fieldName.POSTALCODE.getName(), UserData.fieldName.DEAR_SALUTATION.getName(), UserData.fieldName.FIRSTNAME.getName(),
          UserData.fieldName.LASTNAME.getName(), UserData.fieldName.NAME1.getName(), UserData.fieldName.COMPANY.getName(),
          UserData.fieldName.SEGMENT_CODE.getName(), UserData.fieldName.DONATION_AMOUNT_ARRAY.getName(),
          UserData.fieldName.DONATION_DATE_ARRAY.getName() };

  /*
   * (non-Javadoc)
   * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
   */
  @Override
  public void run(UserData userData) throws Exception {

    // The delimiter used to delimit the donation amount and donation date arrays
    String fieldDelimiter = "\\|";

    // Flag if delimiter seen, surely we should've found at least one right?
    boolean hasFoundDelimiter = false;

    // The epoch reference to convert date value from excel
    LocalDate EXCEL_EPOCH_REFERENCE = LocalDate.of(1899, Month.DECEMBER, 30);

    // The users' input data
    String[][] data = userData.getData();

    // Loop through the users' input data
    for (int i = 0; i < data.length; ++i) {

      // Get all the fields we can for now
      String add1 = data[i][userData.getAdd1Index()];
      String add2 = data[i][userData.getAdd2Index()];
      String pCode = data[i][userData.getPCodeIndex()];
      String segCode = data[i][userData.getSegCodeIndex()];
      String dearSal = data[i][userData.getDearSalIndex()];
      String paraSal = dearSal;
      String fnam = data[i][userData.getFstNameIndex()];
      String lnam = data[i][userData.getLstNameIndex()];
      String nam1 = data[i][userData.getNam1Index()];
      String cmpny = data[i][userData.getCmpnyIndex()];
      String dnAmtArr = data[i][userData.getDnAmtArrIndex()];
      String dnDatArr = data[i][userData.getDnDatArrIndex()];

      // Split the donation amounts and dates by delimiter into arrays
      String[] splitDnAmtArr = dnAmtArr.split(fieldDelimiter);
      String[] splitDnDatArr = dnDatArr.split(fieldDelimiter);

      // Ensure the Donation and Date arrays are correctly built
      if (splitDnAmtArr.length != splitDnDatArr.length)
        throw new Exception(
                String.format("The Donation Amount Array size of \"%d\" and Donation Date Array size of \"%d\" are not the same size at Record %d.",
                        splitDnAmtArr.length, splitDnDatArr.length, (i + 1)));

      // Arrays are now ensured to be correctly delimited
      if (!hasFoundDelimiter && (splitDnAmtArr.length > 1 || splitDnDatArr.length > 1))
        hasFoundDelimiter = true;

      // Make a list to hold the donations as Double
      List<BigDecimal> splitDnAmtArrAsBigDecimal = new ArrayList<>();
      BigDecimal lastGiftDateAsBigDecimal = new BigDecimal("0.0");
      BigDecimal lastGiftAmountAsBigDecimal = new BigDecimal("0.0");
      BigDecimal sumOfGifts = new BigDecimal("0.0");

      // Now we can safely loop through the Donation and Date arrays
      for (int j = 0; j < splitDnAmtArr.length; ++j) {
        if (splitDnAmtArr[j].length() == 0)
          break;

        // Try to parse out a decimal number from the string and save the value.
        BigDecimal currentDonationAmount;
        String currentDonationAmountAsInt = splitDnAmtArr[j].replaceAll("[^0-9\\.]", "");

        // Set current donation amount to 0 if one couldn't be parsed
        if (currentDonationAmountAsInt.length() == 0 || currentDonationAmountAsInt.equals("."))
          currentDonationAmountAsInt = "0.0";

        // save the parsed String decimal value as a BigDecimal for increased accuracy
        currentDonationAmount = new BigDecimal(currentDonationAmountAsInt);

        // Add the current donation to an array to hold all values
        splitDnAmtArrAsBigDecimal.add(currentDonationAmount);

        // Try to parse out a number holding the excel date value string and save the value
        BigDecimal currentDonationDate;
        String currentDonationDateAsInt = splitDnDatArr[j].replaceAll("[^0-9]", "");
        // Set current donation date to 0 if one couldn't be parsed
        if (currentDonationDateAsInt.length() == 0)
          currentDonationDateAsInt = "0.0";
        currentDonationDate = new BigDecimal(currentDonationDateAsInt);

        // If the current donation date is larger the previous, update the last gift and date amounts
        if (currentDonationDate.subtract(lastGiftDateAsBigDecimal).intValue() > 0) {
          lastGiftDateAsBigDecimal = currentDonationDate;
          lastGiftAmountAsBigDecimal = currentDonationAmount;
        }

        // Update the sum of our gifts
        sumOfGifts = sumOfGifts.add(currentDonationAmount);
      }

      // Extract the number of whole days, dropping the fraction.
      long days = lastGiftDateAsBigDecimal.longValue();
      LocalDate lastGiftDateAsLocalDate = EXCEL_EPOCH_REFERENCE.plusDays(days);
      String lastDnDate = lastGiftDateAsLocalDate.toString();

      // Initialize the mean gift amount
      BigDecimal meanGiftAmount = new BigDecimal("0.0");

      // Check if no gifts have been given
      if (lastGiftAmountAsBigDecimal.intValue() == 0) {
        meanGiftAmount = new BigDecimal("5.0");
        splitDnAmtArrAsBigDecimal.add(meanGiftAmount);
        sumOfGifts = meanGiftAmount;
      }

      // Update the mean gift amount: Sum / numOfGifts
      meanGiftAmount = sumOfGifts.divide(new BigDecimal(splitDnAmtArrAsBigDecimal.size()), 2, RoundingMode.CEILING);

      // Calculate the standard deviation
      BigDecimal standardDeviation = Common.calculateStandardDeviation(splitDnAmtArrAsBigDecimal);

      // The amount to round by
      BigDecimal roundingAmount = new BigDecimal("5.0");

      // Initialize the gift asks
      BigDecimal giftAsk1 = meanGiftAmount.divide(roundingAmount, 2, RoundingMode.HALF_EVEN).setScale(0, RoundingMode.CEILING)
              .multiply(roundingAmount);
      BigDecimal giftAsk2 = new BigDecimal("0.0");
      BigDecimal giftAsk3 = new BigDecimal("0.0");

      // If standard deviation is greater than zero, then round by 5 and add to each gift ask
      if (standardDeviation.intValue() > 0) {

        BigDecimal roundedStandardDeviation = standardDeviation.divide(roundingAmount, 2, RoundingMode.HALF_UP).setScale(0, RoundingMode.CEILING)
                .multiply(roundingAmount);
        giftAsk2 = giftAsk1.add(roundedStandardDeviation);
        giftAsk3 = giftAsk2.add(roundedStandardDeviation);
      } else {

        giftAsk2 = meanGiftAmount.multiply(new BigDecimal("1.25")).divide(roundingAmount, 2, RoundingMode.HALF_UP).setScale(0, RoundingMode.CEILING)
                .multiply(roundingAmount);
        giftAsk3 = meanGiftAmount.multiply(new BigDecimal("1.50")).divide(roundingAmount, 2, RoundingMode.HALF_UP).setScale(0, RoundingMode.CEILING)
                .multiply(roundingAmount);
      }

      // Increment giftAsk3 if it is the same as GiftAsk2 by the difference of GiftAsk1 and GiftAsk2
      if (giftAsk3.intValue() == giftAsk2.intValue())
        giftAsk3 = giftAsk3.add(BigDecimal.valueOf(giftAsk2.doubleValue() - giftAsk1.doubleValue()));

      // Initialize the monthly donation amounts
      String mDn1Amt = "";
      String mDn2Amt = "";
      String mDn3Amt = "";
      String mODnAmt = "Other $ ________";

      if (lastGiftAmountAsBigDecimal.doubleValue() < 100) {
        mDn1Amt = "10";
        mDn2Amt = "15";
        mDn3Amt = "20";
      } else if (Math.round(lastGiftAmountAsBigDecimal.intValue() / 5.0) * 5 < 250) {
        mDn1Amt = "15";
        mDn2Amt = "25";
        mDn3Amt = "35";
      } else {
        mDn1Amt = "25";
        mDn2Amt = "50";
        mDn3Amt = "75";
      }

      // System.out.println(giftAsk1.toPlainString() + "\n" + giftAsk2.toPlainString() + "\n" + giftAsk3.toPlainString() + "\n");

      // Compile the segment code patterns to match
      Pattern activePattern = Pattern.compile("[a][c][t][i][n][d]");
      Pattern currentPattern = Pattern.compile("[c][u][r][i][n][d]");
      Pattern lapsedPattern = Pattern.compile("[l][p][s][i][n][d]");
      Pattern leaderPattern = Pattern.compile("[l][d][r]");
      Pattern orgPattern = Pattern.compile("[o][r][g]");
      Pattern monthlyPattern = Pattern.compile("[m][o][n]");
      Pattern tributePattern = Pattern.compile("[t][r][i][b]");

      String seg = "";
      String loweredSegCode = segCode.toLowerCase();

      // Check if our patterns match, if not, throw an error
      if (activePattern.matcher(loweredSegCode).find())
        seg = "Active";
      else if (currentPattern.matcher(loweredSegCode).find())
        seg = "Current";
      else if (lapsedPattern.matcher(loweredSegCode).find())
        seg = "Lapsed";
      else if (leaderPattern.matcher(loweredSegCode).find())
        seg = "Leader";
      else if (orgPattern.matcher(loweredSegCode).find())
        seg = "Organization";
      else if (monthlyPattern.matcher(loweredSegCode).find())
        seg = "Monthly";
      else if (tributePattern.matcher(loweredSegCode).find())
        seg = "Tribute";
      else if (loweredSegCode.length() == 0)
        seg = "Seed";
      else
        throw new Exception(String.format("Unknown segment code \"%s\" found at Record %d.", segCode, (i + 1)));

      String fullName = fnam + lnam;

      // Update dear salutation and paragraph salutation for organizations
      if (seg.equalsIgnoreCase("organization")) {
        if (fullName.trim().length() == 0) {
          dearSal = "Friend";
          paraSal = "";
        }

        if (nam1.trim().equalsIgnoreCase(cmpny.trim()))
          nam1 = "";
      }

      // If the record is a seed, update the last donation date to be blank and mean to 0
      if (seg.equalsIgnoreCase("seed")) {
        lastDnDate = "";
        meanGiftAmount = new BigDecimal("0.0");
      }

      // Build the Record - use company for name2
      Record record = new Record.Builder(i, data[i], add1, add2, pCode).setDearSal(dearSal).setParaSal(paraSal).setFstName(fnam).setLstName(lnam)
              .setNam1(nam1).setNam2(cmpny).setSegCode(segCode).setSeg(seg)
              .setLstDnAmt(lastGiftAmountAsBigDecimal.setScale(2, RoundingMode.HALF_EVEN).toPlainString()).setLstDnDat(lastDnDate)
              .setDn1Amt(String.valueOf(giftAsk1.intValue())).setMeanAmt(meanGiftAmount.toPlainString()).setSDevAmt(standardDeviation.toPlainString())
              .setDn2Amt(String.valueOf(giftAsk2.intValue())).setDn3Amt(String.valueOf(giftAsk3.intValue())).setODnAmt("$ ________")
              .setMDn1Amt(mDn1Amt).setMDn2Amt(mDn2Amt).setMdDn3Amt(mDn3Amt).setMODnAmt(mODnAmt).build();

      // add the record the recordList in userData
      userData.add(record);
    }

    // if we haven't seen a donation array
    if (!hasFoundDelimiter)
      throw new Exception(
              String.format("Donations were parsed but no arrays were found, please ensure the arrays are delmited by \"%s\"", fieldDelimiter));

    // Sort on the Donors' segment code
    Collections.sort(userData.getRecordList(), new RecordSorters.CompareByFieldAsc(UserData.fieldName.SEGMENT.getName()));

    // Initialize variable to hold last segment value
    String lastSegment = userData.getRecordList().get(0).getSeg();

    // List to hold our list of segments
    List<List<Record>> segments = new ArrayList<>();

    // List to hold a segment
    List<Record> innerSegment = new ArrayList<>();

    // Loop through the data
    for (int i = 0; i < userData.getRecordList().size(); ++i) {
      innerSegment.add(userData.getRecordList().get(i));

      // if our segments don't match our we've reached the end of the data, add our segment list to the master segment list
      if (!userData.getRecordList().get(i).getSeg().equals(lastSegment) || i == userData.getRecordList().size() - 1) {
        segments.add(innerSegment);
        innerSegment = new ArrayList<>();
      }

      // Update the last segment found
      lastSegment = userData.getRecordList().get(i).getSeg();
    }

    // loop through the master segment list
    for (int i = 0; i < segments.size(); ++i) {

      // Randomize each segment
      Collections.shuffle(segments.get(i));

      // loop through the inner segment list
      for (int j = 0; j < segments.get(i).size(); ++j) {
        Record currentRecord = segments.get(i).get(j);

        // if the record is even, set to A otherwise B
        if (j % 2 == 0)
          currentRecord.setABGroup("C");
        else
          currentRecord.setABGroup("T");

        // loop through the original record list
        for (int k = 0; k < userData.getRecordList().size(); ++k) {
          Record dbRecord = userData.getRecordList().get(k);

          // if the dfIds match
          if (currentRecord.getDfId() == dbRecord.getDfId()) {
            dbRecord.setABGroup(currentRecord.getABGroup());

            // if the record is part of group A, update gift arrays to standard formula
            if (dbRecord.getABGroup().equalsIgnoreCase("a")) {
              BigDecimal roundingAmount = new BigDecimal("5.0");
              BigDecimal newLastDonationAmount = new BigDecimal(currentRecord.getLstDnAmt());
              BigDecimal newRoundedLastDonationAmount = newLastDonationAmount.divide(roundingAmount, 2, RoundingMode.HALF_EVEN)
                      .setScale(0, RoundingMode.CEILING).multiply(roundingAmount);

              if (newLastDonationAmount.doubleValue() < 20.00) {
                dbRecord.setDn1Amt("20");
                dbRecord.setDn2Amt("30");
                dbRecord.setDn3Amt("40");
                dbRecord.setODnAmt("$ ________");
              } else if (newLastDonationAmount.doubleValue() < 500.00) {
                dbRecord.setDn1Amt(String.valueOf(Math.round(newRoundedLastDonationAmount.intValue() / 5.0) * 5));
                dbRecord.setDn2Amt(String.valueOf(Math.round((newRoundedLastDonationAmount.intValue() * 1.2) / 5.0) * 5));
                dbRecord.setDn3Amt(String.valueOf(Math.round((newRoundedLastDonationAmount.intValue() * 1.4) / 5.0) * 5));
                dbRecord.setODnAmt("$ ________");
              } else {
                dbRecord.setDn1Amt("");
                dbRecord.setDn2Amt("");
                dbRecord.setDn3Amt("");
                dbRecord.setODnAmt("Here's my special gift of $__________");
              }

            }
          }
        }
      }
    }

    // Sort the data back to its original order
    Collections.sort(userData.getRecordList(), new RecordSorters.CompareByDfId());

    // set the Header fields that we want to export
    userData.setDfHeaders(new String[] { UserData.fieldName.DEAR_SALUTATION.getName(), UserData.fieldName.PARAGRAPH_SALUTATION.getName(),
            UserData.fieldName.NAME1.getName(), UserData.fieldName.NAME2.getName(), UserData.fieldName.SEGMENT.getName(),
            UserData.fieldName.AB_GROUP.getName(), UserData.fieldName.LAST_DONATION_AMOUNT.getName(), UserData.fieldName.LAST_DONATION_DATE.getName(),
            UserData.fieldName.MEAN_AMOUNT.getName(), UserData.fieldName.STANDARD_DEVIATION_AMOUNT.getName(),
            UserData.fieldName.DONATION1_AMOUNT.getName(), UserData.fieldName.DONATION2_AMOUNT.getName(),
            UserData.fieldName.DONATION3_AMOUNT.getName(), UserData.fieldName.OPEN_DONATION_AMOUNT.getName(),
            UserData.fieldName.MONTHLY_DONATION1_AMOUNT.getName(), UserData.fieldName.MONTHLY_DONATION2_AMOUNT.getName(),
            UserData.fieldName.MONTHLY_DONATION3_AMOUNT.getName(), UserData.fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName() });

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
		return false;
	}

}
