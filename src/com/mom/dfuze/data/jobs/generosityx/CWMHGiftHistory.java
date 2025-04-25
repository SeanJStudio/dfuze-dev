package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class CWMHGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String inMemory;
	private String tributeType;
	private String appeal;
	
	public CWMHGiftHistory() {}
	
	public CWMHGiftHistory(String id, double giftAmount, LocalDate giftDate, String inMemory, String tributeType, String appeal) {
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.inMemory = inMemory;
		this.tributeType = tributeType;
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

	public String getInMemory() {
		return inMemory;
	}

	public void setInMemory(String inMemory) {
		this.inMemory = inMemory;
	}

	public String getTributeType() {
		return tributeType;
	}

	public void setTributeType(String tributeType) {
		this.tributeType = tributeType;
	}
	
	public String getAppeal() {
		return appeal;
	}

	public void setAppeal(String appeal) {
		this.appeal = appeal;
	}

	@Override
	public String toString() {
		return "CWMHGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate + ", inMemory="
				+ inMemory + ", tributeType=" + tributeType + ", appeal=" + appeal + "]";
	}
	
}
	