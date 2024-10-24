/**
 * Project: Dfuze
 * File: JTextFieldLimit.java
 * Date: Apr 12, 2020
 * Time: 8:02:37 AM
 */
package com.mom.dfuze.ui;


import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import javax.swing.text.PlainDocument;


/**
 * JTextFieldLimit Class to limited the amount of character in a textField
 * This Class is used to restrict the textField in the delimiterSelectDialog and DataExportDialogs to 1 character
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class JTextFieldLimit extends PlainDocument {
  private int limit;

  public JTextFieldLimit(int limit) {
    super();
    this.limit = limit;
  }

  @Override
  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
    if (str == null)
      return;

    if ((getLength() + str.length()) <= limit) {
      super.insertString(offset, str, attr);
    }
  }
  
}
