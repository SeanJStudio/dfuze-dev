/**
 * Project: Dfuze
 * File: RecordSorters.java
 * Date: Apr 25, 2020
 * Time: 11:43:04 AM
 */
package com.mom.dfuze.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import com.mom.dfuze.data.util.Common;

/**
 * Collection of Inner Classes to compare and sort Records
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RecordSorters {
	static final String BAD_DATE_TIME_FORMAT = "d/M/yyyy"; 

	/**
	 * Inner Class CompareByField to compare a record by a fieldName in ascending order
	 */
	public static class CompareByFieldAsc implements Comparator<Record> {

		private String fieldToGet;

		public CompareByFieldAsc(String fieldToGet) {
			this.fieldToGet = fieldToGet;
		}

		@Override
		public int compare(Record record1, Record record2) {
			String record1Field = UserData.getRecordFieldByName(fieldToGet, record1);
			String record2Field = UserData.getRecordFieldByName(fieldToGet, record2);
			return (record1Field).compareTo(record2Field);
		}
	}

	/**
	 * Inner Class CompareByField to compare a record by a fieldName in descending order
	 */
	public static class CompareByFieldDesc implements Comparator<Record> {

		private String fieldToGet;

		public CompareByFieldDesc(String fieldToGet) {
			this.fieldToGet = fieldToGet;
		}

		@Override
		public int compare(Record record1, Record record2) {
			String record1Field = UserData.getRecordFieldByName(fieldToGet, record1);
			String record2Field = UserData.getRecordFieldByName(fieldToGet, record2);
			return (record2Field).compareTo(record1Field);
		}
	}

	/**
	 * Inner Class CompareByFieldAsAscAsNumber compares a records by a fieldName as an number in ascending order
	 */
	public static class CompareByFieldAscAsNumber implements Comparator<Record> {

		private String fieldToGet;

		public CompareByFieldAscAsNumber(String fieldToGet) {
			this.fieldToGet = fieldToGet;
		}

		@Override
		public int compare(Record record1, Record record2) {
			String record1Field = UserData.getRecordFieldByName(fieldToGet, record1).replaceAll("[^0-9\\.]", "");
			String record2Field = UserData.getRecordFieldByName(fieldToGet, record2).replaceAll("[^0-9\\.]", "");

			try {
				return new BigDecimal(record1Field).compareTo(new BigDecimal(record2Field));
			} catch (Exception err) {
				return record1Field.compareTo(record2Field);
			}
		}
	}

	/**
	 * Inner Class CompareByFieldAsDescAsNumber compares a records by a fieldName as an number in ascending order
	 */
	public static class CompareByFieldDescAsNumber implements Comparator<Record> {

		private String fieldToGet;

		public CompareByFieldDescAsNumber(String fieldToGet) {
			this.fieldToGet = fieldToGet;
		}

		@Override
		public int compare(Record record1, Record record2) {
			String record1Field = UserData.getRecordFieldByName(fieldToGet, record1).replaceAll("[^0-9\\.]", "");
			String record2Field = UserData.getRecordFieldByName(fieldToGet, record2).replaceAll("[^0-9\\.]", "");

			try {
				return new BigDecimal(record2Field).compareTo(new BigDecimal(record1Field));
			} catch (Exception err) {
				return record2Field.compareTo(record1Field);
			}
		}
	}

	/**
	 * Inner Class CompareByFieldAscAsDate compares a records by a fieldName as an date in ascending order
	 */
	public static class CompareByFieldAscAsDate implements Comparator<Record> {

		String fieldToGet;
		DateTimeFormatter dateTimeFormatter;


		public CompareByFieldAscAsDate(String fieldToGet, DateTimeFormatter dateTimeFormatter) {
			this.fieldToGet = fieldToGet;
			this.dateTimeFormatter = dateTimeFormatter;
		}

		@Override
		public int compare(Record record1, Record record2) {

			String record1Field = UserData.getRecordFieldByName(fieldToGet, record1);
			String record2Field = UserData.getRecordFieldByName(fieldToGet, record2);
			LocalDate record1Date;
			LocalDate record2Date;
			try {
				record1Date = LocalDate.parse(record1Field, dateTimeFormatter);
			}
			catch(Exception e) {       	  
				record1Date = LocalDate.parse("1/1/9000", DateTimeFormatter.ofPattern(BAD_DATE_TIME_FORMAT));
			}

			try {
				record2Date = LocalDate.parse(record2Field, dateTimeFormatter);
			}
			catch(Exception e) {       	  
				record2Date = LocalDate.parse("1/1/9000", DateTimeFormatter.ofPattern(BAD_DATE_TIME_FORMAT));
			}

			return record1Date.compareTo(record2Date);

		}

	}

	/**
	 * Inner Class CompareByFieldAscAsDate compares a records by a fieldName as an date in ascending order
	 */
	public static class CompareByFieldDescAsDate implements Comparator<Record> {

		String fieldToGet;
		DateTimeFormatter dateTimeFormatter;


		public CompareByFieldDescAsDate(String fieldToGet, DateTimeFormatter dateTimeFormatter) {
			this.fieldToGet = fieldToGet;
			this.dateTimeFormatter = dateTimeFormatter;
		}

		@Override
		public int compare(Record record1, Record record2) {

			String record1Field = UserData.getRecordFieldByName(fieldToGet, record1);
			String record2Field = UserData.getRecordFieldByName(fieldToGet, record2);
			LocalDate record1Date;
			LocalDate record2Date;
			try {
				record1Date = LocalDate.parse(record1Field, dateTimeFormatter);
			}
			catch(Exception e) {       	  
				record1Date = LocalDate.parse("1/1/1000", DateTimeFormatter.ofPattern(BAD_DATE_TIME_FORMAT));
			}

			try {
				record2Date = LocalDate.parse(record2Field, dateTimeFormatter);
			}
			catch(Exception e) {       	  
				record2Date = LocalDate.parse("1/1/1000", DateTimeFormatter.ofPattern(BAD_DATE_TIME_FORMAT));
			}

			return record2Date.compareTo(record1Date);

		}

	}

	/**
	 * Inner Class CompareByPCode to compare and sort by a records postal code
	 */
	public static class CompareByPCode implements Comparator<Record> {
		@Override
		public int compare(Record record1, Record record2) {
			return record1.getPCode().compareTo(record2.getPCode());
		}
	}

	/**
	 * Inner Class ComparebyPCode compare and sort by a records postal code, add1 and add2
	 */
	public static class CompareByDupePCodeDupeAdd1DupeAdd2 implements Comparator<Record> {
		@Override
		public int compare(Record record1, Record record2) {

			int add1Val = record2.getDupeAdd1().compareTo(record1.getDupeAdd1());
			int add2Val = record2.getDupeAdd2().compareTo(record1.getDupeAdd2());
			int pCodeVal = record1.getDupePCode().compareTo(record2.getDupePCode());
			int rrVal = record2.getDupeRR().compareTo(record1.getDupeRR());
			int poBoxVal = record2.getDupePOBox().compareTo(record1.getDupePOBox());
			int poBoxExtra = record2.getDupePOBoxExtra().compareTo(record1.getDupePOBoxExtra());

			if (pCodeVal == 0)
				if(rrVal == 0)
					if(poBoxVal == 0)
						if(poBoxExtra == 0)
							if (add1Val == 0)
								if (add2Val == 0)
									return 0;
								else
									return add2Val;
							else
								return add1Val;
						else
							return poBoxExtra;
					else
						return poBoxVal;
				else
					return rrVal;
			else
				return pCodeVal;
		}
	}
	
	/**
	 * Inner Class ComparebyPCode compare and sort by a records postal code, add1 and add2
	 */
	public static class CompareByDupeName1Name2LengthDesc implements Comparator<Record> {
		@Override
		public int compare(Record record1, Record record2) {
			int name1Val = record2.getDupeName1().length() - record1.getDupeName1().length();
			int name2Val = record2.getDupeName2().length() - record1.getDupeName2().length();
			
			if(name1Val == 0)
				if(name2Val == 0)
					return 0;
				else
					return name2Val;
			else
				return name1Val;
		}
	}

	/**
	 * Inner Class CompareBySeg to compare and sort by a records segment
	 */
	public static class CompareBySeg implements Comparator<Record> {
		@Override
		public int compare(Record record1, Record record2) {
			return record1.getSeg().compareTo(record2.getSeg());
		}
	}

	/**
	 * Inner Class CompareBySegCode to compare and sort by a records segment code
	 */
	public static class CompareBySegCode implements Comparator<Record> {
		@Override
		public int compare(Record record1, Record record2) {
			return record1.getSegCode().compareTo(record2.getSegCode());
		}
	}

	public static class CompareBySegCodeLastDonationAmount implements Comparator<Record> {
		@Override
		public int compare(Record record1, Record record2) {
			int segCodeVal = record1.getSegCode().compareTo(record2.getSegCode());
			int lastDnAmtVal = Common.extractBigDecimal(record1.getLstDnAmt()).compareTo(Common.extractBigDecimal(record2.getLstDnAmt()));

			if (segCodeVal == 0)
				if (lastDnAmtVal == 0)
					return 0;
				else
					return lastDnAmtVal;
			else
				return segCodeVal;

		}
	}

	/**
	 * Inner Class CompareByDfId to compare and sort by a records inner Dfuze ID
	 */
	public static class CompareByDfId implements Comparator<Record> {
		@Override
		public int compare(Record record1, Record record2) {
			return record1.getDfId() - record2.getDfId();
		}
	}

	/**
	 * Inner Class CompareByInFieldAsc to compare and sort by a field in the records original input data in ascending order
	 * 
	 * Uses an index value to retrieve an element from the records input data
	 */
	public static class CompareByInFieldAsc implements Comparator<Record> {

		int index;

		public CompareByInFieldAsc(int index) {
			this.index = index;
		}

		@Override
		public int compare(Record record1, Record record2) {
			if (record1.getDfInData()[index] != null && record2.getDfInData()[index] != null) { // check for null in case we add seeds
				return record1.getDfInData()[index].trim().compareTo(record2.getDfInData()[index].trim());
			}
			return 0;
		}
	}

	/**
	 * Inner Class CompareByInFieldAscAsNumber to compare and sort by a field in the records original input data as a number in ascending order
	 * 
	 * Uses an index value to retrieve an element from the records input data
	 */
	public static class CompareByInFieldAscAsNumber implements Comparator<Record> {

		int index;

		public CompareByInFieldAscAsNumber(int index) {
			this.index = index;
		}

		@Override
		public int compare(Record record1, Record record2) {
			if (record1.getDfInData()[index] != null && record2.getDfInData()[index] != null) {
				try {
					return new BigDecimal(record1.getDfInData()[index].replaceAll("[^0-9\\.]", "")).compareTo(new BigDecimal(record2.getDfInData()[index].replaceAll("[^0-9\\.]", "")));
				} catch (Exception err) {
					return record1.getDfInData()[index].trim().compareTo(record2.getDfInData()[index].trim());
				}
			}
			return 0;

		}

	}

	/**
	 * Inner Class CompareByInFieldAscAsDate to compare and sort by a field in the records original input data as a date in ascending order
	 * 
	 * Uses an index value to retrieve an element from the records input data
	 */
	public static class CompareByInFieldAscAsDate implements Comparator<Record> {

		int index;
		DateTimeFormatter dateTimeFormatter;

		public CompareByInFieldAscAsDate(int index, DateTimeFormatter dateTimeFormatter) {
			this.index = index;
			this.dateTimeFormatter = dateTimeFormatter;
		}

		@Override
		public int compare(Record record1, Record record2) {

			String record1Field = record1.getDfInData()[index];
			String record2Field = record2.getDfInData()[index];
			LocalDate record1Date;
			LocalDate record2Date;
			try {
				record1Date = LocalDate.parse(record1Field, dateTimeFormatter);
			}
			catch(Exception e) {       	  
				record1Date = LocalDate.parse("1/1/9000", DateTimeFormatter.ofPattern(BAD_DATE_TIME_FORMAT));
			}

			try {
				record2Date = LocalDate.parse(record2Field, dateTimeFormatter);
			}
			catch(Exception e) {       	  
				record2Date = LocalDate.parse("1/1/9000", DateTimeFormatter.ofPattern(BAD_DATE_TIME_FORMAT));
			}

			return record1Date.compareTo(record2Date);

		}

	}

	/**
	 * Inner Class CompareByInFieldAscAsDate to compare and sort by a field in the records original input data as a date in ascending order
	 * 
	 * Uses an index value to retrieve an element from the records input data
	 */
	public static class CompareByInFieldDescAsDate implements Comparator<Record> {

		int index;
		DateTimeFormatter dateTimeFormatter;


		public CompareByInFieldDescAsDate(int index, DateTimeFormatter dateTimeFormatter) {
			this.index = index;
			this.dateTimeFormatter = dateTimeFormatter;
		}

		@Override
		public int compare(Record record1, Record record2) {
			String record1Field = record1.getDfInData()[index];
			String record2Field = record2.getDfInData()[index];
			LocalDate record1Date;
			LocalDate record2Date;
			try {
				record1Date = LocalDate.parse(record1Field, dateTimeFormatter);
			}
			catch(Exception e) {       	  
				record1Date = LocalDate.parse("1/1/1000", DateTimeFormatter.ofPattern(BAD_DATE_TIME_FORMAT));
			}

			try {
				record2Date = LocalDate.parse(record2Field, dateTimeFormatter);
			}
			catch(Exception e) {       	  
				record2Date = LocalDate.parse("1/1/1000", DateTimeFormatter.ofPattern(BAD_DATE_TIME_FORMAT));
			}

			return record2Date.compareTo(record1Date);

		}

	}

	/**
	 * Inner Class CompareByInFieldDesc to compare and sort by a field in the records original input data in descending order
	 * 
	 * Uses an index value to retrieve an element from the records input data
	 */
	public static class CompareByInFieldDesc implements Comparator<Record> {

		int index;

		public CompareByInFieldDesc(int index) {
			this.index = index;
		}

		@Override
		public int compare(Record record1, Record record2) {
			if (record2.getDfInData()[index] != null && record1.getDfInData()[index] != null) { // check for null in case we add seeds
				return record2.getDfInData()[index].trim().compareTo(record1.getDfInData()[index].trim());
			}
			return 0;
		}
	}

	/**
	 * Inner Class CompareByInFieldDesc to compare and sort by a field in the records original input data as a number in descending order
	 * 
	 * Uses an index value to retrieve an element from the records input data
	 */
	public static class CompareByInFieldDescAsNumber implements Comparator<Record> {

		int index;

		public CompareByInFieldDescAsNumber(int index) {
			this.index = index;
		}

		@Override
		public int compare(Record record1, Record record2) {
			if (record1.getDfInData()[index] != null && record2.getDfInData()[index] != null) {
				try {
					return new BigDecimal(record2.getDfInData()[index].replaceAll("[^0-9\\.]", "")).compareTo(new BigDecimal(record1.getDfInData()[index].replaceAll("[^0-9\\.]", "")));
				} catch (Exception err) {
					return record2.getDfInData()[index].trim().compareTo(record1.getDfInData()[index].trim());
				}
			}
			return 0;

		}

	}
}
