package data;

import gui.VennMaker;
import interview.panels.other.AttributeCheckBox;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


public class AttributeType implements Serializable, Transferable, Cloneable
{

	public static final String	ACTOR					= "ACTOR";

	public static final String	STANDARD_RELATION	= "STANDARDRELATION";

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private int						id						= -1;

	/**
	 * Bezeichnung des Attributtyps. Muss nicht eindeutig sein.
	 */
	private String					label;

	/**
	 * Moegliche Bereiche fuer die Attributsgueltigkeit.
	 */
	public enum Scope
	{
		NETWORK("network"), PROJECT("project");

		private String	label;

		private Scope(String s)
		{
			this.label = s;
		}

		public String getTypeName()
		{
			return label;
		}
	}

	/**
	 * Bereich der Gueltigkeit: sind Werte netzwerkweit oder projektweit
	 * (Standard) fest?
	 */
	private Scope		scope	= Scope.PROJECT;

	/**
	 * Relationsattribut oder Akteursattribut
	 */
	private String		type	= "ACTOR";

	/**
	 * Kann eine optionale Beschreibung des Attributtyps enthalten.
	 */
	private String		description;

	/**
	 * Welche (textuelle) Fragestellung bildet dieser Attributtyp ab?
	 */
	private String		question;

	/**
	 * Kann eine Liste von moeglichen Werten enthalten.
	 */
	private Object[]	predefinedValues;

	/**
	 * Kann einen Standardwert enthalten. Wenn predefinedValues gesetzt ist muss
	 * defaultValue in predefinedValues vorkommen.
	 */
	private Object		defaultValue;

	public AttributeType()
	{
		id = VennMaker.getInstance().getProject().getNewAttributeTypeId();
	}

	public AttributeType(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public Scope getScope()
	{
		return scope;
	}

	public void setScope(Scope scope)
	{
		this.scope = scope;
	}

	/**
	 * 
	 * @return Type collector
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * 
	 * @param type
	 *           collector
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return label;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getQuestion()
	{
		return question;
	}

	public void setQuestion(String question)
	{
		this.question = question;
	}

	public Object[] getPredefinedValues()
	{
		return predefinedValues;
	}

	/**
	 * 
	 * @return numerical representations of the attribute values
	 */
	public ArrayList<Object> getPredefinedValuesCodes()
	{
		if (predefinedValues == null)
			return new ArrayList<Object>();

		ArrayList<Object> codes = new ArrayList<Object>();

		int counter = 1;
		for (Object tmp : predefinedValues)
		{
			codes.add(counter);
			counter++;
		}
		return codes;
	}

	/**
	 * 
	 * @param value
	 *           attribute value
	 * @return numerical representation of the attribute value
	 */
	public int getPredefinedValueCode(String value)
	{
		if (predefinedValues != null)
		{
			ArrayList<Object> list = new ArrayList<Object>(
					Arrays.asList(this.predefinedValues));

			return list.indexOf(value) + 1;
		}

		return -1;
	}

	public Object getPredefinedValue(int q)
	{
		return this.predefinedValues[q];
	}

	public void setPredefinedValue(Object o, int q)
	{
		this.predefinedValues[q] = o;
	}

	public void setPredefinedValues(Object[] predefinedValues)
	{
		this.predefinedValues = predefinedValues;
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue)
	{
		boolean contains = false;
		if (defaultValue != null)
		{
			for (Object o : predefinedValues)
				if (o.equals(defaultValue))
					contains = true;
			if (!contains)
				throw new IllegalArgumentException(
						"default value needs to be a predefined value!");
		}
		this.defaultValue = defaultValue;
	}

	public Boolean isFirst(Object value)
	{
		// Ergibt nur Sinn bei Liste vorgegebener Werte
		if (this.predefinedValues == null || this.predefinedValues.length <= 0)
			throw new IllegalArgumentException(
					"IsFirst() useless for non categorical attribute types!");

		return this.predefinedValues[0] == value;
	}

	public Boolean isLast(Object value)
	{
		// Ergibt nur Sinn bei Liste vorgegebener Werte
		if (this.predefinedValues == null || this.predefinedValues.length <= 0)
			throw new IllegalArgumentException(
					"IsLast() useless for non categorical attribute types!");

		return this.predefinedValues[this.predefinedValues.length - 1] == value;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj instanceof AttributeType)
		{
			AttributeType atype = (AttributeType) obj;

			String aLabel = atype.getLabel();
			String aDesc = atype.getDescription();
			Scope aScope = atype.getScope();
			String aType = atype.getType();
			String aQuestion = atype.getQuestion();

			if (aLabel != null)
				if (!aLabel.equals(this.label))
					return false;

			if (aDesc != null && this.description != null)
			{
				if (!aDesc.equals(this.description))
					return false;
			}

			if (!aScope.equals(this.scope))
				return false;

			if (aType == null)
				return false;

			if (!aType.equals(this.type))
				return false;

			if (aQuestion != null && this.question != null)
			{
				if (!aQuestion.equals(this.question))
					return false;
			}

			Object[] o = atype.getPredefinedValues();
			Object[] mine = this.getPredefinedValues();

			if (o == null && mine == null)
				return true;
			if (o == null || mine == null)
				return false;
			else if (o.length != mine.length)
				return false;
			else
			{
				for (int i = 0; i < o.length; i++)
				{
					if (!mine[i].equals(o[i]))
						return false;
				}
			}
			return true;
		}

		return false;
	}

	public AttributeType clone()
	{
		try
		{
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(bOut);

			oOut.writeObject(this);

			ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
			ObjectInputStream oIn = new ObjectInputStream(bIn);

			AttributeType a = (AttributeType) oIn.readObject();
			bOut.close();
			oOut.close();
			bIn.close();
			oIn.close();

			a.id = VennMaker.getInstance().getProject().getNewAttributeTypeId(); // neue
																										// ID
																										// (UNIQUE)
			return a;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void changeTo(AttributeType a)
	{
		this.defaultValue = a.defaultValue;
		this.description = a.description;
		// this.id = a.id; //id nicht �bernehmen (UNIQUE)
		this.predefinedValues = a.predefinedValues;
		this.label = a.label;
		this.question = a.question;
		this.scope = a.scope;
		this.type = a.type;
	}

	@Override
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException
	{
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return AttributeCheckBox.flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor arg0)
	{
		return arg0.equals(AttributeCheckBox.ATTRIBUTE_FLAVOR);
	}
}
