package com.mom.dfuze.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.io.AccessReader;
import com.mom.dfuze.io.AccessWriter;

public class FieldPref {

	private static final File FIELD_PREFS_FILE = new File("fields/field_prefs.mdb");
	private static final File FIELD_PREFS_TEMP_FILE = new File("fields/temp_field_prefs.mdb");
	
	public static final String FIELD_PREFS_TABLE = "FIELD_PREFS";

	public static final int FIELD_PREFS_DFUZE_INDEX = 0;
	public static final int FIELD_PREFS_USER_INDEX = 1;

	public static final String DFUZE_FIELD_NAME_HEADER = "DFUZE_FIELD";
	public static final String USER_FIELD_NAME_HEADER = "USER_FIELD";
	
	public static final String[] FIELD_PREFS_HEADERS = {DFUZE_FIELD_NAME_HEADER, USER_FIELD_NAME_HEADER};

	private FieldPref() {}

	public static ArrayList<FieldPrefField> getFieldPrefs() throws ApplicationException {
		
		try {
			String[][] data = AccessReader.readAsStringArray(FIELD_PREFS_FILE, FIELD_PREFS_TABLE);
			ArrayList<FieldPrefField> fpfList = new ArrayList<>();

			for(String[] row : data) {
				FieldPrefField fpf = new FieldPrefField(row[FIELD_PREFS_DFUZE_INDEX], row[FIELD_PREFS_USER_INDEX]);
				fpfList.add(fpf);
			}

			return fpfList;

		}catch(Exception e) {
			throw new ApplicationException(e);
		}
	}

	public static void updateFieldPrefs(ArrayList<FieldPrefField> fpfList) throws ApplicationException {
		try{
			fpfList.sort(Comparator.comparing(FieldPrefField::getDfuzeFieldName));
			String[][] data = new String[fpfList.size()][FIELD_PREFS_HEADERS.length];

			int counter = 0;
			for(FieldPrefField fpf : fpfList) {
				data[counter][FIELD_PREFS_DFUZE_INDEX] = fpf.getDfuzeFieldName();
				data[counter][FIELD_PREFS_USER_INDEX] = fpf.getUserFieldName();
				++counter;
			}

			AccessWriter.dropTable(FIELD_PREFS_FILE, FIELD_PREFS_TABLE);
			AccessWriter.writeTableToDatabase255(FIELD_PREFS_FILE, FIELD_PREFS_HEADERS, data, FIELD_PREFS_TABLE);
		}catch(Exception e) {
			throw new ApplicationException(e);
		}
	}

	public static void initDatabase() throws ApplicationException {
		try {
			if(AccessWriter.createDatabase(FIELD_PREFS_FILE)) {

				ArrayList<FieldPrefField> fpfList = new ArrayList<>();
				String userFieldName = "";

				for(String dfuzeHeader : UserData.allDfHeaders)
					fpfList.add(new FieldPrefField(dfuzeHeader, userFieldName));

				fpfList.sort(Comparator.comparing(FieldPrefField::getDfuzeFieldName));
				String[][] data = new String[fpfList.size()][FIELD_PREFS_HEADERS.length];

				int counter = 0;
				for(FieldPrefField tempFpf : fpfList) {
					data[counter][FIELD_PREFS_DFUZE_INDEX] = tempFpf.getDfuzeFieldName();
					data[counter][FIELD_PREFS_USER_INDEX] = tempFpf.getUserFieldName();
					++counter;
				}

				AccessWriter.writeTableToDatabase255(FIELD_PREFS_FILE, FIELD_PREFS_HEADERS, data, FIELD_PREFS_TABLE);
			}
		}catch(Exception e) {
			throw new ApplicationException(e);
		}
	}

	public static FieldPrefField getFieldPrefsField(String dfuzeFieldName) throws ApplicationException {
		ArrayList<FieldPrefField> fpfList = getFieldPrefs();

		for(FieldPrefField fpf : fpfList) {
			if(fpf.getDfuzeFieldName().equalsIgnoreCase(dfuzeFieldName))
					return fpf;
		}

		throw new ApplicationException("Could not find " + dfuzeFieldName + " in " + FIELD_PREFS_TABLE);

	}
	
	public static void validateFieldPrefs() throws Exception {
		System.out.println("validating fieldPrefFields");
		
		ArrayList<FieldPrefField> fpfList = getFieldPrefs();
		ArrayList<FieldPrefField> newFpfList = new ArrayList<>();
		HashSet<String> usedHeaders = new HashSet<>();


			for(FieldPrefField fpf : fpfList) {
				if(Arrays.asList(UserData.allDfHeaders).contains(fpf.getDfuzeFieldName())) {
					newFpfList.add(fpf);
					usedHeaders.add(fpf.getDfuzeFieldName());
				}
			}
			
			String userFieldName = "";
			
			for(String dfuzeHeader : UserData.allDfHeaders) {
				if(usedHeaders.add(dfuzeHeader))
					newFpfList.add(new FieldPrefField(dfuzeHeader, userFieldName));
			}

			updateFieldPrefs(newFpfList);
	}

	public static void buildNewJobPrefDb() throws ApplicationException {
		AccessWriter.makeCopy(FIELD_PREFS_FILE, FIELD_PREFS_TEMP_FILE);
		FIELD_PREFS_FILE.delete();
		FIELD_PREFS_TEMP_FILE.renameTo(FIELD_PREFS_FILE);
	}

	public static void setup(Job[] jobs) throws Exception {
		initDatabase(); // ensure the field prefs database exists
		validateFieldPrefs(); // ensures all fields needed are in the table
		buildNewJobPrefDb(); // since the file size increases, make a copy and delete the old one
	}

}
