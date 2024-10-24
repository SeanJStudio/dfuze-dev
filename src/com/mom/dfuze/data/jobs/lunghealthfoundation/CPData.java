package com.mom.dfuze.data.jobs.lunghealthfoundation;

public class CPData {
	private int from;
	private int to;
	private String lookup;
	private String pc;

	
	public CPData() {}
	
	public CPData(int from, int to, String lookup, String pc) {
		this.from = from;
		this.to = to;
		this.lookup = lookup;
		this.pc = pc;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public String getPc() {
		return pc;
	}

	public void setPc(String pc) {
		this.pc = pc;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	@Override
	public String toString() {
		return "CPData [from=" + from + ", to=" + to + ", lookup=" + lookup + ", pc=" + pc + "]";
	}


}
