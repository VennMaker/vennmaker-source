package data;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Objects of this class contains the user definied data for
 * <code>CDialogRelationColor</code>
 * 
 * 
 * 
 */
public class RelationColorData
{
	/**
	 * The definied colors
	 */
	private Map<String, Vector<Color>>	predefinedColor;

	/**
	 * The belonging values to the colors
	 */
	private Map<String, Vector<String>>	predefiniedValues;
	
	
	private Map<AttributeType, Color> attributeColors;
	

	/**
	 * Creates a new <code>RelationColorData</code> object
	 * 
	 * @param predefinedColor
	 *           the definied colors
	 * @param predefiniedValues
	 *           the belonging values
	 */
	public RelationColorData(Map<String, Vector<Color>> predefinedColor,
			Map<String, Vector<String>> predefiniedValues)
	{
		this.predefinedColor = predefinedColor;
		this.predefiniedValues = predefiniedValues;
		
		this.attributeColors = new HashMap<AttributeType, Color>();
	}

	/**
	 * Returns the definied colors
	 * 
	 * @return the definied colors
	 */
	public Map<String, Vector<Color>> getPredefinedColor()
	{
		return predefinedColor;
	}

	/**
	 * Sets the definied colors
	 * 
	 * @param predefinedColor
	 *           the definied colors
	 */
	public void setPredefinedColor(Map<String, Vector<Color>> predefinedColor)
	{
		this.predefinedColor = predefinedColor;
	}

	/**
	 * Returns the values belonging to the colors
	 * 
	 * @return the values belonging to the colors
	 */
	public Map<String, Vector<String>> getPredefiniedValues()
	{
		return predefiniedValues;
	}

	/**
	 * Sets the values belonging to the colors
	 * 
	 * @param predefiniedValues
	 *           the values belonging to the colors
	 */
	public void setPredefiniedValues(
			Map<String, Vector<String>> predefiniedValues)
	{
		this.predefiniedValues = predefiniedValues;
	}

	public Map<AttributeType, Color> getAttributeColors()
	{
		return attributeColors;
	}

	public void setAttributeColors(Map<AttributeType, Color> attributeColors)
	{
		this.attributeColors = attributeColors;
	}
	
	public Color getAttributeColor(AttributeType type)
	{
		return this.attributeColors.get(type);
	}
}
