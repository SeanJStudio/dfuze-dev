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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;

import net.miginfocom.swing.MigLayout;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import com.mom.dfuze.ApplicationException;
import com.mom.dfuze.data.Theme;

import javax.swing.JSeparator;


@SuppressWarnings("serial")
public class UserMultiRadioDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JLabel lblDescription;
	private JButton btnNext;
	
	private int radioButtonNum = 0;
	
	private static final int RADIO_MIN = 2;
	private static final int RADIO_MAX = 5;
	
	private ButtonGroup btnGroup = new ButtonGroup();
	private JRadioButton radio1;
	private JRadioButton radio2;
	private JRadioButton radio3;
	private JRadioButton radio4;
	private JRadioButton radio5;
	
	private boolean isNextPressed = false;
	private int selectedRadioButton = 1;
	private JSeparator separator;

	public UserMultiRadioDialog(Frame frame, int radioButtonNum, String description) throws ApplicationException {

		setResizable(false);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		setRadioButtonNum(radioButtonNum);
		setContentPanel();
		setRadioButtons();
		
		setLblDescription(description);
		
		separator = new JSeparator();
		String seperatorLayout = String.format("cell 0 %d 4 1,growx,aligny center", radioButtonNum+2);
		contentPanel.add(separator, seperatorLayout);
		
		setNextButton();

		pack();
		setLocationRelativeTo(frame);
	}
	
	private void setLblDescription(String description) {
		lblDescription = new JLabel(description);
		lblDescription.setForeground(Theme.TITLE_COLOR);
		lblDescription.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblDescription, "cell 0 0 4 1,aligny center");
	}
	
	private void setRadioButtonNum(int radioButtonNum) throws ApplicationException {
		if(radioButtonNum >= RADIO_MIN && radioButtonNum <= RADIO_MAX)
			this.radioButtonNum = radioButtonNum;
		else
			throw new ApplicationException(String.format("radioButtonNum must be between %d and %d when creating a UserMultiRadioDialog",RADIO_MIN, RADIO_MAX));
	}
	
	private void setRadioButtons() {
		radio1 = new JRadioButton("");
		radio2 = new JRadioButton("");
		radio3 = new JRadioButton("");
		radio4 = new JRadioButton("");
		radio5 = new JRadioButton("");
		
		radio1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		radio2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		radio3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		radio4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		radio5.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		
		radio1.setForeground(Color.BLACK);
		radio2.setForeground(Color.BLACK);
		radio3.setForeground(Color.BLACK);
		radio4.setForeground(Color.BLACK);
		radio5.setForeground(Color.BLACK);
		
		btnGroup = new ButtonGroup();
		btnGroup.add(radio1);
		btnGroup.add(radio2);
		btnGroup.add(radio3);
		btnGroup.add(radio4);
		btnGroup.add(radio5);
		
		switch(radioButtonNum) {
			case 2:
				contentPanel.add(radio1, "cell 0 1");
				contentPanel.add(radio2, "cell 0 2");
				break;
			case 3:
				contentPanel.add(radio1, "cell 0 1");
				contentPanel.add(radio2, "cell 0 2");
				contentPanel.add(radio3, "cell 0 3");
				break;
			case 4:
				contentPanel.add(radio1, "cell 0 1");
				contentPanel.add(radio2, "cell 0 2");
				contentPanel.add(radio3, "cell 0 3");
				contentPanel.add(radio4, "cell 0 4");
				break;
			case 5:
				contentPanel.add(radio1, "cell 0 1");
				contentPanel.add(radio2, "cell 0 2");
				contentPanel.add(radio3, "cell 0 3");
				contentPanel.add(radio4, "cell 0 4");
				contentPanel.add(radio5, "cell 0 5");
				break;
			default:
				break;
		}
	}
	
	private void setNextButton() {
		btnNext = new JButton("Next");
		btnNext.setBackground(Color.WHITE);
		btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnNext.addActionListener(new BtnNextHandler());
		clickOnKey( btnNext, "BtnNextHandler", KeyEvent.VK_ENTER );
		
		String layout = String.format("cell 3 %d,grow", radioButtonNum+3);
		contentPanel.add(btnNext, layout);
	}
	
	private String getDialogRowLayout() {
		StringBuilder sb = new StringBuilder();
		sb.append("[36px:n:36px][28px:n:28px][28px:n:28px][28px:n:28px]");
		
		for(int i = 0; i < radioButtonNum; ++i)
			sb.append("[28px:n:28px]");

		return sb.toString();
	}
	
	private void setContentPanel() {
		contentPanel.setBorder(new LineBorder(new Color(192, 192, 192)));
		String rowLayout = getDialogRowLayout();
		contentPanel.setLayout(
				new MigLayout("insets 10 28 14 28, gap 0", "[128px:n:128px][128px:n:128px][14px:n:14px][128px:n:128px]", rowLayout));
	}
	
	public void setRadioButton1(String radioButtonText) {
		radio1.setText(radioButtonText);
		radio1.setSelected(true);
		contentPanel.add(radio1, "cell 0 1");
	}
	
	public void setRadioButton2(String radioButtonText) {
		radio2.setText(radioButtonText);
	}
	
	public void setRadioButton3(String radioButtonText) {
		radio3.setText(radioButtonText);
	}
	
	public void setRadioButton4(String radioButtonText) {
		radio4.setText(radioButtonText);
	}
	
	public void setRadioButton5(String radioButtonText) {
		radio5.setText(radioButtonText);
	}
	
	
	private void setIsNextPressed(boolean isNextPressed) {
		this.isNextPressed = isNextPressed;
	}
	
	public boolean getIsNextPressed() {
		return isNextPressed;
	}
	
	public int getSelectedRadioButton() {
		return selectedRadioButton;
	}
	
	

	public JRadioButton getRadio1() {
		return radio1;
	}

	public JRadioButton getRadio2() {
		return radio2;
	}

	public JRadioButton getRadio3() {
		return radio3;
	}

	public JRadioButton getRadio4() {
		return radio4;
	}

	public JRadioButton getRadio5() {
		return radio5;
	}



	private class BtnNextHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnNext) {		
					setIsNextPressed(true);
					
					if(radio1.isSelected())
						selectedRadioButton = 1;
					else if(radio2.isSelected())
						selectedRadioButton = 2;
					else if(radio3.isSelected())
						selectedRadioButton = 3;
					else if(radio4.isSelected())
						selectedRadioButton = 4;
					else if(radio5.isSelected())
						selectedRadioButton = 5;
					
					UserMultiRadioDialog.this.dispose();
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