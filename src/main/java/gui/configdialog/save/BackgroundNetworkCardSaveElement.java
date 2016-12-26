package gui.configdialog.save;

import gui.VennMaker;

import java.util.List;
import java.util.Vector;

import data.Netzwerk;

public class BackgroundNetworkCardSaveElement extends SaveElement
{
	Vector<Integer> networkIDs;
	List<String> networkNames;
	
	/**
	 * Dieser Konstruktor sorgt fuer Abwaertskompatiblitaet
	 */
	public BackgroundNetworkCardSaveElement(List<String> networkNames)
	{
		this.networkNames = networkNames;
	}
	
	public BackgroundNetworkCardSaveElement(Vector<Integer> networkIDs)
	{
		this.networkIDs = networkIDs;
	}

	/**
	 * @return the network Ids
	 */
	public Vector<Integer> getNetworkIDs()
	{
		/**
		 * falls altes Template geladen wurde und nach IDs gefragt wird,
		 * werden die Namen auf Ids abgebildet
		 */
		if(networkIDs==null && networkNames!=null)
		{
			networkIDs = new Vector<Integer>();
			List<Netzwerk> networks = VennMaker.getInstance().getProject().getNetzwerke();
			for(String name : networkNames)
			{
				for(Netzwerk n : networks)
						if(name != null && name.equals(n.toString()))
						{
							networkIDs.add(n.getId());
							break;
						}
			}
			networkNames = null;
		}
		return networkIDs;
	}
}
