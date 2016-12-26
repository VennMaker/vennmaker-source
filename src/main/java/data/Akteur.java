package data;

import data.AttributeType.Scope;
import events.AddActorEvent;
import events.AddRelationEvent;
import events.ChangeDirectionEvent;
import events.ChangeTypeOfRelationEvent;
import events.MoveActorEvent;
import events.RelationEvent;
import events.RemoveActorEvent;
import events.RemoveRelationEvent;
import events.RenameActorEvent;
import events.VennMakerEvent;
import gui.VennMaker;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Die Klasse Akteur repräsentiert einen Akteur mit all seinen Eigenschaften,
 * dazu zählen auch Relationen zu anderen Akteuren.
 * 
 */
public class Akteur implements Serializable, QuestionSubject, AttributeSubject,
		Comparable<Akteur>
{
	/**
	 * 
	 */
	private static final long														serialVersionUID	= 1L;

	/**
	 * Zaehler der aktuellen ID - zur Vergabe der eindeutigen ID
	 */
	private static int																actorIdCount		= 0;

	/**
	 * Eine eindeutige ID...
	 */
	private int																			id;

	/**
	 * Der Name des Akteurs
	 */
	private String																		name;

	/**
	 * History über Relationsmengen... Analog zu Undo/Redo-Queue, wer kam
	 * eigentlich auf die Idee, keine relationale Datenbank zu implementieren!?
	 */
	private Map<Netzwerk, LinkedList<Map<Akteur, Vector<Relation>>>>	relationsHistory;

	/**
	 * Kopiert alle netzwerkabhängigen Eigenschaften des Akteurs anhand eines
	 * Ausgangsnetzwerks für ein neues.
	 * 
	 * @param src
	 *           Das Netzwerk für das die Eigenschaften gelten.
	 * @param dst
	 *           Das Netzwerk für das die Eigenschaften auch gelten sollen.
	 */
	public void copyNetworkProperties(Netzwerk src, Netzwerk dst)
	{
		setLocation(dst, getLocation(src));

		for (AttributeType attributeType : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if (attributes.get(dst) == null)
				attributes.put(dst, new HashMap<AttributeType, Object>());

			if (attributes.get(src) != null)
				attributes.get(dst).put(attributeType,
						attributes.get(src).get(attributeType));
		}
	}

	/**
	 * Merged alle netzwerkabhaengigen Eigenschaften des Akteurs anhand eines
	 * Ausgangsnetzwerks fuer ein neues.
	 * 
	 * @param src
	 *           Das Netzwerk fuer das die Eigenschaften gelten.
	 * @param dst
	 *           Das Netzwerk fuer das die Eigenschaften auch gelten sollen.
	 */
	public void mergeNetworkProperties(Netzwerk src, Netzwerk dst)
	{
		setLocation(dst, getLocation(src));

		for (AttributeType attributeType : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if (attributes.get(dst) == null)
				attributes.put(dst, new HashMap<AttributeType, Object>());
			/* for stability reasons - check source too */
			if (attributes.get(src) == null)
				attributes.put(src, new HashMap<AttributeType, Object>());

			attributes.get(dst).put(attributeType,
					attributes.get(src).get(attributeType));
		}
	}

	/**
	 * Kopiert alle netzwerkabhaengigen Eigenschaften des Akteurs anhand eines
	 * virtuellen Ausgangsnetzwerks fuer ein neues. Virtuelle Netzwerke sind
	 * Netzwerke, die ueber den ConfigDialog aus einer Datei geladen wurden
	 * 
	 * @param src
	 *           Netzwerk fuer das die Eigenschaften gelten.
	 * 
	 * @param dst
	 *           Netzwerk fuer das die Eigenschaften gelten sollen.
	 */
	public void copyVirtualNetworkProperties(Netzwerk src, Netzwerk dst)
	{
		Vector<AttributeType> attributeTypes = VennMaker.getInstance()
				.getProject().getAttributeTypes();

		for (AttributeType type : attributeTypes)
		{
			if (attributes.get(dst) == null)
				attributes.put(dst, new HashMap<AttributeType, Object>());

			Map<AttributeType, Object> oldAttributes = attributes.get(src);
			Set<Map.Entry<AttributeType, Object>> oldSet = oldAttributes
					.entrySet();

			for (Map.Entry<AttributeType, Object> iterator : oldSet)
			{
				Map.Entry<AttributeType, Object> entry = iterator;

				if (!type.toString().equals(entry.getKey().toString()))
					continue;

				attributes.get(dst).put(type, entry.getValue());
				break;
			}
		}

	}

	/**
	 * Liefert die Position (Mittelpunkt) in VennMaker-Koordinaten für das
	 * angegebene Netzwerk zurück.
	 * 
	 * @param n
	 *           Das Netzwerk, in dem der Akteur enthalten ist und für das die
	 *           Koordinaten abgefragt werden. Darf nicht <code>null</code> sein.
	 * @return Einen Punkt in VennMaker-Koordinaten oder <code>null</code> wenn
	 *         kein solcher Punkt gefunden werden konnte.
	 * 
	 */
	public Point2D getLocation(Netzwerk n)
	{
		assert (n != null);

		// Kann null sein, wenn auch keine Polarkoordinaten gefunden
		// wurden.
		return locations.get(n);
	}

	/**
	 * Setzt den neuen Aufenthaltsort des Akteurs (Mittelpunkt) im angegebenen
	 * Netzwerk in VennMaker-Koordinaten.
	 * 
	 * @param n
	 *           Das entsprechende Netzwerk. Darf nicht <code>null</code> sein.
	 * @param point
	 *           Der entsprechende Punkt. Darf nicht <code>null</code> sein.
	 */
	public void setLocation(Netzwerk n, Point2D point)
	{
		assert (n != null);
		assert (point != null);
		this.locations.put(n, point);
	}

	/**
	 * Speichert die Position des Akteurs im VennMaker-Koordinatensystem
	 * (Absolutes System, mit unendlicher Ausdehnung und (0,0) als Mittelpunkt.
	 */
	private Map<Netzwerk, Point2D>				locations		= new HashMap<Netzwerk, Point2D>();

	/**
	 * Die Groesse des Akteurs im Netzwerk.
	 */
	@Deprecated
	public Map<Netzwerk, Integer>					groesse			= new HashMap<Netzwerk, Integer>();

	/**
	 * Die zu Beginn im Wizard zugewiesene Standardgroesse (in Pixeln)
	 */
	@Deprecated
	private Integer									defaultGroesse	= 9;

	/**
	 * Mapping from networks to relations
	 */
	private Map<Netzwerk, Vector<Relation>>	relations;

	/**
	 * Der Typ des Akteurs.
	 */
	@Deprecated
	public AkteurTyp									typ;

	/**
	 * Ein Kommentar...
	 */
	@Deprecated
	private Kommentar<? extends Object>			kommentar;

	/**
	 * Erzeugt einen neuen Akteur mit dem angegebenen Namen.
	 * 
	 * @param name
	 */
	public Akteur(String name)
	{
		setId(actorIdCount);
		actorIdCount++;
		relations = new HashMap<Netzwerk, Vector<Relation>>();
		this.relationsHistory = new HashMap<Netzwerk, LinkedList<Map<Akteur, Vector<Relation>>>>();
		setName(name);
	}

	/**
	 * Erzeugt einen Akteur ohne Namen
	 */
	public Akteur()
	{
		this("");
	}

	/**
	 * Damit Drag'n'Drop keinen Müll anzeigt.
	 */
	@Override
	public String toString()
	{
		return getName();
	}

	@XStreamOmitField
	private boolean	isRegistered	= false;

	/**
	 * Erzwingt das Anmelden aller Modellobjekte am <code>EventProcessor</code>.
	 * Nach dem Deserialisieren muss diese Methode explizit aufgerufen werden!
	 * Dies wird von <code>Projekt.registerEventListeners()</code> erledigt.
	 */
	protected void registerEventListeners()
	{
		// Ensure that this actor is registered only once.
		if (isRegistered)
		{
			return;
		}
		isRegistered = true;
		EventProcessor.getInstance().addEventListener(
				new EventListener<AddActorEvent>()
				{

					@Override
					public void eventOccured(VennMakerEvent event)
					{
						AddActorEvent ev = (AddActorEvent) event;
						if (ev.getAkteur().equals(Akteur.this))
						{
							setLocation(ev.getNetzwerk(), ev.getLocation());
							if (ev.isUndoevent())
							{
								assert (relationsHistory.get(ev.getNetzwerk()).size() > 0);
								Map<Akteur, Vector<Relation>> m = relationsHistory.get(
										ev.getNetzwerk()).removeLast();

								for (Akteur a : m.keySet())
								{
									a.relations.get(ev.getNetzwerk()).addAll(m.get(a));
								}
							}
						}
					}

					@Override
					public Class<AddActorEvent> getEventType()
					{
						return AddActorEvent.class;
					}
				});
		EventProcessor.getInstance().addEventListener(
				new EventListener<RemoveActorEvent>()
				{

					@Override
					public void eventOccured(VennMakerEvent event)
					{
						RemoveActorEvent ev = (RemoveActorEvent) event;
						if (ev.getAkteur().equals(Akteur.this))
						{
							if (!ev.isUndoevent())
							{
								// Speichere alle Relationen, die gelöscht werden
								// müssen.
								// Ist zwar kompliziert, da je zwei Richtungen
								// betrachtet werden müssen,
								// aber scheint zu gehen.
								Map<Akteur, Vector<Relation>> curState = new HashMap<Akteur, Vector<Relation>>();
								curState.put(Akteur.this, new Vector<Relation>(
										relations.get(ev.getNetzwerk())));
								for (Akteur a : ev.getNetzwerk().getAkteure())
								{

									if (a == Akteur.this)
										continue;

									Vector<Relation> curInActor = new Vector<Relation>();
									for (Relation r : a.getRelations(ev.getNetzwerk()))
									{
										if (r.getAkteur().equals(Akteur.this))
										{
											curInActor.add(r);
										}
									}
									curState.put(a, curInActor);
									a.relations.get(ev.getNetzwerk()).removeAll(
											curInActor);
								}

								if (relationsHistory.get(ev.getNetzwerk()) == null)
								{
									relationsHistory.put(
											ev.getNetzwerk(),
											new LinkedList<Map<Akteur, Vector<Relation>>>());
								}
								relationsHistory.get(ev.getNetzwerk()).add(curState);
								relations.get(ev.getNetzwerk()).clear();
							}
						}
					}

					@Override
					public Class<RemoveActorEvent> getEventType()
					{
						return RemoveActorEvent.class;
					}
				});

		EventProcessor.getInstance().addEventListener(
				new EventListener<MoveActorEvent>()
				{

					@Override
					public void eventOccured(VennMakerEvent event)
					{
						MoveActorEvent ev = (MoveActorEvent) event;
						if (!ev.getAkteur().equals(Akteur.this))
						{
							// Ignoriere Events für andere Modelle
							// System.out.println("ignoriere move von
							// "+ev.getAkteur()+"("+ev.getAkteur().hashCode()+"), weil
							// ich selbst "+this+"("+this.hashCode()+") bin");
							return;
						}
						Point2D loc = getLocation(ev.getNetzwerk());
						Point2D newLoc = new Point2D.Double(loc.getX()
								+ ev.getMove().getX(), loc.getY() + ev.getMove().getY());
						setLocation(ev.getNetzwerk(), newLoc);
					}

					@Override
					public Class<MoveActorEvent> getEventType()
					{
						return MoveActorEvent.class;
					}
				});
		EventProcessor.getInstance().addEventListener(
				new EventListener<ChangeTypeOfRelationEvent>()
				{

					@Override
					public void eventOccured(VennMakerEvent event)
					{
						ChangeTypeOfRelationEvent ev = (ChangeTypeOfRelationEvent) event;
						if (ev.getAkteur().equals(Akteur.this))
						{
							ev.getRelation().setTyp(ev.getNewType());

						}
					}

					@Override
					public Class<ChangeTypeOfRelationEvent> getEventType()
					{
						return ChangeTypeOfRelationEvent.class;
					}
				});
		EventProcessor.getInstance().addEventListener(
				new EventListener<ChangeDirectionEvent>()
				{

					@Override
					public void eventOccured(VennMakerEvent event)
					{
						ChangeDirectionEvent ev = (ChangeDirectionEvent) event;
						if (ev.getAkteur().equals(Akteur.this))
						{
							// Somehow ugly...
							Akteur.this.getRelations(ev.getNetzwerk()).remove(
									ev.getRelation());
							ev.getRelation().getAkteur()
									.getRelations(ev.getNetzwerk())
									.add(ev.getRelation());
							ev.getRelation().setAkteur(Akteur.this);
						}
					}

					@Override
					public Class<ChangeDirectionEvent> getEventType()
					{
						return ChangeDirectionEvent.class;
					}
				});
		EventProcessor.getInstance().addEventListener(
				new EventListener<AddRelationEvent>()
				{

					@Override
					public void eventOccured(VennMakerEvent event)
					{
						if (((RelationEvent) event).getAkteur() != Akteur.this)
						{
							// Ignoriere Events für andere Modelle
							return;
						}

						RelationEvent ev = (RelationEvent) event;
						getRelations(ev.getNetzwerk()).add(ev.getRelation());
					}

					@Override
					public Class<AddRelationEvent> getEventType()
					{
						return AddRelationEvent.class;
					}
				});
		EventProcessor.getInstance().addEventListener(
				new EventListener<RemoveRelationEvent>()
				{

					@Override
					public void eventOccured(VennMakerEvent event)
					{
						if (((RelationEvent) event).getAkteur() != Akteur.this)
						{
							// Ignoriere Events für andere Modelle
							return;
						}
						RelationEvent ev = (RelationEvent) event;
						// relationen.remove(ev.getRelation()); // URGH!
						getRelations(ev.getNetzwerk()).remove(ev.getRelation());

					}

					@Override
					public Class<RemoveRelationEvent> getEventType()
					{
						return RemoveRelationEvent.class;
					}
				});

		EventProcessor.getInstance().addEventListener(
				new EventListener<RenameActorEvent>()
				{

					@Override
					public void eventOccured(VennMakerEvent event)
					{
						if (((RenameActorEvent) event).getAkteur() != Akteur.this)
						{
							return;
						}
						RenameActorEvent ev = (RenameActorEvent) event;
						setName(ev.getNewName());
					}

					@Override
					public Class<RenameActorEvent> getEventType()
					{
						return RenameActorEvent.class;
					}

				});

	}

	public int getId()
	{
		return this.id;
	}

	private void setId(int id)
	{
		this.id = id;
	}

	private Set<Netzwerk>	netzwerke;

	/**
	 * Liefert eine Menge der Netzwerke zurück, zu denen der Akteur gehört.
	 * 
	 * @return List of networks
	 */
	public Set<Netzwerk> getNetzwerke()
	{
		// Typisch: Wird von XStream nicht gesetzt und ist nicht immer definiert.
		if (netzwerke == null)
			netzwerke = new HashSet<Netzwerk>();
		return netzwerke;
	}

	/**
	 * Zeigt an, dass der Akteur zu <code>n</code> dazugehört.
	 * 
	 * @param n
	 */
	public void addNetzwerk(Netzwerk n)
	{
		getNetzwerke().add(n);
	}

	/**
	 * Zeigt an, dass der Akteur aus <code>n</code> entfernt wurde.
	 * 
	 * @param n
	 */
	public void removeNetzwerk(Netzwerk n)
	{
		getNetzwerke().remove(n);
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns a set of all relations starting at this actor in this network.
	 * 
	 * @param n
	 *           The current network.
	 * @return A list of all relations (may be empty).
	 */
	public Vector<Relation> getRelations(Netzwerk n)
	{
		// Backward-Comp.
		if (this.relations == null){
			this.relations = new HashMap<Netzwerk, Vector<Relation>>();
		}
		
		if (!this.relations.containsKey(n)){
			this.relations.put(n, new Vector<Relation>());
		}

		return this.relations.get(n);
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

	/**
	 * 
	 * @param actor
	 *           the actor, the relation leads to
	 * @param network
	 *           the current network
	 * 
	 * @return the relation between actor and the current actor
	 */
	public Relation getRelationTo(Akteur actor, Netzwerk network)
	{
		for (final Relation relation : getRelations(network))
			if (relation.getAkteur() == actor)
				return relation;
		return null;
	}

	/**
	 * Returns the <code>Relation</code> between this actor and the given actor
	 * with the given <code>Netzwerk</code> and <code>AttributeType</code>. Or
	 * <code>null</code> if no such <code>Relation</code> exists
	 * 
	 * @param actor
	 *           the target actor
	 * @param network
	 *           the network in which the <code>Relation</code> should be
	 *           searched
	 * @param type
	 *           the given <code>AttributeType</code> of the
	 *           <code>Relation</code>
	 * @return the <code>Relation</code> between this actor and the given actor
	 *         with the given <code>Netzwerk</code> and
	 *         <code>AttributeType</code>. Or <code>null</code> if no such
	 *         <code>Relation</code> exists
	 */
	public Relation getRelationTo(Akteur actor, Netzwerk network,
			AttributeType type)
	{
		for (Relation r : this.getRelations(network))
			if ((r.getAkteur() == actor)
					&& (r.getAttributeValue(type, network) != null))
				return r;
		return null;
	}

	
	/**
	 * Returns all <code>Relations</code> between this actor and the given actor
	 * with the given <code>Netzwerk</code> and <code>AttributeType</code>. Or
	 * <code>null</code> if no such <code>Relation</code> exists
	 * 
	 * @param actor
	 *           the target actor
	 * @param network
	 *           the network in which the <code>Relation</code> should be
	 *           searched
	 * @param type
	 *           the given <code>AttributeType</code> of the
	 *           <code>Relation</code>
	 * @return the <code>Relation</code> between this actor and the given actor
	 *         with the given <code>Netzwerk</code> and
	 *         <code>AttributeType</code>. Or <code>null</code> if no such
	 *         <code>Relation</code> exists
	 */
	public Vector<Relation> getAllRelationsTo(Akteur actor, Netzwerk network,
			AttributeType type)
	{
		Vector<Relation> relations = new Vector<Relation>();
		for (Relation r : this.getRelations(network))
			if ((r.getAkteur() == actor)
					&& (r.getAttributeValue(type, network) != null)){
			relations.add(r);
			}

		return relations;
	}	
	/**
	 * 
	 * @param n
	 *           the current network
	 * 
	 * @return number of circle where the actor is situated
	 */
	public int getCircle(Netzwerk n)
	{

			double actorPosX = getLocation(n).getX();
			double actorPosY = getLocation(n).getY();
			
			return n.getCircle(0,0,actorPosX,actorPosY);
	}

	/**
	 * 
	 * @param n
	 *           the current network
	 * 
	 * @return number of sector where the actor is situated
	 */
	public int getSector(Netzwerk n)
	{
		double actorPosX = getLocation(n).getX();
		double actorPosY = getLocation(n).getY();
		
		return n.getSector(0, 0, actorPosX, actorPosY);
	}

	/**
	 * Hier werden fuer jedes Netzwerk die Attribute gespeichert.
	 */
	private Map<Netzwerk, Map<AttributeType, Object>>	attributes	= new HashMap<Netzwerk, Map<AttributeType, Object>>();

	/**
	 * attention this getter will change the value if its "null" to the default
	 * value of this attribute type
	 * 
	 * @return value of given attribute in given network
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
		setAttributeValue(type, network, value, VennMaker.getInstance()
				.getProject().getNetzwerke());
	}

	@Override
	/**
	 * sets the values of "type" in all networks of the vector or when scope==network only to the given "network" to the given value
	 */
	public void setAttributeValue(AttributeType type, Netzwerk network,
			Object value, Vector<Netzwerk> networks)
	{
		
		if ( (type != null) && (type.getScope() == Scope.NETWORK) )
		{
			putAttributeValue(type, network, value);
		}
		else
		{
			for (Netzwerk n : networks)
			{
				putAttributeValue(type, n, value);
			}
		}
	}

	/**
	 * inserts the value for the given AttributeType in the given Network
	 * 
	 * @param type
	 *           the type, which is about to receive a new value
	 * @param network
	 *           the network, in which this change will take place
	 * @param value
	 *           the new value for the given type in the given network
	 */
	private void putAttributeValue(AttributeType type, Netzwerk network,
			Object value)
	{
		if (attributes.get(network) == null)
			attributes.put(network, new HashMap<AttributeType, Object>());

		attributes.get(network).put(type, value);
	}

	public float getGroesse(Netzwerk netzwerk)
	{
		return netzwerk.getActorSizeVisualizer().getSize(this, netzwerk);
	}

	/**
	 * Wird nach dem Deserialisieren ausgefuehrt.
	 */
	private Object readResolve()
	{
		if (attributes == null)
			attributes = new HashMap<Netzwerk, Map<AttributeType, Object>>();
		return this;
	}

	public int compareTo(Akteur o)
	{
		String name = this.toString();
		String name2 = o.toString();

		if (name == null)
			name = "";
		if (name2 == null)
			name2 = "";

		return name.compareToIgnoreCase(name2);

	}

	/**
	 * removes the value of an attribute in the given network for this actor
	 * 
	 * @param type
	 *           the attributetype, of which the value needs to be removed
	 * @param net
	 *           the specific network, where the attribute has to be removed
	 */
	public void removeAttributeValue(AttributeType type, Netzwerk net)
	{
		attributes.get(net).remove(type);
	}

	/**
	 * Setzen bzw abfragen der aktuellen id
	 */
	public void setGlobalId(int newId)
	{
		actorIdCount = newId;
	}

	public int getGlobalId()
	{
		return actorIdCount;
	}

	/**
	 * resets actorIdCount to 1 and changes active actor's id to 0
	 */
	public void resetGlobalID()
	{
		setId(0);
		actorIdCount = 1;
	}

	public void addRelation(Netzwerk n, Relation r)
	{
		Vector<Relation> relations = this.getRelations(n);
		relations.add(r);
		this.relations.put(n, relations);
	}
}
