package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.SaveElement;
import gui.configdialog.save.TriggerSaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingChangeTrigger;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import data.AttributeType;

/**
 * Dialog zur Wahl der Trigger.
 * 
 * 
 */
public class CDialogTrigger extends ConfigDialogElement
{
	private static final long				serialVersionUID				= 1L;

	private JComboBox							mainGeneratorTypeBox;

	private JComboBox							mainAttributeTypeBox;

	private HashMap<String, JComboBox>	mainRelationGeneratorBoxes	= new HashMap<String, JComboBox>();

	private Vector<AttributeType> getActualAttributeTypes()
	{
		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete("ACTOR");
		return aTypes;
	}

	private HashMap<String, Vector<AttributeType>> getRelationAttributeTypes()
	{
		HashMap<String, Vector<AttributeType>> returnMap = new HashMap<String, Vector<AttributeType>>();
		for (String attCollector : VennMaker.getInstance().getProject()
				.getAttributeCollectors())
		{
			returnMap.put(attCollector, VennMaker.getInstance().getProject()
					.getAttributeTypesDiscrete(attCollector));
		}
		return returnMap;
	}

	@Override
	public void buildPanel()
	{
		if (mainGeneratorTypeBox == null)
		{
			mainGeneratorTypeBox = new JComboBox(getActualAttributeTypes());
			mainGeneratorTypeBox.addItem("");
			AttributeType at = VennMaker.getInstance().getProject()
					.getMainGeneratorType("ACTOR");
			if (at != null)
				mainGeneratorTypeBox.setSelectedItem(at);
		}

		if (mainAttributeTypeBox == null)
		{
			mainAttributeTypeBox = new JComboBox(getActualAttributeTypes());
			mainAttributeTypeBox.addItem("");
			AttributeType at = VennMaker.getInstance().getProject()
					.getMainAttributeType("ACTOR");
			if (at != null)
				mainAttributeTypeBox.setSelectedItem(at);
		}
		// AttributeComboBoxRenderer renderer = new AttributeComboBoxRenderer();
		// mainGeneratorTypeBox.setRenderer(renderer);
		// mainAttributeTypeBox.setRenderer(renderer);

		GridBagLayout layout = new GridBagLayout();
		dialogPanel = new JPanel(layout);

		GridBagConstraints gbc;

		JLabel label = new JLabel(Messages.getString("CDialogTrigger.0")); //$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 0, 10);
		layout.setConstraints(label, gbc);
		dialogPanel.add(label);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(5, 10, 0, 10);
		layout.setConstraints(mainGeneratorTypeBox, gbc);
		dialogPanel.add(mainGeneratorTypeBox);

		label = new JLabel(Messages.getString("CDialogTrigger.1")); //$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(20, 10, 0, 10);
		layout.setConstraints(label, gbc);
		dialogPanel.add(label);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(5, 10, 0, 10);
		layout.setConstraints(mainAttributeTypeBox, gbc);
		dialogPanel.add(mainAttributeTypeBox);

		for (String relationCollector : getRelationAttributeTypes().keySet())
		{
			if (mainRelationGeneratorBoxes.get(relationCollector) == null)
			{
				JComboBox relationAttributeBox = new JComboBox(
						getRelationAttributeTypes().get(relationCollector));
				// relationAttributeBox.setRenderer(renderer);

				mainRelationGeneratorBoxes.put(relationCollector,
						relationAttributeBox);
				mainRelationGeneratorBoxes.get(relationCollector).addItem("");
				mainRelationGeneratorBoxes.get(relationCollector).setSelectedItem(
						VennMaker.getInstance().getProject()
								.getMainGeneratorType(relationCollector));
			}
			label = new JLabel(
					Messages.getString("CDialogTrigger.2") + " (" + relationCollector + ")"); //$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = GridBagConstraints.RELATIVE;
			gbc.insets = new Insets(20, 10, 0, 10);
			layout.setConstraints(label, gbc);
			dialogPanel.add(label);

			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = GridBagConstraints.RELATIVE;
			gbc.insets = new Insets(5, 10, 0, 10);
			layout.setConstraints(
					mainRelationGeneratorBoxes.get(relationCollector), gbc);
			dialogPanel.add(mainRelationGeneratorBoxes.get(relationCollector));
		}

		label = new JLabel("");
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(5, 10, 0, 10);
		gbc.weighty = 1;
		gbc.weightx = 1;
		layout.setConstraints(label, gbc);
		dialogPanel.add(label);
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		/*
		 * in beide Richtungen testen, ob sich was an den Relationsattributen
		 * geaendert hat wenn dazugekommen, dann zuviele bei den Boxes loeschen,
		 * wenn welche geloescht wurden, neue Boxes hinzufuegen
		 */
		boolean rebuild = false;
		for (String checkString : getRelationAttributeTypes().keySet())
		{
			if (!mainRelationGeneratorBoxes.keySet().contains(checkString))
			{
				rebuild = true;
				break;
			}
		}

		for (String checkString : mainRelationGeneratorBoxes.keySet().toArray(
				new String[mainRelationGeneratorBoxes.keySet().size()]))
		{
			if (!getRelationAttributeTypes().keySet().contains(checkString))
			{
				mainRelationGeneratorBoxes.remove(checkString);
				rebuild = true;
			}
		}

		if (rebuild)
			buildPanel();

		Object selectedItem = mainGeneratorTypeBox.getSelectedItem();

		mainGeneratorTypeBox.removeAllItems();
		for (AttributeType a : getActualAttributeTypes())
			mainGeneratorTypeBox.addItem(a);
		mainGeneratorTypeBox.addItem("");
		mainGeneratorTypeBox.setSelectedItem(selectedItem);

		selectedItem = mainAttributeTypeBox.getSelectedItem();

		mainAttributeTypeBox.removeAllItems();
		for (AttributeType a : getActualAttributeTypes())
			mainAttributeTypeBox.addItem(a);

		mainAttributeTypeBox.addItem("");
		mainAttributeTypeBox.setSelectedItem(selectedItem);

		for (String relCollector : getRelationAttributeTypes().keySet())
		{
			selectedItem = mainRelationGeneratorBoxes.get(relCollector)
					.getSelectedItem();

			mainRelationGeneratorBoxes.get(relCollector).removeAllItems();
			for (AttributeType a : getRelationAttributeTypes().get(relCollector))
				mainRelationGeneratorBoxes.get(relCollector).addItem(a);
			mainRelationGeneratorBoxes.get(relCollector).addItem("");
			mainRelationGeneratorBoxes.get(relCollector).setSelectedItem(
					selectedItem);
		}
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		Object mainO = mainAttributeTypeBox.getSelectedItem();
		Object genO = mainGeneratorTypeBox.getSelectedItem();
		AttributeType mainA = (mainO == null || mainO.equals("")) ? null
				: (AttributeType) mainO;
		AttributeType genA = (genO == null || genO.equals("")) ? null
				: (AttributeType) genO;

		HashMap<String, AttributeType> generators = new HashMap<String, AttributeType>();

		generators.put("ACTOR", genA);

		for (String relationCollector : mainRelationGeneratorBoxes.keySet())
		{
			Object genR = mainRelationGeneratorBoxes.get(relationCollector)
					.getSelectedItem();
			generators.put(relationCollector,
					(genR == null || genR.equals("")) ? null : (AttributeType) genR);
		}
		SettingChangeTrigger s = new SettingChangeTrigger(generators, mainA,
				"ACTOR");
		return s;
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof TriggerSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);

		TriggerSaveElement element = (TriggerSaveElement) setting;

		AttributeType main = element.getMainAttributeType();
		AttributeType gen = element.getMainGeneratorType();

		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR");
		for (AttributeType a : aTypes)
		{
			if ((main != null) && (a.getId() == main.getId()))
				main = a;
			if ((gen != null) && (a.getId() == gen.getId()))
				gen = a;
		}

		mainAttributeTypeBox.setSelectedItem(main == null ? "" : main);
		mainGeneratorTypeBox.setSelectedItem(gen == null ? "" : gen);

		int index = 0;
		Vector<String> relationCollectors = element.getRelationCollector();
		Vector<String> relationCollectorValues = element
				.getRelationCollectorValues();

		while (index < relationCollectors.size())
		{
			JComboBox relationBox = mainRelationGeneratorBoxes
					.get(relationCollectors.get(index));

			if (relationBox == null)
			{
				index++;
				continue;
			}

			for (int i = 0; i < relationBox.getItemCount(); i++)
			{
				boolean found = false;

				for (int j = 0; j < relationCollectorValues.size(); j++)
				{
					if (relationBox.getItemAt(i).toString()
							.equals(relationCollectorValues.get(j)))
					{
						relationBox.setSelectedIndex(i);
						found = true;
						break;
					}
				}

				if (found)
					continue;
			}

			index++;
		}
	}

	@Override
	public SaveElement getSaveElement()
	{
		Object mainO = mainAttributeTypeBox.getSelectedItem();
		Object genO = mainGeneratorTypeBox.getSelectedItem();
		AttributeType mainA = (mainO == null || mainO.equals("")) ? null
				: (AttributeType) mainO;
		AttributeType genA = (genO == null || genO.equals("")) ? null
				: (AttributeType) genO;

		Vector<String> relationCollectors = new Vector<String>();
		Vector<String> relationCollectorValues = new Vector<String>();

		for (String relationCollector : mainRelationGeneratorBoxes.keySet())
		{
			relationCollectors.add(relationCollector);
			JComboBox generatorBox = mainRelationGeneratorBoxes
					.get(relationCollector);

			if (generatorBox.getSelectedItem() == null)
				continue;

			relationCollectorValues.add(generatorBox.getSelectedItem().toString());
		}

		TriggerSaveElement tse = new TriggerSaveElement(mainA, genA,
				relationCollectors, relationCollectorValues);
		tse.setElementName(this.getClass().getSimpleName());

		return tse;
	}
}

class AttributeComboBoxRenderer extends JLabel implements ListCellRenderer
{
	private static final long	serialVersionUID	= 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
	{
		setOpaque(true);

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

		if (index == -1)
		{
			setOpaque(false);
		}

		if (value instanceof AttributeType)
		{
			AttributeType a = (AttributeType) value;
			if (a != null)
				setText(ConfigDialog.getElementCaption(a.getLabel()));
		}
		else
			setText(" ");

		return this;
	}
}
