/**
 * 
 */
package gui.configdialog.elements;

import gui.ColorEditor;
import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.NewAttributeListener;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import gui.configdialog.save.ActorPieSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingActorPie;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import data.AttributeType;

/**
 * Dialog der eine Auswahl an Attributen bietet die als "Actorpie" dargestellt
 * werden koennen.
 * 
 * 
 */
public class CDialogActorPie extends ConfigDialogElement
{
	private static final long							serialVersionUID	= 1L;

	private JTable											table;

	private MyTableModel									model;

	private Map<AttributeType, Object>				typeAndSelection;

	private Map<AttributeType, Object>				sectorColor;

	private Map<AttributeType, Vector<Object>>	auswahl;

	private JButton										newAttributeButton;

	private Vector<AttributeType>						aTypes;

	private String											getType				= "ACTOR";

	@Override
	public void buildPanel()
	{
		if (sectorColor == null)
		{
			sectorColor = new HashMap<AttributeType, Object>(net
					.getActorSectorVisualizer().getSectorColor());

			typeAndSelection = new HashMap<AttributeType, Object>(net
					.getActorSectorVisualizer().getAttributeTypesAndSelection());

			// Auswahlliste neu aufbauen und mit einer weiteren Auswahl versehen
			auswahl = new HashMap<AttributeType, Vector<Object>>();

			aTypes = VennMaker.getInstance().getProject()
					.getAttributeTypesDiscrete(getType);

			for (AttributeType a : aTypes)
			{
				Vector<Object> tmp = new Vector<Object>();
				tmp.add(""); //$NON-NLS-1$
				for (Object t : a.getPredefinedValues())
					tmp.add(t);

				auswahl.put(a, tmp);
				if (sectorColor.get(a) == null)
					sectorColor.put(a, new Color(255, 255, 255));
			}
		}

		GridBagLayout layout = new GridBagLayout();
		dialogPanel = new JPanel(layout);

		if (table == null)
		{
			table = buildTable();
		}
		JScrollPane tableScrollPane = new JScrollPane(table);

		newAttributeButton = new JButton(
				Messages.getString("EditIndividualAttributeTypeDialog.19")); //$NON-NLS-1$
		newAttributeButton.addActionListener(new NewAttributeListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				super.actionPerformed(e);
				refreshBeforeGetDialog();
			}
		});

		GridBagConstraints gbc;

		/*
		 * Hier werden erst 3 Labels als "filler" eingefuegt
		 */
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.25;
		gbc.insets = new Insets(10, 10, 0, 10);
		dialogPanel.add(new JLabel(), gbc);

		gbc.gridx++;
		gbc.insets = new Insets(10, 0, 0, 10);
		dialogPanel.add(new JLabel(), gbc);

		gbc.gridx++;
		dialogPanel.add(new JLabel(), gbc);

		/*
		 * Nun folgen die "echten" Komponenten
		 */
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 0.10;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(newAttributeButton, gbc);
		dialogPanel.add(newAttributeButton);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 4;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(tableScrollPane, gbc);
		dialogPanel.add(tableScrollPane);
	}

	private JTable buildTable()
	{
		/**
		 * Tabelle fuer Categories (predefined values)
		 */
		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(getType);
		model = new MyTableModel(aTypes);

		/**
		 * auswahl neu fuellen (wegen moeglichen Temp Attributen)
		 */
		auswahl.clear();
		for (AttributeType a : aTypes)
		{
			Object[] items = a.getPredefinedValues();
			Vector<Object> v = new Vector<Object>();
			v.add("");
			for (Object o : items)
				v.add(o);
			auswahl.put(a, v);
		}

		table = new JTable(model);

		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new ColorEditor());

		table.getColumnModel().getColumn(0)
				.setCellRenderer(new AttributeTypeRenderer());
		table.getColumnModel().getColumn(1)
				.setCellEditor(new MyComboBoxEditor(this.auswahl));
		table.getColumnModel().getColumn(3)
				.setCellRenderer(new EditAttributeCellRenderer());
		table.getColumnModel().getColumn(3)
				.setCellEditor(new EditAttributeCellEditor());

		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);

		table.setRowHeight(20);

		return table;
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(getType);
		auswahl.clear();
		for (AttributeType a : aTypes)
		{
			Vector<Object> tmp = new Vector<Object>();
			tmp.add(""); //$NON-NLS-1$
			for (Object t : a.getPredefinedValues())
				tmp.add(t);

			auswahl.put(a, tmp);
			if (sectorColor.get(a) == null)
				sectorColor.put(a, new Color(255, 255, 255));
		}
		model.attributeType = aTypes;
		model.fireTableDataChanged();
	}

	private AttributeType getAttributeAtRow(int row)
	{
		// /* HashMap Keyset ist extra sortiert; diese Sortierung muss nicht mit
		// der Sortierung in der Tabelle uebereinstimmen! */
		// int current = 0;
		// Iterator<AttributeType> iter = auswahl.keySet().iterator();
		// AttributeType at = null;
		// while (iter.hasNext())
		// {
		// at = iter.next();
		// if (current >= row)
		// break;
		// current++;
		// }
		//
		// if (current != row)
		// at = null;
		//
		// return at;

		return aTypes.get(row);
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		ConfigDialogSetting s = new SettingActorPie(net, typeAndSelection,
				sectorColor);
		return s;
	}

	class MyTableModel extends AbstractTableModel
	{
		static final long					serialVersionUID	= 1L;

		private Vector<AttributeType>	attributeType;

		public MyTableModel(Vector<AttributeType> t)
		{
			this.attributeType = t;
		}

		@Override
		public int getColumnCount()
		{
			return 4;
		}

		@Override
		public int getRowCount()
		{
			return this.attributeType.size();
		}

		@Override
		public String getColumnName(int col)
		{
			String r;
			switch (col)
			{
				case 0:
					r = Messages.getString("CDialogActorPie.Col0"); //$NON-NLS-1$
					break;

				case 1:
					r = Messages.getString("CDialogActorPie.Col1"); //$NON-NLS-1$
					break;

				case 2:
					r = Messages.getString("CDialogActorPie.Col2"); //$NON-NLS-1$
					break;

				default:
					r = Messages.getString("CDialogEditAttributeTypes.4"); //$NON-NLS-1$
					break;
			}
			return r;
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			Object r = null;
			switch (col)
			{
				case 0:
					r = this.attributeType.get(row);
					break;

				case 1:
					Object tmp = typeAndSelection.get(this.attributeType.get(row));
					if (tmp != null)
						r = tmp;
					else
						r = ""; //$NON-NLS-1$

					break;

				case 2:
					r = sectorColor.get(this.attributeType.get(row));
					break;

				default:
					r = row;
					break;
			}

			return r;
		}

		@Override
		public Class<?> getColumnClass(int col)
		{
			switch (col)
			{

				case 0:
					return AttributeType.class;

				case 1:
					return Object.class;

				case 2:
					return Color.class;

				case 3:
					return Object.class;

				default:
					return getValueAt(0, col).getClass();

			}
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			switch (col)
			{
				case 0:
					return true;
				case 1:
					return true;
				case 2:
					if (typeAndSelection.get(this.attributeType.get(row)) == null)
						return false;
				default:
					return true;
			}
		}

		@Override
		public void setValueAt(Object v, int row, int col)
		{
			switch (col)
			{
				case 0:
					break;

				case 1:
					if (v.equals("")) //$NON-NLS-1$
					{
						typeAndSelection.remove(this.attributeType.get(row));
						sectorColor.remove(this.attributeType.get(row));
					}
					else
					{ // Workaround
						// Es muss überprüft werden ob der Attributwert
						// abgeschnitten wurde
						// um in diesem Fall den Orginalwert in die Liste
						// einzufügen...
						AttributeType att = this.attributeType.get(row);
						Object val = v;
						if (v.toString().endsWith("..."))
						{
							String vS = v.toString();
							vS = vS.substring(0, vS.length() - 3);
							for (Object obj : att.getPredefinedValues())
								if (obj.toString().startsWith(vS))
								{
									val = obj;
									break;
								}
						}
						typeAndSelection.put(att, val);
					}
					break;

				case 2:
					if (typeAndSelection.get(this.attributeType.get(row)) != null)
						sectorColor.put(this.attributeType.get(row), (Color) v);
					break;
			}
			fireTableDataChanged();
		}

	}

	class MyComboBoxEditor extends DefaultCellEditor
	{
		private static final long							serialVersionUID	= 1L;

		private Map<AttributeType, Vector<Object>>	selection;

		public MyComboBoxEditor(Map<AttributeType, Vector<Object>> t)
		{
			super(new JComboBox());
			selection = t;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column)
		{
			delegate.setValue(value);

			((JComboBox) editorComponent).removeAllItems();

			if (column == 0)
			{
				for (AttributeType o : selection.keySet())
					((JComboBox) editorComponent).addItem(o);

				((JComboBox) editorComponent).setEditable(false);

			}

			if (column == 1)
			{

				for (Object o : selection.get(table.getValueAt(row, 0)))
					((JComboBox) editorComponent).addItem(o == null ? ""
							: ConfigDialog.getElementCaption(o.toString()));

				((JComboBox) editorComponent).setEditable(false);
			}

			return editorComponent;
		}

	}

	public class ColorRenderer extends JLabel implements TableCellRenderer
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;

		Border							unselectedBorder	= null;

		Border							selectedBorder		= null;

		boolean							isBordered			= true;

		public ColorRenderer(boolean isBordered)
		{
			this.isBordered = isBordered;
			setOpaque(true); // MUST do this for background to show up.
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object color, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			Color newColor = (Color) color;
			setBackground(newColor);
			if (isBordered)
			{
				if (isSelected)
				{
					if (selectedBorder == null)
					{
						selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
								table.getSelectionBackground());
					}
					setBorder(selectedBorder);
				}
				else
				{
					if (unselectedBorder == null)
					{
						unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
								5, table.getBackground());
					}
					setBorder(unselectedBorder);
				}
			}

			return this;
		}
	}

	class EditAttributeCellRenderer extends JButton implements TableCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			if (isSelected)
			{
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			}
			else
			{
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			this.setText(Messages.getString("CDialogEditAttributeTypes.11"));
			return this;
		}
	}

	class EditAttributeCellEditor extends DefaultCellEditor
	{
		private static final long	serialVersionUID	= 1L;

		private boolean				isPushed;

		private JButton				button;

		private int						value;

		public EditAttributeCellEditor()
		{
			super(new JCheckBox());
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					value = Integer.parseInt(e.getActionCommand());
					fireEditingStopped();
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int col)
		{
			this.value = row;
			if (isSelected)
			{
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			}
			else
			{
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			button.setText(Messages.getString("CDialogEditAttributeTypes.11"));
			button.setActionCommand("" + row);
			isPushed = true;
			return button;
		}

		@Override
		public Object getCellEditorValue()
		{
			if (isPushed)
			{
				EditIndividualAttributeTypeDialog e = new EditIndividualAttributeTypeDialog();
				e.showDialog(getAttributeAtRow(value));
				refreshBeforeGetDialog();
			}
			isPushed = false;
			return value;
		}

		@Override
		public boolean stopCellEditing()
		{
			isPushed = false;
			return super.stopCellEditing();
		}

		@Override
		protected void fireEditingStopped()
		{
			super.fireEditingStopped();
		}
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof ActorPieSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		ActorPieSaveElement element = (ActorPieSaveElement) setting;

		Map<AttributeType, Object> selection = element.getTypeAndSelection();
		Map<AttributeType, Object> color = element.getSectorColor();

		Set<Map.Entry<AttributeType, Object>> selectionValues = selection
				.entrySet();
		Set<Map.Entry<AttributeType, Object>> colors = color.entrySet();

		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR"); //$NON-NLS-1$

		for (AttributeType type : aTypes)
		{
			for (Iterator<Map.Entry<AttributeType, Object>> iterator = selectionValues
					.iterator(); iterator.hasNext();)
			{
				Map.Entry<AttributeType, Object> value = iterator.next();

				if (value.getKey().toString().equals(type.toString()))
				{
					Object[] preVals = type.getPredefinedValues();

					for (Object obj : preVals)
					{
						if (value.getValue().toString().equals(obj.toString()))
						{
							typeAndSelection.put(type, obj);
							break;
						}
					}
					break;
				}
			}

			for (Iterator<Map.Entry<AttributeType, Object>> iterator = colors
					.iterator(); iterator.hasNext();)
			{
				Map.Entry<AttributeType, Object> value = iterator.next();

				if (value.getKey().toString().equals(type.toString()))
				{
					sectorColor.put(type, value.getValue());
					break;
				}
			}
		}

		model.fireTableDataChanged();
	}

	@Override
	public SaveElement getSaveElement()
	{
		ActorPieSaveElement element = new ActorPieSaveElement(typeAndSelection,
				sectorColor);
		element.setElementName(this.getClass().getSimpleName());

		return element;
	}
}

class AttributeTypeRenderer extends JLabel implements TableCellRenderer
{
	private static final long	serialVersionUID	= 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		String text;

		if (value instanceof AttributeType)
		{
			AttributeType at = (AttributeType) value;
			text = at.getLabel();
		}
		else
		{
			text = (String) value;
		}

		this.setText(text);
		this.setToolTipText(text);

		return this;
	}
}
