/**
 * Project: Dfuze
 * File: Job.java
 * Date: Mar 10, 2020
 * Time: 5:23:05 PM
 */
package com.mom.dfuze.data;

/**
 * Job Class uses the Strategy Pattern to abstract its unique data manipulation RunBehaviour interface
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public abstract class Job {

  protected String clientName;
  private RunBehavior runBehavior;

  /**
   * Constructor for Objects of Class Job
   * 
   * @param runBehavior
   *          the runBehavior to set
   */
  public Job(RunBehavior runBehavior, String clientName) {
    setRunBehavior(runBehavior);
    setClientName(clientName);
  }

  /**
   * @param userData
   *          the userData to run the data manipulations on
   * @throws Exception
   *           this exception will prevent the job from completing in case something goes wrong.
   */
  public void run(UserData userData) throws Exception {
    runBehavior.run(userData);
  }

  /**
   * @param runBehavior
   *          the runBehavior to set
   */
  public void setRunBehavior(RunBehavior runBehavior) {
    this.runBehavior = runBehavior;
  }

  /**
   * @return the clientName as a String
   */
  public String getClientName() {
    return this.clientName;
  }

  /**
   * @param clientName
   *          the clientName to set
   */
  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  /**
   * @return the runBehavior as a RunBehavior
   */
  public RunBehavior getRunBehavior() {
    return runBehavior;
  }

  /**
   * Gets the runBehaviorName
   */
  public void setRunBehaviorName() {
    this.runBehavior.getRunBehaviorName();
  }

}