/**
 * Project: Dfuze
 * File: UserData.java
 * Date: Mar 22, 2020
 * Time: 2:24:09 PM
 */
package com.mom.dfuze.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mom.dfuze.Dfuze;

/**
 * UserData Class to hold the data and fields of the data throughout its lifetime of the application
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class UserData {

	private String[] dfHeaders;
	private String[] inHeaders; // never sort these, needed to find the index values on data mapping
	private String[][] data; // the user's data
	private List<Record> recordList = new ArrayList<Record>(); // arraylist holding the record

	// longest character length
	private int[] exportMaxFieldLength;

	public static final int DEFAULT_INDEX = -1;

	// Internal
	private int spFilt1Index = DEFAULT_INDEX; // extra donation reference field to be used for whatever
	private int spFilt2Index = DEFAULT_INDEX; // extra donation reference field to be used for whatever

	// Internal Dedupe
	private int dupeStreetAdd1Index = DEFAULT_INDEX;
	private int dupeStreetAdd2Index = DEFAULT_INDEX;
	private int dupeMetaStreetAdd1Index = DEFAULT_INDEX;
	private int dupeMetaStreetAdd2Index = DEFAULT_INDEX;
	private int dupeAdd1Index = DEFAULT_INDEX;
	private int dupeAdd2Index = DEFAULT_INDEX;
	private int dupePCodeIndex = DEFAULT_INDEX;
	private int dupePOBoxIndex = DEFAULT_INDEX;
	private int dupePOBoxExtraIndex = DEFAULT_INDEX;
	private int dupeStreetDirectionIndex = DEFAULT_INDEX;
	private int dupeRRIndex = DEFAULT_INDEX;
	private int dupeName1Index = DEFAULT_INDEX;
	private int dupeName1FirstPersonIndex = DEFAULT_INDEX;
	private int dupeName1SecondPersonIndex = DEFAULT_INDEX;
	private int dupeName1NormalizedIndex = DEFAULT_INDEX;
	private int dupeName2Index = DEFAULT_INDEX;
	private int dupeName2FirstPersonIndex = DEFAULT_INDEX;
	private int dupeName2SecondPersonIndex = DEFAULT_INDEX;
	private int dupeName2NormalizedIndex = DEFAULT_INDEX;
	private int dupeGroupIdIndex = DEFAULT_INDEX;
	private int dupeGroupSizeIndex = DEFAULT_INDEX;

	// Miscelaneous
	private int inIdIndex = DEFAULT_INDEX; // supplied ID in data
	private int langIndex = DEFAULT_INDEX; // language indicator
	private int barCodeIndex = DEFAULT_INDEX; // barcode
	private int recTypeIndex = DEFAULT_INDEX; // record Type indicator ex. I = Individual or O = Organization
	private int statusIndex = DEFAULT_INDEX; // status
	private int listNumIndex = DEFAULT_INDEX; // order number of the list
	private int dayIndex = DEFAULT_INDEX; // date day
	private int monthIndex = DEFAULT_INDEX; // date month
	private int yearIndex = DEFAULT_INDEX; // date year
	private int longitudeIndex = DEFAULT_INDEX; // longitude geocoordinates
	private int latitudeIndex = DEFAULT_INDEX; // latitude geocoordinates
	private int quantityIndex = DEFAULT_INDEX; // quantity
	private int phone1Index = DEFAULT_INDEX; // phone1
	private int phone2Index = DEFAULT_INDEX; // phone2
	private int mobilePhoneIndex = DEFAULT_INDEX; // mobilePhone
	private int emailIndex = DEFAULT_INDEX; // email
	private int storeIdIndex = DEFAULT_INDEX; // store id

	// Name and address members
	private int prefixIndex = DEFAULT_INDEX; // ex. Mr.
	private int suffixIndex = DEFAULT_INDEX; // ex. Jr
	private int fstNameIndex = DEFAULT_INDEX; // first name
	private int midNameIndex = DEFAULT_INDEX; // middle name
	private int lstNameIndex = DEFAULT_INDEX; // last name
	private int nam1Index = DEFAULT_INDEX; // full name
	private int nam1_2Index = DEFAULT_INDEX; // the second half of nam1 when split on 'n' characters
	private int nam2Index = DEFAULT_INDEX; // optional full name2
	
	private int spousePrefixIndex = DEFAULT_INDEX; // spousal prefix
	private int spouseSuffixIndex = DEFAULT_INDEX; // spousal suffix
	private int spouseFstNameIndex = DEFAULT_INDEX; // spousal first name
	private int spouseMidNameIndex = DEFAULT_INDEX; // spousal middle name
	private int spouseLstNameIndex = DEFAULT_INDEX; // spousal last name
	private int spouseNam1Index = DEFAULT_INDEX; // spousal full name
	private int spouseNam2Index = DEFAULT_INDEX; // spousal optional full name2
	
	private int nam2_2Index = DEFAULT_INDEX; // the second half of nam2 when split on 'n' characters
	private int cmpnyIndex = DEFAULT_INDEX; // company
	private int cmpny_2Index = DEFAULT_INDEX; // the second half of cmpny when split on 'n' characters
	private int cmpnyCnIndex = DEFAULT_INDEX; // The contact for the company
	private int dearSalIndex = DEFAULT_INDEX; // dear salutation
	private int paraSalIndex = DEFAULT_INDEX; // paragraph salutation
	private int cmpnySalIndex = DEFAULT_INDEX; // company salutation
	private int cmpnyAdd1Index = DEFAULT_INDEX; // Company address 1
	private int cmpnyAdd2Index = DEFAULT_INDEX; // Company address 2
	private int add1Index = DEFAULT_INDEX; // address 1
	private int add1_2Index = DEFAULT_INDEX; // the second half of add1 when split on 'n' characters
	private int add2Index = DEFAULT_INDEX; // address 2
	private int add2_2Index = DEFAULT_INDEX; // the second half of add2 when split on 'n' characters
	private int cityIndex = DEFAULT_INDEX; // city
	private int provIndex = DEFAULT_INDEX; // province
	private int pCodeIndex = DEFAULT_INDEX; // postal code
	private int cntryIndex = DEFAULT_INDEX; // country
	private int ncoaIndex = DEFAULT_INDEX; // ncoa
	private int dnmIndex = DEFAULT_INDEX; // dnm
	private int deceasedIndex = DEFAULT_INDEX; // deceased

	// Donation members
	private int dnAmtArrIndex = DEFAULT_INDEX; // donation amounts supplied as a delimited array
	private int dnDatArrIndex = DEFAULT_INDEX; // donation dates supplied as a delimited array
	private int numDnIndex = DEFAULT_INDEX; // # of donations given
	private int numDnLst12MnthsIndex = DEFAULT_INDEX; // # of donations give from last 12 months
	private int ttlDnAmtIndex = DEFAULT_INDEX; // total donation amount given in $
	private int ttlDnAmtLst12MnthsIndex = DEFAULT_INDEX; // total donation amount given in $ from last 12 months
	private int ttlDnAmtCrntYrIndex = DEFAULT_INDEX; // total donation amount given in the current year
	private int ttlDnAmtLstYrIndex = DEFAULT_INDEX; // total donation amount given in last year
	private int fstDnAmtIndex = DEFAULT_INDEX; // first donation amount give
	private int lstDnAmtIndex = DEFAULT_INDEX; // last donation amount given
	private int lrgDnAmtIndex = DEFAULT_INDEX; // largest donation amount given
	private int smlDnAmtIndex = DEFAULT_INDEX; // smallest donation amount given
	private int lMDnAmtIndex = DEFAULT_INDEX; // last monthly donation amount given
	private int fstDnDatIndex = DEFAULT_INDEX; // date of the first donation
	private int lstDnDatIndex = DEFAULT_INDEX; // date of the last donation
	private int lMDnDatIndex = DEFAULT_INDEX; // last monthly donation date given
	private int penultAmtIndex = DEFAULT_INDEX; // second last donation amount given
	private int penultDatIndex = DEFAULT_INDEX; // second last donation date
	private int dn1AmtIndex = DEFAULT_INDEX; // first suggested donation amount
	private int dn2AmtIndex = DEFAULT_INDEX; // second suggested donation amount
	private int dn3AmtIndex = DEFAULT_INDEX; // third suggested donation amount
	private int dn4AmtIndex = DEFAULT_INDEX; // fourth suggested donation amount
	private int oDnAmtIndex = DEFAULT_INDEX; // open donation amount
	private int mDn1AmtIndex = DEFAULT_INDEX; // monthly first suggested donation amount
	private int mDn2AmtIndex = DEFAULT_INDEX; // monthly second suggested donation amount
	private int mDn3AmtIndex = DEFAULT_INDEX; // monthly third suggested donation amount
	private int mDn4AmtIndex = DEFAULT_INDEX; // monthly fourth suggested donation amount
	private int mODnAmtIndex = DEFAULT_INDEX; // monthly open donation amount
	private int provide1Index = DEFAULT_INDEX; // provides x of or to something
	private int provide2Index = DEFAULT_INDEX; // provides x of or to something
	private int provide3Index = DEFAULT_INDEX; // provides x of or to something
	private int provide4Index = DEFAULT_INDEX; // provides x of or to something

	// Segmentation
	private int priorityIndex = DEFAULT_INDEX; // some value used as a priority
	private int abGroupIndex = DEFAULT_INDEX; // group identifier ex. A / B
	private int appealIndex = DEFAULT_INDEX; // the appeal
	private int segIndex = DEFAULT_INDEX; // segment
	private int segCodeIndex = DEFAULT_INDEX; // segment code
	private int letVerIndex = DEFAULT_INDEX; // the letter version to receive
	private int pkgVerIndex = DEFAULT_INDEX; // the package version to receive
	private int repVerIndex = DEFAULT_INDEX; // the reply version to recieve
	private int codeLineIndex = DEFAULT_INDEX; // the codeLine ex. ID + segCode + c/t test

	private int meanAmtIndex = DEFAULT_INDEX; // mean donation amount
	private int medianIndex = DEFAULT_INDEX; // median
	private int sDevAmtIndex = DEFAULT_INDEX; // standard deviation amount

	// RFM
	private int rScoreIndex = DEFAULT_INDEX; // recency score
	private int fScoreIndex = DEFAULT_INDEX; // frequency score
	private int mScoreIndex = DEFAULT_INDEX; // monetary score
	private int rfmScoreIndex = DEFAULT_INDEX; // total rfm score

	// PARCEL
	private int lengthIndex = DEFAULT_INDEX; // length
	private int widthIndex = DEFAULT_INDEX; // width
	private int heightIndex = DEFAULT_INDEX; // height
	private int weightIndex = DEFAULT_INDEX; // weight

	/**
	 * fieldName enum to hold all the fieldNames
	 */
	public enum fieldName {
		// Internal
		DFID("dfId"),
		IS_DUPE("dfIsDupe"),
		SEGMENT_PLAN_FILTER_1("dfSpFilt1"),
		SEGMENT_PLAN_FILTER_2("dfSpFilt2"),

		// Internal Dupe members
		DUPE_STREET_ADD1("dfDuSAdd1"),
		DUPE_STREET_ADD2("dfDuSAdd2"),
		DUPE_META_STREET_ADD1("dfDuMSAdd1"),
		DUPE_META_STREET_ADD2("dfDuMSAdd2"),
		DUPE_ADD1("dfDuAdd1"),
		DUPE_ADD2("dfDuAdd2"),
		DUPE_PCODE("dfDuPCode"),
		DUPE_POBOX("dfDuBox"),
		DUPE_POBOX_EXTRA("dfDuBoxEx"),
		DUPE_STREET_DIRECTION("dfDuStDir"),
		DUPE_RR("dfDuRR"),
		DUPE_NAME1("dfDuNam1"),
		DUPE_NAME1_FIRST_PERSON("dfDuNam1P1"),
		DUPE_NAME1_SECOND_PERSON("dfDuNam1P2"),
		DUPE_NAME1_NORMALIZED("dfDuNam1Nm"),
		DUPE_NAME2("dfDuNam2"),
		DUPE_NAME2_FIRST_PERSON("dfDuNam2P1"),
		DUPE_NAME2_SECOND_PERSON("dfDuNam2P2"),
		DUPE_NAME2_NORMALIZED("dfDuNam2Nm"),
		DUPE_GROUP_ID("dfDuGrpId"),
		DUPE_GROUP_SIZE("dfDuGrpSze"),

		// Miscelaneous
		IN_ID("dfInId"),
		LANGUAGE("dfLang"),
		BARCODE("dfBarCode"),
		RECORD_TYPE("dfRecType"),
		STATUS("dfStatus"),
		LIST_NUMBER("dfListNum"),
		DAY("dfDay"),
		MONTH("dfMonth"),
		YEAR("dfYear"),
		LATITUDE("dfLat"),
		LONGITUDE("dfLong"),
		QUANTITY("dfQty"),
		PHONE1("dfPhone1"),
		PHONE2("dfPhone2"),
		MOBILE_PHONE("dfMobile"),
		EMAIL("dfEmail"),
		STORE_ID("dfStoreId"),

		// Name and address members
		PREFIX("dfPrefix"), // ex. Mr.
		SUFFIX("dfSuffix"), // ex. Jr
		FIRSTNAME("dfFstName"), // first name
		MIDDLENAME("dfMidName"), // middle name
		LASTNAME("dfLstName"), // last name
		NAME1("dfNam1"), // full name
		NAME1_2("dfNam1_2"), // the second half of nam1 when split on 'n' characters
		NAME2("dfNam2"), // optional full name2
		NAME2_2("dfNam2_2"), // the second half of nam2 when split on 'n' characters
		
		SPOUSE_PREFIX("dfSPrefix"), // ex. Mr.
		SPOUSE_SUFFIX("dfSSuffix"), // ex. Jr
		SPOUSE_FIRSTNAME("dfSFstName"), // first name
		SPOUSE_MIDDLENAME("dfSMidName"), // middle name
		SPOUSE_LASTNAME("dfSLstName"), // last name
		SPOUSE_NAME1("dfSNam1"), // full name
		SPOUSE_NAME2("dfSNam2"), // optional full name2
		
		COMPANY("dfCmpny"), // company
		COMPANY_2("dfCmpny_2"), // the second half of cmpny when split on 'n' characters
		COMPANY_CONTACT("dfCmpnyCn"), // The company contact
		DEAR_SALUTATION("dfDearSal"), // dear salutation
		PARAGRAPH_SALUTATION("dfParaSal"), // paragraph salutation
		COMPANY_SALUTATION("dfCmpnySal"),
		COMPANY_ADDRESS1("dfCmpnyAdd1"), // Company address 1
		COMPANY_ADDRESS2("dfCmpnyAdd2"), // Company address 2
		ADDRESS1("dfAdd1"), // address 1
		ADDRESS1_2("dfAdd1_2"), // the second half of add1 when split on 'n' characters
		ADDRESS2("dfAdd2"), // address 2
		ADDRESS2_2("dfAdd2_2"), // the second half of add2 when split on 'n' characters
		CITY("dfCity"), // city
		PROVINCE("dfProv"), // province
		POSTALCODE("dfPCode"), // postal code
		COUNTRY("dfCntry"), // country
		NCOA("dfNcoa"), // ncoa
		DNM("dfDnm"), // do not mail
		DECEASED("dfDeceased"), // deceased

		// Donation members
		DONATION_AMOUNT_ARRAY("dfDnAmtArr"), // donation amounts supplied as a delimited array
		DONATION_DATE_ARRAY("dfDnDatArr"), // donation dates supplied as a delimited array
		NUMBER_OF_DONATIONS("dfNumDn"), // # of donations given
		NUMBER_OF_DONATIONS_LAST_12_MONTHS("dfNumDnLst12Mnths"), // # of donations given from last 12 months
		TOTAL_DONATION_AMOUNT("dfTtlDnAmt"), // total donation amount given in $
		TOTAL_DONATION_AMOUNT_LAST_12_MONTHS("dfTtlDnAmtLst12Mnths"), // total donation amount given in $ from last 12 months
		TOTAL_DONATION_AMOUNT_CURRENT_YEAR("dfTtlDnAmtCrntYr"), // total donation amount given in the current year
		TOTAL_DONATION_AMOUNT_LAST_YEAR("dfTtlDnAmtLstYr"), // total donation amount given in the last year
		FIRST_DONATION_AMOUNT("dfFstDnAmt"), // first donation amount given
		LAST_DONATION_AMOUNT("dfLstDnAmt"), // last donation amount given
		LARGEST_DONATION_AMOUNT("dfLrgDnAmt"), // largest donation amount given
		SMALLEST_DONATION_AMOUNT("dfSmlDnAmt"), // smallest donation amount given
		LAST_MONTHLY_DONATION_AMOUNT("dfLMDnAmt"), // last monthly donation amount given
		FIRST_DONATION_DATE("dfFstDnDat"), // date of the first donation
		LAST_DONATION_DATE("dfLstDnDat"), // date of the last donation
		LAST_MONTHLY_DONATION_DATE("dfLMDnDat"), // date of the last monthly donation
		PENULTIMATE_AMOUNT("dfPenultAmt"), // second last donation amount
		PENULTIMATE_DATE("dfPenultDat"), //second last donation date
		DONATION1_AMOUNT("dfDn1Amt"), // first suggested donation amount
		DONATION2_AMOUNT("dfDn2Amt"), // second suggested donation amount
		DONATION3_AMOUNT("dfDn3Amt"), // third suggested donation amount
		DONATION4_AMOUNT("dfDn4Amt"), // fourth suggested donation amount
		OPEN_DONATION_AMOUNT("dfODnAmt"), // open donation amount
		MONTHLY_DONATION1_AMOUNT("dfMDn1Amt"), // monthly first suggested donation amount
		MONTHLY_DONATION2_AMOUNT("dfMDn2Amt"), // monthly second suggested donation amount
		MONTHLY_DONATION3_AMOUNT("dfMDn3Amt"), // monthly third suggested donation amount
		MONTHLY_DONATION4_AMOUNT("dfMDn4Amt"), // monthly fourth suggested donation amount
		MONTHLY_OPEN_DONATION_AMOUNT("dfMODnAmt"), // monthly open donation amount
		PROVIDE1("dfProvide1"), // Provides x of or to something
		PROVIDE2("dfProvide2"), // Provides x of or to something
		PROVIDE3("dfProvide3"), // Provides x of or to something
		PROVIDE4("dfProvide4"), // Provides x of or to something

		// Segmentation
		PRIORITY("dfPriority"), // some value used as a priority
		AB_GROUP("dfABGroup"), // A / B Group testing
		APPEAL("dfAppeal"), // appeal
		SEGMENT("dfSeg"), // segment
		SEGMENT_CODE("dfSegCode"), // segment code
		LETTER_VERSION("dfLetVer"), // the letter version to receive
		PACKAGE_VERSION("dfPkgVer"), // the package version to receive
		REPLY_VERSION("dfRepVer"), // the reply version to receive
		CODELINE("dfCodeLine"), // the codeline ex. ID + segCode + a/b test

		MEAN_AMOUNT("dfMeanAmt"), // mean donation amount
		MEDIAN("dfMedian"), // median
		STANDARD_DEVIATION_AMOUNT("dfSDevAmt"), // standard deviation amount

		// RFM
		RECENCY("dfRScore"), // recency score
		FREQUENCY("dfFScore"), // frequency score
		MONETARY("dfMScore"), // monetary score
		RFM("dfRfmScore"), // total rfm score

		// PARCEL
		LENGTH("dfLength"),
		WIDTH("dfWidth"),
		HEIGHT("dfHeight"),
		WEIGHT("dfWeight");

		String name;

		private fieldName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	};

	/**
	 * allDfHeaders holds all of the UserData header values as a string
	 */
	public static final String[] allDfHeaders = { 
			fieldName.IN_ID.getName(),
			fieldName.DFID.getName(),
			fieldName.IS_DUPE.getName(),
			fieldName.SEGMENT_PLAN_FILTER_1.getName(),
			fieldName.SEGMENT_PLAN_FILTER_2.getName(),
			
			fieldName.DUPE_META_STREET_ADD1.getName(),
			fieldName.DUPE_META_STREET_ADD2.getName(),
			fieldName.DUPE_ADD1.getName(),
			fieldName.DUPE_ADD2.getName(),
			fieldName.DUPE_PCODE.getName(),
			fieldName.DUPE_POBOX.getName(),
			fieldName.DUPE_POBOX_EXTRA.getName(),
			fieldName.DUPE_STREET_DIRECTION.getName(),
			fieldName.DUPE_RR.getName(),
			fieldName.DUPE_NAME1.getName(),
			fieldName.DUPE_NAME1_FIRST_PERSON.getName(),
			fieldName.DUPE_NAME1_SECOND_PERSON.getName(),
			fieldName.DUPE_NAME1_NORMALIZED.getName(),
			fieldName.DUPE_NAME2.getName(),
			fieldName.DUPE_NAME2_FIRST_PERSON.getName(),
			fieldName.DUPE_NAME2_SECOND_PERSON.getName(),
			fieldName.DUPE_NAME2_NORMALIZED.getName(),
			fieldName.DUPE_GROUP_ID.getName(),
			fieldName.DUPE_GROUP_SIZE.getName(),
			
			fieldName.LANGUAGE.getName(),
			fieldName.BARCODE.getName(),
			fieldName.RECORD_TYPE.getName(),
			fieldName.STATUS.getName(),
			fieldName.LIST_NUMBER.getName(),
			fieldName.DAY.getName(),
			fieldName.MONTH.getName(),
			fieldName.YEAR.getName(),
			fieldName.LATITUDE.getName(),
			fieldName.LONGITUDE.getName(),
			fieldName.QUANTITY.getName(),
			fieldName.PHONE1.getName(),
			fieldName.PHONE2.getName(),
			fieldName.MOBILE_PHONE.getName(),
			fieldName.EMAIL.getName(),
			fieldName.STORE_ID.getName(),
			
			fieldName.PREFIX.getName(),
			fieldName.SUFFIX.getName(),
			fieldName.FIRSTNAME.getName(),
			fieldName.MIDDLENAME.getName(),
			fieldName.LASTNAME.getName(),
			fieldName.NAME1.getName(),
			fieldName.NAME1_2.getName(),
			fieldName.NAME2.getName(),
			
			fieldName.SPOUSE_PREFIX.getName(),
			fieldName.SPOUSE_SUFFIX.getName(),
			fieldName.SPOUSE_FIRSTNAME.getName(),
			fieldName.SPOUSE_MIDDLENAME.getName(),
			fieldName.SPOUSE_LASTNAME.getName(),
			fieldName.SPOUSE_NAME1.getName(),
			fieldName.SPOUSE_NAME2.getName(),
			
			fieldName.NAME2_2.getName(),
			fieldName.COMPANY.getName(),
			fieldName.COMPANY_2.getName(),
			fieldName.COMPANY_CONTACT.getName(),
			fieldName.DEAR_SALUTATION.getName(),
			fieldName.PARAGRAPH_SALUTATION.getName(),
			fieldName.COMPANY_ADDRESS1.getName(),
			fieldName.COMPANY_ADDRESS2.getName(),
			fieldName.ADDRESS1.getName(),
			fieldName.ADDRESS1_2.getName(),
			fieldName.ADDRESS2.getName(),
			fieldName.ADDRESS2_2.getName(),
			fieldName.CITY.getName(),
			fieldName.PROVINCE.getName(),
			fieldName.POSTALCODE.getName(),
			fieldName.COUNTRY.getName(),
			fieldName.NCOA.getName(),
			fieldName.DNM.getName(),
			fieldName.DECEASED.getName(),
			
			fieldName.DONATION_AMOUNT_ARRAY.getName(),
			fieldName.DONATION_DATE_ARRAY.getName(),
			fieldName.NUMBER_OF_DONATIONS.getName(),
			fieldName.NUMBER_OF_DONATIONS_LAST_12_MONTHS.getName(),
			fieldName.TOTAL_DONATION_AMOUNT.getName(),
			fieldName.TOTAL_DONATION_AMOUNT_LAST_12_MONTHS.getName(),
			fieldName.TOTAL_DONATION_AMOUNT_CURRENT_YEAR.getName(),
			fieldName.TOTAL_DONATION_AMOUNT_LAST_YEAR.getName(),
			fieldName.FIRST_DONATION_AMOUNT.getName(),
			fieldName.LAST_DONATION_AMOUNT.getName(),
			fieldName.LARGEST_DONATION_AMOUNT.getName(),
			fieldName.SMALLEST_DONATION_AMOUNT.getName(),
			fieldName.LAST_MONTHLY_DONATION_AMOUNT.getName(),
			fieldName.FIRST_DONATION_DATE.getName(),
			fieldName.LAST_DONATION_DATE.getName(),
			fieldName.LAST_MONTHLY_DONATION_DATE.getName(),
			fieldName.PENULTIMATE_AMOUNT.getName(),
			fieldName.PENULTIMATE_DATE.getName(),
			fieldName.DONATION1_AMOUNT.getName(),
			fieldName.DONATION2_AMOUNT.getName(),
			fieldName.DONATION3_AMOUNT.getName(),
			fieldName.DONATION4_AMOUNT.getName(),
			fieldName.OPEN_DONATION_AMOUNT.getName(),
			fieldName.MONTHLY_DONATION1_AMOUNT.getName(),
			fieldName.MONTHLY_DONATION2_AMOUNT.getName(),
			fieldName.MONTHLY_DONATION3_AMOUNT.getName(),
			fieldName.MONTHLY_DONATION4_AMOUNT.getName(),
			fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName(),
			fieldName.PROVIDE1.getName(),
			fieldName.PROVIDE2.getName(),
			fieldName.PROVIDE3.getName(),
			fieldName.PROVIDE4.getName(),
			
			fieldName.PRIORITY.getName(),
			fieldName.AB_GROUP.getName(),
			fieldName.APPEAL.getName(),
			fieldName.SEGMENT.getName(),
			fieldName.SEGMENT_CODE.getName(),
			fieldName.LETTER_VERSION.getName(),
			fieldName.PACKAGE_VERSION.getName(),
			fieldName.REPLY_VERSION.getName(),
			fieldName.CODELINE.getName(),
			
			fieldName.MEAN_AMOUNT.getName(),
			fieldName.MEDIAN.getName(),
			fieldName.STANDARD_DEVIATION_AMOUNT.getName(),
			fieldName.RECENCY.getName(),
			fieldName.FREQUENCY.getName(),
			fieldName.MONETARY.getName(),
			fieldName.RFM.getName(),
			fieldName.LENGTH.getName(),
			fieldName.WIDTH.getName(),
			fieldName.HEIGHT.getName(),
			fieldName.WEIGHT.getName()
	};

	/**
	 * Default constructor for Objects of Class UserData
	 */
	public UserData() {

	}

	public void setInHeaders(String[] inHeaders) {
		this.inHeaders = inHeaders;
	}

	public void setData(String[][] data) {
		this.data = data;
	}

	public String[] getInHeaders() {
		return inHeaders;
	}

	public String[][] getData() {
		return data;
	}

	// Index Getters
	public int getSpFilt1Index() {
		return spFilt1Index;
	}

	public int getSpFilt2Index() {
		return spFilt2Index;
	}

	public int getDupeStreetAdd1Index() {
		return dupeStreetAdd1Index;
	}

	public int getDupeStreetAdd2Index() {
		return dupeStreetAdd2Index;
	}

	public int getDupeMetaStreetAdd1Index() {
		return dupeMetaStreetAdd1Index;
	}

	public int getDupeMetaStreetAdd2Index() {
		return dupeMetaStreetAdd2Index;
	}

	public int getDupeAdd1Index() {
		return dupeAdd1Index;
	}

	public int getDupeAdd2Index() {
		return dupeAdd2Index;
	}

	public int getDupePCodeIndex() {
		return dupePCodeIndex;
	}

	public int getDupePOBoxIndex() {
		return dupePOBoxIndex;
	}

	public int getDupePOBoxExtraIndex() {
		return dupePOBoxExtraIndex;
	}

	public int getDupeStreetDirectionIndex() {
		return dupeStreetDirectionIndex;
	}

	public int getDupeRRIndex() {
		return dupeRRIndex;
	}

	public int getDupeName1Index() {
		return dupeName1Index;
	}

	public int getDupeName1FirstPersonIndex() {
		return dupeName1FirstPersonIndex;
	}

	public int getDupeName1SecondPersonIndex() {
		return dupeName1SecondPersonIndex;
	}

	public int getDupeName1NormalizedIndex() {
		return dupeName1NormalizedIndex;
	}

	public int getDupeName2Index() {
		return dupeName2Index;
	}

	public int getDupeName2FirstPersonIndex() {
		return dupeName2FirstPersonIndex;
	}

	public int getDupeName2SecondPersonIndex() {
		return dupeName2SecondPersonIndex;
	}

	public int getDupeName2NormalizedIndex() {
		return dupeName2NormalizedIndex;
	}
	
	public int getDupeGroupIdIndex() {
		return dupeGroupIdIndex;
	}
	
	public int getDupeGroupSizeIndex() {
		return dupeGroupSizeIndex;
	}

	public int getInIdIndex() {
		return inIdIndex;
	}

	public int getLangIndex() {
		return langIndex;
	}

	public int getBarCodeIndex() {
		return barCodeIndex;
	}

	public int getRecTypeIndex() {
		return recTypeIndex;
	}
	
	public int getStatusIndex() {
		return statusIndex;
	}
	
	public int getListNumIndex() {
		return listNumIndex;
	}
	
	public int getDayIndex() {
		return dayIndex;
	}
	
	public int getMonthIndex() {
		return monthIndex;
	}
	
	public int getYearIndex() {
		return yearIndex;
	}
	
	public int getLatitudeIndex() {
		return latitudeIndex;
	}
	
	public int getLongitudeIndex() {
		return longitudeIndex;
	}
	
	public int getQuantityIndex() {
		return quantityIndex;
	}
	
	public int getPhone1Index() {
		return phone1Index;
	}
	
	public int getPhone2Index() {
		return phone2Index;
	}
	
	public int getMobilePhoneIndex() {
		return mobilePhoneIndex;
	}
	
	public int getEmailIndex() {
		return emailIndex;
	}
	
	public int getStoreIdIndex() {
		return storeIdIndex;
	}

	public int getPrefixIndex() {
		return prefixIndex;
	}

	public int getSuffixIndex() {
		return suffixIndex;
	}

	public int getFstNameIndex() {
		return fstNameIndex;
	}

	public int getMidNameIndex() {
		return midNameIndex;
	}

	public int getLstNameIndex() {
		return lstNameIndex;
	}

	public int getNam1Index() {
		return nam1Index;
	}

	public int getNam1_2Index() {
		return nam1_2Index;
	}

	public int getNam2Index() {
		return nam2Index;
	}

	public int getNam2_2Index() {
		return nam2_2Index;
	}

	public int getSpousePrefixIndex() {
		return spousePrefixIndex;
	}

	public int getSpouseSuffixIndex() {
		return spouseSuffixIndex;
	}

	public int getSpouseFstNameIndex() {
		return spouseFstNameIndex;
	}

	public int getSpouseMidNameIndex() {
		return spouseMidNameIndex;
	}

	public int getSpouseLstNameIndex() {
		return spouseLstNameIndex;
	}

	public int getSpouseNam1Index() {
		return spouseNam1Index;
	}

	public int getSpouseNam2Index() {
		return spouseNam2Index;
	}

	public int getCmpnyIndex() {
		return cmpnyIndex;
	}

	public int getCmpny_2Index() {
		return cmpny_2Index;
	}

	public int getCmpnyCnIndex() {
		return cmpnyCnIndex;
	}

	public int getDearSalIndex() {
		return dearSalIndex;
	}

	public int getParaSalIndex() {
		return paraSalIndex;
	}

	public int getCmpnySalIndex() {
		return cmpnySalIndex;
	}
	
	public int getCmpnyAdd1Index() {
		return cmpnyAdd1Index;
	}
	
	public int getCmpnyAdd2Index() {
		return cmpnyAdd2Index;
	}

	public int getAdd1Index() {
		return add1Index;
	}

	public int getAdd1_2Index() {
		return add1_2Index;
	}

	public int getAdd2Index() {
		return add2Index;
	}

	public int getAdd2_2Index() {
		return add2_2Index;
	}

	public int getCityIndex() {
		return cityIndex;
	}

	public int getProvIndex() {
		return provIndex;
	}

	public int getPCodeIndex() {
		return pCodeIndex;
	}

	public int getCntryIndex() {
		return cntryIndex;
	}
	
	public int getNcoaIndex() {
		return ncoaIndex;
	}
	
	public int getDnmIndex() {
		return dnmIndex;
	}
	
	public int getDeceasedIndex() {
		return deceasedIndex;
	}

	public int getDnAmtArrIndex() {
		return dnAmtArrIndex;
	}

	public int getDnDatArrIndex() {
		return dnDatArrIndex;
	}

	public int getNumDnIndex() {
		return numDnIndex;
	}
	
	public int getNumDnLst12MnthsIndex() {
		return numDnLst12MnthsIndex;
	}

	public int getTttlDnAmtIndex() {
		return ttlDnAmtIndex;
	}
	
	public int getTtlDnAmtLst12MnthsIndex() {
		return ttlDnAmtLst12MnthsIndex;
	}
	
	public int getTtlDnAmtCrntYrIndex() {
		return ttlDnAmtCrntYrIndex;
	}
	
	public int getTtlDnAmtLstYrIndex() {
		return ttlDnAmtLstYrIndex;
	}

	public int getFstDnAmtIndex() {
		return fstDnAmtIndex;
	}

	public int getLstDnAmtIndex() {
		return lstDnAmtIndex;
	}

	public int getLrgDnAmtIndex() {
		return lrgDnAmtIndex;
	}

	public int getSmlDnAmtIndex() {
		return smlDnAmtIndex;
	}

	public int getLMDnAmtIndex() {
		return lMDnAmtIndex;
	}

	public int getFstDnDatIndex() {
		return fstDnDatIndex;
	}

	public int getLstDnDatIndex() {
		return lstDnDatIndex;
	}

	public int getLMDnDatIndex() {
		return lMDnDatIndex;
	}
	
	public int getPenultAmtIndex() {
		return penultAmtIndex;
	}
	
	public int getPenultDatIndex() {
		return penultDatIndex;
	}

	public int getDn1AmtIndex() {
		return dn1AmtIndex;
	}

	public int getDn2AmtIndex() {
		return dn2AmtIndex;
	}

	public int getDn3AmtIndex() {
		return dn3AmtIndex;
	}

	public int getDn4AmtIndex() {
		return dn4AmtIndex;
	}

	public int getODnAmtIndex() {
		return oDnAmtIndex;
	}

	public int getMDn1AmtIndex() {
		return mDn1AmtIndex;
	}

	public int getMDn2AmtIndex() {
		return mDn2AmtIndex;
	}

	public int getMDn3AmtIndex() {
		return mDn3AmtIndex;
	}

	public int getMDn4AmtIndex() {
		return mDn4AmtIndex;
	}

	public int getMODnAmtIndex() {
		return mODnAmtIndex;
	}

	public int getProvide1Index() {
		return provide1Index;
	}

	public int getProvide2Index() {
		return provide2Index;
	}

	public int getProvide3Index() {
		return provide3Index;
	}
	
	public int getProvide4Index() {
		return provide4Index;
	}

	public int getPriorityIndex() {
		return priorityIndex;
	}

	public int getABGroupIndex() {
		return abGroupIndex;
	}

	public int getAppealIndex() {
		return appealIndex;
	}

	public int getSegIndex() {
		return segIndex;
	}

	public int getSegCodeIndex() {
		return segCodeIndex;
	}

	public int getLetVerIndex() {
		return letVerIndex;
	}

	public int getPkgVerIndex() {
		return pkgVerIndex;
	}

	public int getRepVerIndex() {
		return repVerIndex;
	}

	public int getCodeLineIndex() {
		return codeLineIndex;
	}

	public int getMeanAmtIndex() {
		return meanAmtIndex;
	}
	
	public int getMedianIndex() {
		return medianIndex;
	}

	public int getSDevAmtIndex() {
		return sDevAmtIndex;
	}

	public int getRScoreIndex() {
		return rScoreIndex;
	}

	public int getFScoreIndex() {
		return fScoreIndex;
	}

	public int getMScoreIndex() {
		return mScoreIndex;
	}

	public int getRfmScoreIndex() {
		return rfmScoreIndex;
	}

	public int getLengthIndex() {
		return lengthIndex;
	}

	public int getWidthIndex() {
		return widthIndex;
	}

	public int getHeightIndex() {
		return heightIndex;
	}

	public int getWeightIndex() {
		return weightIndex;
	}

	//
	//
	//
	//
	// Index Setters
	//
	//
	//
	//
	public void setSpFilt1Index(int spFilt1Index) {
		this.spFilt1Index = spFilt1Index;
	}

	public void setSpFilt2Index(int spFilt2Index) {
		this.spFilt2Index = spFilt2Index;
	}

	public void setDupeStreetAdd1Index(int dupeStreetAdd1Index) {
		this.dupeStreetAdd1Index = dupeStreetAdd1Index;
	}

	public void setDupeStreetAdd2Index(int dupeStreetAdd2Index) {
		this.dupeStreetAdd2Index = dupeStreetAdd2Index;
	}

	public void setDupeMetaStreetAdd1Index(int dupeMetaStreetAdd1Index) {
		this.dupeMetaStreetAdd1Index = dupeMetaStreetAdd1Index;
	}

	public void setDupeMetaStreetAdd2Index(int dupeMetaStreetAdd2Index) {
		this.dupeMetaStreetAdd2Index = dupeMetaStreetAdd2Index;
	}

	public void setDupeAdd1Index(int dupeAdd1Index) {
		this.dupeAdd1Index = dupeAdd1Index;
	}

	public void setDupeAdd2Index(int dupeAdd2Index) {
		this.dupeAdd2Index = dupeAdd2Index;
	}

	public void setDupePCodeIndex(int dupePCodeIndex) {
		this.dupePCodeIndex = dupePCodeIndex;
	}

	public void setDupePOBoxIndex(int dupePOBoxIndex) {
		this.dupePOBoxIndex = dupePOBoxIndex;
	}

	public void setDupePOBoxExtraIndex(int dupePOBoxExtraIndex) {
		this.dupePOBoxExtraIndex = dupePOBoxExtraIndex;
	}

	public void setDupeStreetDirectionIndex(int dupeStreetDirectionIndex) {
		this.dupeStreetDirectionIndex = dupeStreetDirectionIndex;
	}

	public void setDupeRRIndex(int dupeRRIndex) {
		this.dupeRRIndex = dupeRRIndex;
	}

	public void setDupeName1Index(int dupeName1Index) {
		this.dupeName1Index = dupeName1Index;
	}

	public void setDupeName1FirstPersonIndex(int dupeName1FirstPersonIndex) {
		this.dupeName1FirstPersonIndex = dupeName1FirstPersonIndex;
	}

	public void setDupeName1SecondPersonIndex(int dupeName1SecondPersonIndex) {
		this.dupeName1SecondPersonIndex = dupeName1SecondPersonIndex;
	}

	public void setDupeName1NormalizedIndex(int dupeName1NormalizedIndex) {
		this.dupeName1NormalizedIndex = dupeName1NormalizedIndex;
	}

	public void setDupeName2Index(int dupeName2Index) {
		this.dupeName2Index = dupeName2Index;
	}

	public void setDupeName2FirstPersonIndex(int dupeName2FirstPersonIndex) {
		this.dupeName2FirstPersonIndex = dupeName2FirstPersonIndex;
	}

	public void setDupeName2SecondPersonIndex(int dupeName2SecondPersonIndex) {
		this.dupeName2SecondPersonIndex = dupeName2SecondPersonIndex;
	}

	public void setDupeName2NormalizedIndex(int dupeName2NormalizedIndex) {
		this.dupeName2NormalizedIndex = dupeName2NormalizedIndex;
	}
	
	public void setDupeGroupIdIndex(int dupeGroupIdIndex) {
		this.dupeGroupIdIndex = dupeGroupIdIndex;
	}
	
	public void setDupeGroupSizeIndex(int dupeGroupSizeIndex) {
		this.dupeGroupSizeIndex = dupeGroupSizeIndex;
	}

	public void setInIdIndex(int inIdIndex) {
		this.inIdIndex = inIdIndex;
	}

	public void setLangIndex(int langIndex) {
		this.langIndex = langIndex;
	}

	public void setBarCodeIndex(int barCodeIndex) {
		this.barCodeIndex = barCodeIndex;
	}

	public void setRecTypeIndex(int recTypeIndex) {
		this.recTypeIndex = recTypeIndex;
	}
	
	public void setStatusIndex(int statusIndex) {
		this.statusIndex = statusIndex;
	}
	
	public void setListNumIndex(int listNumIndex) {
		this.listNumIndex = listNumIndex;
	}

	public void setDayIndex(int dayIndex) {
		this.dayIndex = dayIndex;
	}
	
	public void setMonthIndex(int monthIndex) {
		this.monthIndex = monthIndex;
	}
	
	public void setYearIndex(int yearIndex) {
		this.yearIndex = yearIndex;
	}
	
	public void setLongitudeIndex(int longitudeIndex) {
		this.longitudeIndex = longitudeIndex;
	}
	
	public void setLatitudeIndex(int latitudeIndex) {
		this.latitudeIndex = latitudeIndex;
	}
	
	public void setQuantityIndex(int quantityIndex) {
		this.quantityIndex = quantityIndex;
	}
	
	public void setPhone1Index(int phone1Index) {
		this.phone1Index = phone1Index;
	}
	
	public void setPhone2Index(int phone2Index) {
		this.phone2Index = phone2Index;
	}
	
	public void setMobilePhoneIndex(int mobilePhoneIndex) {
		this.mobilePhoneIndex = mobilePhoneIndex;
	}
	
	public void setEmailIndex(int emailIndex) {
		this.emailIndex = emailIndex;
	}
	
	public void setStoreIdIndex(int storeIdIndex) {
		this.storeIdIndex = storeIdIndex;
	}

	public void setPrefixIndex(int prefixIndex) {
		this.prefixIndex = prefixIndex;
	}

	public void setSuffixIndex(int suffixIndex) {
		this.suffixIndex = suffixIndex;
	}

	public void setFstNameIndex(int fstNameIndex) {
		this.fstNameIndex = fstNameIndex;
	}

	public void setMidNameIndex(int midNameIndex) {
		this.midNameIndex = midNameIndex;
	}

	public void setLstNameIndex(int lstNameIndex) {
		this.lstNameIndex = lstNameIndex;
	}

	public void setNam1Index(int nam1Index) {
		this.nam1Index = nam1Index;
	}

	public void setNam1_2Index(int nam1_2Index) {
		this.nam1_2Index = nam1_2Index;
	}

	public void setNam2Index(int nam2Index) {
		this.nam2Index = nam2Index;
	}

	public void setNam2_2Index(int nam2_2Index) {
		this.nam2_2Index = nam2_2Index;
	}
	
	public void setSpousePrefixIndex(int spousePrefixIndex) {
		this.spousePrefixIndex = spousePrefixIndex;
	}

	public void setSpouseSuffixIndex(int spouseSuffixIndex) {
		this.spouseSuffixIndex = spouseSuffixIndex;
	}

	public void setSpouseFstNameIndex(int spouseFstNameIndex) {
		this.spouseFstNameIndex = spouseFstNameIndex;
	}

	public void setSpouseMidNameIndex(int spouseMidNameIndex) {
		this.spouseMidNameIndex = spouseMidNameIndex;
	}

	public void setSpouseLstNameIndex(int spouseLstNameIndex) {
		this.spouseLstNameIndex = spouseLstNameIndex;
	}

	public void setSpouseNam1Index(int spouseNam1Index) {
		this.spouseNam1Index = spouseNam1Index;
	}

	public void setSpouseNam2Index(int spouseNam2Index) {
		this.spouseNam2Index = spouseNam2Index;
	}
	
	public void setCmpnyIndex(int cmpnyIndex) {
		this.cmpnyIndex = cmpnyIndex;
	}

	public void setCmpny_2Index(int cmpny_2Index) {
		this.cmpny_2Index = cmpny_2Index;
	}

	public void setCmpnyCnIndex(int cmpnyCnIndex) {
		this.cmpnyCnIndex = cmpnyCnIndex;
	}

	public void setDearSalIndex(int dearSalIndex) {
		this.dearSalIndex = dearSalIndex;
	}

	public void setParaSalIndex(int paraSalIndex) {
		this.paraSalIndex = paraSalIndex;
	}

	public void setCmpnySalIndex(int cmpnySalIndex) {
		this.cmpnySalIndex = cmpnySalIndex;
	}
	
	public void setCmpnyAdd1Index(int cmpnyAdd1Index) {
		this.cmpnyAdd1Index = cmpnyAdd1Index;
	}
	
	public void setCmpnyAdd2Index(int cmpnyAdd2Index) {
		this.cmpnyAdd2Index = cmpnyAdd2Index;
	}

	public void setAdd1Index(int add1Index) {
		this.add1Index = add1Index;
	}

	public void setAdd1_2Index(int add1_2Index) {
		this.add1_2Index = add1_2Index;
	}

	public void setAdd2Index(int add2Index) {
		this.add2Index = add2Index;
	}

	public void setAdd2_2Index(int add2_2Index) {
		this.add2_2Index = add2_2Index;
	}

	public void setCityIndex(int cityIndex) {
		this.cityIndex = cityIndex;
	}

	public void setProvIndex(int provIndex) {
		this.provIndex = provIndex;
	}

	public void setPCodeIndex(int pCodeIndex) {
		this.pCodeIndex = pCodeIndex;
	}

	public void setCntryIndex(int cntryIndex) {
		this.cntryIndex = cntryIndex;
	}
	
	public void setNcoaIndex(int ncoaIndex) {
		this.ncoaIndex = ncoaIndex;
	}
	
	public void setDnmIndex(int dnmIndex) {
		this.dnmIndex = dnmIndex;
	}
	
	public void setDeceasedIndex(int deceasedIndex) {
		this.deceasedIndex = deceasedIndex;
	}

	public void setDnAmtArrIndex(int dnAmtArrIndex) {
		this.dnAmtArrIndex = dnAmtArrIndex;
	}

	public void setDnDatArrIndex(int dnDatArrIndex) {
		this.dnDatArrIndex = dnDatArrIndex;
	}

	public void setNumDnIndex(int numDnIndex) {
		this.numDnIndex = numDnIndex;
	}
	
	public void setNumDnLst12MnthsIndex(int numDnLst12MnthsIndex) {
		this.numDnLst12MnthsIndex = numDnLst12MnthsIndex;
	}

	public void setTtlDnAmtIndex(int ttlDnAmtIndex) {
		this.ttlDnAmtIndex = ttlDnAmtIndex;
	}
	
	public void setTtlDnAmtLst12MnthsIndex(int ttlDnAmtLst12MnthsIndex) {
		this.ttlDnAmtLst12MnthsIndex = ttlDnAmtLst12MnthsIndex;
	}
	
	public void setTtlDnAmtCrntYrIndex(int ttlDnAmtCrntYrIndex) {
		this.ttlDnAmtCrntYrIndex = ttlDnAmtCrntYrIndex;
	}
	
	public void setTtlDnAmtLstYrIndex(int ttlDnAmtLstYrIndex) {
		this.ttlDnAmtLstYrIndex = ttlDnAmtLstYrIndex;
	}
	
	public void setFstDnAmtIndex(int fstDnAmtIndex) {
		this.fstDnAmtIndex = fstDnAmtIndex;
	}

	public void setLstDnAmtIndex(int lstDnAmtIndex) {
		this.lstDnAmtIndex = lstDnAmtIndex;
	}

	public void setLrgDnAmtIndex(int lDnAmtIndex) {
		this.lrgDnAmtIndex = lDnAmtIndex;
	}

	public void setSmlDnAmtIndex(int smlDnAmtIndex) {
		this.smlDnAmtIndex = smlDnAmtIndex;
	}
	
	public void setLMDnAmtIndex(int lMDnAmtIndex) {
		this.lMDnAmtIndex = lMDnAmtIndex;
	}

	public void setFstDnDatIndex(int fstDnDatIndex) {
		this.fstDnDatIndex = fstDnDatIndex;
	}

	public void setLstDnDatIndex(int lstDnDatIndex) {
		this.lstDnDatIndex = lstDnDatIndex;
	}
	
	public void setLMDnDatIndex(int lMDnDatIndex) {
		this.lMDnDatIndex = lMDnDatIndex;
	}
	
	public void setPenultAmtIndex(int penultAmtIndex) {
		this.penultAmtIndex = penultAmtIndex;
	}
	
	public void setPenultDatIndex(int penultDatIndex) {
		this.penultDatIndex = penultDatIndex;
	}

	public void setDn1AmtIndex(int dn1AmtIndex) {
		this.dn1AmtIndex = dn1AmtIndex;
	}

	public void setDn2AmtIndex(int dn2AmtIndex) {
		this.dn2AmtIndex = dn2AmtIndex;
	}

	public void setDn3AmtIndex(int dn3AmtIndex) {
		this.dn3AmtIndex = dn3AmtIndex;
	}

	public void setDn4AmtIndex(int dn4AmtIndex) {
		this.dn4AmtIndex = dn4AmtIndex;
	}

	public void setODnAmtIndex(int oDnAmtIndex) {
		this.oDnAmtIndex = oDnAmtIndex;
	}

	public void setMDn1AmtIndex(int mDn1AmtIndex) {
		this.mDn1AmtIndex = mDn1AmtIndex;
	}

	public void setMDn2AmtIndex(int mDn2AmtIndex) {
		this.mDn2AmtIndex = mDn2AmtIndex;
	}

	public void setMDn3AmtIndex(int mDn3AmtIndex) {
		this.mDn3AmtIndex = mDn3AmtIndex;
	}

	public void setMDn4AmtIndex(int mDn4AmtIndex) {
		this.mDn4AmtIndex = mDn4AmtIndex;
	}

	public void setMODnAmtIndex(int mODnAmtIndex) {
		this.mODnAmtIndex = mODnAmtIndex;
	}

	public void setProvide1Index(int provide1Index) {
		this.provide1Index = provide1Index;
	}

	public void setProvide2Index(int provide2Index) {
		this.provide2Index = provide2Index;
	}

	public void setProvide3Index(int provide3Index) {
		this.provide3Index = provide3Index;
	}
	
	public void setProvide4Index(int provide4Index) {
		this.provide4Index = provide4Index;
	}

	public void setPriorityIndex(int priorityIndex) {
		this.priorityIndex = priorityIndex;
	}

	public void setABGroupIndex(int abGroupIndex) {
		this.abGroupIndex = abGroupIndex;
	}

	public void setAppealIndex(int appealIndex) {
		this.appealIndex = appealIndex;
	}

	public void setSegIndex(int segIndex) {
		this.segIndex = segIndex;
	}

	public void setSegCodeIndex(int segCodeIndex) {
		this.segCodeIndex = segCodeIndex;
	}

	public void setLetVerIndex(int letVerIndex) {
		this.letVerIndex = letVerIndex;
	}

	public void setPkgVerIndex(int pkgVerIndex) {
		this.pkgVerIndex = pkgVerIndex;
	}

	public void setRepVerIndex(int repVerIndex) {
		this.repVerIndex = repVerIndex;
	}

	public void setCodeLineIndex(int codeLineIndex) {
		this.codeLineIndex = codeLineIndex;
	}

	public void setMeanAmtIndex(int meanAmtIndex) {
		this.meanAmtIndex = meanAmtIndex;
	}
	
	public void setMedianIndex(int medianIndex) {
		this.medianIndex = medianIndex;
	}

	public void setSDevAmtIndex(int sDevAmtIndex) {
		this.sDevAmtIndex = sDevAmtIndex;
	}

	public void setRScoreIndex(int rScoreIndex) {
		this.rScoreIndex = rScoreIndex;
	}

	public void setFScoreIndex(int fScoreIndex) {
		this.fScoreIndex = fScoreIndex;
	}

	public void setMScoreIndex(int mScoreIndex) {
		this.mScoreIndex = mScoreIndex;
	}

	public void setRfmScoreIndex(int rfmScoreIndex) {
		this.rfmScoreIndex = rfmScoreIndex;
	}

	public void setLengthIndex(int lengthIndex) {
		this.lengthIndex = lengthIndex;
	}

	public void setWidthIndex(int widthIndex) {
		this.widthIndex = widthIndex;
	}

	public void setHeightIndex(int heightIndex) {
		this.heightIndex = heightIndex;
	}

	public void setWeightIndex(int weightIndex) {
		this.weightIndex = weightIndex;
	}

	public void add(Record record) {
		this.recordList.add(record);
	}

	public List<Record> getRecordList() {
		return recordList;
	}

	public void setRecordList(ArrayList<Record> recordList) {
		if (recordList != null)
			recordList.trimToSize();

		this.recordList = recordList;
	}

	public void setDfHeaders(String[] dfHeaders) {
		this.dfHeaders = dfHeaders;
	}

	public String[] getDfHeaders() {
		return dfHeaders;
	}

	/**
	 * Gets the exportData
	 * 
	 * @param recordList
	 *          the recordList to export data from
	 * @return exportData as a String[][]
	 * @throws Exception
	 */
	public String[][] getExportData(List<Record> recordList) throws Exception {
		String[][] exportData = new String[recordList.size()][inHeaders.length + dfHeaders.length];

		exportMaxFieldLength = new int[inHeaders.length + dfHeaders.length];
		Arrays.fill(exportMaxFieldLength, 1);

		for (int i = 0; i < recordList.size(); ++i) {
			
			Record record = recordList.get(i);

			int j;
			for (j = 0; j < recordList.get(0).getDfInData().length; ++j) { // loop through the original data
				if (record.getDfInData() != null)
					exportData[i][j] = record.getDfInData()[j]; // in case we add records and there is no original data
				else
					exportData[i][j] = "";

				//System.out.println(exportData[i][j]);

				if (exportData[i][j].length() > exportMaxFieldLength[j]) // get the max field length of in data
					exportMaxFieldLength[j] = exportData[i][j].length();
			}

			// Determines what field to print
			for (int k = 0; k < dfHeaders.length; k++) {
				if (getRecordFieldByName(dfHeaders[k], record) != null)
					exportData[i][j++] = getRecordFieldByName(dfHeaders[k], record);
				else
					throw new Exception(String.format("the %s field %s was not set for the active job", Dfuze.APP_NAME, dfHeaders[k]));

				if (exportData[i][j - 1].length() > exportMaxFieldLength[j - 1]) // get the max field length of df data
					exportMaxFieldLength[j - 1] = exportData[i][j - 1].length();

			}

		}

		return exportData;

	}

	/**
	 * Gets the data to display during deduplication
	 * 
	 * @param list
	 *          the list of Records to get data from
	 * @param dedupeFields
	 *          the extra fields to show duing deduplication
	 * @return exportData as a String[][]
	 */
	public String[][] getDedupeExportData(ArrayList<Record> list, String[] dedupeFields) {
		
		final int EXTRA_FIELDS_ADDED = 6;

		final int EXTRA_COLUMNS_NUM = 1 + dedupeFields.length;
		String[][] exportData = new String[list.size()][EXTRA_FIELDS_ADDED + dedupeFields.length];

		exportMaxFieldLength = new int[EXTRA_COLUMNS_NUM + inHeaders.length + dfHeaders.length];
		Arrays.fill(exportMaxFieldLength, 1);

		for (int i = 0; i < list.size(); ++i) {
			Record record = list.get(i);
			
			int j = 0;
			exportData[i][j] = Boolean.toString(list.get(i).getIsDupe());
			exportMaxFieldLength[j++] = 5;
			
			exportData[i][j] = Integer.toString(list.get(i).getDupeGroupId());
			exportMaxFieldLength[j] = exportData[i][j++].length();
			
			exportData[i][j] = Integer.toString(list.get(i).getDupeGroupSize());
			exportMaxFieldLength[j] = exportData[i][j++].length();

			exportData[i][j] = list.get(i).getAdd1();
			exportMaxFieldLength[j] = exportData[i][j++].length();

			exportData[i][j] = list.get(i).getAdd2();
			exportMaxFieldLength[j] = exportData[i][j++].length();

			exportData[i][j] = list.get(i).getPCode();
			exportMaxFieldLength[j] = exportData[i][j++].length();

			for (int k = 0; k < dedupeFields.length; ++k) {
				boolean dfFieldFound = false;
				for (String dfHeader : allDfHeaders) {
					if (dedupeFields[k].equals(dfHeader)) {
						dfFieldFound = true;
						exportData[i][j] = getRecordFieldByName(dedupeFields[k], record);
						exportMaxFieldLength[j] = exportData[i][j++].length();
					}
				}

				if (!dfFieldFound) {
					for (int l = 0; l < inHeaders.length; ++l) {
						if (dedupeFields[k].equals(inHeaders[l])) {
							if (list.get(i).getDfInData() != null) // in case we add seeds with null inData
								exportData[i][j] = list.get(i).getDfInData()[l];
							else
								exportData[i][j] = "";
							exportMaxFieldLength[j] = exportData[i][j++].length();
						}
					}
				}

			}

		}

		return exportData;

	}

	/**
	 * Gets the headers to display during deduplication
	 * 
	 * @param dedupeHeaders
	 *          the extra headers to display during deduplication
	 * @return
	 */
	public String[] getDedupeExportHeaders(String dedupeHeaders[]) {

		String[] headers = {
				UserData.fieldName.IS_DUPE.getName(),
				UserData.fieldName.DUPE_GROUP_ID.getName(),
				UserData.fieldName.DUPE_GROUP_SIZE.getName(),
				UserData.fieldName.ADDRESS1.getName(),
				UserData.fieldName.ADDRESS2.getName(),
				UserData.fieldName.POSTALCODE.getName()
		};

		List<String> exportDedupeHeaders = new ArrayList<>();

		for (int i = 0; i < headers.length; ++i) {
			exportDedupeHeaders.add(headers[i]);
		}

		for (int i = 0; i < dedupeHeaders.length; ++i) {
			exportDedupeHeaders.add(dedupeHeaders[i]);
		}

		return exportDedupeHeaders.toArray(new String[0]);

	}

	/**
	 * Gets a field value from a Record
	 * 
	 * @param fieldToGet
	 *          the fieldName to the header that corresponds to the value wanted
	 * @param record
	 *          the record to get the String value from
	 * @return the fields value as a String
	 */
	public static String getRecordFieldByName(String fieldToGet, Record record) {
		if (fieldToGet.equals(fieldName.DFID.getName()))
			return Integer.toString(record.getDfId());
		else if (fieldToGet.equals(fieldName.IN_ID.getName()))
			return record.getInId();
		else if (fieldToGet.equals(fieldName.IS_DUPE.getName()))
			return Boolean.toString(record.getIsDupe());
		else if (fieldToGet.equals(fieldName.SEGMENT_PLAN_FILTER_1.getName()))
			return record.getSpFilt1();
		else if (fieldToGet.equals(fieldName.SEGMENT_PLAN_FILTER_2.getName()))
			return record.getSpFilt2();
		else if (fieldToGet.equals(fieldName.DUPE_META_STREET_ADD1.getName()))
			return record.getDupeMetaStreetAdd1();
		else if (fieldToGet.equals(fieldName.DUPE_META_STREET_ADD2.getName()))
			return record.getDupeMetaStreetAdd2();
		else if (fieldToGet.equals(fieldName.DUPE_ADD1.getName()))
			return record.getDupeAdd1();
		else if (fieldToGet.equals(fieldName.DUPE_ADD2.getName()))
			return record.getDupeAdd2();
		else if (fieldToGet.equals(fieldName.DUPE_PCODE.getName()))
			return record.getDupePCode();
		else if (fieldToGet.equals(fieldName.DUPE_POBOX.getName()))
			return record.getDupePOBox();
		else if (fieldToGet.equals(fieldName.DUPE_POBOX_EXTRA.getName()))
			return record.getDupePOBoxExtra();
		else if (fieldToGet.equals(fieldName.DUPE_STREET_DIRECTION.getName()))
			return record.getDupeStreetDirection();
		else if (fieldToGet.equals(fieldName.DUPE_RR.getName()))
			return record.getDupeRR();
		else if (fieldToGet.equals(fieldName.DUPE_NAME1.getName()))
			return record.getDupeName1();
		else if (fieldToGet.equals(fieldName.DUPE_NAME1_FIRST_PERSON.getName()))
			return record.getDupeName1FirstPerson();
		else if (fieldToGet.equals(fieldName.DUPE_NAME1_SECOND_PERSON.getName()))
			return record.getDupeName1SecondPerson();
		else if (fieldToGet.equals(fieldName.DUPE_NAME1_NORMALIZED.getName()))
			return record.getDupeName1Normalized();
		else if (fieldToGet.equals(fieldName.DUPE_NAME2.getName()))
			return record.getDupeName2();
		else if (fieldToGet.equals(fieldName.DUPE_NAME2_FIRST_PERSON.getName()))
			return record.getDupeName2FirstPerson();
		else if (fieldToGet.equals(fieldName.DUPE_NAME2_SECOND_PERSON.getName()))
			return record.getDupeName2SecondPerson();
		else if (fieldToGet.equals(fieldName.DUPE_NAME2_NORMALIZED.getName()))
			return record.getDupeName2Normalized();
		else if (fieldToGet.equals(fieldName.DUPE_GROUP_ID.getName()))
			return Integer.toString(record.getDupeGroupId());
		else if (fieldToGet.equals(fieldName.DUPE_GROUP_SIZE.getName()))
			return Integer.toString(record.getDupeGroupSize());
		else if (fieldToGet.equals(fieldName.LANGUAGE.getName()))
			return record.getLang();
		else if (fieldToGet.equals(fieldName.BARCODE.getName()))
			return record.getBarCode();
		else if (fieldToGet.equals(fieldName.RECORD_TYPE.getName()))
			return record.getRecType();
		else if (fieldToGet.equals(fieldName.STATUS.getName()))
			return record.getStatus();
		else if (fieldToGet.equals(fieldName.LIST_NUMBER.getName()))
			return record.getListNum();
		else if (fieldToGet.equals(fieldName.DAY.getName()))
			return record.getDay();
		else if (fieldToGet.equals(fieldName.MONTH.getName()))
			return record.getMonth();
		else if (fieldToGet.equals(fieldName.YEAR.getName()))
			return record.getYear();
		else if (fieldToGet.equals(fieldName.LATITUDE.getName()))
			return record.getLatitude();
		else if (fieldToGet.equals(fieldName.LONGITUDE.getName()))
			return record.getLongitude();
		else if (fieldToGet.equals(fieldName.QUANTITY.getName()))
			return record.getQuantity();
		else if (fieldToGet.equals(fieldName.PHONE1.getName()))
			return record.getPhone1();
		else if (fieldToGet.equals(fieldName.PHONE2.getName()))
			return record.getPhone2();
		else if (fieldToGet.equals(fieldName.MOBILE_PHONE.getName()))
			return record.getMobilePhone();
		else if (fieldToGet.equals(fieldName.EMAIL.getName()))
			return record.getEmail();
		else if (fieldToGet.equals(fieldName.STORE_ID.getName()))
			return record.getStoreId();
		else if (fieldToGet.equals(fieldName.PREFIX.getName()))
			return record.getPrefix();
		else if (fieldToGet.equals(fieldName.SUFFIX.getName()))
			return record.getSuffix();
		else if (fieldToGet.equals(fieldName.FIRSTNAME.getName()))
			return record.getFstName();
		else if (fieldToGet.equals(fieldName.MIDDLENAME.getName()))
			return record.getMidName();
		else if (fieldToGet.equals(fieldName.LASTNAME.getName()))
			return record.getLstName();
		else if (fieldToGet.equals(fieldName.NAME1.getName()))
			return record.getNam1();
		else if (fieldToGet.equals(fieldName.NAME1_2.getName()))
			return record.getNam1_2();
		else if (fieldToGet.equals(fieldName.NAME2.getName()))
			return record.getNam2();
		else if (fieldToGet.equals(fieldName.SPOUSE_PREFIX.getName()))
			return record.getSpousePrefix();
		else if (fieldToGet.equals(fieldName.SPOUSE_SUFFIX.getName()))
			return record.getSpouseSuffix();
		else if (fieldToGet.equals(fieldName.SPOUSE_FIRSTNAME.getName()))
			return record.getSpouseFstName();
		else if (fieldToGet.equals(fieldName.SPOUSE_MIDDLENAME.getName()))
			return record.getSpouseMidName();
		else if (fieldToGet.equals(fieldName.SPOUSE_LASTNAME.getName()))
			return record.getSpouseLstName();
		else if (fieldToGet.equals(fieldName.SPOUSE_NAME1.getName()))
			return record.getSpouseNam1();
		else if (fieldToGet.equals(fieldName.SPOUSE_NAME2.getName()))
			return record.getSpouseNam2();
		else if (fieldToGet.equals(fieldName.NAME2_2.getName()))
			return record.getNam2_2();
		else if (fieldToGet.equals(fieldName.COMPANY.getName()))
			return record.getCmpny();
		else if (fieldToGet.equals(fieldName.COMPANY_2.getName()))
			return record.getCmpny_2();
		else if (fieldToGet.equals(fieldName.COMPANY_CONTACT.getName()))
			return record.getCmpnyCn();
		else if (fieldToGet.equals(fieldName.DEAR_SALUTATION.getName()))
			return record.getDearSal();
		else if (fieldToGet.equals(fieldName.PARAGRAPH_SALUTATION.getName()))
			return record.getParaSal();
		else if (fieldToGet.equals(fieldName.COMPANY_SALUTATION.getName()))
			return record.getCmpnySal();
		else if (fieldToGet.equals(fieldName.COMPANY_ADDRESS1.getName()))
			return record.getCmpnyAdd1();
		else if (fieldToGet.equals(fieldName.COMPANY_ADDRESS2.getName()))
			return record.getCmpnyAdd2();
		else if (fieldToGet.equals(fieldName.ADDRESS1.getName()))
			return record.getAdd1();
		else if (fieldToGet.equals(fieldName.ADDRESS1_2.getName()))
			return record.getAdd1_2();
		else if (fieldToGet.equals(fieldName.ADDRESS2.getName()))
			return record.getAdd2();
		else if (fieldToGet.equals(fieldName.ADDRESS2_2.getName()))
			return record.getAdd2_2();
		else if (fieldToGet.equals(fieldName.CITY.getName()))
			return record.getCity();
		else if (fieldToGet.equals(fieldName.PROVINCE.getName()))
			return record.getProv();
		else if (fieldToGet.equals(fieldName.POSTALCODE.getName()))
			return record.getPCode();
		else if (fieldToGet.equals(fieldName.COUNTRY.getName()))
			return record.getCntry();
		else if (fieldToGet.equals(fieldName.NCOA.getName()))
			return record.getNcoa();
		else if (fieldToGet.equals(fieldName.DNM.getName()))
			return record.getDnm();
		else if (fieldToGet.equals(fieldName.DECEASED.getName()))
			return record.getDeceased();
		else if (fieldToGet.equals(fieldName.DONATION_AMOUNT_ARRAY.getName()))
			return record.getDnAmtArr();
		else if (fieldToGet.equals(fieldName.DONATION_DATE_ARRAY.getName()))
			return record.getDnDatArr();
		else if (fieldToGet.equals(fieldName.NUMBER_OF_DONATIONS.getName()))
			return record.getNumDn();
		else if (fieldToGet.equals(fieldName.NUMBER_OF_DONATIONS_LAST_12_MONTHS.getName()))
			return record.getNumDnLst12Mnths();
		else if (fieldToGet.equals(fieldName.TOTAL_DONATION_AMOUNT.getName()))
			return record.getTtlDnAmt();
		else if (fieldToGet.equals(fieldName.TOTAL_DONATION_AMOUNT_LAST_12_MONTHS.getName()))
			return record.getTtlDnAmtLst12Mnths();
		else if (fieldToGet.equals(fieldName.TOTAL_DONATION_AMOUNT_CURRENT_YEAR.getName()))
			return record.getTtlDnAmtCrntYr();
		else if (fieldToGet.equals(fieldName.TOTAL_DONATION_AMOUNT_LAST_YEAR.getName()))
			return record.getTtlDnAmtLstYr();
		else if (fieldToGet.equals(fieldName.FIRST_DONATION_AMOUNT.getName()))
			return record.getFstDnAmt();
		else if (fieldToGet.equals(fieldName.LAST_DONATION_AMOUNT.getName()))
			return record.getLstDnAmt();
		else if (fieldToGet.equals(fieldName.LARGEST_DONATION_AMOUNT.getName()))
			return record.getLrgDnAmt();
		else if (fieldToGet.equals(fieldName.SMALLEST_DONATION_AMOUNT.getName()))
			return record.getSDnAmt();
		else if (fieldToGet.equals(fieldName.LAST_MONTHLY_DONATION_AMOUNT.getName()))
			return record.getLMDnAmt();
		else if (fieldToGet.equals(fieldName.FIRST_DONATION_DATE.getName()))
			return record.getFstDnDat();
		else if (fieldToGet.equals(fieldName.LAST_DONATION_DATE.getName()))
			return record.getLstDnDat();
		else if (fieldToGet.equals(fieldName.LAST_MONTHLY_DONATION_DATE.getName()))
			return record.getLMDnDat();
		else if (fieldToGet.equals(fieldName.PENULTIMATE_AMOUNT.getName()))
			return record.getPenultAmt();
		else if (fieldToGet.equals(fieldName.PENULTIMATE_DATE.getName()))
			return record.getPenultDat();
		else if (fieldToGet.equals(fieldName.DONATION1_AMOUNT.getName()))
			return record.getDn1Amt();
		else if (fieldToGet.equals(fieldName.DONATION2_AMOUNT.getName()))
			return record.getDn2Amt();
		else if (fieldToGet.equals(fieldName.DONATION3_AMOUNT.getName()))
			return record.getDn3Amt();
		else if (fieldToGet.equals(fieldName.DONATION4_AMOUNT.getName()))
			return record.getDn4Amt();
		else if (fieldToGet.equals(fieldName.OPEN_DONATION_AMOUNT.getName()))
			return record.getODnAmt();
		else if (fieldToGet.equals(fieldName.MONTHLY_DONATION1_AMOUNT.getName()))
			return record.getMDn1Amt();
		else if (fieldToGet.equals(fieldName.MONTHLY_DONATION2_AMOUNT.getName()))
			return record.getMDn2Amt();
		else if (fieldToGet.equals(fieldName.MONTHLY_DONATION3_AMOUNT.getName()))
			return record.getMDn3Amt();
		else if (fieldToGet.equals(fieldName.MONTHLY_DONATION4_AMOUNT.getName()))
			return record.getMDn4Amt();
		else if (fieldToGet.equals(fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName()))
			return record.getMODnAmt();
		else if (fieldToGet.equals(fieldName.PROVIDE1.getName()))
			return record.getProvide1();
		else if (fieldToGet.equals(fieldName.PROVIDE2.getName()))
			return record.getProvide2();
		else if (fieldToGet.equals(fieldName.PROVIDE3.getName()))
			return record.getProvide3();
		else if (fieldToGet.equals(fieldName.PROVIDE4.getName()))
			return record.getProvide4();
		else if (fieldToGet.equals(fieldName.PRIORITY.getName()))
			return record.getPriority();
		else if (fieldToGet.equals(fieldName.AB_GROUP.getName()))
			return record.getABGroup();
		else if (fieldToGet.equals(fieldName.APPEAL.getName()))
			return record.getAppeal();
		else if (fieldToGet.equals(fieldName.SEGMENT.getName()))
			return record.getSeg();
		else if (fieldToGet.equals(fieldName.SEGMENT_CODE.getName()))
			return record.getSegCode();
		else if (fieldToGet.equals(fieldName.LETTER_VERSION.getName()))
			return record.getLetVer();
		else if (fieldToGet.equals(fieldName.PACKAGE_VERSION.getName()))
			return record.getPkgVer();
		else if (fieldToGet.equals(fieldName.REPLY_VERSION.getName()))
			return record.getRepVer();
		else if (fieldToGet.equals(fieldName.CODELINE.getName()))
			return record.getCodeLine();
		else if (fieldToGet.equals(fieldName.MEAN_AMOUNT.getName()))
			return record.getMeanAmt();
		else if (fieldToGet.equals(fieldName.MEDIAN.getName()))
			return record.getMedian();
		else if (fieldToGet.equals(fieldName.STANDARD_DEVIATION_AMOUNT.getName()))
			return record.getSDevAmt();
		else if (fieldToGet.equals(fieldName.RECENCY.getName()))
			return record.getRScore();
		else if (fieldToGet.equals(fieldName.FREQUENCY.getName()))
			return record.getFScore();
		else if (fieldToGet.equals(fieldName.MONETARY.getName()))
			return record.getMScore();
		else if (fieldToGet.equals(fieldName.RFM.getName()))
			return record.getRfmScore();
		else if (fieldToGet.equals(fieldName.LENGTH.getName()))
			return record.getLength();
		else if (fieldToGet.equals(fieldName.WIDTH.getName()))
			return record.getWidth();
		else if (fieldToGet.equals(fieldName.HEIGHT.getName()))
			return record.getHeight();
		else if (fieldToGet.equals(fieldName.WEIGHT.getName()))
			return record.getWeight();
		else
			return null;
	}

	/**
	 * Sets a field value from a Record
	 * 
	 * @param fieldToSet
	 *          the fieldName to the header that corresponds to the value to set
	 * @param record
	 *          the record to get the String value from
	 */
	public static void setRecordFieldByName(String fieldToSet, String valueToSet, Record record) {
		if (fieldToSet.equals(fieldName.DFID.getName()))
			record.setDfId(Integer.parseInt(valueToSet));
		else if (fieldToSet.equals(fieldName.IN_ID.getName()))
			record.setInId(valueToSet);
		else if (fieldToSet.equals(fieldName.IS_DUPE.getName()))
			record.setIsDupe(Boolean.parseBoolean(valueToSet));
		else if (fieldToSet.equals(fieldName.SEGMENT_PLAN_FILTER_1.getName()))
			record.setSpFilt1(valueToSet);
		else if (fieldToSet.equals(fieldName.SEGMENT_PLAN_FILTER_2.getName()))
			record.setSpFilt2(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_META_STREET_ADD1.getName()))
			record.setDupeMetaStreetAdd1(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_META_STREET_ADD2.getName()))
			record.setDupeMetaStreetAdd2(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_ADD1.getName()))
			record.setDupeAdd1(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_ADD2.getName()))
			record.setDupeAdd2(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_PCODE.getName()))
			record.setDupePCode(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_POBOX.getName()))
			record.setDupePOBox(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_POBOX_EXTRA.getName()))
			record.setDupePOBoxExtra(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_STREET_DIRECTION.getName()))
			record.setDupeStreetDirection(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_RR.getName()))
			record.setDupeRR(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_NAME1.getName()))
			record.setDupeName1(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_NAME1_FIRST_PERSON.getName()))
			record.setDupeName1FirstPerson(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_NAME1_SECOND_PERSON.getName()))
			record.setDupeName1SecondPerson(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_NAME1_NORMALIZED.getName()))
			record.setDupeName1Normalized(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_NAME2.getName()))
			record.setDupeName2(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_NAME2_FIRST_PERSON.getName()))
			record.setDupeName2FirstPerson(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_NAME2_SECOND_PERSON.getName()))
			record.setDupeName2SecondPerson(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_NAME2_NORMALIZED.getName()))
			record.setDupeName2Normalized(valueToSet);
		else if (fieldToSet.equals(fieldName.DUPE_GROUP_ID.getName()))
			record.setDupeGroupId(Integer.parseInt(valueToSet));
		else if (fieldToSet.equals(fieldName.DUPE_GROUP_SIZE.getName()))
			record.setDupeGroupSize(Integer.parseInt(valueToSet));
		else if (fieldToSet.equals(fieldName.LANGUAGE.getName()))
			record.setLang(valueToSet);
		else if (fieldToSet.equals(fieldName.BARCODE.getName()))
			record.setBarCode(valueToSet);
		else if (fieldToSet.equals(fieldName.RECORD_TYPE.getName()))
			record.setRecType(valueToSet);
		else if (fieldToSet.equals(fieldName.STATUS.getName()))
			record.setStatus(valueToSet);
		else if (fieldToSet.equals(fieldName.LIST_NUMBER.getName()))
			record.setListNum(valueToSet);
		else if (fieldToSet.equals(fieldName.DAY.getName()))
			record.setDay(valueToSet);
		else if (fieldToSet.equals(fieldName.MONTH.getName()))
			record.setMonth(valueToSet);
		else if (fieldToSet.equals(fieldName.YEAR.getName()))
			record.setYear(valueToSet);
		else if (fieldToSet.equals(fieldName.LATITUDE.getName()))
			record.setLatitude(valueToSet);
		else if (fieldToSet.equals(fieldName.LONGITUDE.getName()))
			record.setLongitude(valueToSet);
		else if (fieldToSet.equals(fieldName.QUANTITY.getName()))
			record.setQuantity(valueToSet);
		else if (fieldToSet.equals(fieldName.PHONE1.getName()))
			record.setPhone1(valueToSet);
		else if (fieldToSet.equals(fieldName.PHONE2.getName()))
			record.setPhone2(valueToSet);
		else if (fieldToSet.equals(fieldName.MOBILE_PHONE.getName()))
			record.setMobilePhone(valueToSet);
		else if (fieldToSet.equals(fieldName.EMAIL.getName()))
			record.setEmail(valueToSet);
		else if (fieldToSet.equals(fieldName.STORE_ID.getName()))
			record.setStoreId(valueToSet);
		else if (fieldToSet.equals(fieldName.PREFIX.getName()))
			record.setPrefix(valueToSet);
		else if (fieldToSet.equals(fieldName.SUFFIX.getName()))
			record.setSuffix(valueToSet);
		else if (fieldToSet.equals(fieldName.FIRSTNAME.getName()))
			record.setFstName(valueToSet);
		else if (fieldToSet.equals(fieldName.MIDDLENAME.getName()))
			record.setMidName(valueToSet);
		else if (fieldToSet.equals(fieldName.LASTNAME.getName()))
			record.setLstName(valueToSet);
		else if (fieldToSet.equals(fieldName.NAME1.getName()))
			record.setNam1(valueToSet);
		else if (fieldToSet.equals(fieldName.NAME1_2.getName()))
			record.setNam1_2(valueToSet);
		else if (fieldToSet.equals(fieldName.NAME2.getName()))
			record.setNam2(valueToSet);
		else if (fieldToSet.equals(fieldName.NAME2_2.getName()))
			record.setNam2_2(valueToSet);
		else if (fieldToSet.equals(fieldName.SPOUSE_PREFIX.getName()))
			record.setSpousePrefix(valueToSet);
		else if (fieldToSet.equals(fieldName.SPOUSE_SUFFIX.getName()))
			record.setSpouseSuffix(valueToSet);
		else if (fieldToSet.equals(fieldName.SPOUSE_FIRSTNAME.getName()))
			record.setSpouseFstName(valueToSet);
		else if (fieldToSet.equals(fieldName.SPOUSE_MIDDLENAME.getName()))
			record.setSpouseMidName(valueToSet);
		else if (fieldToSet.equals(fieldName.SPOUSE_LASTNAME.getName()))
			record.setSpouseLstName(valueToSet);
		else if (fieldToSet.equals(fieldName.SPOUSE_NAME1.getName()))
			record.setSpouseNam1(valueToSet);
		else if (fieldToSet.equals(fieldName.SPOUSE_NAME2.getName()))
			record.setSpouseNam2(valueToSet);
		else if (fieldToSet.equals(fieldName.COMPANY.getName()))
			record.setCmpny(valueToSet);
		else if (fieldToSet.equals(fieldName.COMPANY_2.getName()))
			record.setCmpny_2(valueToSet);
		else if (fieldToSet.equals(fieldName.COMPANY_CONTACT.getName()))
			record.setCmpnyCn(valueToSet);
		else if (fieldToSet.equals(fieldName.DEAR_SALUTATION.getName()))
			record.setDearSal(valueToSet);
		else if (fieldToSet.equals(fieldName.PARAGRAPH_SALUTATION.getName()))
			record.setParaSal(valueToSet);
		else if (fieldToSet.equals(fieldName.COMPANY_SALUTATION.getName()))
			record.setCmpnySal(valueToSet);
		else if (fieldToSet.equals(fieldName.COMPANY_ADDRESS1.getName()))
			record.setCmpnyAdd1(valueToSet);
		else if (fieldToSet.equals(fieldName.COMPANY_ADDRESS2.getName()))
			record.setCmpnyAdd2(valueToSet);
		else if (fieldToSet.equals(fieldName.ADDRESS1.getName()))
			record.setAdd1(valueToSet);
		else if (fieldToSet.equals(fieldName.ADDRESS1_2.getName()))
			record.setAdd1_2(valueToSet);
		else if (fieldToSet.equals(fieldName.ADDRESS2.getName()))
			record.setAdd2(valueToSet);
		else if (fieldToSet.equals(fieldName.ADDRESS2_2.getName()))
			record.setAdd2_2(valueToSet);
		else if (fieldToSet.equals(fieldName.CITY.getName()))
			record.setCity(valueToSet);
		else if (fieldToSet.equals(fieldName.PROVINCE.getName()))
			record.setProv(valueToSet);
		else if (fieldToSet.equals(fieldName.POSTALCODE.getName()))
			record.setPCode(valueToSet);
		else if (fieldToSet.equals(fieldName.COUNTRY.getName()))
			record.setCntry(valueToSet);
		else if (fieldToSet.equals(fieldName.NCOA.getName()))
			record.setNcoa(valueToSet);
		else if (fieldToSet.equals(fieldName.DNM.getName()))
			record.setDnm(valueToSet);
		else if (fieldToSet.equals(fieldName.DECEASED.getName()))
			record.setDeceased(valueToSet);
		else if (fieldToSet.equals(fieldName.DONATION_AMOUNT_ARRAY.getName()))
			record.setDnAmtArr(valueToSet);
		else if (fieldToSet.equals(fieldName.DONATION_DATE_ARRAY.getName()))
			record.setDnDatArr(valueToSet);
		else if (fieldToSet.equals(fieldName.NUMBER_OF_DONATIONS.getName()))
			record.setNumDn(valueToSet);
		else if (fieldToSet.equals(fieldName.NUMBER_OF_DONATIONS_LAST_12_MONTHS.getName()))
			record.setNumDnLst12Mnths(valueToSet);
		else if (fieldToSet.equals(fieldName.TOTAL_DONATION_AMOUNT.getName()))
			record.setTtlDnAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.TOTAL_DONATION_AMOUNT_LAST_12_MONTHS.getName()))
			record.setTtlDnAmtLst12Mnths(valueToSet);
		else if (fieldToSet.equals(fieldName.TOTAL_DONATION_AMOUNT_CURRENT_YEAR.getName()))
			record.setTtlDnAmtCrntYr(valueToSet);
		else if (fieldToSet.equals(fieldName.TOTAL_DONATION_AMOUNT_LAST_YEAR.getName()))
			record.setTtlDnAmtLstYr(valueToSet);
		else if (fieldToSet.equals(fieldName.FIRST_DONATION_AMOUNT.getName()))
			record.setFstDnAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.LAST_DONATION_AMOUNT.getName()))
			record.setLstDnAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.LARGEST_DONATION_AMOUNT.getName()))
			record.setLrgDnAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.SMALLEST_DONATION_AMOUNT.getName()))
			record.setSDnAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.LAST_MONTHLY_DONATION_AMOUNT.getName()))
			record.setLMDnAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.FIRST_DONATION_DATE.getName()))
			record.setFstDnDat(valueToSet);
		else if (fieldToSet.equals(fieldName.LAST_DONATION_DATE.getName()))
			record.setLstDnDat(valueToSet);
		else if (fieldToSet.equals(fieldName.LAST_MONTHLY_DONATION_DATE.getName()))
			record.setLMDnDat(valueToSet);
		else if (fieldToSet.equals(fieldName.PENULTIMATE_AMOUNT.getName()))
			record.setPenultAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.PENULTIMATE_DATE.getName()))
			record.setPenultDat(valueToSet);
		else if (fieldToSet.equals(fieldName.DONATION1_AMOUNT.getName()))
			record.setDn1Amt(valueToSet);
		else if (fieldToSet.equals(fieldName.DONATION2_AMOUNT.getName()))
			record.setDn2Amt(valueToSet);
		else if (fieldToSet.equals(fieldName.DONATION3_AMOUNT.getName()))
			record.setDn3Amt(valueToSet);
		else if (fieldToSet.equals(fieldName.DONATION4_AMOUNT.getName()))
			record.setDn4Amt(valueToSet);
		else if (fieldToSet.equals(fieldName.OPEN_DONATION_AMOUNT.getName()))
			record.setODnAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.MONTHLY_DONATION1_AMOUNT.getName()))
			record.setMDn1Amt(valueToSet);
		else if (fieldToSet.equals(fieldName.MONTHLY_DONATION2_AMOUNT.getName()))
			record.setMDn2Amt(valueToSet);
		else if (fieldToSet.equals(fieldName.MONTHLY_DONATION3_AMOUNT.getName()))
			record.setMDn3Amt(valueToSet);
		else if (fieldToSet.equals(fieldName.MONTHLY_DONATION4_AMOUNT.getName()))
			record.setMDn4Amt(valueToSet);
		else if (fieldToSet.equals(fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName()))
			record.setMODnAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.PROVIDE1.getName()))
			record.setProvide1(valueToSet);
		else if (fieldToSet.equals(fieldName.PROVIDE2.getName()))
			record.setProvide2(valueToSet);
		else if (fieldToSet.equals(fieldName.PROVIDE3.getName()))
			record.setProvide3(valueToSet);
		else if (fieldToSet.equals(fieldName.PROVIDE4.getName()))
			record.setProvide4(valueToSet);
		else if (fieldToSet.equals(fieldName.PRIORITY.getName()))
			record.setPriority(valueToSet);
		else if (fieldToSet.equals(fieldName.AB_GROUP.getName()))
			record.setABGroup(valueToSet);
		else if (fieldToSet.equals(fieldName.APPEAL.getName()))
			record.setAppeal(valueToSet);
		else if (fieldToSet.equals(fieldName.SEGMENT.getName()))
			record.setSeg(valueToSet);
		else if (fieldToSet.equals(fieldName.SEGMENT_CODE.getName()))
			record.setSegCode(valueToSet);
		else if (fieldToSet.equals(fieldName.LETTER_VERSION.getName()))
			record.setLetVer(valueToSet);
		else if (fieldToSet.equals(fieldName.PACKAGE_VERSION.getName()))
			record.setPkgVer(valueToSet);
		else if (fieldToSet.equals(fieldName.REPLY_VERSION.getName()))
			record.setRepVer(valueToSet);
		else if (fieldToSet.equals(fieldName.CODELINE.getName()))
			record.setCodeLine(valueToSet);
		else if (fieldToSet.equals(fieldName.MEAN_AMOUNT.getName()))
			record.setMeanAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.MEDIAN.getName()))
			record.setMedian(valueToSet);
		else if (fieldToSet.equals(fieldName.STANDARD_DEVIATION_AMOUNT.getName()))
			record.setSDevAmt(valueToSet);
		else if (fieldToSet.equals(fieldName.RECENCY.getName()))
			record.setRScore(valueToSet);
		else if (fieldToSet.equals(fieldName.FREQUENCY.getName()))
			record.setFScore(valueToSet);
		else if (fieldToSet.equals(fieldName.MONETARY.getName()))
			record.setMScore(valueToSet);
		else if (fieldToSet.equals(fieldName.RFM.getName()))
			record.setRfmScore(valueToSet);
		else if (fieldToSet.equals(fieldName.LENGTH.getName()))
			record.setLength(valueToSet);
		else if (fieldToSet.equals(fieldName.WIDTH.getName()))
			record.setWidth(valueToSet);
		else if (fieldToSet.equals(fieldName.HEIGHT.getName()))
			record.setHeight(valueToSet);
		else if (fieldToSet.equals(fieldName.WEIGHT.getName()))
			record.setWeight(valueToSet);

	}
	
	/**
	 * Sets a field value for a Record
	 * 
	 * @param fieldToSet
	 *          the fieldName to the header that corresponds to the value to set
	 * @param record
	 *          the record to set a value for
	 */
	public void setDfFieldFromInData(String fieldToSet, Record record) {
		String[] inData = record.getDfInData();
		//if (fieldToSet.equals(fieldName.DFID.getName()))
			//record.setDfId(Integer.parseInt(inData[dfIdIndex]));
		//else if (fieldToSet.equals(fieldName.IS_DUPE.getName()))
		//	record.setIsDupe(Boolean.parseBoolean(valueToSet));
		if (fieldToSet.equals(fieldName.IN_ID.getName()))
			record.setInId(inData[inIdIndex]);
		else if (fieldToSet.equals(fieldName.SEGMENT_PLAN_FILTER_1.getName()))
			record.setSpFilt1(inData[spFilt1Index]);
		else if (fieldToSet.equals(fieldName.SEGMENT_PLAN_FILTER_2.getName()))
			record.setSpFilt2(inData[spFilt2Index]);
		else if (fieldToSet.equals(fieldName.DUPE_META_STREET_ADD1.getName()))
			record.setDupeMetaStreetAdd1(inData[dupeStreetAdd1Index]);
		else if (fieldToSet.equals(fieldName.DUPE_META_STREET_ADD2.getName()))
			record.setDupeMetaStreetAdd2(inData[dupeStreetAdd2Index]);
		else if (fieldToSet.equals(fieldName.DUPE_ADD1.getName()))
			record.setDupeAdd1(inData[dupeAdd1Index]);
		else if (fieldToSet.equals(fieldName.DUPE_ADD2.getName()))
			record.setDupeAdd2(inData[dupeAdd2Index]);
		else if (fieldToSet.equals(fieldName.DUPE_PCODE.getName()))
			record.setDupePCode(inData[dupePCodeIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_POBOX.getName()))
			record.setDupePOBox(inData[dupePOBoxIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_POBOX_EXTRA.getName()))
			record.setDupePOBoxExtra(inData[dupePOBoxExtraIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_STREET_DIRECTION.getName()))
			record.setDupeStreetDirection(inData[dupeStreetDirectionIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_RR.getName()))
			record.setDupeRR(inData[dupeRRIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_NAME1.getName()))
			record.setDupeName1(inData[dupeName1Index]);
		else if (fieldToSet.equals(fieldName.DUPE_NAME1_FIRST_PERSON.getName()))
			record.setDupeName1FirstPerson(inData[dupeName1FirstPersonIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_NAME1_SECOND_PERSON.getName()))
			record.setDupeName1SecondPerson(inData[dupeName1SecondPersonIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_NAME1_NORMALIZED.getName()))
			record.setDupeName1Normalized(inData[dupeName1NormalizedIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_NAME2.getName()))
			record.setDupeName2(inData[dupeName2Index]);
		else if (fieldToSet.equals(fieldName.DUPE_NAME2_FIRST_PERSON.getName()))
			record.setDupeName2FirstPerson(inData[dupeName2FirstPersonIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_NAME2_SECOND_PERSON.getName()))
			record.setDupeName2SecondPerson(inData[dupeName2SecondPersonIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_NAME2_NORMALIZED.getName()))
			record.setDupeName2Normalized(inData[dupeName2NormalizedIndex]);
		else if (fieldToSet.equals(fieldName.DUPE_GROUP_ID.getName()))
			record.setDupeGroupId(Integer.parseInt(inData[dupeGroupIdIndex]));
		else if (fieldToSet.equals(fieldName.DUPE_GROUP_SIZE.getName()))
			record.setDupeGroupSize(Integer.parseInt(inData[dupeGroupSizeIndex]));
		else if (fieldToSet.equals(fieldName.LANGUAGE.getName()))
			record.setLang(inData[langIndex]);
		else if (fieldToSet.equals(fieldName.BARCODE.getName()))
			record.setBarCode(inData[barCodeIndex]);
		else if (fieldToSet.equals(fieldName.RECORD_TYPE.getName()))
			record.setRecType(inData[recTypeIndex]);
		else if (fieldToSet.equals(fieldName.STATUS.getName()))
			record.setStatus(inData[statusIndex]);
		else if (fieldToSet.equals(fieldName.LIST_NUMBER.getName()))
			record.setListNum(inData[listNumIndex]);
		else if (fieldToSet.equals(fieldName.DAY.getName()))
			record.setDay(inData[dayIndex]);
		else if (fieldToSet.equals(fieldName.MONTH.getName()))
			record.setMonth(inData[monthIndex]);
		else if (fieldToSet.equals(fieldName.YEAR.getName()))
			record.setYear(inData[yearIndex]);
		else if (fieldToSet.equals(fieldName.LATITUDE.getName()))
			record.setLatitude(inData[latitudeIndex]);
		else if (fieldToSet.equals(fieldName.LONGITUDE.getName()))
			record.setLongitude(inData[longitudeIndex]);
		else if (fieldToSet.equals(fieldName.QUANTITY.getName()))
			record.setQuantity(inData[quantityIndex]);
		else if (fieldToSet.equals(fieldName.PHONE1.getName()))
			record.setPhone1(inData[phone1Index]);
		else if (fieldToSet.equals(fieldName.PHONE2.getName()))
			record.setPhone2(inData[phone2Index]);
		else if (fieldToSet.equals(fieldName.MOBILE_PHONE.getName()))
			record.setMobilePhone(inData[mobilePhoneIndex]);
		else if (fieldToSet.equals(fieldName.EMAIL.getName()))
			record.setEmail(inData[emailIndex]);
		else if (fieldToSet.equals(fieldName.STORE_ID.getName()))
			record.setStoreId(inData[storeIdIndex]);
		else if (fieldToSet.equals(fieldName.PREFIX.getName()))
			record.setPrefix(inData[prefixIndex]);
		else if (fieldToSet.equals(fieldName.SUFFIX.getName()))
			record.setSuffix(inData[suffixIndex]);
		else if (fieldToSet.equals(fieldName.FIRSTNAME.getName()))
			record.setFstName(inData[fstNameIndex]);
		else if (fieldToSet.equals(fieldName.MIDDLENAME.getName()))
			record.setMidName(inData[midNameIndex]);
		else if (fieldToSet.equals(fieldName.LASTNAME.getName()))
			record.setLstName(inData[lstNameIndex]);
		else if (fieldToSet.equals(fieldName.NAME1.getName()))
			record.setNam1(inData[nam1Index]);
		else if (fieldToSet.equals(fieldName.NAME1_2.getName()))
			record.setNam1_2(inData[nam1_2Index]);
		else if (fieldToSet.equals(fieldName.NAME2.getName()))
			record.setNam2(inData[nam2Index]);
		else if (fieldToSet.equals(fieldName.NAME2_2.getName()))
			record.setNam2_2(inData[nam2_2Index]);
		else if (fieldToSet.equals(fieldName.SPOUSE_PREFIX.getName()))
			record.setSpousePrefix(inData[spousePrefixIndex]);
		else if (fieldToSet.equals(fieldName.SPOUSE_SUFFIX.getName()))
			record.setSpouseSuffix(inData[spouseSuffixIndex]);
		else if (fieldToSet.equals(fieldName.SPOUSE_FIRSTNAME.getName()))
			record.setSpouseFstName(inData[spouseFstNameIndex]);
		else if (fieldToSet.equals(fieldName.SPOUSE_MIDDLENAME.getName()))
			record.setSpouseMidName(inData[spouseMidNameIndex]);
		else if (fieldToSet.equals(fieldName.SPOUSE_LASTNAME.getName()))
			record.setSpouseLstName(inData[spouseLstNameIndex]);
		else if (fieldToSet.equals(fieldName.SPOUSE_NAME1.getName()))
			record.setSpouseNam1(inData[spouseNam1Index]);
		else if (fieldToSet.equals(fieldName.SPOUSE_NAME2.getName()))
			record.setSpouseNam2(inData[spouseNam2Index]);
		else if (fieldToSet.equals(fieldName.COMPANY.getName()))
			record.setCmpny(inData[cmpnyIndex]);
		else if (fieldToSet.equals(fieldName.COMPANY_2.getName()))
			record.setCmpny_2(inData[cmpny_2Index]);
		else if (fieldToSet.equals(fieldName.COMPANY_CONTACT.getName()))
			record.setCmpnyCn(inData[cmpnyCnIndex]);
		else if (fieldToSet.equals(fieldName.DEAR_SALUTATION.getName()))
			record.setDearSal(inData[dearSalIndex]);
		else if (fieldToSet.equals(fieldName.PARAGRAPH_SALUTATION.getName()))
			record.setParaSal(inData[paraSalIndex]);
		else if (fieldToSet.equals(fieldName.COMPANY_SALUTATION.getName()))
			record.setCmpnySal(inData[cmpnySalIndex]);
		else if (fieldToSet.equals(fieldName.COMPANY_ADDRESS1.getName()))
			record.setAdd1(inData[cmpnyAdd1Index]);
		else if (fieldToSet.equals(fieldName.COMPANY_ADDRESS2.getName()))
			record.setAdd1(inData[cmpnyAdd2Index]);
		else if (fieldToSet.equals(fieldName.ADDRESS1.getName()))
			record.setAdd1(inData[add1Index]);
		else if (fieldToSet.equals(fieldName.ADDRESS1_2.getName()))
			record.setAdd1_2(inData[add1_2Index]);
		else if (fieldToSet.equals(fieldName.ADDRESS2.getName()))
			record.setAdd2(inData[add2Index]);
		else if (fieldToSet.equals(fieldName.ADDRESS2_2.getName()))
			record.setAdd2_2(inData[add2_2Index]);
		else if (fieldToSet.equals(fieldName.CITY.getName()))
			record.setCity(inData[cityIndex]);
		else if (fieldToSet.equals(fieldName.PROVINCE.getName()))
			record.setProv(inData[provIndex]);
		else if (fieldToSet.equals(fieldName.POSTALCODE.getName()))
			record.setPCode(inData[pCodeIndex]);
		else if (fieldToSet.equals(fieldName.COUNTRY.getName()))
			record.setCntry(inData[cntryIndex]);
		else if (fieldToSet.equals(fieldName.NCOA.getName()))
			record.setNcoa(inData[ncoaIndex]);
		else if (fieldToSet.equals(fieldName.DNM.getName()))
			record.setDnm(inData[dnmIndex]);
		else if (fieldToSet.equals(fieldName.DECEASED.getName()))
			record.setDeceased(inData[deceasedIndex]);
		else if (fieldToSet.equals(fieldName.DONATION_AMOUNT_ARRAY.getName()))
			record.setDnAmtArr(inData[dnAmtArrIndex]);
		else if (fieldToSet.equals(fieldName.DONATION_DATE_ARRAY.getName()))
			record.setDnDatArr(inData[dnDatArrIndex]);
		else if (fieldToSet.equals(fieldName.NUMBER_OF_DONATIONS.getName()))
			record.setNumDn(inData[numDnIndex]);
		else if (fieldToSet.equals(fieldName.NUMBER_OF_DONATIONS_LAST_12_MONTHS.getName()))
			record.setNumDnLst12Mnths(inData[numDnLst12MnthsIndex]);
		else if (fieldToSet.equals(fieldName.TOTAL_DONATION_AMOUNT.getName()))
			record.setTtlDnAmt(inData[ttlDnAmtIndex]);
		else if (fieldToSet.equals(fieldName.TOTAL_DONATION_AMOUNT_LAST_12_MONTHS.getName()))
			record.setTtlDnAmtLst12Mnths(inData[ttlDnAmtLst12MnthsIndex]);
		else if (fieldToSet.equals(fieldName.TOTAL_DONATION_AMOUNT_CURRENT_YEAR.getName()))
			record.setTtlDnAmtCrntYr(inData[ttlDnAmtCrntYrIndex]);
		else if (fieldToSet.equals(fieldName.TOTAL_DONATION_AMOUNT_LAST_YEAR.getName()))
			record.setTtlDnAmtLstYr(inData[ttlDnAmtLstYrIndex]);
		else if (fieldToSet.equals(fieldName.FIRST_DONATION_AMOUNT.getName()))
			record.setFstDnAmt(inData[fstDnAmtIndex]);
		else if (fieldToSet.equals(fieldName.LAST_DONATION_AMOUNT.getName()))
			record.setLstDnAmt(inData[lstDnAmtIndex]);
		else if (fieldToSet.equals(fieldName.LARGEST_DONATION_AMOUNT.getName()))
			record.setLrgDnAmt(inData[lrgDnAmtIndex]);
		else if (fieldToSet.equals(fieldName.SMALLEST_DONATION_AMOUNT.getName()))
			record.setSDnAmt(inData[smlDnAmtIndex]);
		else if (fieldToSet.equals(fieldName.LAST_MONTHLY_DONATION_AMOUNT.getName()))
			record.setLMDnAmt(inData[lMDnAmtIndex]);
		else if (fieldToSet.equals(fieldName.FIRST_DONATION_DATE.getName()))
			record.setFstDnDat(inData[fstDnDatIndex]);
		else if (fieldToSet.equals(fieldName.LAST_DONATION_DATE.getName()))
			record.setLstDnDat(inData[lstDnDatIndex]);
		else if (fieldToSet.equals(fieldName.LAST_MONTHLY_DONATION_DATE.getName()))
			record.setLMDnDat(inData[lMDnDatIndex]);
		else if (fieldToSet.equals(fieldName.PENULTIMATE_AMOUNT.getName()))
			record.setPenultAmt(inData[penultAmtIndex]);
		else if (fieldToSet.equals(fieldName.PENULTIMATE_DATE.getName()))
			record.setPenultDat(inData[penultDatIndex]);
		else if (fieldToSet.equals(fieldName.DONATION1_AMOUNT.getName()))
			record.setDn1Amt(inData[dn1AmtIndex]);
		else if (fieldToSet.equals(fieldName.DONATION2_AMOUNT.getName()))
			record.setDn2Amt(inData[dn2AmtIndex]);
		else if (fieldToSet.equals(fieldName.DONATION3_AMOUNT.getName()))
			record.setDn3Amt(inData[dn3AmtIndex]);
		else if (fieldToSet.equals(fieldName.DONATION4_AMOUNT.getName()))
			record.setDn4Amt(inData[dn4AmtIndex]);
		else if (fieldToSet.equals(fieldName.OPEN_DONATION_AMOUNT.getName()))
			record.setODnAmt(inData[oDnAmtIndex]);
		else if (fieldToSet.equals(fieldName.MONTHLY_DONATION1_AMOUNT.getName()))
			record.setMDn1Amt(inData[mDn1AmtIndex]);
		else if (fieldToSet.equals(fieldName.MONTHLY_DONATION2_AMOUNT.getName()))
			record.setMDn2Amt(inData[mDn2AmtIndex]);
		else if (fieldToSet.equals(fieldName.MONTHLY_DONATION3_AMOUNT.getName()))
			record.setMDn3Amt(inData[mDn3AmtIndex]);
		else if (fieldToSet.equals(fieldName.MONTHLY_DONATION4_AMOUNT.getName()))
			record.setMDn4Amt(inData[mDn4AmtIndex]);
		else if (fieldToSet.equals(fieldName.MONTHLY_OPEN_DONATION_AMOUNT.getName()))
			record.setMODnAmt(inData[mODnAmtIndex]);
		else if (fieldToSet.equals(fieldName.PROVIDE1.getName()))
			record.setProvide1(inData[provide1Index]);
		else if (fieldToSet.equals(fieldName.PROVIDE2.getName()))
			record.setProvide2(inData[provide2Index]);
		else if (fieldToSet.equals(fieldName.PROVIDE3.getName()))
			record.setProvide3(inData[provide3Index]);
		else if (fieldToSet.equals(fieldName.PROVIDE4.getName()))
			record.setProvide4(inData[provide4Index]);
		else if (fieldToSet.equals(fieldName.PRIORITY.getName()))
			record.setPriority(inData[priorityIndex]);
		else if (fieldToSet.equals(fieldName.AB_GROUP.getName()))
			record.setABGroup(inData[abGroupIndex]);
		else if (fieldToSet.equals(fieldName.APPEAL.getName()))
			record.setAppeal(inData[appealIndex]);
		else if (fieldToSet.equals(fieldName.SEGMENT.getName()))
			record.setSeg(inData[segIndex]);
		else if (fieldToSet.equals(fieldName.SEGMENT_CODE.getName()))
			record.setSegCode(inData[segCodeIndex]);
		else if (fieldToSet.equals(fieldName.LETTER_VERSION.getName()))
			record.setLetVer(inData[letVerIndex]);
		else if (fieldToSet.equals(fieldName.PACKAGE_VERSION.getName()))
			record.setPkgVer(inData[pkgVerIndex]);
		else if (fieldToSet.equals(fieldName.REPLY_VERSION.getName()))
			record.setRepVer(inData[repVerIndex]);
		else if (fieldToSet.equals(fieldName.CODELINE.getName()))
			record.setCodeLine(inData[codeLineIndex]);
		else if (fieldToSet.equals(fieldName.MEAN_AMOUNT.getName()))
			record.setMeanAmt(inData[meanAmtIndex]);
		else if (fieldToSet.equals(fieldName.MEDIAN.getName()))
			record.setMedian(inData[medianIndex]);
		else if (fieldToSet.equals(fieldName.STANDARD_DEVIATION_AMOUNT.getName()))
			record.setSDevAmt(inData[sDevAmtIndex]);
		else if (fieldToSet.equals(fieldName.RECENCY.getName()))
			record.setRScore(inData[rScoreIndex]);
		else if (fieldToSet.equals(fieldName.FREQUENCY.getName()))
			record.setFScore(inData[fScoreIndex]);
		else if (fieldToSet.equals(fieldName.MONETARY.getName()))
			record.setMScore(inData[mScoreIndex]);
		else if (fieldToSet.equals(fieldName.RFM.getName()))
			record.setRfmScore(inData[rfmScoreIndex]);
		else if (fieldToSet.equals(fieldName.LENGTH.getName()))
			record.setLength(inData[lengthIndex]);
		else if (fieldToSet.equals(fieldName.WIDTH.getName()))
			record.setWidth(inData[widthIndex]);
		else if (fieldToSet.equals(fieldName.HEIGHT.getName()))
			record.setHeight(inData[heightIndex]);
		else if (fieldToSet.equals(fieldName.WEIGHT.getName()))
			record.setWeight(inData[weightIndex]);
	}
	
	public void autoSetRecordList() {
		for(int i = 0; i < data.length; ++i) {			
			Record record = new Record.Builder(i, data[i], "", "", "").build();
			add(record); // add the record the recordList in userData
		}
	}
	
	public void autoSetRecordListFields(String[] fieldsToSet) {
		for(Record record : recordList)
			for(String field : fieldsToSet)
				setDfFieldFromInData(field, record);
	}

	/**
	 * Gets the headers to export as a String[]
	 * 
	 * @return exportHeaders as a String
	 */
	public String[] getExportHeaders() {

		String[][] allHeaders = { inHeaders, dfHeaders };
		String[] exportHeaders = new String[inHeaders.length + dfHeaders.length];
		int counter = 0;
		for (int i = 0; i < allHeaders.length; i++) {
			for (int j = 0; j < allHeaders[i].length; j++) {
				exportHeaders[counter++] = allHeaders[i][j];
			}
		}

		return exportHeaders;

	}

	/**
	 * Gets the max field length from the userData
	 * 
	 * @return exmprtMaxFieldLength as an int[]
	 */
	public int[] getExportMaxFieldLength() {
		return exportMaxFieldLength;
	}

	/**
	 * The headers and data to set
	 * 
	 * @param inHeaders
	 *          the inHeaders to set
	 * @param data
	 *          the data to set
	 */
	public void load(String[] inHeaders, String[][] data) {
		setInHeaders(inHeaders);
		setData(data);

	}

	/**
	 * Gets all the internal Dfuze userData fieldNames as a String[]
	 * 
	 * @return allDfHeaders as a String[]
	 */
	public static String[] getAllDfHeaders() {
		return allDfHeaders;
	}

}
