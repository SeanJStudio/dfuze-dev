package com.mom.dfuze.io;

import java.io.File;

import com.mom.dfuze.data.FileExtensions;

public class FileExporter {

	private FileExporter() {}

	public static void exportData(String[] headers, String[][] data, File file) throws Exception {
	
		String fileExt = file.getName();
		if (fileExt.contains("."))
			fileExt = fileExt.substring(fileExt.lastIndexOf("."), fileExt.length());
		
		// ensure a valid extension is used
		if(!FileExtensions.FILE_EXPORT_EXTENSIONS_LIST.contains(fileExt))
			throw new Exception("Invalid file extension: " + fileExt);
		
		switch(fileExt.toLowerCase()) {
		case FileExtensions.DBF:
			DbfWriter.write(file, headers, data);
			break;
		case FileExtensions.MDB:
		case FileExtensions.ACCDB:
			AccessWriter.write(file, headers, data);
			break;
		case FileExtensions.XLSX:
			XLSXWriter.write(file, headers, data, false, false);
			break;
		case FileExtensions.TXT:
		case FileExtensions.DAT:
		case FileExtensions.CSV:
				TextWriter.write(file, ',', true, headers, data);
			break;
		default:
			throw new Exception("Invalid file extension: " + fileExt);	
		}
		
	}

}
