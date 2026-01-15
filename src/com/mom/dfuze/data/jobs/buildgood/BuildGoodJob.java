/**
 * Project: Dfuze
 * File: MissionBonAccuielJob.java
 * Date: Mar 15, 2020
 * Time: 3:03:40 PM
 */
package com.mom.dfuze.data.jobs.buildgood;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * BuildGoodJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Datacore
 *         Date: 01/13/2026
 *
 */
public class BuildGoodJob extends Job {

  private static final String CLIENT_NAME = "BuildGood";

  /**
   * @param runBehavior
   */
  public BuildGoodJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
