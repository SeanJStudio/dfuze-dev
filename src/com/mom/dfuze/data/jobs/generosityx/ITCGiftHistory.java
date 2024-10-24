package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class ITCGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String giftDesignation;
	
	public ITCGiftHistory() {}
	
	public ITCGiftHistory(String id, double giftAmount, LocalDate giftDate, String giftDesignation) {
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.giftDesignation = giftDesignation;
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

	@Override
	public String toString() {
		return "ITCGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate
				+ ", giftDesignation=" + giftDesignation + "]";
	}



}
