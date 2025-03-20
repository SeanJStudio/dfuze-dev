package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class ITCGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String giftDesignation;
	private String isRecurring;
	
	public ITCGiftHistory() {}
	
	public ITCGiftHistory(String id, double giftAmount, LocalDate giftDate, String giftDesignation, String isRecurring) {
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.giftDesignation = giftDesignation;
		this.isRecurring = isRecurring;
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

	public String getGiftDesignation() {
		return giftDesignation;
	}

	public void setGiftDesignation(String giftDesignation) {
		this.giftDesignation = giftDesignation;
	}

	public String getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(String isRecurring) {
		this.isRecurring = isRecurring;
	}

	@Override
	public String toString() {
		return "ITCGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate
				+ ", giftDesignation=" + giftDesignation + ", isRecurring=" + isRecurring + "]";
	}

}
