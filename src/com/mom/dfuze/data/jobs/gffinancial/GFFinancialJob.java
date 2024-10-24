/**
 * Project: Dfuze
 * File: MissionBonAccuielJob.java
 * Date: Mar 15, 2020
 * Time: 3:03:40 PM
 */
package com.mom.dfuze.data.jobs.gffinancial;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * MGFFinancialJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class GFFinancialJob extends Job {

  private static final String CLIENT_NAME = "G&F Financial";

  /**
   * @param runBehavior
   */
  public GFFinancialJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
