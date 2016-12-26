/**
 * 
 */
package interview;

import gui.Messages;
import interview.elements.InterviewElement;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
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

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * JTree implementation with drag & drop support
 * 
 * 
 * 
 */
public class InterviewTreeDragAndDrop extends JTree implements
		TreeSelectionListener, DragGestureListener, DropTargetListener,
		DragSourceListener
{
	private DragSource			ds;

	private DragSourceContext	dsc;

	private DropTarget			dt;

	private InterviewTreeNode	nodeToDrag;

	private int						indexToDrag;

	public InterviewTreeDragAndDrop(TreeNode root)
	{
		super(root);

		ds = DragSource.getDefaultDragSource();

		DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY_OR_MOVE, this);

		dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

		dt = new DropTarget(this, this);

		this.addTreeSelectionListener(this);
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void dragEnter(DragSourceDragEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DragSourceEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DragSourceDragEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dropActionChanged(DragSourceDragEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragEnter(DropTargetDragEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DropTargetEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DropTargetDragEvent arg0)
	{
		// Point dragPoint = arg0.getLocation();
		//
		// TreePath dragPath = getPathForLocation(dragPoint.x, dragPoint.y);
		//
		// if (dragPath == null)
		// return;
		//
		// InterviewTreeNode dragNode = (InterviewTreeNode) dragPath
		// .getLastPathComponent();
		//
		// if (dragNode == nodeToDrag || dragNode == nodeToDrag.getParent())
		// arg0.rejectDrag();
		//
		// try
		// {
		// arg0.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
		// } catch (Exception e)
		// {
		//
		// }

	}

	/**
	 * Called if the element which was dragged got dropped. This method adds an
	 * dropped element either to the top level to the choosen position, or it
	 * adds a dragged node to another node as a child.
	 */
	@Override
	public void drop(DropTargetDropEvent arg0)
	{
		Transferable trans = arg0.getTransferable();

		if (!trans.isDataFlavorSupported(InterviewElement.INTERVIEW_FLAVOR))
			arg0.rejectDrop();

		Point location = arg0.getLocation();

		TreePath destination = getPathForLocation(location.x, location.y);

		if (destination == null)
		{
			arg0.rejectDrop();
			return;
		}

		InterviewTreeNode destinationNode = (InterviewTreeNode) destination.getLastPathComponent();

		if (destinationNode.equals(this.nodeToDrag)
				|| destinationNode.isNodeAncestor(this.nodeToDrag))
		{
			/*
			 * destination and source are the same, or source is of higher level
			 * than destination
			 */
			arg0.rejectDrop();
			return;
		}

		int indexToReplace;
		InterviewTreeNode parent = null;

		if (destinationNode == (InterviewTreeNode) getModel().getRoot() )
			/*	|| JOptionPane
						.showConfirmDialog(
								this,
								Messages
										.getString("CDialogInterviewCreator.TreeChild1")
										+ " "
										+ nodeToDrag.toString()
										+ " "
										+ Messages
												.getString("CDialogInterviewCreator.TreeChild2") + " " + destinationNode.toString() + " ?", "Question", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		*/
		{
			parent = destinationNode;
			indexToReplace = 0;
		}
		else
		{
			indexToReplace = this.getModel().getIndexOfChild(destinationNode.getParent(), destinationNode);		
			//parent = (InterviewTreeNode) destinationNode.getParent();
			parent = (InterviewTreeNode) getModel().getRoot(); //keine child-child konstruktion mehr...
		}

		InterviewTreeModel dtm = (InterviewTreeModel) this.getModel();

		InterviewLayer layer = InterviewLayer.getInstance();

		InterviewElement dragElement = (InterviewElement) this.nodeToDrag.getUserObject();
		InterviewElement parentElement = dragElement.getParent();

		if (parentElement != null)
		{
			parentElement.removeChild(dragElement);
			dragElement.setChildStatus(false);
		}

		
		/* set the new parentnode */
/*		parentElement = (InterviewElement) parent.getUserObject();
		
		if ((parentElement != null)
				&& (parent != (InterviewTreeNode) getModel().getRoot()))
		{
			parentElement.addChild(dragElement);
			dragElement.setChildStatus(true);
		}
		else*/
		{
			layer.setElementAt(indexToReplace, dragElement, InterviewLayer.INSERT);
			dragElement.setChildStatus(false);
		}

/*		if (parent != (InterviewTreeNode) getModel().getRoot())
		{
			dtm.insertNodeInto(nodeToDrag, parent, 0);
		}
		else
	*/	{
		dtm.insertNodeInto(nodeToDrag, parent, indexToReplace);
		}

		dtm.reload();
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent arg0)
	{
		if (nodeToDrag == null || nodeToDrag.getParent() == null)
			return;
		if (nodeToDrag.getParent().equals(getModel().getRoot()))
			indexToDrag = getModel().getIndexOfChild(nodeToDrag.getParent(),
					nodeToDrag);

		if (nodeToDrag == null)
			return;

		Transferable trans = (Transferable) nodeToDrag.getUserObject();

		ds.startDrag(arg0, DragSource.DefaultCopyDrop, trans, this);

	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0)
	{
		TreePath selectionPath = arg0.getNewLeadSelectionPath();

		if (selectionPath == null)
		{
			nodeToDrag = null;
			return;
		}

		nodeToDrag = (InterviewTreeNode) selectionPath.getLastPathComponent();
	}

}
