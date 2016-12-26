/**
 * 
 */
package gui.configdialog.elements;

import gui.ColorEditor;
import gui.Messages;
import gui.PaintLegendPolicy;
import gui.VennMaker;
import gui.configdialog.AuxiliaryFunctions;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogRelationCache;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.RelationColorSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingRelationColor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import data.AttributeType;

/**
 * Dialog zum verknuepfen eines Attributwertes mit einer Farbe einer Relation.
 * 
 * 
 */
public class CDialogRelationColor extends ConfigDialogElement implements
		ItemListener, ActionListener
{
	private static final long									serialVersionUID	= 1L;

	private JTable													table;

	private JComboBox												attributeCollectorList;

	/**
	 * the active relationcollector
	 */
	private String													activeType			= "STANDARDRELATION";											//$NON-NLS-1$

	private Vector<String>										tempTypeList;

	private JComboBox												attributeList;

	/** HashMaps for every given attributeCollector (the Subpanels) */
	private HashMap<String, JPanel>							typePanel			= new HashMap<String, JPanel>();

	/** HashMaps for every given attributeCollector (the tablemodels) */
	private HashMap<String, MyTableModel>					model					= new HashMap<String, MyTableModel>();

	private Map<String, JTable>								tables				= new HashMap<String, JTable>();

	/**
	 * HashMaps for every given attributeCollector (the corresponding values for
	 * the colors)
	 */

	private Map<String, Map<String, Vector<Color>>>		selectedColor		= new HashMap<String, Map<String, Vector<Color>>>();

	private Map<String, Map<String, Vector<String>>>	predefValues		= new HashMap<String, Map<String, Vector<String>>>();

	/**
	 * HashMaps for every given attributeCollector (which attributetype is chosen
	 * for the active attributecollector)
	 */
	private HashMap<String, AttributeType>					comboSelected		= new HashMap<String, AttributeType>();

	private ConfigDialogRelationCache<Color>				attributeTypeCache;

	private boolean												paintColorLegend	= true;

	private AttributeType										activeAttributeType;

	private CDialogRelationColorTable						newestVersion;

	public CDialogRelationColor()
	{
		newestVersion = new CDialogRelationColorTable();
	}

	@Override
	public void buildPanel()
	{
		if (newestVersion != null)
		{
			newestVersion.buildPanel();

			return;
		}

		dialogPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		dialogPanel.setLayout(layout);

		typePanel = new HashMap<String, JPanel>();

		Vector<String> tempTypes = VennMaker.getInstance().getProject()
				.getAttributeCollectors();

		if (tempTypes == null || tempTypes.size() <= 0)
			return;

		typePanel.clear();
		tempTypeList = new Vector<String>();
		for (String tempType : tempTypes)
		{
			if (!tempType.equals("ACTOR") //$NON-NLS-1$
					&& !typePanel.keySet().contains(tempType)
					&& AuxiliaryFunctions.checkType(tempType))
			{
				typePanel.put(tempType, (JPanel) subDialog(tempType));
				tempTypeList.add(tempType);
			}
		}

		if (!typePanel.keySet().contains(activeType))
			activeType = tempTypes.firstElement();

		attributeCollectorList = new JComboBox(tempTypeList);
		attributeCollectorList.insertItemAt("", 0);
		attributeCollectorList.setSelectedItem(activeType);

		GridBagConstraints gbc;

		int zeile = 0;
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 5, 10, 5);
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		dialogPanel.add(attributeCollectorList, gbc);

		attributeCollectorList.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{

				if (attributeCollectorList.getSelectedItem() != null
						&& attributeCollectorList.getSelectedItem().equals(""))
				{
					setTableEnabled(false);

					return;
				}

				activeType = (String) attributeCollectorList.getSelectedItem();
				activeAttributeType = (AttributeType) attributeList
						.getSelectedItem();

				for (String tempType : tempTypeList)
				{
					if (!tempType.equals("ACTOR") && !tempType.equals("")) //$NON-NLS-1$
					{
						if (tempType.equals(activeType))
							typePanel.get(tempType).setVisible(true);
						else
							typePanel.get(tempType).setVisible(false);
					}

				}

				if (!activeType.equals(""))
					setTableEnabled(true);
			}
		});

		for (String tempType : tempTypeList)
		{
			if (!tempType.equals("ACTOR") && !tempType.equals("")) //$NON-NLS-1$
			{
				gbc = new GridBagConstraints();
				gbc.gridx = 0;
				gbc.gridy = GridBagConstraints.RELATIVE;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridwidth = 6;
				gbc.gridheight = 7;
				gbc.weightx = 6;
				gbc.weighty = 7;
				dialogPanel.add(typePanel.get(tempType), gbc);
				if (tempType.equals(activeType))
					typePanel.get(tempType).setVisible(true);
				else
					typePanel.get(tempType).setVisible(false);
			}
		}

		if (!VennMaker.getInstance().getProject().getPaintLegendPolicy()
				.paintColor())
		{
			setTableEnabled(false);

			attributeCollectorList.setSelectedIndex(0);
			attributeList.setSelectedIndex(0);
		}
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		if (newestVersion != null)
		
		{
			newestVersion.refreshBeforeGetDialog();
			return;
		}
		updateAttributes(activeType);

		buildPanel();

		/* die aufgelisteten Attribute auf den aktuellen Stand bringen... */
		this.connectColorsAndValues(comboSelected.get(activeType), activeType);

		model.get(activeType).setPredefinedColor(
				selectedColor.get(activeType).get(activeAttributeType.toString()));
		model.get(activeType).setPredefinedValue(
				predefValues.get(activeType).get(activeAttributeType.toString()));

		/* ...und das auch dem Tablemodel mitteilen */
		this.model.get(activeType).fireTableDataChanged();
	}

	private void setTableEnabled(boolean enabled)
	{
		// JTable table = this.tables.get(this.activeType);
		//
		// if (table == null)
		// return;
		//
		// table.setEnabled(enabled);
		// table.setBackground(enabled ? Color.WHITE : Color.LIGHT_GRAY);
		//
		// this.paintColorLegend = enabled;
	}

	/**
	 * Return the panel, to configure the visualization for the chosen
	 * attributecollector
	 * 
	 * @param attributeCollector
	 *           for which attributecollector?
	 * @return returns the panel with the table to configure the visualization
	 */
	private JPanel subDialog(String attributeCollector)
	{
		List<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(attributeCollector);

		PaintLegendPolicy policy = VennMaker.getInstance().getProject()
				.getPaintLegendPolicy();

		// if (policy != null && policy.getColorData() != null)
		// {
		// predefinedColor = (HashMap<String, Vector<Color>>) policy
		// .getColorData().getPredefinedColor();
		// predefinedValue = (HashMap<String, Vector<String>>) policy
		// .getColorData().getPredefiniedValues();
		// }

		for (AttributeType type : aTypes)
		{

			if (type.getPredefinedValues() == null)
				continue;

			if (activeAttributeType == null)
				activeAttributeType = type;

			Vector<Color> colorVector = new Vector<Color>();
			Vector<String> valueVector = new Vector<String>();

			for (Object o : type.getPredefinedValues())
			{
				colorVector.add(getNet().getRelationColorVisualizer(
						attributeCollector, type).getColor(o));
				valueVector.add(o.toString());
			}

			if (selectedColor.get(attributeCollector) == null)
			{

				Map<String, Vector<Color>> colors = new HashMap<String, Vector<Color>>();
				Map<String, Vector<String>> values = new HashMap<String, Vector<String>>();

				colors.put(type.toString(), colorVector);
				values.put(type.toString(), valueVector);

				selectedColor.put(attributeCollector, colors);
				predefValues.put(attributeCollector, values);
			}
			else if (selectedColor.get(attributeCollector).get(type.toString()) == null)
			{
				selectedColor.get(attributeCollector).put(type.toString(),
						colorVector);
				predefValues.get(attributeCollector).put(type.toString(),
						valueVector);
			}
		}

		updateAttributes(attributeCollector);

		JPanel subpanel = new JPanel();

		/**
		 * Für Tabelle: Predefined values mit entsprechenen Farben verbinden
		 */
		if (model.get(attributeCollector) == null)
			model.put(
					attributeCollector,
					new MyTableModel(this.selectedColor.get(attributeCollector)
							.values().iterator().next(), this.predefValues
							.get(attributeCollector).values().iterator().next()));

		table = new JTable(model.get(attributeCollector));
		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(30);
		table.getColumnModel().getColumn(1).setCellEditor(new MyColorBoxEditor());

		TableColumn column = table.getColumnModel().getColumn(1);
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new ColorEditor());

		this.tables.put(attributeCollector, table);

		ColorRenderer cRenderer = new ColorRenderer(true);

		column.setCellRenderer(cRenderer);

		int zeile = 0;

		GridBagLayout layout = new GridBagLayout();
		subpanel.setLayout(layout);

		AttributeType[] aTypesArray = new AttributeType[aTypes.size()];
		aTypes.toArray(aTypesArray);

		AttributeType aSelected = comboSelected.get(attributeCollector);

		if (!aTypes.contains(aSelected) && aTypes.size() > 0)
		{
			aSelected = aTypes.get(0);
			comboSelected.put(attributeCollector, aSelected);
		}

		/*
		 * all atributetypes in the current attributecollector which own
		 * predefined values (to configure the visualization)
		 */
		attributeList = new JComboBox(aTypesArray);
		attributeList.insertItemAt("", 0);
		attributeList.setSelectedItem(comboSelected.get(attributeCollector));
		attributeList.addItemListener(this);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(attributeList, gbc);
		subpanel.add(attributeList);

		zeile++;

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
		subpanel.add(scrollPane);

		return subpanel;
	}

	/**
	 * updates all attributes and the corresponding values of the given
	 * attributeCollector
	 * 
	 * @param atCollector
	 *           the currently chosen attributeCollector
	 */
	private void updateAttributes(String atCollector)
	{
		/* get current attributetypes */
		List<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(atCollector);

		if (aTypes == null || aTypes.size() <= 0)
			return;

		// Falls schon gewaehlt
		if (comboSelected.get(atCollector) != null
				&& predefValues.get(atCollector) != null
				&& selectedColor.get(atCollector) != null
				&& VennMaker.getInstance().getProject()
						.getAttributeTypesDiscrete(atCollector)
						.contains(comboSelected.get(atCollector)))
		{
			// Falls am Attribut was geaendert wurde
			for (int i = 0; i < aTypes.size(); i++)
			{
				AttributeType a = aTypes.get(i);
				if (a.getLabel().equals(comboSelected.get(atCollector).getLabel()))
				{
					if (comboSelected.get(atCollector).getPredefinedValues().length != a
							.getPredefinedValues().length)
					{
						comboSelected.put(atCollector, a);
						this.connectColorsAndValues(comboSelected.get(atCollector),
								atCollector);
					}
					break;
				}
			}
		}
		else
		{
			if (aTypes.contains(net.getActiveRelationColorVisualizer(atCollector)
					.getAttributeType()))
			{
				comboSelected.put(atCollector, net
						.getActiveRelationColorVisualizer(atCollector)
						.getAttributeType());
			}
			else
			{
				comboSelected.put(atCollector, aTypes.get(0));
				this.activeAttributeType = aTypes.get(0);
			}

			this.connectColorsAndValues(comboSelected.get(atCollector),
					atCollector);
		}

	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		if (newestVersion != null)
		{
			return newestVersion.getFinalSetting();
		}
		for (String atCollector : comboSelected.keySet())
			updateAttributes(comboSelected.get(atCollector).getType());

		return new SettingRelationColor(net, predefValues, selectedColor, true);
	}

	class MyTableModel extends AbstractTableModel
	{

		static final long			serialVersionUID	= 1L;

		private Vector<Color>	predefinedColor;

		public void setPredefinedColor(Vector<Color> predefinedColor)
		{
			this.predefinedColor = predefinedColor;
		}

		public void setPredefinedValue(Vector<String> predefinedValue)
		{
			this.predefinedValue = predefinedValue;
		}

		private Vector<String>	predefinedValue;

		public MyTableModel(Vector<Color> predefinedColor,
				Vector<String> predefinedValue)
		{
			this.predefinedColor = predefinedColor;
			this.predefinedValue = predefinedValue;
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public int getRowCount()
		{
			if (predefinedColor != null)
				return this.predefinedColor.size();
			else
				return 0;
		}

		@Override
		public String getColumnName(int col)
		{
			String r;
			switch (col)
			{
				case 0:
					r = Messages.getString("CDialogRelationColor.4"); //$NON-NLS-1$
					break;

				case 1:
					r = Messages.getString("CDialogRelationColor.5"); //$NON-NLS-1$
					break;

				default:
					r = "?"; //$NON-NLS-1$
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
					r = ConfigDialog
							.getElementCaption(this.predefinedValue.get(row));
					break;

				case 1:

					r = this.predefinedColor.get(row);
					break;

				default:
					r = Messages.getString("CDialogActorImage.5"); //$NON-NLS-1$
					break;
			}
			return r;
		}

		@Override
		public Class<?> getColumnClass(int col)
		{
			Class<?> r = null;
			switch (col)
			{
				case 0:
					r = String.class;
					break;
				case 1:
					r = Integer.class;
					break;

			}
			return r;
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			switch (col)
			{
				case 0:
					return false;
				default:
					return true;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			Color v = (Color) value;
			this.predefinedColor.set(row, v);
		}
	}

	class MyColorBoxEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener
	{
		/**
		 * 
		 */
		private static final long		serialVersionUID	= 1L;

		Color									currentColor;

		JButton								button;

		JColorChooser						colorChooser;

		JDialog								dialog;

		protected static final String	EDIT					= "edit";	//$NON-NLS-1$

		public MyColorBoxEditor()
		{
			button = new JButton();
			button.setActionCommand(EDIT);
			button.addActionListener(this);
			button.setBorderPainted(false);

			// Set up the dialog that the button brings up.
			colorChooser = new JColorChooser();
			dialog = JColorChooser.createDialog(button, "Pick a Color", true, // modal //$NON-NLS-1$
					colorChooser, this, // OK button handler
					null); // no CANCEL button handler
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (EDIT.equals(e.getActionCommand()))
			{
				// The user has clicked the cell, so
				// bring up the dialog.
				button.setBackground(currentColor);
				colorChooser.setColor(currentColor);
				dialog.setVisible(true);

				fireEditingStopped(); // Make the renderer reappear.

			}
			else
			{ // User pressed dialog's "OK" button.
				currentColor = colorChooser.getColor();
			}
		}

		// Implement the one CellEditor method that AbstractCellEditor doesn't.
		@Override
		public Object getCellEditorValue()
		{
			return currentColor;
		}

		// Implement the one method defined by TableCellEditor.
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column)
		{
			currentColor = (Color) value;
			return button;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent ev)
	{
		JComboBox cb = (JComboBox) ev.getSource();

		if (cb.getSelectedItem() instanceof String)
		{
			setTableEnabled(false);
			return;
		}

		if (!this.table.isEnabled())
			setTableEnabled(true);

		activeAttributeType = (AttributeType) cb.getSelectedItem();

		comboSelected.put(activeType, (AttributeType) cb.getSelectedItem());
		this.connectColorsAndValues(comboSelected.get(activeType), activeType);
		model.get(activeType).setPredefinedColor(
				selectedColor.get(activeType).get(activeAttributeType.toString()));
		model.get(activeType).setPredefinedValue(
				predefValues.get(activeType).get(activeAttributeType.toString()));
		/*
		 * FacadeVisualizer f = new FacadeVisualizer();
		 * f.newVisualizerAttributeType(((AttributeType) cb.getSelectedItem()),
		 * network, data.FacadeVisualizer.Visualization.SYMBOL);
		 */
		this.model.get(activeType).fireTableDataChanged();
	}

	/**
	 * Predefined values mit entsprechenen Relationsfarben verbinden
	 * 
	 * @param at
	 *           : AttributeType
	 */
	private void connectColorsAndValues(AttributeType at, String atCollector)
	{
		if (this.attributeTypeCache == null)
			this.attributeTypeCache = new ConfigDialogRelationCache(net,
					Color.class);

		this.attributeTypeCache.update(at);

		Vector<Color> colors = this.selectedColor.get(atCollector).get(
				at.toString());
/*		Vector<String> values = this.predefValues.get(atCollector).get(
				at.toString());
	*/	
		Vector<String> names = this.attributeTypeCache.getNames(at);


		if (colors == null || colors.size() != names.size())
		{
			this.selectedColor.get(atCollector).put(at.toString(),
					this.attributeTypeCache.getValues(at));
			this.predefValues.get(atCollector).put(at.toString(),
					this.attributeTypeCache.getNames(at));
		}
		else
		{
			this.selectedColor.get(atCollector).put(at.toString(), colors);
			this.predefValues.get(atCollector).put(at.toString(), names);
		}
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
		// TODO Auto-generated method stub

	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (newestVersion != null)
		{
			newestVersion.setAttributesFromSetting(setting);
			return;
		}
		if (!(setting instanceof RelationColorSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		RelationColorSaveElement element = (RelationColorSaveElement) setting;

		if (element.getOldSelectedColors() != null
				&& element.getOldSelectedType() != null)
		{
			/**
			 * An old Template has been loaded
			 */

			AttributeType selectedType = null;

			for (AttributeType type : VennMaker.getInstance().getProject()
					.getAttributeTypes(element.getActiveType()))
			{
				if (type.toString().equals(element.getOldSelectedType()))
				{
					selectedType = type;
					break;
				}
			}

			if (selectedType == null || selectedType.getPredefinedValues() == null)
				return;

			Object[] preVals = selectedType.getPredefinedValues();

			Map<String, Vector<Color>> colors = new HashMap<String, Vector<Color>>();
			Map<String, Vector<String>> preValues = new HashMap<String, Vector<String>>();

			Vector<String> valueNames = new Vector<String>();

			for (Object obj : preVals)
				valueNames.add(obj.toString());

			preValues.put(element.getOldSelectedType(), valueNames);
			colors.put(element.getOldSelectedType(),
					new Vector<Color>(Arrays.asList(element.getOldSelectedColors())));

			this.selectedColor.put(element.getActiveType(), colors);
			this.predefValues.put(element.getActiveType(), preValues);

		}
		else
		{
			this.selectedColor = element.getSelectedColor();
			this.predefValues = element.getPredefValues();
		}
	}

	@Override
	public SaveElement getSaveElement()
	{
		if (newestVersion != null)
		{
			return newestVersion.getSaveElement();
		}
		RelationColorSaveElement element = new RelationColorSaveElement(
				predefValues, selectedColor);
		element.setElementName(this.getClass().getSimpleName());

		return element;
	}
}
