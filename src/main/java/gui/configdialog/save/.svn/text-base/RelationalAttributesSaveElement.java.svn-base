package gui.configdialog.save;

import java.util.Map;
import java.util.Vector;

import data.AttributeType;

public class RelationalAttributesSaveElement extends SaveElement
{
	/**
	 * Holds the attribute types of the relations
	 */
	private Vector<AttributeType>	relationTypes;

	/**
	 * a <code>Map</code> containing if a relation is directed or not
	 */
	private Map<String, Boolean>	directedRelations;

	/**
	 * Constructs a new <code>SaveElement</code> for
	 * <code>CDialogEditRelationalAttributeTypes</code>
	 * 
	 * @param relationTypes
	 *           Holds the attribute types of the relations
	 * @param directedRelations
	 *           a <code>Map</code> containing if a relation is directed or not
	 */
	public RelationalAttributesSaveElement(Vector<AttributeType> relationTypes,
			Map<String, Boolean> directedRelations)
	{
		this.relationTypes = relationTypes;
		this.directedRelations = directedRelations;
	}

	/**
	 * Returns a <code>Vector</code> with the attribute types
	 * 
	 * @return a <code>Vector</code> with the attribute types
	 */
	public Vector<AttributeType> getAttributeTypes()
	{
		return this.relationTypes;
	}

	/**
	 * Return a <code>Map</code> containing if a relation is directed or not
	 * 
	 * @return a <code>Map</code> containing if a relation is directed or not
	 */
	public Map<String, Boolean> getDirectedRelations()
	{
		return directedRelations;
	}

	/**
	 * Sets a <code>Vector</code> with the attribute types
	 * 
	 * @param relationTypes
	 *           a <code>Vector</code> with the attribute types
	 */
	public void setRelationTypes(Vector<AttributeType> relationTypes)
	{
		this.relationTypes = relationTypes;
	}

	/**
	 * Sets a <code>Map</code> containing if a relation is directed or not
	 * 
	 * @param directedRelations
	 *           a <code>Map</code> containing if a relation is directed or not
	 */
	public void setDirectedRelations(Map<String, Boolean> directedRelations)
	{
		this.directedRelations = directedRelations;
	}
}
