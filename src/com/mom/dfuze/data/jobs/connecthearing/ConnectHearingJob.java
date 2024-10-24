/**
 * Project: Dfuze
 * File: MissionBonAccuielJob.java
 * Date: Mar 15, 2020
 * Time: 3:03:40 PM
 */
package com.mom.dfuze.data.jobs.connecthearing;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * ConnectHearingJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/16/2023
 *
 */
public class ConnectHearingJob extends Job {

  private static final String CLIENT_NAME = "Connect Hearing";

  /**
   * @param runBehavior
   */
  public ConnectHearingJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
