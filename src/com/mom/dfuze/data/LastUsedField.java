package com.mom.dfuze.data;

public class LastUsedField {
	
	private String lastUsedField;
	private String lastUsedFieldValue;
	
	public LastUsedField(String lastUsedField, String lastUsedFieldValue) {
		this.lastUsedField = lastUsedField;
		this.lastUsedFieldValue = lastUsedFieldValue;
	}

	public String getLastUsedField() {
		return lastUsedField;
	}

	public void setLastUsedField(String lastUsedField) {
		this.lastUsedField = lastUsedField;
	}

	public String getLastUsedFieldValue() {
		return lastUsedFieldValue;
	}

	public void setLastUsedFieldValue(String lastUsedFieldValue) {
		this.lastUsedFieldValue = lastUsedFieldValue;
	}

	@Override
	public String toString() {
		return "LastUsedField [lastUsedField=" + lastUsedField + ", lastUsedFieldValue=" + lastUsedFieldValue + "]";
	}
	
	

}
