/**
 * Project: Dfuze
 * File: QualityControlReport.java
 * Date: Jul 5, 2020
 * Time: 2:39:24 PM
 */
package com.mom.dfuze.io;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Quality Control Report
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class QualityControlReport {

  public static final String REPORT_FILENAME = "quality_control_report.txt";
  public static final String HORIZONTAL_BORDER_LINE = "====================================================================================";
  public static final String INPUT_FILE_FORMAT = "%-19s %-2s %-39s %-21s";
  public static final String MAPPED_FIELDS_FORMAT = "%-19s %-2s %-22s %-38s";
  public static final String DEDUPES_FORMAT = "%-19s %-5d %-16s %-16s %-16s %-4s";
  public static final String ROW_FORMAT = "%08d %-12s %-40s %-40s %4d %6.3f %13d %-60s";

  private String clientName;
  private String jobTemplateName;
  private LocalDate dateProcessed;
  private int numOfRecordsImported;
  private int numOfRecordsDeduped;
  private int finalNumOfRecords;
  private String finalOutputFileName;
  private List<InputFileEntry> inputFileEntryList = new ArrayList<>();
  private List<MappedFieldEntry> mappedFieldEntryList = new ArrayList<>();
  private List<DedupeEntry> dedupeEntryList = new ArrayList<>();

  public QualityControlReport() {

  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public void setJobTemplateName(String jobTemplateName) {
    this.jobTemplateName = jobTemplateName;
  }

  public void setDateProcessed(LocalDate dateProcessed) {
    this.dateProcessed = dateProcessed;
  }

  public void setNumOfRecordsImported(int numOfRecordsImported) {
    this.numOfRecordsImported = numOfRecordsImported;
  }

  public void setNumOfRecordsDeduped(int numOfRecordsDeduped) {
    this.numOfRecordsDeduped = numOfRecordsDeduped;
  }

  public void setFinalNumOfRecords(int finalNumOfRecords) {
    this.finalNumOfRecords = finalNumOfRecords;
  }

  public void setFinalOutpuName(String finalOutputFileName) {
    this.finalOutputFileName = finalOutputFileName;
  }

  public String getClientName() {
    return clientName;
  }

  public String getJobTemplateName() {
    return jobTemplateName;
  }

  public LocalDate getDateProcessed() {
    return dateProcessed;
  }

  public int getNumOfRecordsImported() {
    return numOfRecordsImported;
  }

  public int getNumOfRecordsDeduped() {
    return numOfRecordsDeduped;
  }

  public int getFinalNumOfRecords() {
    return finalNumOfRecords;
  }

  public String getFinalOutputFileName() {
    return finalOutputFileName;
  }

  public List<InputFileEntry> getInputFileEntryList() {
    return inputFileEntryList;
  }

  public List<MappedFieldEntry> getMappedFieldEntryList() {
    return mappedFieldEntryList;
  }

  public List<DedupeEntry> getDedeupEntryList() {
    return dedupeEntryList;
  }

  protected class InputFileEntry {
    String fileName;
    int fileQuantity;

    public void setFileName(String fileName) {
      this.fileName = fileName;
    }

    public void setFileQuantity(int fileQuantity) {
      this.fileQuantity = fileQuantity;
    }

    public String getFileName() {
      return fileName;
    }

    public int getFileQuantity() {
      return fileQuantity;
    }
  }

  protected class MappedFieldEntry {
    String requiredFieldName;
    String mappedFieldName;

    public void setRequiredFieldName(String requiredFieldName) {
      this.requiredFieldName = requiredFieldName;
    }

    public void setMappedFieldName(String mappedFieldName) {
      this.mappedFieldName = mappedFieldName;
    }

    public String getRequiredFieldName() {
      return requiredFieldName;
    }

    public String getMappedFieldName() {
      return mappedFieldName;
    }
  }

  protected class DedupeEntry {
    String fullNameFieldUsed;
    String fullName2FieldUsed;
    String priorityNameFieldUsed;
    String sortOrderUsed;
    Boolean isExcludePoBoxesSelected;
    int numOfDupesDeleted;

    public void setFullName(String fullNameFieldUsed) {
      this.fullNameFieldUsed = fullNameFieldUsed;
    }

    public void setFullName2(String fullName2FieldUsed) {
      this.fullName2FieldUsed = fullName2FieldUsed;
    }

    public void setPriority(String priorityNameFieldUsed) {
      this.priorityNameFieldUsed = priorityNameFieldUsed;
    }

    public void setSortOrderUsed(String sortOrderUsed) {
      this.sortOrderUsed = sortOrderUsed;
    }

    public void setNumOfDupesDeleted(int numOfDupesDeleted) {
      this.numOfDupesDeleted = numOfDupesDeleted;
    }

    public void setIsExcludePoBoxesSelected(boolean isExcludePoBoxesSelected) {
      this.isExcludePoBoxesSelected = isExcludePoBoxesSelected;
    }

    public String getFullNameFieldUsed() {
      return fullNameFieldUsed;
    }

    public String getFullName2FieldUsed() {
      return fullName2FieldUsed;
    }

    public String getSortOrderUsed() {
      return sortOrderUsed;
    }

    public int getNumOfDupesDeleted() {
      return numOfDupesDeleted;
    }

    public boolean getIsExcludePoBoxesSelected() {
      return isExcludePoBoxesSelected;
    }
  }

  public void print() {

  }

}
