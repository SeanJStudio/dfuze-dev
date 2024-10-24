/**
 * Project: Dfuze
 * File: HarveyMckinnonAssociatesJob.java
 * Date: Jun 17, 2020
 * Time: 11:34:32 AM
 */
package com.mom.dfuze.data.jobs.harveymckinnonassociates;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * HarveyMckinnonAssociatesJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class HarveyMckinnonAssociatesJob extends Job {

  private static final String CLIENT_NAME = "HMA";

  /**
   * @param runBehavior
   */
  public HarveyMckinnonAssociatesJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}