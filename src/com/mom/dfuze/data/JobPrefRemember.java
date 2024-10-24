package com.mom.dfuze.data;

public class JobPrefRemember {
	private String client;
	private String job;
	private boolean isRemember;
	public static final String REMEMBER_TRUE = "1";
	public static final String REMEMBER_FALSE = "0";
	
	public JobPrefRemember() {}
	
	public JobPrefRemember(String client, String job, String isRemember) {
		this.client = client;
		this.job = job;
		
		if(isRemember.equals(REMEMBER_TRUE))
			this.isRemember = true;
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

	public boolean isRemember() {
		return isRemember;
	}

	public void setRemember(boolean isRemember) {
		this.isRemember = isRemember;
	}
	
	public String getKey() {
		return client+job;
	}

	@Override
	public String toString() {
		return "JobPrefRemember [client=" + client + ", job=" + job + ", isRemember=" + isRemember + "]";
	}

	
	
	
}
