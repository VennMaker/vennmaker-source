/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.PaintLegendPolicy;
import gui.VennMaker;
import gui.configdialog.AuxiliaryFunctions;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogRelationCache;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.RelationDashSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingRelationDash;

import java.awt.BasicStroke;
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
import java.lang.reflect.Array;
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
 * Dialog zum verknuepfen eines Attributwertes mit der Linienart einer Relation.
 * 
 * 
 */
public class CDialogRelationDash extends ConfigDialogElement implements
		ItemListener
{
	private static final long									serialVersionUID		= 1L;

	private JTable													table;

	private JComboBox												attributeCollectorList;

	private String													activeType				= "STANDARDRELATION";												//$NON-NLS-1$

	private AttributeType										activeAttributeType;

	private Vector<String>										tempTypeList;

	private HashMap<String, JPanel>							typePanel;

	private JComboBox												attributeList;

	private HashMap<String, MyTableModel>					model						= new HashMap<String, MyTableModel>();

	private Map<String, JTable>								tables					= new HashMap<String, JTable>();

	private Map<String, Map<String, Vector<float[]>>>	selectedDashing		= new HashMap<String, Map<String, Vector<float[]>>>();

	private Map<String, Map<String, Vector<String>>>	predefValues			= new HashMap<String, Map<String, Vector<String>>>();

	private HashMap<String, AttributeType>					comboSelected			= new HashMap<String, AttributeType>();

	private ConfigDialogRelationCache<float[]>			attributeTypeCache;

	private boolean												paintDashingLegend	= true;

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
		attributeCollectorList = new JComboBox(tempTypeList);

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
				.paintDashing())
		{
			setTableEnabled(false);

			attributeCollectorList.setSelectedIndex(0);
			attributeList.setSelectedIndex(0);
		}
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		updateAttributes(activeType);

		buildPanel();

		this.connectSizesAndValues(comboSelected.get(activeType), activeType);

		model.get(activeType).setPredefinedDashing(
				this.selectedDashing.get(activeType).get(
						activeAttributeType.toString()));
		model.get(activeType).setPredefinedValue(
				this.predefValues.get(activeType).get(
						activeAttributeType.toString()));

		this.model.get(activeType).fireTableDataChanged();
	}

	private JPanel subDialog(final String attributeCollector)
	{
		List<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(attributeCollector);

		PaintLegendPolicy policy = VennMaker.getInstance().getProject()
				.getPaintLegendPolicy();

		// if (policy != null && policy.getDashingData() != null)
		// {
		// predefinedDashing = (HashMap<String, Vector<float[]>>) policy
		// .getDashingData().getDashing();
		// predefinedValue = (HashMap<String, Vector<String>>) policy
		// .getDashingData().getValues();
		// }

		/**
		 * Für Tabelle: Predefined values mit entsprechenen "Strichmustern"
		 * verbinden
		 */

		for (AttributeType type : VennMaker.getInstance().getProject()
				.getAttributeTypes(attributeCollector))
		{

			if (type.getPredefinedValues() == null
					|| type.getPredefinedValues().length == 0)
				continue;

			if (activeAttributeType == null)
				activeAttributeType = type;

			Vector<float[]> dashing = new Vector<float[]>();
			Vector<String> values = new Vector<String>();

			for (Object o : type.getPredefinedValues())
			{
				dashing.add(this.getNet()
						.getRelationDashVisualizer(attributeCollector, type)
						.getDasharray(o));
				values.add(o.toString());
			}

			if (selectedDashing.get(attributeCollector) == null)
			{
				Map<String, Vector<float[]>> dashingMap = new HashMap<String, Vector<float[]>>();
				dashingMap.put(type.toString(), dashing);

				Map<String, Vector<String>> valuesMap = new HashMap<String, Vector<String>>();
				valuesMap.put(type.toString(), values);

				selectedDashing.put(attributeCollector, dashingMap);
				predefValues.put(attributeCollector, valuesMap);
			}
			else if (selectedDashing.get(attributeCollector).get(type.toString()) == null)
			{
				selectedDashing.get(attributeCollector).put(type.toString(),
						dashing);
				predefValues.get(attributeCollector).put(type.toString(), values);
			}

			if (model.get(attributeCollector) == null)
			{
				model.put(attributeCollector, new MyTableModel(this.selectedDashing
						.get(attributeCollector).get(type.toString()),
						this.predefValues.get(attributeCollector)
								.get(type.toString())));
			}
		}

		updateAttributes(attributeCollector);

		JPanel subpanel = new JPanel();

		table = new JTable(model.get(attributeCollector));
		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(30);

		tables.put(attributeCollector, table);

		float[][] dashArrays = { { 1.0f, 0.f }, { 1.0f, 3.0f }, { 5.0f, 5.0f },
				{ 10.0f, 10.0f }, { 20.0f, 10.0f }, { 20.0f, 20.0f },
				{ 25.0f, 9.0f, 2.0f, 9.0f },
				{ 25.0f, 9.0f, 2.0f, 9.0f, 2.0f, 9.0f },
				{ 25.0f, 9.0f, 2.0f, 9.0f, 2.0f, 9.0f, 2.0f, 9.0f },
				{ 25.0f, 9.0f, 5.0f, 9.0f } };

		final JComboBox dashTypeComboBox = new JComboBox(dashArrays)
		{
			public String toString()
			{
				return "DashBoxes for " + attributeCollector;
			}
		};

		dashTypeComboBox.setRenderer(new DashTypeRenderer(
				new float[] { 1.0f, 0.f }));

		dashTypeComboBox.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent ev)
			{
				if (ev.getStateChange() == ItemEvent.DESELECTED)
				{
					int row = tables.get(activeType).getSelectedRow();

					Vector<float[]> currentDashing = selectedDashing.get(activeType)
							.get(activeAttributeType.toString());

					currentDashing.remove(row);
					currentDashing.add(tables.get(activeType).getSelectedRow(),
							(float[]) dashTypeComboBox.getSelectedItem());

					System.out.println("DEBUG_STOP");
				}
			}
		});

		TableColumn column = table.getColumnModel().getColumn(1);
		column.setCellEditor(new DefaultCellEditor(dashTypeComboBox));
		column.setCellRenderer(new DashTypeRenderer(new float[] { 1.0f, 0.f }));

		int zeile = 0;

		GridBagLayout layout = new GridBagLayout();
		subpanel.setLayout(layout);

		AttributeType[] aTypesArray = new AttributeType[aTypes.size()];
		aTypes.toArray(aTypesArray);

		attributeList = new JComboBox(aTypesArray);

		AttributeType aSelected = comboSelected.get(attributeCollector);

		if (!aTypes.contains(aSelected) && aTypes.size() > 0)
		{
			aSelected = aTypes.get(0);
			comboSelected.put(attributeCollector, aSelected);
		}

		attributeList.insertItemAt("", 0);
		attributeList.setSelectedItem(aSelected);
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
				&& selectedDashing.get(atCollector) != null
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
			if (aTypes.contains(net.getActiveRelationDashVisualizer(atCollector)
					.getAttributeType()))
			{
				comboSelected.put(atCollector,
						net.getActiveRelationDashVisualizer(atCollector)
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

		return new SettingRelationDash(selectedDashing, predefValues, net);

	}

	class MyTableModel extends AbstractTableModel
	{

		static final long			serialVersionUID	= 1L;

		private Vector<float[]>	predefinedDashing;

		public void setPredefinedDashing(Vector<float[]> predefinedDashing)
		{
			this.predefinedDashing = predefinedDashing;
		}

		public void setPredefinedValue(Vector<String> predefinedValue)
		{
			this.predefinedValue = predefinedValue;
		}

		private Vector<String>	predefinedValue;

		public MyTableModel(Vector<float[]> predefinedDashing,
				Vector<String> predefinedValue)
		{
			this.predefinedDashing = predefinedDashing;
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
			if (this.predefinedDashing != null)
				return this.predefinedDashing.size();
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
					r = Messages.getString("CDialogRelationDash.3"); //$NON-NLS-1$
					break;

				case 1:
					r = Messages.getString("CDialogRelationDash.4"); //$NON-NLS-1$
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

					r = this.predefinedDashing.get(row);
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
			float[] v = (float[]) value;
			this.predefinedDashing.set(row, v);
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
		{
			setTableEnabled(true);
		}

		activeAttributeType = (AttributeType) cb.getSelectedItem();

		comboSelected.put(activeType, (AttributeType) cb.getSelectedItem());
		this.connectSizesAndValues(comboSelected.get(activeType), activeType);

		model.get(activeType)
				.setPredefinedDashing(
						selectedDashing.get(activeType).get(
								activeAttributeType.toString()));
		model.get(activeType).setPredefinedValue(
				predefValues.get(activeType).get(activeAttributeType.toString()));
		/*
		 * FacadeVisualizer f = new FacadeVisualizer();
		 * f.newVisualizerAttributeType(((AttributeType) cb.getSelectedItem()),
		 * network, data.FacadeVisualizer.Visualization.SYMBOL);
		 */
		this.model.get(activeType).fireTableDataChanged();

	}

	private void setTableEnabled(boolean enabled)
	{

		// JTable table = this.tables.get(this.activeType);
		//
		// if(table == null)
		// return;
		//
		// table.setEnabled(enabled);
		// table.setBackground(enabled ? Color.WHITE : Color.LIGHT_GRAY);
		//
		// this.paintDashingLegend = enabled;
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
					Array.class);
		this.attributeTypeCache.update(at);

		Vector<float[]> dashing = this.selectedDashing.get(atCollector).get(
				at.toString());
		Vector<String> names = this.attributeTypeCache.getNames(at);

		if (dashing == null || dashing.size() != names.size())
		{
			this.selectedDashing.get(atCollector).put(at.toString(),
					this.attributeTypeCache.getValues(at));
			this.predefValues.get(atCollector).put(at.toString(),
					this.attributeTypeCache.getNames(at));
		}
		else
		{
			this.selectedDashing.get(atCollector).put(at.toString(), dashing);

			this.predefValues.get(atCollector).put(at.toString(), names);
		}
	}

	public class DashTypeRenderer extends JComponent implements
			ListCellRenderer, TableCellRenderer
	{
		static final long	serialVersionUID	= 1L;

		private float[]	dashArray;

		private Dimension	preferredSize;

		public DashTypeRenderer(final float[] dashArray)
		{
			this.dashArray = dashArray;
			this.preferredSize = new Dimension(80, 18);
		}

		/**
		 * returns an array which describes the choosen dash
		 */
		public float[] getDashArray()
		{
			return this.dashArray;
		}

		/**
		 * use this to set the dash
		 */
		public void setDashArray(final float[] dashArray)
		{
			this.dashArray = dashArray;
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
			final Point2D two = new Point2D.Double(xx + ww - 6, yy + hh / 2);

			// draw a line connecting the points
			final Line2D line = new Line2D.Double(one, two);
			g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10, this.dashArray, 0));
			g2.draw(line);
		}

		@Override
		public Component getListCellRendererComponent(final JList list,
				final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus)
		{
			if (value instanceof float[])
				setDashArray((float[]) value);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax
		 * .swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			if (value instanceof float[])
				setDashArray((float[]) value);
			return this;
		}
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof RelationDashSaveElement))
			return;
		if (dialogPanel == null)
			buildPanel();

		RelationDashSaveElement element = (RelationDashSaveElement) setting;

		if (element.getOldPredefinedDashing() != null
				&& element.getOldPredefinedValues() != null)
		{
			/**
			 * An old template has been loaded. So we have to convert the old
			 * template properties to the new
			 */

			Map<String, AttributeType> selectedAttributes = element
					.getComboSelected();

			for (String attributeCollector : selectedAttributes.keySet())
			{
				Map<String, Vector<float[]>> dashing = new HashMap<String, Vector<float[]>>();
				dashing.put(selectedAttributes.get(attributeCollector).toString(),
						element.getOldPredefinedDashing().get(attributeCollector));

				Map<String, Vector<String>> predefValues = new HashMap<String, Vector<String>>();
				predefValues.put(selectedAttributes.get(attributeCollector)
						.toString(),
						element.getOldPredefinedValues().get(attributeCollector));

				this.selectedDashing.put(attributeCollector, dashing);
				this.predefValues.put(attributeCollector, predefValues);
			}
		}
		else
		{
			this.selectedDashing = element.getDashing();
			this.predefValues = element.getPredefValues();
		}

		this.comboSelected = element.getComboSelected();
		this.activeAttributeType = this.comboSelected.get(activeType);

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		ConfigDialogTempCache.getInstance().addSetting(getFinalSetting());
	}

	@Override
	public SaveElement getSaveElement()
	{

		RelationDashSaveElement elem = new RelationDashSaveElement(
				this.selectedDashing, this.predefValues, this.comboSelected,
				getNet());
		elem.setElementName(this.getClass().getSimpleName());

		return elem;
	}
}
