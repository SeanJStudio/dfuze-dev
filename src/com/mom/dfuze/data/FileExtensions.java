package com.mom.dfuze.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileExtensions {
	
	// Exportable
	public static final String ACCDB = ".accdb";
	public static final String CSV = ".csv";
	public static final String DAT = ".dat";
	public static final String DBF = ".dbf";
	public static final String MDB = ".mdb";
	public static final String TABLE = "table in database";
	public static final String TXT = ".txt";
	public static final String XLSX = ".xlsx";
	
	// Non-Exportable
	public static final String CERT1 = ".cert-1";
	public static final String XLS = ".xls";
	public static final String XLSM = ".xlsm";
	
	public static final List<String> FILE_EXPORT_EXTENSIONS_LIST = new ArrayList<>(Arrays.asList(
			ACCDB,
			CSV,
			DAT,
			DBF,
			MDB,
			TABLE,
			TXT,
			XLSX
			));
	
	public static final List<String> FILE_IMPORT_EXTENSIONS_LIST = new ArrayList<>(Arrays.asList(
			ACCDB,
			CSV,
			CERT1,
			DAT,
			DBF,
			MDB,
			TXT,
			XLS,
			XLSX,
			XLSM
			));
	
	private FileExtensions() {}

}
