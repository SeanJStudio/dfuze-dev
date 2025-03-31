/**
 * Project: DFuze
 * File: MainFrame.java
 * Date: Mar 2, 2020
 * Time: 8:48:35 PM
 */
package com.mom.dfuze.ui;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import net.miginfocom.swing.MigLayout;
import java.awt.Color;

/**
 * MainFrame Class to display the main frame of the Dfuze application to the user
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

  private static final Logger LOG = LogManager.getLogger(MainFrame.class);

  private JMenuBar menuBar;

  // File items
  private JMenuItem mntmNewJob;
  private JMenuItem mntmQuit;

  // About items
  private JMenuItem mntmAbout;

  private JProgressBar progressBar;

  private boolean isJobComplete = false;
  private boolean isLoadComplete = false;
  private JMenu mnTools;
  private JMenuItem mntmDedupe;

  private JMenu mnData;
  private JMenuItem mntmExportData;

  // Job
  private JMenu mnJob;
  private JMenuItem mntmRunJob;
  private JMenuItem mntmInkjet;
  private JMenuItem mntmDataVerification;
  private JMenuItem mntmEncodingCorrection;
  private JMenuItem mntmCasingConversion;
  private JMenuItem mntmProofMaker;
  private JMenuItem mntmSampleMaker;
  private JMenuItem mntmViewData;
  private JMenuItem mntmNewMenuItem;

  /**
   * Create the frame.
   */
  public MainFrame() {
    getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 12));
    LOG.debug("Creating the MainFrame");

    createUi();
    addEventHandlers();
  }

  /**
   * 
   */
  private void createUi() {
    LOG.debug("createUi");
    // setTitle("Dfuze");

    setBounds(100, 100, 450, 300);
    setResizable(false);
    menuBar = new JMenuBar();
    menuBar.setForeground(Color.DARK_GRAY);
    menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    setJMenuBar(menuBar);

    JMenu mnFile = new JMenu("File ");
    mnFile.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    menuBar.add(mnFile);

    mntmNewJob = new JMenuItem("New Job");
    mntmNewJob.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmNewJob.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
    mnFile.add(mntmNewJob);

    JSeparator separator_1 = new JSeparator();
    mnFile.add(separator_1);

    mntmQuit = new JMenuItem("Quit");
    mntmQuit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0));
    mnFile.add(mntmQuit);

    mnJob = new JMenu("Job ");
    mnJob.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    menuBar.add(mnJob);

    mntmRunJob = new JMenuItem("Run job");
    mntmRunJob.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmRunJob.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
    mnJob.add(mntmRunJob);
    mnJob.setEnabled(false);

    mnTools = new JMenu("Tools ");
    mnTools.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    mnTools.setEnabled(false);
    menuBar.add(mnTools);
    
    mntmDataVerification = new JMenuItem("Data Verification");
    mntmDataVerification.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmDataVerification.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0));
    mntmDataVerification.setEnabled(false);

    mntmDedupe = new JMenuItem("Dedupe");
    mntmDedupe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmDedupe.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0));
    mntmDedupe.setEnabled(false);
    
    mntmInkjet = new JMenuItem("Inkjet Maker");
    mntmInkjet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmInkjet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0));   
    mntmInkjet.setEnabled(false);
    
    mntmEncodingCorrection = new JMenuItem("Encoding Correction");
    mntmEncodingCorrection.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmEncodingCorrection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0));
    mntmEncodingCorrection.setEnabled(false);
    
    mntmCasingConversion = new JMenuItem("Casing Conversion");
    mntmCasingConversion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmCasingConversion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0));
    mntmCasingConversion.setEnabled(false);
    
    mnTools.add(mntmCasingConversion);
    mnTools.add(mntmDataVerification);
    mnTools.add(mntmDedupe);
    mnTools.add(mntmEncodingCorrection);
    mnTools.add(mntmInkjet);
    
    //mntmNewMenuItem = new JMenuItem("Label Maker");
    //mntmNewMenuItem.setEnabled(false);
    //mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0));
    //mnTools.add(mntmNewMenuItem);
    
    mntmProofMaker = new JMenuItem("Proof Maker");
    mntmProofMaker.setEnabled(false);
    mntmProofMaker.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
    mnTools.add(mntmProofMaker);
    
    mntmSampleMaker = new JMenuItem("Sample Maker");
    mntmSampleMaker.setEnabled(false);
    mntmSampleMaker.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
    mnTools.add(mntmSampleMaker);

    mnData = new JMenu("Data");
    mnData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    menuBar.add(mnData);
    mnData.setEnabled(false);

    mntmExportData = new JMenuItem("Export");
    mntmExportData.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmExportData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0));
    mnData.add(mntmExportData);
    
    mntmViewData = new JMenuItem("View");
    mntmViewData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0));
    mnData.add(mntmViewData);

    JMenu mnHelp = new JMenu("Help ");
    mnHelp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    menuBar.add(mnHelp);

    mntmAbout = new JMenuItem("About");
    mntmAbout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    mntmAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    mnHelp.add(mntmAbout);

    getContentPane().setLayout(new MigLayout("", "[][grow]", "[grow][][24px:n]"));

    progressBar = new JProgressBar();
    progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    getContentPane().add(progressBar, "cell 0 1 2 2,grow");

    try {
      BufferedImage img = ImageIO.read(new File("img/logo.png"));
      BufferedImage logo128 = ImageIO.read(new File("img/logo_icon_128.png"));
      BufferedImage logo64 = ImageIO.read(new File("img/logo_icon_64.png"));
      BufferedImage logo32 = ImageIO.read(new File("img/logo_icon_32.png"));
      BufferedImage logo16 = ImageIO.read(new File("img/logo_icon_16.png"));
      List<BufferedImage> icons = new ArrayList<>();
      icons.add(logo128);
      icons.add(logo64);
      icons.add(logo32);
      icons.add(logo16);
      setIconImages(icons);
      Image dimg = img.getScaledInstance(161, 84, Image.SCALE_SMOOTH);
      ImageIcon imageIcon = new ImageIcon(dimg);
      JLabel label = new JLabel(imageIcon);
      getContentPane().add(label, "cell 0 0 2 1,alignx center,aligny center");

    } catch (IOException e) {
      e.printStackTrace();
    }

  }
  
  /**
   * 
   */
  private void addEventHandlers() {
    LOG.debug("addEventHandlers");

    new UiController(this);

    addWindowListener(new UiController.MainFrameWindowEevntHandler());

    // File event handlers
    mntmNewJob.addActionListener(new UiController.NewJobMenuItemHandler());
    mntmQuit.addActionListener(new UiController.QuitMenuItemHandler());

    mntmRunJob.addActionListener(new UiController.RunJobItemHandler());

    mntmExportData.addActionListener(new UiController.DataExportItemHandler());
    
    mntmViewData.addActionListener(new UiController.DataViewItemHandler());
    
    mntmDataVerification.addActionListener(new UiController.ToolsDataVerificationItemHandler());
    
    mntmDedupe.addActionListener(new UiController.ToolsDedupeItemHandler());
    
    mntmInkjet.addActionListener(new UiController.ToolsInkjetMakerItemHandler());
    
    mntmEncodingCorrection.addActionListener(new UiController.ToolsEncodingCorrectionHandler());
    
    mntmCasingConversion.addActionListener(new UiController.ToolsCasingConversionHandler());
    
    mntmProofMaker.addActionListener(new UiController.ToolsProofMakerHandler());
    
    mntmSampleMaker.addActionListener(new UiController.ToolsSampleMakerHandler());

    // About event handlers
    mntmAbout.addActionListener(new UiController.AboutMenuItemHandler());
  }

  public void setIsJobComplete(boolean isJobComplete) {
    this.isJobComplete = isJobComplete;
    if (isJobComplete) {
      mnData.setEnabled(true);
      mnTools.setEnabled(true);
      mntmDataVerification.setEnabled(true);
      mntmInkjet.setEnabled(true);
      mntmEncodingCorrection.setEnabled(true);
      mntmCasingConversion.setEnabled(true);
      mntmProofMaker.setEnabled(true);
      mntmSampleMaker.setEnabled(true);
      
      if(UiController.getJobSelectDialog().getSelectedJob().getRunBehavior().getRunBehaviorName().equalsIgnoreCase("dedupe")){
        mntmDedupe.setEnabled(true);
      } else {
        mntmDedupe.setEnabled(false);
      }
      mnJob.setEnabled(false);
    } else {
      mnData.setEnabled(false);
      mnTools.setEnabled(false);
    }
  }

  public void setIsLoadComplete(boolean isLoadComplete) {
    this.isLoadComplete = isLoadComplete;
    if (isLoadComplete) {
      mnJob.setEnabled(true);
    } else {
      mnJob.setEnabled(false);
    }
  }

  public boolean getIsJobComplete() {
    return isJobComplete;
  }

  public boolean getIsLoadComplete() {
    return isLoadComplete;
  }

  public JProgressBar getJProgressBar() {
    return progressBar;
  }

public JMenuItem getMntmNewJob() {
	return mntmNewJob;
}

public void setMntmNewJob(JMenuItem mntmNewJob) {
	this.mntmNewJob = mntmNewJob;
}


public void setMenuBar(JMenuBar menuBar) {
	this.menuBar = menuBar;
}

public JMenuItem getMntmQuit() {
	return mntmQuit;
}

public void setMntmQuit(JMenuItem mntmQuit) {
	this.mntmQuit = mntmQuit;
}

public JMenuItem getMntmAbout() {
	return mntmAbout;
}

public void setMntmAbout(JMenuItem mntmAbout) {
	this.mntmAbout = mntmAbout;
}

public JMenu getMnTools() {
	return mnTools;
}

public void setMnTools(JMenu mnTools) {
	this.mnTools = mnTools;
}

public JMenuItem getMntmDedupe() {
	return mntmDedupe;
}

public void setMntmDedupe(JMenuItem mntmDedupe) {
	this.mntmDedupe = mntmDedupe;
}

public JMenu getMnData() {
	return mnData;
}

public void setMnData(JMenu mnData) {
	this.mnData = mnData;
}

public JMenuItem getMntmExportData() {
	return mntmExportData;
}

public void setMntmExportData(JMenuItem mntmExportData) {
	this.mntmExportData = mntmExportData;
}

public JMenu getMnJob() {
	return mnJob;
}

public void setMnJob(JMenu mnJob) {
	this.mnJob = mnJob;
}

public JMenuItem getMntmRunJob() {
	return mntmRunJob;
}

public void setMntmRunJob(JMenuItem mntmRunJob) {
	this.mntmRunJob = mntmRunJob;
}

public JMenuItem getMntmInkjet() {
	return mntmInkjet;
}

public void setMntmInkjet(JMenuItem mntmInkjet) {
	this.mntmInkjet = mntmInkjet;
}

public JMenuItem getMntmDataVerification() {
	return mntmDataVerification;
}

public void setMntmDataVerification(JMenuItem mntmDataVerification) {
	this.mntmDataVerification = mntmDataVerification;
}

public JMenuItem getMntmEncodingCorrection() {
	return mntmEncodingCorrection;
}

public void setMntmEncodingCorrection(JMenuItem mntmEncodingCorrection) {
	this.mntmEncodingCorrection = mntmEncodingCorrection;
}

public JMenuItem getMntmCasingConversion() {
	return mntmCasingConversion;
}

public void setMntmCasingConversion(JMenuItem mntmCasingConversion) {
	this.mntmCasingConversion = mntmCasingConversion;
}

public JMenuItem getMntmProofMaker() {
	return mntmProofMaker;
}

public void setMntmProofMaker(JMenuItem mntmProofMaker) {
	this.mntmProofMaker = mntmProofMaker;
}

public JMenuItem getMntmSampleMaker() {
	return mntmSampleMaker;
}

public void setMntmSampleMaker(JMenuItem mntmSampleMaker) {
	this.mntmSampleMaker = mntmSampleMaker;
}

public JMenuItem getMntmViewData() {
	return mntmViewData;
}

public void setMntmViewData(JMenuItem mntmViewData) {
	this.mntmViewData = mntmViewData;
}

public JMenuItem getMntmNewMenuItem() {
	return mntmNewMenuItem;
}

public void setMntmNewMenuItem(JMenuItem mntmNewMenuItem) {
	this.mntmNewMenuItem = mntmNewMenuItem;
}

public void setJobComplete(boolean isJobComplete) {
	this.isJobComplete = isJobComplete;
}

public void setLoadComplete(boolean isLoadComplete) {
	this.isLoadComplete = isLoadComplete;
}
  
  

}