package com.mom.dfuze.data.jobs.ritchiebrothers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.dedupe.DedupeDialog;

public class RegularProcess implements RunRitchieBrothersBehavior {

	private final String BEHAVIOR_NAME = "Regular Process";
	protected String[] REQUIRED_FIELDS = {
			UserData.fieldName.COMPANY.getName(),
			UserData.fieldName.FIRSTNAME.getName(),
			UserData.fieldName.LASTNAME.getName(),
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.POSTALCODE.getName(),
			UserData.fieldName.COUNTRY.getName()
			};
	
	protected String DESCRIPTION = "<html>Instructions<ol><li>Check the \"Add file name to data checkbox\" below</li><li>Load the data and run the job</li><li>Once complete, export and split on the field \"dfFileName\"</li></ol>Description<ul><li>Standardizes US zipcodes</li><li>Standardizes US Country codes</li><li>Removes \"Unknown bidders\"</li><li>Dedupes data</li></ul>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";
	
	private final static Pattern USA_COUNTRY = Pattern.compile("^us|uniteds|america", Pattern.CASE_INSENSITIVE);
	private final static Pattern UNKNOWN = Pattern.compile("(?<=^|\\s)unk(?=$|\\s)|unknown|xx", Pattern.CASE_INSENSITIVE);
	private final static String USA = "USA";

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#getRunBehaviorName()
	 */
	@Override
	public String getRunBehaviorName() {
		return BEHAVIOR_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#getDescription()
	 */
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#getRequiredFields()
	 */
	@Override
	public String[] getRequiredFields() {
		return REQUIRED_FIELDS;
	}
	
	@Override
	public Boolean isFileNameRequired() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) throws ApplicationException {

		/********************************
		 * MAKE SURE FILENAME IS INCLUDED
		 ********************************/
		if(!Common.hasFileName(userData))
			throw new ApplicationException(String.format("Error: file name is not included\n\nPlease start a new job and ensure the \"Add file name to data\" checkbox is checked."));
		
		int fileIndex = 0;
		
		for(int i = 0 ; i < userData.getInHeaders().length; ++i)
			if(userData.getInHeaders()[i].equalsIgnoreCase("dfFileName"))
				fileIndex = i;
		
		String[][] data = userData.getData();
		
		Map<String, Integer> originalFileCounts = new HashMap<>();
		
		for(String[] row : data) {
			String key = row[fileIndex];
			
			if(originalFileCounts.containsKey(key))
				originalFileCounts.put(key, originalFileCounts.get(key) + 1);
			else
				originalFileCounts.put(key, 0);
		}
		
		Map<String, Integer> unknownFileCounts = new HashMap<>();
		Map<String, Integer> dedupeFileCounts = new HashMap<>();
		
		for(Entry<String, Integer> entry : originalFileCounts.entrySet())
			unknownFileCounts.put(entry.getKey(), entry.getValue());
		
		int counter = -1;

		for (int i = 0; i < data.length; i++) {
			
			boolean isUnknown = false;
			
			for(String value : data[i])
				if(UNKNOWN.matcher(value).find())
					isUnknown = true;

			
			if(isUnknown) {
				String key = data[i][fileIndex];
				unknownFileCounts.put(key, unknownFileCounts.get(key) - 1);
				continue;
			}
			
			String fnam = data[i][userData.getFstNameIndex()].trim();
			String lnam = data[i][userData.getLstNameIndex()].trim();
			String add1 = data[i][userData.getAdd1Index()].trim();
			String add2 = data[i][userData.getAdd2Index()].trim();
			String pCode = data[i][userData.getPCodeIndex()].trim();
			String cntry = fixCountry(data[i][userData.getCntryIndex()].trim());
			
			String nam1 = (fnam + " " + lnam).trim();
			
			if(cntry.equalsIgnoreCase(USA))
				pCode = Common.fixZip(pCode);
			
			data[i][userData.getPCodeIndex()] = pCode;
			data[i][userData.getCntryIndex()] = cntry;

			Record record = new Record.Builder(++counter, data[i], "", "", "").setNam1(nam1).setAdd1(add1).setAdd2(add2).setPCode(pCode).setIsDupe(false).build();

			userData.add(record); // add the record the recordList in userData

		}
		
		// set the Header fields that we have used
		userData.setDfHeaders(new String[] {
				UserData.fieldName.NAME1.getName(),
				UserData.fieldName.IS_DUPE.getName(),
		});
		
		DedupeDialog dedupeDialog = new DedupeDialog(UiController.getMainFrame());
		dedupeDialog.getComboBoxName1().setSelectedIndex(Arrays.asList(userData.getExportHeaders()).indexOf(UserData.fieldName.NAME1.getName()));
		dedupeDialog.getComboBoxIfName1Blank().setSelectedIndex(userData.getCmpnyIndex());
		dedupeDialog.setVisible(true);
		
		for(Record record : userData.getRecordList()) {
			String file = record.getDfInData()[fileIndex];
			if(dedupeFileCounts.containsKey(file))
				dedupeFileCounts.put(file, dedupeFileCounts.get(file) + 1);
			else
				dedupeFileCounts.put(file, 0);
		}
		
		String unknownReport = createUnknownReport(originalFileCounts, unknownFileCounts);
		String dedupeReport = createDedupeReport(unknownFileCounts, dedupeFileCounts);
		
		JEditorPane editorPane = new JEditorPane("text/html",
			"<html><body><pre>" + 
			unknownReport +
			"\n\n" +
			dedupeReport +
			"</pre></body></html>");
		
		editorPane.setEditable(false);
		
		JOptionPane.showMessageDialog(UiController.getMainFrame(), editorPane, "Final Report", JOptionPane.INFORMATION_MESSAGE);

	}
	
	
	private static String fixCountry(String country) {
		Matcher matcher = USA_COUNTRY.matcher(country.replaceAll("[^a-zA-Z]", ""));
		
		if(matcher.find())
			return USA;
		
		return country;
	}
	
	private static String createUnknownReport(Map<String, Integer> in, Map<String, Integer> out) {
		StringBuilder report = new StringBuilder();
		report.append("Unknown Report\n");
		report.append(String.format("%-30s %-10s %-10s %-10s  \n", "File", "Qty In", "Qty Out", "Difference"));
		report.append("------------------------------ ---------- ---------- ----------\n");
		
		for(Entry<String, Integer> entry : in.entrySet())
				report.append(String.format("%-30s %-10d %-10d %-10d\n", entry.getKey(), entry.getValue(), out.get(entry.getKey()), (entry.getValue() - out.get(entry.getKey()))));
		
		return report.toString();
	}
	
	private static String createDedupeReport(Map<String, Integer> in, Map<String, Integer> out) {
		StringBuilder report = new StringBuilder();
		report.append("Dedupe Report\n");
		report.append(String.format("%-30s %-10s %-10s %-10s  \n", "File", "Qty In", "Qty Out", "Difference"));
		report.append("------------------------------ ---------- ---------- ----------\n");
		
		for(Entry<String, Integer> entry : in.entrySet())
				report.append(String.format("%-30s %-10d %-10d %-10d\n", entry.getKey(), entry.getValue(), out.get(entry.getKey()), (entry.getValue() - out.get(entry.getKey()))));

		return report.toString();
	}

}
