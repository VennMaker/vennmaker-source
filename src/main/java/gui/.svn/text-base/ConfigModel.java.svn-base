package gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Model for Config Dialog containing basic information
 * 
 * 
 * 
 */
public class ConfigModel extends AbstractTableModel
{
	private static final long	serialVersionUID	= 1L;

	List<?>							modelData;

	public void setData(List<?> data)
	{
		modelData = data;
	}

	public int getRowCount()
	{
		return (modelData != null) ? modelData.size() : 0;
	}

	public int getColumnCount()
	{
		return 3;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return (modelData != null) ? modelData.get(rowIndex) : null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return ConfigData.class;
			case 1:
				return ConfigData.class;
			case 2:
				return ConfigData.class;
			default:
				return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public String getColumnName(int column)
	{
		return "";
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return true;
		// return super.isCellEditable(rowIndex, columnIndex);
	}

	public List<?> getDataList()
	{
		return modelData;
	}
}
