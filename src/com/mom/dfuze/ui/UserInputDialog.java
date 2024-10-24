package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.mom.dfuze.data.Theme;
import javax.swing.JCheckBox;


@SuppressWarnings("serial")
public class UserInputDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JLabel lblDescription;
	private JButton btnNext;
	private String inputDescription;
	private boolean isNextPressed = false;
	private boolean isOptionAdded = false;
	private JCheckBox chckbxOption;

	public UserInputDialog(Frame frame, String inputDescription) {
		setInputDescription(inputDescription);
		
		setResizable(false);
		
		//setUndecorated(true);
		//getRootPane().setWindowDecorationStyle(JRootPane.NONE);

		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 10 28 28 28, gap 0", "[128px:n:128px][128px:n:128px][14px:n:14px][128px:n:128px]", "[36px:n:36px][28px:n:28px][36px:n:36px]"));
		
		lblDescription = new JLabel(inputDescription);
		lblDescription.setForeground(Theme.TITLE_COLOR);
		lblDescription.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblDescription, "cell 0 0,aligny center");
		
		textField = new JTextField("");
		textField.setForeground(Color.BLACK);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(textField, "cell 0 1 2 1,grow");
		textField.setColumns(10);
		
		btnNext = new JButton("Next");
		btnNext.setBackground(Color.WHITE);
		btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnNext.addActionListener(new BtnNextHandler());
		clickOnKey( btnNext, "BtnNextHandler", KeyEvent.VK_ENTER );
		contentPanel.add(btnNext, "cell 3 1,grow");
		
		pack();
		setLocationRelativeTo(frame);
	}
	
	private void setInputDescription(String inputDescription) {
		this.inputDescription = inputDescription;
	}
	
	public String getInputDescription() {
		return inputDescription;
	}
	
	private void setIsNextPressed(boolean isNextPressed) {
		this.isNextPressed = isNextPressed;
	}
	
	public boolean getIsNextPressed() {
		return isNextPressed;
	}
	
	public String getUserInput() {
		return textField.getText();
	}
	
	public JTextField getTextField() {
		return textField;
	}

	public void setChckbxOption(String option) {
		
		if(isOptionAdded) return;
		
		chckbxOption = new JCheckBox(option);
		contentPanel.add(chckbxOption, "cell 0 2,aligny bottom");
		isOptionAdded = true;
	}
	
	public JCheckBox getChckbxOption() {
		return chckbxOption;
	}

	private class BtnNextHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnNext) {
					//if(textField.getText().isBlank())
						//throw new ApplicationException(String.format("ERROR: %s can not be blank.", getInputDescription()));
						
					setIsNextPressed(true);
					UserInputDialog.this.dispose();
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