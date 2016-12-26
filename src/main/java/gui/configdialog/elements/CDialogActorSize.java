package gui.configdialog.elements;

import files.FileOperations;
import gui.BufferedImageTranscoder;
import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.NewAttributeListener;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import gui.configdialog.save.ActorSizeSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingActorSize;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

import data.AttributeType;
import data.VennMakerCoordinateSystem;

/**
 * Dialog zur Auswahl eines Attributtypen dessen Attributwerte in verschieden
 * Groessen visualisiert werden sollen.
 * 
 * 
 */
public class CDialogActorSize extends ConfigDialogElement
{
	private static final long		serialVersionUID	= 1L;

	private JTable						table;

	private ActorSizeTableModel	model;

	private AttributeType			at;

	private List<AttributeType>	aTypes;

	private JComboBox					cb;

	// Icon das als Vorlage fuer die verschiedenen Groessen genommen wird
	private ImageIcon					defaultIcon;

	// Icon in verschiedenen Groessen
	private ImageIcon[]				icons;

	private Object[]					attributeValues;

	// Ausgewaehlte ComboBox-Eintraege
	private Integer[]					selectedIndexes;

	// Nummerierung der ComboBox-Eintraege
	private Integer[]					intArray;

	// Anfang der Akteursgroessen
	private final int					MINVAL				= 2;

	// Ende der Akteursgroessen
	private final int					MAXVAL				= 30;

	// Zur initialisierung der Arrays
	private final int					DISTANCE				= MAXVAL - MINVAL + 1;

	private JButton					editAttributeButton;

	private JButton					newAttributeButton;

	private String						getType				= "ACTOR";

	@Override
	public void buildPanel()
	{
		dialogPanel = new JPanel();
		dialogPanel.setLayout(new GridBagLayout());

		aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(getType);
		model = new ActorSizeTableModel();

		defaultIcon = getImageIcon(FileOperations
				.getAbsolutePath("/icons/Circle.svg")); //$NON-NLS-1$

		if (cb == null)
		{
			cb = new JComboBox();

			icons = new ImageIcon[DISTANCE];
			intArray = new Integer[DISTANCE];
			float tz = VennMaker.getInstance().getProject().getTextZoom();
			VennMakerCoordinateSystem vmcs = VennMakerView.getVmcs();
			int u = MINVAL;
			for (int i = 0; i < DISTANCE; i++)
			{
				int size = Math.round(vmcs.toJava2D((float) u * tz));
				icons[i] = new ImageIcon(defaultIcon.getImage().getScaledInstance(
						size, size, Image.SCALE_SMOOTH));
				icons[i].setDescription("" + u); //$NON-NLS-1$
				intArray[i] = new Integer(i);
				u++;
			}

			cb.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					int i = cb.getSelectedIndex();

					if (i >= 0)
					{
						at = aTypes.get(i);
						model.fireTableDataChanged();
						table.revalidate();
					}
				}
			});
		}

		editAttributeButton = new JButton(
				Messages.getString("CDialogEditAttributeTypes.4")); //$NON-NLS-1$
		newAttributeButton = new JButton(
				Messages.getString("EditIndividualAttributeTypeDialog.19")); //$NON-NLS-1$

		editAttributeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (at != null)
				{
					EditIndividualAttributeTypeDialog e = new EditIndividualAttributeTypeDialog();
					e.showDialog(at);

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
				int index = aTypes.indexOf(a);
				if (a != null && index != -1)
					cb.setSelectedIndex(index);
			}
		});

		if (at == null)
			at = net.getActorSizeVisualizer().getAttributeType();

		if (at != null && selectedIndexes == null)
		{
			/* get current visualized sizes from the netactorvisualizer */
			Map<Object, Integer> map = net.getActorSizeVisualizer().getSizes();

			attributeValues = at.getPredefinedValues();

			if (at.getPredefinedValues().length == net.getActorSizeVisualizer()
					.getSizes().size())
			{
				selectedIndexes = new Integer[map.size()];
				for (int i = 0; i < map.size(); i++)
				{
					if (map.containsKey(attributeValues[i]))
					{
						selectedIndexes[i] = map.get(attributeValues[i]) - MINVAL;
					}
					else
						selectedIndexes[i] = MINVAL;

				}
			}
			else
			{
				selectedIndexes = new Integer[at.getPredefinedValues().length];
				for (int i = 0; i < at.getPredefinedValues().length; i++)
				{
					if (map.get(attributeValues[i]) != null)
					{
						if (map.containsValue(attributeValues[i]))
							selectedIndexes[i] = map.get(attributeValues[i]) - MINVAL;
					}
					else
						selectedIndexes[i] = MINVAL;
				}
			}

		}
		if (table == null)
			table = buildTable();
		JScrollPane scroll = new JScrollPane(table);

		JLabel attLabel = new JLabel(
				Messages.getString("CDialogActorSize.Label1")); //$NON-NLS-1$

		GridBagLayout layout = new GridBagLayout();
		dialogPanel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();

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

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.25;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(cb, gbc);
		dialogPanel.add(cb);

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

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 4;
		gbc.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(scroll, gbc);
		dialogPanel.add(scroll);
	}

	private JTable buildTable()
	{
		JTable t = new JTable(model);

		// Die RowHeight aus dem Groessten Symbol berechnen
		float tz = VennMaker.getInstance().getProject().getTextZoom();
		VennMakerCoordinateSystem vmcs = VennMakerView.getVmcs();
		int size = Math.round(vmcs.toJava2D(((float) MAXVAL + 1) * tz));
		t.setRowHeight(size);

		JComboBox cb = new JComboBox(intArray);
		cb.setRenderer(new ComboBoxRenderer());

		ActorSizeCellRenderer renderer = new ActorSizeCellRenderer();
		renderer.setPreferredSize(new Dimension(200, 100));

		TableColumn col = t.getColumnModel().getColumn(1);
		col.setCellEditor(new ComboBoxEditor());
		col.setCellEditor(new DefaultCellEditor(cb));
		col.setCellRenderer(renderer);

		return t;
	}

	private ImageIcon getImageIcon(String path)
	{
		File icon = new File(path);

		if (!icon.exists())
		{
			File dict = icon.getParentFile();
			File[] icons = dict.listFiles();
			
			if (icons != null)
			if (icons.length >= 1){
				path = icons[0].getAbsolutePath();
			}
			else
				JOptionPane.showMessageDialog(this,
						Messages.getString("CDialogActorSize.4")); //$NON-NLS-1$
		}

		BufferedImageTranscoder t = new BufferedImageTranscoder();
		t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, 100f);
		ImageIcon imgIcon = null;
		try
		{
			InputStream is = new FileInputStream(path);
			TranscoderInput ti = new TranscoderInput(is);
			t.transcode(ti, null);

			imgIcon = new ImageIcon(t.getImage());
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (TranscoderException e)
		{
			e.printStackTrace();
		}

		return imgIcon;
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		// refreshBeforeGetDialog();

		if (at != null)
		{
			Object[] preVals = at.getPredefinedValues();
			Map<Object, Integer> sizes = new HashMap<Object, Integer>();
			if (preVals != null && preVals.length == selectedIndexes.length)
			{
				for (int i = 0; i < preVals.length; i++)
				{
					// TODO: workaround: selectedIndexes muesste eigentlich immer
					// aktuell sein
					if ((preVals[i] != null) && (sizes != null)
							&& (selectedIndexes[i] != null))
					{
						sizes.put(preVals[i], selectedIndexes[i] + MINVAL);
					}
					else
					{
						System.out
								.println("Error: NP: CDialogActorSize: getFinalSetting");
					}
				}

				return new SettingActorSize(at, sizes, net);
			}
		}
		return null;
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(getType);

		if (!aTypes.contains(at))
			at = null;

		cb.removeAllItems();

		// save index first ... because attribute is changed
		// in checkbox Actionlistener!
		int index = (at == null) ? 0 : aTypes.indexOf(at);

		// if Selected Attribute is still existing and didn't changed
		// values count save the selections
		Integer[] selectedIndexesDummy = null;
		if (index != 0
				&& at.getPredefinedValues().length == selectedIndexes.length)
		{
			selectedIndexesDummy = new Integer[selectedIndexes.length];
			System.arraycopy(selectedIndexes, 0, selectedIndexesDummy, 0,
					selectedIndexes.length);
		}

		// while adding Items the actionListener may changes the attribute & the
		// selectedIndexes...
		for (AttributeType a : aTypes)
			cb.addItem(ConfigDialog.getElementCaption(a.getLabel()));

		// if selectedIndexes were saved attribute stayed the same so write them
		// back...
		if (selectedIndexesDummy != null)
		{
			attributeValues = new Object[at.getPredefinedValues().length];
			System.arraycopy(at.getPredefinedValues(), 0, attributeValues, 0,
					selectedIndexes.length);
			selectedIndexes = selectedIndexesDummy;
		}

		cb.setSelectedIndex(index);
	}

	class ActorSizeTableModel extends DefaultTableModel
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int col)
		{
			switch (col)
			{
				case 0:
					return String.class;
				default:
					return Integer.class;
			}
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			return (col != 0);
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public int getRowCount()
		{
			return attributeValues == null ? 0 : attributeValues.length;
		}

		@Override
		public String getColumnName(int col)
		{
			switch (col)
			{
				case 0:
					return Messages.getString("CDialogActorSize.Col0"); //$NON-NLS-1$
				default:
					return Messages.getString("CDialogActorSize.Col1"); //$NON-NLS-1$
			}
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			switch (col)
			{
				case 0:
					return ConfigDialog.getElementCaption(attributeValues[row]
							.toString());
				default:
					return selectedIndexes[row];
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			Integer v = (Integer) value;
			selectedIndexes[row] = v;
		}

		@Override
		public void fireTableDataChanged()
		{
			attributeValues = at.getPredefinedValues();
			if (selectedIndexes != null
					&& selectedIndexes.length == attributeValues.length)
				return;

			selectedIndexes = new Integer[attributeValues.length];
			for (int i = 0; i < selectedIndexes.length; i++)
			{
				int value = (DISTANCE / selectedIndexes.length) * (i + 1);
				if (value < 0)
					value = 0;
				else if (value >= DISTANCE)
					value = DISTANCE - 1;
				selectedIndexes[i] = value;
			}

			super.fireTableDataChanged();
		}
	}

	class ComboBoxEditor extends DefaultCellEditor
	{
		private static final long	serialVersionUID	= 1L;

		public ComboBoxEditor()
		{
			super(new JComboBox());
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column)
		{
			delegate.setValue((Integer) value);
			return null;
		}

	}

	class ActorSizeCellRenderer extends DefaultTableCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			Integer v = (Integer) value;
			if ((icons != null) && (v != null))
				if (icons[v] != null)
				{
					setIcon(icons[v]);
					setText(icons[v].getDescription());
				}

			return this;
		}
	}

	class ComboBoxRenderer extends JLabel implements ListCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			int selectedIndex;

			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);

			if (value == null)
			{
				selectedIndex = new Integer(MINVAL);
			}
			else
			{
				selectedIndex = Integer.parseInt(value.toString());
			}

			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			ImageIcon icon = icons[selectedIndex];

			if (icon != null)
			{
				String name = icons[selectedIndex].getDescription();
				setIcon(icon);
				setText(name);
				setFont(list.getFont());
			}
			else
			{
				setIcon(null);
				setText(Messages.getString("CDialogActorSize.6")); //$NON-NLS-1$
				setFont(list.getFont());
			}

			return this;
		}
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof ActorSizeSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);

		ActorSizeSaveElement element = (ActorSizeSaveElement) setting;

		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR"); //$NON-NLS-1$

		for (AttributeType type : aTypes)
			if (type.toString().equals(element.getType().toString()))
			{
				this.at = type;

				if (cb.getItemCount() > 0)
				{
					System.out.println(element.getBoxIndex()+" und "+this.cb.getItemCount() );
					this.cb.setSelectedIndex(element.getBoxIndex());
				}
			}

		this.selectedIndexes = element.getSizes();

		// ConfigDialogTempCache.getInstance().addSetting(getFinalSetting());

		if (cb.getItemCount() == 0)
			return;

		this.model.fireTableDataChanged();

		if (this.table != null)
		{

			this.table.revalidate();
			if (this.table.getRootPane() != null)
			{
				this.table.getRootPane().revalidate();
				this.table.getRootPane().repaint();
			}
		}
	}

	@Override
	public SaveElement getSaveElement()
	{
		ActorSizeSaveElement element = new ActorSizeSaveElement(at,
				selectedIndexes, cb.getSelectedIndex());
		element.setElementName(this.getClass().getSimpleName());

		return element;
	}
}
