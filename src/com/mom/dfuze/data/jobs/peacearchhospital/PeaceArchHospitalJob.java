package com.mom.dfuze.data.jobs.peacearchhospital;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

public class PeaceArchHospitalJob extends Job {

	  private static final String CLIENT_NAME = "Peace Arch Hospital";

	  /**
	   * @param runBehavior
	   */
	  public PeaceArchHospitalJob(RunBehavior runBehavior) {
	    super(runBehavior, CLIENT_NAME);
	  }
}
