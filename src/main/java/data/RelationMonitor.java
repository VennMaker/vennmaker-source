/**
 * 
 */
package data;

import gui.VennMaker;

import java.awt.Shape;
import java.util.HashMap;
import java.util.Vector;

/**
 * 
 * 
 *         This class features all methods to monitor and edit the relations in
 *         the current project. Instead of looping through all networks and
 *         actors it mirrors the relations and provides easy access to them
 * 
 */
public class RelationMonitor
{
	/**
	 * speichert alle Relationen
	 */
	private Vector<Relation>													allRelations;

	/**
	 * speichert die Relationen nach Netzwerk
	 */
	private HashMap<Netzwerk, Vector<Vector<Relation>>>				networkRelations;

	/**
	 * speichert die Relationen nach Relationstyp
	 */
	private HashMap<String, Vector<Relation>>								relationsByType;

	/**
	 * speichert Relationen, die zuletzt bewegt wurden;
	 */
	private Vector<Relation>													movedRelations;

	/**
	 * speichert die Akteure, von denen die Relationen ausgehen
	 */
	private HashMap<Relation, Akteur>										startActors;

	/**
	 * saves all relations ending at given Actor
	 */
	private HashMap<Netzwerk, HashMap<Akteur, Vector<Relation>>>	toActor;

	/**
	 * stores all visible relations for the given network
	 */
	private HashMap<Netzwerk, HashMap<Shape, Relation>>				visibleRelations;

	/**
	 * saves the instance of the relationmonitor
	 */
	private static RelationMonitor											instance;

	/**
	 * creates all HashMaps and Vectors needed
	 */
	public RelationMonitor()
	{
		this.initialize();
	}

	/**
	 * erstellen der einzelnen Hashmaps und Vektoren, damit auf diese
	 * zurueckgegriffen werden kann
	 */
	public void initialize()
	{
		visibleRelations = new HashMap<Netzwerk, HashMap<Shape, Relation>>();
		allRelations = new Vector<Relation>();
		networkRelations = new HashMap<Netzwerk, Vector<Vector<Relation>>>();
		startActors = new HashMap<Relation, Akteur>();
		relationsByType = new HashMap<String, Vector<Relation>>();
		movedRelations = new Vector<Relation>();
		toActor = new HashMap<Netzwerk, HashMap<Akteur, Vector<Relation>>>();
		update();
	}

	/**
	 * returns the instance of this relationmonitor
	 * 
	 * @return the relationmonitor
	 */
	public static RelationMonitor getInstance()
	{
		if (instance == null)
			instance = new RelationMonitor();
		return instance;
	}

	/**
	 * bringt den Relationsmonitor auf den aktuellen Stand. Fuegt fehlende
	 * Relationen hinzu, loescht nicht mehr existierende Relationen
	 */
	public void update()
	{
		allRelations = new Vector<Relation>();

		/* loop throuh all networks in the current project */
		for (Netzwerk n : VennMaker.getInstance().getProject().getNetzwerke())
		{
			/*
			 * create new Vecor for every network, fill it and add it to the
			 * networkrelationmap
			 */
			networkRelations.put(n, new Vector<Vector<Relation>>());

			/* for each network, save a map of relations going to a specific actor */
			toActor.put(n, new HashMap<Akteur, Vector<Relation>>());

			for (Akteur a : n.getAkteure())
			{
				/* add all relations of current actor to the networkmap */
				networkRelations.get(n).add(a.getRelations(n));

				for (Relation r : a.getRelations(n))
				{
					startActors.put(r, a);
					allRelations.add(r);

					/* store the relations by their relationtype */
					if (relationsByType.containsKey(r.getAttributeCollectorValue()))
						relationsByType.get(r.getAttributeCollectorValue()).add(r);
					else
					{
						Vector<Relation> newVector = new Vector<Relation>();
						newVector.add(r);
						relationsByType
								.put(r.getAttributeCollectorValue(), newVector);
					}

					/* store all relations going to a specific actor */
					Akteur targetActor = r.getAkteur();
					if (toActor.get(n).containsKey(targetActor))
						toActor.get(n).get(targetActor).add(r);
					else
					{
						Vector<Relation> newVector = new Vector<Relation>();
						newVector.add(r);
						toActor.get(n).put(targetActor, newVector);
					}
				}
			}
		}
	}

	/**
	 * method to return all relations, currently appearing in the project
	 * 
	 * @return all relations
	 */
	public Vector<Relation> getRelations()
	{
		return allRelations;
	}

	/**
	 * gibt die Relationen eines bestimmten Netzwerks zurueck
	 * 
	 * @param n
	 *           das geforderte Netzwerk
	 * @return die Relationen des abgefragten Netzwerkes
	 */
	public Vector<Relation> getNetworkRelations(Netzwerk n)
	{
		Vector<Relation> returnVector = new Vector<Relation>();
		if (networkRelations.get(n) != null)
			for (Vector<Relation> tempVector : networkRelations.get(n))
			{
				returnVector.addAll(tempVector);
			}
		return returnVector;
	}

	/**
	 * gibt den zur Relation gehoerenden Startakteur zurueck
	 * 
	 * @param r
	 *           die Relation, von der man den Startakteur erfahren moechte
	 * @return der Startakteur der Relation r
	 */
	public Akteur getStartActor(Relation r)
	{
		return startActors.get(r);
	}

	/**
	 * loescht den Vektor der zuletzt bewegten Relationen
	 */
	public void clearMovedRelations()
	{
		movedRelations.clear();
	}

	/**
	 * gibt alle zuletzt bewegten Relationen zurueck
	 * 
	 * @return last moved relations
	 */
	public Vector<Relation> getMovedRelations()
	{
		return this.movedRelations;
	}

	/**
	 * returns the last moved relations of Network n (removes all other moved
	 * relations destructively)
	 * 
	 * @param n
	 *           the network
	 * @return the last moved relations for this network
	 */
	public Vector<Relation> getMovedRelations(Netzwerk n)
	{
		this.movedRelations.retainAll(getNetworkRelations(n));
		return movedRelations;
	}

	/**
	 * adds a new moved relation to the corresponding vector
	 * 
	 * @param r
	 *           the relation to be added to this vector
	 */
	public void addMovedRelation(Relation r)
	{
		this.movedRelations.add(r);
	}

	/**
	 * adds all relations coming from or going to this actor
	 * 
	 * @param a
	 *           the target or sourceactor of the relations (to add all
	 *           relations, when this actor is moved)
	 * @param n
	 *           the network of the moved actor
	 */
	public void addMovedRelation(Akteur a, Netzwerk n)
	{
		for (Relation r : a.getRelations(n))
		{
			if (!this.movedRelations.contains(r))
			{
				this.movedRelations.add(r);
			}
		}
	}

	/**
	 * returns the relations which start at Actor a
	 * 
	 * @param a
	 *           the startactor for the relations
	 * @param n
	 *           the network of the actor
	 * @return the vector of all relations starting at actor a in network n
	 */
	public Vector<Relation> getRelationsFromActor(Akteur a, Netzwerk n)
	{
		return a.getRelations(n);
	}

	/**
	 * returns all Relations with the given targetactor in the specific network
	 * 
	 * @param a
	 *           the targetactor to whom all relations are going
	 * @param n
	 *           the network in which these relations are going to this actor
	 * @return all relations to the given actor in the given network
	 */
	public Vector<Relation> getRelationsToActor(Akteur a, Netzwerk n)
	{
		return toActor.get(n).get(a);
	}

	/**
	 * returns all relations which are going to or coming from the given actor in
	 * the specified network
	 * 
	 * @param a
	 *           the actor connected to the relations
	 * @param n
	 *           the network in which you are looking for the connected relations
	 * @return all relations from and to this actor in this network
	 */
	public Vector<Relation> getRelationsFromAndToActor(Akteur a, Netzwerk n)
	{
		Vector<Relation> returnVector = new Vector<Relation>();
		Vector<Relation> relations = toActor.get(n).get(a);
		if (relations != null)
			returnVector.addAll(relations);
		for (Relation r : a.getRelations(n))
		{
			if (!returnVector.contains(r))
				returnVector.add(r);
		}
		return returnVector;
	}

	/**
	 * returns all relations in the specified network between the two actors
	 * 
	 * @param a
	 *           first actor
	 * @param b
	 *           second actor
	 * @param n
	 *           the network
	 * @return all relations between actor a and actor b
	 */
	public Vector<Relation> getRelationsBetween(Akteur a, Akteur b, Netzwerk n)
	{
		Vector<Relation> returnVector = getRelationsFromAndToActor(a, n);
		returnVector.retainAll(getRelationsFromAndToActor(b, n));
		return returnVector;
	}

	public HashMap<Shape, Relation> getVisibleRelations(Netzwerk n)
	{
		return visibleRelations.get(n);
	}
}
