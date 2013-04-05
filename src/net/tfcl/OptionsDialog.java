package net.tfcl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import net.tfcl.utils.GeneralException;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OptionsDialog extends JDialog
{
	private static final long serialVersionUID = -4901176932262816158L;
	
	private final JPanel contentPanel = new JPanel();
	
	private AppSettings settings;
	private JToggleButton tglForceUpdate;
	private JTextField installDirField;
	
	/**
	 * Create the dialog.
	 */
	public OptionsDialog(Window parent, AppSettings settings)
	{
		super(parent);
		this.settings = settings;
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		setTitle("Options");
		setBounds(100, 100, 450, 180);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{402, 0};
		gbl_contentPanel.rowHeights = new int[] {0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 1.0};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JPanel installDirPanel = new JPanel();
			installDirPanel.setAlignmentY(Component.TOP_ALIGNMENT);
			GridBagConstraints gbc_installDirPanel = new GridBagConstraints();
			gbc_installDirPanel.fill = GridBagConstraints.HORIZONTAL;
			gbc_installDirPanel.anchor = GridBagConstraints.NORTH;
			gbc_installDirPanel.insets = new Insets(0, 0, 5, 0);
			gbc_installDirPanel.gridx = 0;
			gbc_installDirPanel.gridy = 0;
			contentPanel.add(installDirPanel, gbc_installDirPanel);
			GridBagLayout gbl_installDirPanel = new GridBagLayout();
			gbl_installDirPanel.columnWidths = new int[]{64, 86, 0};
			gbl_installDirPanel.rowHeights = new int[]{20, 0};
			gbl_installDirPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
			gbl_installDirPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			installDirPanel.setLayout(gbl_installDirPanel);
			{
				JLabel lblInstallFolder = new JLabel("Install folder:");
				GridBagConstraints gbc_lblInstallFolder = new GridBagConstraints();
				gbc_lblInstallFolder.anchor = GridBagConstraints.WEST;
				gbc_lblInstallFolder.insets = new Insets(0, 0, 0, 5);
				gbc_lblInstallFolder.gridx = 0;
				gbc_lblInstallFolder.gridy = 0;
				installDirPanel.add(lblInstallFolder, gbc_lblInstallFolder);
			}
			{
				installDirField = new JTextField();
				GridBagConstraints gbc_installDirField = new GridBagConstraints();
				gbc_installDirField.fill = GridBagConstraints.HORIZONTAL;
				gbc_installDirField.anchor = GridBagConstraints.NORTH;
				gbc_installDirField.gridx = 1;
				gbc_installDirField.gridy = 0;
				installDirPanel.add(installDirField, gbc_installDirField);
				installDirField.setColumns(10);
			}
		}
		{
			tglForceUpdate = new JToggleButton("Force update?");
			GridBagConstraints gbc_tglForceUpdate = new GridBagConstraints();
			gbc_tglForceUpdate.fill = GridBagConstraints.HORIZONTAL;
			gbc_tglForceUpdate.insets = new Insets(5, 0, 5, 0);
			gbc_tglForceUpdate.gridx = 0;
			gbc_tglForceUpdate.gridy = 1;
			contentPanel.add(tglForceUpdate, gbc_tglForceUpdate);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						applySettings();
						saveSettings();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		loadSettings();
	}
	
	/**
	 * Load settings from the settings object into the GUI.
	 */
	public void loadSettings()
	{
		tglForceUpdate.setSelected(settings.getForceUpdate());
		installDirField.setText(settings.getInstallPath().getPath());
	}
	
	/**
	 * Write settings from the GUI to the settings object.
	 */
	public void applySettings()
	{
		settings.setForceUpdate(tglForceUpdate.isSelected());
		settings.setInstallPath(new File(installDirField.getText()));
	}
	
	private void saveSettings()
	{
		try
		{
			settings.saveSettings();
		} catch (GeneralException e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), 
					"Failed to save settings.", JOptionPane.ERROR_MESSAGE);
		}
	}
}
