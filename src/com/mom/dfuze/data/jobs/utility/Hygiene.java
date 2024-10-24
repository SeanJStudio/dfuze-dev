package com.mom.dfuze.data.jobs.utility;

import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.ui.ExportDataDialog;
import com.mom.dfuze.ui.UiController;

public class Hygiene implements RunUtilityBehavior {

	private final String BEHAVIOR_NAME = "Hygiene";
	protected String[] REQUIRED_FIELDS = {};
	protected String DESCRIPTION = "<html>"
			+ "Description<br/>"
			+ "<ul>"
			+ "<li>Performs generic data hygiene steps</li>"
			+ "<li>Finds and corrects strange dashes</li>"
			+ "<li>Finds and corrects bad encoding</li>"
			+ "<li>Finds returns and notifies user</li>"
			+ "</ul>"
			+ "Instructions<br/>"
			+ "<ul>"
			+ "<li>Load file and run</li>"
			+ "</ul>"
			+ "</html>";


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
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.mom.dfuze.data.RunBehavior#run(com.mom.dfuze.data.UserData)
	 */
	@Override
	public void run(UserData userData) {
		
		final Pattern RETURN_PATTERN = Pattern.compile("\r\n|\r|\n", Pattern.CASE_INSENSITIVE);
		Map<String, String> encodingMap = new HashMap<>();
		fillHashMap(encodingMap);

		String[][] data = userData.getData();
		
		int countBadDash = 0;
		int countBadEncoding = 0;
		//int countReturn = 0;
		
		List<String> recordsWithReturn = new ArrayList<>();
		List<Record> returnRecordList = new ArrayList<>();

		int counter = -1;
		for (int i = 0; i < data.length; i++) {
			
			boolean hasReturn = false;

			for(int j = 0; j < data[i].length; ++j) {
				String original = data[i][j];

				// Encoding correction (This must be done first because hyphens are in the encoding)
				correctEncoding(data[i][j], encodingMap);
				if(!data[i][j].equals(original))
					++countBadEncoding;
				
				original = data[i][j]; // update
				
				// Dash correction
				data[i][j] = data[i][j].replaceAll("\\p{Pd}", "-");
				if(!data[i][j].equals(original))
					++countBadDash;
				
				// Identify returns
				Matcher matcher = RETURN_PATTERN.matcher(data[i][j]);
				
				if(matcher.find()) {
				//	++countReturn;
					hasReturn = true;
					recordsWithReturn.add(String.valueOf(i+1));
				}
				
			}

			Record record = new Record.Builder(++counter, data[i], "", "", "").build();

			if(hasReturn)
				returnRecordList.add(record);
			
			userData.add(record); // add the record the recordList in userData

		}

		// set the Header fields that we have used
		userData.setDfHeaders(new String[] {});
		
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><pre>");
		sb.append("Fields corrected\r\n");
		sb.append("----------------\r\n");
		sb.append("Dashes   : " + countBadDash);
		sb.append("\r\nEncoding : " + countBadEncoding);
		//sb.append("\r\nReturns  : " + countReturn);
		
		if(recordsWithReturn.size() > 0) {
			sb.append("\r\n\r\nThe following records have returns\r\n----------------------------------\r\n" + recordsWithReturn.stream().collect(Collectors.joining(",")));
			sb.append("\r\n\r\nWould you like to export the records with returns?");
		}
		
		sb.append("</pre></body></html>");
		
		JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
		editorPane.setEditable(false);
		
		if(recordsWithReturn.size() == 0)
			JOptionPane.showMessageDialog(UiController.getMainFrame(), editorPane, "Results", JOptionPane.INFORMATION_MESSAGE);
		else {
			int result = JOptionPane.showConfirmDialog(UiController.getMainFrame(), editorPane, "Results", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(result == JOptionPane.YES_OPTION) {
				ExportDataDialog exportDataDialog = null;
				try {
					exportDataDialog = new ExportDataDialog(UiController.getMainFrame(), UiController.getUserData().getExportHeaders(), userData.getExportData(returnRecordList));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				exportDataDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				exportDataDialog.setModalityType(ModalityType.APPLICATION_MODAL);
				exportDataDialog.setVisible(true);
			}
		}
			

	}
	
	private void fillHashMap(Map<String, String> encodingMap) {
		encodingMap.put("Ã€", "À");
		encodingMap.put("Ã‚", "Â");
		encodingMap.put("Ãƒ", "Ã");
		encodingMap.put("Ã„", "Ä");
		encodingMap.put("Ã…", "Å");
		encodingMap.put("Ã†", "Æ");
		encodingMap.put("Ã‡", "Ç");
		encodingMap.put("Ãˆ", "È");
		encodingMap.put("Ã‰", "É");
		encodingMap.put("ÃŠ", "Ê");
		encodingMap.put("Ã‹", "Ë");
		encodingMap.put("ÃŽ", "Î");
		encodingMap.put("Ã‘", "Ñ");
		encodingMap.put("Ã’", "Ò");
		encodingMap.put("Ã“", "Ó");
		encodingMap.put("Ã”", "Ô");
		encodingMap.put("Ã•", "Õ");
		encodingMap.put("Ã–", "Ö");
		encodingMap.put("Ã—", "×");
		encodingMap.put("Ã˜", "Ø");
		encodingMap.put("Ã™", "Ù");
		encodingMap.put("Ã›", "Û");
		encodingMap.put("Ãœ", "Ü");
		encodingMap.put("ÃŸ", "ß");
		encodingMap.put("Ã¡", "á");
		encodingMap.put("Ã¢", "â");
		encodingMap.put("Ã£", "ã");
		encodingMap.put("Ã¤", "ä");
		encodingMap.put("Ã¥", "å");
		encodingMap.put("Ã¦", "æ");
		encodingMap.put("Ã§", "ç");
		encodingMap.put("Ã¨", "è");
		encodingMap.put("Ã©", "é");
		encodingMap.put("Ãª", "ê");
		encodingMap.put("Ã«", "ë");
		encodingMap.put("Ã¬", "ì");
		encodingMap.put("Ã®", "î");
		encodingMap.put("Ã¯", "ï");
		encodingMap.put("Ã°", "ð");
		encodingMap.put("Ã±", "ñ");
		encodingMap.put("Ã²", "ò");
		encodingMap.put("Ã³", "ó");
		encodingMap.put("Ã´", "ô");
		encodingMap.put("Ãµ", "õ");
		encodingMap.put("Ã¶", "ö");
		encodingMap.put("Ã·", "÷");
		encodingMap.put("Ã¸", "ø");
		encodingMap.put("Ã¹", "ù");
		encodingMap.put("Ãº", "ú");
		encodingMap.put("Ã»", "û");
		encodingMap.put("Ã¼", "ü");
		encodingMap.put("Ã½", "ý");
		encodingMap.put("Ã¾", "þ");
		encodingMap.put("Ã¿", "ÿ");
		encodingMap.put("Ă§", "ç");
		encodingMap.put("ĂŠ", "é");
		encodingMap.put("Ăť", "û");
		encodingMap.put("Ă´", "ô");
		encodingMap.put("ĂŽ", "î");
		encodingMap.put("ĂŞ", "ê");
		encodingMap.put("Ă˘", "â");
		encodingMap.put("Ă˛", "ò");
		encodingMap.put("ĂŹ", "ì");
		encodingMap.put("Ă¨", "è");
		encodingMap.put("ĂŻ", "ï");
		encodingMap.put("Ã", "à");
		encodingMap.put("Ă", "à");
	}
	
	private String correctEncoding(String value, Map<String, String> encodingMap) {
		for(Map.Entry<String, String> entry : encodingMap.entrySet()) {
			value = value.replaceAll("(?i)" + Pattern.quote(entry.getKey()), Matcher.quoteReplacement(entry.getValue()));
		}
		
		return value;
	}

}
