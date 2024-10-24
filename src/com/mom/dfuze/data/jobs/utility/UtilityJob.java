package com.mom.dfuze.data.jobs.utility;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

public class UtilityJob extends Job {

  private static final String CLIENT_NAME = "0 - Utility";

  /**
   * @param runBehavior
   */
  public UtilityJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
