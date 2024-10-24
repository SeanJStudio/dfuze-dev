/**
 * Project: Dfuze
 * File: CanuckPlaceChildrensHospiceJob.java
 * Date: Jun 1, 2020
 * Time: 11:54:07 AM
 */
package com.mom.dfuze.data.jobs.canadianceliacassociation;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.RunBehavior;

/**
 * CanadianCeliacAssociationJob extends the Job to hold the client name
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 11/01/2022
 *
 */
public class CanadianCeliacAssociationJob extends Job {

  private static final String CLIENT_NAME = "Canadian Celiac Association";

  /**
   * @param runBehavior
   */
  public CanadianCeliacAssociationJob(RunBehavior runBehavior) {
    super(runBehavior, CLIENT_NAME);
  }

}
