/**
 * Project: DFuze
 * File: ApplicationException.java
 * Date: Mar 2, 2020
 * Time: 9:27:37 PM
 */
package com.mom.dfuze;

/**
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class ApplicationException extends Exception {

  /**
   * Construct an ApplicationException
   * 
   * @param message
   *          the exception message.
   */
  public ApplicationException(String message) {
    super(message);

  }

  /**
   * Construct an ApplicationException
   * 
   * @param throwable
   *          a Throwable.
   */
  public ApplicationException(Throwable throwable) {
    super(throwable);
  }

}