package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import files.FileOperations;
import files.ImageOperations;
import files.VMPaths;
import files.SystemOperations;

/**
 * Dialog zur Wahl der Admin/Interview-Oberfläche.
 * 
 * 
 */
public class StartChooser extends JDialog implements ActionListener,
		WindowListener
{

	public static final int			FREE_DRAWING						= 0;

	public static final int			LOAD_CONFIGURATION_FOR_EDIT	= 1;

	public static final int			PERFORM_INTERVIEW					= 2;

	public static final int			LOAD_PROJECT						= 3;

	public static final int			LOAD_CONFIGURATION				= 4;

	public static final int			UPDATE								= 5;

	public static final int			CREATE_QUESTIONAIRE				= 6;

	public static final int			EDIT_TEMPLATE						= 7;

	/** standard spacing height in between buttons */
	private static final int		HEIGHT								= 5;

	/** standard spacing width in between buttons */
	private static final int		WIDTH									= 15;

	private static final long		serialVersionUID					= 1L;

	private final int					buttonWidth							= 2;

	private final int					buttonWidthDouble					= 4;

	private boolean					closedWithoutDecision			= true;

	private JComboBox					languagesCombo;

	private int							selectedMode;

	/**
	 * HashMap, containing all buttons / items, which need to be refreshed, when
	 * the language is changed as keys, the corresponding messages as String
	 */
	private Map<Object, String>	itemsToRefresh						= new HashMap<Object, String>();

	/**
	 * HashMap to store replacements, which aren't dependent on the different
	 * languages, but need to be changed, when languages are switched (i.e.
	 * $DATE$)
	 */
	private Map<String, String>	replacements						= new HashMap<String, String>();

	public StartChooser()
	{
		super((JFrame) null, Messages.getString("VennMaker.VennMaker") //$NON-NLS-1$
				+ VennMaker.VERSION, true);

		setIconImage(new ImageIcon(
				FileOperations.getAbsolutePath("icons/intern/icon.png")).getImage()); //$NON-NLS-1$

		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		this.setResizable(false);

		GridBagConstraints gbc;

		int zeile = 0;

		languagesCombo = new JComboBox(VennMaker.LANGUAGES);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 4;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(WIDTH, HEIGHT, WIDTH, HEIGHT);
		layout.setConstraints(languagesCombo, gbc);
		add(languagesCombo);
		languagesCombo.addActionListener(new ActionListener()
		{

			/* when languages are switched, change the buttons as well */
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String selectedLanguage = (String) languagesCombo.getSelectedItem();

				Locale.setDefault(VennMaker.MESSAGELOCALES.get(selectedLanguage));
				Messages.refresh();

				/* loop through all objects, which contain languagedependent data */
				for (Object c : itemsToRefresh.keySet())
				{
					String text = Messages.getString(itemsToRefresh.get(c));
					/*
					 * replace constants like $DATE$ to the language independent
					 * constants
					 */
					for (String foo : replacements.keySet())
					{
						text = text.replaceAll(foo, replacements.get(foo));
					}

					if (c instanceof JButton)
						((JButton) c).setText(text);

					if (c instanceof JLabel)
						((JLabel) c).setText(text);

				}
			}

		});

		zeile++;

		JLabel label;
		label = new JLabel();

		BufferedImage img = ImageOperations.loadImage(new File(FileOperations
				.getAbsolutePath("icons/intern/VennMakerLogo-klein.png"))); //$NON-NLS-1$
		ImageIcon imgIcon = new ImageIcon(img);
		label.setIcon(imgIcon);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = zeile;
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
		gbc.insets = new Insets(WIDTH * 2, HEIGHT * 2, 0, WIDTH * 2);
		layout.setConstraints(label, gbc);
		add(label);

		zeile++;

		boolean test = false;
		String testVersion = "";
		long testEndDate = 0;

		if (TestVersion.getEndDate() > 0)
		{
			test = TestVersion.testVersion();
			testVersion = TestVersion.getVersion();
			testEndDate = TestVersion.getEndDate();

			SimpleDateFormat sdfToDate = new SimpleDateFormat("d MMM yyyy");

			replacements.put("\\$VERSION\\$", testVersion);
			replacements.put("\\$DATE\\$", sdfToDate.format(testEndDate));

			String ausgabe = "";

			JLabel info = new JLabel(ausgabe);

			if (test == true)
			{
				ausgabe = Messages.getString("StartChooser.Expired");

				/*
				 * to accept different orders in different languages, $DATE$ and
				 * $VERSION$ need to be replaced by the actual numbers
				 */
				ausgabe = ausgabe.replaceFirst("\\$DATE\\$",
						sdfToDate.format(testEndDate));
				ausgabe = ausgabe.replaceFirst("\\$VERSION\\$", testVersion);

				itemsToRefresh.put(info, "StartChooser.Expired");
			}
			else
			{
				ausgabe = Messages.getString("StartChooser.ExpireDate");

				ausgabe = ausgabe.replaceFirst("\\$DATE\\$",
						sdfToDate.format(testEndDate));
				ausgabe = ausgabe.replaceFirst("\\$VERSION\\$", testVersion);

				itemsToRefresh.put(info, "StartChooser.ExpireDate");
			}

			info.setText(ausgabe);

			add(info, getStandardConstraints(1, zeile, 4, 1));
		}

		zeile++;

		JButton freeNetworkButton = new JButton(
				Messages.getString("StartChooser.ModeFreeNetwork"), new ImageIcon("icons/intern/startchooser_freedrawing_icon_small.png")); //$NON-NLS-1$
		itemsToRefresh.put(freeNetworkButton, "StartChooser.ModeFreeNetwork");
		freeNetworkButton.setActionCommand("admin"); //$NON-NLS-1$
		freeNetworkButton.addActionListener(this);
		freeNetworkButton.setMargin(new Insets(HEIGHT, WIDTH, HEIGHT, WIDTH));

		if (test == true)
			freeNetworkButton.setEnabled(false);

		add(freeNetworkButton, getStandardConstraints(1, zeile, buttonWidth, 1));

		zeile++;

		JButton loadQuestionaireButton = new JButton(
				Messages.getString("StartChooser.ModeLoadQuestionaire"),
				new ImageIcon(
						"icons/intern/startchooser_load_perform_icon_small.png")); //$NON-NLS-1$
		itemsToRefresh.put(loadQuestionaireButton,
				"StartChooser.ModeLoadQuestionaire");
		loadQuestionaireButton.setActionCommand("interview"); //$NON-NLS-1$
		loadQuestionaireButton.addActionListener(this);
		loadQuestionaireButton
				.setMargin(new Insets(HEIGHT, WIDTH, HEIGHT, WIDTH));

		if (test == true)
			loadQuestionaireButton.setEnabled(false);
		add(loadQuestionaireButton,
				getStandardConstraints(1, zeile, buttonWidth, 1));

		zeile++;

		JButton openTemplateButton = new JButton(
				Messages.getString("StartChooser.ModeEditTemplate"), new ImageIcon("icons/intern/startchooser_edit_template_small.png")); //$NON-NLS-1$
		itemsToRefresh.put(openTemplateButton, "StartChooser.ModeEditTemplate");
		openTemplateButton.setActionCommand("editTemplate"); //$NON-NLS-1$
		openTemplateButton.addActionListener(this);
		openTemplateButton.setMargin(new Insets(HEIGHT, WIDTH, HEIGHT, WIDTH));

		if (test == true)
			openTemplateButton.setEnabled(false);
		add(openTemplateButton, getStandardConstraints(1, zeile, buttonWidth, 1));

		zeile++;

		JButton update = new JButton(
				Messages.getString("StartChooser.Documentation"), new ImageIcon("icons/intern/startchooser_update_icon_small.png")); //$NON-NLS-1$
		itemsToRefresh.put(update, "StartChooser.Documentation");
		update.setActionCommand("update"); //$NON-NLS-1$
		update.addActionListener(this);
		update.setMargin(new Insets(HEIGHT, WIDTH, HEIGHT, WIDTH));

		add(update, getStandardConstraints(1, zeile, buttonWidthDouble, 1));

		zeile++;

		// -------------------------------------------- Information bei alter Java
		// Version ------------------------------------------------

		String s = System.getProperty("java.version");
		String release = s.substring(2, 3);
		System.out.println("Java version: "+s);
		double ver = 0; 
		boolean wrongVersion = true;
		
		try {
			ver = Double.parseDouble(s);
			if (ver >= 1.8) {
				wrongVersion = false;
			}
		} catch(NumberFormatException e) {
			release = s.substring(0, 2);

			try {
				ver = Double.parseDouble(release);
				if (ver >= 10) {
					wrongVersion = false;
				}
			} catch(NumberFormatException f) {
			}

		}

		if (wrongVersion == true)
		{
			JTextArea taJavaVersion = new JTextArea(
					"You are using outdated Java Version "
							+ release
							+ ".\n"
							+ "This can probably cause problems loading VennMaker projects.\n"
							+ "Please use the following link to update Java:");

			taJavaVersion.setEditable(false);
			taJavaVersion.setLineWrap(true);
			taJavaVersion.setWrapStyleWord(true);
			taJavaVersion.setBackground(this.getBackground());
			taJavaVersion.setForeground(Color.red);
			taJavaVersion.setPreferredSize(new Dimension(buttonWidthDouble, 60));

			GridBagConstraints g = getStandardConstraints(1, zeile,
					buttonWidthDouble, 1);
			g.insets.top += 10;
			g.insets.bottom = 0;
			add(taJavaVersion, g);

			final JLabel linkLabel = new JLabel("http://www.java.com/download");
			linkLabel.setForeground(Color.blue);
			linkLabel.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					try
					{
						URI link = new URI("http://www.java.com/download");
						Desktop.getDesktop().browse(link);
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}

				@Override
				public void mouseEntered(MouseEvent e)
				{
					linkLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					linkLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});

			zeile++;
			linkLabel.setOpaque(true);
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = zeile;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(HEIGHT, 23, HEIGHT, 10);
			add(linkLabel, gbc);
		}
		// --------------------------------------------------------------------------------------------------------------------------------

		zeile = 3;

		JButton openVmpButton = new JButton(
				Messages.getString("StartChooser.ModeLoadInterview"), new ImageIcon("icons/intern/startchooser_open_vmp_icon_small.png")); //$NON-NLS-1$
		itemsToRefresh.put(openVmpButton, "StartChooser.ModeLoadInterview");
		openVmpButton.setActionCommand("openVmp"); //$NON-NLS-1$
		openVmpButton.addActionListener(this);
		openVmpButton.setMargin(new Insets(HEIGHT, WIDTH, HEIGHT, WIDTH));

		if (test == true)
			openVmpButton.setEnabled(false);

		add(openVmpButton, getStandardConstraints(4, zeile, buttonWidth, 1));

		zeile++;

		JButton createQuestionaireButton = new JButton(
				Messages.getString("StartChooser.ModeCreateQuestionaire"), new ImageIcon("icons/intern/startchooser_create_questionnaire_small.png")); //$NON-NLS-1$
		itemsToRefresh.put(createQuestionaireButton,
				"StartChooser.ModeCreateQuestionaire");
		createQuestionaireButton.setActionCommand("createQuestionaire"); //$NON-NLS-1$
		createQuestionaireButton.addActionListener(this);
		createQuestionaireButton.setMargin(new Insets(HEIGHT, WIDTH, HEIGHT,
				WIDTH));

		if (test == true)
			createQuestionaireButton.setEnabled(false);
		add(createQuestionaireButton,
				getStandardConstraints(4, zeile, buttonWidth, 1));

		zeile++;

		JButton editQuestionaireButton = new JButton(
				Messages.getString("StartChooser.ModeEditQuestionaire"), new ImageIcon("icons/intern/startchooser_load_edit_small.png")); //$NON-NLS-1$
		itemsToRefresh.put(editQuestionaireButton,
				"StartChooser.ModeEditQuestionaire");
		editQuestionaireButton.setActionCommand("configure"); //$NON-NLS-1$
		editQuestionaireButton.addActionListener(this);
		editQuestionaireButton
				.setMargin(new Insets(HEIGHT, WIDTH, HEIGHT, WIDTH));

		if (test == true)
			editQuestionaireButton.setEnabled(false);
		add(editQuestionaireButton,
				getStandardConstraints(4, zeile, buttonWidth, 1));

		getRootPane().setDefaultButton(freeNetworkButton);
		pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - this.getHeight()) / 2;
		int left = (screenSize.width - this.getWidth()) / 2;
		setLocation(left, top);
		this.setResizable(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.addWindowListener(this);
	}

	/**
	 * creates the constraints for the buttons, depending on their position
	 * 
	 * @param x
	 *           x-coordinate to put the button (or other component, with same
	 *           insets)
	 * @param y
	 *           y-coordinate to put the button (or other component, with same
	 *           insets)
	 * @param width
	 *           how wide
	 * @param height
	 *           how high
	 * @return the gridbagconstraints containing all of the data above
	 */
	private GridBagConstraints getStandardConstraints(int x, int y, int width,
			int height)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = 1;
		if (x < 2)
			gbc.insets = new Insets(HEIGHT, WIDTH, HEIGHT, 10); // button left
		else
			gbc.insets = new Insets(HEIGHT, 10, HEIGHT, 10); // button right

		return gbc;
	}

	/**
	 * Liefert <code>true</code>, wenn der Dialog ohne Auswahl einer Option
	 * "Admin" oder "Interview" geschlossen wurde (durch Schließen des Fensters
	 * beispielsweise).
	 * 
	 * @return <code>false</code> sonst.
	 */
	public boolean isClosedWithoutDecision()
	{
		return closedWithoutDecision;
	}

	private boolean	configuring	= false;

	/**
	 * Returns <code>true</code> if VennMaker is started in "Configure Interview"
	 * mode.
	 * 
	 * @return <code>false</code> otherwise
	 */
	public boolean isConfiguring()
	{
		return configuring;
	}

	public void actionPerformed(ActionEvent event)
	{
		if ("update".equals(event.getActionCommand())) { //$NON-NLS-1$
			String link;
			try
			{
				if (languagesCombo.getSelectedItem().equals("Deutsch"))
					link = "http://www.vennmaker.com/download/?q=update";
				else
					link = "http://www.vennmaker.com/en/download/?q=update";
				SystemOperations.openUrl(link);

			} catch (URISyntaxException exn)
			{
				exn.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			selectedMode = UPDATE;
		}

		if ("admin".equals(event.getActionCommand())) { //$NON-NLS-1$
			closedWithoutDecision = false;
			this.dispose();
		}
		else if ("configure".equals(event.getActionCommand()))
		{ //$NON-NLS-1$
			closedWithoutDecision = false;
			configuring = true;

			selectedMode = LOAD_CONFIGURATION_FOR_EDIT;

			this.dispose();
		}
		else if ("createQuestionaire".equals(event.getActionCommand()))
		{
			closedWithoutDecision = false;

			selectedMode = CREATE_QUESTIONAIRE;

			this.dispose();
		}
		else if ("editTemplate".equals(event.getActionCommand()))
		{
			closedWithoutDecision = false;

			selectedMode = EDIT_TEMPLATE;

			this.dispose();
		}
		else if ("interview".equals(event.getActionCommand()))
		{
			closedWithoutDecision = false;

			selectedMode = PERFORM_INTERVIEW;

			this.dispose();
		}
		else if ("openVmp".equals(event.getActionCommand()))
		{
			closedWithoutDecision = false;

			selectedMode = LOAD_PROJECT;

			this.dispose();
		}
	}

	public int getStartMode()
	{
		return selectedMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e)
	{
		File tempDir = new File(VMPaths.VENNMAKER_TEMPDIR);

		if (tempDir.exists())
			FileOperations.deleteFolder(tempDir);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent
	 * )
	 */
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent
	 * )
	 */
	@Override
	public void windowDeiconified(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}
}
