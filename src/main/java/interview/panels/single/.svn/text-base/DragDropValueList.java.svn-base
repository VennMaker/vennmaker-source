package interview.panels.single;

import gui.VennMaker;
import interview.panels.other.ActorTransferHandler;
import interview.panels.other.DraggableActor;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;


/**
 * panel with only one JList element representing one categorical attribute value
 * for drag & drop
 * 
 * 
 */
public class DragDropValueList extends JPanel
{
	private static final long	serialVersionUID	= 1L;
	
	private AttributeType 		aType;
	
	private Object 				val;
	
	private JList<DraggableActor> list;
	
	private Map<Netzwerk,Object> oldValues;
	
	public DragDropValueList(AttributeType aType, Object value)
	{
		this.aType = aType;
		this.val = value;
		
		oldValues = new HashMap<Netzwerk,Object>();
		
		DefaultListModel<DraggableActor> dflm = new DefaultListModel<DraggableActor>();
		list = new JList<DraggableActor>(dflm);
		list.setDragEnabled(true);
		list.setDropMode(DropMode.INSERT);
		list.setTransferHandler(new ActorTransferHandler(list,false));
		
		JScrollPane listPane = new JScrollPane(list);	
		this.setLayout(new BorderLayout());
		
		this.setBorder(BorderFactory.createTitledBorder(value.toString())); 
		this.add(listPane,BorderLayout.CENTER);
	}
	
	public void addActor(Akteur a)
	{
		DefaultListModel<DraggableActor> dflm = (DefaultListModel<DraggableActor>) list.getModel();
		dflm.addElement(new DraggableActor(a));
	}
	
	public AttributeType getAttributeType()
	{
		return aType;
	}
	
	public Object getValue()
	{
		return val;
	}
	
	public void performChanges()
	{
		List<Netzwerk> networks = VennMaker.getInstance().getProject().getNetzwerke();
		
		for(int i=0; i<list.getModel().getSize();i++)
		{
			DraggableActor da = (DraggableActor)list.getModel().getElementAt(i);
			for(Netzwerk n : networks)
			{
				oldValues.put(n, da.actor.getAttributeValue(aType, n));
				da.actor.setAttributeValue(aType, n, val);
			}
		}
	}
	
	public void undoChanges()
	{
List<Netzwerk> networks = VennMaker.getInstance().getProject().getNetzwerke();
		
		for(int i=0; i<list.getModel().getSize();i++)
		{
			DraggableActor da = (DraggableActor)list.getModel().getElementAt(i);
			for(Netzwerk n : networks)
				da.actor.setAttributeValue(aType, n, oldValues.get(n));
		}
	}
}