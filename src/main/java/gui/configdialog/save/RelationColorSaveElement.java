package gui.configdialog.save;

import java.awt.Color;
import java.util.Map;
import java.util.Vector;

import data.AttributeType;

public class RelationColorSaveElement extends SaveElement
{
	private String													activeType;

	private String													selectedType;

	private Color[]												selectedColors;

	private Map<String, Map<String, Vector<Color>>>		selectedColor;

	private Map<String, Map<String, Vector<String>>>	predefValues;

	private Map<String, AttributeType>						selectedAttributes;

	public RelationColorSaveElement(
			Map<String, Map<String, Vector<String>>> predefValues,
			Map<String, Map<String, Vector<Color>>> selectedColor)
	{
		this.predefValues = predefValues;
		this.selectedColor = selectedColor;
	}

	public RelationColorSaveElement(
			Map<String, Map<String, Vector<String>>> predefValues,
			Map<String, Map<String, Vector<Color>>> selectedColor,
			Map<String, AttributeType> selectedAttributes)
	{
		this.predefValues = predefValues;
		this.selectedColor = selectedColor;
		this.selectedAttributes = selectedAttributes;
	}

	/**
	 * @return the activeType
	 */
	public String getActiveType()
	{
		return activeType;
	}

	/**
	 * @return the selectedType
	 */
	public String getOldSelectedType()
	{
		return selectedType;
	}

	/**
	 * @return the selectedColors
	 */
	public Color[] getOldSelectedColors()
	{
		return selectedColors;
	}

	public Map<String, Map<String, Vector<Color>>> getSelectedColor()
	{
		return selectedColor;
	}

	public void setSelectedColor(
			Map<String, Map<String, Vector<Color>>> selectedColor)
	{
		this.selectedColor = selectedColor;
	}

	public Map<String, Map<String, Vector<String>>> getPredefValues()
	{
		return predefValues;
	}

	public void setPredefValues(
			Map<String, Map<String, Vector<String>>> predefValues)
	{
		this.predefValues = predefValues;
	}

	/**
	 * @return Combobox Selection
	 */
	public Map<String, AttributeType> getSelectedAttributes()
	{
		return selectedAttributes;
	}
}
