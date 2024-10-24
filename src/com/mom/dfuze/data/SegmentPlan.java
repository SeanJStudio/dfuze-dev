/**
 * Project: Dfuze
 * File: SegmentCriteria.java
 * Date: Jun 17, 2020
 * Time: 8:52:06 PM
 */
package com.mom.dfuze.data;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mom.dfuze.data.util.Validators;

/**
 * SegmentPlan Class to provide a bulk set of segmentation instructions to Dfuze
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class SegmentPlan {
  private int segmentID;
  private double priority; // file priority to set
  private String segmentCode; // segment code to set
  private String segmentName; // segment name to set
  private boolean isIndividual; // should individuals be included? Y or N
  private boolean isBusiness; // should business' be included? Y or N
  private String giftDateUsed; // which gift date are we using, last, first? F or L
  private String fromGiftDate; // what is the starting point of gift dates to include?
  private String toGiftDate; // what is the last gift date to include?
  private int fromGiftDateMonths; // from how many months from today and the last gift date should be considered?
  private int toGiftDateMonths; // to how many months from today and the last gift date should be considered?
  private String giftUsed; // which gift are we using, last, first? F or L
  private String fromGift; // what is the lowest gift amount to consider?
  private String toGift; // what is the highest gift amount to consider?
  private int fromNumOfGifts; // what is the lowest amount of gifts to consider?
  private int toNumOfGifts; // what is the highest amount of gifts to consider?
  private String letterVersion; // which letter version does this criteria belong to?
  private String packageVersion; // which package version does this criteria belong to?
  private String replyVersion; // which reply version does this criteria belong to?
  private String staticAsk1;
  private String staticAsk2;
  private String staticAsk3;
  private String askOpen;
  private String staticMonthlyAsk1;
  private String staticMonthlyAsk2;
  private String staticMonthlyAsk3;
  private String monthlyAskOpen;
  private String segmentPlanFilter1;
  private String segmentPlanFilter2;
  private boolean isSuppressed;


  // Harvey McKinnon Associates specific
  private boolean isStandardAsk; // should the standard ask array formula be used? Y or N
  private boolean isActiveAsk; // should the active ask array formula be used? Y or N
  private boolean isStandardMonthlyAsk; // should the standard monthly ask array formula be used? Y or N
  private boolean isActiveMonthlyAsk; // should the active monthly ask array formula be used? Y or N
  private boolean isSpecial;
  private boolean isHolidaySpecial;
  private boolean isMonthlySpecial;

  // One and All specific
  private String oneAndAllCCMSegmentDescription;
  private String campaignID;
  private int split;

  public final static Pattern OAA_RECENCY_PATTERN = Pattern.compile("\\d+\\+?\\s?-?\\s?\\d*\\s?[m][o]?[n]?[t]?[h]?[s]?", Pattern.CASE_INSENSITIVE);
  public final static Pattern OAA_FREQUENCY_PATTERN = Pattern.compile("\\d+\\+?\\s?-?\\s?\\d*\\s?[g][i]?[f]?[t]?[s]?", Pattern.CASE_INSENSITIVE);
  public final static Pattern OAA_MONETARY_PATTERN = Pattern.compile("\\$\\d+,?\\d*\\+?\\.?\\d*\\+?\\s?-?\\s?\\$?\\d*,?\\d*\\.?\\d*",
          Pattern.CASE_INSENSITIVE);
  public final static Pattern OAA_SPLIT_PATTERN = Pattern.compile("\\d+\\s?\\%", Pattern.CASE_INSENSITIVE);

  private String[] fieldsToSearch;
  private String[] valuesToSearch;
  private String valueToExclude;
  private boolean isValueToSeachExact;
  private boolean isValueToSeachPattern;

  /**
   * fieldName enum to hold all the fieldNames
   */
  public enum fieldName {

    PRIORITY("PRIORITY"), SEGMENT_CODE("SEGMENT_CODE"), SEGMENT_NAME("SEGMENT_NAME"), IS_INDIVIDUAL("IS_INDIVIDUAL"), IS_BUSINESS(
            "IS_BUSINESS"), GIFT_DATE_USED("GIFT_DATE_USED"), FROM_GIFT_DATE("FROM_GIFT_DATE"), TO_GIFT_DATE("TO_GIFT_DATE"), FROM_GIFT_DATE_MONTHS(
                    "FROM_GIFT_DATE_MONTHS"), TO_GIFT_DATE_MONTHS("TO_GIFT_DATE_MONTHS"), GIFT_USED("GIFT_USED"), FROM_GIFT("FROM_GIFT"), TO_GIFT(
                            "TO_GIFT"), FROM_NUM_OF_GIFTS("FROM_NUM_OF_GIFTS"), TO_NUM_OF_GIFTS("TO_NUM_OF_GIFTS"), LETTER_VERSION(
                                    "LETTER_VERSION"), PACKAGE_VERSION("PACKAGE_VERSION"), REPLY_VERSION("REPLY_VERSION"), IS_STANDARD_ASK(
                                            "IS_STANDARD_ASK"), IS_ACTIVE_ASK("IS_ACTIVE_ASK"), STATIC_ASK_1("STATIC_ASK_1"), STATIC_ASK_2(
                                                    "STATIC_ASK_2"), STATIC_ASK_3("STATIC_ASK_3"), ASK_OPEN("ASK_OPEN"), IS_STANDARD_MONTHLY_ASK(
                                                            "IS_STANDARD_MONTHLY_ASK"), IS_ACTIVE_MONTHLY_ASK(
                                                                    "IS_ACTIVE_MONTHLY_ASK"), STATIC_MONTHLY_ASK_1(
                                                                            "STATIC_MONTHLY_ASK_1"), STATIC_MONTHLY_ASK_2(
                                                                                    "STATIC_MONTHLY_ASK_2"), STATIC_MONTHLY_ASK_3(
                                                                                            "STATIC_MONTHLY_ASK_3"), MONTHLY_ASK_OPEN(
                                                                                                    "MONTHLY_ASK_OPEN"), SEGMENT_PLAN_FILTER_1(
                                                                                                            "SEGMENT_FILTER_1"), SEGMENT_PLAN_FILTER_2(
                                                                                                                    "SEGMENT_FILTER_2"), IS_SUPPRESSED(
                                                                                                                            "IS_SUPPRESSED"), ONE_AND_ALL_CCM_SEGMENT_DESCRIPTION(
                                                                                                                                    "ONE_AND_ALL_CCM_SEGMENT_DESCRIPTION"), CAMPAIGN_ID(
                                                                                                                                            "CAMPAIGN_ID"), SPLIT(
                                                                                                                                                    "SPLIT");

    String name;

    private fieldName(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

  };

  /**
   * allSegmentPlanHeaders holds all of the SegmentPlan header values as a string
   */
  public static final String[] allSegmentPlanHeaders =

          { fieldName.PRIORITY.getName(), fieldName.SEGMENT_CODE.getName(), fieldName.SEGMENT_NAME.getName(), fieldName.IS_INDIVIDUAL.getName(),
                  fieldName.IS_BUSINESS.getName(), fieldName.GIFT_DATE_USED.getName(), fieldName.FROM_GIFT_DATE.getName(),
                  fieldName.TO_GIFT_DATE.getName(), fieldName.FROM_GIFT_DATE_MONTHS.getName(), fieldName.TO_GIFT_DATE_MONTHS.getName(),
                  fieldName.GIFT_USED.getName(), fieldName.FROM_GIFT.getName(), fieldName.TO_GIFT.getName(), fieldName.FROM_NUM_OF_GIFTS.getName(),
                  fieldName.TO_NUM_OF_GIFTS.getName(), fieldName.LETTER_VERSION.getName(), fieldName.PACKAGE_VERSION.getName(),
                  fieldName.REPLY_VERSION.getName(), fieldName.IS_STANDARD_ASK.getName(), fieldName.IS_ACTIVE_ASK.getName(),
                  fieldName.STATIC_ASK_1.getName(), fieldName.STATIC_ASK_2.getName(), fieldName.STATIC_ASK_3.getName(), fieldName.ASK_OPEN.getName(),
                  fieldName.IS_STANDARD_MONTHLY_ASK.getName(), fieldName.IS_ACTIVE_MONTHLY_ASK.getName(), fieldName.STATIC_MONTHLY_ASK_1.getName(),
                  fieldName.STATIC_MONTHLY_ASK_2.getName(), fieldName.STATIC_MONTHLY_ASK_3.getName(), fieldName.MONTHLY_ASK_OPEN.getName(),
                  fieldName.SEGMENT_PLAN_FILTER_1.getName(), fieldName.SEGMENT_PLAN_FILTER_2.getName(), fieldName.IS_SUPPRESSED.getName(),
                  fieldName.ONE_AND_ALL_CCM_SEGMENT_DESCRIPTION.getName(), fieldName.CAMPAIGN_ID.getName(), fieldName.SPLIT.getName() };

  /**
   * Constructor for Objects of Class SegmentPlan
   * 
   * @param builder
   *          the builder to set data from
   */
  public SegmentPlan() {
  }

  /*
   * Getters
   */
  public int getSegmentID() {
    return segmentID;
  }

  public double getPriority() {
    return priority;
  }

  public String getSegmentCode() {
    return segmentCode;
  }

  public String getSegmentName() {
    return segmentName;
  }

  public boolean getIsIndividual() {
    return isIndividual;
  }

  public boolean getIsBusiness() {
    return isBusiness;
  }

  public String getGiftDateUsed() {
    return giftDateUsed;
  }

  public String getFromGiftDate() {
    return fromGiftDate;
  }

  public String getToGiftDate() {
    return toGiftDate;
  }

  public int getFromGiftDateMonths() {
    return fromGiftDateMonths;
  }

  public int getToGiftDateMonths() {
    return toGiftDateMonths;
  }

  public String getGiftUsed() {
    return giftUsed;
  }

  public String getFromGift() {
    return fromGift;
  }

  public String getToGift() {
    return toGift;
  }

  public int getFromNumOfGifts() {
    return fromNumOfGifts;
  }

  public int getToNumOfGifts() {
    return toNumOfGifts;
  }

  public String getLetterVersion() {
    return letterVersion;
  }

  public String getPackageVersion() {
    return packageVersion;
  }

  public String getReplyVersion() {
    return replyVersion;
  }

  public String getStaticAsk1() {
    return staticAsk1;
  }

  public String getStaticAsk2() {
    return staticAsk2;
  }

  public String getStaticAsk3() {
    return staticAsk3;
  }

  public String getAskOpen() {
    return askOpen;
  }

  public String getStaticMonthlyAsk1() {
    return staticMonthlyAsk1;
  }

  public String getStaticMonthlyAsk2() {
    return staticMonthlyAsk2;
  }

  public String getStaticMonthlyAsk3() {
    return staticMonthlyAsk3;
  }

  public String getMonthlyAskOpen() {
    return monthlyAskOpen;
  }

  public String getSegmentPlanFilter1() {
    return segmentPlanFilter1;
  }

  public String getSegmentPlanFilter2() {
    return segmentPlanFilter2;
  }

  // Harvey McKinnon Associates specific
  public boolean getIsStandardMonthlyAsk() {
    return isStandardMonthlyAsk;
  }

  public boolean getIsActiveMonthlyAsk() {
    return isActiveMonthlyAsk;
  }

  public boolean getIsSpecial() {
    return isSpecial;
  }

  public boolean getIsHolidaySpecial() {
    return isHolidaySpecial;
  }

  public boolean getIsMonthlySpecial() {
    return isMonthlySpecial;
  }

  public boolean getIsStandardAsk() {
    return isStandardAsk;
  }

  public boolean getIsActiveAsk() {
    return isActiveAsk;
  }

  public boolean getIsSuppressed() {
    return isSuppressed;
  }

  // One and All specific
  public String getOneAndAllCCMSegmentDescription() {
    return oneAndAllCCMSegmentDescription;
  }

  public String getCampignID() {
    return campaignID;
  }

  public int getSplit() {
    return split;
  }

  public String[] getFieldsToSearch() {
    return fieldsToSearch;
  }
  
  public String[] getValuesToSearch() {
	    return valuesToSearch;
  }
  
  public String getValueToExclude() {
	return valueToExclude;
  }

public void setValueToExclude(String valueToExclude) {
	if(valueToExclude == null)
		valueToExclude = "";
	
	this.valueToExclude = valueToExclude;
}

public boolean getIsValueToSeachExact() {
	  return isValueToSeachExact;
  }
  
  public boolean getIsValueToSeachPattern() {
	  return isValueToSeachPattern;
  }


  /*
   * Setters
   */

  public void setSegmentID(int segmentID) {
    this.segmentID = segmentID;
  }

  public void setPriority(String priority) {
    if (Validators.isNumber(priority))
      this.priority = Double.parseDouble(priority);
  }

  public void setSegmentCode(String segmentCode) {
    this.segmentCode = segmentCode;
  }

  public void setSegmentName(String segmentName) {
    this.segmentName = segmentName;
  }

  public void setIsIndividual(String isIndividual) throws Exception {
    if (isIndividual.equalsIgnoreCase("y"))
      this.isIndividual = true;
    else if (isIndividual.equalsIgnoreCase("n"))
      this.isBusiness = false;
    else
      throw new Exception(String.format("Invalid %s format, should be Y or N\nY = include individuals\nN = don't include individuals.",
              fieldName.IS_INDIVIDUAL.getName()));
  }

  public void setIsBusiness(String isBusiness) throws Exception {
    if (isBusiness.equalsIgnoreCase("y"))
      this.isBusiness = true;
    else if (isBusiness.equalsIgnoreCase("n"))
      this.isBusiness = false;
    else
      throw new Exception(String.format("Invalid %s format, should be Y or N\nY = include business'\nN = don't include business'",
              fieldName.IS_BUSINESS.getName()));
  }

  public void setGiftDateUsed(String giftDateUsed) throws Exception {
    if (giftDateUsed.equalsIgnoreCase("f") || giftDateUsed.equalsIgnoreCase("l"))
      this.giftDateUsed = giftDateUsed;
    else
      throw new Exception(String.format("Invalid %s format, should be F or L\nF = use first gift date.\nL = use last gift date",
              fieldName.GIFT_DATE_USED.getName()));
  }

  public void setFromGiftDate(String fromGiftDate) throws Exception {

    if (!Validators.isValidMDYYYYDate(fromGiftDate))
      throw new Exception(String.format("Invalid %s format, should be M/D/YYYY\nThe first date of used gift date to include",
              fieldName.FROM_GIFT_DATE.getName()));

    this.fromGiftDate = fromGiftDate;
  }

  public void setToGiftDate(String toGiftDate) throws Exception {
    if (!Validators.isValidMDYYYYDate(toGiftDate))
      throw new Exception(
              String.format("Invalid %s format, should be M/D/YYYY\nThe last date of used gift date to include", fieldName.TO_GIFT_DATE.getName()));

    this.toGiftDate = toGiftDate;
  }

  public void setFromGiftDateMonths(String fromGiftDateMonths) throws Exception {

    try {
      Integer.parseInt(fromGiftDateMonths);
    } catch (Exception e) {
      throw new Exception(
              String.format("Invalid %s format, should be a whole number\nValue: %s", fieldName.FROM_GIFT_DATE_MONTHS.getName(), fromGiftDateMonths));
    }

    int num = Integer.parseInt(fromGiftDateMonths);

    if (num < 0)
      throw new Exception(String.format("Invalid %s format, number should be positive\nValue: %s", fieldName.FROM_GIFT_DATE_MONTHS.getName(),
              fromGiftDateMonths));

    this.fromGiftDateMonths = num;
  }

  public void setToGiftDateMonths(String toGiftDateMonths) throws Exception {
    try {
      Integer.parseInt(toGiftDateMonths);
    } catch (Exception e) {
      throw new Exception(
              String.format("Invalid %s format, should be a whole number\nValue: %s", fieldName.FROM_GIFT_DATE_MONTHS.getName(), toGiftDateMonths));
    }

    int num = Integer.parseInt(toGiftDateMonths);

    if (num < 0)
      throw new Exception(
              String.format("Invalid %s format, number should be positive\nValue: %s", fieldName.FROM_GIFT_DATE_MONTHS.getName(), toGiftDateMonths));

    this.toGiftDateMonths = num;
  }

  public void setGiftUsed(String giftUsed) throws Exception {
    if (giftUsed.equalsIgnoreCase("f") || giftUsed.equalsIgnoreCase("l"))
      this.giftUsed = giftUsed;
    else
      throw new Exception(String.format("Invalid %s format, should be F or L\nF = use first gift\nL = use last gift", fieldName.GIFT_USED.getName()));
  }

  public void setFromGift(String fromGift) throws Exception {

    if (!Validators.isNumber(fromGift))
      throw new Exception(String.format("Invalid %s format, should be blank or a number\nThe lowest gift to include", fieldName.FROM_GIFT.getName()));

    this.fromGift = fromGift;
  }

  public void setToGift(String toGift) throws Exception {

    if (!Validators.isNumber(toGift))
      throw new Exception(String.format("Invalid %s format, should be blank or a number\nThe largest gift to include", fieldName.TO_GIFT));

    this.toGift = toGift;
  }

  public void setFromNumOfGifts(String fromNumOfGifts) throws Exception {
    try {
      Integer.parseInt(fromNumOfGifts);
    } catch (Exception e) {
      throw new Exception(String.format("Invalid %s format, should be a whole number", fieldName.FROM_NUM_OF_GIFTS.getName()));
    }

    int num = Integer.parseInt(fromNumOfGifts);

    if (num < 0)
      throw new Exception(String.format("Invalid %s format, number should be positive", fieldName.FROM_NUM_OF_GIFTS.getName()));

    this.fromNumOfGifts = num;
  }

  public void setToNumOfGifts(String toNumOfGifts) throws Exception {
    try {
      Integer.parseInt(toNumOfGifts);
    } catch (Exception e) {
      throw new Exception(String.format("Invalid %s format, should be a whole number", fieldName.TO_NUM_OF_GIFTS.getName()));
    }

    int num = Integer.parseInt(toNumOfGifts);

    if (num < 0)
      throw new Exception(String.format("Invalid %s format, number should be positive", fieldName.TO_NUM_OF_GIFTS.getName()));

    this.toNumOfGifts = num;
  }

  public void setLetterVersion(String letterVersion) {
    this.letterVersion = letterVersion;
  }

  public void setPackageVersion(String packageVersion) {
    this.packageVersion = packageVersion;
  }

  public void setReplyVersion(String replyVersion) {
    this.replyVersion = replyVersion;
  }

  public void setStaticAsk1(String staticAsk1) throws Exception {
    if (!Validators.isNumber(staticAsk1) && !staticAsk1.isEmpty())
      throw new Exception(String.format("Invalid %s format, should be blank or a number\nThe value of %s to use", fieldName.STATIC_ASK_1.getName(),
              fieldName.STATIC_ASK_1.getName()));

    this.staticAsk1 = staticAsk1;
  }

  public void setStaticAsk2(String staticAsk2) throws Exception {
    if (!Validators.isNumber(staticAsk2) && !staticAsk2.isEmpty())
      throw new Exception(String.format("Invalid %s format, should be blank or a number\nThe value of %s to use", fieldName.STATIC_ASK_2.getName(),
              fieldName.STATIC_ASK_2.getName()));

    this.staticAsk2 = staticAsk2;
  }

  public void setStaticAsk3(String staticAsk3) throws Exception {
    if (!Validators.isNumber(staticAsk3) && !staticAsk3.isEmpty())
      throw new Exception(String.format("Invalid %s format, should be blank or a number\nThe value of %s to use", fieldName.STATIC_ASK_3.getName(),
              fieldName.STATIC_ASK_3.getName()));

    this.staticAsk3 = staticAsk3;
  }

  public void setAskOpen(String askOpen) {
    this.askOpen = askOpen;
  }

  public void setStaticMonthlyAsk1(String staticMonthlyAsk1) throws Exception {
    if (!Validators.isNumber(staticMonthlyAsk1) && !staticMonthlyAsk1.isEmpty())
      throw new Exception(String.format("Invalid %s format, should be blank or a number\nThe value of %s to use",
              fieldName.STATIC_MONTHLY_ASK_1.getName(), fieldName.STATIC_MONTHLY_ASK_1.getName()));

    this.staticMonthlyAsk1 = staticMonthlyAsk1;
  }

  public void setStaticMonthlyAsk2(String staticMonthlyAsk2) throws Exception {
    if (!Validators.isNumber(staticMonthlyAsk2) && !staticMonthlyAsk2.isEmpty())
      throw new Exception(String.format("Invalid %s format, should be blank or a number\nThe value of %s to use",
              fieldName.STATIC_MONTHLY_ASK_2.getName(), fieldName.STATIC_MONTHLY_ASK_2.getName()));

    this.staticMonthlyAsk2 = staticMonthlyAsk2;
  }

  public void setStaticMonthlyAsk3(String staticMonthlyAsk3) throws Exception {
    if (!Validators.isNumber(staticMonthlyAsk3) && !staticMonthlyAsk3.isEmpty())
      throw new Exception(String.format("Invalid %s format, should be blank or a number\nThe value of %s to use",
              fieldName.STATIC_MONTHLY_ASK_3.getName(), fieldName.STATIC_MONTHLY_ASK_3.getName()));

    this.staticMonthlyAsk3 = staticMonthlyAsk3;
  }

  public void setMonthlyAskOpen(String monthlyAskOpen) {
    this.monthlyAskOpen = monthlyAskOpen;
  }

  public void setSegmentPlanFilter1(String segmentPlanFilter1) {
    this.segmentPlanFilter1 = segmentPlanFilter1;
  }

  public void setSegmentPlanFilter2(String segmentPlanFilter2) {
    this.segmentPlanFilter2 = segmentPlanFilter2;
  }

  // Harvey Mckinnon Associates specific
  public void setIsStandardAsk(String isStandardAsk) throws Exception {
    if (isStandardAsk.equalsIgnoreCase("y"))
      this.isStandardAsk = true;
    else if (isStandardAsk.equalsIgnoreCase("n"))
      this.isStandardAsk = false;
    else
      throw new Exception(
              String.format("Invalid %s format, should be Y or N\nY = use the standard ask array formula", fieldName.IS_STANDARD_ASK.getName()));
  }

  public void setIsActiveAsk(String isActiveAsk) throws Exception {
    if (isActiveAsk.equalsIgnoreCase("y"))
      this.isActiveAsk = true;
    else if (isActiveAsk.equalsIgnoreCase("n"))
      this.isActiveAsk = false;
    else
      throw new Exception(
              String.format("Invalid %s format, should be Y or N\nY = use the active ask array formula", fieldName.IS_ACTIVE_ASK.getName()));
  }

  public void setIsStandardMonthlyAsk(String isStandardMonthlyAsk) throws Exception {
    if (isStandardMonthlyAsk.equalsIgnoreCase("y"))
      this.isStandardMonthlyAsk = true;
    else if (isStandardMonthlyAsk.equalsIgnoreCase("n"))
      this.isStandardMonthlyAsk = false;
    else
      throw new Exception(String.format("Invalid %s format, should be Y or N\nY = use the standard monthly ask array formula",
              fieldName.IS_STANDARD_MONTHLY_ASK.getName()));
  }

  public void setIsActiveMonthlyAsk(String isActiveMonthlyAsk) throws Exception {
    if (isActiveMonthlyAsk.equalsIgnoreCase("y"))
      this.isActiveMonthlyAsk = true;
    else if (isActiveMonthlyAsk.equalsIgnoreCase("n"))
      this.isActiveMonthlyAsk = false;
    else
      throw new Exception(String.format("Invalid %s format, should be Y or N\nY = use the active monthly ask array formula",
              fieldName.IS_ACTIVE_MONTHLY_ASK.getName()));
  }

  public void setIsSpecial(boolean isSpecial) {
    this.isSpecial = isSpecial;
  }

  public void setHolidaySpecial(boolean isHolidaySpecial) {
    this.isHolidaySpecial = isHolidaySpecial;
  }

  public void setMonthlySpecial(boolean isMonthlySpecial) {
    this.isMonthlySpecial = isMonthlySpecial;
  }

  public void setIsSuppressed(String isSuppressed) throws Exception {
    if (isSuppressed.equalsIgnoreCase("y"))
      this.isSuppressed = true;
    else if (isSuppressed.equalsIgnoreCase("n"))
      this.isSuppressed = false;
    else
      throw new Exception(String.format("Invalid %s format, should be Y or N\nY = Records matching this criteria should be suppressed.",
              fieldName.IS_SUPPRESSED.getName()));
  }

  public void setOneAndAllCCMSegmentDescription(String oneAndAllCCMSegmentDescription) throws Exception {
    String description = oneAndAllCCMSegmentDescription.toLowerCase().replaceAll("[a][l][l][ ][m][a][i][l][i][n][g][s]", "")
            .replaceAll("[f][r][e][n][c][h][ ][d][o][n][o][r][s]", "")
            .replaceAll("[m][a][j][o][r][ ][d][o][n][o][r][s]", "");

    this.oneAndAllCCMSegmentDescription = oneAndAllCCMSegmentDescription;
    Matcher matcherRecency = OAA_RECENCY_PATTERN.matcher(oneAndAllCCMSegmentDescription);
    Matcher matcherFrequency = OAA_FREQUENCY_PATTERN.matcher(oneAndAllCCMSegmentDescription);
    Matcher matcherMonetary = OAA_MONETARY_PATTERN.matcher(oneAndAllCCMSegmentDescription);
    Matcher matcherSplit = OAA_SPLIT_PATTERN.matcher(oneAndAllCCMSegmentDescription);

    String recency = null, frequency = null, monetary = null;

    if (matcherRecency.find()) {
      description = description.replaceAll(Pattern.quote(matcherRecency.group(0)), "");
      recency = matcherRecency.group(0).replaceAll("[^\\d-\\+]", "");
    }

    if (matcherFrequency.find()) {
      description = description.replaceAll(Pattern.quote(matcherFrequency.group(0)), "");
      frequency = matcherFrequency.group(0).replaceAll("[^\\d\\+]", "");
    }

    if (matcherMonetary.find()) {
      description = description.replaceAll(Pattern.quote(matcherMonetary.group(0)), "");
      monetary = matcherMonetary.group(0).replaceAll("[^\\d-\\.\\+]", "");
    }

    if (matcherSplit.find()) {
      description = description.replaceAll(Pattern.quote(matcherSplit.group(0)), "");
      setSplit(matcherSplit.group(0).replaceAll("[^\\d]", ""));
    }

    description = description.replaceAll("[^a-zA-Z]", "");
    description = description.replaceAll("[m][o][n][t][h][s][g][i][f][t][s]", "").replaceAll("[m][o][n][t][h][s][g][i][f][t]", "")
            .replaceAll("[m][g]", "");
    // System.out.println(recency);
    // System.out.println(frequency);
    // System.out.println(monetary);

    setOAARecency(recency);
    setOAAFrequency(frequency);
    setOAAMonetary(monetary);

    if (!description.isBlank()) {
      System.out.println(description);
      setIsSpecial(true);
    }

  }

  public void setOAAMonetary(String monetary) throws Exception {
    final String MONETARY = "Monetary";

    if (monetary != null) {
      String monetaryArray[] = monetary.split("-");
      String fromGift = "";
      String toGift = "";

      if (monetaryArray.length == 1) {

        if (monetary.endsWith("+")) {
          monetary = monetary.replaceAll("[^\\d\\.]", "");
          fromGift = monetary;
          toGift = String.valueOf(Integer.MAX_VALUE);
        } else {
          fromGift = monetaryArray[0];
          toGift = monetaryArray[0];
        }

      } else if (monetaryArray.length == 2) {
        fromGift = monetaryArray[0];
        toGift = monetaryArray[1];
      } else {
        throw new Exception(String.format("Invalid %s format\nInput = \"%s\" ", MONETARY, monetary));
      }

      setFromGift(fromGift);
      setToGift(toGift);

    } else {
      setFromGift("0");
      setToGift(String.valueOf(Integer.MAX_VALUE));
    }

  }

  public void setOAAFrequency(String frequency) throws Exception {
    final String FREQUENCY = "Frequency";

    if (frequency != null) {
      String frequencyArray[] = frequency.split("-");
      String fromNumOfGifts = "";
      String toNumOfGifts = "";

      if (frequencyArray.length == 1) {

        if (frequency.endsWith("+")) {
          frequency = frequency.replaceAll("[^\\d]", "");
          fromNumOfGifts = frequency;
          toNumOfGifts = String.valueOf(Integer.MAX_VALUE);
        } else {
          fromNumOfGifts = frequencyArray[0];
          toNumOfGifts = frequencyArray[0];
        }

      } else if (frequencyArray.length == 2) {
        fromNumOfGifts = frequencyArray[0];
        toNumOfGifts = frequencyArray[1];
      } else {
        throw new Exception(String.format("Invalid %s format\nInput = \"%s\" ", FREQUENCY, frequency));
      }

      setFromNumOfGifts(fromNumOfGifts);
      setToNumOfGifts(toNumOfGifts);

    } else {
      setFromNumOfGifts("0");
      setToNumOfGifts(String.valueOf(Integer.MAX_VALUE));
    }

  }

  public void setOAARecency(String recency) throws Exception {
    final String RECENCY = "Recency";

    if (recency != null) {
      String recencyArray[] = recency.split("-");
      String fromGiftDateMonths = "";
      String toGiftDateMonths = "";

      if (recencyArray.length == 1) {

        if (recency.endsWith("+")) {
          recency = recency.replaceAll("[^\\d]", "");
          fromGiftDateMonths = recency;
          toGiftDateMonths = String.valueOf(Integer.MAX_VALUE);
        } else {
          fromGiftDateMonths = recencyArray[0];
          toGiftDateMonths = recencyArray[0];
        }

      } else if (recencyArray.length == 2) {
        fromGiftDateMonths = recencyArray[0];
        toGiftDateMonths = recencyArray[1];
      } else {
        throw new Exception(String.format("Invalid %s format\nInput = \"%s\" ", RECENCY, recency));
      }

      setFromGiftDateMonths(fromGiftDateMonths);
      setToGiftDateMonths(toGiftDateMonths);

    } else {
      setFromGiftDateMonths("0");
      setToGiftDateMonths(String.valueOf(Integer.MAX_VALUE));
    }

  }

  public void setCampaignID(String campaignID) {
    this.campaignID = campaignID;
  }

  public void setSplit(String split) throws Exception {
    split = split.replaceAll("[^\\d]", "");
    if (Validators.isNumber(split))
      this.split = Integer.valueOf(split);
    else
      throw new Exception(String.format("Invalid %s format, value must be a number.\nInput: %s", fieldName.SPLIT.getName(), split));
  }

  public void setFieldsToSearch(String[] fieldsToSearch) {
    this.fieldsToSearch = fieldsToSearch;
  }
  
  public void setValuesToSearch(String[] valuesToSearch) {
	    this.valuesToSearch = valuesToSearch;
	  }
  
  public void setIsValueToSeachExact(boolean isValueToSeachExact) {
	  this.isValueToSeachExact = isValueToSeachExact;
  }
  
  public void setIsValueToSeachPattern(boolean isValueToSeachPattern) {
	  this.isValueToSeachPattern = isValueToSeachPattern;
  }
}
