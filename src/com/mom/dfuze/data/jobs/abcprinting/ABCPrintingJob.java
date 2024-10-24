/**
 * Project: Dfuze
 * File: CanuckPlaceChildrensHospiceJob.java
 * Date: Jun 1, 2020
 * Time: 11:54:07 AM
 */
package com.mom.dfuze.data.jobs.abcprinting;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * ABCPrinting extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class ABCPrintingJob extends Job {

  private static final String CLIENT_NAME = "ABC Printing";

  /**
   * @param runBehavior
   */
  public ABCPrintingJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
