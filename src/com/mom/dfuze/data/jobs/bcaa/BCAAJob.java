package com.mom.dfuze.data.jobs.bcaa;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

public class BCAAJob extends Job {

	  private static final String CLIENT_NAME = "BCAA";

	  /**
	   * @param runBehavior
	   */
	  public BCAAJob(RunBehavior runBehavior) {
	    super(runBehavior, CLIENT_NAME);
	  }

	}
