package gui.configdialog.save;

import java.util.Map;

import data.AttributeType;

/**
 * Objects of this class contains all necessary information to save the
 * contents of CDialogActorPie
 * 
 *
 */
public class ActorPieSaveElement extends SaveElement
{
	private Map<AttributeType, Object> typeAndSelection;
	private Map<AttributeType, Object> sectorColor;
	
	
	public ActorPieSaveElement(Map<AttributeType, Object> typeAndSelection,
			Map<AttributeType, Object> sectorColor)
	{
		this.typeAndSelection = typeAndSelection;
		this.sectorColor = sectorColor;
	}


	/**
	 * @return the typeAndSelection
	 */
	public Map<AttributeType, Object> getTypeAndSelection()
	{
		return typeAndSelection;
	}


	/**
	 * @return the sectorColor
	 */
	public Map<AttributeType, Object> getSectorColor()
	{
		return sectorColor;
	}
}
