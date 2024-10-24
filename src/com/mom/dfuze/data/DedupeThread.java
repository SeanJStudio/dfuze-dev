package com.mom.dfuze.data;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.ui.UiController;
import com.mom.dfuze.ui.dedupe.DedupeDialog;

public class DedupeThread implements Runnable {

    private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(false);
    
    private JProgressBar progressBar;
    private JLabel lblElapsedTimeAmount;
    DedupeDialog dedupeDialog;

    public DedupeThread(JProgressBar progressBar, JLabel lblElapsedTimeAmount, DedupeDialog dedupeDialog) {
    	this.progressBar = progressBar;
    	this.lblElapsedTimeAmount = lblElapsedTimeAmount;
    	this.dedupeDialog = dedupeDialog;
    }

    public void interrupt() {
        running.set(false);
        worker.interrupt();
    }

    boolean isRunning() {
        return running.get();
    }

    boolean isStopped() {
        return stopped.get();
    }

    public void run() {
        running.set(true);
        stopped.set(false);
        while (running.get()) {
            try {
            	progressBar.setMinimum(0);
				progressBar.setMaximum(UiController.getUserData().getRecordList().size());
				Instant startTime = Instant.now();
				Timer timer = new Timer(0, e -> {
					Instant endTime = Instant.now();
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					} 
					lblElapsedTimeAmount.setText(String.format("%.2f sec", new Object[] { Double.valueOf(Duration.between(startTime, endTime).toMillis() / 1000.0D) }));
				});
				timer.start();
				for (Record record : UiController.getUserData().getRecordList()) {
					record.setIsDupe(false); 
					record.setDupeGroupId(0);
					record.setDupeGroupSize(0);
				}
				dedupeDialog.dedupe(UiController.getUserData());
            } catch (ApplicationException e){
                Thread.currentThread().interrupt();
                System.out.println(
                  "Thread was interrupted, Failed to complete operation");
            }
            // do something
        }
        stopped.set(true);
    }
}