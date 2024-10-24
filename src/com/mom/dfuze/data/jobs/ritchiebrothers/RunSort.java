/**
 * Project: Dfuze
 * File: RunSort.java
 * Date: Mar 10, 2020
 * Time: 10:24:05 PM
 */
package com.mom.dfuze.data.jobs.ritchiebrothers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.RecordSorters;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.data.util.Unidecode;

/**
 * RunSort implements a RunBehavior for Ritchie Brothers Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RunSort implements RunRitchieBrothersBehavior {

  protected String BEHAVIOR_NAME = "Sort";
  protected String DESCRIPTION = "Splits the company, address1, and address2 fields by 45 characters or less if the limit is reached within the middle of a word. Splits the company, address1, and address2 fields by 45 characters or less if the limit is reached within the middle of a word. Splits the company, address1, and address2 fields by 45 characters or less if the limit is reached within the middle of a word. Splits the company, address1, and address2 fields by 45 characters or less if the limit is reached within the middle of a word.";
  protected String[] REQUIRED_FIELDS = { UserData.fieldName.ADDRESS1.getName(), UserData.fieldName.ADDRESS2.getName(),
          UserData.fieldName.POSTALCODE.getName(), UserData.fieldName.NAME1.getName() };

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

    int counter = 0;

    for (int i = 0; i < data.length; i++) {

      String nam1 = data[i][userData.getNam1Index()];
      String add1 = data[i][userData.getAdd1Index()];
      String add2 = data[i][userData.getAdd2Index()];
      String pCode = data[i][userData.getPCodeIndex()];

      Record record = new Record.Builder(++counter, data[i], add1, add2, pCode).setNam1(nam1).build();

      userData.add(record); // add the record the recordList in userData

    }

    List<Record> sortedArray = userData.getRecordList();
    System.out.println(sortedArray.get(1).toString());
    Collections.sort(sortedArray, new RecordSorters.CompareByPCode());
    Map<Record, Set<Record>> dupes = new HashMap<Record, Set<Record>>(); // key = record to keep, values = Set of duplicate records to remove
    Set<Integer> allDupes = new HashSet<Integer>(); // set to keep track of all records flagged as duplicates

    for (int i = 0; i < sortedArray.size(); ++i) {
      Record tempRec = sortedArray.get(i); // tempRec to reference the current record
      Set<Record> tempDupes = new HashSet<Record>(); // set to hold all duplicates associated with tempRec

      boolean enteredPostalCodeRange = false;
      boolean isDone = false;
      int j = 0;
      while (!isDone && j < sortedArray.size()) { // if our data is sorted by postal code we can save time
        for (j = i + 1; j < sortedArray.size(); ++j) { // +1 as we don't need to compare to tempRec

          Record nextRec = sortedArray.get(j); // the next record to compare to

          if (tempRec.getPCode().equals(nextRec.getPCode())) { // if we find the same postal code
            enteredPostalCodeRange = true;
            boolean dupeAddFound = false;
            if (!tempRec.getAdd1().isEmpty()) {
              if (tempRec.getAdd1().equalsIgnoreCase(nextRec.getAdd1()))
                dupeAddFound = true;
              else if (tempRec.getAdd1().equalsIgnoreCase(nextRec.getAdd2()))
                dupeAddFound = true;
            } else if (!tempRec.getAdd2().isEmpty()) {
              if (tempRec.getAdd2().equalsIgnoreCase(nextRec.getAdd1()))
                dupeAddFound = true;
              else if (tempRec.getAdd2().equalsIgnoreCase(nextRec.getAdd2()))
                dupeAddFound = true;
            }

            if (dupeAddFound) { // if we find record with duplicate postal codes and address'
              String tempRecName = Unidecode.decode(tempRec.getNam1()); // Get the ASCII transliteration of a Unicode string
              String nextRecName = Unidecode.decode(nextRec.getNam1()); // safely converts all characters to standard a-zA-Z0-9\\s

              boolean dupeNameFound = false;
              if (!tempRec.getNam1().isEmpty() && !nextRecName.isEmpty()) { // if both records aren't blank

                if (Common.getInitials(tempRecName).equalsIgnoreCase(Common.getInitials(nextRecName))) // Initials
                  dupeNameFound = true;
                else if (Common.getNysiis(tempRecName).equalsIgnoreCase(Common.getNysiis(nextRecName))) // NYSIIS
                  dupeNameFound = true;
                else if (Common.getMetaphone3(tempRecName).equalsIgnoreCase(Common.getMetaphone3(nextRecName))) // Metaphone3
                  dupeNameFound = true;
                else if (Common.getRefinedSoundex(tempRecName).equalsIgnoreCase(Common.getRefinedSoundex(nextRecName))) // Refined Soundex
                  dupeNameFound = true;
                else if (tempRecName.equalsIgnoreCase(Common.getReversedString(nextRecName))) // Fully reversed Strings
                  dupeNameFound = true;

              }

              if (dupeNameFound) // if a duplicate was found
                if (allDupes.add(sortedArray.get(j).getDfId())) { // if we can add the duplicate to our set
                  tempDupes.add(sortedArray.get(j));
                }
            }
          } else if (enteredPostalCodeRange) {
            isDone = true;
          }

        }
      }
      if (tempDupes.size() > 0) // if we have found duplicates
        dupes.put(sortedArray.get(i), tempDupes); // add record to keep as a key and the duplicates as values to the HashMap
    }

    // Print the duplicates
    for (Entry<Record, Set<Record>> entry : dupes.entrySet()) {
      System.out.print("Orgi: " + entry.getKey().getNam1() + " Dupes: ");
      for (Record record : entry.getValue()) {
        System.out.print(record.getNam1() + " ");
      }
      System.out.print('\n');
    }

    // set the Header fields that we want to output
    userData.setDfHeaders(new String[] { UserData.fieldName.NAME1.getName() });

  }

}
