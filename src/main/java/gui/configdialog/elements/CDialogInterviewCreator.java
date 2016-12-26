package gui.configdialog.elements;

import gui.Messages;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.InterviewSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingInterviewCreator;
import gui.configdialog.settings.SettingInterviewCreatorFinal;
import interview.InterviewController;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.InterviewTreeDragAndDrop;
import interview.InterviewTreeModel;
import interview.InterviewTreeNode;
import interview.categories.IECategory;
import interview.configuration.AddToTreePanel;
import interview.configuration.PreviewPanel;
import interview.elements.InterviewElement;
import interview.elements.RootElement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import data.Reflection;

public class CDialogInterviewCreator extends ConfigDialogElement implements
		ActionListener
{
	private static final long			serialVersionUID	= 1L;

	private JTree							interviewTree;

	private JPanel							configPanel;

	private AddToTreePanel				addButtonPanel;

	private int								elementId;

	private InterviewElement			selectedValue;

	private InterviewTreeModel			treeModel;

	private InterviewTreeNode			interviewRoot;

	private InterviewLayer				tmpLayer;

	private GridBagLayout				gbl;

	private JMenuBar						menuBar;

	private List<InterviewElement>	elementInstances;

	private JButton						btnRemove;

	private TreeSaveListener			listener;

	public CDialogInterviewCreator()
	{
		gbl = new GridBagLayout();
		tmpLayer = InterviewLayer.getInstance();
		elementInstances = new ArrayList<InterviewElement>();

		if (tmpLayer.loadInterviewTree() == null)
		{
			interviewRoot = new InterviewTreeNode(new RootElement());
			interviewTree = new InterviewTreeDragAndDrop(interviewRoot);
			interviewTree.setModel(new InterviewTreeModel(interviewRoot,
					new ArrayList<InterviewTreeNode>()));
			treeModel = (InterviewTreeModel) interviewTree.getModel();
			treeModel.setRoot(interviewRoot);
		}
		else
		{
			interviewTree = tmpLayer.loadInterviewTree();
			treeModel = (InterviewTreeModel) interviewTree.getModel();
			interviewRoot = (InterviewTreeNode) treeModel.getRoot();

			if (tmpLayer.getAllElements().size() > 0)
				this.elementId = tmpLayer.getLastId() + 1;
		}
	}

	private JMenuBar buildCategories()
	{
		JMenuBar bar = new JMenuBar();
		bar.setBorder(BorderFactory.createLineBorder(Color.black));
		Map<Class<? extends IECategory>, List<Class<? extends InterviewElement>>> map = InterviewLayer
				.getInstance().getRegisteredElements();
		Set<Class<? extends IECategory>> categories = map.keySet();

		for (Class<? extends IECategory> c : categories)
		{
			Color color = Reflection.getClassAttribute(c, "fontColor"); //$NON-NLS-1$
			String colorCode = "000000";
			if (color != null)
			{
				colorCode = String.format("%02X", color.getRed())
						+ String.format("%02X", color.getGreen())
						+ String.format("%02X", color.getBlue());
			}
			JMenu menu = new JMenu(
					"<html><font color='#" + colorCode + "'>&#8594</font> " + Messages.getString(c.getSimpleName())); //$NON-NLS-1$
			menu.setBorder(BorderFactory.createLineBorder(Color.black));
			menu.setMargin(new Insets(0, 5, 0, 5));

			List<Class<? extends InterviewElement>> elems = map.get(c);
			for (final Class<? extends InterviewElement> ie : elems)
			{
				JMenuItem item = new JMenuItem(Messages.getString(ie
						.getSimpleName() + ".Name")); //$NON-NLS-1$

				InterviewElement instance = (InterviewElement) Reflection
						.createInstance(ie);
				Image img = instance.getPreview();
				if (img != null)
				{
					img = img.getScaledInstance(80, 80, BufferedImage.SCALE_SMOOTH);
					ImageIcon i = new ImageIcon(img);
					item.setIcon(i);
				}
				elementInstances.add(instance);
				item.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						selectElement(ie);
					}
				});
				menu.add(item);
			}
			bar.add(menu);
		}

		return bar;
	}

	public void selectElement(Class<? extends InterviewElement> kindOfElem)
	{
		// update and unselect Tree Element
		InterviewTreeNode node = (InterviewTreeNode) interviewTree
				.getLastSelectedPathComponent();
		if (node != null)
		{
			if (selectedValue != null)
			{
				selectedValue.addToTree();
				node.setUserObject(selectedValue);

				InterviewLayer.getInstance().replaceElement(selectedValue);
			}
		}
		interviewTree.setSelectionPath(null);

		selectedValue = null;
		// find instance of kindOfElem in all instances
		for (InterviewElement ie : elementInstances)
			if (ie.getClass().equals(kindOfElem))
			{
				selectedValue = ie;
				break;
			}

		if (selectedValue == null)
			return;

		selectedValue.setDescription(selectedValue.getDescription());

		configPanel.removeAll();

		btnRemove.setEnabled(false);
		rebuild();
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		configPanel.removeAll();
		rebuild();
	}

	public void rebuild()
	{
		if (selectedValue == null)
			return;
		JPanel elementPanel = selectedValue.getConfigurationDialog();
		JLabel label = new JLabel("<html>" + selectedValue.getInstructionText()); //$NON-NLS-1$
		GridBagConstraints c = new GridBagConstraints();

		JLabel titleLabel = new JLabel();
		Font f = titleLabel.getFont();
		Font f2 = f.deriveFont((float) (f.getSize() + 6));
		titleLabel.setFont(f2);
		Color col = Reflection.getClassAttribute(selectedValue.getClass(),
				"fontColor");
		titleLabel.setForeground(col != null ? col : Color.black);
		titleLabel.setText(Messages.getString(selectedValue.getClass()
				.getSimpleName() + ".Name"));

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridheight = 1;
		c.gridwidth = 1;
		gbl.setConstraints(titleLabel, c);
		configPanel.add(titleLabel);

		c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridheight = 1;
		c.gridwidth = 1;
		gbl.setConstraints(label, c);
		configPanel.add(label);

		c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 5.0;
		c.weighty = 5.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		c.gridheight = 1;
		c.gridwidth = 1;
		gbl.setConstraints(elementPanel, c);
		configPanel.add(elementPanel);

		if (selectedValue.hasPreview())
			addButtonPanel.addComponent(new PreviewPanel(selectedValue));
		else
			addButtonPanel.addComponent(new JPanel());

		configPanel.validate();
		configPanel.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		InterviewLayer layer = tmpLayer;

		if (e.getActionCommand().equals("start")) //$NON-NLS-1$
		{
			InterviewController controller = new InterviewController();
			controller.setCalledFromConfigDialog(true);
			controller.startInTestMode();
		}
		else if (e.getActionCommand().equals("add")) //$NON-NLS-1$
		{
			add(false);
		}
		else if (e.getActionCommand().equals("clone")) //$NON-NLS-1$
		{
			if ((interviewTree.getSelectionPath() == null)
					|| (interviewTree.getSelectionPath().getParentPath() == null))
				return;

			InterviewTreeNode node = (InterviewTreeNode) interviewTree
					.getSelectionPath().getLastPathComponent();
			InterviewElement elem = (InterviewElement) node.getUserObject();

			InterviewElement clonedElement = elem.cloneElement();
			selectedValue = clonedElement;
			add(true);
		}
		else
		{
			/* remove - Button */
			TreePath path = interviewTree.getSelectionPath();
			if (path == null)
				return;
			InterviewTreeNode node = (InterviewTreeNode) interviewTree
					.getSelectionPath().getLastPathComponent();
			InterviewElement elem = (InterviewElement) node.getUserObject();

			if (node.getChildCount() > 0)
			{
				if (JOptionPane.showConfirmDialog(configPanel, Messages
						.getString("CDialogInterviewCreator.deleteAllChildren")) == JOptionPane.NO_OPTION) //$NON-NLS-1$
					return;
			}

			layer.removeElement(elem);

			this.treeModel.removeNodeFromParent(node);

			if (layer.getAllElements().size() < 1)
				this.btnRemove.setEnabled(false);
		}
	}

	public void add(boolean addClonedElement)
	{
		if (selectedValue == null || !selectedValue.addToTree())
			return;

		if (selectedValue.getDescription() == null)
		{
			JOptionPane.showMessageDialog(this,
					Messages.getString("CDialogInterviewCreator.RequestItemName"),
					"", JOptionPane.WARNING_MESSAGE);
			return;
		}

		InterviewElement newValue = selectedValue.cloneElement();

		InterviewTreeNode node = new InterviewTreeNode(newValue);
		TreePath selectedPath = interviewTree.getSelectionPath();
		InterviewTreeNode root = null;

		if (selectedPath != null && !addClonedElement)
			root = (InterviewTreeNode) selectedPath.getLastPathComponent();

		InterviewElement parent = null;

		if (root != null && root.getUserObject() instanceof InterviewElement)
			parent = (InterviewElement) root.getUserObject();

		int pos = -1;

		if (!addClonedElement)
		{
			for (int i = 0; i < elementInstances.size(); i++)
			{
				if (elementInstances.get(i).getClass().equals(newValue.getClass()))
				{
					pos = i;
					break;
				}
			}
		}
		else
		{
			InterviewElement elem = ((InterviewElement) ((InterviewTreeNode) interviewTree
					.getSelectionPath().getLastPathComponent()).getUserObject());

			for (int i = 0; i < elementInstances.size(); i++)
			{
				if (elementInstances.get(i).getClass().getCanonicalName()
						.equals(elem.getClass().getCanonicalName()))
				{
					pos = i;
					break;
				}
			}
		}

		listener.setAddCalled(true);
		InterviewElement newInstance = null;
		if (pos > -1)
		{
			elementInstances.remove(pos);

			newInstance = (InterviewElement) Reflection.createInstance(newValue
					.getClass());
			elementInstances.add(newInstance);
		}

		newValue.setId(InterviewLayer.getInstance().generateId());

		SettingInterviewCreator setting = new SettingInterviewCreator();
		setting.setInterviewNode(node);
		setting.setInterviewTree(interviewTree);
		setting.setInterviewValue(newValue);

		setting.setRoot(interviewRoot);

		ConfigDialogTempCache.getInstance().addSetting(setting);
		treeModel.reload();

		if (pos > -1)
			selectElement(newInstance.getClass());
		else
			selectElement(newValue.getClass());

		selectedValue = newInstance;

	}

	/**
	 * This method saves all changed TreeNodes which are not saved yet
	 */
	public void saveTreeNodesOnClose()
	{
		TreePath path = interviewTree.getSelectionPath();
		TreeSelectionEvent evt = new TreeSelectionEvent(this, path, true, path,
				path);
		listener.valueChanged(evt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.configdialog.ConfigDialogElement#buildPanel()
	 */
	@Override
	public void buildPanel()
	{
		configPanel = new JPanel(gbl); // contains the actual selected Element
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel treePanel = new JPanel(new BorderLayout()); // contains the
																			// treeView with the
																			// whole interview
		JPanel buttonPanel = new JPanel(new GridBagLayout()); // panel with "add"
																				// "remove" "start"
																				// buttons
		if (menuBar == null)
			menuBar = buildCategories();

		// button panel ------------------------
		btnRemove = new JButton(
				Messages.getString("CDialogInterviewCreator.Remove")); //$NON-NLS-1$
		JButton start = new JButton(
				Messages.getString("CDialogInterviewCreator.Start")); //$NON-NLS-1$
		JButton clone = new JButton(
				Messages.getString("CDialogInterviewCreator.clone")); //$NON-NLS-1$

		clone.addActionListener(this);
		clone.setActionCommand("clone"); //$NON-NLS-1$

		btnRemove.addActionListener(this);
		btnRemove.setActionCommand("remove"); //$NON-NLS-1$
		btnRemove.setEnabled(false);

		start.addActionListener(this);
		start.setActionCommand("start"); //$NON-NLS-1$

		gbc.gridx = 0;
		gbc.gridy = 0;
		buttonPanel.add(btnRemove, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		buttonPanel.add(clone, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		// buttonPanel.add(start,gbc);

		GridBagConstraints logoConstraints = new GridBagConstraints();
		JLabel vmLogo = new JLabel(new ImageIcon(
				"icons/intern/VennMakerLogo-gross.png"));
		gbl.setConstraints(vmLogo, logoConstraints);
		configPanel.add(vmLogo); //$NON-NLS-1$

		logoConstraints = new GridBagConstraints();
		logoConstraints.gridy = 1;

		JLabel introText = new JLabel(
				Messages.getString("CDialogInterviewCreator.IntroText"));
		gbl.setConstraints(introText, logoConstraints);
		configPanel.add(introText); //$NON-NLS-1$

		// centerPanel
		GridBagLayout gbLayout = new GridBagLayout();
		JPanel centerPanel = new JPanel(gbLayout);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(10, 10, 0, 0);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbLayout.setConstraints(configPanel, gbc);
		centerPanel.add(configPanel);

		addButtonPanel = new AddToTreePanel(this);
		addButtonPanel.addComponent(new JPanel());

		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 20, 0);
		gbLayout.setConstraints(addButtonPanel, gbc);
		centerPanel.add(addButtonPanel);

		// tree Panel ------------------------
		JScrollPane jspRight = new JScrollPane(interviewTree);
		listener = new TreeSaveListener(configPanel);
		TreeModelListener modelListener = new InterviewTreeModelListener();
		treeModel.removeTreeModelListener(modelListener);
		treeModel.addTreeModelListener(modelListener);
		interviewTree.removeTreeSelectionListener(listener);
		interviewTree.addTreeSelectionListener(listener);
		interviewTree.setCellRenderer(new ColorTreeRenderer());
		treePanel.add(jspRight, BorderLayout.CENTER);
		treePanel.add(buttonPanel, BorderLayout.SOUTH);

		// SplitPanel
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				centerPanel, treePanel);
		int width = ConfigDialog.getInstance().getSize().width;
		splitPane.setDividerLocation((int) (width - width / 2.25));

		// Super Panel
		GridBagLayout gbl = new GridBagLayout();
		JPanel p = new JPanel(gbl);
		GridBagConstraints g = new GridBagConstraints();

		g.gridx = 0;
		g.gridy = 0;
		g.weightx = 0;
		g.weighty = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.FIRST_LINE_START;

		gbl.setConstraints(menuBar, g);
		p.add(menuBar);

		g.gridy = 1;
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;

		gbl.setConstraints(splitPane, g);
		p.add(splitPane);

		dialogPanel = p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.configdialog.ConfigDialogElement#getFinalSetting()
	 */
	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		return new SettingInterviewCreatorFinal(this);
	}

	class TreeSaveListener implements TreeSelectionListener
	{
		private int			id;

		private JPanel		configPanel;

		private boolean	addCalled;

		public TreeSaveListener(JPanel configPanel)
		{
			this.configPanel = configPanel;
		}

		public TreeSaveListener()
		{
			this.id = 0;
		}

		@Override
		public void valueChanged(TreeSelectionEvent event)
		{
			TreePath pathToShow = event.getNewLeadSelectionPath();
			TreePath oldPath = event.getOldLeadSelectionPath();

			if (pathToShow != null && pathToShow.getPathCount() <= 1)
			{
				interviewTree.setSelectionPath(new TreePath(interviewRoot));

				configPanel.removeAll();

				GridBagConstraints logoConstraints = new GridBagConstraints();
				JLabel vmLogo = new JLabel(new ImageIcon(
						"icons/intern/VennMakerLogo-gross.png"));
				gbl.setConstraints(vmLogo, logoConstraints);
				configPanel.add(vmLogo); //$NON-NLS-1$

				logoConstraints = new GridBagConstraints();
				logoConstraints.gridy = 1;

				JLabel introText = new JLabel(
						Messages.getString("CDialogInterviewCreator.IntroText"));
				gbl.setConstraints(introText, logoConstraints);
				configPanel.add(introText); //$NON-NLS-1$

				/* cannot remove the root of the interview */
				btnRemove.setEnabled(false);

				configPanel.revalidate();
				configPanel.repaint();

				selectedValue = null;

				return;
			}
			if (pathToShow == null)
			{
				btnRemove.setEnabled(false);
				return;
			}

			try
			{
				if (oldPath != null
						&& (oldPath.getLastPathComponent() != interviewRoot))
				{
					InterviewTreeNode nodeToSave = (InterviewTreeNode) oldPath
							.getLastPathComponent();

					if ((nodeToSave.getUserObject() instanceof InterviewElement))
					{
						// InterviewElement elementToSave = (InterviewElement)
						// nodeToSave
						// .getUserObject();
						//
						// if (!elementToSave.addToTree())
						// return;

						if (!addCalled && selectedValue != null)
						{
							selectedValue.addToTree();
							selectedValue.setParent(((InterviewElement) nodeToSave
									.getUserObject()).getParent());
							nodeToSave.setUserObject(selectedValue);

							InterviewLayer.getInstance().replaceElement(selectedValue);
						}
					}

					((DefaultTreeModel) interviewTree.getModel())
							.nodeChanged(nodeToSave);
				}

				setAddCalled(false);

				InterviewTreeNode nodeToShow = (InterviewTreeNode) pathToShow
						.getLastPathComponent();
				InterviewElement elementToShow = ((InterviewElement) nodeToShow
						.getUserObject());
				elementToShow.setId(((InterviewElement) nodeToShow.getUserObject())
						.getId());
				selectedValue = elementToShow;
				if (configPanel != null)
					configPanel.removeAll();
				btnRemove.setEnabled(true);
				rebuild();

			} catch (ClassCastException exn)
			{
				return;
			}
		}

		public int getId()
		{
			return this.id;
		}

		public void setAddCalled(boolean addCalled)
		{
			this.addCalled = addCalled;
		}

		public void setConfigPanel(JPanel configPanel)
		{
			this.configPanel = configPanel;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof TreeSaveListener))
				return false;

			TreeSaveListener listener = (TreeSaveListener) obj;

			if (listener.getId() == this.getId())
				return true;

			return false;
		}

		@Override
		public int hashCode()
		{
			return this.id;
		}

	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof InterviewSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		interviewRoot.removeAllChildren();
		treeModel = new InterviewTreeModel(interviewRoot,
				new ArrayList<InterviewTreeNode>());
		interviewTree.setModel(treeModel);
		InterviewSaveElement element = (InterviewSaveElement) setting;
		InterviewLayer layer = InterviewLayer.getInstance();

		layer.saveInterviewTree(interviewTree);

		int startId = 0;

		for (InterviewElementInformation info : element.getElementInformations())
		{

			if (info == null)
				continue;

			try
			{
				InterviewElement elem = info.getElementClass().newInstance();
				if (!elem.isChild())
				{
					if (startId < info.getId())
						startId = info.getId();

					InterviewTreeNode node = new InterviewTreeNode(elem);
					interviewRoot.add(node);
					treeModel.addNodeToData(node);

					elem.setId(info.getId());
					elem.setFilter(info.getFilter());
					elem.setFilterIndex(info.getFilterIndex());
					// elem.setChildStatus(false);
					elem.setTime(info.getTime());

					layer.addElement(elem);
					elem.setElementInfo(info);

					SettingInterviewCreator settingToCache = new SettingInterviewCreator();
					settingToCache.setInterviewNode(node);
					settingToCache.setInterviewTree(interviewTree);
					settingToCache.setInterviewValue(elem);
					settingToCache.setParentElement(null);
					settingToCache.setParent(null);
					settingToCache.setRoot(interviewRoot);

					ConfigDialogTempCache.getInstance().addSetting(settingToCache);
				}
				else
				{
					SettingInterviewCreator settingToCache = new SettingInterviewCreator();
					settingToCache.setInterviewNode(treeModel.getNodeFrom(elem));
					settingToCache.setInterviewTree(interviewTree);
					settingToCache.setInterviewValue(elem);
					settingToCache.setParentElement(elem.getParent());
					settingToCache
							.setParent(treeModel.getNodeFrom(elem.getParent()));
					settingToCache.setRoot(interviewRoot);

					ConfigDialogTempCache.getInstance().addSetting(settingToCache);

				}

			} catch (IllegalAccessException iae)
			{
				System.err.println("Could not access class"); //$NON-NLS-1$
				iae.printStackTrace();
			} catch (InstantiationException ie)
			{
				System.err.println("Could not create an instance of class"); //$NON-NLS-1$
				ie.printStackTrace();
			}
		}
		treeModel.reload();
		InterviewLayer.getInstance().setStartId(startId);
	}

	@Override
	public SaveElement getSaveElement()
	{
		List<InterviewElementInformation> topLevelInfos = new ArrayList<InterviewElementInformation>();
		List<InterviewElement> elements = InterviewLayer.getInstance()
				.getTopLevelElements();

		for (InterviewElement elem : elements)
		{
			InterviewElementInformation info = elem.getElementInfo();

			if (info == null)
				continue;

			if (elem.getFilter() != null && elem.getFilterIndex() != null)
			{
				info.setFilter(elem.getFilter());
				info.setFilterIndex(elem.getFilterIndex().toArray(
						new Integer[elem.getFilterIndex().size()]));
				info.setTime(elem.getTime());
			}

			topLevelInfos.add(info);
		}

		InterviewSaveElement elem = new InterviewSaveElement(topLevelInfos);

		elem.setElementName(this.getClass().getSimpleName());

		return elem;
	}
}

class InterviewTreeModelListener implements TreeModelListener
{
	private int	id;

	public InterviewTreeModelListener()
	{
		this.id = 0;
	}

	@Override
	public void treeNodesChanged(TreeModelEvent arg0)
	{
	}

	@Override
	public void treeNodesInserted(TreeModelEvent arg0)
	{
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent arg0)
	{
	}

	@Override
	public void treeStructureChanged(TreeModelEvent arg0)
	{
	}

	public int getId()
	{
		return this.id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof InterviewTreeModelListener))
			return false;

		InterviewTreeModelListener listener = (InterviewTreeModelListener) obj;

		if (listener.getId() == this.getId())
			return true;

		return false;
	}

	@Override
	public int hashCode()
	{
		return this.id;
	}
}

class SaveWrapper
{
	private InterviewTreeNode	root;

	private InterviewLayer		layer;

	public SaveWrapper(InterviewTreeNode root, InterviewLayer layer)
	{
		this.root = root;
		this.layer = layer;
	}

	public InterviewTreeNode getRoot()
	{
		return this.root;
	}

	public InterviewLayer getLayer()
	{
		return this.layer;
	}
}

class ColorTreeRenderer extends DefaultTreeCellRenderer
{
	private static final long	serialVersionUID	= 1L;

	public ColorTreeRenderer()
	{
		setOpaque(true);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
				hasFocus);

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object inside = node.getUserObject();

		Color color = Color.black;
		try
		{
			color = (Color) inside.getClass().getField("fontColor").get(null); //$NON-NLS-1$
		} catch (Exception e)
		{
		}

		setForeground(color);
		setBackground(sel ? Color.lightGray : Color.white);

		return this;
	}
}
