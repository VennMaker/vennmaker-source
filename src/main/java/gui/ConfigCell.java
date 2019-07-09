package gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import data.AttributeType;

/**
 * Class for rendering and editing main cell values of the config table
 * 
 * 
 * 
 */
public class ConfigCell extends AbstractCellEditor implements TableCellEditor,
		TableCellRenderer
{

	private static final long		serialVersionUID	= 1L;
	private JPanel					panel;
	private JLabel					attribGroupName;
	private JComboBox<Object>		attribSelector;
	private ConfigCellInnerTable	innerTable;
	private JScrollPane				jsp;
	
	public ConfigCell()
	{
		panel 			= new JPanel();
		attribGroupName = new JLabel();
		attribSelector 	= new JComboBox<Object>();
		innerTable 		= new ConfigCellInnerTable();

		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints(0, 1, 1, 3, 1, 1,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0);

		panel.add(attribGroupName, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 3, 0.5, 1,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0);

		panel.add(attribSelector, gbc);

		jsp = new JScrollPane(innerTable);

		gbc = new GridBagConstraints(2, 1, 1, 3, 2, 1,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0);

		panel.add(jsp, gbc);

		attribSelector.addItemListener(new ItemListener()
		{

			public void itemStateChanged(ItemEvent e)
			{
				innerTable
						.setActiveSelectedAttribute((AttributeType) attribSelector
								.getSelectedItem());
			}
		});
	}

	protected void updateData(ConfigData data, boolean isSelected, JTable table,
			int row)
	{
		attribGroupName.setText(data.attributeGroupName);
		attribSelector.setModel(data.attributeSelector.getModel());

		innerTable.updateIfRequired(data);

		innerTable.setActiveSelectedAttribute((AttributeType) attribSelector
				.getSelectedItem());

		table.setRowHeight(row, attribSelector.getPreferredSize().height * 3);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		ConfigData data = (ConfigData) value;
		updateData(data, isSelected, table, row);
		switch (column)
		{
			case 0:
				return attribGroupName;
			case 1:
				return attribSelector;
			case 2:
				return jsp;
			default:
				return panel;
		}
		// return panel;
	}

	public Object getCellEditorValue()
	{
		return null;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
	{
		ConfigData data = (ConfigData) value;
		updateData(data, isSelected, table, row);
		switch (column)
		{
			case 0:
				return attribGroupName;
			case 1:
				return attribSelector;
			case 2:
				return jsp;
			default:
				return panel;
		}
		// return panel;
	}

}
