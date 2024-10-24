package com.mom.dfuze.data;

public class FieldPrefField {
	private String dfuzeFieldName;
	private String userFieldName;
	
	public FieldPrefField() {}
	
	public FieldPrefField(String dfuzeFieldName, String userFieldName) {
		this.dfuzeFieldName = dfuzeFieldName;
		this.userFieldName = userFieldName;
	}

	public String getDfuzeFieldName() {
		return dfuzeFieldName;
	}

	public void setDfuzeFieldName(String dfuzeFieldName) {
		this.dfuzeFieldName = dfuzeFieldName;
	}

	public String getUserFieldName() {
		return userFieldName;
	}

	public void setUserFieldName(String userFieldName) {
		this.userFieldName = userFieldName;
	}

	@Override
	public String toString() {
		return "FieldPrefField [dfuzeFieldName=" + dfuzeFieldName + ", userFieldName=" + userFieldName + "]";
	}
	
}
