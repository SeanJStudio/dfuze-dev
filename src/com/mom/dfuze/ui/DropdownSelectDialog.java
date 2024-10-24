/**
 * Project: Dfuze
 * File: DatabaseSelectDialog.java
 * Date: May 27, 2020
 * Time: 6:57:45 AM
 */
package com.mom.dfuze.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import com.mom.dfuze.data.Theme;

import net.miginfocom.swing.MigLayout;
import java.awt.Color;

/**
 * Database select Dialog to allow users to select a table name from a dropdown list of databse table names
 * 
 * @author Sean Johnson
 *         Mail-o-Matic Services Ltd
 *         Date: 06/18/2023
 *
 */
@SuppressWarnings("serial")
public class DropdownSelectDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JLabel lblDescription;
	private JButton btnNext;
	private JComboBox<String> comboBoxValues;
	private final String COMBOBOX_PROTOTYPE_DISPLAY = "XXXXXXXXXXXXXXXXXXXXXX";

	private boolean isNextPressed = false;

	public DropdownSelectDialog(Frame frame, String[] values, String description) {
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		//setSize(388, 213);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 245));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(
				new MigLayout("", "[6px:n:6px][34px:n:34px][34px:n:34px][304px:n:34px][14px:n:14px][34px:n:34px][34px:n:34px][34px:n:34px][14px:n:14px][34px:n:34px][34px:n:34px][6px:n:6px]", "[6px:n:6px][28px:n:28px][28px:n:28px][14px:n:14px][28px:n:28px][28px:n:28px]"));

		JLabel lblDescription = new JLabel(description);
		lblDescription.setForeground(Theme.TITLE_COLOR);
		lblDescription.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		contentPanel.add(lblDescription, "cell 1 1 7 1,aligny center");

		comboBoxValues = new JComboBox<String>(getDropdownSelectComboBoxModel(values));
		comboBoxValues.setForeground(new Color(0, 0, 0));
		comboBoxValues.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		comboBoxValues.setPrototypeDisplayValue(COMBOBOX_PROTOTYPE_DISPLAY);
		contentPanel.add(comboBoxValues, "cell 1 2 10 1,grow");

		JSeparator separator = new JSeparator();
		contentPanel.add(separator, "cell 0 4 12 1,growx,aligny center");

		btnNext = new JButton("Next");
		btnNext.setBackground(new Color(255, 255, 255));
		btnNext.setForeground(new Color(0, 0, 0));
		btnNext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		contentPanel.add(btnNext, "cell 9 5 2 1,grow");
		btnNext.addActionListener(new NextButtonHandler());
		
		pack();
		setLocationRelativeTo(frame);
	}
	
	public void setValues(String[] values) {
		comboBoxValues.setModel(getDropdownSelectComboBoxModel(values));
	}

	public boolean isNextPressed() {
		return isNextPressed;
	}

	public String getSelectedValue() {
		return comboBoxValues.getSelectedItem().toString();
	}
	
	public int getSelectedValueIndex() {
		return comboBoxValues.getSelectedIndex();
	}

	private class NextButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnNext) {
					isNextPressed = true;
					DropdownSelectDialog.this.dispose();
				}
			} catch (Exception err) {
				UiController.handle(err);
			}
		}
	}

	private DefaultComboBoxModel<String> getDropdownSelectComboBoxModel(String[] array) {
		return new DefaultComboBoxModel<>(array);
	}

	public JComboBox<String> getComboBoxValues() {
		return comboBoxValues;
	}

	public void setComboBoxValues(JComboBox<String> comboBoxValues) {
		this.comboBoxValues = comboBoxValues;
	}

	public JLabel getLblDescription() {
		return lblDescription;
	}

	public void setLblDescription(JLabel lblDescription) {
		this.lblDescription = lblDescription;
	}
	
	public void setLblDescriptionText(String descriptionText) {
		lblDescription.setText(descriptionText);
	}

	public void setNextPressed(boolean isNextPressed) {
		this.isNextPressed = isNextPressed;
	}
	
	
}
