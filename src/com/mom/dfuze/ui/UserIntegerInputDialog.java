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
import javax.swing.JRootPane;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;

import com.mom.dfuze.data.Theme;


@SuppressWarnings("serial")
public class UserIntegerInputDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JLabel lblDescription;
	private JButton btnNext;
	private String inputKeyword;
	private boolean isNextPressed = false;

	public UserIntegerInputDialog(Frame frame, String inputDescription) {
		setInputKeyword(inputKeyword);

		setResizable(false);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new LineBorder(new Color(192, 192, 192)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("insets 10 28 28 28, gap 0", "[128px:n:128px][128px:n:128px][14px:n:14px][128px:n:128px]", "[36px:n:36px][28px:n:28px]"));

		lblDescription = new JLabel(inputDescription);
		lblDescription.setForeground(Theme.TITLE_COLOR);
		lblDescription.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblDescription, "cell 0 0,aligny center");

		textField = new JTextField("");
		textField.setForeground(Color.BLACK);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentPanel.add(textField, "cell 0 1 2 1,grow");
		((AbstractDocument) textField.getDocument()).setDocumentFilter(new IntegerDocumentFilter(2));

		btnNext = new JButton("Next");
		btnNext.setBackground(Color.WHITE);
		btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnNext.addActionListener(new BtnNextHandler());
		clickOnKey( btnNext, "BtnNextHandler", KeyEvent.VK_ENTER );
		contentPanel.add(btnNext, "cell 3 1,grow");

		pack();
		setLocationRelativeTo(frame);
	}



	private void setInputKeyword(String inputKeyword) {
		this.inputKeyword = inputKeyword;
	}

	public String getInputKeyword() {
		return inputKeyword;
	}

	private void setIsNextPressed(boolean isNextPressed) {
		this.isNextPressed = isNextPressed;
	}

	public boolean getIsNextPressed() {
		return isNextPressed;
	}

	public int getUserInput() {

		String text = textField.getText();

		if(text.length() == 0)
			return 0;
		else
			return Integer.parseInt(textField.getText());
	}

	public JTextField getTextField() {
		return textField;
	}



	private class BtnNextHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnNext) {
					//if(textField.getText().isBlank())
					//throw new ApplicationException(String.format("ERROR: %s can not be blank.", getInputKeyword()));

					setIsNextPressed(true);
					UserIntegerInputDialog.this.dispose();
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