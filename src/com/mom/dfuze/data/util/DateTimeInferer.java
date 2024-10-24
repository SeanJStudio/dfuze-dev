package com.mom.dfuze.data.util;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

public class DateTimeInferer {

	public static String MDYYYYSLASHFormat = "M/d/yyyy";
	
	private DateTimeInferer() {}

	// infer date format for userdata
	public static String inferFormat(UserData userData, String dateTimeFieldName) {

		Map<String, Integer> dateTimeFormatMap = buildMap();

		int fieldIndex = -1;

		if(Arrays.asList(userData.getInHeaders()).contains(dateTimeFieldName))
			fieldIndex = Arrays.asList(userData.getInHeaders()).indexOf(dateTimeFieldName);

		for (int i = 0; i < userData.getRecordList().size(); i++) {
			Record record = userData.getRecordList().get(i);
			String lstDnDat = (fieldIndex == -1) ? UserData.getRecordFieldByName(dateTimeFieldName, record) : record.getDfInData()[fieldIndex];

			lstDnDat = lstDnDat.replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/");

			for (Map.Entry<String, Integer> entry : dateTimeFormatMap.entrySet())
				if (Validators.isStringOfDateFormat(lstDnDat, entry.getKey()))
					entry.setValue(entry.getValue() + 1);
		}

		String formatToUse = "";
		int largestFormatMatch = -1;
		for (Map.Entry<String, Integer> entry : dateTimeFormatMap.entrySet()) {
			//System.out.println(entry.getKey() + " " + entry.getValue());
			if (entry.getValue() > largestFormatMatch) {
				formatToUse = entry.getKey();
				largestFormatMatch = entry.getValue();
			}
		}

		//System.out.println(formatToUse);
		return formatToUse;
	}
	
	// infer date format from a records in-data
	public static String inferFormat(List<Record> recordList, int inDataDatetimeFieldIndex) throws Exception{

		Map<String, Integer> dateTimeFormatMap = buildMap();

		for (int i = 0; i < recordList.size(); i++) {
			Record record = recordList.get(i);
			String lstDnDat = record.getDfInData()[inDataDatetimeFieldIndex];

			lstDnDat = lstDnDat.replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/");

			for (Map.Entry<String, Integer> entry : dateTimeFormatMap.entrySet())
				if (Validators.isStringOfDateFormat(lstDnDat, entry.getKey()))
					entry.setValue(entry.getValue() + 1);
		}

		String formatToUse = "";
		int largestFormatMatch = -1;
		for (Map.Entry<String, Integer> entry : dateTimeFormatMap.entrySet()) {
			//System.out.println(entry.getKey() + " " + entry.getValue());
			if (entry.getValue() > largestFormatMatch) {
				formatToUse = entry.getKey();
				largestFormatMatch = entry.getValue();
			}
		}

		//System.out.println(formatToUse);
		return formatToUse;
	}
	
	// infer date format from a records member
		public static String inferFormat(List<Record> recordList, String dfFieldName) throws Exception{

			Map<String, Integer> dateTimeFormatMap = buildMap();

			for (int i = 0; i < recordList.size(); i++) {
				Record record = recordList.get(i);
				String lstDnDat = UserData.getRecordFieldByName(dfFieldName, record);

				lstDnDat = lstDnDat.replaceAll("[^a-zA-Z0-9]", "/").replaceAll("/+", "/");

				for (Map.Entry<String, Integer> entry : dateTimeFormatMap.entrySet())
					if (Validators.isStringOfDateFormat(lstDnDat, entry.getKey()))
						entry.setValue(entry.getValue() + 1);
			}

			String formatToUse = "";
			int largestFormatMatch = -1;
			for (Map.Entry<String, Integer> entry : dateTimeFormatMap.entrySet()) {
				//System.out.println(entry.getKey() + " " + entry.getValue());
				if (entry.getValue() > largestFormatMatch) {
					formatToUse = entry.getKey();
					largestFormatMatch = entry.getValue();
				}
			}

			//System.out.println(formatToUse);
			return formatToUse;
		}


	private static Map<String, Integer> buildMap() {
		// Day month yyyy with slash
		String DMYYYYSLASHFormat = "d/M/yyyy";
		String DMMYYYYSLASHFormat = "d/MM/yyyy";
		String DMMMYYYYSLASHFormat = "d/MMM/yyyy";
		String DMMMMYYYYSLASHFormat = "d/MMMM/yyyy";
		String DDMYYYYSLASHFormat = "dd/M/yyyy";
		String DDMMYYYYSLASHFormat = "dd/MM/yyyy";
		String DDMMMYYYYSLASHFormat = "dd/MMM/yyyy";
		String DDMMMMYYYYSLASHFormat = "dd/MMMM/yyyy";

		// Day month yy
		String DMYYSLASHFormat = "d/M/yy";
		String DMMYYSLASHFormat = "d/MM/yy";
		String DMMMYYSLASHFormat = "d/MMM/yy";
		String DMMMMYYSLASHFormat = "d/MMMM/yy";
		String DDMYYSLASHFormat = "dd/M/yy";
		String DDMMYYSLASHFormat = "dd/MM/yy";
		String DDMMMYYSLASHFormat = "dd/MMM/yy";
		String DDMMMMYYSLASHFormat = "dd/MMMM/yy";

		// Month Day yyyy with slash
		String MDYYYYSLASHFormat = "M/d/yyyy";
		String MDDYYYYSLASHFormat = "M/dd/yyyy";
		String MMDYYYYSLASHFormat = "MM/d/yyyy";
		String MMDDYYYYSLASHFormat = "MM/dd/yyyy";
		String MMMDYYYYSLASHFormat = "MMM/d/yyyy";
		String MMMDDYYYYSLASHFormat = "MMM/dd/yyyy";
		String MMMMDYYYYSLASHFormat = "MMMM/d/yyyy";
		String MMMMDDYYYYSLASHFormat = "MMMM/dd/yyyy";

		// Month Day yy with slash
		String MDYYSLASHFormat = "M/d/yy";
		String MDDYYSLASHFormat = "M/dd/yy";
		String MMDYYSLASHFormat = "MM/d/yy";
		String MMDDYYSLASHFormat = "MM/dd/yy";
		String MMMDYYSLASHFormat = "MMM/d/yy";
		String MMMDDYYSLASHFormat = "MMM/dd/yy";
		String MMMMDYYSLASHFormat = "MMMM/d/yy";
		String MMMMDDYYSLASHFormat = "MMMM/dd/yy";

		// yyyy month day slash
		String YYYYMDSLASHFormat = "yyyy/M/d";
		String YYYYMMDSLASHFormat = "yyyy/MM/d";
		String YYYYMMMDSLASHFormat = "yyyy/MMM/d";
		String YYYYMMMMDSLASHFormat = "yyyy/MMMM/d";
		String YYYYMDDSLASHFormat = "yyyy/M/dd";
		String YYYYMMDDSLASHFormat = "yyyy/MM/dd";
		String YYYYMMMDDSLASHFormat = "yyyy/MMM/dd";
		String YYYYMMMMDDSLASHFormat = "yyyy/MMMM/dd";

		// yyyy day month slash
		String YYYYDMSLASHFormat = "yyyy/d/M";
		String YYYYDMMSLASHFormat = "yyyy/d/MM";
		String YYYYDMMMSLASHFormat = "yyyy/d/MMM";
		String YYYYDMMMMSLASHFormat = "yyyy/d/MMMM";
		String YYYYDDMSLASHFormat = "yyyy/dd/M";
		String YYYYDDMMSLASHFormat = "yyyy/dd/MM";
		String YYYYDDMMMSLASHFormat = "yyyy/dd/MMM";
		String YYYYDDMMMMSLASHFormat = "yyyy/dd/MMMM";

		Map<String, Integer> dateTimeFormatMap = new HashMap<>();

		dateTimeFormatMap.put(DMYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(DMMYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(DMMMYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(DMMMMYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(DDMYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(DDMMYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(DDMMMYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(DDMMMMYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(DMYYSLASHFormat, 0);
		dateTimeFormatMap.put(DMMYYSLASHFormat, 0);
		dateTimeFormatMap.put(DMMMYYSLASHFormat, 0);
		dateTimeFormatMap.put(DMMMMYYSLASHFormat, 0);
		dateTimeFormatMap.put(DDMYYSLASHFormat, 0);
		dateTimeFormatMap.put(DDMMYYSLASHFormat, 0);
		dateTimeFormatMap.put(DDMMMYYSLASHFormat, 0);
		dateTimeFormatMap.put(DDMMMMYYSLASHFormat, 0);
		dateTimeFormatMap.put(MDYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(MDDYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMDYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMDDYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMMDYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMMDDYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMMMDYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMMMDDYYYYSLASHFormat, 0);
		dateTimeFormatMap.put(MDYYSLASHFormat, 0);
		dateTimeFormatMap.put(MDDYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMDYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMDDYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMMDYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMMDDYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMMMDYYSLASHFormat, 0);
		dateTimeFormatMap.put(MMMMDDYYSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYMDSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYMMDSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYMMMDSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYMMMMDSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYMDDSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYMMDDSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYMMMDDSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYMMMMDDSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYDMSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYDMMSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYDMMMSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYDMMMMSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYDDMSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYDDMMSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYDDMMMSLASHFormat, 0);
		dateTimeFormatMap.put(YYYYDDMMMMSLASHFormat, 0);

		return dateTimeFormatMap;
	}


}
