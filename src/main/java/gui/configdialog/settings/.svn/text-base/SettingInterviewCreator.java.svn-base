/**
 * 
 */
package gui.configdialog.settings;

import interview.InterviewLayer;
import interview.InterviewTreeNode;
import interview.elements.InterviewElement;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

/**
 * 
 *
 */
public class SettingInterviewCreator implements ImmidiateConfigDialogSetting
{
	private InterviewElement interviewValue;
	private InterviewElement parentElement;
	private InterviewTreeNode interviewNode;
	private InterviewTreeNode parent;
	private InterviewTreeNode root;
	
	private JTree interviewTree;
	
	public SettingInterviewCreator(JTree interviewTree,InterviewElement interviewValue, InterviewElement parentElement, InterviewTreeNode interviewNode, InterviewTreeNode parent, InterviewTreeNode root)
	{
		this.interviewValue = interviewValue;
		this.parentElement = parentElement;
		this.interviewNode = interviewNode;
		this.parent = parent;
		this.root = root;
		this.interviewTree = interviewTree;
	}
	
	
	public SettingInterviewCreator()
	{
		
	}
	
	@Override
	public void set()
	{
		InterviewLayer layer = InterviewLayer.getInstance();
		
		if(layer.contains(parentElement))
		{
			parentElement.addChild(interviewValue);
			layer.addChildElement(interviewValue);
		}
		else
		{
			layer.addElement(interviewValue);
		}
		
		if(parent != null)
			parent.add(interviewNode);
		else
			root.add(interviewNode);
		
		layer.saveInterviewTree(interviewTree);
	}
	
	
	@Override
	public void undo()
	{
		InterviewLayer layer = InterviewLayer.getInstance();
		
		DefaultTreeModel treeModel = (DefaultTreeModel)layer.loadInterviewTree().getModel();
		
		layer.removeElement(interviewValue);
		if(interviewNode.getParent() != null)
			treeModel.removeNodeFromParent(interviewNode);
		treeModel.reload();
	}

	/**
	 * @return the interviewValue
	 */
	public InterviewElement getInterviewValue()
	{
		return interviewValue;
	}

	/**
	 * @param interviewValue the interviewValue to set
	 */
	public void setInterviewValue(InterviewElement interviewValue)
	{
		this.interviewValue = interviewValue;
	}

	/**
	 * @return the parentElement
	 */
	public InterviewElement getParentElement()
	{
		return parentElement;
	}

	/**
	 * @param parentElement the parentElement to set
	 */
	public void setParentElement(InterviewElement parentElement)
	{
		this.parentElement = parentElement;
	}

	/**
	 * @return the interviewNode
	 */
	public InterviewTreeNode getInterviewNode()
	{
		return interviewNode;
	}

	/**
	 * @param interviewNode the interviewNode to set
	 */
	public void setInterviewNode(InterviewTreeNode interviewNode)
	{
		this.interviewNode = interviewNode;
	}

	/**
	 * @return the parent
	 */
	public InterviewTreeNode getParent()
	{
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(InterviewTreeNode parent)
	{
		this.parent = parent;
	}

	/**
	 * @return the root
	 */
	public InterviewTreeNode getRoot()
	{
		return root;
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(InterviewTreeNode root)
	{
		this.root = root;
	}

	/**
	 * @return the interviewTree
	 */
	public JTree getInterviewTree()
	{
		return interviewTree;
	}

	/**
	 * @param interviewTree the interviewTree to set
	 */
	public void setInterviewTree(JTree interviewTree)
	{
		this.interviewTree = interviewTree;
	}
}
