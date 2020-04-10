package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import data.AttributeType;

/**
 * Class representing an inner table in configdialogs for attribute
 * representation
 * 
 * 
 * 
 */
public class ConfigCellInnerTable extends JTable
{

	private static final long	serialVersionUID	= 1L;

	ConfigData						internalData;

	ConfigCellInnerTableModel	model;

	AttributeType					activeSelectedAttribute;

	public ConfigCellInnerTable()
	{

		setModel(model = new ConfigCellInnerTableModel());
		activeSelectedAttribute = null;
	}

	public void updateIfRequired(ConfigData internalData)
	{
		// no update required
		if (internalData == this.internalData)
			return;

		this.internalData = internalData;
		if (this.internalData.attributeSettings
				.containsKey(activeSelectedAttribute))
		{
			model.linkInternalData(this.internalData.attributeSettings
					.get(activeSelectedAttribute));
		}

	}

	public void setActiveSelectedAttribute(AttributeType attribName)
	{
		if (activeSelectedAttribute == attribName)
			return;
		// TODO: determinate if using AttributeType instead of their name
		activeSelectedAttribute = attribName;
		// for (Object o : this.internalData.attributeSettings.keySet())
		// {
		// System.out.println(o.getClass() + " ? " + attribName.getClass());
		// System.out.println(o.equals(attribName));
		// }
		if (this.internalData.attributeSettings
				.containsKey(activeSelectedAttribute))
			model.linkInternalData(this.internalData.attributeSettings
					.get(activeSelectedAttribute));

	}

	public class ConfigCellInnerTableModel extends AbstractTableModel
	{

		private static final long	serialVersionUID	= 1L;

		String[]							keys;

		Object[]							values;

		Class<?>							castType;

		public void linkInternalData(ConfigData.Internal data)
		{
			// ensure correct bindings
			this.keys = data.keys;
			this.values = data.values;
			castType = data.type;

			setRendererAndEditorByType(castType);

			fireTableDataChanged();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex == 1;
		}

		@Override
		public String getColumnName(int column)
		{
			return "";
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return String.class;
				case 1:
					return castType;
				default:
					return super.getColumnClass(columnIndex);
			}
		}

		public ConfigCellInnerTableModel()
		{
			keys = null;
			values = null;
			this.castType = null;

		}

		public int getRowCount()
		{
			return keys != null ? keys.length : 0;
		}

		public int getColumnCount()
		{
			return 2;
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{

			switch (columnIndex)
			{
				case 0:
					return (keys == null || rowIndex > keys.length) ? null
							: keys[rowIndex];
				case 1:
					return (values == null || rowIndex > values.length) ? null
							: values[rowIndex];
				default:
					return null;
			}
		}

		protected void setRendererAndEditorByType(Class<?> type)
		{

			if (type == Color.class)
			{
				setDefaultRenderer(type, new ConfigCellInnerTable.ColorRenderer(
						true));
				setDefaultEditor(type, new ColorEditor());
				return;
			}
			if (type == Integer.class)
			{
				/* Die voreingestellten Liniendicken, die verfgbar sein sollen */
				Integer[] sizeArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
						14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28,
						29, 30 };
				JComboBox<Integer> relationSizeComboBox = new JComboBox<Integer>(
						sizeArray);

				/* einstellen des unten beschriebenen RelationSizeRenderers */
				RelationSizeRenderer rsr = new RelationSizeRenderer(5);
				relationSizeComboBox.setRenderer(rsr);

				setDefaultEditor(type, new DefaultCellEditor(relationSizeComboBox));

				setDefaultRenderer(type, rsr);
				return;
			}
			if (type == float[].class)
			{
				float[][] dashArrays = { { 1.0f, 0.f }, { 1.0f, 3.0f },
						{ 5.0f, 5.0f }, { 10.0f, 10.0f }, { 20.0f, 10.0f },
						{ 20.0f, 20.0f }, { 25.0f, 9.0f, 2.0f, 9.0f },
						{ 25.0f, 9.0f, 2.0f, 9.0f, 2.0f, 9.0f },
						{ 25.0f, 9.0f, 2.0f, 9.0f, 2.0f, 9.0f, 2.0f, 9.0f },
						{ 25.0f, 9.0f, 5.0f, 9.0f } };

				final JComboBox dashTypeComboBox = new JComboBox(dashArrays);

				dashTypeComboBox.setRenderer(new DashTypeRenderer(new float[] {
						1.0f, 0.f }));

				dashTypeComboBox.addItemListener(new ItemListener()
				{

					@Override
					public void itemStateChanged(ItemEvent ev)
					{
						// if (ev.getStateChange() == ItemEvent.DESELECTED)
						// {
						// int row = tables.get(activeType).getSelectedRow();
						//
						// Vector<float[]> currentDashing = selectedDashing.get(
						// activeType).get(activeAttributeType.toString());
						//
						// currentDashing.remove(row);
						// currentDashing.add(
						// tables.get(activeType).getSelectedRow(),
						// (float[]) dashTypeComboBox.getSelectedItem());
						//
						// System.out.println("DEBUG_STOP");
						// }
					}
				});

				DashTypeRenderer rdr = new DashTypeRenderer(
						new float[] { 1.0f, 0.f });
				setDefaultRenderer(type, rdr);
				setDefaultEditor(type, new DefaultCellEditor(dashTypeComboBox));
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (castType == null || !castType.isInstance(aValue))
				return;

			values[rowIndex] = castType.cast(aValue);
			// super.setValueAt(aValue, rowIndex, columnIndex);
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

	/**
	 * Renderer zum Darstellung von Linien in der Combobox. zustzlich
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
			g2.setStroke(new BasicStroke((float) this.relationSize,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10));
			g2.draw(line);
			g2.drawString(Integer.toString(this.relationSize),
					(int) (xx + ww - 20), (int) (yy + hh));

		}

		public Component getListCellRendererComponent(final JList list,
				final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus)
		{
			if (value instanceof Integer)
				setRelationSize(Integer.parseInt(value.toString()));
			list.setFixedCellHeight(this.preferredSize.height);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent
		 * (javax .swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			if (value instanceof Integer)
				setRelationSize(Integer.parseInt(value.toString()));

			// table.setRowHeight(row, this.getPreferredSize().height * 2);
			return this;
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

}
