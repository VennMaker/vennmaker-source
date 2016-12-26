/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.NewAttributeListener;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import gui.configdialog.save.RelationalAttributesSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.ImmidiateConfigDialogMacroSetting;
import gui.configdialog.settings.SettingAddAttributeType;
import gui.configdialog.settings.SettingChangeAttributeTypeOrder;
import gui.configdialog.settings.SettingDeleteAttributeType;
import gui.configdialog.settings.SettingEditAttributeType;
import gui.configdialog.settings.SettingRelationDirection;
import gui.configdialog.settings.SettingRenameAttributeGroup;
import interview.InterviewLayer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;
import data.Relation;

/**
 * Dialog zum modifizieren der einzelnen Attributtypen. (Relations Attribute)
 * 
 * 
 */
public class CDialogEditRelationalAttributeTypes extends ConfigDialogElement
		implements ActionListener
{
	private static final long				serialVersionUID	= 1L;

	private JComboBox<String>				attributeTypeSelector;

	private Icon								icon;

	private String								activeType;

	private HashMap<String, SubPanel>	typePanel;

	private HashMap<String, JCheckBox>	isDirected;

	private Vector<AttributeType>			tempTypes;

	private Vector<String>					tempTypeList;

	private JPanel								panel					= new JPanel();

	@Override
	public void buildPanel()
	{

		if (dialogPanel != null)
			return;

		typePanel = new HashMap<String, SubPanel>();
		isDirected = new HashMap<String, JCheckBox>();
		Vector<String> tempTypes = VennMaker.getInstance().getProject()
				.getAttributeCollectors();
		for (String tempType : tempTypes)
		{
			if (activeType == null)
				activeType = tempType;

			JCheckBox isDirectedBox = new JCheckBox(
					Messages
							.getString("CDialogEditRelationalAttributeTypes.directed"));//$NON-NLS-1$
			isDirectedBox.setSelected(VennMaker.getInstance().getProject()
					.getIsDirected(tempType));
			isDirected.put(tempType, isDirectedBox);

			isDirected.get(tempType).setActionCommand(tempType);
			isDirected.get(tempType).addActionListener(
					new DirectedActionListener());

			typePanel.put(tempType, new SubPanel(tempType));
		}
		dialogPanel = collectorPanel();
	}

	private JPanel collectorPanel()
	{
		panel = new JPanel();

		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);

		tempTypes = VennMaker.getInstance().getProject().getAttributeTypes();

		tempTypeList = new Vector<String>();
		for (AttributeType tempType : tempTypes)
		{
			if (!tempType.getType().equals("ACTOR") //$NON-NLS-1$
					&& (!tempTypeList.contains(tempType.getType())))
			{
				tempTypeList.add(tempType.getType());
			}
		}
		attributeTypeSelector = new JComboBox<String>(tempTypeList);
		attributeTypeSelector.setRenderer(new RelationTypeCellRenderer());

		GridBagConstraints gbc;

		int zeile = 0;
		icon = new ImageIcon("icons/intern/icon.png"); //$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 0, 10);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.3;
		layout.setConstraints(attributeTypeSelector, gbc);
		attributeTypeSelector.setActionCommand("changeActiveType");
		panel.add(attributeTypeSelector);

		attributeTypeSelector.addActionListener(this);

		final JButton newButton = new JButton(
				Messages.getString("CDialogEditRelationalAttributeTypes.2")); //$NON-NLS-1$
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 4;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.3;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(newButton, gbc);
		panel.add(newButton);

		newButton.setActionCommand("new");
		newButton.addActionListener(this);

		final JButton renameButton = new JButton(Messages.getString("CDialogEditRelationalAttributeTypes.rename")); //$NON-NLS-1$
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 5;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.3;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(renameButton, gbc);
		panel.add(renameButton);

		renameButton.setActionCommand("rename");
		renameButton.addActionListener(this);

		JButton deleteButton = new JButton(
				Messages.getString("CDialogEditRelationalAttributeTypes.7")); //$NON-NLS-1$
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 6;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.3;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(deleteButton, gbc);
		panel.add(deleteButton);

		deleteButton.setActionCommand("delete");
		deleteButton.addActionListener(this);

		zeile++;

		for (String tempType : tempTypeList)
		{
			if (!tempType.equals("ACTOR")) //$NON-NLS-1$
			{
				gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = GridBagConstraints.RELATIVE;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridwidth = 6;
				gbc.gridheight = 7;
				gbc.weightx = 6;
				gbc.weighty = 7;
				gbc.insets = new Insets(0, 10, 10, 10);
				typePanel.get(tempType).reset();
				panel.add(typePanel.get(tempType).getPanel(), gbc);
				if (tempType.equals(activeType))
					typePanel.get(tempType).getPanel().setVisible(true);
				else
					typePanel.get(tempType).getPanel().setVisible(false);

				gbc.gridx = 3;
				gbc.gridy = zeile - 1;
				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				gbc.weightx = 0.5;
				gbc.weighty = 0.3;
				gbc.insets = new Insets(0, 10, 0, 10);
				panel.add(isDirected.get(tempType), gbc);
			}
		}
		attributeTypeSelector.setSelectedItem(activeType);
		return panel;
	}

	@Override
	public void refreshBeforeGetDialog()
	{

		Vector<String> tempTypes = VennMaker.getInstance().getProject()
				.getAttributeCollectors();

		Boolean changed = false;

		for (String newType : tempTypes)
		{
			if ( (tempTypeList != null) && (!tempTypeList.contains(newType) ) )
			{
				changed = true;
				tempTypeList.add(newType);
				typePanel.put(newType, new SubPanel(newType));

				isDirected
						.put(newType,
								new JCheckBox(
										Messages
												.getString("CDialogEditRelationalAttributeTypes.directed")));//$NON-NLS-1$
				isDirected.get(newType).setActionCommand(newType);
				isDirected.get(newType).addActionListener(
						new DirectedActionListener());

				GridBagConstraints gbc;
				gbc = new GridBagConstraints();
				gbc.insets = new Insets(0, 10, 10, 10);
				gbc.gridx = 0;
				gbc.gridy = GridBagConstraints.RELATIVE;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridwidth = 6;
				gbc.gridheight = 7;
				gbc.weightx = 6;
				gbc.weighty = 7;
				typePanel.get(newType).reset();
				panel.add(typePanel.get(newType).getPanel(), gbc);

				gbc.gridx = 3;
				gbc.gridy = 0;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				gbc.weightx = 0.5;
				gbc.weighty = 0.3;
				panel.add(isDirected.get(newType), gbc);
			}
		}

		if (changed)
		{
			for (String tempType : tempTypeList)
			{
				if (!tempType.equals("ACTOR")) //$NON-NLS-1$
				{
					if (tempType.equals(activeType))
					{
						typePanel.get(tempType).getPanel().setVisible(true);
						isDirected.get(tempType).setVisible(true);
					}
					else
					{
						typePanel.get(tempType).getPanel().setVisible(false);
						isDirected.get(tempType).setVisible(false);
					}
				}
			}
		}

		if (typePanel != null)
			typePanel.get(activeType).updateAttributeTypes();
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		return null;
	}

	class SubPanel implements ActionListener
	{
		/**
		 * 
		 */
		private static final long		serialVersionUID	= 1L;

		private List<AttributeType>	aTypes;

		private JButton					b1						= new JButton(
																				Messages
																						.getString("CDialogEditAttributeTypes.11")); //$NON-NLS-1$

		private JButton					b3						= new JButton(
																				Messages
																						.getString("VennMaker.Delete_type"));			//$NON-NLS-1$

		private JTable						table;

		private String						getType;

		private MyTableModel				model;

		private JPanel						panel;

		public SubPanel()
		{

		}

		/**
		 * 
		 */
		public void destroy()
		{
			this.getPanel().setVisible(false);

			for (AttributeType a : aTypes)
				ConfigDialogTempCache.getInstance().addSetting(
						new SettingDeleteAttributeType(a));

			this.aTypes.clear();
		}

		/**
		 * @param actor
		 */
		public SubPanel(String type)
		{
			getType = type;
			constructArrayList(type);
			model = new MyTableModel();
			table = new JTable(model);

			panel = panel1(type);
		}

		public void reset()
		{
			constructArrayList(getType);
			model = new MyTableModel();
			table = new JTable(model);

			panel = panel1(getType);
		}

		public JPanel getPanel()
		{
			return panel;
		}

		private JPanel panel1(final String getType)
		{
			JPanel panel = new JPanel();
			GridBagLayout layout = new GridBagLayout();
			panel.setLayout(layout);

			GridBagConstraints gbc;

			int zeile = 0;
			SelectionListener listener = new SelectionListener(table);
			table.getSelectionModel().addListSelectionListener(listener);

			table.setPreferredScrollableViewportSize(new Dimension(300, 200));
			table.setFillsViewportHeight(true);
			table.setCellSelectionEnabled(true);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setDragEnabled(true);

			table.setDropTarget(new DropTarget()
			{
				private static final long	serialVersionUID	= 1L;

				@Override
				public void drop(DropTargetDropEvent dtde)
				{
					Point point = dtde.getLocation();
					int row = table.rowAtPoint(point);
					SettingChangeAttributeTypeOrder s = new SettingChangeAttributeTypeOrder(
							row, table.getSelectedRow(), getType);
					ConfigDialogTempCache.getInstance().addSetting(s);
					// handle drop inside current table
					super.drop(dtde);
					refreshBeforeGetDialog();
				}
			});
			// TableRowSorter<MyTableModel> sorter = new
			// TableRowSorter<MyTableModel>(
			// model);
			// table.setRowSorter(sorter);

			this.b1.setEnabled(false);
			this.b3.setEnabled(false);

			JScrollPane scrollPane = new JScrollPane(table);
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = zeile;
			gbc.gridwidth = 6;
			gbc.gridheight = 1;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.insets = new Insets(10, 0, 10, 0);
			layout.setConstraints(scrollPane, gbc);
			panel.add(scrollPane);

			zeile++;

			this.b1.setActionCommand("edit"); //$NON-NLS-1$
			this.b1.addActionListener(this);
			this.b1.setName("edit");//$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(0, 0, 0, 10);
			layout.setConstraints(this.b1, gbc);
			panel.add(this.b1);

			JButton b2 = new JButton(
					Messages.getString("CDialogEditRelationalAttributeTypes.12")); //$NON-NLS-1$
			b2.setActionCommand("new"); //$NON-NLS-1$
			/*
			 * Change ActionListener from "this" to "NewAttributeListener()"
			 * (gui.configDialog.NewAttributListener) to eliminate CodeClones
			 */
			b2.addActionListener(new NewAttributeListener(getType)
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					super.actionPerformed(event);
					updateAttributeTypes();
				}
			});
			b2.setName("new");//$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 2;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(0, 0, 0, 10);
			layout.setConstraints(b2, gbc);
			panel.add(b2);

			this.b3.setActionCommand("delete"); //$NON-NLS-1$
			this.b3.addActionListener(this);
			this.b3.setName("delete");//$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.EAST;
			gbc.gridx = 3;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			layout.setConstraints(this.b3, gbc);
			panel.add(this.b3);

			return panel;
		}

		private void constructArrayList(String getType)
		{
			aTypes = VennMaker.getInstance().getProject()
					.getAttributeTypes(getType);
		}

		class SelectionListener implements ListSelectionListener
		{
			JTable	table;

			SelectionListener(JTable table)
			{
				this.table = table;
			}

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getSource() == table.getSelectionModel()
						&& table.getRowSelectionAllowed())
				{
					if (e.getFirstIndex() == -1)
					{
						b1.setEnabled(false);
						b3.setEnabled(false);
					}
					else
					{
						b1.setEnabled(true);
						b3.setEnabled(true);
					}
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			ConfigDialogSetting s = null;

			if (table.getCellEditor() != null)
				table.getCellEditor().stopCellEditing();

			if ("new".equals(event.getActionCommand())) //$NON-NLS-1$
			{
				/*
				 * CODEPART REPLACED BY NewAttributeListener (see creation of
				 * buttons further on top
				 */
				//
				// String label = (String) JOptionPane
				// .showInputDialog(
				// ConfigDialog.getInstance(),
				// Messages
				//										.getString("EditIndividualAttributeTypeDialog.15"), Messages.getString("EditIndividualAttributeTypeDialog.19"), JOptionPane.QUESTION_MESSAGE, icon, null, null); //$NON-NLS-1$ //$NON-NLS-2$
				// if (label != null)
				// {
				//					if (!label.equals("")) //$NON-NLS-1$
				// {
				// AttributeType attributeType = new AttributeType();
				// attributeType.setLabel(label);
				// attributeType.setType(getType);
				// s = new SettingAddAttributeType(attributeType);
				// }
				// else
				// JOptionPane
				// .showMessageDialog(
				// null,
				//										Messages.getString("VennMaker.Empty_Name"), Messages //$NON-NLS-1$
				//												.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				// }
			}
			else if ("delete".equals(event.getActionCommand())) //$NON-NLS-1$
			{
				int selectedRow = table.getSelectedRow();
				if (selectedRow != -1)
				{
					int row = table.convertRowIndexToModel(selectedRow);
					/* check, if it's the las categorial attributetype */
					if ((VennMaker.getInstance().getProject()
							.getAttributeTypesDiscrete(activeType).size() == 1)
							&& aTypes.get(row).getPredefinedValues() != null)
					{
						JOptionPane
								.showMessageDialog(
										null,
										Messages
												.getString("CDialogEditRelationalAttributeTypes.11"), Messages //$NON-NLS-1$
												.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
					}
					else
					{
						/**
						 * check if this attribute is in use in any network
						 */
						AttributeType toBeDeleted = aTypes.get(row);

						String inUseIn = "";
						Boolean inUse = false;
						Vector<Netzwerk> currentNetworks = VennMaker.getInstance()
								.getProject().getNetzwerke();

						for (Netzwerk n : currentNetworks)
						{
							Boolean skipNetwork = false;
							for (Akteur a : n.getAkteure())
							{
								for (Relation r : a.getRelations(n))
								{
									if (r.getAttributeValue(toBeDeleted, n) != null)
									{
										skipNetwork = true;
										inUse = true;
										inUseIn += n.getBezeichnung() + "\r\n";
										break;
									}
								}
								/* once found, network can be skipped over */
								if (skipNetwork)
									break;
							}
						}

						/**
						 * check if this attribute is in use in the interview
						 */
						String usedInInterview = InterviewLayer.getInstance()
								.isAttribUsedInElement(toBeDeleted);
						if (usedInInterview != null)
						{
							inUse = true;
							inUseIn += ":\r\n" + usedInInterview;
						}

						if (!inUse)
						{
							s = new SettingDeleteAttributeType(toBeDeleted);
						}
						else
						{
							JOptionPane
									.showMessageDialog(
											null,
											Messages
													.getString("CDialogEditRelationalAttributeTypes.14") + "\r\n" + inUseIn, Messages //$NON-NLS-1$
													.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			}
			else if ("edit".equals(event.getActionCommand())) //$NON-NLS-1$
			{
				int selectedRow = table.getSelectedRow();
				if (selectedRow != -1)
				{
					int row = table.convertRowIndexToModel(selectedRow);

					AttributeType attributeType;
					{
						attributeType = aTypes.get(row);
						new EditIndividualAttributeTypeDialog()
								.showDialog(attributeType);
					}
				}
			}

			ConfigDialogTempCache.getInstance().addSetting(s);
			updateAttributeTypes();
		}

		private void updateAttributeTypes()
		{
			aTypes = VennMaker.getInstance().getProject()
					.getAttributeTypes(getType);
			model.fireTableDataChanged();
		}

		class MyTableModel extends AbstractTableModel
		{
			static final long	serialVersionUID	= 1L;

			public MyTableModel()
			{

			}

			@Override
			public int getColumnCount()
			{
				return 1;
			}

			@Override
			public int getRowCount()
			{
				return aTypes.size();
			}

			@Override
			public String getColumnName(int col)
			{

				return Messages.getString("CDialogEditRelationalAttributeTypes.13"); //$NON-NLS-1$
			}

			@Override
			public Object getValueAt(int row, int col)
			{

				return aTypes.get(row).getLabel();
			}

			@Override
			public Class<?> getColumnClass(int col)
			{

				return String.class;
			}

			@Override
			public boolean isCellEditable(int row, int col)
			{
				return true;
			}

			@Override
			public void setValueAt(Object value, int row, int col)
			{
				if (!value.equals("")) //$NON-NLS-1$
				{
					aTypes.get(row).setLabel((String) value);
				}
				else
					JOptionPane.showMessageDialog(null,
							Messages.getString("CDialogEditAttributeTypes.12"), //$NON-NLS-1$
							Messages.getString("VennMaker.Error"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);

				fireTableDataChanged();
			}

		}
	}

	class DirectedActionListener implements ActionListener
	{
		/*
		 * ActionListener fuer die Checkbox "directed" bei Click neues Setting
		 * erstellen
		 */
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			ConfigDialogSetting s = new SettingRelationDirection(
					arg0.getActionCommand(),
					((JCheckBox) arg0.getSource()).isSelected());
			ConfigDialogTempCache.getInstance().addSetting(s);
		}

	}

	class RelationTypeCellRenderer extends JLabel implements ListCellRenderer
	{
		/**
		 * CellRenderer zur Internationalisierung von "STANDARDRELATION"
		 */
		private static final long	serialVersionUID	= 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			setOpaque(true);

			if (value.toString().equals("STANDARDRELATION")) //$NON-NLS-1$
				setText(Messages.getString("AttributeCollector.StandardRelation"));//$NON-NLS-1$
			else
				setText(value.toString());

			setBackground(isSelected ? Color.GRAY : Color.white);
			setForeground(isSelected ? Color.white : Color.black);

			return this;
		}

	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof RelationalAttributesSaveElement))
			return;

		RelationalAttributesSaveElement element = (RelationalAttributesSaveElement) setting;

		Vector<AttributeType> attributeTypes = VennMaker.getInstance()
				.getProject().getAttributeTypes();

		Vector<AttributeType> currentTypes = new Vector<AttributeType>();

		for (AttributeType aType : attributeTypes)
			if (!(aType.getType().equals("ACTOR"))) //$NON-NLS-1$
				currentTypes.add(aType);

		Vector<AttributeType> newTypes = element.getAttributeTypes();

		/**
		 * If attributes have been deleted, we search them by their name in the
		 * current AttributeTypes and delete them
		 */
		if (currentTypes.size() > newTypes.size())
		{

			for (AttributeType cType : currentTypes)
			{
				boolean found = false;

				for (AttributeType nType : newTypes)
				{
					if (cType.toString().equals(nType.toString()))
					{
						found = true;
						break;
					}
				}

				if (!found)
					ConfigDialogTempCache.getInstance().addSetting(
							new SettingDeleteAttributeType(cType));
			}
		}
		else
		{
			for (AttributeType nType : newTypes)
			{
				boolean nTypeFound = false;

				for (AttributeType cType : currentTypes)
				{
					if (cType.toString().equals(nType.toString()))
					{
						/**
						 * Attribute already exists, but may has been modified
						 */

						ConfigDialogTempCache.getInstance().addSetting(
								new SettingEditAttributeType(cType, nType));
						nTypeFound = true;
						break;
					}
				}

				/**
				 * AttributeType doesn't exist, so it has to be created
				 */
				if (!nTypeFound)
				{

					VennMaker.getInstance().getProject()
							.setMainGeneratorType(nType.getType(), nType);

					ConfigDialogTempCache.getInstance().addSetting(
							new SettingAddAttributeType(nType));
				}
			}
		}
		// currently not fired upon loading!
		buildPanel();
		// attributeTypeSelector = new JComboBox<>(tempTypeList);

		Map<String, Boolean> directed = element.getDirectedRelations();

		for (String collector : VennMaker.getInstance().getProject()
				.getAttributeCollectors())
		{
			if (directed != null)
			{

				if (directed.get(collector))
				{
					// attempt to fix nullptr exception
					JCheckBox isDirectedBox = new JCheckBox(
							Messages
									.getString("CDialogEditRelationalAttributeTypes.directed"));//$NON-NLS-1$
					isDirectedBox.setSelected(VennMaker.getInstance().getProject()
							.getIsDirected(collector));
					isDirectedBox.setActionCommand(collector);
					isDirectedBox.addActionListener(new DirectedActionListener());

					isDirected.put(collector, isDirectedBox);

					isDirected.get(collector).setSelected(directed.get(collector));
					ConfigDialogTempCache.getInstance().addSetting(
							new SettingRelationDirection(collector, directed
									.get(collector)));

				}
			}
		}
	}

	@Override
	public SaveElement getSaveElement()
	{
		Vector<AttributeType> relationalAttributes = new Vector<AttributeType>();

		Vector<AttributeType> currentTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes();

		Map<String, Boolean> directed = new HashMap<String, Boolean>();

		for (AttributeType t : currentTypes)
		{
			if (!(t.getType().equals("ACTOR"))) //$NON-NLS-1$
			{
				relationalAttributes.add(t);

				directed.put(t.getType(), isCheckboxSelected(t.getType()));
			}
		}

		RelationalAttributesSaveElement elem = new RelationalAttributesSaveElement(
				relationalAttributes, directed);

		elem.setElementName(this.getClass().getSimpleName());
		return elem;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		// TODO Auto-generated method stub

		/* attributeTypeSelector */
		if ("changeActiveType".equals(arg0.getActionCommand()))
		{
			activeType = (String) attributeTypeSelector.getSelectedItem();

			for (String tempType : tempTypeList)
			{
				if (!tempType.equals("ACTOR")) //$NON-NLS-1$
				{
					if (tempType.equals(activeType))
					{
						typePanel.get(tempType).getPanel().setVisible(true);
						isDirected.get(tempType).setVisible(true);
					}
					else
					{
						typePanel.get(tempType).getPanel().setVisible(false);
						isDirected.get(tempType).setVisible(false);
					}
				}

			}
		}

		/* new relationgroup */
		if ("new".equals(arg0.getActionCommand()))
		{
			String newType = JOptionPane.showInputDialog(Messages
					.getString("CDialogEditRelationalAttributeTypes.3")); //$NON-NLS-1$
			if (newType != null)
				/* keine doppelten Typen! */
				if (tempTypeList.contains(newType)
						|| newType.trim().equals("ACTOR"))
					JOptionPane
							.showMessageDialog(
									(JButton) arg0.getSource(),
									Messages
											.getString("CDialogEditRelationalAttributeTypes.4"), //$NON-NLS-1$
									Messages
											.getString("CDialogEditRelationalAttributeTypes.5"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
				else
				{
					tempTypeList.add(newType);
					activeType = newType;
					typePanel.put(newType, new SubPanel(newType));

					isDirected
							.put(newType,
									new JCheckBox(
											Messages
													.getString("CDialogEditRelationalAttributeTypes.directed")));//$NON-NLS-1$
					isDirected.get(newType).setActionCommand(newType);
					isDirected.get(newType).addActionListener(
							new DirectedActionListener());

					GridBagConstraints gbc;
					gbc = new GridBagConstraints();
					gbc.insets = new Insets(0, 10, 10, 10);
					gbc.gridx = 0;
					gbc.gridy = GridBagConstraints.RELATIVE;
					gbc.fill = GridBagConstraints.BOTH;
					gbc.gridwidth = 6;
					gbc.gridheight = 7;
					gbc.weightx = 6;
					gbc.weighty = 7;
					typePanel.get(newType).reset();
					panel.add(typePanel.get(newType).getPanel(), gbc);

					gbc.gridx = 3;
					gbc.gridy = 0;
					gbc.fill = GridBagConstraints.BOTH;
					gbc.gridwidth = 1;
					gbc.gridheight = 1;
					gbc.weightx = 0.5;
					gbc.weighty = 0.3;
					panel.add(isDirected.get(newType), gbc);

					for (String tempType : tempTypeList)
					{
						if (!tempType.equals("ACTOR")) //$NON-NLS-1$
						{
							if (tempType.equals(activeType))
							{
								typePanel.get(tempType).getPanel().setVisible(true);
								isDirected.get(tempType).setVisible(true);
							}
							else
							{
								typePanel.get(tempType).getPanel().setVisible(false);
								isDirected.get(tempType).setVisible(false);
							}
						}
					}

					attributeTypeSelector.setSelectedItem(activeType);

					/* Neues Attribut in dieser Gruppe anlegen */
					ConfigDialogSetting s = null;
					String label = (String) JOptionPane
							.showInputDialog(
									ConfigDialog.getInstance(),
									Messages
											.getString("EditIndividualAttributeTypeDialog.20"), Messages.getString("EditIndividualAttributeTypeDialog.19"), JOptionPane.QUESTION_MESSAGE, icon, null, null); //$NON-NLS-1$ //$NON-NLS-2$

					boolean delGroup = true;

					if (label != null)
					{
						if (!label.equals("")) //$NON-NLS-1$
						{
							AttributeType attributeType = new AttributeType();
							attributeType.setLabel(label);
							attributeType.setType(activeType);
							s = new SettingAddAttributeType(attributeType);

							/*
							 * direkt EditierDialog oeffnen, um neues Attribut
							 * einzustellen
							 */
							EditIndividualAttributeTypeDialog createNewAttribute = new EditIndividualAttributeTypeDialog();
							createNewAttribute.showDialog(attributeType);

							/* MainGenerator auf das erste Attribut setzen */
							if (!createNewAttribute.wasCanceled())
							{
								if (attributeType.getPredefinedValues() != null
										&& (attributeType.getPredefinedValues().length > 0))
									VennMaker
											.getInstance()
											.getProject()
											.setMainGeneratorType(activeType,
													attributeType);

								delGroup = false;
							}
						}
						else
							JOptionPane
									.showMessageDialog(
											null,
											Messages.getString("VennMaker.Empty_Name"), Messages //$NON-NLS-1$
													.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
					}

					if (delGroup)
					{
						/* erstellte Gruppe wieder loeschen */

						tempTypeList.remove(activeType);
						isDirected.get(activeType).setVisible(false);
						typePanel.get(activeType).destroy();
						Object prevActiveType = attributeTypeSelector.getSelectedItem();
						activeType = tempTypeList.firstElement();
						attributeTypeSelector.setSelectedItem(activeType);
						attributeTypeSelector.removeItem(prevActiveType);
					}
					else{

						ConfigDialogTempCache.getInstance().addSetting(s);
					}
				}

			typePanel.get(activeType).updateAttributeTypes();
			refreshBeforeGetDialog();
		}

		/* delete relationgroup */
		if ("delete".equals(arg0.getActionCommand()))
		{
			if (JOptionPane
					.showConfirmDialog(
							null,
							Messages
									.getString("CDialogEditRelationalAttributeTypes.8"), //$NON-NLS-1$
							Messages
									.getString("CDialogEditRelationalAttributeTypes.9"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) //$NON-NLS-1$
			{
				if ((tempTypeList.size() > 1))
				{
					String itemToDelete = (String) attributeTypeSelector
							.getSelectedItem();
					tempTypeList.remove(itemToDelete);
					activeType = tempTypeList.firstElement();
					attributeTypeSelector.setSelectedItem(activeType);

					ImmidiateConfigDialogMacroSetting s = new ImmidiateConfigDialogMacroSetting();

					for (AttributeType attributeToDelete : VennMaker.getInstance()
							.getProject().getAttributeTypes(itemToDelete))
					{
						if (attributeToDelete.getType().equals(itemToDelete))
							s.addSetting(new SettingDeleteAttributeType(
									attributeToDelete));
					}

					ConfigDialogTempCache.getInstance().addSetting(s);

					typePanel.get(itemToDelete).destroy();
					isDirected.get(itemToDelete).setVisible(false);
					refreshBeforeGetDialog();
				}
				else
					JOptionPane
							.showMessageDialog(
									null,
									Messages
											.getString("CDialogEditRelationalAttributeTypes.10"), //$NON-NLS-1$
									Messages
											.getString("CDialogEditRelationalAttributeTypes.11"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
			}
		}

		/* rename relationgroup */
		if ("rename".equals(arg0.getActionCommand()))
		{

			String newName = JOptionPane.showInputDialog(Messages
					.getString("CDialogEditRelationalAttributeTypes.newname"));

			if (newName != null)
				/* keine doppelten Typen! */
				if (tempTypeList.contains(newName)
						|| newName.trim().equals("ACTOR"))
					JOptionPane
							.showMessageDialog(
									(JButton) arg0.getSource(),
									Messages
											.getString("CDialogEditRelationalAttributeTypes.4"), //$NON-NLS-1$
									Messages
											.getString("CDialogEditRelationalAttributeTypes.5"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
				else
				{
					String renameItem = (String) attributeTypeSelector
							.getSelectedItem();

					ImmidiateConfigDialogMacroSetting s = new ImmidiateConfigDialogMacroSetting();

					for (AttributeType attributeToRename : VennMaker.getInstance()
							.getProject().getAttributeTypes(renameItem))
					{
						if (attributeToRename.getType().equals(renameItem))
							s.addSetting(new SettingRenameAttributeGroup(
									attributeToRename, newName));
					}

					ConfigDialogTempCache.getInstance().addSetting(s);

					typePanel.remove(renameItem);
					typePanel.put(newName, new SubPanel(newName));

					JCheckBox directed = isDirected.remove(renameItem);
					isDirected.put(newName, directed);

					tempTypeList.removeElement(renameItem);
					tempTypeList.add(newName);

					activeType = newName;

					attributeTypeSelector.setSelectedItem(activeType);

					refreshBeforeGetDialog();
				}
		}
	}

	/**
	 * checks, if the corresponding checkbox is selected - if none exists, only
	 * standardvalue will be returned (false)
	 */
	private Boolean isCheckboxSelected(String type)
	{
		if (isDirected.get(type) != null)
		{
			return isDirected.get(type).isSelected();
		}

		return false;
	}

}
