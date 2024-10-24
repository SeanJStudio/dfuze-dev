/**
 * Project: Dfuze
 * File: DatabaseSelectDialog.java
 * Date: May 27, 2020
 * Time: 6:57:45 AM
 */
package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import com.mom.dfuze.data.Theme;

import net.miginfocom.swing.MigLayout;
import java.awt.Color;

/**
 * Database select Dialog to allow users to select a table name from a dropdown list of databse table names
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class DatabaseSelectDialog extends JDialog {
  private final JPanel contentPanel = new JPanel();
  private JButton btnNext;
  private JComboBox<String> comboBoxDatabaseNames;
  JCheckBox chckbxMergeDatabasesCheckBox;
  private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXXXXXXXXXXXXXXX";

  private boolean isNextPressed = false;
  private JCheckBox chckbxCreateHeaders;

  public DatabaseSelectDialog(Frame frame, String[] databaseNames) {
    setResizable(false);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModalityType(ModalityType.APPLICATION_MODAL);
    setSize(388, 213);
    setLocationRelativeTo(frame);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBackground(new Color(245, 245, 245));
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(
            new MigLayout("", "[6px:n:6px][34px:n:34px][34px:n:34px][304px:n:34px][14px:n:14px][34px:n:34px][34px:n:34px][34px:n:34px][14px:n:14px][34px:n:34px][34px:n:34px][6px:n:6px]", "[6px:n:6px][28px:n:28px][28px:n:28px][14px:n:14px][28px:n:28px][28px:n:28px]"));

    JLabel lblSelect = new JLabel("Select the database to load");
    lblSelect.setForeground(Theme.TITLE_COLOR);
    lblSelect.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
    contentPanel.add(lblSelect, "cell 1 1 7 1,aligny center");

    comboBoxDatabaseNames = new JComboBox<String>(getDatabaseSelectComboBoxModel(databaseNames));
    comboBoxDatabaseNames.setForeground(new Color(0, 0, 0));
    comboBoxDatabaseNames.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    comboBoxDatabaseNames.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
    contentPanel.add(comboBoxDatabaseNames, "cell 1 2 10 1,grow");

    JSeparator separator = new JSeparator();
    contentPanel.add(separator, "cell 0 4 12 1,growx,aligny center");

    chckbxMergeDatabasesCheckBox = new JCheckBox("Merge all databases");
    chckbxMergeDatabasesCheckBox.setForeground(Color.BLACK);
    chckbxMergeDatabasesCheckBox.setBackground(new Color(245, 245, 245));
    chckbxMergeDatabasesCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    contentPanel.add(chckbxMergeDatabasesCheckBox, "cell 1 5 4 1");
        
        chckbxCreateHeaders = new JCheckBox("Create headers");
        chckbxCreateHeaders.setForeground(Color.BLACK);
        chckbxCreateHeaders.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chckbxCreateHeaders.setBackground(new Color(245, 245, 245));
        contentPanel.add(chckbxCreateHeaders, "cell 5 5 3 1");
    
        btnNext = new JButton("Next");
        btnNext.setBackground(new Color(255, 255, 255));
        btnNext.setForeground(new Color(0, 0, 0));
        btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(btnNext, "cell 9 5 2 1,grow");
    btnNext.addActionListener(new NextButtonHandler());
  }

  public boolean isNextPressed() {
    return isNextPressed;
  }

  public void disableChckbxMergeDatabases() {
    chckbxMergeDatabasesCheckBox.setEnabled(false);
  }

  public boolean isMergeDatabasesChecked() {
    return chckbxMergeDatabasesCheckBox.isSelected();
  }

  public String getSelectedDatabase() {
    return comboBoxDatabaseNames.getSelectedItem().toString();
  }
  
  public boolean isCreateHeaderRowSelected() {
	  return chckbxCreateHeaders.isSelected();
  }

  private class NextButtonHandler implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        // LOG.debug("Customer List item pressed");
        if (e.getSource() == btnNext) {
          isNextPressed = true;
          DatabaseSelectDialog.this.dispose();
        }
      } catch (Exception err) {
        UiController.handle(err);
      }
    }
  }

  private DefaultComboBoxModel<String> getDatabaseSelectComboBoxModel(String[] array) {
    return new DefaultComboBoxModel<>(array);
  }
}
