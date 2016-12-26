/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.RelationLabelSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingRelationLabel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import data.AttributeType;

/**
 * Dialog fuer Einstellungen rund um die Darstellung der Informationen um den
 * Akteur. </br> (Label position, Label, Tooltip)
 * 
 * 
 */
public class CDialogRelationLabel extends ConfigDialogElement
{

	private static Double				Double					= new Ellipse2D.Double(
																					8, 4, 16, 16);

	private static final BasicStroke	S							= new BasicStroke(2.5f);

	private static final long			serialVersionUID		= 1L;

	private RelationLabelTableModel	tModel;

	private/**
				 * Jeder Relationstyp hat eine Liste von Object-Arrays 		<br>
				 * (ein Array = eine Zeile der Tabelle)		<br>
				 * 		<br>
				 * Waehlt man als Relationstyp z.B. "STANDARDRELATION",		<br>
				 * so wird die Tabelle mit diesen Eintraegen aus der Map gefuellt...		<br>
				 * 		<br>
				 * Bsp.:		<br>
				 * 		<br>
				 * Zeile = tableData.get(row)		<br>
				 * 		<br>
				 * Zeile[0] = boolean ErsteZeile		NICHT ANZEIGEN (nur fuer Verarbeitung)
				 * Zeile[1] = Relationsgruppe			nur in 1. Zeile dieser Gruppe anzeigen		<br>
				 * Zeile[2] = Relationstyp		<br>
				 * Zeile[3] = boolean Label				nur in 1. Zeile dieser Relation anzeigen		<br>
				 * Zeile[4] = boolean Tooltip			nur in 1. Zeile dieser Relation anzeigen		<br>
				 * 
				 */
	List<Object[]>							tableData;

	private static final int			HIDDEN_COLS				= 1;

	private static final int			INDEX_FIRST_ROW		= 0;

	private static final int			INDEX_RELATION_GROUP	= 1;

	private static final int			INDEX_ATTR_TYPE		= 2;

	private static final int			INDEX_BOOL_LABEL		= 3;

	private static final int			INDEX_BOOL_TOOLTIP	= 4;

	@Override
	public void buildPanel()
	{
		dialogPanel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		dialogPanel.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();

		createTableData();

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0.5;
		dialogPanel.add(new JScrollPane(createRelationLabelTable()), gbc);
	}

	/**
	 * fills the table with the values from the given attributetypes
	 */
	private void createTableData()
	{
		VennMaker v = VennMaker.getInstance();

		List<Object[]> oldTableData = null;
		if (tableData != null)
		{
			oldTableData = new ArrayList<Object[]>();

			for (Object[] row : tableData)
			{
				oldTableData.add(row.clone());
			}
		}
		tableData = new ArrayList<Object[]>();

		List<AttributeType> labelList = v.getProject().getDisplayedAtRelation();
		List<AttributeType> tooltipList = v.getProject()
				.getDisplayedAtRelationTooltip();

		for (String collector : v.getProject().getAttributeCollectors())
		{
			/*
			 * first row with the current attributcollectors name: first column
			 * equals true to define relationgroups
			 */
			tableData.add(new Object[] { true, collector, null, false, false });

			Vector<AttributeType> aTypes = v.getProject().getAttributeTypes(
					collector);

			for (AttributeType a : aTypes)
			{
				Object[] o = new Object[5];

				o[INDEX_FIRST_ROW] = false;
				o[INDEX_RELATION_GROUP] = collector;
				o[INDEX_ATTR_TYPE] = a;
				// falls neu erzeugt (= keine alten Werte) setze Belegung vom
				// Projekt
				if (oldTableData == null)
				{
					o[INDEX_BOOL_LABEL] = labelList != null && labelList.contains(a);
					o[INDEX_BOOL_TOOLTIP] = tooltipList != null
							&& tooltipList.contains(a);
				}
				// falls refresh setze Werte von vorher
				else
				{
					// init mit "false" falls Attributtyp neu
					// denn dann wird er nicht in der Liste der alten gefunden
					o[INDEX_BOOL_LABEL] = false;
					o[INDEX_BOOL_TOOLTIP] = false;
					for (Object[] row : oldTableData)
					{
						AttributeType toCompare = (AttributeType) row[INDEX_ATTR_TYPE];
						if (toCompare != null && toCompare.equals(a))
						{
							o[INDEX_BOOL_LABEL] = row[INDEX_BOOL_LABEL];
							o[INDEX_BOOL_TOOLTIP] = row[INDEX_BOOL_TOOLTIP];

							break;
						}
					}
				}
				tableData.add(o);
			}
		}
	}

	private JTable createRelationLabelTable()
	{
		tModel = new RelationLabelTableModel();
		JTable table = new JTable(tModel);
		((JLabel) table.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(SwingConstants.CENTER);
		table.getTableHeader().setReorderingAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(true);

		TableColumnModel cm = table.getColumnModel();

		cm.getColumn(INDEX_BOOL_LABEL - HIDDEN_COLS).setCellRenderer(
				new CheckBoxRenderer());
		cm.getColumn(INDEX_BOOL_TOOLTIP - HIDDEN_COLS).setCellRenderer(
				new CheckBoxRenderer());

		cm.getColumn(INDEX_RELATION_GROUP - HIDDEN_COLS).setCellRenderer(
				new StandardRenderer(true));
		cm.getColumn(INDEX_ATTR_TYPE - HIDDEN_COLS).setCellRenderer(
				new StandardRenderer(false));
		return table;
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		createTableData();
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		ArrayList<AttributeType> labelList = new ArrayList<AttributeType>();
		ArrayList<AttributeType> tooltipList = new ArrayList<AttributeType>();
		for (Object[] row : tableData)
		{
			/* regard only the rows with attributetypes in them */
			if (!(Boolean) row[INDEX_FIRST_ROW])
			{
				AttributeType a = (AttributeType) row[INDEX_ATTR_TYPE];
				if ((Boolean) row[INDEX_BOOL_LABEL])
					labelList.add(a);
				if ((Boolean) row[INDEX_BOOL_TOOLTIP])
					tooltipList.add(a);
			}
		}
		return new SettingRelationLabel(labelList, tooltipList);
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof RelationLabelSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		RelationLabelSaveElement element = (RelationLabelSaveElement) setting;

		createTableData();

		List<AttributeType> labelList = element.getLabelList();
		List<AttributeType> tooltipList = element.getTooltipList();

		/*
		 * Jede Auswahl wird false bis auf die des SaveElements
		 */
		for (Object[] row : tableData)
		{
			if (row[INDEX_FIRST_ROW] == null || !(Boolean) row[INDEX_FIRST_ROW])
			{
				AttributeType a = (AttributeType) row[INDEX_ATTR_TYPE];
				row[INDEX_BOOL_LABEL] = labelList.contains(a);
				row[INDEX_BOOL_TOOLTIP] = tooltipList.contains(a);
			}
		}

		ConfigDialogTempCache.getInstance().addSetting(getFinalSetting());
	}

	@Override
	public SaveElement getSaveElement()
	{
		List<AttributeType> labelList = new ArrayList<AttributeType>();
		List<AttributeType> tooltipList = new ArrayList<AttributeType>();

		for (Object[] row : tableData)
		{
			if (row[INDEX_FIRST_ROW] == null || !(Boolean) row[INDEX_FIRST_ROW])
			{
				AttributeType a = (AttributeType) row[INDEX_ATTR_TYPE];
				if ((Boolean) row[INDEX_BOOL_LABEL])
					labelList.add(a);
				if ((Boolean) row[INDEX_BOOL_TOOLTIP])
					tooltipList.add(a);
			}
		}

		RelationLabelSaveElement elem = new RelationLabelSaveElement(labelList,
				tooltipList);
		elem.setElementName(this.getClass().getSimpleName());

		return elem;
	}

	class RelationLabelTableModel extends AbstractTableModel
	{
		private static final long	serialVersionUID	= 1L;

		private String[]				colHeader;

		public RelationLabelTableModel()
		{
			colHeader = new String[6];
			colHeader[0] = Messages.getString("RelationTableDialog.Column1"); //$NON-NLS-1$
			colHeader[1] = Messages.getString("RelationTableDialog.Column2"); //$NON-NLS-1$
			colHeader[2] = Messages.getString("CDialogActorLabel.2"); //$NON-NLS-1$
			colHeader[3] = Messages.getString("CDialogActorLabel.3"); //$NON-NLS-1$
		}

		@Override
		public int getColumnCount()
		{
			return INDEX_BOOL_TOOLTIP;
		}

		@Override
		public int getRowCount()
		{
			return tableData.size();
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			if (tableData == null || tableData.size() < row)
			{
				return null;
			}
			return tableData.get(row)[HIDDEN_COLS + col];
		}

		@Override
		public Class<?> getColumnClass(int col)
		{
			switch (col + HIDDEN_COLS)
			{
				case INDEX_BOOL_LABEL:
				case INDEX_BOOL_TOOLTIP:
					return Boolean.class;
				default:
					return JLabel.class;
			}
		}

		@Override
		public String getColumnName(int col)
		{
			if (col < colHeader.length)
				return colHeader[col];
			return null;
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			// wenn es nicht um die erste Zeile dieser Beziehung geht wird nur ein
			// leeres Label zurueckgegeben
			boolean first = tableData.get(row)[INDEX_FIRST_ROW] != null
					&& (Boolean) tableData.get(row)[INDEX_FIRST_ROW];

			if (first)
				return false;
			else
			{
				switch (col + HIDDEN_COLS)
				{
					case INDEX_BOOL_LABEL:
					case INDEX_BOOL_TOOLTIP:
						return true;
					default:
						return false;
				}
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			tableData.get(row)[HIDDEN_COLS + col] = value;
			fireTableCellUpdated(row, col);
		}
	}

	class CheckBoxRenderer extends JCheckBox implements TableCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		public CheckBoxRenderer()
		{
			super();
			setOpaque(true);
			this.setBackground(Color.white);
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			if (tableData.get(row)[INDEX_FIRST_ROW] == null
					|| !(Boolean) tableData.get(row)[INDEX_FIRST_ROW])
			{
				this.setSelected((Boolean) tableData.get(row)[col + HIDDEN_COLS]);
				return this;
			}

			/*
			 * don't return NULL or the dialog will crash (Drawing mechanism
			 * doesn't know how to draw NULL)
			 * 
			 * instead of null: new JLabel with backgroundcolor = white (to match
			 * rest of table, but to hide the checkbox)
			 */
			JLabel nullLabel = new JLabel("");
			nullLabel.setBackground(Color.white);
			nullLabel.setOpaque(true);
			return nullLabel;
		}
	}

	class StandardRenderer extends JLabel implements TableCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		private boolean				first;

		public StandardRenderer(boolean first)
		{
			super();
			this.first = first;
			setOpaque(true);
			this.setBackground(Color.white);
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			// if(first)
			// {
			// if(tableData.get(row)[INDEX_FIRST_ROW]!=null &&
			// (Boolean)tableData.get(row)[INDEX_FIRST_ROW])
			// {
			// if(value==null)
			// this.setText("");
			// else
			// this.setText(value.toString());
			// return this;
			// }
			// return null;
			// }
			// else
			// {
			// if(tableData.get(row)[INDEX_FIRST_ROW]==null ||
			// !(Boolean)tableData.get(row)[INDEX_FIRST_ROW])
			// {
			// if(value==null)
			// this.setText("");
			// else
			// this.setText(value.toString());
			// return this;
			// }
			// return null;
			// }
			if (first && tableData.get(row)[INDEX_FIRST_ROW] != null
					&& !(Boolean) tableData.get(row)[INDEX_FIRST_ROW])
				this.setText("");
			else
				this.setText(value == null ? "" : value.toString());
			return this;
		}
	}
}
