/**
 * Project: Dfuze
 * File: JobProcedure.java
 * Date: Mar 10, 2020
 * Time: 5:29:53 PM
 */
package com.mom.dfuze.data;

/**
 * RunBehavior interface to abstract the data manipulation algorithm at runtime
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public abstract interface RunBehavior {

  /**
   * Abstractly runs the data manipulation algorithm at run time
   * 
   * @param userData
   *          the users data to maniplate
   * @throws Exception
   *           the exception to throw in case something goes wrong
   */
  public void run(UserData userData) throws Exception;

  /**
   * @return runBehaviorName as a String
   */
  public abstract String getRunBehaviorName();

  /**
   * @return runBehavior description as a String
   */
  public abstract String getDescription();

  /**
   * @return required fields for runBehavior as a String[]
   */
  public abstract String[] getRequiredFields();
  
  public abstract Boolean isFileNameRequired();
}
