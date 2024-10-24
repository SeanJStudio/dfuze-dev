package com.mom.dfuze.data.jobs.ritchiebrothers;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;
import com.mom.dfuze.data.util.Common;
import com.mom.dfuze.ui.InkjetDialog;
import com.mom.dfuze.ui.UiController;

public class MakeInkjetFileForBuhrs implements RunRitchieBrothersBehavior {

	private final String BEHAVIOR_NAME = "Make Inkjet File for Buhrs";
	protected String[] REQUIRED_FIELDS = {
			UserData.fieldName.IN_ID.getName(),
			UserData.fieldName.NAME1.getName(),
			UserData.fieldName.COMPANY.getName(),
			UserData.fieldName.ADDRESS1.getName(),
			UserData.fieldName.ADDRESS2.getName(),
			UserData.fieldName.CITY.getName(),
			UserData.fieldName.PROVINCE.getName(),
			UserData.fieldName.POSTALCODE.getName()
	};
	
	protected String DESCRIPTION = "<html>Instructions<br/><ul><li>Load sorted data</li></ul>"
			+ "Description<br/><ul><li>Creates Inkjet file in the directory of the loaded data</li></ul>"
			+ Common.arrayFieldsToHTMLList(REQUIRED_FIELDS) +"</html>";

	
	private final static Pattern BARCODE_PATTERN = Pattern.compile("IM.*BARCODE", Pattern.CASE_INSENSITIVE);
	private final static Pattern SACK_PATTERN = Pattern.compile("SACK", Pattern.CASE_INSENSITIVE);
	private final static Pattern PACK_PATTERN = Pattern.compile("PACK", Pattern.CASE_INSENSITIVE);
	private final static Pattern ENDR_PATTERN = Pattern.compile("ENDR", Pattern.CASE_INSENSITIVE);
	private final static Pattern SEQ_PATTERN = Pattern.compile("SEQ", Pattern.CASE_INSENSITIVE);

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
	public void run(UserData userData) throws ApplicationException {
		
		int barcodeIndex = -1, sackIndex = -1, packIndex = -1, endrIndex = -1, seqIndex = -1;
		
		String[] newInHeaders = Arrays.copyOf(userData.getInHeaders(), userData.getInHeaders().length + 6);
		
		String[] extraHeaders = new String[]{"Line1","Line2","Line3","Line4","Line5","Line6"};
		int[] extraHeaderIndexes = new int[6];
		
		int extraHeadersCounter = -1;
		for(int i = userData.getInHeaders().length; i < newInHeaders.length; ++i) {
			++extraHeadersCounter;
			newInHeaders[i] = extraHeaders[extraHeadersCounter];
			extraHeaderIndexes[extraHeadersCounter] = i;
		}
		
		userData.setInHeaders(newInHeaders);
		
		for(int i = 0; i < userData.getInHeaders().length; ++i) {
			String header = userData.getInHeaders()[i];
			if(BARCODE_PATTERN.matcher(header).find())
				barcodeIndex = i;
			else if(SACK_PATTERN.matcher(header).find())
				sackIndex = i;
			else if(PACK_PATTERN.matcher(header).find())
				packIndex = i;
			else if(ENDR_PATTERN.matcher(header).find())
				endrIndex = i;
			else if(SEQ_PATTERN.matcher(header).find())
				seqIndex = i;
		}
		
		if(barcodeIndex == -1)
			throw new ApplicationException("Could not find the IM Barcode field");
		if(sackIndex == -1)
			throw new ApplicationException("Could not find the Sack field");
		if(packIndex == -1)
			throw new ApplicationException("Could not find the Pack field");
		if(endrIndex == -1)
			throw new ApplicationException("Could not find the Endr field");
		if(seqIndex == -1)
			throw new ApplicationException("Could not find the Seq field");

		String[][] data = userData.getData();
		
		String previousPack = "";

		int counter = -1;
		for (int i = 0; i < data.length; i++) {
			String id = data[i][userData.getInIdIndex()];
			String barcode = data[i][barcodeIndex];
			String pack = data[i][packIndex];
			String sack = data[i][sackIndex];
			String endr = data[i][endrIndex];
			String seq = data[i][seqIndex];
			data[i][userData.getPCodeIndex()] = " " + data[i][userData.getPCodeIndex()];
			String bundleMarker = "";
			
			if(!pack.equalsIgnoreCase(previousPack)) {
				previousPack = pack;
				bundleMarker = "#";
			}
			
			String bagBun = (!sack.equalsIgnoreCase("")) ? sack + "/" + pack : "";
			
			String line1 = id;
			String line2 = barcode;
			String line3 = "";
			String line4 = bundleMarker;
			String line5 = endr;
			String line6 = String.format("%-30s%-15s%s", id, bagBun, seq);
			
			String[] extraLines = new String[]{line1, line2, line3, line4, line5, line6};
			
			String[] newIn = Arrays.copyOf(data[i], data[i].length + 6);
			int extraLinesCounter = -1;
			for(int j = data[i].length; j < newIn.length; ++j) {
				newIn[j] = extraLines[++extraLinesCounter];
			}

			Record record = new Record.Builder(++counter, newIn, "", "", "").build();

			userData.add(record); // add the record the recordList in userData

		}

		// set the Header fields that we have used
		userData.setDfHeaders(new String[] {
		});
		
		InkjetDialog inkjetDialog = new InkjetDialog(UiController.getMainFrame());
		inkjetDialog.getChckbxUppercase().setSelected(true);
		inkjetDialog.getComboBoxLine1().setSelectedIndex(extraHeaderIndexes[0]);
		inkjetDialog.getComboBoxLine2().setSelectedIndex(extraHeaderIndexes[1]);
		inkjetDialog.getComboBoxLine3().setSelectedIndex(extraHeaderIndexes[2]);
		inkjetDialog.getComboBoxLine4().setSelectedIndex(extraHeaderIndexes[3]);
		inkjetDialog.getComboBoxLine5().setSelectedIndex(extraHeaderIndexes[4]);
		inkjetDialog.getComboBoxLine6().setSelectedIndex(extraHeaderIndexes[5]);
		inkjetDialog.getComboBoxLine7().setSelectedIndex(userData.getNam1Index());
		inkjetDialog.getComboBoxLine8().setSelectedIndex(userData.getCmpnyIndex());
		inkjetDialog.getComboBoxAddress1().setSelectedIndex(userData.getAdd1Index());
		inkjetDialog.getComboBoxAddress2().setSelectedIndex(userData.getAdd2Index());
		inkjetDialog.getComboBoxCity().setSelectedIndex(userData.getCityIndex());
		inkjetDialog.getComboBoxProv().setSelectedIndex(userData.getProvIndex());
		inkjetDialog.getComboBoxPC().setSelectedIndex(userData.getPCodeIndex());
		inkjetDialog.getComboBoxListOrder().setSelectedIndex(seqIndex);
		inkjetDialog.setVisible(true);

	}

}
