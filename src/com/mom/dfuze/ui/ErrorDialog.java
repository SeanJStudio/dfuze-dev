/**
 * Project: DFuze
 * File: ErrorDialog.java
 * Date: Mar 2, 2020
 * Time: 9:21:00 PM
 */
package com.mom.dfuze.ui;

import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.Color;

/**
 * ErrorDialog class to display an error message to the user
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class ErrorDialog extends JDialog {

  /**
   * Create the dialog.
   */
  public ErrorDialog(final String message) {
  	getContentPane().setForeground(Color.BLACK);
    getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 11));
    Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
    JOptionPane.showMessageDialog(activeWindow, message, "Error",JOptionPane.ERROR_MESSAGE);
  }
}