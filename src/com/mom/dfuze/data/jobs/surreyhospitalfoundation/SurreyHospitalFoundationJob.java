/**
 * Project: Dfuze
 * File: CanuckPlaceChildrensHospiceJob.java
 * Date: Jun 1, 2020
 * Time: 11:54:07 AM
 */
package com.mom.dfuze.data.jobs.surreyhospitalfoundation;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * SurreyHospitalFoundationJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 10/27/2023
 *
 */
public class SurreyHospitalFoundationJob extends Job {

  private static final String CLIENT_NAME = "Surrey Hospital Foundation";

  /**
   * @param runBehavior
   */
  public SurreyHospitalFoundationJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
