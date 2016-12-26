package gui;

import gui.ConfigData.Internal;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ConfigPanel extends JPanel
{
	private static final long	serialVersionUID	= 1L;

	public ConfigPanel(ConfigModel model)
	{
		setLayout(new GridLayout());
		JTable table = new JTable(model);
		JScrollPane sp = new JScrollPane(table);

		table.setDefaultRenderer(ConfigData.class, new ConfigCell());
		table.setDefaultEditor(ConfigData.class, new ConfigCell());

		add(sp);
		setVisible(true);
	}

	protected ConfigModel createModel()
	{
		ConfigModel model = new ConfigModel();

		List<ConfigData> items = new ArrayList<ConfigData>();

		HashMap<Object, Internal> map = new HashMap<Object, ConfigData.Internal>();

		map.put("Relation", new ConfigData.Internal(new String[] { "positive",
				"neutral", "negative" }, new Color[] { Color.green, Color.black,
				Color.red }, Color.class));

		map.put("Test", new ConfigData.Internal(new String[] { "true", "false" },
				new Color[] { Color.green, Color.red }, Color.class));

		items.add(new ConfigData("StandardRelation", new String[] { "Relation",
				"Test" }, map));

		// items.add(new ConfigData("CustomRelation", new String[] { "1st", "2nd",
		// "3rd" }, new ConfigData.Internal(new String[] { "positive",
		// "neutral", "negative" }, new Color[] { Color.green, Color.black,
		// Color.red }, Color.class)));
		//
		map = new HashMap<Object, ConfigData.Internal>();
		map.put("1st", new ConfigData.Internal(new String[] { "positive",
				"neutral", "negative" }, new Integer[] { 5, 5, 5 }, Integer.class));
		items.add(new ConfigData("SizeTest", new String[] { "1st" }, map));

		map = new HashMap<Object, ConfigData.Internal>();
		map.put("2nd", new ConfigData.Internal(new String[] { "positive",
				"neutral", "negative" }, new Integer[] { 5, 5, 5 }, Integer.class));
		items.add(new ConfigData("SizeTest", new String[] { "2nd" }, map));

		model.setData(items);

		return model;
	}
}
