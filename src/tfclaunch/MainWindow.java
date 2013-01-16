package tfclaunch;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import tfclaunch.utils.AppUtils;
import tfclaunch.utils.GeneralException;

public class MainWindow
{
	private JFrame frmTerrafirmacraftLauncher;
	private JPanel loginPanel;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JCheckBox chckbxRememberPassword;
	private JButton btnLogin;
	private JButton btnOptions;
	private JScrollPane newsScrollPane;
	private JTextPane newsPane;
	private JLabel lblLoginStatus;
	
	private AppSettings settings;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					MainWindow window = new MainWindow();
					window.frmTerrafirmacraftLauncher.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public MainWindow()
	{
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frmTerrafirmacraftLauncher = new JFrame();
		frmTerrafirmacraftLauncher.setTitle("TerraFirmaCraft Launcher");
		frmTerrafirmacraftLauncher.setBounds(0, 0, 854, 480);
		frmTerrafirmacraftLauncher.setLocationRelativeTo(null);
		frmTerrafirmacraftLauncher
				.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		frmTerrafirmacraftLauncher.getContentPane().setLayout(gridBagLayout);
		
		newsScrollPane = new JScrollPane();
		GridBagConstraints gbc_newsScrollPane = new GridBagConstraints();
		gbc_newsScrollPane.gridwidth = 2;
		gbc_newsScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_newsScrollPane.fill = GridBagConstraints.BOTH;
		gbc_newsScrollPane.gridx = 0;
		gbc_newsScrollPane.gridy = 0;
		frmTerrafirmacraftLauncher.getContentPane().add(newsScrollPane,
				gbc_newsScrollPane);
		
		newsPane = new JTextPane();
		newsPane.setEditable(false);
		newsPane.setMargin(null);
		newsPane.setBackground(Color.WHITE);
		newsPane.setContentType("text/html");
		newsPane.setText("<html><head></head><body><p style=\"font-size:16pt; text-align:center;\">Loading news...</p></body></html>");
		newsPane.addHyperlinkListener(new HyperlinkListener()
		{
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					if (Desktop.isDesktopSupported())
					{
						Desktop desktop = Desktop.getDesktop();
						if (desktop.isSupported(Desktop.Action.BROWSE))
						{
							try
							{
								desktop.browse(e.getURL().toURI());
							} catch (IOException e1)
							{
								e1.printStackTrace();
							} catch (URISyntaxException e1)
							{
								e1.printStackTrace();
							}
						}
					}
				}
			}
		});
		newsScrollPane.setViewportView(newsPane);
		
		loginPanel = new JPanel();
		GridBagConstraints gbc_loginPanel = new GridBagConstraints();
		gbc_loginPanel.insets = new Insets(12, 12, 12, 12);
		gbc_loginPanel.anchor = GridBagConstraints.SOUTHEAST;
		gbc_loginPanel.gridx = 1;
		gbc_loginPanel.gridy = 1;
		frmTerrafirmacraftLauncher.getContentPane().add(loginPanel,
				gbc_loginPanel);
		GridBagLayout gbl_loginPanel = new GridBagLayout();
		gbl_loginPanel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_loginPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_loginPanel.columnWeights = new double[] { 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		gbl_loginPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		loginPanel.setLayout(gbl_loginPanel);
		
		lblLoginStatus = new JLabel("Status");
		lblLoginStatus.setVisible(false);
		GridBagConstraints gbc_lblLoginStatus = new GridBagConstraints();
		gbc_lblLoginStatus.gridwidth = 3;
		gbc_lblLoginStatus.insets = new Insets(0, 0, 2, 0);
		gbc_lblLoginStatus.gridx = 0;
		gbc_lblLoginStatus.gridy = 0;
		loginPanel.add(lblLoginStatus, gbc_lblLoginStatus);
		
		lblUsername = new JLabel("Username:");
		lblUsername.setDisplayedMnemonic('U');
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.EAST;
		gbc_lblUsername.insets = new Insets(3, 0, 3, 5);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 1;
		loginPanel.add(lblUsername, gbc_lblUsername);
		
		usernameField = new JTextField();
		lblUsername.setLabelFor(usernameField);
		usernameField.setPreferredSize(new Dimension(140, 23));
		GridBagConstraints gbc_usernameField = new GridBagConstraints();
		gbc_usernameField.insets = new Insets(3, 0, 3, 5);
		gbc_usernameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_usernameField.gridx = 1;
		gbc_usernameField.gridy = 1;
		loginPanel.add(usernameField, gbc_usernameField);
		usernameField.setColumns(10);
		
		lblPassword = new JLabel("Password:");
		lblPassword.setDisplayedMnemonic('P');
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.insets = new Insets(3, 0, 3, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 2;
		loginPanel.add(lblPassword, gbc_lblPassword);
		
		passwordField = new JPasswordField();
		lblPassword.setLabelFor(passwordField);
		passwordField.setPreferredSize(new Dimension(140, 23));
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(3, 0, 3, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 2;
		loginPanel.add(passwordField, gbc_passwordField);
		
		chckbxRememberPassword = new JCheckBox("Remember password?");
		chckbxRememberPassword.setMnemonic('R');
		GridBagConstraints gbc_chckbxRememberPassword = new GridBagConstraints();
		gbc_chckbxRememberPassword.insets = new Insets(0, 0, 0, 6);
		gbc_chckbxRememberPassword.gridx = 1;
		gbc_chckbxRememberPassword.gridy = 3;
		loginPanel.add(chckbxRememberPassword, gbc_chckbxRememberPassword);
		
		btnOptions = new JButton("Options");
		btnOptions.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onOptionsClicked();
			}
		});
		btnOptions.setMnemonic('O');
		GridBagConstraints gbc_btnOptions = new GridBagConstraints();
		gbc_btnOptions.insets = new Insets(3, 5, 3, 0);
		gbc_btnOptions.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOptions.gridx = 2;
		gbc_btnOptions.gridy = 1;
		loginPanel.add(btnOptions, gbc_btnOptions);
		
		btnLogin = new JButton("Login");
		btnLogin.setMnemonic('L');
		btnLogin.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onLoginClicked();
			}
		});
		GridBagConstraints gbc_btnLogin = new GridBagConstraints();
		gbc_btnLogin.insets = new Insets(3, 5, 3, 0);
		gbc_btnLogin.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLogin.gridx = 2;
		gbc_btnLogin.gridy = 2;
		loginPanel.add(btnLogin, gbc_btnLogin);
		
		frmTerrafirmacraftLauncher.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{usernameField, passwordField, chckbxRememberPassword, btnLogin}));
		
		settings = new AppSettings(new File(AppUtils.getAppDataDir(), "tfclauncher.cfg"));
		try
		{
			settings.loadSettings();
		} catch (GeneralException e1)
		{
			JOptionPane.showMessageDialog(frmTerrafirmacraftLauncher, e1.getMessage(), 
					"Failed to load settings.", JOptionPane.ERROR_MESSAGE);
		}
		
		UserInfo uinfo = new UserInfo("", "");
		
		uinfo.readFile(new File(AppUtils.getAppDataDir(), "lastlogin"));
		
		usernameField.setText(uinfo.getUsername());
		passwordField.setText(uinfo.getPassword());
		
		loadNewsPage();
	}
	
	/**
	 * Sets the status label above the login box to the given color with the given text.
	 * If <code>status</code> is empty, the label is hidden, otherwise, it is shown.
	 * @param status The status message to display.
	 * @param color The color of the label.
	 */
	private void setStatusLabel(String status, Color color)
	{
		lblLoginStatus.setVisible(!status.isEmpty());
		lblLoginStatus.setText(status);
		lblLoginStatus.setForeground(color);
	}
	
	/**
	 * Sets the status label above the login box with the given status message shown in black.
	 * If <code>status</code> is empty, the label is hidden, otherwise, it is shown.
	 * @param status The status message to display.
	 */
	private void setStatusMessage(String status)
	{
		setStatusLabel(status, Color.BLACK);
	}
	
	/**
	 * Sets the status label above the login box with the given error message shown in red.
	 * If <code>status</code> is empty, the label is hidden, otherwise, it is shown.
	 * @param status The error message to display.
	 */
	private void setStatusError(String status)
	{
		setStatusLabel(status, Color.RED);
	}
	
	/**
	 * Hides the status label blank, hiding it.
	 */
	private void setNoStatus()
	{
		lblLoginStatus.setText("");
		lblLoginStatus.setVisible(false);
	}
	
	/**
	 * Enables or disables the buttons and text-boxes in the login panel.
	 * @param enable Whether or not to enable the controls.
	 */
	private void enableLoginPanel(boolean enable)
	{
		Component[] components = loginPanel.getComponents();
		for (int i = 0; i < components.length; i++)
			if (components[i] != lblLoginStatus)
				components[i].setEnabled(enable);
	}
	
	private void loadNewsPage()
	{
		try
		{
			// Yes, let's create a document builder factory, an object used to create a document builder.
			// What does this document builder do? It creates Documents, of course! 
			// An object to create an object to create an object. Gotta love dat Java!
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse("http://terrafirmacraft.com/feed/");
			
			String newsHTML = NewsPageGenerator.generateNewsPage(doc);
			newsPane.setText(newsHTML);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					newsScrollPane.getVerticalScrollBar().setValue(0);
				}
			});
		} catch (IOException e)
		{
			e.printStackTrace();
			newsPane.setText("<html><head></head><body>" +
					"<p style=\"color:red; text-align:center;\">Failed to load news.</p>" +
					"<p style=\"color:red; text-align:center;\">IOException: " + e.getMessage() + "</p>" +
					"</body></html>");
			return;
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			newsPane.setText("<html><head></head><body>" +
					"<p style=\"color:red; text-align:center;\">Failed to load news.</p>" +
					"<p style=\"color:red; text-align:center;\">ParserConfigurationException: " + e.getMessage() + "</p>" +
					"</body></html>");
			return;
		} catch (SAXException e)
		{
			e.printStackTrace();
			newsPane.setText("<html><head></head><body>" +
					"<p style=\"color:red; text-align:center;\">Failed to load news.</p>" +
					"<p style=\"color:red; text-align:center;\">SAXException: " + e.getMessage() + "</p>" +
					"</body></html>");
			return;
		}
	}
	
	private void onLoginClicked()
	{
		UserInfo uinfo = new UserInfo(usernameField.getText(), new String(passwordField.getPassword()));
		try
		{
			uinfo.writeFile(new File(AppUtils.getAppDataDir(), "lastlogin"), chckbxRememberPassword.isSelected());
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		LoginWorker login = new LoginWorker(uinfo)
		{
			@Override
			protected void done()
			{
				setNoStatus();
				
				LoginResponse response = null;
				try
				{
					response = get();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				} catch (ExecutionException e)
				{
					e.printStackTrace();
					if (e.getCause() instanceof GeneralException)
					{
						JOptionPane.showMessageDialog(frmTerrafirmacraftLauncher, e.getMessage(), 
								"Login Failed", JOptionPane.ERROR_MESSAGE);
						enableLoginPanel(true);
					}
				}
				
				if (response == null)
				{
					setStatusError("Login failed: An unexpected error occurred.");
					enableLoginPanel(true);
					return;
				}
				else if (response.succeeded())
				{
					doGameUpdate(response);
				}
				else
				{
//					JOptionPane.showMessageDialog(frmTerrafirmacraftLauncher, response.getErrorMsg(), 
//							"Login Failed", JOptionPane.ERROR_MESSAGE);
					setStatusError("Login failed: " + response.getErrorMsg());
					enableLoginPanel(true);
				}
			}
		};
		
		// Show the status label with the text "logging in."
		setStatusMessage("Logging in...");
		enableLoginPanel(false);
		login.execute();
	}
	
	private void doGameUpdate(final LoginResponse sessionInfo)
	{
		if (!sessionInfo.succeeded())
			throw new IllegalArgumentException("Game update requires successful login response.");
		
		final ProgressMonitor progMon = new ProgressMonitor(frmTerrafirmacraftLauncher, "Downloading game...", "Please wait...", 0, 100);
		final GameUpdater updater = new GameUpdater(settings.getInstallPath(), settings.getForceUpdate())
		{
			@Override
			protected void done()
			{
				progMon.close();
				
				// Catch exceptions
				try
				{
					get();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
					return;
				} catch (ExecutionException e)
				{
					e.printStackTrace();
					
					if (e.getCause() instanceof GeneralException)
					{
						JOptionPane.showMessageDialog(frmTerrafirmacraftLauncher, e.getCause().getMessage(),
								"Install Failed", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog(frmTerrafirmacraftLauncher, 
								"Failed to install TerraFirmaCraft. An unknown error occurred:\n" + e.getMessage(), 
								"Install Failed", JOptionPane.ERROR_MESSAGE);
					}
					return;
				}
				
				launchGame(installDir.getPath(), sessionInfo);
			}
		};
		
		updater.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (progMon.isCanceled())
					updater.cancel(false);
				
				if (!updater.isDone())
				{
					int prog = updater.getProgress();
					if (prog < 0)
						prog = 0;
					if (prog > 100)
						prog = 100;
					progMon.setProgress(prog);
					progMon.setNote(updater.getStatus());
				}
			}
		});
		
		updater.execute();
	}
	
	private void launchGame(String installDir, LoginResponse sessionInfo)
	{
		// Start the game.
		GameLauncher launcher = new GameLauncher(installDir, sessionInfo);
		boolean launched = true;
		try
		{
			launcher.launch();
		} catch (GeneralException e)
		{
			e.printStackTrace();
			launched = false;
			JOptionPane.showMessageDialog(frmTerrafirmacraftLauncher, e.getMessage(),
					"Launch Failed", JOptionPane.ERROR_MESSAGE);
		}
		if (launched)
			frmTerrafirmacraftLauncher.setVisible(false);
	}
	
	private void onOptionsClicked()
	{
		OptionsDialog opsDlg = new OptionsDialog(frmTerrafirmacraftLauncher, settings);
		opsDlg.setLocationRelativeTo(frmTerrafirmacraftLauncher);
		opsDlg.setVisible(true);
	}
}
