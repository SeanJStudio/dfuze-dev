/**
 * Project: Dfuze
 * File: MissionBonAccuielJob.java
 * Date: Mar 15, 2020
 * Time: 3:03:40 PM
 */
package com.mom.dfuze.data.jobs.bchydro;

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
public class BCHydroJob extends Job {

  private static final String CLIENT_NAME = "BC Hydro";

  /**
   * @param runBehavior
   */
  public BCHydroJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
