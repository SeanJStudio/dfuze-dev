/**
 * Project: Dfuze
 * File: RitchieBrothersJob.java
 * Date: Mar 10, 2020
 * Time: 8:55:04 PM
 */
package com.mom.dfuze.data.jobs.ritchiebrothers;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * RitchieBrothersJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class RitchieBrothersJob extends Job {

  private static final String CLIENT_NAME = "Ritchie Brothers";

  /**
   * @param runBehavior
   */
  public RitchieBrothersJob(RunBehavior runbehavior) {
    super(runbehavior, CLIENT_NAME);
  }

}
