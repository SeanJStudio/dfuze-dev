/**
 * Project: Dfuze
 * File: JobSorters.java
 * Date: May 31, 2020
 * Time: 6:19:52 PM
 */
package com.mom.dfuze.data;

import java.util.Comparator;

/**
 * Collection of Inner Classes to compare and sort Jobs
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class JobSorters {

  /**
   * Inner Class CompareName to compare Job names
   */
  public static class CompareName implements Comparator<Job> {
    @Override
    public int compare(Job job1, Job job2) {
      int c = job1.getClientName().compareTo(job2.getClientName());
      if (c == 0)
        c = job1.getRunBehavior().getRunBehaviorName().compareTo(job2.getRunBehavior().getRunBehaviorName());
      return c;
    }
  }

}
