package gui.configdialog;

import gui.VennMaker;

import java.util.Vector;

import data.AttributeType;

/**
 * contains functions needed to control attributetypes, or other simple requests
 * 
 * 
 * 
 */
public class AuxiliaryFunctions
{
	/**
	 * checks, if the given type is suitable for a relationColor; it basically
	 * looks at the predefined values of the attributes of the given type
	 * 
	 * @param tempType
	 *           the type to be checked for predefined values
	 * @return true, if there are suitable attributes of this type, else false
	 */
	static public boolean checkType(String tempType)
	{
		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete(tempType);

		for (AttributeType at : aTypes)
		{
			if (at.getPredefinedValues() != null)
			{
				return true;
			}
		}
		return false;
	}

}
