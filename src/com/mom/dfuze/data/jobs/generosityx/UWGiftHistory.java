package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class UWGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String leadSource;
	private String staffSupported;
	
	public UWGiftHistory() {}

	public UWGiftHistory(String id, double giftAmount, LocalDate giftDate, String leadSource, String staffSupported) {
		super();
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.leadSource = leadSource;
		this.staffSupported = staffSupported;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getGiftAmount() {
		return giftAmount;
	}

	public void setGiftAmount(double giftAmount) {
		this.giftAmount = giftAmount;
	}

	public LocalDate getGiftDate() {
		return giftDate;
	}

	public void setGiftDate(LocalDate giftDate) {
		this.giftDate = giftDate;
	}

	public String getLeadSource() {
		return leadSource;
	}

	public void setLeadSource(String leadSource) {
		this.leadSource = leadSource;
	}

	public String getStaffSupported() {
		return staffSupported;
	}

	public void setStaffSupported(String staffSupported) {
		this.staffSupported = staffSupported;
	}

	@Override
	public String toString() {
		return "UWGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate + ", leadSource="
				+ leadSource + ", staffSupported=" + staffSupported + "]";
	}
}
