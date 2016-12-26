package gui.configdialog.save;

import data.AttributeType;

/**
 * Objects of this class contain save information of the
 * CDialogActorImage-Element
 * 
 * 
 * 
 */
public class ActorImageSaveElement extends SaveElement
{

	private AttributeType	aType;

	private int[]				indicies;

	private String[]			iconNames;

	private String[]			imageData;

	private String[]			iconsToDelete;

	public ActorImageSaveElement(AttributeType aType, int[] indicies,
			String[] imageData, String[] iconNames)
	{
		this.aType = aType;
		this.indicies = indicies;
		this.imageData = imageData;
		this.iconNames = iconNames;
	}

	public ActorImageSaveElement(AttributeType aType, int[] indicies)
	{
		this(aType, indicies, null, null);
	}

	/**
	 * Returns the selected indicies
	 * 
	 * @return selected indicies of attributeList
	 */
	public int[] getIndicies()
	{
		return this.indicies;
	}

	/**
	 * @return the newIcons
	 */
	public String[] getImageData()
	{
		return imageData;
	}

	/**
	 * @return the aType
	 */
	public AttributeType getType()
	{
		return aType;
	}

	public String[] getIconNames()
	{
		return iconNames;
	}

	/**
	 * Returns an <code>Array</code> of Icons which should be deleted
	 * 
	 * @return an <code>Array</code> of Icons which should be deleted
	 */
	public String[] getIconsToDelete()
	{
		return iconsToDelete;
	}

	/**
	 * Sets an <code>Array</code> of Icons which should be deleted
	 * 
	 * @param iconsToDelete
	 *           an <code>Array</code> of Icons which should be deleted
	 */
	public void setIconsToDelete(String[] iconsToDelete)
	{
		this.iconsToDelete = iconsToDelete;
	}

	/**
	 * Returns the selected <code>AttributeType</code>
	 * 
	 * @return the selected <code>AttributeType</code>
	 */
	public AttributeType getaType()
	{
		return aType;
	}

	/**
	 * Sets the selected <code>AttributeType</code>
	 * 
	 * @param aType
	 *           the selected <code>AttributeType</code>
	 */
	public void setaType(AttributeType aType)
	{
		this.aType = aType;
	}

	/**
	 * Sets the selected indicies for the current selected
	 * <code>AttributeType</code>
	 * 
	 * @param indicies
	 *           the selected indicies for the current selected
	 *           <code>AttributeType</code>
	 */
	public void setIndicies(int[] indicies)
	{
		this.indicies = indicies;
	}

	/**
	 * Sets the names of new added icons
	 * 
	 * @param iconNames
	 *           the names of new added icons
	 */
	public void setIconNames(String[] iconNames)
	{
		this.iconNames = iconNames;
	}

	/**
	 * Sets the base64 encoded added images
	 * 
	 * @param imageData
	 *           the base64 encoded added images
	 */
	public void setImageData(String[] imageData)
	{
		this.imageData = imageData;
	}
}
