package com.mom.dfuze.data.jobs.bccancerfoundation;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

public class BCCancerFoundationJob extends Job {

	  private static final String CLIENT_NAME = "BC Cancer Foundation";

	  /**
	   * @param runBehavior
	   */
	  public BCCancerFoundationJob(RunBehavior runBehavior) {
	    super(runBehavior, CLIENT_NAME);
	  }

	}
