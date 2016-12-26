package gui.configdialog.save;

import java.util.List;
import java.util.Vector;

import data.Netzwerk;

/**
 * This class contains all network wide SaveElements
 * 
 * 
 * 
 */
public class NetworkSaveElement
{
	private Netzwerk				network;

	private List<SaveElement>	networkSaveElements;

	public NetworkSaveElement(Netzwerk network)
	{
		if (network == null)
			throw new IllegalArgumentException("Network should not be null");

		this.network = network;
		this.networkSaveElements = new Vector<SaveElement>();

	}

	public NetworkSaveElement()
	{
		this.networkSaveElements = new Vector<SaveElement>();
	}

	/**
	 * Returns all network wide SaveElements
	 * 
	 * @return Returns all network wide SaveElements
	 */
	public List<SaveElement> getNetworkSaveElements()
	{
		return this.networkSaveElements;
	}

	/**
	 * Adds a SaveElement
	 * 
	 * @param elem
	 *           Element to add
	 */
	public void addSaveElement(SaveElement elem)
	{
		if (elem != null && !this.networkSaveElements.contains(elem))
			this.networkSaveElements.add(elem);
	}

	/**
	 * Removes a network wide SaveElement from the list
	 * 
	 * @param elem
	 *           Element to remove
	 * @return true if successfull, false if element was not in list, or could
	 *         not removed
	 */
	public boolean removeNetworkSaveElement(SaveElement elem)
	{
		return this.networkSaveElements.remove(elem);
	}

	/**
	 * Returns the network
	 * 
	 * @return the network
	 */
	public Netzwerk getNetwork()
	{
		return this.network;
	}

	/**
	 * Sets the network for all SaveElements
	 * 
	 * @param network
	 * @throws IllegalArgumentException
	 */
	public void setNetwork(Netzwerk network)
	{
		if (network == null)
			throw new IllegalArgumentException("Network should not be null");

		this.network = network;
	}

	/**
	 * Returns the SaveElement at specified index
	 * 
	 * @param index
	 * @return SaveElement at index
	 * @throws IllegalArgumentException
	 */
	public SaveElement getSaveElementAt(int index)
	{
		if (index < 0 || index > this.networkSaveElements.size())
			throw new IllegalArgumentException("Illegal index position");

		return this.networkSaveElements.get(index);
	}
}
