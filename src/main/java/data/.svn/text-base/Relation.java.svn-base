package data;

import gui.VennMaker;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import data.AttributeType.Scope;

/**
 * Eine Relation beschreibt die Verbindung zwischen zwei Akteuren, wobei nur der
 * zweite Akteur in der Relation gespeichert wird. Der erste Akteur ergibt sich
 * aus der Position des Relation-Objekts.
 * 
 * 
 */
public class Relation implements QuestionSubject
{
	private RelationTyp			typ;

	private String					attributeCollectorValue	= "STANDARDRELATION";

	@Deprecated
	private Kommentar<String>	comment;

	/**
	 * Der Akteur, auf den die Relation zeigt
	 */
	private Akteur					akteur;

	/**
	 * Erzeugt eine neue Relation zum Akteur <code>akteur</code> vom gegebenen
	 * Typ.
	 * 
	 * @param akteur
	 *           Der Zielakteur (Pfeilspitze zeigt auf ihn bei gerichteten
	 *           relationen)
	 * @param collectorValue
	 *           Der Relationstyp
	 */
	public Relation(Akteur akteur, String collectorValue)
	{
		this.akteur = akteur;
		this.attributeCollectorValue = collectorValue;
		this.comment = new Kommentar<String>(""); //$NON-NLS-1$
	}
	
	public Relation(Netzwerk network, Akteur akteur, AttributeType type, Object attributeValue)
	{
		this.akteur = akteur;
		this.attributeCollectorValue = type.getType();
		this.setAttributeValue(type, network, attributeValue);
		this.comment = new Kommentar<String>(""); //$NON-NLS-1$
	}

	/**
	 * gibt den Akteur zurueck, auf den die Relation zeigt
	 * @return
	 * 	der entsprechende Akteur
	 */
	public Akteur getAkteur()
	{
		return this.akteur;
	}

	public void setAkteur(Akteur akteur)
	{
		this.akteur = akteur;
	}

	public RelationTyp getTyp()
	{
		return this.typ;
	}

	public void setTyp(RelationTyp typ)
	{
		this.typ = typ;
	}

	@Override
	public String toString()
	{
		return "Relation to " + akteur.getName() + ", Objekt: "
				+ super.toString();
	}

	/**
	 * Antworten auf Fragen als Abbildung Fragen-Label -> Antwort.
	 */
	private Map<String, String>	answers	= new HashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.QuestionSubject#getAnswer(java.lang.String)
	 */
	@Override
	public String getAnswer(String label)
	{
		return answers.get(label);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.QuestionSubject#saveAnswer(java.lang.String, java.lang.String)
	 */
	@Override
	public void saveAnswer(String label, String answer)
	{
		answers.put(label, answer);
	}

	private Map<Netzwerk, Map<AttributeType, Object>>	attributes	= new HashMap<Netzwerk, Map<AttributeType, Object>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.AttributeSubject#getAttributeValue(data.AttributeType,
	 * data.Netzwerk)
	 */
	@Override
	public Object getAttributeValue(AttributeType type, Netzwerk network)
	{
		if (type != null)
		{
			if (attributes.get(network) == null)
				attributes.put(network, new HashMap<AttributeType, Object>());
			if (attributes.get(network).get(type) == null
					&& type.getDefaultValue() != null)
				attributes.get(network).put(type, type.getDefaultValue());
			return attributes.get(network).get(type);
		}
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.AttributeSubject#getAttributes(data.Netzwerk)
	 */
	@Override
	public Map<AttributeType, Object> getAttributes(Netzwerk network)
	{
		if (attributes.get(network) == null)
			attributes.put(network, new HashMap<AttributeType, Object>());
		return attributes.get(network);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.AttributeSubject#setAttributeValue(data.AttributeType,
	 * data.Netzwerk, java.lang.Object)
	 */
	@Override
	public void setAttributeValue(AttributeType type, Netzwerk network,
			Object value)
	{
		this.setAttributeValue(type, network, value, VennMaker.getInstance()
				.getProject().getNetzwerke());
	}

	@Override
	public void setAttributeValue(AttributeType type, Netzwerk network,
			Object value, Vector<Netzwerk> networks)
	{
		if (type.getScope() == Scope.NETWORK)
		{

			if (attributes.get(network) == null)
				attributes.put(network, new HashMap<AttributeType, Object>());

			attributes.get(network).put(type, value);
		}
		else
		{

			for (Netzwerk n : networks)
			{
				if (attributes.get(n) == null)
					attributes.put(n, new HashMap<AttributeType, Object>());
				attributes.get(n).put(type, value);
			}
		}
	}

	public int getGroesse(Netzwerk netzwerk, AttributeType type)
	{
		return netzwerk.getRelationSizeVisualizer(this.attributeCollectorValue, type).getSize(this, netzwerk);
	}

	/**
	 * Wird nach dem Deserialisieren ausgeführt.
	 */
	private Object readResolve()
	{
		if (attributes == null)
			attributes = new HashMap<Netzwerk, Map<AttributeType, Object>>();
		return this;
	}

	public void removeAttributeValue(AttributeType type, Netzwerk net)
	{
		if (net != null)
		{
		attributes.get(net).remove(type);
		}
	}

	/**
	 * @return Aktueller Attributtyp
	 */
	public String getAttributeCollectorValue()
	{
		return this.attributeCollectorValue;
	}

	/**
	 * Setzen des Attributtyps
	 * 
	 * @param attributeCollector
	 */
	public void setAttributeCollectorValue(String attributeCollector)
	{
		this.attributeCollectorValue = attributeCollector;
	}

	/**
	 * @param network
	 * @param attributes
	 */
	public void setAttributes(Netzwerk network, Map<AttributeType, Object> attributes)
	{
		this.attributes.put(network, attributes);		
	}
}
