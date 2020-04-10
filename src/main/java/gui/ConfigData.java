package gui;

import java.util.HashMap;

import javax.swing.JComboBox;

/**
 * Data class for ConfigDialogs attribute representation
 * 
 * 
 * 
 */
public class ConfigData
{
	/**
	 * Klasse zur Verwaltung aller Eigenschaften zu dem zugehrigem Attribut
	 * 
	 */
	static public class Internal
	{

		public String[]	keys;

		public Object[]	values;

		public Class<?>	type;

		/**
		 * Erstellt 1:1 Abbildung von keys (AttributeType-Name auf Wert) 
		 * @param keys Eigenschaft
		 * @param values Mgliche Werte 
		 * @param valueType Angabe der Klasse der Werte
		 */
		public Internal(String[] keys, Object[] values, Class<?> valueType)
		{
			super();
			this.keys = keys;
			this.values = values;

			this.type = valueType;
		}

	}

	/**
	 * AttributeCollector
	 */
	public String							attributeGroupName;

	/**
	 * Checkboxen fr die Attribute
	 */
	public JComboBox<Object>			attributeSelector;

	/**
	 * Einzelnen Internen Eigenschaften zu einem Attribut 
	 */
	public HashMap<Object, Internal>	attributeSettings;

	public ConfigData(String groupName, Object[] attributes,
			HashMap<Object, Internal> attributeSettings)
	{
		attributeGroupName = groupName;
		attributeSelector = new JComboBox<Object>(attributes);

		this.attributeSettings = attributeSettings;

	}

	public String toString()
	{

		return (super.toString() + " " + this.attributeGroupName + " Active: "
				+ this.attributeSelector.getSelectedItem() + " of " + this.attributeSettings
					.keySet());
	}
}
