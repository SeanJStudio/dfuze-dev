package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class BISGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String giftType;
	private String appeal;
	
	public BISGiftHistory() {}
	
	public BISGiftHistory(String id, double giftAmount, LocalDate giftDate, String giftType, String appeal) {
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.giftType = giftType;
		this.appeal = appeal;
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

	public String getGiftType() {
		return giftType;
	}

	public void setGiftType(String giftType) {
		this.giftType = giftType;
	}

	public String getAppeal() {
		return appeal;
	}

	public void setAppeal(String appeal) {
		this.appeal = appeal;
	}

	@Override
	public String toString() {
		return "BISGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate + ", giftType="
				+ giftType + ", appeal=" + appeal + "]";
	}

	
}
