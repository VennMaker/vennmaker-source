package interview.panels.other;

import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * transfer handler to use actors for drag & drop 
 * (needed in DragAndDropAnswerPanel)
 * 
 * 
 */
public class ActorTransferHandler extends TransferHandler
{
	private static final long	serialVersionUID	= 1L;
	private JList<DraggableActor> list;
	private boolean sortedImport;
	
	public ActorTransferHandler(JList<DraggableActor> list, boolean sortedImport)
	{
		this.list = list;
		this.sortedImport = sortedImport;
	}
	
	public int getSourceActions(JComponent comp) 
	{
   	return MOVE;
	}

	private int index = 0;

	public Transferable createTransferable(JComponent comp) 
	{
   	index = list.getSelectedIndex();
      if (index < 0 || index >= ((DefaultListModel<DraggableActor>)list.getModel()).getSize()) 
          return null;
      return (DraggableActor)((DefaultListModel<DraggableActor>)list.getModel()).getElementAt(index);
  }
  
  public void exportDone(JComponent comp, Transferable trans, int action) 
  {
      if (action != MOVE)
      	return;

      ((DefaultListModel<DraggableActor>)list.getModel()).removeElement(trans);
  }
     
	public boolean canImport(TransferSupport support)
	{
		if(!support.isDataFlavorSupported(DraggableActor.flavor))
			return false;
		JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
		return !(dl.getIndex() == -1);
	}
	
	public boolean importData(TransferSupport support)
	{
		if(!canImport(support))
			return false;
		
		DraggableActor a = null;
		try {
         a = (DraggableActor)support.getTransferable().getTransferData(DraggableActor.flavor);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
		int index = sortedImport?dl.getIndex():0;

		((DefaultListModel<DraggableActor>)list.getModel()).insertElementAt(a, index);
		Rectangle r = list.getCellBounds(index, index);
		list.scrollRectToVisible(r);
		return true;
	}
}