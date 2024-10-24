/**
 * Project: Dfuze
 * File: DelimiterSelectDialog.java
 * Date: Apr 12, 2020
 * Time: 7:33:20 AM
 */
package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Theme;


import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import javax.swing.JCheckBox;

/**
 * DelimiterSelectDialog to allow users to select how text based files should be delimited upon opening
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class DelimiterSelectDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private JTextField delimiterField;
  private boolean isNextPressed = false;

  private JLabel lblDelimiterLabel;
  private JComboBox<String> comboBoxDelimiter;
  private JLabel lblChar;
  private JScrollPane scrollPane;
  private JTextArea textArea;
  private JLabel lblDelimiterSelect;
  private JCheckBox chckbxCreateHeaders;
  private JCheckBox chckbxFixedWidth;
  private JButton btnFixedHelp;
  private JButton btnDelimitHelp;

  public DelimiterSelectDialog(Frame frame, String previewText) {
	  super(frame, true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModalityType(ModalityType.APPLICATION_MODAL);
    //setSize(682, 442);
    setResizable(false);

    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("insets 10 28 20 28, gap 0", "[64px:n:64px][64px:n:64px][28px:n:28px][36px:n:36px][][260px:n:260px][28px:n:28px][128px:n:128px]", "[14px:n:14px][36px:n:36px][336px:n:336px][14px:n:14px][36px:n:36px][28px:n:28px][36px:n:36px][28px:n:28px]"));

    lblDelimiterSelect = new JLabel("Delimit Text");
    lblDelimiterSelect.setForeground(Theme.TITLE_COLOR);
    lblDelimiterSelect.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
    contentPanel.add(lblDelimiterSelect, "cell 0 0 1 2,aligny center");
    
    btnDelimitHelp = new JButton("?");
    btnDelimitHelp.setForeground(Color.BLACK);
    btnDelimitHelp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    btnDelimitHelp.addActionListener(new HelpDelimitButtonHandler());
    btnDelimitHelp.putClientProperty( "JButton.buttonType", "help" );
    contentPanel.add(btnDelimitHelp, "cell 1 1,alignx center,aligny top");

    scrollPane = new JScrollPane();
    contentPanel.add(scrollPane, "cell 0 2 8 1,grow");

    textArea = new JTextArea();
    textArea.setForeground(Theme.TITLE_COLOR);
    textArea.setBackground(Theme.BG_LIGHT_COLOR);
    textArea.setFont(new Font("Consolas", Font.PLAIN, 11));
    setPreviewText(previewText);
    textArea.setBorder(BorderFactory.createCompoundBorder(
    		textArea.getBorder(), 
            BorderFactory.createEmptyBorder(4, 1, 4, 4)));
    scrollPane.setViewportView(textArea);
    textArea.setSelectionStart(0);
    textArea.setSelectionEnd(0);
    
    final UndoManager undo = new UndoManager(); //instantiate an UndoManager
    Document doc = textArea.getDocument();  //instantiate a Document class of the txtArea
    
    doc.addUndoableEditListener(new UndoableEditListener() {
    	@Override
      public void undoableEditHappened(UndoableEditEvent evt) {
        undo.addEdit(evt.getEdit());
      }

    });
 
    textArea.getActionMap().put("Undo", new AbstractAction("Undo") {
      public void actionPerformed(ActionEvent evt) {
        try {
          if (undo.canUndo()) {
            undo.undo();
          }
        } catch (CannotUndoException e) {
        }
      }
    });
 
    textArea.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
    

    lblDelimiterLabel = new JLabel("Delimit by");
    lblDelimiterLabel.setForeground(Color.BLACK);
    lblDelimiterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    
    contentPanel.add(lblDelimiterLabel, "cell 0 4 2 1,alignx left,aligny center");

    lblChar = new JLabel("Char");
    lblChar.setForeground(Color.BLACK);
    lblChar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    contentPanel.add(lblChar, "cell 3 4,aligny center");

    comboBoxDelimiter = new JComboBox<String>(getClientComboBoxModel(new String[] { "Char", "Tab" }));
    comboBoxDelimiter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    contentPanel.add(comboBoxDelimiter, "cell 0 5 2 1,grow");

    comboBoxDelimiter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          if (e.getSource() == comboBoxDelimiter) {
            String selectedDelimiter = comboBoxDelimiter.getSelectedItem().toString();
            if (selectedDelimiter.equalsIgnoreCase("char")) {
              delimiterField.setEnabled(true);
              lblChar.setEnabled(true);
            } else {
              delimiterField.setEnabled(false);
              lblChar.setEnabled(false);
            }
          }

        } catch (Exception err) {
          UiController.handle(err);
        }
      }
    });

    delimiterField = new JTextField();
    delimiterField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    delimiterField.setHorizontalAlignment(SwingConstants.CENTER);
    delimiterField.setDocument(new JTextFieldLimit(1));
    delimiterField.setText(",");
    contentPanel.add(delimiterField, "cell 3 5,grow");
    delimiterField.setColumns(10);

    JSeparator separator = new JSeparator();
    contentPanel.add(separator, "cell 0 6 8 1,growx,aligny center");
    
    chckbxFixedWidth = new JCheckBox("Delimit fixed width");
    chckbxFixedWidth.setForeground(Color.BLACK);
    chckbxFixedWidth.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    chckbxFixedWidth.addActionListener(new DelimitFixedWidthHandler());
    contentPanel.add(chckbxFixedWidth, "cell 0 7,alignx left,aligny center");
    
    btnFixedHelp = new JButton("?");
    btnFixedHelp.setForeground(Color.BLACK);
	btnFixedHelp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	btnFixedHelp.putClientProperty( "JButton.buttonType", "help" );
	btnFixedHelp.addActionListener(new HelpFixedButtonHandler());
    contentPanel.add(btnFixedHelp, "cell 2 7");
    
    chckbxCreateHeaders = new JCheckBox("Create headers");
    chckbxCreateHeaders.setForeground(Color.BLACK);
    chckbxCreateHeaders.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    contentPanel.add(chckbxCreateHeaders, "cell 5 7,alignx right");

    JButton btnNewButton = new JButton("Next");
    btnNewButton.setBackground(new Color(255, 255, 255));
    btnNewButton.setForeground(new Color(0, 0, 0));
    btnNewButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    contentPanel.add(btnNewButton, "cell 7 7,grow");
    btnNewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {

          if (e.getSource() == btnNewButton) {
            if (delimiterField.getText().equals(""))
              throw new ApplicationException(String.format("Delimiter value cannot be blank."));

            isNextPressed = true;
            DelimiterSelectDialog.this.dispose();
          }
        } catch (Exception err) {
          UiController.handle(err);
        }
      }
    });
    
    pack();
    setLocationRelativeTo(frame);
    
    delimiterField.requestFocusInWindow();
  }
  
  private class HelpFixedButtonHandler implements ActionListener {
		private HelpFixedButtonHandler() {}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnFixedHelp) {
				JEditorPane editorPane = new JEditorPane("text/html", 
						"<html><body><pre>To delimit fixed width data, follow these steps:\r\n"
						+ "\r\n"
						+ "  1. On the first line of the <span style='color:red'>data under this window,</span> add a '|' delimiter between each field.\r\n"
						+ "\r\n"
						+ "  2. The data will then be split into columns based on the location of the '|' character.\r\n"
						+ "\r\n"
						+ "For example, if your data looks like this:\r\n"
						+ "\r\n"
						+ "1234567890\r\n"
						+ "\r\n"
						+ "You would add a '|' between each field to create a delimited version of the data:\r\n"
						+ "\r\n"
						+ "123|456|789|0\r\n"
						+ "\r\n"
						+ "The data will then be split into columns based on the location of the '|' character,\r\n"
						+ "allowing you to easily read and manipulate the data.</pre></body></html>");
				editorPane.setEditable(false);
				JOptionPane.showMessageDialog(DelimiterSelectDialog.this, editorPane, "Fixed width", 1);
			} 
		}
	}
  
  private class HelpDelimitButtonHandler implements ActionListener {
		private HelpDelimitButtonHandler() {}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnDelimitHelp) {
				JEditorPane editorPane = new JEditorPane("text/html", 
						"<html><body><pre>To delimit your imported data, select the \"Delimit by\" option and choose either \r\n"
						+ "a delimiter of your choice or fixed width. You can also choose to automatically create a header \r\n"
						+ "row for your data by checking the \"Create header row\" option. This will generate a row of column \r\n"
						+ "names based on the delimited data. Please note that choosing a delimiter or fixed width and creating \r\n"
						+ "a header row are optional and can be adjusted at any time during the import process.</pre></body></html>");
				editorPane.setEditable(false);
				JOptionPane.showMessageDialog(DelimiterSelectDialog.this, editorPane, "Fixed width", 1);
			} 
		}
	}
  
  public void disableDelimiters() {
		delimiterField.setEnabled(false);
		lblChar.setEnabled(false);
		comboBoxDelimiter.setEnabled(false);
		lblDelimiterLabel.setEnabled(false);
  }
  
  private class DelimitFixedWidthHandler implements ActionListener {
		private DelimitFixedWidthHandler() {}

		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == chckbxFixedWidth) {
				if(chckbxFixedWidth.isSelected()) {
					delimiterField.setEnabled(false);
					lblChar.setEnabled(false);
					comboBoxDelimiter.setEnabled(false);
					lblDelimiterLabel.setEnabled(false);
				} else {
					comboBoxDelimiter.setEnabled(true);
					lblDelimiterLabel.setEnabled(true);
					String selectedDelimiter = comboBoxDelimiter.getSelectedItem().toString();
					if (selectedDelimiter.equalsIgnoreCase("char")) {
			              delimiterField.setEnabled(true);
			              lblChar.setEnabled(true);
			            } else {
			              delimiterField.setEnabled(false);
			              lblChar.setEnabled(false);
			            }
				}
			} 
		}
	}

  public char getDelimiterField() {
    if (comboBoxDelimiter.getSelectedItem().toString().equalsIgnoreCase("char"))
      return delimiterField.getText().charAt(0);

    return '\t';
  }

  public boolean getIsNextPressed() {
    return isNextPressed;
  }
  
  public boolean getIsCreateHeaderRowSelected() {
	  return chckbxCreateHeaders.isSelected();
  }
  
  public void setChckbxCreateHeaders(boolean b) {
	  chckbxCreateHeaders.setSelected(b);
  }

  private DefaultComboBoxModel<String> getClientComboBoxModel(String[] array) {
    return new DefaultComboBoxModel<>(array);
  }

  private void setPreviewText(String previewText) {
    if (previewText != null)
      textArea.setText(previewText);
  }
  
  
  

public JCheckBox getChckbxFixedWidth() {
	return chckbxFixedWidth;
}

public void setChckbxFixedWidth(JCheckBox chckbxFixedWidth) {
	this.chckbxFixedWidth = chckbxFixedWidth;
}

public String getPreviewText() {
	  return textArea.getText();
  }
  
  public void setDelimiterToTab() {
	  this.comboBoxDelimiter.setSelectedIndex(1);
  }
  
  public void setDelimiterField(Character c) {
		delimiterField.setText(c.toString());
	}

}
