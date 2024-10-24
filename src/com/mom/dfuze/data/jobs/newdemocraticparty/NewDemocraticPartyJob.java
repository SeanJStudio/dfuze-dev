/**
 * Project: Dfuze
 * File: RitchieBrothersJob.java
 * Date: Mar 10, 2020
 * Time: 8:55:04 PM
 */
package com.mom.dfuze.data.jobs.newdemocraticparty;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * NewDemocraticPartyJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 09/11/2023
 *
 */
public class NewDemocraticPartyJob extends Job {

  private static final String CLIENT_NAME = "New Democratic Party";

  /**
   * @param runBehavior
   */
  public NewDemocraticPartyJob(RunBehavior runbehavior) {
    super(runbehavior, CLIENT_NAME);
  }

}
