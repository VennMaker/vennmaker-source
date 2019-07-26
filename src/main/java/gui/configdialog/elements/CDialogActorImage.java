/**
 * 
 */
package gui.configdialog.elements;

import files.FileOperations;
import files.ImageOperations;
import files.VMPaths;
import gui.BufferedImageTranscoder;
import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.NewAttributeListener;
import gui.configdialog.individual.DeleteIconDialog;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import gui.configdialog.save.ActorImageSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingActorImages;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.iharder.Base64;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

import data.AttributeType;

/**
 * Ermoeglicht die Auswahl eines Attributtypen zur Darstellung der
 * Akteuere.</br> Dazu lassen sich die Symbole zu den verschiedenen
 * Attributwerten auswaehlen.
 * 
 * 
 */
public class CDialogActorImage extends ConfigDialogElement implements
		ItemListener
{
	private static final long		serialVersionUID	= 1L;

	private String						getType				= "ACTOR";

	private DeleteIconDialog		diag;

	// UI Elemente
	private JButton					editAttributeButton;

	private JButton					newAttributeButton;

	private JComboBox					imageComboList;

	private JTable						table;

	/**
	 * Combobox mit allen Attributtypen die zur Auswahl stehen
	 */
	private JComboBox<AttributeType>					cbAttribute;

	/**
	 * Durch die Combobox cbAttribute ausgewaehltes momentaner Attributtyp
	 */
	private AttributeType			selectedAttribute;

	/**
	 * Table Model 2 Spalten (Attributwer, Icon zu diesem Wert(Combobox))
	 */
	private MyTableModel				model;

	/**
	 * Beinhaltet alle Icons (hinzugefuegte + geloeschte + currentIcons)
	 */
	private ArrayList<ImageIcon>	allIcons;

	// Die Listen die jetzt kommen beziehen sich alle auf allIcons

	/**
	 * Namen der geloeschten Icons
	 */
	private ArrayList<String>		deletedIcons;

	/**
	 * Namen der hinzugefuegten Icons (Im Falle von Cancel, werden diese wieder
	 * verworfen)
	 */
	private ArrayList<String>		addedIcons;

	/**
	 * Indizes, welche die momentan ausgewaehlten Icons repraesentieren (Laenge =
	 * Anzahl Attributwerte des selectedAttribute)
	 */
	private ArrayList<Integer>		selectedIcons;

	// private ImageListModel imageListModel;

	private boolean					skipLoadImages;

	public CDialogActorImage()
	{
		initIconLists();
		diag = new DeleteIconDialog(this);
	}

	private void initIconLists()
	{
		allIcons = new ArrayList<ImageIcon>();
		deletedIcons = new ArrayList<String>();
		addedIcons = new ArrayList<String>();
		selectedIcons = new ArrayList<Integer>();
	}

	/**
	 * Liefert den configurierten Icon Pfad zurueck
	 */
	private String getIconPath()
	{
		return VMPaths.getCurrentWorkingDirectory() + "/" + VMPaths.VENNMAKER_SYMBOLS;
	}

	/**
	 * Liefert den Absoluten Pfad zu einem Akteurs Bild, entweder den "tempIcon"
	 * Pfad oder den normalen Symbolpfad
	 */
	private String getFileName(String fileName)
	{
		return getIconPath() + fileName; //$NON-NLS-1$
	}

	/**
	 * Laed den im ActorImageVisualizer eingestelleten Attributtyp
	 */
	private void getVisualizerAttribut()
	{
		selectedAttribute = net.getActorImageVisualizer().getAttributeType();

		selectedIcons.clear();
		// Indizes der im Projekt eingestellten Bilder suchen
		Object[] preVals = selectedAttribute.getPredefinedValues();
		for (Object o : preVals)
		{
			int index = -1;
			String name = net.getActorImageVisualizer().getImages().get(o);

			if (name != null)
				for (int i = 0; i < allIcons.size(); i++)
				{
					ImageIcon icon = allIcons.get(i);
					if (icon.getDescription().equals(new File(name).getName()))
					{
						index = i;
						break;
					}
				}
			if (index == -1)
				index = 0;
			selectedIcons.add(index);
		}
	}

	/**
	 * Aktuallisiert Liste der Attribut-Typen und setzt ggf. die Auswahl des
	 * Attributtyps
	 */
	private void updateAttributes()
	{
		if (selectedAttribute == null)
		{
			getVisualizerAttribut();
		}
		List<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(getType);
		cbAttribute.removeItemListener(this);

		cbAttribute.removeAllItems();
		for (AttributeType a : aTypes)
		{
			cbAttribute.addItem(a);
		}

		cbAttribute.addItemListener(this);
		cbAttribute.setSelectedItem(selectedAttribute);
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		updateAttributes();

		if (!skipLoadImages)
			loadImages();

		updateSelection();
		

		registerTable();
		
	}

	/**
	 * Laed svg oder jpg/png Icons aus dem Icon Verzeichnis
	 * 
	 * @return
	 */
	private ImageIcon loadImage(String name)
	{
		ImageIcon icon = null;
		try
		{
			// SVG
			if (name.toLowerCase().endsWith(".svg")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				BufferedImageTranscoder t = new BufferedImageTranscoder();
				t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) 20);

				TranscoderInput ti = null;

				String filename = getFileName(name);

				InputStream is = new FileInputStream(filename);
				ti = new TranscoderInput(is);
				t.transcode(ti, null);

				t.getImage();

				icon = new ImageIcon(t.getImage());
			}
			// JPG und PNG
			else
			{
				String filename = getFileName(name);

				BufferedImage bi = ImageOperations.loadImage(new File(filename));
				
				if (bi != null){
				icon = new ImageIcon(bi.getScaledInstance(20, 20,
						BufferedImage.SCALE_SMOOTH));
				}else
					icon = new ImageIcon( VennMakerView.getDefaultActorImage((int) 20) );
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (TranscoderException e)
		{
			System.err.println(Messages.getString("CDialogActorImage.3") + name); //$NON-NLS-1$
		}
		return icon;
	}

	/**
	 * Laed die Icons aus dem Image-Verzeichnis besonders die "allIcons" Liste
	 */
	private void loadImages()
	{
		File dir = new File(getIconPath()); //$NON-NLS-1$

		if (dir.exists())
		{
			String[] allFiles = dir.list();

			if (allFiles != null) Arrays.sort(allFiles);
			allIcons.clear();
			
			if (allFiles != null)
			for (int i = 0; i < allFiles.length; i++)
			{
				ImageIcon icon = loadImage(allFiles[i]);
				if (icon != null)
				{
					icon.setDescription(allFiles[i]);
					allIcons.add(icon);
				}
			}
		}
	}

	public void addIconToDelete(String name)
	{
		deletedIcons.add(name);
	}

	public void updateDelete()
	{
		// Alte Auswahl sichern
		ArrayList<String> selectedIconNames = new ArrayList<String>();
		for (int i = 0; i < selectedIcons.size(); i++)
		{
			ImageIcon icon = allIcons.get(selectedIcons.get(i));
			selectedIconNames.add(icon.getDescription());
		}

		for (String s : deletedIcons)
		{
			int index = getIndexOf(s);
			if (index != -1)
				allIcons.remove(index);
		}

		// alte Auwahl wiederherstellen
		selectedIcons.clear();
		for (int i = 0; i < selectedIconNames.size(); i++)
		{
			selectedIcons.add(getIndexOf(selectedIconNames.get(i)));
		}

		registerTable();

		updateAttributes();
		updateSelection();
		model.fireTableDataChanged();
	}

	/**
	 * Verbindet die Bilder mit ihren Werten, anhand des momentan ausgewaehlten
	 * Attributtyps selectedAttribute
	 */
	private void updateSelection()
	{
		if (selectedAttribute == null)
			return;
		int newSize = selectedAttribute.getPredefinedValues().length;
		int oldSize = selectedIcons.size();

		// falls das nun gewaehlte Attribut mehr Werte als das vorherige hat
		// fuege das erste Bilder der Liste an die fehlenden Positionen der
		// Auswahl ein
		if (oldSize < newSize)
			for (int i = oldSize; i < newSize; i++)
				selectedIcons.add(new Integer(0));
		// sonst loesche die Auswahlen die zu viel sind
		else if (oldSize > newSize)
			for (int i = oldSize - 1; i >= newSize; i--)
				selectedIcons.remove(i);

		for (int i = newSize - 1; i >= 0; i--)
		{
			int sel = selectedIcons.get(i);
			if (sel < 0 || sel >= allIcons.size())
				sel = 0;
			selectedIcons.remove(i);
			selectedIcons.add(i, sel);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		selectedAttribute = (AttributeType) cbAttribute.getSelectedItem();
		if (selectedAttribute != null)
			updateSelection();

		this.model.fireTableDataChanged();
	}

	@Override
	public void buildPanel()
	{
		if (dialogPanel == null)
			dialogPanel = new JPanel();
		else
			dialogPanel.removeAll();

		loadImages();
		registerTable();

		editAttributeButton = new JButton(
				Messages.getString("CDialogEditAttributeTypes.4")); //$NON-NLS-1$
		newAttributeButton = new JButton(
				Messages.getString("EditIndividualAttributeTypeDialog.19")); //$NON-NLS-1$
		editAttributeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (selectedAttribute != null)
				{
					EditIndividualAttributeTypeDialog e = new EditIndividualAttributeTypeDialog();
					e.showDialog(selectedAttribute);
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
				if (a != null)
					cbAttribute.setSelectedItem(a);
			}
		});

		JButton addIconButton = new JButton(
				Messages.getString("CDialogActorImage.7")); //$NON-NLS-1$

		addIconButton.addActionListener(new AddIconListener());
		JButton deleteIconsButton = new JButton(
				Messages.getString("CDialogDeleteIcon.4")); //$NON-NLS-1$

		deleteIconsButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				diag.makeVisible(allIcons);
			}
		});

		JLabel attLabel = new JLabel(
				Messages.getString("CDialogActorSize.Label1")); //$NON-NLS-1$

		int zeile = 0;

		GridBagLayout layout = new GridBagLayout();
		dialogPanel.setLayout(layout);

		GridBagConstraints gbc;

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.25;
		gbc.insets = new Insets(10, 10, 0, 10);
		layout.setConstraints(attLabel, gbc);
		dialogPanel.add(attLabel);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.25;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(cbAttribute, gbc);
		dialogPanel.add(cbAttribute);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.25;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(editAttributeButton, gbc);
		dialogPanel.add(editAttributeButton);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 3;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.25;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(newAttributeButton, gbc);
		dialogPanel.add(newAttributeButton);

		zeile++;

		JScrollPane scrollPane = new JScrollPane(table);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 4;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(10, 10, 0, 10);
		layout.setConstraints(scrollPane, gbc);
		dialogPanel.add(scrollPane);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = zeile + 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.25;
		gbc.insets = new Insets(10, 10, 10, 0);
		layout.setConstraints(addIconButton, gbc);
		dialogPanel.add(addIconButton);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 3;
		gbc.gridy = zeile + 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.25;
		gbc.insets = new Insets(10, 0, 10, 10);
		layout.setConstraints(deleteIconsButton, gbc);
		dialogPanel.add(deleteIconsButton);

	}

	private void registerTable()
	{
		if (model == null)
			model = new MyTableModel();
		if (table == null)
			table = new JTable(model);

		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(30);

		Integer[] vals = new Integer[allIcons.size()];

		for (int i = 0; i < allIcons.size(); i++)
			vals[i] = i;

		imageComboList = new JComboBox(vals);

		ComboBoxRenderer renderer = new ComboBoxRenderer();
		renderer.setPreferredSize(new Dimension(100, 50));
		imageComboList.setRenderer(renderer);
		imageComboList.setMaximumRowCount(13);
		if (imageComboList.getItemCount()>0)
			imageComboList.setSelectedIndex(0);

		TableColumn column = table.getColumnModel().getColumn(1);
		column.setCellEditor(new DefaultCellEditor(imageComboList));
		IconTableCellRenderer tRenderer = new IconTableCellRenderer();

		column.setCellRenderer(tRenderer);

		if (cbAttribute == null)
		{
			List<AttributeType> aTypes = VennMaker.getInstance().getProject()
					.getAttributeTypesDiscrete(getType);

			AttributeType[] aTypesArray = new AttributeType[aTypes.size()];
			aTypes.toArray(aTypesArray);
			cbAttribute = new JComboBox(aTypesArray);
			cbAttribute.setRenderer(new AttributeComboBoxRenderer());
			cbAttribute.setSelectedItem(selectedAttribute);
			cbAttribute.addItemListener(this);
		}
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		Map<Object, String> images = new HashMap<Object, String>();
		if (selectedAttribute == null)
			return null;
		Object[] preVals = selectedAttribute.getPredefinedValues();
		for (int i = 0; i < preVals.length; i++)
		{
			ImageIcon icon = allIcons.get(selectedIcons.get(i));
			images.put(preVals[i], getFileName(icon.getDescription()));
		}
		ConfigDialogSetting s = new SettingActorImages(net, selectedAttribute,
				images, deletedIcons);
		return s;
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof ActorImageSaveElement))
			return;

		initIconLists();

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		ActorImageSaveElement element = (ActorImageSaveElement) setting;
		selectedAttribute = element.getType();

		List<ImageIcon> currentIcons = new ArrayList<ImageIcon>();

		String[] imageData = element.getImageData();
		String[] imageNames = element.getIconNames();
		int[] values = element.getIndicies();

		selectedIcons.clear();

		for (int i = 0; i < values.length; i++)
			selectedIcons.add(values[i]);
		if (imageData != null && imageData.length > 0)
		{
			File dir = new File(getIconPath());

			if (!dir.exists())
				dir.mkdir();
			try
			{
				for (int i = 0; i < imageData.length; i++)
				{
					String fileName = dir.getAbsolutePath() + VMPaths.SEPARATOR
							+ imageNames[i];
					if (fileName.toLowerCase().endsWith(".svg"))
					{
						BufferedWriter bwriter = new BufferedWriter(new FileWriter(
								new File(fileName)));
						bwriter.write(imageData[i]);
						bwriter.close();
					}
					else{
						if (imageData[i] != null)
						Base64.decodeToFile(imageData[i], fileName);
					}
				}
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}

		loadImages();
		registerTable();
		updateAttributes();
		updateSelection();

		for (ImageIcon icon : allIcons)
			currentIcons.add(icon);

		if (element.getIconsToDelete() != null)
		{

			for (int i = 0; i < currentIcons.size(); i++)
			{
				for (String iconToDelete : element.getIconsToDelete())
				{
					if (iconToDelete.equals(currentIcons.get(i).toString()))
					{
						allIcons.remove(currentIcons.get(i));
						deletedIcons.add(currentIcons.get(i).toString());
						break;
					}
				}
			}

			selectedIcons.clear();

			for (int i = 0; i < element.getIndicies().length; i++)
				selectedIcons.add(element.getIndicies()[i]);

			this.skipLoadImages = true;

			registerTable();
			updateAttributes();
			updateSelection();
		}

		if (dialogPanel != null)
			dialogPanel.revalidate();
	}

	/**
	 * Methode um Index zu einem Bild Namen zu bekommen
	 * 
	 * @return index - falls gefunden<br>
	 *         -1 wenn nicht
	 */
	private int getIndexOf(String name)
	{
		for (int i = 0; i < allIcons.size(); i++)
		{
			ImageIcon icon = allIcons.get(i);
			if (icon.getDescription().equals(name))
				return i;
		}
		return -1;
	}

	@Override
	public SaveElement getSaveElement()
	{
		int[] indicies = new int[model.getRowCount()];

		for (int i = 0; i < model.getRowCount(); i++)
			indicies[i] = (Integer) model.getValueAt(i, 1);

		ActorImageSaveElement element = null;

		File originalIconsFolder = new File(VMPaths.VENNMAKER_ICONS);

		int folderOffset = 0;

		if (originalIconsFolder.listFiles() != null)
		for (File file : originalIconsFolder.listFiles())
			if (file.isDirectory())
				folderOffset++;

		if (addedIcons.size() == 0 && originalIconsFolder.list() != null &&
				 allIcons.size() == originalIconsFolder.list().length
						- folderOffset)
		{
			element = new ActorImageSaveElement(selectedAttribute, indicies);
		}
		else
		{
			for (ImageIcon icon : allIcons)
			{

				boolean found = false;

				if (originalIconsFolder.listFiles() != null)
				for (File iconFile : originalIconsFolder.listFiles())
				{

					if (iconFile.isDirectory())
						continue;

					if (icon.toString().equals(iconFile.getName()))
					{
						found = true;
						break;
					}
				}

				if (!found && !addedIcons.contains(icon.toString()))
					addedIcons.add(icon.toString());
			}

			List<String> compareIcons = new ArrayList<String>();

			if (originalIconsFolder.listFiles() != null)
			for (File iconFile : originalIconsFolder.listFiles())
			{
				boolean found = false;

				if (iconFile.isDirectory())
					continue;

				for (ImageIcon icon : allIcons)
				{
					if (iconFile.getName().equals(icon.toString()))
					{
						found = true;
						break;
					}
				}

				if (found)
					compareIcons.add(iconFile.getName());
			}

			if (originalIconsFolder.listFiles() != null)
			for (File iconFile : originalIconsFolder.listFiles())
			{
				boolean found = false;

				if (iconFile.isDirectory())
					continue;

				for (String iconName : compareIcons)
				{
					if (iconName.equals(iconFile.getName()))
					{
						found = true;
						break;
					}
				}

				if (!found)
					deletedIcons.add(iconFile.getName());
			}

			element = new ActorImageSaveElement(selectedAttribute, indicies, null,
					null);
			element.setIconsToDelete(deletedIcons.toArray(new String[deletedIcons
					.size()]));
		}

		if (addedIcons.size() > 0)
		{
			String[] imageData = new String[addedIcons.size()];
			String[] imageNames = new String[addedIcons.size()];

			for (int i = 0; i < imageData.length; i++)
			{
				String fileName = getFileName(addedIcons.get(i));
				String data = null;
				try
				{
					if (fileName.toLowerCase().endsWith(".svg"))
					{
						BufferedInputStream bis = new BufferedInputStream(
								new FileInputStream(fileName));
						StringBuffer stbuff = new StringBuffer();

						int b = 0;

						while ((b = bis.read()) != -1)
							stbuff.append((char) b);

						data = stbuff.toString();
					}
					else
						data = Base64.encodeFromFile(fileName);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				imageData[i] = data;
				imageNames[i] = addedIcons.get(i);
			}

			element.setIndicies(indicies);
			element.setImageData(imageData);
			element.setIconNames(imageNames);
		}

		element.setElementName(this.getClass().getSimpleName());

		return element;
	}

	class MyTableModel extends AbstractTableModel
	{
		static final long	serialVersionUID	= 1L;

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public int getRowCount()
		{
			return selectedIcons.size();
		}

		@Override
		public String getColumnName(int col)
		{
			String r;
			switch (col)
			{
				case 0:
					r = Messages.getString("CDialogActorImage.2"); //$NON-NLS-1$
					break;

				case 1:
					r = Messages.getString("CDialogActorImage.4"); //$NON-NLS-1$
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
					r = ConfigDialog.getElementCaption(""
							+ selectedAttribute.getPredefinedValues()[row]);
					break;

				case 1:

					r = selectedIcons.get(row);
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
			selectedIcons.remove(row);
			selectedIcons.add(row, (Integer) value);
		}
	}

	class IconTableCellRenderer extends DefaultTableCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			Integer v = (Integer) value;
			if (allIcons.size() <= v)
				return null;
			ImageIcon icon = allIcons.get(v);
			setIcon(icon);
			setText(icon.getDescription());
			return this;
		}
	}

	/**
	 * Combobox Renderer fuer die Combobox Spalte der Tabelle
	 */
	class ComboBoxRenderer extends JLabel implements ListCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		public ComboBoxRenderer()
		{
			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			int i = 0;
			if (value != null)
				i = (Integer) value;

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

			// Set the icon and text. If icon was null, say so.
			ImageIcon icon = null;
			if (i < allIcons.size())
				icon = allIcons.get(i);

			if (icon != null)
			{
				String name = icon.getDescription();
				setIcon(icon);
				setText(name);
				setFont(list.getFont());
			}
			else
			{
				setIcon(null);
				setText(Messages.getString("CDialogActorImage.6")); //$NON-NLS-1$
				setFont(list.getFont());
			}

			return this;
		}
	}

	class AddIconListener implements ActionListener
	{
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileFilter()
			{
				final private String[]	endings	= new String[] { ".svg", ".jpg",
																	".png" };

				@Override
				public boolean accept(File f)
				{
					String name = f.getName().toLowerCase();
					boolean accepted = f.isDirectory();
					for (String e : endings)
						accepted |= name.endsWith(e);

					return accepted;
				}

				@Override
				public String getDescription()
				{
					String desc = Arrays.toString(endings);
					return desc.substring(1, desc.length() - 1); //$NON-NLS-1$
				}

			});

			if (chooser.showOpenDialog(VennMaker.getInstance()) == JFileChooser.APPROVE_OPTION)
			{
				File temporaryIcons = new File(getIconPath()); //$NON-NLS-1$

				if (!temporaryIcons.exists())
					temporaryIcons.mkdir();

				List<String> selectedIconNames = new ArrayList<String>();
				// Name des Icons aus der momentanen Liste speichern,
				// weil sich die Indizes beim Hinzufuegen / Loeschen aendern
				if (selectedIcons.size() > 0)
					for (int i = 0; i < selectedIcons.size(); i++)
					{
						ImageIcon icon = allIcons.get(selectedIcons.get(i));
						selectedIconNames.add(icon.getDescription());
					}

				File[] src = chooser.getSelectedFiles();

				for (int i = 0; i < src.length; i++)
				{
					try
					{

						File dest = new File(getIconPath() + src[i].getName());

						// falls das neue Icon aus "jpg" oder "png" Datei
						// erzeugt wird,wird aus Performance Grnden kleinere
						// Version
						// gespeichert
						String name = src[i].getName().toLowerCase();
						if (name.endsWith(".svg"))
							FileOperations.copyFile(src[i], dest, 4096, true);
						else if (ImageOperations.copyScaledImage(src[i], dest, 150,
								150) == false)
							throw new IOException(
									"Could'nt create scaled Instance of " + src[i]);

						addedIcons.add(src[i].getName());
						ImageIcon icon = loadImage(src[i].getName());
						if (icon != null)
						{
							icon.setDescription(src[i].getName());
							allIcons.add(icon);
						}

					} catch (IOException exn)
					{
						exn.printStackTrace();
					}
				}

				Collections.sort(allIcons, new Comparator()
				{
					public int compare(Object o1, Object o2)
					{
						ImageIcon i1 = (ImageIcon) o1;
						ImageIcon i2 = (ImageIcon) o2;

						return i1.getDescription().compareTo(i2.getDescription());
					}
				});

				// Alte Auswahl laden
				selectedIcons.clear();
				for (int i = 0; i < selectedIconNames.size(); i++)
				{
					int index = getIndexOf(selectedIconNames.get(i));
					if (index == -1)
						index = 0;
					selectedIcons.add(index);
				}

				if (src.length > 1)
				{
					JOptionPane.showMessageDialog(ConfigDialog.getInstance(),
							Messages.getString("CDialogActorImage.8")); //$NON-NLS-1$

				}
				else
				{
					JOptionPane.showMessageDialog(ConfigDialog.getInstance(),
							Messages.getString("CDialogActorImage.9")); //$NON-NLS-1$
				}
				registerTable();
				model.fireTableDataChanged();
			}
		}
	}
}
