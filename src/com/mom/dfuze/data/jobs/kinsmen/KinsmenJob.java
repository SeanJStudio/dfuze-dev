/**
 * Project: Dfuze
 * File: CanuckPlaceChildrensHospiceJob.java
 * Date: Jun 1, 2020
 * Time: 11:54:07 AM
 */
package com.mom.dfuze.data.jobs.kinsmen;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * Kinsmen extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class KinsmenJob extends Job {

  private static final String CLIENT_NAME = "Kinsmen";

  /**
   * @param runBehavior
   */
  public KinsmenJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
