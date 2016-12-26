package gui.configdialog.save;

import java.util.Vector;

import data.AttributeType;

public class TriggerSaveElement extends SaveElement
{
	private AttributeType	mainAttributeType;

	private AttributeType	mainGeneratorType;

	private Vector<String>	relationCollector;

	private Vector<String>	relationCollectorValues;

	public TriggerSaveElement(AttributeType mainAttributeType,
			AttributeType mainGeneratorType, Vector<String> relationCollector,
			Vector<String> relationCollectorValues)
	{
		this.mainAttributeType = mainAttributeType;
		this.mainGeneratorType = mainGeneratorType;
		this.relationCollector = relationCollector;
		this.relationCollectorValues = relationCollectorValues;
	}

	/**
	 * @return the mainAttributeType
	 */
	public AttributeType getMainAttributeType()
	{
		return mainAttributeType;
	}

	/**
	 * @return the mainGeneratorType
	 */
	public AttributeType getMainGeneratorType()
	{
		return mainGeneratorType;
	}

	/**
	 * @return the relationCollector
	 */
	public Vector<String> getRelationCollector()
	{
		return relationCollector;
	}

	/**
	 * 
	 * @return the relationCollector values
	 */
	public Vector<String> getRelationCollectorValues()
	{
		return relationCollectorValues;
	}
}
