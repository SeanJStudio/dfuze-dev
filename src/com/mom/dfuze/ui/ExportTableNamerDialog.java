package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.mom.dfuze.data.Theme;
import com.mom.dfuze.io.AccessReader;
import com.mom.dfuze.io.AccessWriter;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ExportTableNamerDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldTableName;
	private JButton btnAdd;
	private JLabel lblNewLabel;
	private File file;
	private String tableName;

	private boolean isComplete = false;

	public ExportTableNamerDialog(Component c, File file) {
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("Table namer");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 245));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 10 28 28 28, gap 0", "[128px:n:128px][128px:n:128px][14px:n:14px][128px:n:128px]", "[36px:n:36px][28px:n:28px]"));

		lblNewLabel = new JLabel("Enter the table name");
		lblNewLabel.setForeground(Theme.TITLE_COLOR);
		lblNewLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblNewLabel, "cell 0 0 4 1,aligny center");

		textFieldTableName = new JTextField();
		textFieldTableName.setForeground(Color.BLACK);
		textFieldTableName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(textFieldTableName, "cell 0 1 2 1,grow");
		textFieldTableName.setColumns(10);

		btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		contentPanel.add(btnAdd, "cell 3 1,grow");
		clickOnKey( btnAdd, "BtnAddHandler", KeyEvent.VK_ENTER );
		btnAdd.addActionListener(new BtnAddHandler());
		
		setFile(file);
		pack();
		setLocationRelativeTo(c);
	}
	
	public void setTableName(String tableName) throws Exception {
		if(tableName.trim().isEmpty())
			throw new Exception("Table name cannot be empty, please choose a name.");
		
		if(tableName.trim().contains("."))
			throw new Exception("Table name cannot include '.', please choose a new name.");
		
		if(tableName.length() > AccessWriter.MAX_TABLE_NAME_LENGTH)
			throw new Exception("Table name cannot exceed " + AccessWriter.MAX_TABLE_NAME_LENGTH + " characters, please choose a new name.");
		
		String[] tableNames = AccessReader.readTableNames(getFile());
		
		for(String table : tableNames)
			if(tableName.trim().equalsIgnoreCase(table)) 
				throw new Exception("Table name already exists in database.\nPlease choose a new name.");

		this.tableName  = tableName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}

	public void setIsComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public boolean getIsComplete() {
		return isComplete;
	}

	private class BtnAddHandler  implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnAdd) {
					setTableName(textFieldTableName.getText());
					isComplete = true;
					ExportTableNamerDialog.this.dispose();
				}
			} catch (Exception err) {
				UiController.handle(err);
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