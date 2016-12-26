package gui.configdialog.save;

import data.AttributeType;

/**
 * Objects of this class save attributes wich are used by the
 * @see gui.configdialog.elements.CDialogCircle
 * 
 *
 */
public class CircleSaveElement extends SaveElement
{
	private AttributeType 	circleAttribute;
	
	private boolean 		isAsc;
	
	/**
	 * Creates an new CircleSaveElement 
	 * @param circleAttribute Attribute to save
	 * @param isAsc are circles ascending
	 */
	public CircleSaveElement(AttributeType circleAttribute, boolean isAsc)
	{
		this.circleAttribute = circleAttribute;
		this.isAsc = isAsc;
	}
	
	/**
	 * Returns the circle attribute
	 * @return AttributeType represented by the circles
	 */
	public AttributeType getCircleAttribute()
	{
		return circleAttribute;
	}

	/**
	 * 
	 * @return true if ascending, false otherwise
	 */
	public boolean isAsc()
	{
		return isAsc;
	}
}
