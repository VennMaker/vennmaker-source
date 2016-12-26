package gui.configdialog.save;

import java.util.Map;
import java.util.Vector;

import data.AttributeType;

public class RelationSizeSaveElement extends SaveElement
{
	private String													activeType;

	private String													selectedType;

	private int[]													selectedSize;

	private Map<String, Map<String, Vector<Integer>>>	sizes;

	private Map<String, Map<String, Vector<String>>>	predefValues;

	private Map<String, AttributeType>						selectedAttributes;

	public RelationSizeSaveElement(
			Map<String, Map<String, Vector<Integer>>> sizes,
			Map<String, Map<String, Vector<String>>> predefValues)
	{
		this.sizes = sizes;
		this.predefValues = predefValues;
	}

	public RelationSizeSaveElement(
			Map<String, Map<String, Vector<Integer>>> sizes,
			Map<String, Map<String, Vector<String>>> predefValues,
			Map<String, AttributeType> selectedAttributes)
	{
		this.sizes = sizes;
		this.predefValues = predefValues;

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
	 * @return the selectedSize
	 */
	public int[] getOldSelectedSize()
	{
		return selectedSize;
	}

	/**
	 * Returns the sizes selected by the user
	 * 
	 * @return the sizes selected by the user
	 */
	public Map<String, Map<String, Vector<Integer>>> getSizes()
	{
		return sizes;
	}

	/**
	 * Sets the sizes selected by the user
	 * 
	 * @param sizes
	 *           the sizes selected by the user
	 */
	public void setSizes(Map<String, Map<String, Vector<Integer>>> sizes)
	{
		this.sizes = sizes;
	}

	/**
	 * Returns the predefinied values from the selected
	 * <code>AttributeType</code>
	 * 
	 * @return the predefinied values from the selected
	 *         <code>AttributeType</code>
	 */
	public Map<String, Map<String, Vector<String>>> getPredefValues()
	{
		return predefValues;
	}

	/**
	 * Sets the predefinied values from the selected <code>AttributeType</code>
	 * 
	 * @param predefValues
	 *           the predefinied values from the selected
	 *           <code>AttributeType</code>
	 */
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
