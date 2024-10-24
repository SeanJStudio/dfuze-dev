package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class CWMHGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String giftDonorStatus;
	
	public CWMHGiftHistory() {}
	
	public CWMHGiftHistory(String id, double giftAmount, LocalDate giftDate, String giftDonorStatus) {
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.giftDonorStatus = giftDonorStatus;
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

	public String getGiftDonorStatus() {
		return giftDonorStatus;
	}

	public void setGiftDonorStatus(String giftDonorStatus) {
		this.giftDonorStatus = giftDonorStatus;
	}

	@Override
	public String toString() {
		return "CWMHGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate
				+ ", giftDonorStatus=" + giftDonorStatus + "]";
	}

}
