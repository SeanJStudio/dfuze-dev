package com.mom.dfuze.data.util;

import com.mom.dfuze.data.Record;
import com.mom.dfuze.data.UserData;

public class Analyze {
	
	private Analyze() {}
	
	public static void minMaxRFM(UserData userData) {

		double minR = userData.getRecordList()
				.stream()
				.map(r ->Double.parseDouble(r.getRScore()))
				.mapToDouble(Double::doubleValue)
				.min()
				.getAsDouble();

		double maxR = userData.getRecordList()
				.stream()
				.map(r ->Double.parseDouble(r.getRScore()))
				.mapToDouble(Double::doubleValue)
				.max()
				.getAsDouble();

		double minF = userData.getRecordList()
				.stream()
				.map(r ->Double.parseDouble(r.getFScore()))
				.mapToDouble(Double::doubleValue)
				.min()
				.getAsDouble();

		double maxF = userData.getRecordList()
				.stream()
				.map(r ->Double.parseDouble(r.getFScore()))
				.mapToDouble(Double::doubleValue)
				.max()
				.getAsDouble();

		double minM = userData.getRecordList()
				.stream()
				.map(r ->Double.parseDouble(r.getMScore()))
				.mapToDouble(Double::doubleValue)
				.min()
				.getAsDouble();

		double maxM = userData.getRecordList()
				.stream()
				.map(r ->Double.parseDouble(r.getMScore()))
				.mapToDouble(Double::doubleValue)
				.max()
				.getAsDouble();

		for(Record record : userData.getRecordList()) {
			Double rScore = Double.parseDouble(record.getRScore());
			Double fScore = Double.parseDouble(record.getFScore());
			Double mScore = Double.parseDouble(record.getMScore());

			record.setRScore(String.format("%.9f", ((maxR - rScore) / (maxR - minR))));
			record.setFScore(String.format("%.9f", ((fScore - minF) / (maxF - minF))));
			record.setMScore(String.format("%.9f", ((mScore - minM) / (maxM - minM))));

		}

	}
	
	public static void prioritizeRFMBuildGood(UserData userData) {
		for(Record record : userData.getRecordList()) {
			double sum = 0;
			sum += Double.parseDouble(record.getRScore());
			sum += Double.parseDouble(record.getMScore());
			sum += Double.parseDouble(record.getFScore());
			record.setPriority(String.valueOf(sum));
		}
	}
	
	public static void prioritizeRFM(UserData userData) {
		for(Record record : userData.getRecordList()) {
			double sum = 0;
			sum += Double.parseDouble(record.getRScore()) * 100.0;
			sum += Double.parseDouble(record.getTtlDnAmtLst12Mnths()) / 1000.0;
			sum += Double.parseDouble(record.getMScore()) * 11.0;
			sum += Double.parseDouble(record.getFScore()) * 6.0;
			record.setPriority(String.valueOf(sum));
		}
	}
	
	public static void prioritizeRFMAdra(UserData userData) {
		for(Record record : userData.getRecordList()) {
			double sum = 0;
			sum += Double.parseDouble(record.getRScore()) * 100.0;
			sum += Double.parseDouble(record.getTtlDnAmtLst12Mnths()) / 1000.0;
			sum += Double.parseDouble(record.getMScore()) * 11.0;
			sum += Double.parseDouble(record.getFScore()) * 16.0;
			record.setPriority(String.valueOf(sum));
		}
	}

}
