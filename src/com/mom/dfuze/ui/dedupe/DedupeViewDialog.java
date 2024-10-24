/**
 * Project: Dfuze
 * File: DedupeViewDialog.java
 * Date: May 10, 2020
 * Time: 8:54:37 PM
 */
package com.mom.dfuze.ui.dedupe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.mom.dfuze.data.Record;

import net.miginfocom.swing.MigLayout;

/**
 * DedupeViewDialog to allow users to review and modify any duplicates found
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 03/02/2020
 *
 */
@SuppressWarnings("serial")
public class DedupeViewDialog extends JDialog {
  private final JPanel contentPanel = new JPanel();
  private JTable table;
  private final JButton btnSetToKeep = new JButton("Set to Keep (S)");
  private final JButton btnSetToDelete = new JButton("Set to Delete (D)");
  private final JLabel lblReviewDuplicationsLabel = new JLabel("Review duplications");
  private final JButton btnDeflag = new JButton("Deflag Record (backspace or delete)");
  private DedupeTableModel dedupeTableModel;
  private String[] selectedFields;
  private final JSeparator separator = new JSeparator();
  private final JLabel lblDedupeViewInfo = new JLabel("Rows in white are set to be kept and rows in grey are set to be deleted.");
  private DedupeDialog dedupeDialog;

  public DedupeViewDialog(DedupeDialog dedupeDialog, ArrayList<Record> dupes, String[] selectedFields) {
  	setFont(new Font("Segoe UI", Font.PLAIN, 12));
    getContentPane().setFont(new Font("Segoe UI Semilight", Font.PLAIN, 12));

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModalityType(ModalityType.APPLICATION_MODAL);
    //setSize(860, 600);
    
    this.dedupeDialog = dedupeDialog;

    setTitle("View duplicate records");
    this.selectedFields = selectedFields;
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("insets 10 28 28 28, gap 0", "[128px:n:128px][14px:n:14px][128px:n:128px][14px:n:14px][128px:n:128px][128px:n:128px][128px:n:128px][128px:n,grow][]", "[36px:n:36px][28px:n:28px][36px:n:36px][28px:n:28px][28px:n:28px][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][28px:n:28px,grow][300px:300px,grow]"));
    lblReviewDuplicationsLabel.setForeground(new Color(25, 25, 112));
    lblReviewDuplicationsLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));

    contentPanel.add(lblReviewDuplicationsLabel, "cell 0 0 9 1,aligny center");
    lblDedupeViewInfo.setForeground(Color.BLACK);
    lblDedupeViewInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));

    contentPanel.add(lblDedupeViewInfo, "cell 0 1 6 1,aligny center");

    contentPanel.add(separator, "cell 0 2 6 1,growx");
    btnSetToKeep.setBackground(new Color(255, 255, 255));
    btnSetToKeep.setForeground(new Color(0, 0, 0));
    btnSetToKeep.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    btnSetToKeep.addActionListener(new SetToKeepButtonHandler());
    clickOnKey( btnSetToKeep, "SetToKeepButtonHandler", KeyEvent.VK_S );

    contentPanel.add(btnSetToKeep, "cell 0 3,grow");
    btnSetToDelete.setBackground(new Color(255, 255, 255));
    btnSetToDelete.setForeground(new Color(0, 0, 0));
    btnSetToDelete.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    btnSetToDelete.addActionListener(new SetToDeleteButtonHandler());
    clickOnKey( btnSetToDelete, "SetToDeleteButtonHandler", KeyEvent.VK_D );

    contentPanel.add(btnSetToDelete, "cell 2 3,grow");
    btnDeflag.setBackground(new Color(255, 255, 255));
    btnDeflag.setForeground(new Color(0, 0, 0));
    btnDeflag.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    btnDeflag.addActionListener(new DeflagButtonHandler());
    clickOnKey( btnDeflag, "DeflagButtonHandler", KeyEvent.VK_BACK_SPACE );
    clickOnKey( btnDeflag, "DeflagButtonHandler", KeyEvent.VK_DELETE );

    contentPanel.add(btnDeflag, "cell 4 3,grow");
    setDedupeTableModel(dupes);
    table = new JTable(dedupeTableModel);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    table.setFillsViewportHeight(true);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setShowGrid(true);

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        String status = (String) table.getModel().getValueAt(row, 0);
        if (!table.isRowSelected(row)) {
          if ("true".equals(status)) {
            setBackground(Color.decode("#E5E5E5"));
            // setForeground(Color.WHITE);
          } else if ("false".equals(status)) {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
          }
        }
        return this;
      }
    });

    for (int column = 0; column < table.getColumnCount(); column++) {
      TableColumn tableColumn = table.getColumnModel().getColumn(column);
      FontMetrics fm = table.getFontMetrics(table.getFont());
      int charWidth = fm.stringWidth(table.getModel().getColumnName(column));
      int colHeaderWidth = (table.getModel().getColumnName(column).length() + charWidth + 4) + table.getIntercellSpacing().width;
      int preferredWidth = tableColumn.getMinWidth();
      int maxWidth = tableColumn.getMaxWidth();

      for (int row = 0; row < table.getRowCount(); row++) {
        TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
        Component c = table.prepareRenderer(cellRenderer, row, column);
        int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
        preferredWidth = Math.max(preferredWidth, width);
        table.prepareRenderer(cellRenderer, row, column);
        // We've exceeded the maximum width, no need to check other rows

        if (preferredWidth >= maxWidth) {
          preferredWidth = maxWidth;
          break;
        }
      }

      tableColumn.setPreferredWidth((preferredWidth > colHeaderWidth) ? preferredWidth : colHeaderWidth);
    }

    /*btnSetToKeep.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // check for selected row first
        if (table.getSelectedRow() != -1) {
          // remove selected row from the model
          dedupeTableModel.keep(table.getSelectedRows());
        }
      }

    });*/

   /* btnSetToDelete.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // check for selected row first
        if (table.getSelectedRow() != -1) {
          // remove selected row from the model
          dedupeTableModel.remove(table.getSelectedRows());
        }
      }

    });*/

    /*btnDeflag.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // check for selected row first
        if (table.getSelectedRow() != -1) {
          // remove selected row from the model
          dedupeTableModel.deflag(table.getSelectedRows());
        }
      }

    });*/

    JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    contentPanel.add(scrollPane, "cell 0 5 9 10,grow");
    scrollPane.setViewportView(table);
    
    pack();
	getContentPane().requestFocusInWindow();
	setLocationRelativeTo(dedupeDialog);
  }

  public DedupeTableModel getDedupeTableModel() {
    return dedupeTableModel;
  }

  public void setDedupeTableModel(ArrayList<Record> dupes) {
    dedupeTableModel = new DedupeTableModel(dedupeDialog, dupes, selectedFields);
  }
  
	private class SetToKeepButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnSetToKeep) {
		        // check for selected row first
		        if (table.getSelectedRow() != -1) {
		          // remove selected row from the model
		          dedupeTableModel.keep(table.getSelectedRows());
		        }
			}
		}
	}
	
	private class SetToDeleteButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnSetToDelete) {
		        // check for selected row first
		        if (table.getSelectedRow() != -1) {
		          // remove selected row from the model
		          dedupeTableModel.remove(table.getSelectedRows());
		        }
			}
		}
	}
	
	private class DeflagButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnDeflag) {
		        // check for selected row first
		        if (table.getSelectedRow() != -1) {
		          // remove selected row from the model
		          dedupeTableModel.deflag(table.getSelectedRows());
		        }
			}
		}
	}
  
  //https://stackoverflow.com/questions/4240740/shortcut-key-for-jbutton-without-using-alt-key
  public static void clickOnKey(final AbstractButton button, String actionName, int key) {
		    button.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke( key, 0 ), actionName);

		    button.getActionMap().put(actionName, new AbstractAction(){
		        public void actionPerformed(ActionEvent e) {
		            button.doClick();
		        }
		    } );
		}

}
