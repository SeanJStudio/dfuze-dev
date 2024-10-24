/**
 * Project: Dfuze
 * File: CanuckPlaceChildrensHospiceJob.java
 * Date: Jun 1, 2020
 * Time: 11:54:07 AM
 */
package com.mom.dfuze.data.jobs.thecommongood;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * Kinsmen extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 10/10/2023
 *
 */
public class TheCommonGoodJob extends Job {

  private static final String CLIENT_NAME = "The Common Good";

  /**
   * @param runBehavior
   */
  public TheCommonGoodJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
