/**
 * Project: Dfuze
 * File: MissionBonAccuielJob.java
 * Date: Mar 15, 2020
 * Time: 3:03:40 PM
 */
package com.mom.dfuze.data.jobs.generosityx;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * GenerosityXJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 07/06/2023
 *
 */
public class GenerosityXJob extends Job {

  private static final String CLIENT_NAME = "Generosity X";

  /**
   * @param runBehavior
   */
  public GenerosityXJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
