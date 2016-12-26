/**
 * 
 */
package gui.configdialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Beinhaltet Listen mit den Elementen die im Config-Dialog dargestellt werden
 * sollen.</br> Jedes Element wird nur einmal durch seine Klasse repraesentiert.
 * Bei Netzwerk Elementen</br> wird dann einfach im ConfigDialog fuer jedes
 * Netzwerk eine Instanz dieses Elements erzeugt.</br></br> Netzwerk-weite
 * Elemente -> NetworkElements</br> Projekt-weite Elemente ->
 * ProjectElements</br> Einzigartige Elemente -> UniqueNetElements</br>
 * 
 * 
 */
@SuppressWarnings("rawtypes")
public class ConfigDialogLayer
{
	/**
	 * Liste mit Netzwerk-weiten Elementen
	 */
	private List<Class>																networkElems						= new ArrayList<Class>();

	/**
	 * Liste mit Projekt-weiten Elementen
	 */
	private List<Class>																projectElems						= new ArrayList<Class>();

	/**
	 * Liste mit einzigartigen Netzwerk-Elementen
	 */
	private List<Class>																uniqueNetElems						= new ArrayList<Class>();

	/** stores currently activated configdialogelements (networkwise) */
	private Map<String, HashMap<String, ConfigDialogElement>>			activateNetworkdElements		= new HashMap<String, HashMap<String, ConfigDialogElement>>();

	/** stores currently activated configdialogelements (projectwise) */
	private Map<String, ConfigDialogElement>									activeProjectElements			= new HashMap<String, ConfigDialogElement>();

	/**
	 * stores activated (activated at any time) configdialogelements
	 * (networkwise)
	 */
	private static Map<String, HashMap<String, ConfigDialogElement>>	allActivateNetworkdElements	= new HashMap<String, HashMap<String, ConfigDialogElement>>();

	/**
	 * stores activated (activated at any time) configdialogelements
	 * (projectwise)
	 */
	private static Map<String, ConfigDialogElement>							allActiveProjectElements		= new HashMap<String, ConfigDialogElement>();

	/**
	 * Referenz auf sich selbst (Singleton Pattern)
	 */
	private static ConfigDialogLayer												cdLayer;

	private ConfigDialogLayer()
	{
		cdLayer = this;
	}

	/**
	 * Benutzen um Instanz zu bekommen.</br> Singleton Pattern
	 */
	public static ConfigDialogLayer getInstance()
	{
		if (cdLayer == null)
		{
			cdLayer = new ConfigDialogLayer();
		}

		return cdLayer;
	}

	/**
	 * Ein Netzwerk Element in die Liste einfuegen.
	 */
	public void addNetworkElement(Class c)
	{
		networkElems.add(c);
	}

	/**
	 * Ein Projekt Element in die Liste einfuegen.
	 */
	public void addProjectElement(Class c)
	{
		projectElems.add(c);
	}

	/**
	 * Ein UniqueNet Element in die Liste einfuegen.
	 */
	public void addUniqueNetElement(Class c)
	{
		uniqueNetElems.add(c);
	}

	/**
	 * Liefert alle Netzwerk-Element zurueck.
	 */
	public List<Class> getNetworkElements()
	{
		return networkElems;
	}

	/**
	 * Liefert alle Netzwerk-Element zurueck.
	 */
	public List<Class> getProjectElements()
	{
		return projectElems;
	}

	/**
	 * Liefert alle Netzwerk-Element zurueck.
	 */
	public List<Class> getUniqueNetElements()
	{
		return uniqueNetElems;
	}

	public ConfigDialogElement getActivatedNetworkElement(String name,
			String networkName)
	{
		if (activateNetworkdElements.get(networkName) == null)
			return null;

		return this.activateNetworkdElements.get(networkName).get(name);
	}

	public Map<String, ConfigDialogElement> getAllActiveNetworkElements(String networkName)
	{
		return this.activateNetworkdElements.get(networkName);
	}
	
	public List<ConfigDialogElement> getActiviatedElements()
	{
		List<ConfigDialogElement> elements = new ArrayList<ConfigDialogElement>();

		for (Iterator<ConfigDialogElement> iterator = activeProjectElements
				.values().iterator(); iterator.hasNext();)
		{
			ConfigDialogElement elem = iterator.next();
			elem.buildPanel();
			elements.add(elem);
		}

		for (Iterator<HashMap<String, ConfigDialogElement>> iterator = activateNetworkdElements
				.values().iterator(); iterator.hasNext();)
		{
			for (Iterator<ConfigDialogElement> elementIterator = iterator.next()
					.values().iterator(); elementIterator.hasNext();)
			{
				ConfigDialogElement elem = elementIterator.next();
				elem.buildPanel();
				elements.add(elem);
			}
		}

		return elements;
	}

	public void setActivatedNetworkElement(String name,
			ConfigDialogElement elem, String networkName)
	{
		if (activateNetworkdElements.get(networkName) == null)
			activateNetworkdElements.put(networkName,
					new HashMap<String, ConfigDialogElement>());

		this.activateNetworkdElements.get(networkName).put(name, elem);

		if (allActivateNetworkdElements.get(networkName) == null)
			allActivateNetworkdElements.put(networkName,
					new HashMap<String, ConfigDialogElement>());

		this.allActivateNetworkdElements.get(networkName).put(name, elem);
	}

	public ConfigDialogElement getActivatedProjectElement(String name)
	{
		return this.activeProjectElements.get(name);
	}

	public void setActivatedProjectElement(String name, ConfigDialogElement elem)
	{
		this.activeProjectElements.put(name, elem);
		this.allActiveProjectElements.put(name, elem);
	}

	public void resetActiveElements()
	{
		activateNetworkdElements.clear();
		activeProjectElements.clear();
	}
	
	
	public List<ConfigDialogElement> getAllActiviatedElements()
	{
		List<ConfigDialogElement> elements = new ArrayList<ConfigDialogElement>();

		for (Iterator<ConfigDialogElement> iterator = allActiveProjectElements
				.values().iterator(); iterator.hasNext();)
		{
			ConfigDialogElement elem = iterator.next();
			elem.buildPanel();
			elements.add(elem);
		}

		for (Iterator<HashMap<String, ConfigDialogElement>> iterator = allActivateNetworkdElements
				.values().iterator(); iterator.hasNext();)
		{
			for (Iterator<ConfigDialogElement> elementIterator = iterator.next()
					.values().iterator(); elementIterator.hasNext();)
			{
				ConfigDialogElement elem = elementIterator.next();
				elem.buildPanel();
				elements.add(elem);
			}
		}

		return elements;
	}
}
