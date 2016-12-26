package interview.panels.other;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import data.Akteur;

/**
 * wrapper for an actor to enable drag & drop
 * 
 * 
 */
public class DraggableActor implements Transferable
{
	public Akteur actor;
	
	public DraggableActor(Akteur a)
	{
		this.actor = a;
	}
	
	@Override
	public String toString()
	{
		if(actor!=null)
			return actor.getName();
		
		return "";
	}
	
	/*
	 * for Hransferhandler only
	 */
	public final static DataFlavor flavor = new DataFlavor( DraggableActor.class, "DraggableActor" );
	public final static DataFlavor[] flavors = { flavor };
	@Override
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException
	{
		return this;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor arg0)
	{
		return arg0 == flavor;
	}
}
