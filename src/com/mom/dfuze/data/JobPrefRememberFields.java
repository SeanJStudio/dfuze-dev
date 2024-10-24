package com.mom.dfuze.data;

public class JobPrefRememberFields {
	private String client;
	private String job;
	private String field;
	private String value;
	
	public JobPrefRememberFields() {}
	
	public JobPrefRememberFields(String client, String job, String field, String value) {
		this.client = client;
		this.job = job;
		this.field = field;
		this.value = value;

	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

	@Override
	public String toString() {
		return "JobPrefRemember [client=" + client + ", job=" + job + ", field=" + field + ", value=" + value + "]";
	}
	
	
	
}
