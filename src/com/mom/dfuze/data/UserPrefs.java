package com.mom.dfuze.data;

import java.util.prefs.Preferences;

public class UserPrefs {
	public static final Preferences pref = Preferences.userRoot();
	
	//Files
	public static final String LAST_USED_FILE_FOLDER = "DF_LAST_USED_FILE_FOLDER";
	public static final String LAST_USED_FILE_EXTENSION = "DF_LAST_USED_FILE_EXTENSION";
	public static final String LAST_USED_FILE_DELIMITER = "DF_LAST_USED_FILE_DELIMITER";
	
	//Dedupe
	public static final String LAST_USED_DEDUPE_MATCH_PERCENTAGE = "DF_LAST_USED_DEDUPE_PERCENTAGE";
	
	private UserPrefs() {}
	
	public static void setLastUsedFolder(String newLastUsedFolder) {
		pref.put(UserPrefs.LAST_USED_FILE_FOLDER, newLastUsedFolder);
	}
	
	public static String getLastUsedFolder() {
		return pref.get(UserPrefs.LAST_USED_FILE_FOLDER, "");
	}
	
	public static void setLastUsedFileExtension(String fileExtension) {
		if(FileExtensions.FILE_EXPORT_EXTENSIONS_LIST.contains(fileExtension)) {
			pref.put(UserPrefs.LAST_USED_FILE_EXTENSION, fileExtension);
		}
	}
	
	public static String getLastUsedFileExtension() {
		return pref.get(UserPrefs.LAST_USED_FILE_EXTENSION, "");
	}
	
	public static void setLastUsedFileDelimiter(String fileDelimiter) {
		pref.put(UserPrefs.LAST_USED_FILE_DELIMITER, String.valueOf(fileDelimiter));
	}
	
	public static String getLastUsedFileDelimiter() {
		return pref.get(UserPrefs.LAST_USED_FILE_DELIMITER, "");
	}
	
	public static void setLastUsedDedupeMatchPercentage(String dedupePercentage) {
		pref.put(UserPrefs.LAST_USED_DEDUPE_MATCH_PERCENTAGE, dedupePercentage);
	}
	
	public static String getLastUsedDedupeMatchPercentage() {
		return pref.get(UserPrefs.LAST_USED_DEDUPE_MATCH_PERCENTAGE, "");
	}

}
