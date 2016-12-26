/**
 * 
 */
package events;

import data.AttributeSubject;
import data.AttributeType;
import data.Netzwerk;

/**
 * Wird erzeugt, wenn ein Atribute eines Akteurs oder einer Relation geändert
 * wird.
 * 
 * 
 * 
 */
public class SetAttributeEvent extends VennMakerEvent
{
	private AttributeSubject	subject;

	private AttributeType		type;

	private Object					value;

	private Object					oldValue;

	private Netzwerk				network;

	public SetAttributeEvent(AttributeSubject subject, AttributeType type,
			Netzwerk network, Object value)
	{
		super();
		this.subject = subject;
		this.type = type;
		this.network = network;
		this.value = value;
		this.oldValue = subject.getAttributeValue(type, network);

	}

	/**
	 * Ermöglicht es, zum nächsten oder vorigen Wert zu springen.
	 * 
	 * @param subject
	 * @param type
	 * @param network
	 * @param jump
	 *           -1 oder 1
	 */
	public SetAttributeEvent(AttributeSubject subject, AttributeType type,
			Netzwerk network, int jump)
	{
		super();
		this.subject = subject;
		this.type = type;
		this.network = network;

		assert jump == 1 || jump == -1;
		assert type.getPredefinedValues().length > 0;

		this.oldValue = subject.getAttributeValue(type, network);

		// Wenn das Attribut noch nicht gesetzt ist und ein Standaardwert
		// konfiguriert ist tun wir so als hätte das Attribut vorher diesen Wert.
		if (this.oldValue == null && type.getDefaultValue() != null)
			this.oldValue = type.getDefaultValue();

		int index = -1;
		for (int i = 0; i < type.getPredefinedValues().length; i++)
		{
			if (type.getPredefinedValues()[i] == this.oldValue)
				index = i;
		}

		// Ein Sprung ist nur möglich wenn der vorige Wert in der Liste der
		// möglichen Werte auftaucht ...
		assert index >= 0;
		// ... und der Wert nach Sprung existiert.
		if ((index + jump) >= 0
				&& type.getPredefinedValues()[index + jump] != null)
			this.value = type.getPredefinedValues()[index + jump];
	}

	public Object getValue()
	{
		return this.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getRedoEvent()
	 */
	@Override
	public VennMakerEvent getRepeatEvent()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getUndoEvent()
	 */
	@Override
	public VennMakerEvent getUndoEvent()
	{
		return new SetAttributeEvent(subject, type, network, oldValue)
		{
			@Override
			public boolean isUndoevent()
			{
				return true;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see events.VennMakerEvent#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Attribut gesetzt";
	}

	public AttributeSubject getSubject()
	{
		return subject;
	}

	public AttributeType getType()
	{
		return type;
	}

	public Object getOldValue()
	{
		return oldValue;
	}

	public Netzwerk getNetwork()
	{
		return network;
	}

}
