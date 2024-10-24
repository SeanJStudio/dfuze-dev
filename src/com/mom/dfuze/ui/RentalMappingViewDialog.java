/**
 * Project: Dfuze
 * File: DedupeViewDialog.java
 * Date: May 10, 2020
 * Time: 8:54:37 PM
 */
package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;

import javax.swing.border.EmptyBorder;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;

/**
 * RentalMappingViewDialog to allow users to review mapped fields by filename
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class RentalMappingViewDialog extends JDialog {
  private final JPanel contentPanel = new JPanel();
  private JTable mainTable;
  private final JLabel lblReviewDuplicationsLabel = new JLabel("Ensure the correct fields are mapped and then click continue");

  private final JSeparator separator = new JSeparator();
  private final JLabel lblDedupeViewInfo = new JLabel("<html>\r\nColumn headers:&nbsp;&nbsp;Pre-defined fields for mapping<br/>\r\nRow headers:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Names of the files<br/>\r\nCell values:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Fields mapped from the file</html>");
  private final JButton btnContinue = new JButton("Continue");
  private final JSeparator separator_1 = new JSeparator();
  private final JButton btnCancel = new JButton("Cancel");
  private boolean isContinuePressed = false;
  private boolean isCancelPressed = false;

  public RentalMappingViewDialog(String[] predefinedHeaders, String[] fileNames, String[][] fileArrayMappingData) {
  	setFont(new Font("Segoe UI", Font.PLAIN, 12));
    getContentPane().setFont(new Font("Segoe UI Semilight", Font.PLAIN, 12));

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModalityType(ModalityType.APPLICATION_MODAL);

    setTitle("Review mapped fields");

    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("insets 10 28 28 28, gap 0", "[140px:n:140px][14px:n:14px][140px:n:140px][14px:n:14px][140px:n:140px][140px:n:140px][14px:n:14px][140px:n:140px][140px:n,grow][]", "[36px:n:36px][28px:n:28px][36px:n:36px][28px:n:28px][28px:n:28px][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][100px:100px,grow][28px:n:28px][28px:n:28px]"));
    lblReviewDuplicationsLabel.setForeground(new Color(25, 25, 112));
    lblReviewDuplicationsLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));

    contentPanel.add(lblReviewDuplicationsLabel, "cell 0 0 10 1,aligny center");
    lblDedupeViewInfo.setForeground(Color.BLACK);
    lblDedupeViewInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));

    contentPanel.add(lblDedupeViewInfo, "cell 0 1 9 2,aligny center");

    contentPanel.add(separator, "cell 0 3 9 1,growx");
    
    mainTable = new JTable(fileArrayMappingData, predefinedHeaders);
    mainTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    mainTable.setFillsViewportHeight(true);
    mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    mainTable.setShowGrid(true);

    JTable rowTable = new RowNumberTable(mainTable, fileNames);
 
    for (int column = 0; column < mainTable.getColumnCount(); column++) {
        TableColumn tableColumn = mainTable.getColumnModel().getColumn(column);
        FontMetrics fm = mainTable.getFontMetrics(mainTable.getFont());
        int charWidth = fm.stringWidth(mainTable.getModel().getColumnName(column));
        int colHeaderWidth = (mainTable.getModel().getColumnName(column).length() + charWidth + 4) + mainTable.getIntercellSpacing().width;
        int preferredWidth = tableColumn.getMinWidth();
        int maxWidth = tableColumn.getMaxWidth();

        for (int row = 0; row < mainTable.getRowCount(); row++) {
          TableCellRenderer cellRenderer = mainTable.getCellRenderer(row, column);
          Component c = mainTable.prepareRenderer(cellRenderer, row, column);
          int width = c.getPreferredSize().width + mainTable.getIntercellSpacing().width;
          preferredWidth = Math.max(preferredWidth, width);
          mainTable.prepareRenderer(cellRenderer, row, column);
          // We've exceeded the maximum width, no need to check other rows

          if (preferredWidth >= maxWidth) {
            preferredWidth = maxWidth;
            break;
          }
        }

        tableColumn.setPreferredWidth((preferredWidth > colHeaderWidth) ? preferredWidth : colHeaderWidth);
      }
    
   

    JScrollPane scrollPane = new JScrollPane(mainTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    mainTable.setEnabled(false);
    mainTable. getTableHeader().setReorderingAllowed(false);
    contentPanel.add(scrollPane, "cell 0 5 10 10,grow");
    scrollPane.setRowHeaderView(rowTable);
    scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
        rowTable.getTableHeader());
    
    contentPanel.add(separator_1, "cell 0 15 9 1,growx,aligny center");
    btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    
    contentPanel.add(btnCancel, "cell 5 16,grow");
    btnCancel.addActionListener(new BtnCancelHandler());
    
    btnContinue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    
    contentPanel.add(btnContinue, "cell 7 16 2 1,grow");
    btnContinue.addActionListener(new BtnContinueHandler());
    
    pack();
	getContentPane().requestFocusInWindow();
	setLocationRelativeTo(UiController.getMainFrame());
  }
  
  
  
	public boolean isContinuePressed() {
		return isContinuePressed;
	}

	public void setContinuePressed(boolean isContinuePressed) {
		this.isContinuePressed = isContinuePressed;
	}

	public boolean isCancelPressed() {
		return isCancelPressed;
	}

	public void setCancelPressed(boolean isCancelPressed) {
		this.isCancelPressed = isCancelPressed;
	}


	private class BtnContinueHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnContinue) {
					setContinuePressed(true);
					RentalMappingViewDialog.this.dispose();
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}
	
	private class BtnCancelHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnCancel) {
					setCancelPressed(true);
					RentalMappingViewDialog.this.dispose();
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}
  
  //https://stackoverflow.com/questions/4240740/shortcut-key-for-jbutton-without-using-alt-key
  public static void clickOnKey(final AbstractButton button, String actionName, int key) {
		}

}
