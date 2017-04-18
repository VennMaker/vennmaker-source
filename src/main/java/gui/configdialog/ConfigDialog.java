/**
 * 
 */
package gui.configdialog;

import files.FileOperations;
import files.IO;
import files.VMPaths;
import gui.Messages;
import gui.VennMaker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import data.IconTreeNode;
import data.Netzwerk;

/**
 * Beinhaltet den Baum der Config-Dialoge und verwaltet sie und ihre
 * Darstellung.
 * 
 * 
 */
@SuppressWarnings("rawtypes")
public class ConfigDialog extends JFrame implements ActionListener
{
	private static final long		serialVersionUID	= 1L;

	/**
	 * Referenz auf den Temp-Cache
	 */
	private ConfigDialogTempCache	tempCache;

	private ConfigDialogLayer		cdLayer;

	private Class						activeElem;

	private JTree						configTree;

	private DefaultTreeModel		tModel;

	private TreePath					selectionPath;

	private IconTreeNode				searchNode;

	private JPanel						windowPanel;

	private JPanel						labelPanel;

	private JLabel						descriptionLabel;

	private JLabel						titleLabel;

	private ConfigSaveListener		listener;

	private boolean					expandTree;

	private String						lastTemplateLocation;

	private static ConfigDialog	cd;

	/**
	 * Standardkonstruktor (Sektoren Dialog wird aktives Element)
	 */
	public ConfigDialog()
	{
		// this(CDialogSector.class);
		
		this.expandTree = true;
		
		this.initialization(true);

	}

	/**
	 * 
	 * @param Configdialog
	 *           soll dieses Element als aktives Element anzeigen
	 */
	public ConfigDialog(Class activeElem)
	{
		this.activeElem = activeElem;
		this.initialization(true);
	}

	public ConfigDialog(Class activeElem, boolean show)
	{
		this.activeElem = activeElem;
		this.initialization(show);
	}

	private void initialization(boolean show)
	{
		this.setResizable(true);
		cd = this;
		this.cdLayer = ConfigDialogLayer.getInstance();
		tempCache = ConfigDialogTempCache.getInstance();

		// Fenster-Einstellungen
		this.toFront();
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				tempCache.cancelAllSettings();
			}
		});
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(Messages.getString("ConfigDialog.0")); //$NON-NLS-1$
		setIconImage(new ImageIcon(
				FileOperations.getAbsolutePath("icons/intern/icon.png")).getImage()); //$NON-NLS-1$
		setLayout(new BorderLayout());

		descriptionLabel = new JLabel();
		descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$

		titleLabel = new JLabel();
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 18)); //$NON-NLS-1$
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		labelPanel = new JPanel(gbLayout);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(5, 10, 10, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		gbLayout.setConstraints(titleLabel, c);
		labelPanel.add(titleLabel);

		c.gridy = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		gbLayout.setConstraints(descriptionLabel, c);
		labelPanel.add(descriptionLabel);

		windowPanel = new JPanel(new BorderLayout());
		windowPanel.add(createLogoPanel());
		JButton okButton = new JButton(Messages.getString("ConfigDialog.40")); //$NON-NLS-1$
		okButton.setActionCommand("OK");
		JButton cancelButton = new JButton(Messages.getString("ConfigDialog.1")); //$NON-NLS-1$
		JButton saveButton = new JButton(Messages.getString("ConfigDialog.38")); //$NON-NLS-1$
		JButton loadButton = new JButton(Messages.getString("ConfigDialog.39")); //$NON-NLS-1$

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		listener = new ConfigSaveListener();

		saveButton.addActionListener(listener);
		loadButton.addActionListener(listener);

		saveButton.setActionCommand("save"); //$NON-NLS-1$
		loadButton.setActionCommand("load"); //$NON-NLS-1$

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(loadButton);

		JScrollPane scrollableWindowPane = new JScrollPane(windowPanel);
		scrollableWindowPane.setBorder(null);

		tModel = new DefaultTreeModel(new IconTreeNode(
				Messages.getString("ConfigDialog.7"), null)); //$NON-NLS-1$
		configTree = new JTree(tModel);
		configTree.setCellRenderer(new MyTreeCellRenderer());
		constructTree();
		expandNodesToActiveElement();
		configTree.setRowHeight(20);
		configTree.addTreeSelectionListener(new MyTreeListener());
		JScrollPane js = new JScrollPane(configTree);
		add(buttonPanel, BorderLayout.SOUTH);

		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, js,
				scrollableWindowPane);
		jsp.setDividerLocation(200 + jsp.getInsets().left);
		add(jsp);

		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// int top = (screenSize.height - this.getHeight()) / 2;
		// int left = (screenSize.width - this.getWidth()) / 2;
		// setLocation(left, top);
		if (this.expandTree)
			expandTree();

		// issue #1
		int configWidth = 600, configHeight = 400; // default 600x400
		Dimension mainProgramDemension = VennMaker.getInstance().getSize();
		double mainProgWidth = mainProgramDemension.getWidth(),
				mainProgHeight = mainProgramDemension.getHeight();
		double configDialogRatio = 2.0 / 3; // or 2/3 of the main window
		double optWidth = mainProgWidth * configDialogRatio,
				optHeight = mainProgHeight * configDialogRatio;
		if (mainProgWidth < configWidth) {
			configWidth = (int)Math.floor(mainProgWidth);
		} else if (optWidth > configWidth) {
			configWidth = (int)Math.floor(optWidth);
		}
		if (mainProgHeight < configHeight) {
			configHeight = (int)Math.floor(mainProgHeight);
		} else if (optHeight > configHeight) {
			configHeight = (int)Math.floor(optHeight);
		}
		this.setSize(configWidth, configHeight);
		this.setLocationRelativeTo(VennMaker.getInstance());
		
		setVisible(show);
	}

	/**
	 * Methode um den ganzen Baum der Elemente zu erneuern.</br> z.B. noetig wenn
	 * ein neues Netzwerk hinzukommt.
	 */
	public void updateTree()
	{
		constructTree();
		tModel.reload();
		expandNodesToActiveElement();
	}

	/**
	 * shows a dialog for loading VennMaker templates
	 * 
	 * @param withEvents
	 *           <code>true</code> if <code>LoadTemplateEvent</code> should be
	 *           fired (used for loading a template an immediately starting the
	 *           <code>InterviewController</code> to perform an interview
	 * @return <code>IO.OPERATION_SUCESSFULL if operation was sucessfull or <code>IO.OPERATION_FAILED</code>
	 *         if operation was not sucessfull
	 */
	public int showLoadTemplateDialog(boolean withEvents)
	{
		return IO.loadTemplate(withEvents);
	}

	/**
	 * Wird genutzt um den Baum der Elemente bis auf das aktive Element zu
	 * erweitern.
	 */
	private void expandNodesToActiveElement()
	{
		if (searchNode != null)
		{
			selectionPath = new TreePath(searchNode.getPath());
			configTree.setSelectionPath(selectionPath);
			setConfigDialog((IconTreeNode) selectionPath.getLastPathComponent());
		}
	}

	/**
	 * Baut den ConfigDialog-Baum mit allen Elementen die im ConfigDialogLayer
	 * vorhanden sind auf.</br> Dazu werden per Reflection neue Instanzen der
	 * Klassen gebildet und in den Baum gehangen.
	 */
	private void constructTree()
	{
		IconTreeNode node = null;
		IconTreeNode root = (IconTreeNode) tModel.getRoot();
		root.removeAllChildren();
		ConfigDialogElement tmp = null;
		// Project Elements
		for (Class c : cdLayer.getProjectElements())
		{
			try
			{
				Object o = c.newInstance();
				// tmp = (ConfigDialogElement) o;
				if (ConfigDialogLayer.getInstance().getActivatedProjectElement(
						c.getSimpleName()) == null)
				{
					tmp = (ConfigDialogElement) o;

					ConfigDialogLayer.getInstance().setActivatedProjectElement(
							tmp.getClass().getSimpleName(), tmp);
				}
				else
				{
					tmp = ConfigDialogLayer.getInstance()
							.getActivatedProjectElement(c.getSimpleName());
				}

				node = new IconTreeNode(tmp, tmp.getIcon());
				root.add(node);

				if (c == activeElem)
				{
					searchNode = node;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		IconTreeNode mainNetNode = new IconTreeNode(
				Messages.getString("ConfigDialog.6"), null); //$NON-NLS-1$
		// Network Elements
		Netzwerk activeNet = VennMaker.getInstance().getActualVennMakerView()
				.getNetzwerk();
		for (Netzwerk net : VennMaker.getInstance().getProject().getNetzwerke())
		{
			IconTreeNode networkNode = new IconTreeNode(net, null);
			for (Class c : cdLayer.getNetworkElements())
			{
				try
				{
					Object o = c.newInstance();

					if (ConfigDialogLayer.getInstance().getActivatedNetworkElement(
							c.getSimpleName(), net.getBezeichnung()) == null)
					{
						tmp = (ConfigDialogElement) o;
						tmp.setNet(net);
						ConfigDialogLayer.getInstance().setActivatedNetworkElement(
								tmp.getClass().getSimpleName(), tmp,
								net.getBezeichnung());
					}
					else
					{
						tmp = ConfigDialogLayer.getInstance()
								.getActivatedNetworkElement(c.getSimpleName(),
										net.getBezeichnung());
					}
					// tmp.buildPanel();
					node = new IconTreeNode(tmp, tmp.getIcon());
					networkNode.add(node);

					if (c == activeElem && net.equals(activeNet))
					{
						searchNode = node;
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			mainNetNode.add(networkNode);
		}
		root.add(mainNetNode);

		// Unique NetworkElements
		for (Class c : cdLayer.getUniqueNetElements())
		{
			try
			{
				Object o = c.newInstance();
				tmp = (ConfigDialogElement) o;
				node = new IconTreeNode(tmp, tmp.getIcon());
				mainNetNode.add(node);
				ConfigDialogLayer.getInstance().setActivatedProjectElement(
						tmp.toString(), tmp);
				if (c == activeElem)
				{
					searchNode = node;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handler fuer "Ok" und "Cancel"
	 */
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getActionCommand().equals("OK")) //$NON-NLS-1$
		{
			tempCache.setAllSettings();
		}
		else
		{
			tempCache.cancelAllSettings();
		}
		cd = null;
		Netzwerk net = VennMaker.getInstance().getProject().getCurrentNetzwerk();
		VennMaker.getInstance().updateRelations();
		VennMaker.getInstance().updateRelationsPanel();
		VennMaker.getInstance().refresh();
		VennMaker.getInstance().setCurrentNetwork(net);
		dispose();
	}

	/**
	 * Methode um ein Element (was angeklickt wurde) zu aktivieren und
	 * darzustellen.
	 */
	private void setConfigDialog(IconTreeNode dm)
	{
		Object o = dm.getUserObject();
		if (o instanceof ConfigDialogElement)
		{
			ConfigDialogElement elem = (ConfigDialogElement) o;
			activeElem = elem.getClass();
			// hier wird erst das dialogPanel erstellt. (in addActiveElem)
			ConfigDialogTempCache.getInstance().addActiveElement(elem);
			windowPanel.removeAll();
			titleLabel.setText(elem.getTitle());
			descriptionLabel.setText(elem.getDescription());
			windowPanel.add(labelPanel, BorderLayout.NORTH);
			windowPanel.add(elem.getDialog(), BorderLayout.CENTER);
			windowPanel.validate();
			windowPanel.repaint();
			validate();
			repaint();
		}
		else
		{
			windowPanel.removeAll();
			windowPanel.add(createLogoPanel());
			windowPanel.validate();
			windowPanel.repaint();
			validate();
			repaint();
		}
	}

	// Singleton Pattern
	/**
	 * Liefert Instanz des ConfigDialog zurueck.</br> Sollte immer benutzt werden
	 * um eine Instanz zu erzeugen/erhalten.</br> (Singleton-Pattern)
	 */
	public static ConfigDialog getInstance()
	{
		if (cd == null)
		{
			cd = new ConfigDialog(null, false);
		}

		return cd;
	}

	class MyTreeListener implements TreeSelectionListener
	{
		@Override
		public void valueChanged(TreeSelectionEvent arg0)
		{
			IconTreeNode dm = (IconTreeNode) configTree
					.getLastSelectedPathComponent();
			if (dm == null)
			{
				return;
			}			
			setConfigDialog(dm);
			
		}
	}

	class MyTreeCellRenderer extends DefaultTreeCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
					row, hasFocus);

			IconTreeNode treeNode = (IconTreeNode) value;

			Icon ic = treeNode.getIcon();
			if (ic != null)
			{
				setIcon(ic);
			}
			else if (treeNode.getDepth() == 0)
			{
				setIcon(getDefaultLeafIcon());
			}
			else
			{
				setIcon(getDefaultClosedIcon());
			}
			return this;
		}
	}

	public IconTreeNode getRootNode()
	{
		return (IconTreeNode) this.configTree.getModel().getRoot();
	}

	public void setRootNode(IconTreeNode rootNode)
	{
		if (rootNode == null)
			throw new IllegalArgumentException("Root node should not be null"); //$NON-NLS-1$

		((DefaultTreeModel) configTree.getModel()).setRoot(rootNode);
	}

	/**
	 * Config Dialoge sollten texte der Elemente (Buttons usw) von hier beziehen
	 * um eine maximale Länge zu garantieren (20 chars).
	 */
	public static String getElementCaption(String s)
	{
		if (s != null && s.length() > 20)
			s = s.substring(0, 17) + "..."; //$NON-NLS-1$
		return s;
	}

	/**
	 * Creates a panel with the VennMaker logo and a description text about the
	 * ConfigDialog
	 * 
	 * @return a panel with the VennMaker logo and a description text about the
	 *         ConfigDialog
	 */
	private JPanel createLogoPanel()
	{
		JPanel logoPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel vmLogo = new JLabel(new ImageIcon(VMPaths.VENNMAKER_INTERNAL_ICONS
				+ "VennMakerLogo-gross.png")); //$NON-NLS-1$
		logoPanel.add(vmLogo, gbc);

		gbc.gridy = 1;

		JLabel descriptionLabel = new JLabel(
				Messages.getString("ConfigDialog.Description"), SwingConstants.CENTER); //$NON-NLS-1$

		logoPanel.add(descriptionLabel, gbc);

		return logoPanel;
	}

	/**
	 * Expands all nodes of the ConfigDialog tree
	 */
	private void expandTree()
	{
		for (int i = 0; i < configTree.getRowCount(); i++)
			configTree.expandRow(i);
	}

	/**
	 * Returns the last location a template was saved
	 * 
	 * @return the last location a template was saved
	 */
	public String getLastTemplateLocation()
	{
		return lastTemplateLocation;
	}

	/**
	 * Sets the last location a template was saved
	 * 
	 * @param lastTemplateLocation
	 *           the last location a template was saved
	 */
	public void setLastTemplateLocation(String lastTemplateLocation)
	{
		this.lastTemplateLocation = lastTemplateLocation;
	}
}
