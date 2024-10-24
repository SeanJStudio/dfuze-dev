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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import com.mom.dfuze.data.Theme;

import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JList;

/**
 * Database select Dialog to allow users to select an option from a dropdown list of options
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class OptionSelectDialog extends JDialog {
  private final JPanel contentPanel = new JPanel();
  private JButton btnNext;

  private boolean isNextPressed = false;

  private JScrollPane scrollPane;
  private JList<String> list;
  private JLabel lblNewLabel;

  public OptionSelectDialog(Frame frame, String prompt, String[] options) {
    setResizable(false);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModalityType(ModalityType.APPLICATION_MODAL);
    setSize(396, 400);
    setLocationRelativeTo(frame);

    setTitle("Option");
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBackground(new Color(245, 245, 245));
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(
            new MigLayout("", "[6px:n:6px][34px:n:34px,grow][34px:n:34px][304px:n:34px][14px:n:14px][34px:n:34px][34px:n:34px][34px:n:34px][14px:n:14px][34px:n:34px][34px:n:34px][6px:n:6px]", "[6px:n:6px][28px:n:28px][56px:n:56px][168px:n:168px][28px:n:28px][28px:n:28px]"));

    JLabel lblSelect = new JLabel("Select an Option");
    lblSelect.setForeground(Theme.TITLE_COLOR);
    lblSelect.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
    contentPanel.add(lblSelect, "cell 1 1 7 1,aligny top");
    
    lblNewLabel = new JLabel(String.format("<html>%s</html>", prompt));
    lblNewLabel.setForeground(Color.BLACK);
    lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    contentPanel.add(lblNewLabel, "cell 1 2 10 1,aligny top");
    
    scrollPane = new JScrollPane();
    contentPanel.add(scrollPane, "cell 1 3 10 1,grow");
    
    list = new JList<>();
    list.setForeground(Color.BLACK);
    list.setListData(options);
    list.setSelectedIndex(0);
	list.setFixedCellHeight(32);
	list.setFont(new Font("Segoe UI", Font.PLAIN, 11));
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	scrollPane.setViewportView(list);

    JSeparator separator = new JSeparator();
    contentPanel.add(separator, "cell 0 4 12 1,growx,aligny center");
    
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


  private class NextButtonHandler implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        // LOG.debug("Customer List item pressed");
        if (e.getSource() == btnNext) {
          isNextPressed = true;
          OptionSelectDialog.this.dispose();
        }
      } catch (Exception err) {
        UiController.handle(err);
      }
    }
  }
  
  public String getSelectedOption(){
	  return list.getSelectedValue();
  }

}
