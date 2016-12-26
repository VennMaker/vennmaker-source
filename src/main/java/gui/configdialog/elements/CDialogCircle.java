/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;
import gui.configdialog.CirclePreview;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.NewAttributeListener;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import gui.configdialog.save.CircleSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingCircle;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import data.AttributeType;

/**
 * Dialog der die Auswahl eines Attributtypen ermoeglicht der in konzentrischen
 * Kreisen auf</br> dem Netzwerk dargestellt wird und die Attributwerte der
 * Akteure in den Kreisen bestimmt.
 * 
 * 
 */
public class CDialogCircle extends ConfigDialogElement
{

	private AttributeType			circleAttrib;

	private List<AttributeType>	aTypes;

	private static final long		serialVersionUID	= 1L;

	private JButton					editAttributeButton;

	private JButton					newAttributeButton;

	private JComboBox					cb;

	private CircleTableModel		tModel;

	private String						getType				= "ACTOR";

	private boolean					runActionListener;

	/**
	 * Panel to show the current circles
	 */
	private CirclePreview			previewCircles;

	@Override
	public void buildPanel()
	{
		// Nur Panel aufbauen, wenn noch nicht vorhanden;
		if (dialogPanel == null)
		{
			dialogPanel = new JPanel();

			previewCircles = new CirclePreview();

			editAttributeButton = new JButton(
					Messages.getString("CDialogEditAttributeTypes.4")); //$NON-NLS-1$
			newAttributeButton = new JButton(
					Messages.getString("EditIndividualAttributeTypeDialog.19")); //$NON-NLS-1$

			if (circleAttrib == null)
				circleAttrib = net.getHintergrund().getCircleAttribute();

			final VennMakerView view = VennMaker.getInstance().getViewOfNetwork(
					net);

			if (circleAttrib == null || view == null || view.getNetzwerk() == null || view.getNetzwerk().getHintergrund() == null)
			{
				tModel = new CircleTableModel();
			}
			else
			{
				
				tModel = new CircleTableModel(circleAttrib, view.getNetzwerk()
						.getHintergrund().isCirclesAsc());
			}

			cb = new JComboBox();
			fillComboBox(cb, circleAttrib);

			cb.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (runActionListener)
					{
						int index = cb.getSelectedIndex() - 1;
						if (index >= 0)
						{
							AttributeType a = aTypes.get(index);
							tModel.setSelectedAttribute(a, tModel.isAsc());
							circleAttrib = a;
							editAttributeButton.setEnabled(true);
						}
						else
						{
							circleAttrib = null;
							tModel.setSelectedAttribute(null, tModel.isAsc());
							editAttributeButton.setEnabled(false);
						}
					}
				}
			});

			this.runActionListener = true;
			editAttributeButton.setEnabled(circleAttrib != null);
			editAttributeButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					if (circleAttrib != null)
					{
						EditIndividualAttributeTypeDialog e = new EditIndividualAttributeTypeDialog();
						e.showDialog(circleAttrib);
						refreshBeforeGetDialog();
					}
				}
			});
			newAttributeButton.addActionListener(new NewAttributeListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					super.actionPerformed(e);
					refreshBeforeGetDialog();
					AttributeType a = super.getAttributeType();
					cb.setSelectedItem(a == null ? "" : a.getLabel());
				}
			});
			JLabel attLabel = new JLabel(
					Messages.getString("CDialogActorPie.Col0"));

			JTable table = new JTable(tModel);

			JTableHeader header = table.getTableHeader();
			// header.setUpdateTableInRealTime(true); // "Seit 1.3 ungebr�uchlich"
			header.addMouseListener(tModel.new ColumnListener(table));
			header.setReorderingAllowed(true);

			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setPreferredSize(new Dimension(400, 200));

			GridBagConstraints gbc;
			GridBagLayout layout = new GridBagLayout();
			dialogPanel.setLayout(layout);

			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0.25;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			layout.setConstraints(attLabel, gbc);
			dialogPanel.add(attLabel);

			/* Add combobox to choose the current attribute */
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 0.25;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(10, 0, 0, 10);
			layout.setConstraints(cb, gbc);
			dialogPanel.add(cb);

			/* "Edit attribute" - Button */
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.weightx = 0.25;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(10, 0, 0, 10);
			layout.setConstraints(editAttributeButton, gbc);
			dialogPanel.add(editAttributeButton);

			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 3;
			gbc.gridy = 0;
			gbc.weightx = 0.25;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(10, 0, 0, 10);
			layout.setConstraints(newAttributeButton, gbc);
			dialogPanel.add(newAttributeButton);

			/* Previewwindow to display current circles */
			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 4;
			gbc.weightx = 5.0;
			gbc.weighty = 5.0;
			gbc.insets = new Insets(10, 10, 10, 10);
			layout.setConstraints(previewCircles, gbc);
			dialogPanel.add(previewCircles);

			/* the table in a scrollpane to see current attributetypes */
			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.LAST_LINE_START;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 4;
			gbc.weightx = 5.0;
			gbc.weighty = 2.5;
			gbc.insets = new Insets(10, 10, 10, 10);
			layout.setConstraints(scrollPane, gbc);
			dialogPanel.add(scrollPane);
		}
	}

	private void fillComboBox(JComboBox cb, AttributeType selectedAttribute)
	{
		if (cb == null)
			return;
		cb.removeAllItems();

		aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(getType);

		cb.addItem("");

		for (int i = 0; i < aTypes.size(); i++)
		{
			AttributeType a = aTypes.get(i);
			cb.addItem(ConfigDialog.getElementCaption(a.getLabel()));
			if (a.equals(selectedAttribute))
				cb.setSelectedIndex(cb.getItemCount() - 1);
		}

	}

	/**
	 * repaint the current preview to match the chosen attributetype
	 * 
	 * @param selectedAttribute
	 *           the current attributetype to visualize
	 */
	public void refreshPreview(AttributeType selectedAttribute)
	{
		if (selectedAttribute != null
				&& selectedAttribute.getPredefinedValues() != null
				&& tModel != null)
		{
			ArrayList<String> predefinedValues = new ArrayList<String>();

			for (int i = 0; i < tModel.getRowCount(); i++)
			{
				predefinedValues.add(tModel.getValueAt(i, 0));
			}

			previewCircles.setCircles(predefinedValues);
		}
		else
		{
			previewCircles.setCircles(null);
		}
		previewCircles.repaint();
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		fillComboBox(cb, circleAttrib);
		if(dialogPanel == null) {
			buildPanel();
		}
		tModel.fireTableDataChanged();
		refreshPreview(circleAttrib);
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		refreshBeforeGetDialog();
		ConfigDialogSetting s = new SettingCircle(circleAttrib, tModel.isAsc(),
				net);
		return s;
	}

	class CircleTableModel extends AbstractTableModel
	{
		private static final long	serialVersionUID	= 1L;

		private int						sortCol				= 0;

		/**
		 * values sorted ascending?
		 */
		private boolean				isSortAsc;

		private AttributeType		selectedAttrib;

		private List<String>			predefinedValues;

		public CircleTableModel()
		{
		}

		public CircleTableModel(AttributeType a, boolean asc)
		{
			setSelectedAttribute(a, asc);
			isSortAsc = asc;
		}

		/**
		 * should the value order be ascending?
		 */
		public boolean isAsc()
		{
			return isSortAsc;
		}

		/**
		 * inits and updates the table
		 * 
		 * @param a
		 *           - Attribute Type which was selected
		 * @param asc
		 *           - order ascending ?
		 */
		public void setSelectedAttribute(AttributeType a, boolean asc)
		{
			selectedAttrib = a;
			if (a != null && a.getPredefinedValues() != null)
			{
				Object[] vals = a.getPredefinedValues();
				predefinedValues = new ArrayList<String>();
				for (int i = 0; i < vals.length; i++)
				{
					predefinedValues.add((String) vals[i]);
				}

				/* if sorted ascending, reverse order of predefined values */
				if (asc)
					Collections.reverse(predefinedValues);
				isSortAsc = asc;
			}
			fireTableDataChanged();
			refreshPreview(a);
		}

		/**
		 * returns the choosen Attribute
		 */
		public AttributeType getSelectedAttrib()
		{
			return selectedAttrib;
		}

		@Override
		public int getColumnCount()
		{
			return 1;
		}

		@Override
		public String getColumnName(int col)
		{
			return Messages.getString("CDialogSector.Col0") + (isSortAsc ? "   <" : "   >"); //$NON-NLS-1$
		}

		@Override
		public int getRowCount()
		{
			if (selectedAttrib == null)
				return 0;
			return predefinedValues.size();
		}

		@Override
		public String getValueAt(int row, int col)
		{
			if (selectedAttrib == null)
				return "";
			return (String) predefinedValues.get(row);
		}

		class ColumnListener extends MouseAdapter
		{

			protected JTable	table;

			public ColumnListener(JTable t)
			{
				table = t;
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				TableColumnModel colModel = table.getColumnModel();
				int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
				int modelIndex = colModel.getColumn(columnModelIndex)
						.getModelIndex();

				if (modelIndex < 0)
					return;
				if (sortCol == modelIndex)
					isSortAsc = !isSortAsc;
				else
					sortCol = modelIndex;

				for (int i = 0; i < getColumnCount(); i++)
				{
					TableColumn column = colModel.getColumn(i);
					column.setHeaderValue(getColumnName(column.getModelIndex()));
				}

				table.getTableHeader().repaint();

				if (predefinedValues == null)
					return;

				Collections.reverse(predefinedValues);

				table.repaint();
				fireTableDataChanged();
				refreshPreview(circleAttrib);
			}
		}
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof CircleSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);

		CircleSaveElement cse = (CircleSaveElement) setting;

		if (tModel == null)
			tModel = new CircleTableModel(circleAttrib, cse.isAsc());

		this.circleAttrib = cse.getCircleAttribute();
		net.getHintergrund().setCirclesAsc(cse.isAsc());

		this.runActionListener = false;
		fillComboBox(cb, circleAttrib);
		this.runActionListener = true;

		this.tModel.setSelectedAttribute(circleAttrib, tModel.isAsc());
	}

	@Override
	public SaveElement getSaveElement()
	{
		refreshBeforeGetDialog();
		CircleSaveElement cse = new CircleSaveElement(this.circleAttrib,
				this.tModel.isAsc());
		cse.setElementName(this.getClass().getSimpleName());
		return cse;
	}
}
