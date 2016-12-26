package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import gui.configdialog.save.AttributeTypeSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingAddAttributeType;
import gui.configdialog.settings.SettingChangeAttributeTypeOrder;
import gui.configdialog.settings.SettingDeleteAttributeType;
import gui.configdialog.settings.SettingEditAttributeType;
import interview.InterviewLayer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import data.AttributeType;
import data.Netzwerk;

/**
 * Dialog zum modifizieren der einzelnen Attributtypen. (Akteur Attribute)
 * 
 * 
 */
public class CDialogEditAttributeTypes extends ConfigDialogElement implements
		ActionListener
{
	private static final long			serialVersionUID	= 1L;

	private JTable							table;

	private MyTableModel					model;

	private JButton						b1						= new JButton(
																				Messages
																						.getString("CDialogEditAttributeTypes.11")); //$NON-NLS-1$

	private JButton						b3						= new JButton(
																				Messages
																						.getString("VennMaker.Delete_type"));			//$NON-NLS-1$

	private Icon							icon;

	private String							getType				= "ACTOR";

	private AttributeTypeSaveElement	attributeSaveElement;

	@Override
	public void buildPanel()
	{
		dialogPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		dialogPanel.setLayout(layout);

		attributeSaveElement = new AttributeTypeSaveElement();

		GridBagConstraints gbc;

		int zeile = 0;
		icon = new ImageIcon("icons/intern/icon.png"); //$NON-NLS-1$
		model = new MyTableModel(VennMaker.getInstance().getProject()
				.getAttributeTypes(getType));
		table = new JTable(model);
		SelectionListener listener = new SelectionListener(table);
		table.getSelectionModel().addListSelectionListener(listener);
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

		b1.setEnabled(false);
		b3.setEnabled(false);

		JScrollPane scrollPane = new JScrollPane(table);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 6;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(scrollPane, gbc);
		dialogPanel.add(scrollPane);

		zeile++;

		b1.setActionCommand("edit"); //$NON-NLS-1$
		b1.addActionListener(this);
		b1.setName("edit");//$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 10, 10, 10);
		layout.setConstraints(b1, gbc);
		dialogPanel.add(b1);

		JButton b2 = new JButton(Messages.getString("VennMaker.New_Type")); //$NON-NLS-1$
		b2.setActionCommand("new"); //$NON-NLS-1$
		b2.addActionListener(this);
		b2.setName("new");//$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 0, 10, 10);
		layout.setConstraints(b2, gbc);
		dialogPanel.add(b2);

		b3.setActionCommand("delete"); //$NON-NLS-1$
		b3.addActionListener(this);
		b3.setName("delete");//$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.EAST;
		gbc.gridx = 3;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 0, 10, 10);
		layout.setConstraints(b3, gbc);
		dialogPanel.add(b3);
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		return null;
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

	private boolean existingName(String s)
	{
		s = s.toLowerCase();
		for (AttributeType a : VennMaker.getInstance().getProject()
				.getAttributeTypes(getType))
		{
			if (a.getLabel().toLowerCase().equals(s))
				return true;
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		ConfigDialogSetting s = null;

		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();

		if ("new".equals(event.getActionCommand())) //$NON-NLS-1$
		{

			String label = (String) JOptionPane
					.showInputDialog(
							ConfigDialog.getInstance(),
							Messages.getString("EditIndividualAttributeTypeDialog.15"), Messages.getString("EditIndividualAttributeTypeDialog.19"), JOptionPane.QUESTION_MESSAGE, icon, null, null); //$NON-NLS-1$ //$NON-NLS-2$
			if (label != null)
			{
				if (existingName(label))
				{
					JOptionPane
							.showMessageDialog(
									null,
									Messages.getString("VennMaker.Allready_Existing"), Messages //$NON-NLS-1$
											.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				}
				else if (!label.equals("")) //$NON-NLS-1$
				{
					AttributeType attributeType = new AttributeType();
					attributeType.setLabel(label);

					s = new SettingAddAttributeType(attributeType);
				}
				else
					JOptionPane
							.showMessageDialog(
									null,
									Messages.getString("VennMaker.Empty_Name"), Messages //$NON-NLS-1$
											.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			}
		}
		else if ("delete".equals(event.getActionCommand())) //$NON-NLS-1$
		{
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1)
			{

				int row = table.convertRowIndexToModel(selectedRow);

				/**
				 * Ueberpruefung ob eins der zu loeschenden Attribute im
				 * ActorSizeVisualizer registriert ist Wenn Ja wird eine Meldung
				 * ausgegeben und das loeschen ist nicht moeglich
				 * 
				 */
				Vector<Netzwerk> networks = VennMaker.getInstance().getProject()
						.getNetzwerke();
				boolean inUseByNetwork = false;
				AttributeType toDelete = VennMaker.getInstance().getProject()
						.getAttributeTypes(getType).get(row);
				String verwendendeNetzwerke = "";
				if (getType.equals("ACTOR"))
					for (Netzwerk n : networks)
					{
						AttributeType sizeAttribute = n.getActorSizeVisualizer()
								.getAttributeType();
						AttributeType symbolAttribute = n.getActorImageVisualizer()
								.getAttributeType();
						if (toDelete.equals(sizeAttribute)
								|| toDelete.equals(symbolAttribute))
						{
							verwendendeNetzwerke += n.getBezeichnung() + "\n";
						}
					}
				inUseByNetwork = verwendendeNetzwerke.length() > 0;

				
				
				
				/**
				 * Ueberpruefung ob eins der zu loeschenden Attribute fuer Sekoren verwendet wird.
				 * Wenn Ja wird eine Meldung
				 * ausgegeben und das loeschen ist nicht moeglich
				 * 
				 */			
				boolean inUseByNetworkSector = false;
				
				if (getType.equals("ACTOR"))
					for (Netzwerk n : networks)
					{
						AttributeType sectorAttribute = n.getHintergrund().getSectorAttribute();

						if (toDelete.equals(sectorAttribute))
						{
							verwendendeNetzwerke += n.getBezeichnung() + "\n";
							inUseByNetworkSector = true;
						}
					}
				
				
				
				
				/**
				 * Ueberpruefung ob eins der zu loeschenden Attribute in einem
				 * Interview Element verwendet werden (Ueberpruefung unnoetig wenn
				 * schon durch Netzwerke benutzt)
				 */
				boolean inUseByInterview = false;
				String interviewElems = null;
				if (!inUseByNetwork)
				{
					interviewElems = InterviewLayer.getInstance()
							.isAttribUsedInElement(toDelete);
					inUseByInterview = interviewElems != null;
				}

				/**
				 * Delete it or not?...
				 */
				if (!inUseByNetwork && !inUseByInterview && !inUseByNetworkSector)
				{
					s = new SettingDeleteAttributeType(toDelete);
					attributeSaveElement.addAttributeToDelete(toDelete);
				}
				else if (inUseByNetwork)
				{
					JOptionPane.showMessageDialog(ConfigDialog.getInstance(),
							Messages.getString("CDialogEditAttributeTypes.Error1")
									+ "\n\n" + verwendendeNetzwerke,
							Messages.getString("CDialogEditAttributeTypes.Error0"),
							JOptionPane.ERROR_MESSAGE);
				}
				else if (inUseByInterview)
				{
					JOptionPane.showMessageDialog(ConfigDialog.getInstance(),
							Messages.getString("CDialogEditAttributeTypes.Error2")
									+ "\n\n" + interviewElems,
							Messages.getString("CDialogEditAttributeTypes.Error0"),
							JOptionPane.ERROR_MESSAGE);
				}
				else if (inUseByNetworkSector)
				{
					JOptionPane.showMessageDialog(ConfigDialog.getInstance(),
							Messages.getString("CDialogEditAttributeTypes.ErrorSector")
									+ "\n\n" + verwendendeNetzwerke,
							Messages.getString("CDialogEditAttributeTypes.Error0"),
							JOptionPane.ERROR_MESSAGE);
				}
				
				
				
			}
		}
		else if ("edit".equals(event.getActionCommand())) //$NON-NLS-1$
		{
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1)
			{
				int row = table.convertRowIndexToModel(selectedRow);
				AttributeType attributeType = VennMaker.getInstance().getProject()
						.getAttributeTypes(getType).get(row);
				EditIndividualAttributeTypeDialog diag = new EditIndividualAttributeTypeDialog();
				diag.showDialog(attributeType);
			}
		}
		ConfigDialogTempCache.getInstance().addSetting(s);
		model.updateAttributes();
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		model.updateAttributes();
	}

	class MyTableModel extends AbstractTableModel
	{
		private List<AttributeType>	attributes;

		static final long					serialVersionUID	= 1L;

		public MyTableModel(List<AttributeType> at)
		{
			attributes = at;
		}

		/**
		 * call this if attributes might changed
		 */
		public void updateAttributes()
		{
			attributes = VennMaker.getInstance().getProject()
					.getAttributeTypes(getType);
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount()
		{
			return 1;
		}

		@Override
		public int getRowCount()
		{
			return attributes.size();
		}

		@Override
		public String getColumnName(int col)
		{

			return Messages.getString("CDialogEditAttributeTypes.13"); //$NON-NLS-1$
		}

		@Override
		public Object getValueAt(int row, int col)
		{

			return attributes.get(row).getLabel();
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
				attributes.get(row).setLabel((String) value);
			}
			else
				JOptionPane.showMessageDialog(null,
						Messages.getString("CDialogEditAttributeTypes.12"), //$NON-NLS-1$
						Messages.getString("VennMaker.Error"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);

			fireTableDataChanged();
		}
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof AttributeTypeSaveElement))
			return;

		AttributeTypeSaveElement elem = (AttributeTypeSaveElement) setting;

		List<AttributeType> typesToAdd = elem.getAttributesToAdd();

		List<AttributeType> typesToDelete = elem.getAttributesToDelete();

		ConfigDialogTempCache cache = ConfigDialogTempCache.getInstance();
		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR"); //$NON-NLS-1$

		cache.addActiveElement(this);

		/**
		 * New AttributeTypes can be set easy
		 */
		for (AttributeType s : typesToAdd)
		{
			boolean contains = false;
			AttributeType oldType = null;

			for (AttributeType a : aTypes)
			{
				if (a.toString().equals(s.toString()))
				{
					contains = true;
					oldType = a;
					break;
				}
			}

			if (contains)
				cache.addSetting(new SettingEditAttributeType(oldType, s));
			else
				cache.addSetting(new SettingAddAttributeType(s));
		}

		/**
		 * If standard AttributeTypes should be deleted, we have to search them
		 * also by their names
		 */
		for (AttributeType s : typesToDelete)
		{
			for (AttributeType type : aTypes)
			{
				if (s.toString().equals(type.toString()))
				{
					cache.addSetting(new SettingDeleteAttributeType(type));
					break;
				}
			}
		}

		if (this.model != null)
			this.model.updateAttributes();
	}

	@Override
	public SaveElement getSaveElement()
	{
		Vector<AttributeType> actorTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR");

		for (AttributeType a : actorTypes)
			attributeSaveElement.addAttribute(a);

		attributeSaveElement.setElementName(this.getClass().getSimpleName());

		return attributeSaveElement;
	}
}
