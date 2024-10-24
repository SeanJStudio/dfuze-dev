package com.mom.dfuze.io;

import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mom.dfuze.data.FileExtensions;
import com.mom.dfuze.data.UserPrefs;
import com.mom.dfuze.ui.DelimiterSelectDialog;
import com.mom.dfuze.ui.TableSelectDialog;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.UserInputDialog;

public class FileIngestor {
	
	private FileIngestor() {}
	
	public static List<List<String>> ingest() throws Exception {

	    List<List<String>> ingestedFile = null;

	    try {      
	      JFileChooser fileChooser = new JFileChooser(UserPrefs.getLastUsedFolder());
	      fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(String.join(" ", FileExtensions.FILE_IMPORT_EXTENSIONS_LIST), "accdb", "csv", "cert-1", "dat", "dbf",
	              "mdb", "txt", "xlsx", "xlsm", "xls"));
	      fileChooser.setAcceptAllFileFilterUsed(true);

	      int returnVal = fileChooser.showOpenDialog(UiController.getMainFrame());

	      if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	  TableSelectDialog tableSelectDialog = null;

	        File file = fileChooser.getSelectedFile();
	        String fileExt = file.getName();
	        if (fileExt.contains("."))
	          fileExt = fileExt.substring(fileExt.lastIndexOf("."), fileExt.length());

	        if (fileExt.toLowerCase().equals(FileExtensions.DBF))
	        	ingestedFile = DbfReader.readRaw(file);
	        else if (fileExt.toLowerCase().equals(FileExtensions.ACCDB) || fileExt.toLowerCase().equals(FileExtensions.MDB)) {
				String[] tableNames = AccessReader.readTableNames(file);
				tableSelectDialog = new TableSelectDialog(UiController.getMainFrame(), tableNames);
				tableSelectDialog.disableCreateHeaders();
				tableSelectDialog.setSingleSelection();
				tableSelectDialog.setModalityType(ModalityType.APPLICATION_MODAL);
				
				if(tableNames.length > 1)
					tableSelectDialog.setVisible(true);
				
				if (tableSelectDialog.isNextPressed() || tableNames.length == 1) {
					String tableName = tableSelectDialog.getSelectedTables().get(0);
					ingestedFile = AccessReader.readRaw(file, tableName);
				} else {
					throw new Exception("Import cancelled, please restart the job");
				}

	        } else if (fileExt.toLowerCase().equals(FileExtensions.XLSX) || fileExt.toLowerCase().equals(FileExtensions.XLSM)) {
	        	
	        	String password = "";
				boolean isEncrypted = ExcelReader.isXLSXEncrypted(file.getAbsolutePath());
				
				if(isEncrypted) {
					UserInputDialog newFieldDialog = new UserInputDialog(UiController.getMainFrame(), "Enter password for " + file.getName());
					newFieldDialog.setVisible(true);
					
					if(newFieldDialog.getIsNextPressed())
						password = newFieldDialog.getUserInput();
				}
	        	
	          String[] tableNames = ExcelReader.readXLSXSheetNames(file, isEncrypted, password);
	          tableSelectDialog = new TableSelectDialog(UiController.getMainFrame(), tableNames);
	          tableSelectDialog.disableCreateHeaders();
	          tableSelectDialog.setSingleSelection();
	          tableSelectDialog.setModalityType(ModalityType.APPLICATION_MODAL);

	          if(tableNames.length > 1)
	        	  tableSelectDialog.setVisible(true);

	          if (tableSelectDialog.isNextPressed() || tableNames.length == 1) {
	        	  String tableName = tableSelectDialog.getSelectedTables().get(0);
	        	  ingestedFile = ExcelReader.readXLSXRaw(file, tableName, isEncrypted, password);
	          } else {
	        	  throw new Exception("Import cancelled, please restart the job");
	          }

	        } else if (fileExt.toLowerCase().equals(FileExtensions.XLS)) {
	        	
	        	String password = "";
				boolean isEncrypted = ExcelReader.isXLSEncrypted(file.getAbsolutePath());
				
				if(isEncrypted) {
					UserInputDialog newFieldDialog = new UserInputDialog(UiController.getMainFrame(), "Enter password for " + file.getName());
					newFieldDialog.setVisible(true);
					
					if(newFieldDialog.getIsNextPressed())
						password = newFieldDialog.getUserInput();
				}
	        	
	          String[] tableNames = ExcelReader.readXLSSheetNames(file, isEncrypted, password);
	          tableSelectDialog = new TableSelectDialog(UiController.getMainFrame(), tableNames);
	          tableSelectDialog.disableCreateHeaders();
	          tableSelectDialog.setSingleSelection();
	          tableSelectDialog.setModalityType(ModalityType.APPLICATION_MODAL);

	          if(tableNames.length > 1)
	        	  tableSelectDialog.setVisible(true);

	          if (tableSelectDialog.isNextPressed() || tableNames.length == 1) {
	        	  String tableName = tableSelectDialog.getSelectedTables().get(0);
	        	  ingestedFile = ExcelReader.readXLSRaw(file, tableName, isEncrypted, password);
	          } else {
	        	  throw new Exception("Import cancelled, please restart the job");
	          }

	        } else if (fileExt.toLowerCase().equals(FileExtensions.CSV) || fileExt.toLowerCase().equals(FileExtensions.CERT1) || fileExt.toLowerCase().equals(FileExtensions.TXT) || fileExt.toLowerCase().equals(FileExtensions.DAT)) {
	          String previewText = TextReader.previewText(file);
	          DelimiterSelectDialog delimiterSelectDialog = new DelimiterSelectDialog(UiController.getMainFrame(), previewText);
	          delimiterSelectDialog.getChckbxFixedWidth().setEnabled(false);
	          delimiterSelectDialog.setVisible(true);

	          if (delimiterSelectDialog.getIsNextPressed()) { // dont forget to get the encoding for text based files
	        	  String charsetName = TextReader.getCharset(file);
	              
	        	  FileInputStream fis = new FileInputStream(file);
	              UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(fis);
	              InputStreamReader isr = new InputStreamReader(ubis, charsetName);
	  			
	  			// see https://stackoverflow.com/questions/1835430/byte-order-mark-screws-up-file-reading-in-java/1835529#1835529
	  			ubis.skipBOM();
	        	  
	            FileReader in = new FileReader(file);

	            
	            ingestedFile = org.simpleflatmapper.csv.CsvParser.separator(delimiterSelectDialog.getDelimiterField()).stream(isr).map(Arrays::asList)
	                    .collect(Collectors.toList());
	            in.close();
	          }
	        } else {
	          throw new Exception("The selected file type is not supported.");
	        }

	      } else {
	        throw new Exception("File ingestion failed");
	      }
	    } catch (Exception e) {
	      throw new Exception(e.getMessage());
	    }

	    return ingestedFile;

	  }

}
