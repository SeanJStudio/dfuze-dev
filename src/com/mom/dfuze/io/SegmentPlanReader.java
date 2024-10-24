/**
 * Project: Dfuze
 * File: SegmentPlanReader.java
 * Date: Nov 26, 2020
 * Time: 11:01:42 AM
 */
package com.mom.dfuze.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.SegmentPlan;
import com.mom.dfuze.ui.UiController;


/**
 * @author Sean Johnson, A00940079
 *
 */
public class SegmentPlanReader {

  private SegmentPlanReader() {

  }

  public static List<List<String>> readSegmentPlanData(String segmentPlanName) throws Exception {

      JOptionPane.showMessageDialog(UiController.getMainFrame(), String.format("Please import the %s segment plan.\n", segmentPlanName),
              "Import Segment Plan", JOptionPane.INFORMATION_MESSAGE);
      
      return FileIngestor.ingest();
  }

  /**
   * Reads segmentPlan data from a Microsoft Access Database file into a List<SegmentPlan>
   * Each row in the data represents a SegmentPlan Object
   * 
   * @param segmentPlanName
   *          the name of the segmentPlan table in the segmentPlan Database
   * @return List<SegmentPlan>, the list of segmentPlans from the database
   * @throws ApplicationException
   *           throws an Exception in case something goes wrong
   */
  public static List<SegmentPlan> createSegmentPlanList(String segmentPlanName, List<List<String>> segmentPlanData) throws ApplicationException {
    List<SegmentPlan> segmentPlanList = null;

    String segmentPlanPath = "segment-plans/segment-plans.mdb";

    try {
      File segmentPlanfile = new File(segmentPlanPath);
      System.out.println(segmentPlanfile.getCanonicalPath());
      String[] tableNames = AccessReader.readTableNames(segmentPlanfile);

      boolean hasTableName = false;
      for (String tableName : tableNames) {
        if (tableName.trim().equalsIgnoreCase(segmentPlanName.trim())) {
          hasTableName = true;
          break;
        }
      }

      if (!hasTableName)
        throw new ApplicationException(String.format("The %s segment plan table could not be found in the database.", segmentPlanName));
      System.out.println("read the tables");
      /*
       * /
       */

      Database db = DatabaseBuilder.open(segmentPlanfile);

      System.out.println("opened the db");
      Table table = db.getTable(segmentPlanName);
      System.out.println("got the table");

      int numberOfFields = table.getColumnCount(); // get the amount of fields as an int
      // System.out.println(table.getRowCount());
      String[] requiredSegmentPlanFields = new String[numberOfFields]; // create new String[] to store fields aka headers
      int headerIndex = -1;

      for (Column column : table.getColumns()) {
        String columnName = column.getName();
        System.out.println(columnName);
        requiredSegmentPlanFields[++headerIndex] = columnName;
      }
      db.close();

      /*
       * /
       */

      String[] inHeaders = new String[segmentPlanData.get(0).size()];
      inHeaders = segmentPlanData.get(0).toArray(inHeaders);

      segmentPlanData.remove(0);

      if (requiredSegmentPlanFields.length != inHeaders.length)
        throw new ApplicationException(
                String.format("Was expecting %d fields but found %d in the segment plan", requiredSegmentPlanFields.length, inHeaders.length));

      for (int i = 0; i < requiredSegmentPlanFields.length; ++i) {

        if (!requiredSegmentPlanFields[i].equals(inHeaders[i]))
          throw new ApplicationException(String.format("Found %s but was expecting required segment plan field of %s at column %d in the import file",
                  inHeaders[i], requiredSegmentPlanFields[i], (i + 1)));

        boolean fieldFoundInSegmentPlanDb = false;
        for (String field : SegmentPlan.allSegmentPlanHeaders) {
          if (requiredSegmentPlanFields[i].equals(field)) {
            fieldFoundInSegmentPlanDb = true;
            break;
          }
        }

        if (!fieldFoundInSegmentPlanDb) {
          throw new ApplicationException(String.format(
                  "Could not find the unkown required segment plan field of %s in the Segment Plan field database", requiredSegmentPlanFields[i]));
        }

      }

      segmentPlanList = new ArrayList<>();

      for (int k = 0; k < segmentPlanData.size(); ++k) {
        SegmentPlan segmentPlan = new SegmentPlan();
        for (int l = 0; l < segmentPlanData.get(k).size(); ++l) {
          String fieldToSet = inHeaders[l];
          String fieldValue = segmentPlanData.get(k).get(l);
          if (fieldValue == null)
            fieldValue = "";

          fieldValue = fieldValue.trim();
          System.out.println(fieldToSet + " " + fieldValue);
          try {
            if (fieldToSet.equals(SegmentPlan.fieldName.PRIORITY.getName()))
              segmentPlan.setPriority(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.SEGMENT_CODE.getName()))
              segmentPlan.setSegmentCode(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.SEGMENT_NAME.getName()))
              segmentPlan.setSegmentName(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.IS_INDIVIDUAL.getName()))
              segmentPlan.setIsIndividual(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.IS_BUSINESS.getName()))
              segmentPlan.setIsBusiness(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.GIFT_DATE_USED.getName()))
              segmentPlan.setGiftDateUsed(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.FROM_GIFT_DATE.getName()))
              segmentPlan.setFromGiftDate(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.TO_GIFT_DATE.getName()))
              segmentPlan.setToGiftDate(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.FROM_GIFT_DATE_MONTHS.getName()))
              segmentPlan.setFromGiftDateMonths(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.TO_GIFT_DATE_MONTHS.getName()))
              segmentPlan.setToGiftDateMonths(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.GIFT_USED.getName()))
              segmentPlan.setGiftUsed(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.FROM_GIFT.getName()))
              segmentPlan.setFromGift(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.TO_GIFT.getName()))
              segmentPlan.setToGift(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.FROM_NUM_OF_GIFTS.getName()))
              segmentPlan.setFromNumOfGifts(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.TO_NUM_OF_GIFTS.getName()))
              segmentPlan.setToNumOfGifts(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.LETTER_VERSION.getName()))
              segmentPlan.setLetterVersion(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.PACKAGE_VERSION.getName()))
              segmentPlan.setPackageVersion(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.REPLY_VERSION.getName()))
              segmentPlan.setReplyVersion(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.IS_STANDARD_ASK.getName()))
              segmentPlan.setIsStandardAsk(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.IS_ACTIVE_ASK.getName()))
              segmentPlan.setIsActiveAsk(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_ASK_1.getName()))
              segmentPlan.setStaticAsk1(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_ASK_2.getName()))
              segmentPlan.setStaticAsk2(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_ASK_3.getName()))
              segmentPlan.setStaticAsk3(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.ASK_OPEN.getName()))
              segmentPlan.setAskOpen(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.IS_STANDARD_MONTHLY_ASK.getName()))
              segmentPlan.setIsStandardMonthlyAsk(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.IS_ACTIVE_MONTHLY_ASK.getName()))
              segmentPlan.setIsActiveMonthlyAsk(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_MONTHLY_ASK_1.getName()))
              segmentPlan.setStaticMonthlyAsk1(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_MONTHLY_ASK_2.getName()))
              segmentPlan.setStaticMonthlyAsk2(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.STATIC_MONTHLY_ASK_3.getName()))
              segmentPlan.setStaticMonthlyAsk3(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.MONTHLY_ASK_OPEN.getName()))
              segmentPlan.setMonthlyAskOpen(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.SEGMENT_PLAN_FILTER_1.getName()))
              segmentPlan.setSegmentPlanFilter1(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.SEGMENT_PLAN_FILTER_2.getName()))
              segmentPlan.setSegmentPlanFilter2(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.IS_SUPPRESSED.getName()))
              segmentPlan.setIsSuppressed(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.ONE_AND_ALL_CCM_SEGMENT_DESCRIPTION.getName()))
              segmentPlan.setOneAndAllCCMSegmentDescription(fieldValue);
            else if (fieldToSet.equals(SegmentPlan.fieldName.CAMPAIGN_ID.getName()))
              segmentPlan.setCampaignID(fieldValue);
            else {
              throw new ApplicationException(String.format("Field of %s doesn't have a setter in the segmentPlanFactory", fieldToSet));
            }
          } catch (Exception e) {
        	  e.printStackTrace();
        	  System.out.println(e.getStackTrace());
        	  
            throw new ApplicationException(String.format("Error on Line %d at field %d\n%s", k + 1, l + 1, e.getMessage()));
          }

        }

        if (segmentPlan.getIsStandardAsk() && segmentPlan.getIsActiveAsk())
          throw new ApplicationException(String.format("Only one of %s and %s can be set to \"Y\"", SegmentPlan.fieldName.IS_STANDARD_ASK.getName(),
                  SegmentPlan.fieldName.IS_ACTIVE_ASK.getName()));

        if (segmentPlan.getIsStandardMonthlyAsk() && segmentPlan.getIsActiveMonthlyAsk())
          throw new ApplicationException(String.format("Only one of %s and %s can be set to \"Y\"",
                  SegmentPlan.fieldName.IS_STANDARD_MONTHLY_ASK.getName(), SegmentPlan.fieldName.IS_ACTIVE_MONTHLY_ASK.getName()));

        segmentPlan.setSegmentID(k);

        segmentPlanList.add(segmentPlan);
      }

    } catch (Exception e) {
      throw new ApplicationException(e.getMessage());
    } finally {

    }
    return segmentPlanList;
  }

}
