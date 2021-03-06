package data;

import events.ActorInNetworkEvent;
import events.AddActorEvent;
import events.AddRelationEvent;
import events.ComplexEvent;
import events.MergeNetworkEvent;
import events.NewNetworkEvent;
import events.RemoveActorEvent;
import events.SetAttributeEvent;
import events.VennMakerEvent;
import files.FileOperations;
import files.VMPaths;
import gui.Messages;
import gui.VennMaker;
import gui.configdialog.elements.CDialogCircle;
import gui.configdialog.elements.CDialogNetworkClone;
import gui.configdialog.elements.CDialogSector;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Ein Netzwerk ist Bestandteil eines Projektes und enthaelt eine Liste von
 * Akteuren mit deren Eigenschaften.
 * 
 */
public class Netzwerk implements EventLogger
{

	private static int																networkCount	= 0;

	private EventLogger																evLogger			= new EventLoggerImpl();

	/**
	 * Die Akteure, die zu diesem Netzwerk gehören.
	 */
	private Vector<Akteur>															akteure;

	private int																			id					= -1;

	private String																		bezeichnung		= "";

	private BackgroundInfo															hintergrund;

	private Vector<Akteur>															filter;

	private ArrayList<CDialogSector>												tempSectors;

	private ArrayList<CDialogCircle>												tempCircles;

	private Vector<Akteur>															tempActors;

	private ActorImageVisualizer													actorImageVisualizer;

	private Vector<Akteur>															filteredActors	= new Vector<Akteur>();

	/**
	 * Notizen des Users zu jeder Netzwerkkarte
	 */
	private String																		metaInformation;

	private boolean																	isLogEvent;

	private Map<String, Map<AttributeType, RelationColorVisualizer>>	colorVisualizers;

	private Map<String, Map<AttributeType, RelationSizeVisualizer>>	sizeVisualizers;

	private Map<String, Map<AttributeType, RelationDashVisualizer>>	dashVisualizer;

	/**
	 * Enthält alle Akteure die zur Zeit im Netzwerk gesperrt sind. Gesperrte
	 * Akteure können nicht angewählt werden und es können keine Relationen zu
	 * ihnen gezeichnet werden Akteure werden gesperrt wenn sie nicht positiv auf
	 * eine Filterregel gematcht werden.
	 */
	private ArrayList<Akteur>														lockedActors;

	/**
	 * Enthält die gesperrten Relationen
	 */
	private ArrayList<Relation>													lockedRelations;

	/**
	 * Creates a new network.
	 */
	public Netzwerk()
	{
		this.id = networkCount++;
		akteure = new Vector<Akteur>();
		hintergrund = new BackgroundInfo();
		filter = new Vector<Akteur>();
		tempActors = new Vector<Akteur>();
		actorImageVisualizer = new ActorImageVisualizer();

		relationDashVisualizer = new HashMap<String, RelationDashVisualizer>();
		relationSizeVisualizer = new HashMap<String, RelationSizeVisualizer>();
		relationColorVisualizer = new HashMap<String, RelationColorVisualizer>();

		resetVisualizers();

		lockedActors = new ArrayList<Akteur>();
		lockedRelations = new ArrayList<Relation>();

		this.isLogEvent = true;
	}

	public int getId()
	{
		return id;
	}

	/**
	 * Needed for example while loading a template with networks. After Cloning
	 * the old, use the id of the old one (ONLY IF THE OLD ONE GETS DELETED)
	 * 
	 * @param id
	 */
	public void setId(int id)
	{
		this.id = checkID(id);

		if (this.id >= networkCount)
			networkCount = this.id + 1;
	}

	/**
	 * checks, if the given id is already taken or if it is free; if it is free,
	 * it returns the id - if it is taken, it returns a new free id
	 * 
	 * @param id
	 *           the id to check
	 * @return the id, which was given or a new, free one.
	 */
	private int checkID(int id)
	{
		int newId = id;
		int maxId = 1;

		for (Netzwerk n : VennMaker.getInstance().getProject().getNetzwerke())
		{
			if ((n.getId() == newId) && (!n.equals(this)))
			{
				maxId = (n.getId() > newId) ? n.getId() : newId;
				newId = maxId + 1;
			}
		}

		return newId;
	}

	@XStreamOmitField
	private boolean	isRegistered	= false;

	/**
	 * Erzwingt das Anmelden des Modellobjekts am <code>EventProcessor</code>.
	 * Nach dem Deserialisieren muss diese Methode explizit aufgerufen werden!
	 * Dies wird von <code>Projekt.registerEventListeners()</code> erledigt.
	 * Etwaig vorhande Akteure im Netzwerk werden nicht angemeldet. Dies wird
	 * ebenfalls zentral von <code>Projekt</code> erledigt.
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
						ActorInNetworkEvent ev = (ActorInNetworkEvent) event;
						if (ev.getNetzwerk() != Netzwerk.this)
							return;
						addAkteur(ev.getAkteur());
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
						ActorInNetworkEvent ev = (ActorInNetworkEvent) event;
						if (ev.getNetzwerk() != Netzwerk.this)
							return;
						removeAkteur(ev.getAkteur());
					}

					@Override
					public Class<RemoveActorEvent> getEventType()
					{
						return RemoveActorEvent.class;
					}
				});

		EventProcessor.getInstance().addEventListener(
				new EventListener<SetAttributeEvent>()
				{

					@Override
					public void eventOccured(VennMakerEvent event)
					{
						SetAttributeEvent ev = (SetAttributeEvent) event;

						// gebe den neuen Attributwert an das Subject weiter

						ev.getSubject().setAttributeValue(ev.getType(),
								ev.getNetwork(), ev.getValue());

						VennMakerActions.refreshFilter();
					}

					@Override
					public Class<SetAttributeEvent> getEventType()
					{
						return SetAttributeEvent.class;
					}
				});

	}

	public void addAkteur(Akteur akteur)
	{
		if (akteur != null)
		{
			akteur.addNetzwerk(this);
			akteure.add(akteur);

			VennMakerActions.refreshFilter();

		}
	}

	/**
	 * Loescht den angegebenen Akteur aus der Menge der Akteure des Netzwerkes
	 * 
	 * @param akteur
	 */
	public void removeAkteur(Akteur akteur)
	{
		if (akteur != null)
		{
			removeFilter(akteur);
			akteur.removeNetzwerk(this);
			akteure.remove(akteur);
		}
	}

	/**
	 * Gibt das Ego zurück, wenn in diesem Netzwerk gesetzt. Sonst null.
	 * 
	 * @return Ego
	 */
	public Akteur getEgo()
	{
		if (akteure.contains(VennMaker.getInstance().getProject().getEgo()))
			return VennMaker.getInstance().getProject().getEgo();
		else
			return null;
	}

	/*
	public Collection<Akteur> getAkteure()
	{
		return Collections.unmodifiableCollection(akteure);
	}
	*/
	
	
	public Vector<Akteur> getAkteure()
	{
		return akteure;
	}

	public BackgroundInfo getHintergrund()
	{
		return this.hintergrund;
	}

	public void setHintergrund(BackgroundInfo hintergrund)
	{
		this.hintergrund = hintergrund;
	}

	public String getBezeichnung()
	{
		if (bezeichnung == null)
			bezeichnung = "";

		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung)
	{
		if (bezeichnung != null)
			this.bezeichnung = bezeichnung;
		else
			this.bezeichnung = "";
	}

	public void addTemporaryActor(Akteur akt)
	{
		tempActors.add(akt);
	}

	/**
	 * Neues Blatt im Tree des ConfigDialogs im Knoten "Netzwerk" hinzufuegen
	 */

	public ArrayList<CDialogSector> getTemporarySectors()
	{
		if (tempSectors == null)
			tempSectors = new ArrayList<CDialogSector>();
		return tempSectors;
	}

	public ArrayList<CDialogCircle> getTemporaryCircles()
	{
		if (tempCircles == null)
			tempCircles = new ArrayList<CDialogCircle>();
		return tempCircles;
	}

	public Akteur getTemporaryActorAt(int index)
	{
		if (index < tempActors.size())
		{
			return tempActors.get(index);
		}
		else
		{
			throw new IllegalArgumentException("Ungueltiger Index");
		}
	}

	public int getTemporaryActorSize()
	{
		if (tempActors != null)
		{
			return tempActors.size();
		}
		else
		{
			return 0;
		}
	}

	public void removeTemporaryActor(Akteur a)
	{
		if (tempActors.contains(a))
			tempActors.remove(a);
	}

	public boolean isLocked(Akteur akt)
	{
		if (lockedActors != null)
		{
			return lockedActors.contains(akt);
		}
		return false;
	}

	public void lockActor(Akteur akt)
	{
		if (lockedActors != null)
		{
			lockedActors.add(akt);
		}
		else
		{
			lockedActors = new ArrayList<Akteur>();
			lockedActors.add(akt);
		}
	}

	public void unlockActor(Akteur akt)
	{
		if (lockedActors != null)
		{
			lockedActors.remove(akt);
		}
	}

	public void unlockAllActors()
	{
		if (lockedActors != null)
		{
			lockedActors.clear();
		}
	}

	public void addLockedRelation(Relation r)
	{
		if (lockedRelations != null)
		{
			lockedRelations.add(r);
		}
		else
		{
			lockedRelations = new ArrayList<Relation>();
			lockedRelations.add(r);
		}
	}

	public void unlockAllRelations()
	{
		if (lockedRelations != null)
		{
			lockedRelations.clear();
		}
	}

	public boolean isLocked(Relation r)
	{
		if (lockedRelations != null)
		{
			return lockedRelations.contains(r);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#getLastEvent()
	 */
	public VennMakerEvent getLastEvent() throws NoSuchElementException
	{
		return evLogger.getLastEvent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#getLastUndoEvent()
	 */
	public VennMakerEvent getLastUndoEvent()
	{
		return evLogger.getLastUndoEvent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#isEmpty()
	 */
	public boolean isEmpty()
	{
		return evLogger.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#logEvent(events.VennMakerEvent, boolean)
	 */
	public void logEvent(VennMakerEvent event, boolean clearUndoCache)
	{
		evLogger.logEvent(event, clearUndoCache);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#redoLastEvent()
	 */
	public void redoLastEvent()
	{
		evLogger.redoLastEvent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#undoLastEvent()
	 */
	public void undoLastEvent()
	{
		evLogger.undoLastEvent();
	}

	/*
	 * Check if the filter for actor a exists
	 */

	public boolean getFilter(Akteur a)
	{
		if (filter != null)
			return filter.contains(a);
		return false;
	}

	/*
	 * Add a filter for actor a
	 */
	public void setFilter(Akteur a)
	{
		if (!filter.contains(a))
			filter.add(a);

	}

	/*
	 */
	public void removeFilter(Akteur a)
	{
		if (filter.contains(a))
			filter.remove(a);
	}

	/*
	 * Returns whether at least one actor filter is activated
	 */
	public boolean getStatusFilter()
	{
		if (filter == null)
			return false;
		if (filter.size() > 0)
			return true;

		return false;
	}

	/*
	 * Creates the filter
	 */
	public void createFilter()
	{
		filter = new Vector<Akteur>();

	}

	private ActorSizeVisualizer	actorSizeVisualizer	= new ActorSizeVisualizer();

	public ActorSizeVisualizer getActorSizeVisualizer()
	{
		return actorSizeVisualizer;
	}

	public void setActorSizeVisualizer(ActorSizeVisualizer actorSizeVisualizer)
	{
		this.actorSizeVisualizer = actorSizeVisualizer;
	}

	public ActorImageVisualizer getActorImageVisualizer()
	{
		return actorImageVisualizer;
	}

	public void setActorImageVisualizer(ActorImageVisualizer actorImageVisualizer)
	{
		this.actorImageVisualizer = actorImageVisualizer;
	}

	private ActorSectorVisualizer	actorSectorVisualizer	= new ActorSectorVisualizer();

	public ActorSectorVisualizer getActorSectorVisualizer()
	{
		return actorSectorVisualizer;
	}

	public void setActorSectorVisualizer(
			ActorSectorVisualizer actorSectorVisualizer)
	{
		this.actorSectorVisualizer = actorSectorVisualizer;
	}

	private HashMap<String, RelationColorVisualizer>	relationColorVisualizer	= new HashMap<String, RelationColorVisualizer>();

	public RelationColorVisualizer getActiveRelationColorVisualizer(
			String attributeCollector)
	{
		if (this.relationColorVisualizer.get(attributeCollector) == null)
		{
			this.relationColorVisualizer.put(attributeCollector,
					new RelationColorVisualizer());
			// Why id = 11??????
			// this.relationColorVisualizer.get(attributeCollector).setAttributeType(
			// new AttributeType(11));
			this.relationColorVisualizer.get(attributeCollector).setAttributeType(
					new AttributeType());
		}
		return this.relationColorVisualizer.get(attributeCollector);
	}

	public void resetVisualizers()
	{
		this.sizeVisualizers = new HashMap<String, Map<AttributeType, RelationSizeVisualizer>>();
		this.dashVisualizer = new HashMap<String, Map<AttributeType, RelationDashVisualizer>>();
		this.colorVisualizers = new HashMap<String, Map<AttributeType, RelationColorVisualizer>>();
	}

	/**
	 * fills the visualizers with new and empty visualizers, if they don't exist
	 * already
	 */
	public void createNewVisualizers()
	{
		if ((this.sizeVisualizers == null) && (this.dashVisualizer == null)
				&& (this.colorVisualizers == null))
		{
			resetVisualizers();
if (this.relationColorVisualizer != null)
			for (String atCollector : this.relationColorVisualizer.keySet())
			{
				Map<AttributeType, RelationColorVisualizer> visualizerMap = new HashMap<AttributeType, RelationColorVisualizer>();
				visualizerMap.put(this.relationColorVisualizer.get(atCollector)
						.getAttributeType(), this.relationColorVisualizer
						.get(atCollector));
				this.colorVisualizers.put(atCollector, visualizerMap);				
			}

if (this.relationDashVisualizer != null)
			for (String atCollector : this.relationDashVisualizer.keySet())
			{
				Map<AttributeType, RelationDashVisualizer> visualizerMap = new HashMap<AttributeType, RelationDashVisualizer>();
				visualizerMap.put(this.relationDashVisualizer.get(atCollector)
						.getAttributeType(), this.relationDashVisualizer
						.get(atCollector));
				this.dashVisualizer.put(atCollector, visualizerMap);
			}

if (this.relationSizeVisualizer != null)
			for (String atCollector : this.relationSizeVisualizer.keySet())
			{
				Map<AttributeType, RelationSizeVisualizer> visualizerMap = new HashMap<AttributeType, RelationSizeVisualizer>();
				visualizerMap.put(this.relationSizeVisualizer.get(atCollector)
						.getAttributeType(), this.relationSizeVisualizer
						.get(atCollector));
				this.sizeVisualizers.put(atCollector, visualizerMap);
			}
		}
	}

	public boolean hasRelationColorVisualizer(String attributeCollector,
			AttributeType type)
	{
		return this.colorVisualizers.containsKey(attributeCollector)
				&& this.colorVisualizers.get(attributeCollector).containsKey(type);
	}

	/**
	 * returns the corresponding relationColorVisualizer for the given
	 * attributetype if none exists, a new one is created
	 * 
	 * @param attributeCollector
	 *           the attributeCollector to retrieve the relationColorVisualizer
	 * @param type
	 *           the corresponding attributeType
	 * @return the RelationColorVisualizer for the given AttributeType or a new
	 *         RelationColorVisualizer, if none is present
	 */
	public RelationColorVisualizer getRelationColorVisualizer(
			String attributeCollector, AttributeType type)
	{		
		if (this.colorVisualizers == null)
		{
			resetVisualizers();
		}

		if (this.colorVisualizers.get(attributeCollector) == null)
		{
			this.colorVisualizers.put(attributeCollector,
					new HashMap<AttributeType, RelationColorVisualizer>());
		}

		if (this.colorVisualizers.get(attributeCollector).get(type) == null)
		{
			RelationColorVisualizer visualizer = new RelationColorVisualizer();
			visualizer.setAttributeType(type);
			this.colorVisualizers.get(attributeCollector).put(type, visualizer);
		}

		return this.colorVisualizers.get(attributeCollector).get(type);
	}

	/**
	 * returns all RelationColorVisualizers for a given attributeCollector
	 * 
	 * @param attributeCollector
	 * @return
	 * 
	 */
	public Map<AttributeType, RelationColorVisualizer> getRelationColorVisualizerByCollector(
			String attributeCollector)
	{
		if (this.colorVisualizers == null)
			resetVisualizers();
		
		if (this.colorVisualizers.get(attributeCollector) == null)
		{	
			this.colorVisualizers.put(attributeCollector,
					new HashMap<AttributeType, RelationColorVisualizer>());
		}

		return this.colorVisualizers.get(attributeCollector);
	}

	public void setActiveRelationColorVisualizer(String attributeCollector,
			RelationColorVisualizer visualizer)
	{
		this.relationColorVisualizer.put(attributeCollector, visualizer);
	}

	/**
	 * sets the relationColorVisualizer for a given AttributeType and makes it
	 * the default one if none is specified up to this moment
	 * 
	 * @param attributeCollector
	 * @param type
	 * @param visualizer
	 */
	public void setRelationColorVisualizer(String attributeCollector,
			AttributeType type, RelationColorVisualizer visualizer)
	{
		if (this.colorVisualizers.get(attributeCollector) == null)
			this.colorVisualizers.put(attributeCollector,
					new HashMap<AttributeType, RelationColorVisualizer>());

		if (this.relationColorVisualizer.get(attributeCollector) == null)
			this.relationColorVisualizer.put(attributeCollector, visualizer);

		this.colorVisualizers.get(attributeCollector).put(type, visualizer);
	}

	private HashMap<String, RelationDashVisualizer>	relationDashVisualizer	= new HashMap<String, RelationDashVisualizer>();

	public RelationDashVisualizer getActiveRelationDashVisualizer(
			String attributeCollector)
	{
		if (this.relationDashVisualizer.get(attributeCollector) == null)
		{
			this.relationDashVisualizer.put(attributeCollector,
					new RelationDashVisualizer());

			// this.relationDashVisualizer.get(attributeCollector).setAttributeType(
			// new AttributeType(12));
			this.relationDashVisualizer.get(attributeCollector).setAttributeType(
					new AttributeType());
		}
		return this.relationDashVisualizer.get(attributeCollector);
	}

	public void setActiveRelationSizeVisualizer(String attributeCollector,
			RelationSizeVisualizer visualizer)
	{
		this.relationSizeVisualizer.put(attributeCollector, visualizer);
	}

	public boolean hasRelationDashVisualizer(String attributeCollector,
			AttributeType type)
	{

		return this.dashVisualizer.containsKey(attributeCollector)
				&& this.dashVisualizer.get(attributeCollector).containsKey(type);
	}

	public RelationDashVisualizer getRelationDashVisualizer(
			String attributeCollector, AttributeType type)
	{
		if (this.dashVisualizer.get(attributeCollector) == null)
		{
			this.dashVisualizer.put(attributeCollector,
					new HashMap<AttributeType, RelationDashVisualizer>());
		}
		if (this.dashVisualizer.get(attributeCollector).get(type) == null)
		{
			RelationDashVisualizer visualizer = new RelationDashVisualizer();
			visualizer.setAttributeType(type);

			this.dashVisualizer.get(attributeCollector).put(type, visualizer);
		}

		return this.dashVisualizer.get(attributeCollector).get(type);
	}

	public Map<AttributeType, RelationDashVisualizer> getRelationDashVisualizerByCollector(
			String attributeCollector)
	{
		if (this.dashVisualizer.get(attributeCollector) == null)
			this.dashVisualizer.put(attributeCollector,
					new HashMap<AttributeType, RelationDashVisualizer>());

		return this.dashVisualizer.get(attributeCollector);
	}

	public void setActiveRelationDashVisualizer(String attributeCollector,
			RelationDashVisualizer visualizer)
	{
		this.relationDashVisualizer.put(attributeCollector, visualizer);
	}

	public void setRelationDashVisualizer(String attributeCollector,
			AttributeType type, RelationDashVisualizer visualizer)
	{
		if (this.dashVisualizer.get(attributeCollector) == null)
			this.dashVisualizer.put(attributeCollector,
					new HashMap<AttributeType, RelationDashVisualizer>());

		if (this.relationDashVisualizer.get(attributeCollector) == null)
			this.relationDashVisualizer.put(attributeCollector, visualizer);

		this.dashVisualizer.get(attributeCollector).put(type, visualizer);
	}

	private HashMap<String, RelationSizeVisualizer>	relationSizeVisualizer	= new HashMap<String, RelationSizeVisualizer>();

	public RelationSizeVisualizer getActiveRelationSizeVisualizer(
			String attributeCollector)
	{
		if (this.relationSizeVisualizer.get(attributeCollector) == null)
		{
			this.relationSizeVisualizer.put(attributeCollector,
					new RelationSizeVisualizer());
			// this.relationSizeVisualizer.get(attributeCollector).setAttributeType(
			// new AttributeType(10));
			this.relationSizeVisualizer.get(attributeCollector).setAttributeType(
					new AttributeType());
		}
		return this.relationSizeVisualizer.get(attributeCollector);
	}

	public boolean hasRelationSizeVisualizer(String attributeCollector,
			AttributeType type)
	{
		return this.sizeVisualizers.containsKey(attributeCollector)
				&& this.sizeVisualizers.get(attributeCollector).containsKey(type);
	}

	public RelationSizeVisualizer getRelationSizeVisualizer(
			String attributeCollector, AttributeType type)
	{
 
		if (this.sizeVisualizers == null)
		{
			this.sizeVisualizers = new HashMap<String, Map<AttributeType, RelationSizeVisualizer>>();
		}

		if (this.sizeVisualizers.get(attributeCollector) == null)
		{
			this.sizeVisualizers.put(attributeCollector,
					new HashMap<AttributeType, RelationSizeVisualizer>());
		}

		if (this.sizeVisualizers.get(attributeCollector).get(type) == null)
		{
			RelationSizeVisualizer visualizer = new RelationSizeVisualizer();
			visualizer.setAttributeType(type);

			this.sizeVisualizers.get(attributeCollector).put(type, visualizer);
		}

		return this.sizeVisualizers.get(attributeCollector).get(type);
	}

	public Map<AttributeType, RelationSizeVisualizer> getRelationSizeVisualizerByCollector(
			String attributeCollector)
	{
		if (this.sizeVisualizers.get(attributeCollector) == null)
			this.sizeVisualizers.put(attributeCollector,
					new HashMap<AttributeType, RelationSizeVisualizer>());

		return this.sizeVisualizers.get(attributeCollector);
	}

	public void setRelationSizeVisualizer(String attributeCollector,
			AttributeType type, RelationSizeVisualizer visualizer)
	{
		if (this.sizeVisualizers.get(attributeCollector) == null)
			this.sizeVisualizers.put(attributeCollector,
					new HashMap<AttributeType, RelationSizeVisualizer>());

		if (this.relationSizeVisualizer.get(attributeCollector) == null)
			this.relationSizeVisualizer.put(attributeCollector, visualizer);

		this.sizeVisualizers.get(attributeCollector).put(type, visualizer);
	}

	public String toString()
	{
		return bezeichnung;
	}

	/**
	 * Wird nach dem Deserialisieren ausgefuehrt.
	 */
	private Object readResolve()
	{
		if (actorSizeVisualizer == null)
			actorSizeVisualizer = new ActorSizeVisualizer();
		if (actorImageVisualizer == null)
			actorImageVisualizer = new ActorImageVisualizer();
		if (actorSectorVisualizer == null)
			actorSectorVisualizer = new ActorSectorVisualizer();
		return this;
	}

	/**
	 * Kompatibilitaet: Wenn in alter Datei noch keine Visualisierer, muessen
	 * diese erst erstellt werden
	 */
	public void createVisualizer()
	{
		relationDashVisualizer = new HashMap<String, RelationDashVisualizer>();
		relationSizeVisualizer = new HashMap<String, RelationSizeVisualizer>();
		relationColorVisualizer = new HashMap<String, RelationColorVisualizer>();
	}

	public Netzwerk getNewNetwork(String name, boolean isLogEvent)
	{
		this.isLogEvent = isLogEvent;

		return getNewNetwork(name);
	}

	/**
	 * Gibt ein Netzwerk mit gleichen Visualisierern wie dem aktuellen zurueck
	 * 
	 * @param name
	 *           der Name des neuen Netzwerkes
	 * @return das neue Netzwerk
	 */
	public Netzwerk getNewNetwork(String name)
	{
		String uniqueName = this.createUniqueName(name);

		Netzwerk netzwerk = new Netzwerk();
		netzwerk.setBezeichnung(uniqueName);//$NON-NLS-1$

		ComplexEvent event = new ComplexEvent(
				Messages.getString("VennMaker.Network_NewEmpty"));

		event.setIsLogEvent(this.isLogEvent);

		event.addEvent(new NewNetworkEvent(netzwerk));
		if (VennMaker.getInstance().getProject().getEgo() != null)
			event.addEvent(new AddActorEvent(VennMaker.getInstance().getProject()
					.getEgo(), netzwerk, VennMaker.getInstance().getProject()
					.getEgo().getLocation(this)));

		ActorSizeVisualizer as = new ActorSizeVisualizer();
		as.setAttributeType(this.getActorSizeVisualizer().getAttributeType());
		as.setSizes(this.getActorSizeVisualizer().getSizes());
		netzwerk.setActorSizeVisualizer(as);

		ActorImageVisualizer a = new ActorImageVisualizer();
		a.setAttributeType(this.getActorImageVisualizer().getAttributeType());
		a.setImages(this.getActorImageVisualizer().getImages());
		netzwerk.setActorImageVisualizer(a);

		/*
		 * Visualisierer auf gleich denen des aktuellen Netzwerkes setzen
		 */
		for (String atCollector : VennMaker.getInstance().getProject()
				.getAttributeCollectors())
		{
			for (AttributeType type : VennMaker.getInstance().getProject()
					.getAttributeTypes(atCollector))
			{

				RelationSizeVisualizer rsv = new RelationSizeVisualizer();
				rsv.setAttributeType(this.getRelationSizeVisualizer(atCollector,
						type).getAttributeType());
				rsv.setSizes(this.getRelationSizeVisualizer(atCollector, type)
						.getSizes());
				netzwerk.setRelationSizeVisualizer(atCollector, type, rsv);

				RelationColorVisualizer rcv = new RelationColorVisualizer();
				rcv.setAttributeType(this.getRelationColorVisualizer(atCollector,
						type).getAttributeType());
				rcv.setColors(this.getRelationColorVisualizer(atCollector, type)
						.getColors());
				netzwerk.setRelationColorVisualizer(atCollector, type, rcv);

				RelationDashVisualizer rdv = new RelationDashVisualizer();
				rdv.setAttributeType(this.getRelationDashVisualizer(atCollector,
						type).getAttributeType());
				rdv.setDasharrays(this.getRelationDashVisualizer(atCollector, type)
						.getDasharrays());
				netzwerk.setRelationDashVisualizer(atCollector, type, rdv);
			}
		}

		this.isLogEvent = true;
		EventProcessor.getInstance().fireEvent(event);
		return netzwerk;
	}

	/**
	 * Returns <code>true</code> if the given name is available,
	 * <code>false</code> if not
	 * 
	 * @param name
	 *           the name to check for availability
	 * @return <code>true</code> if the given name is available,
	 *         <code>false</code> if not
	 */
	public boolean nameCheck(String name)
	{
		for (Netzwerk net : VennMaker.getInstance().getProject().getNetzwerke())
		{
			if (net.getBezeichnung().equals(name))
			{
				JOptionPane.showMessageDialog(VennMaker.getInstance(),
						"A network with the given name already exists",
						"Network already exists", JOptionPane.ERROR_MESSAGE);

				return false;
			}
		}

		return true;
	}

	/**
	 * gibt eine Kopie des Netzwerkes mit dem angegebenen Namen zurueck mit
	 * erstellung der entsprechenden visualisierern.
	 * 
	 * @param isFinalEvent
	 *           <code>true</code> wenn das NetzwerkEvent nicht mehr rueckgaengig
	 *           gemacht werden soll
	 */

	public Netzwerk cloneNetwork(String name, boolean isLogEvent)
	{
		this.isLogEvent = isLogEvent;

		return cloneNetwork(name);
	}

	/**
	 * gibt eine Kopie des Netzwerkes mit dem angegebenen Namen zurueck mit
	 * erstellung der entsprechenden visualisierern
	 */
	public Netzwerk cloneNetwork(String name)
	{
		/**
		 * Namecheck wird durch "createUniqueName(String)" ueberfluessig (oder
		 * umgekehrt..)
		 */
		// if (!nameCheck(name))
		// return null;

		Netzwerk clonedNetwork = new Netzwerk();
		if (name == null || name.equals("")) //$NON-NLS-1$
			name = Messages.getString("VennMaker.Copy_of")//$NON-NLS-1$
					+ this.getBezeichnung();

		/* Netzwerke mit doppelten Namen durchnummerieren */
		name = createUniqueName(name);

		clonedNetwork.setBezeichnung(name);

		ComplexEvent event = new ComplexEvent(
				Messages.getString("VennMaker.Network_Clone")); //$NON-NLS-1$

		event.setIsLogEvent(this.isLogEvent);

		NewNetworkEvent nnEvent = new NewNetworkEvent(clonedNetwork);
		nnEvent.setCloneFather(this);
		event.addEvent(nnEvent);

		/* Akteure,die geklont werden sollen */
		Vector<Akteur> actorsForClone = new Vector<Akteur>();

		if ((this.getMarkedActors() != null)
				&& (VennMaker.getInstance().getViewOfNetwork(this).isFilterActive()))
		{
			actorsForClone = this.getMarkedActors();
		}
		else
			actorsForClone = (Vector<Akteur>) this.akteure;

		for (Akteur akteur : actorsForClone)
		{
			akteur.copyNetworkProperties(this, clonedNetwork);
			event.addEvent(new AddActorEvent(akteur, clonedNetwork, akteur
					.getLocation(clonedNetwork)));
			// clonedNetwork.addTemporaryActor(akteur);
		}

		cloneRelations(clonedNetwork, event, actorsForClone);

		clonedNetwork.setHintergrund((BackgroundInfo) CDialogNetworkClone
				.deepCopy(this.getHintergrund()));

		// Hintergrundbild Clonen
		// BackgroundInfo bgInfo = this.getHintergrund();
		BackgroundInfo bgInfo = clonedNetwork.getHintergrund();

		// Darf keine Copy sein, es muss DAS Attribut sein
		bgInfo.setCircleAttribute(this.getHintergrund().getCircleAttribute());

		bgInfo.setSectorAttribute(this.getHintergrund().getSectorAttribute());

		String oldName = bgInfo.getFilenameOfOriginalImage();
		String oldSelectionName = bgInfo.getFilename();

		createBackground(bgInfo, oldName, oldSelectionName);

		ActorSizeVisualizer as = new ActorSizeVisualizer();
		as.setAttributeType(this.getActorSizeVisualizer().getAttributeType());
		as.setSizes(this.getActorSizeVisualizer().getSizes());
		clonedNetwork.setActorSizeVisualizer(as);

		ActorImageVisualizer a = new ActorImageVisualizer();
		a.setAttributeType(this.getActorImageVisualizer().getAttributeType());
		a.setImages(this.getActorImageVisualizer().getImages());
		clonedNetwork.setActorImageVisualizer(a);

		for (String atCollector : VennMaker.getInstance().getProject()
				.getAttributeCollectors())
		{

			for (AttributeType type : VennMaker.getInstance().getProject()
					.getAttributeTypes(atCollector))
			{

				RelationSizeVisualizer rsv = new RelationSizeVisualizer();
				rsv.setAttributeType(this.getRelationSizeVisualizer(atCollector,
						type).getAttributeType());
				rsv.setSizes(this.getRelationSizeVisualizer(atCollector, type)
						.getSizes());
				clonedNetwork.setRelationSizeVisualizer(atCollector, type, rsv);

				RelationColorVisualizer rcv = new RelationColorVisualizer();
				rcv.setAttributeType(this.getRelationColorVisualizer(atCollector,
						type).getAttributeType());
				rcv.setColors(this.getRelationColorVisualizer(atCollector, type)
						.getColors());
				clonedNetwork.setRelationColorVisualizer(atCollector, type, rcv);

				RelationDashVisualizer rdv = new RelationDashVisualizer();
				rdv.setAttributeType(this.getRelationDashVisualizer(atCollector,
						type).getAttributeType());
				rdv.setDasharrays(this.getRelationDashVisualizer(atCollector, type)
						.getDasharrays());
				clonedNetwork.setRelationDashVisualizer(atCollector, type, rdv);
			}
		}

		clonedNetwork.setActorSectorVisualizer(this.getActorSectorVisualizer());
		clonedNetwork.setMetaInfo(this.metaInformation);
		EventProcessor.getInstance().fireEvent(event);
		this.isLogEvent = true;
		return clonedNetwork;
	}

	/**
	 * clones the relations for the actors, which are cloned in the specified
	 * network
	 * 
	 * @param clonedNetwork
	 *           the new network which needs the new relations
	 * @param event
	 *           add all added relations to the complexEvent
	 * @param actorsForClone
	 *           the actors, which are cloned, to loop
	 */
	private void cloneRelations(Netzwerk clonedNetwork, ComplexEvent event,
			Vector<Akteur> actorsForClone)
	{
		for (Akteur akteur : actorsForClone)
		{
			for (Relation relation : akteur.getRelations(this))
			{
				if (actorsForClone.contains(relation.getAkteur()))
				{
					final Relation r = new Relation(relation.getAkteur(),
							relation.getAttributeCollectorValue());

					r.setAttributes(clonedNetwork, relation.getAttributes(this));

					event.addEvent(new AddRelationEvent(akteur, clonedNetwork, r));
				}
			}
		}
	}

	/**
	 * gibt eine Kopie des Netzwerkes mit dem angegebenen Namen zurueck mit
	 * erstellung der entsprechenden visualisierern
	 */
	public Netzwerk mergeNetwork(Netzwerk netSource)
	{
		Netzwerk srcNetwork = netSource;

		ComplexEvent event = new ComplexEvent("Merge Network"); //$NON-NLS-1$

		event.setIsLogEvent(this.isLogEvent);

		MergeNetworkEvent nnEvent = new MergeNetworkEvent(srcNetwork);
		event.addEvent(nnEvent);
		EventProcessor.getInstance().fireEvent(event);

		event = new ComplexEvent("Merge Network2"); //$NON-NLS-1$

		/* Akteure,die geklont werden sollen */
		Vector<Akteur> actorsForClone = new Vector<Akteur>();

		if (srcNetwork.getMarkedActors().size() > 0)
		{
			actorsForClone = srcNetwork.getMarkedActors();
		}
		else
			actorsForClone = (Vector<Akteur>) srcNetwork.akteure;

		for (Akteur akteur : actorsForClone)
		{
			akteur.mergeNetworkProperties(srcNetwork, this);
			// event.addEvent(new AddActorEvent(akteur, this,
			// akteur.getLocation(srcNetwork)));
			// clonedNetwork.addTemporaryActor(akteur);
		}
		EventProcessor.getInstance().fireEvent(event);

		event = new ComplexEvent("Merge Network3"); //$NON-NLS-1$

		for (Akteur akteur : actorsForClone)
		{
			for (Relation relation : akteur.getRelations(srcNetwork))
			{
				if (actorsForClone.contains(relation.getAkteur()))
				{
					final Relation r = new Relation(relation.getAkteur(),
							relation.getAttributeCollectorValue());

					r.setAttributes(this, relation.getAttributes(srcNetwork));

					event.addEvent(new AddRelationEvent(akteur, this, r));
				}
			}
		}

		this.setHintergrund((BackgroundInfo) CDialogNetworkClone
				.deepCopy(srcNetwork.getHintergrund()));

		// Hintergrundbild Clonen
		BackgroundInfo bgInfo = srcNetwork.getHintergrund();

		// Darf keine Copy sein, es muss DAS Attribut sein
		bgInfo.setCircleAttribute(srcNetwork.getHintergrund()
				.getCircleAttribute());

		bgInfo.setSectorAttribute(srcNetwork.getHintergrund()
				.getSectorAttribute());

		String oldName = bgInfo.getFilenameOfOriginalImage();
		String oldSelectionName = bgInfo.getFilename();

		createBackground(bgInfo, oldName, oldSelectionName);

		ActorSizeVisualizer as = new ActorSizeVisualizer();
		as.setAttributeType(srcNetwork.getActorSizeVisualizer()
				.getAttributeType());
		as.setSizes(srcNetwork.getActorSizeVisualizer().getSizes());
		this.setActorSizeVisualizer(as);

		ActorImageVisualizer a = new ActorImageVisualizer();
		a.setAttributeType(srcNetwork.getActorImageVisualizer()
				.getAttributeType());
		a.setImages(this.getActorImageVisualizer().getImages());
		this.setActorImageVisualizer(a);

		for (String atCollector : VennMaker.getInstance().getProject()
				.getAttributeCollectors())
		{
			for (AttributeType type : VennMaker.getInstance().getProject()
					.getAttributeTypes(atCollector))
			{

				RelationSizeVisualizer rsv = new RelationSizeVisualizer();
				rsv.setAttributeType(srcNetwork.getRelationSizeVisualizer(
						atCollector, type).getAttributeType());
				rsv.setSizes(srcNetwork
						.getRelationSizeVisualizer(atCollector, type).getSizes());
				this.setRelationSizeVisualizer(atCollector, type, rsv);

				RelationColorVisualizer rcv = new RelationColorVisualizer();

				rcv.setAttributeType(srcNetwork.getRelationColorVisualizer(
						atCollector, type).getAttributeType());

				rcv.setColors(srcNetwork.getRelationColorVisualizer(atCollector,
						type).getColors());

				this.setRelationColorVisualizer(atCollector, type, rcv);

				RelationDashVisualizer rdv = new RelationDashVisualizer();
				rdv.setAttributeType(srcNetwork.getRelationDashVisualizer(
						atCollector, type).getAttributeType());
				rdv.setDasharrays(srcNetwork.getRelationDashVisualizer(atCollector,
						type).getDasharrays());
				this.setRelationDashVisualizer(atCollector, type, rdv);
			}
		}

		this.setActorSectorVisualizer(srcNetwork.getActorSectorVisualizer());

		EventProcessor.getInstance().fireEvent(event);
		this.isLogEvent = true;
		return this;

	}

	/**
	 * This method clones a network, wich is not registered in VennMaker. For
	 * example networks wich were loaded in the ConfigDialog by "Load".
	 * 
	 * The AttributeTypes (Importance, Type etc.) of the virtual network must be
	 * replaced with the AttributeTypes of the current running Projekt during the
	 * cloning Process
	 * 
	 * @param name
	 *           Name of the new network
	 * 
	 * @return a cloned and registered network from a virtual template
	 */
	public Netzwerk cloneVirtualNetwork(String name)
	{
		Netzwerk clonedNetwork = new Netzwerk();
		// VennMaker.getInstance().getProject().checkNextNetworkID(id);

		if (name == null || name.equals("")) //$NON-NLS-1$
			name = Messages.getString("VennMaker.Copy_of")//$NON-NLS-1$
					+ this.getBezeichnung();

		/* Netzwerke mit doppelten Namen durchnummerieren */
		name = createUniqueName(name);

		clonedNetwork.setBezeichnung(name);

		ComplexEvent event = new ComplexEvent(
				Messages.getString("VennMaker.Network_Clone"));//$NON-NLS-1$

		event.setIsLogEvent(false);

		NewNetworkEvent nnEvent = new NewNetworkEvent(clonedNetwork, false);
		nnEvent.setCloneFather(this);
		event.addEvent(nnEvent);

		/* Akteure,die geklont werden sollen */
		Vector<Akteur> actorsForClone = new Vector<Akteur>();

		if ((this.getMarkedActors() != null)
				&& (VennMaker.getInstance().getViewOfNetwork(this) != null)
				&& (VennMaker.getInstance().getViewOfNetwork(this).isFilterActive()))
		{
			actorsForClone = this.getMarkedActors();
		}
		else
			actorsForClone = (Vector<Akteur>) this.akteure;

		Akteur ego = VennMaker.getInstance().getProject().getEgo();

		ego.copyVirtualNetworkProperties(VennMaker.getInstance().getProject()
				.getCurrentNetzwerk(), clonedNetwork);
		event.addEvent(new AddActorEvent(ego, clonedNetwork, ego
				.getLocation(VennMaker.getInstance().getProject()
						.getCurrentNetzwerk())));

		cloneRelations(clonedNetwork, event, actorsForClone);
		clonedNetwork.setHintergrund((BackgroundInfo) CDialogNetworkClone
				.deepCopy(this.getHintergrund()));

		BackgroundInfo bgInfo = clonedNetwork.getHintergrund();

		Vector<AttributeType> types = VennMaker.getInstance().getProject()
				.getAttributeTypes();

		for (AttributeType type : types)
		{
			if (this.getHintergrund().getCircleAttribute() != null
					&& type.toString().equals(
							this.getHintergrund().getCircleAttribute().toString()))
				bgInfo.setCircleAttribute(type);
			else if (this.getHintergrund().getSectorAttribute() != null
					&& type.toString().equals(
							this.getHintergrund().getSectorAttribute().toString()))
				bgInfo.setSectorAttribute(type);
		}

		String oldName = bgInfo.getFilenameOfOriginalImage();
		String oldSelectionName = bgInfo.getFilename();

		createBackground(bgInfo, oldName, oldSelectionName);

		// Vector<AttributeType> aTypes =
		// VennMaker.getInstance().getProject().getAttributeTypes("ACTOR");
		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes();

		AttributeType sizeVisualizerType = this.getActorSizeVisualizer()
				.getAttributeType();

		ActorSizeVisualizer as = new ActorSizeVisualizer();

		AttributeType aType = null;

		for (AttributeType a : aTypes)
			if (a.toString().equals(sizeVisualizerType.toString()))
			{
				as.setAttributeType(a);
				aType = a;
				break;
			}
		
		List<Object> predefValues = null;
		
		if (aType != null) predefValues = Arrays.asList(aType.getPredefinedValues());

		HashMap<String, Object> tempHashMap = new HashMap<String, Object>();

		if (predefValues != null)
		for (Object obj : predefValues)
			tempHashMap.put(obj.toString(), obj);

		Map<Object, Integer> sizes = new HashMap<Object, Integer>();
		Map<Object, Integer> origSizes = this.getActorSizeVisualizer().getSizes();

		Set<Map.Entry<Object, Integer>> origSet = origSizes.entrySet();

		for (Iterator<Map.Entry<Object, Integer>> iterator = origSet.iterator(); iterator
				.hasNext();)
		{
			Map.Entry<Object, Integer> value = iterator.next();

			if (tempHashMap.containsKey(value.getKey().toString()))
			{
				sizes.put(tempHashMap.get(value.getKey().toString()),
						value.getValue());
			}
		}

		as.setSizes(sizes);
		clonedNetwork.setActorSizeVisualizer(as);

		ActorImageVisualizer aiv = new ActorImageVisualizer();

		AttributeType aivType = this.getActorImageVisualizer().getAttributeType();

		for (AttributeType type : aTypes)
			if (type.toString().equals(aivType.toString()))
				aiv.setAttributeType(type);

		Map<Object, String> oldImages = this.getActorImageVisualizer()
				.getImages();
		Map<Object, String> newImages = new HashMap<Object, String>();

		Set<Map.Entry<Object, String>> origImages = oldImages.entrySet();

		for (Iterator<Map.Entry<Object, String>> iterator = origImages.iterator(); iterator
				.hasNext();)
		{
			Map.Entry<Object, String> entry = iterator.next();
			newImages.put(
					new String(entry.getKey().toString()),
					new String(VMPaths.getCurrentWorkingDirectory()
							+ VMPaths.VENNMAKER_SYMBOLS
							+ new File(entry.getValue()).getName()));
		}

		aiv.setImages(newImages);
		clonedNetwork.setActorImageVisualizer(aiv);

		Vector<AttributeType> relationTypes = new Vector<AttributeType>();

		for (AttributeType type : types)
			if (!type.getType().equals("ACTOR"))
				relationTypes.add(type);

		for (String atCollector : VennMaker.getInstance().getProject()
				.getAttributeCollectors())
		{
			for (AttributeType attributeType : VennMaker.getInstance()
					.getProject().getAttributeTypes(atCollector))
			{

				if (attributeType.getPredefinedValues() == null)
					continue;

				RelationSizeVisualizer rsv = new RelationSizeVisualizer();
				AttributeType oldSizeAttribute = null;

				if (this.getRelationSizeVisualizer(atCollector, attributeType) != null)
					oldSizeAttribute = this.getRelationSizeVisualizer(atCollector,
							attributeType).getAttributeType();

				if (oldSizeAttribute != null)
				{
					for (AttributeType type : relationTypes)
					{
						if (type.toString().equals(oldSizeAttribute.toString()))
							rsv.setAttributeType(type);
					}

					if (rsv.getAttributeType() == null)
						rsv.setAttributeType(oldSizeAttribute);

					Map<Object, Integer> newSizes = new HashMap<Object, Integer>();

					Set<Map.Entry<Object, Integer>> oldSet = this
							.getRelationSizeVisualizer(atCollector, attributeType)
							.getSizes().entrySet();

					if (oldSet == null || oldSet.size() == 0)
					{
						for (Object predefValue : this
								.getRelationSizeVisualizer(atCollector, attributeType)
								.getAttributeType().getPredefinedValues())
							newSizes.put(predefValue,
									RelationSizeVisualizer.DEFAULT_SIZE);
					}
					else
					{
						for (Iterator<Map.Entry<Object, Integer>> iterator = oldSet
								.iterator(); iterator.hasNext();)
						{
							Map.Entry<Object, Integer> entry = iterator.next();
							newSizes.put(new String(entry.getKey().toString()),
									entry.getValue());
						}
					}

					rsv.setSizes(newSizes);
				}

				clonedNetwork.setRelationSizeVisualizer(atCollector, attributeType,
						rsv);

				RelationColorVisualizer rcv = new RelationColorVisualizer();
				AttributeType oldColorAttribute = null;

				if (this.getRelationColorVisualizer(atCollector, attributeType) != null)
					oldColorAttribute = this.getRelationColorVisualizer(atCollector,
							attributeType).getAttributeType();

				if (oldColorAttribute != null)
				{
					for (AttributeType type : relationTypes)
					{
						if (type.toString().equals(oldColorAttribute.toString()))
						{
							rcv.setAttributeType(type);
							break;
						}
					}

					if (rcv.getAttributeType() == null)
						rcv.setAttributeType(oldColorAttribute);

					Map<Object, Color> newColors = new HashMap<Object, Color>();
					Set<Map.Entry<Object, Color>> colorSet = this
							.getRelationColorVisualizer(atCollector, attributeType)
							.getColors().entrySet();

					if (colorSet == null || colorSet.size() == 0)
					{
						for (Object predefValue : this
								.getRelationColorVisualizer(atCollector, attributeType)
								.getAttributeType().getPredefinedValues())
							newColors.put(predefValue,
									RelationColorVisualizer.DEFAULT_COLOR);
					}
					else
					{

						for (Iterator<Map.Entry<Object, Color>> iterator = colorSet
								.iterator(); iterator.hasNext();)
						{
							Map.Entry<Object, Color> entry = iterator.next();
							newColors.put(new String(entry.getKey().toString()),
									entry.getValue());
						}
					}

					rcv.setColors(newColors);
				}

				clonedNetwork.setRelationColorVisualizer(atCollector,
						attributeType, rcv);

				RelationDashVisualizer rdv = new RelationDashVisualizer();
				AttributeType dashType = null;

				if (this.getRelationDashVisualizer(atCollector, attributeType) != null)
					dashType = this.getRelationDashVisualizer(atCollector,
							attributeType).getAttributeType();

				if (dashType != null)
				{
					for (AttributeType type : relationTypes)
					{
						if (type.toString().equals(dashType.toString()))
						{
							rdv.setAttributeType(type);
							break;
						}
					}

					if (rdv.getAttributeType() == null)
						rdv.setAttributeType(dashType);

					Map<Object, float[]> newDashArrays = new HashMap<Object, float[]>();
					Set<Map.Entry<Object, float[]>> oldDashArrays = this
							.getRelationDashVisualizer(atCollector, attributeType)
							.getDasharrays().entrySet();

					if (oldDashArrays == null || oldDashArrays.size() == 0)
					{
						for (Object predefValue : this
								.getRelationDashVisualizer(atCollector, attributeType)
								.getAttributeType().getPredefinedValues())
							newDashArrays.put(predefValue,
									RelationDashVisualizer.DEFAULT_DASHING);
					}
					else
					{
						for (Iterator<Map.Entry<Object, float[]>> iterator = oldDashArrays
								.iterator(); iterator.hasNext();)
						{
							Map.Entry<Object, float[]> entry = iterator.next();
							newDashArrays.put(new String(entry.getKey().toString()),
									entry.getValue());
						}
					}

					rdv.setDasharrays(newDashArrays);
				}

				clonedNetwork.setRelationDashVisualizer(atCollector, attributeType,
						rdv);

			}

		}

		// ActorSectorVisualizer asv = new ActorSectorVisualizer();
		// clonedNetwork.setActorSectorVisualizer(asv);
		clonedNetwork.setActorSectorVisualizer(this.getActorSectorVisualizer());
		EventProcessor.getInstance().fireEvent(event);

		VennMaker.getInstance().getProject().setCurrentNetzwerk(clonedNetwork);

		return clonedNetwork;
	}

	private void createBackground(BackgroundInfo bgInfo, String oldName,
			String oldSelectionName)
	{
		// Nur wenn ueberhaupt Hintergrund vorhanden
		if (oldName != null)
		{
			VennMaker.getInstance().getProject().incrementBackgroundNumber();
			int bgNumber = VennMaker.getInstance().getProject()
					.getBackgroundNumber();

			String newName = oldName.substring(0, oldName.length() - 1);
			newName += bgNumber + ""; //$NON-NLS-1$

			String newSelectionName = newName + "Selection"; //$NON-NLS-1$

			try
			{
				FileOperations.copyFile(new File(oldName), new File(newName), 128,
						true);
				bgInfo.setFilename(newName);

				// Wenn es eine Auswahl gibt auch clonen...
				if (!oldName.equals(oldSelectionName))
				{
					FileOperations.copyFile(new File(oldSelectionName), new File(
							newSelectionName), 128, true);
					bgInfo.setFilenameOfSelection(newSelectionName);
				}
			} catch (IOException exn)
			{
				// Bei Fehler wird Hintergrund und Auswahl auf "null"
				// gesetzt
				bgInfo.setUpscaledImgSelection(null);
				bgInfo.setFilenameOfSelection(null);
				bgInfo.setFilename(null);

				// Die Datei wird hier nicht gefunden, falls von einem Template
				// geladen wurde
				// da das Bild erst noch aus dem Template erzeugt werden muss
				// (CDialogBackgroundImage.setAttributesFromSetting())
				if (!(exn instanceof FileNotFoundException))
					exn.printStackTrace();
			}
		}
	}

	/**
	 * Mark an actor as filtered
	 * 
	 * @param akt
	 *           Akteur
	 */
	public void setMarkedActor(Akteur akt)
	{
		if (filteredActors == null)
			filteredActors = new Vector<Akteur>();
		filteredActors.add(akt);
	}

	/**
	 * Returns the marked actors
	 * 
	 * @return filtered actors
	 */
	public Vector<Akteur> getMarkedActors()
	{
		return filteredActors;
	}

	public void removeAllMarkedActors()
	{
		if (filteredActors != null)
			filteredActors.clear();
	}

	/**
	 * Falls sich AttributTypen geaendert haben, kann diese Methode benutzt
	 * werden um die Aenderung auf die Akteure zu uebertragen
	 * 
	 * @param aType
	 *           - geaendertes Attribut
	 */
	public void updateAkteure(AttributeType aType)
	{
		Object[] preVals = aType.getPredefinedValues();
		if (preVals != null)
		{
			for (Akteur akteur : getAkteure())
			{
				// Wenn es wert noch gibt belasse ihn
				boolean exists = false;
				Object aVal = akteur.getAttributeValue(aType, this);

				for (Object o : preVals)
				{
					if (o.equals(aVal))
					{
						exists = true;
						break;
					}
				}
				// sonst auf null setzen
				if (!exists)
				{
					akteur.setAttributeValue(aType, this, null);
				}
			}
		}
	}

	/**
	 * @return Notizen zu diesem Netzwerk
	 */
	public String getMetaInfo()
	{
		return metaInformation;
	}

	/**
	 * 
	 * @param metaData
	 *           - Notizen zu diesem Netzwerk
	 */
	public void setMetaInfo(String metaInfo)
	{
		this.metaInformation = metaInfo;
	}

	/**
	 * creates a name, which is not yet in use, starting of with the original
	 * name and adding a continuous number to make the new name unique
	 * 
	 * @param name
	 *           starting String to derive the new unique name
	 * @return a name, which is not yet in use
	 */
	private String createUniqueName(String name)
	{
		{
			int counter = 0;
			String ursprungsName = name;
			while (VennMaker.getInstance().getProject().getNetzwerknamen()
					.contains(name))
			{
				counter++;
				name = ursprungsName + "(" + counter + ")";
			}
			return name;
		}

	}

	/**
	 * updates the visualizers and responding types in the network to newly added
	 * relation groups
	 */
	public void refreshRelationAttributes()
	{
		Projekt proj = VennMaker.getInstance().getProject();
		for (String atCollector : proj.getAttributeCollectors())
		{
			AttributeType type = VennMaker.getInstance().getProject()
					.getMainGeneratorType(atCollector);

			if ((type != null) && (type.getPredefinedValues() != null)
					&& (type.getPredefinedValues().length != 0))
			{
				RelationSizeVisualizer rsv = new RelationSizeVisualizer();
				rsv.setAttributeType(this.getRelationSizeVisualizer(atCollector,
						type).getAttributeType());
				rsv.setSizes(this.getRelationSizeVisualizer(atCollector, type)
						.getSizes());
				this.setRelationSizeVisualizer(atCollector, type, rsv);
				// this.setActiveRelationSizeVisualizer(atCollector, rsv);

				RelationColorVisualizer rcv = new RelationColorVisualizer();
				rcv.setAttributeType(this.getRelationColorVisualizer(atCollector,
						type).getAttributeType());
				rcv.setColors(this.getRelationColorVisualizer(atCollector, type)
						.getColors());
				this.setRelationColorVisualizer(atCollector, type, rcv);
				// this.setActiveRelationColorVisualizer(atCollector, rcv);

				RelationDashVisualizer rdv = new RelationDashVisualizer();
				rdv.setAttributeType(this.getRelationDashVisualizer(atCollector,
						type).getAttributeType());
				rdv.setDasharrays(this.getRelationDashVisualizer(atCollector, type)
						.getDasharrays());
				this.setRelationDashVisualizer(atCollector, type, rdv);
				// this.setActiveRelationDashVisualizer(atCollector, rdv);
			}
		}
	}
	
	/**
	 * To get the sector index of a specific position, relative to the center of
	 * sectors.
	 * 
	 * @param x_m
	 *           - x coordinate of the center (x of EGO)
	 * @param y_m
	 *           - y coordinate of the center (y of EGO)
	 * 
	 * @param x
	 *           - x coordinate of the destinated position
	 * @param y
	 *           - y coordinate of the destinated position
	 * 
	 * @return sector index of the position (x,y)
	 */
	public int getSector(double x_m, double y_m, double x, double y)
	{
		if (getHintergrund().getNumSectors() > 0)
		{
			double x_mid = x_m;
			double y_mid = y_m;
			double actorPosX = x;
			double actorPosY = y;
			double vektorX = actorPosX - x_mid;
			double vektorY = actorPosY - y_mid;
			double laengeVektor = Math.sqrt(vektorX * vektorX + vektorY * vektorY);
			vektorX = vektorX / laengeVektor;
			vektorY = vektorY / laengeVektor;

			double minLength = Math.min(VennMaker.getInstance().getProject()
					.getViewAreaHeight()
					* VennMaker.getInstance().getConfig().getViewAreaRatio() / 2.0,
					VennMaker.getInstance().getProject().getViewAreaHeight() / 2.0);

			float egoSpace = 0;

			/* Winkel des Akteurs zum Ursprung des Kreises (PI = 180)*/
			double sektor = Math.acos(vektorX);
			/* PI + (PI - sektor) um vollstaendigen Kreis abzubilden */
			if (actorPosY > y_mid)
				sektor = Math.PI + (Math.PI - sektor);
			if (laengeVektor > minLength || laengeVektor < egoSpace)
				return -1;

			double gesamtBreite = 0.0;
			// Ueberpruefen aller Sektoren nach dem Akteur (prozentual vom Kreis
			// berechnet)
			for (int i = 0; i < getHintergrund().getNumSectors(); i++)
			{
				double aktuelleBreite = getHintergrund().getSector(i).width
						* 2 * Math.PI;
				gesamtBreite += aktuelleBreite;
				if (sektor < gesamtBreite){
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * To get the circle index of a specific position, relative to the center of
	 * circles.
	 * 
	 * @param x_m
	 *           - x coordinate of the center (x of EGO)
	 * @param y_m
	 *           - y coordinate of the center (y of EGO)
	 * 
	 * @param x
	 *           - x coordinate of the destinated position
	 * @param y
	 *           - y coordinate of the destinated position
	 * 
	 * @return circle index of the position (x,y)
	 */
	public int getCircle(double x_m, double y_m, double x, double y)
	{
		double actorPosX = x;
		double actorPosY = y;
		double minLength = Math.min(VennMaker.getInstance().getProject()
				.getViewAreaHeight()
				* VennMaker.getInstance().getConfig().getViewAreaRatio() / 2.0,
				VennMaker.getInstance().getProject().getViewAreaHeight() / 2.0);

		minLength = minLength - 1; // Ein wenig Abstand zum Kartenrand

		double x_mid = x_m;
		double y_mid = y_m;

		double abstand = Math.sqrt((actorPosX - x_mid) * (actorPosX - x_mid)
				+ (actorPosY - y_mid) * (actorPosY - y_mid));
		int anzahl = getHintergrund().getNumCircles();

		float egoSpace = VennMaker.getInstance().getProject().getViewAreaHeight() / 10;

		double kreis = (abstand - egoSpace) / (minLength - egoSpace) * anzahl;

		if (Math.round(kreis + .5) == 0)
			kreis = kreis + 1.0; // Vorlaeufige Loesung, bis Kreiabstand frei
		// definiert werden kann

		if ((kreis > anzahl) || (anzahl == 0))
			return -1;
		else
			return (int) Math.round(kreis + .5);
	}
}
