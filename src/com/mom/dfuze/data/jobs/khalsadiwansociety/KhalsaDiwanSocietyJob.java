/**
 * Project: Dfuze
 * File: RitchieBrothersJob.java
 * Date: Mar 10, 2020
 * Time: 8:55:04 PM
 */
package com.mom.dfuze.data.jobs.khalsadiwansociety;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * KhalsaDiwanSocietyJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 10/14/2022
 *
 */
public class KhalsaDiwanSocietyJob extends Job {

  private static final String CLIENT_NAME = "Khalsa Diwan Society";

  /**
   * @param runBehavior
   */
  public KhalsaDiwanSocietyJob(RunBehavior runbehavior) {
    super(runbehavior, CLIENT_NAME);
  }

}
