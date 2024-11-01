package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class UWGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String sourceDetail;
	private String staffSupported;
	private String recurringDonation;
	
	public UWGiftHistory() {}

	public UWGiftHistory(String id, double giftAmount, LocalDate giftDate, String sourceDetail, String staffSupported, String recurringDonation) {
		super();
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.sourceDetail = sourceDetail;
		this.staffSupported = staffSupported;
		this.recurringDonation = recurringDonation;
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

	public String getSourceDetail() {
		return sourceDetail;
	}

	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	public String getStaffSupported() {
		return staffSupported;
	}

	public void setStaffSupported(String staffSupported) {
		this.staffSupported = staffSupported;
	}

	public String getRecurringDonation() {
		return recurringDonation;
	}

	public void setRecurringDonation(String recurringDonation) {
		this.recurringDonation = recurringDonation;
	}

	@Override
	public String toString() {
		return "UWGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate + ", sourceDetail="
				+ sourceDetail + ", staffSupported=" + staffSupported + ", recurringDonation=" + recurringDonation
				+ "]";
	}
	
}
