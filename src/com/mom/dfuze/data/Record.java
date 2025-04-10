/**
 * Project: Dfuze
 * File: EntrySet.java
 * Date: Mar 2, 2020
 * Time: 11:42:04 PM
 */
package com.mom.dfuze.data;

import java.util.Arrays;

/**
 * Record Class uses the Builder Pattern to chain setters for easier Record Object creation
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 * 
 *
 */
public class Record {

  // Internal
  private int dfId;
  private String[] dfInData; // all the original data
  private boolean isDupe;
  private String spFilt1; // segment plan filter 1
  private String spFilt2; // segment plan filter 2

  // Internal - Dedupe specific
  private String dupeStreetAdd1;
  private String dupeStreetAdd2;
  private String dupeMetaStreetAdd1;
  private String dupeMetaStreetAdd2;
  private String dupeAdd1;
  private String dupeAdd2;
  private String dupePCode;
  private String dupePOBox;
  private String dupePOBoxExtra;
  private String dupeStreetDirection;
  private String dupeRR;
  private String dupeName1;
  private String dupeName1FirstPerson;
  private String dupeName1SecondPerson;
  private String dupeName1Normalized;
  private String dupeName2;
  private String dupeName2FirstPerson;
  private String dupeName2SecondPerson;
  private String dupeName2Normalized;
  private int dupeGroupId; // the id for the group of dupes
  private int dupeGroupSize; // the size of the group

  // Miscellaneous
  private String inId; // supplied id in data
  private String lang; // language indicator
  private String barCode; // barcode
  private String recType; // record type indicator ex. I = Individual or O = Organization
  private String status; // status
  private String listNum; // the order number of the list
  private String day; // date day
  private String month; // date month
  private String year; // date year
  private String latitude; // latitude geocoordinates
  private String longitude; // longitude geocoordinates
  private String quantity; // generic quantity
  private String phone1; // phone number 1
  private String phone2; // phone number 2
  private String mobilePhone; // mobile phone
  private String email; // email
  private String storeId; // store id
  private String dateFrom;
  private String dateTo;
  private String timeFrom;
  private String timeTo;
  private String ref;
  private String comments;
  private String isProof;
  private String misc1;
  private String misc2;

  // Name and address members
  private String prefix; // ex. Mr.
  private String suffix; // ex. Jr
  private String fstName; // first name
  private String midName; // middle name
  private String lstName; // last name
  private String nam1; // full name
  private String nam1_2; // the second half of nam1 when split on 'n' characters
  private String nam2; // optional full name2
  private String nam2_2; // the second half of nam2 when split on 'n' characters
  
  private String spousePrefix; // spousal prefix
  private String spouseSuffix; // spousal suffix
  private String spouseFstName; // spousal fname
  private String spouseMidName; // spousal mnam
  private String spouseLstName; // spousal lname
  private String spouseNam1; // spousal full name
  private String spouseNam2; // optional spousal full name2
  
  private String cmpny; // company
  private String cmpny_2; // the second half of cmpny when split on 'n' characters
  private String cmpnyCn; // the contact for a company
  private String dearSal; // dear line salutation
  private String paraSal; // paragraph line salutation
  private String cmpnySal; // company salutation
  private String cmpnyAdd1; // company address 1
  private String cmpnyAdd2; // company address 2
  private String add1; // address 1
  private String add1_2; // the second half of add1 when split on 'n' characters
  private String add2; // address 2
  private String add2_2; // the second half of add2 when split on 'n' characters
  private String city; // city
  private String prov; // province
  private String pCode; // postal code
  private String cntry; // country
  private String streetNum;
  private String streetDir;
  private String streetNam;
  private String streetDef;
  private String aptNum;
  private String serviceAdd;
  private String serviceCity;
  private String poBox;
  private String careOf;
  private String ncoa; // national change of address
  private String dnm; // do not mail
  private String deceased; // deceased

  // Donation members
  private String dnAmtArr; // donation amount supplied as a delimited array
  private String dnDatArr; // donation dates supplied as a delimited array
  private String numDn; // # of donations given
  private String numDnLst12Mnths; // # of donations given from last 12 months
  private String ttlDnAmt; // total donation amount given in $
  private String ttlDnAmtLst12Mnths; // total donation amount given in $ from last 12 months
  private String ttlDnAmtCrntYr; // total donation amount given in current year
  private String ttlDnAmtLstYr; // total donation amount given in last year
  private String fstDnAmt; // first donation amount given
  private String lstDnAmt; // last donation amount given
  private String lMDnAmt; // last monthly donation amount give
  private String lrgDnAmt; // largest donation amount given
  private String smlDnAmt; // smallest donation amount given
  private String fstDnDat; // date of the first donation
  private String lstDnDat; // date of the last donation
  private String lMDnDat; // date of the last monthly donation
  private String penultAmt; // second last donation amount
  private String penultDat; // second last donation date
  private String dn1Amt; // first suggested donation amount
  private String dn2Amt; // second suggested donation amount
  private String dn3Amt; // third suggested donation amount
  private String dn4Amt; // fourth suggested donation amount
  private String oDnAmt; // open donation amount
  private String mDn1Amt; // monthly first suggested donation amount
  private String mDn2Amt; // monthly second suggested donation amount
  private String mDn3Amt; // monthly third suggested donation amount
  private String mDn4Amt; // monthly fourth suggested donation amount
  private String mODnAmt; // monthly open donation amount
  private String provide1; // provides x of something ie. 5 meals or 2 people
  private String provide2; // provides x of something ie. 5 meals or 2 people
  private String provide3; // provides x of something ie. 5 meals or 2 people
  private String provide4; // provides x of something ie. 5 meals or 2 people

  // Segmentation
  private String priority; // some value used as a priority
  private String abGroup; // A / B testing group
  private String appeal; // the appeal of the job
  private String seg; // Segment
  private String segCode; // Segment code
  private String letVer; // letter version to receive
  private String pkgVer; // package version to receive
  private String repVer; // reply version to receive
  private String codeLine; // codeline ex. id + segment + control/test

  private String meanAmt; // mean donation value
  private String median; // median
  private String sDevAmt; // standard deviation value

  // RFM
  private String rScore; // recency score
  private String fScore; // frequency score
  private String mScore; // monetary score
  private String rfmScore; // total rfm score
  
  // Parcel
  private String length; // length of parcel in cm
  private String width; // width of parcel in cm
  private String height; // height of parcel in cm
  private String weight; // weight of parcel in g

  /**
   * Constructor for Objects of Class Record
   * 
   * @param builder
   *          the builder to set the data from
   */
  public Record(Builder builder) {

    dfId = builder.dfId;
    dfInData = builder.dfInData;
    isDupe = builder.isDupe;
    spFilt1 = builder.spFilt1;
    spFilt2 = builder.spFilt2;

    dupeStreetAdd1 = builder.dupeStreetAdd1;
    dupeStreetAdd2 = builder.dupeStreetAdd2;
    dupeMetaStreetAdd1 = builder.dupeMetaStreetAdd1;
    dupeMetaStreetAdd2 = builder.dupeMetaStreetAdd2;
    dupeAdd1 = builder.dupeAdd1;
    dupeAdd2 = builder.dupeAdd2;
    dupePCode = builder.dupePCode;
    dupePOBox = builder.dupePOBox;
    dupePOBoxExtra = builder.dupePOBoxExtra;
    dupeStreetDirection = builder.dupeStreetDirection;
    dupeRR = builder.dupeRR;
    dupeName1 = builder.dupeName1;
    dupeName1FirstPerson = builder.dupeName1FirstPerson;
    dupeName1SecondPerson = builder.dupeName1SecondPerson;
    dupeName1Normalized = builder.dupeName1Normalized;
    dupeName2 = builder.dupeName2;
    dupeName2FirstPerson = builder.dupeName2FirstPerson;
    dupeName2SecondPerson = builder.dupeName2SecondPerson;
    dupeName2Normalized = builder.dupeName2Normalized;
    dupeGroupId = builder.dupeGroupId;
    dupeGroupSize = builder.dupeGroupSize;

    inId = builder.inId;
    lang = builder.lang;
    barCode = builder.barCode;
    recType = builder.recType;
    status = builder.status;
    listNum = builder.listNum;
    day = builder.day;
    month = builder.month;
    year = builder.year;
    latitude = builder.latitude;
    longitude = builder.longitude;
    quantity = builder.quantity;
    phone1 = builder.phone1;
    phone2 = builder.phone2;
    mobilePhone = builder.mobilePhone;
    email = builder.email;
    storeId = builder.storeId;
    dateFrom = builder.dateFrom;
    dateTo = builder.dateTo;
    timeFrom = builder.timeFrom;
    timeTo = builder.timeTo;
    ref = builder.ref;
    comments = builder.comments;
    isProof = builder.isProof;
    misc1 = builder.misc1;
    misc2 = builder.misc2;

    prefix = builder.prefix;
    suffix = builder.suffix;
    fstName = builder.fstName;
    midName = builder.midName;
    lstName = builder.lstName;
    nam1 = builder.nam1;
    nam1_2 = builder.nam1_2;
    nam2 = builder.nam2;
    nam2_2 = builder.nam2_2;
    
    spousePrefix = builder.spousePrefix;
    spouseSuffix = builder.spouseSuffix;
    spouseFstName = builder.spouseFstName;
    spouseMidName = builder.spouseMidName;
    spouseLstName = builder.spouseLstName;
    spouseNam1 = builder.spouseNam1;
    spouseNam2 = builder.spouseNam2;
    
    cmpny = builder.cmpny;
    cmpny_2 = builder.cmpny_2;
    cmpnyCn = builder.cmpnyCn;
    dearSal = builder.dearSal;
    paraSal = builder.paraSal;
    cmpnySal = builder.cmpnySal;
    cmpnyAdd1 = builder.cmpnyAdd1;
    cmpnyAdd2 = builder.cmpnyAdd2;
    add1 = builder.add1;
    add1_2 = builder.add1_2;
    add2 = builder.add2;
    add2_2 = builder.add2_2;
    city = builder.city;
    prov = builder.prov;
    pCode = builder.pCode;
    cntry = builder.cntry;
    streetNum = builder.streetNum;
    streetDir = builder.streetDir;
    streetNam = builder.streetNam;
    streetDef = builder.streetDef;
    aptNum = builder.aptNum;
    serviceAdd = builder.serviceAdd;
    serviceCity = builder.serviceCity;
    poBox = builder.poBox;
    careOf = builder.careOf;
    ncoa = builder.ncoa;
    dnm = builder.dnm;
    deceased = builder.deceased;

    dnAmtArr = builder.dnAmtArr;
    dnDatArr = builder.dnDatArr;
    numDn = builder.numDn;
    numDnLst12Mnths = builder.numDnLst12Mnths;
    ttlDnAmt = builder.ttlDnAmt;
    ttlDnAmtLst12Mnths = builder.ttlDnAmtLst12Mnths;
    ttlDnAmtCrntYr = builder.ttlDnAmtCrntYr;
    ttlDnAmtLstYr = builder.ttlDnAmtLstYr;
    fstDnAmt = builder.fstDnAmt;
    lstDnAmt = builder.lstDnAmt;
    lMDnAmt = builder.lMDnAmt;
    lrgDnAmt = builder.lrgDnAmt;
    smlDnAmt = builder.smlDnAmt;
    fstDnDat = builder.fstDnDat;
    lstDnDat = builder.lstDnDat;
    lMDnDat = builder.lMDnDat;
    penultAmt = builder.penultAmt;
    penultDat = builder.penultDat;
    dn1Amt = builder.dn1Amt;
    dn2Amt = builder.dn2Amt;
    dn3Amt = builder.dn3Amt;
    dn4Amt = builder.dn4Amt;
    oDnAmt = builder.oDnAmt;
    mDn1Amt = builder.mDn1Amt;
    mDn2Amt = builder.mDn2Amt;
    mDn3Amt = builder.mDn3Amt;
    mDn4Amt = builder.mDn4Amt;
    mODnAmt = builder.mODnAmt;
    provide1 = builder.provide1;
    provide2 = builder.provide2;
    provide3 = builder.provide3;
    provide4 = builder.provide4;

    priority = builder.priority;
    appeal = builder.appeal;
    abGroup = builder.abGroup;
    seg = builder.seg;
    segCode = builder.segCode;
    letVer = builder.letVer;
    pkgVer = builder.pkgVer;
    repVer = builder.repVer;
    codeLine = builder.codeLine;

    meanAmt = builder.meanAmt;
    median = builder.median;
    sDevAmt = builder.sDevAmt;

    rScore = builder.rScore;
    fScore = builder.fScore;
    mScore = builder.mScore;
    rfmScore = builder.rfmScore;
    
    length = builder.length;
    width = builder.width;
    height = builder.height;
    weight = builder.weight;

  }

  /**
   * Inner Builder Class
   */
  public static class Builder {

    private int dfId;
    private String[] dfInData;
    private boolean isDupe;
    private String spFilt1;
    private String spFilt2;

    private String dupeStreetAdd1;
    private String dupeStreetAdd2;
    private String dupeMetaStreetAdd1;
    private String dupeMetaStreetAdd2;
    private String dupeAdd1;
    private String dupeAdd2;
    private String dupePCode;
    private String dupePOBox;
    private String dupePOBoxExtra;
    private String dupeStreetDirection;
    private String dupeRR;
    private String dupeName1;
    private String dupeName1FirstPerson;
    private String dupeName1SecondPerson;
    private String dupeName1Normalized;
    private String dupeName2;
    private String dupeName2FirstPerson;
    private String dupeName2SecondPerson;
    private String dupeName2Normalized;
    private int dupeGroupId;
    private int dupeGroupSize;

    private String inId;
    private String lang;
    private String barCode;
    private String recType;
    private String status;
    private String listNum;
    private String day;
    private String month;
    private String year;
    private String latitude;
    private String longitude;
    private String quantity;
    private String phone1;
    private String phone2;
    private String mobilePhone;
    private String email;
    private String storeId;
    private String dateFrom;
    private String dateTo;
    private String timeFrom;
    private String timeTo;
    private String ref;
    private String comments;
    private String isProof;
    private String misc1;
    private String misc2;

    private String prefix;
    private String suffix;
    private String fstName;
    private String midName;
    private String lstName;
    private String nam1;
    private String nam1_2;
    private String nam2;
    private String nam2_2;
    
    private String spousePrefix;
    private String spouseSuffix;
    private String spouseFstName;
    private String spouseMidName;
    private String spouseLstName;
    private String spouseNam1;
    private String spouseNam2;
    
    private String cmpny;
    private String cmpny_2;
    private String cmpnyCn;
    private String dearSal;
    private String paraSal;
    private String cmpnySal;
    private String cmpnyAdd1;
    private String cmpnyAdd2;
    private String add1;
    private String add1_2;
    private String add2;
    private String add2_2;
    private String city;
    private String prov;
    private String pCode;
    private String cntry;
    private String streetNum;
    private String streetDir;
    private String streetNam;
    private String streetDef;
    private String aptNum;
    private String serviceAdd;
    private String serviceCity;
    private String poBox;
    private String careOf;
    private String ncoa;
    private String dnm;
    private String deceased;

    private String dnAmtArr;
    private String dnDatArr;
    private String numDn;
    private String numDnLst12Mnths;
    private String ttlDnAmt;
    private String ttlDnAmtLst12Mnths;
    private String ttlDnAmtCrntYr;
    private String ttlDnAmtLstYr;
    private String fstDnAmt;
    private String lstDnAmt;
    private String lrgDnAmt;
    private String lMDnAmt;
    private String smlDnAmt;
    private String fstDnDat;
    private String lstDnDat;
    private String lMDnDat;
    private String penultAmt;
    private String penultDat;
    private String dn1Amt;
    private String dn2Amt;
    private String dn3Amt;
    private String dn4Amt;
    private String oDnAmt;
    private String mDn1Amt;
    private String mDn2Amt;
    private String mDn3Amt;
    private String mDn4Amt;
    private String mODnAmt;
    private String provide1;
    private String provide2;
    private String provide3;
    private String provide4;

    private String priority;
    private String abGroup;
    private String appeal;
    private String seg;
    private String segCode;
    private String letVer;
    private String pkgVer;
    private String repVer;
    private String codeLine;

    private String meanAmt;
    private String median;
    private String sDevAmt;

    private String rScore;
    private String fScore;
    private String mScore;
    private String rfmScore;
    
    private String length;
    private String width;
    private String height;
    private String weight;

    /**
     * Constructor for Objects of Class Builder
     * 
     * @param dfId
     *          the mandatory internal dfuze ID to set as
     * @param inData
     *          the mandatory input Data to set
     * @param add1
     *          the mandatory address1 to set
     * @param add2
     *          the mandatory addresss2 to set
     * @param pCode
     *          the mandatory postal code to set
     */
    public Builder(int dfId, String[] inData, String add1, String add2, String pCode) {
      this.dfId = dfId;
      this.dfInData = inData;
      this.add1 = add1;
      this.add2 = add2;
      this.pCode = pCode;
    }

    public Builder setDfId(int dfId) {
      this.dfId = dfId;
      return this;
    }

    public Builder setDfInData(String[] dfInData) {
      this.dfInData = dfInData;
      return this;
    }

    public Builder setIsDupe(boolean isDupe) {
      this.isDupe = isDupe;
      return this;
    }

    public Builder setSpFilt1(String spFilt1) {
      this.spFilt1 = spFilt1;
      return this;
    }

    public Builder setSpFilt2(String spFilt2) {
      this.spFilt2 = spFilt2;
      return this;
    }
    
    public Builder setDupeStreetAdd1(String dupeStreetAdd1) {
    	this.dupeStreetAdd1 = dupeStreetAdd1;
    	return this;
    }
    
    public Builder setDupeStreetAdd2(String dupeStreetAdd2) {
    	this.dupeStreetAdd2 = dupeStreetAdd2;
    	return this;
    }

    public Builder setDupeMetaStreetAdd1(String dupeMetaStreetAdd1) {
      this.dupeMetaStreetAdd1 = dupeMetaStreetAdd1;
      return this;
    }

    public Builder setDupeMetaStreetAdd2(String dupeMetaStreetAdd2) {
      this.dupeMetaStreetAdd2 = dupeMetaStreetAdd2;
      return this;
    }

    public Builder setDupeAdd1(String dupeAdd1) {
      this.dupeAdd1 = dupeAdd1;
      return this;
    }

    public Builder setDupeAdd2(String dupeAdd2) {
      this.dupeAdd2 = dupeAdd2;
      return this;
    }

    public Builder setDupePCode(String dupePCode) {
      this.dupePCode = dupePCode;
      return this;
    }

    public Builder setDupePOBox(String dupePOBox) {
      this.dupePOBox = dupePOBox;
      return this;
    }

    public Builder setDupePOBoxExtra(String dupePOBoxExtra) {
      this.dupePOBoxExtra = dupePOBoxExtra;
      return this;
    }

    public Builder setDupeStreetDirection(String dupeStreetDirection) {
      this.dupeStreetDirection = dupeStreetDirection;
      return this;
    }

    public Builder setDupeRR(String dupeRR) {
      this.dupeRR = dupeRR;
      return this;
    }

    public Builder setDupeName1(String dupeName1) {
      this.dupeName1 = dupeName1;
      return this;
    }

    public Builder setDupeName1FirstPerson(String dupeName1FirstPerson) {

      this.dupeName1FirstPerson = dupeName1FirstPerson;
      return this;
    }

    public Builder setDupeName1SecondPerson(String dupeName1SecondPerson) {

      this.dupeName1SecondPerson = dupeName1SecondPerson;
      return this;
    }

    public Builder setDupeName1Normalized(String dupeName1Normalized) {

      this.dupeName1Normalized = dupeName1Normalized;
      return this;
    }

    public Builder setDupeName2(String dupeName2) {
      this.dupeName2 = dupeName2;
      return this;
    }

    public Builder setDupeName2FirstPerson(String dupeName2FirstPerson) {

      this.dupeName2FirstPerson = dupeName2FirstPerson;
      return this;
    }

    public Builder setDupeName2SecondPerson(String dupeName2SecondPerson) {
      this.dupeName2SecondPerson = dupeName2SecondPerson;
      return this;
    }

    public Builder setDupeName2Normalized(String dupeName2Normalized) {

      this.dupeName2Normalized = dupeName2Normalized;
      return this;
    }

    public Builder setDupeGroupId(int dupeGroupId) {
        this.dupeGroupId = dupeGroupId;
        return this;
      }
    
    public Builder setDupeGroupSize(int dupeGroupSize) {
        this.dupeGroupSize = dupeGroupSize;
        return this;
      }
    
    public Builder setInId(String inId) {
      this.inId = inId;
      return this;
    }

    public Builder setLang(String lang) {
      this.lang = lang;
      return this;
    }

    public Builder setBarCode(String barCode) {
      this.barCode = barCode;
      return this;
    }

    public Builder setRecType(String recType) {
      this.recType = recType;
      return this;
    }
    
    public Builder setStatus(String status) {
        this.status = status;
        return this;
      }
    
    public Builder setListNum(String listNum) {
    	this.listNum = listNum;
    	return this;
    }
    
    public Builder setDay(String day) {
    	this.day = day;
    	return this;
    }
    
    public Builder setMonth(String month) {
    	this.month = month;
    	return this;
    }
    
    public Builder setYear(String year) {
    	this.year = year;
    	return this;
    }
    
    public Builder setLatitude(String latitude) {
    	this.latitude = latitude;
    	return this;
    }
    
    public Builder setLongitude(String longitude) {
    	this.longitude = longitude;
    	return this;
    }
    
    public Builder setQuantity(String quantity) {
    	this.quantity = quantity;
    	return this;
    }
    
    public Builder setPhone1(String phone1) {
    	this.phone1 = phone1;
    	return this;
    }
    
    public Builder setPhone2(String phone2) {
    	this.phone2 = phone2;
    	return this;
    }
    
    public Builder setMobilePhone(String mobilePhone) {
    	this.mobilePhone = mobilePhone;
    	return this;
    }
    
    public Builder setEmail(String email) {
    	this.email = email;
    	return this;
    }
    
    public Builder setStoreId(String storeId) {
    	this.storeId = storeId;
    	return this;
    }
    
    public Builder setDateFrom(String dateFrom) {
    	this.dateFrom = dateFrom;
    	return this;
    }
    
    public Builder setDateTo(String dateTo) {
    	this.dateTo = dateTo;
    	return this;
    }
    
    public Builder setTimeFrom(String timeFrom) {
    	this.timeFrom = timeFrom;
    	return this;
    }
    
    public Builder setTimeTo(String timeTo) {
    	this.timeTo = timeTo;
    	return this;
    }
    
    public Builder setRef(String ref) {
    	this.ref = ref;
    	return this;
    }
    
    public Builder setComments(String comments) {
    	this.comments = comments;
    	return this;
    }
    
    public Builder setIsProof(String isProof) {
    	this.isProof = isProof;
    	return this;
    }
    
    public Builder setMisc1(String misc1) {
    	this.misc1 = misc1;
    	return this;
    }
    
    public Builder setMisc2(String misc2) {
    	this.misc2 = misc2;
    	return this;
    }

    public Builder setPrefix(String prefix) {
      this.prefix = prefix;
      return this;
    }

    public Builder setSuffix(String suffix) {
      this.suffix = suffix;
      return this;
    }

    public Builder setFstName(String fstName) {
      this.fstName = fstName;
      return this;
    }

    public Builder setMidName(String midName) {
      this.midName = midName;
      return this;
    }

    public Builder setLstName(String lstName) {
      this.lstName = lstName;
      return this;
    }

    public Builder setNam1(String nam1) {
      this.nam1 = nam1;
      return this;
    }

    public Builder setNam1_2(String nam1_2) {
      this.nam1_2 = nam1_2;
      return this;
    }

    public Builder setNam2(String nam2) {
      this.nam2 = nam2;
      return this;
    }

    public Builder setNam2_2(String nam2_2) {
      this.nam2_2 = nam2_2;
      return this;
    }

    
    public Builder setSpousePrefix(String spousePrefix) {
        this.spousePrefix = spousePrefix;
        return this;
      }

      public Builder setSpouseSuffix(String spouseSuffix) {
        this.spouseSuffix = spouseSuffix;
        return this;
      }

      public Builder setSpouseFstName(String spouseFstName) {
        this.spouseFstName = spouseFstName;
        return this;
      }

      public Builder setSpouseMidName(String spouseMidName) {
        this.spouseMidName = spouseMidName;
        return this;
      }

      public Builder setSpouseLstName(String spouseLstName) {
        this.spouseLstName = spouseLstName;
        return this;
      }

      public Builder setSpouseNam1(String spouseNam1) {
        this.spouseNam1 = spouseNam1;
        return this;
      }

      public Builder setSpouseNam2(String spouseNam2) {
        this.spouseNam2 = spouseNam2;
        return this;
      }

    public Builder setCmpny(String cmpny) {
      this.cmpny = cmpny;
      return this;
    }

    public Builder setCmpny_2(String cmpny_2) {
      this.cmpny_2 = cmpny_2;
      return this;
    }
    
    public Builder setCmpnyCn(String cmpnyCn) {
    	this.cmpnyCn = cmpnyCn;
    	return this;
    }

    public Builder setDearSal(String dearSal) {
      this.dearSal = dearSal;
      return this;
    }

    public Builder setParaSal(String paraSal) {
      this.paraSal = paraSal;
      return this;
    }
    
    public Builder setCmpnySal(String cmpnySal) {
    	this.cmpnySal = cmpnySal;
    	return this;
    }
    
    public Builder setCmpnyAdd1(String cmpnyAdd1) {
        this.cmpnyAdd1 = cmpnyAdd1;
        return this;
    }
    
    public Builder setCmpnyAdd2(String cmpnyAdd2) {
        this.cmpnyAdd2 = cmpnyAdd2;
        return this;
    }

    public Builder setAdd1(String add1) {
      this.add1 = add1;
      return this;
    }

    public Builder setAdd1_2(String add1_2) {
      this.add1_2 = add1_2;
      return this;
    }

    public Builder setAdd2(String add2) {
      this.add2 = add2;
      return this;
    }

    public Builder setAdd2_2(String add2_2) {
      this.add2_2 = add2_2;
      return this;
    }

    public Builder setCity(String city) {
      this.city = city;
      return this;
    }

    public Builder setProv(String prov) {
      this.prov = prov;
      return this;
    }

    public Builder setPCode(String pCode) {
      this.pCode = pCode;
      return this;
    }
    
	public Builder setCntry(String cntry) {
    	this.cntry = cntry;
    	return this;
    }
	
	public Builder setStreetNum(String streetNum) {
    	this.streetNum = streetNum;
    	return this;
    }
	
	public Builder setStreetDir(String streetDir) {
    	this.streetDir = streetDir;
    	return this;
    }
	
	public Builder setStreetNam(String streetNam) {
    	this.streetNam = streetNam;
    	return this;
    }
	
	public Builder setStreetDef(String streetDef) {
    	this.streetDef = streetDef;
    	return this;
    }
	
	public Builder setAptNum(String aptNum) {
    	this.aptNum = aptNum;
    	return this;
    }
	
	public Builder setServiceAdd(String serviceAdd) {
    	this.serviceAdd = serviceAdd;
    	return this;
    }
	
	public Builder setServiceCity(String serviceCity) {
    	this.serviceCity = serviceCity;
    	return this;
    }
	
	public Builder setPoBox(String poBox) {
    	this.poBox = poBox;
    	return this;
    }
	
	public Builder setCareOf(String careOf) {
    	this.careOf = careOf;
    	return this;
    }

    public Builder setNcoa(String ncoa) {
      this.ncoa = ncoa;
      return this;
    }
    
    public Builder setDnm(String dnm) {
        this.dnm = dnm;
        return this;
      }
    
    public Builder setDeceased(String deceased) {
        this.deceased = deceased;
        return this;
      }

    public Builder setDnAmtArr(String dnAmtArr) {
      this.dnAmtArr = dnAmtArr;
      return this;
    }

    public Builder setDnDatArr(String dnDatArr) {
      this.dnDatArr = dnDatArr;
      return this;
    }

    public Builder setNumDn(String numDn) {
      this.numDn = numDn;
      return this;
    }
    
    public Builder setNumDnLst12Mnths(String numDnLst12Mnths) {
        this.numDnLst12Mnths = numDnLst12Mnths;
        return this;
      }

    public Builder setTtlDnAmt(String ttlDnAmt) {
      this.ttlDnAmt = ttlDnAmt;
      return this;
    }
    
    public Builder setTtlDnAmtLst12Mnths(String ttlDnAmtLst12Mnths) {
        this.ttlDnAmtLst12Mnths = ttlDnAmtLst12Mnths;
        return this;
    }
    
    public Builder setTtlDnAmtCrntYr(String ttlDnAmtCrntYr) {
        this.ttlDnAmtCrntYr = ttlDnAmtCrntYr;
        return this;
    }
    
    public Builder setTtlDnAmtLstYr(String ttlDnAmtLstYr) {
        this.ttlDnAmtLstYr = ttlDnAmtLstYr;
        return this;
    }

    public Builder setFstDnAmt(String fstDnAmt) {
      this.fstDnAmt = fstDnAmt;
      return this;
    }

    public Builder setLstDnAmt(String lstDnAmt) {
      this.lstDnAmt = lstDnAmt;
      return this;
    }

    public Builder setLrgDnAmt(String lrgDnAmt) {
      this.lrgDnAmt = lrgDnAmt;
      return this;
    }

    public Builder setSmlDnAmt(String smlDnAmt) {
      this.smlDnAmt = smlDnAmt;
      return this;
    }
    
    public Builder setLMDnAmt(String lMDnAmt) {
    	this.lMDnAmt = lMDnAmt;
    	return this;
    }

    public Builder setFstDnDat(String fstDnDat) {
      this.fstDnDat = fstDnDat;
      return this;
    }

    public Builder setLstDnDat(String lstDnDat) {
      this.lstDnDat = lstDnDat;
      return this;
    }
    
    public Builder setLMDnDat(String lMDnDat) {
    	this.lMDnDat = lMDnDat;
    	return this;
    }
    
    public Builder setPenultAmt(String penultAmt) {
    	this.penultAmt = penultAmt;
    	return this;
    }
    
    public Builder setPenultDat(String penultDat) {
    	this.penultDat = penultDat;
    	return this;
    }

    public Builder setDn1Amt(String dn1Amt) {
      this.dn1Amt = dn1Amt;
      return this;
    }

    public Builder setDn2Amt(String dn2Amt) {
      this.dn2Amt = dn2Amt;
      return this;
    }

    public Builder setDn3Amt(String dn3Amt) {
      this.dn3Amt = dn3Amt;
      return this;
    }

    public Builder setDn4Amt(String dn4Amt) {
      this.dn4Amt = dn4Amt;
      return this;
    }

    public Builder setODnAmt(String oDnAmt) {
      this.oDnAmt = oDnAmt;
      return this;
    }

    public Builder setMDn1Amt(String mDn1Amt) {
      this.mDn1Amt = mDn1Amt;
      return this;
    }

    public Builder setMDn2Amt(String mDn2Amt) {
      this.mDn2Amt = mDn2Amt;
      return this;
    }

    public Builder setMdDn3Amt(String mDn3Amt) {
      this.mDn3Amt = mDn3Amt;
      return this;
    }

    public Builder setMDn4Amt(String mDn4Amt) {
      this.mDn4Amt = mDn4Amt;
      return this;
    }

    public Builder setMODnAmt(String mODnAmt) {
      this.mODnAmt = mODnAmt;
      return this;
    }

    public Builder setProvide1(String provide1) {
      this.provide1 = provide1;
      return this;
    }

    public Builder setProvide2(String provide2) {
      this.provide2 = provide2;
      return this;
    }

    public Builder setProvide3(String provide3) {
      this.provide3 = provide3;
      return this;
    }
    
    public Builder setProvide4(String provide4) {
        this.provide4 = provide4;
        return this;
      }

    public Builder setPriority(String priority) {
      this.priority = priority;
      return this;
    }

    public Builder setABGroup(String abGroup) {
      this.abGroup = abGroup;
      return this;
    }

    public Builder setAppeal(String appeal) {
      this.appeal = appeal;
      return this;
    }

    public Builder setSeg(String seg) {
      this.seg = seg;
      return this;
    }

    public Builder setSegCode(String segCode) {
      this.segCode = segCode;
      return this;
    }

    public Builder setLetVer(String letVer) {
      this.letVer = letVer;
      return this;
    }

    public Builder setPkgVer(String pkgVer) {
      this.pkgVer = pkgVer;
      return this;
    }

    public Builder setRepVer(String repVer) {
      this.repVer = repVer;
      return this;
    }

    public Builder setCodeLine(String codeLine) {
      this.codeLine = codeLine;
      return this;
    }

    public Builder setMeanAmt(String meanAmt) {
      this.meanAmt = meanAmt;
      return this;
    }
    
    public Builder setMedian(String median) {
    	this.median = median;
    	return this;
    }

    public Builder setSDevAmt(String sDevAmt) {
      this.sDevAmt = sDevAmt;
      return this;
    }

    public Builder setRScore(String rScore) {
      this.rScore = rScore;
      return this;
    }

    public Builder setFScore(String fScore) {
      this.fScore = fScore;
      return this;
    }

    public Builder setMScore(String mScore) {
      this.mScore = mScore;
      return this;
    }

    public Builder setRfmScore(String rfmScore) {
      this.rfmScore = rfmScore;
      return this;
    }
    
    public Builder setLength(String length) {
    	this.length = length;
    	return this;
    }
    
    public Builder setWidth(String width) {
    	this.width = width;
    	return this;
    }
    
    public Builder setHeight(String height) {
    	this.height = height;
    	return this;
    }
    
    public Builder setWeight(String weight) {
    	this.weight = weight;
    	return this;
    }

    public Record build() {
      return new Record(this);
    }

  }

  public int getDfId() {
    return dfId;
  }

  public String[] getDfInData() {
    return dfInData;
  }

  public boolean getIsDupe() {
    return isDupe;
  }

  public String getSpFilt1() {
    return spFilt1;
  }

  public String getSpFilt2() {
    return spFilt2;
  }
  
  public String getDupeStreetAdd1() {
	  return dupeStreetAdd1;
  }
  
  public String getDupeStreetAdd2() {
	  return dupeStreetAdd2;
  }

  public String getDupeMetaStreetAdd1() {
    return dupeMetaStreetAdd1;
  }

  public String getDupeMetaStreetAdd2() {
    return dupeMetaStreetAdd2;
  }

  public String getDupeAdd1() {
    return dupeAdd1;
  }

  public String getDupeAdd2() {
    return dupeAdd2;
  }

  public String getDupePCode() {
    return dupePCode;
  }

  public String getDupePOBox() {
    return dupePOBox;
  }

  public String getDupePOBoxExtra() {
    return dupePOBoxExtra;
  }

  public String getDupeStreetDirection() {
    return dupeStreetDirection;
  }

  public String getDupeRR() {
    return dupeRR;
  }

  public String getDupeName1() {
    return dupeName1;
  }

  public String getDupeName1FirstPerson() {
    return dupeName1FirstPerson;
  }

  public String getDupeName1SecondPerson() {
    return dupeName1SecondPerson;
  }

  public String getDupeName1Normalized() {
    return dupeName1Normalized;
  }

  public String getDupeName2() {
    return dupeName2;
  }

  public String getDupeName2FirstPerson() {
    return dupeName2FirstPerson;
  }

  public String getDupeName2SecondPerson() {
    return dupeName2SecondPerson;
  }

  public String getDupeName2Normalized() {
    return dupeName2Normalized;
  }
  
	public int getDupeGroupId() {
	    return dupeGroupId;
	}
	
	public int getDupeGroupSize() {
	    return dupeGroupSize;
	}

  public String getInId() {
    return inId;
  }

  public String getLang() {
    return lang;
  }

  public String getBarCode() {
    return barCode;
  }

  public String getRecType() {
    return recType;
  }
  
  public String getStatus() {
	    return status;
	  }
  
  public String getListNum() {
	  return listNum;
  }

  public String getDay() {
	  return day;
  }
  
  public String getMonth() {
	  return month;
  }
  
  public String getYear() {
	  return year;
  }
  
  public String getLatitude() {
	  return latitude;
  }
  
  public String getLongitude() {
	  return longitude;
  }
  
  public String getQuantity() {
	  return quantity;
  }
  
  public String getPhone1() {
	  return phone1;
  }
  
  public String getPhone2() {
	  return phone2;
  }
  
  public String getMobilePhone() {
	  return mobilePhone;
  }
  
  public String getEmail() {
	  return email;
  }
  
  public String getStoreId() {
	  return storeId;
  }
  
  public String getDateFrom() {
	  return dateFrom;
  }
  
  public String getDateTo() {
	  return dateTo;
  }
  
  public String getTimeFrom() {
	  return timeFrom;
  }
  
  public String getTimeTo() {
	  return timeTo;
  }
  
	public String getRef() {
		return ref;
	}
	
	public String getComments() {
		return comments;
	}
	
	public String getIsProof() {
		return isProof;
	}
	
	public String getMisc1() {
		return misc1;
	}
	
	public String getMisc2() {
		return misc2;
	}

  public String getPrefix() {
    return prefix;
  }

  public String getSuffix() {
    return suffix;
  }

  public String getFstName() {
    return fstName;
  }

  public String getMidName() {
    return midName;
  }

  public String getLstName() {
    return lstName;
  }

  public String getNam1() {
    return nam1;
  }

  public String getNam1_2() {
    return nam1_2;
  }

  public String getNam2() {
    return nam2;
  }

  public String getNam2_2() {
    return nam2_2;
  }

	public String getSpousePrefix() {
		return spousePrefix;
	}

	public String getSpouseSuffix() {
		return spouseSuffix;
	}

	public String getSpouseFstName() {
		return spouseFstName;
	}

	public String getSpouseMidName() {
		return spouseMidName;
	}

	public String getSpouseLstName() {
		return spouseLstName;
	}

	public String getSpouseNam1() {
		return spouseNam1;
	}

	public String getSpouseNam2() {
		return spouseNam2;
	}

  public String getCmpny() {
    return cmpny;
  }

  public String getCmpny_2() {
    return cmpny_2;
  }
  
  public String getCmpnyCn() {
	  return cmpnyCn;
  }

  public String getDearSal() {
    return dearSal;
  }

  public String getParaSal() {
    return paraSal;
  }
  
  public String getCmpnySal() {
	return cmpnySal;
  }
  
  public String getCmpnyAdd1() {
	return cmpnyAdd1;
  }
  
  public String getCmpnyAdd2() {
	return cmpnyAdd2;
  }

  public String getAdd1() {
    return add1;
  }

  public String getAdd1_2() {
    return add1_2;
  }

  public String getAdd2() {
    return add2;
  }

  public String getAdd2_2() {
    return add2_2;
  }

  public String getCity() {
    return city;
  }

  public String getProv() {
    return prov;
  }

  public String getPCode() {
    return pCode;
  }

	public String getCntry() {
		return cntry;
	}
	
	public String getStreetNum() {
		return streetNum;
	}
	
	public String getStreetDir() {
		return streetDir;
	}
	
	public String getStreetNam() {
		return streetNam;
	}
	
	public String getStreetDef() {
		return streetDef;
	}
	
	public String getAptNum() {
		return aptNum;
	}
	
	public String getServiceAdd() {
		return serviceAdd;
	}
	
	public String getServiceCity() {
		return serviceCity;
	}
	
	public String getPoBox() {
		return poBox;
	}
	
	public String getCareOf() {
		return careOf;
	}
  
  public String getNcoa() {
	    return ncoa;
	  }
  
  public String getDnm() {
	    return dnm;
	  }
  
  public String getDeceased() {
	    return deceased;
	  }

  public String getDnAmtArr() {
    return dnAmtArr;
  }

  public String getDnDatArr() {
    return dnDatArr;
  }

  public String getNumDn() {
    return numDn;
  }
  
  public String getNumDnLst12Mnths() {
	  return numDnLst12Mnths;
  }

  public String getTtlDnAmt() {
    return ttlDnAmt;
  }
  
  public String getTtlDnAmtLst12Mnths() {
	  return ttlDnAmtLst12Mnths;
  }
  
  public String getTtlDnAmtCrntYr() {
	  return ttlDnAmtCrntYr;
  }
  
  public String getTtlDnAmtLstYr() {
	  return ttlDnAmtLstYr;
  }

  public String getFstDnAmt() {
    return fstDnAmt;
  }

  public String getLstDnAmt() {
    return lstDnAmt;
  }

  public String getLrgDnAmt() {
    return lrgDnAmt;
  }

  public String getSDnAmt() {
    return smlDnAmt;
  }
  
  public String getLMDnAmt() {
	  return lMDnAmt;
  }

  public String getFstDnDat() {
    return fstDnDat;
  }

  public String getLstDnDat() {
    return lstDnDat;
  }
  
  public String getLMDnDat() {
	  return lMDnDat;
  }
  
  public String getPenultAmt() {
	  return penultAmt;
  }
  
  public String getPenultDat() {
	  return penultDat;
  }

  public String getDn1Amt() {
    return dn1Amt;
  }

  public String getDn2Amt() {
    return dn2Amt;
  }

  public String getDn3Amt() {
    return dn3Amt;
  }

  public String getDn4Amt() {
    return dn4Amt;
  }

  public String getODnAmt() {
    return oDnAmt;
  }

  public String getMDn1Amt() {
    return mDn1Amt;
  }

  public String getMDn2Amt() {
    return mDn2Amt;
  }

  public String getMDn3Amt() {
    return mDn3Amt;
  }

  public String getMDn4Amt() {
    return mDn4Amt;
  }

  public String getMODnAmt() {
    return mODnAmt;
  }

  public String getProvide1() {
    return provide1;
  }

  public String getProvide2() {
    return provide2;
  }

  public String getProvide3() {
    return provide3;
  }
  
  public String getProvide4() {
	    return provide4;
  }

  public String getPriority() {
    return priority;
  }

  public String getABGroup() {
    return abGroup;
  }

  public String getAppeal() {
    return appeal;
  }

  public String getSeg() {
    return seg;
  }

  public String getSegCode() {
    return segCode;
  }

  public String getLetVer() {
    return letVer;
  }

  public String getPkgVer() {
    return pkgVer;
  }

  public String getRepVer() {
    return repVer;
  }

  public String getCodeLine() {
    return codeLine;
  }

  public String getMeanAmt() {
    return meanAmt;
  }
  
  public String getMedian() {
	  return median;
  }

  public String getSDevAmt() {
    return sDevAmt;
  }

  public String getRScore() {
    return rScore;
  }

  public String getFScore() {
    return fScore;
  }

  public String getMScore() {
    return mScore;
  }

  public String getRfmScore() {
    return rfmScore;
  }
  
  public String getLength() {
	return length;
  }
  
  public String getWidth() {
	return width;
  }
  
  public String getHeight() {
	  return height;
  }
  
  public String getWeight() {
	  return weight;
  }

  //
  // setters
  //

  public void setDfId(int dfId) {
    this.dfId = dfId;
  }

  public void setDfInData(String[] dfInData) {
    this.dfInData = dfInData;
  }

  public void setIsDupe(boolean isDupe) {
    this.isDupe = isDupe;
  }

  public void setSpFilt1(String spFilt1) {
    this.spFilt1 = spFilt1;
  }

  public void setSpFilt2(String spFilt2) {
    this.spFilt2 = spFilt2;
  }
  
  public void setDupeStreetAdd1(String dupeStreetAdd1) {
	    this.dupeStreetAdd1 = dupeStreetAdd1;
  }

  public void setDupeStreetAdd2(String dupeStreetAdd2) {
	    this.dupeStreetAdd2 = dupeStreetAdd2;
  }
  
  public void setDupeMetaStreetAdd1(String dupeMetaStreetAdd1) {
    this.dupeMetaStreetAdd1 = dupeMetaStreetAdd1;
  }

  public void setDupeMetaStreetAdd2(String dupeMetaStreetAdd2) {
    this.dupeMetaStreetAdd2 = dupeMetaStreetAdd2;
  }

  public void setDupeAdd1(String dupeAdd1) {
    this.dupeAdd1 = dupeAdd1;
  }

  public void setDupeAdd2(String dupeAdd2) {
    this.dupeAdd2 = dupeAdd2;
  }

  public void setDupePCode(String dupePCode) {
    this.dupePCode = dupePCode;
  }

  public void setDupePOBox(String dupePOBox) {
    this.dupePOBox = dupePOBox;
  }

  public void setDupePOBoxExtra(String dupePOBoxExtra) {
    this.dupePOBoxExtra = dupePOBoxExtra;
  }

  public void setDupeStreetDirection(String dupeStreetDirection) {
    this.dupeStreetDirection = dupeStreetDirection;
  }

  public void setDupeRR(String dupeRR) {
    this.dupeRR = dupeRR;
  }

  public void setDupeName1(String dupeName1) {
    this.dupeName1 = dupeName1;
  }

  public void setDupeName1FirstPerson(String dupeName1FirstPerson) {
    this.dupeName1FirstPerson = dupeName1FirstPerson;
  }

  public void setDupeName1SecondPerson(String dupeName1SecondPerson) {
    this.dupeName1SecondPerson = dupeName1SecondPerson;
  }

  public void setDupeName1Normalized(String dupeName1Normalized) {
    this.dupeName1Normalized = dupeName1Normalized;
  }

  public void setDupeName2(String dupeName2) {
    this.dupeName2 = dupeName2;
  }

  public void setDupeName2FirstPerson(String dupeName2FirstPerson) {
    this.dupeName2FirstPerson = dupeName2FirstPerson;
  }

  public void setDupeName2SecondPerson(String dupeName2SecondPerson) {
    this.dupeName2SecondPerson = dupeName2SecondPerson;
  }

  public void setDupeName2Normalized(String dupeName2Normalized) {
    this.dupeName2Normalized = dupeName2Normalized;
  }
  
  public void setDupeGroupId(int dupeGroupId) {
	  this.dupeGroupId = dupeGroupId;
  }
  
  public void setDupeGroupSize(int dupeGroupSize) {
	  this.dupeGroupSize = dupeGroupSize;
  }

  public void setInId(String inId) {
    this.inId = inId;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public void setBarCode(String barCode) {
    this.barCode = barCode;
  }

  public void setRecType(String recType) {
    this.recType = recType;
  }
  
  public void setStatus(String status) {
	this.status = status;
  }
  
  public void setListNum(String listNum) {
	  this.listNum = listNum;
  }
  
  public void setDay(String day) {
  	this.day = day;
  }
  
  public void setMonth(String month) {
  	this.month = month;
  }
  
  public void setYear(String year) {
  	this.year = year;
  }
  
  public void setLatitude(String latitude) {
  	this.latitude = latitude;
  }
  
  public void setLongitude(String longitude) {
  	this.longitude = longitude;
  }
  
  public void setQuantity(String quantity) {
  	this.quantity = quantity;
  }
  
  public void setPhone1(String phone1) {
	  this.phone1 = phone1;
  }
  
  public void setPhone2(String phone2) {
	  this.phone2 = phone2;
  }
  
  public void setMobilePhone(String mobilePhone) {
	  this.mobilePhone = mobilePhone;
  }
  
  public void setEmail(String email) {
	  this.email = email;
  }
  
  public void setStoreId(String storeId) {
	  this.storeId = storeId;
  }
  
  public void setDateFrom(String dateFrom) {
	  this.dateFrom = dateFrom;
  }
  
  public void setDateTo(String dateTo) {
	  this.dateTo = dateTo;
  }
  
  public void setTimeFrom(String timeFrom) {
	  this.timeFrom = timeFrom;
  }
  
	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public void setIsProof(String isProof) {
		this.isProof = isProof;
	}
	
	public void setMisc1(String misc1) {
		this.misc1 = misc1;
	}
	
	public void setMisc2(String misc2) {
		this.misc2 = misc2;
	}

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public void setFstName(String fstName) {
    this.fstName = fstName;
  }

  public void setMidName(String midName) {
    this.midName = midName;
  }

  public void setLstName(String lstName) {
    this.lstName = lstName;
  }

  public void setNam1(String nam1) {
    this.nam1 = nam1;
  }

  public void setNam1_2(String nam1_2) {
    this.nam1_2 = nam1_2;
  }

  public void setNam2(String nam2) {
    this.nam2 = nam2;
  }

  public void setSpousePrefix(String spousePrefix) {
	    this.spousePrefix = spousePrefix;
	  }

	  public void setSpouseSuffix(String spouseSuffix) {
	    this.spouseSuffix = spouseSuffix;
	  }

	  public void setSpouseFstName(String spouseFstName) {
	    this.spouseFstName = spouseFstName;
	  }

	  public void setSpouseMidName(String spouseMidName) {
	    this.spouseMidName = spouseMidName;
	  }

	  public void setSpouseLstName(String spouseLstName) {
	    this.spouseLstName = spouseLstName;
	  }

	  public void setSpouseNam1(String spouseNam1) {
	    this.spouseNam1 = spouseNam1;
	  }

	  public void setSpouseNam2(String spouseNam2) {
	    this.spouseNam2 = spouseNam2;
	  }

  public void setNam2_2(String nam2_2) {
    this.nam2_2 = nam2_2;
  }

  public void setCmpny(String cmpny) {
    this.cmpny = cmpny;
  }

  public void setCmpny_2(String cmpny_2) {
    this.cmpny_2 = cmpny_2;
  }
  
  public void setCmpnyCn(String cmpnyCn) {
	  this.cmpnyCn = cmpnyCn;
  }

  public void setDearSal(String dearSal) {
    this.dearSal = dearSal;
  }

  public void setParaSal(String paraSal) {
    this.paraSal = paraSal;
  }
  
  public void setCmpnySal(String cmpnySal) {
	  this.cmpnySal = cmpnySal;
  }
  
  public void setCmpnyAdd1(String cmpnyAdd1) {
	this.cmpnyAdd1 = cmpnyAdd1;
  }
  
  public void setCmpnyAdd2(String cmpnyAdd2) {
	this.cmpnyAdd2 = cmpnyAdd2;
  }

  public void setAdd1(String add1) {
    this.add1 = add1;
  }

  public void setAdd1_2(String add1_2) {
    this.add1_2 = add1_2;
  }

  public void setAdd2(String add2) {
    this.add2 = add2;
  }

  public void setAdd2_2(String add2_2) {
    this.add2_2 = add2_2;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setProv(String prov) {
    this.prov = prov;
  }

  public void setPCode(String pCode) {
    this.pCode = pCode;
  }

  public void setCntry(String cntry) {
	  this.cntry = cntry;
  }
  
  public void setStreetNum(String streetNum) {
	  this.streetNum = streetNum;
  }
  
  public void setStreetDir(String streetDir) {
	  this.streetDir = streetDir;
  }
  
  public void setStreetNam(String streetNam) {
	  this.streetNam = streetNam;
  }
  
  public void setStreetDef(String streetDef) {
	  this.streetDef = streetDef;
  }
  
  public void setAptNum(String aptNum) {
	  this.aptNum = aptNum;
  }
  
  public void setServiceAdd(String serviceAdd) {
	  this.serviceAdd = serviceAdd;
  }
  
  public void setServiceCity(String serviceCity) {
	  this.serviceCity = serviceCity;
  }
  
  public void setPoBox(String poBox) {
	  this.poBox = poBox;
  }
  
  public void setCareOf(String careOf) {
	  this.careOf = careOf;
  }
  
  public void setNcoa(String ncoa) {
	    this.ncoa = ncoa;
	  }
  
  public void setDnm(String dnm) {
	    this.dnm = dnm;
	  }
  
  public void setDeceased(String deceased) {
	    this.deceased = deceased;
	  }

  public void setDnAmtArr(String dnAmtArr) {
    this.dnAmtArr = dnAmtArr;
  }

  public void setDnDatArr(String dnDatArr) {
    this.dnDatArr = dnDatArr;
  }

  public void setNumDn(String numDn) {
    this.numDn = numDn;
  }
  
  public void setNumDnLst12Mnths(String numDnLst12Mnths) {
	  this.numDnLst12Mnths = numDnLst12Mnths;
  }

  public void setTtlDnAmt(String ttlDnAmt) {
    this.ttlDnAmt = ttlDnAmt;
  }
  
  public void setTtlDnAmtLst12Mnths(String ttlDnAmtLst12Mnths) {
	  this.ttlDnAmtLst12Mnths = ttlDnAmtLst12Mnths;
  }
  
  public void setTtlDnAmtCrntYr(String ttlDnAmtCrntYr) {
	  this.ttlDnAmtCrntYr = ttlDnAmtCrntYr;
  }
  
  public void setTtlDnAmtLstYr(String ttlDnAmtLstYr) {
	  this.ttlDnAmtLstYr = ttlDnAmtLstYr;
  }

  public void setFstDnAmt(String fstDnAmt) {
    this.fstDnAmt = fstDnAmt;
  }

  public void setLstDnAmt(String lstDnAmt) {
    this.lstDnAmt = lstDnAmt;
  }

  public void setLrgDnAmt(String lrgDnAmt) {
    this.lrgDnAmt = lrgDnAmt;
  }

  public void setSDnAmt(String smlDnAmt) {
    this.smlDnAmt = smlDnAmt;
  }
  
  public void setLMDnAmt(String lMDnAmt) {
	  this.lMDnAmt = lMDnAmt;
  }

  public void setFstDnDat(String fstDnDat) {
    this.fstDnDat = fstDnDat;
  }

  public void setLstDnDat(String lstDnDat) {
    this.lstDnDat = lstDnDat;
  }
  
  public void setLMDnDat(String lMDnDat) {
	  this.lMDnDat = lMDnDat;
  }
  
  public void setPenultAmt(String penultAmt) {
	  this.penultAmt = penultAmt;
  }
  
  public void setPenultDat(String penultDat) {
	  this.penultDat = penultDat;
  }

  public void setDn1Amt(String dn1Amt) {
    this.dn1Amt = dn1Amt;
  }

  public void setDn2Amt(String dn2Amt) {
    this.dn2Amt = dn2Amt;
  }

  public void setDn3Amt(String dn3Amt) {
    this.dn3Amt = dn3Amt;
  }

  public void setDn4Amt(String dn4Amt) {
    this.dn4Amt = dn4Amt;
  }

  public void setODnAmt(String oDnAmt) {
    this.oDnAmt = oDnAmt;
  }

  public void setMDn1Amt(String mDn1Amt) {
    this.mDn1Amt = mDn1Amt;
  }

  public void setMDn2Amt(String mDn2Amt) {
    this.mDn2Amt = mDn2Amt;
  }

  public void setMDn3Amt(String mDn3Amt) {
    this.mDn3Amt = mDn3Amt;
  }

  public void setMDn4Amt(String mDn4Amt) {
    this.mDn4Amt = mDn4Amt;
  }

  public void setMODnAmt(String mODnAmt) {
    this.mODnAmt = mODnAmt;
  }

  public void setProvide1(String provide1) {
    this.provide1 = provide1;
  }

  public void setProvide2(String provide2) {
    this.provide2 = provide2;
  }

  public void setProvide3(String provide3) {
    this.provide3 = provide3;
  }
 
  public void setProvide4(String provide4) {
	this.provide4 = provide4;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public void setABGroup(String abGroup) {
    this.abGroup = abGroup;
  }

  public void setAppeal(String appeal) {
    this.appeal = appeal;
  }

  public void setSeg(String seg) {
    this.seg = seg;
  }

  public void setSegCode(String segCode) {
    this.segCode = segCode;
  }

  public void setLetVer(String letVer) {
    this.letVer = letVer;
  }

  public void setPkgVer(String pkgVer) {
    this.pkgVer = pkgVer;
  }

  public void setRepVer(String repVer) {
    this.repVer = repVer;
  }

  public void setCodeLine(String codeLine) {
    this.codeLine = codeLine;
  }

  public void setMeanAmt(String meanAmt) {
    this.meanAmt = meanAmt;
  }
  
  public void setMedian(String median) {
	  this.median = median;
  }

  public void setSDevAmt(String sDevAmt) {
    this.sDevAmt = sDevAmt;
  }

  public void setRScore(String rScore) {
    this.rScore = rScore;
  }

  public void setFScore(String fScore) {
    this.fScore = fScore;
  }

  public void setMScore(String mScore) {
    this.mScore = mScore;
  }

  public void setRfmScore(String rfmScore) {
    this.rfmScore = rfmScore;
  }
  
  public void setLength(String length) {
	  this.length = length;
  }
  
  public void setWidth(String width) {
	  this.width = width;
  }
  
  public void setHeight(String height) {
	  this.height = height;
  }
  
  public void setWeight(String weight) {
	  this.weight = weight;
  }

@Override
public String toString() {
	return "Record [dfId=" + dfId + ", dfInData=" + Arrays.toString(dfInData) + ", isDupe=" + isDupe + ", spFilt1="
			+ spFilt1 + ", spFilt2=" + spFilt2 + ", dupeStreetAdd1=" + dupeStreetAdd1 + ", dupeStreetAdd2="
			+ dupeStreetAdd2 + ", dupeMetaStreetAdd1=" + dupeMetaStreetAdd1 + ", dupeMetaStreetAdd2="
			+ dupeMetaStreetAdd2 + ", dupeAdd1=" + dupeAdd1 + ", dupeAdd2=" + dupeAdd2 + ", dupePCode=" + dupePCode
			+ ", dupePOBox=" + dupePOBox + ", dupePOBoxExtra=" + dupePOBoxExtra + ", dupeStreetDirection="
			+ dupeStreetDirection + ", dupeRR=" + dupeRR + ", dupeName1=" + dupeName1 + ", dupeName1FirstPerson="
			+ dupeName1FirstPerson + ", dupeName1SecondPerson=" + dupeName1SecondPerson + ", dupeName1Normalized="
			+ dupeName1Normalized + ", dupeName2=" + dupeName2 + ", dupeName2FirstPerson=" + dupeName2FirstPerson
			+ ", dupeName2SecondPerson=" + dupeName2SecondPerson + ", dupeName2Normalized=" + dupeName2Normalized
			+ ", dupeGroupId=" + dupeGroupId + ", dupeGroupSize=" + dupeGroupSize + ", inId=" + inId + ", lang=" + lang
			+ ", barCode=" + barCode + ", recType=" + recType + ", status=" + status + ", listNum=" + listNum + ", day="
			+ day + ", month=" + month + ", year=" + year + ", latitude=" + latitude + ", longitude=" + longitude
			+ ", quantity=" + quantity + ", phone1=" + phone1 + ", phone2=" + phone2 + ", mobilePhone=" + mobilePhone
			+ ", email=" + email + ", storeId=" + storeId + ", dateFrom=" + dateFrom + ", dateTo=" + dateTo
			+ ", timeFrom=" + timeFrom + ", timeTo=" + timeTo + ", ref=" + ref + ", comments=" + comments + ", isProof="
			+ isProof + ", misc1=" + misc1 + ", misc2=" + misc2 + ", prefix=" + prefix + ", suffix=" + suffix
			+ ", fstName=" + fstName + ", midName=" + midName + ", lstName=" + lstName + ", nam1=" + nam1 + ", nam1_2="
			+ nam1_2 + ", nam2=" + nam2 + ", nam2_2=" + nam2_2 + ", spousePrefix=" + spousePrefix + ", spouseSuffix="
			+ spouseSuffix + ", spouseFstName=" + spouseFstName + ", spouseMidName=" + spouseMidName
			+ ", spouseLstName=" + spouseLstName + ", spouseNam1=" + spouseNam1 + ", spouseNam2=" + spouseNam2
			+ ", cmpny=" + cmpny + ", cmpny_2=" + cmpny_2 + ", cmpnyCn=" + cmpnyCn + ", dearSal=" + dearSal
			+ ", paraSal=" + paraSal + ", cmpnySal=" + cmpnySal + ", cmpnyAdd1=" + cmpnyAdd1 + ", cmpnyAdd2="
			+ cmpnyAdd2 + ", add1=" + add1 + ", add1_2=" + add1_2 + ", add2=" + add2 + ", add2_2=" + add2_2 + ", city="
			+ city + ", prov=" + prov + ", pCode=" + pCode + ", cntry=" + cntry + ", streetNum=" + streetNum
			+ ", streetDir=" + streetDir + ", streetNam=" + streetNam + ", streetDef=" + streetDef + ", aptNum="
			+ aptNum + ", serviceAdd=" + serviceAdd + ", serviceCity=" + serviceCity + ", poBox=" + poBox + ", careOf="
			+ careOf + ", ncoa=" + ncoa + ", dnm=" + dnm + ", deceased=" + deceased + ", dnAmtArr=" + dnAmtArr
			+ ", dnDatArr=" + dnDatArr + ", numDn=" + numDn + ", numDnLst12Mnths=" + numDnLst12Mnths + ", ttlDnAmt="
			+ ttlDnAmt + ", ttlDnAmtLst12Mnths=" + ttlDnAmtLst12Mnths + ", ttlDnAmtCrntYr=" + ttlDnAmtCrntYr
			+ ", ttlDnAmtLstYr=" + ttlDnAmtLstYr + ", fstDnAmt=" + fstDnAmt + ", lstDnAmt=" + lstDnAmt + ", lMDnAmt="
			+ lMDnAmt + ", lrgDnAmt=" + lrgDnAmt + ", smlDnAmt=" + smlDnAmt + ", fstDnDat=" + fstDnDat + ", lstDnDat="
			+ lstDnDat + ", lMDnDat=" + lMDnDat + ", penultAmt=" + penultAmt + ", penultDat=" + penultDat + ", dn1Amt="
			+ dn1Amt + ", dn2Amt=" + dn2Amt + ", dn3Amt=" + dn3Amt + ", dn4Amt=" + dn4Amt + ", oDnAmt=" + oDnAmt
			+ ", mDn1Amt=" + mDn1Amt + ", mDn2Amt=" + mDn2Amt + ", mDn3Amt=" + mDn3Amt + ", mDn4Amt=" + mDn4Amt
			+ ", mODnAmt=" + mODnAmt + ", provide1=" + provide1 + ", provide2=" + provide2 + ", provide3=" + provide3
			+ ", provide4=" + provide4 + ", priority=" + priority + ", abGroup=" + abGroup + ", appeal=" + appeal
			+ ", seg=" + seg + ", segCode=" + segCode + ", letVer=" + letVer + ", pkgVer=" + pkgVer + ", repVer="
			+ repVer + ", codeLine=" + codeLine + ", meanAmt=" + meanAmt + ", median=" + median + ", sDevAmt=" + sDevAmt
			+ ", rScore=" + rScore + ", fScore=" + fScore + ", mScore=" + mScore + ", rfmScore=" + rfmScore
			+ ", length=" + length + ", width=" + width + ", height=" + height + ", weight=" + weight + "]";
}

}