package interview.panels.other;

import interview.configuration.attributesSelection.MultiAttributePanel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.io.IOException;

import javax.swing.JCheckBox;

import data.AttributeType;

/**
 * 
 * Objects of this <code>JCheckBox</code> contains an <code>AttributeType</code>
 * and suppoort Drag&Drop
 * 
 */
public class AttributeCheckBox extends JCheckBox implements DragSourceListener,
		DropTargetListener, DragGestureListener, Transferable
{

	public static final DataFlavor	ATTRIBUTE_FLAVOR	= new DataFlavor(
																				AttributeType.class,
																				"AttributeType");

	public static DataFlavor			flavors[]			= { ATTRIBUTE_FLAVOR };

	private static final long			serialVersionUID	= 1L;

	private AttributeType				a;

	private DragSource					dragSource;

	private DropTarget					dropTarget;

	private DragableCheckBoxSource	source;
	
	/**
	 * Constructs a new <code>AttributeCheckBox</code> without Drag&Drop-Support
	 * @param a the <code>AttributeType</code> to store in this <code>AttributeCheckBox</code>
	 */
	public AttributeCheckBox(AttributeType a)
	{
		super();
		setText(MultiAttributePanel.normalizeName(a.getLabel()));
		setToolTipText(a.getLabel());
		this.a = a;

		dragSource = DragSource.getDefaultDragSource();
		dropTarget = new DropTarget(this, this);

	}
	
	/**
	 * Constructs a new <code>AttributeCheckBox</code>
	 * @param a the <code>AttributeType</code> to store in this <code>AttributeCheckBox</code>
	 * @param source the <code>DragableCheckBoxSource</code> this <code>AttributeCheckBox</code> is stored in
	 */
	public AttributeCheckBox(AttributeType a, DragableCheckBoxSource source)
	{

		this(a);

		this.source = source;

		DragGestureRecognizer dgr = dragSource
				.createDefaultDragGestureRecognizer(this,
						DnDConstants.ACTION_COPY_OR_MOVE, this);
		dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);
	}

	public AttributeType getAttributeType()
	{
		return a;
	}

	public void setAttributeType(AttributeType a)
	{
		this.a = a;
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DropTargetEvent dte)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DropTargetDragEvent dtde)
	{
	}

	@Override
	public void drop(DropTargetDropEvent dtde)
	{
		try
		{
			AttributeType type = (AttributeType) dtde.getTransferable()
					.getTransferData(ATTRIBUTE_FLAVOR);

			source.switchCheckBoxValues(type, this.a);
			source.updateCheckBoxes();

		} catch (UnsupportedFlavorException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DragSourceEvent dse)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DragSourceDragEvent dsde)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragGestureRecognized(DragGestureEvent arg0)
	{
		Transferable trans = (Transferable) ((AttributeCheckBox) arg0
				.getComponent()).getAttributeType();
		dragSource.startDrag(arg0, DragSource.DefaultCopyDrop, trans, this);
	}

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
		return arg0.equals(ATTRIBUTE_FLAVOR);
	}

	public String toString()
	{
		return a.toString();
	}
}
