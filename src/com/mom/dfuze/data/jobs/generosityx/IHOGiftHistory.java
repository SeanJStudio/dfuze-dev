package com.mom.dfuze.data.jobs.generosityx;

import java.time.LocalDate;

public class IHOGiftHistory {
	private String id;
	private double giftAmount;
	private LocalDate giftDate;
	private String giftCampaign;
	
	public IHOGiftHistory() {}
	
	public IHOGiftHistory(String id, double giftAmount, LocalDate giftDate, String giftCampaign) {
		this.id = id;
		this.giftAmount = giftAmount;
		this.giftDate = giftDate;
		this.giftCampaign = giftCampaign;
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

	@Override
	public String toString() {
		return "IHOGiftHistory [id=" + id + ", giftAmount=" + giftAmount + ", giftDate=" + giftDate + ", giftCampaign="
				+ giftCampaign + "]";
	}


}
