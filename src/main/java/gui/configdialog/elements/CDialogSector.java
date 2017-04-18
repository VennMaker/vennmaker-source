/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.FakeSectorInfo;
import gui.configdialog.NewAttributeListener;
import gui.configdialog.SectorPreview;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import gui.configdialog.save.SaveElement;
import gui.configdialog.save.SectorSaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingSector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import data.AttributeType;
import data.BackgroundInfo;
import data.BackgroundInfo.SectorInfo;

/**
 * Dialog der eine Auswahl der Attributtypen bietet die als Sektoren auf
 * einer</br> Netzwerkkarte dargestellt werden koennen. Sektoren legen die
 * Attributwerte der</br> Akteure auf der Netzwerkkarte fest.
 * 
 * 
 */
public class CDialogSector extends ConfigDialogElement
{
	private static final long									serialVersionUID	= 1L;

	// Attribut für die Darstellung der Sektoren
	private AttributeType										sectorAttrib;

	private SectorTableModel									sTableModel;

	private List<AttributeType>								aTypes;

	private JButton												editAttributeButton;

	private JButton												newAttributeButton;

	private JSlider												sectorTransparency;

	/**
	 * Sectorpreviewbox
	 */
	private SectorPreview										previewSectors;

	private List<SectorInfo>									tmpSectors;

	private HashMap<AttributeType, List<SectorInfo>>	tmpSectorCache		= new HashMap<AttributeType, List<SectorInfo>>();

	private JComboBox<String>									cb;

	private boolean												executeActionListener;

	@Override
	public void buildPanel()
	{
		if (dialogPanel == null)
		{
			dialogPanel = new JPanel();

			editAttributeButton = new JButton(
					Messages.getString("CDialogEditAttributeTypes.4")); //$NON-NLS-1$
			newAttributeButton = new JButton(
					Messages.getString("EditIndividualAttributeTypeDialog.19")); //$NON-NLS-1$
			// Sector Dialog
			sTableModel = new SectorTableModel(this);
			if (sectorAttrib == null)
			{
				sectorAttrib = net.getHintergrund().getSectorAttribute();
				initializeTmpSectors();
			}
			aTypes = VennMaker.getInstance().getProject()
					.getAttributeTypesDiscrete("ACTOR");

			executeActionListener = true;

			cb = new JComboBox<String>();

			fillComboBox(cb, sectorAttrib);

			cb.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (executeActionListener)
					{
						int index = cb.getSelectedIndex();
						if (index >= 0)
						{
							sectorAttrib = aTypes.get(index);
							editAttributeButton.setEnabled(true);
						}
						else
						{
							sectorAttrib = null;
							editAttributeButton.setEnabled(false);
						}

						initializeTmpSectors();
					}
				}
			});

			
			final JTable table = new JTable(sTableModel);
			JScrollPane scroll = new JScrollPane(table);
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
					switchRows(row, table.getSelectedRow());
					// handle drop inside current table
					super.drop(dtde);
				}

				private void switchRows(int row, int selectedRow)
				{
					if (row == selectedRow || row < 0 || selectedRow < 0
							|| row >= tmpSectors.size()
							|| selectedRow >= tmpSectors.size())
						return;

					Collections.swap(tmpSectors, selectedRow, row);

					sTableModel.fireTableDataChanged();

					List<FakeSectorInfo> fakeTmpSectors = previewSectors
							.getSectors();
					if (row == selectedRow || row < 0 || selectedRow < 0
							|| row >= fakeTmpSectors.size()
							|| selectedRow >= fakeTmpSectors.size())
						return;

					Collections.swap(fakeTmpSectors, selectedRow, row);

					previewSectors.repaint();

				}
			});
			editAttributeButton.setEnabled(sectorAttrib != null);
			editAttributeButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					if (sectorAttrib != null)
					{
						EditIndividualAttributeTypeDialog e = new EditIndividualAttributeTypeDialog();
						e.showDialog(sectorAttrib);
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
			TableColumnModel cm = table.getColumnModel();
			cm.getColumn(1).setCellRenderer(new ColorCellRenderer());
			cm.getColumn(2).setCellEditor(new ColorButtonCellEditor(this));
			cm.getColumn(2).setCellRenderer(new ColorButtonCellRenderer());
			if (cb.getSelectedIndex() > 0)
			{
				sectorAttrib = aTypes.get(cb.getSelectedIndex());
				initializeTmpSectors();
			}

			JLabel attLabel = new JLabel(
					Messages.getString("CDialogActorPie.Col0")); //$NON-NLS-1$

			// Stärke Transparenz Slider
			sectorTransparency = new JSlider(0, 100,
					(int) ((1.0 - net.getHintergrund().alpha) * 100));

			sectorTransparency.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					previewSectors.setTransparency(sectorTransparency.getValue());
					previewSectors.repaint();
				}
			});

			JLabel transLabel = new JLabel(
					Messages.getString("BackgroundConfig.String_4")); //$NON-NLS-1$

			// Previewbox anhaengen
			previewSectors = new SectorPreview(net.getHintergrund(), this);
			previewSectors.setPreferredSize(new Dimension(300, 300));

			/* Elemente auf panel platzieren */

			GridBagLayout layout = new GridBagLayout();
			GridBagConstraints gbc;
			dialogPanel.setLayout(layout);

			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(10, 10, 0, 20);
			layout.setConstraints(attLabel, gbc);
			dialogPanel.add(attLabel);

			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(10, 0, 0, 10);
			layout.setConstraints(cb, gbc);
			dialogPanel.add(cb);

			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(10, 0, 0, 10);
			layout.setConstraints(editAttributeButton, gbc);
			dialogPanel.add(editAttributeButton);

			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 3;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(10, 0, 0, 10);
			layout.setConstraints(newAttributeButton, gbc);
			dialogPanel.add(newAttributeButton);

			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gbc.weightx = 0;
			gbc.insets = new Insets(10, 10, 0, 20);
			layout.setConstraints(transLabel, gbc);
			dialogPanel.add(transLabel);

			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.gridwidth = 3;
			gbc.weightx = 1;
			gbc.insets = new Insets(10, 0, 0, 5);
			layout.setConstraints(sectorTransparency, gbc);
			dialogPanel.add(sectorTransparency);

			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 4;
			gbc.weightx = 1;
			gbc.weighty = 0.5;
			gbc.insets = new Insets(0, 10, 0, 10);
			layout.setConstraints(previewSectors, gbc);
			dialogPanel.add(previewSectors);

			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = 4;
			gbc.gridwidth = 4;
			gbc.weightx = 1;
			gbc.weighty = 0.5;
			gbc.insets = new Insets(0, 10, 10, 10);
			layout.setConstraints(scroll, gbc);
			dialogPanel.add(scroll);
		}
	}

	public void updateSectorInfos()
	{
		List<FakeSectorInfo> fakeSectors = previewSectors.getSectors();
		if (sectorAttrib != null && tmpSectorCache.get(sectorAttrib) != null)
		{
			for (int i = 0; i < tmpSectorCache.get(sectorAttrib).size(); i++)
			{
				SectorInfo si = tmpSectorCache.get(sectorAttrib).get(i);
				FakeSectorInfo fsi = fakeSectors.get(i);
				si.sectorColor = fsi.sectorColor;
				si.width = fsi.width;
				si.off = fsi.off;
			}
			tmpSectors = tmpSectorCache.get(sectorAttrib);
		}
		else
			tmpSectors = null;
	}

	private void updatFromEditAttribute(AttributeType selectedAttribute)
	{
		if (selectedAttribute != null
				&& selectedAttribute.getPredefinedValues() != null
				&& sTableModel != null)
			
		{
				tmpSectorCache.put(selectedAttribute, new ArrayList<SectorInfo>());
				Color[] colors = BackgroundInfo.getStandardColors();
				Object[] vals = selectedAttribute.getPredefinedValues();
				for (int i = 0; i < vals.length; i++)
				{
					SectorInfo si = new BackgroundInfo().new SectorInfo();
					si.label = vals[i].toString();
					si.sectorColor = i >= colors.length ? Color.gray : colors[i];
					si.width = 1.0 / vals.length;
					si.off = 0 + i * 1.0 / vals.length;
					tmpSectorCache.get(sectorAttrib).add(si);
				}

			tmpSectors = tmpSectorCache.get(selectedAttribute);
			
			sTableModel.setSelectedAttribute(selectedAttribute);
	//	updateSectorsPreview();
		}
	}
	
	@Override
	public void refreshBeforeGetDialog()
	{
		fillComboBox(cb, sectorAttrib);
		sTableModel.fireTableDataChanged();
		updatFromEditAttribute(sectorAttrib);
	}

	private void fillComboBox(JComboBox<String> cb,
			AttributeType selectedAttribute)
	{
		if (cb == null)
			return;
		cb.removeAllItems();

		aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete("ACTOR");

		// cb.addItem("");

		for (int i = 0; i < aTypes.size(); i++)
		{
			cb.addItem(ConfigDialog.getElementCaption(aTypes.get(i).getLabel()));
			// if (a.equals(selectedAttribute))
			// cb.setSelectedIndex(cb.getItemCount() - 1);
		}

		if (selectedAttribute != null)
			cb.setSelectedItem(ConfigDialog.getElementCaption(selectedAttribute
					.getLabel()));
		else {
			// cb.setSelectedItem("");
			if (cb.getItemCount() > 0) {
				cb.setSelectedIndex(0);
				cb.setSelectedItem(cb.getItemAt(0));
			}
		}
	}

	public int getNumSectors()
	{
		if (tmpSectors == null)
			return 0;
		return tmpSectors.size();
	}

	public String getSectorLabel(int i)
	{
		if (i < 0 || i >= tmpSectors.size())
			return null;
		return tmpSectors.get(i).label;
	}

	/**
	 * checks, if there are sectors configured; if so, display, if not, bring
	 * them up to date and display afterwards
	 */
	public void initializeTmpSectors()
	{
		if (sectorAttrib != null)
		{
			Object[] vals = sectorAttrib.getPredefinedValues();

			/* first run (Cache empty) - get sectors from backgroundinfo */
			if ((tmpSectorCache.size() == 0) && (sectorAttrib != null))
			{

				BackgroundInfo bginfo = net.getHintergrund();
				if (sectorAttrib.equals(bginfo.getSectorAttribute()))
				{					
					tmpSectorCache.put(sectorAttrib, new ArrayList<SectorInfo>());
					for (int i = 0; i < vals.length; i++)
					{
						SectorInfo toCopy = bginfo.getSector(i);
						SectorInfo si = new BackgroundInfo().new SectorInfo();
						si.label = vals[i].toString();
						si.sectorColor = toCopy.sectorColor;
						si.width = toCopy.width;
						si.off = toCopy.off;						
						tmpSectorCache.get(sectorAttrib).add(si);				
					}
				}
			}
			
			if (!tmpSectorCache.containsKey(sectorAttrib)
					|| tmpSectorCache.get(sectorAttrib).size() != vals.length)
			{
				tmpSectorCache.put(sectorAttrib, new ArrayList<SectorInfo>());
				Color[] colors = BackgroundInfo.getStandardColors();
				for (int i = 0; i < vals.length; i++)
				{
					SectorInfo si = new BackgroundInfo().new SectorInfo();
					si.label = vals[i].toString();
					si.sectorColor = i >= colors.length ? Color.gray : colors[i];
					si.width = 1.0 / vals.length;
					si.off = 0 + i * 1.0 / vals.length;
					tmpSectorCache.get(sectorAttrib).add(si);
				}
			}	
			tmpSectors = tmpSectorCache.get(sectorAttrib);

		}
		else
		{
		}
		
		sTableModel.setSelectedAttribute(sectorAttrib);
		updateSectorsPreview();
	}

	public List<SectorInfo> getTmpSectors()
	{
		return tmpSectors;
	}

	/**
	 * updates the current previewwindow to show the right sectorwidths and
	 * colors
	 */
	private void updateSectorsPreview()
	{
		if ((previewSectors != null) && (sectorAttrib != null))
		{
			previewSectors.setSectorCount(tmpSectorCache.get(sectorAttrib).size());
			List<FakeSectorInfo> fakeSec = previewSectors.getSectors();
			for (int i = 0; i < tmpSectorCache.get(sectorAttrib).size(); i++)
			{
				SectorInfo inf = tmpSectorCache.get(sectorAttrib).get(i);
				fakeSec.get(i).label = inf.label;
				fakeSec.get(i).sectorColor = inf.sectorColor;
				fakeSec.get(i).width = inf.width;
				fakeSec.get(i).off = inf.off;
			}

		}
		else if (previewSectors != null)
		{
			previewSectors.setSectorCount(0);
		}
		else
		{
			previewSectors = new SectorPreview(net.getHintergrund(), this);
		}

		previewSectors.repaint();
	}

	/**
	 * Liefert zu einem index eines Sektors die gewählte Farbe
	 */
	public Color getSectorColor(int i)
	{
		if (i < 0 || i >= tmpSectors.size())
			return null;
		return tmpSectors.get(i).sectorColor;
	}

	/**
	 * Kann benutzt werden um die Farbe eines Sektors neu zu setzen
	 */
	public void setSectorColor(int i, Color c)
	{
		if (i < 0 || i >= tmpSectors.size())
			return;
		tmpSectors.get(i).sectorColor = c;
		previewSectors.getSectors().get(i).sectorColor = c;
		previewSectors.repaint();
		sTableModel.colorChanged(i, c);
	}

	public BackgroundInfo getBackgroundConfig()
	{
		if (net != null)
			return net.getHintergrund();
		return null;
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		/* FakeSectors to check, whether the current settings are valid or not */
		List<SectorInfo> fakeSectors = tmpSectorCache.get(sectorAttrib);
		if (fakeSectors != null)
		{
			if (fakeSectors.size() == 0)
				tmpSectors.clear();

			double alpha = (100 - sectorTransparency.getValue()) / 100.0;
			ConfigDialogSetting s = new SettingSector(net, tmpSectors,
					sectorAttrib, alpha);

			return s;
		}

		/* if no attribute is chosen, return empty sectorlist to reset sectors */
		if (sectorAttrib == null)
			return new SettingSector(net, new Vector<SectorInfo>(), null,
					(100 - sectorTransparency.getValue()) / 100.0);

		return null;
	}

	@Override
	public SaveElement getSaveElement()
	{
		if (tmpSectorCache.size() == 0)
			return null;

		Map<AttributeType, List<SectorInfo>> saveCache = new HashMap<AttributeType, List<SectorInfo>>();

		for (AttributeType at : tmpSectorCache.keySet())
		{
			saveCache.put(at, new ArrayList<SectorInfo>());
			for (SectorInfo si : tmpSectorCache.get(at))
			{
				saveCache.get(at).add(si);
			}
		}

		double alpha = (100 - sectorTransparency.getValue()) / 100.0;
		
		String sectorAttribName = "";
		if (sectorAttrib != null) 
			sectorAttribName = sectorAttrib.toString();
		
		SectorSaveElement s = new SectorSaveElement(net, saveCache, alpha,
				sectorAttribName);

		s.setElementName(this.getClass().getSimpleName());

		return s;
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof SectorSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		SectorSaveElement sectorSetting = (SectorSaveElement) setting;
		AttributeType selectedType = null;

		if (sectorSetting.getSectorCache() != null)
		{
			Map<AttributeType, List<SectorInfo>> oldCache = sectorSetting
					.getSectorCache();

			/* fill the current types with types from saveElement */
			// Vector<AttributeType> types = new Vector<AttributeType>();

			// for (AttributeType at : oldCache.keySet())
			// types.add(at);

			/*
			 * reset the temporary SectorCache and add all settings from a saved
			 * template
			 */
			tmpSectorCache = new HashMap<AttributeType, List<SectorInfo>>();

			for (AttributeType type : oldCache.keySet())
			{
				tmpSectorCache.put(type, new ArrayList<SectorInfo>());

				if (oldCache.get(type) != null)
				{
					for (SectorInfo si : oldCache.get(type))
						tmpSectorCache.get(type).add(si);
				}

				if (type.toString().equals(sectorSetting.getSectorAttribute()))
					selectedType = type;
			}
		}

		if (selectedType != null)
		{
			for (int i = 0; i < cb.getItemCount(); i++)
				if (cb.getItemAt(i).toString().equals(selectedType.toString()))
					cb.setSelectedIndex(i);
		}

		updateSectorsPreview();
	}
}

class SectorTableModel extends AbstractTableModel
{
	private static final long		serialVersionUID	= 1L;

	private AttributeType			selectedAttrib;

	private ArrayList<SectorInfo>	tmpSectors;

	private CDialogSector			caller;

	public SectorTableModel(CDialogSector cDialogSector)
	{
		this.caller = cDialogSector;
		tmpSectors = new ArrayList<SectorInfo>();
	}

	public void colorChanged(int i, Color c)
	{
		if (i < 0 || i >= tmpSectors.size())
			return;
		tmpSectors.get(i).sectorColor = c;
		fireTableCellUpdated(i, 1);
	}

	public ArrayList<SectorInfo> getTmpSectors()
	{
		return tmpSectors;
	}

	/**
	 * Refreshes the current table to match the currently chosen attributetype
	 * 
	 * @param a
	 *           the currently chosen attributetype
	 */
	public void setSelectedAttribute(AttributeType a)
	{
		selectedAttrib = a;
		tmpSectors = (ArrayList<SectorInfo>) caller.getTmpSectors();
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		if (col == 2)
			return true;
		return false;
	}

	public AttributeType getSelectedAttrib()
	{
		return selectedAttrib;
	}

	@Override
	public int getColumnCount()
	{
		return 3;
	}

	@Override
	public String getColumnName(int col)
	{
		switch (col)
		{
			case 0:
				return Messages.getString("CDialogSector.Col0");
			case 1:
				return Messages.getString("CDialogSector.Col1");
			case 2:
				return Messages.getString("CDialogSector.Col2");
		}
		return "";
	}

	@Override
	public int getRowCount()
	{
		if (selectedAttrib == null)
			return 0;
		return tmpSectors.size();
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		switch (col)
		{
			case 0:
				return ConfigDialog.getElementCaption(tmpSectors.get(row).label);
			case 1:
				return tmpSectors.get(row).sectorColor;
			case 2:
				return row;
		}
		return "";
	}

}

class ColorCellRenderer extends JLabel implements TableCellRenderer
{
	private static final long	serialVersionUID	= 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		this.setText("");
		this.setOpaque(true);
		this.setBackground((Color) value);
		return this;
	}

}

class ColorButtonCellRenderer extends JButton implements TableCellRenderer
{
	private static final long	serialVersionUID	= 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col)
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
		this.setText(Messages.getString("CDialogSector.Col2"));
		return this;
	}

}

class ColorButtonCellEditor extends DefaultCellEditor
{
	private static final long	serialVersionUID	= 1L;

	private boolean				isPushed;

	private JButton				button;

	private int						value;

	private CDialogSector		caller;

	public ColorButtonCellEditor(CDialogSector cDialogSector)
	{
		super(new JCheckBox());
		this.caller = cDialogSector;
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
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
		button.setText(Messages.getString("CDialogSector.Col2"));
		isPushed = true;
		return button;
	}

	@Override
	public Object getCellEditorValue()
	{
		if (isPushed)
		{
			Color c = JColorChooser
					.showDialog(
							caller,
							Messages.getString("BackgroundConfig.String_6"), caller.getSectorColor(value)); //$NON-NLS-1$
			if (c != null)
			{
				caller.setSectorColor(value, c);
			}
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
