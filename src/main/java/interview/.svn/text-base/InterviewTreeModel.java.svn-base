package interview;

import interview.elements.InterviewElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class InterviewTreeModel extends DefaultTreeModel
{
	/**
	 * 
	 */
	private static final long					serialVersionUID	= -4873737559026628772L;

	public static final int						NAME_LENGTH			= 30;

	public static final String					NAME_SUFFIX			= "...";

	public static final String					FILTER_SUFFIX		= "_(f)";

	private ArrayList<InterviewTreeNode>	nodesInTree			= new ArrayList<InterviewTreeNode>();

	public InterviewTreeModel(InterviewTreeNode root,
			ArrayList<InterviewTreeNode> nodesInTree)
	{
		super(root);
		this.nodesInTree = nodesInTree;

		for (InterviewTreeNode node : nodesInTree)
		{
			super.insertNodeInto(node, (InterviewTreeNode) node.getParent(), node
					.getParent().getIndex(node));
		}
	}

	public void insertNodeInto(InterviewElement newChild,
			InterviewElement parent, int index)
	{
		InterviewTreeNode childNode = new InterviewTreeNode(newChild);
		InterviewTreeNode parentNode = new InterviewTreeNode(parent);

		for (InterviewTreeNode node : nodesInTree)
		{
			if (((InterviewElement) node.getUserObject()).equals(newChild))
				childNode = node;
			if (((InterviewElement) node.getUserObject()).equals(parent))
				parentNode = node;
		}

		insertNodeInto(childNode, parentNode, index);
	}

	public void insertNodeInto(InterviewTreeNode newChild,
			InterviewTreeNode parent, int index)
	{
		this.add(newChild);
		this.add(parent);
		
		if (parent != null){
			super.insertNodeInto(newChild, parent, index);
		}
		else
			super.insertNodeInto(newChild, (InterviewTreeNode) getRoot(), index);
	}

	public void addNodeToData(InterviewTreeNode node)
	{
		if (node == null)
			throw new IllegalArgumentException("Node must not be null");
		this.add(node);
	}

	public void nodeChanged(TreeNode node)
	{
		if (node instanceof InterviewTreeNode)
		{
			InterviewElement nodeElement = (InterviewElement) ((InterviewTreeNode) node)
					.getUserObject();

			if (nodeElement.getElementNameInTree().length() > NAME_LENGTH)
				nodeElement.setElementNameInTree(nodeElement.getElementNameInTree()
						.substring(0, NAME_LENGTH - NAME_SUFFIX.length())
						+ NAME_SUFFIX);
		}

		super.nodeChanged(node);
	}

	/**
	 * returns the children of the parent in the given tree
	 * 
	 * @param parent
	 * @return
	 */
	public Vector<InterviewElement> getChildrenOf(InterviewTreeNode parent)
	{
		if (parent.getChildCount() == 0)
			return new Vector<InterviewElement>();
		else
		{
			Vector<InterviewElement> returnVector = new Vector<InterviewElement>();
			for (int i = 0; i < parent.getChildCount(); i++)
				returnVector.add((InterviewElement) (((InterviewTreeNode) parent
						.getChildAt(i)).getUserObject()));
			return returnVector;
		}
	}

	/**
	 * returns the children of the parent in the given tree
	 * 
	 * @param parent
	 * @return
	 */
	public Vector<InterviewElement> getChildrenOf(InterviewElement parent)
	{
		LinkedList<InterviewTreeNode> fifoList = new LinkedList<InterviewTreeNode>();

		fifoList.offer((InterviewTreeNode) this.getRoot());

		InterviewTreeNode currentNode;

		while ((currentNode = fifoList.poll()) != null
				&& !currentNode.getUserObject().equals(parent))
		{
			for (int i = 0; i < currentNode.getChildCount(); i++)
				fifoList.offer((InterviewTreeNode) currentNode.getChildAt(i));
		}

		if ((currentNode != null) && (currentNode.getUserObject().equals(parent)))
			return this.getChildrenOf(currentNode);
		return new Vector<InterviewElement>();
	}

	/**
	 * checkes, if given node is already in tree, if not: add
	 * 
	 * @param node
	 *           the node to add to the tree
	 */
	public void add(InterviewTreeNode node)
	{
		if (!this.nodesInTree.contains(node))
			this.nodesInTree.add(node);
	}

	/**
	 * returns the correct node to the given InterviewElement
	 * 
	 * @param elem
	 *           the InterviewElement to return the node from
	 * @return the node to the corresponding InterviewElement
	 */
	public InterviewTreeNode getNodeFrom(InterviewElement elem)
	{
		for (InterviewTreeNode node : this.nodesInTree)
		{
			if (node.getUserObject().equals(elem))
				return node;
		}
		return null;
	}
}
