/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.PaintLegendPolicy;
import gui.VennMaker;
import gui.VennMakerView;
import gui.configdialog.AuxiliaryFunctions;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogRelationCache;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.RelationSizeSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingRelationSize;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import data.AttributeType;

/**
 * Dialog zum verknuepfen eines Attributwertes mit der Linienstaerke einer
 * Relation.
 * 
 * 
 */
public class CDialogRelationSize extends ConfigDialogElement implements
		ItemListener
{
	private static final long									serialVersionUID			= 1L;

	private JTable													table;

	private JComboBox												attributeCollectorList;

	private String													activeType					= "STANDARDRELATION";												//$NON-NLS-1$

	private Vector<String>										tempTypeList;

	private JComboBox												attributeList;

	private HashMap<String, MyTableModel>					model							= new HashMap<String, MyTableModel>();

	private HashMap<String, JPanel>							typePanel					= new HashMap<String, JPanel>();

	private HashMap<String, AttributeType>					comboSelected				= new HashMap<String, AttributeType>();

	private Map<String, Map<String, Vector<Integer>>>	selectedSizes				= new HashMap<String, Map<String, Vector<Integer>>>();

	private Map<String, Map<String, Vector<String>>>	predefValues				= new HashMap<String, Map<String, Vector<String>>>();

	private AttributeType										activeAttributeType;

	private Map<String, JTable>								tables						= new HashMap<String, JTable>();

	private ConfigDialogRelationCache<Integer>			attributeTypeCache;

	private boolean												performRefreshBeforeGet	= true;

	private boolean												paintSizeLegend			= true;

	@Override
	public void buildPanel()
	{
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
				.paintThickness())
		{
			setTableEnabled(false);

			attributeCollectorList.setSelectedIndex(0);
			attributeList.setSelectedIndex(0);
		}

	}

	@Override
	public void refreshBeforeGetDialog()
	{
		if (performRefreshBeforeGet)
		{
			updateAttributes(activeType);
			buildPanel();

			this.connectSizesAndValues(comboSelected.get(activeType), activeType);

			model.get(activeType).setPredefinedSizes(
					this.selectedSizes.get(activeType).get(
							activeAttributeType.toString()));
			model.get(activeType).setPredefinedValue(
					this.predefValues.get(activeType).get(
							activeAttributeType.toString()));

			this.model.get(activeType).fireTableDataChanged();

		}

		performRefreshBeforeGet = true;
	}

	private JPanel subDialog(String attributeCollector)
	{
		List<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(attributeCollector);

		PaintLegendPolicy policy = VennMaker.getInstance().getProject()
				.getPaintLegendPolicy();

		// if (policy != null && policy.getSizeData() != null)
		// {
		// predefinedSizes = (HashMap<String, Vector<Integer>>) policy
		// .getSizeData().getPredefinedSizes();
		// predefinedValue = (HashMap<String, Vector<String>>) policy
		// .getSizeData().getPredefinedValue();
		// }

		JPanel subpanel = new JPanel();

		for (AttributeType type : aTypes)
		{

			if (type.getPredefinedValues() == null
					|| type.getPredefinedValues().length == 0)
				continue;

			if (activeAttributeType == null)
				activeAttributeType = type;

			Vector<Integer> sizes = new Vector<Integer>();
			Vector<String> values = new Vector<String>();

			for (Object o : type.getPredefinedValues())
			{
				sizes.add(this.getNet()
						.getRelationSizeVisualizer(attributeCollector, type)
						.getSize(o));
				values.add(o.toString());
			}

			if (selectedSizes.get(attributeCollector) == null)
			{
				Map<String, Vector<Integer>> sizesMap = new HashMap<String, Vector<Integer>>();
				sizesMap.put(type.toString(), sizes);

				Map<String, Vector<String>> valuesMap = new HashMap<String, Vector<String>>();
				valuesMap.put(type.toString(), values);

				selectedSizes.put(attributeCollector, sizesMap);
				predefValues.put(attributeCollector, valuesMap);
			}
			else if (selectedSizes.get(attributeCollector).get(type.toString()) == null)
			{
				selectedSizes.get(attributeCollector).put(type.toString(), sizes);
				predefValues.get(attributeCollector).put(type.toString(), values);
			}

			if (model.get(attributeCollector) == null)
			{
				model.put(attributeCollector, new MyTableModel(this.selectedSizes
						.get(attributeCollector).get(type.toString()),
						this.predefValues.get(attributeCollector)
								.get(type.toString())));
			}
		}

		updateAttributes(attributeCollector);
		/**
		 * Für Tabelle: Predefined values mit entsprechenen Dicken verbinden
		 */

		table = new JTable(model.get(attributeCollector));
		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(30);

		this.tables.put(attributeCollector, table);

		/* Die voreingestellten Liniendicken, die verfügbar sein sollen */
		Integer[] sizeArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
				15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 };
		JComboBox relationSizeComboBox = new JComboBox(sizeArray);

		/* einstellen des unten beschriebenen RelationSizeRenderers */
		relationSizeComboBox.setRenderer(new RelationSizeRenderer(5));

		TableColumn column = table.getColumnModel().getColumn(1);
		column.setCellEditor(new DefaultCellEditor(relationSizeComboBox));
		column.setCellRenderer(new RelationSizeRenderer(5));

		int zeile = 0;

		GridBagLayout layout = new GridBagLayout();
		subpanel.setLayout(layout);

		AttributeType[] aTypesArray = new AttributeType[aTypes.size()];
		aTypes.toArray(aTypesArray);

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

	private void updateAttributes(String atCollector)
	{
		List<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(atCollector);

		if (aTypes == null || aTypes.size() <= 0)
			return;
		// Falls schon gewählt
		if (comboSelected.get(atCollector) != null
				&& predefValues.get(atCollector) != null
				&& selectedSizes.get(atCollector) != null
				&& VennMaker.getInstance().getProject()
						.getAttributeTypesDiscrete(atCollector)
						.contains(comboSelected.get(atCollector)))
		{
			// Falls am Attribut was geändert wurde
			for (int i = 0; i < aTypes.size(); i++)
			{
				AttributeType a = aTypes.get(i);
				if (a.getLabel().equals(comboSelected.get(atCollector).getLabel()))
				{
					if (comboSelected.get(atCollector).getPredefinedValues().length != a
							.getPredefinedValues().length)
					{
						comboSelected.put(atCollector, a);
						this.connectSizesAndValues(comboSelected.get(atCollector),
								comboSelected.get(atCollector).getType());
					}
					break;
				}
			}
		}
		else
		{
			if (aTypes.contains(net.getActiveRelationSizeVisualizer(atCollector)
					.getAttributeType()))
			{
				comboSelected.put(atCollector,
						net.getActiveRelationSizeVisualizer(atCollector)
								.getAttributeType());
			}

			else
			{
				comboSelected.put(atCollector, aTypes.get(0));
				this.activeAttributeType = aTypes.get(0);
			}

			this.connectSizesAndValues(comboSelected.get(atCollector), atCollector);
		}
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		for (String atCollector : comboSelected.keySet())
			updateAttributes(comboSelected.get(atCollector).getType());

		return new SettingRelationSize(net, predefValues, selectedSizes, true);
	}

	class MyTableModel extends AbstractTableModel
	{

		static final long			serialVersionUID	= 1L;

		private Vector<Integer>	predefinedSizes;

		public void setPredefinedSizes(Vector<Integer> predefinedSizes)
		{
			this.predefinedSizes = predefinedSizes;
		}

		public void setPredefinedValue(Vector<String> predefinedValue)
		{
			this.predefinedValue = predefinedValue;
		}

		private Vector<String>	predefinedValue;

		public MyTableModel(Vector<Integer> predefinedSizes,
				Vector<String> predefinedValue)
		{
			this.predefinedSizes = predefinedSizes;
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
			if (this.predefinedSizes != null)
				return this.predefinedSizes.size();
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
					r = Messages.getString("CDialogRelationSize.3"); //$NON-NLS-1$
					break;

				case 1:
					r = Messages.getString("CDialogRelationSize.4"); //$NON-NLS-1$
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

					r = this.predefinedSizes.get(row);
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
			int v = (Integer) value;
			this.predefinedSizes.set(row, v);
		}

	}

	private void setTableEnabled(boolean enabled)
	{
		JTable table = this.tables.get(this.activeType);

		if (table == null)
			return;

		table.setEnabled(enabled);
		table.setBackground(enabled ? Color.WHITE : Color.LIGHT_GRAY);

		this.paintSizeLegend = enabled;
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

		if (cb.getSelectedItem().equals(""))
		{
			this.table.setEnabled(false);
			this.table.setBackground(Color.LIGHT_GRAY);

			this.paintSizeLegend = false;

			return;
		}

		// if(!this.table.isEnabled())
		// {
		// this.table.setEnabled(true);
		// this.table.setBackground(Color.WHITE);
		//
		// this.paintSizeLegend = true;
		// }

		activeAttributeType = (AttributeType) cb.getSelectedItem();

		comboSelected.put(activeType, (AttributeType) cb.getSelectedItem());
		this.connectSizesAndValues(comboSelected.get(activeType), activeType);

		model.get(activeType).setPredefinedSizes(
				selectedSizes.get(activeType).get(activeAttributeType.toString()));
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
	 * Predefined values mit entsprechenen Liniendicken verbinden
	 * 
	 * @param at
	 *           : AttributeType
	 */
	private void connectSizesAndValues(AttributeType at, String atCollector)
	{
		if (this.attributeTypeCache == null)
			this.attributeTypeCache = new ConfigDialogRelationCache(net,
					Integer.class);
		this.attributeTypeCache.update(at);

		Vector<Integer> sizes = this.selectedSizes.get(atCollector).get(
				at.toString());
		Vector<String> values = this.predefValues.get(atCollector).get(
				at.toString());

		if (sizes == null || sizes.size() != values.size())
		{
			this.selectedSizes.get(atCollector).put(at.toString(),
					this.attributeTypeCache.getValues(at));
			this.predefValues.get(atCollector).put(at.toString(),
					this.attributeTypeCache.getNames(at));
		}
		else
		{
			this.selectedSizes.get(atCollector).put(at.toString(), sizes);
			this.predefValues.get(atCollector).put(at.toString(), values);
		}

	}

	/**
	 * Renderer zum Darstellung von Linien in der Combobox. zusätzlich
	 * Darstellung der aktuell eingestellten Liniendicke als Text neben der
	 * entsprechenden Linie
	 * 
 * 
	 * 
	 */
	public class RelationSizeRenderer extends JComponent implements
			ListCellRenderer, TableCellRenderer
	{
		static final long	serialVersionUID	= 1L;

		private int			relationSize;

		private Dimension	preferredSize;

		public RelationSizeRenderer(final int relationSize)
		{
			this.relationSize = relationSize;
			this.preferredSize = new Dimension(80, 18);
		}

		/**
		 * returns the size of the relation
		 */
		public int getRelationSize()
		{
			return this.relationSize;
		}

		/**
		 * use this to set the relation size and update the component
		 */
		public void setRelationSize(final int relationSize)
		{
			this.relationSize = relationSize;
			repaint();
		}

		@Override
		public Dimension getPreferredSize()
		{
			return this.preferredSize;
		}

		@Override
		public void paintComponent(final Graphics g)
		{
			final Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			final Dimension size = getSize();
			final Insets insets = getInsets();
			final double xx = insets.left;
			final double yy = insets.top;
			final double ww = size.getWidth() - insets.left - insets.right;
			final double hh = size.getHeight() - insets.top - insets.bottom;

			final Point2D one = new Point2D.Double(xx + 6, yy + hh / 2);
			final Point2D two = new Point2D.Double(xx + ww - 30, yy + hh / 2);

			// draw a line connecting the points
			final Line2D line = new Line2D.Double(one, two);
			g2.setStroke(new BasicStroke(VennMakerView.getVmcs().toJava2D(
					(float) this.relationSize / VennMakerView.LINE_WIDTH_SCALE),
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10));
			g2.draw(line);
			g2.drawString(Integer.toString(this.relationSize),
					(int) (xx + ww - 20), (int) (yy + hh / 2));
		}

		@Override
		public Component getListCellRendererComponent(final JList list,
				final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus)
		{
			if (value instanceof Integer)
				setRelationSize(Integer.parseInt(value.toString()));
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent
		 * (javax .swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			if (value instanceof Integer)
				setRelationSize(Integer.parseInt(value.toString()));
			return this;
		}
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof RelationSizeSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		RelationSizeSaveElement element = (RelationSizeSaveElement) setting;

		if (element.getOldSelectedSize() != null
				&& element.getOldSelectedType() != null)
		{
			/**
			 * An old template has been loaded
			 */

			Vector<Integer> sizes = new Vector<Integer>();
			int[] selected = element.getOldSelectedSize();

			for (int i = 0; i < selected.length; i++)
				sizes.add(selected[i]);

			Map<String, Vector<Integer>> sizesForAttribute = new HashMap<String, Vector<Integer>>();
			Map<String, Vector<String>> predefValues = new HashMap<String, Vector<String>>();

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

			if (selectedType == null)
				return;

			Vector<String> predefValueList = new Vector<String>();

			for (Object o : selectedType.getPredefinedValues())
				predefValueList.add(o.toString());

			sizesForAttribute.put(selectedType.toString(), sizes);
			predefValues.put(selectedType.toString(), predefValueList);

			this.selectedSizes.put(element.getActiveType(), sizesForAttribute);
			this.predefValues.put(element.getActiveType(), predefValues);
		}
		else
		{
			this.selectedSizes = element.getSizes();
			this.predefValues = element.getPredefValues();
		}
	}

	@Override
	public SaveElement getSaveElement()
	{
		RelationSizeSaveElement element = new RelationSizeSaveElement(
				selectedSizes, predefValues);
		element.setElementName(getClass().getSimpleName());

		return element;
	}
}
