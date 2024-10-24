/**
 * Project: Dfuze
 * File: MissionBonAccuielJob.java
 * Date: Mar 15, 2020
 * Time: 3:03:40 PM
 */
package com.mom.dfuze.data.jobs.missionbonaccuiel;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * MissionBonAccuielJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class MissionBonAccuielJob extends Job {

  private static final String CLIENT_NAME = "Mission Bon Accuiel";

  /**
   * @param runBehavior
   */
  public MissionBonAccuielJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
