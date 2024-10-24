/**
 * Project: Dfuze
 * File: CanuckPlaceChildrensHospiceJob.java
 * Date: Jun 1, 2020
 * Time: 11:54:07 AM
 */
package com.mom.dfuze.data.jobs.cushmanwakefield;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * Cushman Wakefield extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class CushmanWakefieldJob extends Job {

  private static final String CLIENT_NAME = "Cushman Wakefield";

  /**
   * @param runBehavior
   */
  public CushmanWakefieldJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
