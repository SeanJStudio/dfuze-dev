package com.mom.dfuze.data.jobs.thinktechnica;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

public class ThinkTechnicaJob extends Job {

	  private static final String CLIENT_NAME = "Think Technica";

	  /**
	   * @param runBehavior
	   */
	  public ThinkTechnicaJob(RunBehavior runBehavior) {
	    super(runBehavior, CLIENT_NAME);
	  }

	}
