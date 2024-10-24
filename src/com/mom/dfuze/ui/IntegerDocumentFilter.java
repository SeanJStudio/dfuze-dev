package com.mom.dfuze.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class IntegerDocumentFilter extends DocumentFilter {
	Pattern regEx = Pattern.compile("\\d*");
	private int maxLength = 0;
	
	public IntegerDocumentFilter(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		Matcher matcher = regEx.matcher(text);

		if (!matcher.matches())
			return;

		if ((fb.getDocument().getLength() + text.length() - length) <= maxLength)
			super.replace(fb, offset, length, text, attrs);
	}

	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		if ((fb.getDocument().getLength() + str.length()) <= maxLength) 
			super.insertString(fb, offs, str, a);
	}
}

