package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class KGMGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String giftCampaign;
	private String giftType;
	
	public KGMGiftHistory() {}
	
	public KGMGiftHistory(String id, double giftAmount, LocalDate giftDate, String giftCampaign, String giftType) {
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.giftCampaign = giftCampaign;
		this.giftType = giftType;
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

	public String getGiftCampaign() {
		return giftCampaign;
	}

	public void setGiftCampaign(String giftCampaign) {
		this.giftCampaign = giftCampaign;
	}

	public String getGiftType() {
		return giftType;
	}

	public void setGiftType(String giftType) {
		this.giftType = giftType;
	}

	@Override
	public String toString() {
		return "KGMGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate + ", giftCampaign="
				+ giftCampaign + ", giftType=" + giftType + "]";
	}

	

}
