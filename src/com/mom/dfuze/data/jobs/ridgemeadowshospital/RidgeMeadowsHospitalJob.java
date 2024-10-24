/**
 * Project: Dfuze
 * File: CanuckPlaceChildrensHospiceJob.java
 * Date: Jun 1, 2020
 * Time: 11:54:07 AM
 */
package com.mom.dfuze.data.jobs.ridgemeadowshospital;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * RidgeMeadowsHospitalJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/08/2023
 *
 */
public class RidgeMeadowsHospitalJob extends Job {

  private static final String CLIENT_NAME = "Ridge Meadows Hospital Foundation";

  /**
   * @param runBehavior
   */
  public RidgeMeadowsHospitalJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
