package com.mom.dfuze.data;

import java.util.ArrayList;
import java.util.List;

public class Label {
	
	private List<ArrayList<String>> lines;
	private String dmc = "";
	private String bagBundle = "";
	private String breaks = "";
	private String listOrder = "";
	
	public Label(List<ArrayList<String>> lines, String dmc, String bagBundle, String breaks, String listOrder) {
		setLines(lines);
		setDmc(dmc);
		setBagBundle(bagBundle);
		setBreaks(breaks);
		setListOrder(listOrder);
	}

	public List<ArrayList<String>> getLines() {
		return lines;
	}

	public void setLines(List<ArrayList<String>> lines) {
		this.lines = lines;
	}

	public String getDmc() {
		return dmc;
	}

	public void setDmc(String dmc) {
		this.dmc = dmc;
	}

	public String getBagBundle() {
		return bagBundle;
	}

	public void setBagBundle(String bagBundle) {
		this.bagBundle = bagBundle;
	}

	public String getBreaks() {
		return breaks;
	}

	public void setBreaks(String breaks) {
		this.breaks = breaks;
	}

	public String getListOrder() {
		return listOrder;
	}

	public void setListOrder(String listOrder) {
		this.listOrder = listOrder;
	}
	
	
}
