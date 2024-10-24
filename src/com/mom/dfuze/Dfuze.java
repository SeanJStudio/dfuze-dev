/**
 * Project: DFuze
 * File: Dfuze.java
 * Date: Mar 2, 2020
 * Time: 8:19:53 PM
 */
package com.mom.dfuze;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Instant;
import java.util.ArrayList;

import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.formdev.flatlaf.FlatLightLaf;
import com.mom.dfuze.data.InputData;
import com.mom.dfuze.io.AccessReader;
import com.mom.dfuze.ui.ErrorDialog;
import com.mom.dfuze.ui.MainFrame;
import com.mom.dfuze.ui.UiController;

/**
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
public class Dfuze {

	public static final String APP_NAME = "Dfuze";
	private static final String LOG4J_CONFIG_FILENAME = "log4j2.xml";
	private static Instant startTime;

  static {
    configureLogging();
  }

  private static final Logger LOG = LogManager.getLogger(Dfuze.class);

  private static void configureLogging() {
    ConfigurationSource source;
    try {
      FileInputStream logFile = new FileInputStream(LOG4J_CONFIG_FILENAME);
      try {
        source = new ConfigurationSource(logFile);
        Configurator.initialize(null, source);
      } catch (IOException e) {
        System.out.println(String.format("Can't find the log4j logging configuration file %s.", LOG4J_CONFIG_FILENAME));
      } finally {
        logFile.close();
      }
    } catch (Exception e) {
      System.out.println("Could not close the log file.");
    }

  }

  /**
   * @param args
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static void main(String[] args) {
    startTime = Instant.now();
    LOG.info("Start: " + startTime);

    Dfuze dfuze = null;
    try {
      dfuze = new Dfuze();
      dfuze.run();
    } catch (Exception e) {
      new ErrorDialog(e.getMessage());
      LOG.error(e.getMessage());
    }

    LOG.debug("main thread exiting");
  }

  public Dfuze() throws FileNotFoundException, IOException, ApplicationException {

  }

  private void run() {
    try {
      createUI();
    } catch (Exception e) {
      new ErrorDialog(e.getMessage());
      LOG.error(e.getMessage());
    }
  }

  public static void createUI() {
    LOG.debug("Creating the UI");
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          LOG.debug("Setting the Look & Feel");
          FlatLightLaf.registerCustomDefaultsSource( "com.mom.dfuze.themes" );
          FlatLightLaf.setup(); // install FlatLaf theme so we dont look like boomers
          
          //UIManager.put( "ScrollBar.showButtons", true );
          //UIManager.put( "ScrollBar.thumbArc", 999 );
         // UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
          
          UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE); // incase someone hits enter instead of clicking yes

          
          MainFrame frame = new MainFrame();
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);

          //authenticateMac(); // authenticate here
          
        } catch (Exception e) {
          new ErrorDialog(e.getMessage());
          LOG.error(e.getMessage());
        }
      }
    });
  }
  
  public static void authenticateMac() throws Exception {
	  ArrayList<String> macList = new ArrayList<>();
	  
	  final String DASHIA_MAC = "00-E0-4C-02-45-23";
	  final String SEAN_MAC = "98-EE-CB-31-93-10";
	  final String SEAN_HOME_MAC = "D4-3D-7E-BE-56-DD";
	  final String JEFF_MAC = "98-EE-CB-31-96-44";
	  final String CAM_MAC = "98-EE-CB-3A-3F-B4";
	  final String MING_MAC = "98-EE-CB-2D-F2-EB";
	  final String CHEN_MAC = "B8-AC-6F-1F-78-14";
	  final String DISHA_MAC = "00-25-64-C9-48-DF";
	  final String DIANNA_MAC = "00-23-24-A8-F4-74";
	  final String WILL_MAC = "00-23-24-A8-75-12";
	  
	  macList.add(DASHIA_MAC);
	  macList.add(SEAN_MAC);
	  macList.add(SEAN_HOME_MAC);
	  macList.add(JEFF_MAC);
	  macList.add(CAM_MAC);
	  macList.add(MING_MAC);
	  macList.add(CHEN_MAC);
	  macList.add(DISHA_MAC);
	  macList.add(DIANNA_MAC);
	  macList.add(WILL_MAC);
	  
	  String userMAC = getMACAddress();
	  
	  boolean isAuthenticated = false;

	  for(String mac : macList) {
		  if(userMAC.equalsIgnoreCase(mac)) {
			  isAuthenticated = true;
			  break;
		  }
	  }
	  
	  if (isAuthenticated) {
	      System.out.println("authenticated successfully");
	    } else {
	      UiController.handle(new ApplicationException(String.format("Authentication failed.\nPlease contact the Dfuze Admin")));
	      LOG.error(String.format("Dfuze authentication failed"));
	      System.exit(0);
	    }
  }
  
  private static String getMACAddress() throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
		byte[] hardwareAddress = ni.getHardwareAddress();
		
		String[] hexadecimal = new String[hardwareAddress.length];
		for (int i = 0; i < hardwareAddress.length; i++) {
		    hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
		}
		return String.join("-", hexadecimal);
	}

  /**
   * @return the startTime
   */
  public static Instant getStartTime() {
    return startTime;
  }
  
  public static void authenticateDb() throws Exception {

	    Boolean isAuthenticated = false;
	    File file = new File("Z:\\Dfuze\\auth.mdb");
	    final String TABLE_NAME = "AUTHENTICATE";
	    final String LICENSE_KEY = "d>:pZwko8S$z'WR s[)y)'+*8;eW=xARN5[#c`+]gGuK`tv6jT/TF&r,7$-yBg=S";

	    if (file.exists()) {
	      try {
	        InputData authData = AccessReader.read(file, TABLE_NAME);

	        String foundKey = authData.getData()[0][1];

	        if (LICENSE_KEY.equals(foundKey))
	          isAuthenticated = true;

	      } catch (Exception e) {
	        throw new Exception(e.getMessage());
	      }
	    }

	    if (isAuthenticated) {
	      System.out.println("authenticated successfully");
	    } else {
	      UiController.handle(new ApplicationException(String.format("Authentication failed.\nPlease contact the Dfuze Admin")));
	      LOG.error(String.format("Dfuze authentication failed"));
	      System.exit(0);
	    }

	  }

}
