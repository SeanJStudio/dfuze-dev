/**
 * Project: Dfuze
 * File: JobSelectDialog.java
 * Date: Mar 8, 2020
 * Time: 3:55:55 PM
 */
package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import com.mom.dfuze.data.Job;
import com.mom.dfuze.data.Theme;

import net.miginfocom.swing.MigLayout;

/**
 * JobSelectDialog to allow users to select which Job should be run
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class JobSelectDialog extends JDialog {

  private boolean isLoadDataPressed = false;
  private final JPanel contentPanel = new JPanel();
  private JComboBox<Object> jobComboBox;
  private JButton btnLoadData;
  private JComboBox<Object> clientComboBox;
  private JSeparator separator;
  Map<String, String> jobBehaviorDescriptionList;
  private JTextPane textPane;
  private JScrollPane scrollPane;
  private DefaultComboBoxModel<Object> jobBehaviorNameModel;
  private DefaultComboBoxModel<Object> jobClientNameModel;
  private JLabel lblJob;

  private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXXXXXXXXXXX";
  private JCheckBox chckbxAddFileName;
  private JLabel lblJobSelect;
  private JLabel lblJobSelectionDescription;
  private JSeparator separator_1;
  private Job[] jobs;

  public JobSelectDialog(JFrame frame, Job[] systemJobs) {
    super(frame, true);
    setModalityType(ModalityType.APPLICATION_MODAL);
    setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    setTitle("New Job");

    setResizable(false);


    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("insets 0, gap 0", "[28px:n:28px][106px:n:106px][14px:n:14px][106px:n:106px][42px:n:42px][106px:n:106px][14px:n:14px][106px:n:106px][14px:n:14px][106px:n:106px][14px:n:14px][106px:n:106px][28px:n:28px]", "[28px:n:28px][36px:n:36px][28px:n:28px][28px:n:28px][36px:n:36px][36px:n:36px][28px:n:28px][10px:n:10px][36px:n:36px][28px:n:28px][36px:n:36px][24px:n:24px][28px:n:28px][24px:n:24px]"));

    lblJobSelect = new JLabel("New Job");
    lblJobSelect.setForeground(Theme.TITLE_COLOR);
    lblJobSelect.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
    contentPanel.add(lblJobSelect, "cell 1 1 3 1,aligny top");

    jobs = systemJobs;
    setClientComboBox(jobs);

    lblJobSelectionDescription = new JLabel("<html>Choose a Client and Job below. Details about the Job will display on the right. Press Load Data to continue.</html>");
    lblJobSelectionDescription.setForeground(Color.BLACK);
    lblJobSelectionDescription.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    contentPanel.add(lblJobSelectionDescription, "cell 1 2 3 2,aligny center");
    
    separator_1 = new JSeparator();
    contentPanel.add(separator_1, "cell 1 4 3 1,growx");

    JLabel lblClient = new JLabel("Client");
    lblClient.setForeground(Color.BLACK);
    lblClient.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    contentPanel.add(lblClient, "cell 1 5 3 1,growy");

    contentPanel.add(clientComboBox, "cell 1 6 3 1,grow");

    clientComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {

          if (e.getSource() == clientComboBox && clientComboBox.getSelectedIndex() != -1) {
            Job job = (Job) clientComboBox.getSelectedItem();

            Set<String> jobBehaviorNameDupeCheck = new HashSet<>();

            jobBehaviorNameModel = new DefaultComboBoxModel<Object>();

            for (int i = 0; i < systemJobs.length; ++i)
              if (job.getClientName().equals(systemJobs[i].getClientName())
                      && jobBehaviorNameDupeCheck.add(systemJobs[i].getRunBehavior().getRunBehaviorName()))
                jobBehaviorNameModel.addElement(systemJobs[i]);

            JobSelectDialog.this.jobComboBox.setModel(jobBehaviorNameModel);
            jobComboBox.setRenderer(new DefaultListCellRenderer() {
              @Override
              public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Job) {
                  Job job = (Job) value;
                  setText(job.getRunBehavior().getRunBehaviorName());
                }
                return this;
              }
            });

            Job selectedJob = (Job) jobComboBox.getSelectedItem();
            textPane.setText(selectedJob.getRunBehavior().getDescription());
            jobComboBox.setEnabled(true); // enable job combo box
            lblJob.setEnabled(true);
            btnLoadData.setEnabled(true);// enable load button

            if(job.getRunBehavior().isFileNameRequired()) {
            	chckbxAddFileName.setSelected(true);
            	chckbxAddFileName.setEnabled(false);
            } else {
            	chckbxAddFileName.setSelected(false);
            	chckbxAddFileName.setEnabled(true);
            }
            
            
            System.out.println(job.getClientName());
          }

        } catch (Exception err) {
          UiController.handle(err);
        }
      }
    });

    scrollPane = new JScrollPane();

    contentPanel.add(scrollPane, "cell 5 1 7 9,grow");

    textPane = new JTextPane();
    textPane.setContentType("text/html");
    textPane.setBorder(BorderFactory.createCompoundBorder(
    		textPane.getBorder(), 
            BorderFactory.createEmptyBorder(24, 24, 24, 24)));
    textPane.setForeground(Theme.TITLE_COLOR);
    textPane.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    textPane.setBackground(Theme.BG_LIGHT_COLOR);
    scrollPane.setViewportView(textPane);
    textPane.setEditable(false);
    //textPane.setLineWrap(true);
   // textPane.setWrapStyleWord(true);
    DefaultCaret caret = (DefaultCaret) textPane.getCaret();
    caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE); // forces the scrollbar to the top of the scrollpane

    lblJob = new JLabel("Job");
    lblJob.setForeground(Color.BLACK);
    lblJob.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblJob.setEnabled(false);
    contentPanel.add(lblJob, "cell 1 8 3 1,growy");

    jobComboBox = new JComboBox<Object>();
    jobComboBox.setForeground(Color.BLACK);
    jobComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    jobComboBox.setEnabled(false);
    jobComboBox.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
    contentPanel.add(jobComboBox, "cell 1 9 3 1,grow");

    jobComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        try {
        	
          if (clientComboBox.getSelectedIndex() != -1) {
            Job job = (Job) jobComboBox.getSelectedItem();
            textPane.setText(job.getRunBehavior().getDescription());
            
            if(job.getRunBehavior().isFileNameRequired()) {
            	chckbxAddFileName.setSelected(true);
            	chckbxAddFileName.setEnabled(false);
            } else {
            	chckbxAddFileName.setSelected(false);
            	chckbxAddFileName.setEnabled(true);
            }
          }
          
          if(clientComboBox.getSelectedIndex() == -1) {
          	chckbxAddFileName.setSelected(false);
      		chckbxAddFileName.setEnabled(true);
          }
          
        } catch (Exception err) {
          UiController.handle(err);
        }
      }
    });

    separator = new JSeparator();
    contentPanel.add(separator, "cell 1 10 11 1,growx,aligny bottom");

    chckbxAddFileName = new JCheckBox("Add file name to data");
    chckbxAddFileName.setForeground(Color.BLACK);
    chckbxAddFileName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    chckbxAddFileName.setEnabled(false);
    chckbxAddFileName.setSelected(false);
    contentPanel.add(chckbxAddFileName, "cell 5 12 3 1,alignx right");

    btnLoadData = new JButton("Load Data");
    btnLoadData.setBackground(new Color(255, 255, 255));
    btnLoadData.setForeground(new Color(0, 0, 0));
    btnLoadData.setToolTipText("supports .accdb .csv .dat .dbf .mdb .txt .xlsx .xlsm .xls");
    btnLoadData.setFont(new Font("Segoe UI", Font.PLAIN, 14));

    contentPanel.add(btnLoadData, "cell 9 12 3 1,grow");
    btnLoadData.setEnabled(false);

    btnLoadData.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        try {
          if (e.getSource() == btnLoadData) {
            isLoadDataPressed = true;
            JobSelectDialog.this.dispose();
          }
        } catch (Exception err) {
          UiController.handle(err);
        }
      }
    });
    
    pack();
    setLocationRelativeTo(frame);
	getContentPane().requestFocusInWindow();

  }

  public void reset() {
    clientComboBox.setSelectedIndex(-1);
    jobComboBox.setSelectedIndex(-1);
    jobComboBox.setEnabled(false);
    lblJob.setEnabled(false);
    textPane.setText("<html><p></p></html>");
    btnLoadData.setEnabled(false);
    chckbxAddFileName.setSelected(false);
    chckbxAddFileName.setEnabled(false);
  }

  public boolean getIsLoadDataPressed() {
    return isLoadDataPressed;
  }

  public boolean isChckbxAddFileNameSelected() {
    return chckbxAddFileName.isSelected();
  }

  public void setIsLoadDataPressed(boolean isLoadDataPressed) {
    this.isLoadDataPressed = isLoadDataPressed;
  }

  public Job getSelectedJob() {
    return (Job) jobComboBox.getSelectedItem();
  }
  
  public Job[] getJobs() {
	    return jobs;
	  }

  private void setClientComboBox(Job[] jobs) {

    jobClientNameModel = new DefaultComboBoxModel<Object>();
    Set<String> jobNameDupeCheck = new HashSet<>();

    for (int i = 0; i < jobs.length; ++i)
      if (jobNameDupeCheck.add(jobs[i].getClientName()))
        jobClientNameModel.addElement(jobs[i]);

    clientComboBox = new JComboBox<Object>(jobClientNameModel);
    clientComboBox.setForeground(Color.BLACK);
    clientComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    clientComboBox.setRenderer(new DefaultListCellRenderer() {

      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Job) {
          Job job = (Job) value;
          setText(job.getClientName());

        }
        return this;
      }
    });

    clientComboBox.setSelectedIndex(-1);
    clientComboBox.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
  }
}
