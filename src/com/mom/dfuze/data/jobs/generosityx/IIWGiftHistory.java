package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class IIWGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String transactionType;
	
	public IIWGiftHistory() {}
	
	public IIWGiftHistory(String id, double giftAmount, LocalDate giftDate, String transactionType) {
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.transactionType = transactionType;
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

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	@Override
	public String toString() {
		return "IIWGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate
				+ ", transactionType=" + transactionType + "]";
	}



}
